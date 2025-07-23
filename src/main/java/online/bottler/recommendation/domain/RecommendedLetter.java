package online.bottler.recommendation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import online.bottler.recommendation.domain.event.RecommendedLetterCreatedEvent;
import online.bottler.shared.event.DomainEventPublisher;

@Entity
@Table(name = "recommended_letter",
        uniqueConstraints = @UniqueConstraint(name = "uq_user_letter", columnNames = {"requesterId", "letterId"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendedLetter extends AbstractAuditing {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "letter_id", nullable = false)
    private Long letterId;

    @Builder
    private RecommendedLetter(Long id, Long userId, Long letterId) {
        this.id = id;
        this.userId = userId;
        this.letterId = letterId;

        DomainEventPublisher.publish(new RecommendedLetterCreatedEvent(userId, letterId));
    }

    public static RecommendedLetter record(Long userId, Long letterId) {
        return RecommendedLetter.builder()
                .userId(userId)
                .letterId(letterId)
                .build();
    }
}
