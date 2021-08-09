package s05.p12a104.mafia.redispubsub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.api.service.GameSessionVoteService;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.redispubsub.message.DayDiscussionMessage;
import s05.p12a104.mafia.redispubsub.message.DayEliminationMessage;
import s05.p12a104.mafia.stomp.response.GameStatusRes;

@Slf4j
@RequiredArgsConstructor
@Service
public class DayDiscussionFinSubscriber {

  private final ObjectMapper objectMapper;
  private final SimpMessagingTemplate template;
  private final RedisPublisher redisPublisher;
  private final GameSessionService gameSessionService;
  private final GameSessionVoteService gameSessionVoteService;
  private final ChannelTopic topicDayEliminationFin;

  public void sendMessage(String message) {
    try {
      DayDiscussionMessage dayDisscusionMessage =
          objectMapper.readValue(message, DayDiscussionMessage.class);
      String roomId = dayDisscusionMessage.getRoomId();
      GameSession gameSession = gameSessionService.findById(roomId);
      List<String> suspiciousList = dayDisscusionMessage.getSuspiciousList();
      // DAY_TO_NIGHT으로
      if (suspiciousList.isEmpty()) {
        setDayToNight(roomId);
        return;
      }

      // DAY ELIMINATION으로
      setDayElimination(gameSession, suspiciousList);
      template.convertAndSend("/sub/" + roomId, GameStatusRes.of(gameSession));

      Map<String, String> players = new HashMap<>();

      gameSession.getPlayerMap().forEach((playerId, player) -> {
        if (player.isAlive()) {
          players.put(playerId, null);
        }
      });

      gameSessionVoteService.startVote(roomId, gameSession.getPhase(), gameSession.getTimer(),
          players);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  private void setDayElimination(GameSession gameSession, List<String> suspiciousList) {
    log.info("suspiciousList", suspiciousList.toString());
    gameSession.setPhase(GamePhase.DAY_ELIMINATION);
    gameSession.setPhaseCount(gameSession.getPhaseCount() + 1);
    gameSession.setTimer(30);
    Map<String, Player> playerMap = gameSession.getPlayerMap();
    // 의심자 체크
    for (String suspicious : suspiciousList) {
      playerMap.get(suspicious).setSuspicious(true);
    }
    gameSessionService.update(gameSession);
  }

  private void setDayToNight(String roomId) {
    log.info("no suspiciousList", roomId);
    DayEliminationMessage dayEliminationMessage = new DayEliminationMessage(roomId, null);
    redisPublisher.publish(topicDayEliminationFin, dayEliminationMessage);
  }

}
