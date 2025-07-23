package online.bottler.mapletter.adaptor.in.web;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import online.bottler.auth.CustomUserDetails;
import online.bottler.mapletter.adaptor.in.web.request.DeleteArchivedLettersRequest;
import online.bottler.mapletter.application.MapLetterFacade;
import online.bottler.mapletter.application.port.in.MapLetterArchiveUseCase;
import online.bottler.mapletter.application.response.FindAllArchiveLettersResponse;
import online.bottler.mapletter.application.response.MapLetterPageResponse;
import online.bottler.mapletter.application.response.OneLetterResponse;
import online.bottler.shared.response.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapLetterArchiveController {

    private final MapLetterArchiveUseCase mapLetterArchiveUseCase;
    private final MapLetterFacade mapLetterFacade;

    @PostMapping("/{letterId}")
    @Operation(summary = "편지 보관", description = "로그인 필수. 퍼블릭 편지를 보관한다.")
    public ApiResponse<?> mapLetterArchive(@PathVariable Long letterId,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        mapLetterArchiveUseCase.mapLetterArchive(letterId, userId);
        return ApiResponse.onCreateSuccess("편지 저장이 성공되었습니다.");
    }

    @GetMapping("/archived")
    @Operation(summary = "보관한 편지 조회", description = "로그인 필수. 보관한 편지를 조회한다.")
    public ApiResponse<MapLetterPageResponse<FindAllArchiveLettersResponse>> findArchiveLetters(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return ApiResponse.onSuccess(
                MapLetterPageResponse.from(mapLetterArchiveUseCase.findArchiveLetters(page, size, userId)));
    }

    @DeleteMapping("/archived")
    @Operation(summary = "편지 보관 취소(삭제).", description = "로그인 필수. 편지를 보관함에서 삭제한다.(리스트로 1~n개 까지의 편지를 한 번에 삭제 한다.")
    public ApiResponse<?> deleteArchiveLetter(@RequestBody DeleteArchivedLettersRequest deleteArchivedLettersRequest
            , @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        mapLetterArchiveUseCase.deleteArchivedLetter(
                deleteArchivedLettersRequest.toCommand(), userId);
        return ApiResponse.onDeleteSuccess(deleteArchivedLettersRequest);
    }

    @GetMapping("/archive/{letterId}")
    @Operation(summary = "마이페이지에 있는 편지 상세 조회", description = "로그인 필수. 기존 편지 상세 조회는 15m 내에 있는 편지만 조회가 가능해서 거리 제한 없이 편지 조회가 가능한 api 별도 생성")
    public ApiResponse<OneLetterResponse> findArchiveOneLetter(@PathVariable Long letterId,
                                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return ApiResponse.onSuccess(mapLetterFacade.findArchiveOneLetter(letterId, userId));
    }
}
