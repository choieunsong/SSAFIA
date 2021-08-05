package s05.p12a104.mafia.domain.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import s05.p12a104.mafia.domain.enums.AuthProvider;

@Entity
@Getter
@Setter
// @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String email;

  private String imageUrl;

  private Boolean emailVerified = false;

  @JsonIgnore
  private String password = null;

  @Enumerated(EnumType.STRING)
  private AuthProvider provider;

  private String providerId;
}
