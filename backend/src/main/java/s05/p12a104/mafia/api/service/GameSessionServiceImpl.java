package s05.p12a104.mafia.api.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.api.requset.GameSessionPostReq;
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
  public GameSession makeGame(User user, GameSessionPostReq typeInfo) {
    String id = "";

    while (true) {
      id = RandomRoomIdUtils.randomRoomId(typeInfo.getAccessType());
      if (gameSessionRedisRepository.findById(id).equals(Optional.empty())) {
        break;
      }
    }

    if (gameSessionRedisRepository.findByCreatorEmail(user.getEmail()).size() >= 2) {
      throw new GameSessionException("더 이상 방을 만들 수 없습니다");
    }
    gameSessionRedisRepository.save(
        new GameSession(id, user.getEmail(), typeInfo.getAccessType(), typeInfo.getRoomType()));

    GameSession made = gameSessionRedisRepository.findById(id)
        .orElseThrow(() -> new GameSessionException("방정보를 찾을 수 없습니다"));

    return made;
  }

  @Override
  public GameSession enterGame(String roomId) {
    GameSession enter = gameSessionRedisRepository.findById(roomId)
        .orElseThrow(() -> new GameSessionException("방정보를 찾을 수 없습니다"));
    return enter;
  }
}
