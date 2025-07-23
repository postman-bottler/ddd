package online.bottler.mapletter.adaptor.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.bottler.auth.CustomUserDetails;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.mapletter.application.MapLetterCreateFacade;
import online.bottler.mapletter.application.MapLetterFacade;
import online.bottler.mapletter.application.port.in.MapLetterReplyUseCase;
import online.bottler.mapletter.application.port.in.MapLetterUseCase;
import online.bottler.shared.response.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import online.bottler.mapletter.adaptor.in.web.request.CreatePublicMapLetterRequest;
import online.bottler.mapletter.adaptor.in.web.request.CreateTargetMapLetterRequest;
import online.bottler.mapletter.adaptor.in.web.request.DeleteMapLettersRequest;
import online.bottler.mapletter.application.response.MapLetterPageResponse;

@RestController
@RequestMapping("/map")
@RequiredArgsConstructor
@Tag(name = "지도 편지 컨트롤러")
public class MapLetterController {

    private final MapLetterUseCase mapLetterUseCase;
    private final MapLetterReplyUseCase mapLetterReplyUseCase;
    private final MapLetterFacade mapLetterFacade;
    private final MapLetterCreateFacade mapLetterCreateFacade;

    @PostMapping("/public")
    @Operation(summary = "지도 퍼블릭 편지 생성", description = "로그인 필수. 제목 없으면 무제로 넣어주세요.")
    public ApiResponse<?> createMapLetter(
            @Valid @RequestBody CreatePublicMapLetterRequest createPublicMapLetterRequest,
            BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            throw new AdaptorException(bindingResult.getFieldErrors().get(0).getDefaultMessage());
        }

        Long userId = userDetails.getUserId();
        mapLetterUseCase.createPublicMapLetter(
                createPublicMapLetterRequest.toCommand(), userId);
        return ApiResponse.onCreateSuccess("지도 편지 생성이 성공되었습니다.");
    }

    @PostMapping("/target")
    @Operation(summary = "지도 타겟 편지 생성", description = "로그인 필수. 제목 없으면 무제로 넣어주세요.")
    public ApiResponse<?> createTargetLetter(
            @Valid @RequestBody CreateTargetMapLetterRequest createTargetMapLetterRequest,
            BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            throw new AdaptorException(bindingResult.getFieldErrors().get(0).getDefaultMessage());
        }

        Long userId = userDetails.getUserId();
        mapLetterCreateFacade.createTargetMapLetter(createTargetMapLetterRequest.toCommand(), userId);
        return ApiResponse.onCreateSuccess("타겟 편지 생성이 성공되었습니다.");
    }

    @GetMapping("/saved")
    public ApiResponse<?> savedLetters(@RequestParam String type, @AuthenticationPrincipal CustomUserDetails user,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "9") int size) {
        Long userId = user.getUserId();
        return switch (type) {
            case "sent-all" -> //보낸 편지 전체 조회(지도편지, 답장 편지)
                    ApiResponse.onSuccess(
                            MapLetterPageResponse.from(mapLetterFacade.findSentMapLetters(page, size, userId)));
            case "sent-reply" -> //보낸 답장 편지 전체 조회
                    ApiResponse.onSuccess(
                            MapLetterPageResponse.from(
                                    mapLetterReplyUseCase.findAllSentReplyMapLetters(page, size, userId)));
            case "sent-map" -> //보낸 지도 편지 전체 조회
                    ApiResponse.onSuccess(
                            MapLetterPageResponse.from(mapLetterFacade.findAllSentMapLetters(page, size, userId)));
            case "received-all" -> //받은 편지 전체 조회(타겟 편지, 답장 편지)
                    ApiResponse.onSuccess(
                            MapLetterPageResponse.from(mapLetterFacade.findReceivedMapLetters(page, size, userId)));
            case "received-reply" -> //받은 답장 편지 전체 조회
                    ApiResponse.onSuccess(
                            MapLetterPageResponse.from(
                                    mapLetterReplyUseCase.findAllReceivedReplyMapLetters(page, size, userId)));
            case "received-map" -> //받은 타겟 편지 전체 조회
                    ApiResponse.onSuccess(
                            MapLetterPageResponse.from(mapLetterFacade.findAllReceivedLetters(page, size, userId)));
            default -> throw new AdaptorException("잘못된 저장된 지도 편지 조회 타입입니다.");
        };
    }

    @DeleteMapping()
    @Operation(summary = "편지 삭제", description = "로그인 필수. 지도편지, 답장편지 구분해서 보내주세요. 리스트 형태로 1개 ~ n개까지 삭제 가능")
    public ApiResponse<?> deleteMapLetter(@RequestBody DeleteMapLettersRequest deleteMapLettersRequest,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        mapLetterUseCase.deleteSentMapLetters(deleteMapLettersRequest.toCommand(), userId);
        return ApiResponse.onDeleteSuccess(deleteMapLettersRequest);
    }

    @DeleteMapping("/all")
    @Operation(summary = "편지 전체 삭제", description = "로그인 필수. 타입(SENT, SENT-MAP, SENT-REPLY, RECEIVED, RECEIVED-MAP, RECEIVED-REPLY) 나눠서 보내주세요")
    public ApiResponse<?> deleteAllMapLetter(@RequestParam String type,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        mapLetterUseCase.deleteAllMapLetters(type, userId);
        return ApiResponse.onDeleteSuccess(type);
    }

    @DeleteMapping("/sent")
    @Operation(summary = "보낸 편지 삭제", description = "로그인 필수. 지도편지, 답장편지 구분해서 보내주세요. 리스트 형태로 1개 ~ n개까지 삭제 가능")
    public ApiResponse<?> deleteSentMapLetter(@RequestBody DeleteMapLettersRequest deleteMapLettersRequest,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        mapLetterUseCase.deleteSentMapLetters(deleteMapLettersRequest.toCommand(), userId);
        return ApiResponse.onDeleteSuccess(deleteMapLettersRequest);
    }

    @DeleteMapping("/received")
    @Operation(summary = "받은 편지 삭제", description = "로그인 필수. 지도편지, 답장편지 구분해서 보내주세요. 리스트 형태로 1개 ~ n개까지 삭제 가능. 받은 편지 자체를 삭제하는게 아니라 받은 사람의 마이페이지에서만 삭제")
    public ApiResponse<?> deleteReceivedMapLetter(@RequestBody DeleteMapLettersRequest deleteMapLettersRequest,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        mapLetterUseCase.deleteReceivedMapLetters(deleteMapLettersRequest.toCommand(), userId);
        return ApiResponse.onDeleteSuccess(deleteMapLettersRequest);
    }
}
