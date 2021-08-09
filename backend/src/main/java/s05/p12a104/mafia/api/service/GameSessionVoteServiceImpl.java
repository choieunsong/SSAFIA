package s05.p12a104.mafia.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.entity.Vote;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.repository.VoteRepository;
import s05.p12a104.mafia.redispubsub.RedisPublisher;
import s05.p12a104.mafia.redispubsub.message.DayDiscussionMessage;
import s05.p12a104.mafia.stomp.request.GameSessionVoteReq;
import s05.p12a104.mafia.stomp.task.DayDiscussionVoteFinTimerTask;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameSessionVoteServiceImpl implements GameSessionVoteService {

  private final RedisPublisher redisPublisher;
  private final VoteRepository voteRepository;
  private final GameSessionService gameSessionService;
  private final ChannelTopic topicDayDiscussionFin;

  @Override
  public void startVote(String roomId, GamePhase phase, int time, Map players) {
    voteRepository.createVote(roomId, phase, players);
    Timer timer = new Timer();
    DayDiscussionVoteFinTimerTask task = new DayDiscussionVoteFinTimerTask(this);
    task.setRoomId(roomId);
    task.setPhase(phase);;
    timer.schedule(task, time * 1000);
  }

  @Override
  public void endVote(String roomId, GamePhase phase) {
    String voteId = getVoteId(roomId, phase);
    Vote vote = voteRepository.findVoteById(voteId);
    if (vote == null) {
      return;
    } else {
      publishRedis(roomId, vote);
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

  private void publishRedis(String roomId, Vote vote) {
    GameSession gameSession = gameSessionService.findById(roomId);
    
    // 여기에서 나간사람 체크
    
    if (gameSession.getPhase() == GamePhase.DAY_DISCUSSION) {
      DayDiscussionMessage dayDiscussionMessage =
          new DayDiscussionMessage(roomId, getSuspiciousList(gameSession, vote.getVoteResult()));
      redisPublisher.publish(topicDayDiscussionFin, dayDiscussionMessage);
    }
  }

  public List<String> getSuspiciousList(GameSession gameSession, Map<String, String> voteResult) {
    List<String> suspiciousList = new ArrayList<>();

    Map<String, Integer> result = new HashMap<String, Integer>();
    result.put("null", 0);
    voteResult.forEach((playerId, vote) -> {
      if (vote == null) {
        result.put("null", result.get("null") + 1);
        return;
      }

      result.put(vote, result.getOrDefault(vote, 0) + 1);
    });

    int alivePlayer = gameSession.getAlivePlayer();
    // 의심자 찾기
    if (result.get("null") <= alivePlayer / 2) {
      List<String> keyList = new ArrayList<>(result.keySet());
      keyList.remove("null");
      // 투표수 오름차순
      Collections.sort(keyList, (o1, o2) -> result.get(o2).compareTo(result.get(o1)));

      Map<String, Player> playerMap = gameSession.getPlayerMap();
      int voteMax = result.get(keyList.get(0));
      for (String suspect : keyList) {
        // 동점자가 아니면 더이상 동점자가 없기때문에 끝내기
        if (result.get(suspect) != voteMax)
          break;

        // 중간 나간 사람이 포함되어 있을 수 있으므로 살아있는지 체크
        if (playerMap.get(suspect).isAlive())
          suspiciousList.add(suspect);
      }

      // 살아있는 사람 기준으로 6명이상이면 3명까지 5이하면 2명까지
      if (suspiciousList.size() > 3 || (alivePlayer <= 5 && suspiciousList.size() > 2))
        suspiciousList = new ArrayList<String>();
    }

    return suspiciousList;
  }
}
