package s05.p12a104.mafia.api.service;

import s05.p12a104.mafia.domain.entity.Vote;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.stomp.request.GameSessionVoteReq;

public interface GameSessionVoteService {
  void createVote(String roomId, GamePhase phase);

  void startVote(String roomId, GamePhase phase, int time);

  void endVote(String roomId, GameSessionVoteReq req);

  void endVote(String voteId, GamePhase phase);

  Vote vote(String roomId, String playerId, GameSessionVoteReq req);

  Vote getVote(String roomId, GameSessionVoteReq req);

  int confirmVote(String roomId, String playerId, GameSessionVoteReq req);

  void finishVote(String roomId, GameSessionVoteReq req);

}
