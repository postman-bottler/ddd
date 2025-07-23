package online.bottler.letter.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LetterBoxType {

    @Enumerated(EnumType.STRING)
    @Column(name = "letter_type", nullable = false)
    private LetterType letterType;

    @Enumerated(EnumType.STRING)
    @Column(name = "box_type", nullable = false)
    private BoxType boxType;

    @Builder
    private LetterBoxType(LetterType letterType, BoxType boxType) {
        this.letterType = letterType;
        this.boxType = boxType;
    }

    public static LetterBoxType of(LetterType letterType, BoxType boxType) {
        return LetterBoxType.builder().letterType(letterType).boxType(boxType).build();
    }

    public static LetterBoxType from(String letterType, String boxType) {
        return LetterBoxType.builder()
                .letterType(LetterType.valueOf(letterType))
                .boxType(BoxType.valueOf(boxType))
                .build();
    }

    public boolean isValid() {
        return letterType != null && boxType != null;
    }
}
