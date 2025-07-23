package online.bottler.letter.presentation;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.bottler.letter.application.facade.LetterBoxFacade;
import online.bottler.letter.presentation.response.LetterBoxDto.LetterBoxSummaryResponse;
import online.bottler.letter.presentation.response.PageResponse;
import online.bottler.letter.presentation.request.CommonPageRequest;
import online.bottler.letter.presentation.request.LetterDeleteRequest;
import online.bottler.letter.presentation.spec.LetterBoxApiSpec;
import online.bottler.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/letterbox")
@RequiredArgsConstructor
@Tag(name = "Letter Box", description = "보관된(saved) 편지 관리 API")
public class LetterBoxController implements LetterBoxApiSpec {

    private final LetterBoxFacade letterBoxFacade;

    @Override
    @GetMapping
    public ApiResponse<PageResponse<LetterBoxSummaryResponse>> getLetters(
            @RequestParam(value = "boxType", required = false) String boxType,
            @Valid CommonPageRequest commonPageRequest
    ) {

        return ApiResponse.onSuccess(
                PageResponse.from(
                        letterBoxFacade.getLetterBoxSummaries(boxType, commonPageRequest.toCommand())
                                .map(LetterBoxSummaryResponse::from)
                )
        );
    }

    @Override
    @DeleteMapping
    public ApiResponse<String> deleteLetterInBox(@RequestBody @Valid List<LetterDeleteRequest> letterDeleteRequests) {
        letterBoxFacade.deleteLetters(LetterDeleteRequest.toCommandList(letterDeleteRequests));
        return ApiResponse.onSuccess("보관된 편지를 삭제했습니다.");
    }

    @Override
    @DeleteMapping("/all")
    public ApiResponse<String> deleteLettersInBox(
            @RequestParam(value = "boxType", required = false) String boxType
    ) {
        letterBoxFacade.deleteAllLetters(boxType);
        return ApiResponse.onSuccess("보관된 편지를 모두 삭제했습니다");
    }
}
