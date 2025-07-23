package online.bottler.notification.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.bottler.auth.CustomUserDetails;
import online.bottler.notification.application.response.NotificationResponse;
import online.bottler.notification.application.response.SubscriptionResponse;
import online.bottler.notification.application.response.UnreadNotificationResponse;
import online.bottler.notification.application.port.NotificationUseCase;
import online.bottler.notification.application.port.SubscriptionUseCase;
import online.bottler.notification.domain.NotificationType;
import online.bottler.shared.response.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Tag(name = "알림 API", description = "로그인 사용자만 가능")
public class NotificationController {
    private final NotificationUseCase notificationUseCase;
    private final SubscriptionUseCase subscriptionUseCase;

    @Operation(summary = "알림 생성",
            description = "알림 유형, 알림 대상은 필수, 편지 관련 알림은 편지 ID와 라벨 이미지를 등록합니다.")
    @PostMapping
    public ApiResponse<NotificationResponse> create(
            @Valid @RequestBody NotificationRequest notificationRequest) {
        NotificationResponse response = notificationUseCase.sendNotification(
                NotificationType.from(notificationRequest.notificationType()),
                notificationRequest.receiver(),
                notificationRequest.letterId(), notificationRequest.label());
        return ApiResponse.onCreateSuccess(response);
    }

    @Operation(summary = "알림 조회", description = "사용자의 알림을 조회합니다.")
    @PatchMapping
    public ApiResponse<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<NotificationResponse> userNotifications = notificationUseCase.getUserNotifications(
                customUserDetails.getUserId());
        return ApiResponse.onSuccess(userNotifications);
    }

    @Operation(summary = "읽지 않은 알림 개수 조회", description = "사용자의 안읽은 알림 개수를 조회합니다.")
    @GetMapping("/unread")
    public ApiResponse<UnreadNotificationResponse> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UnreadNotificationResponse unreadNotificationCount = notificationUseCase.getUnreadNotificationCount(
                customUserDetails.getUserId());
        return ApiResponse.onSuccess(unreadNotificationCount);
    }

    @Operation(summary = "알림 허용", description = "사용자의 기기 토큰을 등록합니다.")
    @PostMapping("/subscribe")
    public ApiResponse<SubscriptionResponse> subscribe(@RequestBody SubscriptionRequest subscriptionRequest,
                                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        SubscriptionResponse response = subscriptionUseCase.subscribe(
                customUserDetails.getUserId(),
                subscriptionRequest.token());
        return ApiResponse.onCreateSuccess(response);
    }

    @Operation(summary = "전체 알림 비허용", description = "사용자의 알림 기기를 모두 삭제합니다.")
    @DeleteMapping("/subscribe/all")
    public ApiResponse<String> unsubscribeAll(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        subscriptionUseCase.unsubscribeAll(customUserDetails.getUserId());
        return ApiResponse.onDeleteSuccess("삭제 성공");
    }

    @Operation(summary = "기기 알림 비허용", description = "특정 토큰을 삭제합니다.")
    @DeleteMapping("/subscribe")
    public ApiResponse<String> unsubscribe(@RequestBody UnsubscriptionRequest unsubscriptionRequest,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        subscriptionUseCase.unsubscribe(customUserDetails.getUserId(), unsubscriptionRequest.token());
        return ApiResponse.onDeleteSuccess("삭제 성공");
    }
}
