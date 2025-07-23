package online.bottler.letter.presentation.spec;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import online.bottler.letter.presentation.request.CreateLetterRequest;
import online.bottler.letter.presentation.request.DeleteLetterRequest;
import online.bottler.letter.presentation.response.LetterDto;
import online.bottler.letter.presentation.response.LetterDto.FrequentKeywordsResponse;
import online.bottler.letter.presentation.response.LetterDto.LetterRecommendSummaryResponse;
import online.bottler.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "키워드 편지", description = "키워드 편지 API")
public interface LetterApiSpec {

    @Operation(summary = "키워드 편지 생성", description = "새로운 키워드 편지를 생성합니다.")
    ApiResponse<LetterDto.LetterResponse> createLetter(CreateLetterRequest request);

    @Operation(summary = "키워드 편지 상세 조회", description = "편지 ID로 키워드 편지의 상세 정보를 조회합니다.")
    ApiResponse<LetterDto.LetterDetailResponse> getLetterDetail(@PathVariable Long letterId);

    @Operation(summary = "추천 키워드 편지 조회", description = "사용자에게 현재 추천된 키워드 편지들의 정보를 제공합니다.")
    ApiResponse<List<LetterRecommendSummaryResponse>> getRecommendLetters();

    @Operation(summary = "키워드 편지 삭제", description = "키워드 편지ID, BoxType 송수신(SEND, RECEIVE)을 기반으로 키워드 편지를 삭제합니다.")
    ApiResponse<String> deleteLetter(@RequestBody @Valid DeleteLetterRequest deleteLetterRequest);

    @Operation(summary = "사용자의 자주 쓰는 키워드 조회", description = "현재 사용자의 자주 쓰는 키워드를 조회합니다")
    ApiResponse<FrequentKeywordsResponse> getMostFrequentKeywords();
}
