package s05.p12a104.mafia.api.controller;

import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import s05.p12a104.mafia.api.requset.GameSessionJoinReq;
import s05.p12a104.mafia.api.requset.GameSessionPostReq;
import s05.p12a104.mafia.api.response.GameSessionJoinRes;
import s05.p12a104.mafia.api.response.GameSessionRes;
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

  @ApiOperation(value = "생성된 방 정보", notes = "방을 생성합니다.", response = ApiResponseDto.class)
  @ApiResponses({@ApiResponse(code = 200, message = "성공"),
      @ApiResponse(code = 401, message = "인증 실패"), @ApiResponse(code = 404, message = "리소스 없음"),
      @ApiResponse(code = 500, message = "서버 오류")})
  @PostMapping
  @PreAuthorize("hasRole('USER')")
  public ApiResponseDto<GameSessionRes> makeRoom(
      @ApiIgnore @CurrentUser UserPrincipal userPrincipal,
      @RequestBody @ApiParam GameSessionPostReq typeInfo)
      throws OpenViduJavaClientException, OpenViduHttpException {

    GameSession gameSession =
        gameSessionService.makeGame(
            userRepository.findById(userPrincipal.getId()).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", userPrincipal.getId())),
            typeInfo);
    return ApiResponseDto.success(GameSessionRes.of(gameSession));
  }

  @ApiOperation(value = "입장 가능 여부", notes = "플레이어가 방의 입장할 수 있는지 미리 확인합니다.", response = ApiResponseDto.class)
  @ApiResponses({@ApiResponse(code = 200, message = "성공"),
      @ApiResponse(code = 401, message = "인증 실패"), @ApiResponse(code = 404, message = "리소스 없음"),
      @ApiResponse(code = 500, message = "서버 오류")})
  @GetMapping("/{roomId}")
  public ApiResponseDto<?> getGameSessionState(@PathVariable("roomId") String roomId,
      @RequestParam(required = false) String playerId) {
    GameSessionJoinRes res = gameSessionService.getPlayerJoinableState(roomId, playerId);
    return ApiResponseDto.success(res);
  }

  @ApiOperation(value = "입장 가능 여부", notes = "플레이어가 방에 입장할 수 있는지 확인합니다.", response = ApiResponseDto.class)
  @ApiResponses({@ApiResponse(code = 200, message = "성공"),
      @ApiResponse(code = 401, message = "인증 실패"), @ApiResponse(code = 404, message = "리소스 없음"),
      @ApiResponse(code = 500, message = "서버 오류")})
  @PostMapping("/{roomId}")
  public ApiResponseDto<GameSessionJoinRes> joinGameSession(@PathVariable("roomId") String roomId,
      @RequestBody GameSessionJoinReq req) {
    String nickname = req.getNickname();
    GameSessionJoinRes res = gameSessionService.addUser(roomId, nickname);
    return ApiResponseDto.success(res);
  }

}
