package my.ep.web.managedbean.pp.procurementplan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.constant.pp.ProcurementPlanConst;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pp.procurementplan.PpExpenditureItem;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpProcurementItem;
import my.ep.persistence.entity.pp.procurementplan.PpRejectReason;
import my.ep.type.pm.RoleCode;
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
	
	
	private PpPlan selectedPlan;
	private List<PpExpenditureItem> deleteEpItems;
	private List<PpProcurementItem> deletePpItems;
	private DualListModel<PmUser> approverModel;
	private DualListModel<PmUser> verifierModel;
	private List<PpRejectReason> selectedRejectReasons;
	private BigDecimal epTotalBudget;
	private BigDecimal epTotalPlan;
	private BigDecimal epTotalNonEpAlloc;
	private BigDecimal ppTotalEst;
	

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





	
	
	
	
	
	
	

	//	TODO remove here
	private String currentRole; //TODO
	private List<String> roleList; //TODO
	private Long currentStatus; //TODO
	private List<Long> statusList; //TODO
	
	
/**
	 * @return the currentRole
	 */
	public String getCurrentRole() {
		return currentRole;
	}

	/**
	 * @param currentRole the currentRole to set
	 */
	public void setCurrentRole(String currentRole) {
		this.currentRole = currentRole;
	}

	/**
	 * @return the roleList
	 */
	public List<String> getRoleList() {
		if(roleList==null){
			roleList = new ArrayList<String>();
			roleList.add("NULL");
			roleList.add(RoleCode.PLAN_PROCU_OFFICER.value());
			roleList.add(RoleCode.PLAN_VERFICATION_OFFICER.value());
			roleList.add(RoleCode.PLAN_APPROVER.value());
			roleList.add(RoleCode.BPK_ADMIN.value());
		}
		return roleList;
	}

	/**
	 * @param roleList the roleList to set
	 */
	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}
	
	/**
	 * @return the statusList
	 */
	public List<Long> getStatusList() {
		if(statusList==null){
			statusList = new ArrayList<Long>();
			statusList.add(ProcurementPlanConst.PPP_PENDING_SUBMISSION);
			statusList.add(ProcurementPlanConst.PPP_PENDING_REVISION);
			statusList.add(ProcurementPlanConst.PPP_PENDING_VERIFICATION);
			statusList.add(ProcurementPlanConst.PPP_PENDING_APPROVAL);
			statusList.add(ProcurementPlanConst.PPP_APPROVED);
		}
		return statusList;
	}

	/**
	 * @param modeList the statusList to set
	 */
	public void setStatusList(List<Long> statusList) {
		this.statusList = statusList;
	}

	/**
	 * @return the currentStatus
	 */
	public Long getCurrentStatus() {
		return currentStatus;
	}

	/**
	 * @param currentStatus the currentStatus to set
	 */
	public void setCurrentStatus(Long currentStatus) {
		this.currentStatus = currentStatus;
	}
	
	
	

}
