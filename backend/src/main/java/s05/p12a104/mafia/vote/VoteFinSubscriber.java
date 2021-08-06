package s05.p12a104.mafia.vote;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class VoteFinSubscriber implements MessageListener {

  private final ObjectMapper objectMapper;
  private final RedisTemplate redisTemplate;
  private final SimpMessagingTemplate template;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    System.out.println("Redis Sub Start!");
    try {
      System.out.println(message.getBody().toString());
      String publishMessage =
          (String) redisTemplate.getValueSerializer().deserialize(message.getBody());
      System.out.println("Redis Sub Message: " + publishMessage);
//      VoteDao vote = objectMapper.readValue(publishMessage, VoteDao.class);
//      template.convertAndSend("/sub/" + vote.getVoteId(), vote.toString());
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
