package com.bookbook.booklink.point_service.controller;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.point_service.controller.docs.PointApiDocs;
import com.bookbook.booklink.point_service.model.dto.request.PointUseDto;
import com.bookbook.booklink.point_service.model.dto.response.PointBalanceDto;
import com.bookbook.booklink.point_service.model.dto.response.PointExchangeDto;
import com.bookbook.booklink.point_service.model.dto.response.PointHistoryListDto;
import com.bookbook.booklink.point_service.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PointController implements PointApiDocs {
    private final PointService pointService;

    @Override
    public ResponseEntity<BaseResponse<PointBalanceDto>> getPointBalance(
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        log.info("[PointController] [userId = {}] Get Point Balance request received.", member.getId());

        PointBalanceDto balanceDto = pointService.getPointBalance(member);

        log.info("[PointController] [userId = {}] Get Point Balance success. balance={}", member.getId(), balanceDto.getBalance());

        return ResponseEntity.ok()
                .body(BaseResponse.success(balanceDto));
    }

    @Override
    public ResponseEntity<BaseResponse<PointBalanceDto>> usePoint(
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestBody PointUseDto pointUseDto,
            @RequestHeader("Trace-Id") UUID traceId
    ) {
        log.info("[PointController] [userId = {}] Use Point request received. pointUseDto={}", member.getId(), pointUseDto);

        PointBalanceDto balanceDto = pointService.usePoint(pointUseDto, traceId, member);

        log.info("[PointController] [userId = {}] Use Point success. newBalance={}", member.getId(), balanceDto.getBalance());

        return ResponseEntity.ok()
                .body(BaseResponse.success(balanceDto));
    }

    @Override
    public ResponseEntity<BaseResponse<List<PointHistoryListDto>>> getPointHistory(
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        log.info("[PointController] [userId = {}] Get Point History request received.", member.getId());

        List<PointHistoryListDto> historyListDtoList = pointService.getPointHistoryList(member);

        log.info("[PointController] [userId = {}] Get Point History success. size={}", member.getId(), historyListDtoList.size());

        return ResponseEntity.ok()
                .body(BaseResponse.success(historyListDtoList));
    }

    @Override
    public ResponseEntity<BaseResponse<Integer>> chargePoint(
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestParam String paymentId,
            @RequestHeader("Trace-Id") UUID traceId
    ) {
        log.info("[PointController] [userId = {}] Charge Point request received. paymentId={}", member.getId(), paymentId);

        Integer chargedAmount = pointService.chargePoint(member, paymentId, traceId);

        log.info("[PointController] [userId = {}] Charge Point success. chargedAmount={}", member.getId(), chargedAmount);

        return ResponseEntity.ok()
                .body(BaseResponse.success(chargedAmount));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> cancelPayment(
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestParam String paymentId,
            @RequestParam Integer amount,
            @RequestParam String reason
    ) {
        log.info("[PointController] [userId = {}] Cancel Payment request received. paymentId={}, amount={}, reason={}",
                member.getId(), paymentId, amount, reason);

        pointService.cancelPoint(member, paymentId, amount, reason);

        log.info("[PointController] [userId = {}] Cancel Payment success. paymentId={}", member.getId(), paymentId);

        return ResponseEntity.ok(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<PointExchangeDto>> exchangePoint(
            @AuthenticationPrincipal(expression = "member") Member member,
            @RequestParam Integer num,
            @RequestHeader("Trace-Id") UUID traceId
    ) {
        log.info("[PointController] [userId = {}] Exchange Point request received. num={}, traceId={}",
                member.getId(), num, traceId);

        PointExchangeDto pointExchangeDto = pointService.exchangePoint(member, traceId, num);

        log.info("[PointController] [userId = {}] Exchange Point success. exchangedPoint={}", member.getId(), pointExchangeDto);

        return ResponseEntity.ok()
                .body(BaseResponse.success(pointExchangeDto));
    }
}