package com.bookbook.booklink.borrow_service.scheduler;

import com.bookbook.booklink.borrow_service.model.Borrow;
import com.bookbook.booklink.borrow_service.model.BorrowStatus;
import com.bookbook.booklink.borrow_service.repository.BorrowRepository;
import com.bookbook.booklink.point_service.model.TransactionType;
import com.bookbook.booklink.point_service.model.dto.request.PointUseDto;
import com.bookbook.booklink.point_service.service.PointService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowScheduler {

    private final BorrowRepository borrowRepository;
    private final PointService pointService;

    /**
     * 매일 자정(00:00)에 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void markOverdueBorrows() {
        LocalDateTime now = LocalDateTime.now();

        // 1) 대여 상태가 BORROWED, EXTENDED인데 dueDate가 지난 것들 조회
        List<Borrow> overdueBorrows = borrowRepository.findAllByStatusInAndDueAtBefore(
                List.of(BorrowStatus.BORROWED, BorrowStatus.EXTENDED),
                now
        );
        overdueBorrows.forEach(Borrow::markOverdue);

        // 2) 3일 이상 확정 안 된 신청 자동 취소
        LocalDateTime threeDaysAgo = now.minusDays(3);
        List<Borrow> cancelList = borrowRepository.findAllByStatusAndBorrowedAtBefore(
                BorrowStatus.REQUESTED,
                threeDaysAgo
        );

        cancelList.forEach(borrow -> {
            borrow.suspendBorrow();
            PointUseDto pointUseDto = PointUseDto.builder()
                    .type(TransactionType.REFUND)
                    .amount(borrow.getLibraryBookCopy().getLibraryBook().getDeposit())
                    .build();
            //pointService.usePoint(pointUseDto, UUID.randomUUID(), UUID.fromString("spring-scheduler"));
        });
    }
}