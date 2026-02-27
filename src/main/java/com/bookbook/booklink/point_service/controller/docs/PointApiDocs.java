package com.bookbook.booklink.point_service.controller.docs;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.point_service.model.dto.request.PointUseDto;
import com.bookbook.booklink.point_service.model.dto.response.PointBalanceDto;
import com.bookbook.booklink.point_service.model.dto.response.PointExchangeDto;
import com.bookbook.booklink.point_service.model.dto.response.PointHistoryListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Point API", description = "포인트 관련 API")
@RequestMapping("/api/point")
public interface PointApiDocs {

    @Operation(
            summary = "포인트 잔액 조회",
            description = "회원의 포인트 잔액을 조회합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.POINT_NOT_FOUND, ErrorCode.DATABASE_ERROR, ErrorCode.METHOD_UNAUTHORIZED})
    @GetMapping("/balance")
    ResponseEntity<BaseResponse<PointBalanceDto>> getPointBalance(
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "포인트 사용/적립",
            description = "포인트를 사용하거나 적립합니다."
    )
    @ApiErrorResponses({ErrorCode.DUPLICATE_REQUEST, ErrorCode.VALIDATION_FAILED, ErrorCode.POINT_NOT_FOUND, ErrorCode.DATABASE_ERROR, ErrorCode.METHOD_UNAUTHORIZED})
    @PostMapping("/use")
    ResponseEntity<BaseResponse<PointBalanceDto>> usePoint(
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestBody PointUseDto pointUseDto,
            @RequestHeader("Trace-Id") UUID traceId
    );

    @Operation(
            summary = "포인트 히스토리 조회",
            description = "회원의 포인트 사용/적립 내역을 조회합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR, ErrorCode.METHOD_UNAUTHORIZED})
    @GetMapping("/history")
    ResponseEntity<BaseResponse<List<PointHistoryListDto>>> getPointHistory(
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(
            summary = "포인트 충전",
            description = "회원의 포인트를 결제 ID를 통해 충전합니다."
    )
    @ApiErrorResponses({ErrorCode.DUPLICATE_REQUEST, ErrorCode.VALIDATION_FAILED,
            ErrorCode.POINT_NOT_FOUND, ErrorCode.INVALID_API_TOKEN,
            ErrorCode.DATABASE_ERROR, ErrorCode.METHOD_UNAUTHORIZED,
            ErrorCode.PAYMENT_NOT_FOUND, ErrorCode.PAYMENT_AMOUNT_MISMATCH, ErrorCode.JSON_PARSING_ERROR})
    @PostMapping("/charge")
    ResponseEntity<BaseResponse<Integer>> chargePoint(
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestParam String paymentId,
            @RequestHeader("Trace-Id") UUID traceId
    );

    @Operation(
            summary = "포인트 환불",
            description = "결제 취소 사유와 금액을 입력받아 포인트를 환불합니다."
    )
    @ApiErrorResponses({ErrorCode.VALIDATION_FAILED, ErrorCode.DATABASE_ERROR, ErrorCode.INVALID_API_TOKEN, ErrorCode.PAYMENT_CANCEL_FAILED, ErrorCode.METHOD_UNAUTHORIZED})
    @PostMapping("/cancel")
    ResponseEntity<BaseResponse<Boolean>> cancelPayment(
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestParam String paymentId,
            @RequestParam Integer amount,
            @RequestParam String reason
    );

    @Operation(
            summary = "포인트 전환",
            description = "포인트를 지정한 비율로 전환합니다."
    )
    @ApiErrorResponses({ErrorCode.DUPLICATE_REQUEST, ErrorCode.VALIDATION_FAILED, ErrorCode.POINT_NOT_ENOUGH, ErrorCode.DATABASE_ERROR, ErrorCode.METHOD_UNAUTHORIZED})
    @PostMapping("/exchange")
    ResponseEntity<BaseResponse<PointExchangeDto>> exchangePoint(
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestParam Integer num,
            @RequestHeader("Trace-Id") UUID traceId
    );
}
