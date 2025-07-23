package online.bottler.recommendation.domain.event;

public record RecommendedLetterCreatedEvent(Long userId, Long letterId) {
}
