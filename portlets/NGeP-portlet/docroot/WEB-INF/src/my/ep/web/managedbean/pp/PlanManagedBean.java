package my.ep.web.managedbean.pp;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;


import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "planManagedBean")
@SessionScoped
public class PlanManagedBean extends BaseManagedBean{


private static final long serialVersionUID = 1L;

	private String pageTitle;
	
	private String current;
	
	private boolean brandNew;
	private String pageInd;
	private String procurementMode;
	private String viewMode;
	private boolean popup = false;
	private String planType;

	//temp
	private boolean approve;
	//temp
	private List planList = null;
	//temp
	private String fulfillmentType;
	//temp
	private String periodicType;
	//temp
	private String documentType;
	//temp
	private Boolean verification  = false;
	//temp
	private Integer multipleLevel= 0;
	//temp
	private String favouriteOption = "N";
	//temp
	private String quotationTenderType;
	//temp
	private String typeOfGoodsServices;
	//temp
	private Integer mtoRequired = 0;
	//temp
	private String contractType;
	//temp
	private Integer recurringFreq;
	//temp
	private String maksudCode;
	
	private List ppItemList = null;
	//temp
	private List remarkList = null;
	
	
	public boolean isApprove() {
		return approve;
	}
	public void setApprove(boolean approve) {
		this.approve = approve;
	}


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
	public String getViewMode() {
		return viewMode;
	}
	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
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
	public List getPlanList() {
		return planList;
	}
	public void setPlanList(List planList) {
		this.planList = planList;
	}
	
	public String getPageInd() {
		return pageInd;
	}
	public void setPageInd(String pageInd) {
		this.pageInd = pageInd;
	}
	
	public void cleanAll(){
		this.setPageInd(null);
		this.setPopup(false);
		this.setPlanList(null);
		this.setDocumentType(null);
		this.setFavouriteOption("N");
		this.setFulfillmentType(null);
		this.setMultipleLevel(0);
		this.setPeriodicType(null);
		this.setProcurementMode(null);
		this.setQuotationTenderType(null);
		this.setVerification(false);
		this.setViewMode(null);
		this.setTypeOfGoodsServices(null);
		this.setMtoRequired(0);
		this.setRecurringFreq(0);
		this.setContractType(null);
		this.setBrandNew(false);
		this.setCurrent(null);
	}
	public Integer getMultipleLevel() {
		return multipleLevel;
	}
	public void setMultipleLevel(Integer multipleLevel) {
		this.multipleLevel = multipleLevel;
	}
	public Integer getMtoRequired() {
		return mtoRequired;
	}
	public void setMtoRequired(Integer mtoRequired) {
		this.mtoRequired = mtoRequired;
	}
	public String getPageTitle() {
		return pageTitle;
	}
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	

	public String getCurrent() {
		return current;
	}
	public void setCurrent(String current) {
		this.current = current;
	}
}

