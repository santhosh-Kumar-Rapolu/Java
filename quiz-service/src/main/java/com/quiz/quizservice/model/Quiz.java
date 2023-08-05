package com.quiz.quizservice.model;

import java.util.*;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Quiz {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String title;
	@ElementCollection
	private List<Integer> questionIds;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Integer> getQuestionIds() {
		return questionIds;
	}
	public void setQuestionIds(List<Integer> questionIds) {
		this.questionIds = questionIds;
	}
	

}
