package s05.p12a104.mafia.redispubsub.message;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DayDiscussionMessage {
  private String roomId;
  private List<String> suspiciousList;
}
