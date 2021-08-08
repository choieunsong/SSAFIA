package s05.p12a104.mafia.redispubsub;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.api.service.GameSessionVoteService;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.stomp.response.GameStatusRes;

@Slf4j
@RequiredArgsConstructor
@Service
public class DayDiscussionFinSubscriber {

  private final ObjectMapper objectMapper;
  private final SimpMessagingTemplate template;
  private final GameSessionService gameSessionService;

  public void sendMessage(String redisMessageStr) {
    try {
      String roomId = objectMapper.readValue(redisMessageStr, String.class);
      GameSession gameSession = gameSessionService.findById(roomId);
      gameSession.setPhase(GamePhase.DAY_ELIMINATION);
      gameSession.setTimer(100);
      gameSessionService.update(gameSession);

      log.info("DAY_DISCUSSION 투표 종료!");
      template.convertAndSend("/sub/" + roomId, GameStatusRes.of(gameSession));

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
