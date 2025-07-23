package online.bottler.recommendation.application.service;

import java.util.List;

public interface RecommendationStore {
    void saveTempRecommendations(Long userId, List<Long> recommendedLetters);

    void saveDeveloperLetter(Long userId, List<Long> recommendations);

    List<Long> fetchTempRecommendations(Long userId);

    List<Long> fetchActiveRecommendations(Long userId);

    void updateActiveRecommendations(Long userId, Long letterId);
}
