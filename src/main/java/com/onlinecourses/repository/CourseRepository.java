package com.onlinecourses.repository;

import com.onlinecourses.entity.Course;
import com.onlinecourses.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    List<Course> findByCreator(User creator);
    List<Course> findAllByOrderByCreatedAtDesc();
    List<Course> findAllByCreator(User creator);

}
