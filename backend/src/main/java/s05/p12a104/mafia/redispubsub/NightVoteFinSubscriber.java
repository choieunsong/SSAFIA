package s05.p12a104.mafia.redispubsub;

import java.util.Timer;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.redispubsub.message.NightVoteMessage;
import s05.p12a104.mafia.stomp.task.StartFinTimerTask;

@Slf4j
@RequiredArgsConstructor
@Service
public class NightVoteFinSubscriber {

  private final ObjectMapper objectMapper;
  private final SimpMessagingTemplate template;
  private final RedisPublisher redisPublisher;
  private final GameSessionService gameSessionService;
  private final ChannelTopic topicNightToDayFin;

  public void sendMessage(String message) {
    try {
      NightVoteMessage nightVoteMessage = objectMapper.readValue(message, NightVoteMessage.class);
      String roomId = nightVoteMessage.getRoomId();
//      Map roleVote = nightVoteMessage.getRoleVoteResult();
      GameSession gameSession = gameSessionService.findById(roomId);

      // Timer를 돌릴 마땅한 위치가 없어서 추후에 통합 예정
      Timer timer = new Timer();
      StartFinTimerTask task = new StartFinTimerTask(redisPublisher, topicNightToDayFin);
      task.setRoomId(roomId);
      timer.schedule(task, gameSession.getTimer() * 1000);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  private void setNightToDay(GameSession gameSession, String deadPlayer) {
    gameSession.setPhase(GamePhase.NIGHT_TO_DAY);
    gameSession.setPhaseCount(gameSession.getPhaseCount() + 1);
    gameSession.setTimer(15);

    gameSessionService.update(gameSession);
  }
}
