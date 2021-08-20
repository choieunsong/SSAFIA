package s05.p12a104.mafia.stomp.response;

import java.util.List;
import lombok.Getter;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
public class PlayerRoleRes {

  private StompMessageType type;
  private GameRole role;
  private List<String> mafias;

  public static PlayerRoleRes of(Player player, List<String> mafias) {
    PlayerRoleRes playerRoleRes = new PlayerRoleRes();
    playerRoleRes.type = StompMessageType.ROLE;
    playerRoleRes.role = player.getRole();
    playerRoleRes.mafias = mafias;
    return playerRoleRes;
  }

  public static PlayerRoleRes of(Player player) {
    PlayerRoleRes playerRoleRes = new PlayerRoleRes();
    playerRoleRes.type = StompMessageType.ROLE;
    playerRoleRes.role = player.getRole();
    return playerRoleRes;
  }

}
