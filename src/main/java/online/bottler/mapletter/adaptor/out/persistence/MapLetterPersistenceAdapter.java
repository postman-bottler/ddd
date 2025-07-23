package online.bottler.mapletter.adaptor.out.persistence;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.mapletter.adaptor.out.persistence.repository.MapLetterJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import online.bottler.mapletter.domain.MapLetter;
import online.bottler.mapletter.application.dto.FindReceivedMapLetterDTO;
import online.bottler.mapletter.application.dto.FindSentMapLetter;
import online.bottler.mapletter.application.dto.MapLetterAndDistance;
import online.bottler.mapletter.adaptor.out.persistence.entity.MapLetterEntity;
import online.bottler.mapletter.application.port.out.MapLetterPersistencePort;

@Repository
@RequiredArgsConstructor
public class MapLetterPersistenceAdapter implements MapLetterPersistencePort {
    private final MapLetterJpaRepository mapLetterJpaRepository;

    @Override
    public MapLetter save(MapLetter mapLetter) {
        MapLetterEntity mapLetterEntity = MapLetterEntity.from(mapLetter);
        MapLetterEntity save = mapLetterJpaRepository.save(mapLetterEntity);
        return MapLetterEntity.toDomain(save);
    }

    @Override
    public MapLetter findById(Long id) {
        MapLetterEntity mapLetter = mapLetterJpaRepository.findById(id)
                .orElseThrow(() -> new AdaptorException("해당 편지를 찾을 수 없습니다."));
        return MapLetterEntity.toDomain(mapLetter);
    }

    @Override
    public void softDelete(Long letterId) {
        MapLetterEntity letter = mapLetterJpaRepository.findById(letterId)
                .orElseThrow(() -> new AdaptorException("해당 편지를 찾을 수 없습니다."));

        letter.updateDelete(true);
    }

    @Override
    public Page<MapLetter> findActiveByCreateUserId(Long userId, Pageable pageable) {
        Page<MapLetterEntity> findActiveLetters = mapLetterJpaRepository.findActiveByCreateUserId(userId, pageable);

        return findActiveLetters.map(MapLetterEntity::toDomain);
    }

    @Override
    public Page<MapLetter> findActiveByTargetUserId(Long userId, Pageable pageable) {
        Page<MapLetterEntity> findActiveLetters = mapLetterJpaRepository.findActiveByTargetUserId(userId, pageable);

        return findActiveLetters.map(MapLetterEntity::toDomain);
    }

    @Override
    public List<MapLetterAndDistance> findLettersByUserLocation(BigDecimal latitude, BigDecimal longitude,
                                                                Long userId) {
        return mapLetterJpaRepository.findLettersByUserLocation(latitude, longitude, userId);
    }

    @Override
    public MapLetter findSourceMapLetterById(Long sourceMapLetterId) {
        MapLetterEntity activeLetter = mapLetterJpaRepository.findActiveById(sourceMapLetterId);
        if (activeLetter == null) {
            throw new AdaptorException("원본 편지를 찾을 수 없습니다. 편지가 존재하지 않거나 삭제되었습니다.");
        }
        return MapLetterEntity.toDomain(activeLetter);
    }

    @Override
    public void letterBlock(Long letterId) {
        mapLetterJpaRepository.letterBlock(letterId);
    }

    @Override
    public Double findDistanceByLatitudeAndLongitudeAndLetterId(BigDecimal latitude, BigDecimal longitude,
                                                                Long letterId) {
        return mapLetterJpaRepository.findDistanceByLatitudeAndLongitudeAndLetterId(latitude, longitude, letterId);
    }

    @Override
    public Page<FindSentMapLetter> findSentLettersByUserId(Long userId, Pageable pageable) {
        return mapLetterJpaRepository.findSentLettersByUserId(userId, pageable);
    }

    @Override
    public Page<FindReceivedMapLetterDTO> findActiveReceivedMapLettersByUserId(Long userId, PageRequest pageRequest) {
        return mapLetterJpaRepository.findActiveReceivedMapLettersByUserId(userId, pageRequest);
    }

    @Override
    public List<MapLetterAndDistance> guestFindLettersByUserLocation(BigDecimal latitude, BigDecimal longitude) {
        return mapLetterJpaRepository.guestFindLettersByUserLocation(latitude, longitude);
    }

    @Override
    public List<MapLetter> findAllByIds(List<Long> ids) {
        return mapLetterJpaRepository.findAllById(ids).stream()
                .map(MapLetterEntity::toDomain)
                .toList();
    }

    @Override
    public void updateRead(MapLetter mapLetter) {
        MapLetterEntity mapLetterEntity = mapLetterJpaRepository.findById(mapLetter.getId())
                .orElseThrow(() -> new AdaptorException("해당 편지를 찾을 수 없습니다."));
        mapLetterEntity.updateRead();
    }

    @Override
    public void softDeleteAllByCreateUserId(Long userId) {
        List<MapLetterEntity> mapLetterEntities = mapLetterJpaRepository.findAllByCreateUserId(userId);

        if (mapLetterEntities.isEmpty()) {
            throw new AdaptorException("삭제할 편지가 없습니다.");
        }

        mapLetterEntities.forEach(mapLetterEntity -> mapLetterEntity.updateDelete(true));
    }

    @Override
    public void softDeleteForRecipient(Long letterId) {
        MapLetterEntity deleteLetter = mapLetterJpaRepository.findById(letterId)
                .orElseThrow(() -> new AdaptorException("편지를 찾을 수 없습니다."));
        deleteLetter.updateRecipientDeleted(true);
    }

    @Override
    public void softDeleteAllForRecipient(Long userId) {
        List<MapLetterEntity> mapLetterEntities = mapLetterJpaRepository.findAllByTargetUserId(userId);

        if (mapLetterEntities.isEmpty()) {
            throw new AdaptorException("삭제 할 편지가 없습니다.");
        }

        mapLetterEntities.forEach(mapLetterEntity -> mapLetterEntity.updateRecipientDeleted(true));
    }

    @Override
    public void softDeleteAll(List<MapLetter> mapLetters) {
        List<Long> ids=mapLetters.stream().map(MapLetter::getId).toList();
        List<MapLetterEntity> mapLetterEntities = mapLetterJpaRepository.findAllById(ids);
        mapLetterEntities.forEach(mapLetterEntity -> mapLetterEntity.updateDelete(true));
    }
}
