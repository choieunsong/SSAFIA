package s05.p12a104.mafia.api.requset;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameSessionLeaveReq {
  private String token;
  private String userId;
}
