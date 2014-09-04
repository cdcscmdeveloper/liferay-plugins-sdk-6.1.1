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
	private PmParameter docCategoryParam;
	private PmParameter suppTypeParam;
	private PmParameter comTypeParam;
	private PmParameter appTypeParam;
	private PmZone pmZone;
	private String docName;
	private String businessCategory;
	
	/**
	 * @return the docCategoryParam
	 */
	public PmParameter getDocCategoryParam() {
		if(docCategoryParam==null)
			docCategoryParam  = new PmParameter(); 
		return docCategoryParam;
	}
	/**
	 * @param docCategoryParam the docCategoryParam to set
	 */
	public void setDocCategoryParam(PmParameter docCategoryParam) {
		this.docCategoryParam = docCategoryParam;
	}
	/**
	 * @return the suppTypeParam
	 */
	public PmParameter getSuppTypeParam() {
		if(suppTypeParam==null)
			suppTypeParam  = new PmParameter(); 
		return suppTypeParam;
	}
	/**
	 * @param suppTypeParam the suppTypeParam to set
	 */
	public void setSuppTypeParam(PmParameter suppTypeParam) {
		this.suppTypeParam = suppTypeParam;
	}
	/**
	 * @return the comTypeParam
	 */
	public PmParameter getComTypeParam() {
		if(comTypeParam==null)
			comTypeParam  = new PmParameter(); 
		return comTypeParam;
	}
	/**
	 * @param comTypeParam the comTypeParam to set
	 */
	public void setComTypeParam(PmParameter comTypeParam) {
		this.comTypeParam = comTypeParam;
	}
	/**
	 * @return the appTypeParam
	 */
	public PmParameter getAppTypeParam() {
		if(appTypeParam==null)
			appTypeParam  = new PmParameter(); 
		return appTypeParam;
	}
	/**
	 * @param appTypeParam the appTypeParam to set
	 */
	public void setAppTypeParam(PmParameter appTypeParam) {
		this.appTypeParam = appTypeParam;
	}
	/**
	 * @return the pmZone
	 */
	public PmZone getPmZone() {
		if(pmZone==null)
			pmZone  = new PmZone(); 
		return pmZone;
	}
	/**
	 * @param pmZone the pmZone to set
	 */
	public void setPmZone(PmZone pmZone) {
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
