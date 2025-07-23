package online.bottler.user.application;

import jakarta.mail.MessagingException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.bottler.auth.JwtTokenProvider;
import online.bottler.shared.exception.ApplicationException;
import online.bottler.user.application.command.AuthEmailCommand;
import online.bottler.user.application.command.ChangePasswordCommand;
import online.bottler.user.application.command.CheckDuplicateNicknameCommand;
import online.bottler.user.application.command.CheckPasswordCommand;
import online.bottler.user.application.command.EmailCommand;
import online.bottler.user.application.command.NicknameCommand;
import online.bottler.user.application.command.ProfileImgCommand;
import online.bottler.user.application.command.SignInCommand;
import online.bottler.user.application.command.SignUpCommand;
import online.bottler.user.application.response.AccessTokenResponse;
import online.bottler.user.application.response.ExistingUserResponse;
import online.bottler.user.application.response.SignIn;
import online.bottler.user.application.response.UserResponse;
import online.bottler.user.domain.EmailCode;
import online.bottler.user.domain.EmailForm;
import online.bottler.user.domain.ProfileImage;
import online.bottler.user.domain.RefreshToken;
import online.bottler.user.domain.User;
import online.bottler.user.application.port.in.EmailUseCase;
import online.bottler.user.application.port.in.UserUseCase;
import online.bottler.user.application.port.out.EmailCodePersistencePort;
import online.bottler.user.application.port.out.ProfileImagePersistencePort;
import online.bottler.user.application.port.out.RefreshTokenPersistencePort;
import online.bottler.user.application.port.out.UserPersistencePort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {
    private final UserPersistencePort userPersistencePort;
    private final RefreshTokenPersistencePort refreshTokenPersistencePort;
    private final ProfileImagePersistencePort profileImagePersistencePort;
    private final EmailCodePersistencePort emailCodePersistencePort;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final EmailUseCase emailUseCase;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();

    public String findRandomProfileImageUrl() {
        return profileImagePersistencePort.findProfileImage();
    }

    public User createUser(User user) {
        return userPersistencePort.save(user);
    }

    @Transactional
    public void createDeveloper(SignUpCommand signUpCommand) {
        String profileImageUrl = profileImagePersistencePort.findProfileImage();
        User user = User.createDeveloper(signUpCommand.email(), passwordEncoder.encode(signUpCommand.password()),
                signUpCommand.nickname(), profileImageUrl);
        userPersistencePort.save(user);
    }

    @Transactional
    public void checkEmail(EmailCommand emailCommand) {
        if (userPersistencePort.existsByEmail(emailCommand.email())) {
            throw new ApplicationException("이메일이 중복되었습니다.");
        }
    }

    @Transactional
    public void checkNickname(CheckDuplicateNicknameCommand checkDuplicateNicknameCommand) {
        if (userPersistencePort.existsByNickname(checkDuplicateNicknameCommand.nickname())) {
            throw new ApplicationException("닉네임이 중복되었습니다.");
        }
    }

    @Transactional
    public SignIn signin(SignInCommand signInCommand) {
        try {
            return authenticateAndGenerateTokens(signInCommand.email(), signInCommand.password());
        } catch (BadCredentialsException e) {
            throw new ApplicationException("비밀번호가 일치하지 않습니다.");
        }
    }

    @Transactional
    public AccessTokenResponse validateRefreshToken(String refreshToken) {
        //db에 저장되어 있는 email과 refreshToken의 email과 같은지 비교
        String emailFromRefreshToken = jwtTokenProvider.getEmailFromToken(refreshToken);
        String storedEmail = refreshTokenPersistencePort.findEmailByRefreshToken(refreshToken);
        if (!storedEmail.equals(emailFromRefreshToken)) {
            throw new ApplicationException("유효하지 않은 jwt 토큰입니다.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        return new AccessTokenResponse(newAccessToken);
    }

    @Transactional
    public void deleteUser(CheckPasswordCommand checkPasswordCommand, String email) {
        User user = userPersistencePort.findByEmail(email);
        if (!passwordEncoder.matches(checkPasswordCommand.password(), user.getPassword())) {
            throw new ApplicationException("비밀번호가 일치하지 않습니다.");
        }
        userPersistencePort.softDeleteUser(user.getUserId());
        refreshTokenPersistencePort.deleteByEmail(email);
    }

    @Transactional
    public UserResponse findUser(String email) {
        User user = userPersistencePort.findByEmail(email);
        return UserResponse.from(user);
    }

    @Transactional
    public void updateNickname(NicknameCommand nicknameCommand, String email) {
        if (userPersistencePort.existsByNickname(nicknameCommand.nickname())) {
            throw new ApplicationException("닉네임이 중복되었습니다.");
        }
        User user = userPersistencePort.findByEmail(email);
        userPersistencePort.updateNickname(user.getUserId(), nicknameCommand.nickname());
    }

    @Transactional
    public void updatePassword(ChangePasswordCommand changePasswordCommand, String email) {
        User user = userPersistencePort.findByEmail(email);
        if (!passwordEncoder.matches(changePasswordCommand.existingPassword(), user.getPassword())) {
            throw new ApplicationException("비밀번호가 일치하지 않습니다.");
        }
        userPersistencePort.updatePassword(user.getUserId(),
                passwordEncoder.encode(changePasswordCommand.newPassword()));
    }

    @Transactional
    public void updateProfileImage(ProfileImgCommand profileImgCommand, String email) {
        if (!profileImagePersistencePort.existsByUrl(profileImgCommand.imageUrl())) {
            throw new ApplicationException("유효하지 않은 프로필 이미지 URL입니다.");
        }
        User user = userPersistencePort.findByEmail(email);
        userPersistencePort.updateProfileImageUrl(user.getUserId(), profileImgCommand.imageUrl());
    }

    @Transactional
    public void createProfileImg(ProfileImgCommand profileImgCommand) {
        ProfileImage profileImage = ProfileImage.createProfileImg(profileImgCommand.imageUrl());
        profileImagePersistencePort.save(profileImage);
    }

    @Transactional
    public ExistingUserResponse findExistingUser(String nickname) {
        return new ExistingUserResponse(userPersistencePort.existsByNickname(nickname));
    }

    @Transactional
    public void sendCodeToEmail(EmailCommand emailCommand) {
        String authCode = createCode();
        EmailForm emailForm = EmailForm.EMAIL_AUTH;

        String title = emailForm.getTitle();
        String content = emailForm.getContent(authCode);

        try {
            emailUseCase.sendEmail(emailCommand.email(), title, content);
            emailCodePersistencePort.save(EmailCode.createEmailCode(emailCommand.email(), authCode));
        } catch (RuntimeException | MessagingException e) {
            throw new ApplicationException("인증코드 요청에 실패했습니다.");
        }
    }

    private String createCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }
        return code.toString();
    }

    @Transactional
    public void verifyCode(AuthEmailCommand authEmailCommand) {
        EmailCode emailCode = emailCodePersistencePort.findEmailCode(authEmailCommand.email(), authEmailCommand.code());
        emailCode.checkExpiration();
    }

    @Transactional
    public User findById(Long userId) {
        return userPersistencePort.findById(userId);
    }

    public SignIn authenticateAndGenerateTokens(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        refreshTokenPersistencePort.createRefreshToken(RefreshToken.createRefreshToken(email, refreshToken));

        return new SignIn(accessToken, refreshToken);
    }

    public boolean isUserExistsByKakaoId(String kakaoId) {
        return userPersistencePort.existsByEmailAndProvider(kakaoId);
    }

    public boolean isUserExistsByNickname(String nickname) {
        return userPersistencePort.existsByNickname(nickname);
    }

    @Transactional
    public String getProfileImageUrlById(Long userId) {
        return userPersistencePort.findById(userId).getImageUrl();
    }

    @Transactional
    public String getNicknameById(Long userId) {
        return userPersistencePort.findById(userId).getNickname();
    }

    public User updateUserWarningCountByUserId(Long userId) {
        User user = userPersistencePort.findById(userId);
        user.updateWarningCount();
        return user;
    }

    public void updateWarningCount(User user) {
        userPersistencePort.updateWarningCount(user);
    }

    @Transactional
    public List<Long> getAllUserIds() {
        List<User> users = userPersistencePort.findAllUserId();
        return users.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long getUserIdByNickname(String nickname) {
        User user = userPersistencePort.findByNickname(nickname);
        return user.getUserId();
    }

    @Transactional
    public void deleteEmailCode(AuthEmailCommand authEmailCommand) {
        emailCodePersistencePort.deleteByEmail(authEmailCommand.email());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> getNicknamesByIds(Set<Long> ids) {
        List<Object[]> getIdAndNicknames = userPersistencePort.findIdAndNicknameByUserIdIn(ids);

        return getIdAndNicknames.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (String) row[1]
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> getProfileImageUrlsByIds(Set<Long> ids) {
        List<Object[]> getProfileImageUrls = userPersistencePort.findIdAndImageUrlByUserIdIn(ids);

        return getProfileImageUrls.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (String) row[1]
                ));
    }
}
