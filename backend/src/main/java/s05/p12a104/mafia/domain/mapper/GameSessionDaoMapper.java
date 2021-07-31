package s05.p12a104.mafia.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import s05.p12a104.mafia.domain.dao.GameSessionDao;
import s05.p12a104.mafia.domain.entity.GameSession;

@Mapper
public interface GameSessionDaoMapper {
  GameSessionDaoMapper INSTANCE = Mappers.getMapper(GameSessionDaoMapper.class);
  @Mapping(target = "sessionId", source = "session.sessionId")
  GameSessionDao toDao(GameSession gameSession);
}
