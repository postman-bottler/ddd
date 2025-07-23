package online.bottler.recommendation.infra;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.letter.util.RedisLetterKeyUtil;
import online.bottler.recommendation.application.service.RecommendationStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RecommendationRedisRepository implements RecommendationStore {

    @Value("${recommendation.limit.active-recommendations}")
    private int maxRecommendations;

    private final RedisTemplate<String, List<Long>> redisTemplate;

    @Override
    public void saveTempRecommendations(Long userId, List<Long> recommendations) {
        redisTemplate.opsForValue().set(getTempRecommendationKey(userId), recommendations);
    }

    @Override
    public void updateActiveRecommendations(Long userId, Long letterId) {
        List<Long> activeRecommendations = fetchActiveRecommendations(userId);

        if (activeRecommendations.size() - maxRecommendations >= 0) {
            activeRecommendations.subList(0, activeRecommendations.size() - maxRecommendations).clear();
        }
        activeRecommendations.add(letterId);

        redisTemplate.opsForValue().set(getActiveRecommendationKey(userId), activeRecommendations);
        redisTemplate.delete(getTempRecommendationKey(userId));
    }

    @Override
    public List<Long> fetchActiveRecommendations(Long userId) {
        return fetchRecommendations(getActiveRecommendationKey(userId));
    }

    @Override
    public List<Long> fetchTempRecommendations(Long userId) {
        return fetchRecommendations(getTempRecommendationKey(userId));
    }

    private List<Long> fetchRecommendations(String key) {
        List<Long> recommendations = redisTemplate.opsForValue().get(key);
        if (isExistRecommendations(recommendations)) {
            return recommendations;
        } else {
            return new ArrayList<>();
        }
    }

    private boolean isExistRecommendations(List<Long> recommendations) {
        return !(recommendations == null || recommendations.isEmpty());
    }

    private String getTempRecommendationKey(Long userId) {
        return RedisLetterKeyUtil.getTempRecommendationKey(userId);
    }

    private String getActiveRecommendationKey(Long userId) {
        return RedisLetterKeyUtil.getActiveRecommendationKey(userId);
    }

    @Override
    public void saveDeveloperLetter(Long userId, List<Long> recommendations) {
        redisTemplate.opsForValue().set(getActiveRecommendationKey(userId), recommendations);
    }
}
