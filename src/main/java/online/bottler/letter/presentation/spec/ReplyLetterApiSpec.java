package online.bottler.letter.presentation.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import online.bottler.letter.presentation.response.PageResponse;
import online.bottler.letter.presentation.request.CommonPageRequest;
import online.bottler.letter.presentation.request.ReplyLetterDeleteRequest;
import online.bottler.letter.presentation.request.ReplyLetterRequest;
import online.bottler.letter.presentation.response.ReplyLetterDto.ReplyLetterDetailResponse;
import online.bottler.letter.presentation.response.ReplyLetterDto.ReplyLetterResponse;
import online.bottler.letter.presentation.response.ReplyLetterDto.ReplyLetterSummaryResponse;
import online.bottler.shared.response.ApiResponse;

@Tag(name = "Reply Letters", description = "키워드 편지 API")
public interface ReplyLetterApiSpec {

    @Operation(summary = "키워드 편지 생성", description = "지정된 편지 ID에 대한 답장을 생성합니다.")
    ApiResponse<ReplyLetterResponse> createReplyLetter(Long letterId, ReplyLetterRequest replyLetterRequest);

    @Operation(summary = "특정 키워드 편지에 대한 답장 목록 조회", description = "지정된 편지 ID에 대한 답장들의 제목, 라벨이미지, 작성날짜를 페이지네이션 형태로 반환합니다."
            + "\nPage Default: page(1) size(9) sort(createAt)")
    ApiResponse<PageResponse<ReplyLetterSummaryResponse>> getRepliesForLetter(
            Long letterId,
            CommonPageRequest commonPageRequest
    );

    @Operation(summary = "답장 편지 상세 조회", description = "지정된 답장 편지의 ID에 대한 상세 정보를 반환합니다.")
    ApiResponse<ReplyLetterDetailResponse> getReplyLetter(Long replyLetterId);

    @Operation(summary = "답장 편지 삭제", description = "답장 편지ID, 송수신 타입(SEND, RECEIVE)을 기반으로 답장 편지를 삭제합니다.")
    ApiResponse<String> deleteReplyLetter(ReplyLetterDeleteRequest replyLetterDeleteRequest);
}
