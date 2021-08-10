package s05.p12a104.mafia.stomp.task;

import java.util.TimerTask;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.stomp.response.GameSessionReadyRes;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReadyTimerTask extends TimerTask {

  private String roomId;
  private final GameSessionService gameSessionService;
  private final SimpMessagingTemplate template;

  @Override
  public void run() {
    log.info("send ready message");
    GameSession gameSession = gameSessionService.findById(roomId);
    gameSessionService.endGame(gameSession);
    
    template.convertAndSend("/sub/" + roomId, GameSessionReadyRes.of(gameSession));
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }
}
