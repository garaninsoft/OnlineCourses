package com.onlinecourses.repository;

import com.onlinecourses.entity.TestSection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TestSectionRepository extends JpaRepository<TestSection, UUID> {
}
