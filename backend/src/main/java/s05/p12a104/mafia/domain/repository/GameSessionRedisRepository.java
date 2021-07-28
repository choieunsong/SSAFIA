package s05.p12a104.mafia.domain.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import s05.p12a104.mafia.domain.entity.GameSession;

@Repository
public interface GameSessionRedisRepository extends CrudRepository<GameSession, String> {
  List<GameSession> findByCreatorEmail(String email);
}