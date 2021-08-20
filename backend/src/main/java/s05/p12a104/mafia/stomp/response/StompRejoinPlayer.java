package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
@ToString
@RequiredArgsConstructor
public class StompRejoinPlayer {
  private final StompMessageType type = StompMessageType.REJOIN;
  private final String rejoiningPlayerId;
  private final boolean alive;
  private final boolean suspicious;

  public static StompRejoinPlayer of(Player player) {
    return new StompRejoinPlayer(player.getId(), player.isAlive(), player.isSuspicious());
  }
}
