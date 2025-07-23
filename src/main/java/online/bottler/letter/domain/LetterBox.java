package online.bottler.letter.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import online.bottler.shared.ddd.AggregateRoot;

@Entity
@AggregateRoot
@Table(name = "letter_box",
        indexes = @Index(name = "idx_letterbox_user_box_createdat", columnList = "requesterId, boxType, createdAt DESC"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LetterBox extends AbstractAuditing {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "letter_id", nullable = false)
    private Long letterId;

    @Embedded
    private LetterBoxType letterBoxType;

    @Builder
    private LetterBox(
            Long id,
            Long userId,
            Long letterId,
            LetterBoxType letterBoxType
    ) {
        this.id = id;
        this.userId = userId;
        this.letterId = letterId;
        this.letterBoxType = letterBoxType;
    }

    public static LetterBox archive(Long userId, Long letterId, LetterBoxType letterBoxType) {
        return LetterBox.builder()
                .userId(userId).
                letterId(letterId).
                letterBoxType(letterBoxType)
                .build();
    }
}
