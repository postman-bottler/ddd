package online.bottler.letter.application.dto;

import java.time.LocalDateTime;
import online.bottler.letter.domain.ReplyLetter;

public record ReplyLetterDetailInfo(
        Long replyLetterId,
        String content,
        String font,
        String paper,
        String label,
        boolean isReplied,
        LocalDateTime createdAt
) {
    public static ReplyLetterDetailInfo from(ReplyLetter replyLetter, boolean isReplied) {
        return new ReplyLetterDetailInfo(
                replyLetter.getId(),
                replyLetter.getContent(),
                replyLetter.getFont(),
                replyLetter.getPaper(),
                replyLetter.getLabel(),
                isReplied,
                replyLetter.getCreatedAt()
        );
    }
}
