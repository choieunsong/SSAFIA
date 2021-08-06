package s05.p12a104.mafia.stomp.request;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Getter;
import lombok.NonNull;
import s05.p12a104.mafia.domain.enums.GamePhase;

@Getter
public class GameSessionVoteReq {
  @Enumerated(EnumType.STRING)
  private GamePhase phase;

  @NonNull
  String vote;
}
