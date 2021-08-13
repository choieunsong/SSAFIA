package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
public class PlayerDeadRes {

  private StompMessageType type;
  private GameRole role;

  public static PlayerDeadRes of() {
    PlayerDeadRes playerRoleRes = new PlayerDeadRes();
    playerRoleRes.type = StompMessageType.ROLE;
    playerRoleRes.role = GameRole.OBSERVER;
    return playerRoleRes;
  }

}
