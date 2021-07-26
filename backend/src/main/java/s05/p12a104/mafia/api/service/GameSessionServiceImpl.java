package s05.p12a104.mafia.api.service;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  private final GameSessionRedisRepository gameSessionRedisRepository;

  @Override
  public GameSession makeGame(User user, Map<String, String> type) {
    String Id = "";

    while (true) {
      Id = RandomRoomIdUtils.randomRoomId(type.get("accessType"));
      if (gameSessionRedisRepository.findById(Id).equals(Optional.empty()))
        break;
    }

    int cnt = 0;
    for (GameSession g : gameSessionRedisRepository.findAll()) {
      if (g.getCreator().getId().equals(user.getId()))
        cnt++;
      if (cnt > 2)
        throw new GameSessionException("더 이상 방을 만들 수 없습니다");
    }

    gameSessionRedisRepository
        .save(new GameSession(Id, user, type.get("accessType"), type.get("roomType")));

    GameSession maked = gameSessionRedisRepository.findById(Id)
        .orElseThrow(() -> new GameSessionException("방정보를 찾을 수 없습니다"));

    return maked;
  }

  @Override
  public GameSession enterGame(String Id) {
    GameSession enter = gameSessionRedisRepository.findById(Id)
        .orElseThrow(() -> new GameSessionException("방정보를 찾을 수 없습니다"));
    return enter;
  }
}
