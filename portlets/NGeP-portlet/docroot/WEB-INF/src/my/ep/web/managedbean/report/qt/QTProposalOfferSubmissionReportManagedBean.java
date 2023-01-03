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

import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.sc.quationtender.ScQt;
import my.ep.persistence.entity.sc.quationtender.ScQtProposal;

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


}
