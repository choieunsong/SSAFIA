package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import lombok.Setter;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.enums.GamePhase;

@Setter
@Getter
public class GameStatus {
  private int day;
  private GamePhase phase;
  private int timer;
  private int aliveMafia;

  public static GameStatus of(GameSession gameSession) {
    GameStatus gameStatus = new GameStatus();
    gameStatus.day = gameSession.getDay();
    gameStatus.phase = gameSession.getPhase();
    gameStatus.timer = gameSession.getTimer();
    gameStatus.aliveMafia = gameSession.getAliveMafia();
    return gameStatus;
  }

}
