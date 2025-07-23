package online.bottler.recommendation.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.ddd.ApplicationService;
import online.bottler.recommendation.domain.repository.RecommendedLetterJpaRepository;
import org.springframework.transaction.annotation.Transactional;

@ApplicationService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendedLetterService {
    private final RecommendedLetterJpaRepository recommendedLetterRepository;

    @Transactional(readOnly = true)
    public List<Long> getRecommendedLetterIds(Long userId) {
        return recommendedLetterRepository.findIdsByUserId(userId);
    }
}
