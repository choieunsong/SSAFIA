package s05.p12a104.mafia.stomp.response;

import java.util.Map;
import lombok.Getter;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
public class ConfirmResultRes {
  private StompMessageType type;
  private Map<String, Boolean> playerMap;

  public static ConfirmResultRes of(Map<String, Boolean> confirmResult) {
    ConfirmResultRes confirmResultRes = new ConfirmResultRes();
    confirmResultRes.type = StompMessageType.CONFIRM;
    confirmResultRes.playerMap = confirmResult;

    return confirmResultRes;
  }
}
