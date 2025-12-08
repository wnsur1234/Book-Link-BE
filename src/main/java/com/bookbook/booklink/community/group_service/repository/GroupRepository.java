package com.bookbook.booklink.community.group_service.repository;

import com.bookbook.booklink.community.group_service.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GroupRepository extends JpaRepository<Group, UUID> {
    List<Group> findAllByNameContaining(String name);
}
    