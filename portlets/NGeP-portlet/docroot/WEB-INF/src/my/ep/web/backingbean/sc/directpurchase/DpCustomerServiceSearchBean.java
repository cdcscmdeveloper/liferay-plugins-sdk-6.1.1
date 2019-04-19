/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.backingbean.sc.directpurchase;

import java.io.Serializable;
import java.util.Date;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;

/**
 * The Class DpCustomerServiceSearchBean.
 * 
 * @author Garikapati Mangaiah.
 */
public class DpCustomerServiceSearchBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private PmOrgProfile orgProfilePtjCode;
	private PmOrgProfile orgProfilePtjname=new PmOrgProfile();
	private String dpTitle;
	private String documentType;
	private String documentNo;
	private Date createdDateFrom;
	private Date createdDateTo;
	private String dpStatus;
	
	/**
	 * @return the dpTitle
	 */
	public String getDpTitle() {
		return dpTitle;
	}
	/**
	 * @param dpTitle the dpTitle to set
	 */
	public void setDpTitle(String dpTitle) {
		this.dpTitle = dpTitle;
	}
	/**
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}
	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	/**
	 * @return the documentNo
	 */
	public String getDocumentNo() {
		return documentNo;
	}
	/**
	 * @param documentNo the documentNo to set
	 */
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
	/**
	 * @return the createdDateFrom
	 */
	public Date getCreatedDateFrom() {
		return createdDateFrom;
	}
	/**
	 * @param createdDateFrom the createdDateFrom to set
	 */
	public void setCreatedDateFrom(Date createdDateFrom) {
		this.createdDateFrom = createdDateFrom;
	}
	/**
	 * @return the createdDateTo
	 */
	public Date getCreatedDateTo() {
		return createdDateTo;
	}
	/**
	 * @param createdDateTo the createdDateTo to set
	 */
	public void setCreatedDateTo(Date createdDateTo) {
		this.createdDateTo = createdDateTo;
	}
	/**
	 * @return the dpStatus
	 */
	public String getDpStatus() {
		return dpStatus;
	}
	/**
	 * @param dpStatus the dpStatus to set
	 */
	public void setDpStatus(String dpStatus) {
		this.dpStatus = dpStatus;
	}
	/**
	 * @return the orgProfilePtjCode
	 */
	public PmOrgProfile getOrgProfilePtjCode() {
		return orgProfilePtjCode;
	}
	/**
	 * @param orgProfilePtjCode the orgProfilePtjCode to set
	 */
	public void setOrgProfilePtjCode(PmOrgProfile orgProfilePtjCode) {
		this.orgProfilePtjCode = orgProfilePtjCode;
	}
	/**
	 * @return the orgProfilePtjname
	 */
	public PmOrgProfile getOrgProfilePtjname() {
		return orgProfilePtjname;
	}
	/**
	 * @param orgProfilePtjname the orgProfilePtjname to set
	 */
	public void setOrgProfilePtjname(PmOrgProfile orgProfilePtjname) {
		this.orgProfilePtjname = orgProfilePtjname;
	}
	
	
	

}
