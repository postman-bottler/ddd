package online.bottler.letter.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import online.bottler.letter.domain.event.LetterBlockedEvent;
import online.bottler.shared.ddd.AggregateRoot;
import online.bottler.letter.domain.exception.LetterAuthorMismatchException;
import online.bottler.shared.event.DomainEventPublisher;

@Getter
@Entity
@AggregateRoot
@Table(name = "letters",
        indexes = @Index(name = "idx_letter_status_id", columnList = ("status, id")))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Letter extends BaseLetter {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(
            mappedBy = "letter",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<LetterKeyword> keywords = new ArrayList<>();

    @Builder(access = AccessLevel.PRIVATE)
    private Letter(
            Long id,
            Long userId,
            LetterContent letterContent,
            LetterStatus status
    ) {
        this.id = id;
        this.userId = userId;
        this.letterContent = letterContent;
        this.status = status;
    }

    public static Letter write(
            Long userId,
            String title, String content, String font, String paper, String label,
            List<String> keywords
    ) {

        return Letter.builder()
                .userId(userId)
                .letterContent(
                        LetterContent.compose(validateTitle(title), content, font, paper, label)
                )
                .status(LetterStatus.OPEN)
                .build()
                .addKeywords(keywords);
    }

    public List<String> getKeywords() {
        return keywords.stream().map(LetterKeyword::getKeyword).toList();
    }

    public boolean isOwner(Long requesterId) {
        return this.userId.equals(requesterId);
    }

    public void delete(Long requesterId) {
        if (!isOwner(requesterId)) {
            throw new LetterAuthorMismatchException();
        }
        this.status = LetterStatus.DELETED;
        this.keywords.forEach(LetterKeyword::delete);
    }

    public void block() {
        this.status = LetterStatus.BLOCKED;
        this.keywords.forEach(LetterKeyword::block);

        DomainEventPublisher.publish(new LetterBlockedEvent(this.userId));
    }

    private static String validateTitle(String title) {
        return (title == null || title.trim().isEmpty()) ? "무제" : title;
    }

    private Letter addKeywords(List<String> keywords) {
        List<LetterKeyword> letterKeywords = LetterKeyword.createAll(keywords);
        this.keywords.addAll(letterKeywords);
        letterKeywords.forEach(l -> l.changeLetter(this));
        return this;
    }
}
