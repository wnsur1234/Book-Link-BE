package com.bookbook.booklink.community.group_service.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.chat_service.group.service.GroupChatsService;
import com.bookbook.booklink.common.event.LockEvent;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.service.IdempotencyService;
import com.bookbook.booklink.community.group_service.model.Group;
import com.bookbook.booklink.community.group_service.model.GroupMember;
import com.bookbook.booklink.community.group_service.model.dto.request.GroupCreateDto;
import com.bookbook.booklink.community.group_service.model.dto.response.GroupDetailDto;
import com.bookbook.booklink.community.group_service.model.dto.response.GroupListDto;
import com.bookbook.booklink.community.group_service.model.dto.response.ParticipantMemberListDto;
import com.bookbook.booklink.community.group_service.repository.GroupMemberRepository;
import com.bookbook.booklink.community.group_service.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {
    private final IdempotencyService idempotencyService;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupChatsService groupChatsService;
    /**
     * 새로운 모임을 생성하고 생성자를 해당 모임의 호스트 및 멤버로 등록합니다.
     *
     * @param groupCreateDto 모임 생성에 필요한 데이터 (이름, 설명, 비밀번호 등)
     * @param member         모임을 생성하는 현재 사용자 (호스트)
     */
    @Transactional
    public void createGroup(GroupCreateDto groupCreateDto, Member member, String traceId) {
        String key = idempotencyService.generateIdempotencyKey("group:create", traceId);

        log.info("[GroupService] [traceId={}, userId={}] create group initiate, name={}",
                traceId, member.getId(), groupCreateDto.getName());

        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        // 비밀번호 인코딩
        String encodedPassword = (groupCreateDto.getPassword() != null && !groupCreateDto.getPassword().trim().isEmpty())
                ? passwordEncoder.encode(groupCreateDto.getPassword())
                : null;

        Group newGroup = Group.toEntity(groupCreateDto, encodedPassword, member);
        Group savedGroup = groupRepository.save(newGroup);

        // 생성자를 모임 멤버로 추가 (호스트)
        GroupMember hostMember = GroupMember.addMember(savedGroup, member);
        groupMemberRepository.save(hostMember);

        log.info("[GroupService] [traceId={}, userId={}] create group success, groupId={}",
                traceId, member.getId(), savedGroup.getId());

        // 단톡 생성
        groupChatsService.createGroupChatRoom(savedGroup);
    }


    /**
     * 모임 정보를 수정합니다. 오직 호스트만 수정할 수 있습니다.
     *
     * @param groupId        수정할 모임의 UUID
     * @param groupCreateDto 수정할 모임 데이터
     * @param member         현재 사용자 (호스트 여부 확인용)
     * @throws CustomException 호스트가 아니거나 모임을 찾을 수 없을 때
     */
    @Transactional
    public void updateGroup(UUID groupId, GroupCreateDto groupCreateDto, Member member) {
        log.info("[GroupService] [userId={}] update group initiate, groupId={}", member.getId(), groupId);

        Group existingGroup = findGroupById(groupId);

        // 호스트 권한 검증
        validateHostAuthority(existingGroup, member);

        // 비밀번호가 존재하면 인코딩하여 업데이트
        String encodedPassword = (groupCreateDto.getPassword() != null && !groupCreateDto.getPassword().trim().isEmpty())
                ? passwordEncoder.encode(groupCreateDto.getPassword())
                : null;

        existingGroup.update(groupCreateDto, encodedPassword);

        log.info("[GroupService] [userId={}] update group success, groupId={}", member.getId(), groupId);
    }

    /**
     * 모임을 삭제합니다.
     *
     * @param groupId 삭제할 모임의 UUID
     * @throws CustomException 모임을 찾을 수 없을 때
     */
    @Transactional
    public void deleteGroup(UUID groupId, Member member) {
        log.info("[GroupService] delete group initiate, groupId={}", groupId);
        Group group = findGroupById(groupId);
        validateHostAuthority(group, member);
        groupRepository.delete(group);

        log.info("[GroupService] delete group success, groupId={}", groupId);
    }

    /**
     * 사용자를 모임에 참여시킵니다.
     * 인원 제한, 중복 가입, 비밀번호 일치 여부를 검사합니다.
     *
     * @param groupId  참여할 모임의 UUID
     * @param password 모임 비밀번호 (비공개 모임일 경우 필요)
     * @param member   참여하는 현재 사용자
     * @throws CustomException 이미 멤버이거나, 인원 초과, 비밀번호 불일치 시
     */
    @Transactional
    public void addParticipantToGroup(UUID groupId, String password, Member member) {
        log.info("[GroupService] [userId={}] add participant initiate, groupId={}", member.getId(), groupId);

        Group group = findGroupById(groupId);

        if (isMember(group, member)) {
            log.warn("[GroupService] [userId={}] Already member of group, groupId={}", member.getId(), groupId);
            throw new CustomException(ErrorCode.ALREADY_GROUP_MEMBER);
        }
        if (group.getParticipantCount() >= group.getMaxCapacity()) {
            log.warn("[GroupService] [userId={}] Group is full, groupId={}", member.getId(), groupId);
            throw new CustomException(ErrorCode.GROUP_IS_FULL);
        }

        // 비공개 모임이고, 비밀번호가 일치하지 않으면 예외 발생
        if (group.getIsPrivate() && !passwordEncoder.matches(password, group.getPassword())) {
            log.warn("[GroupService] [userId={}] Invalid group password for private group, groupId={}", member.getId(), groupId);
            throw new CustomException(ErrorCode.INVALID_GROUP_PASSWORD);
        }

        GroupMember newGroupMember = GroupMember.addMember(group, member);
        group.addParticipant(); // 참여 인원수 증가
        groupMemberRepository.save(newGroupMember);

        log.info("[GroupService] [userId={}] add participant success, groupId={}", member.getId(), groupId);
    }

    /**
     * 사용자가 모임에서 스스로 탈퇴합니다. 호스트는 탈퇴할 수 없습니다.
     *
     * @param groupId 탈퇴할 모임의 UUID
     * @param member  탈퇴하는 현재 사용자
     * @throws CustomException 모임 멤버가 아니거나, 호스트가 탈퇴를 시도할 때
     */
    @Transactional
    public void removeParticipantFromGroup(UUID groupId, Member member) {
        log.info("[GroupService] [userId={}] remove participant initiate (self-leave), groupId={}", member.getId(), groupId);

        Group group = findGroupById(groupId);

        if (isHost(group, member)) {
            log.warn("[GroupService] [userId={}] Host cannot leave the group, groupId={}", member.getId(), groupId);
            throw new CustomException(ErrorCode.HOST_CANNOT_LEAVE);
        }

        GroupMember participant = groupMemberRepository.findByGroupAndMember(group, member)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTICIPANT_NOT_FOUND));

        groupMemberRepository.delete(participant);
        group.removeParticipant(); // 참여 인원수 감소

        log.info("[GroupService] [userId={}] remove participant success (self-leave), groupId={}", member.getId(), groupId);
    }

    /**
     * 호스트가 특정 멤버를 모임에서 강제로 퇴장시킵니다.
     *
     * @param groupId        강제 퇴장할 모임의 UUID
     * @param host           강제 퇴장을 시도하는 호스트 사용자
     * @param memberToRemove 강제 퇴장 대상 사용자
     * @throws CustomException 시도하는 사용자가 호스트가 아니거나, 퇴장 대상이 호스트일 때
     */
    @Transactional
    public void forceRemoveParticipantFromGroup(UUID groupId, Member host, Member memberToRemove) {
        log.info("[GroupService] [hostId={}] force remove participant initiate, groupId={}, targetId={}",
                host.getId(), groupId, memberToRemove.getId());

        Group group = findGroupById(groupId);

        // 호스트 권한 검증
        validateHostAuthority(group, host);

        // 퇴장 대상이 호스트인지 확인 (호스트는 강퇴 불가)
        if (isHost(group, memberToRemove)) {
            log.warn("[GroupService] [hostId={}] Host tried to remove another host (or self) forcefully, groupId={}",
                    host.getId(), groupId);
            throw new CustomException(ErrorCode.HOST_CANNOT_LEAVE);
        }

        GroupMember participant = groupMemberRepository.findByGroupAndMember(group, memberToRemove)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTICIPANT_NOT_FOUND));

        groupMemberRepository.delete(participant);
        group.removeParticipant(); // 참여 인원수 감소

        log.info("[GroupService] [hostId={}] force remove participant success, groupId={}, targetId={}",
                host.getId(), groupId, memberToRemove.getId());
    }

    /**
     * 특정 모임의 모든 멤버 목록을 조회합니다.
     *
     * @param groupId 모임의 UUID
     * @return 멤버 목록 DTO (ID, 이름)
     */
    @Transactional(readOnly = true)
    public List<ParticipantMemberListDto> getGroupMembers(UUID groupId) {
        Group group = findGroupById(groupId);
        return groupMemberRepository.findAllByGroup(group).stream()
                .map(ParticipantMemberListDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 현재 사용자가 참여하고 있는 모임 목록을 조회합니다.
     *
     * @param member 현재 사용자
     * @return 참여 중인 모임 목록 DTO
     */
    @Transactional(readOnly = true)
    public List<GroupListDto> getMyGroups(Member member) {
        return groupMemberRepository.findAllGroupByMember(member).stream()
                .map(GroupListDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 모임의 상세 정보를 조회합니다.
     * 비공개 모임일 경우, 멤버만 상세 정보를 볼 수 있습니다.
     *
     * @param groupId 모임의 UUID
     * @param member  현재 사용자
     * @return 모임 상세 정보 DTO
     * @throws CustomException 비공개 모임인데 멤버가 아닐 경우
     */
    @Transactional(readOnly = true)
    public GroupDetailDto getGroupDetail(UUID groupId, Member member) {
        Group group = findGroupById(groupId);
        boolean alreadyJoined = isMember(group, member);

        // 비공개 모임인데 멤버가 아니면 접근 불가
        if (group.getIsPrivate() && !alreadyJoined) {
            throw new CustomException(ErrorCode.NOT_GROUP_MEMBER);
        }

        // 멤버인 경우에만 멤버 목록을 함께 반환
        List<ParticipantMemberListDto> memberList = alreadyJoined ? getGroupMembers(groupId) : null;

        return GroupDetailDto.fromEntity(group, memberList);
    }

    /**
     * 모임 목록을 조회합니다. 모임 이름으로 검색할 수 있습니다.
     *
     * @param groupName 검색할 모임 이름 (null 또는 비어 있으면 전체 조회)
     * @return 모임 목록 DTO
     */
    @Transactional(readOnly = true)
    public List<GroupListDto> getGroups(String groupName) {
        List<Group> groupList;
        String trimmedName = (groupName != null) ? groupName.trim() : null;

        if (trimmedName == null || trimmedName.isEmpty()) {
            groupList = groupRepository.findAll();
        } else {
            groupList = groupRepository.findAllByNameContaining(trimmedName);
        }

        return groupList.stream()
                .map(GroupListDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 모임의 호스트 권한을 다른 멤버에게 위임합니다.
     * 오직 현재 호스트만 위임할 수 있습니다.
     *
     * @param groupId     모임의 UUID
     * @param currentHost 현재 호스트
     * @param newHost     새로운 호스트가 될 멤버
     * @throws CustomException 현재 사용자가 호스트가 아닐 경우
     */
    @Transactional
    public void transferHost(UUID groupId, Member currentHost, Member newHost) {
        log.info("[GroupService] [hostId={}] transfer host initiate, groupId={}, newHostId={}",
                currentHost.getId(), groupId, newHost.getId());

        Group group = findGroupById(groupId);

        // 현재 사용자가 호스트인지 검증
        validateHostAuthority(group, currentHost);

        // 새로운 호스트가 그룹의 멤버인지 검증
        validateMemberAuthority(group, newHost);

        group.transferHost(newHost);

        log.info("[GroupService] [hostId={}] transfer host success, groupId={}, newHostId={}",
                currentHost.getId(), groupId, newHost.getId());
    }

    /**
     * ID를 사용하여 모임을 조회합니다. 없으면 예외를 발생시킵니다.
     *
     * @param id 모임의 UUID
     * @return 조회된 Group 엔티티
     * @throws CustomException 모임을 찾을 수 없을 때 (GROUP_NOT_FOUND)
     */
    public Group findGroupById(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.GROUP_NOT_FOUND));
    }

    /**
     * 특정 사용자가 모임의 호스트인지 확인합니다.
     *
     * @param group  확인할 모임 엔티티
     * @param member 확인할 사용자 엔티티
     * @return 호스트이면 true, 아니면 false
     */
    public Boolean isHost(Group group, Member member) {
        return group.getHostId().equals(member.getId());
    }

    /**
     * 특정 사용자가 모임의 멤버인지 확인합니다.
     *
     * @param group  확인할 모임 엔티티
     * @param member 확인할 사용자 엔티티
     * @return 멤버이면 true, 아니면 false
     */
    public Boolean isMember(Group group, Member member) {
        return groupMemberRepository.existsByGroupAndMember(group, member);
    }

    /**
     * 특정 사용자가 모임의 멤버가 아닐 경우 {@code NOT_GROUP_MEMBER} 예외를 발생시킵니다.
     *
     * @param group  확인할 모임 엔티티
     * @param member 확인할 사용자 엔티티 (멤버여야 함)
     * @throws CustomException 멤버가 아닐 경우
     */
    public void validateMemberAuthority(Group group, Member member) {
        if (!isMember(group, member)) {
            throw new CustomException(ErrorCode.NOT_GROUP_MEMBER);
        }
    }

    /**
     * 특정 사용자가 모임의 호스트가 아닐 경우 {@code METHOD_UNAUTHORIZED} 예외를 발생시킵니다.
     *
     * @param group  확인할 모임 엔티티
     * @param member 확인할 사용자 엔티티 (호스트여야 함)
     * @throws CustomException 호스트가 아닐 경우
     */
    public void validateHostAuthority(Group group, Member member) {
        if (!isHost(group, member)) {
            throw new CustomException(ErrorCode.METHOD_UNAUTHORIZED);
        }
    }
}