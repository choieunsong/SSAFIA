package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import s05.p12a104.mafia.domain.enums.GameRole;

@Getter
public class ObserverRole {

  private GameRole role;

  public static ObserverRole of(GameRole role) {
    ObserverRole obserberRole = new ObserverRole();
    obserberRole.role = role;
    return obserberRole;
  }

}
