package s05.p12a104.mafia.stomp.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import s05.p12a104.mafia.domain.enums.GameRole;

@Getter
@AllArgsConstructor
public class ObserverRole {

  private GameRole role;

}
