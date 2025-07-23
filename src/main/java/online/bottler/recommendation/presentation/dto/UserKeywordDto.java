package online.bottler.recommendation.presentation.dto;

import java.util.List;
import online.bottler.recommendation.application.dto.UserKeywordInfo;

public class UserKeywordDto {
    public record UserKeywordResponse(List<String> keywords) {
        public static UserKeywordResponse from(UserKeywordInfo userKeywordInfo) {
            return new UserKeywordResponse(userKeywordInfo.keywords());
        }
    }
}
