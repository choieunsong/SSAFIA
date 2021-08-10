package s05.p12a104.mafia.stomp.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
@RequiredArgsConstructor
public class GameSessionStompRejoinRes {

  private final StompMessageType type = StompMessageType.REJOIN;
  private final String hostId;
  private final GameStatus gameStatus;
  private final Map<String, StompExistingPlayer> playerMap;
  private final GameRole role;
  private final List<String> mafias;

  public static GameSessionStompRejoinRes of(GameSession gameSession, String reJoiningplayerId) {
    Map<String, StompExistingPlayer> playerMap = new HashMap<>();
    gameSession.getPlayerMap().forEach((playerId, player) ->
        playerMap.put(playerId, StompExistingPlayer.of(player))
    );

    Player rejoiningPlayer = gameSession.getPlayerMap().get(reJoiningplayerId);
    GameStatus gameStatus = GameStatus.of(gameSession);

    return new GameSessionStompRejoinRes(gameSession.getHostId(), gameStatus, playerMap,
        rejoiningPlayer.getRole(), gameSession.getMafias());
  }
}
