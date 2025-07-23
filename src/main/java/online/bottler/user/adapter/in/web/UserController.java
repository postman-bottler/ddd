package online.bottler.user.adapter.in.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import online.bottler.auth.CustomUserDetails;
import online.bottler.shared.exception.AdaptorException;
import online.bottler.shared.response.ApiResponse;
import online.bottler.user.adapter.in.web.request.AuthEmailRequest;
import online.bottler.user.adapter.in.web.request.ChangePasswordRequest;
import online.bottler.user.adapter.in.web.request.CheckDuplicateNicknameRequest;
import online.bottler.user.adapter.in.web.request.CheckPasswordRequest;
import online.bottler.user.adapter.in.web.request.EmailRequest;
import online.bottler.user.adapter.in.web.request.NicknameRequest;
import online.bottler.user.adapter.in.web.request.ProfileImgRequest;
import online.bottler.user.adapter.in.web.request.SignUpRequest;
import online.bottler.user.application.UserFacade;
import online.bottler.user.application.port.in.UserUseCase;
import online.bottler.user.application.response.ExistingUserResponse;
import online.bottler.user.application.response.UserResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "유저", description = "유저 관련 API")
public class UserController {
    private final UserUseCase userUseCase;
    private final UserFacade userFacade;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임으로 회원가입을 합니다.")
    @PostMapping("/signup")
    public ApiResponse<String> signup(@Valid @RequestBody SignUpRequest signUpRequest,
                                      BindingResult bindingResult) {
        validateRequest(bindingResult);
        userFacade.createUser(signUpRequest.toCommand());
        return ApiResponse.onCreateSuccess("회원가입 성공");
    }

    @Operation(summary = "이메일 중복 확인", description = "입력된 이메일이 이미 등록된 이메일인지 확인합니다.")
    @PostMapping("/duplicate-check/email")
    public ApiResponse<String> checkDuplicateEmail(@Valid @RequestBody EmailRequest emailRequest,
                                                   BindingResult bindingResult) {
        validateRequest(bindingResult);
        userUseCase.checkEmail(emailRequest.toCommand());
        return ApiResponse.onSuccess("사용 가능한 이메일입니다.");
    }

    @Operation(summary = "닉네임 중복 확인", description = "입력된 닉네임이 이미 등록된 닉네임인지 확인합니다.")
    @PostMapping("/duplicate-check/nickname")
    public ApiResponse<String> checkDuplicateNickname(
            @Valid @RequestBody CheckDuplicateNicknameRequest checkDuplicateNicknameRequest,
            BindingResult bindingResult) {
        validateRequest(bindingResult);
        userUseCase.checkNickname(checkDuplicateNicknameRequest.toCommand());
        return ApiResponse.onSuccess("사용 가능한 닉네임입니다.");
    }

    @Operation(summary = "유저 탈퇴", description = "(로그인 필요) 비밀번호를 확인 후 탈퇴를 진행합니다.")
    @DeleteMapping
    public ApiResponse<String> deleteUser(@Valid @RequestBody CheckPasswordRequest checkPasswordRequest,
                                          BindingResult bindingResult,
                                          @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        validateRequest(bindingResult);
        userUseCase.deleteUser(checkPasswordRequest.toCommand(), customUserDetails.getEmail());
        return ApiResponse.onDeleteSuccess("성공적으로 탈퇴되었습니다.");
    }

    @Operation(summary = "유저 정보 조회", description = "(로그인 필요) 로그인한 유저의 정보를 조회합니다.")
    @GetMapping
    public ApiResponse<UserResponse> findUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        UserResponse userResponse = userUseCase.findUser(customUserDetails.getEmail());
        return ApiResponse.onSuccess(userResponse);
    }

    @Operation(summary = "닉네임 수정", description = "(로그인 필요) 입력된 닉네임으로 수정합니다.")
    @PatchMapping("/nickname")
    public ApiResponse<String> updateNickname(@Valid @RequestBody NicknameRequest nicknameRequest,
                                              BindingResult bindingResult,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        validateRequest(bindingResult);
        userUseCase.updateNickname(nicknameRequest.toCommand(), customUserDetails.getEmail());
        return ApiResponse.onSuccess("닉네임이 수정되었습니다.");
    }

    @Operation(summary = "비밀번호 수정", description = "(로그인 필요) 입력된 비밀번호를 확인 후 수정합니다.")
    @PatchMapping("/password")
    public ApiResponse<String> updatePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest,
                                              BindingResult bindingResult,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        validateRequest(bindingResult);
        userUseCase.updatePassword(changePasswordRequest.toCommand(),
                customUserDetails.getEmail());
        return ApiResponse.onSuccess("비밀번호가 수정되었습니다.");
    }

    @Operation(summary = "프로필 이미지 수정", description = "(로그인 필요) 입력된 프로필 이미지 URL을 확인 후 수정합니다.")
    @PatchMapping("/profileImg")
    public ApiResponse<String> updateProfileImage(@Valid @RequestBody ProfileImgRequest profileImgRequest,
                                                  BindingResult bindingResult,
                                                  @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        validateRequest(bindingResult);
        userUseCase.updateProfileImage(profileImgRequest.toCommand(), customUserDetails.getEmail());
        return ApiResponse.onSuccess("프로필 이미지가 수정되었습니다.");
    }

    @Operation(summary = "프로필 이미지 생성", description = "프로필 이미지 URL을 DB에 저장합니다. 실제 서비스에서 사용하는 API는 아닙니다!")
    @PostMapping("/profileImg")
    public ApiResponse<String> createProfileImg(@Valid @RequestBody ProfileImgRequest profileImgRequest,
                                                BindingResult bindingResult) {
        validateRequest(bindingResult);
        userUseCase.createProfileImg(profileImgRequest.toCommand());
        return ApiResponse.onCreateSuccess("프로필 이미지 DB 저장 성공");
    }

    @Operation(summary = "닉네임으로 사용자 존재 확인", description = "(로그인 필요) 입력된 닉네임에 해당하는 사용자가 있는지 확인합니다.")
    @GetMapping("/exists")
    public ApiResponse<ExistingUserResponse> findUser(@RequestParam String nickname) {
        ExistingUserResponse existingUserResponse = userUseCase.findExistingUser(nickname);
        return ApiResponse.onSuccess(existingUserResponse);
    }

    @Operation(summary = "이메일 인증 코드 요청", description = "입력된 이메일로 인증 코드 요청 이메일을 보냅니다.")
    @PostMapping("/email/send")
    public ApiResponse<String> sendEmail(@Valid @RequestBody EmailRequest emailRequest,
                                         BindingResult bindingResult) {
        validateRequest(bindingResult);
        userUseCase.sendCodeToEmail(emailRequest.toCommand());
        return ApiResponse.onSuccess("이메일 인증 요청을 성공했습니다.");
    }

    @Operation(summary = "이메일 인증 코드 검사", description = "인증 코드를 검사합니다.")
    @PostMapping("/email/verify")
    public ApiResponse<String> verifyEmail(@Valid @RequestBody AuthEmailRequest authEmailRequest) {
        userUseCase.verifyCode(authEmailRequest.toCommand());
        userUseCase.deleteEmailCode(authEmailRequest.toCommand());
        return ApiResponse.onSuccess("이메일 인증을 성공했습니다.");
    }

    @Operation(summary = "개발자 계정 생성", description = "개발자 계정 생성")
    @PostMapping("/developer")
    public ApiResponse<String> createDeveloper(@Valid @RequestBody SignUpRequest signUpRequest,
                                               BindingResult bindingResult) {
        validateRequest(bindingResult);
        userUseCase.createDeveloper(signUpRequest.toCommand());
        return ApiResponse.onCreateSuccess("개발자 회원가입 성공");
    }

    private void validateRequest(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> {
                switch (error.getField()) {
                    case "email", "password", "existingPassword", "newPassword", "nickname", "imageUrl", "code" -> throw new AdaptorException(error.getDefaultMessage());
                    default -> throw new IllegalArgumentException(
                            bindingResult.getAllErrors().get(0).getDefaultMessage());
                }
            });
        }
    }
}
