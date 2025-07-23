package online.bottler.letter.application.dto;

import java.util.List;

public record FrequentKeywordsInfo(List<String> keywords) {
    public static FrequentKeywordsInfo from(List<String> keywords) {
        return new FrequentKeywordsInfo(keywords);
    }
}
