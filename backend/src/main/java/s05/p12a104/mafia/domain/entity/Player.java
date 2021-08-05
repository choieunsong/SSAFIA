package s05.p12a104.mafia.domain.entity;

import io.openvidu.java.client.OpenViduRole;
import java.io.Serializable;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.domain.enums.Color;
import s05.p12a104.mafia.domain.enums.GameRole;

@Slf4j
@Getter
@Builder
public class Player implements Serializable {

  @Id
  private final String id;

  private final String nickname;

  private GameRole role;

  private boolean alive;

  private final Color color;

  private final String token;

  private final OpenViduRole openViduRole;

  public void setRole(GameRole role) {
    this.role = role;
  }

  public void setAlive(boolean alive) {
    this.alive = alive;
  }

  public static PlayerBuilder builder(String id, String nickname, Color color, String token,
      OpenViduRole openViduRole) {
    return new PlayerBuilder()
        .id(id)
        .nickname(nickname)
        .color(color)
        .token(token)
        .openViduRole(openViduRole);
  }
}
