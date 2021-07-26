package s05.p12a104.mafia.domain.entity;

import java.security.Timestamp;
import java.util.List;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RedisHash("GameSession")
public class GameSession {

  @Id
  private String roomId;

  private int day;

  private int phaseCount;

  private boolean night;

  @Indexed
  private String creator;

  private List<Player> players;

  private String url;

  private String accessType;

  private String roomType;

  @Enumerated(EnumType.STRING)
  private GameState state;

  @Enumerated(EnumType.STRING)
  private GamePhase phase;

  @CreationTimestamp
  private Timestamp createtime;

  private Timestamp lastEnter;

  @Builder
  public GameSession(String roomId, String creator, String accessType, String roomType) {
    this.roomId = roomId;
    this.creator = creator;
    this.accessType = accessType;
    this.roomType = roomType;
    this.state = GameState.wait;
  }

}
