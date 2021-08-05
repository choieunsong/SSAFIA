package s05.p12a104.mafia.stomp.Interceptor;

import java.util.List;
import java.util.Map;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageHeaderAccessor;
import s05.p12a104.mafia.domain.entity.PlayerStompPrincipal;


public class PlayerInterceptor implements ChannelInterceptor {

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {

    StompHeaderAccessor accessor =
        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      Object raw = message
          .getHeaders()
          .get(SimpMessageHeaderAccessor.NATIVE_HEADERS);

      if (raw instanceof Map) {
        Object name = ((Map) raw).get("playerId");

        if (name instanceof List) {
          accessor.setUser(new PlayerStompPrincipal(((List) name).get(0).toString()));
        }
      }
    }
    return message;
  }
}
