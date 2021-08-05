package s05.p12a104.mafia.domain.enums;

import s05.p12a104.mafia.common.util.EnumType;

public enum AuthProvider implements EnumType {
  google;

  @Override
  public String getId() {
    return this.name();
  }

  @Override
  public String getText() {
    return this.name();
  }
}
