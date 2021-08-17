package s05.p12a104.mafia.redispubsub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.common.exception.RedissonLockNotAcquiredException;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameRole;
import s05.p12a104.mafia.stomp.response.GameStatusRes;
import s05.p12a104.mafia.stomp.service.GameSessionVoteService;

@Slf4j
@RequiredArgsConstructor
@Service
public class StartFinSubscriber {

  private final ObjectMapper objectMapper;
  private final SimpMessagingTemplate template;
  private final GameSessionService gameSessionService;
  private final GameSessionVoteService gameSessionVoteService;
  private final RedissonClient redissonClient;
  private static final String KEY = "GameSession";

  public void sendMessage(String redisMessageStr) {
    try {
      String roomId = objectMapper.readValue(redisMessageStr, String.class);

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

        // 나간 사람 체크 및 기본 세팅
        List<String> victims = gameSession.changePhase(GamePhase.DAY_DISCUSSION, 100);
        gameSession.passADay();
        gameSessionService.update(gameSession);

        // 종료 여부 체크
        if (gameSessionService.isDone(gameSession, victims)) {
          return;
        }

        log.info("Start Day " + gameSession.getDay());

        template.convertAndSend("/sub/" + roomId, GameStatusRes.of(gameSession));

        Map<String, GameRole> players = new HashMap();

        gameSession.getPlayerMap().forEach((playerId, player) -> {
          if (player.isAlive()) {
            players.put(playerId, player.getRole());
          }
        });

        gameSessionVoteService.startVote(roomId, gameSession.getPhase(), gameSession.getTimer(),
            players);

      } finally {
        lock.unlock();
      }

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

}
