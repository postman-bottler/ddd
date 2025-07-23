package online.bottler.user.application;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import online.bottler.label.application.port.in.LabelUseCase;
import online.bottler.label.domain.Label;
import online.bottler.letter.application.service.ArchiveLetterBoxService;
import online.bottler.notification.application.port.NotificationUseCase;
import online.bottler.recommendation.application.service.RecommendService;
import online.bottler.slack.application.port.in.SlackUseCase;
import online.bottler.slack.domain.SlackConstant;
import online.bottler.user.application.command.SignUpCommand;
import online.bottler.user.application.port.in.BanUseCase;
import online.bottler.user.application.port.in.UserUseCase;
import online.bottler.user.application.response.SignIn;
import online.bottler.user.domain.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserUseCase userUseCase;
    private final SlackUseCase slackUseCase;
    private final BanUseCase banUseCase;
    private final NotificationUseCase notificationUseCase;
    private final RecommendService recommendService;
    private final ArchiveLetterBoxService archiveLetterBoxService;
    private final LabelUseCase labelUseCase;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updateWarningCount(Long userId) {
        User user = userUseCase.updateUserWarningCountByUserId(userId);

        slackUseCase.sendSlackMessage(SlackConstant.WARNING, user.getUserId());

        if (user.checkBan()) {
            banUseCase.banUser(user);
            slackUseCase.sendSlackMessage(SlackConstant.BAN, userId);
            notificationUseCase.sendBanNotification(userId);
        }

        userUseCase.updateWarningCount(user);
    }

    @Transactional
    public void createUser(SignUpCommand signUpCommand) {
        String profileImageUrl = userUseCase.findRandomProfileImageUrl();

        User user = User.createUser(signUpCommand.email(), passwordEncoder.encode(signUpCommand.password()),
                signUpCommand.nickname(), profileImageUrl);
        User storedUser = userUseCase.createUser(user);

        giveDefaultLabelsToNewUser(storedUser);

        List<Long> randomDevelopLetter = findRandomDevelopLetter();
        recommendService.saveDeveloperLetter(storedUser.getUserId(), randomDevelopLetter);
        archiveLetterBoxService.archiveLetters(storedUser.getUserId(), randomDevelopLetter);
    }

    @Transactional
    public SignIn kakaoSignin(String kakaoId, String nickname) {
        if (!userUseCase.isUserExistsByKakaoId(kakaoId)) {
            nickname = generateUniqueNickname(nickname);
            String profileImageUrl = userUseCase.findRandomProfileImageUrl();
            User user = User.createKakaoUser(kakaoId, nickname, profileImageUrl, passwordEncoder.encode(kakaoId));
            User storedUser = userUseCase.createUser(user);
            giveDefaultLabelsToNewUser(storedUser);
        }
        return userUseCase.authenticateAndGenerateTokens(kakaoId, kakaoId);
    }

    private String generateUniqueNickname(String nickname) {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);

        while (userUseCase.isUserExistsByNickname(nickname)) {
            int randomNumber = random.nextInt(10000);
            nickname = nickname + randomNumber;
        }
        return nickname;
    }

    private void giveDefaultLabelsToNewUser(User storedUser) {
        List<Long> defaultLabelIds = List.of(1L, 2L);
        for (Long labelId : defaultLabelIds) {
            Label label = labelUseCase.findLabelByLabelId(labelId);
            giveLabelToUser(storedUser, label);
        }
    }

    private void giveLabelToUser(User user, Label label) {
        labelUseCase.updateOwnedCount(label);
        labelUseCase.createUserLabel(user, label);
    }

    private List<Long> findRandomDevelopLetter() {
        Random random = new SecureRandom();
        Set<Long> randomNumbers = new LinkedHashSet<>();

        while (randomNumbers.size() < 3) {
            long number = 1L + random.nextInt(8);
            randomNumbers.add(number);
        }

        return new ArrayList<>(randomNumbers);
    }

}
