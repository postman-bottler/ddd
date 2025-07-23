package online.bottler.complaint.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import online.bottler.auth.CustomUserDetails;
import online.bottler.complaint.application.ComplaintFacade;
import online.bottler.complaint.application.ComplaintResponse;
import online.bottler.shared.response.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static online.bottler.complaint.domain.ComplaintType.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "신고 API", description = "로그인 사용자만 가능")
public class ComplaintController {
    private final ComplaintFacade complaintFacade;

    @Operation(summary = "키워드 편지 신고", description = "신고하는 편지 ID와 신고 사유를 등록합니다.")
    @PostMapping("/letters/{letterId}/complaint")
    public ApiResponse<ComplaintResponse> complainKeywordLetter(@PathVariable Long letterId,
                                                                @RequestBody ComplaintRequest complaintRequest,
                                                                @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ComplaintResponse response = complaintFacade.complain(complaintRequest.toCommand(KEYWORD_LETTER, letterId, customUserDetails.getUserId()));
        return ApiResponse.onCreateSuccess(response);
    }

    @Operation(summary = "지도 편지 신고", description = "신고하는 편지 ID와 신고 사유를 등록합니다.")
    @PostMapping("/map/{letterId}/complaint")
    public ApiResponse<ComplaintResponse> complainMapLetter(@PathVariable Long letterId,
                                                            @RequestBody ComplaintRequest complaintRequest,
                                                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ComplaintResponse response = complaintFacade.complain(complaintRequest.toCommand(MAP_LETTER, letterId, customUserDetails.getUserId()));
        return ApiResponse.onCreateSuccess(response);
    }

    @Operation(summary = "지도 답장 편지 신고", description = "신고하는 편지 ID와 신고 사유를 등록합니다.")
    @PostMapping("/map/reply/{replyLetterId}/complaint")
    public ApiResponse<ComplaintResponse> complainMapReplyLetter(@PathVariable Long replyLetterId,
                                                                 @RequestBody ComplaintRequest complaintRequest,
                                                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ComplaintResponse response = complaintFacade.complain(complaintRequest.toCommand(MAP_REPLY_LETTER, replyLetterId, customUserDetails.getUserId()));
        return ApiResponse.onCreateSuccess(response);
    }

    @Operation(summary = "키워드 답장 편지 신고", description = "신고하는 편지 ID와 신고 사유를 등록합니다.")
    @PostMapping("/letters/reply/{replyLetterId}/complaint")
    public ApiResponse<ComplaintResponse> complainKeywordReplyLetter(@PathVariable Long replyLetterId,
                                                                     @RequestBody ComplaintRequest complaintRequest,
                                                                     @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ComplaintResponse response = complaintFacade.complain(complaintRequest.toCommand(KEYWORD_REPLY_LETTER, replyLetterId, customUserDetails.getUserId()));
        return ApiResponse.onCreateSuccess(response);
    }
}
