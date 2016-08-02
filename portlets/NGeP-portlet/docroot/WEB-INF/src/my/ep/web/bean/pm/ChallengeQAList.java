/*
 * Copyright © 2015 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * OIM Challenge Question & Answer List
 * 
 * @author KEN WONG
 * @since 20/5/2015	 
 *
 */
public class ChallengeQAList implements Serializable{


	private static final long serialVersionUID = 1L;
	private String question;
	private String answer;
	
	public ChallengeQAList(){}
	
	public ChallengeQAList(String question,String answer){
		this.question = question;
		this.answer = answer;
	}
	
	/**
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}
	/**
	 * @param question the question to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}
	/**
	 * @return the answer
	 */
	public String getAnswer() {
		return answer;
	}
	/**
	 * @param answer the answer to set
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}
}
