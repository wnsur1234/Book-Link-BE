package com.bookbook.booklink.chat_service.group.repository;

import com.bookbook.booklink.auth_service.model.Member;
import com.bookbook.booklink.chat_service.group.model.GroupChats;
import com.bookbook.booklink.community.group_service.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupChatsRepository extends JpaRepository<GroupChats, UUID> {
    
    Optional<GroupChats> findById(UUID uuid);
    Optional<GroupChats> findByGroup(Group group);

    @Query("""
        select gc 
        from GroupChats gc
        join gc.group g
        join GroupMember gm on gm.group = g
        where gm.member = :member
        """)
    List<GroupChats> findAllByMember(@Param("member") Member member);
}
