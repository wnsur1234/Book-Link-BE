package com.bookbook.booklink.book_service.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LibraryBookCopy {

    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue
    @UuidGenerator
    @Getter
    @Schema(description = "도서관이 소장한 책 한 권의 실제 인스턴스 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID id;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "library_book_id", nullable = false)
    @Schema(description = "도서관별 도서 정보")
    private LibraryBook libraryBook;

    @Setter(AccessLevel.PACKAGE)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "현재 대여 상태", example = "대여 가능", requiredMode = Schema.RequiredMode.REQUIRED)
    private BookStatus status = BookStatus.AVAILABLE;

    @Schema(description = "대여일", example = "2025-09-22T12:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime borrowedAt;

    @Schema(description = "반납 예정일", example = "2025-09-29T12:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime dueAt;

    public static LibraryBookCopy toEntity() {
        return LibraryBookCopy.builder().build();
    }

    public void borrow(LocalDateTime borrowedAt, LocalDateTime dueAt) {
        this.status = BookStatus.BORROWED;
        this.borrowedAt = borrowedAt;
        this.dueAt = dueAt;
    }

    public void returnBook() {
        this.status = BookStatus.AVAILABLE;
        this.borrowedAt = null;
        this.dueAt = null;
    }

    public void extendBook(LocalDateTime extendedAt) {
        this.status = BookStatus.EXTENDED;
        this.dueAt = extendedAt;
    }

    public void overdueBook() {
        this.status = BookStatus.OVERDUE;
    }
}
