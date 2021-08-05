package s05.p12a104.mafia.stomp.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.stomp.response.GameSessionStompJoinRes;
import s05.p12a104.mafia.stomp.response.GameSessionStompLeaveRes;

@Slf4j
@RequiredArgsConstructor
@Controller
public class RoomController {

  private final GameSessionService gameSessionService;
  private final SimpMessagingTemplate simpMessagingTemplate;

  @MessageMapping("/{roomId}/join")
  public void joinGameSession(@DestinationVariable String roomId) {
    GameSessionStompJoinRes res = GameSessionStompJoinRes.of(gameSessionService.findById(roomId));
    simpMessagingTemplate.convertAndSend("/sub/" + roomId, res);
  }

  @MessageMapping("/{roomId}/leave")
  public void leaveGameSession(SimpMessageHeaderAccessor accessor,
      @DestinationVariable String roomId) {
    String playerId = accessor.getUser().getName();

    GameSession gameSession = gameSessionService.removeUser(roomId, playerId);
    GameSessionStompLeaveRes res = new GameSessionStompLeaveRes(gameSession.getHostId(), playerId);
    simpMessagingTemplate.convertAndSend("/sub/" + roomId, res);
  }
}
