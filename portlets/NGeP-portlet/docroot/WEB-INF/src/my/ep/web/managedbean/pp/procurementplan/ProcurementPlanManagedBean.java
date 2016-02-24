package my.ep.web.managedbean.pp.procurementplan;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pp.procurementplan.PpExpenditureItem;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpProcurementItem;
import my.ep.persistence.entity.pp.procurementplan.PpRejectReason;
import my.ep.persistence.entity.pp.procurementplan.PpRemark;
import my.ep.persistence.entity.pp.procurementplan.PpWorkflowStatus;
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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ProcurementPlanSearchBean planSearchBean;
	private String filterOrgCondition;
	private PmOrgProfile currentActiveOrg;
	private PmOrgProfile orgProfile;
	
	private boolean modeReview;
	private boolean modeReviewEditable;
	private boolean modeEditable;
	private boolean isSearch;
	private boolean backAllow;
	private boolean approvedPlan;
	private String btnValue;
	
	private PpWorkflowStatus ppWorkflowStatus;
	private String bpmStatusCreator;
	private Timestamp bpmStatusDate;
	private Long bpmStatusId;
	private String invitationYear;
	
	private List <PpExpenditureItem> epItemsB;
	private BigDecimal epTotalBudgetB;
	private BigDecimal epTotalPlanB;
	private BigDecimal epTotalNonEpAllocB;
	private List <PpExpenditureItem> epItemsT;
	private BigDecimal epTotalBudgetT;
	private BigDecimal epTotalPlanT;
	private BigDecimal epTotalNonEpAllocT;
	private List <PpExpenditureItem> epItemsP;
	private BigDecimal epTotalBudgetP;
	private BigDecimal epTotalPlanP;
	private BigDecimal epTotalNonEpAllocP;
	private List <PpExpenditureItem> epItemsS;
	private BigDecimal epTotalBudgetS;
	private BigDecimal epTotalPlanS;
	private BigDecimal epTotalNonEpAllocS;
	private List <PpExpenditureItem> epItemsH;
	private BigDecimal epTotalBudgetH;
	private BigDecimal epTotalPlanH;
	private BigDecimal epTotalNonEpAllocH;
	
	
	private PpPlan selectedPlan;
//	private List<PpExpenditureItem> deleteEpItems;
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
	private BigDecimal ppTotalEst;

	private int selectedSearchType;
	private List<SelectItem> searchOptions;
	private String searchValue;
	private PmOrgProfile selectedPTJ;
	private PmOrgProfile selectedKumpulanPTJ;
	private PmOrgProfile selectedMinistry;
	private boolean isDisabledMinistryLov = false;
	private boolean isDisabledKumpulanPTJLov = false;
	private boolean isDisabledPTJLov = false;
	
	private boolean isTrackingDiary;

	/**
	 * @return the epItemsB
	 */
	public List<PpExpenditureItem> getEpItemsB() {
		return epItemsB;
	}

	/**
	 * @param epItemsB the epItemsB to set
	 */
	public void setEpItemsB(List<PpExpenditureItem> epItemsB) {
		this.epItemsB = epItemsB;
	}

	/**
	 * @return the epItemsT
	 */
	public List<PpExpenditureItem> getEpItemsT() {
		return epItemsT;
	}

	/**
	 * @param epItemsT the epItemsT to set
	 */
	public void setEpItemsT(List<PpExpenditureItem> epItemsT) {
		this.epItemsT = epItemsT;
	}

	/**
	 * @return the epItemsP
	 */
	public List<PpExpenditureItem> getEpItemsP() {
		return epItemsP;
	}

	/**
	 * @param epItemsP the epItemsP to set
	 */
	public void setEpItemsP(List<PpExpenditureItem> epItemsP) {
		this.epItemsP = epItemsP;
	}

	/**
	 * @return the epItemsS
	 */
	public List<PpExpenditureItem> getEpItemsS() {
		return epItemsS;
	}

	/**
	 * @param epItemsS the epItemsS to set
	 */
	public void setEpItemsS(List<PpExpenditureItem> epItemsS) {
		this.epItemsS = epItemsS;
	}

	/**
	 * @return the epItemsH
	 */
	public List<PpExpenditureItem> getEpItemsH() {
		return epItemsH;
	}

	/**
	 * @param epItemsH the epItemsH to set
	 */
	public void setEpItemsH(List<PpExpenditureItem> epItemsH) {
		this.epItemsH = epItemsH;
	}

	/**
	 * @return the approvedPlan
	 */
	public boolean isApprovedPlan() {
		return approvedPlan;
	}

	/**
	 * @param approvedPlan the approvedPlan to set
	 */
	public void setApprovedPlan(boolean approvedPlan) {
		this.approvedPlan = approvedPlan;
	}

	/**
	 * @return the invitationYear
	 */
	public String getInvitationYear() {
		return invitationYear;
	}

	/**
	 * @param invitationYear the invitationYear to set
	 */
	public void setInvitationYear(String invitationYear) {
		this.invitationYear = invitationYear;
	}

	/**
	 * @return the bpmStatusCreator
	 */
	public String getBpmStatusCreator() {
		return bpmStatusCreator;
	}

	/**
	 * @param bpmStatusCreator the bpmStatusCreator to set
	 */
	public void setBpmStatusCreator(String bpmStatusCreator) {
		this.bpmStatusCreator = bpmStatusCreator;
	}

	/**
	 * @return the bpmStatusDate
	 */
	public Timestamp getBpmStatusDate() {
		return bpmStatusDate;
	}

	/**
	 * @param bpmStatusDate the bpmStatusDate to set
	 */
	public void setBpmStatusDate(Timestamp bpmStatusDate) {
		this.bpmStatusDate = bpmStatusDate;
	}

	/**
	 * @return the bpmStatusId
	 */
	public Long getBpmStatusId() {
		return bpmStatusId;
	}

	/**
	 * @param bpmStatusId the bpmStatusId to set
	 */
	public void setBpmStatusId(Long bpmStatusId) {
		this.bpmStatusId = bpmStatusId;
	}

	/**
	 * @return the ppWorkflowStatus
	 */
	public PpWorkflowStatus getPpWorkflowStatus() {
		return ppWorkflowStatus;
	}

	/**
	 * @param ppWorkflowStatus the ppWorkflowStatus to set
	 */
	public void setPpWorkflowStatus(PpWorkflowStatus ppWorkflowStatus) {
		this.ppWorkflowStatus = ppWorkflowStatus;
	}

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

//	/**
//	 * @return the deleteEpItems
//	 */
//	public List<PpExpenditureItem> getDeleteEpItems() {
//		return deleteEpItems;
//	}
//
//	/**
//	 * @param deleteEpItems the deleteEpItems to set
//	 */
//	public void setDeleteEpItems(List<PpExpenditureItem> deleteEpItems) {
//		this.deleteEpItems = deleteEpItems;
//	}

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

	/**
	 * @return the epTotalBudgetB
	 */
	public BigDecimal getEpTotalBudgetB() {
		if(epTotalBudgetB==null)
			epTotalBudgetB = new BigDecimal(0);
		return epTotalBudgetB;
	}

	/**
	 * @param epTotalBudgetB the epTotalBudgetB to set
	 */
	public void setEpTotalBudgetB(BigDecimal epTotalBudgetB) {
		this.epTotalBudgetB = epTotalBudgetB;
	}

	/**
	 * @return the epTotalPlanB
	 */
	public BigDecimal getEpTotalPlanB() {
		if(epTotalPlanB==null)
			epTotalPlanB = new BigDecimal(0);
		return epTotalPlanB;
	}

	/**
	 * @param epTotalPlanB the epTotalPlanB to set
	 */
	public void setEpTotalPlanB(BigDecimal epTotalPlanB) {
		this.epTotalPlanB = epTotalPlanB;
	}

	/**
	 * @return the epTotalNonEpAllocB
	 */
	public BigDecimal getEpTotalNonEpAllocB() {
		if(epTotalNonEpAllocB==null)
			epTotalNonEpAllocB = new BigDecimal(0);
		return epTotalNonEpAllocB;
	}

	/**
	 * @param epTotalNonEpAllocB the epTotalNonEpAllocB to set
	 */
	public void setEpTotalNonEpAllocB(BigDecimal epTotalNonEpAllocB) {
		this.epTotalNonEpAllocB = epTotalNonEpAllocB;
	}

	/**
	 * @return the epTotalBudgetT
	 */
	public BigDecimal getEpTotalBudgetT() {
		if(epTotalBudgetT==null)
			epTotalBudgetT = new BigDecimal(0);
		return epTotalBudgetT;
	}

	/**
	 * @param epTotalBudgetT the epTotalBudgetT to set
	 */
	public void setEpTotalBudgetT(BigDecimal epTotalBudgetT) {
		this.epTotalBudgetT = epTotalBudgetT;
	}

	/**
	 * @return the epTotalPlanT
	 */
	public BigDecimal getEpTotalPlanT() {
		if(epTotalPlanT==null)
			epTotalPlanT = new BigDecimal(0);
		return epTotalPlanT;
	}

	/**
	 * @param epTotalPlanT the epTotalPlanT to set
	 */
	public void setEpTotalPlanT(BigDecimal epTotalPlanT) {
		this.epTotalPlanT = epTotalPlanT;
	}

	/**
	 * @return the epTotalNonEpAllocT
	 */
	public BigDecimal getEpTotalNonEpAllocT() {
		if(epTotalNonEpAllocT==null)
			epTotalNonEpAllocT = new BigDecimal(0);
		return epTotalNonEpAllocT;
	}

	/**
	 * @param epTotalNonEpAllocT the epTotalNonEpAllocT to set
	 */
	public void setEpTotalNonEpAllocT(BigDecimal epTotalNonEpAllocT) {
		this.epTotalNonEpAllocT = epTotalNonEpAllocT;
	}

	/**
	 * @return the epTotalBudgetP
	 */
	public BigDecimal getEpTotalBudgetP() {
		if(epTotalBudgetP==null)
			epTotalBudgetP = new BigDecimal(0);
		return epTotalBudgetP;
	}

	/**
	 * @param epTotalBudgetP the epTotalBudgetP to set
	 */
	public void setEpTotalBudgetP(BigDecimal epTotalBudgetP) {
		this.epTotalBudgetP = epTotalBudgetP;
	}

	/**
	 * @return the epTotalPlanP
	 */
	public BigDecimal getEpTotalPlanP() {
		if(epTotalPlanP==null)
			epTotalPlanP = new BigDecimal(0);
		return epTotalPlanP;
	}

	/**
	 * @param epTotalPlanP the epTotalPlanP to set
	 */
	public void setEpTotalPlanP(BigDecimal epTotalPlanP) {
		this.epTotalPlanP = epTotalPlanP;
	}

	/**
	 * @return the epTotalNonEpAllocP
	 */
	public BigDecimal getEpTotalNonEpAllocP() {
		if(epTotalNonEpAllocP==null)
			epTotalNonEpAllocP = new BigDecimal(0);
		return epTotalNonEpAllocP;
	}

	/**
	 * @param epTotalNonEpAllocP the epTotalNonEpAllocP to set
	 */
	public void setEpTotalNonEpAllocP(BigDecimal epTotalNonEpAllocP) {
		this.epTotalNonEpAllocP = epTotalNonEpAllocP;
	}

	/**
	 * @return the epTotalBudgetS
	 */
	public BigDecimal getEpTotalBudgetS() {
		if(epTotalBudgetS==null)
			epTotalBudgetS = new BigDecimal(0);
		return epTotalBudgetS;
	}

	/**
	 * @param epTotalBudgetS the epTotalBudgetS to set
	 */
	public void setEpTotalBudgetS(BigDecimal epTotalBudgetS) {
		this.epTotalBudgetS = epTotalBudgetS;
	}

	/**
	 * @return the epTotalPlanS
	 */
	public BigDecimal getEpTotalPlanS() {
		if(epTotalPlanS==null)
			epTotalPlanS = new BigDecimal(0);
		return epTotalPlanS;
	}

	/**
	 * @param epTotalPlanS the epTotalPlanS to set
	 */
	public void setEpTotalPlanS(BigDecimal epTotalPlanS) {
		this.epTotalPlanS = epTotalPlanS;
	}

	/**
	 * @return the epTotalNonEpAllocS
	 */
	public BigDecimal getEpTotalNonEpAllocS() {
		if(epTotalNonEpAllocS==null)
			epTotalNonEpAllocS = new BigDecimal(0);
		return epTotalNonEpAllocS;
	}

	/**
	 * @param epTotalNonEpAllocS the epTotalNonEpAllocS to set
	 */
	public void setEpTotalNonEpAllocS(BigDecimal epTotalNonEpAllocS) {
		this.epTotalNonEpAllocS = epTotalNonEpAllocS;
	}

	/**
	 * @return the epTotalBudgetH
	 */
	public BigDecimal getEpTotalBudgetH() {
		if(epTotalBudgetH==null)
			epTotalBudgetH = new BigDecimal(0);
		return epTotalBudgetH;
	}

	/**
	 * @param epTotalBudgetH the epTotalBudgetH to set
	 */
	public void setEpTotalBudgetH(BigDecimal epTotalBudgetH) {
		this.epTotalBudgetH = epTotalBudgetH;
	}

	/**
	 * @return the epTotalPlanH
	 */
	public BigDecimal getEpTotalPlanH() {
		if(epTotalPlanH==null)
			epTotalPlanH = new BigDecimal(0);
		return epTotalPlanH;
	}

	/**
	 * @param epTotalPlanH the epTotalPlanH to set
	 */
	public void setEpTotalPlanH(BigDecimal epTotalPlanH) {
		this.epTotalPlanH = epTotalPlanH;
	}

	/**
	 * @return the epTotalNonEpAllocH
	 */
	public BigDecimal getEpTotalNonEpAllocH() {
		if(epTotalNonEpAllocH==null)
			epTotalNonEpAllocH = new BigDecimal(0);
		return epTotalNonEpAllocH;
	}

	/**
	 * @param epTotalNonEpAllocH the epTotalNonEpAllocH to set
	 */
	public void setEpTotalNonEpAllocH(BigDecimal epTotalNonEpAllocH) {
		this.epTotalNonEpAllocH = epTotalNonEpAllocH;
	}
	
	/**
	 * @return the orgProfile
	 */
	public PmOrgProfile getOrgProfile() {
		return orgProfile;
	}
	
	/**
	 * @param orgProfile
	 *            the orgProfile to set
	 */
	public void setOrgProfile(PmOrgProfile orgProfile) {
		this.orgProfile = orgProfile;
	}

	/**
	 * @return the selectedSearchType
	 */
	public int getSelectedSearchType() {
		return selectedSearchType;
	}

	/**
	 * @param selectedSearchType the selectedSearchType to set
	 */
	public void setSelectedSearchType(int selectedSearchType) {
		this.selectedSearchType = selectedSearchType;
	}

	/**
	 * @return the searchOptions
	 */
	public List<SelectItem> getSearchOptions() {
		return searchOptions;
	}

	/**
	 * @param searchOptions the searchOptions to set
	 */
	public void setSearchOptions(List<SelectItem> searchOptions) {
		this.searchOptions = searchOptions;
	}

	/**
	 * @return the selectedPTJ
	 */
	public PmOrgProfile getSelectedPTJ() {
		return selectedPTJ;
	}

	/**
	 * @param selectedPTJ the selectedPTJ to set
	 */
	public void setSelectedPTJ(PmOrgProfile selectedPTJ) {
		this.selectedPTJ = selectedPTJ;
	}

	/**
	 * @return the searchValue
	 */
	public String getSearchValue() {
		return searchValue;
	}

	/**
	 * @param searchValue the searchValue to set
	 */
	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	/**
	 * @return the selectedKumpulanPTJ
	 */
	public PmOrgProfile getSelectedKumpulanPTJ() {
		return selectedKumpulanPTJ;
	}

	/**
	 * @param selectedKumpulanPTJ the selectedKumpulanPTJ to set
	 */
	public void setSelectedKumpulanPTJ(PmOrgProfile selectedKumpulanPTJ) {
		this.selectedKumpulanPTJ = selectedKumpulanPTJ;
	}

	/**
	 * @return the selectedMinistry
	 */
	public PmOrgProfile getSelectedMinistry() {
		return selectedMinistry;
	}

	/**
	 * @param selectedMinistry the selectedMinistry to set
	 */
	public void setSelectedMinistry(PmOrgProfile selectedMinistry) {
		this.selectedMinistry = selectedMinistry;
	}

	/**
	 * @return the isDisabledMinistryLov
	 */
	public boolean isDisabledMinistryLov() {
		return isDisabledMinistryLov;
	}

	/**
	 * @param isDisabledMinistryLov the isDisabledMinistryLov to set
	 */
	public void setDisabledMinistryLov(boolean isDisabledMinistryLov) {
		this.isDisabledMinistryLov = isDisabledMinistryLov;
	}

	/**
	 * @return the isDisabledKumpulanPTJLov
	 */
	public boolean isDisabledKumpulanPTJLov() {
		return isDisabledKumpulanPTJLov;
	}

	/**
	 * @param isDisabledKumpulanPTJLov the isDisabledKumpulanPTJLov to set
	 */
	public void setDisabledKumpulanPTJLov(boolean isDisabledKumpulanPTJLov) {
		this.isDisabledKumpulanPTJLov = isDisabledKumpulanPTJLov;
	}

	/**
	 * @return the isDisabledPTJLov
	 */
	public boolean isDisabledPTJLov() {
		return isDisabledPTJLov;
	}

	/**
	 * @param isDisabledPTJLov the isDisabledPTJLov to set
	 */
	public void setDisabledPTJLov(boolean isDisabledPTJLov) {
		this.isDisabledPTJLov = isDisabledPTJLov;
	}

	/**
	 * @return the btnValue
	 */
	public String getBtnValue() {
		return btnValue;
	}

	/**
	 * @param btnValue the btnValue to set
	 */
	public void setBtnValue(String btnValue) {
		this.btnValue = btnValue;
	}

	/**
	 * @return the isTrackingDiary
	 */
	public boolean isTrackingDiary() {
		return isTrackingDiary;
	}

	/**
	 * @param isTrackingDiary the isTrackingDiary to set
	 */
	public void setTrackingDiary(boolean isTrackingDiary) {
		this.isTrackingDiary = isTrackingDiary;
	}


}
