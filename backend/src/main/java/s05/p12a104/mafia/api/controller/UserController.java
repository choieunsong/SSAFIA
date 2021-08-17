package s05.p12a104.mafia.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.common.exception.ResourceNotFoundException;
import s05.p12a104.mafia.common.reponse.ApiResponseDto;
import s05.p12a104.mafia.domain.entity.User;
import s05.p12a104.mafia.domain.repository.UserRepository;
import s05.p12a104.mafia.security.CurrentUser;
import s05.p12a104.mafia.security.UserPrincipal;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  @ApiOperation(value = "사용자 정보", notes = "인증된 사용자의 정보를 반환합니다.", response = ApiResponseDto.class)
  @ApiResponses({@ApiResponse(code = 200, message = "성공"),
      @ApiResponse(code = 401, message = "인증 실패"), @ApiResponse(code = 404, message = "페이지 없음"),
      @ApiResponse(code = 500, message = "서버 오류")})
  @GetMapping("/profile")
  @PreAuthorize("hasRole('USER')")
  public ApiResponseDto<User> getCurrentUser(@ApiIgnore @CurrentUser UserPrincipal userPrincipal) {
    User user = userRepository.findById(userPrincipal.getId())
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    
    log.info("Get user profile: the User Email - {}",  user.getEmail());
    
    return ApiResponseDto.success(user);
  }
}
