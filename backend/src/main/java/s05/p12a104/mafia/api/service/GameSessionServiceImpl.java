package s05.p12a104.mafia.api.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.ConnectionType;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.api.requset.GameSessionPostReq;
import s05.p12a104.mafia.api.response.GameSessionJoinRes;
import s05.p12a104.mafia.common.exception.AlreadyGameStartedException;
import s05.p12a104.mafia.common.exception.GameSessionNotFoundException;
import s05.p12a104.mafia.common.exception.OpenViduRuntimeException;
import s05.p12a104.mafia.common.exception.OpenViduSessionNotFoundException;
import s05.p12a104.mafia.common.exception.OverMaxIndividualRoomCountException;
import s05.p12a104.mafia.common.exception.OverMaxPlayerCountException;
import s05.p12a104.mafia.common.exception.OverMaxTotalRoomCountException;
import s05.p12a104.mafia.common.util.RoleUtils;
import s05.p12a104.mafia.common.util.RoomIdUtils;
import s05.p12a104.mafia.common.util.UrlUtils;
import s05.p12a104.mafia.domain.dao.GameSessionDao;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.entity.User;
import s05.p12a104.mafia.domain.enums.Color;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.domain.enums.GameState;
import s05.p12a104.mafia.domain.mapper.GameSessionDaoMapper;
import s05.p12a104.mafia.domain.repository.GameSessionRedisRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameSessionServiceImpl implements GameSessionService {

  private final GameSessionRedisRepository gameSessionRedisRepository;

  private final OpenVidu openVidu;

  private static final int MAX_TOTAL_ROOM_COUNT = 200;
  private static final int MAX_INDIVIDUAL_ROOM_COUNT = 2;
  private static final int MAX_PLAYER_COUNT = 10;

  @Override
  public GameSession makeGame(User user, GameSessionPostReq typeInfo)
      throws OpenViduJavaClientException, OpenViduHttpException {

    if (gameSessionRedisRepository.findAll().size() >= MAX_TOTAL_ROOM_COUNT) {
      throw new OverMaxTotalRoomCountException();
    }

    if (gameSessionRedisRepository.findByCreatorEmail(user.getEmail())
        .size() >= MAX_INDIVIDUAL_ROOM_COUNT) {
      throw new OverMaxIndividualRoomCountException();
    }

    Session newSession = openVidu.createSession();
    String newRoomId =
        RoomIdUtils.getIdPrefix(typeInfo.getAccessType()) + newSession.getSessionId().split("_")[1];

    LocalDateTime createdTime = LocalDateTime.now();
    GameSession newGameSession = GameSession.builder(newRoomId, user.getEmail(),
        typeInfo.getAccessType(), typeInfo.getRoomType(), createdTime, newSession, null)
        .finishedTime(createdTime).build();

    GameSessionDao newDao = GameSessionDaoMapper.INSTANCE.toDao(newGameSession);
    return toEntity(gameSessionRedisRepository.save(newDao));
  }

  @Override
  public GameState getGameSessionState(String roomId) {
    GameSession gameSession = findById(roomId);
    validateToBePossibleToJoin(gameSession);

    return gameSession.getState();
  }

  @Override
  public void removeGameSession(GameSession gameSession) {
    gameSessionRedisRepository.deleteById(gameSession.getRoomId());
    log.info("Room {} removed and closed", gameSession.getRoomId());
  }

  @Override
  public GameSession findById(String id) {
    GameSessionDao gameSessionDao =
        gameSessionRedisRepository.findById(id).orElseThrow(GameSessionNotFoundException::new);

    return toEntity(gameSessionDao);
  }

  @Override
  public void update(GameSession update) {
    GameSessionDao updateDto = GameSessionDaoMapper.INSTANCE.toDao(update);
    gameSessionRedisRepository.save(updateDto);
  }

  @Override
  public GameSessionJoinRes addUser(String roomId, String nickname) {
    GameSession gameSession = findById(roomId);
    validateToBePossibleToJoin(gameSession);

    OpenViduRole role = OpenViduRole.PUBLISHER;

    String serverData = "{\"serverData\": \"" + nickname + "\"}";

    // Build connectionProperties object with the serverData and the role
    ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
        .type(ConnectionType.WEBRTC).data(serverData).role(role).build();

    try {
      // ex> wss://localhost:4443?sessionId=ses_Ogize1yQIj&token=tok_A1c0pNsLJFwVJTeb
      String token = gameSession.getSession().createConnection(connectionProperties).getToken();

      // ex> tok_A1c0pNsLJFwVJTeb
      String userId = UrlUtils.getUrlQueryParam(token, "token")
          .orElseThrow(OpenViduSessionNotFoundException::new).substring(4);

      Player player =
          Player.builder(userId, nickname, getNewColor(gameSession), token, role).build();

      gameSession.getPlayerMap().put(userId, player);
      if (gameSession.getPlayerMap().size() == 1) {
        gameSession.setHostId(userId);
      }
      update(gameSession);

      return new GameSessionJoinRes(token, userId);
    } catch (OpenViduJavaClientException e1) {
      // If internal error generate an error message and return it to client
      throw new OpenViduRuntimeException(e1.getMessage());
    } catch (OpenViduHttpException e2) {
      if (404 == e2.getStatus()) {
        removeGameSession(gameSession);
      }
      throw new OpenViduRuntimeException(e2.getMessage());
    }
  }

  @Override
  public GameSession removeUser(String roomId, String userId) {
    GameSession gameSession = findById(roomId);
    Session session = gameSession.getSession();
    Map<String, Player> playerMap = gameSession.getPlayerMap();

    // If the session exists ("TUTORIAL" in this case)
    if (session == null || playerMap == null) {
      log.info("Problems in the app server: the SESSION does not exist");
      throw new OpenViduSessionNotFoundException();
    }

    if (playerMap.remove(userId) == null) {
      log.info("Problems in the app server: the USER_ID - {} wasn't valid", userId);
    }

    // User left the session
    if (playerMap.isEmpty()) {
      removeGameSession(gameSession);
    } else {
      if (userId.equals(gameSession.getHostId())) {
        // 다음 player를 방장으로 등록
        gameSession.setHostId(playerMap.keySet().iterator().next());
      }
    }
    return gameSession;
  }

  public GameSession toEntity(GameSessionDao dao) {
    Session entitySession = null;
    for (Session session : openVidu.getActiveSessions()) {
      if (session.getSessionId().equals(dao.getSessionId())) {
        entitySession = session;
        break;
      }
    }
    if (entitySession == null) {
      throw new OpenViduSessionNotFoundException();
    }

    Map<String, Player> playerMap = dao.getPlayerMap();
    if (playerMap == null) {
      playerMap = new LinkedHashMap<>();
    }

    GameSession gameSession = GameSession
        .builder(dao.getRoomId(), dao.getCreatorEmail(), dao.getAccessType(), dao.getRoomType(),
            dao.getCreatedTime(), entitySession, playerMap)
        .finishedTime(dao.getFinishedTime()).day(dao.getDay()).isNight(dao.isNight())
        .aliveMafia(dao.getAliveMafia()).alivePlayer(dao.getAlivePlayer()).timer(dao.getTimer()).phase(dao.getPhase())
        .lastEnter(dao.getLastEnter()).state(dao.getState()).hostId(dao.getHostId()).build();

    return gameSession;
  }

  private void validateToBePossibleToJoin(GameSession gameSession) {
    if (gameSession.getState() == GameState.STARTED) {
      throw new AlreadyGameStartedException();
    }

    if (gameSession.getPlayerMap().size() >= MAX_PLAYER_COUNT) {
      throw new OverMaxPlayerCountException();
    }
  }

  private Color getNewColor(GameSession gameSession) {
    Set<Color> usedColors = new HashSet<>();
    for (Player player : gameSession.getPlayerMap().values()) {
      usedColors.add(player.getColor());
    }

    for (int i = 0; i < MAX_PLAYER_COUNT; i++) {
      Color newColor = Color.randomColor();
      if (!usedColors.contains(newColor)) {
        return newColor;
      }
    }
    return Color.RED;
  }

  @Override
  public void startGame(GameSession gameSession) {
    // 각 역할에 맞는 인원수 구하기
    Map<GameRole, Integer> roleNum = RoleUtils.getRoleNum(gameSession);

    // game 초기 setting
    gameSession.setState(GameState.STARTED);
    gameSession.setDay(1);
    gameSession.setAliveMafia(roleNum.get(GameRole.MAFIA));
    gameSession.setPhase(GamePhase.START);
    gameSession.setTimer(15);
    // player 초기화
    gameSession.getPlayerMap().forEach((playerId, player) -> {
      player.setAlive(true);
      player.setSuspicious(false);
    });
    
    // 역할 부여
    RoleUtils.assignRole(roleNum, gameSession.getPlayerMap());
    
    // redis에 저장
    update(gameSession);
  }
}
