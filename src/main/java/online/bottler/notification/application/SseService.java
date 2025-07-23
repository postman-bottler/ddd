package online.bottler.notification.application;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.notification.application.port.ListenerPort;
import online.bottler.notification.application.port.SseEmitterPort;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
@Slf4j
public class SseService {

    private final SseEmitterPort sseEmitterPort;
    private final ListenerPort listenerPort;

    public SseEmitter connect(String userId) {
        SseEmitter emitter = new SseEmitter(15 * 60 * 1000L);
        sseEmitterPort.save(userId, emitter);
        listenerPort.save(userId, emitter);

        emitter.onCompletion(() -> cleanUp(userId));
        emitter.onTimeout(() -> cleanUp(userId));

        try {
            emitter.send(SseEmitter.event()
                    .id(userId)
                    .name("connected")
                    .data("Connection established for user: " + userId)
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new AdaptorException("SSE 연결에 실패하였습니다. userId: " + userId);
        }
        return emitter;
    }

    private void cleanUp(String userId) {
        sseEmitterPort.delete(userId);
        listenerPort.delete(userId);
    }
}
