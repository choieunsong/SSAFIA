package s05.p12a104.mafia.domain.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.domain.entity.Vote;
import s05.p12a104.mafia.domain.entity.VoteInfo;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;

@RequiredArgsConstructor
@Slf4j
@Repository
public class VoteRepository {

  private final VoteRedisRepository voteRedisRepository;
  private ConcurrentHashMap<String, VoteInfo> voteInfosMap;

  @PostConstruct
  private void init() {
    voteInfosMap = new ConcurrentHashMap<String, VoteInfo>();
  }

  public void startVote(String roomId, int phaseCount, GamePhase phase,
      Map<String, GameRole> players) {
    VoteInfo voteInfo = VoteInfo.builder(phaseCount, players);
    voteInfosMap.put(roomId, voteInfo);
    log.info("Room {} VoteInfo {} Updated for {}", roomId, voteInfo.toString(), phase);
    voteRedisRepository.startVote(getVoters(roomId), phase);
  }

  public boolean isEnd(String roomId, int phaseCount) {
    VoteInfo voteInfo = voteInfosMap.get(roomId);
    if (voteInfo == null || voteInfo.getPhaseCount() != phaseCount) {
      return true;
    }
    return false;
  }

  public boolean isValid(String playerId, GamePhase phase) {
    return voteRedisRepository.isExist(playerId) == true
        ? (voteRedisRepository.getVote(playerId).getPhase() == phase ? true : false)
        : false;
  }

  public Map<String, String> getVoteResult(String roomId) {
    return voteResultConvert(getRedisVoteResult(getVoters(roomId)));
  }

  public Map<String, String> vote(String roomId, String playerId, String player) {
    voteRedisRepository.vote(playerId, player);

    return voteResultConvert(getRedisVoteResult(getVoters(roomId)));
  }

  public Map<String, String> nightVote(String roomId, String playerId, String player,
      GameRole roleName) {
    voteRedisRepository.vote(playerId, player);

    return voteResultConvert(getRedisVoteResult(getNightVoters(roomId, roleName)));
  }

  public Map<String, Boolean> confirmVote(String roomId, String playerId) {
    voteRedisRepository.confirmVote(playerId);

    return confirmResultConvert(getRedisVoteResult(getVoters(roomId)));
  }

  public Map<String, Boolean> getConfirm(String roomId, String playerId) {
    return confirmResultConvert(getRedisVoteResult(getVoters(roomId)));
  }

  public Map<String, Boolean> getNightConfirm(String roomId, GameRole roleName) {
    return confirmResultConvert(getRedisVoteResult(getNightVoters(roomId, roleName)));
  }

  public void endVote(String roomId, GamePhase phase) {
    voteRedisRepository.endVote(getVoters(roomId), phase);
    voteInfosMap.remove(roomId);
  }

  private List<String> getVoters(String roomId) {
    return voteInfosMap.get(roomId).getVotersMap().keySet().stream().collect(Collectors.toList());
  }

  private List<String> getNightVoters(String roomId, GameRole roleName) {
    Map<String, GameRole> voters = voteInfosMap.get(roomId).getVotersMap();
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

  private Map<String, Boolean> confirmResultConvert(Map<String, Vote> voteResult) {
    Map<String, Boolean> confirmResult = new HashMap<String, Boolean>();
    voteResult.forEach((playerId, vote) -> {
      confirmResult.put(playerId, vote.isConfirm());
    });
    return confirmResult;
  }

  private Map<String, Vote> getRedisVoteResult(List<String> voters) {
    return voteRedisRepository.getVoteResult(voters);
  }
}
