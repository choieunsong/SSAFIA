package s05.p12a104.mafia.stomp.service;

import java.time.LocalDateTime;
import java.util.Map;
import s05.p12a104.mafia.domain.entity.Vote;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.stomp.request.GameSessionVoteReq;

public interface GameSessionVoteService {

  void startVote(String roomId, int phaseCount, GamePhase phase, LocalDateTime time, Map players);

  void endVote(String voteId, int phaseCount, GamePhase phase);

  Map<String, String> vote(String roomId, String playerId, GameSessionVoteReq req);

  Map<String, String> nightVote(String roomId, String playerId, GameSessionVoteReq req,
      GameRole roleName);

  Map<String, Boolean> confirmVote(String roomId, String playerId, GameSessionVoteReq req);

  Map<String, Boolean> getNightConfirm(String roomId, String playerId, GameSessionVoteReq req,
      GameRole roleName);

  Map<String, String> getVoteResult(String roomId, GameSessionVoteReq req);
}
