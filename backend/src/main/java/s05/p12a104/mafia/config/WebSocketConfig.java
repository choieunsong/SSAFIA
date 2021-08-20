package s05.p12a104.mafia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import s05.p12a104.mafia.stomp.Interceptor.PlayerInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws/gamesession/").setAllowedOriginPatterns("*").withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(new PlayerInterceptor());
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // publisher : message-handling methods로 라우팅됨
    registry.setApplicationDestinationPrefixes("/pub");
    // subscriber : topic으로 시작되는 메시지가 메세지브로커로 라우팅됨
    registry.enableSimpleBroker("/sub");
  }

}
