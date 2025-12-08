package com.bookbook.booklink.payment_service.controller.docs;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.payment_service.model.dto.request.PaymentInitDto;
import com.bookbook.booklink.payment_service.model.dto.response.PaymentResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Payment API", description = "결제 관련 API")
@RequestMapping("/api/payment")
public interface PaymentApiDocs {

    @Operation(
            summary = "결제 초기화",
            description = "사용자가 결제를 시작할 때 결제 ID, 금액, 결제 수단을 등록합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR, ErrorCode.DUPLICATE_REQUEST,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.DATA_INTEGRITY_VIOLATION, ErrorCode.PAYMENT_ALREADY_EXISTS})
    @PostMapping("/init")
    ResponseEntity<BaseResponse<Boolean>> initPayment(
            @RequestBody PaymentInitDto paymentInitDto,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "결제 단건 조회",
            description = "결제 ID를 기준으로 특정 결제 정보를 조회합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED, ErrorCode.PAYMENT_NOT_FOUND, ErrorCode.PAYMENT_NOT_FOUND})
    @GetMapping("/{paymentId}")
    ResponseEntity<BaseResponse<PaymentResDto>> getPayment(
            @PathVariable String paymentId
    );

    @Operation(
            summary = "사용자 결제 내역 조회",
            description = "특정 사용자의 모든 결제 내역을 조회합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR,
            ErrorCode.METHOD_UNAUTHORIZED})
    @GetMapping("/user/{userId}")
    ResponseEntity<BaseResponse<List<PaymentResDto>>> getPaymentsByUser(
            @AuthenticationPrincipal(expression = "member") Member member
    );
}
