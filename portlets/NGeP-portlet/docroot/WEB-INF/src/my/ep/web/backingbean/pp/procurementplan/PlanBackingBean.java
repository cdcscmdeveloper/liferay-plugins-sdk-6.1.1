package my.ep.web.backingbean.pp.procurementplan;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pp.budgetallocation.BudgetAllocationBackingBean;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.pp.procurementplan.PlanManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;

import org.primefaces.event.TabChangeEvent;

@ManagedBean(name="planBackingBean")
@RequestScoped
public class PlanBackingBean extends BaseBackingBean<PlanManagedBean>{

	public PlanBackingBean() {
		System.out.println("---------->>>>>>>>>>>>>>>>>>>>>>>>>>>");
		

	}

	
	public String getNavToViewPlan(){
		return ProcurementPlanConst.NAV_TO_VIEW_PLAN;
	}
	public String getNavToCreatePlan(){
		return ProcurementPlanConst.NAV_TO_CREATE_PLAN;
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
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		this.getManagedBean().setCurrent(ProcurementPlanConst.MODE_NEW);
		return ProcurementPlanConst.NAV_TO_CREATE_PLAN;
    } 
	
//	public String redirectToListBudgetAllocation(){
//		System.out.println("----------NAV_TO_VIEW_BUDGET_ALLOCATION");
//		SubMenuBackingBean subMenuBackingBean = new SubMenuBackingBean();
//		return subMenuBackingBean.getNavToListBudgetAllocation();
//    }
//	
//	public String redirectToListPlan(){
//		this.getManagedBean().cleanAll();
//		SubMenuBackingBean subMenuBackingBean = new SubMenuBackingBean();
//		return subMenuBackingBean.getNavToListPlan();
//    }

	public void updateViewMode(){
		if(this.getManagedBean().getProcurementMode()!=null && !this.getManagedBean().getProcurementMode().isEmpty()){
			String procurementMode = this.getManagedBean().getProcurementMode();
			if(ProcurementPlanConst.PROCURMENET_QUOTATION.equals(procurementMode) || ProcurementPlanConst.PROCURMENET_DIRECT_NEGO.equals(procurementMode)
					|| ProcurementPlanConst.PROCURMENET_TENDER.equals(procurementMode)){
				this.getManagedBean().setFavouriteOption(ProcurementPlanConst.FAVOURITE_TYPE_NONE);
				this.getManagedBean().setFulfillmentType(null);
				this.getManagedBean().setPeriodicType(null);
				this.getManagedBean().setPlanType(ProcurementPlanConst.TYPE_PROCUREMENT);
			}else if(ProcurementPlanConst.PROCURMENET_EXISTING_CONTRACT.equals(procurementMode) 
					|| ProcurementPlanConst.PROCURMENET_DIRECT_PURCHASE.equals(procurementMode)){
				this.getManagedBean().setFavouriteOption(ProcurementPlanConst.FAVOURITE_TYPE_NONE);
				this.getManagedBean().setFulfillmentType(null);
				this.getManagedBean().setPeriodicType(null);
				this.getManagedBean().setMtoRequired(0);
				this.getManagedBean().setQuotationTenderType(null);
				this.getManagedBean().setTypeOfGoodsServices(null);
				this.getManagedBean().setRecurringFreq(0);
				this.getManagedBean().setPlanType(ProcurementPlanConst.TYPE_EXPENDITURE);
			}else{
				clearViewMode();
			}
		}else{
			clearViewMode();
			
		}
    }
	
	private void clearViewMode(){
		this.getManagedBean().setFavouriteOption(ProcurementPlanConst.FAVOURITE_TYPE_NONE);
		this.getManagedBean().setFulfillmentType(null);
		this.getManagedBean().setPeriodicType(null);
		this.getManagedBean().setMtoRequired(0);
		this.getManagedBean().setQuotationTenderType(null);
		this.getManagedBean().setTypeOfGoodsServices(null);
		this.getManagedBean().setRecurringFreq(0);
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
	}
	
	public void showMtoPlanDetails(){
		this.getManagedBean().setMtoRequired(1);
		System.out.println("xxxxxxxxxx mto " + this.getManagedBean().getMtoRequired() + ", " + this.getManagedBean().getViewMode());
		System.out.println(this.getManagedBean().getVerification());
		System.out.println(this.getManagedBean().getProcurementMode());
	}
//	public String submitNewPlan(){
//		System.out.println(this.getManagedBean().getVerification());
//		System.out.println(this.getManagedBean().getProcurementMode());
//		return redirectToListPlan();
//	}
	
	public String redirectToViewPlan(){
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_MAINTAIN);
		this.getManagedBean().setBrandNew(false);
		return ProcurementPlanConst.NAV_TO_VIEW_PLAN;
    } 
	
	public String generateHeader(String viewMode){
		String pageTitle = "";
		
		if(ProcurementPlanConst.MODE_NEW.equals(viewMode)){
			pageTitle = "lbl.pp.plan.title.new";
		}else if(ProcurementPlanConst.MODE_LIST.equals(viewMode)){
			pageTitle = "lbl.pp.plan.title.list";
		}else if(ProcurementPlanConst.MODE_APPROVAL.equals(viewMode)){
			pageTitle = "lbl.pp.plan.title.approval";
		}else if(ProcurementPlanConst.MODE_VERIFICATION.equals(viewMode)){
			pageTitle = "lbl.pp.plan.title.verification";
		}else if(ProcurementPlanConst.MODE_REVISION.equals(viewMode)){
			pageTitle = "lbl.pp.plan.title.revision";
		}else if(ProcurementPlanConst.MODE_MAINTAIN.equals(viewMode)){
			pageTitle = "lbl.pp.plan.title.maintain";
		}else if(ProcurementPlanConst.MODE_NEW_MTO.equals(viewMode)){
			pageTitle = "lbl.pp.plan.title.mto.plan";
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
		procurementModeOptions.add(new SelectItem("QT", "Quotation"));
		procurementModeOptions.add(new SelectItem("DN", "Direct Negotiation"));
		procurementModeOptions.add(new SelectItem("TD", "Tender"));
		procurementModeOptions.add(new SelectItem("EC", "Existing Contract"));
		procurementModeOptions.add(new SelectItem("DP", "Direct Purchase"));
		return procurementModeOptions;
	}
	
	public void addRemark() {
		System.out.println("------------addRemark");
		if(this.getManagedBean().getRemarkList()==null){
			this.getManagedBean().setRemarkList(new ArrayList());
		}
		this.getManagedBean().getRemarkList().add(new Object());
	}
	
	public String redirectToCreateMtoPlan(){
		System.out.println("----------NAV_TO_CREATE_MTO_PLAN");
		this.getManagedBean().cleanAll();
		this.getManagedBean().setPlanType(ProcurementPlanConst.TYPE_PROCUREMENT);
		this.getManagedBean().setProcurementMode(ProcurementPlanConst.PROCURMENET_TENDER);//as per main plan
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW_MTO);
		this.getManagedBean().setBrandNew(true);
		this.getManagedBean().setTypeOfGoodsServices(ProcurementPlanConst.GOOD_SERVICE_TYPE_IMPORTED);
		this.getManagedBean().setMtoRequired(1);
		return ProcurementPlanConst.NAV_TO_CREATE_MTO_PLAN;
    }
	public void updateCurrent(TabChangeEvent val){
		
		if("tabN".equals(val.getTab().getId())){
			this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_NEW);
			this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		}else if("tabT".equals(val.getTab().getId())){
			this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_MTO);
			this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		}
		
		System.out.println("viewMode, Current >>>>>>>>>>>>>> " + 	
		this.getManagedBean().getCurrent() + ", " + 	this.getManagedBean().getViewMode());
	}
}
