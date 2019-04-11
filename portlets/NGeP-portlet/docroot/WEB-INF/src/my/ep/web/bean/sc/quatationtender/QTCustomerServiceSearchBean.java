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
package my.ep.web.bean.sc.quatationtender;

import java.io.Serializable;
import java.util.Date;

import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;

/**
 * The Class QTCustomerServiceSearchbeanBean.
 * 
 * @author Garikapati Mangaiah.
 */
public class QTCustomerServiceSearchBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String qtNo;
	private String qtTitle;
	private Date createdFromDate;
	private Date createdToDate;
	private Date publishFromDate;
	private Date publishToDate;
	private Date closeFromDate;
	private Date closeToDate;
	private PmOrgProfile orgProfilePtjCode;
	private PmOrgProfile orgProfilePtjname=new PmOrgProfile();
	private String qtStatus;
	private String documentType=ParameterConst.DOCTYPE_QUOTATION;
	/**
	 * @return the qtNo
	 */
	public String getQtNo() {
		return qtNo;
	}
	/**
	 * @param qtNo the qtNo to set
	 */
	public void setQtNo(String qtNo) {
		this.qtNo = qtNo;
	}
	/**
	 * @return the qtTitle
	 */
	public String getQtTitle() {
		return qtTitle;
	}
	/**
	 * @param qtTitle the qtTitle to set
	 */
	public void setQtTitle(String qtTitle) {
		this.qtTitle = qtTitle;
	}
	/**
	 * @return the createdFromDate
	 */
	public Date getCreatedFromDate() {
		return createdFromDate;
	}
	/**
	 * @param createdFromDate the createdFromDate to set
	 */
	public void setCreatedFromDate(Date createdFromDate) {
		this.createdFromDate = createdFromDate;
	}
	/**
	 * @return the createdToDate
	 */
	public Date getCreatedToDate() {
		return createdToDate;
	}
	/**
	 * @param createdToDate the createdToDate to set
	 */
	public void setCreatedToDate(Date createdToDate) {
		this.createdToDate = createdToDate;
	}
	/**
	 * @return the publishFromDate
	 */
	public Date getPublishFromDate() {
		return publishFromDate;
	}
	/**
	 * @param publishFromDate the publishFromDate to set
	 */
	public void setPublishFromDate(Date publishFromDate) {
		this.publishFromDate = publishFromDate;
	}
	/**
	 * @return the publishToDate
	 */
	public Date getPublishToDate() {
		return publishToDate;
	}
	/**
	 * @param publishToDate the publishToDate to set
	 */
	public void setPublishToDate(Date publishToDate) {
		this.publishToDate = publishToDate;
	}
	/**
	 * @return the closeFromDate
	 */
	public Date getCloseFromDate() {
		return closeFromDate;
	}
	/**
	 * @param closeFromDate the closeFromDate to set
	 */
	public void setCloseFromDate(Date closeFromDate) {
		this.closeFromDate = closeFromDate;
	}
	/**
	 * @return the closeToDate
	 */
	public Date getCloseToDate() {
		return closeToDate;
	}
	/**
	 * @param closeToDate the closeToDate to set
	 */
	public void setCloseToDate(Date closeToDate) {
		this.closeToDate = closeToDate;
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
	 * @return the qtStatus
	 */
	public String getQtStatus() {
		return qtStatus;
	}
	/**
	 * @param qtStatus the qtStatus to set
	 */
	public void setQtStatus(String qtStatus) {
		this.qtStatus = qtStatus;
	}
	
	
}
