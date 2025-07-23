package online.bottler.user.adapter.out.persistence;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.user.adapter.out.persistence.entity.RefreshTokenEntity;
import online.bottler.user.adapter.out.persistence.repository.RefreshTokenJpaRepository;
import online.bottler.user.application.port.out.RefreshTokenPersistencePort;
import online.bottler.user.domain.RefreshToken;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements RefreshTokenPersistencePort {
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public void createRefreshToken(RefreshToken refreshToken) {
        Optional<RefreshTokenEntity> refreshTokenEntityOpt = refreshTokenJpaRepository.findByEmail(
                refreshToken.getEmail());

        if (refreshTokenEntityOpt.isEmpty()) {
            refreshTokenJpaRepository.save(RefreshTokenEntity.from(refreshToken));
        } else {
            RefreshTokenEntity refreshTokenEntity = refreshTokenEntityOpt.get();
            refreshTokenEntity.updateRefreshToken(refreshToken.getRefreshToken());
            refreshTokenJpaRepository.save(refreshTokenEntity);
        }
    }

    @Override
    public String findEmailByRefreshToken(String refreshToken) {
        RefreshTokenEntity refreshTokenEntity = refreshTokenJpaRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AdaptorException("유효하지 않은 jwt 토큰입니다."));
        return refreshTokenEntity.getEmail();
    }

    @Override
    public void deleteByEmail(String email) {
        refreshTokenJpaRepository.deleteByEmail(email);
    }
}
