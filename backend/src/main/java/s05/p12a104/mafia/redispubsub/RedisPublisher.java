package s05.p12a104.mafia.redispubsub;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisPublisher {
  private final RedisTemplate<String, Object> redisTemplate;
  
  public void publish(ChannelTopic topic, String roomId) {
    redisTemplate.convertAndSend(topic.getTopic(), roomId);    
  }
}
