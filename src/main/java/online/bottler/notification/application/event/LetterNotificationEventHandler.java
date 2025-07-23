package online.bottler.notification.application.event;

import static online.bottler.notification.domain.NotificationType.KEYWORD_REPLY;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.bottler.letter.application.service.GetLetterLabelService;
import online.bottler.letter.domain.event.LetterBlockedEvent;
import online.bottler.letter.domain.event.ReplyLetterBlockedEvent;
import online.bottler.letter.domain.event.ReplyLetterCreatedEvent;
import online.bottler.notification.application.port.NotificationUseCase;
import online.bottler.notification.application.request.RecommendNotificationCommand;
import online.bottler.recommendation.domain.event.RecommendedLetterCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class LetterNotificationEventHandler {

    private final NotificationUseCase notificationUseCase;
    private final GetLetterLabelService getLetterLabelService;

    @EventListener
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void onRecommendedLetterCreatedEvent(RecommendedLetterCreatedEvent event) {
        try {
            String label = getLetterLabelService.getLabel(event.letterId());
            notificationUseCase.sendKeywordNotification(
                    RecommendNotificationCommand.of(event.userId(), event.letterId(), label)
            );
            log.info("추천 알림 처리 완료: userId={}, letterId={}", event.userId(), event.letterId());
        } catch (Exception e) {
            log.error("추천 알림 처리 중 예외 발생: {}", e.getMessage(), e);
        }
    }

    @EventListener
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void onReplyLetterCreatedEvent(ReplyLetterCreatedEvent event) {
        try {
            String label = getLetterLabelService.getLabel(event.letterId());
            notificationUseCase.sendLetterNotification(
                    KEYWORD_REPLY, event.userId(), event.letterId(), label
            );
        } catch (Exception e) {
            log.error("알림 실패 : {}", e.getMessage());
        }
    }

    @EventListener
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void onReplyLetterBlockedEvent(ReplyLetterBlockedEvent event) {
        try {
            notificationUseCase.sendWarningNotification(event.userId());
        } catch (Exception e) {
            log.error("알림 실패 : {}", e.getMessage());
        }
    }

    @EventListener
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void onLetterBlockedEvent(LetterBlockedEvent event) {
        try {
            notificationUseCase.sendWarningNotification(event.userId());
        } catch (Exception e) {
            log.error("알림 실패 : {}", e.getMessage());
        }
    }
}
