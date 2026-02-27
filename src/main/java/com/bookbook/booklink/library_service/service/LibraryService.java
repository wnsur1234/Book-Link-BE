package com.bookbook.booklink.library_service.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.book_service.model.LibraryBook;
import com.bookbook.booklink.book_service.service.LibraryBookService;
import com.bookbook.booklink.common.dto.PageResponse;
import com.bookbook.booklink.common.event.LockEvent;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.service.IdempotencyService;
import com.bookbook.booklink.library_service.model.Library;
import com.bookbook.booklink.library_service.model.LibraryLikes;
import com.bookbook.booklink.library_service.model.dto.request.LibraryRegDto;
import com.bookbook.booklink.library_service.model.dto.request.LibraryUpdateDto;
import com.bookbook.booklink.library_service.model.dto.response.LibraryDetailDto;
import com.bookbook.booklink.library_service.model.dto.response.LibraryDistanceProjection;
import com.bookbook.booklink.library_service.repository.LibraryLikesRepository;
import com.bookbook.booklink.library_service.repository.LibraryRepository;
import com.bookbook.booklink.review_service.model.dto.response.ReviewListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Library 관련 비즈니스 로직 처리 서비스
 *
 * <p>등록(register)과 수정(update) 시 멱등성을 보장하기 위해
 * Redis를 활용한 Lock 체크를 수행</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final LibraryLikesRepository libraryLikesRepository;
    private final IdempotencyService idempotencyService;
    private final LibraryBookService libraryBookService;

    /**
     * 새로운 Library 등록
     * <p>
     * 동일 traceId 요청 시 중복 처리 방지를 위해 Redis Lock 체크 수행
     *
     * @param libraryRegDto Library 등록 정보 DTO
     * @param traceId       요청 멱등성 체크용 ID (클라이언트 전달)
     * @param member        요청 사용자
     * @return 등록된 Library ID
     */
    @Transactional
    public UUID registerLibrary(LibraryRegDto libraryRegDto, String traceId, Member member) {

        UUID userId = member.getId();

        log.info("[LibraryService] [traceId={}, userId={}] register library initiate, name={}",
                traceId, userId, libraryRegDto.getName());

        String key = idempotencyService.generateIdempotencyKey("library:register", traceId);

        // Redis Lock으로 멱등성 체크
        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        // Library 엔티티 생성 후 DB 저장
        Library newLibrary = Library.toEntity(libraryRegDto, member);
        Library savedLibrary = save(newLibrary);

        log.info("[LibraryService] [traceId={}, userId={}] register library success, name={}",
                traceId, userId, savedLibrary.getName());

        return savedLibrary.getId();
    }

    /**
     * 기존 Library 정보 수정
     * <p>
     * update 요청도 멱등성 보장을 위해 Redis Lock 사용
     *
     * @param libraryUpdateDto 수정 정보 DTO
     * @param traceId          요청 멱등성 체크용 ID
     * @param userId           요청 사용자 ID
     * @return 수정된 Library ID
     */
    @Transactional
    public UUID updateLibrary(LibraryUpdateDto libraryUpdateDto, String traceId, UUID userId) {
        log.info("[LibraryService] [traceId={}, userId={}] update library initiate",
                traceId, userId);

        String key = idempotencyService.generateIdempotencyKey("library:update", traceId);

        // Redis Lock으로 멱등성 체크
        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        // 기존 Library 조회 후 정보 갱신
        Library existingLibrary = findById(libraryUpdateDto.getLibraryId());
        existingLibrary.updateLibraryInfo(libraryUpdateDto);

        Library savedLibrary = libraryRepository.save(existingLibrary);

        log.info("[LibraryService] [traceId={}, userId={}] update library success",
                traceId, userId);

        return savedLibrary.getId();
    }

    /**
     * 등록된 도서관 삭제
     *
     * @param libraryId 삭제할 Library Id
     * @param traceId   요청 멱등성 체크용 ID
     * @param userId    요청 사용자 ID
     */
    @Transactional
    public void deleteLibrary(UUID libraryId, String traceId, UUID userId) {
        log.info("[LibraryService] [traceId={}, userId={}] delete library initiate, libraryId={}",
                traceId, userId, libraryId);

        String key = idempotencyService.generateIdempotencyKey("library:delete", traceId);

        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        // 기존 Library 조회 후 삭제
        Library existingLibrary = findById(libraryId);

        libraryRepository.delete(existingLibrary);

        log.info("[LibraryService] [traceId={}, userId={}] delete library success",
                traceId, userId);
    }

    /**
     * 특정 도서관 조회 (단일 객체 반환)
     *
     * @param libraryId 조회할 Library Id
     * @return 변환된 dto
     */
    @Transactional(readOnly = true)
    public LibraryDetailDto getLibrary(UUID userId, UUID libraryId, List<LibraryBook> top5List, List<ReviewListDto> top5Review) {

        Library library = findById(libraryId);

        Boolean isLiked = libraryLikesRepository.existsByLibraryAndUserId(library, userId);

        return LibraryDetailDto.fromEntity(library, top5List, top5Review, isLiked);
    }

    @Transactional(readOnly = true)
    public LibraryDetailDto getMyLibrary(Member member) {
        Library library = findByUserId(member.getId());

        return LibraryDetailDto.fromEntity(library);
    }

    /**
     * 현재 위치를 기준으로 가장 가까운 순으로 도서관을 조회하고 페이지네이션 적용.
     *
     * @param lat      현재위치(위도)
     * @param lng      현재위치(경도)
     * @param name     검색어
     * @param pageable 페이지네이션 정보 (페이지 번호, 크기)
     * @return 페이지네이션된 도서관 정보 DTO Page
     */
    @Transactional(readOnly = true)
    public PageResponse<LibraryDetailDto> getLibraries(UUID userId, Double lat, Double lng, String name, Pageable pageable) {

        Page<LibraryDistanceProjection> libraryPage = libraryRepository.findLibrariesOrderByDistance(lat, lng, name, pageable);
        List<UUID> libraryIds = libraryPage.getContent().stream()
                .map(p -> p.getLibrary().getId())
                .toList();

        List<LibraryBook> allLibraryBooks = libraryBookService.findTop5BooksList(libraryIds);

        Map<UUID, List<LibraryBook>> topBooksMap = allLibraryBooks.stream()
                .collect(Collectors.groupingBy(
                        lb -> lb.getLibrary().getId(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .limit(5)
                                        .collect(Collectors.toList())
                        )
                ));
        Page<LibraryDetailDto> dtoPage = libraryPage.map(projection -> {
            Library library = projection.getLibrary();
            Double distance = projection.getDistance();

            List<LibraryBook> top5List = topBooksMap.getOrDefault(library.getId(), Collections.emptyList());

            Boolean isLiked = libraryLikesRepository.existsByLibraryAndUserId(library, userId);

            return LibraryDetailDto.fromEntity(library, distance, top5List, isLiked);
        });

        return PageResponse.from(dtoPage);
    }

    /**
     * Library 엔티티 DB 저장
     *
     * @param library 저장할 Library 엔티티
     * @return 저장된 Library Entity
     */
    public Library save(Library library) {
        return libraryRepository.save(library);
    }

    /**
     * Id로 Library 조회
     *
     * @param libraryId 조회할 Library의 Id
     * @return Library Entity
     */
    public Library findById(UUID libraryId) {
        return libraryRepository.findById(libraryId)
                .orElseThrow(() -> new CustomException(ErrorCode.LIBRARY_NOT_FOUND));
    }

    public Library findByUserId(UUID userId) {
        return libraryRepository.findByMemberId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.LIBRARY_NOT_FOUND));
    }

    /**
     * 좋아요 누르기
     */
    public void likeLibrary(UUID libraryId, Member member) {
        Library library = findById(libraryId);

        if (libraryLikesRepository.existsByLibraryAndUserId(library, member.getId())) {
            throw new CustomException(ErrorCode.LIBRARY_ALREADY_LIKE);
        }

        LibraryLikes newLike = LibraryLikes.create(library, member.getId());

        library.like();
        libraryLikesRepository.save(newLike);

    }

    /**
     * 좋아요 취소
     */
    public void unlikeLibrary(UUID libraryId, Member member) {
        Library library = findById(libraryId);

        LibraryLikes existingLike = libraryLikesRepository.findByLibraryAndUserId(library, member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.LIBRARY_LIKE_NOT_FOUND));

        library.unlike();
        libraryLikesRepository.delete(existingLike);
    }

    public Page<Library> findLikedLibraries(UUID userId, Pageable pageable) {
        Page<LibraryLikes> libraryLikes = libraryLikesRepository.findAllByUserId(userId, pageable);

        return libraryLikes.map(LibraryLikes::getLibrary);
    }

    public PageResponse<LibraryDetailDto> getLikedLibraries(Member member, Pageable pageable) {

        Page<Library> libraryPage = findLikedLibraries(member.getId(), pageable);

        Page<LibraryDetailDto> dtoPage = libraryPage.map(LibraryDetailDto::fromEntity);
        return PageResponse.from(dtoPage);
    }
}
