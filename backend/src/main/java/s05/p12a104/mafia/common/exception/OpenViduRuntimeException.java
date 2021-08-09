package s05.p12a104.mafia.common.exception;

public class OpenViduRuntimeException extends RuntimeException {
  public OpenViduRuntimeException() {
    super("OpenVidu에 문제가 발생하였습니다.");
  }

  public OpenViduRuntimeException(String message) {
    super(message);
  }
}
