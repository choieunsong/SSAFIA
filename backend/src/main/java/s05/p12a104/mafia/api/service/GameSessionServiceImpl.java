package s05.p12a104.mafia.api.service;

import io.openvidu.java.client.ConnectionProperties;
import io.openvidu.java.client.ConnectionType;
import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import s05.p12a104.mafia.api.requset.GameSessionPostReq;
import s05.p12a104.mafia.api.response.GameSessionJoinRes;
import s05.p12a104.mafia.api.response.PlayerJoinRoomState;
import s05.p12a104.mafia.common.exception.AlreadyGameStartedException;
import s05.p12a104.mafia.common.exception.GameSessionNotFoundException;
import s05.p12a104.mafia.common.exception.OpenViduRuntimeException;
import s05.p12a104.mafia.common.exception.OpenViduSessionNotFoundException;
import s05.p12a104.mafia.common.exception.OverMaxIndividualRoomCountException;
import s05.p12a104.mafia.common.exception.OverMaxPlayerCountException;
import s05.p12a104.mafia.common.exception.OverMaxTotalRoomCountException;
import s05.p12a104.mafia.common.exception.PlayerNotLeftException;
import s05.p12a104.mafia.common.exception.RedissonLockNotAcquiredException;
import s05.p12a104.mafia.common.util.RoleUtils;
import s05.p12a104.mafia.common.util.RoomIdUtils;
import s05.p12a104.mafia.common.util.TimeUtils;
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
import s05.p12a104.mafia.redispubsub.RedisPublisher;
import s05.p12a104.mafia.redispubsub.message.EndMessgae;
import s05.p12a104.mafia.stomp.response.GameResult;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameSessionServiceImpl implements GameSessionService {

  private final GameSessionRedisRepository gameSessionRedisRepository;

  private final RedisKeyValueTemplate redisKVTemplate;

  private final RedisPublisher redisPublisher;

  private final RedissonClient redissonClient;

  private final ChannelTopic topicEnd;

  private final OpenVidu openVidu;

  private static final int MAX_TOTAL_ROOM_COUNT = 200;
  private static final int MAX_INDIVIDUAL_ROOM_COUNT = 2;
  private static final int MAX_PLAYER_COUNT = 10;
  private static final String KEY = "GameSession";

  @Override
  public GameSession makeGame(User user, GameSessionPostReq typeInfo)
      throws OpenViduJavaClientException, OpenViduHttpException {

    if (gameSessionRedisRepository.count() >= MAX_TOTAL_ROOM_COUNT) {
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
    return GameSession.of(gameSessionRedisRepository.save(newDao), openVidu);
  }

  @Override
  public GameSessionJoinRes getPlayerJoinableState(String roomId, String playerId) {

    RLock lock = redissonClient.getLock(KEY + roomId);
    boolean isLocked = false;
    try {
      isLocked = lock.tryLock(2, 3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (!isLocked) {
      throw new RedissonLockNotAcquiredException("Lock을 얻을 수 없습니다 - Key : " + KEY + roomId);
    }

    try {
      GameSession gameSession = findById(roomId);
      Player player = gameSession.getPlayerMap().get(playerId);
      if (player == null) {
        validateToBePossibleToJoin(gameSession);
        return new GameSessionJoinRes(PlayerJoinRoomState.JOINABLE, null, null);
      }

      if (!player.isLeft()) {
        throw new PlayerNotLeftException();
      }

      String token = createOpenViduToken(gameSession, player.getNickname());
      player.setToken(token);
      player.setLeft(false);
      player.setLeftPhaseCount(null);
      update(gameSession);
      return new GameSessionJoinRes(PlayerJoinRoomState.REJOIN, token, playerId);

    } finally {
      lock.unlock();
    }

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

    return GameSession.of(gameSessionDao, openVidu);
  }

  @Override
  public void update(GameSession update) {
    GameSessionDao updateDao = GameSessionDaoMapper.INSTANCE.toDao(update);
    redisKVTemplate.update(updateDao);
    log.info("Room {} updated", update.getRoomId());
  }

  @Override
  public GameSessionJoinRes addUser(String roomId, String nickname) {
    GameSession gameSession = findById(roomId);

    RLock lock = redissonClient.getLock(KEY + roomId);
    boolean isLocked = false;
    try {
      isLocked = lock.tryLock(2, 3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (!isLocked) {
      throw new RedissonLockNotAcquiredException("Lock을 얻을 수 없습니다 - Key : " + KEY + roomId);
    }

    try {
      validateToBePossibleToJoin(gameSession);
      String token = createOpenViduToken(gameSession, nickname);

      // ex> tok_A1c0pNsLJFwVJTeb
      String playerId = UrlUtils.getUrlQueryParam(token, "token")
          .orElseThrow(OpenViduRuntimeException::new).substring(4);

      Player player =
          Player.builder(playerId, nickname, getNewColor(gameSession)).token(token).build();

      gameSession.getPlayerMap().put(playerId, player);
      if (gameSession.getPlayerMap().size() == 1) {
        gameSession.setHostId(playerId);
      }
      update(gameSession);

      return new GameSessionJoinRes(PlayerJoinRoomState.JOIN, token, playerId);

    } finally {
      lock.unlock();
    }
  }

  @Override
  public GameSession removeUser(String roomId, String playerId) {
    GameSession gameSession = findById(roomId);

    RLock lock = redissonClient.getLock(KEY + roomId);
    boolean isLocked = false;
    try {
      isLocked = lock.tryLock(2, 3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (!isLocked) {
      throw new RedissonLockNotAcquiredException("Lock을 얻을 수 없습니다 - Key : " + KEY + roomId);
    }

    try {
      Session session = gameSession.getSession();
      Map<String, Player> playerMap = gameSession.getPlayerMap();

      if (session == null || playerMap == null) {
        log.info("Problems in the app server: the SESSION does not exist");
        throw new OpenViduSessionNotFoundException();
      }

      Player player = playerMap.get(playerId);
      if (player != null) {
        if (gameSession.getState() == GameState.STARTED) {
          removePlayer(gameSession, player);
        } else {
          removeReadyUser(gameSession, player);
        }
      }
      if (gameSessionRedisRepository.findById(roomId).isPresent()) {
        update(gameSession);
      }
      return gameSession;

    } finally {
      lock.unlock();
    }
  }

  /**
   * 게임 진행 중에 나간 Player 제거.
   *
   * @param gameSession : Player가 나간 Game Session
   * @param player : 나간 player
   */
  private void removePlayer(GameSession gameSession, Player player) {
    if (player.isLeft() || !player.isAlive()) {
      return;
    }

    player.setLeftPhaseCount(gameSession.getPhaseCount());
    player.setLeft(true);
    update(gameSession);

    final int TIME_TO_DIE = 30; // 30초
    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        RLock lock = redissonClient.getLock(KEY + gameSession.getRoomId());
        boolean isLocked = false;
        try {
          isLocked = lock.tryLock(2, 3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        if (!isLocked) {
          throw new RedissonLockNotAcquiredException(
              "Lock을 얻을 수 없습니다 - Key : " + KEY + gameSession.getRoomId());
        }

        try {
          if (!player.isLeft()) {
            return;
          }
          GameSession gameSessionTemp = findById(gameSession.getRoomId());
          if (gameSessionTemp.getState() == GameState.READY) {
            return;
          }

          Player playerTemp = gameSessionTemp.getPlayerMap().get(player.getId());
          if (!playerTemp.isLeft()) {
            return;
          }

          String playerId = playerTemp.getId();
          gameSessionTemp.eliminatePlayer(playerId);
          update(gameSessionTemp);

        } finally {
          lock.unlock();
        }
      }
    };
    timer.schedule(timerTask, TIME_TO_DIE * 1000);
  }

  /**
   * 아직 게임이 시작하지 않은 방에서 나간 Player 제거.
   *
   * @param gameSession : Player가 나간 Game Session
   * @param player      : 나간 player
   */
  private void removeReadyUser(GameSession gameSession, Player player) {
    Map<String, Player> playerMap = gameSession.getPlayerMap();
    if (playerMap.remove(player.getId()) == null) {
      log.info("Problems in the app server: the Player Id - {} wasn't valid", player.getId());
    }
    // User left the session
    if (playerMap.isEmpty()) {
      removeGameSession(gameSession);
    } else {
      if (player.getId().equals(gameSession.getHostId())) {
        // 다음 player를 방장으로 등록
        gameSession.setHostRandomly();
      }
    }
  }

  private String createOpenViduToken(GameSession gameSession, String nickname) {
    String serverData = "{\"serverData\": \"" + nickname + "\"}";

    // Build connectionProperties object with the serverData and the role
    ConnectionProperties connectionProperties = new ConnectionProperties.Builder()
        .type(ConnectionType.WEBRTC).data(serverData).role(OpenViduRole.PUBLISHER).build();
    try {
      // ex> wss://localhost:4443?sessionId=ses_Ogize1yQIj&token=tok_A1c0pNsLJFwVJTeb
      return gameSession.getSession().createConnection(connectionProperties).getToken();
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
    RLock lock = redissonClient.getLock(KEY + gameSession.getRoomId());
    boolean isLocked = false;
    try {
      isLocked = lock.tryLock(2, 3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    if (!isLocked) {
      throw new RedissonLockNotAcquiredException(
          "Lock을 얻을 수 없습니다 - Key : " + KEY + gameSession.getRoomId());
    }

    try {
      // 각 역할에 맞는 인원수 구하기
      Map<GameRole, Integer> roleNum = RoleUtils.getRoleNum(gameSession);

      // game 초기 setting
      gameSession.setState(GameState.STARTED);
      gameSession.setDay(0);
      gameSession.setAliveMafia(roleNum.get(GameRole.MAFIA));
      gameSession.setPhase(GamePhase.START);
      gameSession.setTimer(TimeUtils.getFinTime(15));

      // player 초기화
      gameSession.getPlayerMap().forEach((playerId, player) -> {
        player.setAlive(true);
        player.setSuspicious(false);
      });

      // alive player 초기화
      gameSession.setAlivePlayer(gameSession.getPlayerMap().size());

      // 역할 부여
      gameSession.setMafias(RoleUtils.assignRole(roleNum, gameSession.getPlayerMap()));

      // alive Not Civilian 초기화
      int notCivilianCnt = gameSession.getPlayerMap().entrySet().stream()
          .filter(e -> e.getValue().getRole() != GameRole.CIVILIAN).collect(Collectors.toList())
          .size();
      gameSession.setAliveNotCivilian(notCivilianCnt);

      // redis에 저장
      update(gameSession);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean isDone(GameSession gameSession, List<String> victims) {
    GameResult gameResult = GameResult.of(gameSession, victims);
    if (gameResult.getWinner() == null) {
      return false;
    }

    redisPublisher.publish(topicEnd, new EndMessgae(gameSession.getRoomId(), gameResult));
    return true;
  }

  @Override
  public void endGame(GameSession gameSession) {
    // 중간에 나간 사람, 다시 들어오지 않은 사람 playerMap에서 제거 -> leave 처리
    // 그러면 hostId가 처리가 되겠지..?
    Map<String, Player> playerMap = gameSession.getPlayerMap();
    List<Player> removePlayers = new ArrayList<>();
    for (Player player : playerMap.values()) {
      if (player.getLeftPhaseCount() == null) {
        continue;
      }
      removePlayers.add(player);
    }

    for (Player player : removePlayers) {
      removeReadyUser(gameSession, player);
    }

    if (gameSessionRedisRepository.findById(gameSession.getRoomId()).isPresent()) {
      gameSession.setState(GameState.READY);
      gameSession.setTimer(TimeUtils.getFinTime(0));
      gameSession.setDay(0);
      gameSession.setAliveMafia(0);

      update(gameSession);
    }
  }

  @Override
  public Map<String, GameRole> addObserver(String roomId, String playerId) {
    Map<String, GameRole> observer;
    Map<String, Player> playerMap = findById(roomId).getPlayerMap();

    observer = playerMap.entrySet().stream().filter(e -> e.getValue().getRole() != null)
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getRole()));

    return observer;
  }
}
