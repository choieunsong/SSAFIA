package s05.p12a104.mafia.api.response;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiModel("GameSessionJoinResponse")
@Getter
@RequiredArgsConstructor
public class GameSessionJoinRes {
  private final PlayerJoinRoomState state;
  private final String token;
  private final String playerId;
}
