package s05.p12a104.mafia.stomp.response;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
public class ObserberJoinRes {

  private StompMessageType type;
  private Map<String, ObserberRole> playerMap;

  public static ObserberJoinRes of(Map<String, GameRole> playerMap) {
    ObserberJoinRes playerRoleRes = new ObserberJoinRes();
    playerRoleRes.type = StompMessageType.DEAD;
    playerRoleRes.playerMap = new HashMap();
    playerMap.forEach((playerId, role) -> {
      playerRoleRes.playerMap.put(playerId, ObserberRole.of(role));
    });
    return playerRoleRes;
  }

}
