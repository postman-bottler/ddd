package online.bottler.letter.application.dto;

import java.time.LocalDateTime;
import online.bottler.letter.domain.ReplyLetter;

public record ReplyLetterInfo(
        Long replyLetterId,
        String content,
        String font,
        String paper,
        String label,
        LocalDateTime createdAt
) {
    public static ReplyLetterInfo from(ReplyLetter replyLetter) {
        return new ReplyLetterInfo(
                replyLetter.getId(),
                replyLetter.getContent(),
                replyLetter.getFont(),
                replyLetter.getPaper(),
                replyLetter.getLabel(),
                replyLetter.getCreatedAt()
        );
    }
}
