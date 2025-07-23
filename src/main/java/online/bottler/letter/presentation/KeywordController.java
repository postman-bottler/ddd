package online.bottler.letter.presentation;

import lombok.RequiredArgsConstructor;
import online.bottler.letter.application.service.KeywordService;
import online.bottler.letter.presentation.response.KeywordDto.KeywordResponse;
import online.bottler.letter.presentation.spec.KeywordApiSpec;
import online.bottler.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/keywords")
public class KeywordController implements KeywordApiSpec {

    private final KeywordService keywordService;

    @Override
    @GetMapping
    public ApiResponse<KeywordResponse> getKeywordList() {
        return ApiResponse.onSuccess(
                KeywordResponse.from(keywordService.getKeywords())
        );
    }
}
