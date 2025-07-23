package online.bottler.letter.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import online.bottler.letter.domain.LetterBox;

public interface LetterBoxJpaRepository extends JpaRepository<LetterBox, Long> {
    boolean existsByUserIdAndLetterId(Long userId, Long letterId);
}
