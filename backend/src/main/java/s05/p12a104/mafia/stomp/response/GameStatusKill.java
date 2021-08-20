package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import lombok.Setter;
import s05.p12a104.mafia.common.util.TimeUtils;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;

@Setter
@Getter
public class GameStatusKill {
  private int day;
  private GamePhase phase;
  private int timer;
  private int aliveMafia;
  private String victim = null;
  private boolean victimIsMafia;

  public static GameStatusKill of(GameSession gameSession, Player dead) {
    GameStatusKill gameStatus = new GameStatusKill();
    gameStatus.day = gameSession.getDay();
    gameStatus.phase = gameSession.getPhase();
    gameStatus.timer = TimeUtils.getRemainingTime(gameSession.getTimer());
    gameStatus.aliveMafia = gameSession.getAliveMafia();
    if (dead != null) {
      gameStatus.victim = dead.getId();
      gameStatus.victimIsMafia = dead.getRole() == GameRole.MAFIA ? true : false;
    }
    return gameStatus;
  }

}
