package s05.p12a104.mafia.api.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.api.service.GameSessionService;
import s05.p12a104.mafia.common.exception.ResourceNotFoundException;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.repository.GameSessionRedisRepository;
import s05.p12a104.mafia.domain.repository.UserRepository;
import s05.p12a104.mafia.security.CurrentUser;
import s05.p12a104.mafia.security.UserPrincipal;

@RequestMapping("/api/gamesession")
@RestController
@RequiredArgsConstructor
public class GameSessionController {
  
  private final UserRepository userRepository;

  private final GameSessionService gameSessionService;

  @PostMapping("")
  @PreAuthorize("hasRole('USER')")
  public GameSession makeRoom(@CurrentUser UserPrincipal userPrincipal,
      @RequestBody Map<String, String> type) {
    
    GameSession response =
        gameSessionService.makeGame(userRepository.findById(userPrincipal.getId()).orElseThrow(
            () -> new ResourceNotFoundException("User", "id", userPrincipal.getId())), type);
    return response;
  }

  @GetMapping("/{roomId}")
  public GameSession enterRoom(@PathVariable String roomId) {
    
    GameSession response = gameSessionService.enterGame(roomId);
    return response;
  }
}
