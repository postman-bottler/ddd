package online.bottler.letter.presentation.response;

import java.time.LocalDateTime;
import online.bottler.letter.application.dto.LetterBoxSummaryInfo;
import online.bottler.letter.domain.BoxType;
import online.bottler.letter.domain.LetterType;

public class LetterBoxDto {
    public record LetterBoxSummaryResponse(
            Long letterId,
            String title, String label,
            LetterType letterType, BoxType boxType,
            LocalDateTime createdAt
    ) {
        public static LetterBoxSummaryResponse from(LetterBoxSummaryInfo letterBoxSummaryInfo) {
            return new LetterBoxSummaryResponse(
                    letterBoxSummaryInfo.letterId(),
                    letterBoxSummaryInfo.title(), letterBoxSummaryInfo.label(),
                    letterBoxSummaryInfo.letterType(), letterBoxSummaryInfo.boxType(),
                    letterBoxSummaryInfo.createdAt()
            );
        }
    }

}
