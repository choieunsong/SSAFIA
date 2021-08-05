package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
@RequiredArgsConstructor
public class GameSessionStompLeaveRes {

  private final StompMessageType type = StompMessageType.LEAVE;
  private final String hostId;
  private final String leftPlayerId;
}