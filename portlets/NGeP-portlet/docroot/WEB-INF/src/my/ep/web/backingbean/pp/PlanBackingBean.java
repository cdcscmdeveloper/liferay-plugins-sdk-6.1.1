package my.ep.web.backingbean.pp;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.type.pp.ProcurementMode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pp.PlanManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;

@ManagedBean(name="planBackingBean")
@RequestScoped
public class PlanBackingBean extends BaseBackingBean<PlanManagedBean>{
	private static final String NAV_TO_VIEW_PLAN = "viewPlan";
	private static final String NAV_TO_CREATE_PLAN = "createPlan";
	public static final String NEW = "N";
	public static final String REVISION = "S";
	public static final String VERIFICATION = "V";
	public static final String APPROVAL = "A";
	public static final String PROCUREMENT = "P";
	public static final String EXPENDITURE = "E";
	public static final String MAINTAIN = "M";
	public static final String VIEW = "R";
	public static final String LIST = "L";

	public PlanBackingBean() {
		System.out.println("---------->>>>>>>>>>>>>>>>>>>>>>>>>>>");
		

	}

	
	public String getNavToViewPlan(){
		return NAV_TO_VIEW_PLAN;
	}
	public String getNavToCreatePlan(){
		return NAV_TO_CREATE_PLAN;
	}
	
	public void searchPlan(boolean popup) {
		//Retrieve related plans only
		this.getManagedBean().setPopup(popup);
		if(this.getManagedBean().getPlanList()==null){
			this.getManagedBean().setPlanList(new ArrayList());
		}
		this.getManagedBean().getPlanList().add(new Object());
	}
	
	public void viewBudget(boolean popup) {
		//Retrieve allocated budget only
		BudgetAllocationBackingBean budgetAllocationbb = new BudgetAllocationBackingBean();
		budgetAllocationbb.getManagedBean().setPopup(popup);
	}
	
	public String redirectToCreatePlan(){
		this.getManagedBean().setBrandNew(true);
		//populateAutFuncAreaDetails(new AutFuncArea(), null);
		this.getManagedBean().setViewMode(NEW);
		return NAV_TO_CREATE_PLAN+"?faces-redirect=true";
    } 
	
	public String redirectToListBudgetAllocation(){
		System.out.println("----------NAV_TO_VIEW_BUDGET_ALLOCATION");
		SubMenuBackingBean subMenuBackingBean = new SubMenuBackingBean();
		return subMenuBackingBean.getNavToListBudgetAllocation();
    }
	
	public String redirectToListPlan(){
		this.getManagedBean().cleanAll();
		SubMenuBackingBean subMenuBackingBean = new SubMenuBackingBean();
		return subMenuBackingBean.getNavToListPlan();
    }

	public void updateViewMode(){
		if(this.getManagedBean().getProcurementMode()!=null && !this.getManagedBean().getProcurementMode().isEmpty()){
			String procurementMode = this.getManagedBean().getProcurementMode();
			if(ProcurementMode.QUOTATION.value().equals(procurementMode) || ProcurementMode.SELECTIVETEMDER.value().equals(procurementMode)
					|| ProcurementMode.TENDER.value().equals(procurementMode)){
				this.getManagedBean().setFavouriteOption(null);
				this.getManagedBean().setFulfillmentType(null);
				this.getManagedBean().setPeriodicType(null);
				this.getManagedBean().setPlanType(PROCUREMENT);
			}else if(ProcurementMode.EXISTINTCONTRACT.value().equals(procurementMode) || ProcurementMode.DIRECTPURCHASE.value().equals(procurementMode)){
				this.getManagedBean().setFavouriteOption(null);
				this.getManagedBean().setFulfillmentType(null);
				this.getManagedBean().setPeriodicType(null);
				this.getManagedBean().setMtoRequired(0);
				this.getManagedBean().setQuotationTenderType(null);
				this.getManagedBean().setTypeOfGoodsServices(null);
				this.getManagedBean().setRecurringFreq(0);
				this.getManagedBean().setPlanType(EXPENDITURE);
			}else{
				clearViewMode();
			}
		}else{
			clearViewMode();
			
		}
    }
	
	private void clearViewMode(){
		this.getManagedBean().setFavouriteOption(null);
		this.getManagedBean().setFulfillmentType(null);
		this.getManagedBean().setPeriodicType(null);
		this.getManagedBean().setMtoRequired(0);
		this.getManagedBean().setQuotationTenderType(null);
		this.getManagedBean().setTypeOfGoodsServices(null);
		this.getManagedBean().setRecurringFreq(0);
		this.getManagedBean().setViewMode(NEW);
	}
	
	public void showMtoPlanDetails(){
		this.getManagedBean().setMtoRequired(1);
		System.out.println("xxxxxxxxxx mto " + this.getManagedBean().getMtoRequired() + ", " + this.getManagedBean().getViewMode());
		System.out.println(this.getManagedBean().getVerification());
		System.out.println(this.getManagedBean().getProcurementMode());
	}
	public String submitNewPlan(){
		System.out.println(this.getManagedBean().getVerification());
		System.out.println(this.getManagedBean().getProcurementMode());
		return redirectToListPlan();
	}
	
	public String redirectToViewPlan(){
		this.getManagedBean().setViewMode(MAINTAIN);
		this.getManagedBean().setBrandNew(false);
		return NAV_TO_VIEW_PLAN;
    } 
	
	public String generateHeader(String viewMode){
		String pageTitle = "";
		
		if(NEW.equals(viewMode)){
			pageTitle = "lbl.pp.plan.title.new";
		}else if(LIST.equals(viewMode)){
			pageTitle = "lbl.pp.plan.title.list";
		}else if(APPROVAL.equals(viewMode)){
			pageTitle = "lbl.pp.plan.title.approval";
		}else if(VERIFICATION.equals(viewMode)){
			pageTitle = "lbl.pp.plan.title.verification";
		}else if(REVISION.equals(viewMode)){
			pageTitle = "lbl.pp.plan.title.revision";
		}else if(MAINTAIN.equals(viewMode)){
			pageTitle = "lbl.pp.plan.title.maintain";
		}
		return getMessageCommon(pageTitle);
	}

	public String getMessageCommon(String name){
		return ResourceBundleUtils.getMessage(name, ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
	}
	
	public List<String> sourcingAct() {
		List <String> sourcingAct = new ArrayList<String>();
		sourcingAct.add("Create Sourcing");
		sourcingAct.add("Appoint Committee");
		sourcingAct.add("Committee Approval");
		sourcingAct.add("Prepare Specification");
		sourcingAct.add("Specification Approval by Ministry");
		sourcingAct.add("Approval for Publication");
		sourcingAct.add("Notice Publish");
		sourcingAct.add("Closing");
		sourcingAct.add("Opening");
		sourcingAct.add("Proposal Preparation");
		sourcingAct.add("Proposal Evaluation");
		sourcingAct.add("LOA");
		sourcingAct.add("Budget Approval");
		sourcingAct.add("MoF Approval");
		sourcingAct.add("MITI Approval");
		sourcingAct.add("ICT Approval");
		sourcingAct.add("Briefing");
		sourcingAct.add("Site Visit");
		sourcingAct.add("Shortlisting of supplier for online bidding invitation.");
		sourcingAct.add("Online bidding");
		sourcingAct.add("LOI");
		return sourcingAct;
	}
	
	public List<SelectItem> procurementModeOptions() {
		List<SelectItem>procurementModeOptions = new ArrayList<SelectItem>();
		procurementModeOptions.add(new SelectItem(ProcurementMode.QUOTATION.value(), "Quotation"));
		procurementModeOptions.add(new SelectItem(ProcurementMode.SELECTIVETEMDER.value(), "Direct Negotiation"));
		procurementModeOptions.add(new SelectItem(ProcurementMode.TENDER.value(), "Tender"));
		procurementModeOptions.add(new SelectItem(ProcurementMode.EXISTINTCONTRACT.value(), "Existing Contract"));
		procurementModeOptions.add(new SelectItem(ProcurementMode.DIRECTPURCHASE.value(), "Direct Purchase"));
		return procurementModeOptions;
	}

	public void addPpItemDet() {
		System.out.println("------------addPpItemDet");
		if(this.getManagedBean().getPpItemList()==null){
			this.getManagedBean().setPpItemList(new ArrayList());
		}
		this.getManagedBean().getPpItemList().add(new Object());
	}
	
	public void addRemark() {
		System.out.println("------------addRemark");
		if(this.getManagedBean().getRemarkList()==null){
			this.getManagedBean().setRemarkList(new ArrayList());
		}
		this.getManagedBean().getRemarkList().add(new Object());
	}
	
}
