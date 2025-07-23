package online.bottler.letter.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.bottler.letter.application.facade.ReplyLetterFacade;
import online.bottler.letter.presentation.response.PageResponse;
import online.bottler.letter.presentation.request.CommonPageRequest;
import online.bottler.letter.presentation.request.ReplyLetterDeleteRequest;
import online.bottler.letter.presentation.request.ReplyLetterRequest;
import online.bottler.letter.presentation.response.ReplyLetterDto.ReplyLetterDetailResponse;
import online.bottler.letter.presentation.response.ReplyLetterDto.ReplyLetterResponse;
import online.bottler.letter.presentation.response.ReplyLetterDto.ReplyLetterSummaryResponse;
import online.bottler.letter.presentation.spec.ReplyLetterApiSpec;
import online.bottler.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/letters/replies")
@RequiredArgsConstructor
public class ReplyLetterController implements ReplyLetterApiSpec {

    private final ReplyLetterFacade replyLetterFacade;

    @Override
    @PostMapping("/{letterId}")
    public ApiResponse<ReplyLetterResponse> createReplyLetter(
            @PathVariable Long letterId,
            @RequestBody ReplyLetterRequest replyLetterRequest
    ) {
        return ApiResponse.onCreateSuccess(
                ReplyLetterResponse.from(replyLetterFacade.write(replyLetterRequest.toCommand(letterId)))
        );
    }

    @Override
    @GetMapping("/{letterId}")
    public ApiResponse<PageResponse<ReplyLetterSummaryResponse>> getRepliesForLetter(
            @PathVariable Long letterId,
            @Valid CommonPageRequest commonPageRequest
    ) {
        return ApiResponse.onSuccess(
                PageResponse.from(
                        replyLetterFacade.getSummaries(letterId, commonPageRequest.toCommand())
                                .map(ReplyLetterSummaryResponse::from)
                )
        );
    }

    @Override
    @GetMapping("/detail/{replyLetterId}")
    public ApiResponse<ReplyLetterDetailResponse> getReplyLetter(@PathVariable Long replyLetterId) {
        return ApiResponse.onSuccess(
                ReplyLetterDetailResponse.from(replyLetterFacade.getDetail(replyLetterId))
        );
    }

    @Override
    @DeleteMapping
    public ApiResponse<String> deleteReplyLetter(@RequestBody @Valid ReplyLetterDeleteRequest replyLetterDeleteRequest) {
        replyLetterFacade.delete(replyLetterDeleteRequest.toCommand());
        return ApiResponse.onSuccess("답장 편지가 성공적으로 삭제되었습니다.");
    }
}
