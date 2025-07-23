package online.bottler.letter.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import online.bottler.letter.domain.event.ReplyLetterBlockedEvent;
import online.bottler.shared.ddd.AggregateRoot;
import online.bottler.letter.domain.exception.LetterAuthorMismatchException;
import online.bottler.shared.event.DomainEventPublisher;

@Getter
@Entity
@AggregateRoot
@Table(
        name = "reply_letters",
        indexes = {
                @Index(name = "idx_replyletter_receiverId_letterId_isDeleted", columnList = "receiverId, letterId, status"),
                @Index(name = "idx_senderId_status", columnList = "senderId, status")
        },
        uniqueConstraints = @UniqueConstraint(name = "uq_letter_sender", columnNames = {"senderId", "letterId"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyLetter extends BaseLetter {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "letter_id", nullable = false)
    private Long letterId;

    @Builder
    private ReplyLetter(
            Long id,
            Long senderId, Long receiverId,
            Long letterId,
            LetterContent letterContent,
            LetterStatus status
    ) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.letterId = letterId;
        this.letterContent = letterContent;
        this.status = status;
    }

    public static ReplyLetter write(
            Long senderId, Long receiverId,
            Long letterId,
            String title, String content, String font, String paper, String label
    ) {
        return ReplyLetter.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .letterId(letterId)
                .letterContent(
                        LetterContent.compose(formatReplyTitle(title), content, font, paper, label)
                )
                .status(LetterStatus.OPEN)
                .build();
    }

    private static String formatReplyTitle(String title) {
        return "RE: [" + title + "]";
    }

    public void delete(Long requesterId) {
        validateOwner(requesterId);
        this.status = LetterStatus.DELETED;
    }

    public void block() {
        this.status = LetterStatus.BLOCKED;

        DomainEventPublisher.publish(new ReplyLetterBlockedEvent(this.getSenderId()));
    }

    private void validateOwner(Long requesterId) {
        if (!isOwner(requesterId)) {
            throw new LetterAuthorMismatchException();
        }
    }

    public boolean isOwner(Long requesterId) {
        return senderId.equals(requesterId);
    }
}
