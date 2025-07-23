package online.bottler.letter.application.service;

import java.util.List;

public interface ArchiveLetterBoxService {
    void archiveLetters(Long userId, List<Long> letterIds);
}
