package online.bottler.mapletter.adaptor.in.web;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.bottler.auth.CustomUserDetails;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.mapletter.adaptor.in.web.request.CreateReplyMapLetterRequest;
import online.bottler.mapletter.adaptor.in.web.request.DeleteReplyMapLettersRequest;
import online.bottler.mapletter.application.MapLetterCreateFacade;
import online.bottler.mapletter.application.MapLetterFacade;
import online.bottler.mapletter.application.port.in.MapLetterReplyUseCase;
import online.bottler.mapletter.application.response.CheckReplyMapLetterResponse;
import online.bottler.mapletter.application.response.FindAllReplyMapLettersResponse;
import online.bottler.mapletter.application.response.MapLetterPageResponse;
import online.bottler.mapletter.application.response.OneReplyLetterResponse;
import online.bottler.shared.response.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
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
public class MapLetterReplyController {

    private final MapLetterReplyUseCase mapLetterReplyUseCase;
    private final MapLetterFacade mapLetterFacade;
    private final MapLetterCreateFacade mapLetterCreateFacade;

    @PostMapping("/reply")
    @Operation(summary = "답장 편지 생성", description = "로그인 필수. 지도편지 답장을 생성한다.")
    public ApiResponse<?> createReplyMapLetter(
            @Valid @RequestBody CreateReplyMapLetterRequest createReplyMapLetterRequestDTO,
            BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            throw new AdaptorException(bindingResult.getFieldErrors().get(0).getDefaultMessage());
        }

        Long userId = userDetails.getUserId();
        mapLetterCreateFacade.createReplyMapLetter(createReplyMapLetterRequestDTO.toCommand(), userId);
        return ApiResponse.onCreateSuccess("답장 편지 생성이 성공되었습니다.");
    }

    @GetMapping("/{letterId}/reply")
    @Operation(summary = "특정 편지 답장 전체보기", description = "로그인 필수. 특정 편지에 대한 답장을 조회한다.")
    public ApiResponse<MapLetterPageResponse<FindAllReplyMapLettersResponse>> findAllReplyMapLetter(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "9") int size,
            @PathVariable Long letterId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return ApiResponse.onSuccess(
                MapLetterPageResponse.from(mapLetterReplyUseCase.findAllReplyMapLetters(page, size, letterId, userId)));
    }

    @GetMapping("/reply/{letterId}")
    @Operation(summary = "답장 편지 상세 조회", description = "로그인 필수. 답장 편지를 상세조회한다.")
    public ApiResponse<OneReplyLetterResponse> findOneReplyMapLetter(@PathVariable Long letterId,
                                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return ApiResponse.onSuccess(mapLetterReplyUseCase.findReplyMapLetter(letterId, userId));
    }

    @GetMapping("/reply/check/{letterId}")
    @Operation(summary = "유저가 해당 편지에 답장을 보냈는지 확인합니다.", description = "로그인 필수. 편지 보관할 때 이미 답장이 있으면 에러를 터트리지만 혹시 몰라서 넣었어요.")
    public ApiResponse<CheckReplyMapLetterResponse> checkReplyMapLetter(@PathVariable Long letterId,
                                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        return ApiResponse.onSuccess(mapLetterReplyUseCase.checkReplyMapLetter(letterId, userId));
    }

    @DeleteMapping("/reply")
    @Operation(summary = "답장 편지 삭제", description = "로그인 필수. 답장 편지 삭제. 리스트 형태. 3차 스프린트 기간 삭제 예정")
    public ApiResponse<?> deleteReplyMapLetter(@RequestBody DeleteReplyMapLettersRequest letters,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        mapLetterReplyUseCase.deleteReplyMapLetter(letters.toCommand(), userId);
        return ApiResponse.onDeleteSuccess(letters);
    }
}
