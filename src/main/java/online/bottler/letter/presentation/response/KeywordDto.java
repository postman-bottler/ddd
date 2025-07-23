package online.bottler.letter.presentation.response;

import java.util.List;
import online.bottler.letter.application.dto.KeywordInfo;
import online.bottler.letter.application.dto.KeywordInfo.CategoryKeywordsInfo;

public class KeywordDto {

    public record KeywordResponse(List<CategoryKeywordsResponse> categories) {
        public static KeywordResponse from(KeywordInfo keywordInfo) {
            return new KeywordResponse(
                    keywordInfo.categoryKeywordsInfos().stream()
                            .map(CategoryKeywordsResponse::from)
                            .toList()
            );
        }

        public record CategoryKeywordsResponse(String category, List<String> keywords) {
            public static CategoryKeywordsResponse from(CategoryKeywordsInfo categoryKeywordsInfo) {
                return new CategoryKeywordsResponse(
                        categoryKeywordsInfo.category(),
                        categoryKeywordsInfo.keywords()
                );
            }
        }
    }
}
