package online.bottler.letter.application.command;

import online.bottler.letter.domain.BoxType;

public record ReplyLetterDeleteCommand(Long id, BoxType boxType) {
    public static ReplyLetterDeleteCommand of(Long id, String boxType) {
        return new ReplyLetterDeleteCommand(id, BoxType.valueOf(boxType));
    }
}
