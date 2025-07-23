package online.bottler.letter.application.service;

import lombok.RequiredArgsConstructor;
import online.bottler.letter.application.dto.KeywordInfo;
import online.bottler.shared.ddd.ApplicationService;
import online.bottler.letter.domain.repository.KeywordJpaRepository;
import org.springframework.transaction.annotation.Transactional;

@ApplicationService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordService {
    private final KeywordJpaRepository keywordRepository;

    public KeywordInfo getKeywords() {
        return KeywordInfo.from(keywordRepository.findAll());
    }
}
