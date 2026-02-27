package com.bookbook.booklink.community.schedule_service.service;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.common.event.LockEvent;
import com.bookbook.booklink.common.exception.CustomException;
import com.bookbook.booklink.common.exception.ErrorCode;
import com.bookbook.booklink.common.service.IdempotencyService;
import com.bookbook.booklink.community.group_service.model.Group;
import com.bookbook.booklink.community.group_service.model.dto.response.ParticipantMemberListDto;
import com.bookbook.booklink.community.group_service.repository.GroupMemberRepository;
import com.bookbook.booklink.community.schedule_service.model.GroupSchedule;
import com.bookbook.booklink.community.schedule_service.model.ScheduleParticipant;
import com.bookbook.booklink.community.schedule_service.model.dto.request.ScheduleCreateDto;
import com.bookbook.booklink.community.schedule_service.model.dto.response.ScheduleDetailDto;
import com.bookbook.booklink.community.schedule_service.model.dto.response.ScheduleListDto;
import com.bookbook.booklink.community.schedule_service.repository.GroupScheduleRepository;
import com.bookbook.booklink.community.schedule_service.repository.ScheduleParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final IdempotencyService idempotencyService;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupScheduleRepository groupScheduleRepository;
    private final ScheduleParticipantRepository scheduleParticipantRepository;


    /**
     * 특정 모임의 모든 일정을 조회합니다.
     */
    public List<ScheduleListDto> getGroupSchedules(Group group, Member member) {

        List<GroupSchedule> groupScheduleList = group.getScheduleList();

        return groupScheduleList.stream()
                .map(ScheduleListDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 일정의 상세 정보를 조회합니다.
     */
    public ScheduleDetailDto getScheduleDetail(UUID id, Member member) {
        GroupSchedule schedule = findGroupScheduleById(id);

        // 해당 모임의 멤버인지 확인 (일정 접근 권한)
        if (!groupMemberRepository.existsByGroupAndMember(schedule.getGroup(), member)) {
            throw new CustomException(ErrorCode.NOT_GROUP_MEMBER);
        }

        List<Member> memberList = scheduleParticipantRepository.findMemberBySchedule(schedule);
        List<ParticipantMemberListDto> memberListDto = memberList.stream()
                .map(ParticipantMemberListDto::fromEntity)
                .toList();

        return ScheduleDetailDto.fromEntity(schedule, memberListDto);
    }

    /**
     * 내가 참여 중인 모든 일정을 조회합니다.
     */
    public List<ScheduleListDto> getMySchedules(Member member) {
        List<GroupSchedule> groupScheduleList = scheduleParticipantRepository.findScheduleByMember(member);

        return groupScheduleList.stream()
                .map(ScheduleListDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 일정을 생성하고 생성자를 참여자로 등록합니다.
     */
    @Transactional
    public void createSchedule(ScheduleCreateDto scheduleCreateDto, Group group, Member member, String traceId) {
        String key = idempotencyService.generateIdempotencyKey("schedule:create", traceId);

        log.info("[ScheduleService] [traceId={}, userId={}] create schedule initiate, groupId={}, title={}",
                traceId, member.getId(), group.getId(), scheduleCreateDto.getTitle());

        idempotencyService.checkIdempotency(key, 1,
                () -> LockEvent.builder().key(key).build());

        GroupSchedule newSchedule = GroupSchedule.toEntity(scheduleCreateDto, group, member);
        GroupSchedule savedSchedule = groupScheduleRepository.save(newSchedule);

        ScheduleParticipant newMember = ScheduleParticipant.create(member, savedSchedule);
        scheduleParticipantRepository.save(newMember);

        log.info("[ScheduleService] [traceId={}, userId={}] create schedule success, scheduleId={}",
                traceId, member.getId(), savedSchedule.getId());
    }

    /**
     * 일정 정보를 수정합니다. (호스트만 가능)
     */
    @Transactional
    public void updateSchedule(UUID scheduleId, ScheduleCreateDto scheduleCreateDto, Member member) {
        log.info("[ScheduleService] [userId={}] update schedule initiate, scheduleId={}", member.getId(), scheduleId);

        GroupSchedule schedule = findGroupScheduleById(scheduleId);

        // 권한 검증: 일정 작성자만 수정 가능
        if (!schedule.getHostId().equals(member.getId())) {
            log.warn("[ScheduleService] [userId={}] Unauthorized attempt to update schedule hostId={}, scheduleId={}",
                    member.getId(), schedule.getHostId(), scheduleId);
            throw new CustomException(ErrorCode.METHOD_UNAUTHORIZED);
        }

        schedule.update(scheduleCreateDto);

        log.info("[ScheduleService] [userId={}] update schedule success, scheduleId={}", member.getId(), scheduleId);
    }

    /**
     * 일정을 삭제합니다. (호스트만 가능)
     */
    @Transactional
    public void deleteSchedule(UUID scheduleId, Member member) {
        log.info("[ScheduleService] [userId={}] delete schedule initiate, scheduleId={}", member.getId(), scheduleId);

        GroupSchedule schedule = findGroupScheduleById(scheduleId);

        // 권한 검증: 일정 작성자만 삭제 가능
        if (!schedule.getHostId().equals(member.getId())) {
            log.warn("[ScheduleService] [userId={}] Unauthorized attempt to delete schedule hostId={}, scheduleId={}",
                    member.getId(), schedule.getHostId(), scheduleId);
            throw new CustomException(ErrorCode.METHOD_UNAUTHORIZED);
        }

        groupScheduleRepository.delete(schedule);

        log.info("[ScheduleService] [userId={}] delete schedule success, scheduleId={}", member.getId(), scheduleId);
    }

    /**
     * 특정 일정에 참여합니다.
     */
    @Transactional
    public void addMemberToSchedule(UUID scheduleId, Member member) {
        log.info("[ScheduleService] [userId={}] add member to schedule initiate, scheduleId={}", member.getId(), scheduleId);

        GroupSchedule schedule = findGroupScheduleById(scheduleId);

        // 1. 모임 멤버 여부 확인
        if (!groupMemberRepository.existsByGroupAndMember(schedule.getGroup(), member)) {
            log.warn("[ScheduleService] [userId={}] Non-member attempted to join schedule, scheduleId={}", member.getId(), scheduleId);
            throw new CustomException(ErrorCode.NOT_GROUP_MEMBER);
        }

        // 2. 이미 참여 중인지 확인
        if (scheduleParticipantRepository.existsByScheduleAndMember(schedule, member)) {
            log.warn("[ScheduleService] [userId={}] Already a participant in schedule, scheduleId={}", member.getId(), scheduleId);
            throw new CustomException(ErrorCode.ALREADY_SCHEDULE_PARTICIPANT);
        }

        // NOTE: 스케줄 엔티티에 maxCapacity 필드가 있다면 여기서 인원 초과 검증을 추가해야 합니다.
        // 현재 GroupSchedule 엔티티에 maxCapacity 필드가 없으므로 생략합니다.

        // 3. 참여자 등록 및 카운트 증가
        ScheduleParticipant newMember = ScheduleParticipant.create(member, schedule);
        scheduleParticipantRepository.save(newMember);
        schedule.add();

        log.info("[ScheduleService] [userId={}] add member to schedule success, scheduleId={}, count={}",
                member.getId(), scheduleId, schedule.getParticipantCount());
    }

    /**
     * 특정 일정에서 참여를 취소합니다.
     */
    @Transactional
    public void removeMemberFromSchedule(UUID scheduleId, Member member) {
        log.info("[ScheduleService] [userId={}] remove member from schedule initiate, scheduleId={}", member.getId(), scheduleId);

        GroupSchedule schedule = findGroupScheduleById(scheduleId);

        // 1. 일정 참여 멤버인지 확인
        ScheduleParticipant participant = scheduleParticipantRepository.findByScheduleAndMember(schedule, member)
                .orElseThrow(() -> {
                    log.warn("[ScheduleService] [userId={}] Not a participant in schedule, scheduleId={}", member.getId(), scheduleId);
                    return new CustomException(ErrorCode.NOT_SCHEDULE_PARTICIPANT);
                });

        // 2. 참여 취소 및 카운트 감소
        scheduleParticipantRepository.delete(participant);
        schedule.remove();

        log.info("[ScheduleService] [userId={}] remove member from schedule success, scheduleId={}, count={}",
                member.getId(), scheduleId, schedule.getParticipantCount());
    }

    /**
     * ID를 사용하여 GroupSchedule 엔티티를 조회하고, 없으면 예외를 발생시킵니다.
     */
    public GroupSchedule findGroupScheduleById(UUID id) {
        return groupScheduleRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
    }
}