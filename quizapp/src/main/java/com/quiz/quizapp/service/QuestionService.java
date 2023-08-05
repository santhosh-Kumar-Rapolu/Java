package com.quiz.quizapp.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.quiz.quizapp.dao.QuestionDao;
import com.quiz.quizapp.model.Question;

@Service
public class QuestionService {

	@Autowired
	QuestionDao questionDao;

	public ResponseEntity<List<Question>> getAllQuestions() {
		try {
			return new ResponseEntity(questionDao.findAll(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity(new ArrayList<>(), HttpStatus.BAD_REQUEST);
	}

	public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
		try {
			return new ResponseEntity(questionDao.findByCategory(category), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity(new ArrayList<>(), HttpStatus.BAD_REQUEST);
	}

	public ResponseEntity<String> addQuestion(@RequestBody Question question) {
		try {
			questionDao.save(question);
			return new ResponseEntity("success", HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity("failed", HttpStatus.BAD_REQUEST);
	}
}
