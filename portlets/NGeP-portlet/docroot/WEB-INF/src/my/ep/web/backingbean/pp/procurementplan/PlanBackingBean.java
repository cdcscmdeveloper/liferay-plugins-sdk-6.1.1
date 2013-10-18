package my.ep.web.backingbean.pp.procurementplan;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanActor;
import my.ep.persistence.entity.pp.procurementplan.PpPlanItem;
import my.ep.persistence.entity.pp.procurementplan.PpPlanMaster;
import my.ep.persistence.entity.pp.procurementplan.PpQtActivity;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pp.budgetallocation.BudgetAllocationBackingBean;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.pp.procurementplan.PlanManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.pp.common.CommonMethod;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
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
		//TODO: temp
		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
		log.info("PlanBackingBean - End");
	}

	public String redirectToCreatePlan(){
		log.info("redirectToCreatePlan - Start");
		
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_NEW);
		this.getManagedBean().setSelectedPlanMaster(new PpPlanMaster());
		this.getManagedBean().setSelectedPlan(new PpPlan());
		this.getManagedBean().getSelectedPlan().setEditable(true);
		this.getManagedBean().getSelectedPlan().setStatusId(1L);
		this.getManagedBean().getSelectedPlan().setPpQtActivities(new ArrayList<PpQtActivity>());
		this.getManagedBean().getSelectedPlan().setPpRemarks(new ArrayList<PpRemark>());
		
		
		//Plan Approver
		PpPlanActor approver = new PpPlanActor();
		approver.setActorType("A");
		approver.setActorSeq(1);
		this.getManagedBean().getSelectedPlan().setApprover(approver);
		
		//PVO
		this.getManagedBean().getSelectedPlan().setVerificationOfficers(new ArrayList<PpPlanActor>());
		for(int i=1; i<=3; i++){
			PpPlanActor vo = new PpPlanActor();
			vo.setActorSeq(i);
			vo.setActorType("V");
			this.getManagedBean().getSelectedPlan().addVerificationOfficers(vo);
		}
		
		log.info("redirectToCreatePlan - End");
		return ProcurementPlanConst.NAV_TO_PLAN_DET;
    } 	
	
	public void showPpItem(PpPlanItem item){
		log.info("showPpItem - Start");
		if(item==null){
			this.getManagedBean().setSelectedItemRemark(new PpRemark());
			this.getManagedBean().setSelectedItem(new PpPlanItem());
			this.getManagedBean().getSelectedItem().setBrandNew(true);
		}else{
			item.setEditable(false);
			item.setSelected(true);
			this.getManagedBean().setSelectedItem(item);
		}
		log.info("showPpItem - End");
	}
	
	public void updateViewMode(){
		log.info("updateViewMode - Start");
		if(this.getManagedBean().getSelectedPlanMaster().getProcurementMode()!=null && !this.getManagedBean().getSelectedPlanMaster().getProcurementMode().isEmpty()){
			String procurementMode = this.getManagedBean().getSelectedPlanMaster().getProcurementMode();
			if(ProcurementPlanConst.PROCUREMENT_QUOTATION.equals(procurementMode) || ProcurementPlanConst.PROCUREMENT_DIRECT_NEGO.equals(procurementMode)
					|| ProcurementPlanConst.PROCUREMENT_TENDER.equals(procurementMode)){
				this.getManagedBean().getSelectedPlan().setFavouriteOption(ProcurementPlanConst.FAVOURITE_TYPE_NONE);
				this.getManagedBean().getSelectedPlanMaster().setFulfilmentType(null);
				this.getManagedBean().getSelectedPlanMaster().setPlanType(ProcurementPlanConst.TYPE_PROCUREMENT);
			}else if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(procurementMode) 
					|| ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode)){
				//rollback PP data
				this.getManagedBean().getSelectedPlanMaster().setRequiredMto(false);
				this.getManagedBean().getSelectedPlanMaster().setQtType(null);
				this.getManagedBean().getSelectedPlanMaster().setItemOrigin(null);
				
				this.getManagedBean().getSelectedPlan().setFavouriteOption(ProcurementPlanConst.FAVOURITE_TYPE_NONE);
				this.getManagedBean().getSelectedPlanMaster().setFulfilmentType(null);
//				this.getManagedBean().setRecurringFreq(0);
				this.getManagedBean().getSelectedPlanMaster().setPlanType(ProcurementPlanConst.TYPE_EXPENDITURE);
			}
		}else{
			clearViewMode();
		}
		enableDetailTab();
		updateMtoFlag();
		
		log.info("plan type: " +this.getManagedBean().getSelectedPlanMaster().getPlanType());
		log.info("updateViewMode - End");
    }
	
	
	
	public void enableDetailTab(){
		log.info("enableDetailTab - Start");
		this.getManagedBean().setEnableDetail(false);
		String procurementMode = this.getManagedBean().getSelectedPlanMaster().getProcurementMode();
		String fulfillmentType = this.getManagedBean().getSelectedPlanMaster().getFulfilmentType();
		String favoriteOption = this.getManagedBean().getSelectedPlan().getFavouriteOption();
		log.info("procurementMode: " + procurementMode);
		log.info("fulfillmentType: " + fulfillmentType);
		log.info("favoriteOption: " + favoriteOption);
		if(ProcurementPlanConst.PROCUREMENT_QUOTATION.equals(procurementMode) || ProcurementPlanConst.PROCUREMENT_DIRECT_NEGO.equals(procurementMode)
				|| ProcurementPlanConst.PROCUREMENT_TENDER.equals(procurementMode)){
			this.getManagedBean().setEnableDetail(true);
		}else{
			if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(procurementMode) &&
					(fulfillmentType!=null && fulfillmentType!="")){
				this.getManagedBean().setEnableDetail(true);
			}else if(ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode) && 
					(favoriteOption!=null && favoriteOption!="")){
				this.getManagedBean().setEnableDetail(true);
			}	
		}
		log.info("enableDetailTab - End");
	}
	
	
	public void updateMtoFlag(){
		log.info("updateMtoFlag - Start");
		if(ProcurementPlanConst.TYPE_PROCUREMENT.equals(this.getManagedBean().getSelectedPlanMaster().getPlanType())
				&& this.getManagedBean().getSelectedPlanMaster().isRequiredMto()
				&& (ProcurementPlanConst.GOOD_SERVICE_TYPE_IMPORTED.equals(this.getManagedBean().getSelectedPlanMaster().getItemOrigin())
						|| ProcurementPlanConst.GOOD_SERVICE_TYPE_BOTH.equals(this.getManagedBean().getSelectedPlanMaster().getItemOrigin()))){
			this.getManagedBean().setEnableMto(true);
			if(this.getManagedBean().getMtoPlanMaster()==null){
				//Initial MTO plan details
				this.getManagedBean().setMtoPlanMaster(new PpPlanMaster());
				this.getManagedBean().setMtoPlan(new PpPlan());
				//Plan Approver
				PpPlanActor approver = new PpPlanActor();
				approver.setActorType("A");
				approver.setActorSeq(1);
				this.getManagedBean().getMtoPlan().setApprover(approver);
				//PVO
				this.getManagedBean().getMtoPlan().setVerificationOfficers(new ArrayList<PpPlanActor>());
				for(int i=1; i<=3; i++){
					PpPlanActor vo = new PpPlanActor();
					vo.setActorSeq(i);
					vo.setActorType("V");
					this.getManagedBean().getMtoPlan().addVerificationOfficers(vo);
				}
				
			}
			
		}else{
			this.getManagedBean().setEnableMto(false);
		}
		log.info("updateMtoFlag - End");
	}
	

	
	public void updateCurrentView(TabChangeEvent val){
		log.info("updateCurrentView - Start");
		log.info("tab: " + val.getTab().getId());
		
		
		if(ProcurementPlanConst.CURRENT_NEW.equals(val.getTab().getId())){
			log.info("...tabN");
			this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_NEW);
			this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
			
//			//Hide MTO plan details 
//			this.getManagedBean().setMtoPlanMaster(this.getManagedBean().getSelectedPlanMaster());
//			this.getManagedBean().setMtoPlan(this.getManagedBean().getSelectedPlan());
//			
//			//Restore plan details to view
//			this.getManagedBean().setSelectedPlanMaster(this.getManagedBean().getPlanMaster());
//			this.getManagedBean().setSelectedPlan(this.getManagedBean().getPlan());
			
		}else if(ProcurementPlanConst.CURRENT_MTO.equals(val.getTab().getId())){
			log.info("...tabM");
			this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_MTO);
			this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
			
			this.getManagedBean().getMtoPlanMaster().setProcurementMode(this.getManagedBean().getSelectedPlanMaster().getProcurementMode());
			this.getManagedBean().getMtoPlanMaster().setQtType(this.getManagedBean().getSelectedPlanMaster().getQtType());
			this.getManagedBean().getMtoPlanMaster().setItemOrigin(ProcurementPlanConst.GOOD_SERVICE_TYPE_IMPORTED);
			this.getManagedBean().getMtoPlanMaster().setRequiredMto(true);
			
//			//Hide plan details
//			this.getManagedBean().setPlanMaster(this.getManagedBean().getSelectedPlanMaster());
//			this.getManagedBean().setPlan(this.getManagedBean().getSelectedPlan());
//			
//			//Restore MTO plan details to view
//			this.getManagedBean().setSelectedPlanMaster(this.getManagedBean().getMtoPlanMaster());
//			this.getManagedBean().setSelectedPlan(this.getManagedBean().getMtoPlan());
//			this.getManagedBean().getSelectedPlanMaster().setDepartmentId(22222L);
//		
		}
		this.getManagedBean().getMtoPlanMaster().setPlanType(ProcurementPlanConst.TYPE_PROCUREMENT);
		log.info("updateCurrentView - End");
	}
	
	
	public void addItemToList(){
		log.info("addItemToList - Start");
		//Initial item list if empty
		if (this.getManagedBean().getSelectedPlan().getPpPlanItems()== null){
			this.getManagedBean().getSelectedPlan().setPpPlanItems(new ArrayList<PpPlanItem>());
		}

		if(this.getManagedBean().getSelectedItem().isBrandNew() && !this.getManagedBean().getSelectedItem().isSelected()){
			this.getManagedBean().getSelectedPlan().addPpItems(this.getManagedBean().getSelectedItem());
		}else{		
			//TODO
		}
		log.info("addItemToList - End");
	}
	
	
	public void addPlanRemark() {
		log.info("addPlanRemark - Start");
		PpRemark remark = new PpRemark();
		if(this.getManagedBean().getSelectedPlan().getPpRemarks()==null){
			this.getManagedBean().getSelectedPlan().setPpRemarks(new ArrayList<PpRemark>());
		}
		this.getManagedBean().getSelectedPlan().getPpRemarks().add(remark); //TODO add pm status parent
		System.out.println(">>>>>>>> " + this.getManagedBean().getSelectedPlan().getPpRemarks().size());
		log.info("addPlanRemark - End");
	}
	
	public String generateHeader(String viewMode){
		return CommonMethod.generateHeader(viewMode, ProcurementPlanConst.PROG_PLAN);
	}
	
	
	
	
	
//	public String redirectToCreateMtoPlan(){
//		log.info("redirectToCreateMtoPlan - Start");
//		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
//		this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_MTO);
//		this.getManagedBean().setSelectedPlanMaster(new PpPlanMaster());
//		this.getManagedBean().setSelectedPlan(new PpPlan());
//		this.getManagedBean().getSelectedPlan().setEditable(true);
//		this.getManagedBean().getSelectedPlan().setStatusCode("New");
//		
////		//Plan Approver
////		PpPlanActor approver = new PpPlanActor();
////		approver.setActorType("A");
////		approver.setSeqNo(1);
////		this.getManagedBean().setSelectedPlanApprover(approver);
//		
//		
////		
////		this.getManagedBean().setPlanType(ProcurementPlanConst.TYPE_PROCUREMENT);
////		this.getManagedBean().setProcurementMode(ProcurementPlanConst.PROCUREMENT_TENDER);//as per main plan
////		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW_MTO);
////		this.getManagedBean().setBrandNew(true);
////		this.getManagedBean().setTypeOfGoodsServices(ProcurementPlanConst.GOOD_SERVICE_TYPE_IMPORTED);
////		this.getManagedBean().setMtoRequired(1);
//		log.info("redirectToCreateMtoPlan - End");
//		return ProcurementPlanConst.NAV_TO_PLAN_DET;
//    }
	
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
	
	
	
	
	
	
	

	
	
//	public void updateOtherTabs(TabChangeEvent val){
//		if(ProcurementPlanConst.CURRENT_NEW.equals(val.getTab().getId())){
//			this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_NEW);
//			this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
//		}else if(ProcurementPlanConst.CURRENT_MTO.equals(val.getTab().getId())){
//			this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_MTO);
//			this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
//		}
//		
//		System.out.println("viewMode, Current >>>>>>>>>>>>>> " + 	
//		this.getManagedBean().getCurrent() + ", " + 	this.getManagedBean().getViewMode());
//	}
	
	
	
	/**
	 * DROP DOWN LIST
	 */
	public List<String> pmTaskActivities() {
		
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
		sourcingAct.add("Shortlisting of supplier for online bidding invitation");
		sourcingAct.add("Online bidding");
		sourcingAct.add("LOI");
		
		for(String sa: sourcingAct){
			PpQtActivity activity = new PpQtActivity();
//			activity.setActivityId(activityId);
			this.getManagedBean().getSelectedPlan().addPpQtActivities(activity);
		}
		return sourcingAct;
	}
	
	public List<SelectItem> procurementModeOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.PROCUREMENT_QUOTATION, "Quotation"));
		options.add(new SelectItem(ProcurementPlanConst.PROCUREMENT_DIRECT_NEGO, "Direct Negotiation"));
		options.add(new SelectItem(ProcurementPlanConst.PROCUREMENT_TENDER, "Tender"));
		options.add(new SelectItem(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT, "Existing Contract"));
		options.add(new SelectItem(ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE, "Direct Purchase"));
		return options;
	}
	
	public List<SelectItem> fulfillmentTypeOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.FULFILLMENT_ONE_OFF, "One-Off"));
		options.add(new SelectItem(ProcurementPlanConst.FULFILLMENT_AS_AND_WHEN, "Periodic - As and When"));
		options.add(new SelectItem(ProcurementPlanConst.FULFILLMENT_SCHEDULE, "Periodic - Schedule"));
		return options;
	}
	
	public List<SelectItem> favouriteOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.FAVOURITE_TYPE_ITEM, "Item"));
		options.add(new SelectItem(ProcurementPlanConst.FAVOURITE_TYPE_PLAN, "Plan"));
		options.add(new SelectItem(ProcurementPlanConst.FAVOURITE_TYPE_NONE, "None"));
		return options;
	}

	public List<SelectItem> qtTypeOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.QT_TYPE_BUMIPUTERA, "Bumiputera"));
		options.add(new SelectItem(ProcurementPlanConst.QT_TYPE_OPEN, "Open"));
		options.add(new SelectItem(ProcurementPlanConst.QT_TYPE_PANEL, "Panel"));
		options.add(new SelectItem(ProcurementPlanConst.QT_TYPE_RESTRICTED, "Restricted"));
		return options;
	}
	
	public List<SelectItem> goodsTypeOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.GOOD_SERVICE_TYPE_IMPORTED, "Imported"));
		options.add(new SelectItem(ProcurementPlanConst.GOOD_SERVICE_TYPE_LOCAL, "Local"));
		options.add(new SelectItem(ProcurementPlanConst.GOOD_SERVICE_TYPE_BOTH, "Imported & Local"));
		return options;
	}
	
	public List<SelectItem> goOptions() {
		//retrieve GO list based on ptj
		this.getManagedBean().getSelectedPlanMaster().getOrgProfile().getOrgProfileId(); //TODO check this 
		
		
		List<SelectItem>options = new ArrayList<SelectItem>();
		return options;
	}
	
	public List<SelectItem> soOptions() {
		//retrieve SO list based on selected GO
		this.getManagedBean().getSelectedItem().getGoId();
		this.getManagedBean().getSelectedItem().getSoId();
		
		
		List<SelectItem>options = new ArrayList<SelectItem>();
		return options;
	}
	
	public List<SelectItem> doOptions() {
		//retrieve DO list based on selected GO, SO
		this.getManagedBean().getSelectedItem().getGoId();
		this.getManagedBean().getSelectedItem().getSoId();
		
		//retrieve DO list based on selected GO, SO
		List<SelectItem>options = new ArrayList<SelectItem>();
		return options;
	}
	
	public List<SelectItem> productOptions() {
		//retrieve DO list based on selected GO, SO
		this.getManagedBean().getSelectedItem().getGoId();
		this.getManagedBean().getSelectedItem().getSoId();
		
		//retrieve DO list based on selected GO, SO
		List<SelectItem>options = new ArrayList<SelectItem>();
		return options;
	}

}
