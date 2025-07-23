package online.bottler.notification.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.bottler.notification.application.port.PushNotificationPort;
import online.bottler.notification.application.request.RecommendNotificationCommand;
import online.bottler.notification.application.response.NotificationResponse;
import online.bottler.notification.application.response.UnreadNotificationResponse;
import online.bottler.notification.application.port.NotificationUseCase;
import online.bottler.notification.application.port.NotificationPersistencePort;
import online.bottler.notification.application.port.SubscriptionPersistencePort;
import online.bottler.notification.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements NotificationUseCase {
    private final NotificationPersistencePort notificationPersistencePort;
    private final SubscriptionPersistencePort subscriptionPersistencePort;
//    private final PushNotificationPort pushNotificationPort;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendBanNotification(Long userId) {
        try {
            sendNotification(NotificationType.BAN, userId, null, null);
        } catch (RuntimeException e) {
            log.error("알림 저장 실패 : {}", e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendWarningNotification(Long userId) {
        try {
            sendNotification(NotificationType.WARNING, userId, null, null);
        } catch (RuntimeException e) {
            log.error("알림 저장 실패 : {}", e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendLetterNotification(NotificationType type, Long userId, Long letterId, String label) {
        try {
            sendNotification(type, userId, letterId, label);
        } catch (RuntimeException e) {
            log.error("알림 저장 실패 : {}", e.getMessage());
        }
    }

    public NotificationResponse sendNotification(NotificationType type, Long userId, Long letterId, String label) {
        Notification notification = Notification.create(type, userId, letterId, label);
        Subscriptions subscriptions = subscriptionPersistencePort.findByUserId(userId);
        NotificationResponse result = NotificationResponse.from(notificationPersistencePort.save(notification));
        if (subscriptions.isPushEnabled()) {
            pushMessage(type, subscriptions);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public UnreadNotificationResponse getUnreadNotificationCount(Long userId) {
        Notifications notifications = notificationPersistencePort.findByReceiver(userId);
        return new UnreadNotificationResponse(notifications.getUnreadCount());
    }

    @Transactional
    public List<NotificationResponse> getUserNotifications(Long userId) {
        Notifications notifications = notificationPersistencePort.findByReceiver(userId);
        notifications.orderByCreatedAt();
        List<NotificationResponse> result = NotificationResponse.from(notifications);
        notificationPersistencePort.updateNotifications(notifications.markAsRead());
        return result;
    }

    public void sendKeywordNotification(RecommendNotificationCommand request) {
        notificationPersistencePort.save(
                Notification.create(
                        NotificationType.NEW_LETTER, request.userId(), request.letterId(), request.label()
                )
        );

        Subscriptions allSubscriptions = subscriptionPersistencePort.findAll();

        if (allSubscriptions.isPushEnabled()) {
            pushMessage(NotificationType.NEW_LETTER, allSubscriptions);
        }
    }

    private void pushMessage(NotificationType type, Subscriptions subscriptions) {
        PushMessages pushMessages = subscriptions.makeMessages(type);
//        pushNotificationPort.pushAll(pushMessages);
    }
}
