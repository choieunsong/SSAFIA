package s05.p12a104.mafia.api.service;

import s05.p12a104.mafia.domain.entity.Vote;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.vote.GameSessionVoteReq;

public interface GameSessionVoteService {
  void createVote(String roomId, GamePhase phase);

  void startTimer(String roomId);
  
  Vote vote(String roomId, String playerId, GameSessionVoteReq req);

  Vote getVote(String roomId, GameSessionVoteReq req);

  int confirmVote(String roomId, String playerId, GameSessionVoteReq req);

  void finishVote(String roomId, GameSessionVoteReq req);

  void publishRedis(String roomId);
}
