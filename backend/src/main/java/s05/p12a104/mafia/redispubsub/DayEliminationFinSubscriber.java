package s05.p12a104.mafia.redispubsub;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.redispubsub.message.DayEliminationMessage;

@Slf4j
@RequiredArgsConstructor
@Service
public class DayEliminationFinSubscriber {

  private final ObjectMapper objectMapper;
  private final SimpMessagingTemplate template;
  private final GameSessionService gameSessionService;

  public void sendMessage(String message) {
    try {
      DayEliminationMessage dayDisscusionMessage =
          objectMapper.readValue(message, DayEliminationMessage.class);
      String roomId = dayDisscusionMessage.getRoomId();
      String deadPlayerId = dayDisscusionMessage.getDeadPlayerId();
      
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

}
