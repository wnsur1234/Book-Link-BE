package com.bookbook.booklink.payment_service.controller;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.payment_service.controller.docs.PaymentApiDocs;
import com.bookbook.booklink.payment_service.model.dto.request.PaymentInitDto;
import com.bookbook.booklink.payment_service.model.dto.response.PaymentResDto;
import com.bookbook.booklink.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApiDocs {
    private final PaymentService paymentService;

    @Override
    public ResponseEntity<BaseResponse<Boolean>> initPayment(
            @RequestBody PaymentInitDto paymentInitDto,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        log.info("[PaymentController] Init payment request received. paymentId={}, amount={}, method={}",
                paymentInitDto.getPaymentId(), paymentInitDto.getAmount(), paymentInitDto.getMethod());

        paymentService.initPayment(paymentInitDto, member);

        log.info("[PaymentController] Init payment success. paymentId={}", paymentInitDto.getPaymentId());

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<PaymentResDto>> getPayment(
            @PathVariable String paymentId
    ) {
        log.info("[PaymentController] Get payment request received. paymentId={}", paymentId);

        PaymentResDto paymentRes = paymentService.getPayment(paymentId);

        log.info("[PaymentController] Get payment success. paymentId={}", paymentId);

        return ResponseEntity.ok(BaseResponse.success(paymentRes));
    }

    @Override
    public ResponseEntity<BaseResponse<List<PaymentResDto>>> getPaymentsByUser(
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        log.info("[PaymentController] Get payments by user request received. userId={}", member.getId());

        List<PaymentResDto> payments = paymentService.getPaymentsByUser(member);

        log.info("[PaymentController] Get payments by user success. userId={}, count={}", member.getId(), payments.size());

        return ResponseEntity.ok(BaseResponse.success(payments));
    }
}
