package online.bottler.letter.domain.event;

public record ReplyLetterCreatedEvent(Long userId, Long letterId) {
}
