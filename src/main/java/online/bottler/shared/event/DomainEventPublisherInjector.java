package online.bottler.shared.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DomainEventPublisherInjector {

    public DomainEventPublisherInjector(ApplicationEventPublisher applicationEventPublisher) {
        DomainEventPublisher.setPublisher(applicationEventPublisher);
    }
}
