package online.bottler.recommendation.presentation;

import lombok.RequiredArgsConstructor;
import online.bottler.recommendation.application.facade.UserKeywordFacade;
import online.bottler.recommendation.presentation.dto.UserKeywordDto.UserKeywordResponse;
import online.bottler.recommendation.presentation.spec.UserKeywordApiSpec;
import online.bottler.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userkeyword")
public class UserKeywordController implements UserKeywordApiSpec {

    private final UserKeywordFacade userKeywordFacade;

    @Override
    @PostMapping
    public ApiResponse<String> createKeywords(@RequestBody UserKeywordRequest userKeywordRequest) {
        userKeywordFacade.create(userKeywordRequest.toCommand());
        return ApiResponse.onSuccess("사용자 키워드를 생성하였습니다.");
    }

    @Override
    @GetMapping
    public ApiResponse<UserKeywordResponse> getKeywords() {
        return ApiResponse.onSuccess(UserKeywordResponse.from(userKeywordFacade.getKeywords()));
    }
}
