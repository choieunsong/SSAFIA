package s05.p12a104.mafia.common.exception;

public class OverMaxPlayerCountException extends RuntimeException {

  public OverMaxPlayerCountException() {
    super("현재 방의 정원이 가득찼습니다.");
  }
}
