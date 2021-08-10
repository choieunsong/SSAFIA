package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.enums.GameRole;

@Getter
public class SuspectVoteRes {
  private String vote;
  private boolean isMafia;

  public static SuspectVoteRes of(Player suspect) {
    SuspectVoteRes suspectVoteRes = new SuspectVoteRes();
    suspectVoteRes.vote = suspect.getId();
    suspectVoteRes.isMafia = suspect.getRole() == GameRole.MAFIA ? true : false;
    return suspectVoteRes;
  }
}
