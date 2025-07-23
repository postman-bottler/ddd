package online.bottler.letter.application.service;

import static online.bottler.letter.domain.LetterStatus.OPEN;
import static online.bottler.letter.domain.LetterType.REPLY_LETTER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.ddd.ApplicationService;
import online.bottler.letter.application.command.ReplyLetterCommand;
import online.bottler.letter.application.command.ReplyLetterDeleteCommand;
import online.bottler.letter.application.command.ReplyLetterSummariesQuery;
import online.bottler.letter.application.exception.DuplicateReplyLetterException;
import online.bottler.letter.application.exception.LetterNotFoundException;
import online.bottler.letter.application.exception.UnauthorizedLetterAccessException;
import online.bottler.letter.domain.Letter;
import online.bottler.letter.domain.ReplyLetter;
import online.bottler.letter.domain.repository.LetterBoxJpaRepository;
import online.bottler.letter.domain.repository.LetterJpaRepository;
import online.bottler.letter.domain.repository.ReplyLetterJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

@ApplicationService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyLetterService implements BlockReplyLetterService{

    private final ReplyLetterJpaRepository replyLetterRepository;
    private final LetterJpaRepository letterRepository;
    private final LetterBoxJpaRepository letterBoxRepository;

    @Transactional
    public ReplyLetter write(Long userId, ReplyLetterCommand command) {
        if (replyLetterRepository.existsBySenderIdAndLetterId(userId, command.letterId())) {
            throw new DuplicateReplyLetterException();
        }

        Letter letter = letterRepository.findByIdAndStatus(command.letterId(), OPEN)
                .orElseThrow(LetterNotFoundException::new);

        return replyLetterRepository.save(
                ReplyLetter.write(
                        userId, letter.getUserId(),
                        command.letterId(),
                        letter.getTitle(), command.content(), command.font(), command.paper(), command.label()
                )
        );
    }

    @Transactional(readOnly = true)
    public ReplyLetter getReplyLetter(Long userId, Long id) {
        if (!letterBoxRepository.existsByUserIdAndLetterId(userId, id)) {
            throw new UnauthorizedLetterAccessException();
        }

        return loadReplyLetter(id);
    }

    @Transactional(readOnly = true)
    public List<ReplyLetter> getReplyLetters(List<Long> ids) {
        return replyLetterRepository.findAllByIdInAndStatus(ids, OPEN);
    }

    @Transactional(readOnly = true)
    public List<Long> getReplyLetterIds(Long userId) {
        return replyLetterRepository.findIdsBySenderIdAndStatus(userId, OPEN);
    }

    @Transactional(readOnly = true)
    public Page<ReplyLetter> getPagedReplyLetters(Long userId, ReplyLetterSummariesQuery query) {
        if (!letterBoxRepository.existsByUserIdAndLetterId(userId, query.letterId())) {
            throw new UnauthorizedLetterAccessException();
        }

        return replyLetterRepository.findAllByReceiverIdAndLetterIdAndStatus(
                userId,
                query.letterId(),
                OPEN,
                query.commonPageCommand().toPageable()
        );
    }

    @Transactional(readOnly = true)
    public boolean isReplied(Long userId, Long letterId) {
        return replyLetterRepository.existsBySenderIdAndLetterId(userId, letterId);
    }

    @Transactional
    public void removeReplyLetter(Long userId, ReplyLetterDeleteCommand command) {
        ReplyLetter replyLetter = loadReplyLetter(command.id());

        replyLetter.delete(userId);
    }

    @Transactional
    public void removeReplyLetters(Long requesterId, List<Long> ids) {
        List<ReplyLetter> replyLetters = replyLetterRepository.findAllByIdInAndStatus(ids, OPEN);

        replyLetters.forEach(replyLetter -> replyLetter.delete(requesterId));
    }

    @Transactional
    public void blockReplyLetter(Long id) {
        ReplyLetter replyLetter = loadReplyLetter(id);

        replyLetter.block();
    }

    private ReplyLetter loadReplyLetter(Long id) {
        return replyLetterRepository.findByIdAndStatus(id, OPEN)
                .orElseThrow(() -> new LetterNotFoundException(REPLY_LETTER));
    }
}
