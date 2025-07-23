package online.bottler.recommendation.application.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.letter.application.service.LetterBoxService;
import online.bottler.letter.application.service.LetterService;
import online.bottler.letter.application.service.RecommendLetterService;
import online.bottler.recommendation.application.service.RecommendService;
import online.bottler.recommendation.application.service.UserKeywordService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendFacade {

    @Value("${recommendation.limit.candidate}")
    private int recommendationCandidateLimit;

    private final RecommendService recommendService;
    private final UserKeywordService userKeywordService;
    private final RecommendLetterService recommendLetterService;
    private final LetterService letterService;
    private final LetterBoxService letterBoxService;

    public String generateRecommendation(Long userId) {
        List<Long> recommendedLetterIds = recommendService.findRecommendedLetters(userId);

        List<String> userKeywords = userKeywordService.getKeywords(userId);

        List<Long> recommendLetters = recommendLetterService.recommendLetters(userKeywords, recommendedLetterIds, recommendationCandidateLimit);

        return recommendService.generate(userId, recommendLetters);
    }

    public void updateRecommendationsFromTemp(Long userId) {
        List<Long> recommendations = recommendService.fetchActiveRecommendations(userId);

        Long recommendationId = letterService.getFirstValidLetterId(recommendations);

        recommendService.updateRecommendationsFromTemp(userId, recommendationId);

        letterBoxService.archiveLetters(userId, List.of(recommendationId));
    }
}
