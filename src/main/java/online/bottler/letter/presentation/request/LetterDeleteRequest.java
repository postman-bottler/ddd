package online.bottler.letter.presentation.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import online.bottler.letter.application.command.LetterDeleteCommand;

public record LetterDeleteRequest(@NotBlank(message = "Letter ID는 필수입니다.") Long letterId,
                                  @NotBlank(message = "Letter Type은 필수입니다.") String letterType,
                                  @NotBlank(message = "Box Type은 필수입니다.") String boxType) {

    public LetterDeleteCommand toCommand() {
        return LetterDeleteCommand.of(letterId, letterType, boxType);
    }

    public static List<LetterDeleteCommand> toCommandList(List<LetterDeleteRequest> letterDeleteRequests) {
        return letterDeleteRequests.stream().map(LetterDeleteRequest::toCommand).toList();
    }
}
