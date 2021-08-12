package s05.p12a104.mafia.redispubsub;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.common.exception.RedissonLockNotAcquiredException;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.redispubsub.message.NightVoteMessage;
import s05.p12a104.mafia.stomp.response.GameStatusKillRes;
import s05.p12a104.mafia.stomp.response.ObserberJoinRes;
import s05.p12a104.mafia.stomp.response.PlayerDeadRes;
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
  private final RedissonClient redissonClient;
  private static final String KEY = "GameSession";

  public void sendMessage(String message) {
    try {
      NightVoteMessage nightVoteMessage = objectMapper.readValue(message, NightVoteMessage.class);
      String roomId = nightVoteMessage.getRoomId();
      Map<GameRole, String> roleVote = nightVoteMessage.getRoleVoteResult();

      RLock lock = redissonClient.getLock(KEY + roomId);
      boolean isLocked = false;
      try {
        isLocked = lock.tryLock(2, 3, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (!isLocked) {
        throw new RedissonLockNotAcquiredException("Lock을 얻을 수 없습니다 - Key : " + KEY + roomId);
      }
      GameSession gameSession = null;
      String deadPlayerId = roleVote.get(GameRole.MAFIA);
      String protectedPlayerId = roleVote.get(GameRole.DOCTOR);
      String suspectPlayerId = roleVote.get(GameRole.POLICE);

      // 의사가 살렸을 경우 부활
      if (deadPlayerId != null && deadPlayerId.equals(protectedPlayerId)) {
        deadPlayerId = null;
      }

      try {
        gameSession = gameSessionService.findById(roomId);

        Player deadPlayer = gameSession.getPlayerMap().get(deadPlayerId);

        Player suspectPlayer = gameSession.getPlayerMap().get(suspectPlayerId);

        List<String> victims = setNightToDay(gameSession, deadPlayerId, protectedPlayerId);

        // 종료 여부 체크
        if (gameSessionService.isDone(gameSession, victims)) {
          return;
        }

        // 밤투표 결과
        template.convertAndSend("/sub/" + roomId, GameStatusKillRes.of(gameSession, deadPlayer));

        // 사망자 OBSERVER 변경
        if (deadPlayer != null) {
          template.convertAndSend("/sub/" + roomId + "/" + deadPlayerId, PlayerDeadRes.of());
        }

        // 용의자 Role 결과
        if (suspectPlayer != null) {
          template.convertAndSend("/sub/" + roomId + "/" + GameRole.POLICE,
              SuspectVoteRes.of(suspectPlayer));
          template.convertAndSend("/sub/" + roomId + "/" + GameRole.OBSERVER,
              SuspectVoteRes.of(suspectPlayer));
        }
      } finally {
        lock.unlock();
      }

      log.info("deadPlayerId: " + deadPlayerId);
      log.info("protectedPlayerId: " + protectedPlayerId);
      log.info("suspectPlayerId: " + suspectPlayerId);

      // Timer를 돌릴 마땅한 위치가 없어서 추후에 통합 예정
      Timer timer = new Timer();
      StartFinTimerTask task = new StartFinTimerTask(redisPublisher, topicStartFin);
      task.setRoomId(roomId);
      timer.schedule(task, gameSession.getTimer() * 1000);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  private List<String> setNightToDay(GameSession gameSession, String deadPlayerId,
      String protectedPlayerId) {
    // 나간 사람 체크 및 기본 세팅
    List<String> victims = gameSession.changePhase(GamePhase.NIGHT_TO_DAY, 15);

    if (deadPlayerId != null) {
      gameSession.eliminatePlayer(deadPlayerId);
      victims.add(gameSession.getPlayerMap().get(deadPlayerId).getNickname());
    }

    gameSessionService.update(gameSession);

    return victims;
  }
}
