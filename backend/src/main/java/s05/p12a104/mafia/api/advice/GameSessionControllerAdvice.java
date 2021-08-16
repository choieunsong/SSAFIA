package s05.p12a104.mafia.api.advice;

import static s05.p12a104.mafia.common.reponse.ApiResponseCode.FAIL;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import s05.p12a104.mafia.common.exception.AlreadyGameStartedException;
import s05.p12a104.mafia.common.exception.BadRequestException;
import s05.p12a104.mafia.common.exception.GameSessionNotFoundException;
import s05.p12a104.mafia.common.exception.OpenViduRuntimeException;
import s05.p12a104.mafia.common.exception.OpenViduSessionNotFoundException;
import s05.p12a104.mafia.common.exception.OverMaxIndividualRoomCountException;
import s05.p12a104.mafia.common.exception.OverMaxPlayerCountException;
import s05.p12a104.mafia.common.exception.OverMaxTotalRoomCountException;
import s05.p12a104.mafia.common.exception.PlayerNotLeftException;
import s05.p12a104.mafia.common.reponse.ApiResponseDto;

@RestControllerAdvice
public class GameSessionControllerAdvice {

  @ExceptionHandler(IllegalStateException.class)
  public ApiResponseDto<?> illegalStateExceptionHandler(IllegalStateException e) {
    return new ApiResponseDto<>(FAIL, e.getMessage());
  }

  @ExceptionHandler(OverMaxTotalRoomCountException.class)
  public ApiResponseDto<?> overMaxTotalRoomCountException(OverMaxTotalRoomCountException e) {
    return new ApiResponseDto<>(FAIL, e.getMessage());
  }

  @ExceptionHandler(OverMaxIndividualRoomCountException.class)
  public ApiResponseDto<?> overMaxIndividualRoomCountExceptionHandler(
      OverMaxIndividualRoomCountException e) {
    return new ApiResponseDto<>(FAIL, e.getMessage());
  }

  @ExceptionHandler(AlreadyGameStartedException.class)
  public ApiResponseDto<?> alreadyGameStartedExceptionHandler(AlreadyGameStartedException e) {
    return new ApiResponseDto<>(FAIL, e.getMessage());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(GameSessionNotFoundException.class)
  public ApiResponseDto<?> gameSessionNotFoundExceptionHandler(GameSessionNotFoundException e) {
    return new ApiResponseDto<>(FAIL, e.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BadRequestException.class)
  public ApiResponseDto<?> badRequestExceptionHandler(BadRequestException e) {
    return new ApiResponseDto<>(FAIL, e.getMessage());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(OpenViduSessionNotFoundException.class)
  public ApiResponseDto<?> openViduSessionNotFoundExceptionHandler(
      OpenViduSessionNotFoundException e) {
    return new ApiResponseDto<>(FAIL, e.getMessage());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(OpenViduRuntimeException.class)
  public ApiResponseDto<?> openViduRuntimeExceptionHandler(OpenViduRuntimeException e) {
    return new ApiResponseDto<>(FAIL, e.getMessage());
  }

  @ExceptionHandler(OverMaxPlayerCountException.class)
  public ApiResponseDto<?> overMaxPlayerCountExceptionHandler(OverMaxPlayerCountException e) {
    return new ApiResponseDto<>(FAIL, e.getMessage());
  }

  @ExceptionHandler(PlayerNotLeftException.class)
  public ApiResponseDto<?> playerNotLeftExceptionHandler(PlayerNotLeftException e) {
    return new ApiResponseDto<>(FAIL, e.getMessage());
  }
}
