package s05.p12a104.mafia.redispubsub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
public class DayToNightFinSubscriber {

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

      GameSession gameSession = null;
      Map<String, GameRole> players = new HashMap();
      try {
        gameSession = gameSessionService.findById(roomId);
        // 나간 사람 체크 및 기본 세팅
        List<String> victims = gameSession.changePhase(GamePhase.NIGHT_VOTE, 30);
        gameSession.setAliveNotCivilian(gameSession.getPlayerMap().entrySet().stream()
            .filter(e -> e.getValue().getRole() != GameRole.CIVILIAN)
            .filter(e -> e.getValue().isAlive()).collect(Collectors.toList()).size());
        gameSessionService.update(gameSession);

        // 종료 여부 체크
        if (gameSessionService.isDone(gameSession, victims)) {
          return;
        }

        log.info("Room {} start Day {} {} ", gameSession.getRoomId(), gameSession.getDay(),
            gameSession.getPhase());

        template.convertAndSend("/sub/" + roomId, GameStatusRes.of(gameSession));

        gameSession.getPlayerMap().forEach((playerId, player) -> {
          if (player.isAlive() && player.getRole() != GameRole.CIVILIAN) {
            players.put(playerId, player.getRole());
          }
        });

      } finally {
        lock.unlock();
      }

      gameSessionVoteService.startVote(roomId, gameSession.getPhase(), gameSession.getTimer(),
          players);
      log.info("NIGHT_VOTE 투표 생성!", roomId);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }

}
