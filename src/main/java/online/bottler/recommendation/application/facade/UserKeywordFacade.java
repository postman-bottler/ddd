package online.bottler.recommendation.application.facade;

import lombok.RequiredArgsConstructor;
import online.bottler.recommendation.command.UserKeywordCommand;
import online.bottler.recommendation.application.service.UserKeywordService;
import online.bottler.recommendation.application.dto.UserKeywordInfo;
import online.bottler.shared.security.AuthenticationProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserKeywordFacade {

    private final UserKeywordService userKeywordService;
    private final AuthenticationProvider authenticationProvider;

    public void create(UserKeywordCommand command) {
        Long userId = authenticationProvider.getCurrentUserId();
        userKeywordService.create(userId, command);
    }

    public UserKeywordInfo getKeywords() {
        Long userId = authenticationProvider.getCurrentUserId();
        return UserKeywordInfo.from(userKeywordService.getKeywords(userId));
    }
}
