package s05.p12a104.mafia.vote;

import java.util.Timer;
import java.util.TimerTask;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.api.service.GameSessionVoteService;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.redispubsub.RedisPublisher;
import s05.p12a104.mafia.stomp.response.VoteResultRes;

@RequiredArgsConstructor
@RestController
public class VoteController {

  private final GameSessionService gameSessionService;
  private final GameSessionVoteService gameSessionVoteService;
  private final RedisPublisher redisPublisher;
  private final SimpMessagingTemplate simpMessagingTemplate;

  @MessageMapping("/{roomId}/createvote")
  public void createVote(SimpMessageHeaderAccessor accessor, @DestinationVariable String roomId,
      @Payload String message) {

    Gson g = new Gson();
    GameSessionVoteReq req = g.fromJson(message, GameSessionVoteReq.class);

    Task(roomId, req);
  }

  public void Task(String roomId, GameSessionVoteReq req) {

    Timer timer = new Timer();

    int pcnt = 3;
    TimerTask timerTask = new TimerTask() {
      boolean confirm = false;
      int time = 0;
      final int endTime = 60;
      int confirmCnt = 0;

      @Override
      public void run() {
        time++;
        confirmCnt = gameSessionVoteService.getVote(roomId, req).getConfirmCnt();
        simpMessagingTemplate.convertAndSend("/sub/" + roomId,
            "Timer: " + String.valueOf(time) + " 확정자 수: " + String.valueOf(confirmCnt));

        if (time == endTime || confirmCnt == pcnt) {
          confirm = true;
        }
        if (confirm) {
          this.cancel();
          return;
        }
      }

      @Override
      public boolean cancel() {
        gameSessionVoteService.finishVote(roomId, req);
        simpMessagingTemplate.convertAndSend("/sub/" + roomId, "투표가 종료되었습니다");
        return super.cancel();
      }
    };

    timer.scheduleAtFixedRate(timerTask, 100, 1000);
  }

  @MessageMapping("/{roomId}/vote")
  public void dayVote(SimpMessageHeaderAccessor accessor, @DestinationVariable String roomId,
      @Payload String message) {
    System.out.println("여기는 컨트롤러 vote 받은 메시지: " + message);
    String playerId = accessor.getUser().getName();

    Gson g = new Gson();
    GameSessionVoteReq req = g.fromJson(message, GameSessionVoteReq.class);
    GameSession gameSession = gameSessionService.findById(roomId);

    gameSessionVoteService.vote(roomId, playerId, req);
    simpMessagingTemplate.convertAndSend("/sub/" + roomId, VoteResultRes.of(gameSession));
  }

  @MessageMapping("/{roomId}/{roleName}/vote")
  public void nightVote(SimpMessageHeaderAccessor accessor, @DestinationVariable String roomId,
      @DestinationVariable String roleName, @Payload String message) {
    String playerId = accessor.getUser().getName();

    Gson g = new Gson();
    GameSessionVoteReq req = g.fromJson(message, GameSessionVoteReq.class);

  }

  @MessageMapping("/{roomId}/confirm")
  public void confirmVote(SimpMessageHeaderAccessor accessor, @DestinationVariable String roomId,
      @Payload String message) {
    String playerId = accessor.getUser().getName();

    Gson g = new Gson();
    GameSessionVoteReq req = g.fromJson(message, GameSessionVoteReq.class);
    GameSession gameSession = gameSessionService.findById(roomId);

    if (gameSessionVoteService.confirmVote(roomId, playerId, req) == gameSession.getAlivePlayer()) {

    }
  }

}
