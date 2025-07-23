package online.bottler.letter.application.facade;

import static online.bottler.letter.domain.LetterType.LETTER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.bottler.letter.application.command.LetterCommand;
import online.bottler.letter.application.command.DeleteLetterCommand;
import online.bottler.letter.application.command.RemoveLetterBoxCommand;
import online.bottler.letter.application.dto.FrequentKeywordsInfo;
import online.bottler.letter.application.dto.LetterDetailInfo;
import online.bottler.letter.application.dto.LetterRecommendSummaryInfo;
import online.bottler.letter.application.dto.LetterInfo;
import online.bottler.letter.application.service.LetterBoxService;
import online.bottler.letter.application.service.LetterService;
import online.bottler.letter.application.service.ReplyLetterService;
import online.bottler.letter.domain.Letter;
import online.bottler.letter.domain.LetterBoxType;
import online.bottler.recommendation.application.service.RecommendService;
import online.bottler.shared.security.AuthenticationProvider;
import online.bottler.user.application.UserService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LetterFacade {

    private final LetterService letterService;
    private final ReplyLetterService replyLetterService;
    private final LetterBoxService letterBoxService;
    private final RecommendService recommendService;
    private final UserService userService;
    private final AuthenticationProvider authenticationProvider;

    @Transactional
    public LetterInfo writeLetter(LetterCommand command) {
        Long userId = authenticationProvider.getCurrentUserId();

        Letter letter = letterService.writeLetter(userId, command);

        letterBoxService.archiveLetter(letter);

        return LetterInfo.from(letter);
    }

    @Transactional(readOnly = true)
    public LetterDetailInfo getLetterDetail(Long letterId) {
        Long userId = authenticationProvider.getCurrentUserId();

        Letter letter = letterService.getLetterWithKeywords(userId, letterId);

        String profile = userService.getProfileImageUrlById(letter.getUserId());

        boolean isOwner = letter.isOwner(userId);

        boolean isReplied = replyLetterService.isReplied(userId, letterId);

        return LetterDetailInfo.of(letter, profile, isOwner, isReplied);
    }

    @Transactional(readOnly = true)
    public List<LetterRecommendSummaryInfo> getRecommendedLetter() {
        Long userId = authenticationProvider.getCurrentUserId();

        List<Long> recommendedLetterIds = recommendService.getRecommendedLetterIds(userId);

        List<Letter> letters = letterService.getLettersIncludingAllStatus(recommendedLetterIds);

        return LetterRecommendSummaryInfo.fromList(letters);
    }

    @Transactional(readOnly = true)
    public FrequentKeywordsInfo getMostFrequentKeywords() {
        Long userId = authenticationProvider.getCurrentUserId();

        List<String> mostFrequentKeywords = letterService.getMostFrequentKeywords(userId);

        return FrequentKeywordsInfo.from(mostFrequentKeywords);
    }

    @Transactional
    public void delete(DeleteLetterCommand command) {
        Long userId = authenticationProvider.getCurrentUserId();

        letterService.removeLetter(userId, command.letterId());

        letterBoxService.removeLettersFromBox(
                RemoveLetterBoxCommand.byLetterId(command.letterId(), LetterBoxType.of(LETTER, null))
        );
    }
}
