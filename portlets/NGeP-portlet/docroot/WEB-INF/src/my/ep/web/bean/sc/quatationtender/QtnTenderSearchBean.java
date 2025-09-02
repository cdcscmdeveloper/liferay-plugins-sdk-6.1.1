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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import my.ep.biz.bean.search.sc.QTSearchBean;
import my.ep.constant.pm.ParameterConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sc.quationtender.ScQt;

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
	
	private ScQt scQt;
	
	/** The loa no. */
	private String loaNo;//#29468 Added by Vinod
	
	/** The Proposal no. */
	private String proposalNo;//#14306 Harun
	private List<String> scStatusManyMenu;//#29580
	private String scCreatedFromMonth;//#14306
	private String scCreatedFromYear;//#14306
	private String scCreatedToMonth;//#14306
	private String scCreatedToYear;//#14306
	private String scPropSubmitFromMonth;//#14306
	private String scPropSubmitFromYear;//#14306
	private String scPropSubmitToMonth;//#14306
	private String scPropSubmitToYear;//#14306
	
	/**
	 * flag to indicate if the qtStatus is to filter ScQt by status
	 * default to true 
	 */
	private boolean qtStatusForQt = true;
	
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
		this.fromCreatedDate = getFromDate(fromCreatedDate);
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
		this.toCreatedDate = getToDate(toCreatedDate);
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
		this.fromPublishDate = getFromDate(fromPublishDate);
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
		this.toPublishDate = getToDate(toPublishDate);
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
		this.fromClosingDate = getFromDate(fromClosingDate);
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
		this.toClosingDate = getToDate(toClosingDate);
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
		this.setQtStatusForQt(ParameterConst.DOCTYPE_QUOTATION.equals(this.docType));
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
	
	private Date getFromDate(Date date){
		if(date != null){
			Calendar fromDate = Calendar.getInstance();
			fromDate.setTime(date);
			fromDate.set(Calendar.HOUR_OF_DAY,0);
			fromDate.set(Calendar.MINUTE,0);
			fromDate.set(Calendar.SECOND,0);
			fromDate.set(Calendar.MILLISECOND,0);
			return fromDate.getTime();
		}else{
			return date;
		}
	}
	
	private Date getToDate(Date date){
		if(date != null){
			Calendar toDate = Calendar.getInstance();
			toDate.setTime(date);
			toDate.set(Calendar.HOUR_OF_DAY,23);
			toDate.set(Calendar.MINUTE,59);
			toDate.set(Calendar.SECOND,59);
			toDate.set(Calendar.MILLISECOND,59);
			return toDate.getTime();
		}else{
			return date;
		}
	}
	
	// ***25201
	public QTSearchBean toQTSearchBean(){
		QTSearchBean t = new QTSearchBean();
		t.setCommitteeType(committeeType);
		t.setDocNo(docNo);
		t.setDocType(docType);
		t.setFromClosingDate(fromClosingDate);
		t.setFromCreatedDate(fromCreatedDate);
		t.setFromPublishDate(fromPublishDate);
		t.setMinistryProfile(ministryProfile);
		t.setProcurementMode(procurementMode);
		t.setPtjGroupProfile(ptjGroupProfile);
		t.setPtjProfile(ptjProfile);
		t.setQtNo(qtNo);
		t.setQtStatus(qtStatus);
		t.setQtTitle(qtTitle);
		t.setRecordStatus(recordStatus);
		t.setResultDocType(resultDocType);
		t.setScQt(scQt);
		t.setToClosingDate(toClosingDate);
		t.setToCreatedDate(toCreatedDate);
		t.setToPublishDate(toPublishDate);
		t.setQtStatusForQt(qtStatusForQt);
		return t;
	}

	public boolean isQtStatusForQt() {
		return qtStatusForQt;
	}

	public void setQtStatusForQt(boolean qtStatusForQt) {
		this.qtStatusForQt = qtStatusForQt;
	}

	//#29468 Start Vinod
	public String getLoaNo() {
		return loaNo;
	}

	public void setLoaNo(String loaNo) {
		this.loaNo = loaNo;
	}
	//#29468 End Vinod

	//#14306 Start Harun
	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public List<String> getScStatusManyMenu() {
		return scStatusManyMenu;
	}

	public void setScStatusManyMenu(List<String> scStatusManyMenu) {
		this.scStatusManyMenu = scStatusManyMenu;
	}

	public String getScCreatedFromMonth() {
		return scCreatedFromMonth;
	}

	public void setScCreatedFromMonth(String scCreatedFromMonth) {
		this.scCreatedFromMonth = scCreatedFromMonth;
	}

	public String getScCreatedFromYear() {
		return scCreatedFromYear;
	}

	public void setScCreatedFromYear(String scCreatedFromYear) {
		this.scCreatedFromYear = scCreatedFromYear;
	}

	public String getScCreatedToMonth() {
		return scCreatedToMonth;
	}

	public void setScCreatedToMonth(String scCreatedToMonth) {
		this.scCreatedToMonth = scCreatedToMonth;
	}

	public String getScCreatedToYear() {
		return scCreatedToYear;
	}

	public void setScCreatedToYear(String scCreatedToYear) {
		this.scCreatedToYear = scCreatedToYear;
	}

	public String getScPropSubmitFromMonth() {
		return scPropSubmitFromMonth;
	}

	public void setScPropSubmitFromMonth(String scPropSubmitFromMonth) {
		this.scPropSubmitFromMonth = scPropSubmitFromMonth;
	}

	public String getScPropSubmitFromYear() {
		return scPropSubmitFromYear;
	}

	public void setScPropSubmitFromYear(String scPropSubmitFromYear) {
		this.scPropSubmitFromYear = scPropSubmitFromYear;
	}

	public String getScPropSubmitToMonth() {
		return scPropSubmitToMonth;
	}

	public void setScPropSubmitToMonth(String scPropSubmitToMonth) {
		this.scPropSubmitToMonth = scPropSubmitToMonth;
	}

	public String getScPropSubmitToYear() {
		return scPropSubmitToYear;
	}

	public void setScPropSubmitToYear(String scPropSubmitToYear) {
		this.scPropSubmitToYear = scPropSubmitToYear;
	}
	
	//#14306 End Harun
	
	
}
