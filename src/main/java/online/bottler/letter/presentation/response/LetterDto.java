package online.bottler.letter.presentation.response;

import java.time.LocalDateTime;
import java.util.List;
import online.bottler.letter.application.dto.FrequentKeywordsInfo;
import online.bottler.letter.application.dto.LetterDetailInfo;
import online.bottler.letter.application.dto.LetterInfo;
import online.bottler.letter.application.dto.LetterRecommendSummaryInfo;

public class LetterDto {
    public record LetterResponse(
            Long letterId,
            String title,
            String content,
            List<String> keywords,
            String font,
            String paper,
            String label,
            LocalDateTime createdAt
    ) {
        public static LetterResponse from(LetterInfo letterInfo) {
            return new LetterResponse(
                    letterInfo.letterId(),
                    letterInfo.title(),
                    letterInfo.content(),
                    letterInfo.keywords(),
                    letterInfo.font(),
                    letterInfo.paper(),
                    letterInfo.label(),
                    letterInfo.createdAt()
            );
        }
    }

    public record LetterDetailResponse(
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
        public static LetterDetailResponse from(LetterDetailInfo letterDetailInfo) {
            return new LetterDetailResponse(
                    letterDetailInfo.letterId(),
                    letterDetailInfo.title(),
                    letterDetailInfo.content(),
                    letterDetailInfo.keywords(),
                    letterDetailInfo.font(),
                    letterDetailInfo.paper(),
                    letterDetailInfo.profile(),
                    letterDetailInfo.label(),
                    letterDetailInfo.isOwner(),
                    letterDetailInfo.isReplied(),
                    letterDetailInfo.createdAt()
            );
        }
    }

    public record LetterRecommendSummaryResponse(
            Long letterId,
            String title,
            String label
    ) {
        public static LetterRecommendSummaryResponse from(LetterRecommendSummaryInfo letterRecommendSummaryInfo) {
            return new LetterRecommendSummaryResponse(
                    letterRecommendSummaryInfo.letterId(),
                    letterRecommendSummaryInfo.title(),
                    letterRecommendSummaryInfo.label()
            );
        }

        public static List<LetterRecommendSummaryResponse> fromList(List<LetterRecommendSummaryInfo> infos) {
            return infos.stream()
                    .map(LetterRecommendSummaryResponse::from)
                    .toList();
        }
    }

    public record FrequentKeywordsResponse(List<String> keywords) {
        public static FrequentKeywordsResponse from(FrequentKeywordsInfo info) {
            return new FrequentKeywordsResponse(info.keywords());
        }
    }
}
