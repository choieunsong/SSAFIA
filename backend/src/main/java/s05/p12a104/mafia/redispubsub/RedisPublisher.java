package s05.p12a104.mafia.redispubsub;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import s05.p12a104.mafia.redispubsub.message.DayDiscussionMessage;
import s05.p12a104.mafia.redispubsub.message.DayEliminationMessage;
import s05.p12a104.mafia.redispubsub.message.EndMessgae;
import s05.p12a104.mafia.redispubsub.message.NightVoteMessage;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisPublisher {
  private final RedisTemplate<String, Object> redisTemplate;

  public void publish(ChannelTopic topic, String roomId) {
    redisTemplate.convertAndSend(topic.getTopic(), roomId);
  }

  public void publish(ChannelTopic topic, DayDiscussionMessage message) {
    redisTemplate.convertAndSend(topic.getTopic(), message);
  }

  public void publish(ChannelTopic topic, DayEliminationMessage message) {
    redisTemplate.convertAndSend(topic.getTopic(), message);
  }

  public void publish(ChannelTopic topic, NightVoteMessage message) {
    redisTemplate.convertAndSend(topic.getTopic(), message);
  }

  public void publish(ChannelTopic topic, EndMessgae message) {
    redisTemplate.convertAndSend(topic.getTopic(), message);
  }

}
