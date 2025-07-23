package online.bottler.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.shared.response.ApiResponse;
import online.bottler.user.application.UserFacade;
import online.bottler.user.application.port.in.CookieUseCase;
import online.bottler.user.application.port.in.KakaoUseCase;
import online.bottler.user.application.response.SignIn;
import online.bottler.user.application.response.SignInResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
@Tag(name = "소셜로그인", description = "소셜로그인 관련 API")
public class OAuthController {
    private final KakaoUseCase kakaoUseCase;
    private final CookieUseCase cookieUseCase;
    private final UserFacade userFacade;

    @Operation(summary = "카카오 소셜로그인", description = "카카오 서버로 요청을 보내 회원가입 및 로그인을 합니다.")
    @GetMapping("/kakao")
    public RedirectView kakaoCode() {
        return new RedirectView(kakaoUseCase.getRequestURL());
    }

    @GetMapping("/kakao/token")
    public ApiResponse<SignInResponse> kakaoSignin(@RequestParam("code") String code, HttpServletResponse response) {
        if (code == null || code.trim().isEmpty()) {
            throw new AdaptorException("인가 코드가 비어있습니다.");
        }
        String accessToken = kakaoUseCase.getKakaoAccessToken(code);
        Map<String, String> userInfo = kakaoUseCase.getUserInfo(accessToken);
        String kakaoId = userInfo.get("kakaoId");
        String nickname = userInfo.get("nickname");

        SignIn signIn = userFacade.kakaoSignin(kakaoId, nickname);

        cookieUseCase.addCookie(response, "refreshToken", signIn.refreshToken());
        return ApiResponse.onSuccess(new SignInResponse(signIn.accessToken()));
    }
}
