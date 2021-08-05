package s05.p12a104.mafia.stomp.response;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
public class GameStatusRes {

  private StompMessageType type;
  private GameStatus gameStatus;
  private Map<String, PlayerStatus> players;

  public static GameStatusRes of(GameSession gameSession) {
    GameStatusRes gameStatusRes = new GameStatusRes();
    gameStatusRes.type = StompMessageType.PHASE_CHANGED;
    gameStatusRes.gameStatus = GameStatus.of(gameSession);
    gameStatusRes.players = new HashMap<>();
    gameSession.getPlayerMap().forEach(
        (playerId, player) -> gameStatusRes.players.put(playerId, PlayerStatus.of(player)));
    return gameStatusRes;
  }

}
