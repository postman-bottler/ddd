package online.bottler.letter.application.strategy;

import static online.bottler.letter.domain.BoxType.RECEIVE;
import static online.bottler.letter.domain.LetterType.LETTER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.letter.application.command.RemoveLetterBoxCommand;
import online.bottler.letter.application.service.LetterBoxService;
import online.bottler.letter.domain.LetterBoxType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReplyLetterDeleteStrategyReceive implements LetterDeleteStrategy {
    private final LetterBoxService letterBoxService;

    @Override
    public void deleteLetters(Long userId, List<Long> letterIds) {
        letterBoxService.removeLettersFromBox(
                RemoveLetterBoxCommand.byUserAndLetterIds(userId, letterIds, LetterBoxType.of(LETTER, RECEIVE))
        );
    }
}
