/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmZone;

/**
 * Search Bean for Support Document 
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
public class SupportDocumentSearchBean {
	private Long docCategoryParam;
	private Long suppTypeParam;
	private Long comTypeParam;
	private Long appTypeParam;
	private Long pmZone;
	private String docName;
	private String businessCategory;
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
	
	
	
	
}
