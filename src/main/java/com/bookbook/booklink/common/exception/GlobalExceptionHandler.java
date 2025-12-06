package com.bookbook.booklink.common.exception;

import com.bookbook.booklink.common.dto.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler(ValidationException.class)
    private ResponseEntity<BaseResponse<Object>> buildValidationErrorResponse(
            BindingResult bindingResult, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        String path = request.getRequestURI();
        String method = request.getMethod();
        String query = request.getQueryString();

        String detailMessage = bindingResult.getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        if (query != null) {
            log.warn("Validation failed [{} {}?{}]: {}", method, path, query, detailMessage);
        } else {
            log.warn("Validation failed [{} {}]: {}", method, path, detailMessage);
        }

        return ResponseEntity
                .status(errorCode.getHttpStatus().value())
                .body(BaseResponse.error(errorCode, path));
    }

    /**
     * DTO 검증 실패 (ex: @Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        return buildValidationErrorResponse(ex.getBindingResult(), request);
    }

    /**
     * 바인딩 예외 (ex: 타입 불일치)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<BaseResponse<Object>> handleBindExceptions(
            BindException ex, HttpServletRequest request) {
        return buildValidationErrorResponse(ex.getBindingResult(), request);
    }

    /**
     * 데이터 정합성 예외
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.DATA_INTEGRITY_VIOLATION;
        String path = request.getRequestURI();
        log.error("DB constraint violation at [{}]: {}", path, ex.getMessage(), ex);

        return ResponseEntity
                .status(errorCode.getHttpStatus().value())
                .body(BaseResponse.error(errorCode, path));
    }

    /**
     * 데이터베이스에 문제 생김
     */
    @ExceptionHandler({JpaSystemException.class, TransactionSystemException.class, DataAccessException.class})
    public ResponseEntity<BaseResponse<Object>> handleJpaSystemExceptions(
            RuntimeException ex, HttpServletRequest request) {
        ErrorCode errorCode = ErrorCode.DATABASE_ERROR;
        String path = request.getRequestURI();
        log.error("Unexpected DB error at [{}]: {}", path, ex.getMessage(), ex);

        return ResponseEntity
                .status(errorCode.getHttpStatus().value())
                .body(BaseResponse.error(errorCode, path));
    }


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse<String>> handleCustomException(CustomException ex, HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        String path = request.getRequestURI();
        String method = request.getMethod();
        String query = request.getQueryString();
        String message = ex.getMessage();

        // 어떤 비즈니스 로직에서 발생했는지 로그에 남김
        if (query != null) {
            log.warn("Business exception [{} {}?{}]: code={}, message={}", method, path, query, errorCode.name(), message);
        } else {
            log.warn("Business exception [{} {}]: code={}, message={}", method, path, errorCode.name(), message);
        }

        return ResponseEntity
                .status(errorCode.getHttpStatus().value())
                .body(BaseResponse.error(errorCode, path));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponse<String>> handleAuthenticationException(Exception ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String query = request.getQueryString();
        String message = ex.getMessage();

        ErrorCode errorCode = ErrorCode.USER_LOGIN_FAILED;

        if (query != null) {
            log.warn("Authentication exception [{} {}?{}]: code={}, message={}", method, path, query, errorCode.name(), message);
        } else {
            log.warn("Authentication exception [{} {}]: code={}, message={}", method, path, errorCode.name(), message);
        }


        return ResponseEntity
                .status(errorCode.getHttpStatus().value())
                .body(BaseResponse.error(errorCode, path));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<BaseResponse<String>> handleAuthorizationDeniedException(Exception ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String query = request.getQueryString();
        String message = ex.getMessage();

        ErrorCode errorCode = ErrorCode.METHOD_UNAUTHORIZED;

        if (query != null) {
            log.warn("AuthorizationDenied exception [{} {}?{}]: code={}, message={}", method, path, query, errorCode.name(), message);
        } else {
            log.warn("AuthorizationDenied exception [{} {}]: code={}, message={}", method, path, errorCode.name(), message);
        }

        return ResponseEntity
                .status(errorCode.getHttpStatus().value())
                .body(BaseResponse.error(errorCode, path));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<String>> handleException(Exception ex, HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String query = request.getQueryString();

        if (query != null) {
            log.error("Unhandled exception [{} {}?{}]: {}", method, path, query, ex.getMessage(), ex);
        } else {
            log.error("Unhandled exception [{} {}]: {}", method, path, ex.getMessage(), ex);
        }

        ErrorCode code = ErrorCode.UNKNOWN_ERROR;
        String message = ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace());

        return ResponseEntity
                .status(code.getHttpStatus().value())
                .body(BaseResponse.error(message, path));
    }

}
