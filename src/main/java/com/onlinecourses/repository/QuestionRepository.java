package com.onlinecourses.repository;

import com.onlinecourses.entity.Question;
import com.onlinecourses.entity.TestSection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByTestSection(TestSection testSection);
}
