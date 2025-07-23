package online.bottler.reply.adaptor.in.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.auth.CustomUserDetails;
import online.bottler.reply.application.port.in.ReplyUseCase;
import online.bottler.shared.response.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import online.bottler.reply.application.response.ReplyResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reply")
@Tag(name = "최근 답장 3개 보여주는 컨트롤러")
public class ReplyController {
    private final ReplyUseCase replyUseCase;

    @GetMapping
    public ApiResponse<List<ReplyResponse>> findRecentReplyLetters(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ApiResponse.onSuccess(replyUseCase.findRecentReplyLetters(customUserDetails.getUserId()));
    }
}
