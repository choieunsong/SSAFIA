package s05.p12a104.mafia.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.api.requset.GameSessionPostReq;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.common.exception.ResourceNotFoundException;
import s05.p12a104.mafia.common.reponse.ApiResponseDto;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.repository.UserRepository;
import s05.p12a104.mafia.security.CurrentUser;
import s05.p12a104.mafia.security.UserPrincipal;
import springfox.documentation.annotations.ApiIgnore;

@RequestMapping("/api/gamesession")
@RestController
@RequiredArgsConstructor
public class GameSessionController {

  private final UserRepository userRepository;

  private final GameSessionService gameSessionService;

  @ApiOperation(value = "방 정보", notes = "만들어진 방의 정보를 반환합니다.", response = ApiResponseDto.class)
  @ApiResponses({@ApiResponse(code = 200, message = "성공"),
      @ApiResponse(code = 401, message = "인증 실패"), @ApiResponse(code = 404, message = "페이지 없음"),
      @ApiResponse(code = 500, message = "서버 오류")})
  @PostMapping
  @PreAuthorize("hasRole('USER')")
  public ApiResponseDto<GameSession> makeRoom(@ApiIgnore @CurrentUser UserPrincipal userPrincipal,
      @RequestBody @ApiParam GameSessionPostReq typeInfo) {

    GameSession gamesession =
        gameSessionService.makeGame(
            userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userPrincipal.getId())),
            typeInfo);
    return ApiResponseDto.success(gamesession);
  }

  @ApiOperation(value = "방 정보", notes = "들어가는 방의 정보를 반환합니다.", response = ApiResponseDto.class)
  @ApiResponses({@ApiResponse(code = 200, message = "성공"),
      @ApiResponse(code = 401, message = "인증 실패"), @ApiResponse(code = 404, message = "페이지 없음"),
      @ApiResponse(code = 500, message = "서버 오류")})
  @GetMapping("/{roomId}")
  public ApiResponseDto<GameSession> enterRoom(@PathVariable("roomId") String roomId) {

    GameSession gamesession = gameSessionService.enterGame(roomId);
    return ApiResponseDto.success(gamesession);
  }
}
