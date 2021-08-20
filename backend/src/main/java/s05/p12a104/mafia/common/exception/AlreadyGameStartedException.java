package s05.p12a104.mafia.common.exception;

public class AlreadyGameStartedException extends RuntimeException {

  public AlreadyGameStartedException() {
    super("이미 게임이 시작되었습니다.");
  }
}
