package s05.p12a104.mafia.domain.entity;

import java.io.Serializable;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player implements Serializable{

  @Id
  private String id;
  
  private String nickname;
  
  private String gameRole;
  
  private boolean alive;
}
