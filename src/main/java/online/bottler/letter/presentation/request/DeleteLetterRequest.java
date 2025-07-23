package online.bottler.letter.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import online.bottler.letter.application.command.DeleteLetterCommand;

public record DeleteLetterRequest(@NotNull(message = "Letter ID는 필수입니다.") Long letterId,
                                  @NotBlank(message = "Box Type은 필수입니다.") String boxType) {
    public DeleteLetterCommand toCommand() {
        return DeleteLetterCommand.of(letterId, boxType);
    }
}
