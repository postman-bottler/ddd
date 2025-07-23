package online.bottler.user.application;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.ApplicationException;
import online.bottler.user.domain.Ban;
import online.bottler.user.application.port.in.BanUseCase;
import online.bottler.user.application.port.out.BanPersistencePort;
import online.bottler.user.application.port.out.UserPersistencePort;
import online.bottler.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BanService implements BanUseCase {
    private static final Long BAN_DAYS = 7L;

    private final BanPersistencePort banPersistencePort;
    private final UserPersistencePort userPersistencePort;

    public void banUser(User user) {
        if (user.isBanned()) {
            Ban ban = banPersistencePort.findByUserId(user.getUserId())
                    .orElseThrow(() -> new ApplicationException("정지된 유저가 아닙니다."));
            ban.extendBanDuration(BAN_DAYS);
            banPersistencePort.updateBan(ban);
            return;
        }
        Ban ban = Ban.create(user.getUserId(), BAN_DAYS);
        user.banned();
        banPersistencePort.save(ban);
    }

    @Transactional
    public void unbans(LocalDateTime now) {
        List<Ban> expiredBans = banPersistencePort.findExpiredBans(now);
        List<User> willBeUnbanned = userPersistencePort.findWillBeUnbannedUsers(expiredBans);
        willBeUnbanned.forEach(User::unban);
        userPersistencePort.updateUsers(willBeUnbanned);
        banPersistencePort.deleteBans(expiredBans);
    }
}
