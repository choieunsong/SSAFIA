package s05.p12a104.mafia.api.service;

import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.common.exception.GameSessionException;
import s05.p12a104.mafia.common.util.RandomRoomIdUtils;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.User;
import s05.p12a104.mafia.domain.repository.GameSessionRedisRepository;

@Service
@RequiredArgsConstructor
public class GameSessionServiceImpl implements GameSessionService {

  private final GameSessionRedisRepository gameSessionRedisRepository;

  @Override
  public GameSession makeGame(User user, Map<String, String> type) {
    String id = "";

    while (true) {
      id = RandomRoomIdUtils.randomRoomId(type.get("accessType"));
      if (gameSessionRedisRepository.findById(id).equals(Optional.empty())) {
        break;
      }
    }
    
    if (gameSessionRedisRepository.findByCreator(user.getEmail()).size() >= 2) {
      throw new GameSessionException("더 이상 방을 만들 수 없습니다");
    }
    gameSessionRedisRepository
        .save(new GameSession(id, user.getEmail(), type.get("accessType"), type.get("roomType")));

    GameSession maked = gameSessionRedisRepository.findById(id)
        .orElseThrow(() -> new GameSessionException("방정보를 찾을 수 없습니다"));

    return maked;
  }

  @Override
  public GameSession enterGame(String id) {
    GameSession enter = gameSessionRedisRepository.findById(id)
        .orElseThrow(() -> new GameSessionException("방정보를 찾을 수 없습니다"));
    return enter;
  }
}
