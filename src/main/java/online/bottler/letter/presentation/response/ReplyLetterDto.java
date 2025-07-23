package online.bottler.letter.presentation.response;

import java.time.LocalDateTime;
import online.bottler.letter.application.dto.ReplyLetterDetailInfo;
import online.bottler.letter.application.dto.ReplyLetterInfo;
import online.bottler.letter.application.dto.ReplyLetterSummaryInfo;

public class ReplyLetterDto {

    public record ReplyLetterResponse(
            Long replyLetterId,
            String content,
            String font,
            String paper,
            String label,
            LocalDateTime createdAt
    ) {
        public static ReplyLetterResponse from(ReplyLetterInfo replyLetterInfo) {
            return new ReplyLetterResponse(
                    replyLetterInfo.replyLetterId(),
                    replyLetterInfo.content(),
                    replyLetterInfo.font(),
                    replyLetterInfo.paper(),
                    replyLetterInfo.label(),
                    replyLetterInfo.createdAt()
            );
        }
    }

    public record ReplyLetterSummaryResponse(
            Long replyLetterId,
            String title, String label,
            LocalDateTime createdAt
    ) {
        public static ReplyLetterSummaryResponse from(ReplyLetterSummaryInfo replyLetterSummaryInfo) {
            return new ReplyLetterSummaryResponse(
                    replyLetterSummaryInfo.replyLetterId(),
                    replyLetterSummaryInfo.title(),
                    replyLetterSummaryInfo.label(),
                    replyLetterSummaryInfo.createdAt()
            );
        }
    }

    public record ReplyLetterDetailResponse(
            Long replyLetterId,
            String content,
            String font,
            String paper,
            String label,
            boolean isReplied,
            LocalDateTime createdAt
    ) {
        public static ReplyLetterDetailResponse from(ReplyLetterDetailInfo replyLetterDetailInfo) {
            return new ReplyLetterDetailResponse(
                    replyLetterDetailInfo.replyLetterId(),
                    replyLetterDetailInfo.content(),
                    replyLetterDetailInfo.font(),
                    replyLetterDetailInfo.paper(),
                    replyLetterDetailInfo.label(),
                    replyLetterDetailInfo.isReplied(),
                    replyLetterDetailInfo.createdAt()
            );
        }
    }

}
