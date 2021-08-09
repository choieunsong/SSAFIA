package s05.p12a104.mafia;

import java.time.LocalDateTime;
import java.util.HashMap;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import io.openvidu.java.client.OpenVidu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.domain.dao.GameSessionDao;
import s05.p12a104.mafia.domain.enums.AccessType;
import s05.p12a104.mafia.domain.enums.GameState;
import s05.p12a104.mafia.domain.enums.RoomType;
import s05.p12a104.mafia.domain.repository.GameSessionRedisRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("default")
public class DummyGameSessionCreationRunner implements ApplicationRunner {

  private final GameSessionRedisRepository gameSessionRedisRepository;
  private final OpenVidu openVidu;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    LocalDateTime createdTime = LocalDateTime.now();
    GameSessionDao newGameSessionDao = GameSessionDao.builder()
        .roomId("V1234").accessType(AccessType.PRIVATE).roomType(RoomType.BASIC)
        .creatorEmail("dummy@dummy.com").createdTime(createdTime)
        .finishedTime(createdTime)
        .state(GameState.WAIT)
        .sessionId(openVidu.createSession().getSessionId())
        .playerMap(new HashMap())
        //지우기
        .hostId("dummy")
        .alivePlayer(1)
        .build();

    GameSessionDao saved = gameSessionRedisRepository.save(newGameSessionDao);

    log.info("Dummy Gamesession Created - roomId : {}", saved.getRoomId());
  }
}
