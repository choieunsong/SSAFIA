package s05.p12a104.mafia.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import s05.p12a104.mafia.domain.entity.GameRole;
import s05.p12a104.mafia.domain.entity.GameSession;
import s05.p12a104.mafia.domain.entity.Player;
import s05.p12a104.mafia.domain.entity.RoomType;

public class RoleUtils {

  public static Map<GameRole, Integer> getRoleNum(GameSession gameSession) {
    Map<GameRole, Integer> roleNum = new HashMap<>();

    RoomType roomType = gameSession.getRoomType();
    int num = gameSession.getPlayers().size();

    int mafia = 0, doctor = 0, police = 0;
    if ("basic".equals(roomType)) {
      switch (num) {
        case 4:
          mafia = 1;
          break;
        case 5:
          mafia = 1;
          doctor = 1;
          break;
        default: // 6 ~ 10
          mafia = num / 3;
          doctor = 1;
          police = 1;
          break;
      }
//    } else {
//      mafia = gameConfigReq.getMafia();
//      doctor = gameConfigReq.getDoctor();
//      police = gameConfigReq.getPolice();
    }

    roleNum.put(GameRole.CIVILIAN, num - (mafia + doctor + police));
    roleNum.put(GameRole.MAFIA, mafia);
    roleNum.put(GameRole.DOCTOR, doctor);
    roleNum.put(GameRole.POLICE, police);

    return roleNum;
  }
  
  public static void assignRole(Map<GameRole, Integer> roleNum, Map<String, Player> players) {

    List<String> mafias = new ArrayList<>();

    players.forEach((playerId, player) -> {
      GameRole role = GameRole.getRandomRole();
      while (roleNum.get(role) == 0) {
        role = GameRole.getRandomRole();
      }

      player.setRole(role);
      roleNum.put(role, roleNum.get(role) - 1);

      player.setMafias(null);
      if (role == GameRole.MAFIA) {
        mafias.add(player.getNickname());
        player.setMafias(mafias);
      }
    });
  }
}
