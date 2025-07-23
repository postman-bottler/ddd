package online.bottler.recommendation.domain.repository;

import java.util.List;
import online.bottler.recommendation.domain.UserKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserKeywordJpaRepository extends JpaRepository<UserKeyword, Long> {
    @Query(
            """
                SELECT uk.keyword
                FROM UserKeyword uk
                WHERE uk.userId = :userId
            """
    )
    List<String> findKeywordsByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
