package online.bottler.mapletter.adaptor.out.persistence;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.mapletter.adaptor.out.persistence.repository.MapLetterArchiveJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import online.bottler.mapletter.domain.MapLetterArchive;
import online.bottler.mapletter.application.dto.FindAllArchiveLettersDTO;
import online.bottler.mapletter.adaptor.out.persistence.entity.MapLetterArchiveEntity;
import online.bottler.mapletter.application.port.out.MapLetterArchivePersistencePort;

@Repository
@RequiredArgsConstructor
public class MapLetterArchivePersistenceAdapter implements MapLetterArchivePersistencePort {
    private final MapLetterArchiveJpaRepository mapLetterArchiveJpaRepository;

    @Override
    public MapLetterArchive save(MapLetterArchive archive) {
        MapLetterArchiveEntity mapLetterArchiveEntity = MapLetterArchiveEntity.from(archive);
        MapLetterArchiveEntity save = mapLetterArchiveJpaRepository.save(mapLetterArchiveEntity);
        return MapLetterArchiveEntity.toDomain(save);
    }

    @Override
    public Page<FindAllArchiveLettersDTO> findAllById(Long userId, Pageable pageable) {
        return mapLetterArchiveJpaRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public MapLetterArchive findById(Long archiveId) {
        return MapLetterArchiveEntity.toDomain(mapLetterArchiveJpaRepository.findById(archiveId)
                .orElseThrow(() -> new AdaptorException("해당 편지를 찾을 수 없습니다.")));
    }

    @Override
    public boolean findByLetterIdAndUserId(Long letterId, Long userId) {
        return mapLetterArchiveJpaRepository.findByMapLetterIdAndUserId(letterId, userId).isPresent();
        // 값이 없으면 false
    }

    @Override
    public List<MapLetterArchive> findAllById(List<Long> letterIds) {
        return mapLetterArchiveJpaRepository.findAllById(letterIds)
                .stream()
                .map(MapLetterArchiveEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByIdInBatch(List<Long> letterIds) {
        mapLetterArchiveJpaRepository.deleteAllByIdInBatch(letterIds);
    }
}
