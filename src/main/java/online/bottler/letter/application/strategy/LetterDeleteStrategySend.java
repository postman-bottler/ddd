package online.bottler.letter.application.strategy;

import static online.bottler.letter.domain.LetterType.LETTER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.letter.application.command.RemoveLetterBoxCommand;
import online.bottler.letter.application.service.LetterBoxService;
import online.bottler.letter.application.service.LetterService;
import online.bottler.letter.domain.LetterBoxType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LetterDeleteStrategySend implements LetterDeleteStrategy {

    private final LetterBoxService letterBoxService;
    private final LetterService letterService;

    @Override
    public void deleteLetters(Long userId, List<Long> letterIds) {
        letterService.removeLetters(userId, letterIds);
        letterBoxService.removeLettersFromBox(
                RemoveLetterBoxCommand.byLetterIds(letterIds, LetterBoxType.of(LETTER, null))
        );
    }
}
