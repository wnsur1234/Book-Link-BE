package com.bookbook.booklink.point_service.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.common.event.LockEvent;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.service.IdempotencyService;
import com.bookbook.booklink.payment_service.service.PaymentService;
import com.bookbook.booklink.payment_service.service.PortOneService;
import com.bookbook.booklink.point_service.model.Point;
import com.bookbook.booklink.point_service.model.PointHistory;
import com.bookbook.booklink.point_service.model.TransactionType;
import com.bookbook.booklink.point_service.model.dto.request.PointUseDto;
import com.bookbook.booklink.point_service.model.dto.response.PointBalanceDto;
import com.bookbook.booklink.point_service.model.dto.response.PointExchangeDto;
import com.bookbook.booklink.point_service.model.dto.response.PointHistoryListDto;
import com.bookbook.booklink.point_service.repository.PointHistoryRepository;
import com.bookbook.booklink.point_service.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 포인트 서비스
 * <p>
 * 포인트 잔액 조회, 사용/적립, 충전, 환불, 전환, 거래 내역 관리 등의 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;
    private final PortOneService portOneService;
    private final PaymentService paymentService;
    private final IdempotencyService idempotencyService;
    private final PointHistoryRepository pointHistoryRepository;

    private final Integer EXCHANGE_VALUE = 12000;

    /**
     * 회원의 포인트 잔액 조회
     *
     * @param member 회원
     * @return 현재 포인트 잔액 DTO
     */
    @Transactional(readOnly = true)
    public PointBalanceDto getPointBalance(Member member) {
        UUID userId = member.getId();
        log.info("[PointService] [userId = {}] Get Point Balance request received.", userId);

        Point point = findPointByUserId(userId);

        log.info("[PointService] [userId = {}] Get Point Balance success. balance={}", userId, point.getBalance());
        return PointBalanceDto.fromEntity(point);
    }

    /**
     * 포인트 사용/적립
     * <p>
     * 멱등성 체크 후 거래 내역을 저장하고, 회원의 잔액을 업데이트합니다.
     *
     * @param pointUseDto 요청 DTO (금액, 거래 타입 등)
     * @param traceId     멱등성 추적 ID
     * @param member      회원
     * @return 업데이트된 포인트 잔액 DTO
     */
    @Transactional
    public PointBalanceDto usePoint(PointUseDto pointUseDto, UUID traceId, Member member) {

        UUID userId = member.getId();
        log.info("[PointService] [userId = {}] Use Point request received. traceId={}, pointUseDto={}",
                userId, traceId, pointUseDto);

        // 멱등성 체크
        String key = idempotencyService.generateIdempotencyKey("point:use", String.valueOf(traceId));
        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        // 거래 내역 저장
        PointHistory newHistory = PointHistory.toEntity(pointUseDto, member);
        pointHistoryRepository.save(newHistory);

        // 잔액 업데이트
        Point point = findPointByUserId(userId);
        Integer amount = pointUseDto.getAmount();
        if (amount >= 0) {
            point.addPoint(amount);
        } else {
            point.usePoint(pointUseDto.getAmount());
        }
        Point updatedPoint = pointRepository.save(point);

        log.info("[PointService] [userId = {}] Use Point success. newBalance={}", userId, updatedPoint.getBalance());
        return PointBalanceDto.fromEntity(updatedPoint);
    }

    /**
     * 회원의 포인트 거래 내역 조회
     *
     * @param member 회원
     * @return 포인트 거래 내역 리스트
     */
    @Transactional(readOnly = true)
    public List<PointHistoryListDto> getPointHistoryList(Member member) {

        UUID userId = member.getId();
        log.info("[PointService] [userId = {}] Get Point History request received.", userId);

        List<PointHistory> pointHistoryList = pointHistoryRepository.findAllByMember_IdOrderByCreatedAt(userId);

        log.info("[PointService] [userId = {}] Get Point History success. size={}", userId, pointHistoryList.size());
        return pointHistoryList.stream().map(PointHistoryListDto::fromEntity).toList();
    }

    /**
     * 포인트 전환
     * <p>
     * 멱등성 체크 후 포인트를 지정된 단위(num)만큼 차감하고, 교환 코드를 발급합니다.
     *
     * @param member  회원
     * @param traceId 멱등성 추적 ID
     * @param num     교환 단위 수량
     * @return 포인트 전환 결과 DTO
     */
    @Transactional
    public PointExchangeDto exchangePoint(Member member, UUID traceId, Integer num) {

        UUID userId = member.getId();
        log.info("[PointService] [userId = {}] Exchange Point request received. traceId={}, num={}",
                userId, traceId, num);

        // 멱등성 체크
        String key = idempotencyService.generateIdempotencyKey("point:exchange", String.valueOf(traceId));
        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        Point point = findPointByUserId(userId);

        if (point.getBalance() < EXCHANGE_VALUE * num) {
            log.warn("[PointService] [userId = {}] Exchange Point failed. Insufficient balance. balance={}, required={}",
                    userId, point.getBalance(), EXCHANGE_VALUE * num);
            throw new CustomException(ErrorCode.POINT_NOT_ENOUGH);
        }

        point.usePoint(num);
        Point updatedPoint = pointRepository.save(point);

        PointHistory pointHistory = PointHistory.toEntity(EXCHANGE_VALUE * num, TransactionType.EXCHANGE, member);
        pointHistoryRepository.save(pointHistory);

        PointExchangeDto result = PointExchangeDto.builder()
                .balance(updatedPoint.getBalance())
                .rewardCode(UUID.randomUUID())
                .build();

        log.info("[PointService] [userId = {}] Exchange Point success. newBalance={}, rewardCode={}",
                userId, result.getBalance(), result.getRewardCode());
        return result;
    }

    /**
     * 포인트 충전
     * <p>
     * 멱등성 체크 후 결제 정보를 확인하고, 해당 금액만큼 포인트를 적립합니다.
     *
     * @param member    회원
     * @param paymentId 결제 ID
     * @param traceId   멱등성 추적 ID
     * @return 충전된 금액
     */
    @Transactional
    public Integer chargePoint(Member member, String paymentId, UUID traceId) {

        UUID userId = member.getId();
        log.info("[PointService] [userId = {}] Charge Point request received. traceId={}, paymentId={}",
                userId, traceId, paymentId);

        // 멱등성 체크
        String key = idempotencyService.generateIdempotencyKey("point:charge", String.valueOf(traceId));
        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        // 1. PortOne 서버에서 결제 정보 조회
        Integer amount = portOneService.getPaymentInfo(paymentId);

        // 2. 가맹점 결제 검증
        paymentService.validatePayment(paymentId, amount);

        // 3. 포인트 적립 (1원 = 1포인트)
        Point point = findPointByUserId(userId);
        point.usePoint(amount);
        pointRepository.save(point);

        // 4. 히스토리 기록
        PointHistory history = PointHistory.toEntity(amount, TransactionType.CHARGE, member);
        pointHistoryRepository.save(history);

        log.info("[PointService] [userId = {}] Charge Point success. chargedAmount={}, newBalance={}",
                userId, amount, point.getBalance());
        return amount;
    }

    /**
     * 포인트 환불
     * <p>
     * 결제를 취소하고, 해당 금액만큼 포인트를 차감합니다.
     *
     * @param member    회원 ID
     * @param paymentId 결제 ID
     * @param amount    환불 금액
     * @param reason    환불 사유
     */
    @Transactional
    public void cancelPoint(Member member, String paymentId, Integer amount, String reason) {
        UUID userId = member.getId();
        log.info("[PointService] [userId = {}] Cancel Payment request received. paymentId={}, amount={}, reason={}",
                userId, paymentId, amount, reason);

        paymentService.cancelPayment(paymentId, amount, reason);

        Point point = findPointByUserId(userId);
        point.usePoint(amount);

        PointHistory history = PointHistory.toEntity(amount, TransactionType.REFUND, member);
        pointHistoryRepository.save(history);

        log.info("[PointService] [userId = {}] Cancel Payment success. paymentId={}, refundedAmount={}, newBalance={}",
                userId, paymentId, amount, point.getBalance());
    }

    /**
     * 회원의 포인트 객체 조회
     *
     * @param userId 회원 ID
     * @return 회원의 포인트 엔티티
     */
    public Point findPointByUserId(UUID userId) {
        return pointRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("[PointService] [userId = {}] Point not found.", userId);
                    return new CustomException(ErrorCode.POINT_NOT_FOUND);
                });
    }
}
