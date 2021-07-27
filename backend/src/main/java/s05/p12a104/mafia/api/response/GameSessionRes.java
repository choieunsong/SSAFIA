package s05.p12a104.mafia.api.response;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import s05.p12a104.mafia.domain.entity.GameSession;

@Getter
@Setter
@ApiModel("GameSessionResponse")
public class GameSessionRes {
  String id;
  String accessType;
  String roomType;

  public static GameSessionRes of(GameSession gameSession) {
    GameSessionRes res = new GameSessionRes();
    res.setId(gameSession.getRoomId());
    res.setAccessType(gameSession.getAccessType());
    res.setRoomType(gameSession.getRoomType());
    return res;
  }
}
