package online.bottler.recommendation.application.dto;

import java.util.List;

public record UserKeywordInfo(List<String> keywords) {
    public static UserKeywordInfo from(List<String> keywords) {
        return new UserKeywordInfo(keywords);
    }
}
