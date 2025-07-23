package online.bottler.letter.application.command;

public record ReplyLetterCommand(
        Long letterId,
        String content, String font, String paper, String label
) {
    public static ReplyLetterCommand of(
            Long letterId,
            String content, String font, String paper, String label
    ) {
        return new ReplyLetterCommand(
                letterId,
                content, font, paper, label
        );
    }
}
