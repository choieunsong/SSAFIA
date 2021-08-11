package s05.p12a104.mafia.redispubsub;

import java.util.Map;
import java.util.Timer;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.redispubsub.message.NightVoteMessage;
import s05.p12a104.mafia.stomp.response.GameStatusKillRes;
import s05.p12a104.mafia.stomp.response.SuspectVoteRes;
import s05.p12a104.mafia.stomp.task.StartFinTimerTask;

@Slf4j
@RequiredArgsConstructor
@Service
public class NightVoteFinSubscriber {

  private final ObjectMapper objectMapper;
  private final SimpMessagingTemplate template;
  private final RedisPublisher redisPublisher;
  private final GameSessionService gameSessionService;
  private final ChannelTopic topicStartFin;

  public void sendMessage(String message) {
    try {
      NightVoteMessage nightVoteMessage = objectMapper.readValue(message, NightVoteMessage.class);
      String roomId = nightVoteMessage.getRoomId();
      Map<GameRole, String> roleVote = nightVoteMessage.getRoleVoteResult();
      GameSession gameSession = gameSessionService.findById(roomId);

      String deadPlayerId = roleVote.get(GameRole.MAFIA);
      String protectedPlayerId = roleVote.get(GameRole.DOCTOR);

      setNightToDay(gameSession, deadPlayerId, protectedPlayerId);

      // 종료 여부 체크
      if (gameSessionService.isDone(gameSession))
        return;

      Player deadPlayer = gameSession.getPlayerMap().get(deadPlayerId);
      // 의사가 살렸을 경우 부활
      if (deadPlayerId == protectedPlayerId) {
        deadPlayer = null;
      }

      // 밤투표 결과
      template.convertAndSend("/sub/" + roomId, GameStatusKillRes.of(gameSession, deadPlayer));

      String suspectPlayerId = roleVote.get(GameRole.POLICE);

      Player suspectPlayer = gameSession.getPlayerMap().get(suspectPlayerId);

      // 용의자 Role 결과
      if (suspectPlayer != null) {
        template.convertAndSend("/sub/" + roomId + "/police", SuspectVoteRes.of(suspectPlayer));
      }

      // Timer를 돌릴 마땅한 위치가 없어서 추후에 통합 예정
      Timer timer = new Timer();
      StartFinTimerTask task = new StartFinTimerTask(redisPublisher, topicStartFin);
      task.setRoomId(roomId);
      timer.schedule(task, gameSession.getTimer() * 1000);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  private void setNightToDay(GameSession gameSession, String deadPlayerId,
      String protectedPlayerId) {
    // 나간 사람 체크 및 기본 세팅
    gameSession.changePhase(GamePhase.NIGHT_TO_DAY, 15);

    if (deadPlayerId != null && deadPlayerId != protectedPlayerId) {
      gameSession.eliminatePlayer(deadPlayerId);
    }

    gameSessionService.update(gameSession);
  }
}
