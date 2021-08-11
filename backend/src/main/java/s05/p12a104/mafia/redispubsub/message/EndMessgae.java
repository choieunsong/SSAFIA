package s05.p12a104.mafia.redispubsub.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import s05.p12a104.mafia.stomp.response.GameResult;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EndMessgae {
  private String roomId;
  private GameResult gameResult;
}
