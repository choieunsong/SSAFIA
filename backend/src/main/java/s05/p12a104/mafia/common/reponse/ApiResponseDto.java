package s05.p12a104.mafia.common.reponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ApiResponseDto<T> {
  public static final ApiResponseDto<String> DEFAULT_SUCCESS = new ApiResponseDto<>(ApiResponseCode.SUCCESS);
  public static final ApiResponseDto<String> DEFAULT_UNAUTHORIZED = new ApiResponseDto<>(ApiResponseCode.UNAUTHORIZED);

  private ApiResponseCode code;
  private T data;
  private String message;

  public ApiResponseDto(ApiResponseCode code) {
    this.code = code;
  }

  public ApiResponseDto(ApiResponseCode code, T data) {
    this(code);
    this.data = data;
  }

  public ApiResponseDto(ApiResponseCode code, String message) {
    this(code);
    this.message = message;
  }

  public static <T> ApiResponseDto<T> success(T data) {
    return new ApiResponseDto<>(ApiResponseCode.SUCCESS, data);
  }
}
