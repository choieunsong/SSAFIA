package s05.p12a104.mafia.domain.entity;

import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Session;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Builder
@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GameSession {

  @NonNull
  private final String roomId;

  private int day;

  private int phaseCount;

  private boolean isNight;

  @NonNull
  private final String creatorEmail;

//  private List<Player> players;

  @NonNull
  @Enumerated(EnumType.STRING)
  private final AccessType accessType;

  @NonNull
  @Enumerated(EnumType.STRING)
  private RoomType roomType;

  @NonNull
  @Enumerated(EnumType.STRING)
  private GameState state;

  @Enumerated(EnumType.STRING)
  private GamePhase phase;

  @NonNull
  private final LocalDateTime createdTime;

  @NonNull
  private LocalDateTime finishedTime;

  private String lastEnter;

  @NonNull
  private final Session session;

  private String hostId;

  private final Map<String, OpenViduRole> mapSessionNamesTokens;

  public static GameSessionBuilder builder(String roomId, String creatorEmail, AccessType accessType,
      RoomType roomType, LocalDateTime createdTime, Session session,
      Map<String, OpenViduRole> mapSessionNamesTokens) {
    return new GameSessionBuilder()
        .roomId(roomId)
        .creatorEmail(creatorEmail)
        .accessType(accessType)
        .roomType(roomType)
        .createdTime(createdTime)
        .session(session)
        .state(GameState.WAIT)
        .mapSessionNamesTokens(mapSessionNamesTokens);
  }
}
