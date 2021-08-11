package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
}
