package my.ep.web.managedbean.pp.procurementplan;

import java.math.BigDecimal;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pp.procurementplan.PpExpenditureItem;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpProcurementItem;
import my.ep.persistence.entity.pp.procurementplan.PpRejectReason;
import my.ep.persistence.entity.pp.procurementplan.PpRemark;
import my.ep.web.bean.pp.ProcurementPlanSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;

/**
 * Managed Bean for Procurement Plan 
 * 
 * @author RAIN POH
 * @since 20/10/2013
 *
 */
@ManagedBean(name = "pPlanManagedBean")
@SessionScoped
public class ProcurementPlanManagedBean extends BaseManagedBean{

	private ProcurementPlanSearchBean planSearchBean;
	private String filterOrgCondition;
	private PmOrgProfile currentActiveOrg;
	
	private boolean modeReview;
	private boolean modeReviewEditable;
	private boolean modeEditable;
	private boolean isSearch;
	private boolean backAllow;
	
	private PpPlan selectedPlan;
	private List<PpExpenditureItem> deleteEpItems;
	private List<PpProcurementItem> deletePpItems;
	private DualListModel<PmUser> approverModel;
	private DualListModel<PmUser> verifierModel;
	private boolean confirmJustification;
	private boolean confirmReason;
	private boolean confirmRemark;
	private String selectedReasonDesc;
	private String selectedRemarkDesc;
	private String selectedItemRemarkDesc;
	private String selectedJustificationDesc;
	private List<PpRejectReason> selectedRejectReasons;
	private List<PpRemark> selectedRemarks;
	private List<PpRemark> selectedJustifications;
	private List<PpRemark> selectedItemRemarks;
	private BigDecimal epTotalBudget;
	private BigDecimal epTotalPlan;
	private BigDecimal epTotalNonEpAlloc;
	private BigDecimal ppTotalEst;
	
	
	
	

	/**
	 * @return the backAllow
	 */
	public boolean isBackAllow() {
		return backAllow;
	}

	/**
	 * @param backAllow the backAllow to set
	 */
	public void setBackAllow(boolean backAllow) {
		this.backAllow = backAllow;
	}

	/**
	 * @return the selectedRemarkDesc
	 */
	public String getSelectedRemarkDesc() {
		return selectedRemarkDesc;
	}

	/**
	 * @param selectedRemarkDesc the selectedRemarkDesc to set
	 */
	public void setSelectedRemarkDesc(String selectedRemarkDesc) {
		this.selectedRemarkDesc = selectedRemarkDesc;
	}

	/**
	 * @return the selectedItemRemarks
	 */
	public List<PpRemark> getSelectedItemRemarks() {
		return selectedItemRemarks;
	}

	/**
	 * @param selectedItemRemarks the selectedItemRemarks to set
	 */
	public void setSelectedItemRemarks(List<PpRemark> selectedItemRemarks) {
		this.selectedItemRemarks = selectedItemRemarks;
	}



	/**
	 * @return the confirmJustification
	 */
	public boolean isConfirmJustification() {
		return confirmJustification;
	}

	/**
	 * @param confirmJustification the confirmJustification to set
	 */
	public void setConfirmJustification(boolean confirmJustification) {
		this.confirmJustification = confirmJustification;
	}

	/**
	 * @return the confirmReason
	 */
	public boolean isConfirmReason() {
		return confirmReason;
	}

	/**
	 * @param confirmReason the confirmReason to set
	 */
	public void setConfirmReason(boolean confirmReason) {
		this.confirmReason = confirmReason;
	}

	/**
	 * @return the confirmRemark
	 */
	public boolean isConfirmRemark() {
		return confirmRemark;
	}

	/**
	 * @param confirmRemark the confirmRemark to set
	 */
	public void setConfirmRemark(boolean confirmRemark) {
		this.confirmRemark = confirmRemark;
	}

	/**
	 * @return the isSearch
	 */
	public boolean isSearch() {
		return isSearch;
	}

	/**
	 * @param isSearch the isSearch to set
	 */
	public void setSearch(boolean isSearch) {
		this.isSearch = isSearch;
	}

	/**
	 * @return the selectedRemarks
	 */
	public List<PpRemark> getSelectedRemarks() {
		return selectedRemarks;
	}

	/**
	 * @param selectedRemarks the selectedRemarks to set
	 */
	public void setSelectedRemarks(List<PpRemark> selectedRemarks) {
		this.selectedRemarks = selectedRemarks;
	}

	/**
	 * @return the selectedReasonDesc
	 */
	public String getSelectedReasonDesc() {
		return selectedReasonDesc;
	}

	/**
	 * @param selectedReasonDesc the selectedReasonDesc to set
	 */
	public void setSelectedReasonDesc(String selectedReasonDesc) {
		this.selectedReasonDesc = selectedReasonDesc;
	}

	/**
	 * @return the filterOrgCondition
	 */
	public String getFilterOrgCondition() {
		return filterOrgCondition;
	}

	/**
	 * @param filterOrgCondition the filterOrgCondition to set
	 */
	public void setFilterOrgCondition(String filterOrgCondition) {
		this.filterOrgCondition = filterOrgCondition;
	}

	/**
	 * @return the currentActiveOrg
	 */
	public PmOrgProfile getCurrentActiveOrg() {
		return currentActiveOrg;
	}

	/**
	 * @param currentActiveOrg the currentActiveOrg to set
	 */
	public void setCurrentActiveOrg(PmOrgProfile currentActiveOrg) {
		this.currentActiveOrg = currentActiveOrg;
	}

	/**
	 * @return the modeReviewEditable
	 */
	public boolean isModeReviewEditable() {
		return modeReviewEditable;
	}

	/**
	 * @param modeReviewEditable the modeReviewEditable to set
	 */
	public void setModeReviewEditable(boolean modeReviewEditable) {
		this.modeReviewEditable = modeReviewEditable;
	}

	/**
	 * @return the selectedRejectReasons
	 */
	public List<PpRejectReason> getSelectedRejectReasons() {
		return selectedRejectReasons;
	}

	/**
	 * @param selectedRejectReasons the selectedRejectReasons to set
	 */
	public void setSelectedRejectReasons(List<PpRejectReason> selectedRejectReasons) {
		this.selectedRejectReasons = selectedRejectReasons;
	}



	/**
	 * @return the approverModel
	 */
	public DualListModel<PmUser> getApproverModel() {
		return approverModel;
	}

	/**
	 * @param approverModel the approverModel to set
	 */
	public void setApproverModel(DualListModel<PmUser> approverModel) {
		this.approverModel = approverModel;
	}

	/**
	 * @return the verifierModel
	 */
	public DualListModel<PmUser> getVerifierModel() {
		return verifierModel;
	}

	/**
	 * @param verifierModel the verifierModel to set
	 */
	public void setVerifierModel(DualListModel<PmUser> verifierModel) {
		this.verifierModel = verifierModel;
	}

	/**
	 * @return the deleteEpItems
	 */
	public List<PpExpenditureItem> getDeleteEpItems() {
		return deleteEpItems;
	}

	/**
	 * @param deleteEpItems the deleteEpItems to set
	 */
	public void setDeleteEpItems(List<PpExpenditureItem> deleteEpItems) {
		this.deleteEpItems = deleteEpItems;
	}

	/**
	 * @return the deletePpItems
	 */
	public List<PpProcurementItem> getDeletePpItems() {
		return deletePpItems;
	}

	/**
	 * @param deletePpItems the deletePpItems to set
	 */
	public void setDeletePpItems(List<PpProcurementItem> deletePpItems) {
		this.deletePpItems = deletePpItems;
	}


	/**
	 * @return the planSearchBean
	 */
	public ProcurementPlanSearchBean getPlanSearchBean() {
		if(planSearchBean==null)
			planSearchBean = new ProcurementPlanSearchBean();
		return planSearchBean;
	}

	/**
	 * @param planSearchBean the planSearchBean to set
	 */
	public void setPlanSearchBean(ProcurementPlanSearchBean planSearchBean) {
		this.planSearchBean = planSearchBean;
	}

	/**
	 * @return the selectedPlan
	 */
	public PpPlan getSelectedPlan() {
		return selectedPlan;
	}

	/**
	 * @param selectedPlan the selectedPlan to set
	 */
	public void setSelectedPlan(PpPlan selectedPlan) {
		this.selectedPlan = selectedPlan;
	}

	/**
	 * @return the modeReview
	 */
	public boolean isModeReview() {
		return modeReview;
	}

	/**
	 * @param modeReview the modeReview to set
	 */
	public void setModeReview(boolean modeReview) {
		this.modeReview = modeReview;
	}

	/**
	 * @return the modeEditable
	 */
	public boolean isModeEditable() {
		return modeEditable;
	}

	/**
	 * @param modeEditable the modeEditable to set
	 */
	public void setModeEditable(boolean modeEditable) {
		this.modeEditable = modeEditable;
	}
//
//	/**
//	 * @return the selectedRemarks
//	 */
//	public List<PpRemark> getSelectedRemarks() {
//		return selectedRemarks;
//	}
//
//	/**
//	 * @param selectedRemarks the selectedRemarks to set
//	 */
//	public void setSelectedRemarks(List<PpRemark> selectedRemarks) {
//		this.selectedRemarks = selectedRemarks;
//	}


	/**
	 * @return the epTotalBudget
	 */
	public BigDecimal getEpTotalBudget() {
		if(epTotalBudget==null)
			epTotalBudget = new BigDecimal(0);
		return epTotalBudget;
	}

	/**
	 * @param epTotalBudget the epTotalBudget to set
	 */
	public void setEpTotalBudget(BigDecimal epTotalBudget) {
		this.epTotalBudget = epTotalBudget;
	}

	/**
	 * @return the epTotalPlan
	 */
	public BigDecimal getEpTotalPlan() {
		if(epTotalPlan==null)
			epTotalPlan = new BigDecimal(0);
		return epTotalPlan;
	}

	/**
	 * @param epTotalPlan the epTotalPlan to set
	 */
	public void setEpTotalPlan(BigDecimal epTotalPlan) {
		this.epTotalPlan = epTotalPlan;
	}

	/**
	 * @return the epTotalNonEpAlloc
	 */
	public BigDecimal getEpTotalNonEpAlloc() {
		if(epTotalNonEpAlloc==null)
			epTotalNonEpAlloc = new BigDecimal(0);
		return epTotalNonEpAlloc;
	}

	/**
	 * @param epTotalNonEpAlloc the epTotalNonEpAlloc to set
	 */
	public void setEpTotalNonEpAlloc(BigDecimal epTotalNonEpAlloc) {
		this.epTotalNonEpAlloc = epTotalNonEpAlloc;
	}

	/**
	 * @return the ppTotalEst
	 */
	public BigDecimal getPpTotalEst() {
		if(ppTotalEst==null)
			ppTotalEst = new BigDecimal(0);
		return ppTotalEst;
	}

	/**
	 * @param ppTotalEst the ppTotalEst to set
	 */
	public void setPpTotalEst(BigDecimal ppTotalEst) {
		this.ppTotalEst = ppTotalEst;
	}

	/**
	 * @return the selectedJustifications
	 */
	public List<PpRemark> getSelectedJustifications() {
		return selectedJustifications;
	}

	/**
	 * @param selectedJustifications the selectedJustifications to set
	 */
	public void setSelectedJustifications(List<PpRemark> selectedJustifications) {
		this.selectedJustifications = selectedJustifications;
	}

	/**
	 * @return the selectedItemRemarkDesc
	 */
	public String getSelectedItemRemarkDesc() {
		return selectedItemRemarkDesc;
	}

	/**
	 * @param selectedItemRemarkDesc the selectedItemRemarkDesc to set
	 */
	public void setSelectedItemRemarkDesc(String selectedItemRemarkDesc) {
		this.selectedItemRemarkDesc = selectedItemRemarkDesc;
	}

	/**
	 * @return the selectedJustificationDesc
	 */
	public String getSelectedJustificationDesc() {
		return selectedJustificationDesc;
	}

	/**
	 * @param selectedJustificationDesc the selectedJustificationDesc to set
	 */
	public void setSelectedJustificationDesc(String selectedJustificationDesc) {
		this.selectedJustificationDesc = selectedJustificationDesc;
	}

	




	
	

}
