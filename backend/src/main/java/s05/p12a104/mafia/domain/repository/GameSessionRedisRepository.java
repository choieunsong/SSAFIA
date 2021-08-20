package s05.p12a104.mafia.domain.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import s05.p12a104.mafia.domain.dao.GameSessionDao;

@Repository
public interface GameSessionRedisRepository extends CrudRepository<GameSessionDao, String> {
  List<GameSessionDao> findByCreatorEmail(String email);

  @Override
  List<GameSessionDao> findAll();
}
