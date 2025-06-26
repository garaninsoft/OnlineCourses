package com.onlinecourses.repository;

import com.onlinecourses.entity.TestResult;
import com.onlinecourses.entity.TestSection;
import com.onlinecourses.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestResultRepository extends JpaRepository<TestResult, UUID> {
    Optional<TestResult> findByStudentAndTestSection(User student, TestSection testSection);
    List<TestResult> findByStudent(User student);
}
