package online.bottler.letter.application.command;

import online.bottler.letter.domain.BoxType;

public record DeleteLetterCommand(Long letterId, BoxType boxType) {
    public static DeleteLetterCommand of(Long letterId, String boxType) {
        return new DeleteLetterCommand(letterId, BoxType.valueOf(boxType));
    }
}
