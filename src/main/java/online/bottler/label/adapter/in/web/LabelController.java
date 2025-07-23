package online.bottler.label.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.auth.CustomUserDetails;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.label.adapter.in.web.request.LabelRequest;
import online.bottler.label.application.LabelFacade;
import online.bottler.label.application.port.in.LabelUseCase;
import online.bottler.label.application.response.LabelResponse;
import online.bottler.shared.response.ApiResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/labels")
@Tag(name = "라벨", description = "라벨 관련 API")
public class LabelController {
    private final LabelUseCase labelUseCase;
    private final LabelFacade labelFacade;

    @Operation(summary = "라벨 생성", description = "라벨 이미지 URL을 DB에 저장합니다. 실제 서비스에서 사용하는 API는 아닙니다!")
    @PostMapping
    public ApiResponse<String> createLabel(@RequestParam String labelImageUrl,
                                           @RequestParam(required = false) Integer limitCount) {
        validateLabelImageUrl(labelImageUrl);
        limitCount = validateLimitCount(limitCount);

        labelUseCase.createLabel(labelImageUrl, limitCount);
        return ApiResponse.onCreateSuccess("라벨 추가 성공");
    }

    private void validateLabelImageUrl(String labelImageUrl) {
        if (labelImageUrl == null || labelImageUrl.trim().isEmpty()) {
            throw new AdaptorException("라벨 이미지 URL이 비어 있습니다.");
        }
    }

    private Integer validateLimitCount(Integer limitCount) {
        if (limitCount == null) {
            limitCount = Integer.MAX_VALUE;
        }
        if (limitCount < 0) {
            throw new AdaptorException("선착순으로 라벨을 받을 수 있는 최대 인원 수는 0보다 작을 수 없습니다.");
        }
        return limitCount;
    }

    @Operation(summary = "전체 라벨 조회", description = "(로그인 필요) 모든 라벨을 조회합니다.")
    @GetMapping
    public ApiResponse<List<LabelResponse>> findAllLabels() {
        List<LabelResponse> labelResponse = labelUseCase.findAllLabels();
        return ApiResponse.onSuccess(labelResponse);
    }

    @Operation(summary = "사용자 라벨 조회", description = "(로그인 필요) 로그인한 사용자가 소유한 라벨을 조회합니다.")
    @GetMapping("/user")
    public ApiResponse<List<LabelResponse>> findUserLabels(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<LabelResponse> labelResponse = labelUseCase.findUserLabels(customUserDetails.getUserId());
        return ApiResponse.onSuccess(labelResponse);
    }

    @Operation(summary = "선착순 라벨 뽑기 대상 라벨 조회", description = "선착순 뽑기 대상 라벨을 조회합니다.")
    @GetMapping("/first-come")
    public ApiResponse<List<LabelResponse>> findFirstComeLabels() {
        List<LabelResponse> labelResponse = labelUseCase.findFirstComeLabels();
        return ApiResponse.onSuccess(labelResponse);
    }

    @Operation(summary = "선착순 라벨 뽑기 대상 라벨 변경 예약", description = "선착순 뽑기 대상 라벨로 변경 예약되었습니다.")
    @PatchMapping("/first-come")
    public ApiResponse<String> updateFirstComeLabel(@Valid @RequestBody LabelRequest labelRequest,
                                                    BindingResult bindingResult) {
        validateRequest(bindingResult);
        labelFacade.updateFirstComeLabel(labelRequest.toCommand());
        return ApiResponse.onCreateSuccess("선착순 뽑기 대상 라벨로 변경 예약되었습니다.");
    }

    private void validateRequest(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                switch (error.getField()) {
                    case "labelIds", "scheduledDateTime" -> throw new AdaptorException(error.getDefaultMessage());
                    default -> throw new IllegalArgumentException(
                            bindingResult.getAllErrors().get(0).getDefaultMessage());
                }
            });
        }
    }

    @Operation(summary = "선착순 라벨 뽑기", description = "(로그인 필요) 로그인한 사용자가 뽑기 대상 라벨들 중 하나를 선착순으로 가져갑니다.")
    @PostMapping("/first-come")
    public ApiResponse<LabelResponse> createFirstComeFirstServedLabel(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        LabelResponse labelResponse = labelFacade.createFirstComeFirstServedLabel(customUserDetails.getUserId());
        return ApiResponse.onCreateSuccess(labelResponse);
    }
}
