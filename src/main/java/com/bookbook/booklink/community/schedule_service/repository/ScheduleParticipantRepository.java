package com.bookbook.booklink.community.schedule_service.repository;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.community.schedule_service.model.GroupSchedule;
import com.bookbook.booklink.community.schedule_service.model.ScheduleParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleParticipantRepository extends JpaRepository<ScheduleParticipant, UUID> {

    Optional<ScheduleParticipant> findByScheduleAndMember(GroupSchedule schedule, Member member);

    @Query("""
            select sp.member from ScheduleParticipant sp
            join sp.member
            where sp.schedule = :schedule
            """)
    List<Member> findMemberBySchedule(GroupSchedule schedule);

    @Query("""
            select sp.schedule from ScheduleParticipant sp
            join sp.schedule
            where sp.member = :member
            """)
    List<GroupSchedule> findScheduleByMember(Member member);

    Boolean existsByScheduleAndMember(GroupSchedule schedule, Member member);
}
