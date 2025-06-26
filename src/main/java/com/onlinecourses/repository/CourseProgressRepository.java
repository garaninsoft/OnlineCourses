package com.onlinecourses.repository;

import com.onlinecourses.entity.CourseProgress;
import com.onlinecourses.entity.Course;
import com.onlinecourses.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseProgressRepository extends JpaRepository<CourseProgress, UUID> {
    Optional<CourseProgress> findByStudentAndCourse(User student, Course course);
    List<CourseProgress> findByStudent(User student);
    List<CourseProgress> findByCourse(Course course);
}
