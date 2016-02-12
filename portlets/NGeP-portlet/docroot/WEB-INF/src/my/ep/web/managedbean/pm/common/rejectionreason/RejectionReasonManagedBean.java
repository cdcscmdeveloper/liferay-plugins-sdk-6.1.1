/*
 * Copyright © 2014 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.common.rejectionreason;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmPIDReason;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmSupplierReason;
import my.ep.web.bean.pm.ReasonSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Rejection Reason
 * 
 * @author RAIN POH
 * @since 8/7/2013
 *
 */
@ManagedBean(name = "rejectionReasonManagedBean")
@SessionScoped
public class RejectionReasonManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	
	private ReasonSearchBean reasonSearchBean;
	private PmReason selectedReason;
	private boolean suppReason;
	private boolean searchSuppReason;
	private List <PmSupplierReason> appTypeRestrictions;
	private List <PmSupplierReason> comTypeRestrictions;
	private List <PmSupplierReason> suppTypeRestrictions;
	private List <PmSupplierReason> deleteAppTypeRestrictions;
	private List <PmSupplierReason> deleteComTypeRestrictions;
	private List <PmSupplierReason> deleteSuppTypeRestrictions;
	private boolean suppAppCheck;
	private boolean suppComCheck;
	private boolean suppTypeCheck;
	private boolean contractPurchaseCheck;
	
	private List <PmSupplierReason> deleteRestrictions;
	private int selectedTabIndex;
	private List<PmPIDReason> pmPidListOrigin = new ArrayList<PmPIDReason>();
	/**
	 * @return the reasonSearchBean
	 */
	public ReasonSearchBean getReasonSearchBean() {
		if(reasonSearchBean==null)
			reasonSearchBean = new ReasonSearchBean();
		return reasonSearchBean;
	}

	/**
	 * @param reasonSearchBean the reasonSearchBean to set
	 */
	public void setReasonSearchBean(ReasonSearchBean reasonSearchBean) {
		this.reasonSearchBean = reasonSearchBean;
	}

	/**
	 * @return the selectedReason
	 */
	public PmReason getSelectedReason() {
		if(selectedReason==null)
			selectedReason = new PmReason();
		return selectedReason;
	}

	/**
	 * @param selectedReason the selectedReason to set
	 */
	public void setSelectedReason(PmReason selectedReason) {
		this.selectedReason = selectedReason;
	}

	/**
	 * @return the suppReason
	 */
	public boolean isSuppReason() {
		return suppReason;
	}

	/**
	 * @param suppReason the suppReason to set
	 */
	public void setSuppReason(boolean suppReason) {
		this.suppReason = suppReason;
	}

	/**
	 * @return the searchSuppReason
	 */
	public boolean isSearchSuppReason() {
		return searchSuppReason;
	}

	/**
	 * @param searchSuppReason the searchSuppReason to set
	 */
	public void setSearchSuppReason(boolean searchSuppReason) {
		this.searchSuppReason = searchSuppReason;
	}

	/**
	 * @return the appTypeRestrictions
	 */
	public List<PmSupplierReason> getAppTypeRestrictions() {
		return appTypeRestrictions;
	}

	/**
	 * @param appTypeRestrictions the appTypeRestrictions to set
	 */
	public void setAppTypeRestrictions(List<PmSupplierReason> appTypeRestrictions) {
		this.appTypeRestrictions = appTypeRestrictions;
	}

	/**
	 * @return the comTypeRestrictions
	 */
	public List<PmSupplierReason> getComTypeRestrictions() {
		return comTypeRestrictions;
	}

	/**
	 * @param comTypeRestrictions the comTypeRestrictions to set
	 */
	public void setComTypeRestrictions(List<PmSupplierReason> comTypeRestrictions) {
		this.comTypeRestrictions = comTypeRestrictions;
	}

	/**
	 * @return the suppTypeRestrictions
	 */
	public List<PmSupplierReason> getSuppTypeRestrictions() {
		return suppTypeRestrictions;
	}

	/**
	 * @param suppTypeRestrictions the suppTypeRestrictions to set
	 */
	public void setSuppTypeRestrictions(List<PmSupplierReason> suppTypeRestrictions) {
		this.suppTypeRestrictions = suppTypeRestrictions;
	}

//	if(deleteTypeRestrictions==null){
//		deleteTypeRestrictions = new ArrayList<PmSupplierReason>();
//	}
	/**
	 * @return the deleteAppTypeRestrictions
	 */
	public List<PmSupplierReason> getDeleteAppTypeRestrictions() {
		return deleteAppTypeRestrictions;
	}

	/**
	 * @param deleteAppTypeRestrictions the deleteAppTypeRestrictions to set
	 */
	public void setDeleteAppTypeRestrictions(
			List<PmSupplierReason> deleteAppTypeRestrictions) {
		this.deleteAppTypeRestrictions = deleteAppTypeRestrictions;
	}

	/**
	 * @return the deleteComTypeRestrictions
	 */
	public List<PmSupplierReason> getDeleteComTypeRestrictions() {
		return deleteComTypeRestrictions;
	}

	/**
	 * @param deleteComTypeRestrictions the deleteComTypeRestrictions to set
	 */
	public void setDeleteComTypeRestrictions(
			List<PmSupplierReason> deleteComTypeRestrictions) {
		this.deleteComTypeRestrictions = deleteComTypeRestrictions;
	}

	/**
	 * @return the deleteSuppTypeRestrictions
	 */
	public List<PmSupplierReason> getDeleteSuppTypeRestrictions() {
		return deleteSuppTypeRestrictions;
	}

	/**
	 * @param deleteSuppTypeRestrictions the deleteSuppTypeRestrictions to set
	 */
	public void setDeleteSuppTypeRestrictions(
			List<PmSupplierReason> deleteSuppTypeRestrictions) {
		this.deleteSuppTypeRestrictions = deleteSuppTypeRestrictions;
	}

	/**
	 * @return the suppAppCheck
	 */
	public boolean isSuppAppCheck() {
		return suppAppCheck;
	}

	/**
	 * @param suppAppCheck the suppAppCheck to set
	 */
	public void setSuppAppCheck(boolean suppAppCheck) {
		this.suppAppCheck = suppAppCheck;
	}

	/**
	 * @return the suppComCheck
	 */
	public boolean isSuppComCheck() {
		return suppComCheck;
	}

	/**
	 * @param suppComCheck the suppComCheck to set
	 */
	public void setSuppComCheck(boolean suppComCheck) {
		this.suppComCheck = suppComCheck;
	}

	/**
	 * @return the suppTypeCheck
	 */
	public boolean isSuppTypeCheck() {
		return suppTypeCheck;
	}

	/**
	 * @param suppTypeCheck the suppTypeCheck to set
	 */
	public void setSuppTypeCheck(boolean suppTypeCheck) {
		this.suppTypeCheck = suppTypeCheck;
	}

	/**
	 * @return the deleteRestrictions
	 */
	public List<PmSupplierReason> getDeleteRestrictions() {
		return deleteRestrictions;
	}

	/**
	 * @param deleteRestrictions the deleteRestrictions to set
	 */
	public void setDeleteRestrictions(List<PmSupplierReason> deleteRestrictions) {
		this.deleteRestrictions = deleteRestrictions;
	}

	public int getSelectedTabIndex() {
		return selectedTabIndex;
	}

	public void setSelectedTabIndex(int selectedTabIndex) {
		this.selectedTabIndex = selectedTabIndex;
	}

	public boolean isContractPurchaseCheck() {
		return contractPurchaseCheck;
	}

	public void setContractPurchaseCheck(boolean contractPurchaseCheck) {
		this.contractPurchaseCheck = contractPurchaseCheck;
	}

	public List<PmPIDReason> getPmPidListOrigin() {
		return pmPidListOrigin;
	}

	public void setPmPidListOrigin(List<PmPIDReason> pmPidListOrigin) {
		this.pmPidListOrigin = pmPidListOrigin;
	}
	
}
