package online.bottler.mapletter.adaptor.out.persistence;

import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.mapletter.adaptor.out.persistence.repository.ReplyMapLetterJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import online.bottler.mapletter.domain.ReplyMapLetter;
import online.bottler.mapletter.application.dto.ReplyProjectDTO;
import online.bottler.mapletter.adaptor.out.persistence.entity.ReplyMapLetterEntity;
import online.bottler.mapletter.application.port.out.ReplyMapLetterPersistencePort;

@Repository
@RequiredArgsConstructor
public class ReplyMapLetterPersistenceAdapter implements ReplyMapLetterPersistencePort {
    private final ReplyMapLetterJpaRepository replyMapLetterJpaRepository;
    private final EntityManager em;

    @Override
    public ReplyMapLetter save(ReplyMapLetter replyMapLetter) {
        ReplyMapLetterEntity replyMapLetterEntity = ReplyMapLetterEntity.from(replyMapLetter);
        ReplyMapLetterEntity save = replyMapLetterJpaRepository.save(replyMapLetterEntity);
        return ReplyMapLetterEntity.toDomain(save);
    }

    @Override
    public Page<ReplyMapLetter> findActiveReplyMapLettersBySourceUserId(Long userId, Pageable pageable) {
        Page<ReplyMapLetterEntity> findActiveLetters = replyMapLetterJpaRepository.findActiveReplyMapLettersBySourceUserId(
                userId, pageable);

        return findActiveLetters.map(ReplyMapLetterEntity::toDomain);
    }

    @Override
    public Page<ReplyMapLetter> findReplyMapLettersBySourceLetterId(Long letterId, Pageable pageable) {
        Page<ReplyMapLetterEntity> findActiveLetters = replyMapLetterJpaRepository.findReplyMapLettersBySourceLetterId(
                letterId, pageable);

        return findActiveLetters.map(ReplyMapLetterEntity::toDomain);
    }

    @Override
    public ReplyMapLetter findById(Long letterId) {
        ReplyMapLetterEntity replyMapLetterEntity = replyMapLetterJpaRepository.findById(letterId)
                .orElseThrow(() -> new AdaptorException("해당 편지를 찾을 수 없습니다."));

        return ReplyMapLetterEntity.toDomain(replyMapLetterEntity);
    }

    @Override
    public boolean findByLetterIdAndUserId(Long letterId, Long userId) {
        return replyMapLetterJpaRepository.findBySourceLetterIdAndCreateUserId(letterId, userId).isPresent();
        // 값이 없으면 false
    }

    @Override
    public void letterBlock(Long letterId) {
        replyMapLetterJpaRepository.letterBlock(letterId);
    }

    @Override
    public void softDelete(Long letterId) {
        ReplyMapLetterEntity letter = replyMapLetterJpaRepository.findById(letterId)
                .orElseThrow(() -> new AdaptorException("해당 편지를 찾을 수 없습니다."));

        ReplyMapLetterEntity replyMapLetter = em.find(ReplyMapLetterEntity.class, letterId);
        replyMapLetter.updateDelete(true);
    }

    @Override
    public Page<ReplyMapLetter> findAllSentReplyByUserId(Long userId, PageRequest pageRequest) {
        Page<ReplyMapLetterEntity> letters = replyMapLetterJpaRepository.findAllSentReplyByUserId(userId, pageRequest);
        return letters.map(ReplyMapLetterEntity::toDomain);
    }

    @Override
    public List<ReplyProjectDTO> findRecentMapKeywordReplyByUserId(Long userId, int fetchItemSize) {
        return replyMapLetterJpaRepository.findRecentMapKeywordReplyByUserId(userId, fetchItemSize);
    }

    @Override
    public void softDeleteAllByCreateUserId(Long userId) {
        List<ReplyMapLetterEntity> letters = replyMapLetterJpaRepository.findAllByCreateUserId(userId);

        if (letters.isEmpty()) {
            throw new AdaptorException("삭제할 편지가 없습니다.");
        }

        letters.forEach(letter -> letter.updateDelete(true));
    }

    @Override
    public void softDeleteForRecipient(Long letterId) {
        ReplyMapLetterEntity letter = replyMapLetterJpaRepository.findById(letterId)
                .orElseThrow(() -> new AdaptorException("편지를 찾을 수 없습니다."));

        letter.updateRecipientDeleted(true);
    }

    @Override
    public void softDeleteAllForRecipient(Long userId) {
        List<ReplyMapLetterEntity> letters = replyMapLetterJpaRepository.findAllBySourceLetterCreateUserId(userId);

        if (letters.isEmpty()) {
            throw new AdaptorException("삭제할 편지가 없습니다.");
        }

        letters.forEach(letter -> letter.updateRecipientDeleted(true));
    }

    @Override
    public List<ReplyMapLetter> findByIds(List<Long> letterIds) {
        List<ReplyMapLetterEntity> replyLetters = replyMapLetterJpaRepository.findAllById(letterIds);
        return replyLetters.stream().map(ReplyMapLetterEntity::toDomain).toList();
    }
}
