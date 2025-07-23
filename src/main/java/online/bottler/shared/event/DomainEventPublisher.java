package online.bottler.shared.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
public class DomainEventPublisher {

    private static ApplicationEventPublisher publisher;

    public static <T> void publish(T event) {
        if (publisher == null) {
            log.warn("ApplicationEventPublisher is not initialized. Skipping publish for event: {}", event);
            return;
        }

        try {
            publisher.publishEvent(event);
            log.info("Publish event success: {}", event);
        } catch (Exception e) {
            log.error("Publish event fail: {}", event, e);
        }
    }

    static void setPublisher(ApplicationEventPublisher applicationEventPublisher) {
        DomainEventPublisher.publisher = applicationEventPublisher;
    }
}
