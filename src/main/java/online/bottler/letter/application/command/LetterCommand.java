package online.bottler.letter.application.command;

import java.util.List;

public record LetterCommand(
        String title, String content, String font, String paper, String label,
        List<String> keywords
) {
    public static LetterCommand of(String title, String content, String font, String paper, String label, List<String> keywords) {
        return new LetterCommand(title, content, font, paper, label, keywords);
    }
}
