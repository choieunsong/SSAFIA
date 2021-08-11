package s05.p12a104.mafia.api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.stream.Collectors;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.entity.Vote;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.domain.repository.VoteRepository;
import s05.p12a104.mafia.redispubsub.RedisPublisher;
import s05.p12a104.mafia.redispubsub.message.DayDiscussionMessage;
import s05.p12a104.mafia.redispubsub.message.DayEliminationMessage;
import s05.p12a104.mafia.redispubsub.message.NightVoteMessage;
import s05.p12a104.mafia.stomp.request.GameSessionVoteReq;
import s05.p12a104.mafia.stomp.task.VoteFinTimerTask;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameSessionVoteServiceImpl implements GameSessionVoteService {

  private final RedisPublisher redisPublisher;
  private final VoteRepository voteRepository;
  private final GameSessionService gameSessionService;
  private final ChannelTopic topicDayDiscussionFin;
  private final ChannelTopic topicDayEliminationFin;
  private final ChannelTopic topicNightVoteFin;

  @Override
  public void startVote(String roomId, GamePhase phase, int time, Map players) {
    voteRepository.createVote(roomId, phase, players);
    Timer timer = new Timer();
    VoteFinTimerTask task = new VoteFinTimerTask(this);
    task.setRoomId(roomId);
    task.setPhase(phase);
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
  public Vote nightVote(String roomId, String playerId, GameSessionVoteReq req, GameRole roleName) {

    String voteId = getVoteId(roomId, GamePhase.NIGHT_VOTE);

    if (voteRepository.findVoteById(voteId) == null) {
      return null;
    }

    Vote vote = voteRepository.vote(voteId, playerId, req.getVote());
    GameSession gameSession = gameSessionService.findById(roomId);
    Map<String, Player> playerMap = gameSession.getPlayerMap();

    Map<String, String> roleVote = new HashMap();

    vote.getVoteResult().forEach((id, player) -> {
      if (playerMap.get(id).getRole() == roleName) {
        roleVote.put(id, player);
      }
    });
    vote.setVoteResult(roleVote);

    return vote;
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

    // TODO: 여기에서 나간사람 체크

    if (gameSession.getPhase() == GamePhase.DAY_DISCUSSION) {
      DayDiscussionMessage dayDiscussionMessage =
          new DayDiscussionMessage(roomId, getSuspiciousList(gameSession, vote.getVoteResult()));
      redisPublisher.publish(topicDayDiscussionFin, dayDiscussionMessage);
    } else if (gameSession.getPhase() == GamePhase.DAY_ELIMINATION) {
      DayEliminationMessage dayEliminationMessage = new DayEliminationMessage(roomId,
          getEliminationPlayer(gameSession, vote.getVoteResult()));
      redisPublisher.publish(topicDayEliminationFin, dayEliminationMessage);
    } else if (gameSession.getPhase() == GamePhase.NIGHT_VOTE) {
      NightVoteMessage nightVoteMessage =
          new NightVoteMessage(roomId, getNightVoteResult(gameSession, vote.getVoteResult()));
      redisPublisher.publish(topicNightVoteFin, nightVoteMessage);
    }
  }

  public List<String> getSuspiciousList(GameSession gameSession, Map<String, String> voteResult) {
    List<String> suspiciousList = new ArrayList<>();

    Map<String, Integer> voteNum = new HashMap<String, Integer>();
    int voteCnt = 0;
    for (String vote : voteResult.values()) {
      if (vote == null) {
        continue;
      }

      voteCnt++;
      voteNum.put(vote, voteNum.getOrDefault(vote, 0) + 1);
    }

    // 의심자 찾기
    int alivePlayer = gameSession.getAlivePlayer();
    if (voteCnt > (alivePlayer - 1) / 2) {
      List<String> suspects = new ArrayList<>(voteNum.keySet());
      // 투표수 오름차순
      Collections.sort(suspects, (o1, o2) -> voteNum.get(o2).compareTo(voteNum.get(o1)));

      Map<String, Player> playerMap = gameSession.getPlayerMap();
      int voteMax = voteNum.get(suspects.get(0));
      for (String suspect : suspects) {
        // 동점자가 아니면 더이상 동점자가 없기때문에 끝내기
        if (voteNum.get(suspect) != voteMax) {
          break;
        }

        // 중간 나간 사람이 포함되어 있을 수 있으므로 살아있는지 체크
        if (playerMap.get(suspect).isAlive()) {
          suspiciousList.add(suspect);
        }
      }

      // 살아있는 사람 기준으로 6명이상이면 3명까지 5이하면 2명까지
      if (suspiciousList.size() > 3 || (alivePlayer <= 5 && suspiciousList.size() > 2)) {
        suspiciousList.clear();
      }
    }

    return suspiciousList;
  }

  private String getEliminationPlayer(GameSession gameSession, Map<String, String> voteResult) {
    String deadPlayerId = null;
    Map<String, Integer> voteNum = new HashMap<String, Integer>();
    int voteCnt = 0;
    for (String vote : voteResult.values()) {
      if (vote == null) {
        continue;
      }

      voteCnt++;
      voteNum.put(vote, voteNum.getOrDefault(vote, 0) + 1);
    }

    int alivePlayer = gameSession.getAlivePlayer();
    if (voteCnt > (alivePlayer - 1) / 2) {

      // 최다 득표 수 구하기
      Integer max = voteNum.entrySet().stream()
          .max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue();

      // 최다 득표한 Player List
      List deadList = voteNum.entrySet().stream().filter(entry -> entry.getValue() == max)
          .map(Map.Entry::getKey).collect(Collectors.toList());

      // 한명일 경우
      if (deadList.size() == 1) {
        deadPlayerId = deadList.get(0).toString();
      }
    }
    return deadPlayerId;
  }

  private Map getNightVoteResult(GameSession gameSession, Map<String, String> voteResult) {
    Map<GameRole, String> result = new HashMap();

    Map<String, Player> playerMap = gameSession.getPlayerMap();

    // 마피아가 아닌 직업들 결과에 담기
    result = voteResult.entrySet().stream()
        .filter(e -> playerMap.get(e.getKey()).getRole() != GameRole.MAFIA)
        .filter(e -> e.getValue() != null)
        .collect(Collectors.toMap(e -> playerMap.get(e.getKey()).getRole(), e -> e.getValue()));

    // 마피아들의 투표만 추려서 Map<투표 받은 사람,List<투표한사람>>으로 저장
    Map<String, List<String>> mafiaVote =
        voteResult.keySet().stream().filter(key -> playerMap.get(key).getRole() == GameRole.MAFIA)
            .filter(key -> voteResult.get(key) != null)
            .collect(Collectors.groupingBy(key -> voteResult.get(key)));

    // 마피아가 투표를 했을 경우
    if (mafiaVote.size() > 0) {

      // 최다 득표 수 구하기
      int max = mafiaVote.entrySet().stream()
          .max((entry1, entry2) -> entry1.getValue().size() > entry2.getValue().size() ? 1 : -1)
          .get().getValue().size();

      // 최다 득표한 Player List
      List deadList = mafiaVote.entrySet().stream().filter(entry -> entry.getValue().size() == max)
          .map(Map.Entry::getKey).collect(Collectors.toList());

      // 한명일 경우
      if (deadList.size() == 1) {
        result.put(GameRole.MAFIA, deadList.get(0).toString());
      }
    }

    return result;
  }
}
