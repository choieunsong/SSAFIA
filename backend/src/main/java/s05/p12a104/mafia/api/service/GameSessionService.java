package s05.p12a104.mafia.api.service;

import s05.p12a104.mafia.api.requset.GameSessionPostReq;
import s05.p12a104.mafia.common.exception.GameSessionException;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.User;

public interface GameSessionService {
  GameSession makeGame(User user, GameSessionPostReq typeInfo) throws GameSessionException;

  GameSession enterGame(String roomInfo) throws GameSessionException;
}
