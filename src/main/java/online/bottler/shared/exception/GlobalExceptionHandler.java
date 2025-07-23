package online.bottler.shared.exception;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import online.bottler.shared.response.ApiResponse;
import online.bottler.shared.response.code.ErrorStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CommonForbiddenException.class)
    public ApiResponse<?> handleCommonForbiddenException(CommonForbiddenException e) {
        log.error(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        return ApiResponse.onFailure(ErrorStatus.FORBIDDEN.getCode(), e.getMessage(), null);
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ApiResponse<?> handleInvalidLoginException(InvalidLoginException e) {
        log.error(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        return ApiResponse.onFailure(ErrorStatus.INVALID_LOGIN.getCode(), e.getMessage(), null);
    }

    @ExceptionHandler(DomainException.class)
    public ApiResponse<?> handleDomainException(DomainException e) {
        log.error(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        return ApiResponse.onFailure(ErrorStatus.COMMON_BAD_REQUEST.getCode(), e.getMessage(), null);
    }

    @ExceptionHandler(ApplicationException.class)
    public ApiResponse<?> handleApplicationException(ApplicationException e) {
        log.error(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        return ApiResponse.onFailure(ErrorStatus.COMMON_BAD_REQUEST.getCode(), e.getMessage(), null);
    }

    @ExceptionHandler(AdaptorException.class)
    public ApiResponse<?> handleAdaptorException(AdaptorException e) {
        log.error(e.getMessage());
        log.error(Arrays.toString(e.getStackTrace()));
        return ApiResponse.onFailure(ErrorStatus.COMMON_BAD_REQUEST.getCode(), e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationError(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Optional.ofNullable(error.getDefaultMessage()).orElse("유효성 검증 실패"),
                        (existing, replacement) -> existing // 중복 필드는 무시
                ));

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.onFailure(
                        ErrorStatus.VALIDATION_ERROR.getCode(),
                        "유효성 검사 실패",
                        errors
                ));
    }
}
