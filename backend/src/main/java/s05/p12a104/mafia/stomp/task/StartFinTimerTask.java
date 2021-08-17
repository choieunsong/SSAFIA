package s05.p12a104.mafia.stomp.task;

import java.util.TimerTask;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.redispubsub.RedisPublisher;

@Slf4j
@RequiredArgsConstructor
@Service
public class StartFinTimerTask extends TimerTask {

  private String roomId;
  private final RedisPublisher redisPublisher;
  private final ChannelTopic topicStartFin;

  @Override
  public void run() {
    redisPublisher.publish(topicStartFin, roomId);
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }
}
