package com.bookbook.booklink.library_service.service;

import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.service.IdempotencyService;
import com.bookbook.booklink.library_service.event.LibraryLockEvent;
import com.bookbook.booklink.library_service.model.Library;
import com.bookbook.booklink.library_service.model.dto.request.LibraryRegDto;
import com.bookbook.booklink.library_service.model.dto.request.LibraryUpdateDto;
import com.bookbook.booklink.library_service.model.dto.response.LibraryDetailDto;
import com.bookbook.booklink.library_service.repository.LibraryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

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
    private final IdempotencyService idempotencyService;

    /**
     * 새로운 Library 등록
     * <p>
     * 동일 traceId 요청 시 중복 처리 방지를 위해 Redis Lock 체크 수행
     *
     * @param libraryRegDto Library 등록 정보 DTO
     * @param traceId       요청 멱등성 체크용 ID (클라이언트 전달)
     * @param userId        요청 사용자 ID
     * @return 등록된 Library ID
     */
    @Transactional
    public UUID registerLibrary(LibraryRegDto libraryRegDto, String traceId, UUID userId) {
        log.info("[LibraryService] [traceId={}, userId={}] register library initiate, name={}",
                traceId, userId, libraryRegDto.getName());

        String key = idempotencyService.generateIdempotencyKey("library:register", traceId);

        // Redis Lock으로 멱등성 체크
        idempotencyService.checkIdempotency(key, 1,
                () -> LibraryLockEvent.builder().key(key).build());

        // Library 엔티티 생성 후 DB 저장
        Library newLibrary = Library.toEntity(libraryRegDto);
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
                () -> LibraryLockEvent.builder().key(key).build());

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
                () -> LibraryLockEvent.builder().key(key).build());

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
    public LibraryDetailDto getLibrary(UUID libraryId) {

        Library library = findById(libraryId);
        return LibraryDetailDto.fromEntity(library);
    }

    /**
     * 내 주변 3km 이내의 도서관 조회 (리스트 반환)
     *
     * @param lat 현재위치(위도)
     * @param lng 현재위치(경도)
     * @return 현재위치로부터 3km 이내의 도서관 정보 리스트
     */
    @Transactional(readOnly = true)
    public List<LibraryDetailDto> getLibraries(Double lat, Double lng) {

        List<Library> libraries = libraryRepository.findNearbyLibraries(lat, lng);

        return libraries.stream().map(LibraryDetailDto::fromEntity).toList();
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
}
