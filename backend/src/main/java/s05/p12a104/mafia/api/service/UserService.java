package s05.p12a104.mafia.api.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
  UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
