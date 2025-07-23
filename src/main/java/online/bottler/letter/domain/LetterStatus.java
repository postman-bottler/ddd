package online.bottler.letter.domain;

public enum LetterStatus {
    OPEN, BLOCKED, DELETED;

    public boolean isOpen() {
        return this == OPEN;
    }
    public boolean isBlocked() {
        return this == BLOCKED;
    }
    public boolean isDeleted() {
        return this == DELETED;
    }
}
