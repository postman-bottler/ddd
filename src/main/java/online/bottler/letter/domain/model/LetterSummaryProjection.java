package online.bottler.letter.domain.model;

import java.time.LocalDateTime;
import online.bottler.letter.domain.BoxType;
import online.bottler.letter.domain.LetterSummary;
import online.bottler.letter.domain.LetterType;

public record LetterSummaryProjection(Long letterId, String title, String label, LetterType letterType, BoxType boxType,
                                      LocalDateTime createdAt) {
    public LetterSummary toDomain() {
        return LetterSummary.of(letterId, title, label, letterType, boxType, createdAt);
    }
}
