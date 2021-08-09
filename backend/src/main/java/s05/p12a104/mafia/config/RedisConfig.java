package s05.p12a104.mafia.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import s05.p12a104.mafia.redispubsub.DayDiscussionFinSubscriber;
import s05.p12a104.mafia.redispubsub.DayEliminationFinSubscriber;
import s05.p12a104.mafia.redispubsub.StartFinSubscriber;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

  @Bean
  @Primary
  public RedisProperties redisProperties() {
    return new RedisProperties();
  }

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    RedisProperties properties = redisProperties();
    return new LettuceConnectionFactory(properties.getHost(), properties.getPort());
  }

  @Bean
  public RedisMessageListenerContainer redisMessageListener(
      RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter,
      MessageListenerAdapter dayDisculistenerAdapter,
      MessageListenerAdapter dayEliminationlistenerAdapter) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    container.addMessageListener(listenerAdapter, new ChannelTopic("START_FIN"));
    container.addMessageListener(dayDisculistenerAdapter, new ChannelTopic("DAY_DISCUSSION_FIN"));
    container.addMessageListener(dayEliminationlistenerAdapter, new ChannelTopic("DAY_ELIMINAION_FIN"));
    return container;
  }

  @Bean
  public MessageListenerAdapter listenerAdapter(StartFinSubscriber subscriber) {
    return new MessageListenerAdapter(subscriber, "sendMessage");
  }

  @Bean
  public MessageListenerAdapter dayDisculistenerAdapter(DayDiscussionFinSubscriber subscriber) {
    return new MessageListenerAdapter(subscriber, "sendMessage");
  }

  @Bean
  public MessageListenerAdapter dayEliminationlistenerAdapter(
      DayEliminationFinSubscriber subscriber) {
    return new MessageListenerAdapter(subscriber, "sendMessage");
  }

  @Bean
  public RedisTemplate<?, ?> redisTemplate() {
    RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory());
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
    redisTemplate.setHashKeySerializer(new StringRedisSerializer());
    redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    return redisTemplate;
  }

}
