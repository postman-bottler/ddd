package online.bottler.letter.domain.exception;

import online.bottler.letter.application.exception.LetterCustomException;
import online.bottler.shared.response.code.ErrorStatus;

public class InvalidSortFieldException extends LetterCustomException {
    public InvalidSortFieldException() {
        super(ErrorStatus.INVALID_SORT_FIELD, "유효하지 않은 값입니다.");
    }
}