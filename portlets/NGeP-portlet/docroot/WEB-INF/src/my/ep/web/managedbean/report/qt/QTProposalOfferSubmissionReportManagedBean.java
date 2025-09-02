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

package my.ep.web.managedbean.report.qt;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;
import my.ep.web.bean.sc.quatationtender.QtnTenderSearchBean;

/**
 * The Class BriefingPaperReportManagedBean.
 * 
 * @author:Garikapati Mangaiah.
 */

@ManagedBean(name = "qtPropOfferSubReportMB")
@SessionScoped
public class QTProposalOfferSubmissionReportManagedBean extends QtReportManagedBean{
	private static final long serialVersionUID = 1L;
	
	private String supplierName;
	private String qtTitle;
	private Long latestApplId;
	private String proposalNo;
	private ScQt autoScQt;
	private ScQtProposal autoScQtProposal;
	private Long supplierId;//#14306
//	private String scPropSubmitFromMonth;//#14306
//	private String scPropSubmitFromYear;//#14306
//	private String scPropSubmitToMonth;//#14306
//	private String scPropSubmitToYear;//#14306
	/** The clone qtn tender search bean. */
	private QtnTenderSearchBean proposalQtnTenderSearchBean; //#14306
	
	

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
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
	 * @return the proposalNo
	 */
	public String getProposalNo() {
		return proposalNo;
	}

	/**
	 * @param proposalNo the proposalNo to set
	 */
	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}
	
	public ScQt getAutoScQt() {
		if (autoScQt == null)
			autoScQt = new ScQt();
		return autoScQt;
	}

	public void setAutoScQt(ScQt autoScQt) {
		this.autoScQt = autoScQt;
	}
	
	public ScQtProposal getAutoScQtProposal() {
		if (autoScQtProposal == null)
			autoScQtProposal = new ScQtProposal();
		return autoScQtProposal;
	}

	public void setAutoScQtProposal(ScQtProposal autoScQtProposal) {
		this.autoScQtProposal = autoScQtProposal;
	}

	/**
	 * @return the latestApplId
	 */
	public Long getLatestApplId() {
		return latestApplId;
	}

	/**
	 * @param latestApplId the latestApplId to set
	 */
	public void setLatestApplId(Long latestApplId) {
		this.latestApplId = latestApplId;
	}
	
	//#14306 - Start
	/**
	 * @return the supplierId
	 */
	public Long getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}
	
//	/**
//	 * @return the scPropSubmitFromMonth
//	 */
//	public String getScPropSubmitFromMonth() {
//		return scPropSubmitFromMonth;
//	}
//	/**
//	 * @param scPropSubmitFromMonth the scPropSubmitFromMonth to set
//	 */
//	public void setScPropSubmitFromMonth(String scPropSubmitFromMonth) {
//		this.scPropSubmitFromMonth = scPropSubmitFromMonth;
//	}
//	/**
//	 * @return the scPropSubmitFromYear
//	 */
//	public String getScPropSubmitFromYear() {
//		return scPropSubmitFromYear;
//	}
//	/**
//	 * @param scPropSubmitFromYear the scPropSubmitFromYear to set
//	 */
//	public void setScPropSubmitFromYear(String scPropSubmitFromYear) {
//		this.scPropSubmitFromYear = scPropSubmitFromYear;
//	}
//	/**
//	 * @return the scPropSubmitToMonth
//	 */
//	public String getScPropSubmitToMonth() {
//		return scPropSubmitToMonth;
//	}
//	/**
//	 * @param scPropSubmitToMonth the scPropSubmitToMonth to set
//	 */
//	public void setScPropSubmitToMonth(String scPropSubmitToMonth) {
//		this.scPropSubmitToMonth = scPropSubmitToMonth;
//	}
//	/**
//	 * @return the scPropSubmitToYear
//	 */
//	public String getScPropSubmitToYear() {
//		return scPropSubmitToYear;
//	}
//	/**
//	 * @param scPropSubmitToYear the scPropSubmitToYear to set
//	 */
//	public void setScPropSubmitToYear(String scPropSubmitToYear) {
//		this.scPropSubmitToYear = scPropSubmitToYear;
//	}
	//#14306 - End

	/**
	 * @return the proposalQtnTenderSearchBean
	 */
	public QtnTenderSearchBean getProposalQtnTenderSearchBean() {
		return proposalQtnTenderSearchBean;
	}

	/**
	 * @param proposalQtnTenderSearchBean the proposalQtnTenderSearchBean to set
	 */
	public void setProposalQtnTenderSearchBean(QtnTenderSearchBean proposalQtnTenderSearchBean) {
		this.proposalQtnTenderSearchBean = proposalQtnTenderSearchBean;
	}

	

}
