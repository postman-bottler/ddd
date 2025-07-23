package online.bottler.letter.presentation.request;

import jakarta.validation.constraints.NotBlank;
import online.bottler.letter.application.command.ReplyLetterCommand;

public record ReplyLetterRequest(@NotBlank(message = "편지 내용은 필수입니다.") String content,
                                 @NotBlank(message = "글씨체는 필수입니다.") String font,
                                 @NotBlank(message = "편지지는 필수입니다.") String paper,
                                 @NotBlank(message = "라벨은 필수입니다.") String label) {
    public ReplyLetterCommand toCommand(Long letterId) {
        return ReplyLetterCommand.of(letterId, content, font, paper, label);
    }
}
