package online.bottler.letter.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import online.bottler.letter.domain.Letter;

public record LetterInfo(
        Long letterId,
        String title,
        String content,
        List<String> keywords,
        String font,
        String paper,
        String label,
        LocalDateTime createdAt
) {
    public static LetterInfo from(Letter letter) {
        return new LetterInfo(
                letter.getId(),
                letter.getTitle(),
                letter.getContent(),
                letter.getKeywords(),
                letter.getFont(),
                letter.getPaper(),
                letter.getLabel(),
                letter.getCreatedAt()
        );
    }
}
