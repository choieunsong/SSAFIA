package s05.p12a104.mafia.api.response;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@ApiModel("GameSessionStatusResponse")
@Getter
@RequiredArgsConstructor
public class GameSessionStatusRes {

  private final String status;
}