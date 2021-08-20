package s05.p12a104.mafia.stomp.task;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.common.exception.RedissonLockNotAcquiredException;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.stomp.response.GameSessionReadyRes;

@Slf4j
@RequiredArgsConstructor
public class ReadyTimerTask extends TimerTask {

  private String roomId;
  private final GameSessionService gameSessionService;
  private final SimpMessagingTemplate template;
  private final RedissonClient redissonClient;
  private static final String KEY = "GameSession";

  @Override
  public void run() {
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

    try {
      GameSession gameSession = gameSessionService.findById(roomId);
      gameSessionService.endGame(gameSession);

      log.info("Send ready message to frontend: the room id - {}", gameSession.getRoomId());
      
      template.convertAndSend("/sub/" + roomId, GameSessionReadyRes.of(gameSession));

    } finally {
      lock.unlock();
    }
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }
}
