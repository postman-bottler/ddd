package online.bottler.letter.application.dto;

import java.time.LocalDateTime;
import online.bottler.letter.domain.ReplyLetter;

public record ReplyLetterSummaryInfo(
        Long replyLetterId,
        String title, String label,
        LocalDateTime createdAt
) {
    public static ReplyLetterSummaryInfo from(ReplyLetter replyLetter) {
        return new ReplyLetterSummaryInfo(
                replyLetter.getId(),
                replyLetter.getTitle(), replyLetter.getLabel(),
                replyLetter.getCreatedAt()
        );
    }
}
