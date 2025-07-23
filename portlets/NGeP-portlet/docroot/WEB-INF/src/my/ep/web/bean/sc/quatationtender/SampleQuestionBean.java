/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 CDC. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;
import java.math.BigDecimal;

import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;

/**
 * 11104-Reendorse
 * 
 * @author mangaiah
 * 
 */
public class SampleQuestionBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5155820654362481606L;

	private Long specQuestionId;
	private boolean isSample;
	private BigDecimal sampleQuestionQuantity;
	private BigDecimal sampleQuestionScore;
	private TplSpecQuestion sampleSpecQuestion;
	
	/**
	 * Instantiates a new SampleQuestionBean bean.
	 *
	 * @param specQuestionId the specQuestionId
	 * @param sampleSpecQuestion the sampleSpecQuestion
	 */
	public SampleQuestionBean(Long specQuestionId, TplSpecQuestion sampleSpecQuestion) {
		this.specQuestionId = specQuestionId;
		this.sampleSpecQuestion = sampleSpecQuestion;
	}
	/**
	 * Instantiates a new SampleQuestionBean bean.
	 *
	 * @param specQuestionId the specQuestionId
	 * @param isSample the isSample
	 * @param sampleQuestionQuantity the sampleQuestionQuantity
	 * @param sampleQuestionScore the sampleQuestionScore
	 * 
	 */
	public SampleQuestionBean(Long specQuestionId, boolean isSample,BigDecimal sampleQuestionQuantity,BigDecimal sampleQuestionScore){
		this.specQuestionId=specQuestionId;
		this.isSample=isSample;
		this.sampleQuestionQuantity=sampleQuestionQuantity;
		this.sampleQuestionScore=sampleQuestionScore;
	}

	/**
	 * @return the specQuestionId
	 */
	public Long getSpecQuestionId() {
		return specQuestionId;
	}

	/**
	 * @param specQuestionId the specQuestionId to set
	 */
	public void setSpecQuestionId(Long specQuestionId) {
		this.specQuestionId = specQuestionId;
	}

	/**
	 * @return the isSample
	 */
	public boolean isSample() {
		return isSample;
	}

	/**
	 * @param isSample the isSample to set
	 */
	public void setSample(boolean isSample) {
		this.isSample = isSample;
	}

	/**
	 * @return the sampleQuestionQuantity
	 */
	public BigDecimal getSampleQuestionQuantity() {
		return sampleQuestionQuantity;
	}

	/**
	 * @param sampleQuestionQuantity the sampleQuestionQuantity to set
	 */
	public void setSampleQuestionQuantity(BigDecimal sampleQuestionQuantity) {
		this.sampleQuestionQuantity = sampleQuestionQuantity;
	}

	/**
	 * @return the sampleQuestionScore
	 */
	public BigDecimal getSampleQuestionScore() {
		return sampleQuestionScore;
	}

	/**
	 * @param sampleQuestionScore the sampleQuestionScore to set
	 */
	public void setSampleQuestionScore(BigDecimal sampleQuestionScore) {
		this.sampleQuestionScore = sampleQuestionScore;
	}

	/**
	 * @return the sampleSpecQuestion
	 */
	public TplSpecQuestion getSampleSpecQuestion() {
		return sampleSpecQuestion;
	}

	/**
	 * @param sampleSpecQuestion the sampleSpecQuestion to set
	 */
	public void setSampleSpecQuestion(TplSpecQuestion sampleSpecQuestion) {
		this.sampleSpecQuestion = sampleSpecQuestion;
	}

}
