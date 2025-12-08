package com.bookbook.booklink.borrow_service.model;

import com.bookbook.booklink.book_service.model.LibraryBookCopy;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.auth_service.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Borrow {

    @Id
    @UuidGenerator
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    @Schema(description = "대여 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    @Getter
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "현재 대출 상태", example = "BORROWED", requiredMode = Schema.RequiredMode.REQUIRED)
    private BorrowStatus status = BorrowStatus.REQUESTED;

    @Column(nullable = false, updatable = false)
    @Schema(description = "대여일", example = "2025-09-22T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime borrowedAt;

    @Column(nullable = false)
    @Schema(description = "반납 예정일", example = "2025-09-29T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime dueAt;

    @Schema(description = "실제 반납일", example = "2025-09-30T12:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime returnedAt;

    @Schema(description = "반납 이미지", example = "반납 이미지", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "library_book_copy_id", nullable = false)
    private LibraryBookCopy libraryBookCopy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static Borrow createBorrow(LibraryBookCopy copy, Member member, LocalDateTime borrowedAt, LocalDateTime dueAt) {
        return Borrow.builder()
                .libraryBookCopy(copy)
                .member(member)
                .borrowedAt(borrowedAt)
                .dueAt(dueAt)
                .build();
    }

    public void setBorrowed() {
        this.status = BorrowStatus.BORROWED;
    }

    public void returnBook(LocalDateTime returnedAt, String imageUrl) {
        if (this.status != BorrowStatus.BORROWED && this.status != BorrowStatus.EXTENDED) {
            throw new CustomException(ErrorCode.ILLEGAL_BOOK_STATE);
        }
        this.status = BorrowStatus.RETURNED;
        this.returnedAt = returnedAt;
        this.imageUrl = imageUrl;
    }

    public void extendBook(LocalDateTime extendedAt) {
        if (this.dueAt.isAfter(extendedAt)) {
            throw new CustomException(ErrorCode.ILLEGAL_EXTEND_DATE);
        }

        this.status = BorrowStatus.EXTENDED;
        this.dueAt = extendedAt;
        this.libraryBookCopy.extendBook(extendedAt);
    }

    public void suspendBorrow() {
        this.status = BorrowStatus.SUSPENDED;
        this.libraryBookCopy.returnBook();
    }

    public void markOverdue() {
        this.status = BorrowStatus.OVERDUE;
        this.libraryBookCopy.overdueBook();
    }
}
