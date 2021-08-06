package s05.p12a104.mafia.redispubsub;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class StartFinSubscriber {

  private final ObjectMapper objectMapper;
  private final RedisPublisher redisPublisher;
  private final SimpMessagingTemplate template;
  private final GameSessionService gameSessionService;
  private final GameSessionVoteService gameSessionVoteService;

  public void sendMessage(String redisMessageStr) {
    try {
      String roomId = objectMapper.readValue(redisMessageStr, String.class);
      GameSession gameSession = gameSessionService.findById(roomId);
      gameSession.setPhase(GamePhase.DAY_DISCUSSION);
      gameSession.setTimer(100);
      gameSessionService.update(gameSession);

      template.convertAndSend("/sub/" + roomId, GameStatusRes.of(gameSession));

      gameSessionVoteService.createVote(roomId, gameSession.getPhase());
      gameSessionVoteService.startTimer(roomId);
      System.out.println("DAY_DISCUSSION 투표 생성!");
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

}
