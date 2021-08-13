package s05.p12a104.mafia.common.util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
  public static Date getFinTime(int time) {
    Calendar cal = Calendar.getInstance();
    
    // 미리 끝나는 시간을 설정하기 때문에 프론트로 timer를 전달할 때까지 대략 0.4초 차이 난다고 했을 때  
    // 정한 시간보다 0.4초 후에 끝내기 위해 400을 더함 
    cal.add(Calendar.MILLISECOND, (time * 1000) + 400);

    return cal.getTime();
  }

  public static int getRemainingTime(Date timer) {
    Date now = Calendar.getInstance().getTime();
    
    // finTime을 구했을때와 현재 시간이 0.xx초 차이가 나서 정확한 초를 전달하기 위해 올림으로 만듦.
    // 남은 값이 (timer+0.4)가 되고 0.599를 더해서 (timer+0.999)/1000 = timer로 
    return (int) (((timer.getTime()) - now.getTime() + 599) / 1000);
  }
}
