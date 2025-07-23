package online.bottler.recommendation.presentation.spec;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface RecommendationApiSpec {
    @Operation(summary = "키워드 편지 추천 요청", description = "현재 3개가 추천됩니다")
    ResponseEntity<String> processRecommendation();

    @Operation(summary = "추천 된 편지 조회 요청", description = "현재 추천된 편지의 id 들을 반환합니다")
    ResponseEntity<Map<Long, List<Long>>> getRecommendationResult();

    @Operation(summary = "업데이트된 추천 편지 변경 요청", description = "기존 추천된 편지 중 가장 오래된 편지를 밀어내고 추천 후보 중 조건에 맞는 편지 id를 등록해줍니다.")
    ResponseEntity<String> updateRecommendationsFromTemp();

    @Operation(summary = "추천될 키워드 편지 id 정보들 조회 요청", description = "사용자에게 제공된 추천 편지가 아닌 추천될 편지의 아이디 목록입니다."
            + "\n 추천할 때 편지가 삭제되어 있을 가능성이 있기 떄문에 검증 후 순차적으로 이중 하나를 사용자에게 추천합니다.")
    ResponseEntity<Map<Long, List<Long>>> getRecommendTemp();

    ResponseEntity<Map<Long, List<Long>>> getRecommendRecommended();
}
