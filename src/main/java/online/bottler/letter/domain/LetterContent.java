package online.bottler.letter.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LetterContent {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "font", nullable = false)
    private String font;

    @Column(name = "paper", nullable = false)
    private String paper;

    @Column(name = "label", nullable = false)
    private String label;

    @Builder(access = AccessLevel.PRIVATE)
    private LetterContent(String title, String content, String font, String paper, String label) {
        this.title = title;
        this.content = content;
        this.font = font;
        this.paper = paper;
        this.label = label;
    }

    public static LetterContent compose(
            String title,
            String content,
            String font,
            String paper,
            String label
    ) {
        return LetterContent.builder()
                .title(title)
                .content(content)
                .font(font)
                .paper(paper)
                .label(label)
                .build();
    }
}
