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
package my.ep.web.managedbean.tpl.spec;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.tpl.spec.TplSpecQuestion;
import my.ep.web.bean.tpl.TemplateSpecBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Template Specification Managed Bean
 * 
 * @author kian seng
 * @since 24/4/2013
 */

@ManagedBean(name="templateSpecMB")
@SessionScoped
public class TemplateSpecManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private String searchSpecName;
	private String searchSpecDesc; 
	private Long searchProcurementCategoryId;
	private String searchTemplateTypeCode;
	private PmOrgProfile searchOrgProfile = new PmOrgProfile();
	private String searchTemplateTypeUser;
	private Long searchItemTypeId;
	private Long searchSpecTypeId;
	private boolean pidSQ;
	
	private List<TplSpecQuestion> tplSpecQuestions;
	private TemplateSpecBean templateSpecBean = new TemplateSpecBean();
	private ScQt scQt;
	private String docType;
	
	public TemplateSpecManagedBean() {
	}

	/**
	 * @return the searchSpecName
	 */
	public String getSearchSpecName() {
		return searchSpecName;
	}

	/**
	 * @param searchSpecName the searchSpecName to set
	 */
	public void setSearchSpecName(String searchSpecName) {
		this.searchSpecName = searchSpecName;
	}

	/**
	 * @return the searchSpecDesc
	 */
	public String getSearchSpecDesc() {
		return searchSpecDesc;
	}

	/**
	 * @param searchSpecDesc the searchSpecDesc to set
	 */
	public void setSearchSpecDesc(String searchSpecDesc) {
		this.searchSpecDesc = searchSpecDesc;
	}

	/**
	 * @return the searchProcurementCategoryId
	 */
	public Long getSearchProcurementCategoryId() {
		return searchProcurementCategoryId;
	}

	/**
	 * @param searchProcurementCategoryId the searchProcurementCategoryId to set
	 */
	public void setSearchProcurementCategoryId(Long searchProcurementCategoryId) {
		this.searchProcurementCategoryId = searchProcurementCategoryId;
	}

	/**
	 * @return the searchTemplateTypeCode
	 */
	public String getSearchTemplateTypeCode() {
		return searchTemplateTypeCode;
	}

	/**
	 * @param searchTemplateTypeCode the searchTemplateTypeCode to set
	 */
	public void setSearchTemplateTypeCode(String searchTemplateTypeCode) {
		this.searchTemplateTypeCode = searchTemplateTypeCode;
	}

	/**
	 * @return the searchOrgProfile
	 */
	public PmOrgProfile getSearchOrgProfile() {
		return searchOrgProfile;
	}

	/**
	 * @param searchOrgProfile the searchOrgProfile to set
	 */
	public void setSearchOrgProfile(PmOrgProfile searchOrgProfile) {
		this.searchOrgProfile = searchOrgProfile;
	}

	/**
	 * @return the searchTemplateTypeUser
	 */
	public String getSearchTemplateTypeUser() {
		return searchTemplateTypeUser;
	}

	/**
	 * @param searchTemplateTypeUser the searchTemplateTypeUser to set
	 */
	public void setSearchTemplateTypeUser(String searchTemplateTypeUser) {
		this.searchTemplateTypeUser = searchTemplateTypeUser;
	}

	/**
	 * @return the searchItemTypeId
	 */
	public Long getSearchItemTypeId() {
		return searchItemTypeId;
	}

	/**
	 * @param searchItemTypeId the searchItemTypeId to set
	 */
	public void setSearchItemTypeId(Long searchItemTypeId) {
		this.searchItemTypeId = searchItemTypeId;
	}

	/**
	 * @return the searchSpecTypeId
	 */
	public Long getSearchSpecTypeId() {
		return searchSpecTypeId;
	}

	/**
	 * @param searchSpecTypeId the searchSpecTypeId to set
	 */
	public void setSearchSpecTypeId(Long searchSpecTypeId) {
		this.searchSpecTypeId = searchSpecTypeId;
	}

	/**
	 * @return the tplSpecQuestions
	 */
	public List<TplSpecQuestion> getTplSpecQuestions() {
		return tplSpecQuestions;
	}

	/**
	 * @param tplSpecQuestions the tplSpecQuestions to set
	 */
	public void setTplSpecQuestions(List<TplSpecQuestion> tplSpecQuestions) {
		this.tplSpecQuestions = tplSpecQuestions;
	}

	/**
	 * @return the templateSpecBean
	 */
	public TemplateSpecBean getTemplateSpecBean() {
		return templateSpecBean;
	}

	/**
	 * @param templateSpecBean the templateSpecBean to set
	 */
	public void setTemplateSpecBean(TemplateSpecBean templateSpecBean) {
		this.templateSpecBean = templateSpecBean;
	}

	/**
	 * @return the scQt
	 */
	public ScQt getScQt() {
		return scQt;
	}

	/**
	 * @param scQt the scQt to set
	 */
	public void setScQt(ScQt scQt) {
		this.scQt = scQt;
	}

	/**
	 * @return the docType
	 */
	public String getDocType() {
		return docType;
	}

	/**
	 * @param docType the docType to set
	 */
	public void setDocType(String docType) {
		this.docType = docType;
	}

	/**
	 * @return the pidSQ
	 */
	public boolean isPidSQ() {
		return pidSQ;
	}

	/**
	 * @param pidSQ the pidSQ to set
	 */
	public void setPidSQ(boolean pidSQ) {
		this.pidSQ = pidSQ;
	}
}
