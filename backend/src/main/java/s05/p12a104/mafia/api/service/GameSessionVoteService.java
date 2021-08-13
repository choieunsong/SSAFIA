package s05.p12a104.mafia.api.service;

import java.util.Date;
import java.util.Map;
import s05.p12a104.mafia.domain.entity.Vote;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.stomp.request.GameSessionVoteReq;

public interface GameSessionVoteService {

  void startVote(String roomId, GamePhase phase, Date time, Map players);

  void endVote(String voteId, GamePhase phase);

  Vote vote(String roomId, String playerId, GameSessionVoteReq req);

  Vote nightVote(String roomId, String playerId, GameSessionVoteReq req, GameRole roleName);

  Vote getVote(String roomId, GameSessionVoteReq req);

  int confirmVote(String roomId, String playerId, GameSessionVoteReq req);

  void finishVote(String roomId, GameSessionVoteReq req);

}
