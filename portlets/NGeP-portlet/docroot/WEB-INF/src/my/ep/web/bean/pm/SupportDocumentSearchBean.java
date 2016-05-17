/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

/**
 * Search Bean for Support Document 
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
public class SupportDocumentSearchBean {
	private Long docCategoryParam;
	private String docCategoryParamCode;
	private Long suppTypeParam;
	private Long comTypeParam;
	private Long appTypeParam;
	private Long procureCatParam;
	private Long pmZone;
	private String docName;
	private String businessCategory;
	private String certBodyName;
	private String docStatus;
	private Long glpCertBodyId;
	
	/**
	 * @return the docCategoryParam
	 */
	public Long getDocCategoryParam() {
		return docCategoryParam;
	}
	/**
	 * @param docCategoryParam the docCategoryParam to set
	 */
	public void setDocCategoryParam(Long docCategoryParam) {
		this.docCategoryParam = docCategoryParam;
	}
	/**
	 * @return the suppTypeParam
	 */
	public Long getSuppTypeParam() {
		return suppTypeParam;
	}
	/**
	 * @param suppTypeParam the suppTypeParam to set
	 */
	public void setSuppTypeParam(Long suppTypeParam) {
		this.suppTypeParam = suppTypeParam;
	}
	/**
	 * @return the comTypeParam
	 */
	public Long getComTypeParam() {
		return comTypeParam;
	}
	/**
	 * @param comTypeParam the comTypeParam to set
	 */
	public void setComTypeParam(Long comTypeParam) {
		this.comTypeParam = comTypeParam;
	}
	/**
	 * @return the appTypeParam
	 */
	public Long getAppTypeParam() {
		return appTypeParam;
	}
	/**
	 * @param appTypeParam the appTypeParam to set
	 */
	public void setAppTypeParam(Long appTypeParam) {
		this.appTypeParam = appTypeParam;
	}
	/**
	 * @return the pmZone
	 */
	public Long getPmZone() {
		return pmZone;
	}
	/**
	 * @param pmZone the pmZone to set
	 */
	public void setPmZone(Long pmZone) {
		this.pmZone = pmZone;
	}
	/**
	 * @return the docName
	 */
	public String getDocName() {
		return docName;
	}
	/**
	 * @param docName the docName to set
	 */
	public void setDocName(String docName) {
		this.docName = docName;
	}
	/**
	 * @return the businessCategory
	 */
	public String getBusinessCategory() {
		return businessCategory;
	}
	/**
	 * @param businessCategory the businessCategory to set
	 */
	public void setBusinessCategory(String businessCategory) {
		this.businessCategory = businessCategory;
	}
	/**
	 * @return the procureCatParam
	 */
	public Long getProcureCatParam() {
		return procureCatParam;
	}
	/**
	 * @param procureCatParam the procureCatParam to set
	 */
	public void setProcureCatParam(Long procureCatParam) {
		this.procureCatParam = procureCatParam;
	}
	/**
	 * @return the docCategoryParamCode
	 */
	public String getDocCategoryParamCode() {
		return docCategoryParamCode;
	}
	/**
	 * @param docCategoryParamCode the docCategoryParamCode to set
	 */
	public void setDocCategoryParamCode(String docCategoryParamCode) {
		this.docCategoryParamCode = docCategoryParamCode;
	}
	public String getDocStatus() {
		return docStatus;
	}
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	public String getCertBodyName() {
		return certBodyName;
	}
	public void setCertBodyName(String certBodyName) {
		this.certBodyName = certBodyName;
	}
	/**
	 * @return the glpCertBodyId
	 */
	public Long getGlpCertBodyId() {
		return glpCertBodyId;
	}
	/**
	 * @param glpCertBodyId the glpCertBodyId to set
	 */
	public void setGlpCertBodyId(Long glpCertBodyId) {
		this.glpCertBodyId = glpCertBodyId;
	}
	
	
	
	
}
