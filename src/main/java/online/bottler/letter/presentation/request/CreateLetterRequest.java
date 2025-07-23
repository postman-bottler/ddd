package online.bottler.letter.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import online.bottler.letter.application.command.LetterCommand;

public record CreateLetterRequest(@NotNull(message = "편지 제목은 Null이 될 수 없습니다.") String title,
                                  @NotBlank(message = "편지 내용은 필수입니다.") String content,
                                  @NotNull(message = "키워드는 Null이 될 수 없습니다.") List<String> keywords,
                                  @NotBlank(message = "글씨체는 필수입니다.") String font,
                                  @NotBlank(message = "편지지는 필수입니다.") String paper,
                                  @NotBlank(message = "라벨은 필수입니다.") String label) {
    public LetterCommand toCommand() {
        return LetterCommand.of(title, content, font, paper, label, keywords);
    }
}
