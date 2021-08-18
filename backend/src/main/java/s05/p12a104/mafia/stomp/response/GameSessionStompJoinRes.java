package s05.p12a104.mafia.stomp.response;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
@RequiredArgsConstructor
@ToString
public class GameSessionStompJoinRes {

  private final StompMessageType type = StompMessageType.JOIN;
  private final String hostId;
  private final Map<String, PlayerStompJoinRes> playerMap;

  public static GameSessionStompJoinRes of(GameSession gameSession) {
    Map<String, PlayerStompJoinRes> newPlayerMap = new HashMap<>();
    gameSession.getPlayerMap().forEach((playerId, player) ->
        newPlayerMap.put(playerId, PlayerStompJoinRes.of(player))
    );
    return new GameSessionStompJoinRes(gameSession.getHostId(), newPlayerMap);
  }
}
