package online.bottler.recommendation.command;

import java.util.List;

public record UserKeywordCommand(List<String> keywords) {
    public static UserKeywordCommand of(List<String> keywords) {
        return new UserKeywordCommand(keywords);
    }
}
