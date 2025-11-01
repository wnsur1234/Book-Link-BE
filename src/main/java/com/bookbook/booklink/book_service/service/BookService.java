package com.bookbook.booklink.book_service.service;

import com.bookbook.booklink.book_service.model.Book;
import com.bookbook.booklink.book_service.model.dto.request.BookRegisterDto;
import com.bookbook.booklink.book_service.model.dto.response.BookResponseDto;
import com.bookbook.booklink.book_service.model.dto.response.NationalLibraryResponseDto;
import com.bookbook.booklink.book_service.repository.BookRepository;
import com.bookbook.booklink.common.event.LockEvent;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.service.IdempotencyService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final NationalLibraryService nationalLibraryService;
    private final ModelMapper modelMapper;
    private final IdempotencyService idempotencyService;

    @Transactional
    public BookResponseDto getBook(String isbn, String traceId, UUID userId) {
        log.info("[LibraryBookService] [traceId = {}, userId = {}] get book initiate isbn={}", traceId, userId, isbn);

        Book book = bookRepository.findByISBN(isbn);
        if (book != null) {
            BookResponseDto dto = modelMapper.map(book, BookResponseDto.class);
            dto.setFoundInNationalLibrary(false);
            log.info("[BookService] [traceId={}, userId={}] found bookId={}", traceId, userId, dto.getId());
            return dto;
        }

        NationalLibraryResponseDto apiResponse;
        try {
            apiResponse = nationalLibraryService.searchBookByIsbn(isbn, traceId, userId);
        } catch (Exception e) {
            log.error("[BookService] [traceId={}, userId={}] API 호출 실패 isbn={}", traceId, userId, isbn, e);
            throw new CustomException(ErrorCode.API_FALLBACK_FAIL);
        }

        if (apiResponse == null) {
            throw new CustomException(ErrorCode.INVALID_ISBN_CODE);
        }

        BookResponseDto dto = apiResponse.toBookResponseDto();
        dto.setFoundInNationalLibrary(true);
        log.info("[LibraryBookService] [traceId = {}, userId = {}] get book success from nationalLibraryApi bookId={}, isbn={}", traceId, userId, null, dto.getISBN());
        return dto;
    }

    @Transactional
    public UUID saveBook(BookRegisterDto bookRegisterDto, String traceId, UUID userId) {
        log.info("[BookService] [traceId = {}, userId = {}] get book initiate isbn={}", traceId, userId, bookRegisterDto.getIsbn());

        // 멱등성 체크
        String key = "book:register:" + traceId;
        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        if (bookRepository.existsByISBN(bookRegisterDto.getIsbn())) {
            throw new CustomException(ErrorCode.DUPLICATE_BOOK);
        }

        Book newBook = Book.toEntity(bookRegisterDto);
        Book savedBook = bookRepository.save(newBook);
        UUID bookId = savedBook.getId();

        log.info("[BookService] [traceId = {}, userId = {}] get book success bookId={}", traceId, userId, bookId);

        return bookId;
    }

    public Book findById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.BOOK_NOT_FOUND));
    }
}
