/*
 * Copyright © 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pp.procurementplan;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.service.pm.authorization.orgunit.IOrgUnitSO;
import my.ep.biz.service.pm.common.taskactivity.ITaskActivitySO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitReqDTO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitResDTO;
import my.ep.dto.pm.common.taskactivity.TaskReqDTO;
import my.ep.dto.pm.common.taskactivity.TaskResDTO;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmBusinessActivity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
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
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.CommonMethod;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.event.TabChangeEvent;

@ManagedBean(name="planBackingBean")
@RequestScoped
public class PlanBackingBean extends BaseBackingBean<PlanManagedBean>{
	private static Logger log = Logger.getLogger(BudgetAllocationBackingBean.class);
	private final static Long USER_ID_PTJ= 4L; //TEMP
	
	
	private PortletRequest getPortletRequest() {
		return (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}
	
	public PlanBackingBean() {
		log.info("PlanBackingBean - Start");
		if(this.getManagedBean().getViewMode()==null || ProcurementPlanConst.MODE_LIST.equals(this.getManagedBean().getViewMode())){			
			//set current user profile
			List <PmOrgProfile> ptjProfiles = getOrgProfiles(USER_ID_PTJ, ProcurementPlanConst.TYPE_PTJ);// TODO dynamic
			this.getManagedBean().setCurrentUser(ptjProfiles!=null ? ptjProfiles.get(0) : new PmOrgProfile());
		}
		
		//TODO: temp
		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
		log.info("PlanBackingBean - End");
	}

	/**
	 * redirect to plan content page
	 * @return xhtml
	 */
	public String redirectToCreatePlan(){
		log.info("redirectToCreatePlan - Start");
		
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_NEW);
		cleanPlanDetails();
		
		log.info("redirectToCreatePlan - End");
		return ProcurementPlanConst.NAV_TO_PLAN_DET;
    } 	
	
	/**
	 * initial plan details clean view
	 */
	private void cleanPlanDetails(){
		log.info("cleanPlanDetails - Start");
		PpPlanMaster planMaster = new PpPlanMaster();
		PpPlan plan = new PpPlan();
		
		plan.setEditable(true);
		plan.setStatusId(1L);
		plan.setPpQtActivities(new ArrayList<PpQtActivity>());
		plan.setPpRemarks(new ArrayList<PpRemark>());
		
		//Set Profile Tree
		planMaster.setOrgProfile(this.getManagedBean().getCurrentUser());
		
		//Plan Approver
		PpPlanActor approver = new PpPlanActor();
		approver.setActorType("A");
		approver.setActorSeq(1);
		plan.setApprover(approver);
		
		
		//PVO
		plan.setVerificationOfficers(new ArrayList<PpPlanActor>());
		for(int i=1; i<=3; i++){
			PpPlanActor vo = new PpPlanActor();
			vo.setActorSeq(i);
			vo.setActorType("V");
			plan.addVerificationOfficers(vo);
		}
		
		//Set normal plan
		this.getManagedBean().setPlanMaster(planMaster);
		this.getManagedBean().setPlan(plan);
		//Default view
		this.getManagedBean().setSelectedPlanMaster(planMaster);
		this.getManagedBean().setSelectedPlan(plan);
		
		log.info("cleanPlanDetails - End");
		
	}
	
	/**
	 * update view whenever procurement is changed.
	 */
	public void updateViewMode(){
		log.info("updateViewMode - Start");
		if(CommonMethod.isSelected(this.getManagedBean().getSelectedPlanMaster().getProcurementMode())){
			String procurementMode = this.getManagedBean().getSelectedPlanMaster().getProcurementMode();
			if(ProcurementPlanConst.PROCUREMENT_QUOTATION.equals(procurementMode) || ProcurementPlanConst.PROCUREMENT_DIRECT_NEGO.equals(procurementMode)
					|| ProcurementPlanConst.PROCUREMENT_TENDER.equals(procurementMode)){
				cleanPlanDetails();
				this.getManagedBean().getSelectedPlanMaster().setPlanType(ProcurementPlanConst.PLAN_TYPE_PROCUREMENT);
				this.getManagedBean().getSelectedPlanMaster().setProcurementMode(procurementMode);
			}else if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(procurementMode) 
					|| ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode)){
				cleanPlanDetails();
				this.getManagedBean().getSelectedPlanMaster().setPlanType(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
				this.getManagedBean().getSelectedPlanMaster().setProcurementMode(procurementMode);
				if(ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode)){
					//default favorite options to none
					this.getManagedBean().getSelectedPlan().setFavouriteOption(ProcurementPlanConst.FAVOURITE_TYPE_NONE);
				}
			}
		}
		updateMtoFlag();
		
		log.info("plan type: " +this.getManagedBean().getSelectedPlanMaster().getPlanType());
		log.info("updateViewMode - End");
    }
	
	/**
	 * determine expenditure mode
	 * @return result
	 */
	public boolean isExpenditure(){
		log.info("isExpenditure - Start");
		boolean result = false;
		if(CommonMethod.isSelected(this.getManagedBean().getSelectedPlanMaster().getProcurementMode())){
			String procurementMode = this.getManagedBean().getSelectedPlanMaster().getProcurementMode();
			if(ProcurementPlanConst.PROCUREMENT_QUOTATION.equals(procurementMode) || ProcurementPlanConst.PROCUREMENT_DIRECT_NEGO.equals(procurementMode)
					|| ProcurementPlanConst.PROCUREMENT_TENDER.equals(procurementMode)){
				result = false;
			}else if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(procurementMode) 
					|| ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode)){
				result = true;
			}
		}
		log.info("result " + result);
		log.info("isExpenditure - End");
		return result;
	}
	
	/**
	 * determine procurement mode
	 * @return result
	 */
	public boolean isProcurement(){
		log.info("isProcurement - Start");
		boolean result = false;
		if(CommonMethod.isSelected(this.getManagedBean().getSelectedPlanMaster().getProcurementMode())){
			String procurementMode = this.getManagedBean().getSelectedPlanMaster().getProcurementMode();
			if(ProcurementPlanConst.PROCUREMENT_QUOTATION.equals(procurementMode) || ProcurementPlanConst.PROCUREMENT_DIRECT_NEGO.equals(procurementMode)
					|| ProcurementPlanConst.PROCUREMENT_TENDER.equals(procurementMode)){
				result = true;
			}else if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(procurementMode) 
					|| ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode)){
				result = false;
			}
		}
		log.info("result " + result);
		log.info("isProcurement - End");
		return result;
	}
	
	
	/**
	 * determine availability of details tab
	 * @return result
	 */
	public boolean disableDetailTab(){
		log.info("disableDetailTab - Start");
		boolean result = true;

		String procurementMode = this.getManagedBean().getSelectedPlanMaster().getProcurementMode();
		String fulfillmentType = this.getManagedBean().getSelectedPlanMaster().getFulfilmentType();
		String favoriteOption = this.getManagedBean().getSelectedPlan().getFavouriteOption();
		
		log.info("procurementMode: " + procurementMode);
		log.info("fulfillmentType: " + fulfillmentType);
		log.info("favoriteOption: " + favoriteOption);
		
		if(ProcurementPlanConst.PROCUREMENT_QUOTATION.equals(procurementMode) || ProcurementPlanConst.PROCUREMENT_DIRECT_NEGO.equals(procurementMode)
				|| ProcurementPlanConst.PROCUREMENT_TENDER.equals(procurementMode)){
			result = false;
		}else{
			if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(procurementMode) &&
					CommonMethod.isSelected(fulfillmentType)){
				result = false;
			}else if(ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode) && 
					CommonMethod.isSelected(favoriteOption)){
				result = false;
			}	
		}

		log.info("result " + result);
		log.info("disableDetailTab - End");
		return result;
	}
	
	/**
	 * update MTO flag b plan details
	 */
	public void updateMtoFlag(){
		log.info("updateMtoFlag - Start");
		if(!isExpenditure()
				&& this.getManagedBean().getSelectedPlanMaster().isRequiredMto()
				&& (ProcurementPlanConst.ITEM_ORIGIN_TYPE_IMPORTED.equals(this.getManagedBean().getSelectedPlanMaster().getItemOrigin())
						|| ProcurementPlanConst.ITEM_ORIGIN_TYPE_BOTH.equals(this.getManagedBean().getSelectedPlanMaster().getItemOrigin()))){
			
			//Update MTO flag
			this.getManagedBean().setEnableMto(true);
			initialMtoPlan();
		}else{
			//Update MTO flag
			this.getManagedBean().setEnableMto(false);
		}
		log.info("updateMtoFlag - End");
	}
	
	/**
	 * initial MTO default view
	 */
	private void initialMtoPlan(){
		log.info("initialMtoPlan - Start");
		if(this.getManagedBean().getMtoPlanMaster()==null){
			log.info("initial MTO plan details");
			//Initial MTO plan details
			PpPlanMaster mtoPlanMaster = new PpPlanMaster();
			PpPlan mtoPlan = new PpPlan();
		

			mtoPlan.setEditable(true);
			mtoPlan.setStatusId(1L);
			mtoPlan.setPpQtActivities(new ArrayList<PpQtActivity>());
			mtoPlan.setPpRemarks(new ArrayList<PpRemark>());
			
			//Set Profile Tree
			System.out.println("ssssssssssssssssssssssssssssssssssssssssssssssssss");
			mtoPlanMaster.setOrgProfile(this.getManagedBean().getCurrentUser());

			//Set default values as per main procurement plan
			mtoPlanMaster.setMtoPlan(true);
			mtoPlanMaster.setProcurementMode(this.getManagedBean().getPlanMaster().getProcurementMode());
			mtoPlanMaster.setQtType(this.getManagedBean().getPlanMaster().getQtType());
			mtoPlanMaster.setItemOrigin(ProcurementPlanConst.ITEM_ORIGIN_TYPE_IMPORTED);
			mtoPlanMaster.setRequiredMto(true);
			
			//Plan Approver
			PpPlanActor approver = new PpPlanActor();
			approver.setActorType("A");
			approver.setActorSeq(1);
			mtoPlan.setApprover(approver);
			
			//PVO
			mtoPlan.setVerificationOfficers(new ArrayList<PpPlanActor>());
			for(int i=1; i<=3; i++){
				PpPlanActor vo = new PpPlanActor();
				vo.setActorSeq(i);
				vo.setActorType("V");
				mtoPlan.addVerificationOfficers(vo);
			}
			
			//Set normal plan
			this.getManagedBean().setMtoPlanMaster(mtoPlanMaster);
			this.getManagedBean().setMtoPlan(mtoPlan);
		}
		log.info("initialMtoPlan - End");
	}
	
	/**
	 * generate mto editable mode based on form data
	 * @return result
	 */
	public boolean lockMto(){
		log.info("lockMto - Start");
		boolean result = true;
		
		if((this.getManagedBean().getSelectedPlanMaster().getItemOrigin()!=null 
				&& !ProcurementPlanConst.ITEM_ORIGIN_TYPE_LOCAL.equals(this.getManagedBean().getSelectedPlanMaster().getItemOrigin())) 
				&& this.getManagedBean().getSelectedPlanMaster().isBrandNew()){
			result = false;
		}
		log.info("lockMto - End");
		return result;
	}

	/**
	 * update current view and current objects when MTO is enabled
	 * @param val
	 */
	public void updateCurrentView(TabChangeEvent val){
		log.info("updateCurrentView - Start");
		log.info("tab: " + val.getTab().getId());
		//enter the below function only if MTO is enabled.
		if(this.getManagedBean().isEnableMto()){
			if(ProcurementPlanConst.CURRENT_NEW.equals(val.getTab().getId())){
				log.info("...tabN");
				this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_NEW);
				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
				this.getManagedBean().setSelectedPlanMaster(this.getManagedBean().getPlanMaster());
				this.getManagedBean().setSelectedPlan(this.getManagedBean().getPlan());
				
			}else if(ProcurementPlanConst.CURRENT_MTO.equals(val.getTab().getId())){
				log.info("...tabM");
				this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_MTO);
				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
				this.getManagedBean().setSelectedPlanMaster(this.getManagedBean().getMtoPlanMaster());
				this.getManagedBean().setSelectedPlan(this.getManagedBean().getMtoPlan());
			
			}
		}
		log.info("updateCurrentView - End");
	}
	
	/**
	 * add remark to budget allocation level
	 */
	public void addBudgetAllocRemark(){
		log.info("addBudgetAllocRemark - Start");
		PpRemark remark = new PpRemark();

		if(this.getManagedBean().getSelectedPlan().getPpRemarks()==null){
			this.getManagedBean().getSelectedPlan().setPpRemarks(new ArrayList<PpRemark>());
		}
		this.getManagedBean().getSelectedPlan().addPpRemarks(remark);//TODO check this
		System.out.println(">>>>>>>> " + this.getManagedBean().getSelectedPlan().getPpRemarks().size());
		log.info("addBudgetAllocRemark - End");
	}
	
	/**
	 * populate plan item
	 * @param item
	 */
	public void showPpItem(PpPlanItem item){
		log.info("showPpItem - Start");
		if(item==null){
			this.getManagedBean().setSelectedItemRemark(new PpRemark());
			this.getManagedBean().setSelectedItem(new PpPlanItem());
			this.getManagedBean().getSelectedItem().setBrandNew(true);
			this.getManagedBean().getSelectedItem().setCmUnspsc(new CmUnspsc());
		}else{
			if(ProcurementPlanConst.MODE_REVISION.equals(this.getManagedBean().getViewMode())){
				item.setEditable(true);
			}else{
				item.setEditable(false);
			}
			item.setSelected(true);
			this.getManagedBean().setSelectedItem(item);
			//call populate allocated budget as gosodo
		}
		log.info("showPpItem - End");
	}
	
	/**
	 * delete plan item from the list
	 * @param item
	 */
	public void deletePpItem(PpBudgetAllocDtl item){
		log.info("deletePpItem - Start");
		if(item!=null){
			if(!item.isBrandNew()){
				item.setDeleted(true);
				item.setRecordStatus(false);
				//rollback amount
				//updateSelectedGo(item, "REMOVE");
			}else{
				this.getManagedBean().getSelectedPlan().getPpPlanItems().remove(item);
			}
		}
		log.info("deletePpItem - End");
	}
	
	
	/**
	 * add plan item to the list
	 */
	public void addItemToList(){
		log.info("addItemToList - Start");
		//Initial item list if empty
		if (this.getManagedBean().getSelectedPlan().getPpPlanItems()== null){
			this.getManagedBean().getSelectedPlan().setPpPlanItems(new ArrayList<PpPlanItem>());
		}

		if(this.getManagedBean().getSelectedItem().isBrandNew() && !this.getManagedBean().getSelectedItem().isSelected()){
			//update amount
			this.getManagedBean().getSelectedItem().setStatusId(ProcurementPlanConst.STATUS_PBD_NEW);
//			updateSelectedGo(this.getManagedBean().getSelectedItem(), "ADD");
			this.getManagedBean().getSelectedPlan().addPpItems(this.getManagedBean().getSelectedItem());
		}else{	

			if(this.getManagedBean().getSelectedItem().getBudgetAllocDtlId()==this.getManagedBean().getSelectedItem().getOriPpBudgetAllocDtl().getBudgetAllocDtlId()){
				//update amount
//				updateSelectedGo(this.getManagedBean().getSelectedItem(), "UPDATE");
			}else{
				//rollback amount
//				updateSelectedGo(this.getManagedBean().getSelectedItem(), "CHANGE");
			}
		}
		//Add the item remark to the list //TODO check
		if(this.getManagedBean().getSelectedItem().getPpRemarks()==null){
			this.getManagedBean().getSelectedItem().setPpRemarks(new ArrayList<PpRemark>());
		}
		if(!this.getManagedBean().getSelectedItemRemark().getRemark().isEmpty()){
			this.getManagedBean().getSelectedItem().addPpRemarks(this.getManagedBean().getSelectedItemRemark());
			System.out.println("id....."  + this.getManagedBean().getSelectedItemRemark().getStatusId());
		}
				
		log.info("addItemToList - End");
	}
	
	
	
	/**
	 * generate header for view based on the viewMode
	 * @param viewMode
	 * @return
	 */
	public String generateHeader(String viewMode){
		return CommonMethod.generateHeader(viewMode, ProcurementPlanConst.PROG_PLAN);
	}
	
	/**
	 * redirect to budget allocation listing page
	 * @return xhtml
	 */
	public String redirectToListPlan(){
		log.info("redirectToListPlan");
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
	
	public List <PpQtActivity> pmTaskActivities(){
		log.info("pmTaskActivities - Start");
		try{
			
			List <PmBusinessActivity> taskActivities =  new ArrayList<PmBusinessActivity>();
			ITaskActivitySO taskActSO = (ITaskActivitySO) BusinessDelegate.getInstance().getServiceObject("pm", ITaskActivitySO.class);
			TaskResDTO resDTO = taskActSO.retrieveActivityTaskList();
//			if(resDTO.isSuccessful()){
				taskActivities = resDTO.getBusinessActivityList();
//			}
			
			for(PmBusinessActivity task: taskActivities){
				System.out.println("task " + task.getActivityName());
				PpQtActivity activity = new PpQtActivity();
				activity.setPmBusinessActivity(task);
				activity.setBusinessActivityId(task.getBusinessActivityId());
				System.out.println("act " + activity.getPmBusinessActivity().getActivityName());
				this.getManagedBean().getSelectedPlan().addPpQtActivities(activity);
			}
		
			
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("pmTaskActivities - End");
		return this.getManagedBean().getSelectedPlan().getPpQtActivities();
	}
	
	
	
//	public List<String> pmTaskActivities() {
//		
//		List <String> sourcingAct = new ArrayList<String>();
//		sourcingAct.add("Create Sourcing");
//		sourcingAct.add("Appoint Committee");
//		sourcingAct.add("Committee Approval");
//		sourcingAct.add("Prepare Specification");
//		sourcingAct.add("Specification Approval by Ministry");
//		sourcingAct.add("Approval for Publication");
//		sourcingAct.add("Notice Publish");
//		sourcingAct.add("Closing");
//		sourcingAct.add("Opening");
//		sourcingAct.add("Proposal Preparation");
//		sourcingAct.add("Proposal Evaluation");
//		sourcingAct.add("LOA");
//		sourcingAct.add("Budget Approval");
//		sourcingAct.add("MoF Approval");
//		sourcingAct.add("MITI Approval");
//		sourcingAct.add("ICT Approval");
//		sourcingAct.add("Briefing");
//		sourcingAct.add("Site Visit");
//		sourcingAct.add("Shortlisting of supplier for online bidding invitation");
//		sourcingAct.add("Online bidding");
//		sourcingAct.add("LOI");
//		
//		for(String sa: sourcingAct){
//			PpQtActivity activity = new PpQtActivity();
////			activity.setActivityId(activityId);
//			this.getManagedBean().getSelectedPlan().addPpQtActivities(activity);
//		}
//		return sourcingAct;
//	}
	
	/**
	 * retrieve division list from PM
	 * @return division list
	 */
	public List<PmOrgProfile> divisionOptions() {
		log.info("issueDivisionOptions - Start");
		List<PmOrgProfile> options = new ArrayList<PmOrgProfile>();
		
		if(this.getManagedBean().getSelectedPlanMaster().getOrgProfile()!=null){
			Long orgProfileId = this.getManagedBean().getSelectedPlanMaster().getOrgProfile().getPmOrgProfile().getPmOrgProfile().getOrgProfileId();
			OrgUnitReqDTO reqDTO = new OrgUnitReqDTO();
			reqDTO.setOrgProfileId(orgProfileId);
			IOrgUnitSO orgUnitSO = (IOrgUnitSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgUnitSO.class);
			OrgUnitResDTO resDTO = orgUnitSO.retrieveDivisionByMinistryId(reqDTO);
	//		if(resDTO.isSuccessful()){
				options = resDTO.getPmOrgProfileByMinistryList();
	//		}
		}
		log.info("issueDivisionOptions - End");
		return options;
	}
	
	/**
	 * retrieve department list by division id from PM
	 * @param divisionId
	 * @return department list
	 */
	public List<PmOrgProfile> departmentOptions(Long divisionId) {
		log.info("issueDeptOptions - Start");
		List<PmOrgProfile> options = new ArrayList<PmOrgProfile>();

		if(CommonMethod.isSelected(divisionId)){
			OrgUnitReqDTO reqDTO = new OrgUnitReqDTO();
			reqDTO.setOrgProfileId(divisionId);
			reqDTO.setTypeCodeId(ProcurementPlanConst.TYPE_PTJ);
			IOrgUnitSO orgUnitSO = (IOrgUnitSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgUnitSO.class);
			OrgUnitResDTO resDTO = orgUnitSO.retrieveOrgProfileByParentIdAndType(reqDTO);
	//		if(resDTO.isSuccessful()){
				options = resDTO.getPmorgProfileByParentList();
	//		}
		}
		log.info("issueDeptOptions - End");
		return options;
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
	
	public List<SelectItem> itemOriginTypeOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.ITEM_ORIGIN_TYPE_IMPORTED, "Imported"));
		options.add(new SelectItem(ProcurementPlanConst.ITEM_ORIGIN_TYPE_LOCAL, "Local"));
		options.add(new SelectItem(ProcurementPlanConst.ITEM_ORIGIN_TYPE_BOTH, "Imported & Local"));
		return options;
	}
	
	public List<SelectItem> verificationLevelOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.LEVEL_SINGLE, "Single"));
		options.add(new SelectItem(ProcurementPlanConst.LEVEL_MULTIPLE, "Multiple"));
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

	/**
	 * retrieve org profile by login user and type
	 * @param userId
	 * @param type
	 * @return org profile
	 */
	private List <PmOrgProfile> getOrgProfiles(Long userId, Long type){
		log.info("getOrgProfiles - Start");
		log.info("userId " + userId);
		List <PmOrgProfile> profileList =  new ArrayList<PmOrgProfile>();
		try{
			if(CommonMethod.isSelected(userId) && CommonMethod.isSelected(type)){
				OrgUnitReqDTO reqDTO = new OrgUnitReqDTO();
				reqDTO.setUserId(userId);
				reqDTO.setTypeCodeId(type);
				IOrgUnitSO orgUnitSO = (IOrgUnitSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgUnitSO.class);
				OrgUnitResDTO resDTO = orgUnitSO.retrieveOrgProfileTypeCode(reqDTO);
//				if(resDTO.isSuccessful()){
					profileList = resDTO.getPmorgProfileTypeCodeList();
//				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		log.info("getOrgProfiles - End");
		return profileList;
	}
}
