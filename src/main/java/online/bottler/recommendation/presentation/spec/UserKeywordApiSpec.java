package online.bottler.recommendation.presentation.spec;

import io.swagger.v3.oas.annotations.Operation;
import online.bottler.recommendation.presentation.UserKeywordRequest;
import online.bottler.recommendation.presentation.dto.UserKeywordDto.UserKeywordResponse;
import online.bottler.shared.response.ApiResponse;

public interface UserKeywordApiSpec {
    @Operation(summary = "유저 키워드 등록", description = "유저가 설정한 키워드로 변경합니다.")
    ApiResponse<String> createKeywords(UserKeywordRequest userKeywordRequest);

    @Operation(summary = "유저 키워드 목록 조회", description = "유저가 설정한 키워드 목록을 조회합니다.")
    ApiResponse<UserKeywordResponse> getKeywords();
}
