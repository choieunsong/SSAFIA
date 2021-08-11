package s05.p12a104.mafia.redispubsub.message;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import s05.p12a104.mafia.domain.enums.GameRole;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NightVoteMessage {
  private String roomId;
  private Map<GameRole, String> roleVoteResult;
}
