/*
 * Copyright © 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pp.procurementplan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.service.pm.authorization.orgunit.IOrgUnitSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.taskactivity.ITaskActivitySO;
import my.ep.biz.service.pp.budgetallocation.IBudgetAllocationSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitReqDTO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pm.common.taskactivity.TaskResDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationReqDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.pm.authorization.PmBusinessActivity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
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
	 * redirect to plan details view from BPM & DB
	 * @param taskId
	 * @param status
	 * @return xhtml
	 */
	public String redirectToPlanContent(String taskId, String status){//temp status
		log.info("redirectToPlanContent - Start");
		boolean isComplete = false;
	
		try{
			if(CommonMethod.isSelected(taskId)){
				log.info("populate view");
//				isComplete = retrieveTask(taskId, status);
			}else{
				log.info("new view");
				cleanPlanDetails();
				isComplete = true;
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("redirectToPlanContent - End");
		
		if(isComplete){
			return ProcurementPlanConst.NAV_TO_PLAN_DET;
		}else{
			return "";
		}
	}
	
	/**
	 * initial plan details clean view
	 */
	private void cleanPlanDetails(){
		log.info("cleanPlanDetails - Start");
		
		PpPlanMaster planMaster = new PpPlanMaster();
		PpPlan plan = new PpPlan();
		
		plan.setEditable(true);
		plan.setStatusId(ProcurementPlanConst.STATUS_PPL_NEW);
		plan.setPpRemarks(new ArrayList<PpRemark>());
		plan.setPpQtActivities(new ArrayList<PpQtActivity>());
		
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

		//Default values
		this.getManagedBean().getSelectedGoMapper().clear();
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_NEW);
		this.getManagedBean().setActions(ProcurementPlanConst.BPM_DEFAULT_ACTIONS);
		
		//Set normal plan
		this.getManagedBean().setPlanMaster(planMaster);
		this.getManagedBean().setPlan(plan);
		
		//initialize task activities
		List <PmBusinessActivity>taskActivities = initialPmTaskActivities();
		
		//initial QT activities list
		if(this.getManagedBean().getPlan().getAllPpQtActivities()==null){
			this.getManagedBean().getPlan().setAllPpQtActivities(new ArrayList<PpQtActivity>());
		}
		
		for(PmBusinessActivity task: taskActivities){
			PpQtActivity activity = new PpQtActivity();
			activity.setPmBusinessActivity(task);
			activity.setBusinessActivityId(task.getBusinessActivityId());
			this.getManagedBean().getPlan().getAllPpQtActivities().add(activity);
		}

		
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
	 * determine availability of details tab
	 * @return result
	 */
	public boolean disableDetailTab(){
		log.info("disableDetailTab - Start");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>> " + this.getManagedBean().getSelectedPlan().getPlanDesc());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>> " + this.getManagedBean().getSelectedPlanMaster().getQtType());
		boolean result = true;
		this.getManagedBean().setDynamicDialogName("");
		String procurementMode = this.getManagedBean().getSelectedPlanMaster().getProcurementMode();
		String fulfillmentType = this.getManagedBean().getSelectedPlanMaster().getFulfilmentType();
		String favoriteOption = this.getManagedBean().getSelectedPlan().getFavouriteOption();
		
		log.info("procurementMode: " + procurementMode);
		log.info("fulfillmentType: " + fulfillmentType);
		log.info("favoriteOption: " + favoriteOption);
		
		if(ProcurementPlanConst.PROCUREMENT_QUOTATION.equals(procurementMode) || ProcurementPlanConst.PROCUREMENT_DIRECT_NEGO.equals(procurementMode)
				|| ProcurementPlanConst.PROCUREMENT_TENDER.equals(procurementMode)){
			this.getManagedBean().setDynamicDialogName("details/ppItem.xhtml");
			result = false;
		}else{
			if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(procurementMode) &&
					CommonMethod.isSelected(fulfillmentType)){
				this.getManagedBean().setDynamicDialogName("details/ppDetails.xhtml"); //TODO
				result = false;
			}else if(ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode) && 
					CommonMethod.isSelected(favoriteOption)){
				this.getManagedBean().setDynamicDialogName("details/ppDetails.xhtml"); //TODO
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
		if(ProcurementPlanConst.PLAN_TYPE_PROCUREMENT.equals(this.getManagedBean().getSelectedPlanMaster().getPlanType())
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
			
			//initialize task activities
			List <PmBusinessActivity>taskActivities = initialPmTaskActivities();
			
			//initial QT activities list
			if(this.getManagedBean().getMtoPlan().getAllPpQtActivities()==null){
				this.getManagedBean().getMtoPlan().setAllPpQtActivities(new ArrayList<PpQtActivity>());
			}
			
			for(PmBusinessActivity task: taskActivities){
				PpQtActivity activity = new PpQtActivity();
				activity.setPmBusinessActivity(task);
				activity.setBusinessActivityId(task.getBusinessActivityId());
				this.getManagedBean().getMtoPlan().getAllPpQtActivities().add(activity);
			}
			
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
	 * add remark to pp plan level
	 */
	public void addPlanRemark(){
		log.info("addPlanRemark - Start");
		PpRemark remark = new PpRemark();

		if(this.getManagedBean().getSelectedPlan().getPpRemarks()==null){
			this.getManagedBean().getSelectedPlan().setPpRemarks(new ArrayList<PpRemark>());
		}
		this.getManagedBean().getSelectedPlan().addPpRemarks(remark);//TODO check this
		System.out.println(">>>>>>>> " + this.getManagedBean().getSelectedPlan().getPpRemarks().size());
		log.info("addPlanRemark - End");
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
			this.getManagedBean().getSelectedItem().setPpBudgetAllocDtl(new PpBudgetAllocDtl());
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
	public void deletePpItem(PpPlanItem item){
		log.info("deletePpItem - Start");
		if(item!=null){
			if(!item.isBrandNew()){
				item.setDeleted(true);
				item.setRecordStatus(false);
				//rollback amount
				updateSelectedDo(item, "REMOVE");
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
		System.out.println("rainnnnnnn" + this.getManagedBean().getSelectedItem().getEstimatedAmount());
		//Initial item list if empty
		if (this.getManagedBean().getSelectedPlan().getPpPlanItems()== null){
			this.getManagedBean().getSelectedPlan().setPpPlanItems(new ArrayList<PpPlanItem>());
		}

		if(this.getManagedBean().getSelectedItem().isBrandNew() && !this.getManagedBean().getSelectedItem().isSelected()){
			//update amount
			this.getManagedBean().getSelectedItem().setStatusId(ProcurementPlanConst.STATUS_PBD_NEW);
			updateSelectedDo(this.getManagedBean().getSelectedItem(), "ADD");
			this.getManagedBean().getSelectedPlan().addPpItems(this.getManagedBean().getSelectedItem());
		}else{	

			if(this.getManagedBean().getSelectedItem().getBudgetAllocDtlId()==this.getManagedBean().getSelectedItem().getOriPpBudgetAllocDtl().getBudgetAllocDtlId()){
				//update amount
				updateSelectedDo(this.getManagedBean().getSelectedItem(), "UPDATE");
			}else{
				//rollback amount
				updateSelectedDo(this.getManagedBean().getSelectedItem(), "CHANGE");
			}
		}
		//Add the item remark to the list //TODO check
		if(this.getManagedBean().getSelectedItem().getPpRemarks()==null){
			this.getManagedBean().getSelectedItem().setPpRemarks(new ArrayList<PpRemark>());
		}
//		if(!this.getManagedBean().getSelectedItemRemark().getRemark().isEmpty()){
//			this.getManagedBean().getSelectedItem().addPpRemarks(this.getManagedBean().getSelectedItemRemark());
//			System.out.println("id....."  + this.getManagedBean().getSelectedItemRemark().getStatusId());
//		}
				
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
	 * redirect to plan listing page
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
	 * initial the default QT Activities
	 */
	public List <PmBusinessActivity> initialPmTaskActivities(){
		log.info("initialPmTaskActivities - Start");
		List <PmBusinessActivity> taskActivities =  new ArrayList<PmBusinessActivity>();
		try{
			ITaskActivitySO taskActSO = (ITaskActivitySO) BusinessDelegate.getInstance().getServiceObject("pm", ITaskActivitySO.class);
			TaskResDTO resDTO = taskActSO.retrieveActivityTaskList();
//			if(resDTO.isSuccessful()){
				taskActivities = resDTO.getBusinessActivityList();
//			}
				
//			//initial QT activities list
//			if(this.getManagedBean().getAllTaskActivity()==null){
//				this.getManagedBean().setAllTaskActivity(new ArrayList<PpQtActivity>());
//			}
//			
//			for(PmBusinessActivity task: taskActivities){
//				PpQtActivity activity = new PpQtActivity();
//				activity.setPmBusinessActivity(task);
//				activity.setBusinessActivityId(task.getBusinessActivityId());
//				this.getManagedBean().getAllTaskActivity().add(activity);
//			}
		
			
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("initialPmTaskActivities - End");
		return taskActivities;
	
	}
	
	public void calculateEndDate(PpQtActivity activity){
		log.info("calculateEndDate - Start");
		if(activity!=null){
			if(activity.getPlanStartDate()!=null){
				Calendar startDate = CommonMethod.convertDateToCalender(activity.getPlanStartDate());
				startDate.add(Calendar.DATE, activity.getPmBusinessActivity().getSlaDay());
				activity.setPlanEndDate(startDate.getTime());
			}else{
				activity.setPlanEndDate(null);
			}
		}
		log.info("calculateEndDate - End");
	}
	
	public void updateSelectedDo(PpPlanItem item, String mode){
		log.info("updateSelectedDo - Start");
		
		try{
			log.info("mapper keys " + this.getManagedBean().getSelectedGoMapper().keySet());
			log.info("mapper values " +this.getManagedBean().getSelectedGoMapper().values());
	
			long key = item.getBudgetAllocDtlId();
			if("CHANGE".equals(mode)){
				//change GO
				long oldKey = item.getOriPpBudgetAllocDtl().getBudgetAllocDtlId();
				System.out.println(	this.getManagedBean().getSelectedGoMapper().get(oldKey) + "-" + item.getOriEstimatedAmount());
				this.getManagedBean().getSelectedGoMapper().put(
						oldKey, 
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getSelectedGoMapper().get(oldKey), 
								item.getOriEstimatedAmount())
				);
				System.out.println(">>>>>>>>ori amount " + item.getOriEstimatedAmount());
				System.out.println(">>>>>>>>ori Budget alloc " + item.getOriPpBudgetAllocDtl().getBudgetAllocDtlId());
				System.out.println(	this.getManagedBean().getSelectedGoMapper().get(key) + "+" + item.getEstimatedAmount());
				this.getManagedBean().getSelectedGoMapper().put(
						key, 
						CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getSelectedGoMapper().get(key),
								item.getEstimatedAmount())
				);
				
				item.setOriEstimatedAmount(item.getEstimatedAmount());//update ori amount to new amount
				item.setOriPpBudgetAllocDtl(item.getPpBudgetAllocDtl());//update ori budget to new budget
				System.out.println(">>>>>>>>update ori amount " + item.getOriEstimatedAmount());
				System.out.println(">>>>>>>>update ori Budget alloc " + item.getOriPpBudgetAllocDtl().getBudgetAllocDtlId());
			}else if("REMOVE".equals(mode)){
				//remove item
				this.getManagedBean().getSelectedGoMapper().put(
						key, 
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getSelectedGoMapper().get(key), 
								item.getEstimatedAmount())
				);
			}else if("ADD".equals(mode)){
				//add item
	//			if(this.getManagedBean().getSelectedGoMapper().get(key)==null){
	//				this.getManagedBean().getSelectedGoMapper().put(key, item.getAllocAmount());
	//			}else{
					this.getManagedBean().getSelectedGoMapper().put(
							key, 
							CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getSelectedGoMapper().get(key),
								item.getEstimatedAmount())
					);
				item.setOriEstimatedAmount(item.getEstimatedAmount());//initiate ori amount
				item.setOriPpBudgetAllocDtl(item.getPpBudgetAllocDtl());//initiate ori budget
	//			}
			}else if("UPDATE".equals(mode)){
				//update item with same GO
				this.getManagedBean().getSelectedGoMapper().put(
						key, 
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getSelectedGoMapper().get(key), 
								item.getOriEstimatedAmount())
				);
				System.out.println(">>>>>>>>ori RM " + item.getOriEstimatedAmount());
				this.getManagedBean().getSelectedGoMapper().put(
						key, 
						CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getSelectedGoMapper().get(key),
								item.getEstimatedAmount())
				);
				item.setOriEstimatedAmount(item.getEstimatedAmount());//update ori amount to new amount
				System.out.println(">>>>>>>>update ori RM " + item.getOriEstimatedAmount());
			}else if("INIT".equals(mode)){
				this.getManagedBean().getSelectedGoMapper().put(
						key, new BigDecimal(0));
				item.setOriEstimatedAmount(item.getEstimatedAmount());//initiate ori amount
				item.setOriPpBudgetAllocDtl(item.getPpBudgetAllocDtl());//initiate ori budget
			}
			log.info("mapper keys " + this.getManagedBean().getSelectedGoMapper().keySet());
			log.info("mapper values " +this.getManagedBean().getSelectedGoMapper().values());
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("updateSelectedDo - End");
	}

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
		options.add(new SelectItem(ProcurementPlanConst.QT_TYPE_INTERNATIONAL, "International"));
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
	
	/**
	 * generate available budget amount
	 * @return available budget amount
	 */
	public BigDecimal generateAvailableBudget(){
		return
				CommonMethod.substractBigDecimalNullable(
				this.getManagedBean().getSelectedItem().getPpBudgetAllocDtl().getAllocAmount(),
				this.getManagedBean().getSelectedItem().getPpBudgetAllocDtl().getUsedAmount());
	}
	
	

	/**
	 * populate GO list
	 * @return GO list
	 */
	public List<PpBudgetAllocDtl> populateGo() {
		log.info("populateGo");	
		return getAllBudgetAllocDtls(this.getManagedBean().getCurrentUser().getOrgProfileId());
	}
	
	/**
	 * retrieve default budget by ministry id
	 * @param ministryId
	 * @return budget list
	 */
	private List <PpBudgetAllocDtl> getAllBudgetAllocDtls(Long orgProfileId){
		log.info("getAllBudgetAllocDtls - Start");
		log.info("orgProfileId " + orgProfileId);
		List <PpBudgetAllocDtl> budgetAllocDtlList =  new ArrayList<PpBudgetAllocDtl>();
		try{
			if(CommonMethod.isSelected(orgProfileId)){
				BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
				reqDTO.setOrgProfileId(orgProfileId);	
				IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
				BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetAllocDtlList(reqDTO);
				
				if (resDTO.isSuccessful()) {
					budgetAllocDtlList = resDTO.getBudgetAllocDtlList();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("getAllBudgetAllocDtls - End");
		return budgetAllocDtlList;
	}
	
	public void selectedDo(){
		log.info("selectedDo - Start");
		Long id = this.getManagedBean().getSelectedItem().getBudgetAllocDtlId();
		try{
			if(CommonMethod.isSelected(id)){
				BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
				reqDTO.setBudgetAllocDtlId(id);
				IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
				BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetAllocDtl(reqDTO);
				if(resDTO.isSuccessful()){
					this.getManagedBean().getSelectedItem().setPpBudgetAllocDtl(resDTO.getBudgetAllocDtl());
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("selectedDo - End");		
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
	

	/**
	 * generate plan master with completed children
	 */
	private void getCompletePlanMaster(){
		//getting only selected task activities
		List <PpQtActivity> activities = this.getManagedBean().getSelectedPlan().getAllPpQtActivities();
		for(PpQtActivity act:activities){
			if(act.isBrandNew() && act.getPlanStartDate()!=null){
				this.getManagedBean().getSelectedPlan().addPpQtActivities(act);
			}
		}
		
		//initial new plan
		this.getManagedBean().getSelectedPlanMaster().setPpPlans(new ArrayList<PpPlan>());
		//only one plan
		this.getManagedBean().getSelectedPlanMaster().addPpPlans(this.getManagedBean().getSelectedPlan());

	}
	
	private void mtoPlan(){
		if(this.getManagedBean().isEnableMto()){
			List <PpQtActivity> activities = this.getManagedBean().getMtoPlan().getAllPpQtActivities();
			for(PpQtActivity act:activities){
				if(act.isBrandNew() && act.getPlanStartDate()!=null){
					this.getManagedBean().getMtoPlan().addPpQtActivities(act);
				}
			}
			
			//initial new plan
			this.getManagedBean().getMtoPlanMaster().setPpPlans(new ArrayList<PpPlan>());
			//only one plan
			this.getManagedBean().getMtoPlanMaster().addPpPlans(this.getManagedBean().getMtoPlan());
		}
	}
	
	
	/**
	 * submit plan when button is clicked
	 * @param action
	 * @return xhtml
	 */
	public String submitPlan(String action) {
		log.info("submitPlan - Start");
		String returnPage = "";
		boolean isSuccess = false;
		if(ProcurementPlanConst.BPM_SUBMIT.equals(action)){
			if(this.getManagedBean().getSelectedPlanMaster().isBrandNew()){
				isSuccess = submitNewPlan(action);
			}else{
				isSuccess = submitUpdatePlan(action);
			}
		}else if (ProcurementPlanConst.BPM_REVISE.equals(action)){
			isSuccess = submitUpdatePlan(action);
		}else if (ProcurementPlanConst.BPM_APPROVE.equals(action)){
			isSuccess = submitUpdatePlan(action);
		}else if (ProcurementPlanConst.BPM_SAVE.equals(action)){
//			reserveBudget();
			isSuccess = submitNewPlan(action);
		}
		
		if(isSuccess){
			returnPage = redirectToListPlan();
		}
		log.info("submitPlan - End");
		return returnPage;
	}
	
	
	/**
	 * submit new plan to DB and initial BPM task
	 * @param action
	 * @return submit result
	 */
	public boolean submitNewPlan(String action){
		log.info("submitNewPlan - Start");
		boolean isSubmitted = false;
		try {
			PpPlanMaster ppPlanMaster = savePlan(action);
			if(ppPlanMaster!=null && ppPlanMaster.getPlanMasterId()>0L){
				log.info("calling BPM.......");
//				isSubmitted = initialBpmTask(action, ppPlanMaster);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("submitNewPlan - End");
		return isSubmitted;
	}
	
	/**
	 * submit update plan to DB and submit BPM task
	 * @param action
	 * @return submit result
	 */
	public boolean submitUpdatePlan(String action){
		log.info("submitUpdatePlan - Start");
		boolean isSubmitted = false;
		try {
			PpPlanMaster ppPlanMaster = savePlan(action);
			String taskId = this.getManagedBean().getBpmTaskId();
			if(ppPlanMaster!=null && CommonMethod.isSelected(taskId)){
				log.info("calling BPM.......");
//				isSubmitted = submitBpmTask(taskId, action, ppPlanMaster);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("submitUpdatePlan - End");
		return isSubmitted;
	}

	/**
	 * Save new/udpate Plan to DB
	 * @param action
	 * @return
	 */
	public PpPlanMaster savePlan(String action){
		log.info("savePlan - Start");
		PpPlanMaster resultObj = null;
		boolean brandNew = this.getManagedBean().getSelectedPlanMaster().isBrandNew();
		if (!brandNew) {
			resultObj = updatePlan(action);
		} else {
			getCompletePlanMaster();
			resultObj = addPlan();
		}
		log.info("savePlan - End");
		return resultObj;
	}
	
	/**
	 * update plan to DB
	 * @param action
	 * @return budget allocation with ID
	 */
	public PpPlanMaster updatePlan(String action){
		log.info("updatePlan - Start");
	
		PpPlanMaster resultObj  = null;
		try{
//			BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
//			if(ProcurementPlanConst.BPM_SUBMIT.equals(action)){
//				//rollback revision flag
//				PpBudgetAlloc butgetAlloc = this.getManagedBean().getSelectedBudgetAllocation();
//				for(PpBudgetAllocDtl p :butgetAlloc.getPpBudgetAllocDtls()){
//					p.setRevisionRequired(false);
//					p.setStatusId(ProcurementPlanConst.STATUS_PENDING_APPROVAL); //TODO change 
//				}
//			}
//			
//			reqDTO.setBudgetAllocation(this.getManagedBean().getSelectedBudgetAllocation());
//			reqDTO.setSelectedGoMapper(this.getManagedBean().getSelectedGoMapper());
//			IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
//			BudgetAllocationResDTO resDTO = reqSO.updateBudgetAllocation(reqDTO);
//			
//			if(resDTO.isSuccessful()){
//				resultObj = resDTO.getBudgetAlloc();
//			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("updatePlan - End");
		return resultObj;
	}
	
	/**
	 * save plan to DB
	 * @return plan with ID
	 */
	public PpPlanMaster addPlan(){
		log.info("addPlan - Start");
		PpPlanMaster resultObj  = null;
		try{
			ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
			reqDTO.setPlanMaster(this.getManagedBean().getSelectedPlanMaster());
			reqDTO.setSelectedGoMapper(this.getManagedBean().getSelectedGoMapper());
			IProcurementPlanSO procurementPlanSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IProcurementPlanSO.class);
			ProcurementPlanResDTO resDTO = procurementPlanSO.addPlan(reqDTO);
			if(resDTO.isSuccessful()){
				resultObj = resDTO.getPlanMaster();
				log.info("Generated ID... " + resultObj.getPlanMasterId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("addPlan - End");
		
		return resultObj;
	}
}
