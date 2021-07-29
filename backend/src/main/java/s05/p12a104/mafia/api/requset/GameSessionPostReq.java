package s05.p12a104.mafia.api.requset;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class GameSessionPostReq {
  @ApiModelProperty(name = "Room Type", example = "custom")
  String roomType;

  @ApiModelProperty(name = "Access Type", example = "private")
  String accessType;
}
