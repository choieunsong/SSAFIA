package s05.p12a104.mafia.common.exception;

public class RedissonLockNotAcquiredException extends RuntimeException {
  public RedissonLockNotAcquiredException(String message) {
    super(message);
  }
}
