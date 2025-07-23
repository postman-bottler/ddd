package online.bottler.recommendation.presentation;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import online.bottler.recommendation.command.UserKeywordCommand;

public record UserKeywordRequest(
        @NotBlank(message = "키워드는 최소 하나 이상이어야 합니다.") List<String> keywords
) {
    public UserKeywordCommand toCommand() {
        return UserKeywordCommand.of(keywords);
    }
}
