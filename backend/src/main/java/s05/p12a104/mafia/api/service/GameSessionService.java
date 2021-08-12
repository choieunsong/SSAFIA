package s05.p12a104.mafia.api.service;

import java.util.List;
import java.util.Map;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import s05.p12a104.mafia.api.requset.GameSessionPostReq;
import s05.p12a104.mafia.api.response.GameSessionJoinRes;
import s05.p12a104.mafia.common.exception.GameSessionException;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.User;
import s05.p12a104.mafia.domain.enums.GameRole;

public interface GameSessionService {
  GameSession makeGame(User user, GameSessionPostReq typeInfo)
      throws GameSessionException, OpenViduJavaClientException, OpenViduHttpException;

  GameSessionJoinRes getPlayerJoinableState(String roomId, String playerId);

  void removeGameSession(GameSession gameSession);

  GameSession findById(String id);

  void update(GameSession update);

  GameSessionJoinRes addUser(String roomId, String nickname);

  GameSession removeUser(String roomId, String playerId);

  void startGame(GameSession gameSession);

  boolean isDone(GameSession gameSession, List<String> victims);

  void endGame(GameSession gameSession);

  Map<String, GameRole> addObserver(String roomId, String playerId);
}
