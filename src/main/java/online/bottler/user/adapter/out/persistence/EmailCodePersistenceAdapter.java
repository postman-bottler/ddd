package online.bottler.user.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.user.adapter.out.persistence.entity.EmailCodeEntity;
import online.bottler.user.adapter.out.persistence.repository.EmailCodeJpaRepository;
import online.bottler.user.application.port.out.EmailCodePersistencePort;
import online.bottler.user.domain.EmailCode;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class EmailCodePersistenceAdapter implements EmailCodePersistencePort {
    private final EmailCodeJpaRepository emailCodeJpaRepository;

    @Override
    public void save(EmailCode emailCode) {
        EmailCodeEntity emailCodeEntity = emailCodeJpaRepository.findByEmail(emailCode.getEmail());

        if (emailCodeEntity != null) {
            emailCodeEntity.changeCode(emailCode.getCode());
        } else {
            emailCodeJpaRepository.save(EmailCodeEntity.from(emailCode));
        }
    }

    @Override
    public EmailCode findEmailCode(String email, String code) {
        EmailCodeEntity emailCodeEntity = emailCodeJpaRepository.findByEmailAndCode(email, code)
                .orElseThrow(() -> new AdaptorException("유효하지 않은 인증코드입니다."));
        return EmailCodeEntity.toEmailCode(emailCodeEntity);
    }

    @Override
    public void deleteByEmail(String email) {
        emailCodeJpaRepository.deleteByEmail(email);
    }
}
