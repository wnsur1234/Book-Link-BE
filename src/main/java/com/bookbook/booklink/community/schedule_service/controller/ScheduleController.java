package com.bookbook.booklink.community.schedule_service.controller;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.community.group_service.model.Group;
import com.bookbook.booklink.community.group_service.service.GroupService;
import com.bookbook.booklink.community.schedule_service.controller.docs.ScheduleApiDocs;
import com.bookbook.booklink.community.schedule_service.model.dto.request.ScheduleCreateDto;
import com.bookbook.booklink.community.schedule_service.model.dto.response.ScheduleDetailDto;
import com.bookbook.booklink.community.schedule_service.model.dto.response.ScheduleListDto;
import com.bookbook.booklink.community.schedule_service.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ScheduleController implements ScheduleApiDocs {
    private final ScheduleService scheduleService;
    private final GroupService groupService;

    @Override
    public ResponseEntity<BaseResponse<List<ScheduleListDto>>> getGroupSchedules(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        Group group = groupService.findGroupById(groupId);

        groupService.validateMemberAuthority(group, member);

        return ResponseEntity.ok()
                .body(BaseResponse.success(scheduleService.getGroupSchedules(group, member)));
    }

    @Override
    public ResponseEntity<BaseResponse<ScheduleDetailDto>> getSchedule(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        return ResponseEntity.ok()
                .body(BaseResponse.success(scheduleService.getScheduleDetail(id, member)));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> createSchedule(
            @Valid @RequestBody ScheduleCreateDto scheduleCreateDto,
            @RequestHeader("Trace-Id") String traceId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        UUID groupId = scheduleCreateDto.getGroupId();
        log.info("[ScheduleController] [traceId={}, userId={}] create schedule request, groupId={}, title={}",
                traceId, userId, groupId, scheduleCreateDto.getTitle());

        // 권한 검증 및 Group 로드
        Group group = groupService.findGroupById(groupId);
        groupService.validateHostAuthority(group, member);

        scheduleService.createSchedule(scheduleCreateDto, group, member, traceId);

        log.info("[ScheduleController] [traceId={}, userId={}] create schedule success, groupId={}",
                traceId, userId, groupId);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> updateSchedule(
            @PathVariable UUID id,
            @Valid @RequestBody ScheduleCreateDto scheduleCreateDto,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        UUID groupId = scheduleCreateDto.getGroupId();
        log.info("[ScheduleController] [userId={}] update schedule request, scheduleId={}, groupId={}", userId, id, groupId);

        Group group = groupService.findGroupById(groupId);
        groupService.validateHostAuthority(group, member);

        scheduleService.updateSchedule(id, scheduleCreateDto, member);

        log.info("[ScheduleController] [userId={}] update schedule success, scheduleId={}", userId, id);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> deleteSchedule(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[ScheduleController] [userId={}] delete schedule request, scheduleId={}", userId, id);

        scheduleService.deleteSchedule(id, member);

        log.info("[ScheduleController] [userId={}] delete schedule success, scheduleId={}", userId, id);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> addMemberToSchedule(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[ScheduleController] [userId={}] add member to schedule request, scheduleId={}", userId, id);

        scheduleService.addMemberToSchedule(id, member);

        log.info("[ScheduleController] [userId={}] add member to schedule success, scheduleId={}", userId, id);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> removeMemberFromSchedule(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[ScheduleController] [userId={}] remove member from schedule request, scheduleId={}", userId, id);

        scheduleService.removeMemberFromSchedule(id, member);

        log.info("[ScheduleController] [userId={}] remove member from schedule success, scheduleId={}", userId, id);

        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }


    @Override
    public ResponseEntity<BaseResponse<List<ScheduleListDto>>> getMySchedules(
            @AuthenticationPrincipal Member member
    ) {
        return ResponseEntity.ok()
                .body(BaseResponse.success(scheduleService.getMySchedules(member)));
    }
}