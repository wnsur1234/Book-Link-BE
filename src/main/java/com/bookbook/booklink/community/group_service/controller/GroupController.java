package com.bookbook.booklink.community.group_service.controller;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.auth_service.service.MemberService;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.community.group_service.controller.docs.GroupApiDocs;
import com.bookbook.booklink.community.group_service.model.dto.request.GroupCreateDto;
import com.bookbook.booklink.community.group_service.model.dto.response.GroupDetailDto;
import com.bookbook.booklink.community.group_service.model.dto.response.GroupListDto;
import com.bookbook.booklink.community.group_service.model.dto.response.ParticipantMemberListDto;
import com.bookbook.booklink.community.group_service.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GroupController implements GroupApiDocs {
    private final MemberService memberService;
    private final GroupService groupService;

    @Override
    public ResponseEntity<BaseResponse<Boolean>> createGroup(
            @Valid @RequestBody GroupCreateDto groupCreateDto,
            @RequestHeader("Trace-Id") String traceId,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[GroupController] [traceId={}, userId={}] create group request, name={}",
                traceId, userId, groupCreateDto.getName());

        groupService.createGroup(groupCreateDto, member, traceId);

        log.info("[GroupController] [traceId={}, userId={}] create group success, name={}",
                traceId, userId, groupCreateDto.getName());
        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> updateGroup(
            @PathVariable UUID id,
            @Valid @RequestBody GroupCreateDto groupCreateDto,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[GroupController] [userId={}] update group request, groupId={}", userId, id);

        groupService.updateGroup(id, groupCreateDto, member);

        log.info("[GroupController] [userId={}] update group success, groupId={}", userId, id);
        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<List<GroupListDto>>> getGroups(
            @RequestParam(required = false) String name
    ) {
        return ResponseEntity.ok()
                .body(BaseResponse.success(groupService.getGroups(name)));
    }

    @Override
    public ResponseEntity<BaseResponse<GroupDetailDto>> getGroup(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        return ResponseEntity.ok()
                .body(BaseResponse.success(groupService.getGroupDetail(id, member)));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> deleteGroup(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[GroupController] [userId={}] delete group request, groupId={}", userId, id);

        groupService.deleteGroup(id, member);

        log.info("[GroupController] [userId={}] delete group success, groupId={}", userId, id);
        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> addMember(
            @PathVariable UUID id,
            @RequestParam(required = false) String password,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[GroupController] [userId={}] add member request, groupId={}", userId, id);

        groupService.addParticipantToGroup(id, password, member);

        log.info("[GroupController] [userId={}] add member success, groupId={}", userId, id);
        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> removeMember(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        UUID userId = member.getId();
        log.info("[GroupController] [userId={}] remove member request (self-leave), groupId={}", userId, id);

        groupService.removeParticipantFromGroup(id, member);

        log.info("[GroupController] [userId={}] remove member success (self-leave), groupId={}", userId, id);
        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<List<ParticipantMemberListDto>>> getGroupMembers(
            @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        return ResponseEntity.ok()
                .body(BaseResponse.success(groupService.getGroupMembers(id)));
    }

    @Override
    public ResponseEntity<BaseResponse<List<GroupListDto>>> getMyGroups(
            @AuthenticationPrincipal(expression = "member") Member member
    ) {
        return ResponseEntity.ok()
                .body(BaseResponse.success(groupService.getMyGroups(member)));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> transferHost(
            @PathVariable UUID id,
            @RequestParam UUID newHostId,
            @AuthenticationPrincipal(expression = "member") Member currentHost
    ) {
        UUID currentHostId = currentHost.getId();
        log.info("[GroupController] [hostId={}] transfer host request, groupId={}, newHostId={}",
                currentHostId, id, newHostId);

        Member newHost = memberService.getMemberOrThrow(newHostId);
        groupService.transferHost(id, currentHost, newHost);

        log.info("[GroupController] [hostId={}] transfer host success, groupId={}, newHostId={}",
                currentHostId, id, newHostId);
        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean>> forceRemoveMember(
            @PathVariable UUID groupId,
            @RequestParam UUID userId,
            @AuthenticationPrincipal(expression = "member") Member host
    ) {
        UUID hostId = host.getId();
        log.info("[GroupController] [hostId={}] force remove member request, groupId={}, targetUserId={}",
                hostId, groupId, userId);

        Member memberToRemove = memberService.getMemberOrThrow(userId);
        groupService.forceRemoveParticipantFromGroup(groupId, host, memberToRemove);

        log.info("[GroupController] [hostId={}] force remove member success, groupId={}, targetUserId={}",
                hostId, groupId, userId);
        return ResponseEntity.ok()
                .body(BaseResponse.success(Boolean.TRUE));
    }
}
