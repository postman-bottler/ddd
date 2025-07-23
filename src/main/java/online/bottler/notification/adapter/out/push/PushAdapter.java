package online.bottler.notification.adapter.out.push;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.notification.application.port.PushNotificationPort;
import online.bottler.notification.domain.PushMessages;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PushAdapter implements PushNotificationPort {
    private final FirebaseMessageMapper messageMapper;
    private final RedisTemplate<String, SseMessage> messageTemplate;

    @Override
    @Async
    @CircuitBreaker(name = "fcmBreaker", fallbackMethod = "pushViaSSE")
    public void pushAll(PushMessages pushMessages) {
        List<Message> firebaseMessages = messageMapper.mapToFirebaseMessages(pushMessages);
        try {
            FirebaseMessaging.getInstance().sendEach(firebaseMessages);
        } catch (FirebaseMessagingException e) {
            throw new AdaptorException(e.getMessage());
        }
    }

    protected void pushViaSSE(PushMessages pushMessages, Throwable throwable) {
        log.warn("FCM 알림 전송 실패, SSE로 대체: {}", throwable.getMessage());
        pushMessages.getMessages()
                .forEach(pushMessage -> {
                    String userId = pushMessage.getUserId().toString();
                    messageTemplate.convertAndSend(userId,
                            new SseMessage(pushMessage.getTitle(), pushMessage.getContent()));
                });
    }
}
