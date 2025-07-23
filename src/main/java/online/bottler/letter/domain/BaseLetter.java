package online.bottler.letter.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
abstract class BaseLetter extends AbstractAuditing {

    @Embedded
    protected LetterContent letterContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    protected LetterStatus status;

    public boolean isOpen() {
        return status.isOpen();
    }

    public boolean isDeleted() {
        return status.isDeleted();
    }

    public boolean isBlocked() {
        return status.isBlocked();
    }

    public String getTitle() {
        return letterContent.getTitle();
    }

    public String getContent() {
        return letterContent.getContent();
    }

    public String getFont() {
        return letterContent.getFont();
    }

    public String getPaper() {
        return letterContent.getPaper();
    }

    public String getLabel() {
        return letterContent.getLabel();
    }
}
