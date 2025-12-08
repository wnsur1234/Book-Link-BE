package com.bookbook.booklink.book_service.model;

import com.bookbook.booklink.book_service.model.dto.request.LibraryBookRegisterDto;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.library_service.model.Library;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL") // 조회 시 deleted at이 null인 것만 검색
public class LibraryBook {

    @Id
    @UuidGenerator
    @GeneratedValue
    @Column(updatable = false, nullable = false)
    @Schema(description = "도서관이 소장한 도서 고유 ID (UUID)", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    @Getter
    private UUID id;

    @Column(nullable = true) // todo : 기존 데이터에 대해 migration 필요
    @Schema(description = "도서 설명", example = "도서 상태 상급의 깨끗한 도서로, 쾌적하게 이용 가능합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Min(0)
    @Column(nullable = false)
    @Schema(description = "보유 권수", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer copies;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "보증금 (단위 : 포인트)", example = "1000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer deposit = 0;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "누적 대여 횟수", example = "14", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer totalBorrowCount = 0;

    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    @Schema(description = "대여한 사람 수", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer borrowedCount = 0;

    @Min(0)
    @Column(nullable = false)
    @Schema(description = "대여 가능한 도서 수", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer availableBooks;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Schema(description = "도서 등록일", example = "2025-09-22T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @Column
    @Schema(description = "도서 삭제일", example = "2025-09-25T12:00:00", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "library_id", nullable = false)
    @Schema(description = "이 도서를 소장한 도서관")
    private Library library;

    @Column(nullable = false, updatable = false)
    private UUID ownerId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    @Schema(description = "도서 정보")
    private Book book;

    @OneToMany(mappedBy = "libraryBook", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "도서관이 보유한 각 권 개별 도서")
    private List<LibraryBookCopy> copiesList = new ArrayList<>();

    @OneToMany(mappedBy = "libraryBook", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @Schema(description = "미리보기 이미지 목록")
    private List<PreviewImage> previewImageList = new ArrayList<>();

    public static LibraryBook toEntity(LibraryBookRegisterDto libraryBookRegisterDto, Book book
            , Library library
    ) {
        LibraryBook libraryBook = LibraryBook.builder()
                .description(libraryBookRegisterDto.getDescription())
                .copies(0)
                .availableBooks(0)
                .deposit(libraryBookRegisterDto.getDeposit())
                .book(book)
                .library(library)
                .build();

        for (int i = 0; i < libraryBookRegisterDto.getCopies(); i++) {
            libraryBook.addCopy();
        }
        return libraryBook;
    }

    public void addCopy() {
        LibraryBookCopy copy = LibraryBookCopy.toEntity();
        copiesList.add(copy);
        copy.setLibraryBook(this);
        copies++; // 기존 변수와 동기화
        availableBooks++;
    }

    public void removeCopy(LibraryBookCopy copy) {
        if (copiesList.remove(copy)) {
            copy.setLibraryBook(null);
            copies--;
            if(!copy.getStatus().equals(BookStatus.AVAILABLE)) {
                throw new CustomException(ErrorCode.DATABASE_ERROR);
            }
            availableBooks--;
        }
    }
    public void addImage(String url) {
        PreviewImage image = PreviewImage.toEntity(url);
        previewImageList.add(image);
        image.setLibraryBook(this);
    }
  
    public void borrowCopy(LibraryBookCopy copy, LocalDateTime borrowedAt, LocalDateTime dueAt) {
        if (copy.getStatus() != BookStatus.AVAILABLE) throw new CustomException(ErrorCode.N0T_AVAILABLE_COPY);

        copy.borrow(borrowedAt, dueAt);
        availableBooks--;
        borrowedCount++;
        totalBorrowCount++;
    }

    public void returnCopy(LibraryBookCopy copy) {
        copy.returnBook();
        availableBooks++;
        borrowedCount--;
    }

    public void updateCopies(int targetCopies) {
        int currentCopies = this.copies;

        if (currentCopies > targetCopies) {
            int toRemove = currentCopies - targetCopies;
            List<LibraryBookCopy> removableCopies = copiesList.stream()
                    .filter(c -> c.getStatus() == BookStatus.AVAILABLE)
                    .limit(toRemove)
                    .toList();

            if (removableCopies.size() < toRemove) {
                throw new CustomException(ErrorCode.NOT_ENOUGH_AVAILABLE_COPIES_TO_REMOVE);
            }

            removableCopies.forEach(this::removeCopy);
        } else if (currentCopies < targetCopies) {
            int toAdd = targetCopies - currentCopies;
            for (int i = 0; i < toAdd; i++) {
                addCopy();
            }
        }

        if (copies != copiesList.size() || availableBooks + borrowedCount != copies) {
            throw new  CustomException(ErrorCode.LIBRARY_BOOK_COPIES_MISMATCH);
        }
    }

    public void updateDeposit(int deposit) {
        this.deposit = deposit;
    }

    public void softDelete() {
        if (hasBorrowedCopies()) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_BORROWED_BOOK);
        }
        this.deletedAt = LocalDateTime.now();
    }

    private boolean hasBorrowedCopies() {
        return getCopiesList().stream()
                .anyMatch(copy -> copy.getStatus() != BookStatus.AVAILABLE);
    }

    public void updatePreviewImages(List<String> previewImages) {
        previewImageList.forEach(img -> img.setLibraryBook(null));
        previewImageList.clear();

        if (previewImages != null) {
            previewImages.forEach(this::addImage);
        }
    }

    @PrePersist
    public void setOwnerIdBeforeSave() {
        if (library != null && library.getMember() != null) {
            this.ownerId = library.getMember().getId();
        }
    }

    public void updateDescription(String description) {
        if (description.equals(this.description)) {
            return;
        }
        this.description = description;
    }
}
