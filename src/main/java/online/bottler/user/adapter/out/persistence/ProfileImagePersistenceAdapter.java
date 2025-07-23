package online.bottler.user.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.user.adapter.out.persistence.entity.ProfileImageEntity;
import online.bottler.user.adapter.out.persistence.repository.ProfileImageJpaRepository;
import online.bottler.user.application.port.out.ProfileImagePersistencePort;
import online.bottler.user.domain.ProfileImage;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileImagePersistenceAdapter implements ProfileImagePersistencePort {
    private final ProfileImageJpaRepository profileImageJpaRepository;

    @Override
    public void save(ProfileImage profileImage) {
        profileImageJpaRepository.save(ProfileImageEntity.from(profileImage));
    }

    @Override
    public boolean existsByUrl(String newProfileImage) {
        return profileImageJpaRepository.existsByImageUrl(newProfileImage);
    }

    @Override
    public String findProfileImage() {
        String profileImageUrl = profileImageJpaRepository.findRandomProfileImage();
        if (profileImageUrl == null) {
            throw new AdaptorException("프로필 이미지를 찾을 수 없습니다.");
        }
        return profileImageUrl;
    }
}
