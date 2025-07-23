package online.bottler.letter.presentation.request;

import jakarta.validation.constraints.Min;
import online.bottler.letter.application.command.CommonPageCommand;

public record CommonPageRequest(@Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.") Integer page,
                                @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.") Integer size,
                                String sort) {
    public CommonPageCommand toCommand() {
        return new CommonPageCommand(page, size, sort);
    }
}
