package online.bottler.letter.application.dto;

import java.util.List;
import online.bottler.letter.domain.Letter;

public record LetterRecommendSummaryInfo(Long letterId, String title, String label) {
    public static LetterRecommendSummaryInfo from(Letter letter) {
        return new LetterRecommendSummaryInfo(letter.getId(), letter.getTitle(), letter.getLabel());
    }

    public static List<LetterRecommendSummaryInfo> fromList(List<Letter> letters) {
        return letters.stream().map(LetterRecommendSummaryInfo::from).toList();
    }
}
