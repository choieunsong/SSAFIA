package s05.p12a104.mafia.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import s05.p12a104.mafia.common.util.EnumType;

public enum RoomType implements EnumType {
  @JsonProperty("basic")
  BASIC,
  @JsonProperty("custom")
  CUSTOM;

  @Override
  public String getId() {
    return this.name();
  }

  @Override
  public String getText() {
    return this.name();
  }

}
