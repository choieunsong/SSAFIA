package s05.p12a104.mafia.common.util;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomRoomIdUtils {
  public static String randomRoomId(String type) {
    String id;

    if (type.equals("private")) {
      id = "V";
    } else {
      id = "B";
    }
    id += RandomStringUtils.randomAlphanumeric(6);

    return id;
  }
}
