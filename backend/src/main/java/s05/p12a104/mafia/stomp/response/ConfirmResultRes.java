package s05.p12a104.mafia.stomp.response;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
public class ConfirmResultRes {
  private StompMessageType type;
  private Map<String, Confirm> playerMap;

  public static ConfirmResultRes of(Map<String, Boolean> confirmResult) {
    ConfirmResultRes confirmResultRes = new ConfirmResultRes();
    confirmResultRes.type = StompMessageType.CONFIRM;
    confirmResultRes.playerMap = new HashMap<String, Confirm>();
    confirmResult.forEach((playerId, confirm) -> {
      confirmResultRes.playerMap.put(playerId, new Confirm(confirm));
    });

    return confirmResultRes;
  }
}
