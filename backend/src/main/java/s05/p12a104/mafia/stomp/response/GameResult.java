package s05.p12a104.mafia.stomp.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;

@Getter
@Setter
@ToString
public class GameResult {
  private GamePhase phase = GamePhase.END;
  private GameRole winner;
  private int timer;
  private boolean turnOver;
  private List<String> victims;

  public static GameResult of(GameSession gameSession, List<String> victims) {
    GameResult gameResult = new GameResult();
    gameResult.timer = 15;
    
    int aliveMafia = gameSession.getAliveMafia();
    int aliveCivilian = gameSession.getAlivePlayer() - aliveMafia;
    int day = gameSession.getDay();
    
    // 마피아 >= 시민인 경우
    if (aliveMafia >= aliveCivilian) {
      gameResult.winner = GameRole.MAFIA;
    }
    // 모든 마피아를 제거한 경우
    if (aliveMafia == 0) {
      gameResult.winner = GameRole.CIVILIAN;
    }

    // 15턴 모두 소요된 경우
    if (day > 15) {
      gameResult.winner = GameRole.CIVILIAN;
      gameResult.turnOver = true;
    }
    
    gameResult.victims = victims;

    return gameResult;
  }
}
