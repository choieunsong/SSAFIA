package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
public class GameSessionReadyRes {
  private StompMessageType type = StompMessageType.PHASE_CHANGED;
  private GameReadyStatus gameStatus;
  private String hostId;

  public static GameSessionReadyRes of(GameSession gameSession) {
    GameSessionReadyRes gameSessionReadyRes = new GameSessionReadyRes();
    gameSessionReadyRes.gameStatus = GameReadyStatus.of(gameSession);
    gameSessionReadyRes.hostId = gameSession.getHostId();
    return gameSessionReadyRes;
  }
}
