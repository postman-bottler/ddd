package online.bottler.letter.application.exception;

import lombok.Getter;
import online.bottler.shared.response.code.ErrorStatus;

@Getter
public class LetterCustomException extends RuntimeException {
    private final ErrorStatus errorStatus;

    public LetterCustomException(ErrorStatus errorStatus, String message) {
        super(message);
        this.errorStatus = errorStatus;
    }
}
