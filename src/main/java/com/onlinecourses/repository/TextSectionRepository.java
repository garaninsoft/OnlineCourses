package com.onlinecourses.repository;

import com.onlinecourses.entity.TextSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TextSectionRepository extends JpaRepository<TextSection, UUID> {
}
