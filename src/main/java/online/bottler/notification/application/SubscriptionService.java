package online.bottler.notification.application;

import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.ApplicationException;
import online.bottler.notification.application.response.SubscriptionResponse;
import online.bottler.notification.application.port.SubscriptionUseCase;
import online.bottler.notification.application.port.SubscriptionPersistencePort;
import online.bottler.notification.domain.Device;
import online.bottler.notification.domain.Subscription;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionService implements SubscriptionUseCase {
    private final SubscriptionPersistencePort subscriptionPersistencePort;

    @Transactional
    public SubscriptionResponse subscribe(Long userId, String token) {
        Subscription subscription = Subscription.create(userId, token);
        if (subscriptionPersistencePort.checkDeviceDuplicate(subscription.getDevice())) {
            throw new ApplicationException("해당 기기는 이미 알림이 허용되어 있습니다.");
        }
        Subscription save = subscriptionPersistencePort.save(subscription);
        return SubscriptionResponse.from(save);
    }

    @Transactional
    public void unsubscribeAll(Long userId) {
        subscriptionPersistencePort.deleteAllByUserId(userId);
    }

    @Transactional
    public void unsubscribe(Long userId, String token) {
        subscriptionPersistencePort.deleteByDevice(userId, new Device(token));
    }
}
