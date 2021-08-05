package s05.p12a104.mafia.domain.enums;

import java.util.Random;

public enum GameRole {
  CIVILIAN,
  MAFIA,
  DOCTOR,
  POLICE,
  OBSERVER;
  
  public static GameRole getRandomRole() {
    Random random = new Random();
    return values()[random.nextInt(values().length-1)];
  }
}
