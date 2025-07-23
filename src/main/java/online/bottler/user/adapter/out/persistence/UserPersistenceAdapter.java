package online.bottler.user.adapter.out.persistence;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.user.adapter.out.persistence.entity.UserEntity;
import online.bottler.user.adapter.out.persistence.repository.UserJpaRepository;
import online.bottler.user.application.port.out.UserPersistencePort;
import online.bottler.user.domain.Ban;
import online.bottler.user.domain.Provider;
import online.bottler.user.domain.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPersistencePort {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        try {
            UserEntity userEntity = userJpaRepository.save(UserEntity.from(user));
            return UserEntity.toDomain(userEntity);
        } catch (DataIntegrityViolationException e) {
            throw new AdaptorException("이메일 또는 닉네임이 중복되었습니다.");
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userJpaRepository.existsByNickname(nickname);
    }

    @Override
    public User findByEmail(String email) {
        UserEntity userEntity = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new AdaptorException("유저를 찾을 수 없습니다. " + email));
        return UserEntity.toDomain(userEntity);
    }

    @Override
    public void softDeleteUser(Long userId) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new AdaptorException("해당 토큰에 대한 유저를 찾을 수 없습니다."));
        userEntity.updateIsDelete();
    }

    @Override
    public void updateNickname(Long userId, String nickname) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new AdaptorException("해당 토큰에 대한 유저를 찾을 수 없습니다."));
        userEntity.updateNickname(nickname);
    }

    @Override
    public void updatePassword(Long userId, String password) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new AdaptorException("해당 토큰에 대한 유저를 찾을 수 없습니다."));
        userEntity.updatePassword(password);
    }

    @Override
    public void updateProfileImageUrl(Long userId, String imageUrl) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new AdaptorException("해당 토큰에 대한 유저를 찾을 수 없습니다."));
        userEntity.updateImageUrl(imageUrl);
    }

    @Override
    public User findById(Long userId) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new AdaptorException("유저를 찾을 수 없습니다. " + userId));
        return UserEntity.toDomain(userEntity);
    }

    @Override
    public void updateUsers(List<User> users) {
        List<UserEntity> userEntities = users.stream().map(UserEntity::from).toList();
        userJpaRepository.saveAll(userEntities);
    }

    @Override
    public List<User> findWillBeUnbannedUsers(List<Ban> bans) {
        List<Long> ids = bans.stream()
                .map(Ban::getUserId)
                .toList();
        return userJpaRepository.findByUserIdIn(ids).stream()
                .map(UserEntity::toDomain)
                .toList();
    }

    public boolean existsByEmailAndProvider(String kakaoId) {
        return userJpaRepository.existsByEmailAndProvider(kakaoId, Provider.KAKAO);
    }

    @Override
    public List<User> findAllUserId() {
        List<UserEntity> userEntities = userJpaRepository.findAll();
        return userEntities.stream()
                .map(UserEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void updateWarningCount(User user) {
        UserEntity userEntity = userJpaRepository.findById(user.getUserId())
                .orElseThrow(() -> new AdaptorException("해당 토큰에 대한 유저를 찾을 수 없습니다."));
        userEntity.updateBanUser(user);
    }

    @Override
    public User findByNickname(String nickname) {
        UserEntity userEntity = userJpaRepository.findByNickname(nickname)
                .orElseThrow(() -> new AdaptorException("해당 닉네임에 대한 유저를 찾을 수 없습니다."));
        return UserEntity.toDomain(userEntity);
    }

    @Override
    public List<Object[]> findIdAndNicknameByUserIdIn(Set<Long> ids) {
        return userJpaRepository.getIdAndNicknameByUserIdIn(ids);
    }

    @Override
    public List<Object[]> findIdAndImageUrlByUserIdIn(Set<Long> ids) {
        return userJpaRepository.getIdAndImageByUserIdIn(ids);
    }
}
