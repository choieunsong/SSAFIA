package s05.p12a104.mafia.stomp.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import s05.p12a104.mafia.domain.enums.StompMessageType;

@Getter
@RequiredArgsConstructor
@ToString
public class GameSessionStompLeaveRes {

  private final StompMessageType type = StompMessageType.LEAVE;
  private final String hostId;
  private final String leftPlayerId;
}