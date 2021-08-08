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

  @Override
  public void startVote(String roomId, GamePhase phase, int time) {
    createVote(roomId, phase);
    Timer timer = new Timer();
    DayDiscussionVoteFinTimerTask task = new DayDiscussionVoteFinTimerTask(this);
    task.setRoomId(roomId);
    task.setPhase(phase);;
    timer.schedule(task, time * 1000);
  }

  @Override
  public void endVote(String roomId, GameSessionVoteReq req) {
    String voteId = getVoteId(roomId, req);
    publishRedis(roomId);
    voteRepository.deleteVote(voteId);
  }

  @Override
  public void endVote(String roomId, GamePhase phase) {
    String voteId = getVoteId(roomId, phase);
    if (voteRepository.findVoteById(voteId) == null) {
      return;
    } else {
      publishRedis(roomId);
      voteRepository.deleteVote(voteId);
    }
  }

  @Override
  public Vote vote(String roomId, String playerId, GameSessionVoteReq req) {

    String voteId = getVoteId(roomId, req);

    if (voteRepository.findVoteById(voteId) == null) {
      return null;
    }

    return voteRepository.vote(voteId, playerId, req.getVote());
  }

  @Override
  public Vote getVote(String roomId, GameSessionVoteReq req) {
    String voteId = getVoteId(roomId, req);
    return voteRepository.findVoteById(voteId);
  }

  @Override
  public int confirmVote(String roomId, String playerId, GameSessionVoteReq req) {

    String voteId = getVoteId(roomId, req);

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

  private String getVoteId(String roomId, GameSessionVoteReq req) {
    String voteId = roomId + req.getPhase();
    return voteId;
  }

  private String getVoteId(String roomId, GamePhase phase) {
    String voteId = roomId + phase;
    return voteId;
  }

  private void publishRedis(String roomId) {
    redisPublisher.publish(new ChannelTopic("DAY_DISCUSSION_FIN"), roomId);
  }
}
