package s05.p12a104.mafia.api.service;

import java.util.Timer;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.domain.entity.Vote;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.repository.VoteRepository;
import s05.p12a104.mafia.redispubsub.RedisPublisher;
import s05.p12a104.mafia.stomp.request.GameSessionVoteReq;
import s05.p12a104.mafia.stomp.task.DayDiscussionVoteFinTimerTask;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameSessionVoteServiceImpl implements GameSessionVoteService {

  private final RedisPublisher redisPublisher;
  private final VoteRepository voteRepository;
  private DayDiscussionVoteFinTimerTask task;

  @Override
  public void startVote(String roomId) {
    Timer timer = new Timer();
    task = new DayDiscussionVoteFinTimerTask(redisPublisher);
    task.setRoomId(roomId);
    timer.schedule(task, 15 * 1000);
  }

  @Override
  public void endVote(String roomId, GameSessionVoteReq req) {
    task.cancel();
    String voteId = getVoteId(roomId, req.getPhase());
    voteRepository.deleteVote(voteId);
  }

  @Override
  public Vote vote(String roomId, String playerId, GameSessionVoteReq req) {

    String voteId = getVoteId(roomId, req.getPhase());

    if (voteRepository.findVoteById(voteId) == null) {
      return null;
    }

    voteRepository.vote(voteId, playerId, req.getVote());

    return voteRepository.findVoteById(voteId);
  }

  @Override
  public Vote getVote(String roomId, GameSessionVoteReq req) {
    String voteId = getVoteId(roomId, req.getPhase());
    return voteRepository.findVoteById(voteId);
  }

  @Override
  public int confirmVote(String roomId, String playerId, GameSessionVoteReq req) {

    String voteId = getVoteId(roomId, req.getPhase());

    return voteRepository.confirm(voteId, playerId);
  }

  @Override
  public void createVote(String roomId, GamePhase phase) {
    voteRepository.createVote(roomId, phase);
  }

  @Override
  public void finishVote(String roomId, GameSessionVoteReq req) {
    voteRepository.finishVote(roomId, req.getPhase());
  }

  @Override
  public void publishRedis(String roomId) {
    redisPublisher.publish(new ChannelTopic("VOTE_FIN"), roomId);
  }

  private String getVoteId(String roomId, GamePhase phase) {
    String voteId = roomId + phase.toString();
    return voteId;
  }
}
