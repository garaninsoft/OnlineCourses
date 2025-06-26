package com.onlinecourses.repository;

import com.onlinecourses.entity.Answer;
import com.onlinecourses.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    List<Answer> findByQuestion(Question question);
}

