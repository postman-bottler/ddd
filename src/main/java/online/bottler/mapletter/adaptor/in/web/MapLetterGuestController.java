package online.bottler.mapletter.adaptor.in.web;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.mapletter.adaptor.in.web.MapLetterProximityController.Coordinates;
import online.bottler.mapletter.application.MapLetterFacade;
import online.bottler.mapletter.application.response.FindNearbyLettersResponse;
import online.bottler.mapletter.application.response.OneLetterResponse;
import online.bottler.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map/guest")
@RequiredArgsConstructor
public class MapLetterGuestController {

    private final MapLetterProximityController mapLetterProximityController;
    private final MapLetterFacade mapLetterFacade;

    @GetMapping()
    @Operation(summary = "로그인 하지 않은 유저 주변 편지 조회", description = "로그인 하지 않은 유저의 반경 500m 내 퍼블릭 편지 조회")
    public ApiResponse<List<FindNearbyLettersResponse>> guestFindNearbyMapLetters(@RequestParam String latitude,
                                                                                  @RequestParam String longitude) {
        Coordinates coordinates = mapLetterProximityController.parseCoordinates(latitude, longitude);
        return ApiResponse.onSuccess(
                mapLetterFacade.guestFindNearByMapLetters(coordinates.latitude(), coordinates.longitude()));
    }

    @GetMapping("/{letterId}")
    @Operation(summary = "비로그인 유저 편지 상세 조회", description = "위경도 필수. 반경 15m 내 편지만 상세조회 가능. 내가 타겟인 편지와 퍼블릭 편지만 조회 가능. 나머지는 오류")
    public ApiResponse<OneLetterResponse> guestFindOneMapLetter(@RequestParam String latitude,
                                                                @RequestParam String longitude,
                                                                @PathVariable Long letterId) {
        Coordinates coordinates = mapLetterProximityController.parseCoordinates(latitude, longitude);
        return ApiResponse.onSuccess(
                mapLetterFacade.guestFindOneMapLetter(letterId, coordinates.latitude(), coordinates.longitude()));
    }
}
