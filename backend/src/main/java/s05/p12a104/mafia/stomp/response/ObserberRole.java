package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import s05.p12a104.mafia.domain.enums.GameRole;

@Getter
public class ObserberRole {

  private GameRole role;

  public static ObserberRole of(GameRole role) {
    ObserberRole obserberRole = new ObserberRole();
    obserberRole.role = role;
    return obserberRole;
  }

}
