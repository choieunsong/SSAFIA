package s05.p12a104.mafia.domain.entity;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import s05.p12a104.mafia.domain.enums.GameRole;

@Setter
@Getter
@ToString
@Builder
public class VoteInfo {
  private int phaseCount;
  private Map<String, GameRole> votersMap;
  
  public static VoteInfo builder(int phaseCount, Map<String, GameRole> votersMap) {
    return new VoteInfoBuilder().phaseCount(phaseCount).votersMap(votersMap).build();
  }
}
