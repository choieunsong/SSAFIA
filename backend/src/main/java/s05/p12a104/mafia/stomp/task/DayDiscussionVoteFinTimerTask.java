package s05.p12a104.mafia.stomp.task;

import java.util.TimerTask;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import s05.p12a104.mafia.api.service.GameSessionVoteService;
import s05.p12a104.mafia.domain.enums.GamePhase;

@RequiredArgsConstructor
@Service
@Setter
public class DayDiscussionVoteFinTimerTask extends TimerTask {

  private final GameSessionVoteService gameSessionVoteService;
  private String roomId;
  private GamePhase phase;

  @Override
  public void run() {
    gameSessionVoteService.endVote(roomId, phase);
  }

}
