package com.bookbook.booklink.community.group_service.controller.docs;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.common.dto.BaseResponse;
import com.bookbook.booklink.common.exception.ApiErrorResponses;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.community.group_service.model.dto.request.GroupCreateDto;
import com.bookbook.booklink.community.group_service.model.dto.response.GroupDetailDto;
import com.bookbook.booklink.community.group_service.model.dto.response.GroupListDto;
import com.bookbook.booklink.community.group_service.model.dto.response.ParticipantMemberListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/group")
@Tag(name = "모임(Group) 관리", description = "독서 모임의 생성, 수정, 조회, 참여 및 멤버 관리를 담당하는 API")
public interface GroupApiDocs {

    @Operation(summary = "새 모임 생성", description = "새로운 독서 모임을 생성하고 현재 사용자를 호스트로 등록합니다.")
    @ApiErrorResponses({
            ErrorCode.VALIDATION_FAILED,
            ErrorCode.DATABASE_ERROR
    })
    @PostMapping
    ResponseEntity<BaseResponse<Boolean>> createGroup(
            @Valid @RequestBody GroupCreateDto groupCreateDto,
            @RequestHeader("Trace-Id") String traceId,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "모임 정보 수정", description = "특정 모임의 정보를 수정합니다. (호스트만 가능)")
    @ApiErrorResponses({
            ErrorCode.VALIDATION_FAILED,
            ErrorCode.METHOD_UNAUTHORIZED,
            ErrorCode.GROUP_NOT_FOUND,
            ErrorCode.DATABASE_ERROR
    })
    @PutMapping("/{id}")
    ResponseEntity<BaseResponse<Boolean>> updateGroup(
            @Parameter(description = "수정할 모임 ID") @PathVariable UUID id,
            @Valid @RequestBody GroupCreateDto groupCreateDto,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "모임 목록 조회/검색", description = "전체 모임 목록을 조회하거나 이름으로 모임을 검색합니다.")
    @ApiErrorResponses({
            ErrorCode.DATABASE_ERROR
    })
    @GetMapping
    ResponseEntity<BaseResponse<List<GroupListDto>>> getGroups(
            @Parameter(description = "검색할 모임 이름 (선택 사항)", required = false) @RequestParam(required = false) String name
    );

    @Operation(summary = "모임 상세 정보 조회", description = "특정 모임의 상세 정보를 조회합니다. 비공개 모임은 멤버만 조회 가능합니다.")
    @ApiErrorResponses({
            ErrorCode.GROUP_NOT_FOUND,
            ErrorCode.NOT_GROUP_MEMBER
    })
    @GetMapping("/{id}")
    ResponseEntity<BaseResponse<GroupDetailDto>> getGroup(
            @Parameter(description = "조회할 모임 ID") @PathVariable UUID id,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "모임 삭제", description = "특정 모임을 삭제합니다. (호스트만 가능)")
    @ApiErrorResponses({
            ErrorCode.GROUP_NOT_FOUND,
            ErrorCode.METHOD_UNAUTHORIZED,
            ErrorCode.DATABASE_ERROR
    })
    @DeleteMapping("/{id}")
    ResponseEntity<BaseResponse<Boolean>> deleteGroup(
            @Parameter(description = "삭제할 모임 ID") @PathVariable UUID id,
            @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "모임 가입", description = "특정 모임에 참여합니다. 비공개 모임은 비밀번호가 필요합니다.")
    @ApiErrorResponses({
            ErrorCode.GROUP_NOT_FOUND,
            ErrorCode.ALREADY_GROUP_MEMBER,
            ErrorCode.GROUP_IS_FULL,
            ErrorCode.INVALID_GROUP_PASSWORD,
            ErrorCode.DATABASE_ERROR
    })
    @PostMapping("/join/{id}")
    ResponseEntity<BaseResponse<Boolean>> addMember(
            @Parameter(description = "가입할 모임 ID") @PathVariable UUID id,
            @Parameter(description = "모임 비밀번호 (비공개 모임인 경우)") @RequestParam(required = false) String password,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "모임 탈퇴", description = "특정 모임에서 자진 탈퇴합니다. (호스트는 불가)")
    @ApiErrorResponses({
            ErrorCode.GROUP_NOT_FOUND,
            ErrorCode.HOST_CANNOT_LEAVE,
            ErrorCode.PARTICIPANT_NOT_FOUND,
            ErrorCode.DATABASE_ERROR
    })
    @DeleteMapping("/leave/{id}")
    ResponseEntity<BaseResponse<Boolean>> removeMember(
            @Parameter(description = "탈퇴할 모임 ID") @PathVariable UUID id,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "모임 멤버 목록 조회", description = "특정 모임에 참여하고 있는 멤버 목록을 조회합니다.")
    @ApiErrorResponses({
            ErrorCode.GROUP_NOT_FOUND,
            ErrorCode.DATABASE_ERROR
    })
    @GetMapping("/{id}/members")
    ResponseEntity<BaseResponse<List<ParticipantMemberListDto>>> getGroupMembers(
            @Parameter(description = "모임 ID") @PathVariable UUID id,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "내가 참여 중인 모임 목록 조회", description = "현재 사용자가 멤버로 참여하고 있는 모임 목록을 조회합니다.")
    @ApiErrorResponses({
            ErrorCode.DATABASE_ERROR
    })
    @GetMapping("/my")
    ResponseEntity<BaseResponse<List<GroupListDto>>> getMyGroups(
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member member
    );

    @Operation(summary = "호스트 권한 위임", description = "모임의 호스트 권한을 다른 멤버에게 위임합니다. (현재 호스트만 가능)")
    @ApiErrorResponses({
            ErrorCode.GROUP_NOT_FOUND,
            ErrorCode.METHOD_UNAUTHORIZED,
            ErrorCode.PARTICIPANT_NOT_FOUND,
            ErrorCode.DATABASE_ERROR
    })
    @PutMapping("/{id}/host")
    ResponseEntity<BaseResponse<Boolean>> transferHost(
            @Parameter(description = "모임 ID") @PathVariable UUID id,
            @Parameter(description = "새로운 호스트의 사용자 ID") @RequestParam UUID newHostId,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member currentHost
    );

    @Operation(summary = "멤버 강제 퇴장", description = "호스트가 특정 멤버를 모임에서 강제로 퇴장시킵니다. (호스트만 가능)")
    @ApiErrorResponses({
            ErrorCode.GROUP_NOT_FOUND,
            ErrorCode.METHOD_UNAUTHORIZED,
            ErrorCode.PARTICIPANT_NOT_FOUND,
            ErrorCode.HOST_CANNOT_LEAVE,
            ErrorCode.DATABASE_ERROR
    })
    @DeleteMapping("/remove/{groupId}")
    ResponseEntity<BaseResponse<Boolean>> forceRemoveMember(
            @Parameter(description = "모임 ID") @PathVariable UUID groupId,
            @Parameter(description = "강제 퇴장시킬 사용자 ID") @RequestParam UUID userId,
            @Parameter(hidden = true) @AuthenticationPrincipal(expression = "member") Member host
    );
}