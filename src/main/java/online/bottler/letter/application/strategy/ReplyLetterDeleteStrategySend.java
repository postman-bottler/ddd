package online.bottler.letter.application.strategy;

import static online.bottler.letter.domain.LetterType.REPLY_LETTER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.letter.application.command.RemoveLetterBoxCommand;
import online.bottler.letter.application.service.RecentReplyForLetterService;
import online.bottler.letter.application.service.LetterBoxService;
import online.bottler.letter.application.service.ReplyLetterService;
import online.bottler.letter.domain.LetterBoxType;
import online.bottler.letter.domain.ReplyLetter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReplyLetterDeleteStrategySend implements LetterDeleteStrategy {

    private final RecentReplyForLetterService recentReplyForLetterService;
    private final LetterBoxService letterBoxService;
    private final ReplyLetterService replyLetterService;

    @Override
    public void deleteLetters(Long userId, List<Long> letterIds) {
        replyLetterService.removeReplyLetters(userId, letterIds);

        List<ReplyLetter> replyLetters = replyLetterService.getReplyLetters(letterIds);

        replyLetters.forEach(replyLetter -> recentReplyForLetterService.delete(replyLetter.getReceiverId(),
                replyLetter.getId(), replyLetter.getLabel()));

        letterBoxService.removeLettersFromBox(
                RemoveLetterBoxCommand.byLetterIds(letterIds, LetterBoxType.of(REPLY_LETTER, null))
        );
    }
}
