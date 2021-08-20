package s05.p12a104.mafia.common.util;

import org.apache.commons.lang3.RandomStringUtils;
import s05.p12a104.mafia.domain.enums.AccessType;

public class RoomIdUtils {
  public static String randomRoomId(AccessType accessType) {
    return getIdPrefix(accessType) + RandomStringUtils.randomAlphanumeric(6);
  }

  public static String getIdPrefix(AccessType accessType) {
    if (accessType == AccessType.PRIVATE) {
      return "V";
    } else {
      return "B";
    }
  }
}
