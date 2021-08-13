package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import lombok.Setter;
import s05.p12a104.mafia.common.util.TimeUtils;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.enums.GameState;

@Setter
@Getter
public class GameReadyStatus {
  private int day;
  private GameState phase;
  private int timer;
  private int aliveMafia;

  public static GameReadyStatus of(GameSession gameSession) {
    GameReadyStatus gameReadyStatus = new GameReadyStatus();
    gameReadyStatus.day = gameSession.getDay();
    gameReadyStatus.phase = gameSession.getState();
    gameReadyStatus.timer = TimeUtils.getRemainingTime(gameSession.getTimer());
    gameReadyStatus.aliveMafia = gameSession.getAliveMafia();
    return gameReadyStatus;
  }
}
