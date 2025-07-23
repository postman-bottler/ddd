package online.bottler.recommendation.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.bottler.recommendation.application.service.RecommendService;
import online.bottler.recommendation.application.scheduler.RecommendationScheduler;
import online.bottler.recommendation.application.service.RecommendedLetterService;
import online.bottler.recommendation.presentation.spec.RecommendationApiSpec;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import online.bottler.user.application.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test/recommendations")
@Tag(name = "테스트용")
public class RecommendationController implements RecommendationApiSpec {

    private final RecommendationScheduler recommendationScheduler;
    private final UserService userService;
    private final RecommendService recommendService;
    private final RecommendedLetterService recommendedLetterService;

    @Override
    @PostMapping("/process")
    public ResponseEntity<String> processRecommendation() {
        recommendationScheduler.generateAllUserRecommendationsAsync();
        return ResponseEntity.ok("Recommendation process started for user " + userService.getAllUserIds());
    }

    @Override
    @GetMapping("/result")
    public ResponseEntity<Map<Long, List<Long>>> getRecommendationResult() {
        Map<Long, List<Long>> result = new HashMap<>();
        userService.getAllUserIds().forEach(userId -> result.put(userId, recommendService.fetchActiveRecommendations(userId)));
        return ResponseEntity.ok(result);
    }

    @Override
    @PostMapping("/update")
    public ResponseEntity<String> updateRecommendationsFromTemp() {
        try {
//            userService.getAllUserIds().forEach(recommendService::updateRecommendationsFromTemp);
            return ResponseEntity.ok("추천 키워드 편지 변경 완료");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update recommendations: " + e.getMessage());
        }
    }

    @Override
    @GetMapping("/temp")
    public ResponseEntity<Map<Long, List<Long>>> getRecommendTemp() {
        Map<Long, List<Long>> result = new HashMap<>();
        userService.getAllUserIds().forEach(userId -> result.put(userId, recommendService.fetchTempRecommendations(userId)));
        return ResponseEntity.ok(result);
    }

    @Override
    @GetMapping("/recommended")
    public ResponseEntity<Map<Long, List<Long>>> getRecommendRecommended() {
        Map<Long, List<Long>> result = new HashMap<>();
        userService.getAllUserIds().forEach(userId -> result.put(userId,
                recommendedLetterService.getRecommendedLetterIds(userId)));
        return ResponseEntity.ok(result);
    }
}
