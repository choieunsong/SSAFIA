package s05.p12a104.mafia.stomp.controller;

import java.util.Map;
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
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;
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

    Map<String, String> voteResult = gameSessionVoteService.vote(roomId, playerId, req);
    if (voteResult != null) {
      simpMessagingTemplate.convertAndSend("/sub/" + roomId, VoteResultRes.of(voteResult));
    }
  }

  @MessageMapping("/{roomId}/confirm")
  public void confirmVote(SimpMessageHeaderAccessor accessor, @DestinationVariable String roomId,
      @Payload GameSessionVoteReq req) {
    String playerId = accessor.getUser().getName();

    GameSession gameSession = gameSessionService.findById(roomId);

    // 투표 존재여부 확인
    if (gameSessionVoteService.getVote(roomId, req) == null) {
      return;
    }

    // 투표 확정 인원 확인
    if (gameSessionVoteService.confirmVote(roomId, playerId, req) == gameSession.getAlivePlayer()) {
      gameSessionVoteService.endVote(roomId, req.getPhase());
    }
  }


  @MessageMapping("/{roomId}/{roleName}/vote")
  public void nightVote(SimpMessageHeaderAccessor accessor, @DestinationVariable String roomId,
      @DestinationVariable GameRole roleName, @Payload GameSessionVoteReq req) {
    String playerId = accessor.getUser().getName();

    // GamePhase 체크
    if (req.getPhase() != GamePhase.NIGHT_VOTE) {
      return;
    }

    Map<String, String> voteResult = gameSessionVoteService.nightVote(roomId, playerId, req, roleName);
    Map<String, String> forObserver = gameSessionVoteService.getVote(roomId, req);

    if (voteResult != null) {
      simpMessagingTemplate.convertAndSend("/sub/" + roomId + "/" + roleName,
          VoteResultRes.of(voteResult));
      simpMessagingTemplate.convertAndSend("/sub/" + roomId + "/" + GameRole.OBSERVER,
          VoteResultRes.of(forObserver));
    }


  }

  @MessageMapping("/{roomId}/{roleName}/confirm")
  public void confirmNightVote(SimpMessageHeaderAccessor accessor,
      @DestinationVariable String roomId, @DestinationVariable GameRole roleName) {
    String playerId = accessor.getUser().getName();

    GameSession gameSession = gameSessionService.findById(roomId);
    GameSessionVoteReq req = new GameSessionVoteReq();
    req.setPhase(GamePhase.NIGHT_VOTE);

    // 투표 존재여부 확인
    if (gameSessionVoteService.getVote(roomId, req) == null) {
      return;
    }

    // 투표 확정 인원 확인
    if (gameSessionVoteService.confirmVote(roomId, playerId, req) == gameSession
        .getAliveNotCivilian()) {
      gameSessionVoteService.endVote(roomId, req.getPhase());
    }
  }

}
