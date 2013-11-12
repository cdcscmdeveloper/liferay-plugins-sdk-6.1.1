/*
 * Copyright © 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pp.procurementplan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.activity.IActivitySO;
import my.ep.biz.service.pm.common.maksud.IMaksudSO;
import my.ep.biz.service.pm.common.reason.IReasonSO;
import my.ep.biz.service.pm.common.taskactivity.ITaskActivitySO;
import my.ep.biz.service.pp.budgetallocation.IBudgetAllocationSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.constant.ct.ContractConst;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.ct.ContractReqDTO;
import my.ep.dto.ct.ContractResDTO;
import my.ep.dto.ct.circular.CircularReqDTO;
import my.ep.dto.ct.circular.CircularResDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.activity.ActivityReqDTO;
import my.ep.dto.pm.common.activity.ActivityResDTO;
import my.ep.dto.pm.common.maksud.MaksudResDTO;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.dto.pm.common.reason.ReasonResDTO;
import my.ep.dto.pm.common.taskactivity.TaskResDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationReqDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.cc.CtCircularContract;
import my.ep.persistence.entity.pm.authorization.PmBusinessActivity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmActivity;
import my.ep.persistence.entity.pm.common.PmMaksud;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanActor;
import my.ep.persistence.entity.pp.procurementplan.PpPlanItem;
import my.ep.persistence.entity.pp.procurementplan.PpPlanMaster;
import my.ep.persistence.entity.pp.procurementplan.PpQtActivity;
import my.ep.persistence.entity.pp.procurementplan.PpRecurFreq;
import my.ep.persistence.entity.pp.procurementplan.PpRecurFreqDtl;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
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
	private static Logger log = Logger.getLogger(PlanBackingBean.class);
	private final static Long USER_ID_PTJ= 4L; //TEMP
	
	private SearchFilterBean statusFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean planNumberFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean createdDateFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	
	/**
	 * @return the statusFilterBean
	 */
	public SearchFilterBean getStatusFilterBean() {
		return statusFilterBean;
	}

	/**
	 * @param statusFilterBean the statusFilterBean to set
	 */
	public void setStatusFilterBean(SearchFilterBean statusFilterBean) {
		this.statusFilterBean = statusFilterBean;
	}

	/**
	 * @return the planNumberFilterBean
	 */
	public SearchFilterBean getPlanNumberFilterBean() {
		return planNumberFilterBean;
	}

	/**
	 * @param planNumberFilterBean the planNumberFilterBean to set
	 */
	public void setPlanNumberFilterBean(SearchFilterBean planNumberFilterBean) {
		this.planNumberFilterBean = planNumberFilterBean;
	}

	/**
	 * @return the createdDateFilterBean
	 */
	public SearchFilterBean getCreatedDateFilterBean() {
		return createdDateFilterBean;
	}

	/**
	 * @param createdDateFilterBean the createdDateFilterBean to set
	 */
	public void setCreatedDateFilterBean(SearchFilterBean createdDateFilterBean) {
		this.createdDateFilterBean = createdDateFilterBean;
	}
	
	private PortletRequest getPortletRequest() {
		return (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}
	
	/**
	 * search plan
	 */
	public void searchPlan(){
		log.info("planSearch - Start");
		IProcurementPlanSO procurementPlanSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IProcurementPlanSO.class);
		ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
		
		ISearchBean<PpPlan> searchBean = new SearchBean<PpPlan>();
		
		if(statusFilterBean.getSearchValue()!=null && CommonMethod.isSelected(statusFilterBean.getSearchValue().toString())){
			log.info("+ statusFilterBean " + statusFilterBean.getSearchValue());
			statusFilterBean.setSearchField("pmStatus.statusId");
			searchBean.getDataFilterBeans().add(statusFilterBean);
		}

		if(planNumberFilterBean.getSearchValue()!=null && planNumberFilterBean.getSearchValue()!=""){
			log.info("+ planNumberFilterBean: " + planNumberFilterBean.getSearchValue());
			planNumberFilterBean.setSearchField("planNo");
			searchBean.getDataFilterBeans().add(planNumberFilterBean);
		}
		
		if(createdDateFilterBean.getSearchValue()!=null && createdDateFilterBean.getSearchValue()!="" && !"null".equals(createdDateFilterBean.getSearchValue())){
			log.info("+ createdDateFilterBean: " + createdDateFilterBean.getSearchValue());
			createdDateFilterBean.setSearchField("createdDate");
			searchBean.getDataFilterBeans().add(createdDateFilterBean);
		}
		
		reqDTO.setSearchBean(searchBean);
		ProcurementPlanResDTO resDTO = procurementPlanSO.retrievePlanListByParams(reqDTO);
		log.info("size:"+resDTO.getPlanList().size());
		this.getManagedBean().setPlanList(resDTO.getPlanList());
		
		log.info("planSearch - End");
		
	}
	
	/**
	 * constructor of the backingbean
	 */
	public PlanBackingBean() {
		log.info("PlanBackingBean - Start");
		if(this.getManagedBean().getViewMode()==null || ProcurementPlanConst.MODE_LIST.equals(this.getManagedBean().getViewMode())){			
			//set current user profile
			List <PmOrgProfile> ptjProfiles = getOrgProfiles(USER_ID_PTJ, OrganizationType.DIVISION.value());// TODO dynamic
			this.getManagedBean().setCurrentUser(ptjProfiles!=null ? ptjProfiles.get(0) : new PmOrgProfile());
		}
		
		//TODO: temp
		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
		log.info("PlanBackingBean - End");
	}

	
	/**
	 * load task from task list
	 * @param budgetAlloc
	 * @param taskName
	 */
	public void loadTask(PpPlan ppPlan, String taskName){
		log.info("loadTask - Start");
		if(ppPlan!=null && taskName!=null){
			
			log.info("plan id: " + ppPlan.getPlanId());

			//initialize task activities
			List <PmBusinessActivity>taskActivities = initialPmTaskActivities();
			//initial QT task activities list
			ppPlan.setAllPpQtActivities(ppPlan.getPpQtActivities());
			//merge existing task activities to all activities
			for(PmBusinessActivity task: taskActivities){
				PpQtActivity tempAct = new PpQtActivity();
				tempAct.setPmBusinessActivity(task);
				if(!ppPlan.getAllPpQtActivities().contains(tempAct)){
					PpQtActivity activity = new PpQtActivity();
					activity.setPmBusinessActivity(task);
					activity.setBusinessActivityId(task.getBusinessActivityId());
					ppPlan.getAllPpQtActivities().add(activity);
				}
			}

			Collections.sort(ppPlan.getAllPpQtActivities());
			
			//retrieve and set approver and verification officer
			for(PpPlanActor actor: ppPlan.getPpPlanActors()){
				ppPlan.setVerificationOfficers(new ArrayList<PpPlanActor>());
				if(ProcurementPlanConst.SUBMIT_VERIFICATION.equals(actor.getActorType())){
					ppPlan.addVerificationOfficers(actor);
				}else if(ProcurementPlanConst.SUBMIT_APPROVAL.equals(actor.getActorType())){
					ppPlan.setApprover(actor);
				}
			}
			
			//set default PVO level
			int numPvo = ppPlan.getVerificationOfficers().size() + 1;
			for(int i=numPvo; i<=ProcurementPlanConst.PVO_LEVEL; i++){
				PpPlanActor vo = new PpPlanActor();
				vo.setActorSeq(i);
				vo.setActorType("V");
				ppPlan.addVerificationOfficers(vo);
			}
		
			//load mto plan
			if(ppPlan.getPpPlanMaster().isMtoPlan()){
				
				PpPlan mtoPlan = ppPlan.getPpPlanMaster().getMtoPlanMaster().getPpPlans().get(0);
				
				//initial QT task activities list
				mtoPlan.setAllPpQtActivities(mtoPlan.getPpQtActivities());

				//merge existing task activities to all activities
				for(PmBusinessActivity task: taskActivities){
					if(!mtoPlan.getAllPpQtActivities().contains(task)){
						System.out.println("task: " + task.getActivityName());
						PpQtActivity activity = new PpQtActivity();
						activity.setPmBusinessActivity(task);
						activity.setBusinessActivityId(task.getBusinessActivityId());
						mtoPlan.getAllPpQtActivities().add(activity);
					}
				}
				
				//retrieve and set approver and verification officer
				for(PpPlanActor actor: mtoPlan.getPpPlanActors()){
					mtoPlan.setVerificationOfficers(new ArrayList<PpPlanActor>());
					if(ProcurementPlanConst.SUBMIT_VERIFICATION.equals(actor.getActorType())){
						mtoPlan.addVerificationOfficers(actor);
					}else if(ProcurementPlanConst.SUBMIT_APPROVAL.equals(actor.getActorType())){
						mtoPlan.setApprover(actor);
					}
				}
				//set default PVO level
				int numPvoMto = ppPlan.getVerificationOfficers().size() + 1;
				for(int i=numPvoMto; i<=ProcurementPlanConst.PVO_LEVEL; i++){
					PpPlanActor vo = new PpPlanActor();
					vo.setActorSeq(i);
					vo.setActorType("V");
					mtoPlan.addVerificationOfficers(vo);
				}
				
				
				//set default mto plan
				this.getManagedBean().setMtoPlanMaster(mtoPlan.getPpPlanMaster().getMtoPlanMaster());
				this.getManagedBean().setMtoPlan(mtoPlan);
			}

			//set the default plan
			this.getManagedBean().setPlanMaster(ppPlan.getPpPlanMaster());
			this.getManagedBean().setPlan(ppPlan);
			
			//set the default current view obj
			this.getManagedBean().setSelectedPlanMaster(this.getManagedBean().getPlanMaster());
			this.getManagedBean().setSelectedPlan(this.getManagedBean().getPlan());

			if("Review Plan".equalsIgnoreCase(taskName)){
				log.info("Review Plan");
				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_APPROVAL);
				this.getManagedBean().getPlan().setEditable(false); 
			}else if("Revise Plan".equals(taskName)){
				log.info("Revise Plan");
				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_REVISION);
				this.getManagedBean().getPlan().setEditable(true); 
				
				for(PpPlanItem item:ppPlan.getPpPlanItems()){
					updateSelectedDo(item, "INIT");
				}
			}else if("Verify Plan".equals(taskName)){
				log.info("Verify Plan");
				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_VERIFICATION);
				this.getManagedBean().getPlan().setEditable(false); 
			}
			//enable ep/pp fields, details, mto
			updateViewMode(false); 
		}
		log.info("loadTask - End");
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
				log.info(isComplete);
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
		
		planMaster.setPpPlans(new ArrayList<PpPlan>());
		planMaster.getSelectedGoMapper().clear();
		plan.setEditable(true);
		plan.setStatusId(ProcurementPlanConst.STATUS_PPL_NEW);
		plan.setPpRemarks(new ArrayList<PpRemark>());
		plan.setPpQtActivities(new ArrayList<PpQtActivity>());
		
		
		//Set Profile Tree
		planMaster.setOrgProfile(this.getManagedBean().getCurrentUser());
		planMaster.setOrgProfileId(this.getManagedBean().getCurrentUser().getOrgProfileId());
		
		//Plan Approver
		PpPlanActor approver = new PpPlanActor();
		approver.setActorType("A");
		approver.setActorSeq(1);
		plan.setApprover(approver);
		
		
		//PVO
		plan.setVerificationOfficers(new ArrayList<PpPlanActor>());
		for(int i=1; i<=ProcurementPlanConst.PVO_LEVEL; i++){
			PpPlanActor vo = new PpPlanActor();
			vo.setActorSeq(i);
			vo.setActorType("V");
			plan.addVerificationOfficers(vo);
		}

		//Default values
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_NEW);
		this.getManagedBean().setActions(ProcurementPlanConst.BPM_DEFAULT_ACTIONS);
		this.getManagedBean().setSystemMsg("");
		this.getManagedBean().setEnableMto(false);
		this.getManagedBean().setEnableDetail(false);
	
		
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
	
		//avoid null
		showPpItem(null);
		showPpPlan(null);
		
		
		log.info("cleanPlanDetails - End");
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
		
			mtoPlanMaster.setPpPlans(new ArrayList<PpPlan>());
			mtoPlanMaster.getSelectedGoMapper().clear();
			
			mtoPlan.setEditable(true);
			mtoPlan.setStatusId(1L);
			mtoPlan.setPpQtActivities(new ArrayList<PpQtActivity>());
			mtoPlan.setPpRemarks(new ArrayList<PpRemark>());
			
			//Set Profile Tree
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
			for(int i=1; i<=ProcurementPlanConst.PVO_LEVEL; i++){
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
			
			//avoid null
			showPpItem(null);
		}
		log.info("initialMtoPlan - End");
	}
	

	/**
	 * update view whenever procurement is changed.
	 * @param reset
	 */
	public void updateViewMode(boolean reset){
		log.info("updateViewMode - Start");
		if(CommonMethod.isSelected(this.getManagedBean().getSelectedPlanMaster().getProcurementMode())){
			String procurementMode = this.getManagedBean().getSelectedPlanMaster().getProcurementMode();
			String votType = this.getManagedBean().getSelectedPlanMaster().getVotType();
			if(ProcurementPlanConst.PROCUREMENT_QUOTATION.equals(procurementMode) || ProcurementPlanConst.PROCUREMENT_DIRECT_NEGO.equals(procurementMode)
					|| ProcurementPlanConst.PROCUREMENT_TENDER.equals(procurementMode)){
				if(reset){
					cleanPlanDetails();	
				}
				this.getManagedBean().getSelectedPlanMaster().setPlanType(ProcurementPlanConst.PLAN_TYPE_PROCUREMENT);
				this.getManagedBean().getSelectedPlanMaster().setProcurementMode(procurementMode);
				this.getManagedBean().getSelectedPlanMaster().setVotType(votType);
			}else if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(procurementMode) 
					|| ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode)){
				if(reset){
					cleanPlanDetails();	
				}
				this.getManagedBean().getSelectedPlanMaster().setPlanType(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
				this.getManagedBean().getSelectedPlanMaster().setProcurementMode(procurementMode);
				this.getManagedBean().getSelectedPlanMaster().setVotType(votType);
				if(ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode)){
					//default favorite options to none
					if(this.getManagedBean().getSelectedPlan().isBrandNew()){
						this.getManagedBean().getSelectedPlan().setFavouriteOption(ProcurementPlanConst.FAVOURITE_TYPE_NONE);
					}
				}
			}
			
		}
		
		enableDetailTab(); //reflect to details tab
		updateMtoFlag(); //reflect to MTO tab
		log.info("procurement mode:" + this.getManagedBean().getSelectedPlanMaster().getProcurementMode());
		log.info("plan type: " +this.getManagedBean().getSelectedPlanMaster().getPlanType());
		log.info("updateViewMode - End");
    }
	
	
	/**
	 * determine availability of details tab
	 * @return result
	 */
	public boolean enableDetailTab(){
		log.info("enableDetailTab - Start");
		boolean result = false;
		this.getManagedBean().setDynamicDetailsName("");
		String procurementMode = this.getManagedBean().getSelectedPlanMaster().getProcurementMode();
		String fulfillmentType = this.getManagedBean().getSelectedPlanMaster().getFulfilmentType();
		String favoriteOption = this.getManagedBean().getSelectedPlan().getFavouriteOption();
		
		log.info("procurementMode: " + procurementMode);
		log.info("fulfillmentType: " + fulfillmentType);
		log.info("favoriteOption: " + favoriteOption);
		
		if(ProcurementPlanConst.PROCUREMENT_QUOTATION.equals(procurementMode) || ProcurementPlanConst.PROCUREMENT_DIRECT_NEGO.equals(procurementMode)
				|| ProcurementPlanConst.PROCUREMENT_TENDER.equals(procurementMode)){
			this.getManagedBean().setDynamicDetailsName(ProcurementPlanConst.PP_DET);
			result = true;
		}else{
			if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(procurementMode) &&
					CommonMethod.isSelected(fulfillmentType)){
				this.getManagedBean().setDynamicDetailsName(ProcurementPlanConst.EP_EXISTING_CONTRACT_DET);
				result = true;
			}else if(ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode) && 
					CommonMethod.isSelected(favoriteOption)){
				if(ProcurementPlanConst.FAVOURITE_TYPE_NONE.equals(favoriteOption)){
					this.getManagedBean().setDynamicDetailsName(ProcurementPlanConst.EP_DIRECT_PURCHASE_DET);
				}else if(ProcurementPlanConst.FAVOURITE_TYPE_PLAN.equals(favoriteOption)){
					this.getManagedBean().setDynamicDetailsName(ProcurementPlanConst.EP_FAVOURITE_PLAN_DET);
					showPpPlan(null);
					loadFavouritePlan();
				}else if(ProcurementPlanConst.FAVOURITE_TYPE_ITEM.equals(favoriteOption)){
					this.getManagedBean().setDynamicDetailsName(ProcurementPlanConst.EP_FAVOURITE_ITEM_DET);
					showPpPlan(null);
					loadFavouriteItem();
				}	
				result = true;
			}	
		}
		this.getManagedBean().setEnableDetail(result);
		log.info("result " + result);
		log.info("enableDetailTab - End");
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
		log.info("flag: " + this.getManagedBean().isEnableMto());
		log.info("updateMtoFlag - End");
	}
	
	
	/**
	 * generate mto editable mode based on form data
	 * @return result
	 */
	public boolean lockMto(){
		log.info("lockMto - Start");
		boolean result = true;
		if(CommonMethod.isSelected(this.getManagedBean().getSelectedPlanMaster().getItemOrigin())
				&& !ProcurementPlanConst.ITEM_ORIGIN_TYPE_LOCAL.equals(this.getManagedBean().getSelectedPlanMaster().getItemOrigin())
				&& this.getManagedBean().getSelectedPlanMaster().isBrandNew()){
					result = false;
		}
		
//		if((this.getManagedBean().getSelectedPlanMaster().getItemOrigin()!=null 
//				&& !ProcurementPlanConst.ITEM_ORIGIN_TYPE_LOCAL.equals(this.getManagedBean().getSelectedPlanMaster().getItemOrigin())) 
//				&& this.getManagedBean().getSelectedPlanMaster().isBrandNew()){
//			
//		}
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
		log.info(this.getManagedBean().getViewMode());
		//enter the below function only if MTO is enabled.
		try{
			this.getManagedBean().setSelectedTab(val.getTab().getId());
			if(ProcurementPlanConst.CURRENT_ALLOC_BUDGET.equals(val.getTab().getId())){
				this.getManagedBean().setBudgetAllocDtlList(getAllBudgetAllocDtls(this.getManagedBean().getCurrentUser().getOrgProfileId()));
			}else if(ProcurementPlanConst.CURRENT_PLAN.equals(val.getTab().getId())){
			}else if(ProcurementPlanConst.CURRENT_AUDIT.equals(val.getTab().getId())){
				
				
			}else if(ProcurementPlanConst.CURRENT_NEW.equals(val.getTab().getId())){
				log.info("...tabN");
				this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_NEW);
//				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
				this.getManagedBean().setSelectedPlanMaster(this.getManagedBean().getPlanMaster());
				this.getManagedBean().setSelectedPlan(this.getManagedBean().getPlan());
				
			}else if(ProcurementPlanConst.CURRENT_MTO.equals(val.getTab().getId())){
				log.info("...tabM");
				this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_MTO);
//				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
				this.getManagedBean().setSelectedPlanMaster(this.getManagedBean().getMtoPlanMaster());
				this.getManagedBean().setSelectedPlan(this.getManagedBean().getMtoPlan());
					
			}
		}catch(Exception e){
			e.printStackTrace();
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

			//For favourite item
			String favoriteOption = this.getManagedBean().getSelectedPlan().getFavouriteOption()!=null ? this.getManagedBean().getSelectedPlan().getFavouriteOption() : "";
			if(ProcurementPlanConst.FAVOURITE_TYPE_ITEM.equals(favoriteOption)){
//				loadFavouriteItem();
				PpRecurFreq ppRecurFreq = new PpRecurFreq();
				ppRecurFreq.setRecurFreq(1);
				ppRecurFreq.setPpRecurFreqDtls(new ArrayList<PpRecurFreqDtl>());
				ppRecurFreq.addPpRecurFreqDtls(new PpRecurFreqDtl());
				this.getManagedBean().getSelectedItem().setPpRecurFreq(ppRecurFreq);
				
			}	
			if(ProcurementPlanConst.FAVOURITE_TYPE_ITEM.equals(favoriteOption) 
					|| ProcurementPlanConst.FAVOURITE_TYPE_PLAN.equals(favoriteOption)){
				this.getManagedBean().getSelectedItem().setCmSupplierItem(new CmSupplierItem());
				this.getManagedBean().getSelectedItem().getCmSupplierItem().setSmSupplier(new SmSupplier());
			}

		}else{
			
			if(ProcurementPlanConst.MODE_REVISION.equals(this.getManagedBean().getViewMode())){
				item.setEditable(true);
			}else{
				item.setEditable(false);
			}

			item.setSelected(true);
			//populate selected GOSODO
			item.setGoId(item.getPpBudgetAllocDtl().getPpBudget().getBudgetId());
			item.setSoId(item.getPpBudgetAllocDtl().getSoId());
			item.setDoId(item.getPpBudgetAllocDtl().getDoId());
			
			//populate selected Unspsc level
			CmUnspsc familyObj = selectParentUnspsc(item.getCmUnspsc().getUnspscParentCode());
			CmUnspsc segmentObj = selectParentUnspsc(familyObj.getUnspscParentCode());
			item.setUnspscSegmentCode(segmentObj.getUnspscCode());
			item.setUnspscFamilyCode(familyObj.getUnspscCode());
			item.setUnspscClassCode(item.getCmUnspsc().getUnspscCode());
			
			String favoriteOption = this.getManagedBean().getSelectedPlan().getFavouriteOption()!=null ? this.getManagedBean().getSelectedPlan().getFavouriteOption() : "";
			if(ProcurementPlanConst.FAVOURITE_TYPE_ITEM.equals(favoriteOption) || ProcurementPlanConst.FAVOURITE_TYPE_PLAN.equals(favoriteOption)){
				System.out.println("showPpItem med");
				item.setUnspscCommodityId((item.getCmSupplierItem()!=null && item.getCmSupplierItem().getCmItem()!=null) ? 
						item.getCmSupplierItem().getCmItem().getItemId() : 0L);
				item.setCmSupplierItem(item.getCmSupplierItem()!=null ? item.getCmSupplierItem() : new CmSupplierItem());
			}
			this.getManagedBean().setSelectedItem(item);
			this.getManagedBean().setSelectedItemRemark(new PpRemark());
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
			if(!item.isBrandNew() && !item.isDeleted()){
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
	
		if(this.getManagedBean().getSelectedItem().isBrandNew() && !this.getManagedBean().getSelectedItem().isSelected()){
			//update amount
			this.getManagedBean().getSelectedItem().setStatusId(ProcurementPlanConst.STATUS_PBD_NEW);
			updateSelectedDo(this.getManagedBean().getSelectedItem(), "ADD");
			if(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equals(this.getManagedBean().getSelectedPlanMaster().getPlanType())){
				this.getManagedBean().getSelectedPlanItem().addPpPlanItems(this.getManagedBean().getSelectedItem());
			}else{
				System.out.println("add pp");
				this.getManagedBean().getSelectedPlan().addPpPlanItems(this.getManagedBean().getSelectedItem());
			}
			
		}else{	

			if(this.getManagedBean().getSelectedItem().getPpBudgetAllocDtl().getBudgetAllocDtlId()==this.getManagedBean().getSelectedItem().getOriPpBudgetAllocDtl().getBudgetAllocDtlId()){
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
		if(!this.getManagedBean().getSelectedItemRemark().getRemark().isEmpty()){
			this.getManagedBean().getSelectedItem().addPpRemarks(this.getManagedBean().getSelectedItemRemark());
		}
		if(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equals(this.getManagedBean().getSelectedPlanMaster().getPlanType())){
			//update total estimate amount per plan
			this.getManagedBean().getSelectedPlanItem().setEstimatedAmount(
					CommonMethod.addBigDecimalNullable(this.getManagedBean().getSelectedPlanItem().getEstimatedAmount(),
							this.getManagedBean().getSelectedItem().getEstimatedAmount()
					));
		}else{
			//update total estimate amount for only one plan
			this.getManagedBean().getSelectedPlan().setEstimatedAmount(
					CommonMethod.addBigDecimalNullable(this.getManagedBean().getSelectedPlan().getEstimatedAmount(),
							this.getManagedBean().getSelectedItem().getEstimatedAmount()
					));
			
		}

		//reset item view for DP - none & EC only
//		if((ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(this.getManagedBean().getSelectedPlanMaster().getProcurementMode())
//				&& ProcurementPlanConst.FAVOURITE_TYPE_NONE.equals(this.getManagedBean().getSelectedPlan().getFavouriteOption())) 
//				|| ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(this.getManagedBean().getSelectedPlanMaster().getProcurementMode())){
//			
				showPpItem(null);
			
//		}

		System.out.println("item list size " + this.getManagedBean().getSelectedPlanItem().getPpPlanItems().size());
		
		
		log.info("addItemToList - End");
	}
	
	/**
	 * populate plan
	 * @param plan
	 */
	public void showPpPlan(PpPlan plan){
		try{
			log.info("showPpPlan - Start");
			if(plan==null){
				//set empty view for ppPlan
				this.getManagedBean().setSelectedPlanItem(new PpPlan());
				this.getManagedBean().getSelectedPlanItem().setBrandNew(true);
				
				//set empty view for ppPlanItem
				this.getManagedBean().setSelectedItem(new PpPlanItem());
				this.getManagedBean().setSelectedItemRemark(new PpRemark());
				this.getManagedBean().getSelectedPlanItem().setPpPlanItems(new ArrayList<PpPlanItem>());

			}else{
				if(ProcurementPlanConst.MODE_REVISION.equals(this.getManagedBean().getViewMode())){
					plan.setEditable(true);
				}else{
					plan.setEditable(false);
				}
				plan.setSelected(true);
				this.getManagedBean().setSelectedPlanItem(plan);
				//call populate allocated budget as gosodo
			}
			log.info("showPpPlan - End");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * add plan to the list (sub header)
	 */
	public void addPpPlanToList(){
		log.info("addPpPlanToList - Start");
		if(this.getManagedBean().getSelectedPlanItem().isBrandNew() && !this.getManagedBean().getSelectedPlanItem().isSelected()){
			this.getManagedBean().getSelectedPlanMaster().addPpPlans(this.getManagedBean().getSelectedPlanItem());
		}
		
		log.info("addPpPlanToList - End");
	}	
	
	/**
	 * delete plan from the list
	 * @param plan
	 */
	public void deletePpPlan(PpPlan plan){
		log.info("deletePpPlan - Start");
		if(plan!=null){
			if(!plan.isBrandNew()){
				plan.setDeleted(true);
				plan.setRecordStatus(false);
			}else{
				this.getManagedBean().getSelectedPlanMaster().getPpPlans().remove(plan);
			}
		}
		log.info("deletePpPlan - End");
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

	
	
	/**
	 * update the recurring frequency details by the recurring frequency entered
	 */
	public void changeRecurFreq(){
		log.info("changeRecurFreq - Start");
		int recurFreq = 0;
		int currentSize = 0;
		String favoriteOption = this.getManagedBean().getSelectedPlan().getFavouriteOption();
		
		if(ProcurementPlanConst.FAVOURITE_TYPE_PLAN.equals(favoriteOption)){
			recurFreq = this.getManagedBean().getSelectedPlan().getFavorPlanRecurFreq().getRecurFreq();
			currentSize = this.getManagedBean().getSelectedPlan().getFavorPlanRecurFreq().getPpRecurFreqDtls().size();
			if(recurFreq>currentSize){
				int newFreq = recurFreq - currentSize;
				for(int i=0; i<newFreq; i++){
					this.getManagedBean().getSelectedPlan().getFavorPlanRecurFreq().addPpRecurFreqDtls(new PpRecurFreqDtl());
				}
			}else if (recurFreq<currentSize){
				this.getManagedBean().getSelectedPlan().getFavorPlanRecurFreq().getPpRecurFreqDtls().remove(
						this.getManagedBean().getSelectedPlan().getFavorPlanRecurFreq().getPpRecurFreqDtls().size() - 1);
			}
		}else if(ProcurementPlanConst.FAVOURITE_TYPE_ITEM.equals(favoriteOption)){
			recurFreq = this.getManagedBean().getSelectedItem().getPpRecurFreq().getRecurFreq();
			currentSize = this.getManagedBean().getSelectedItem().getPpRecurFreq().getPpRecurFreqDtls().size();
			if(recurFreq>currentSize){
				int newFreq = recurFreq - currentSize;
				for(int i=0; i<newFreq; i++){
					this.getManagedBean().getSelectedItem().getPpRecurFreq().addPpRecurFreqDtls(new PpRecurFreqDtl());
				}
			}else if (recurFreq<currentSize){
				this.getManagedBean().getSelectedItem().getPpRecurFreq().getPpRecurFreqDtls().remove(
						this.getManagedBean().getSelectedItem().getPpRecurFreq().getPpRecurFreqDtls().size() - 1);
			}
		}
		log.info("changeRecurFreq - End");
		
	}
	
	/**
	 * load and prepare default view for favourite plan
	 */
	public void loadFavouritePlan(){
		log.info("loadFavouritePlan");
		if(this.getManagedBean().getSelectedPlanMaster().isBrandNew()){
			PpRecurFreq ppRecurFreq = new PpRecurFreq();
			ppRecurFreq.setRecurFreq(1);
			ppRecurFreq.setPpRecurFreqDtls(new ArrayList<PpRecurFreqDtl>());
			ppRecurFreq.addPpRecurFreqDtls(new PpRecurFreqDtl());
			this.getManagedBean().getSelectedPlanItem().setFavorPlanRecurFreq(ppRecurFreq);
		}else{
			//load for favourite plan 
			PpPlanItem item = this.getManagedBean().getSelectedPlan().getPpPlanItems().get(0);
			this.getManagedBean().setSelectedPlanItem(this.getManagedBean().getSelectedPlan());
			this.getManagedBean().getSelectedPlanItem().setFavorPlanRecurFreq(item.getPpRecurFreq());
		}
		
	}
	
	/**
	 * load and prepare default view for favourite item
	 */
	public void loadFavouriteItem(){
		log.info("loadFavouriteItem");
		if(!this.getManagedBean().getSelectedPlanMaster().isBrandNew()){
			//load for favourite item 
			this.getManagedBean().setSelectedPlanItem(this.getManagedBean().getSelectedPlan());
		}
	}
	
	
	
	public void calculateEndDate(Date startDate, Integer period){
		log.info("calculateEndDate - Start");

//		if(this.getManagedBean().getSelectedPlanItem().getPlanStartDate()!=null){
//			Calendar startDate = CommonMethod.convertDateToCalender(this.getManagedBean().getSelectedPlanItem().getPlanStartDate());
//			startDate.add(Calendar.DATE, this.getManagedBean().getSelectedPlanItem().getCtContract());
//			activity.setPlanEndDate(startDate.getTime());
//		}else{
//			this.getManagedBean().getSelectedPlanItem().setPlanStartDate(null);
//		}
		
		log.info("calculateEndDate - End");
	}

	
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
			
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("initialPmTaskActivities - End");
		return taskActivities;
	
	}
	/**
	 * calculate activity end date
	 * @param activity
	 */
	public void calculateActEndDate(PpQtActivity activity){
		log.info("calculateActEndDate - Start");
		if(activity!=null){
			if(activity.getPlanStartDate()!=null){
				Calendar startDate = CommonMethod.convertDateToCalender(activity.getPlanStartDate());
				startDate.add(Calendar.DATE, activity.getPmBusinessActivity().getSlaDay());
				activity.setPlanEndDate(startDate.getTime());
			}else{
				activity.setPlanEndDate(null);
			}
		}
		log.info("calculateActEndDate - End");
	}
	
	/**
	 * update local budget alloc dtl while GOSODO is selected
	 * @param item
	 * @param mode
	 */
	public void updateSelectedDo(PpPlanItem item, String mode){
		log.info("updateSelectedDo - Start");
		
		try{
			log.info("mapper keys " + this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().keySet());
			log.info("mapper values " +this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().values());
	
			long key = item.getPpBudgetAllocDtl().getBudgetAllocDtlId();
			if("CHANGE".equals(mode)){
				//change GO
				long oldKey = item.getOriPpBudgetAllocDtl().getBudgetAllocDtlId();
				System.out.println(	this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(oldKey) + "-" + item.getOriEstimatedAmount());
				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
						oldKey, 
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(oldKey), 
								item.getOriEstimatedAmount())
				);
				System.out.println(">>>>>>>>ori amount " + item.getOriEstimatedAmount());
				System.out.println(">>>>>>>>ori Budget alloc " + item.getOriPpBudgetAllocDtl().getBudgetAllocDtlId());
				System.out.println(	this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(key) + "+" + item.getEstimatedAmount());
				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
						key, 
						CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(key),
								item.getEstimatedAmount())
				);
				
				item.setOriEstimatedAmount(item.getEstimatedAmount());//update ori amount to new amount
				item.setOriPpBudgetAllocDtl(item.getPpBudgetAllocDtl());//update ori budget to new budget
				System.out.println(">>>>>>>>update ori amount " + item.getOriEstimatedAmount());
				System.out.println(">>>>>>>>update ori Budget alloc " + item.getOriPpBudgetAllocDtl().getBudgetAllocDtlId());
			}else if("REMOVE".equals(mode)){
				//remove item
				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
						key, 
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(key), 
								item.getEstimatedAmount())
				);
			}else if("ADD".equals(mode)){
				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
						key, 
						CommonMethod.addBigDecimalNullable(
							this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(key),
							item.getEstimatedAmount())
				);
				item.setOriEstimatedAmount(item.getEstimatedAmount());//initiate ori amount
				item.setOriPpBudgetAllocDtl(item.getPpBudgetAllocDtl());//initiate ori budget
			}else if("UPDATE".equals(mode)){
				//update item with same GO
				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
						key, 
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(key), 
								item.getOriEstimatedAmount())
				);
				System.out.println(">>>>>>>>ori RM " + item.getOriEstimatedAmount());
				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
						key, 
						CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(key),
								item.getEstimatedAmount())
				);
				item.setOriEstimatedAmount(item.getEstimatedAmount());//update ori amount to new amount
				System.out.println(">>>>>>>>update ori RM " + item.getOriEstimatedAmount());
			}else if("INIT".equals(mode)){
				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
						key, new BigDecimal(0));
				item.setOriEstimatedAmount(item.getEstimatedAmount());//initiate ori amount
				item.setOriPpBudgetAllocDtl(item.getPpBudgetAllocDtl());//initiate ori budget
			}
			log.info("mapper keys " + this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().keySet());
			log.info("mapper values " +this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().values());
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("updateSelectedDo - End");
	}

	/**
	 * retrieve department list from PM
	 * @return department list
	 */
	public List<PmOrgProfile> departmentOptions() {
		log.info("departmentOptions - Start");
		List<PmOrgProfile> options = new ArrayList<PmOrgProfile>();
		
		if(this.getManagedBean().getSelectedPlanMaster().getOrgProfile()!=null){
			Long orgProfileId = this.getManagedBean().getSelectedPlanMaster().getOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgProfileId();
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setParentOrgProfileId(orgProfileId);
			reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
			
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
	//		if(resDTO.isSuccessful()){
				options = resDTO.getOrgProfileList();
	//		}
		}
		log.info("departmentOptions - End");
		return options;
	}
	
	/**
	 * retrieve division list by department id from PM
	 * @param divisionId
	 * @return division list
	 */
	public List<PmOrgProfile> divisionOptions(Long departmentId) {
		log.info("divisionOptions - Start");
		List<PmOrgProfile> options = new ArrayList<PmOrgProfile>();

		if(CommonMethod.isSelected(departmentId)){
			OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
			reqDTO.setParentOrgProfileId(departmentId);
			reqDTO.setOrgType(OrganizationType.DIVISION.value());
			
			IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
			OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
	//		if(resDTO.isSuccessful()){
				options = resDTO.getOrgProfileList();
	//		}
		}
		log.info("divisionOptions - End");
		return options;
	}
	
	/**
	 * retrieve procurement mode options
	 * @return List<SelectItem>
	 */
	public List<SelectItem> procurementModeOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.PROCUREMENT_QUOTATION, "Quotation"));
		options.add(new SelectItem(ProcurementPlanConst.PROCUREMENT_DIRECT_NEGO, "Direct Negotiation"));
		options.add(new SelectItem(ProcurementPlanConst.PROCUREMENT_TENDER, "Tender"));
		options.add(new SelectItem(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT, "Existing Contract"));
		options.add(new SelectItem(ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE, "Direct Purchase"));
		return options;
	}
	
	/**
	 * retrieve fulfillment type options
	 * @return List<SelectItem>
	 */
	public List<SelectItem> fulfillmentTypeOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.FULFILLMENT_ONE_OFF, "One-Off"));
		options.add(new SelectItem(ProcurementPlanConst.FULFILLMENT_AS_AND_WHEN, "Periodic - As and When"));
		options.add(new SelectItem(ProcurementPlanConst.FULFILLMENT_SCHEDULE, "Periodic - Schedule"));
		return options;
	}
	
	/**
	 * retrieve favourite options
	 * @return List<SelectItem>
	 */
	public List<SelectItem> favouriteOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.FAVOURITE_TYPE_ITEM, "Item"));
		options.add(new SelectItem(ProcurementPlanConst.FAVOURITE_TYPE_PLAN, "Plan"));
		options.add(new SelectItem(ProcurementPlanConst.FAVOURITE_TYPE_NONE, "None"));
		return options;
	}

	/**
	 * retrieve qt type options
	 * @return List<SelectItem>
	 */
	public List<SelectItem> qtTypeOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.QT_TYPE_BUMIPUTERA, "Bumiputera"));
		options.add(new SelectItem(ProcurementPlanConst.QT_TYPE_OPEN, "Open"));
		options.add(new SelectItem(ProcurementPlanConst.QT_TYPE_PANEL, "Panel"));
		options.add(new SelectItem(ProcurementPlanConst.QT_TYPE_RESTRICTED, "Restricted"));
		options.add(new SelectItem(ProcurementPlanConst.QT_TYPE_INTERNATIONAL, "International"));
		return options;
	}
	
	/**
	 * retrieve item origin type options
	 * @return List<SelectItem>
	 */
	public List<SelectItem> itemOriginTypeOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.ITEM_ORIGIN_TYPE_IMPORTED, "Imported"));
		options.add(new SelectItem(ProcurementPlanConst.ITEM_ORIGIN_TYPE_LOCAL, "Local"));
		options.add(new SelectItem(ProcurementPlanConst.ITEM_ORIGIN_TYPE_BOTH, "Imported & Local"));
		return options;
	}
	
	/**
	 * retrieve verification level options
	 * @return List<SelectItem>
	 */
	public List<SelectItem> verificationLevelOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.LEVEL_SINGLE, "Single"));
		options.add(new SelectItem(ProcurementPlanConst.LEVEL_MULTIPLE, "Multiple"));
		return options;
	}
	
	/**
	 * retrieve contract indicator options
	 * @param fulfillmentType
	 * @return List<SelectItem>
	 */
	public List<SelectItem> contractIndOptions(String fulfillmentType) {
		List<SelectItem>options = new ArrayList<SelectItem>();
		if(!ProcurementPlanConst.FULFILLMENT_SCHEDULE.equals(fulfillmentType)){
			options.add(new SelectItem(ProcurementPlanConst.CONTRACT_IND_PKP, "PKP"));
		}
		options.add(new SelectItem(ProcurementPlanConst.CONTRACT_IND_LOA, "LOA"));
		options.add(new SelectItem(ProcurementPlanConst.CONTRACT_IND_CONTRACT, "Contract"));
		return options;
	}
	
	/**
	 * retrieve Contract Type options
	 * @return List<SelectItem>
	 */
	public List<SelectItem> contractTypeOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.CONTRACT_TYPE_MINISTRY, "Ministry"));
		options.add(new SelectItem(ProcurementPlanConst.CONTRACT_IND_CENTRAL, "Central"));
		return options;
	}
	
	/**
	 * retrieve all short month e.g Jul
	 * @return List<SelectItem>
	 */
	public List<SelectItem> shortMonths() {
		return CommonMethod.calendarMonths();
	}

	/**
	 * vot type options
	 * @return
	 */
	public List<SelectItem> votTypeOptions() {
		log.info("votTypeOptions");
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.VOT_BT, "B & T"));
		options.add(new SelectItem(ProcurementPlanConst.VOT_PS, "P & S"));
		options.add(new SelectItem(ProcurementPlanConst.VOT_WITHOUT, "Amanah Without VOT"));
		return options;
	}
	
	/**
	 * retrieve financial year options for current year & current year +1
	 * @return financial year options
	 */
	public List<SelectItem> financialYearOptions() {
		log.info("financialYearOptions - Start");
		return CommonMethod.financialYearOptions();
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

	
	/**
	 * retrieve and set unspsc object
	 */
	public void selectedProduct(){
		log.info("selectedProduct - Start");
		String code = this.getManagedBean().getSelectedItem().getUnspscClassCode();
		try{
			if(CommonMethod.isSelected(code)){
				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setUnspscCode(code);
				ICodificationSO reqSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
				CodificationResDTO resDTO = reqSO.searchUnspscObj(reqDTO);
				if(resDTO.isSuccessful()){
					this.getManagedBean().getSelectedItem().setCmUnspsc(resDTO.getUnspscObj());
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("selectedProduct - End");		
	}
	
	/**
	 * select parent unspsc object
	 */
	public CmUnspsc selectParentUnspsc(String code){
		log.info("selectParentUnspsc - Start");
	
		CmUnspsc parentUnspsc = new CmUnspsc();
		try{
			if(CommonMethod.isSelected(code)){
				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setParentCode(code);
				ICodificationSO reqSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
				CodificationResDTO resDTO = reqSO.searchParentObj(reqDTO);
				if(resDTO.isSuccessful()){
					parentUnspsc =resDTO.getParentObj();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("selectParentUnspsc - End");		
		return parentUnspsc;
	}

	
	/**
	 * retrieve distinct GO options
	 * @return List<PpBudget>
	 */
	public List<PpBudget> goOptions() {
		log.info("goOptions - Start");
		List<PpBudget> goBudgets = new ArrayList<PpBudget>();
		try{
			System.out.println("id " + this.getManagedBean().getCurrentUser().getOrgProfileId());
			BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
			reqDTO.setOrgProfileId(this.getManagedBean().getCurrentUser().getOrgProfileId());	
			IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
			BudgetAllocationResDTO resDTO = reqSO.retrieveDistinctGoList(reqDTO);
			
			if (resDTO.isSuccessful()) {
				goBudgets = resDTO.getGoBudgetList();
				System.out.println("size : " + goBudgets.size());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("goOptions - End");
		return goBudgets;
		
	}

	/**
	 * retrieve distinct SO options
	 * @return List<PmSo> 
	 */
	public List<PmSo> soOptions() {
		log.info("soOptions - Start");
		System.out.println("go "  +this.getManagedBean().getSelectedItem().getGoId());
		List<PmSo> soBudgets = new ArrayList<PmSo>();
		try{
			BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
			reqDTO.setOrgProfileId(this.getManagedBean().getCurrentUser().getOrgProfileId());	
			reqDTO.setGoBudgetId(this.getManagedBean().getSelectedItem().getGoId());
			IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
			BudgetAllocationResDTO resDTO = reqSO.retrieveDistinctSoList(reqDTO);
			
			if (resDTO.isSuccessful()) {
				soBudgets = resDTO.getSoList();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("soOptions - End");
		return soBudgets;
		
	}
	/**
	 * retrieve all DO options
	 * @return List<PpBudgetAllocDtl> 
	 */
	public List<PpBudgetAllocDtl> doOptions() {
		log.info("doOptions - Start");
		System.out.println("so "  +this.getManagedBean().getSelectedItem().getSoId());
		List<PpBudgetAllocDtl> doBudgets = new ArrayList<PpBudgetAllocDtl>();
		try{
			BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
			reqDTO.setOrgProfileId(this.getManagedBean().getCurrentUser().getOrgProfileId());	
			reqDTO.setGoBudgetId(this.getManagedBean().getSelectedItem().getGoId());
			reqDTO.setSoId(this.getManagedBean().getSelectedItem().getSoId());
			IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
			BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetDoList(reqDTO);
			
			if (resDTO.isSuccessful()) {
				doBudgets = resDTO.getDoList();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("doOptions - End");
		return doBudgets;
		
	}
	
	/**
	 * retrieve and set selected GOSODO
	 */
	public void selectedGoSoDo(){
		log.info("selectedGoSoDo - Start");
		log.info("id: " + this.getManagedBean().getSelectedItem().getPpBudgetAllocDtl().getBudgetAllocDtlId());
		try{
			BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
			reqDTO.setBudgetAllocDtlId(this.getManagedBean().getSelectedItem().getPpBudgetAllocDtl().getBudgetAllocDtlId());
			IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
			BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetAllocDtl(reqDTO);
			if (resDTO.isSuccessful()) {
				this.getManagedBean().getSelectedItem().setPpBudgetAllocDtl(resDTO.getBudgetAllocDtl());
				this.getManagedBean().getSelectedItem().setOvrFinancialYear(
						resDTO.getBudgetAllocDtl().getPpBudget().getFinancialYear());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("selectedGoSoDo - End");
		
	}
	

	/**
	 * retrieve extension item and supplier item object
	 */
	public void selectedExtensionItem(){
		log.info("selectedExtensionItem - Start");
		Long id = this.getManagedBean().getSelectedItem().getExtensionId();
		try{
			if(CommonMethod.isSelected(id)){
				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setItemId(id);
				ICodificationSO reqSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
				CodificationResDTO resDTO = reqSO.searchItemObj(reqDTO);
				if(resDTO.isSuccessful()){
					CmItem item = resDTO.getCmItem();
					this.getManagedBean().getSelectedItem().setCmSupplierItem(
							((item!=null && item.getCmSupplierItems()!=null) ? item.getCmSupplierItems().get(0) : new CmSupplierItem())
						);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("selectedExtensionItem - End");		
	}

	/**
	 * retrieve extension item list from CM
	 * @return List<CmItem> 
	 */
	public List<CmItem> extensionItemOptions(){
		log.info("extensionItemOptions - Start");
		Long id = this.getManagedBean().getSelectedItem().getUnspscCommodityId();
		System.out.println("id " + id);
		List<CmItem> options = new ArrayList<CmItem>();
		try{
			if(CommonMethod.isSelected(id)){
				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setUnspscId(id);
				ICodificationSO reqSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
				CodificationResDTO resDTO = reqSO.searchItemList(reqDTO);
				if(resDTO.isSuccessful()){
					options = resDTO.getItemList();
				}
				log.info("result size: " +options.size());
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("extensionItemOptions - End");
		return options;
	}
	
	/**
	 * retrieve unspsc commodity list from CM
	 * @return List<CmUnspsc> 
	 */
	public List<CmUnspsc> unspscCommodityOptions(){
		log.info("unspscCommodityOptions - Start");
		String code = this.getManagedBean().getSelectedItem().getUnspscClassCode();
		List<CmUnspsc> options = new ArrayList<CmUnspsc>();
		try{
			if(CommonMethod.isSelected(code)){
				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setParentCode(code);
				ICodificationSO reqSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
				CodificationResDTO resDTO = reqSO.searchCommodity(reqDTO);
				if(resDTO.isSuccessful()){
					options = resDTO.getCommodityList();
				}
				log.info("result size: " +options.size());
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("unspscCommodityOptions - End");
		return options;
	}
	

	/**
	 * retrieve unspsc class list from CM
	 * @return List<CmUnspsc> 
	 */
	public List<CmUnspsc> unspscClassOptions(){
		log.info("unspscClassOptions - Start");
		String code = this.getManagedBean().getSelectedItem().getUnspscFamilyCode();
		List<CmUnspsc> options = new ArrayList<CmUnspsc>();
		try{
			if(CommonMethod.isSelected(code)){
				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setParentCode(code);
				ICodificationSO reqSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
				CodificationResDTO resDTO = reqSO.searchClass(reqDTO);
				if(resDTO.isSuccessful()){
					options = resDTO.getClassList();
				}
				log.info("result size: " +options.size());
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("unspscClassOptions - End");
		return options;
	}
	
	/**
	 * retrieve unspsc family list from CM
	 * @return List<CmUnspsc> 
	 */
	public List<CmUnspsc> unspscFamilyOptions(){
		log.info("unspscFamilyOptions - Start");
		String code = this.getManagedBean().getSelectedItem().getUnspscSegmentCode();
		List<CmUnspsc> options = new ArrayList<CmUnspsc>();
		try{
			if(CommonMethod.isSelected(code)){
				CodificationReqDTO reqDTO = new CodificationReqDTO();
				reqDTO.setParentCode(code);
				ICodificationSO reqSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
				CodificationResDTO resDTO = reqSO.searchFamily(reqDTO);
				if(resDTO.isSuccessful()){
					options = resDTO.getFamilyList();
				}
				log.info("result size: " +options.size());
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("unspscFamilyOptions - End");
		return options;
	}
	
	/**
	 * retrieve unspsc segment list from CM
	 * @return List<CmUnspsc> 
	 */
	public List<CmUnspsc> unspscSegmentOptions(){
		log.info("unspscSegmentOptions - Start");
		List<CmUnspsc> options = new ArrayList<CmUnspsc>();
		try{
			CodificationReqDTO reqDTO = new CodificationReqDTO();
			ICodificationSO reqSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
			CodificationResDTO resDTO = reqSO.searchSegment(reqDTO);
			if(resDTO.isSuccessful()){
				options = resDTO.getSegmentList();
			}
			log.info("result size: " +options.size());
			
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("unspscSegmentOptions - End");
		return options;
	}
	
	/**
	 * retrieve rejection reason
	 * @return List<PmReason> 
	 */
	public List<PmReason> rejectionReasonOptions(){
		return reasonOptions(ProcurementPlanConst.REASON_REJECT);
	}

	/**
	 * retrieve reason by category
	 * @return List<PmReason> 
	 */
	public List<PmReason> reasonOptions(String category){
		log.info("reasonOptions - Start");
		List<PmReason> options = new ArrayList<PmReason>();
		try{
			ReasonReqDTO reqDTO = new ReasonReqDTO();
			reqDTO.setReasonCatrgory(category);
//			reqDTO.setOrgProfileId("PPR");
			IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject("pm", IReasonSO.class);
			ReasonResDTO resDTO = reqSO.retrieveReasonsListByCategory(reqDTO);
//			if(resDTO.isSuccessful()){
				options = resDTO.getReasonCategoryList();
//			}
			log.info("result size: " +options.size());
			
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("reasonOptions - End");
		return options;
	}


	
	/**
	 * retrieve org profile by login user and type
	 * @param userId
	 * @param type
	 * @return org profile
	 */
	private List <PmOrgProfile> getOrgProfiles(Long userId, String orgType){
		log.info("getOrgProfiles - Start");
		log.info("userId " + userId);
		List <PmOrgProfile> profileList =  new ArrayList<PmOrgProfile>();
		try{
			if(CommonMethod.isSelected(userId) && CommonMethod.isSelected(orgType)){
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setUserId(userId);
				reqDTO.setOrgType(orgType);
				
				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
//				if(resDTO.isSuccessful()){
					profileList = resDTO.getOrgProfileList();
					System.out.println("size..." + profileList.size());
//				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		log.info("getOrgProfiles - End");
		return profileList;
	}
	
	/**
	 * retrieve contract list by type
	 * @return List<CtContract> 
	 */
	public List<CtContract> contractOptions(Long contractType){
		log.info("contractOptions - Start");
		log.info("contractType id: "+contractType);
		List<CtContract> options = new ArrayList<CtContract>();
		try{
			if(CommonMethod.isSelected(contractType)){
				ContractReqDTO reqDTO = new ContractReqDTO();
				reqDTO.setContractTypeCodeId(contractType);//TODO
				IContractSO reqSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
				ContractResDTO resDTO = reqSO.retrieveContractByContractType(reqDTO);
				if(resDTO.isSuccessful()){
					options = resDTO.getCtContracts();
					for(CtContract contract : resDTO.getCtContracts()) {
						System.out.print(contract.getContractName()+" >> "+contract.getSmSupplier().getRegNo());
						if(contract.getContractValue()!=null)
							System.out.print(" $$## "+contract.getContractValue().getContractNo());
						System.out.println();
					}
				}
				log.info("result size: " +options.size());
			}
			
			
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("contractOptions - End");
		return options;
	}

	/**
	 * retrieve circular contract list circular id
	 * @param circularId
	 * @return  List<CtCircularContract>
	 */
	public List<CtCircularContract> cicularContractOptions(Long circularId){
		log.info("cicularContractOptions - Start");
		log.info("circular id: "+circularId);
		List<CtCircularContract> options = new ArrayList<CtCircularContract>();
		try{
			if(CommonMethod.isSelected(circularId)){
				CircularReqDTO reqDTO = new CircularReqDTO();
				reqDTO.setCircularId(circularId);
				ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject("ct", ICircularSO.class);
				CircularResDTO resDTO = circularSO.retrieveCircularContractByCircularId(reqDTO);
				if(resDTO.isSuccessful()){
					options = resDTO.getCircularContractList();
				}
				log.info("result size: " +options.size());
			}
			
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("cicularContractOptions - End");
		return options;
	}
	
	/**
	 * retrieve circular list
	 * @return List<CtCircular> 
	 */
	public List<CtCircular> circularOptions(){
		log.info("circularOptions - Start");
		List<CtCircular> options = new ArrayList<CtCircular>();
		try{
			CircularReqDTO reqDTO = new CircularReqDTO();
			reqDTO.setDocType(ContractConst.CIRCULAR_DOC_TYPE_PANEL);
			ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject("ct", ICircularSO.class);
			CircularResDTO resDTO = circularSO.retrieveCircularListByType(reqDTO);
			if(resDTO.isSuccessful()){
				options = resDTO.getCircularList();
			}
			log.info("result size: " +options.size());
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("circularOptions - End");
		return options;
	}
	/**
	 * retrieve maksud options
	 * @return List<PmMaksud>
	 */
	public List<PmMaksud> maksudCodeOptions(){
		log.info("maksudCodeOptions - Start");
		List<PmMaksud> options = new ArrayList<PmMaksud>();
		try{
			IMaksudSO reqSO = (IMaksudSO) BusinessDelegate.getInstance().getServiceObject("pm", IMaksudSO.class);
			MaksudResDTO resDTO = reqSO.retrieveMaksudList();
			if(resDTO.isSuccessful()){
				options = resDTO.getMaksudList();
			}
			log.info("result size: " +options.size());
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("maksudCodeOptions - End");
		return options;
	}
	
	/**
	 * retrieve PmActivity list by type
	 * @param type
	 * @return List<PmActivity>
	 */
	public List<PmActivity> activityOptions(String type){
		log.info("activityOptions - Start");
		List<PmActivity> options = new ArrayList<PmActivity>();
		try{
			if(CommonMethod.isSelected(type)){
				ActivityReqDTO reqDTO = new ActivityReqDTO();
				reqDTO.setActivityType(type);
				IActivitySO reqSO = (IActivitySO) BusinessDelegate.getInstance().getServiceObject("pm", IActivitySO.class);
				ActivityResDTO resDTO = reqSO.retrieveActivityListByType(reqDTO);
				if(resDTO.isSuccessful()){
					options = resDTO.getActivityTypeList();
				}
				log.info("result size: " +options.size());
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("activityOptions - End");
		return options;
	}
	
	/**
	 * retrieve user list by role
	 * @param roleCode
	 * @return List<PmUser>
	 */
	public List<PmUser> userWithRoleOptions(String roleCode){
		log.info("userWithRoleOptions - Start");
		List<PmUser> options = new ArrayList<PmUser>();
		try{
			if(CommonMethod.isSelected(roleCode)){
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setRoleCode(roleCode);
				IUserSO reqSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
				UserResDTO resDTO = reqSO.retrieveUserListByRole(reqDTO);
				if(resDTO.isSuccessful()){
					options = resDTO.getUserList();
				}
				log.info("result size: " +options.size());
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("userWithRoleOptions - End");
		return options;
	}
	
	public void test(){
		System.out.println("sssssssssssssss rain");
//		if(this.getManagedBean().getSelectedPlanItem().isBrandNew() && !this.getManagedBean().getSelectedPlanItem().isSelected()){
			for(PpPlanItem item: this.getManagedBean().getSelectedPlanItem().getPpPlanItems()){
				System.out.println("item rm: " + item.getEstimatedAmount());
				if(item.getCmSupplierItem()!=null){
					System.out.println("item sup : " + item.getCmSupplierItem().getSupplierItemId());
				}else{
					System.out.println("item sup : x");
				}
				if(item.getPpRecurFreq()!=null){
					System.out.println("item f : " + item.getPpRecurFreq().getPlanName() );
				}else{
					System.out.println("item f : x");
				}
				System.out.println("item : " + item.getPpBudgetAllocDtl().getBudgetAllocDtlId());
			}
//			this.getManagedBean().getPlanMaster().addPpPlans(this.getManagedBean().getSelectedPlanItem());
//		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * copy header data from mainPlan to newPlan
	 * @param mainPlan
	 * @param newPlan
	 */
	private PpPlan clonePlan(PpPlan sourcePlan, PpPlan desPlan){
		log.info("clonePlan - Start");
		if(sourcePlan!=null && desPlan!=null){
			log.info("clonePlan");
			desPlan.setPlanDesc(sourcePlan.getPlanDesc());
			desPlan.setStatusId(sourcePlan.getStatusId());
			desPlan.setSubmissionType(sourcePlan.getSubmissionType());
			desPlan.setFavouriteOption(sourcePlan.getFavouriteOption());
			desPlan.setPpPlanActors(new ArrayList<PpPlanActor>());
			for(PpPlanActor actor:sourcePlan.getPpPlanActors()){
				System.out.println("actor " + actor.getUserId());
				desPlan.addPpPlanActors(actor);
			}
		}
		log.info("clonePlan - end");
		return desPlan;
	}
	
	
	/**
	 * generate plan master with completed children
	 */
	private void getCompletePlanMaster(){
		
		log.info("getCompletePlanMaster - Start");
		try{
			
			test();
			
			
			//getting only selected task activities
			List <PpQtActivity> planActivities = this.getManagedBean().getPlan().getAllPpQtActivities();
			this.getManagedBean().getPlan().setPpQtActivities(new ArrayList<PpQtActivity>());
			for(PpQtActivity act:planActivities){
				if(act.getPlanStartDate()!=null){
					this.getManagedBean().getPlan().addPpQtActivities(act);
	
				}
			}
			
			//initialize actor list
			if(this.getManagedBean().getPlan().getPpPlanActors()==null){
				this.getManagedBean().getPlan().setPpPlanActors(new ArrayList<PpPlanActor>());
			}
			
			//getting approver
			this.getManagedBean().getPlan().addPpPlanActors(this.getManagedBean().getPlan().getApprover());
			
			//getting verification officer
			if(ProcurementPlanConst.SUBMIT_VERIFICATION.equals(this.getManagedBean().getPlan().getSubmissionType())){
				for(PpPlanActor pvo: this.getManagedBean().getPlan().getVerificationOfficers()){
					if(CommonMethod.isSelected(pvo.getUserId())){
						this.getManagedBean().getPlan().addPpPlanActors(pvo);
					}
				}
			}
			
			if(!CommonMethod.isSelected(this.getManagedBean().getPlanMaster().getMaksudId())){
				this.getManagedBean().getPlanMaster().setMaksudId(null);
			}
			
			//for EP
			if(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equals(this.getManagedBean().getPlanMaster().getPlanType())){
				//for FP/FI
				if(ProcurementPlanConst.FAVOURITE_TYPE_PLAN.equals(this.getManagedBean().getPlan().getFavouriteOption())
					|| ProcurementPlanConst.FAVOURITE_TYPE_ITEM.equals(this.getManagedBean().getPlan().getFavouriteOption())){

					if(this.getManagedBean().getSelectedPlanItem().isBrandNew() && !this.getManagedBean().getSelectedPlanItem().isSelected()){
						this.getManagedBean().getPlanMaster().addPpPlans(this.getManagedBean().getSelectedPlanItem());
					}
				}
				
				for(PpPlan epPlan:this.getManagedBean().getPlanMaster().getPpPlans()){
					epPlan = clonePlan(this.getManagedBean().getPlan(), epPlan);
				}

				//for favourite plan
				if(ProcurementPlanConst.FAVOURITE_TYPE_PLAN.equals(this.getManagedBean().getPlan().getFavouriteOption())){
					//clone favourite plan sub header to all item
					for(PpPlanItem item: this.getManagedBean().getSelectedPlanItem().getPpPlanItems()){
						item.setPpRecurFreq(new PpRecurFreq());
						item.getPpRecurFreq().setRecurFreq(this.getManagedBean().getSelectedPlanItem().getFavorPlanRecurFreq().getRecurFreq());
						item.getPpRecurFreq().setPlanName(this.getManagedBean().getSelectedPlanItem().getFavorPlanRecurFreq().getPlanName());
						item.getPpRecurFreq().setPpRecurFreqDtls(new ArrayList<PpRecurFreqDtl>());
						for(PpRecurFreqDtl recurDtl:this.getManagedBean().getSelectedPlanItem().getFavorPlanRecurFreq().getPpRecurFreqDtls()){
							item.getPpRecurFreq().addPpRecurFreqDtls(recurDtl);
						}
					}
				}else if(ProcurementPlanConst.FAVOURITE_TYPE_ITEM.equals(this.getManagedBean().getPlan().getFavouriteOption())){
					System.out.println(".................................Rain 3");
					
				}
				
	
			//for PP
			}else if(ProcurementPlanConst.PLAN_TYPE_PROCUREMENT.equals(this.getManagedBean().getSelectedPlanMaster().getPlanType())){
				//only one plan
				if(this.getManagedBean().getPlanMaster().isBrandNew()){
					this.getManagedBean().getPlanMaster().addPpPlans(this.getManagedBean().getPlan());
				}
				
				if(this.getManagedBean().isEnableMto()){
					List <PpQtActivity> mtoActivities = this.getManagedBean().getMtoPlan().getAllPpQtActivities();
					this.getManagedBean().getMtoPlan().setPpQtActivities(new ArrayList<PpQtActivity>());
					for(PpQtActivity act:mtoActivities){
						if(act.getPlanStartDate()!=null){
							this.getManagedBean().getMtoPlan().addPpQtActivities(act);
						}
					}
		
					//initialize actor list
					if(this.getManagedBean().getMtoPlan().getPpPlanActors()==null){
						this.getManagedBean().getMtoPlan().setPpPlanActors(new ArrayList<PpPlanActor>());
					}
					
					//getting approver
					this.getManagedBean().getMtoPlan().addPpPlanActors(this.getManagedBean().getMtoPlan().getApprover());
					
					//getting verification officer
					if(ProcurementPlanConst.SUBMIT_VERIFICATION.equals(this.getManagedBean().getMtoPlan().getSubmissionType())){
						for(PpPlanActor pvo: this.getManagedBean().getMtoPlan().getVerificationOfficers()){
							if(CommonMethod.isSelected(pvo.getPlanActorId())){
								this.getManagedBean().getMtoPlan().addPpPlanActors(pvo);
							}
						}
					}
					
					//only one plan
					if(this.getManagedBean().getMtoPlanMaster().isBrandNew()){
						this.getManagedBean().getMtoPlanMaster().addPpPlans(this.getManagedBean().getMtoPlan());
					}
					
					this.getManagedBean().getPlanMaster().setMtoPlanMaster(this.getManagedBean().getMtoPlanMaster());
				}
			}
	
			
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("getCompletePlanMaster - End");
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
		boolean brandNew = this.getManagedBean().getSelectedPlanMaster().isBrandNew();
		if (!brandNew) {
			getCompletePlanMaster();
			isSuccess = updatePlan(action);
		} else {
			getCompletePlanMaster();
//			test();
			isSuccess = addPlan(action);
			
		}
		if(isSuccess){
			returnPage = ProcurementPlanConst.NAV_TO_TASK_LIST;
		}
		log.info("isSuccess: " + isSuccess + ", returnPage: "+ returnPage);
		log.info("submitPlan - End");
		return returnPage;
	}


	/**
	 * update plan to DB
	 * @param action
	 * @return boolean
	 */
	public boolean updatePlan(String action){
		log.info("updatePlan - Start");
		boolean result  = false;
		try{
			ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
			if(BpmOutcomeConst.BPM_SUBMIT.equals(action)){
				//rollback revision flag
				PpPlan ppPlan = this.getManagedBean().getSelectedPlan();
				for(PpPlanItem item :ppPlan.getPpPlanItems()){
					item.setRevisionRequired(false);
				}
			}
			reqDTO.setAction(action);
			reqDTO.setWithMto(this.getManagedBean().isEnableMto());
			reqDTO.setTaskId(this.getManagedBean().getBpmTaskId());
			reqDTO.setPlanMaster(this.getManagedBean().getPlanMaster());
			reqDTO.setLoginUser(this.getManagedBean().getLoginUser());
			IProcurementPlanSO procurementPlanSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IProcurementPlanSO.class);
			ProcurementPlanResDTO resDTO = procurementPlanSO.updatePlan(reqDTO);
			
			result = resDTO.isSuccessful();
			log.info("updatePlan: " + result);
		} catch (Exception e) {
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("updateBudgetAllocation - End");
		log.info("updatePlan - End");
		return result;
	}
	
	/**
	 * save plan to DB
	 * @param action
	 * @return plan with ID
	 */
	public boolean addPlan(String action){
		log.info("addPlan - Start");
		boolean result  = false;
		try{
			ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
			reqDTO.setPlanMaster(this.getManagedBean().getPlanMaster());
			reqDTO.setWithMto(this.getManagedBean().isEnableMto());
			reqDTO.setAction(action);
			reqDTO.setLoginUser("roslan");
			IProcurementPlanSO procurementPlanSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IProcurementPlanSO.class);
			ProcurementPlanResDTO resDTO = procurementPlanSO.addPlan(reqDTO);
			if(resDTO.isSuccessful()){
				result = resDTO.isSuccessful();
				log.info("Generated ID... " + resDTO.getPlanMaster().getPlanMasterId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("addPlan - End");
		
		return result;
	}
	
	
	public void testValueChangeEvent(ValueChangeEvent event){
		log.info("enableCustomReason - Start");
		

		System.out.println("a" + event.getNewValue());
		PmOrgProfile profile = (PmOrgProfile)event.getNewValue();
		if(profile!=null){
			System.out.println("not null");
		}else{
			System.out.println("null");
			System.out.println(profile.getOrgCode());
		}
		System.out.println(profile!=null?profile.getOrgName():"xx");

		log.info("enableCustomReason - End");
	}

	public void testAjaxChangeEvent(AjaxBehaviorEvent event){
		log.info("handlerJabatan - Start");
		System.out.println("b" + event.getComponent().getAttributes().get("value"));	
		PmOrgProfile profile = (PmOrgProfile)event.getComponent().getAttributes().get("value");
		if(profile!=null){
			System.out.println("not null");
		}else{
			System.out.println("null");
			System.out.println(profile.getOrgCode());
		}
		System.out.println(profile!=null?profile.getOrgName():"xx");
		log.info("handlerJabatan - End");
	}

}
