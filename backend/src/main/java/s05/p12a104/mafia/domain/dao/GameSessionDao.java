package s05.p12a104.mafia.domain.dao;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.enums.AccessType;
import s05.p12a104.mafia.domain.enums.GamePhase;
import s05.p12a104.mafia.domain.enums.GameState;
import s05.p12a104.mafia.domain.enums.RoomType;

@Getter
@Slf4j
@RedisHash("GameSession")
@Builder
public class GameSessionDao {

  @Id
  private final String roomId;

  private final int day;

  private final int phaseCount;

  private final boolean night;

  private int aliveMafia;

  private int alivePlayer;

  private int aliveNotCivilian;

  private LocalDateTime timer;

  private List<String> mafias;

  @Indexed
  private final String creatorEmail;

  private final Map<String, Player> playerMap;

  private final AccessType accessType;

  private final RoomType roomType;

  @Enumerated(EnumType.STRING)
  private final GameState state;

  @Enumerated(EnumType.STRING)
  private final GamePhase phase;

  private final LocalDateTime createdTime;

  private final LocalDateTime finishedTime;

  private final String lastEnter;

  private final String sessionId;

  private final String hostId;

}
