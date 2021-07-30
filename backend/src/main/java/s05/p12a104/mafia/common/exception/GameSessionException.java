package s05.p12a104.mafia.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Data;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GameSessionException extends RuntimeException {
  public GameSessionException(String message) {
    super(message);
  }
  
  public GameSessionException(String message, Throwable cause) {
    super(message, cause);
  }
}
