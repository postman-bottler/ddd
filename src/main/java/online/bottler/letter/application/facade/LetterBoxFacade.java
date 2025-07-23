package online.bottler.letter.application.facade;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.letter.application.command.CommonPageCommand;
import online.bottler.letter.application.command.LetterDeleteCommand;
import online.bottler.letter.application.dto.LetterBoxSummaryInfo;
import online.bottler.letter.application.service.DeleteLetterService;
import online.bottler.letter.application.service.LetterBoxService;
import online.bottler.letter.domain.LetterSummary;
import online.bottler.shared.security.AuthenticationProvider;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LetterBoxFacade {

    private final LetterBoxService letterBoxService;
    private final DeleteLetterService deleteLetterService;
    private final AuthenticationProvider authenticationProvider;

    public Page<LetterBoxSummaryInfo> getLetterBoxSummaries(String boxType, CommonPageCommand command) {
        Long userId = authenticationProvider.getCurrentUserId();
        Page<LetterSummary> letterBoxSummaries = letterBoxService.getLetterBoxSummaries(userId, boxType, command);
        return letterBoxSummaries.map(LetterBoxSummaryInfo::from);
    }

    public void deleteLetters(List<LetterDeleteCommand> commandList) {
        Long userId = authenticationProvider.getCurrentUserId();
        deleteLetterService.deleteLetters(userId, commandList);
    }

    public void deleteAllLetters(String boxType) {
        Long userId = authenticationProvider.getCurrentUserId();
        deleteLetterService.deleteAllLetters(userId, boxType);
    }
}
