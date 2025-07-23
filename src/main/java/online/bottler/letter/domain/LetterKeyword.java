package online.bottler.letter.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "letter_keyword",
        indexes = @Index(name = "idx_letterkeyword_keyword_status_letter", columnList = "keyword, status, letterId"),
        uniqueConstraints = @UniqueConstraint(name = "uq_letter_keyword", columnNames = {"letterId", "keyword"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LetterKeyword extends AbstractAuditing {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id", nullable = false)
    private Letter letter;

    @Column(name = "keyword", nullable = false)
    private String keyword;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LetterStatus status;

    @Builder
    private LetterKeyword(Long id, String keyword, LetterStatus status) {
        this.id = id;
        this.keyword = keyword;
        this.status = status;
    }

    private static LetterKeyword create(String keyword) {
        return LetterKeyword.builder()
                .keyword(keyword)
                .status(LetterStatus.OPEN)
                .build();
    }

    static List<LetterKeyword> createAll(List<String> keywords) {
        return keywords.stream()
                .map(LetterKeyword::create)
                .toList();
    }

    void changeLetter(Letter letter) {
        this.letter = letter;
    }

    void delete() {
        this.status = LetterStatus.DELETED;
    }

    public void block() {
        this.status = LetterStatus.BLOCKED;
    }
}
