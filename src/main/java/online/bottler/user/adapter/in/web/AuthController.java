package online.bottler.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.shared.response.ApiResponse;
import online.bottler.user.adapter.in.web.request.SignInRequest;
import online.bottler.user.application.port.in.CookieUseCase;
import online.bottler.user.application.port.in.UserUseCase;
import online.bottler.user.application.response.AccessTokenResponse;
import online.bottler.user.application.response.SignIn;
import online.bottler.user.application.response.SignInResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "유저", description = "유저 관련 API")
public class AuthController {
    private final UserUseCase userUseCase;
    private final CookieUseCase cookieUseCase;

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/signin")
    public ApiResponse<SignInResponse> signin(
            @Valid @RequestBody SignInRequest signInRequest,
            BindingResult bindingResult,
            HttpServletResponse response) {
        validateRequest(bindingResult);
        SignIn signIn = userUseCase.signin(signInRequest.toCommand());
        cookieUseCase.addCookie(response, "refreshToken", signIn.refreshToken());
        return ApiResponse.onSuccess(new SignInResponse(signIn.accessToken()));
    }

    @Operation(summary = "리프레시 토큰 유효성 검사", description = "리프레시 토큰 유효성 검사 성공 시 새로운 액세스 토큰 발급합니다.")
    @PostMapping("/validate")
    public ApiResponse<AccessTokenResponse> validateRefreshToken(HttpServletRequest request) {
        String refreshToken = getCookieValue(request);
        if (refreshToken == null) {
            throw new AdaptorException("Refresh token이 존재하지 않습니다.");
        }

        AccessTokenResponse newAccessToken = userUseCase.validateRefreshToken(refreshToken);
        return ApiResponse.onSuccess(newAccessToken);
    }

    private String getCookieValue(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void validateRequest(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                switch (error.getField()) {
                    case "email", "password" -> throw new AdaptorException(error.getDefaultMessage());
                    default -> throw new IllegalArgumentException(
                            bindingResult.getAllErrors().get(0).getDefaultMessage());
                }
            });
        }
    }
}
