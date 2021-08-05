package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.domain.entity.Color;
import s05.p12a104.mafia.domain.entity.Player;

@Getter
@RequiredArgsConstructor
public class PlayerStompJoinRes {

  private final String nickname;
  private final Color color;

  public static PlayerStompJoinRes of(Player player) {
    return new PlayerStompJoinRes(player.getNickname(), player.getColor());
  }
}
