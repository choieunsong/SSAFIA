package s05.p12a104.mafia.api.response;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.enums.Color;

@ApiModel("GameSessionJoinResponse")
@Getter
@ToString
@RequiredArgsConstructor
public class GameSessionJoinRes {

  private final PlayerJoinRoomState state;
  private final String token;
  private final String playerId;
  private final String nickname;
  private final Color color;

  public GameSessionJoinRes(PlayerJoinRoomState state, Player player) {
    this.state = state;
    this.token = player.getToken();
    this.playerId = player.getId();
    this.nickname = player.getNickname();
    this.color = player.getColor();
  }
}
