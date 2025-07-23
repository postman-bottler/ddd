package online.bottler.recommendation.application.service;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.bottler.shared.ddd.ApplicationService;
import online.bottler.recommendation.domain.RecommendedLetter;
import online.bottler.recommendation.domain.repository.RecommendedLetterJpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@ApplicationService
@RequiredArgsConstructor
public class RecommendService {

    private final RecommendationStore recommendationStore;
    private final RecommendedLetterJpaRepository recommendedLetterRepository;

    public void saveDeveloperLetter(Long userId, List<Long> recommendations) {
        recommendationStore.saveDeveloperLetter(userId, recommendations);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generate(Long userId, List<Long> recommendations) {
        try {
            recommendationStore.saveTempRecommendations(userId, recommendations);
            return "Success: 사용자 [" + userId + "] 작업 완료";
        } catch (Exception e) {
            return "Error: 사용자 [" + userId + "] 예외 발생";
        }
    }

    public List<Long> findRecommendedLetters(Long userId) {
        return recommendedLetterRepository.findIdsByUserId(userId);
    }

    public List<Long> getRecommendedLetterIds(Long userId) {
        List<Long> letterIds = recommendationStore.fetchActiveRecommendations(userId);

        return letterIds == null ? Collections.emptyList() : letterIds;
    }

    public List<Long> fetchActiveRecommendations(Long userId) {
        return recommendationStore.fetchActiveRecommendations(userId);
    }

    public List<Long> fetchTempRecommendations(Long userId) {
        return recommendationStore.fetchTempRecommendations(userId);
    }

    @Transactional
    public void updateRecommendationsFromTemp(Long userId, Long recommendationId) {
        if (recommendationId == null) {
            log.info("requesterId={}에 대한 유효한 추천이 없음. 추천을 건너뜁니다.", userId);
        }

        recommendationStore.updateActiveRecommendations(userId, recommendationId);

        RecommendedLetter recommendedLetter = RecommendedLetter.record(userId, recommendationId);
        recommendedLetterRepository.save(recommendedLetter);
    }
}
