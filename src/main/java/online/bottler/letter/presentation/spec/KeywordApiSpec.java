package online.bottler.letter.presentation.spec;

import io.swagger.v3.oas.annotations.Operation;
import online.bottler.letter.presentation.response.KeywordDto.KeywordResponse;
import online.bottler.shared.response.ApiResponse;

public interface KeywordApiSpec {
    @Operation(summary = "전체 키워드 조회", description = "카테고리별로 등록된 키워드 목록을 조회합니다.")
    ApiResponse<KeywordResponse> getKeywordList();
}
