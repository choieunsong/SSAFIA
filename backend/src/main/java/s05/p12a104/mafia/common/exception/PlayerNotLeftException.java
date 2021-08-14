package s05.p12a104.mafia.common.exception;

public class PlayerNotLeftException extends RuntimeException {
  public PlayerNotLeftException() {
    super("아직 나가지 않은 플레이어입니다");
  }
}