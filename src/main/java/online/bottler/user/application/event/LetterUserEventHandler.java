package online.bottler.user.application.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.bottler.letter.domain.event.LetterBlockedEvent;
import online.bottler.letter.domain.event.ReplyLetterBlockedEvent;
import online.bottler.user.application.UserFacade;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LetterUserEventHandler {

    private final UserFacade userFacade;

    @EventListener
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void onReplyLetterBlockedEvent(ReplyLetterBlockedEvent event) {
        try {
            userFacade.updateWarningCount(event.userId());
        } catch (Exception e) {
            log.error("유저 경고 처리 실패 : {}", e.getMessage());
        }
    }

    @EventListener
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void onLetterBlockedEvent(LetterBlockedEvent event) {
        try {
            userFacade.updateWarningCount(event.userId());
        } catch (Exception e) {
            log.error("유저 경고 처리 실패 : {}", e.getMessage());
        }
    }
}
