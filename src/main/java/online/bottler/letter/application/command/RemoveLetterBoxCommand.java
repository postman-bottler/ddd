package online.bottler.letter.application.command;

import java.util.List;
import online.bottler.letter.domain.LetterBoxType;

public record RemoveLetterBoxCommand(
        Long userId,
        List<Long> letterIds,
        LetterBoxType letterBoxType
) {

    public static RemoveLetterBoxCommand byUser(Long userId, LetterBoxType letterBoxType) {
        return new RemoveLetterBoxCommand(userId, null, letterBoxType);
    }

    public static RemoveLetterBoxCommand byLetterId(Long letterId, LetterBoxType letterBoxType) {
        return new RemoveLetterBoxCommand(null, List.of(letterId), letterBoxType);
    }

    public static RemoveLetterBoxCommand byLetterIds(List<Long> letterIds, LetterBoxType letterBoxType) {
        return new RemoveLetterBoxCommand(null, letterIds, letterBoxType);
    }

    public static RemoveLetterBoxCommand byUserAndLetterIds(Long userId, List<Long> letterIds, LetterBoxType letterBoxType) {
        return new RemoveLetterBoxCommand(userId, letterIds, letterBoxType);
    }
}
