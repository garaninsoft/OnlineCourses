package com.onlinecourses.repository;

import com.onlinecourses.entity.VideoSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VideoSectionRepository extends JpaRepository<VideoSection, UUID> {
}
