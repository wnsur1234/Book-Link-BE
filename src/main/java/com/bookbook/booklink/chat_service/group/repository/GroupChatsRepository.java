package com.bookbook.booklink.chat_service.group.repository;

import com.bookbook.booklink.chat_service.group.model.GroupChats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GroupChatsRepository extends JpaRepository<GroupChats, UUID> {
    
    Optional<GroupChats> findById(UUID uuid);
}
