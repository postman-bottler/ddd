package online.bottler.letter.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import online.bottler.letter.domain.Keyword;

public interface KeywordJpaRepository extends JpaRepository<Keyword, Long> {
}
