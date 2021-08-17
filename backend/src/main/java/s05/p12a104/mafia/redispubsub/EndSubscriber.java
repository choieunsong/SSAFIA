package s05.p12a104.mafia.redispubsub;

import java.util.Timer;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.redispubsub.message.EndMessgae;
import s05.p12a104.mafia.stomp.response.GameResult;
import s05.p12a104.mafia.stomp.response.GameResultRes;
import s05.p12a104.mafia.stomp.service.GameSessionVoteService;
import s05.p12a104.mafia.stomp.task.ReadyTimerTask;

@Slf4j
@RequiredArgsConstructor
@Service
public class EndSubscriber {

  private final ObjectMapper objectMapper;
  private final SimpMessagingTemplate template;
  private final GameSessionService gameSessionService;
  private final GameSessionVoteService gameSessionVoteService;
  private final RedissonClient redissonClient;

  public void sendMessage(String message) {
    try {
      EndMessgae endMessgae = objectMapper.readValue(message, EndMessgae.class);
      String roomId = endMessgae.getRoomId();
      GameResult gameResult = endMessgae.getGameResult();

      ReadyTimerTask task = new ReadyTimerTask(gameSessionService, template, redissonClient);
      task.setRoomId(roomId);
      Timer timer = new Timer();
      timer.schedule(task, gameResult.getTimer() * 1000);
      
      log.info("Game is done - {} in Room {}", gameResult, roomId);
      
      template.convertAndSend("/sub/" + roomId, GameResultRes.of(gameResult));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
