package s05.p12a104.mafia.domain.entity;

import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.Session;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.common.exception.OpenViduSessionNotFoundException;
import s05.p12a104.mafia.domain.dao.GameSessionDao;
import s05.p12a104.mafia.domain.enums.AccessType;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.domain.enums.GameState;
import s05.p12a104.mafia.domain.enums.RoomType;
import s05.p12a104.mafia.stomp.response.GameResult;

@Setter
@Getter
@Builder
@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GameSession {

  @NonNull
  private final String roomId;

  private int day;

  private int phaseCount;

  private boolean isNight;

  private int aliveMafia;

  private int alivePlayer;

  private int aliveNotCivilian;

  private int timer;

  private List<String> mafias;

  @NonNull
  private final String creatorEmail;

  private Map<String, Player> playerMap;

  @NonNull
  @Enumerated(EnumType.STRING)
  private final AccessType accessType;

  @NonNull
  @Enumerated(EnumType.STRING)
  private RoomType roomType;

  @NonNull
  @Enumerated(EnumType.STRING)
  private GameState state;

  @Enumerated(EnumType.STRING)
  private GamePhase phase;

  @NonNull
  private final LocalDateTime createdTime;

  @NonNull
  private LocalDateTime finishedTime;

  private String lastEnter;

  @NonNull
  private final Session session;

  private String hostId;

  public static GameSessionBuilder builder(String roomId, String creatorEmail,
      AccessType accessType, RoomType roomType, LocalDateTime createdTime, Session session,
      Map<String, Player> playerMap) {
    return new GameSessionBuilder().roomId(roomId).creatorEmail(creatorEmail).accessType(accessType)
        .roomType(roomType).createdTime(createdTime).session(session).state(GameState.READY)
        .playerMap(playerMap);
  }

  public void setHostRandomly() {
    setHostId(playerMap.keySet().iterator().next());
  }

  public void eliminatePlayer(String playerId) {
    Player player = playerMap.get(playerId);
    if (!player.isAlive()) {
      return;
    }

    player.setAlive(false);
    alivePlayer--;
    if (player.getRole() == GameRole.MAFIA) {
      aliveMafia--;
    }
    player.setRole(GameRole.OBSERVER);
    playerMap.put(playerId, player);
  }

  public void changePhase(GamePhase phase, int timer) {
    this.phase = phase;
    this.phaseCount++;
    setTimer(timer);

    Map<String, Player> playerMap = getPlayerMap();
    playerMap.forEach((playerId, player) -> {
      Integer leftPhaseCount = player.getLeftPhaseCount();
      if (leftPhaseCount == null || leftPhaseCount >= this.phaseCount) {
        return;
      }
      player.setLeftPhaseCount(null);
      eliminatePlayer(playerId);
    });
  }

  public void passADay() {
    this.day++;
  }

  public GameResult getGameResult() {
    GameResult gameResult = new GameResult();
    gameResult.setTimer(15);
    // 마피아 >= 시민인 경우
    if (aliveMafia >= alivePlayer - aliveMafia) {
      gameResult.setWinner(GameRole.MAFIA);
    }
    // 모든 마피아를 제거한 경우
    if (aliveMafia == 0) {
      gameResult.setWinner(GameRole.CIVILIAN);
    }

    // 15턴 모두 소요된 경우
    if (day >= 15) {
      gameResult.setWinner(GameRole.CIVILIAN);
      gameResult.setTurnOver(true);
    }

    return gameResult;
  }

  public static GameSession of(GameSessionDao dao, OpenVidu openVidu) {
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

    List<String> mafias = dao.getMafias();
    if (mafias == null) {
      mafias = new ArrayList<>();
    }

    GameSession gameSession = GameSession
        .builder(dao.getRoomId(), dao.getCreatorEmail(), dao.getAccessType(), dao.getRoomType(),
            dao.getCreatedTime(), entitySession, playerMap)
        .finishedTime(dao.getFinishedTime()).day(dao.getDay()).isNight(dao.isNight())
        .aliveMafia(dao.getAliveMafia()).timer(dao.getTimer()).phase(dao.getPhase())
        .lastEnter(dao.getLastEnter()).state(dao.getState()).mafias(mafias)
        .alivePlayer(dao.getAlivePlayer()).hostId(dao.getHostId())
        .aliveNotCivilian(dao.getAliveNotCivilian()).build();

    return gameSession;
  }
}
