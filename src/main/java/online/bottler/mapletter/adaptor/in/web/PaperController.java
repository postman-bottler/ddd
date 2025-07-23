package online.bottler.mapletter.adaptor.in.web;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import online.bottler.mapletter.application.port.in.PaperUseCase;
import online.bottler.mapletter.application.response.PaperResponse;
import online.bottler.shared.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paper")
@RequiredArgsConstructor
public class PaperController {
    private final PaperUseCase paperUseCase;

    @GetMapping
    @Operation(summary = "", description = "")
    public ApiResponse<List<PaperResponse>> findPapers() {
        return ApiResponse.onSuccess(paperUseCase.findPapers());
    }
}
