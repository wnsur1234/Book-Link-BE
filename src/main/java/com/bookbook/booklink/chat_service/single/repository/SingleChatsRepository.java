package com.bookbook.booklink.chat_service.single.repository;

import com.bookbook.booklink.chat_service.single.model.SingleChats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SingleChatsRepository extends JpaRepository<SingleChats, UUID> {
    /**
     * 두 유저 간 채팅방이 이미 존재하는지 확인
     */
    Optional<SingleChats> findByUser1IdAndUser2Id(UUID user1Id, UUID user2Id);

    /**
     * 유저가 참여한 채팅방을 마지막 메시지 시각 기준으로 내림차순 정렬
     */
    @Query("""
            SELECT s
            FROM SingleChats s
            WHERE (s.user1Id = :memberId AND s.user1Deleted = false) OR
                (s.user2Id = :memberId AND s.user2Deleted = false)
            ORDER BY s.lastSentAt DESC NULLS LAST
            """)
    List<SingleChats> findAllByMemberSorted(@Param("memberId") UUID memberId);

    @Query("""
            select s
            from SingleChats s
            where s.user1Id = :memberId or s.user2Id = :memberId
            """)
    List<SingleChats> findAllByMember(@Param("memberId") UUID memberId);
}
    