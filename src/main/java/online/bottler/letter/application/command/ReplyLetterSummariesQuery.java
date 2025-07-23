package online.bottler.letter.application.command;

public record ReplyLetterSummariesQuery(Long letterId, CommonPageCommand commonPageCommand) {
    public static ReplyLetterSummariesQuery of(Long letterId, CommonPageCommand commonPageCommand) {
        return new ReplyLetterSummariesQuery(letterId, commonPageCommand);
    }
}
