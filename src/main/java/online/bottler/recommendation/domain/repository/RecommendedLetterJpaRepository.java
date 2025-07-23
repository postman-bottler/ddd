package online.bottler.recommendation.domain.repository;

import java.util.List;
import online.bottler.recommendation.domain.RecommendedLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecommendedLetterJpaRepository extends JpaRepository<RecommendedLetter, Long> {
    @Query(
            """
                SELECT rl.letterId
                FROM RecommendedLetter rl
                WHERE rl.userId = :userId
            """
    )
    List<Long> findIdsByUserId(Long userId);
}
