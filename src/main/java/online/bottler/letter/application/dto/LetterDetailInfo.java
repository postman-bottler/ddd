package online.bottler.letter.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import online.bottler.letter.domain.Letter;

public record LetterDetailInfo(
        Long letterId,
        String title,
        String content,
        List<String> keywords,
        String font,
        String paper,
        String profile,
        String label,
        boolean isOwner,
        boolean isReplied,
        LocalDateTime createdAt
) {
    public static LetterDetailInfo of(
            Letter letter,
            String profile,
            boolean isOwner,
            boolean isReplied
    ) {
        return new LetterDetailInfo(
                letter.getId(),
                letter.getTitle(),
                letter.getContent(),
                letter.getKeywords(),
                letter.getFont(),
                letter.getPaper(),
                profile,
                letter.getLabel(),
                isOwner,
                isReplied,
                letter.getCreatedAt()
        );
    }
}
