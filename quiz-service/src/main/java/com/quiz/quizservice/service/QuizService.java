package com.quiz.quizservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
 

import com.quiz.quizservice.dao.QuizDao;
import com.quiz.quizservice.feign.QuizInterface;
import com.quiz.quizservice.model.QuestionWrapper;
import com.quiz.quizservice.model.Quiz;
import com.quiz.quizservice.model.Response;

@Service
public class QuizService {
	@Autowired
	QuizDao quizDao;

	@Autowired
	QuizInterface quizInterface;

	public ResponseEntity<String> createQuiz(String category, Integer numQuestions, String title) {
		try {
			List<Integer> questions = quizInterface.getQuestionsForQuiz(category, numQuestions).getBody();
			Quiz quiz= new Quiz();
			quiz.setTitle(title);
			quiz.setQuestionIds(questions);
			quizDao.save(quiz);
			return new ResponseEntity("success", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
		try {
			Quiz quiz = quizDao.findById(id).get();
			List<Integer> questionIds = quiz.getQuestionIds();
			
			ResponseEntity<List<QuestionWrapper>> questions= quizInterface.getQuestionsFromId(questionIds);
			
			return new ResponseEntity(questions, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
		return new ResponseEntity(quizInterface.getScore(responses), HttpStatus.OK);
	}
}
