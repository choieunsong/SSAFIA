package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import lombok.Setter;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Setter
@Getter
public class SuspectVoteRes {
  private StompMessageType type;
  private String vote;
  private boolean isMafia;

  public static SuspectVoteRes of(Player suspect) {
    SuspectVoteRes suspectVoteRes = new SuspectVoteRes();
    suspectVoteRes.type = StompMessageType.SUSPECT;
    suspectVoteRes.vote = suspect.getId();
    suspectVoteRes.isMafia = suspect.getRole() == GameRole.MAFIA ? true : false;
    return suspectVoteRes;
  }
}
