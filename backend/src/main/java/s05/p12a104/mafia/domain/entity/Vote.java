package s05.p12a104.mafia.domain.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import s05.p12a104.mafia.domain.enums.GamePhase;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vote implements Serializable {

  @Id
  private String voteId;

  @Enumerated(EnumType.STRING)
  private GamePhase phase;

  private int confirmCnt;

  private Map<String, String> voteResult;

  public int incrConfirm() {
    return ++this.confirmCnt;
  }

  public static Vote builder(String voteId, GamePhase phase, Map players) {
    return new VoteBuilder().voteId(voteId).phase(phase).confirmCnt(0).voteResult(players)
        .build();
  }

}
