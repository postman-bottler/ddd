package online.bottler.letter.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.bottler.shared.ddd.ApplicationService;
import online.bottler.letter.domain.LetterStatus;
import online.bottler.letter.domain.repository.LetterJpaRepository;
import online.bottler.letter.domain.repository.LetterKeywordQueryDslRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@ApplicationService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendLetterService {

    private final LetterKeywordQueryDslRepository letterKeywordRepository;
    private final LetterJpaRepository letterJpaRepository;

    public List<Long> recommendLetters(List<String> userKeywords, List<Long> letterIds, int count) {
        List<Long> recommendedLetters = letterKeywordRepository.getMatchedLetters(userKeywords, letterIds, count);

        if (recommendedLetters.size() < count) {
            List<Long> randomLetterIds = getRandomLetterIds(letterIds, count - recommendedLetters.size());
            recommendedLetters.addAll(randomLetterIds);
        }

        if (recommendedLetters.isEmpty()) {
            log.warn("추천할 편지가 없음: userKeywords={}", userKeywords);
        }

        return recommendedLetters;
    }

    private List<Long> getRandomLetterIds(List<Long> excludedLetterIds, int remaining) {
        Optional<Long> maxId = letterJpaRepository.findMaxIdByStatus(LetterStatus.OPEN);

        if (maxId.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> result = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int tryCount = 0;

        while (result.size() < remaining && tryCount < 5) {
            long randomId = random.nextLong(1L, maxId.get() + 1);
            result.addAll(letterJpaRepository.findIdsByIdNotInAndStatus(remaining, randomId, excludedLetterIds, LetterStatus.OPEN));
            tryCount++;
        }

        return result;
    }
}
