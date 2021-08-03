package s05.p12a104.mafia.common.exception;

public class GameSessionNotFoundException extends RuntimeException {

  public GameSessionNotFoundException() {
    super("방 정보를 찾을 수 없습니다");
  }
}
