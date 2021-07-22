package s05.p12a104.mafia.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.common.exception.ResourceNotFoundException;
import s05.p12a104.mafia.domain.entity.User;
import s05.p12a104.mafia.domain.repository.UserRepository;
import s05.p12a104.mafia.security.CurrentUser;
import s05.p12a104.mafia.security.UserPrincipal;

@RequestMapping("/api/user")
@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  @GetMapping("/profile")
  @PreAuthorize("hasRole('USER')")
  public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
    return userRepository.findById(userPrincipal.getId())
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
  }
}
