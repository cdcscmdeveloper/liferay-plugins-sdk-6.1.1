/*
 * Copyright © 2015 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.bean.pm;

import java.io.Serializable;

/**
 * Search Bean for Gl Account
 * 
 * @author venugopal
 * @since 17/4/2015
 */
public class GlAccountSearchBean implements Serializable  {

	private static final long serialVersionUID = -2975003646974502819L;
	private String accountCode;
	private String accountName;
	private String ptjCode;
	private String ptjDescription;
	private String status;
	private String selectedAccType;
	private String selectedAccClass;
	private String selectedGenObjCode;
	
	public GlAccountSearchBean(){}
	
	/**
	 * @return the accountCode
	 */
	public String getAccountCode() {
		return accountCode;
	}
	/**
	 * @param accountCode the accountCode to set
	 */
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}
	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	/**
	 * @return the ptjCode
	 */
	public String getPtjCode() {
		return ptjCode;
	}
	/**
	 * @param ptjCode the ptjCode to set
	 */
	public void setPtjCode(String ptjCode) {
		this.ptjCode = ptjCode;
	}
	/**
	 * @return the ptjDescription
	 */
	public String getPtjDescription() {
		return ptjDescription;
	}
	/**
	 * @param ptjDescription the ptjDescription to set
	 */
	public void setPtjDescription(String ptjDescription) {
		this.ptjDescription = ptjDescription;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the selectedAccType
	 */
	public String getSelectedAccType() {
		return selectedAccType;
	}

	/**
	 * @param selectedAccType the selectedAccType to set
	 */
	public void setSelectedAccType(String selectedAccType) {
		this.selectedAccType = selectedAccType;
	}

	/**
	 * @return the selectedAccClass
	 */
	public String getSelectedAccClass() {
		return selectedAccClass;
	}

	/**
	 * @param selectedAccClass the selectedAccClass to set
	 */
	public void setSelectedAccClass(String selectedAccClass) {
		this.selectedAccClass = selectedAccClass;
	}

	/**
	 * @return the selectedGenObjCode
	 */
	public String getSelectedGenObjCode() {
		return selectedGenObjCode;
	}

	/**
	 * @param selectedGenObjCode the selectedGenObjCode to set
	 */
	public void setSelectedGenObjCode(String selectedGenObjCode) {
		this.selectedGenObjCode = selectedGenObjCode;
	}
	
}
