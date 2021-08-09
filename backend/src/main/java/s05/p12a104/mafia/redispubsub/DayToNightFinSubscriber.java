package s05.p12a104.mafia.redispubsub;

import java.util.HashMap;
import java.util.Map;
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
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.stomp.response.GameStatusRes;

@Slf4j
@RequiredArgsConstructor
@Service
public class DayToNightFinSubscriber {

  private final ObjectMapper objectMapper;
  private final SimpMessagingTemplate template;
  private final GameSessionService gameSessionService;
  private final GameSessionVoteService gameSessionVoteService;

  public void sendMessage(String redisMessageStr) {
    try {
      String roomId = objectMapper.readValue(redisMessageStr, String.class);
      GameSession gameSession = gameSessionService.findById(roomId);
      gameSession.setPhase(GamePhase.NIGHT_VOTE);
      gameSession.setTimer(30);
      gameSessionService.update(gameSession);

      template.convertAndSend("/sub/" + roomId, GameStatusRes.of(gameSession));

      Map<String, String> players = new HashMap();

      gameSession.getPlayerMap().forEach((playerId, player) -> {
        if (player.isAlive() && player.getRole() != GameRole.CIVILIAN) {
          players.put(playerId, null);
        }
      });

      gameSessionVoteService.startVote(roomId, gameSession.getPhase(), gameSession.getTimer(),
          players);
      log.info("NIGHT_VOTE 투표 생성!", roomId);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

}
