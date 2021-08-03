package s05.p12a104.mafia.common.exception;

public class OverMaxTotalRoomCountException extends RuntimeException {

  public OverMaxTotalRoomCountException() {
    super("현재 방을 생성할 수 없습니다.");
  }
}