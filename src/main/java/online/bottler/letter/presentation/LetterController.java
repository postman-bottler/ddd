package online.bottler.letter.presentation;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.letter.application.facade.LetterFacade;
import online.bottler.letter.presentation.request.CreateLetterRequest;
import online.bottler.letter.presentation.request.DeleteLetterRequest;
import online.bottler.letter.presentation.response.LetterDto.FrequentKeywordsResponse;
import online.bottler.letter.presentation.response.LetterDto.LetterDetailResponse;
import online.bottler.letter.presentation.response.LetterDto.LetterRecommendSummaryResponse;
import online.bottler.letter.presentation.response.LetterDto.LetterResponse;
import online.bottler.letter.presentation.spec.LetterApiSpec;
import online.bottler.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/letters")
@RequiredArgsConstructor
public class LetterController implements LetterApiSpec {

    private final LetterFacade letterFacade;

    @Override
    @PostMapping
    public ApiResponse<LetterResponse> createLetter(@RequestBody @Valid CreateLetterRequest createLetterRequest) {
        return ApiResponse.onCreateSuccess(
                LetterResponse.from(letterFacade.writeLetter(createLetterRequest.toCommand()))
        );
    }

    @Override
    @GetMapping("/detail/{letterId}")
    public ApiResponse<LetterDetailResponse> getLetterDetail(@PathVariable Long letterId) {
        return ApiResponse.onSuccess(
                LetterDetailResponse.from(letterFacade.getLetterDetail(letterId))
        );
    }

    @Override
    @GetMapping("/recommend")
    public ApiResponse<List<LetterRecommendSummaryResponse>> getRecommendLetters() {
        return ApiResponse.onSuccess(
                LetterRecommendSummaryResponse.fromList(letterFacade.getRecommendedLetter())
        );
    }

    @Override
    @DeleteMapping
    public ApiResponse<String> deleteLetter(
            @RequestBody @Valid DeleteLetterRequest deleteLetterRequest
    ) {
        letterFacade.delete(deleteLetterRequest.toCommand());
        return ApiResponse.onSuccess("키워드 편지를 삭제했습니다.");
    }

    @Override
    @GetMapping("/frequent")
    public ApiResponse<FrequentKeywordsResponse> getMostFrequentKeywords() {
        return ApiResponse.onSuccess(
                FrequentKeywordsResponse.from(letterFacade.getMostFrequentKeywords())
        );
    }
}
