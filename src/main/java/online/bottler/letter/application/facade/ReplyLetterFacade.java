package online.bottler.letter.application.facade;

import static online.bottler.letter.domain.LetterType.REPLY_LETTER;
import static online.bottler.notification.domain.NotificationType.KEYWORD_REPLY;

import lombok.RequiredArgsConstructor;
import online.bottler.letter.application.command.CommonPageCommand;
import online.bottler.letter.application.command.RemoveLetterBoxCommand;
import online.bottler.letter.application.command.ReplyLetterCommand;
import online.bottler.letter.application.command.ReplyLetterDeleteCommand;
import online.bottler.letter.application.command.ReplyLetterSummariesQuery;
import online.bottler.letter.application.dto.ReplyLetterDetailInfo;
import online.bottler.letter.application.dto.ReplyLetterInfo;
import online.bottler.letter.application.dto.ReplyLetterSummaryInfo;
import online.bottler.letter.application.service.LetterBoxService;
import online.bottler.letter.application.service.RecentReplyForLetterService;
import online.bottler.letter.application.service.ReplyLetterService;
import online.bottler.letter.domain.LetterBoxType;
import online.bottler.letter.domain.ReplyLetter;
import online.bottler.notification.application.NotificationService;
import online.bottler.shared.security.AuthenticationProvider;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReplyLetterFacade {

    private final ReplyLetterService replyLetterService;
    private final NotificationService notificationService;
    private final LetterBoxService letterBoxService;
    private final RecentReplyForLetterService recentReplyForLetterService;
    private final AuthenticationProvider authenticationProvider;

    @Transactional
    public ReplyLetterInfo write(ReplyLetterCommand replyLetterCommand) {
        Long userId = authenticationProvider.getCurrentUserId();

        ReplyLetter replyLetter = replyLetterService.write(userId, replyLetterCommand);

        letterBoxService.archiveLetter(replyLetter);

        recentReplyForLetterService.push(replyLetter.getReceiverId(), replyLetter.getId(), replyLetter.getLabel());

        // 이벤트발행
        notificationService.sendLetterNotification(KEYWORD_REPLY, replyLetter.getReceiverId(), replyLetter.getId(),
                replyLetter.getLabel());

        return ReplyLetterInfo.from(replyLetter);
    }

    @Transactional(readOnly = true)
    public Page<ReplyLetterSummaryInfo> getSummaries(Long letterId, CommonPageCommand command) {
        Long userId = authenticationProvider.getCurrentUserId();
        return replyLetterService.getPagedReplyLetters(userId, ReplyLetterSummariesQuery.of(letterId, command)).map(ReplyLetterSummaryInfo::from);
    }

    @Transactional(readOnly = true)
    public ReplyLetterDetailInfo getDetail(Long id) {
        Long userId = authenticationProvider.getCurrentUserId();

        ReplyLetter replyLetter = replyLetterService.getReplyLetter(userId, id);

        boolean isReplied = replyLetterService.isReplied(userId, id);

        return ReplyLetterDetailInfo.from(replyLetter, isReplied);
    }

    @Transactional
    public void delete(ReplyLetterDeleteCommand command) {
        Long userId = authenticationProvider.getCurrentUserId();

        ReplyLetter replyLetter = replyLetterService.getReplyLetter(userId, command.id());

        replyLetterService.removeReplyLetter(userId, command);

        letterBoxService.removeLettersFromBox(
                RemoveLetterBoxCommand.byLetterId(command.id(), LetterBoxType.of(REPLY_LETTER, command.boxType()))
        );

        recentReplyForLetterService.delete(replyLetter.getReceiverId(), replyLetter.getId(), replyLetter.getLabel());
    }
}
