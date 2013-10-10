package my.ep.web.backingbean.pp.procurementplan;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanActor;
import my.ep.persistence.entity.pp.procurementplan.PpPlanMaster;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pp.budgetallocation.BudgetAllocationBackingBean;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.pp.procurementplan.PlanManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;

import org.apache.log4j.Logger;
import org.primefaces.event.TabChangeEvent;

@ManagedBean(name="planBackingBean")
@RequestScoped
public class PlanBackingBean extends BaseBackingBean<PlanManagedBean>{
	private static Logger log = Logger.getLogger(BudgetAllocationBackingBean.class);
	
	private PortletRequest getPortletRequest() {
		return (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}
	
	public PlanBackingBean() {
		log.info("PlanBackingBean - Start");
		log.info("PlanBackingBean - End");
	}

	public String redirectToCreatePlan(){
		log.info("redirectToCreatePlan - Start");
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_NEW);
		this.getManagedBean().setSelectedPlanMaster(new PpPlanMaster());
		this.getManagedBean().setSelectedPlan(new PpPlan());
		this.getManagedBean().getSelectedPlan().setEditable(true);
		this.getManagedBean().getSelectedPlan().setStatusCode("New");
		
		//Plan Approver
		PpPlanActor approver = new PpPlanActor();
		approver.setActorType("A");
		approver.setSeqNo(1);
		this.getManagedBean().getSelectedPlan().setApprover(approver);
		
		//PVO
		this.getManagedBean().getSelectedPlan().setVerificationOfficers(new ArrayList<PpPlanActor>());
		for(int i=1; i<=3; i++){
			PpPlanActor vo = new PpPlanActor();
			vo.setSeqNo(i);
			vo.setActorType("V");
			this.getManagedBean().getSelectedPlan().addVerificationOfficers(vo);
		}
		
		log.info("redirectToCreatePlan - End");
		return ProcurementPlanConst.NAV_TO_PLAN_DET;
    } 	
	
	public String redirectToCreateMtoPlan(){
		log.info("redirectToCreateMtoPlan - Start");
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_MTO);
		this.getManagedBean().setSelectedPlanMaster(new PpPlanMaster());
		this.getManagedBean().setSelectedPlan(new PpPlan());
		this.getManagedBean().getSelectedPlan().setEditable(true);
		this.getManagedBean().getSelectedPlan().setStatusCode("New");
		
//		//Plan Approver
//		PpPlanActor approver = new PpPlanActor();
//		approver.setActorType("A");
//		approver.setSeqNo(1);
//		this.getManagedBean().setSelectedPlanApprover(approver);
		
		
//		
//		this.getManagedBean().setPlanType(ProcurementPlanConst.TYPE_PROCUREMENT);
//		this.getManagedBean().setProcurementMode(ProcurementPlanConst.PROCUREMENT_TENDER);//as per main plan
//		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW_MTO);
//		this.getManagedBean().setBrandNew(true);
//		this.getManagedBean().setTypeOfGoodsServices(ProcurementPlanConst.GOOD_SERVICE_TYPE_IMPORTED);
//		this.getManagedBean().setMtoRequired(1);
		log.info("redirectToCreateMtoPlan - End");
		return ProcurementPlanConst.NAV_TO_PLAN_DET;
    }
	
	public String redirectToListPlan(){
		log.info("getNavToViewPlan");
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_LIST);
		return ProcurementPlanConst.NAV_TO_LIST_PLAN;
	}

	public String redirectToViewPlan(){
		log.info("getNavToViewPlan");
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_MAINTAIN);
		return ProcurementPlanConst.NAV_TO_PLAN_DET;
    } 
	
	public void searchPlan(boolean popup) {
		//Retrieve related plans only
		this.getManagedBean().setSearchPlan(true);
		if(this.getManagedBean().getPlanList()==null){
			this.getManagedBean().setPlanList(new <PpPlanMaster> ArrayList());
		}
		this.getManagedBean().getPlanList().add(new PpPlanMaster());
	}
	
	public void viewBudget(boolean popup) {
		//Retrieve allocated budget only
		BudgetAllocationBackingBean budgetAllocationbb = new BudgetAllocationBackingBean();
		
	}

	public void updateMtoFlag(){
		log.info("updateMtoFlag - Start");
		if(ProcurementPlanConst.TYPE_PROCUREMENT.equals(this.getManagedBean().getSelectedPlanMaster().getPlanType())
				&& this.getManagedBean().getSelectedPlanMaster().isRequiredMto()
				&& (ProcurementPlanConst.GOOD_SERVICE_TYPE_IMPORTED.equals(this.getManagedBean().getSelectedPlanMaster().getTypeOfGoods())
						|| ProcurementPlanConst.GOOD_SERVICE_TYPE_BOTH.equals(this.getManagedBean().getSelectedPlanMaster().getTypeOfGoods()))){
			this.getManagedBean().setEnableMto(true);
		}else{
			this.getManagedBean().setEnableMto(false);
		}
		log.info("updateMtoFlag - End");
	}
	
	public void updateViewMode(){
		log.info("updateViewMode - Start");
		this.getManagedBean().setEnableDetail(false);
		
		if(this.getManagedBean().getSelectedPlanMaster().getProcurementMode()!=null && !this.getManagedBean().getSelectedPlanMaster().getProcurementMode().isEmpty()){
			log.info("1");
			String procurementMode = this.getManagedBean().getSelectedPlanMaster().getProcurementMode();
			if(ProcurementPlanConst.PROCUREMENT_QUOTATION.equals(procurementMode) || ProcurementPlanConst.PROCUREMENT_DIRECT_NEGO.equals(procurementMode)
					|| ProcurementPlanConst.PROCUREMENT_TENDER.equals(procurementMode)){
				log.info("P");
				this.getManagedBean().getSelectedPlanMaster().setFavouriteOption(ProcurementPlanConst.FAVOURITE_TYPE_NONE);
				this.getManagedBean().getSelectedPlanMaster().setFulfillmentType(null);
				this.getManagedBean().getSelectedPlanMaster().setPlanType(ProcurementPlanConst.TYPE_PROCUREMENT);
				this.getManagedBean().setEnableDetail(true);
			}else if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(procurementMode) 
					|| ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode)){
				log.info("E");
				//rollback PP data
				this.getManagedBean().getSelectedPlanMaster().setRequiredMto(false);
				this.getManagedBean().getSelectedPlanMaster().setQtType(null);
				this.getManagedBean().getSelectedPlanMaster().setTypeOfGoods(null);
				
				
				this.getManagedBean().getSelectedPlanMaster().setFavouriteOption(ProcurementPlanConst.FAVOURITE_TYPE_NONE);
				this.getManagedBean().getSelectedPlanMaster().setFulfillmentType(null);
//				this.getManagedBean().setRecurringFreq(0);
				this.getManagedBean().getSelectedPlanMaster().setPlanType(ProcurementPlanConst.TYPE_EXPENDITURE);
			}
		}else{
			log.info("3");
			clearViewMode();
		}
		updateMtoFlag();
		
		log.info("plan type: " +this.getManagedBean().getSelectedPlanMaster().getPlanType());
		log.info("updateViewMode - End");
    }
	
	private void clearViewMode(){
		log.info("clearViewMode - Start");
//		this.getManagedBean().setFavouriteOption(ProcurementPlanConst.FAVOURITE_TYPE_NONE);
//		this.getManagedBean().setFulfillmentType(null);
//		this.getManagedBean().setPeriodicType(null);
//		this.getManagedBean().setMtoRequired(0);
//		this.getManagedBean().setQuotationTenderType(null);
//		this.getManagedBean().setTypeOfGoodsServices(null);
//		this.getManagedBean().setRecurringFreq(0);
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		log.info("clearViewMode - End");
		
	}
	
	public void showMtoPlanDetails(){
		this.getManagedBean().getSelectedPlanMaster().setRequiredMto(true);
		System.out.println("xxxxxxxxxx mto " + this.getManagedBean().getSelectedPlanMaster().isRequiredMto() + ", " + this.getManagedBean().getViewMode());
		System.out.println(this.getManagedBean().getVerification());
		System.out.println(this.getManagedBean().getProcurementMode());
	}
//	public String submitNewPlan(){
//		System.out.println(this.getManagedBean().getVerification());
//		System.out.println(this.getManagedBean().getProcurementMode());
//		return redirectToListPlan();
//	}
	
	
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
	

	public void updateCurrentView(TabChangeEvent val){
		
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
	
	public void updateOtherTabs(TabChangeEvent val){
		
		if(ProcurementPlanConst.CURRENT_NEW.equals(val.getTab().getId())){
			this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_NEW);
			this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		}else if(ProcurementPlanConst.CURRENT_MTO.equals(val.getTab().getId())){
			this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_MTO);
			this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		}
		
		System.out.println("viewMode, Current >>>>>>>>>>>>>> " + 	
		this.getManagedBean().getCurrent() + ", " + 	this.getManagedBean().getViewMode());
	}
	

}
