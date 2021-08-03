package s05.p12a104.mafia.api.requset;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import s05.p12a104.mafia.domain.entity.AccessType;
import s05.p12a104.mafia.domain.entity.RoomType;

@Getter
@Setter
@ApiModel
public class GameSessionPostReq {
  @ApiModelProperty(name = "Room Type", example = "basic")
  RoomType roomType;

  @ApiModelProperty(name = "Access Type", example = "private")
  AccessType accessType;
}
