package com.bookbook.booklink.community.group_service.repository;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.community.group_service.model.Group;
import com.bookbook.booklink.community.group_service.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository extends JpaRepository<GroupMember, UUID> {

    Optional<GroupMember> findByGroupAndMember(Group group, Member member);

    Boolean existsByGroupAndMember(Group group, Member member);

    @Query("""
            select gm from GroupMember gm
            join fetch gm.member
            where gm.group = :group
            """)
    List<GroupMember> findAllByGroup(Group group);

    @Query("""
            select gm.group from GroupMember gm
            where gm.member = :member
            """)
    List<Group> findAllGroupByMember(Member member);
}
