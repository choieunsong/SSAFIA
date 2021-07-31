package s05.p12a104.mafia.common.util;

import org.apache.commons.lang3.RandomStringUtils;

public class RoomIdUtils {
  public static String randomRoomId(String type) {
    return getIdPrefix(type) + RandomStringUtils.randomAlphanumeric(6);
  }

  public static String getIdPrefix(String type) {
    if (type.equals("private")) {
      return "V";
    } else {
      return "B";
    }
  }
}
