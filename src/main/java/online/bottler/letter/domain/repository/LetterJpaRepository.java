package online.bottler.letter.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import online.bottler.letter.domain.LetterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import online.bottler.letter.domain.Letter;

public interface LetterJpaRepository extends JpaRepository<Letter, Long> {

    @Query(
            """
                SELECT l
                FROM Letter l
                LEFT JOIN FETCH l.keywords
                WHERE l.id = :id AND l.status = :status
            """
    )
    Optional<Letter> findByIdAndStatusWithKeywords(Long id, LetterStatus status);

    List<Letter> findAllByIdIn(Collection<Long> ids);

    @Query(
            """
                SELECT l
                FROM Letter l
                LEFT JOIN FETCH l.keywords
                WHERE  l.status = :status AND l.id IN (:ids)
            """
    )
    List<Letter> findAllByIdInAndStatusWithKeywords(Collection<Long> ids, LetterStatus status);

    List<Long> findIdsByUserIdAndStatus(Long userId, LetterStatus status);

    @Query(
            """
                SELECT MAX(l.id)
                FROM Letter l
                WHERE l.status = :status
            """
    )
    Optional<Long> findMaxIdByStatus(LetterStatus status);

    @Query(
            """
                SELECT l.id
                FROM Letter l
                WHERE l.status = :status AND l.id >= :randomId AND l.id NOT IN :excludedIds
                ORDER BY l.id
                LIMIT :count
            """
    )
    List<Long> findIdsByIdNotInAndStatus(int count, Long randomId, Collection<Long> excludedIds, LetterStatus status);

    boolean existsByIdAndStatus(Long id, LetterStatus status);

    Optional<Letter> findByIdAndStatus(Long id, LetterStatus status);

    @Query(
            """
                SELECT l.id
                FROM Letter l
                JOIN l.keywords lk
                WHERE l.status = :status AND l.userId = :userId
                GROUP BY lk.keyword
                ORDER BY COUNT(lk.keyword) DESC
                LIMIT 5
            """
    )
    List<String> findFrequentKeywords(Long userId);
}
