package online.bottler.letter.presentation.spec;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import online.bottler.letter.presentation.response.LetterBoxDto.LetterBoxSummaryResponse;
import online.bottler.letter.presentation.response.PageResponse;
import online.bottler.letter.presentation.request.CommonPageRequest;
import online.bottler.letter.presentation.request.LetterDeleteRequest;
import online.bottler.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface LetterBoxApiSpec {
    @Operation(
            summary = "보관된 편지 조회",
            description =
                    """
                    페이지네이션을 사용하여 보관된 편지의 제목, 라벨이미지, 작성날짜 정보를 조회합니다.\
                    Page Default: page(1) size(9) sort(createAt)\
                    Param boxType(SEND RECEIVE ALL)
                    """
    )
    ApiResponse<PageResponse<LetterBoxSummaryResponse>> getLetters(
            @RequestParam(value = "boxType", required = false) String boxType,
            @Valid CommonPageRequest commonPageRequest
    );

    @Operation(summary = "보관된 편지 삭제", description = "편지ID, 편지타입(LETTER, REPLY_LETTER), 송수신 타입(SEND, RECEIVE)을 기반으로 키워드 편지를 삭제합니다.")
    ApiResponse<String> deleteLetterInBox(@RequestBody @Valid List<LetterDeleteRequest> letterDeleteRequests);

    @Operation(summary = "보관된 편지 삭제", description = "송수신 타입(SEND, RECEIVE)을 기반으로 키워드 편지를 삭제합니다.")
    ApiResponse<String> deleteLettersInBox(
            @RequestParam(value = "boxType", required = false) String boxType
    );
}
