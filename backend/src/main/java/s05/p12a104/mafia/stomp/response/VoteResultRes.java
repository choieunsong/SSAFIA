package s05.p12a104.mafia.stomp.response;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
public class VoteResultRes {
  private StompMessageType type;
  private Map<String, String> playerMap;

  public static VoteResultRes of(GameSession gameSession) {
    VoteResultRes voteResultRes = new VoteResultRes();
    voteResultRes.type = StompMessageType.UPDATE;
    voteResultRes.playerMap = new HashMap<>();
    gameSession.getPlayerMap().forEach(
        (playerId, player) -> voteResultRes.playerMap.put(playerId, player.getVote()));

    return voteResultRes;
  }
}
