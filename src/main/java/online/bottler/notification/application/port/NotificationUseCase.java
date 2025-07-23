package online.bottler.notification.application.port;

import online.bottler.notification.application.request.RecommendNotificationCommand;
import online.bottler.notification.application.response.NotificationResponse;
import online.bottler.notification.application.response.UnreadNotificationResponse;
import online.bottler.notification.domain.NotificationType;

import java.util.List;

public interface NotificationUseCase {

    void sendBanNotification(Long userId);

    void sendWarningNotification(Long userId);

    void sendLetterNotification(NotificationType type, Long userId, Long letterId, String label);

    NotificationResponse sendNotification(NotificationType type, Long userId, Long letterId, String label);

    UnreadNotificationResponse getUnreadNotificationCount(Long userId);

    List<NotificationResponse> getUserNotifications(Long userId);

    void sendKeywordNotification(RecommendNotificationCommand request);
}
