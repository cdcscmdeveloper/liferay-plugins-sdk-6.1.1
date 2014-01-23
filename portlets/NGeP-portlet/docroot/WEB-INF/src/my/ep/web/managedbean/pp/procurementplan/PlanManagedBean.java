/*
 * Copyright © 2013 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pp.procurementplan;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanActivity;
import my.ep.persistence.entity.pp.procurementplan.PpPlanDtl;
import my.ep.web.bean.pp.ProcurementPlanSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

import org.primefaces.model.DualListModel;

@ManagedBean(name = "planManagedBean")
@SessionScoped
public class PlanManagedBean extends BaseManagedBean{

	private static final long serialVersionUID = 1L;
	
	private String viewMode;

	private List <PmOrgProfile> currentProfiles;
	
	private Boolean amanahFlag;
	
	private PpPlan selectedPlan; 
	
	private PpPlanActivity selectedPlanActivity; 
	
	private DualListModel<PmUser> users;  
	
	private boolean enableTab; //enable the details tab

	private int selectedTab;
	
	
	/**
	 * @return the amanahFlag
	 */
	public Boolean getAmanahFlag() {
		return amanahFlag;
	}

	/**
	 * @param amanahFlag the amanahFlag to set
	 */
	public void setAmanahFlag(Boolean amanahFlag) {
		this.amanahFlag = amanahFlag;
	}

	
	/**
	 * @return the users
	 */
	public DualListModel<PmUser> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(DualListModel<PmUser> users) {
		this.users = users;
	}
	
	
	/**
	 * @return the enableTab
	 */
	public boolean isEnableTab() {
		return enableTab;
	}

	/**
	 * @param enableTab the enableTab to set
	 */
	public void setEnableTab(boolean enableTab) {
		this.enableTab = enableTab;
	}

	/**
	 * @return the selectedTab
	 */
	public int getSelectedTab() {
		return selectedTab;
	}

	/**
	 * @param selectedTab the selectedTab to set
	 */
	public void setSelectedTab(int selectedTab) {
		this.selectedTab = selectedTab;
	}

	/**
	 * @return the selectedPlan
	 */
	public PpPlan getSelectedPlan() {
		if(selectedPlan==null)
			selectedPlan = new PpPlan();
		return selectedPlan;
	}
	/**
	 * @param selectedPlan the selectedPlan to set
	 */
	public void setSelectedPlan(PpPlan selectedPlan) {
		this.selectedPlan = selectedPlan;
	}

	/**
	 * @return the selectedPlanActivity
	 */
	public PpPlanActivity getSelectedPlanActivity() {
		if(selectedPlanActivity==null)
			selectedPlanActivity = new PpPlanActivity();
		return selectedPlanActivity;
	}

	/**
	 * @param selectedPlanActivity the selectedPlanActivity to set
	 */
	public void setSelectedPlanActivity(PpPlanActivity selectedPlanActivity) {
		this.selectedPlanActivity = selectedPlanActivity;
	}

	
	









	private boolean searchPlan;
	
	
	
	
	
//	private List <PpPlanMaster> planMasterList;
//
//	private PpPlanMaster planMaster; //for main master plan

	
	
	

//	private PpPlanMaster selectedPlanMaster; //for ui master plan
	
	
	
	private PpPlan selectedPlanItem; //for ui plan (multiple)

	private PpPlanDtl selectedItem; //for ui item
	
	private PpRemark selectedItemRemark; //for ui item remark
	
	
	
	private boolean enableChildDetail; //enable to add details' child 
	
	private String current;

	
	private String bpmTaskId; 
	
	private List <PpPlanActivity> allTaskActivity;
	
	private String dynamicDetailsName;

	private ProcurementPlanSearchBean ppSearchPlan;
	
	private List <PpBudgetAllocDtl> budgetAllocDtlList;
	
	private List<PpPlan> planList;
	
	
	private boolean reasonRejectOther;
	

	
	
	

//	/**
//	 * @return the planMasterList
//	 */
//	public List<PpPlanMaster> getPlanMasterList() {
//		return planMasterList;
//	}
//
//	/**
//	 * @param planMasterList the planMasterList to set
//	 */
//	public void setPlanMasterList(List<PpPlanMaster> planMasterList) {
//		this.planMasterList = planMasterList;
//	}

	/**
	 * @return the planList
	 */
	public List<PpPlan> getPlanList() {
		return planList;
	}

	/**
	 * @param planList the planList to set
	 */
	public void setPlanList(List<PpPlan> planList) {
		this.planList = planList;
	}

	/**
	 * @return the dynamicDetailsName
	 */
	public String getDynamicDetailsName() {
		return dynamicDetailsName;
	}

	/**
	 * @param dynamicDetailsName the dynamicDetailsName to set
	 */
	public void setDynamicDetailsName(String dynamicDetailsName) {
		this.dynamicDetailsName = dynamicDetailsName;
	}


	/**
	 * @return the budgetAllocDtlList
	 */
	public List<PpBudgetAllocDtl> getBudgetAllocDtlList() {
		return budgetAllocDtlList;
	}

	/**
	 * @param budgetAllocDtlList the budgetAllocDtlList to set
	 */
	public void setBudgetAllocDtlList(
			List<PpBudgetAllocDtl> budgetAllocDtlList) {
		this.budgetAllocDtlList = budgetAllocDtlList;
	}
	
//	/**
//	 * @return the votType
//	 */
//	public String getVotType() {
//		return votType;
//	}
//
//	/**
//	 * @param votType the votType to set
//	 */
//	public void setVotType(String votType) {
//		this.votType = votType;
//	}

	/**
	 * @return the selectedPlanItem
	 */
	public PpPlan getSelectedPlanItem() {
		return selectedPlanItem;
	}

	/**
	 * @param selectedPlanItem the selectedPlanItem to set
	 */
	public void setSelectedPlanItem(PpPlan selectedPlanItem) {
		this.selectedPlanItem = selectedPlanItem;
	}


	/**
	 * @return the allTaskActivity
	 */
	public List<PpPlanActivity> getAllTaskActivity() {
		return allTaskActivity;
	}

	/**
	 * @param allTaskActivity the allTaskActivity to set
	 */
	public void setAllTaskActivity(List<PpPlanActivity> allTaskActivity) {
		this.allTaskActivity = allTaskActivity;
	}

	/**
	 * @return the bpmTaskId
	 */
	public String getBpmTaskId() {
		return bpmTaskId;
	}

	/**
	 * @param bpmTaskId the bpmTaskId to set
	 */
	public void setBpmTaskId(String bpmTaskId) {
		this.bpmTaskId = bpmTaskId;
	}

	
	/**
	 * @return the enableChildDetail
	 */
	public boolean isEnableChildDetail() {
		return enableChildDetail;
	}

	/**
	 * @param enableChildDetail the enableChildDetail to set
	 */
	public void setEnableChildDetail(boolean enableChildDetail) {
		this.enableChildDetail = enableChildDetail;
	}


	/**
	 * @return the selectedItem
	 */
	public PpPlanDtl getSelectedItem() {
		return selectedItem;
	}
	/**
	 * @param selectedItem the selectedItem to set
	 */
	public void setSelectedItem(PpPlanDtl selectedItem) {
		this.selectedItem = selectedItem;
	}
	
	/**
	 * @return the selectedItemRemark
	 */
	public PpRemark getSelectedItemRemark() {
		return selectedItemRemark;
	}
	/**
	 * @param selectedItemRemark the selectedItemRemark to set
	 */
	public void setSelectedItemRemark(PpRemark selectedItemRemark) {
		this.selectedItemRemark = selectedItemRemark;
	}

//
//	/**
//	 * @return the selectedPlanMaster
//	 */
//	public PpPlanMaster getSelectedPlanMaster() {
//		return selectedPlanMaster;
//	}
//	/**
//	 * @param selectedPlanMaster the selectedPlanMaster to set
//	 */
//	public void setSelectedPlanMaster(PpPlanMaster selectedPlanMaster) {
//		this.selectedPlanMaster = selectedPlanMaster;
//	}

	/**
	 * @return the searchPlan
	 */
	public boolean isSearchPlan() {
		return searchPlan;
	}
	/**
	 * @param searchPlan the searchPlan to set
	 */
	public void setSearchPlan(boolean searchPlan) {
		this.searchPlan = searchPlan;
	}
	
	
	/**
	 * @return the current
	 */
	public String getCurrent() {
		return current;
	}
	/**
	 * @param selectedPlan the selectedPlan to set
	 */
	public void setCurrent(String current) {
		this.current = current;
	}
	/**
	 * @return the viewMode
	 */
	public String getViewMode() {
		return viewMode;
	}
	/**
	 * @param selectedPlan the selectedPlan to set
	 */
	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

//	/**
//	 * @return the planMaster
//	 */
//	public PpPlanMaster getPlanMaster() {
//		return planMaster;
//	}
//	/**
//	 * @param planMaster the planMaster to set
//	 */
//	public void setPlanMaster(PpPlanMaster planMaster) {
//		this.planMaster = planMaster;
//	}


	
	
	
	
	
	private boolean brandNew;
	private String procurementMode;
	
	private boolean popup = false;
	private String planType;



	//temp
	private String fulfillmentType;
	//temp
	private String periodicType;
	//temp
	private String documentType;
	//temp
	private Boolean verification  = false;

	//temp
	private String favouriteOption = "N";
	//temp
	private String quotationTenderType;
	//temp
	private String typeOfGoodsServices;

	//temp
	private String contractType;
	//temp
	private Integer recurringFreq;
	//temp
	private String maksudCode;
	
	private List ppItemList = null;
	//temp
	private List remarkList = null;


	public List getPpItemList() {
		return ppItemList;
	}
	public List getRemarkList() {
		return remarkList;
	}
	public void setRemarkList(List remarkList) {
		this.remarkList = remarkList;
	}
	public void setPpItemList(List ppItemList) {
		this.ppItemList = ppItemList;
	}
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
	}
	
	public String getMaksudCode() {
		return maksudCode;
	}
	public void setMaksudCode(String maksudCode) {
		this.maksudCode = maksudCode;
	}
	public Integer getRecurringFreq() {
		return recurringFreq;
	}
	public void setRecurringFreq(Integer recurringFreq) {
		this.recurringFreq = recurringFreq;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	
//	public List<String> getSourcingAct() {
//		return sourcingAct;
//	}
//	public void setSourcingAct(List<String> sourcingAct) {
//		this.sourcingAct = sourcingAct;
//	}
	public boolean isBrandNew() {
		return brandNew;
	}
	public void setBrandNew(boolean brandNew) {
		this.brandNew = brandNew;
	}

	
	public String getTypeOfGoodsServices() {
		return typeOfGoodsServices;
	}
	public void setTypeOfGoodsServices(String typeOfGoodsServices) {
		this.typeOfGoodsServices = typeOfGoodsServices;
	}
	public String getFavouriteOption() {
		return favouriteOption;
	}
	public void setFavouriteOption(String favouriteOption) {
		this.favouriteOption = favouriteOption;
	}
	public String getQuotationTenderType() {
		return quotationTenderType;
	}
	public void setQuotationTenderType(String quotationTenderType) {
		this.quotationTenderType = quotationTenderType;
	}

	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getPeriodicType() {
		return periodicType;
	}
	public void setPeriodicType(String periodicType) {
		this.periodicType = periodicType;
	}
	public String getFulfillmentType() {
		return fulfillmentType;
	}
	public void setFulfillmentType(String fulfillmentType) {
		this.fulfillmentType = fulfillmentType;
	}

	public String getProcurementMode() {
		return procurementMode;
	}
	public void setProcurementMode(String procurementMode) {
		this.procurementMode = procurementMode;
	}

	
	
	public boolean isPopup() {
		return popup;
	}
	public void setPopup(boolean popup) {
		this.popup = popup;
	}

	public Boolean getVerification() {
		return verification;
	}
	public void setVerification(Boolean verification) {
		this.verification = verification;
	}

	

	/**
	 * @return the reasonRejectOther
	 */
	public boolean isReasonRejectOther() {
		return reasonRejectOther;
	}

	/**
	 * @param reasonRejectOther the reasonRejectOther to set
	 */
	public void setReasonRejectOther(boolean reasonRejectOther) {
		this.reasonRejectOther = reasonRejectOther;
	}

	/**
	 * @return the ppSearchPlan
	 */
	public ProcurementPlanSearchBean getPpSearchPlan() {
		if(ppSearchPlan==null)
			ppSearchPlan = new ProcurementPlanSearchBean();
		return ppSearchPlan;
	}

	/**
	 * @param ppSearchPlan the ppSearchPlan to set
	 */
	public void setPpSearchPlan(ProcurementPlanSearchBean ppSearchPlan) {
		this.ppSearchPlan = ppSearchPlan;
	}

	/**
	 * @return the currentProfiles
	 */
	public List<PmOrgProfile> getCurrentProfiles() {
		return currentProfiles;
	}

	/**
	 * @param currentProfiles the currentProfiles to set
	 */
	public void setCurrentProfiles(List<PmOrgProfile> currentProfiles) {
		this.currentProfiles = currentProfiles;
	}



	
	
	
	

}

