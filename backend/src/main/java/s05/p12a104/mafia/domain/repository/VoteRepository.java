package s05.p12a104.mafia.domain.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Repository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.domain.entity.Vote;
import s05.p12a104.mafia.domain.enums.GamePhase;

@Getter
@RequiredArgsConstructor
@Repository
public class VoteRepository {

  private final RedisTemplate<String, Vote> redisTemplate;
  private HashOperations<String, String, Vote> opsHashVote;
  private Map<String, ChannelTopic> topics;
  private final String key = "Vote";

  @PostConstruct
  private void init() {
    opsHashVote = redisTemplate.opsForHash();
    topics = new HashMap<>();
  }

  public List<Vote> findAllVote() {
    return opsHashVote.values(key);
  }

  public Vote findVoteById(String voteId) {
    return opsHashVote.get(key, voteId);
  }

  public Vote createVote(String roomId, GamePhase phase, Map players) {
    String voteId = roomId + phase.toString();
    Vote vote = Vote.builder(voteId, phase, players);
    opsHashVote.put(key, voteId, vote);
    return vote;
  }

  public void deleteVote(String voteId) {
    opsHashVote.delete(key, voteId);
  }

  public void finishVote(String roomId, GamePhase phase) {
    String voteId = roomId + phase.toString();
    opsHashVote.delete(key, voteId);
  }

  public Vote vote(String voteId, String playerId, String player) {
    Vote vote = opsHashVote.get(key, voteId);
    vote.getVoteResult().put(playerId, player);
    opsHashVote.put(key, voteId, vote);
    return vote;
  }

  public int confirm(String voteId, String playerId) {
    Vote vote = opsHashVote.get(key, voteId);
    int confirmCnt = vote.incrConfirm();
    opsHashVote.put(key, voteId, vote);
    return confirmCnt;
  }

  public ChannelTopic getTopic(String voteId) {
    return topics.get(voteId);
  }
}
