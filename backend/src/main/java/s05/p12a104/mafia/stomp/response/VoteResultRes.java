package s05.p12a104.mafia.stomp.response;

import java.util.Map;
import lombok.Getter;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
public class VoteResultRes {
  private StompMessageType type;
  private Map<String, String> playerMap;

  public static VoteResultRes of(Map<String, String> voteResult) {
    VoteResultRes voteResultRes = new VoteResultRes();
    voteResultRes.type = StompMessageType.UPDATE;
    voteResultRes.playerMap = voteResult;

    return voteResultRes;
  }
}
