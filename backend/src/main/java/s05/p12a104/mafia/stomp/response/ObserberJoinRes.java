package s05.p12a104.mafia.stomp.response;

import java.util.Map;
import lombok.Getter;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
public class ObserberJoinRes {

  private StompMessageType type;
  private Map<String, GameRole> playerMap;

  public static ObserberJoinRes of(Map playerMap) {
    ObserberJoinRes playerRoleRes = new ObserberJoinRes();
    playerRoleRes.type = StompMessageType.DEAD;
    playerRoleRes.playerMap = playerMap;
    return playerRoleRes;
  }

}
