package s05.p12a104.mafia.api.service;

import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import s05.p12a104.mafia.api.requset.GameSessionPostReq;
import s05.p12a104.mafia.api.response.GameSessionJoinRes;
import s05.p12a104.mafia.common.exception.GameSessionException;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.GameState;
import s05.p12a104.mafia.domain.entity.User;

public interface GameSessionService {
  GameSession makeGame(User user, GameSessionPostReq typeInfo)
      throws GameSessionException, OpenViduJavaClientException, OpenViduHttpException;

  GameState getGameSessionState(String roomId) throws GameSessionException;

  void removeGameSession(GameSession gameSession);

  GameSession findById(String id);

  void update(GameSession update);

  GameSessionJoinRes addUser(String roomId, String nickname);

  boolean removeUser(String roomId, String token);
}
