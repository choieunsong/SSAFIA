package s05.p12a104.mafia.redispubsub.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DayEliminationMessage {
  private String roomId;
  private String deadPlayerId;
}
