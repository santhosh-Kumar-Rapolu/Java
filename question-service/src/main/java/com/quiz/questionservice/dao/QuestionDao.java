package com.quiz.questionservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.quiz.questionservice.model.Question;

@Repository
public interface QuestionDao extends JpaRepository<Question, Integer>{
	
	List<Question> findByCategory(String category);

	@Query(value = "SELECT * FROM question q Where q.category=:category ORDER BY RANDOM() LIMIT :numQ", nativeQuery = true)
	List<Integer> findRandomQuestionsByCategory(String category, Integer numQ);
	
}
