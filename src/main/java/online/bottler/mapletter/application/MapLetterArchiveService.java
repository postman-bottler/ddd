package online.bottler.mapletter.application;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.ApplicationException;
import online.bottler.mapletter.application.command.DeleteArchivedLettersCommand;
import online.bottler.mapletter.application.dto.FindAllArchiveLettersDTO;
import online.bottler.mapletter.application.port.in.MapLetterArchiveUseCase;
import online.bottler.mapletter.application.port.out.MapLetterArchivePersistencePort;
import online.bottler.mapletter.application.port.out.MapLetterPersistencePort;
import online.bottler.mapletter.application.response.FindAllArchiveLettersResponse;
import online.bottler.mapletter.application.validator.PageValidator;
import online.bottler.mapletter.domain.MapLetter;
import online.bottler.mapletter.domain.MapLetterArchive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MapLetterArchiveService implements MapLetterArchiveUseCase {

    private final MapLetterPersistencePort mapLetterPersistencePort;
    private final MapLetterArchivePersistencePort mapLetterArchivePersistencePort;

    @Override
    @Transactional
    public MapLetterArchive mapLetterArchive(Long letterId, Long userId) {
        MapLetter mapLetter = mapLetterPersistencePort.findById(letterId);
        mapLetter.validMapLetterArchive();

        MapLetterArchive archive = MapLetterArchive.builder().mapLetterId(letterId).userId(userId)
                .createdAt(LocalDateTime.now()).build();

        if (isArchived(letterId, userId)) {
            throw new ApplicationException("해당 지도 편지가 이미 저장되어 있습니다.");
        }

        return mapLetterArchivePersistencePort.save(archive);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FindAllArchiveLettersResponse> findArchiveLetters(int page, int size, Long userId) {
        PageValidator.validMinPage(page);
        Page<FindAllArchiveLettersDTO> letters = mapLetterArchivePersistencePort.findAllById(userId,
                PageRequest.of(page - 1, size));

        if (letters.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page - 1, size), 0);
        }

        PageValidator.validMaxPage(letters.getTotalPages(), page);
        return letters.map(FindAllArchiveLettersDTO::toFindAllArchiveLettersResponse);
    }

    @Override
    @Transactional
    public void deleteArchivedLetter(DeleteArchivedLettersCommand deleteArchivedLettersCommand, Long userId) {
        List<MapLetterArchive> archiveInfos = mapLetterArchivePersistencePort.findAllById(
                deleteArchivedLettersCommand.letterIds());

        if (archiveInfos.size() != deleteArchivedLettersCommand.letterIds().size()) {
            throw new ApplicationException("삭제할 지도 편지를 찾을 수 없습니다.");
        } //일부 아이디가 존재하지 않을 경우 예외 처리

        for (MapLetterArchive archiveInfo : archiveInfos) {
            archiveInfo.validDeleteArchivedLetter(userId);
        }

        mapLetterArchivePersistencePort.deleteAllByIdInBatch(deleteArchivedLettersCommand.letterIds());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isArchived(Long letterId, Long userId) {
        return mapLetterArchivePersistencePort.findByLetterIdAndUserId(letterId, userId);
    }
}
