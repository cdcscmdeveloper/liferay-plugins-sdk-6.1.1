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

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.pm.authorization.orgunit.IOrgUnitSO;
import my.ep.biz.service.pm.common.taskactivity.ITaskActivitySO;
import my.ep.biz.service.pp.budgetallocation.IBudgetAllocationSO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.cm.codification.CodificationReqDTO;
import my.ep.dto.cm.codification.CodificationResDTO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitReqDTO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitResDTO;
import my.ep.dto.pm.common.taskactivity.TaskResDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationReqDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationResDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.jaxb.pp.plandata.PPPlanDataType;
import my.ep.persistence.entity.cm.catalog.CmItem;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.pm.authorization.PmBusinessActivity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAlloc;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanActor;
import my.ep.persistence.entity.pp.procurementplan.PpPlanItem;
import my.ep.persistence.entity.pp.procurementplan.PpPlanMaster;
import my.ep.persistence.entity.pp.procurementplan.PpQtActivity;
import my.ep.persistence.logging.LogKey;
import my.ep.type.aut.SearchOperator;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
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
	private static Logger log = Logger.getLogger(PlanBackingBean.class);
	private final static Long USER_ID_PTJ= 4L; //TEMP
	
	private SearchFilterBean statusFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	private SearchFilterBean planNumberFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_LIKE.value());
	private SearchFilterBean createdDateFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_EQUALS.value());
	
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
		
		if(statusFilterBean!=null && statusFilterBean.getSearchValue()!=""){
			log.info("+ statusFilterBean");
			statusFilterBean.setSearchField("pmStatus.statusId");
			searchBean.getDataFilterBeans().add(statusFilterBean);
		}

		if(planNumberFilterBean!=null && planNumberFilterBean.getSearchValue()!=""){
			log.info("+ planNumberFilterBean");
			planNumberFilterBean.setSearchField("planNo");
			searchBean.getDataFilterBeans().add(planNumberFilterBean);
		}
		
		if(createdDateFilterBean!=null && createdDateFilterBean.getSearchValue()!=""){
			log.info("+ createdDateFilterBean");
			log.debug("+ createdDateFilterBean");
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
	 * load task from task list
	 * @param budgetAlloc
	 * @param taskName
	 */
	public void loadTask(PpPlan ppPlan, String taskName){
		log.info("loadTask - Start");
		if(ppPlan!=null && taskName!=null){
			log.info("plan id: " + ppPlan.getPlanId());
			if("Review Plan".equalsIgnoreCase(taskName)){
				log.info("Review Plan");
				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_APPROVAL);
				this.getManagedBean().setPlanMaster(ppPlan.getPpPlanMaster());
				this.getManagedBean().setPlan(ppPlan);
				this.getManagedBean().getPlan().setEditable(false); 
				System.out.println(">>1org >> " + ppPlan.getPpPlanMaster().getOrgProfileId());
				System.out.println(">>2org >> " + ppPlan.getPpPlanMaster().getOrgProfile());
				if(ppPlan.getPpPlanMaster().isMtoPlan()){
					this.getManagedBean().setMtoPlanMaster(ppPlan.getPpPlanMaster().getMtoPlanMaster());
					PpPlan mtoPlan = ppPlan.getPpPlanMaster().getMtoPlanMaster().getPpPlans().get(0);
					this.getManagedBean().setMtoPlan(mtoPlan);
				}
				//set the default current view obj
				this.getManagedBean().setSelectedPlanMaster(this.getManagedBean().getPlanMaster());
				this.getManagedBean().setSelectedPlan(this.getManagedBean().getPlan());
//				this.getManagedBean().setSelectedBudgetAllocItemRmk(new PpRemark()); //avoid null
//				this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl()); //avoid nul
			}else if("Revise Plan".equals(taskName)){
				log.info("Revise Plan");
				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_REVISION);
				this.getManagedBean().setPlanMaster(ppPlan.getPpPlanMaster());
				this.getManagedBean().setPlan(ppPlan);
				this.getManagedBean().getPlan().setEditable(true); 
//				this.getManagedBean().setSelectedBudgetAllocItemRmk(new PpRemark()); //avoid null
//				this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl()); //avoid nul
//				for(PpBudgetAllocDtl item:budgetAlloc.getPpBudgetAllocDtls()){
//					updateSelectedGo(item, "INIT");
//				}
			}
			//enable details view
			enableDetailTab(); 
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
		for(int i=1; i<=3; i++){
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
		if(!CommonMethod.isSelected(this.getManagedBean().getVotType())){
			this.getManagedBean().setVotType("BT"); //TODO temp set
		}
		
		
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
			
			//avoid null
			showPpItem(null);
		}
		log.info("initialMtoPlan - End");
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
		boolean result = true;
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
			result = false;
		}else{
			if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(procurementMode) &&
					CommonMethod.isSelected(fulfillmentType)){
				this.getManagedBean().setDynamicDetailsName(ProcurementPlanConst.EP_EXISTING_CONTRACT_DET);
				result = false;
			}else if(ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode) && 
					CommonMethod.isSelected(favoriteOption)){
				this.getManagedBean().setDynamicDetailsName(ProcurementPlanConst.EP_EXISTING_CONTRACT_DET); //TODO EP_DIRECT_PURCHASE_DET
				result = false;
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
		if(ProcurementPlanConst.CURRENT_ALLOC_BUDGET.equals(val.getTab().getId())){
			this.getManagedBean().setBudgetAllocDtlList(getAllBudgetAllocDtls(this.getManagedBean().getCurrentUser().getOrgProfileId()));
		}else if(ProcurementPlanConst.CURRENT_PLAN.equals(val.getTab().getId())){
		}else if(ProcurementPlanConst.CURRENT_AUDIT.equals(val.getTab().getId())){
			
			
		}else{
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
		if(!this.getManagedBean().getSelectedItemRemark().getRemark().isEmpty()){
			this.getManagedBean().getSelectedItem().addPpRemarks(this.getManagedBean().getSelectedItemRemark());
			System.out.println("id....."  + this.getManagedBean().getSelectedItemRemark().getStatusId());
		}
		//update total estimate amount
		this.getManagedBean().getSelectedPlan().setEstimatedAmount(
				CommonMethod.addBigDecimalNullable(this.getManagedBean().getSelectedPlan().getEstimatedAmount(),
						this.getManagedBean().getSelectedItem().getEstimatedAmount()
				));
				
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
				this.getManagedBean().setSelectedPlanItem(new PpPlan());
				this.getManagedBean().getSelectedPlanItem().setBrandNew(true);
				this.getManagedBean().getSelectedPlanItem().setCtCircular(new CtCircular());
				this.getManagedBean().getSelectedPlanItem().setCtContract(new CtContract());
				this.getManagedBean().getSelectedPlanItem().setPpPlanItems(new ArrayList<PpPlanItem>());
	//			this.getManagedBean().getSelectedPlanItem().setLoaId();
	//			this.getManagedBean().getSelectedPlanItem().setCmUnspsc(new CmUnspsc());
	//			this.getManagedBean().egetSelectedPlanItem().setPpBudgetAllocDtl(new PpBudgetAllocDtl());
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
			clonePlan(this.getManagedBean().getSelectedPlan(), this.getManagedBean().getSelectedPlanItem());
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
	 * copy header data from mainPlan to newPlan
	 * @param mainPlan
	 * @param newPlan
	 */
	private void clonePlan(PpPlan mainPlan, PpPlan newPlan){
		if(mainPlan!=null && newPlan!=null){
			newPlan.setPlanDesc(mainPlan.getPlanDesc());
			newPlan.setStatusId(mainPlan.getStatusId());
			newPlan.setEstimatedAmount(mainPlan.getEstimatedAmount());//todo
			newPlan.setSubmissionType(mainPlan.getSubmissionType());
			newPlan.setFavouriteOption(mainPlan.getFavouriteOption());
		}
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

	
	
	
	public void calculateEndDate(){
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
	
	public void updateSelectedDo(PpPlanItem item, String mode){
		log.info("updateSelectedDo - Start");
		
		try{
			log.info("mapper keys " + this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().keySet());
			log.info("mapper values " +this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().values());
	
			long key = item.getBudgetAllocDtlId();
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
				//add item
	//			if(this.getManagedBean().getSelectedGoMapper().get(key)==null){
	//				this.getManagedBean().getSelectedGoMapper().put(key, item.getAllocAmount());
	//			}else{
					this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
							key, 
							CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(key),
								item.getEstimatedAmount())
					);
				item.setOriEstimatedAmount(item.getEstimatedAmount());//initiate ori amount
				item.setOriPpBudgetAllocDtl(item.getPpBudgetAllocDtl());//initiate ori budget
	//			}
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
	 * retrieve division list from PM
	 * @return division list
	 */
	public List<PmOrgProfile> divisionOptions() {
		log.info("divisionOptions - Start");
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
		log.info("divisionOptions - End");
		return options;
	}
	
	/**
	 * retrieve department list by division id from PM
	 * @param divisionId
	 * @return department list
	 */
	public List<PmOrgProfile> departmentOptions(Long divisionId) {
		log.info("departmentOptions - Start");
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
		log.info("departmentOptions - End");
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
	
	public List<SelectItem> documentTypeOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.CONTRACT_IND_PKP, "PKP"));
		options.add(new SelectItem(ProcurementPlanConst.CONTRACT_IND_LOA, "LOA"));
		options.add(new SelectItem(ProcurementPlanConst.CONTRACT_IND_CONTRACT, "Contract"));
		return options;
	}
	
	public List<SelectItem> contractTypeOptions() {
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.CONTRACT_TYPE_MINISTRY, "Ministry"));
		options.add(new SelectItem(ProcurementPlanConst.CONTRACT_IND_CENTRAL, "Central"));
		return options;
	}
	
	
	//TEMP VOT 
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
	 * retrieve and set budget alloc dtl
	 */
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
					this.getManagedBean().getSelectedItem().setOvrFinancialYear(resDTO.getBudgetAllocDtl().getPpBudget().getFinancialYear());
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("selectedDo - End");		
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
				CodificationResDTO resDTO = reqSO.searchClassObj(reqDTO);
				if(resDTO.isSuccessful()){
					this.getManagedBean().getSelectedItem().setCmUnspsc(resDTO.getClassObj());
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("selectedProduct - End");		
	}
	

	/**
	 * populate GO list
	 * @return GO list
	 */
	public List<PpBudgetAllocDtl> goOptions() {
		log.info("goOptions");	
		return getAllBudgetAllocDtls(this.getManagedBean().getCurrentUser().getOrgProfileId());
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
	
	/**
	 * retrieve unspsc class from CM
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
	 * retrieve unspsc family from CM
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
	 * retrieve unspsc segment from CM
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
		List <PpQtActivity> planActivities = this.getManagedBean().getPlan().getAllPpQtActivities();
		for(PpQtActivity act:planActivities){
			if(act.isBrandNew() && act.getPlanStartDate()!=null){
				this.getManagedBean().getPlan().addPpQtActivities(act);
			}
		}
		
		//initialize actor list
		this.getManagedBean().getSelectedPlan().setPpPlanActors(new ArrayList<PpPlanActor>());
		//getting approver
		this.getManagedBean().getSelectedPlan().addPpPlanActors(this.getManagedBean().getSelectedPlan().getApprover());
		
		//getting verification officer
		if(this.getManagedBean().getSelectedPlan().isSubmissionType()){
			for(PpPlanActor pvo: this.getManagedBean().getSelectedPlan().getVerificationOfficers()){
				if(CommonMethod.isSelected(pvo.getPlanActorId())){
					this.getManagedBean().getSelectedPlan().addPpPlanActors(pvo);
				}
			}
		}
		
		//only one plan
		this.getManagedBean().getPlanMaster().addPpPlans(this.getManagedBean().getSelectedPlan());
		
		
		
		if(this.getManagedBean().isEnableMto()){
			List <PpQtActivity> mtoActivities = this.getManagedBean().getMtoPlan().getAllPpQtActivities();
			for(PpQtActivity act:mtoActivities){
				if(act.isBrandNew() && act.getPlanStartDate()!=null){
					this.getManagedBean().getMtoPlan().addPpQtActivities(act);
				}
			}

			//only one plan
			this.getManagedBean().getMtoPlanMaster().addPpPlans(this.getManagedBean().getMtoPlan());
			
			this.getManagedBean().getPlanMaster().setMtoPlanMaster(this.getManagedBean().getMtoPlanMaster());
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
		boolean brandNew = this.getManagedBean().getSelectedPlanMaster().isBrandNew();
		if (!brandNew) {
			isSuccess = updatePlan(action);
		} else {
			getCompletePlanMaster();
			isSuccess = addPlan(action);
		}
		if(isSuccess){
			returnPage = redirectToListPlan();
		}
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

}
