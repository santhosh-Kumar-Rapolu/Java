package com.quiz.quizservice.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quiz.quizservice.model.Quiz;

@Repository
public interface QuizDao extends JpaRepository<Quiz, Integer> {

}
