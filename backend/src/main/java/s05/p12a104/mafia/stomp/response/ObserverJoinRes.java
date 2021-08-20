package s05.p12a104.mafia.stomp.response;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
public class ObserverJoinRes {

  private StompMessageType type;
  private Map<String, ObserverRole> playerMap;

  public static ObserverJoinRes of(Map<String, GameRole> playerMap) {
    ObserverJoinRes playerRoleRes = new ObserverJoinRes();
    playerRoleRes.type = StompMessageType.DEAD;
    playerRoleRes.playerMap = new HashMap<String, ObserverRole>();
    playerMap.forEach((playerId, role) -> {
      playerRoleRes.playerMap.put(playerId, new ObserverRole(role));
    });
    return playerRoleRes;
  }

}
