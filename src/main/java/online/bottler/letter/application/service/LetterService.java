package online.bottler.letter.application.service;

import static online.bottler.letter.domain.LetterType.LETTER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.bottler.shared.ddd.ApplicationService;
import online.bottler.letter.application.command.LetterCommand;
import online.bottler.letter.application.exception.LetterNotFoundException;
import online.bottler.letter.application.exception.UnauthorizedLetterAccessException;
import online.bottler.letter.domain.Letter;
import online.bottler.letter.domain.LetterStatus;
import online.bottler.letter.domain.repository.LetterBoxJpaRepository;
import online.bottler.letter.domain.repository.LetterJpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@ApplicationService
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LetterService implements BlockLetterService, GetLetterLabelService {

    private final LetterJpaRepository letterRepository;
    private final LetterBoxJpaRepository letterBoxRepository;

    @Transactional
    public Letter writeLetter(Long userId, LetterCommand command) {
        return letterRepository.save(
                Letter.write(
                        userId,
                        command.title(),
                        command.content(),
                        command.font(),
                        command.paper(),
                        command.label(),
                        command.keywords()
                )
        );
    }

    public Letter getLetterWithKeywords(Long userId, Long letterId) {
        if (!isLetterInBox(userId, letterId)) {
            throw new UnauthorizedLetterAccessException();
        }

        return letterRepository.findByIdAndStatusWithKeywords(letterId, LetterStatus.OPEN)
                .orElseThrow(() -> new LetterNotFoundException(LETTER));
    }

    private boolean isLetterInBox(Long userId, Long letterId) {
        return letterBoxRepository.existsByUserIdAndLetterId(userId, letterId);
    }

    public List<Letter> getLettersIncludingAllStatus(List<Long> letterIds) {
        return letterRepository.findAllByIdIn(letterIds);
    }

    public List<Long> getLetterIds(Long userId) {
        return letterRepository.findIdsByUserIdAndStatus(userId, LetterStatus.OPEN);
    }

    @Override
    public String getLabel(Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new LetterNotFoundException(LETTER));

        return letter.getLabel();
    }

    public List<String> getMostFrequentKeywords(Long userId) {
        return letterRepository.findFrequentKeywords(userId);
    }

    public Long getFirstValidLetterId(List<Long> letterIds) {
        for (Long letterId : letterIds) {
            if (isValidLetter(letterId)) {
                return letterId;
            }
        }
        return null;
    }

    private boolean isValidLetter(Long letterId) {
        return letterRepository.existsByIdAndStatus(letterId, LetterStatus.OPEN);
    }

    @Transactional
    public void removeLetter(Long requesterId, Long letterId) {
        deleteLetters(requesterId, List.of(letterId));
    }

    @Transactional
    public void removeLetters(Long requesterId, List<Long> letterIds) {
        deleteLetters(requesterId, letterIds);
    }

    private void deleteLetters(Long requesterId, List<Long> letterIds) {
        List<Letter> letters = letterRepository.findAllByIdInAndStatusWithKeywords(letterIds, LetterStatus.OPEN);

        letters.forEach(letter -> letter.delete(requesterId));
    }

    @Transactional
    public void blockLetter(Long letterId) {
        Letter letter = letterRepository.findByIdAndStatusWithKeywords(letterId, LetterStatus.OPEN)
                .orElseThrow(() -> new LetterNotFoundException(LETTER));

        letter.block();
    }
}
