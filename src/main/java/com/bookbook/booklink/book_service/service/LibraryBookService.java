package com.bookbook.booklink.book_service.service;

import com.bookbook.booklink.book_service.model.Book;
import com.bookbook.booklink.book_service.model.LibraryBook;
import com.bookbook.booklink.book_service.model.LibraryBookCopy;
import com.bookbook.booklink.book_service.model.dto.request.LibraryBookRegisterDto;
import com.bookbook.booklink.book_service.model.dto.request.LibraryBookSearchReqDto;
import com.bookbook.booklink.book_service.model.dto.request.LibraryBookUpdateDto;
import com.bookbook.booklink.book_service.model.dto.response.*;
import com.bookbook.booklink.book_service.repository.LibraryBookRepository;
import com.bookbook.booklink.common.dto.PageResponse;
import com.bookbook.booklink.common.event.LockEvent;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.service.IdempotencyService;
import com.bookbook.booklink.library_service.model.Library;
import com.bookbook.booklink.library_service.model.dto.response.LibraryBookListProjection;
import com.bookbook.booklink.library_service.repository.LibraryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryBookService {
    private final LibraryBookRepository libraryBookRepository;
    private final IdempotencyService idempotencyService;
    private final BookService bookService;
    private final LibraryRepository libraryRepository;

    @Transactional
    public UUID registerLibraryBook(LibraryBookRegisterDto bookRegisterDto, String traceId, UUID userId, Library library) {
        log.info("[LibraryBookService] [traceId = {}, userId = {}] register library book initiate bookId={}", traceId, userId, bookRegisterDto.getId());

        // 멱등성 체크
        String key = "library-book:register:" + traceId;
        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        // find book & library
        Book book = bookService.findById(bookRegisterDto.getId());

        // todo : 에러났을 때 멱등성 체크 풀기
        LibraryBook libraryBook = LibraryBook.toEntity(bookRegisterDto, book, library);

        for (int i = 0; i < bookRegisterDto.getCopies(); i++) {
            libraryBook.addCopy();
        }
        for (String url : bookRegisterDto.getPreviewImages()) {
            libraryBook.addImage(url);
        }

        library.addBook();

        LibraryBook savedLibraryBook = libraryBookRepository.save(libraryBook);
        UUID bookId = savedLibraryBook.getId();

        log.info("[LibraryBookService] [traceId = {}, userId = {}] register book success bookId={}", traceId, userId, bookId);

        return bookId;
    }

    @Transactional
    public void updateLibraryBook(LibraryBookUpdateDto updateBookDto, String traceId, UUID userId) {
        log.info("[LibraryBookService] [traceId = {}, userId = {}] update library book initiate updateBookDto={}", traceId, userId, updateBookDto);

        LibraryBook libraryBook = getLibraryBookOrThrow(updateBookDto.getId());

        if (updateBookDto.getCopies() != null) libraryBook.updateCopies(updateBookDto.getCopies());
        if (updateBookDto.getDeposit() != null) libraryBook.updateDeposit(updateBookDto.getDeposit());
        if (updateBookDto.getDescription() != null) libraryBook.updateDescription(updateBookDto.getDescription());
        if (updateBookDto.getPreviewImages() != null) {
            libraryBook.updatePreviewImages(updateBookDto.getPreviewImages());
        }

        log.info("[LibraryBookService] [traceId = {}, userId = {}] update library book success libraryBook={}", traceId, userId, libraryBook);
    }

    @Transactional
    public void deleteLibraryBook(UUID libraryBookId, String traceId, UUID userId) {
        log.info("[LibraryBookService] [traceId = {}, userId = {}] delete library book initiate libraryBookId={}", traceId, userId, libraryBookId);

        LibraryBook libraryBook = getLibraryBookOrThrow(libraryBookId);

        libraryBook.softDelete();
        log.info("[LibraryBookService] [traceId = {}, userId = {}] delete library book success libraryBookId={}", traceId, userId, libraryBookId);
    }

    @Transactional(readOnly = true)
    public PageResponse<LibraryBookListDto> getLibraryBookList(LibraryBookSearchReqDto request, UUID userId) {
        int page = request.getPage();
        int size = request.getSize();
        int offset = page * size;
        Double lat = request.getLatitude();
        Double lng = request.getLongitude();
        UUID libraryId = request.getLibraryId();
        UUID myLibraryId = getMyLibraryId(userId);

        List<LibraryBookListProjection> projections =
                libraryBookRepository.findLibraryBooksBySearch(lat, lng, libraryId, myLibraryId, request.getBookName(), request.getSortType().toString(), size, offset);

        long total = libraryBookRepository.countLibraryBooksBySearch(libraryId, request.getBookName());


        List<LibraryBookListDto> dtoList = projections.stream()
                .map(p -> LibraryBookListDto.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .description(p.getDescription())
                        .author(p.getAuthor())
                        .libraryName(p.getLibraryName())
                        .distance(p.getDistance())
                        .copies(p.getCopies())
                        .borrowedCount(p.getBorrowedCount())
                        .deposit(p.getDeposit())
                        .rentedOut(p.getRentedOut() != null && p.getRentedOut() == 1)
                        .expectedReturnDate(p.getExpectedReturnDate())
                        .imageUrl(p.getImageUrl())
                        .isMine(p.getMine() == 1)
                        .build())
                .toList();

        return PageResponse.<LibraryBookListDto>builder()
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .currentPage(page)
                .pageSize(size)
                .content(dtoList)
                .hasNext(offset + dtoList.size() < total)
                .hasPrevious(page > 0)
                .build();
    }

    /**
     * 특정 도서관의 Top 5 도서 목록을 반환하는 메서드
     *
     * @param libraryId 조회할 도서관의 ID
     * @return 해당 도서관의 가장 인기가 많은 도서 5개 리스트
     */
    @Transactional(readOnly = true)
    public List<LibraryBook> findTop5Books(UUID libraryId) {
        return libraryBookRepository.findTop5BooksByLibraryOrderByLikeCount(libraryId, PageRequest.of(0, 5));
    }

    @Transactional(readOnly = true)
    public List<LibraryBook> findTop5BooksList(List<UUID> libraryIds) {
        return libraryBookRepository.findTopBooksByLibraryIds(libraryIds);
    }

    public LibraryBook getLibraryBookOrThrow(UUID libraryBookId) {
        return libraryBookRepository.findById(libraryBookId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));
    }

    public LibraryBookCopy getLibraryBookCopy(UUID libraryBookId) {
        LibraryBook libraryBook = getLibraryBookOrThrow(libraryBookId);
        LibraryBookCopy libraryBookCopy = null;
        for (LibraryBookCopy copy : libraryBook.getCopiesList()) {
            if (copy.getStatus().toString().equals("AVAILABLE")) {
                libraryBookCopy = copy;
            }
        }
        if (libraryBookCopy == null) {
            throw new CustomException(ErrorCode.DATABASE_ERROR);
        }
        return libraryBookCopy;
    }

    @Transactional(readOnly = true)
    public LibraryBookDetailResDto getLibraryBookDetail(UUID libraryBookId, UUID userId) {
        LibraryBook libraryBook = libraryBookRepository.findById(libraryBookId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));
        Book book = libraryBook.getBook();
        Library library = libraryBook.getLibrary();

        UUID myLibraryId = getMyLibraryId(userId);
        boolean isMyLibrary = Objects.equals(library.getId(), myLibraryId);

        LibraryDto libraryDto = LibraryDto.builder()
                .id(library.getId())
                .name(library.getName())
                .latitude(library.getLatitude())
                .longitude(library.getLongitude())
                .build();
        BookDetailDto bookDetailDto = BookDetailDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .category(book.getCategory())
                .ISBN(book.getISBN())
                .originalPrice(book.getOriginalPrice())
                .publishedDate(book.getPublishedDate().toLocalDate())
                .build();
        LibraryBookDetailDto libraryBookDetailDto = LibraryBookDetailDto.builder()
                .id(libraryBook.getId())
                .description(libraryBook.getDescription())
                .copies(libraryBook.getCopies())
                .deposit(libraryBook.getDeposit())
                .borrowedCount(libraryBook.getBorrowedCount())
                .borrowedStatus(LibraryBookStatus.AVAILABLE.toString())
                .previewImages(libraryBook.getPreviewImageListToString())
                .build();

        // todo : libraryBook 의 대여 상태 판별
        // 대여 예약 : 예상 반납 기한 리턴
        // 대여 중 : 대여 id, 대여 상태, 반납 예정 일자 리턴
        // 예약 중 : 예약 id, 예상 반납 기한 리턴


        return LibraryBookDetailResDto.builder()
                .bookDetailDto(bookDetailDto)
                .libraryBookDetailDto(libraryBookDetailDto)
                .libraryDto(libraryDto)
                .isMine(isMyLibrary)
                .build();
    }

    public UUID getMyLibraryId(UUID userId) {
        return Objects.requireNonNull(libraryRepository.findByMemberId(userId)
                        .orElse(null))
                .getId();
    }
}
    