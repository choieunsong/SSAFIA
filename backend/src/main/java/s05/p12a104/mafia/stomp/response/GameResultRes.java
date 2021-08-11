package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
public class GameResultRes {
  private StompMessageType type = StompMessageType.PHASE_CHANGED;
  private GameResult gameStatus;
  
  public static GameResultRes of(GameResult gameResult) {
    GameResultRes gameResultRes = new GameResultRes();
    gameResultRes.gameStatus = gameResult;
    return gameResultRes;
  }
}
