package com.bookbook.booklink.community.schedule_service.controller.docs;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.community.schedule_service.model.dto.request.ScheduleCreateDto;
import com.bookbook.booklink.community.schedule_service.model.dto.response.ScheduleDetailDto;
import com.bookbook.booklink.community.schedule_service.model.dto.response.ScheduleListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/community/schedule")
@Tag(name = "일정(Schedule) 관리", description = "모임 내 일정 생성, 수정, 조회, 참여 관리를 담당하는 API")
public interface ScheduleApiDocs {

    @Operation(summary = "모임 일정 목록 조회", description = "특정 모임의 전체 일정을 조회합니다. 모임 멤버만 접근 가능합니다.")
    @ApiErrorResponses({
            ErrorCode.GROUP_NOT_FOUND,
            ErrorCode.NOT_GROUP_MEMBER,
            ErrorCode.DATABASE_ERROR
    })
    @GetMapping("/{groupId}")
    ResponseEntity<BaseResponse<List<ScheduleListDto>>> getGroupSchedules(
            @Parameter(description = "일정을 조회할 모임 ID") @PathVariable UUID groupId,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "일정 상세 조회", description = "특정 일정의 상세 정보를 조회합니다.")
    @ApiErrorResponses({
            ErrorCode.SCHEDULE_NOT_FOUND,
            ErrorCode.DATABASE_ERROR
    })
    @GetMapping("/{id}")
    ResponseEntity<BaseResponse<ScheduleDetailDto>> getSchedule(
            @Parameter(description = "조회할 일정 ID") @PathVariable UUID id,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "새 일정 생성", description = "특정 모임에 새로운 일정을 등록합니다. 모임 호스트만 가능합니다.")
    @ApiErrorResponses({
            ErrorCode.VALIDATION_FAILED,
            ErrorCode.GROUP_NOT_FOUND,
            ErrorCode.NOT_GROUP_MEMBER,
            ErrorCode.DATABASE_ERROR
    })
    @PostMapping
    ResponseEntity<BaseResponse<Boolean>> createSchedule(
            @Valid @RequestBody ScheduleCreateDto scheduleCreateDto,
            @RequestHeader("Trace-Id") String traceId,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "일정 수정", description = "등록된 일정을 수정합니다. 작성자(호스트)만 가능합니다.")
    @ApiErrorResponses({
            ErrorCode.SCHEDULE_NOT_FOUND,
            ErrorCode.METHOD_UNAUTHORIZED,
            ErrorCode.DATABASE_ERROR
    })
    @PutMapping("/{id}")
    ResponseEntity<BaseResponse<Boolean>> updateSchedule(
            @Parameter(description = "수정할 일정 ID") @PathVariable UUID id,
            @Valid @RequestBody ScheduleCreateDto scheduleCreateDto,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "일정 삭제", description = "등록된 일정을 삭제합니다. 작성자(호스트)만 가능합니다.")
    @ApiErrorResponses({
            ErrorCode.SCHEDULE_NOT_FOUND,
            ErrorCode.METHOD_UNAUTHORIZED,
            ErrorCode.DATABASE_ERROR
    })
    @DeleteMapping("/{id}")
    ResponseEntity<BaseResponse<Boolean>> deleteSchedule(
            @Parameter(description = "삭제할 일정 ID") @PathVariable UUID id,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "일정 참여", description = "특정 일정에 참여합니다. (모임 멤버만 가능)")
    @ApiErrorResponses({
            ErrorCode.SCHEDULE_NOT_FOUND,
            ErrorCode.DATABASE_ERROR
    })
    @PostMapping("/join/{id}")
    ResponseEntity<BaseResponse<Boolean>> addMemberToSchedule(
            @Parameter(description = "참여할 일정 ID") @PathVariable UUID id,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "일정 참여 취소", description = "특정 일정의 참여를 취소합니다.")
    @ApiErrorResponses({
            ErrorCode.SCHEDULE_NOT_FOUND,
            ErrorCode.PARTICIPANT_NOT_FOUND,
            ErrorCode.DATABASE_ERROR
    })
    @DeleteMapping("/leave/{id}")
    ResponseEntity<BaseResponse<Boolean>> removeMemberFromSchedule(
            @Parameter(description = "참여를 취소할 일정 ID") @PathVariable UUID id,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "내가 참여 중인 일정 목록 조회", description = "현재 사용자가 참여하거나 등록한 일정 목록을 조회합니다.")
    @ApiErrorResponses({
            ErrorCode.DATABASE_ERROR
    })
    @GetMapping("/my")
    ResponseEntity<BaseResponse<List<ScheduleListDto>>> getMySchedules(
            @Parameter(hidden = true) @AuthenticationPrincipal Member member
    );
}