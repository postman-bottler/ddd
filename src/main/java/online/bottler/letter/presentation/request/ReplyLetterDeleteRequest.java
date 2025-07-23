package online.bottler.letter.presentation.request;

import jakarta.validation.constraints.NotBlank;
import online.bottler.letter.application.command.ReplyLetterDeleteCommand;

public record ReplyLetterDeleteRequest(@NotBlank(message = "Letter ID는 필수입니다.") Long letterId,
                                       @NotBlank(message = "Box Type은 필수입니다.") String boxType) {
    public ReplyLetterDeleteCommand toCommand() {
        return ReplyLetterDeleteCommand.of(letterId, boxType);
    }
}
