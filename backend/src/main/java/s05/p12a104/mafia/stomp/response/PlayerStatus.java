package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import s05.p12a104.mafia.domain.entity.Player;

@Getter
public class PlayerStatus {
  boolean alive;
  boolean suspicious;

  public static PlayerStatus of(Player player) {
    PlayerStatus playerStatus = new PlayerStatus();
    playerStatus.alive = player.isAlive();
    playerStatus.suspicious = player.isSuspicious();
    return playerStatus;
  }
}
