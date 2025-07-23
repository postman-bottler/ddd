package online.bottler.recommendation.application.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.recommendation.command.UserKeywordCommand;
import online.bottler.shared.ddd.ApplicationService;
import online.bottler.recommendation.domain.UserKeyword;
import online.bottler.recommendation.domain.repository.UserKeywordJpaRepository;
import org.springframework.transaction.annotation.Transactional;

@ApplicationService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserKeywordService {
    private final UserKeywordJpaRepository userKeywordJpaRepository;

    @Transactional
    public void create(Long userId, UserKeywordCommand command) {
        List<UserKeyword> userKeywords = UserKeyword.createList(userId, command.keywords());
        userKeywordJpaRepository.deleteAllByUserId(userId);
        userKeywordJpaRepository.saveAll(userKeywords);
    }

    public List<String> getKeywords(Long userId) {
        return userKeywordJpaRepository.findKeywordsByUserId(userId);
    }
}
