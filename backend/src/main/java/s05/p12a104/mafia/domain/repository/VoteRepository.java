package s05.p12a104.mafia.domain.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.domain.entity.Vote;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;

@RequiredArgsConstructor
@Repository
public class VoteRepository {

  private final VoteRedisRepository voteRedisRepository;
  private Map<String, Map<String, GameRole>> votersMap;

  @PostConstruct
  private void init() {
    votersMap = new HashMap();
  }

  public void startVote(String roomId, GamePhase phase, Map<String, GameRole> players) {
    votersMap.put(roomId, players);
    voteRedisRepository.startVote(getVoters(roomId), phase);
  }

  public boolean isValid(String playerId, GamePhase phase) {
    return voteRedisRepository.isExist(playerId) == true
        ? (voteRedisRepository.getVote(playerId).getPhase() == phase ? true : false)
        : false;
  }

  public Map<String, String> getVoteResult(String roomId) {
    return voteResultConvert(getVoteResult(getVoters(roomId)));
  }

  public Map<String, String> vote(String roomId, GamePhase phase, String playerId, String player) {
    voteRedisRepository.vote(playerId, player);

    return voteResultConvert(getVoteResult(getVoters(roomId)));
  }

  public Map<String, String> nightVote(String roomId, GamePhase phase, String playerId,
      String player, GameRole roleName) {
    voteRedisRepository.vote(playerId, player);

    return voteResultConvert(getVoteResult(getNightVoters(roomId, roleName)));
  }

  public int confirmVote(String roomId, String playerId) {
    voteRedisRepository.confirmVote(playerId);
    Map<String, Vote> voteResult = getVoteResult(getVoters(roomId));

    int confirmCnt = voteResult.entrySet().stream().filter(e -> e.getValue().isConfirm() == true)
        .collect(Collectors.toList()).size();
    return confirmCnt;
  }

  public void endVote(String roomId, GamePhase phase) {
    voteRedisRepository.endVote(getVoters(roomId), phase);
  }

  private List<String> getVoters(String roomId) {
    return votersMap.get(roomId).keySet().stream().collect(Collectors.toList());
  }

  private List<String> getNightVoters(String roomId, GameRole roleName) {
    Map<String, GameRole> voters = votersMap.get(roomId);
    return voters.keySet().stream().collect(Collectors.toList()).stream()
        .filter(key -> voters.get(key) == roleName).collect(Collectors.toList());
  }

  private Map<String, String> voteResultConvert(Map<String, Vote> voteResult) {
    Map<String, String> result = new HashMap<String, String>();
    voteResult.forEach((playerId, vote) -> {
      result.put(playerId, vote.getVote());
    });
    return result;
  }

  private Map<String, Vote> getVoteResult(List<String> voters) {
    return voteRedisRepository.getVoteResult(voters);
  }
}
