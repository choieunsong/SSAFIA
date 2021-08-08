package s05.p12a104.mafia.stomp.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.api.service.GameSessionVoteService;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.Vote;
import s05.p12a104.mafia.stomp.request.GameSessionVoteReq;
import s05.p12a104.mafia.stomp.response.VoteResultRes;

@Slf4j
@RequiredArgsConstructor
@RestController
public class VoteController {

  private final GameSessionService gameSessionService;
  private final GameSessionVoteService gameSessionVoteService;
  private final SimpMessagingTemplate simpMessagingTemplate;

  @MessageMapping("/{roomId}/vote")
  public void dayVote(SimpMessageHeaderAccessor accessor, @DestinationVariable String roomId,
      @Payload GameSessionVoteReq req) {
    String playerId = accessor.getUser().getName();

    Vote vote = gameSessionVoteService.vote(roomId, playerId, req);
    if (vote != null) {
      simpMessagingTemplate.convertAndSend("/sub/" + roomId, VoteResultRes.of(vote));
    }
  }

  @MessageMapping("/{roomId}/{roleName}/vote")
  public void nightVote(SimpMessageHeaderAccessor accessor, @DestinationVariable String roomId,
      @DestinationVariable String roleName, @Payload GameSessionVoteReq req) {
    String playerId = accessor.getUser().getName();

  }

  @MessageMapping("/{roomId}/confirm")
  public void confirmVote(SimpMessageHeaderAccessor accessor, @DestinationVariable String roomId,
      @Payload GameSessionVoteReq req) {
    String playerId = accessor.getUser().getName();

    GameSession gameSession = gameSessionService.findById(roomId);

    if (gameSessionVoteService.confirmVote(roomId, playerId, req) == gameSession.getAlivePlayer()) {
      gameSessionVoteService.endVote(roomId, req);
    }
  }

}
