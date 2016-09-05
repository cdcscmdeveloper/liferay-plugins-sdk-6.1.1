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
 * The Class QtnTenderSearchBean.
 *
 * @author Viswanath
 */
public class QtnTenderSearchBean implements Serializable {
	private static final long serialVersionUID = 1L;

	/** The qt no. */
	private String qtNo;
	
	/** The procurement mode. */
	private String procurementMode;
	
	/** The qt title. */
	private String qtTitle;
	
	/** The from created date. */
	private Date fromCreatedDate;
	
	/** The to created date. */
	private Date toCreatedDate;
	
	/** The from publish date. */
	private Date fromPublishDate;
	
	/** The to publish date. */
	private Date toPublishDate;
	
	/** The from closing date. */
	private Date fromClosingDate;
	
	/** The to closing date. */
	private Date toClosingDate;
	
	/** The qt status. */
	private String qtStatus;
	
	/** The committee type. */
	private String committeeType;
	
	/** The recordStatus. */
	private Long recordStatus;
	
	/** The docType. */
	private String docType = ParameterConst.DOCTYPE_QUOTATION;
	
	/** The resultDocType. */
	private String resultDocType = docType;
	
	/** The docNo. */
	private String docNo;
	
	/** The ministryProfile */
	private PmOrgProfile ministryProfile;
	
	/** The ptjGroupProfile */
	private PmOrgProfile ptjGroupProfile;
	
	/** The ptjProfile */
	private PmOrgProfile ptjProfile;
	
	/**
	 * Instantiates a new qtn tender search bean.
	 */
	public QtnTenderSearchBean() {
	}

	/**
	 * Gets the qt no.
	 *
	 * @return the qtNo
	 */
	public String getQtNo() {
		return qtNo;
	}

	/**
	 * Sets the qt no.
	 *
	 * @param qtNo the qtNo to set
	 */
	public void setQtNo(String qtNo) {
		this.qtNo = qtNo;
	}

	/**
	 * Gets the procurement mode.
	 *
	 * @return the procurementMode
	 */
	public String getProcurementMode() {
		return procurementMode;
	}

	/**
	 * Sets the procurement mode.
	 *
	 * @param procurementMode the procurementMode to set
	 */
	public void setProcurementMode(String procurementMode) {
		this.procurementMode = procurementMode;
	}

	/**
	 * Gets the qt title.
	 *
	 * @return the qtTitle
	 */
	public String getQtTitle() {
		return qtTitle;
	}

	/**
	 * Sets the qt title.
	 *
	 * @param qtTitle the qtTitle to set
	 */
	public void setQtTitle(String qtTitle) {
		this.qtTitle = qtTitle;
	}

	/**
	 * Gets the from created date.
	 *
	 * @return the fromCreatedDate
	 */
	public Date getFromCreatedDate() {
		return fromCreatedDate;
	}

	/**
	 * Sets the from created date.
	 *
	 * @param fromCreatedDate the fromCreatedDate to set
	 */
	public void setFromCreatedDate(Date fromCreatedDate) {
		this.fromCreatedDate = fromCreatedDate;
	}

	/**
	 * Gets the to created date.
	 *
	 * @return the toCreatedDate
	 */
	public Date getToCreatedDate() {
		return toCreatedDate;
	}

	/**
	 * Sets the to created date.
	 *
	 * @param toCreatedDate the toCreatedDate to set
	 */
	public void setToCreatedDate(Date toCreatedDate) {
		this.toCreatedDate = toCreatedDate;
	}

	/**
	 * Gets the from publish date.
	 *
	 * @return the fromPublishDate
	 */
	public Date getFromPublishDate() {
		return fromPublishDate;
	}

	/**
	 * Sets the from publish date.
	 *
	 * @param fromPublishDate the fromPublishDate to set
	 */
	public void setFromPublishDate(Date fromPublishDate) {
		this.fromPublishDate = fromPublishDate;
	}

	/**
	 * Gets the to publish date.
	 *
	 * @return the toPublishDate
	 */
	public Date getToPublishDate() {
		return toPublishDate;
	}

	/**
	 * Sets the to publish date.
	 *
	 * @param toPublishDate the toPublishDate to set
	 */
	public void setToPublishDate(Date toPublishDate) {
		this.toPublishDate = toPublishDate;
	}

	/**
	 * Gets the from closing date.
	 *
	 * @return the fromClosingDate
	 */
	public Date getFromClosingDate() {
		return fromClosingDate;
	}

	/**
	 * Sets the from closing date.
	 *
	 * @param fromClosingDate the fromClosingDate to set
	 */
	public void setFromClosingDate(Date fromClosingDate) {
		this.fromClosingDate = fromClosingDate;
	}

	/**
	 * Gets the to closing date.
	 *
	 * @return the toClosingDate
	 */
	public Date getToClosingDate() {
		return toClosingDate;
	}

	/**
	 * Sets the to closing date.
	 *
	 * @param toClosingDate the toClosingDate to set
	 */
	public void setToClosingDate(Date toClosingDate) {
		this.toClosingDate = toClosingDate;
	}

	/**
	 * Gets the qt status.
	 *
	 * @return the qtStatus
	 */
	public String getQtStatus() {
		return qtStatus;
	}

	/**
	 * Sets the qt status.
	 *
	 * @param qtStatus the qtStatus to set
	 */
	public void setQtStatus(String qtStatus) {
		this.qtStatus = qtStatus;
	}

	/**
	 * Gets the committee type.
	 *
	 * @return the committeeType
	 */
	public String getCommitteeType() {
		return committeeType;
	}

	/**
	 * Sets the committee type.
	 *
	 * @param committeeType the committeeType to set
	 */
	public void setCommitteeType(String committeeType) {
		this.committeeType = committeeType;
	}

	/**
	 * @return the recordStatus
	 */
	public Long getRecordStatus() {
		return recordStatus;
	}

	/**
	 * @param recordStatus the recordStatus to set
	 */
	public void setRecordStatus(Long recordStatus) {
		this.recordStatus = recordStatus;
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
	 * @return the resultDocType
	 */
	public String getResultDocType() {
		return resultDocType;
	}

	/**
	 * @param resultDocType the resultDocType to set
	 */
	public void setResultDocType(String resultDocType) {
		this.resultDocType = resultDocType;
	}

	/**
	 * @return the docNo
	 */
	public String getDocNo() {
		return docNo;
	}

	/**
	 * @param docNo the docNo to set
	 */
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	/**
	 * @return the ministryProfile
	 */
	public PmOrgProfile getMinistryProfile() {
		return ministryProfile;
	}

	/**
	 * @param ministryProfile the ministryProfile to set
	 */
	public void setMinistryProfile(PmOrgProfile ministryProfile) {
		this.ministryProfile = ministryProfile;
	}

	/**
	 * @return the ptjGroupProfile
	 */
	public PmOrgProfile getPtjGroupProfile() {
		return ptjGroupProfile;
	}

	/**
	 * @param ptjGroupProfile the ptjGroupProfile to set
	 */
	public void setPtjGroupProfile(PmOrgProfile ptjGroupProfile) {
		this.ptjGroupProfile = ptjGroupProfile;
	}

	/**
	 * @return the ptjProfile
	 */
	public PmOrgProfile getPtjProfile() {
		return ptjProfile;
	}

	/**
	 * @param ptjProfile the ptjProfile to set
	 */
	public void setPtjProfile(PmOrgProfile ptjProfile) {
		this.ptjProfile = ptjProfile;
	}
	
}
