package online.bottler.letter.application.service;

import static online.bottler.letter.domain.BoxType.RECEIVE;
import static online.bottler.letter.domain.BoxType.SEND;
import static online.bottler.letter.domain.LetterType.LETTER;
import static online.bottler.letter.domain.LetterType.REPLY_LETTER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.ddd.ApplicationService;
import online.bottler.letter.application.command.CommonPageCommand;
import online.bottler.letter.application.command.RemoveLetterBoxCommand;
import online.bottler.letter.domain.BoxType;
import online.bottler.letter.domain.Letter;
import online.bottler.letter.domain.LetterBox;
import online.bottler.letter.domain.LetterBoxType;
import online.bottler.letter.domain.LetterSummary;
import online.bottler.letter.domain.ReplyLetter;
import online.bottler.letter.domain.model.LetterSummaryProjection;
import online.bottler.letter.domain.repository.LetterBoxJpaRepository;
import online.bottler.letter.domain.repository.LetterBoxQueryRepository;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@ApplicationService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LetterBoxService implements ArchiveLetterBoxService{
    private final LetterBoxJpaRepository letterBoxRepository;
    private final LetterBoxQueryRepository letterBoxQueryRepository;

    @Transactional
    public void archiveLetter(Letter letter) {
        archiveLetterToBox(letter.getId(), letter.getUserId(), LetterBoxType.of(LETTER, SEND));
    }

    @Transactional
    public void archiveLetter(ReplyLetter replyLetter) {
        archiveLetterToBox(replyLetter.getSenderId(), replyLetter.getLetterId(), LetterBoxType.of(REPLY_LETTER, SEND));
        archiveLetterToBox(replyLetter.getReceiverId(), replyLetter.getLetterId(), LetterBoxType.of(REPLY_LETTER, RECEIVE));
    }

    @Override
    @Transactional
    public void archiveLetters(Long userId, List<Long> letterIds) {
        letterIds.forEach(letterId -> archiveLetterToBox(userId, letterId, LetterBoxType.of(LETTER, RECEIVE)));
    }

    private void archiveLetterToBox(Long userId, Long letterId, LetterBoxType letterBoxType) {
        LetterBox letterBox = LetterBox.archive(userId, letterId, letterBoxType);
        letterBoxRepository.save(letterBox);
    }

    public Page<LetterSummary> getLetterBoxSummaries(Long userId, String boxType, CommonPageCommand commonPageCommand) {
        return letterBoxQueryRepository.fetchLetterSummariesByUserIdAndBoxType(userId, BoxType.valueOf(boxType), commonPageCommand.toPageable()).map(
                LetterSummaryProjection::toDomain);
    }

    @Transactional
    public void removeLettersFromBox(RemoveLetterBoxCommand command) {
        deleteLettersFromBox(command.userId(), command.letterIds(), command.letterBoxType());
    }

    private void deleteLettersFromBox(Long userId, List<Long> letterIds, LetterBoxType letterBoxType) {
        letterBoxQueryRepository.deleteLetters(userId, letterIds, letterBoxType.getLetterType(), letterBoxType.getBoxType());
    }
}
