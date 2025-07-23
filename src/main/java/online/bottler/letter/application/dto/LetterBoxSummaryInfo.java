package online.bottler.letter.application.dto;

import java.time.LocalDateTime;
import online.bottler.letter.domain.BoxType;
import online.bottler.letter.domain.LetterType;

public record LetterBoxSummaryInfo(
        Long letterId,
        String title, String label,
        LetterType letterType, BoxType boxType,
        LocalDateTime createdAt
) {
    public static LetterBoxSummaryInfo from(online.bottler.letter.domain.LetterSummary letterSummary) {
        return new LetterBoxSummaryInfo(
                letterSummary.letterId(),
                letterSummary.title(), letterSummary.label(),
                letterSummary.letterType(), letterSummary.boxType(),
                letterSummary.createdAt()
        );
    }
}
