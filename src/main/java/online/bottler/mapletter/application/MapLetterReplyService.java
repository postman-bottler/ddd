package online.bottler.mapletter.application;

import static online.bottler.mapletter.application.DeleteLetterType.MAP;
import static online.bottler.mapletter.application.DeleteLetterType.REPLY;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.ApplicationException;
import online.bottler.mapletter.application.command.CreateReplyMapLetterCommand;
import online.bottler.mapletter.application.command.DeleteReplyMapLettersCommand;
import online.bottler.mapletter.application.port.in.MapLetterReplyUseCase;
import online.bottler.mapletter.application.port.out.MapLetterPersistencePort;
import online.bottler.mapletter.application.port.out.RecentReplyCachePort;
import online.bottler.mapletter.application.port.out.ReplyMapLetterPersistencePort;
import online.bottler.mapletter.application.response.CheckReplyMapLetterResponse;
import online.bottler.mapletter.application.response.FindAllReceivedReplyLetterResponse;
import online.bottler.mapletter.application.response.FindAllReplyMapLettersResponse;
import online.bottler.mapletter.application.response.FindAllSentReplyMapLetterResponse;
import online.bottler.mapletter.application.response.OneReplyLetterResponse;
import online.bottler.mapletter.application.validator.PageValidator;
import online.bottler.mapletter.domain.MapLetter;
import online.bottler.mapletter.domain.ReplyMapLetter;
import online.bottler.reply.application.ReplyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MapLetterReplyService implements MapLetterReplyUseCase {

    private final ReplyMapLetterPersistencePort replyMapLetterPersistencePort;
    private final MapLetterPersistencePort mapLetterPersistencePort;
    private final RecentReplyCachePort recentReplyCachePort;

    @Override
    @Transactional
    public ReplyMapLetter createReplyMapLetter(CreateReplyMapLetterCommand createReplyMapLetterCommand, Long userId,
                                               MapLetter source) {
        boolean isReplied = replyMapLetterPersistencePort.findByLetterIdAndUserId(
                createReplyMapLetterCommand.sourceLetter(), userId);
        if (isReplied) {
            throw new ApplicationException("해당 지도 편지에 이미 답장을 했습니다.");
        }

        ReplyMapLetter replyMapLetter = createReplyMapLetterCommand.toReplyMapLetter(userId);
        ReplyMapLetter save = replyMapLetterPersistencePort.save(replyMapLetter);

        recentReplyCachePort.saveRecentReply(
                source.getCreateUserId(), ReplyType.MAP.name(), save.getReplyLetterId(), save.getLabel());

        return save;
    }

    @Override
    @Transactional(readOnly = true)
    public MapLetter findSourceMapLetter(Long LetterId) {
        return mapLetterPersistencePort.findSourceMapLetterById(LetterId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FindAllReplyMapLettersResponse> findAllReplyMapLetters(int page, int size, Long letterId, Long userId) {
        PageValidator.validMinPage(page);
        MapLetter sourceLetter = mapLetterPersistencePort.findById(letterId);

        sourceLetter.validFindAllReplyMapLetter(userId);

        Page<ReplyMapLetter> findReply = replyMapLetterPersistencePort.findReplyMapLettersBySourceLetterId(letterId,
                PageRequest.of(page - 1, size));

        if (findReply.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page - 1, size), 0);
        }

        PageValidator.validMaxPage(findReply.getTotalPages(), page);

        return findReply.map(FindAllReplyMapLettersResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public OneReplyLetterResponse findReplyMapLetter(Long letterId, Long userId) {
        ReplyMapLetter replyMapLetter = replyMapLetterPersistencePort.findById(letterId);
        MapLetter sourceLetter = mapLetterPersistencePort.findById(replyMapLetter.getSourceLetterId());

        replyMapLetter.validFindOneReplyMapLetter(userId, sourceLetter);
        sourceLetter.validDeleteAndBlocked();

        return OneReplyLetterResponse.from(replyMapLetter, userId == replyMapLetter.getCreateUserId());
    }

    @Override
    @Transactional
    public void deleteReplyMapLetter(DeleteReplyMapLettersCommand deleteReplyMapLettersCommand, Long userId) {
        List<ReplyMapLetter> replyMapLetters = replyMapLetterPersistencePort.findByIds(
                deleteReplyMapLettersCommand.letterIds());

        for (ReplyMapLetter replyMapLetter : replyMapLetters) {
            replyMapLetter.validDeleteReplyMapLetter(userId);
            replyMapLetterPersistencePort.softDelete(replyMapLetter.getReplyLetterId());
        }
        deleteRecentRepliesFromRedis(replyMapLetters);
    }

    private void deleteRecentRepliesFromRedis(List<ReplyMapLetter> replyMapLetters) {
        for (ReplyMapLetter replyMapLetter : replyMapLetters) {
            MapLetter sourceLetter = mapLetterPersistencePort.findById(replyMapLetter.getSourceLetterId());
            recentReplyCachePort.deleteRecentReply(sourceLetter.getCreateUserId(), ReplyType.MAP.name(),
                    replyMapLetter.getReplyLetterId(), replyMapLetter.getLabel());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FindAllSentReplyMapLetterResponse> findAllSentReplyMapLetters(int page, int size, Long userId) {
        PageValidator.validMinPage(page);
        Page<ReplyMapLetter> letters = replyMapLetterPersistencePort.findAllSentReplyByUserId(userId,
                PageRequest.of(page - 1, size));
        if (letters.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page - 1, size), 0);
        }
        PageValidator.validMaxPage(letters.getTotalPages(), page);

        return letters.map(replyMapLetter -> {
            MapLetter sourceLetter = mapLetterPersistencePort.findById(replyMapLetter.getSourceLetterId());
            String title = "Re: " + sourceLetter.getTitle();

            return FindAllSentReplyMapLetterResponse.from(replyMapLetter, title, REPLY);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FindAllReceivedReplyLetterResponse> findAllReceivedReplyMapLetters(int page, int size, Long userId) {
        PageValidator.validMinPage(page);
        Page<ReplyMapLetter> letters = replyMapLetterPersistencePort.findActiveReplyMapLettersBySourceUserId(userId,
                PageRequest.of(page - 1, size));

        if (letters.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page - 1, size), 0);
        }

        PageValidator.validMaxPage(letters.getTotalPages(), page);

        return letters.map(replyMapLetter -> {
            MapLetter sourceLetter = mapLetterPersistencePort.findById(replyMapLetter.getSourceLetterId());
            String title = "Re: " + sourceLetter.getTitle();
            return FindAllReceivedReplyLetterResponse.from(replyMapLetter, title, MAP);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public CheckReplyMapLetterResponse checkReplyMapLetter(Long letterId, Long userId) {
        return new CheckReplyMapLetterResponse(replyMapLetterPersistencePort.findByLetterIdAndUserId(letterId, userId));
    }
}
