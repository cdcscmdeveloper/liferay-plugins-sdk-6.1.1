/* 
 ***************************************************   
 * Product nextgen ePerolehan 
 * 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.bean.tpl;

import java.util.List;

import my.ep.constant.tpl.TemplateSpecConst;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.tpl.spec.TplSpecAnswer;
import my.ep.persistence.entity.tpl.spec.TplSpecMaster;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.persistence.entity.tpl.spec.TplSpecZoneDtl;

/**
 * Bean Object for template specification.
 * 
 * @author VidyaSagarReddy
 * @since 19.03.2015
 */
public class TemplateSpecBean {
	private boolean selectAll;
	private TplSpecMaster specMaster;
	private String contentType;
	private SmSupplier supplier;
	private Long selectedScoreTypeId;
	private String specComponentId;
	private TplSpecQuestion selectedSpecQuestion;
	private TplSpecQuestion oriSpecQuestion;
	private List<TplSpecAnswer> specAnswerList;
	private TplSpecZoneDtl tplSpecZoneDtl;
	private boolean updateSpecByAnswers;
	private List<TplSpecAnswer> specAnswersToDelete;
	
	/**
	 * Constructor for template specification bean
	 */
	public TemplateSpecBean() {
		this(TemplateSpecConst.CONTENT_TYPE_QUESTION);
	}

	/**
	 * Constructor for template specification bean
	 * 
	 * @param contentType
	 * 		the content type
	 */
	public TemplateSpecBean(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * @return the selectAll
	 */
	public boolean isSelectAll() {
		return selectAll;
	}

	/**
	 * @param selectAll the selectAll to set
	 */
	public void setSelectAll(boolean selectAll) {
		this.selectAll = selectAll;
	}

	/**
	 * @return the specMaster
	 */
	public TplSpecMaster getSpecMaster() {
		return specMaster;
	}

	/**
	 * @param specMaster the specMaster to set
	 */
	public void setSpecMaster(TplSpecMaster specMaster) {
		this.specMaster = specMaster;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the supplier
	 */
	public SmSupplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(SmSupplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the selectedScoreTypeId
	 */
	public Long getSelectedScoreTypeId() {
		return selectedScoreTypeId;
	}

	/**
	 * @param selectedScoreTypeId the selectedScoreTypeId to set
	 */
	public void setSelectedScoreTypeId(Long selectedScoreTypeId) {
		this.selectedScoreTypeId = selectedScoreTypeId;
	}

	/**
	 * @return the specComponentId
	 */
	public String getSpecComponentId() {
		return specComponentId;
	}

	/**
	 * @param specComponentId the specComponentId to set
	 */
	public void setSpecComponentId(String specComponentId) {
		this.specComponentId = specComponentId;
	}

	/**
	 * @return the selectedSpecQuestion
	 */
	public TplSpecQuestion getSelectedSpecQuestion() {
		return selectedSpecQuestion;
	}

	/**
	 * @param selectedSpecQuestion the selectedSpecQuestion to set
	 */
	public void setSelectedSpecQuestion(TplSpecQuestion selectedSpecQuestion) {
		this.selectedSpecQuestion = selectedSpecQuestion;
	}

	/**
	 * @return the oriSpecQuestion
	 */
	public TplSpecQuestion getOriSpecQuestion() {
		return oriSpecQuestion;
	}

	/**
	 * @param oriSpecQuestion the oriSpecQuestion to set
	 */
	public void setOriSpecQuestion(TplSpecQuestion oriSpecQuestion) {
		this.oriSpecQuestion = oriSpecQuestion;
	}

	/**
	 * @return the specAnswerList
	 */
	public List<TplSpecAnswer> getSpecAnswerList() {
		return specAnswerList;
	}

	/**
	 * @param specAnswerList the specAnswerList to set
	 */
	public void setSpecAnswerList(List<TplSpecAnswer> specAnswerList) {
		this.specAnswerList = specAnswerList;
	}

	/**
	 * @return the tplSpecZoneDtl
	 */
	public TplSpecZoneDtl getTplSpecZoneDtl() {
		return tplSpecZoneDtl;
	}

	/**
	 * @param tplSpecZoneDtl the tplSpecZoneDtl to set
	 */
	public void setTplSpecZoneDtl(TplSpecZoneDtl tplSpecZoneDtl) {
		this.tplSpecZoneDtl = tplSpecZoneDtl;
	}

	/**
	 * @return the updateSpecByAnswers
	 */
	public boolean isUpdateSpecByAnswers() {
		return updateSpecByAnswers;
	}

	/**
	 * @param updateSpecByAnswers the updateSpecByAnswers to set
	 */
	public void setUpdateSpecByAnswers(boolean updateSpecByAnswers) {
		this.updateSpecByAnswers = updateSpecByAnswers;
	}

	/**
	 * @return the specAnswersToDelete
	 */
	public List<TplSpecAnswer> getSpecAnswersToDelete() {
		return specAnswersToDelete;
	}

	/**
	 * @param specAnswersToDelete the specAnswersToDelete to set
	 */
	public void setSpecAnswersToDelete(List<TplSpecAnswer> specAnswersToDelete) {
		this.specAnswersToDelete = specAnswersToDelete;
	}
	
}