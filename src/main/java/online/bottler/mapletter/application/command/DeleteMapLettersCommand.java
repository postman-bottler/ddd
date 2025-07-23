package online.bottler.mapletter.application.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;
import online.bottler.shared.exception.ApplicationException;

public record DeleteMapLettersCommand(
        List<LetterInfo> letters
) {
    public enum LetterType {
        REPLY, MAP;

        @JsonCreator
        public static LetterType from(String value) {
            for (LetterType type : LetterType.values()) {
                if (type.name().equals(value)) {
                    return type;
                }
            }
            throw new ApplicationException("잘못된 지도 편지 타입입니다.");
        }
    }

    public record LetterInfo(
            LetterType letterType,
            Long letterId
    ) {
    }
}
