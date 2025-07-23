package online.bottler.notification.adapter.out.push;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import online.bottler.shared.exception.AdaptorException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
public class NotificationSubscriber implements MessageListener {

    private final Jackson2JsonRedisSerializer<SseMessage> serializer = new Jackson2JsonRedisSerializer<>(
            SseMessage.class);
    private final String userId;
    private final SseEmitter sseEmitter;

    public NotificationSubscriber(SseEmitter sseEmitter, String userId) {
        this.sseEmitter = sseEmitter;
        this.userId = userId;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            SseMessage sseMessage = serializer.deserialize(message.getBody());
            sseEmitter.send(
                    SseEmitter.event()
                            .id(userId)
                            .name(sseMessage.getTitle())
                            .data(sseMessage.getContent())
            );
        } catch (IOException e) {
            log.error("SSE 알림 전송 중 오류 발생: {}", e.getMessage());
            throw new AdaptorException("SSE 알림 전송 실패하였습니다.");
        }
    }
}
