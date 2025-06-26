package com.onlinecourses.repository;

import com.onlinecourses.entity.Topic;
import com.onlinecourses.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TopicRepository extends JpaRepository<Topic, UUID> {
    List<Topic> findByCourseOrderByOrderIndexAsc(Course course);
}
