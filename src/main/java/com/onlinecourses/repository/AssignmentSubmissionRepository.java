package com.onlinecourses.repository;

import com.onlinecourses.entity.AssignmentSubmission;
import com.onlinecourses.entity.AssignmentSection;
import com.onlinecourses.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, UUID> {
    List<AssignmentSubmission> findByAssignmentSection(AssignmentSection assignmentSection);
    Optional<AssignmentSubmission> findByStudentAndAssignmentSection(User student, AssignmentSection assignmentSection);
    Optional<AssignmentSubmission> findByAssignmentSectionAndStudent(AssignmentSection section, User student);
}
