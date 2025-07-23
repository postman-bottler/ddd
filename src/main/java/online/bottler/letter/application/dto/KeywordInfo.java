package online.bottler.letter.application.dto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import online.bottler.letter.domain.Keyword;

public record KeywordInfo(List<CategoryKeywordsInfo> categoryKeywordsInfos) {
    public static KeywordInfo from(List<Keyword> keywordList) {
        Map<String, List<String>> groupedByCategory =
                keywordList.stream()
                        .collect(
                                Collectors.groupingBy(
                                        Keyword::getCategory,
                                        Collectors.mapping(Keyword::getKeyword, Collectors.toList())
                                )
                        );

        List<CategoryKeywordsInfo> categories = groupedByCategory.entrySet().stream()
                .map(entry -> new CategoryKeywordsInfo(entry.getKey(), entry.getValue()))
                .toList();

        return new KeywordInfo(categories);
    }

    public record CategoryKeywordsInfo(String category, List<String> keywords) {
    }
}
