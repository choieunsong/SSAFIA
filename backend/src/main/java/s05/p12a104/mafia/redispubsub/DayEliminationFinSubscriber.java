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
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.redispubsub.message.DayEliminationMessage;
import s05.p12a104.mafia.stomp.response.GameStatusKillRes;
import s05.p12a104.mafia.stomp.task.StartFinTimerTask;

@Slf4j
@RequiredArgsConstructor
@Service
public class DayEliminationFinSubscriber {

  private final ObjectMapper objectMapper;
  private final SimpMessagingTemplate template;
  private final RedisPublisher redisPublisher;
  private final GameSessionService gameSessionService;
  private final ChannelTopic topicDayToNightFin;

  public void sendMessage(String message) {
    try {
      DayEliminationMessage dayEliminationMessage =
          objectMapper.readValue(message, DayEliminationMessage.class);
      String roomId = dayEliminationMessage.getRoomId();
      String deadPlayerId = dayEliminationMessage.getDeadPlayerId();
      GameSession gameSession = gameSessionService.findById(roomId);
      setDayToNight(gameSession, deadPlayerId);

      Player dead = gameSession.getPlayerMap().get(deadPlayerId);
      template.convertAndSend("/sub/" + roomId, GameStatusKillRes.of(gameSession, dead));

      // Timer를 돌릴 마땅한 위치가 없어서 추후에 통합 예정
      Timer timer = new Timer();
      StartFinTimerTask task = new StartFinTimerTask(redisPublisher, topicDayToNightFin);
      task.setRoomId(roomId);
      timer.schedule(task, gameSession.getTimer() * 1000);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  private void setDayToNight(GameSession gameSession, String deadPlayerId) {
    log.info("deadPlayer: " + deadPlayerId);
    gameSession.setPhase(GamePhase.DAY_TO_NIGHT);
    gameSession.setPhaseCount(gameSession.getPhaseCount() + 1);
    gameSession.setTimer(15);

    // 사망 처리
    if (deadPlayerId != null) {
      gameSession.eliminatePlayer(deadPlayerId);
    }

    gameSessionService.update(gameSession);
  }
}
