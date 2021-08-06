package s05.p12a104.mafia.stomp.task;

import java.util.TimerTask;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import s05.p12a104.mafia.redispubsub.RedisPublisher;

@RequiredArgsConstructor
@Service
public class DayDiscussionVoteFinTimerTask extends TimerTask {

  private String roomId;
  private final RedisPublisher redisPublisher;

  @Override
  public void run() {
    redisPublisher.publish(new ChannelTopic("DAYDISCUSSION_FIN"), roomId);
  }
  
  @Override
  public boolean cancel() {
    redisPublisher.publish(new ChannelTopic("DAYDISCUSSION_FIN"), roomId);
    return super.cancel();
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }
}
