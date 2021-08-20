package s05.p12a104.mafia.common.exception;

public class OverMaxIndividualRoomCountException extends RuntimeException {

  public OverMaxIndividualRoomCountException() {
    super("방을 너무 많이 만들었습니다.");
  }
}
