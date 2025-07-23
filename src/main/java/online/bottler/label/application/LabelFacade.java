package online.bottler.label.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.ApplicationException;
import online.bottler.label.application.command.LabelCommand;
import online.bottler.label.application.port.in.LabelUseCase;
import online.bottler.label.application.response.LabelResponse;
import online.bottler.label.domain.Label;
import online.bottler.label.domain.LabelType;
import online.bottler.scheduler.LabelScheduler;
import online.bottler.user.application.port.in.UserUseCase;
import online.bottler.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LabelFacade {

    private final LabelUseCase labelUseCase;
    private final UserUseCase userUseCase;
    private final LabelScheduler labelScheduler;

    @Transactional
    public LabelResponse createFirstComeFirstServedLabel(Long userId) {
        User user = userUseCase.findById(userId);

        List<Label> firstComeLabels = labelUseCase.findByLabelType(LabelType.FIRST_COME);

        for (Label label : firstComeLabels) {
            boolean hasLabel = labelUseCase.isLabelExistsByUserAndLabel(user, label);
            if (!hasLabel && label.isOwnedCountValid()) {
                labelUseCase.updateOwnedCount(label);
                labelUseCase.createUserLabel(user, label);
                return label.toLabelResponse();
            }
        }

        throw new ApplicationException("모든 선착순 뽑기 라벨이 마감되었습니다.");
    }

    @Transactional
    public void updateFirstComeLabel(LabelCommand labelCommand) {
        labelScheduler.scheduleUpdateFirstComeLabel(labelCommand);
    }
}
