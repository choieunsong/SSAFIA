package s05.p12a104.mafia.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeUtils {
  public static LocalDateTime getFinTime(int time) {
    // 한국 시간으로 맞추기
    // 미리 끝나는 시간을 설정하기 때문에 프론트로 timer를 전달할 때까지 대략 0.4초 차이 난다고 했을 때
    // 정한 시간보다 0.4초 후에 끝내기 위해 400000000ns을 더함
    return LocalDateTime.now(ZoneId.of("Asia/Seoul")).plusSeconds(time).plusNanos(400000000);
  }

  public static int getRemainingTime(LocalDateTime timer) {
    LocalDateTime now = LocalDateTime.now();
    // finTime을 구했을때와 현재 시간이 0.xx초 차이가 나서 정확한 초를 전달하기 위해 올림으로 만듦.
    // 남은 값이 (timer+0.4)가 되고 0.599..를 더해서 (timer+0.99999..)
    Duration duration = Duration.between(now, timer.plusNanos(599999999));
    return (int) duration.getSeconds();
  }

  public static Date convertToDate(LocalDateTime time) {
    return Date.from(time.atZone(ZoneId.of("Asia/Seoul")).toInstant());
  }
}
