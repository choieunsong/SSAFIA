package s05.p12a104.mafia.stomp.controller;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.stomp.request.GameSessionVoteReq;
import s05.p12a104.mafia.stomp.response.ConfirmResultRes;
import s05.p12a104.mafia.stomp.response.VoteResultRes;
import s05.p12a104.mafia.stomp.service.GameSessionVoteService;

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


    Map<String, Boolean> confirmResult = gameSessionVoteService.confirmVote(roomId, playerId, req);

    if (confirmResult.size() > 0) {
      simpMessagingTemplate.convertAndSend("/sub/" + roomId, ConfirmResultRes.of(confirmResult));

      // 투표 확정 인원 확인
      int confirmCnt = confirmResult.entrySet().stream().filter(e -> e.getValue() == true)
          .collect(Collectors.toList()).size();
      int alivePlayerCnt = gameSession.getAlivePlayer();
      log.info("Room {} Phase {} Confirm {} : Needed {}", roomId, req.getPhase(), confirmCnt,
          alivePlayerCnt);

      if (confirmCnt == alivePlayerCnt) {
        gameSessionVoteService.endVote(roomId, gameSession.getPhaseCount(), req.getPhase());
      }
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

    Map<String, String> voteResult =
        gameSessionVoteService.nightVote(roomId, playerId, req, roleName);
    Map<String, String> forObserver = gameSessionVoteService.getVoteResult(roomId, req);

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

    Map<String, Boolean> forObserver = gameSessionVoteService.confirmVote(roomId, playerId, req);
    Map<String, Boolean> confirmResult =
        gameSessionVoteService.getNightConfirm(roomId, playerId, req, roleName);

    if (confirmResult.size() > 0) {
      simpMessagingTemplate.convertAndSend("/sub/" + roomId + "/" + roleName,
          ConfirmResultRes.of(confirmResult));
      simpMessagingTemplate.convertAndSend("/sub/" + roomId + "/" + GameRole.OBSERVER,
          ConfirmResultRes.of(forObserver));

      // 투표 확정 인원 확인
      int confirmCnt = confirmResult.entrySet().stream().filter(e -> e.getValue() == true)
          .collect(Collectors.toList()).size();
      int notCivilainCnt = gameSession.getAliveNotCivilian();
      log.info("Room {} Phase {} Confirm {} : Needed {}", roomId, req.getPhase(), confirmCnt,
          notCivilainCnt);;

      if (confirmCnt == notCivilainCnt) {
        gameSessionVoteService.endVote(roomId, gameSession.getPhaseCount(), req.getPhase());
      }
    }

  }

}
