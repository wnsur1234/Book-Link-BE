package com.bookbook.booklink.community.schedule_service.repository;

import com.bookbook.booklink.community.schedule_service.model.GroupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroupScheduleRepository extends JpaRepository<GroupSchedule, UUID> {
}
