package online.bottler.letter.application.exception;

import online.bottler.shared.response.code.ErrorStatus;

public class TempRecommendationsNotFoundException extends LetterCustomException {
    public TempRecommendationsNotFoundException() {
        super(ErrorStatus.TEMP_RECOMMENDATIONS_NOT_FOUND, "추천 데이터가 없습니다.");
    }
}
