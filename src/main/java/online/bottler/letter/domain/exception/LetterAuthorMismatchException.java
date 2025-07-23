package online.bottler.letter.domain.exception;

import online.bottler.letter.application.exception.LetterCustomException;
import online.bottler.shared.response.code.ErrorStatus;

public class LetterAuthorMismatchException extends LetterCustomException {
    public LetterAuthorMismatchException() {
        super(ErrorStatus.LETTER_AUTHOR_MISMATCH, "요청자와 작성자가 일치하지 않습니다.");
    }
}
