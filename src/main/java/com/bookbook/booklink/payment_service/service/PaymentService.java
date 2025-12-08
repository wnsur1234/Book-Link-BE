package com.bookbook.booklink.payment_service.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.payment_service.model.Payment;
import com.bookbook.booklink.payment_service.model.PaymentStatus;
import com.bookbook.booklink.payment_service.model.dto.request.PaymentInitDto;
import com.bookbook.booklink.payment_service.model.dto.response.PaymentResDto;
import com.bookbook.booklink.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 결제 관련 비즈니스 로직을 처리하는 서비스 클래스
 * <p>
 * 결제 생성, 검증, 단건 조회, 사용자별 조회, 결제 취소 기능을 제공한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PortOneService portOneService;

    /**
     * 결제를 초기화한다.
     *
     * @param dto 결제 초기화 요청 DTO
     * @throws CustomException 동일한 paymentId가 이미 존재할 경우
     */
    @Transactional
    public void initPayment(PaymentInitDto dto, Member member) {
        log.info("[PaymentService] Init payment start. paymentId={}, amount={}, method={}",
                dto.getPaymentId(), dto.getAmount(), dto.getMethod());

        if (paymentRepository.existsByPaymentId(dto.getPaymentId())) {
            log.warn("[PaymentService] Duplicate payment detected. paymentId={}", dto.getPaymentId());
            throw new CustomException(ErrorCode.PAYMENT_ALREADY_EXISTS);
        }

        Payment payment = Payment.builder()
                .amount(dto.getAmount())
                .paymentMethod(dto.getMethod())
                .status(PaymentStatus.READY)
                .paymentId(dto.getPaymentId())
                .createdAt(LocalDateTime.now())
                .member(member)
                .build();

        paymentRepository.save(payment);
        log.info("[PaymentService] Init payment success. paymentId={}", dto.getPaymentId());
    }

    /**
     * 결제 결과를 검증한다.
     * <p>
     * - 결제가 중복되지 않았는지 확인
     * - 결제 금액이 일치하는지 확인
     * - 검증 완료 시 결제를 승인 상태로 업데이트
     *
     * @param paymentId  결제 고유 식별자
     * @param paidAmount 실제 결제된 금액
     * @throws CustomException 결제가 존재하지 않거나, 금액이 불일치하는 경우
     */
    @Transactional(noRollbackFor = CustomException.class)
    public void validatePayment(String paymentId, int paidAmount) {
        log.info("[PaymentService] Validate payment. paymentId={}, paidAmount={}",
                paymentId, paidAmount);

        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> {
                    log.error("[PaymentService] Payment not found. paymentId={}", paymentId);
                    return new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
                });

        if (payment.getStatus().equals(PaymentStatus.APPROVED)) {
            log.warn("[PaymentService] Payment already approved. paymentId={}", paymentId);
            return;
        }

        if (!payment.getAmount().equals(paidAmount)) {
            log.error("[PaymentService] Payment amount mismatch. expected={}, actual={}, paymentId={}",
                    payment.getAmount(), paidAmount, paymentId);
            payment.paymentReject();
            paymentRepository.save(payment);
            throw new CustomException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }

        payment.paymentApprove();
        paymentRepository.save(payment);

        log.info("[PaymentService] Payment validated and approved. paymentId={}", paymentId);
    }

    /**
     * 특정 결제 정보를 조회한다.
     *
     * @param paymentId 결제 고유 식별자
     * @return PaymentResDto 결제 상세 응답 DTO
     * @throws CustomException 결제가 존재하지 않는 경우
     */
    public PaymentResDto getPayment(String paymentId) {
        log.info("[PaymentService] Get payment. paymentId={}", paymentId);

        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> {
                    log.error("[PaymentService] Payment not found. paymentId={}", paymentId);
                    return new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
                });

        log.info("[PaymentService] Get payment success. paymentId={}", paymentId);
        return PaymentResDto.fromEntity(payment);
    }

    /**
     * 특정 사용자의 결제 내역을 조회한다.
     *
     * @param member 사용자
     * @return PaymentResDto 리스트
     */
    public List<PaymentResDto> getPaymentsByUser(Member member) {
        log.info("[PaymentService] Get payments by user. userId={}", member.getId());

        List<Payment> paymentList = paymentRepository.findAllByMember(member);

        log.info("[PaymentService] Found {} payments. userId={}", paymentList.size(), member.getId());
        return paymentList.stream().map(PaymentResDto::fromEntity).toList();
    }

    /**
     * 결제를 취소한다.
     * <p>
     * - PortOne API에 환불 요청
     * - DB의 결제 상태를 취소로 변경
     *
     * @param paymentId 결제 고유 식별자
     * @param amount    환불 금액
     * @param reason    취소 사유
     * @throws CustomException 결제가 존재하지 않는 경우
     */
    @Transactional
    public void cancelPayment(String paymentId, Integer amount, String reason) {
        log.info("[PaymentService] Cancel payment start. paymentId={}, amount={}, reason={}",
                paymentId, amount, reason);

        // 포트원 환불 요청
        portOneService.cancelPayment(paymentId, amount, reason);

        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> {
                    log.error("[PaymentService] Payment not found for cancel. paymentId={}", paymentId);
                    return new CustomException(ErrorCode.PAYMENT_NOT_FOUND);
                });

        payment.paymentCancel();
        paymentRepository.save(payment);

        log.info("[PaymentService] Cancel payment success. paymentId={}", paymentId);
    }
}
