package online.bottler.mapletter.adaptor.in.web;

import io.swagger.v3.oas.annotations.Operation;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.auth.CustomUserDetails;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.mapletter.application.MapLetterFacade;
import online.bottler.mapletter.application.port.in.MapLetterProximityUseCase;
import online.bottler.mapletter.application.response.FindNearbyLettersResponse;
import online.bottler.mapletter.application.response.OneLetterResponse;
import online.bottler.shared.response.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapLetterProximityController {

    private final MapLetterProximityUseCase mapLetterProximityUseCase;
    private final MapLetterFacade mapLetterFacade;

    @GetMapping("/{letterId}")
    @Operation(summary = "편지 상세 조회", description = "로그인 필수. 위경도 필수. 반경 15m 내 편지만 상세조회 가능. 내가 타겟인 편지와 퍼블릭 편지만 조회 가능. 나머지는 오류")
    public ApiResponse<OneLetterResponse> findOneMapLetter(@RequestParam String latitude,
                                                           @RequestParam String longitude,
                                                           @PathVariable Long letterId,
                                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Coordinates coordinates = parseCoordinates(latitude, longitude);
        Long userId = userDetails.getUserId();
        return ApiResponse.onSuccess(mapLetterFacade.findOneMapLetter(letterId, userId, coordinates.latitude,
                coordinates.longitude));
    }

    @GetMapping
    @Operation(summary = "주변 편지 조회", description = "로그인 필수. 반경 500m 내 퍼블릭 편지, 나에게 타겟으로 온 편지 조회")
    public ApiResponse<List<FindNearbyLettersResponse>> findNearbyMapLetters(@RequestParam String latitude,
                                                                             @RequestParam String longitude,
                                                                             @AuthenticationPrincipal CustomUserDetails userDetails) {

        Coordinates coordinates = parseCoordinates(latitude, longitude);
        Long userId = userDetails.getUserId();
        return ApiResponse.onSuccess(
                mapLetterFacade.findNearByMapLetters(coordinates.latitude, coordinates.longitude, userId));
    }

    record Coordinates(BigDecimal latitude, BigDecimal longitude) {
        Coordinates {
            if (latitude.compareTo(new BigDecimal("-90")) < 0 || latitude.compareTo(new BigDecimal("90")) > 0) {
                throw new AdaptorException("유효하지 않은 위도입니다. 위도는 -90에서 90 사이어야 합니다.");
            }
            if (longitude.compareTo(new BigDecimal("-180")) < 0 || longitude.compareTo(new BigDecimal("180")) > 0) {
                throw new AdaptorException("유효하지 않은 경도입니다. 위도는 -180에서 180 사이어야 합니다.");
            }
        }
    }

    Coordinates parseCoordinates(String latitudeStr, String longitudeStr) {
        try {
            BigDecimal latitude = new BigDecimal(latitudeStr);
            BigDecimal longitude = new BigDecimal(longitudeStr);
            return new Coordinates(latitude, longitude);
        } catch (NumberFormatException e) {
            throw new AdaptorException("위도 또는 경도 형식이 올바르지 않습니다. 숫자 형식으로 입력해주세요.");
        }
    }
}
