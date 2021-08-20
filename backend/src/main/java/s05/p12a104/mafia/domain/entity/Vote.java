package s05.p12a104.mafia.domain.entity;

import java.io.Serializable;
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
  private String playerId;

  @Enumerated(EnumType.STRING)
  private GamePhase phase;

  private String vote;

  private boolean confirm;

  public static Vote builder(String playerId, GamePhase phase) {
    return new VoteBuilder().playerId(playerId).phase(phase).vote(null).confirm(false).build();
  }

}
