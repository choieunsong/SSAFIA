package s05.p12a104.mafia.common.exception;

public class OpenViduSessionNotFoundException extends RuntimeException {

  public OpenViduSessionNotFoundException() {
    super("OpenVidu session을 찾을 수 없습니다.");
  }
}
