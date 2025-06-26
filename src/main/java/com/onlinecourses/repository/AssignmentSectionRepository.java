package com.onlinecourses.repository;

import com.onlinecourses.entity.AssignmentSection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AssignmentSectionRepository extends JpaRepository<AssignmentSection, UUID> {
}
