package s05.p12a104.mafia.redispubsub;

import java.util.List;
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
import s05.p12a104.mafia.common.util.TimeUtils;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.redispubsub.message.DayEliminationMessage;
import s05.p12a104.mafia.stomp.response.GameStatusKillRes;
import s05.p12a104.mafia.stomp.response.PlayerDeadRes;
import s05.p12a104.mafia.stomp.task.StartFinTimerTask;

@Slf4j
@RequiredArgsConstructor
@Service
public class DayEliminationFinSubscriber {

  private final ObjectMapper objectMapper;
  private final SimpMessagingTemplate template;
  private final RedisPublisher redisPublisher;
  private final GameSessionService gameSessionService;
  private final ChannelTopic topicDayToNightFin;
  private final RedissonClient redissonClient;
  private static final String KEY = "GameSession";

  public void sendMessage(String message) {
    try {
      DayEliminationMessage dayEliminationMessage =
          objectMapper.readValue(message, DayEliminationMessage.class);
      String roomId = dayEliminationMessage.getRoomId();
      String deadPlayerId = dayEliminationMessage.getDeadPlayerId();
      Player deadPlayer = null;

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
      try {
        gameSession = gameSessionService.findById(roomId);
        deadPlayer = gameSession.getPlayerMap().get(deadPlayerId);
        List<String> victims = setDayToNight(gameSession, deadPlayerId);

        // 종료 여부 체크
        if (gameSessionService.isDone(gameSession, victims)) {
          return;
        }

        log.info("Room {} start Day {} {} ", gameSession.getRoomId(), gameSession.getDay(),
            gameSession.getPhase());

        // 밤투표 결과
        template.convertAndSend("/sub/" + roomId, GameStatusKillRes.of(gameSession, deadPlayer));

        // 사망자 OBSERVER 변경
        if (deadPlayer != null) {
          template.convertAndSend("/sub/" + roomId + "/" + deadPlayerId, PlayerDeadRes.of());
        }

      } finally {
        lock.unlock();
      }

      // Timer를 돌릴 마땅한 위치가 없어서 추후에 통합 예정
      Timer timer = new Timer();
      StartFinTimerTask task = new StartFinTimerTask(redisPublisher, topicDayToNightFin);
      task.setRoomId(roomId);
      timer.schedule(task, TimeUtils.convertToDate(gameSession.getTimer()));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

  private List<String> setDayToNight(GameSession gameSession, String deadPlayerId) {
    log.info("deadPlayer: " + deadPlayerId);
    // 나간 사람 체크 및 기본 세팅
    List<String> victims = gameSession.changePhase(GamePhase.DAY_TO_NIGHT, 15);

    // suspicious 초기화
    gameSession.getPlayerMap().forEach((playerId, player) -> player.setSuspicious(false));

    // 사망 처리
    if (deadPlayerId != null) {
      gameSession.eliminatePlayer(deadPlayerId);
      victims.add(gameSession.getPlayerMap().get(deadPlayerId).getNickname());
    }

    gameSessionService.update(gameSession);

    return victims;
  }
}
