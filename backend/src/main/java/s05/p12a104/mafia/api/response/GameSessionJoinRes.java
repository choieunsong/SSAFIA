package s05.p12a104.mafia.api.response;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ApiModel("GameSessionJoinResponse")
@Getter
@ToString
@RequiredArgsConstructor
public class GameSessionJoinRes {
  private final PlayerJoinRoomState state;
  private final String token;
  private final String playerId;
}
