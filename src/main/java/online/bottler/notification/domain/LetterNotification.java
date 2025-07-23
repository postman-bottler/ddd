package online.bottler.notification.domain;

import java.time.LocalDateTime;
import lombok.Getter;
import online.bottler.shared.exception.DomainException;

@Getter
public class LetterNotification extends Notification {
    private final long letterId;

    private final String labelUrl;

    protected LetterNotification(NotificationType type, long receiver, Long letterId, Boolean isRead, String labelUrl) {
        super(type, receiver, isRead);
        validateLetterId(letterId);
        this.letterId = letterId;
        this.labelUrl = labelUrl;
    }

    protected LetterNotification(Long id, NotificationType type, long receiver,
                                 Long letterId, LocalDateTime createdAt, Boolean isRead, String labelUrl) {
        super(id, type, receiver, createdAt, isRead);
        validateLetterId(letterId);
        this.letterId = letterId;
        this.labelUrl = labelUrl;
    }

    private void validateLetterId(Long letterId) {
        if (letterId == null) {
            throw new DomainException("편지 관련 알림은 편지 ID가 있어야 합니다.");
        }
    }
}
