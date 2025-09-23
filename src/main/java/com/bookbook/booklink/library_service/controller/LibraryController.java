package com.bookbook.booklink.library_service.controller;

import com.bookbook.booklink.common.exception.BaseResponse;
import com.bookbook.booklink.library_service.model.dto.request.LibraryRegDto;
import com.bookbook.booklink.library_service.model.dto.request.LibraryUpdateDto;
import com.bookbook.booklink.library_service.model.dto.response.LibraryDetailDto;
import com.bookbook.booklink.library_service.service.LibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/library")
@Tag(name = "Library API", description = "도서관 등록/조회/수정 관련 API")
public class LibraryController {
    private final LibraryService libraryService;


    @Operation(
            summary = "도서관 등록",
            description = "사용자 계정에 새로운 도서관을 등록합니다. " +
                    "하나의 계정당 도서관은 하나만 등록 가능합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "도서관 등록 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "successResponse",
                                            value = BaseResponse.SUCCESS_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "입력값 오류로 인한 예외",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "errorResponse",
                                            value = BaseResponse.ERROR_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류로 인한 예외",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "errorResponse",
                                            value = BaseResponse.ERROR_RESPONSE
                                    )
                            )
                    )
            }
    )
    @PostMapping
    public ResponseEntity<BaseResponse<UUID>> registerLibrary(
            @Valid @RequestBody LibraryRegDto libraryRegDto,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = UUID.randomUUID(); // todo: 실제 인증 정보에서 추출

        log.info("[LibraryController] [traceId = {}, userId = {}] register library request received, name={}",
                traceId, userId, libraryRegDto.getName());

        UUID savedLibraryId = libraryService.registerLibrary(libraryRegDto, traceId, userId);

        log.info("[LibraryController] [traceId = {}, userId = {}] register library response success, libraryId={}",
                traceId, userId, savedLibraryId);
        return ResponseEntity.ok()
                .body(BaseResponse.success(savedLibraryId));
    }

    @Operation(
            summary = "도서관 수정",
            description = "사용자 계정에 등록된 도서관을 수정합니다. ",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "도서관 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "successResponse",
                                            value = BaseResponse.SUCCESS_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "입력값 오류로 인한 예외",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "errorResponse",
                                            value = BaseResponse.ERROR_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류로 인한 예외",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "errorResponse",
                                            value = BaseResponse.ERROR_RESPONSE
                                    )
                            )
                    )
            }
    )
    @PutMapping
    public ResponseEntity<BaseResponse<UUID>> updateLibrary(
            @Valid @RequestBody LibraryUpdateDto libraryUpdateDto,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = UUID.randomUUID(); // todo: 실제 인증 정보에서 추출
        log.info("[LibraryController] [traceId = {}, userId = {}] update library request received, libraryId={}",
                traceId, userId, libraryUpdateDto.getLibraryId());

        UUID updatedLibraryId = libraryService.updateLibrary(libraryUpdateDto, traceId, userId);

        log.info("[LibraryController] [traceId = {}, userId = {}] update library response success, libraryId={}",
                traceId, userId, updatedLibraryId);
        return ResponseEntity.ok()
                .body(BaseResponse.success(updatedLibraryId));
    }

    @Operation(
            summary = "도서관 삭제",
            description = "사용자 계정에 등록된 도서관을 삭제합니다. ",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "도서관 삭제 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "successResponse",
                                            value = BaseResponse.SUCCESS_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "입력값 오류로 인한 예외",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "errorResponse",
                                            value = BaseResponse.ERROR_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류로 인한 예외",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "errorResponse",
                                            value = BaseResponse.ERROR_RESPONSE
                                    )
                            )
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Boolean>> deleteLibrary(
            @PathVariable @NotNull(message = "수정할 도서관의 ID는 필수입니다.") UUID id,
            @RequestHeader("Trace-Id") String traceId
    ) {
        UUID userId = UUID.randomUUID(); // todo: 실제 인증 정보에서 추출
        log.info("[LibraryController] [traceId = {}, userId = {}] delete library request received, libraryId={}",
                traceId, userId, id);

        libraryService.deleteLibrary(id, traceId, userId);

        log.info("[LibraryController] [traceId = {}, userId = {}] delete library response success, libraryId={}",
                traceId, userId, id);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Operation(
            summary = "특정 도서관 조회 (단일 객체 반환)",
            description = "특정 도서관의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "도서관 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "successResponse",
                                            value = BaseResponse.SUCCESS_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "입력값 오류로 인한 예외",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "errorResponse",
                                            value = BaseResponse.ERROR_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류로 인한 예외",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "errorResponse",
                                            value = BaseResponse.ERROR_RESPONSE
                                    )
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<LibraryDetailDto>> getLibrary(
            @PathVariable @NotNull(message = "조회할 도서관의 ID는 필수입니다.") UUID id
    ) {
        LibraryDetailDto libraryDetailDto = libraryService.getLibrary(id);

        return ResponseEntity.ok()
                .body(BaseResponse.success(libraryDetailDto));
    }

    @Operation(
            summary = "내 주변 3km 이내의 도서관 조회 (리스트 반환)",
            description = "내 주변 3km 이내의 도서관을 조회해 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "successResponse",
                                            value = BaseResponse.SUCCESS_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "입력값 오류로 인한 예외",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "errorResponse",
                                            value = BaseResponse.ERROR_RESPONSE
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 오류로 인한 예외",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = BaseResponse.class),
                                    examples = @ExampleObject(
                                            name = "errorResponse",
                                            value = BaseResponse.ERROR_RESPONSE
                                    )
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<BaseResponse<List<LibraryDetailDto>>> getLibraries(
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {

        List<LibraryDetailDto> result = libraryService.getLibraries(lat, lng);

        return ResponseEntity.ok()
                .body(BaseResponse.success(result));
    }
}
