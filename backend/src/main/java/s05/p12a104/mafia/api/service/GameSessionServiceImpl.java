package s05.p12a104.mafia.api.service;

import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.ConnectionType;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.api.requset.GameSessionPostReq;
import s05.p12a104.mafia.api.response.GameSessionJoinRes;
import s05.p12a104.mafia.common.exception.GameSessionException;
import s05.p12a104.mafia.common.util.RoomIdUtils;
import s05.p12a104.mafia.common.util.UrlUtils;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.dao.GameSessionDao;
import s05.p12a104.mafia.domain.mapper.GameSessionDaoMapper;
import s05.p12a104.mafia.domain.entity.GameState;
import s05.p12a104.mafia.domain.entity.User;
import s05.p12a104.mafia.domain.repository.GameSessionRedisRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameSessionServiceImpl implements GameSessionService {

  private final GameSessionRedisRepository gameSessionRedisRepository;

  private final OpenVidu openVidu;


  @Override
  public GameSession makeGame(User user, GameSessionPostReq typeInfo)
      throws OpenViduJavaClientException, OpenViduHttpException {

    if (gameSessionRedisRepository.findByCreatorEmail(user.getEmail()).size() >= 2) {
      throw new GameSessionException("더 이상 방을 만들 수 없습니다");
    }

    Session newSession = openVidu.createSession();
    String newRoomId =
        RoomIdUtils.getIdPrefix(typeInfo.getAccessType())
            + newSession.getSessionId().split("_")[1];

    LocalDateTime createdTime = LocalDateTime.now();
    GameSession newGameSession = GameSession.builder(newRoomId, user.getEmail(),
        typeInfo.getAccessType(), typeInfo.getRoomType(), createdTime,
        newSession, null)
        .finishedTime(createdTime)
        .build();

    GameSessionDao newDao = GameSessionDaoMapper.INSTANCE.toDao(newGameSession);
    return toEntity(gameSessionRedisRepository.save(newDao));
  }

  @Override
  public GameSession getGameSessionStatus(String roomId) {
    GameSession gameSession = findById(roomId);
    if (gameSession.getState() != GameState.wait) {
      throw new GameSessionException("게임이 이미 시작되었습니다.");
    }

    return gameSession;
  }

  @Override
  public void removeGameSession(GameSession gameSession) {
    gameSessionRedisRepository.deleteById(gameSession.getRoomId());
    log.info("Room {} removed and closed", gameSession.getRoomId());
  }

  @Override
  public GameSession findById(String id) {
    GameSessionDao gameSessionDao = gameSessionRedisRepository.findById(id)
        .orElseThrow(() -> new GameSessionException("방 정보를 찾을 수 없습니다"));

    return toEntity(gameSessionDao);
  }

  @Override
  public void update(GameSession update) {
    GameSessionDao updateDto = GameSessionDaoMapper.INSTANCE.toDao(update);
    gameSessionRedisRepository.save(updateDto);
  }

  @Override
  public GameSessionJoinRes addUser(String roomId, String nickname) {
    OpenViduRole role = OpenViduRole.PUBLISHER;

    String serverData = "{\"serverData\": \"" + nickname + "\"}";

    // Build connectionProperties object with the serverData and the role
    ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
        .type(ConnectionType.WEBRTC).data(serverData).role(role).build();

    GameSession gameSession = findById(roomId);
    try {
      // ex> wss://localhost:4443?sessionId=ses_Ogize1yQIj&token=tok_A1c0pNsLJFwVJTeb
      String token = gameSession.getSession().createConnection(connectionProperties).getToken();

      // ex> tok_A1c0pNsLJFwVJTeb
      String userId = UrlUtils.getUrlQueryParam(token, "token")
          .orElseThrow(() -> new GameSessionException(""))
          .substring(4);

      gameSession.getMapSessionNamesTokens().put(token, role);

      if (gameSession.getMapSessionNamesTokens().size() == 1) {
        gameSession.setMasterId(userId);
      }
      update(gameSession);

      return new GameSessionJoinRes(token, userId);
    } catch (OpenViduJavaClientException e1) {
      // If internal error generate an error message and return it to client
      return new GameSessionJoinRes("", "");
    } catch (OpenViduHttpException e2) {
      if (404 == e2.getStatus()) {
        removeGameSession(gameSession);
      }
      return new GameSessionJoinRes("", "");
    }
  }

  @Override
  public boolean removeUser(String roomId, String token) {
    GameSession gameSession = findById(roomId);
    Session session = gameSession.getSession();
    Map<String, OpenViduRole> mapSessionNamesTokens = gameSession.getMapSessionNamesTokens();

    // If the session exists ("TUTORIAL" in this case)
    if (session == null || mapSessionNamesTokens == null) {
      // The SESSION does not exist
      System.out.println("Problems in the app server: the SESSION does not exist");
      return false;
    }
    if (mapSessionNamesTokens.remove(token) == null) {
      // The TOKEN wasn't valid
      System.out.println("Problems in the app server: the TOKEN wasn't valid");
      return false;
    }
    // User left the session
    if (mapSessionNamesTokens.isEmpty()) {
      removeGameSession(gameSession);
    }
    return true;
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
      throw new GameSessionException("session을 찾을 수 없습니다");
    }

    Map<String, OpenViduRole> sessionNamesTokens = dao.getMapSessionNamesTokens();
    if (sessionNamesTokens == null) {
      sessionNamesTokens = new HashMap<>();
    }

    GameSession gameSession = GameSession.builder(dao.getRoomId(), dao.getCreatorEmail(),
        dao.getAccessType(), dao.getRoomType(), dao.getCreatedTime(), entitySession,
        sessionNamesTokens)
        .finishedTime(dao.getFinishedTime())
        .day(dao.getDay())
        .isNight(dao.isNight())
        .phase(dao.getPhase())
        .lastEnter(dao.getLastEnter())
        .state(dao.getState())
        .masterId(dao.getMasterId())
        .build();

    return gameSession;
  }


}