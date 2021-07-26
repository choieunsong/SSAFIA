package s05.p12a104.mafia.api.service;

import java.util.Map;
import s05.p12a104.mafia.common.exception.GameSessionException;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.User;

public interface GameSessionService {
  GameSession makeGame(User user, Map<String, String> type) throws GameSessionException;

  GameSession enterGame(String Id) throws GameSessionException;
}
