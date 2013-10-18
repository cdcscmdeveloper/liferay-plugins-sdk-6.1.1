package my.ep.web.managedbean.pp.procurementplan;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanItem;
import my.ep.persistence.entity.pp.procurementplan.PpPlanMaster;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "planManagedBean")
@SessionScoped
public class PlanManagedBean extends BaseManagedBean{

	private static final long serialVersionUID = 1L;

	private boolean searchPlan;
	
	private boolean withoutVot;
	
	private List <PpPlanMaster> planList;
	
	
	private PpPlanMaster mtoPlanMaster;
	
	private PpPlan mtoPlan;
	
	private PpPlanMaster planMaster;

	private PpPlan plan;
	
	private PpPlanMaster selectedPlanMaster;
	
	private PpPlan selectedPlan;
	
	private PpPlanItem selectedItem;
	
	private PpRemark selectedItemRemark;
	
//	private List<PpQtActivity> selectedPpQtActivityList;

	private String current;

	private String viewMode;
	
	private boolean enableDetail;
	
	private boolean enableMto;

//	public List<PpQtActivity> getSelectedPpQtActivityList() {
//		return selectedPpQtActivityList;
//	}
//	public void setSelectedPpQtActivityList(
//			List<PpQtActivity> selectedPpQtActivityList) {
//		this.selectedPpQtActivityList = selectedPpQtActivityList;
//	}

	/**
	 * @return the enableMto
	 */
	public boolean isEnableMto() {
		return enableMto;
	}
	/**
	 * @param enableMto the enableMto to set
	 */
	public void setEnableMto(boolean enableMto) {
		this.enableMto = enableMto;
	}
	/**
	 * @return the enableDetail
	 */
	public boolean isEnableDetail() {
		return enableDetail;
	}
	/**
	 * @param enableDetail the enableDetail to set
	 */
	public void setEnableDetail(boolean enableDetail) {
		this.enableDetail = enableDetail;
	}
	
	/**
	 * @return the selectedItem
	 */
	public PpPlanItem getSelectedItem() {
		return selectedItem;
	}
	/**
	 * @param selectedItem the selectedItem to set
	 */
	public void setSelectedItem(PpPlanItem selectedItem) {
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


	/**
	 * @return the selectedPlanMaster
	 */
	public PpPlanMaster getSelectedPlanMaster() {
		return selectedPlanMaster;
	}
	/**
	 * @param selectedPlanMaster the selectedPlanMaster to set
	 */
	public void setSelectedPlanMaster(PpPlanMaster selectedPlanMaster) {
		this.selectedPlanMaster = selectedPlanMaster;
	}
	/**
	 * @return the planList
	 */
	public List<PpPlanMaster> getPlanList() {
		return planList;
	}
	/**
	 * @param planList the planList to set
	 */
	public void setPlanList(List<PpPlanMaster> planList) {
		this.planList = planList;
	}

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

	public String getCurrent() {
		return current;
	}
	public void setCurrent(String current) {
		this.current = current;
	}
	
	public String getViewMode() {
		return viewMode;
	}
	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

	
	/**
	 * @return the withoutVot
	 */
	public boolean isWithoutVot() {
		return withoutVot;
	}
	/**
	 * @param withoutVot the withoutVot to set
	 */
	public void setWithoutVot(boolean withoutVot) {
		this.withoutVot = withoutVot;
	}
	/**
	 * @return the mtoPlanMaster
	 */
	public PpPlanMaster getMtoPlanMaster() {
		return mtoPlanMaster;
	}
	/**
	 * @param mtoPlanMaster the mtoPlanMaster to set
	 */
	public void setMtoPlanMaster(PpPlanMaster mtoPlanMaster) {
		this.mtoPlanMaster = mtoPlanMaster;
	}
	/**
	 * @return the mtoPlan
	 */
	public PpPlan getMtoPlan() {
		return mtoPlan;
	}
	/**
	 * @param mtoPlan the mtoPlan to set
	 */
	public void setMtoPlan(PpPlan mtoPlan) {
		this.mtoPlan = mtoPlan;
	}
	/**
	 * @return the planMaster
	 */
	public PpPlanMaster getPlanMaster() {
		return planMaster;
	}
	/**
	 * @param planMaster the planMaster to set
	 */
	public void setPlanMaster(PpPlanMaster planMaster) {
		this.planMaster = planMaster;
	}
	/**
	 * @return the plan
	 */
	public PpPlan getPlan() {
		return plan;
	}
	/**
	 * @param plan the plan to set
	 */
	public void setPlan(PpPlan plan) {
		this.plan = plan;
	}



	
	
	
	
	
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

	

	


}

