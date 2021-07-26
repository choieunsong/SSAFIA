package s05.p12a104.mafia.common.util;

import org.apache.commons.lang3.RandomStringUtils;

public class RandomRoomIdUtils {
  public static String randomRoomId(String type) {
    String Id;
    
    if (type.equals("Private"))
      Id = "V";
    else
      Id = "B";
    
    Id += RandomStringUtils.randomAlphanumeric(6);
    
    return Id;
  }
}
