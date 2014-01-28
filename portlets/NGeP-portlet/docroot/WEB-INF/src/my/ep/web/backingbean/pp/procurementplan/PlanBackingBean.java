/*
 * Copyright © 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pp.procurementplan;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBetweenValue;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.ct.IContractSO;
import my.ep.biz.service.ct.circular.ICircularSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.taskactivity.ITaskActivitySO;
import my.ep.biz.service.pp.procurementplan.IProcurementPlanSO;
import my.ep.constant.ct.ContractConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
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
import my.ep.dto.pm.common.parameter.ParameterReqDTO;
import my.ep.dto.pm.common.parameter.ParameterResDTO;
import my.ep.dto.pm.common.taskactivity.TaskResDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanReqDTO;
import my.ep.dto.pp.procurementplan.ProcurementPlanResDTO;
import my.ep.persistence.entity.cm.catalog.CmUnspsc;
import my.ep.persistence.entity.ct.CtContract;
import my.ep.persistence.entity.ct.circular.CtCircular;
import my.ep.persistence.entity.ct.circular.cc.CtCircularContract;
import my.ep.persistence.entity.pm.authorization.PmBusinessActivity;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.persistence.entity.pp.procurementplan.PpPlanActivity;
import my.ep.persistence.entity.pp.procurementplan.PpPlanDtl;
import my.ep.persistence.entity.pp.procurementplan.PpPlanItem;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pp.ProcurementPlanSearchBean;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.pp.procurementplan.PlanManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.CommonMethod;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

@ManagedBean(name="planBackingBean")
@ViewScoped
public class PlanBackingBean extends BaseBackingBean<PlanManagedBean>{
	private static Logger log = Logger.getLogger(PlanBackingBean.class);


	private IPagingDataModel<PpPlan> planDataModel = new PlanPagingDataModel();
	private IPagingDataModel<PpBudgetAllocDtl> budgetAllocDtlDataModel = new BudgetAllocPagingDataModel();

	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * constructor of the backingbean
	 */
	public PlanBackingBean() {
		log.info("PlanBackingBean - Start");
		if(this.getManagedBean().getViewMode()==null || ProcurementPlanConst.MODE_LIST.equals(this.getManagedBean().getViewMode())){
			if(this.getManagedBean().getCurrentProfiles()==null
					&& getFacesScopes().getUserSessionBean()!=null){
				//set current user profile (login user must be PTJ)
				this.getManagedBean().setCurrentProfiles(CommonMethod.getOrgProfiles(getFacesScopes().getUserSessionBean().getUserId(), OrganizationType.DIVISION.value()));
			}	
		}
		log.info("PlanBackingBean - End");
	}
	
	
	/**
	 * get search bean 
	 * @return ISearchBean<PpPlan>
	 */
	private ISearchBean<PpPlan> getSearchBean(ISearchBean<PpPlan> searchBean){
		log.info("getSearchBean - Start");
		ProcurementPlanSearchBean ppSearchBean = this.getManagedBean().getPpSearchPlan();
		
		//filter by profile
//		SearchFilterBean defaultBean = new SearchFilterBean();
//		defaultBean.setSearchField("ptjOrgProfile");
//		defaultBean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
//		defaultBean.setSearchValue(this.getManagedBean().getCurrentProfiles());
//		searchBean.getDataFilterBeans().add(defaultBean);
		
//		
//			if(ppSearchBean.getStatus()!=null && CommonMethod.isSelected(ppSearchBean.getStatus())){
//				log.info("+ status " + ppSearchBean.getStatus());
//				SearchFilterBean bean = new SearchFilterBean();
//				bean.setSearchField("pmStatus.statusId");
//				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
//				bean.setSearchValue(ppSearchBean.getStatus());
//				searchBean.getDataFilterBeans().add(bean);
//			}
			if(ppSearchBean.getPlanNo()!=null && CommonMethod.isSelected(ppSearchBean.getPlanNo())){
				log.info("+ planNo " + ppSearchBean.getPlanNo());
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("planNo");
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(ppSearchBean.getPlanNo());
				searchBean.getDataFilterBeans().add(bean);
			}
			if(ppSearchBean.getPlanDesc()!=null && CommonMethod.isSelected(ppSearchBean.getPlanDesc())){
				log.info("+ getPlanDesc " + ppSearchBean.getPlanDesc());
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("planDesc");
				bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
				bean.setSearchValue(ppSearchBean.getPlanDesc());
				searchBean.getDataFilterBeans().add(bean);
			}
			if(ppSearchBean.getPlanType()!=null && CommonMethod.isSelected(ppSearchBean.getPlanType())){
				log.info("+ getPlanType " + ppSearchBean.getPlanType());
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("planTypeParam.parameterCode");
				bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
				bean.setSearchValue(ppSearchBean.getPlanType());
				searchBean.getDataFilterBeans().add(bean);
			}
			if(ppSearchBean.getPlanType()!=null && CommonMethod.isSelected(ppSearchBean.getPlanType())){
				if(ppSearchBean.getProcurementMode()!=null && CommonMethod.isSelected(ppSearchBean.getProcurementMode())){
					log.info("+ getProcurementMode " + ppSearchBean.getProcurementMode());
					SearchFilterBean bean = new SearchFilterBean();
					bean.setSearchField("procurementModeParam.parameterCode");
					bean.setSearchOperator(SearchOperator.OPERATOR_EQUALS.value());
					bean.setSearchValue(ppSearchBean.getProcurementMode());
					searchBean.getDataFilterBeans().add(bean);
				}
			}
			if(ppSearchBean.getFromDate()!=null && ppSearchBean.getToDate()!=null){
				log.info("+ createDate " + ppSearchBean.getFromDate() + " - " + ppSearchBean.getToDate());
				SearchFilterBean bean = new SearchFilterBean();
				bean.setSearchField("createdDate");
				bean.setSearchOperator(SearchOperator.OPERATOR_BETWEEN.value());
				SearchBetweenValue<Date> betweenVal = new SearchBetweenValue<Date>(
						ppSearchBean.getFromDate(), ppSearchBean.getToDate());
				bean.setSearchValue(betweenVal);
				searchBean.getDataFilterBeans().add(bean);
			}
		

		log.info("getSearchBean - End");
		return searchBean;
	}
	/**
	 * search plan
	 */
	public void searchPlan(){
		log.info("planSearch - Start");
		planDataModel.reloadData();
		log.info("planSearch - End");
		
	}
	
	/**
	 * Statuses options type options
	 * @return SelectItem[]
	 */
	public SelectItem[] statusDropdownValues(){
		return getStatusDropdownValues(ProcurementPlanConst.MODULE, ParameterConst.DOCTYPE_PLAN);
	}
	
	
//	/**
//	 * load task from task list
//	 * @param budgetAlloc
//	 * @param taskName
//	 */
//	public void loadTask(PpPlan ppPlan, String taskName){
//		log.info("loadTask - Start");
//		
//		try{
//			if(ppPlan!=null){
//				
//				log.info("plan id: " + ppPlan.getPlanId());
//	
//				//initialize task activities
//				List <PmBusinessActivity>taskActivities = initialPmTaskActivities();
//				//initial QT task activities list
//				ppPlan.setAllPpQtActivities(ppPlan.getPpQtActivities());
//				//merge existing task activities to all activities
//				for(PmBusinessActivity task: taskActivities){
//					PpPlanActivity tempAct = new PpPlanActivity();
//					tempAct.setPmBusinessActivity(task);
//					if(!ppPlan.getAllPpQtActivities().contains(tempAct)){
//						PpPlanActivity activity = new PpPlanActivity();
//						activity.setPmBusinessActivity(task);
//						activity.setBusinessActivityId(task.getBusinessActivityId());
//						ppPlan.getAllPpQtActivities().add(activity);
//					}
//				}
//	
//				Collections.sort(ppPlan.getAllPpQtActivities());
//				
//				ppPlan.setVerificationOfficers(new ArrayList<PpPlanActor>());
//				
//				//retrieve and set approver and verification officer
//				for(PpPlanActor actor: ppPlan.getPpPlanActors()){
//					if(ProcurementPlanConst.SUBMIT_VERIFICATION.equals(actor.getActorType())){
//						ppPlan.addVerificationOfficers(actor);
//					}else if(ProcurementPlanConst.SUBMIT_APPROVAL.equals(actor.getActorType())){
//						ppPlan.setApprover(actor);
//					}
//				}
//				
//				//set default PVO level
//				int numPvo = ppPlan.getVerificationOfficers()!=null ? (ppPlan.getVerificationOfficers().size() + 1) : 0;
//				for(int i=numPvo; i<=ProcurementPlanConst.PVO_LEVEL; i++){
//					PpPlanActor vo = new PpPlanActor();
//					vo.setActorSeq(i);
//					vo.setActorType(ProcurementPlanConst.SUBMIT_VERIFICATION);
//					ppPlan.addVerificationOfficers(vo);
//				}
//				
//				initialPlanRemark(ppPlan);
//				
//				//load rejection reasons (if any)
//				if(ppPlan.getPpRejectReasons()!=null 
//						&& !ppPlan.getPpRejectReasons().isEmpty()){
//					
//					ppPlan.setPmRejectReasonIds(new ArrayList<Long>());
//					for(PpRejectReason reason:ppPlan.getPpRejectReasons()){
//						ppPlan.getPmRejectReasonIds().add(reason.getReasonId());
//					}
//				}
//
//	
//				//set the default plan
//				this.getManagedBean().setPlanMaster(ppPlan.getPpPlanMaster());
//				this.getManagedBean().setPlan(ppPlan);
//				
//				if(ProcurementPlanConst.BPM_TASK_PLAN_REVIEW.equalsIgnoreCase(taskName)){
//					log.info("Review Plan");
//					//Review Plan
//					this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_APPROVAL);
//					this.getManagedBean().getPlan().setEditable(true); 
//					this.getManagedBean().getPlanMaster().setPpPlans(new ArrayList<PpPlan>());
//					this.getManagedBean().getPlanMaster().addPpPlans(this.getManagedBean().getPlan());
//				}else if(ProcurementPlanConst.BPM_TASK_PLAN_REVISE.equals(taskName)){
//					log.info("Revise Plan");
//					//Revise Plan
//					this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_REVISION);
//					this.getManagedBean().getPlan().setEditable(true); 
//					for(PpPlanDtl item:ppPlan.getPpPlanItems()){
//						updateSelectedDo(item, "INIT");
//					}
//					this.getManagedBean().getPlanMaster().setPpPlans(new ArrayList<PpPlan>());
//					this.getManagedBean().getPlanMaster().addPpPlans(this.getManagedBean().getPlan());
//				}else if(ProcurementPlanConst.BPM_TASK_PLAN_VERIFY.equals(taskName)){
//					log.info("Verify Plan");
//					//Verify Plan
//					this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_VERIFICATION);
//					this.getManagedBean().getPlan().setEditable(true); 
//					this.getManagedBean().getPlanMaster().setPpPlans(new ArrayList<PpPlan>());
//					this.getManagedBean().getPlanMaster().addPpPlans(this.getManagedBean().getPlan());
//				}else{
//					log.info("Other Mode " + taskName);
//					if(ProcurementPlanConst.MODE_DRAFT.equals(taskName)){
//						log.info("Draft Plan");
//						//Draft Mode
//						this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_DRAFT);
//						this.getManagedBean().getPlan().setEditable(true); 
//						this.getManagedBean().setActions(ProcurementPlanConst.BPM_DEFAULT_ACTIONS);
//					}else{
//						log.info("View Plan");
//						//View Mode
//						this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_VIEW);
//						this.getManagedBean().setActions(null);
//						this.getManagedBean().getPlan().setEditable(false); 
//						this.getManagedBean().getPlanMaster().setPpPlans(new ArrayList<PpPlan>());
//						this.getManagedBean().getPlanMaster().addPpPlans(this.getManagedBean().getPlan());
//					}
//					
//				}
//
//				//set the default current view obj
//				this.getManagedBean().setSelectedPlanMaster(this.getManagedBean().getPlanMaster());
//				this.getManagedBean().setSelectedPlan(this.getManagedBean().getPlan());
//				//set empty view for ppPlan
////				this.getManagedBean().setSelectedPlanItem(new PpPlan());
//				this.getManagedBean().setSelectedItem(new PpPlanDtl());
//	
//				
//				//enable ep/pp fields, details, mto
//				enableDetailTab(); 
//			}
//			
//		}catch(Exception e){
//			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Load plan failed."));
//			e.printStackTrace();
//		}
//		log.info("loadTask - End");
//	}
//	
	/**
	 * redirect to plan details view from ui
	 * @param PpPlan
	 * @return xhtml
	 */
	public String redirectToPlanContent(Long planId){
		log.info("redirectToPlanContent - Start");
		boolean isComplete = false;
		String taskName ="";
		try{
			if(planId!=null && planId>0L){
//				PpPlan ppPlan = retrievePlanById(planId);
//				if(ProcurementPlanConst.MODE_LIST.equals(this.getManagedBean().getViewMode())){
//					taskName = ppPlan.isDraft() ? ProcurementPlanConst.MODE_DRAFT : ProcurementPlanConst.MODE_VIEW;
//				}else{
//					taskName = ProcurementPlanConst.MODE_VIEW;
//				}
//				loadTask(ppPlan, taskName);
//				isComplete = true;
			}else{
				cleanPlanDetails();
				isComplete = true;
			}
			
		}catch (Exception e){
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Load plan failed."));
			e.printStackTrace();
			
		}
		log.info("redirectToPlanContent - End");
		return isComplete ? ProcurementPlanConst.NAV_TO_PLAN_DET : "";
	}


	
	/**
	 * initial plan details clean view
	 */
	private void cleanPlanDetails() throws Exception{
		log.info("cleanPlanDetails - Start");
		
		
		PpPlan plan = new PpPlan();
//		plan.setVotTypeParam(new PmParameter());
//		plan.setPlanTypeParam(new PmParameter());
//		plan.setProcurementModeParam(new PmParameter());
//		plan.setQtTypeParam(new PmParameter());
//		plan.setItemOriginParam(new PmParameter());
//		plan.setCmUnspscFamily(new CmUnspsc());
		plan.getCmUnspscFamily().setCmUnspsc(new CmUnspsc());//parent segment
		plan.setEditable(true);
		generateVerificationOfficers();
		
		
		
//		planMaster.setPpPlans(new ArrayList<PpPlan>());
//		planMaster.getSelectedGoMapper().clear();
//		plan.setEditable(true);
//		plan.setStatusId(ProcurementPlanConst.STATUS_PPL_PENDING_SUBMIT);
//		plan.setPpRemarks(new ArrayList<PpRemark>());
//		plan.setPpQtActivities(new ArrayList<PpPlanActivity>());
//		
//		
//		//Set Profile Tree
//		planMaster.setOrgProfile(this.getManagedBean().getCurrentUser());
//		planMaster.setOrgProfileId(this.getManagedBean().getCurrentUser().getOrgProfileId());
//		
//		//Plan Approver
//		PpPlanActor approver = new PpPlanActor();
//		approver.setActorType("A");
//		approver.setActorSeq(1);
//		plan.setApprover(approver);
//		
//		
//		//PVO
//		plan.setVerificationOfficers(new ArrayList<PpPlanActor>());
//		for(int i=1; i<=ProcurementPlanConst.PVO_LEVEL; i++){
//			PpPlanActor vo = new PpPlanActor();
//			vo.setActorSeq(i);
//			vo.setActorType("V");
//			plan.addVerificationOfficers(vo);
//		}
//
//		//Default values
//		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
//		this.getManagedBean().setCurrent(ProcurementPlanConst.CURRENT_NEW);
//		this.getManagedBean().setActions(ProcurementPlanConst.BPM_DEFAULT_ACTIONS);
//		this.getManagedBean().setEnableDetail(false);
//		this.getManagedBean().setEnableChildDetail(false);
//		this.getManagedBean().setSelectedPlanItem(new PpPlan()); //TODO
//	
		
		//Set normal plan
		
	
		
//		//initialize task activities
//		List <PmBusinessActivity>taskActivities = initialPmTaskActivities();
//		
//		//initial QT activities list
//		if(this.getManagedBean().getPlan().getAllPpQtActivities()==null){
//			this.getManagedBean().getPlan().setAllPpQtActivities(new ArrayList<PpPlanActivity>());
//		}
//		
//		for(PmBusinessActivity task: taskActivities){
//			PpPlanActivity activity = new PpPlanActivity();
//			activity.setPmBusinessActivity(task);
//			activity.setBusinessActivityId(task.getBusinessActivityId());
//			this.getManagedBean().getPlan().getAllPpQtActivities().add(activity);
//		}
//
//		
		//Default view
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		this.getManagedBean().setOutComes(ProcurementPlanConst.BPM_DEFAULT_ACTIONS);
		this.getManagedBean().setSelectedPlan(plan);
		initialPlanRemark(plan);
		
		log.info("cleanPlanDetails - End");
	}
//
//	
//	/**
//	 * update view for plan type
//	 * @param reset
//	 */
//	public void changePlanType(boolean reset){
//		log.info("changePlanType - Start");
//		if(CommonMethod.isSelected(this.getManagedBean().getSelectedPlanMaster().getPlanType())){
//			String planType = this.getManagedBean().getSelectedPlanMaster().getPlanType();
//			cleanPlanDetails();	
//			this.getManagedBean().getSelectedPlanMaster().setPlanType(planType);
//			if(ProcurementPlanConst.PLAN_TYPE_PROCUREMENT.equals(planType)){
//				this.getManagedBean().getSelectedPlan().setCmUnspscFamily(new CmUnspsc());
//			}
//		}
//		enableDetailTab(); //reflect to details tab
//		log.info("plan type: " +this.getManagedBean().getSelectedPlanMaster().getPlanType());
//		log.info("changePlanType - End");
//    }
//	
//		
//	/**
//	 * update view for procurement mode 
//	 * @param reset
//	 */
//	public void changeProcurementMode(){
//		log.info("changeProcurementMode - Start");
//		if(CommonMethod.isSelected(this.getManagedBean().getSelectedPlanMaster().getPlanType())){
//			if(CommonMethod.isSelected(this.getManagedBean().getSelectedPlanMaster().getProcurementMode())){
//				String procurementMode = this.getManagedBean().getSelectedPlanMaster().getProcurementMode();
//				if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(procurementMode) 
//						|| ProcurementPlanConst.PROCUREMENT_DIRECT_PURCHASE.equals(procurementMode)){
//					cleanPlanDetails();	
//					this.getManagedBean().getSelectedPlanMaster().setPlanType(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE);
//					this.getManagedBean().getSelectedPlanMaster().setProcurementMode(procurementMode);
//				}
//			}
//		}
//		
//		enableDetailTab(); //reflect to details tab
//		log.info("procurement mode:" + this.getManagedBean().getSelectedPlanMaster().getProcurementMode());
//		log.info("plan type: " +this.getManagedBean().getSelectedPlanMaster().getPlanType());
//		log.info("changeProcurementMode - End");
//    }
//	
	
	/**
	 * determine availability of details tab
	 * @return result
	 */
	public boolean enableDetailTab(){
		log.info("enableDetailTab - Start");
		boolean enableTab = false;
		int selectedTab = 0;
	
		String planType = this.getManagedBean().getSelectedPlan().getPlanTypeParam().getParameterCode();
		String procurementMode = this.getManagedBean().getSelectedPlan().getProcurementModeParam().getParameterCode();
		log.info("planType " + planType);
		log.info("procurementMode " + procurementMode);
		if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(planType)){
			selectedTab = ProcurementPlanConst.TAB_PROCUREMENT;
			enableTab = true;
			generateProcurementPlan();
		}else if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(planType)){
			if(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE.equals(procurementMode)){	
				selectedTab = ProcurementPlanConst.TAB_DIRECT_PURCHASE;
				enableTab = true;
				generateDirectPurchase();
			}else if(ParameterConst.PROCUREMENT_MODE_CONTRACT.equals(procurementMode)){
				selectedTab = ProcurementPlanConst.TAB_CONTRACT;
				enableTab = true;
				generateContract();
			}		
		}
		this.getManagedBean().setSelectedTab(selectedTab);
		this.getManagedBean().setEnableTab(enableTab);
		log.info("enableTab " + enableTab);
		log.info("selectedTab " + selectedTab);
		log.info("enableDetailTab - End");
		return enableTab;
	}
	
	private void generateDirectPurchase(){
		log.info("generateDirectPurchase - Start");
		if(this.getManagedBean().getSelectedPlan().isBrandNew()){
//			this.getManagedBean().getSelectedPlan().setCmUnspscClass(new CmUnspsc());
			this.getManagedBean().setSelectedPlanActivity(new PpPlanActivity());
			this.getManagedBean().getSelectedPlanActivity().setEditable(true);

		}else{
			
		}
		log.info("generateDirectPurchase - End");
		
	}
	
	private void generateProcurementPlan(){
		log.info("generateProcurementPlan - Start");
		if(this.getManagedBean().getSelectedPlan().isBrandNew()){
			//initial QT activities list
			if(this.getManagedBean().getSelectedPlan().getAllPpPlanActivities()==null){
				this.getManagedBean().getSelectedPlan().setAllPpPlanActivities(new ArrayList<PpPlanActivity>());
				initialPmTaskActivities();
			}
		}else{
			
		}
		log.info("generateProcurementPlan - End");
		
	}
	private void generateContract(){
		log.info("generateContract - Start");
		if(this.getManagedBean().getSelectedPlan().isBrandNew()){
//			this.getManagedBean().getSelectedPlan().setDocTypeParam(new PmParameter());
//			this.getManagedBean().getSelectedPlan().setCtCircular(new CtCircular());
//			this.getManagedBean().getSelectedPlan().setCtContract(new CtContract());
//			this.getManagedBean().getSelectedPlan().setScLoa(new ScLoa());
		}
		log.info("generateContract - End");
	}
	
	
	public void addCommodity(){
		//Initial item list if empty
		PpPlanItem item = new PpPlanItem();
		item.setEditable(true);
		item.setCmUnspsc(new CmUnspsc());
		if(this.getManagedBean().getSelectedPlanActivity().getPpPlanItems()==null){
			this.getManagedBean().getSelectedPlanActivity().setPpPlanItems(new ArrayList<PpPlanItem>());
		}
		this.getManagedBean().getSelectedPlanActivity().addPpPlanItems(item);
	}
	
	public void deleteCommodity(PpPlanItem ppPlanItem){
		log.info("deleteCommodity - Start");
		if(ppPlanItem!=null){
			if(!ppPlanItem.isBrandNew()&& !ppPlanItem.isDeleted()){
				ppPlanItem.setDeleted(true);
				ppPlanItem.setRecordStatus(false);
			}else{
				this.getManagedBean().getSelectedPlanActivity().getPpPlanItems().remove(ppPlanItem);
			}
			this.getManagedBean().setSelectedPlanItem(null);
		}
		log.info("deleteCommodity - End");
	}
	
	public void addItem(){
		//Initial item list if empty
		PpPlanDtl detail = new PpPlanDtl();
		detail.setEditable(true);
		
		if(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE.equals(
				this.getManagedBean().getSelectedPlan().getProcurementModeParam().getParameterCode())){
			
			if(this.getManagedBean().getSelectedPlanItem().getPpPlanDtls()==null){
				this.getManagedBean().getSelectedPlanItem().setPpPlanDtls(new ArrayList<PpPlanDtl>());
			}
			this.getManagedBean().getSelectedPlanItem().addPpPlanDtls(detail);
		}else{

			if(this.getManagedBean().getSelectedPlan().getPpPlanDtls()==null){
				this.getManagedBean().getSelectedPlan().setPpPlanDtls(new ArrayList<PpPlanDtl>());
			}
			this.getManagedBean().getSelectedPlan().addPpPlanDtls(detail);
		}
		
		
	}
	
	public void deleteItem(PpPlanDtl planDtl){
		log.info("deleteItem - Start");
		if(planDtl!=null){
			if(!planDtl.isBrandNew()&& !planDtl.isDeleted()){
				planDtl.setDeleted(true);
				planDtl.setRecordStatus(false);
			}else{
				if(ParameterConst.PROCUREMENT_MODE_DIRECT_PURCHASE.equals(
						this.getManagedBean().getSelectedPlan().getProcurementModeParam().getParameterCode())){
					this.getManagedBean().getSelectedPlanItem().getPpPlanDtls().remove(planDtl);
				}else{
					this.getManagedBean().getSelectedPlan().getPpPlanDtls().remove(planDtl);
				}
			}
			this.getManagedBean().setSelectedPlanItem(null);
		}
		log.info("deleteItem - End");
	}
	
//
//
//
//	public void tabChange(TabChangeEvent val){
//		log.info("tabChange - Start");
//		log.info("tab: " + val.getTab().getId());
//		if("tPlan0".equals(val.getTab().getId())){
//			this.getManagedBean().setSelectedTab(0);
//		}else if("tPlan1".equals(val.getTab().getId())){
//			this.getManagedBean().setSelectedTab(1);
//		}else if("tPlan2".equals(val.getTab().getId())){
//			this.getManagedBean().setSelectedTab(2);
//		}else if("tPlan3".equals(val.getTab().getId())){
//			this.getManagedBean().setSelectedTab(3);
//		}
//		log.info("tabChange - End");
//	}
//
//
/**
	 * initial remark on pp plan level
	 */
	public void initialPlanRemark(PpPlan plan){
		log.info("initialPlanRemark - Start");
		
		if(plan.getPpRemarks()==null){
			plan.setPpRemarks(new ArrayList<PpRemark>());
		}

		PpRemark remark = new PpRemark();
		remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
		remark.setCreatedByObj(getFacesScopes().getUserSessionBean());
		remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		remark.setChangedBy(remark.getCreatedBy());
		remark.setChangedDate(remark.getCreatedDate());
		plan.addPpRemarks(remark);


		log.info("initialPlanRemark - End");
	}
//
//	
//	/**
//	 * populate procurement plan items
//	 * @param item
//	 */
//	public void addPlanItems(){
//		log.info("addPpItems - Start");
//		if(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equals(this.getManagedBean().getSelectedPlanMaster().getPlanType())){
//			if(this.getManagedBean().getSelectedPlanItem().getPpPlanItems()==null){
//				this.getManagedBean().getSelectedPlanItem().setPpPlanItems(new ArrayList<PpPlanDtl>());
//			}
//			this.getManagedBean().getSelectedPlanItem().addPpPlanItems(new PpPlanDtl());
//		}else{
//			if(this.getManagedBean().getSelectedPlan().getPpPlanItems()==null){
//				this.getManagedBean().getSelectedPlan().setPpPlanItems(new ArrayList<PpPlanDtl>());
//			}
//			this.getManagedBean().getSelectedPlan().addPpPlanItems(new PpPlanDtl());
//		}
//		log.info("addPpItems - End");
//	}
//	
//	/**
//	 * add plan item to the list
//	 */
//	public void addItemToList(){
//		log.info("addItemToList - Start");
//		if(this.getManagedBean().getSelectedItem().isBrandNew() && !this.getManagedBean().getSelectedItem().isSelected()){
//			this.getManagedBean().getSelectedPlanItem().addPpPlanItems(this.getManagedBean().getSelectedItem());
//		}
//		log.info("addItemToList - End");
//	}
//	
//	
//	
//	/**
//	 * populate plan item
//	 * @param item
//	 */
//	public void showPpItem(PpPlanDtl item){
//		log.info("showPpItem - Start");
//		System.out.println(this.getManagedBean().getSelectedPlanItem());
//		if(item==null){
//			log.info("new");
//			this.getManagedBean().setSelectedItem(new PpPlanDtl());
//			this.getManagedBean().getSelectedItem().setBrandNew(true);
//			if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(this.getManagedBean().getSelectedPlanMaster().getProcurementMode())){
//				
//			}else{
//				this.getManagedBean().getSelectedItem().setCmUnspscClass(new CmUnspsc());
//			}
//			this.getManagedBean().getSelectedItem().setPpBudgetAllocDtl(new PpBudgetAllocDtl());
//
//		}else{
//			log.info("edit");
//			if(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equals(this.getManagedBean().getSelectedPlanMaster().getPlanType())){
//				item.setEditable((ProcurementPlanConst.MODE_REVISION.equals(this.getManagedBean().getViewMode()) && item.isRevisionRequired()) ? true : false);
//			}else{
//				item.setEditable(ProcurementPlanConst.MODE_REVISION.equals(this.getManagedBean().getViewMode())? true : false);
//			}
//			item.setSelected(true);
//			//populate selected GOSODO
//			item.setBudgetAllocDtlId(item.getPpBudgetAllocDtl().getBudgetAllocDtlId());
//			item.setGoId(item.getPpBudgetAllocDtl().getPpBudget().getBudgetId());
//			item.setSoId(item.getPpBudgetAllocDtl().getSoId());
//			item.setDoId(item.getPpBudgetAllocDtl().getDoId());
//			
//			
//			if(ProcurementPlanConst.PROCUREMENT_EXISTING_CONTRACT.equals(this.getManagedBean().getSelectedPlanMaster().getProcurementMode())){
//				
//			}else{
//				//populate selected Unspsc level
//				CmUnspsc familyObj = selectParentUnspsc(item.getCmUnspscClass().getUnspscParentCode());
//				CmUnspsc segmentObj = selectParentUnspsc(familyObj.getUnspscParentCode());
//				item.setUnspscSegmentCode(segmentObj.getUnspscCode());
//				item.setUnspscFamilyCode(familyObj.getUnspscCode());
//				item.setUnspscClassCode(item.getCmUnspscClass().getUnspscCode());
//			}
//			
//			this.getManagedBean().setSelectedItem(item);
//		}
//		log.info("showPpItem - End");
//	}
//	
//	/**
//	 * delete plan item from the list
//	 * @param item
//	 */
//	public void deletePpItem(PpPlanDtl item){
//		log.info("deletePpItem - Start");
//		if(item!=null){
//			if(!item.isBrandNew() && !item.isDeleted()){
//				item.setDeleted(true);
//				item.setRecordStatus(false);
//				//rollback amount
//				updateSelectedDo(item, "REMOVE");
//			}else{
//				this.getManagedBean().getSelectedPlan().getPpPlanItems().remove(item);
//			}
//		}
//		log.info("deletePpItem - End");
//	}
//	
//
//	
//	/**
//	 * grant budget for each item 
//	 * @param item
//	 * @return BigDecimal
//	 */
//	public BigDecimal grantBudget(PpPlanDtl item){
//		log.info("grantBudget - Start");
//		BigDecimal total = new BigDecimal(0);
//		if(item.isBrandNew() && !item.isSelected()){
//			//update amount
//			item.setStatusId(ProcurementPlanConst.STATUS_PPI_PENDING_SUBMIT);
//			updateSelectedDo(item, "ADD");
//		}else if(item.isDeleted()){
//			updateSelectedDo(item, "REMOVE");
//		}else{
//			if(item.getPpBudgetAllocDtl().getBudgetAllocDtlId()==item.getOriPpBudgetAllocDtl().getBudgetAllocDtlId()){
//				updateSelectedDo(item, "UPDATE");//update amount
//			}else{
//				updateSelectedDo(item, "CHANGE");//rollback amount
//			}
//		}
//		total = CommonMethod.addBigDecimalNullable(this.getManagedBean().getSelectedPlan().getEstimatedAmount(),
//							item.getEstimatedAmount());
//			
//		
//		log.info("grantBudget - End");
//		return total;
//	}
//	
//	
//	/**
//	 * add new plan obj to plan list
//	 */
//	public void addEmptyPlanToList(){
//		log.info("addEmptyPlanToList - Start");
//		if(this.getManagedBean().getSelectedPlanMaster().getPpPlans()==null){
//			this.getManagedBean().getSelectedPlanMaster().setPpPlans(new ArrayList<PpPlan>());
//		}
//
//		this.getManagedBean().getSelectedPlanMaster().addPpPlans(new PpPlan());
//		
//		log.info("addEmptyPlanToList - End");
//	}
//	
////Rain
//	/**
//	 * set selected plan 
//	 */
//	public void selectPlanItem(){
//		log.info("selectPlanItem - Start");
//		if(this.getManagedBean().getSelectedPlanItem()==null){
//			System.out.println("1");
//			this.getManagedBean().setEnableChildDetail(false);
//		}else{
//			System.out.println("2");
//			
//			this.getManagedBean().setEnableChildDetail(true);
//		}
//		
//		log.info("selectPlanItem - End");
//	}
//	
//	/**
//	 * populate contract plan
//	 * @param plan
//	 */
//	public void showContractPlan(PpPlan plan){
//		try{
//			log.info("showContractPlan - Start");
//			if(plan==null){
//				//set empty view for ppPlan
//				this.getManagedBean().setSelectedPlanItem(new PpPlan());
//				this.getManagedBean().getSelectedPlanItem().setBrandNew(true);
//			}else{
//				if(ProcurementPlanConst.MODE_REVISION.equals(this.getManagedBean().getViewMode())){
//					plan.setEditable(true);
//				}else{
//					plan.setEditable(false);
//				}
//				plan.setSelected(true);
//				this.getManagedBean().setSelectedPlanItem(plan);
//			}
//			log.info("showContractPlan - End");
//		}catch(Exception e){
//			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Populate contract failed."));
//			e.printStackTrace();
//		}
//	}
//	
//
//	
//	/**
//	 * add plan to the list (sub header)
//	 */
//	public void addPlanToList(){
//		log.info("addPpPlanToList - Start");
//		if(this.getManagedBean().getSelectedPlanItem().isBrandNew() && !this.getManagedBean().getSelectedPlanItem().isSelected()){
//			this.getManagedBean().getSelectedPlanMaster().addPpPlans(this.getManagedBean().getSelectedPlanItem());
//		}
//		
//		log.info("addPpPlanToList - End");
//	}	
////	
////	/**
////	 * delete plan from the list
////	 * @param plan
////	 */
////	public void deletePpPlan(PpPlan plan){
////		log.info("deletePpPlan - Start");
////		if(plan!=null){
////			if(!plan.isBrandNew()){
////				plan.setDeleted(true);
////				plan.setRecordStatus(false);
////			}else{
////				this.getManagedBean().getSelectedPlanMaster().getPpPlans().remove(plan);
////			}
////			//set null plan for childs relationship
////			this.getManagedBean().setSelectedPlanItem(null);
////			this.getManagedBean().setEnableChildDetail(false);
////		}
////		log.info("deletePpPlan - End");
////	}
////	
////
////	
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
//
//	
//	public void customReason(AjaxBehaviorEvent event){
//		log.info("customReason - Start");
//		System.out.println("ids: " + event.getComponent().getAttributes().get("value"));	
//		List <Long> rejectIds = (ArrayList<Long>)event.getComponent().getAttributes().get("value");
//		if(rejectIds!=null && rejectIds.contains(0L)){
//			this.getManagedBean().setReasonRejectOther(true);
//		}else{
//			this.getManagedBean().setReasonRejectOther(false);
//		}
//
//		log.info("customReason - End");
//	}
//	
//		
//	/**
//	 * calculate plan end date
//	 */
//	public void calculatePlanEndDate(){
//		log.info("calculatePlanEndDate - Start");
//		if(this.getManagedBean().getSelectedPlanItem().getPlanStartDate()!=null){
//			Calendar startDate = CommonMethod.convertDateToCalender(this.getManagedBean().getSelectedPlanItem().getPlanStartDate());
//			startDate.add(Calendar.MONTH, this.getManagedBean().getSelectedPlanItem().getCtContract().getLatestContractVer().getCtContractValue().getContractPeriod());
//			this.getManagedBean().getSelectedPlanItem().setPlanEndDate(startDate.getTime());
//		}else{
//			this.getManagedBean().getSelectedPlanItem().setPlanEndDate(null);
//		}
//		log.info("calculatePlanEndDate - End");
//	}
//	
//	/**
//	 * calculate activity end date
//	 * @param activity
//	 */
//	public void calculateActEndDate(PpPlanActivity activity){
//		log.info("calculateActEndDate - Start");
//		if(activity!=null){
//			if(activity.getPlanStartDate()!=null){
//				Calendar startDate = CommonMethod.convertDateToCalender(activity.getPlanStartDate());
//				startDate.add(Calendar.DATE, activity.getPmBusinessActivity().getSlaDay());
//				activity.setPlanEndDate(startDate.getTime());
//			}else{
//				activity.setPlanEndDate(null);
//			}
//		}
//		log.info("calculateActEndDate - End");
//	}
//	
//
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
			for(PmBusinessActivity task: taskActivities){
				PpPlanActivity activity = new PpPlanActivity();
				activity.setPmBusinessActivity(task);
				this.getManagedBean().getSelectedPlan().addAllPpPlanActivities(activity);
			}
	
	
		}catch (Exception e){
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Fail to retrieve sourcing activity."));
			e.printStackTrace();
			
		}
		log.info("initialPmTaskActivities - End");
		return taskActivities;
	
	}
//
//	/**
//	 * update local budget alloc dtl while GOSODO is selected
//	 * @param item
//	 * @param mode
//	 */
//	public void updateSelectedDo(PpPlanDtl item, String mode){
//		log.info("updateSelectedDo - Start");
//		
//		try{
//			log.info("mapper keys " + this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().keySet());
//			log.info("mapper values " +this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().values());
//	
//			long key = item.getPpBudgetAllocDtl().getBudgetAllocDtlId();
//			if("CHANGE".equals(mode)){
//				//change GO
//				long oldKey = item.getOriPpBudgetAllocDtl().getBudgetAllocDtlId();
//				System.out.println(	this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(oldKey) + "-" + item.getOriEstimatedAmount());
//				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
//						oldKey, 
//						CommonMethod.substractBigDecimalNullable(
//								this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(oldKey), 
//								item.getOriEstimatedAmount())
//				);
//				System.out.println(">>>>>>>>ori amount " + item.getOriEstimatedAmount());
//				System.out.println(">>>>>>>>ori Budget alloc " + item.getOriPpBudgetAllocDtl().getBudgetAllocDtlId());
//				System.out.println(	this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(key) + "+" + item.getEstimatedAmount());
//				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
//						key, 
//						CommonMethod.addBigDecimalNullable(
//								this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(key),
//								item.getEstimatedAmount())
//				);
//				
//				item.setOriEstimatedAmount(item.getEstimatedAmount());//update ori amount to new amount
//				item.setOriPpBudgetAllocDtl(item.getPpBudgetAllocDtl());//update ori budget to new budget
//				System.out.println(">>>>>>>>update ori amount " + item.getOriEstimatedAmount());
//				System.out.println(">>>>>>>>update ori Budget alloc " + item.getOriPpBudgetAllocDtl().getBudgetAllocDtlId());
//			}else if("REMOVE".equals(mode)){
//				//remove item
//				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
//						key, 
//						CommonMethod.substractBigDecimalNullable(
//								this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(key), 
//								item.getEstimatedAmount())
//				);
//			}else if("ADD".equals(mode)){
//				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
//						key, 
//						CommonMethod.addBigDecimalNullable(
//							this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(key),
//							item.getEstimatedAmount())
//				);
//				item.setOriEstimatedAmount(item.getEstimatedAmount());//initiate ori amount
//				item.setOriPpBudgetAllocDtl(item.getPpBudgetAllocDtl());//initiate ori budget
//			}else if("UPDATE".equals(mode)){
//				//update item with same GO
//				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
//						key, 
//						CommonMethod.substractBigDecimalNullable(
//								this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(key), 
//								item.getOriEstimatedAmount())
//				);
//				System.out.println(">>>>>>>>ori RM " + item.getOriEstimatedAmount());
//				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
//						key, 
//						CommonMethod.addBigDecimalNullable(
//								this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().get(key),
//								item.getEstimatedAmount())
//				);
//				item.setOriEstimatedAmount(item.getEstimatedAmount());//update ori amount to new amount
//				System.out.println(">>>>>>>>update ori RM " + item.getOriEstimatedAmount());
//			}else if("INIT".equals(mode)){
//				this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().put(
//						key, new BigDecimal(0));
//				item.setOriEstimatedAmount(item.getEstimatedAmount());//initiate ori amount
//				item.setOriPpBudgetAllocDtl(item.getPpBudgetAllocDtl());//initiate ori budget
//			}
//			log.info("mapper keys " + this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().keySet());
//			log.info("mapper values " +this.getManagedBean().getSelectedPlanMaster().getSelectedGoMapper().values());
//		}catch(Exception e){
//			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Grant budget failed."));
//			e.printStackTrace();
//		}
//		log.info("updateSelectedDo - End");
//	}
//
//	/**
//	 * retrieve department list from PM
//	 * @return department list
//	 */
//	public List<PmOrgProfile> departmentOptions() {
//		log.info("departmentOptions - Start");
//		List<PmOrgProfile> options = new ArrayList<PmOrgProfile>();
//		try{
//			if(this.getManagedBean().getSelectedPlanMaster()!=null 
//					&& this.getManagedBean().getSelectedPlanMaster().getOrgProfile().getParentOrgProfile()!=null
//					&& this.getManagedBean().getSelectedPlanMaster().getOrgProfile().getParentOrgProfile().getParentOrgProfile()!=null){
//				
//				Long orgProfileId = this.getManagedBean().getSelectedPlanMaster().getOrgProfile().getParentOrgProfile().getParentOrgProfile().getOrgProfileId();
//				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
//				reqDTO.setParentOrgProfileId(orgProfileId);
//				reqDTO.setOrgType(OrganizationType.DEPARTMENT.value());
//				
//				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
//				OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
//		//		if(resDTO.isSuccessful()){
//					options = resDTO.getOrgProfileList();
//					System.out.println("rain......" + resDTO.getOrgProfileList().size());
//		//		}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		log.info("departmentOptions - End");
//		return options;
//	}
//	
//	/**
//	 * retrieve division list by department id from PM
//	 * @param divisionId
//	 * @return division list
//	 */
//	public List<PmOrgProfile> divisionOptions(Long departmentId) {
//		log.info("divisionOptions - Start");
//		List<PmOrgProfile> options = new ArrayList<PmOrgProfile>();
//		try{
//			if(CommonMethod.isSelected(departmentId)){
//				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
//				reqDTO.setParentOrgProfileId(departmentId);
//				reqDTO.setOrgType(OrganizationType.DIVISION.value());
//				
//				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
//				OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
//		//		if(resDTO.isSuccessful()){
//					options = resDTO.getOrgProfileList();
//		//		}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		log.info("divisionOptions - End");
//		return options;
//	}
//	
	/**
	 * retrieve plan type options
	 * @return SelectItem[]
	 */
	public SelectItem[] planTypeOptions() {
		log.info("procurementModeOptions");
		return getDropdownValues(ParameterTypeConst.PLAN_TYPE);
	}
	
	public void selectedPlanTypeParam(Long id){
//		if(id!=null && id>0L){
			this.getManagedBean().getSelectedPlan().setPlanTypeParam(retrieveParamObj(id));
//		}
		enableDetailTab();
	}
	
	public void selectedProcurementModeParam(Long id){
//		if(id!=null && id>0L){
			this.getManagedBean().getSelectedPlan().setProcurementModeParam(retrieveParamObj(id));
//		}
		enableDetailTab();
	}
	
	public void selectedDocTypeParam(Long id){
//		if(id!=null && id>0L){
			this.getManagedBean().getSelectedPlan().setDocTypeParam(retrieveParamObj(id));
//		}
	}
	
	
	
	/**
	 * retrieve procurement mode options
	 * @return SelectItem[]
	 */
	public SelectItem[] procurementModeOptions(String planType) {
		log.info("procurementModeOptions");
		SelectItem[] options = null;

		if(planType!=null && !planType.isEmpty()){
			if(ParameterConst.PLAN_TYPE_EXPENDITURE.equals(planType)){
				options = getDropdownValues(ParameterTypeConst.PROCUREMENT_MODE_EXPENDITURE);
			}else if(ParameterConst.PLAN_TYPE_PROCUREMENT.equals(planType)){
				options = getDropdownValues(ParameterTypeConst.SC_PROCUREMENT_MODE);
			}
			
		}else{
			options = new SelectItem[1];
			options[0] =  new SelectItem(null, "- Select One -");
		}
		return options;
	}

	/**
	 * retrieve qt type options
	 * @return SelectItem[]
	 */
	public SelectItem[] qtTypeOptions() {
		log.info("qtTypeOptions");
		return getDropdownValues(ParameterTypeConst.QUOTATION_TENDER_TYPE);
	}	
	
	/**
	 * retrieve item origin type options
	 * @return SelectItem[]
	 */
	public SelectItem[] itemOriginTypeOptions() {
		log.info("itemOriginTypeOptions");
		return getDropdownValues(ParameterTypeConst.ITEM_ORIGIN);
	}
	
	/**
	 * retrieve expenditure contract type
	 * @return SelectItem[]
	 */
	public SelectItem[] epContractTypeOptions() {
		log.info("epContractTypeOptions");
		return getDropdownValues(ParameterTypeConst.EXPENDITURE_CONTRACT_TYPE);
	}
	
	/**
	 * retrieve parameter obj by param id
	 * @param paramId
	 * @return PmParameter
	 */
	 
	public PmParameter retrieveParamObj(Long paramId){
		log.info("retrieveParamObj - Start");
		PmParameter paramObj = new PmParameter();
		try{
			if(paramId!=null && paramId>0L){
				ParameterReqDTO reqDTO = new ParameterReqDTO();
				reqDTO.setParameterId(paramId);
				IParameterSO parameterSO = BusinessDelegate.getInstance().getServiceObject("pm", IParameterSO.class);
				ParameterResDTO resDTO = parameterSO.retrieveParameterById(reqDTO);
				paramObj = resDTO.getPmParameter();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("retrieveParamObj - End");
		return paramObj;
		
	}
//	
//	/**
//	 * retrieve verification level options
//	 * @return List<SelectItem>
//	 */
//	public List<SelectItem> verificationLevelOptions() {
//		List<SelectItem>options = new ArrayList<SelectItem>();
//		options.add(new SelectItem(ProcurementPlanConst.LEVEL_SINGLE, "Single"));
//		options.add(new SelectItem(ProcurementPlanConst.LEVEL_MULTIPLE, "Multiple"));
//		return options;
//	}
//	
//	/**
//	 * retrieve contract indicator options
//	 * @return List<SelectItem>
//	 */
//	public List<SelectItem> contractIndOptions() {
//		List<SelectItem>options = new ArrayList<SelectItem>();
////		if(!ProcurementPlanConst.FULFILLMENT_SCHEDULE.equals(fulfillmentType)){
//			options.add(new SelectItem(ProcurementPlanConst.CONTRACT_IND_PKP, "PKP"));
////		}
//		options.add(new SelectItem(ProcurementPlanConst.CONTRACT_IND_LOA, "LOA"));
//		options.add(new SelectItem(ProcurementPlanConst.CONTRACT_IND_CONTRACT, "Contract"));
//		return options;
//	}
//	
//	/**
//	 * retrieve Contract Type options
//	 * @return List<SelectItem>
//	 */
//	public List<SelectItem> contractTypeOptions() {
//		List<SelectItem>options = new ArrayList<SelectItem>();
//		options.add(new SelectItem(ProcurementPlanConst.CONTRACT_TYPE_MINISTRY, "Ministry"));
//		options.add(new SelectItem(ProcurementPlanConst.CONTRACT_IND_CENTRAL, "Central"));
//		return options;
//	}
//	
	/**
	 * retrieve all short month e.g Jul
	 * @return List<SelectItem>
	 */
	public List<SelectItem> shortMonths() {
		return CommonMethod.calendarMonths();
	}

	/**
	 * vot type options
	 * @return  SelectItem[]
	 */
	public SelectItem[] votTypeOptions() {
		log.info("votTypeOptions");
//		List<SelectItem>options = new ArrayList<SelectItem>();
//		options.add(new SelectItem(1L, "Mengurus"));
//		options.add(new SelectItem(1L, "Tanggungan"));
//		options.add(new SelectItem(1L, "Pembangunan"));
//		options.add(new SelectItem(1L, "Setia"));
//		options.add(new SelectItem(1L, "Amanah"));
		return getDropdownValues(ParameterTypeConst.VOT_TYPE);
	}
//	
//	/**
//	 * retrieve financial year options for current year & current year +1
//	 * @return financial year options
//	 */
//	public List<SelectItem> financialYearOptions() {
//		log.info("financialYearOptions - Start");
//		return CommonMethod.financialYearOptions();
//	} 
//	
//	
//
//	/**
//	 * generate available budget amount
//	 * @param allocAmt
//	 * @param usedAmt
//	 * @return available budget amount
//	 */
//	public BigDecimal generateAvailableBudget(BigDecimal allocAmt, BigDecimal usedAmt){
//		return
//				CommonMethod.substractBigDecimalNullable(allocAmt,usedAmt);
//	}
//
//	
	

//	
//	/**
//	 * select parent unspsc object
//	 */
//	public CmUnspsc selectParentUnspsc(String code){
//		log.info("selectParentUnspsc - Start");
//	
//		CmUnspsc parentUnspsc = new CmUnspsc();
//		try{
//			if(CommonMethod.isSelected(code)){
//				CodificationReqDTO reqDTO = new CodificationReqDTO();
//				reqDTO.setParentCode(code);
//				ICodificationSO reqSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
//				CodificationResDTO resDTO = reqSO.searchParentObj(reqDTO);
//				if(resDTO.isSuccessful()){
//					parentUnspsc =resDTO.getParentObj();
//				}
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//			
//		}
//		log.info("selectParentUnspsc - End");		
//		return parentUnspsc;
//	}
//
//	
//	/**
//	 * retrieve distinct GO options
//	 * @return List<PpBudget>
//	 */
//	public List<PpBudget> goOptions() {
//		log.info("goOptions - Start");
//		
//		Long id = this.getManagedBean().getCurrentUser().getOrgProfileId();
//		System.out.println("id " + id);
//		List<PpBudget> goBudgets = new ArrayList<PpBudget>();
//		try{
//			if(CommonMethod.isSelected(id)){
//				BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
//				reqDTO.setOrgProfileId(id);	
//				IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
//				BudgetAllocationResDTO resDTO = reqSO.retrieveDistinctGoList(reqDTO);
//				
//				if (resDTO.isSuccessful()) {
//					goBudgets = resDTO.getGoBudgetList();
//					System.out.println("size : " + goBudgets.size());
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		log.info("goOptions - End");
//		return goBudgets;
//		
//	}
//
//	/**
//	 * retrieve distinct SO options
//	 * @param goId
//	 * @return List<PmSo> 
//	 */
//	public List<PmSo> soOptions(Long goId) {
//		log.info("soOptions - Start");
//		Long profId = this.getManagedBean().getCurrentUser().getOrgProfileId();
//		System.out.println("profId "  + profId);
//		System.out.println("goId "  + goId);
//		List<PmSo> soBudgets = new ArrayList<PmSo>();
//		try{
//			if(CommonMethod.isSelected(profId) && CommonMethod.isSelected(goId)){
//				BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
//				reqDTO.setOrgProfileId(profId);	
//				reqDTO.setGoBudgetId(goId);
//				IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
//				BudgetAllocationResDTO resDTO = reqSO.retrieveDistinctSoList(reqDTO);
//				
//				if (resDTO.isSuccessful()) {
//					soBudgets = resDTO.getSoList();
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		log.info("soOptions - End");
//		return soBudgets;
//		
//	}
//	/**
//	 * retrieve all DO options
//	 * @param goId
//	 * @param soId
//	 * @return List<PpBudgetAllocDtl> 
//	 */
//	public List<PpBudgetAllocDtl> doOptions(Long goId, Long soId) {
//		log.info("doOptions - Start");
//		Long profId = this.getManagedBean().getCurrentUser().getOrgProfileId();
//		System.out.println("profId "  + profId);
//		System.out.println("goId "  + goId);
//		System.out.println("so "  + soId);
//		List<PpBudgetAllocDtl> doBudgets = new ArrayList<PpBudgetAllocDtl>();
//		try{
//			if(CommonMethod.isSelected(profId) && CommonMethod.isSelected(goId)
//					&& CommonMethod.isSelected(soId)){
//				BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
//				reqDTO.setOrgProfileId(profId);	
//				reqDTO.setGoBudgetId(goId);
//				reqDTO.setSoId(soId);
//				IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
//				BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetDoList(reqDTO);
//				
//				if (resDTO.isSuccessful()) {
//					doBudgets = resDTO.getDoList();
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		log.info("doOptions - End");
//		return doBudgets;
//		
//	}
//	
//
//	/**
//	 * retrieve and set actor
//	 * @param userId
//	 */
//	private PmUser getActor(Long userId){
//		log.info("getActor - Start");
//		System.out.println("userId " + userId);
//		UserReqDTO reqDTO = new UserReqDTO();
//		PmUser user = new PmUser();
//		if(userId!=null){
//			try{
//				IUserSO userSO = BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
//				reqDTO.setUserId(userId);
//				UserResDTO resDTO = userSO.retrieveUserDetails(reqDTO);
//				user = resDTO.getPmUser();
//				
//				
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//		}
//		log.info("getActor - End");
//		return user;
//	
//	}
//
//	/**
//	 * retrieve and set selected GOSODO
//	 * @param ppPlanItem
//	 */
//	public void selectedGoSoDo(PpPlanDtl ppPlanItem){
//		log.info("selectedGoSoDo - Start");
//		
//		Long id = ppPlanItem.getBudgetAllocDtlId();
//		log.info("id " + id);
//		try{
//			if(CommonMethod.isSelected(id)){
//				BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
//				reqDTO.setBudgetAllocDtlId(id);
//				IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
//				BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetAllocDtl(reqDTO);
//				if (resDTO.isSuccessful()) {
//					ppPlanItem.setPpBudgetAllocDtl(resDTO.getBudgetAllocDtl());
////					ppPlanItem.setOvrFinancialYear(
////							resDTO.getBudgetAllocDtl().getPpBudget().getFinancialYear());
//				}
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		log.info("selectedGoSoDo - End");
//		
//	}
//	

	public List<CmUnspsc> unspscClassOptions(String code){
		log.info("unspscClassOptions");
		return retrieveUnspscList(3, code);
	}
	
	public List<CmUnspsc> unspscCommodityOptions(String code){
		log.info("unspscCommodityOptions");
		return retrieveUnspscList(2, code);
	}
	
	public List<CmUnspsc> unspscFamilyOptions(String code){
		log.info("unspscFamilyOptions");
		return retrieveUnspscList(1, code);
	}
	
	public List<CmUnspsc> unspscSegmentOptions(){
		log.info("unspscSegmentOptions");
		return retrieveUnspscList(0, null);
	}
	
	/**
	 * retrieve unspsc list from CM
	 * @param unspscLevel
	 * @param code
	 * @return List<CmUnspsc> 
	 */
	private List<CmUnspsc> retrieveUnspscList(int unspscLevel, String code){
		log.info("retrieveUnspscList - Start");
		CodificationReqDTO reqDTO = null;
		CodificationResDTO resDTO = null;
		List<CmUnspsc> options = null;
		
		try{
//			if(code!=null && !code.isEmpty()){
				options = new ArrayList<CmUnspsc>();
				reqDTO = new CodificationReqDTO();
				resDTO = new CodificationResDTO();
				
				if(code!=null && !code.isEmpty()){
					reqDTO.setParentCode(code);
				}
				ICodificationSO reqSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
				switch (unspscLevel) {
		            case 0: resDTO = reqSO.searchSegment(reqDTO);
		                    break;
		            case 1: resDTO = reqSO.searchFamily(reqDTO);
		                    break;
		            case 2: resDTO = reqSO.searchCommodity(reqDTO);
                    		break;
		            case 3: resDTO = reqSO.searchClass(reqDTO);
            				break;
		        }
				
				if(resDTO.isSuccessful()){
					switch (unspscLevel) {
			            case 0: options = resDTO.getSegmentList();
			                    break;
			            case 1: options = resDTO.getFamilyList();
			                    break;
			            case 2: options = resDTO.getCommodityList();
	                    		break;
			            case 3: options = resDTO.getClassList();
	            				break;
			        }
				}
				log.info("result size: " +options.size());
//			}
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("retrieveUnspscList - End");
		return options;
	}
	
	
	public void selectedCommodity(int index, String code){
		this.getManagedBean().getSelectedPlanActivity().getPpPlanItems().get(index).setCmUnspsc(retrieveUnspscObj(code));
		
	}
	/**
	 * retrieve unspsc class list from CM
	 * @param family code
	 * @return List<CmUnspsc> 
	 */
	private CmUnspsc retrieveUnspscObj(String code){
		log.info("retrieveUnspscObj - Start");
		System.out.println("code " + code);
		CodificationReqDTO reqDTO = null;
		CodificationResDTO resDTO = null;
		CmUnspsc cmUnspsc = null;
		
		try{
			cmUnspsc = new CmUnspsc();
			if(code!=null && !code.isEmpty()){
				
				reqDTO = new CodificationReqDTO();
				resDTO = new CodificationResDTO();
				reqDTO.setUnspscCode(code);
				ICodificationSO reqSO = (ICodificationSO) BusinessDelegate.getInstance().getServiceObject("cm", ICodificationSO.class);
				resDTO = reqSO.searchUnspscObj(reqDTO);
				
				if(resDTO.isSuccessful()){
					cmUnspsc = resDTO.getUnspscObj();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("retrieveUnspscObj - End");
		return cmUnspsc;
	}

	
	
	
	
//	
//	/**
//	 * retrieve rejection reason
//	 * @return List<PmReasonDesc> 
//	 */
//	public List<PmReasonDesc> rejectionReasonOptions(){
//		return reasonOptions(ReasonCategory.PROCUREMENT_PLAN_REJECTION.value());
//	}
//
//	/**
//	 * retrieve reason by category
//	 * @return List<PmReasonDesc> 
//	 */
//	public List<PmReasonDesc> reasonOptions(String category){
//		log.info("reasonOptions - Start");
//		List<PmReasonDesc> options = new ArrayList<PmReasonDesc>();
//		try{
//			FacesScopes fs = new FacesScopes();
//			ReasonReqDTO reqDTO = new ReasonReqDTO();
//			reqDTO.setReasonCategory(category);
//			reqDTO.setLanguage(fs.getUserLocale().getLanguage());
//			IReasonSO reqSO = (IReasonSO) BusinessDelegate.getInstance().getServiceObject("pm", IReasonSO.class);
//			ReasonResDTO resDTO = reqSO.retrieveReasonDescListByCategory(reqDTO);
////			if(resDTO.isSuccessful()){
//				System.out.println("sucess");
//				options = resDTO.getReasonDescList();
////			}
//			log.info("result size: " +options.size());
//			
//		}catch (Exception e){
//			e.printStackTrace();
//			
//		}
//		log.info("reasonOptions - End");
//		return options;
//	}
//
//
	
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
//				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		log.info("getOrgProfiles - End");
		return profileList;
	}
	
	public List<SelectItem> contractOptions(){
		String contractType = this.getManagedBean().getSelectedPlan().getDocTypeParam().getParameterCode();
		List<SelectItem> options = new ArrayList<SelectItem>();
		options.add(new SelectItem(null, "- Select One -"));
		
		if(contractType!=null && !contractType.isEmpty()){
			if(ProcurementPlanConst.CONTRACT_TYPE_PKP.equals(contractType)){
				Long circularId = this.getManagedBean().getSelectedPlan().getCtCircular().getCircularId();
				List<CtCircularContract> circularContracts = cicularContractOptions(circularId);
				for(CtCircularContract circularContract: circularContracts){
					options.add(new SelectItem(circularContract.getCtContract().getContractId(),
							circularContract.getCtContract().getLatestContractVer().getCtContractValue().getContractPhysicalNo()
							));
				}
			}else if(ProcurementPlanConst.CONTRACT_TYPE_MINISTRY_CONTRACT.equals(contractType) 
					|| ProcurementPlanConst.CONTRACT_TYPE_CENTRAL_CONTRACT.equals(contractType) ){
				List<CtContract> contracts = contractOptions(contractType);
				for(CtContract contract: contracts){
					options.add(new SelectItem(contract.getContractId(),
							contract.getLatestContractVer().getCtContractValue().getContractPhysicalNo()
							));
				}
			}
		}
		return options;
	}
	
	/**
	 * retrieve contract list by type
	 * @return List<CtContract> 
	 */
	public List<CtContract> contractOptions(String contractType){
		log.info("contractOptions - Start");
		log.info("contractType id: "+contractType);
		List<CtContract> options = new ArrayList<CtContract>();
		try{
			if(CommonMethod.isSelected(contractType)){
				ContractReqDTO reqDTO = new ContractReqDTO();
//				reqDTO.setContractTypeCodeId(contractType);//TODO
				IContractSO reqSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
				ContractResDTO resDTO = reqSO.retrieveContractByContractType(reqDTO);
				if(resDTO.isSuccessful()){
					options = resDTO.getCtContracts();
				}
				log.info("result size: " +options.size());
			}
		}catch (Exception e){
			e.printStackTrace();
			
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
			reqDTO.setDocType(ContractConst.CIRCULAR_DOC_TYPE_CENTRAL_CONTRACT);
			ICircularSO circularSO = (ICircularSO) BusinessDelegate.getInstance().getServiceObject("ct", ICircularSO.class);
			CircularResDTO resDTO = circularSO.retrieveCircularListByType(reqDTO);
			if(resDTO.isSuccessful()){
				options = resDTO.getCircularList();
			}
			log.info("result size: " +options.size());
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("circularOptions - End");
		return options;
	}
	
	/**
	 * retrieve and set contract by id
	 * @param contractId
	 * @return CtContract
	 */
	public void selectedContract(Long id){
		log.info("selectedContract - Start");
		log.info("id: " + id);
		CtContract contract = new CtContract();
		try{
			if(id!=null && id>0L){
				ContractReqDTO reqDTO = new ContractReqDTO();
				reqDTO.setContractId(id);
				IContractSO reqSO = (IContractSO) BusinessDelegate.getInstance().getServiceObject("ct", IContractSO.class);
				ContractResDTO resDTO = reqSO.retrieveContract(reqDTO);
				if(resDTO.isSuccessful()){
					contract = resDTO.getContract();
				}
			}
			this.getManagedBean().getSelectedPlan().setCtContract(contract);
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("selectedContract - End");
	}
//	
//	public void resetContract(){
//		this.getManagedBean().getSelectedPlanItem().setCtContract(null);
//	}
//	/**
//	 * retrieve the selected contract's items
//	 */
//	public List <CtContractItem> contractItemOptions(){
//		return this.getManagedBean().getSelectedPlanItem().getCtContract()!=null ?
//				this.getManagedBean().getSelectedPlanItem().getCtContract().getLatestContractVer().getCtContractItems():new ArrayList<CtContractItem>();
//		
//	}
//
//	
//	/**
//	 * retrieve PmActivity list by type
//	 * @param type
//	 * @return List<PmActivity>
//	 */
//	public List<PmActivity> activityOptions(String type){
//		log.info("activityOptions - Start");
//		List<PmActivity> options = new ArrayList<PmActivity>();
//		try{
//			if(CommonMethod.isSelected(type)){
//				ActivityReqDTO reqDTO = new ActivityReqDTO();
//				reqDTO.setActivityType(type);
//				IActivitySO reqSO = (IActivitySO) BusinessDelegate.getInstance().getServiceObject("pm", IActivitySO.class);
//				ActivityResDTO resDTO = reqSO.retrieveActivityListByType(reqDTO);
//				if(resDTO.isSuccessful()){
//					options = resDTO.getActivityTypeList();
//				}
//				log.info("result size: " +options.size());
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//			
//		}
//		log.info("activityOptions - End");
//		return options;
//	}
//	
//	
	public List<PmUser> generateApprovers() throws Exception{
		log.info("generateApprovers ");
		return userWithRoleOptions(RoleCode.PLAN_APPROVER.value());
	}
	
	public void generateVerificationOfficers() throws Exception{
		log.info("generateVerificationOfficers ");
		List<PmUser> source = new ArrayList<PmUser>(); 
        List<PmUser> target = new ArrayList<PmUser>(); 
        source = userWithRoleOptions(RoleCode.PLAN_VERFICATION_OFFICER.value());
		this.getManagedBean().setUsers(new DualListModel<PmUser>(source, target));  
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
//				if(resDTO.isSuccessful()){
					options = resDTO.getUserList();
//				}
				log.info("result size: " +options.size());
			}
		}catch (Exception e){
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR, e.toString(),"Role "+roleCode));
			throw e;
			
		}
		log.info("userWithRoleOptions - End");
		return options;
	}


//	
//	
//	/**
//	 * copy header data from mainPlan to newPlan
//	 * @param mainPlan
//	 * @param newPlan
//	 */
//	private PpPlan clonePlan(PpPlan sourcePlan, PpPlan desPlan){
//		log.info("clonePlan - Start");
//		if(sourcePlan!=null && desPlan!=null){
//			if(!sourcePlan.equals(desPlan)){
//				log.info("1clonePlan " + sourcePlan.getPpPlanActors().size());
//				desPlan.setPlanDesc(sourcePlan.getPlanDesc());
//				desPlan.setStatusId(sourcePlan.getStatusId());
//				desPlan.setSubmissionType(sourcePlan.getSubmissionType());
//				desPlan.setPpPlanActors(new ArrayList<PpPlanActor>());
//				for(PpPlanActor actor : sourcePlan.getPpPlanActors()){
//					log.info("clonePlan --- actor");
//					PpPlanActor tempActor = new PpPlanActor();
//					tempActor.setActorSeq(actor.getActorSeq());
//					tempActor.setActorType(actor.getActorType());
//					tempActor.setUserId(actor.getUserId());
//					tempActor.setPmUser(actor.getPmUser());
//					desPlan.addPpPlanActors(tempActor);;
//				}
//
//			}
//		}
//		log.info("clonePlan - end");
//		return desPlan;
//	}
//	
//	
//	/**
//	 * generate plan master with completed children
//	 */
//	private void getCompletePlanMaster()throws Exception{
//		
//		log.info("getCompletePlanMaster - Start");
////		try{
//		System.out.println("2222"+ this.getManagedBean().getPlan().getPlanNo());
//		if(!(this.getManagedBean().getPlan().getPlanNo()!=null && !StringUtils.isEmpty(this.getManagedBean().getPlan().getPlanNo()))){
//			System.out.println("11111");
//			//initialize actor list
//			if(this.getManagedBean().getPlan().getPpPlanActors()==null){
//				this.getManagedBean().getPlan().setPpPlanActors(new ArrayList<PpPlanActor>());
//			}
//
//			//Check Approver
//			changeActors(this.getManagedBean().getPlan().getApprover(), this.getManagedBean().getPlan());
//			
//			//Check Verification Officer
//			if(ProcurementPlanConst.SUBMIT_VERIFICATION.equals(this.getManagedBean().getPlan().getSubmissionType())){
//				for(PpPlanActor pvo: this.getManagedBean().getPlan().getVerificationOfficers()){
//					if(CommonMethod.isSelected(pvo.getUserId())){
//						changeActors(pvo, this.getManagedBean().getPlan());
//					}
//				}
//			}
//
//			if(!CommonMethod.isSelected(this.getManagedBean().getPlanMaster().getMaksudId())){
//				this.getManagedBean().getPlanMaster().setMaksudId(null);
//			}
//			
//			//Rejection Reasons
//			if(ProcurementPlanConst.MODE_VERIFICATION.equals(this.getManagedBean().getViewMode()) 
//					|| ProcurementPlanConst.MODE_APPROVAL.equals(this.getManagedBean().getViewMode())){
//				if(this.getManagedBean().getPlan().getPmRejectReasonIds()!=null &&
//						!this.getManagedBean().getPlan().getPmRejectReasonIds().isEmpty()){
//					for(Long rejectId : this.getManagedBean().getPlan().getPmRejectReasonIds()){
//						PpRejectReason rejectReason  = new PpRejectReason();
//						rejectReason.setReasonId(rejectId);
//						this.getManagedBean().getPlan().addPpRejectReasons(rejectReason);
//					}
//
//				}
//			}
//			
//			BigDecimal totalEstimate = new BigDecimal(0);
//			//for EP
//			if(ProcurementPlanConst.PLAN_TYPE_EXPENDITURE.equals(this.getManagedBean().getPlanMaster().getPlanType())){
//				//multiple plans
//				for(PpPlan epPlan:this.getManagedBean().getPlanMaster().getPpPlans()){
//					epPlan.setTempPlanNo(CommonMethod.generatePlanNumber(true, this.getManagedBean().getPlanMaster().getPlanType(), null));
//					epPlan = clonePlan(this.getManagedBean().getPlan(), epPlan);
//					for(PpPlanDtl item:epPlan.getPpPlanItems()){
//						totalEstimate = grantBudget(item);
//					}
//					epPlan.setEstimatedAmount(totalEstimate);
//				}
//	
//			//for PP
//			}else if(ProcurementPlanConst.PLAN_TYPE_PROCUREMENT.equals(this.getManagedBean().getPlanMaster().getPlanType())){
//				System.out.println("1");
//				//getting only selected task activities
//				List <PpPlanActivity> planActivities = this.getManagedBean().getPlan().getAllPpQtActivities();
//				System.out.println("2");
////				if(this.getManagedBean().getPlan().isBrandNew()){
//					System.out.println("3");
//					this.getManagedBean().getPlan().setPpQtActivities(new ArrayList<PpPlanActivity>());
////				}
//				for(PpPlanActivity act:planActivities){
//					System.out.println("4");
//					if(act.getPlanStartDate()!=null){
//						System.out.println("5");
//						this.getManagedBean().getPlan().addPpQtActivities(act);
//		
//					}
//				}
//				for(PpPlanDtl item:this.getManagedBean().getPlan().getPpPlanItems()){
//					totalEstimate = grantBudget(item);
//				}
//				this.getManagedBean().getPlan().setEstimatedAmount(totalEstimate);
//				//only one plan
//				if(this.getManagedBean().getPlanMaster().isBrandNew()){
//					this.getManagedBean().getPlan().setTempPlanNo(CommonMethod.generatePlanNumber(true, this.getManagedBean().getPlanMaster().getPlanType(), null));
//					this.getManagedBean().getPlanMaster().addPpPlans(this.getManagedBean().getPlan());
//				}				
//			}
//	
//		}
////		}catch(Exception e){
////			e.printStackTrace();
////			throw e;
////		}
//		log.info("getCompletePlanMaster - End");
//	}
//	
//	/**
//	 * submit plan when button is clicked
//	 * @param action
//	 * @return xhtml
//	 */
//	public String submitPlan(String action) {
//		log.info("submitPlan - Start");
//		String returnPage = "";
//		boolean isSuccess = false;
//		boolean brandNew = this.getManagedBean().getSelectedPlanMaster().isBrandNew();
//		try{
//			if (!brandNew) {
//				getCompletePlanMaster();	
//				isSuccess = updatePlanMaster(action);
//			} else {
//				getCompletePlanMaster();
//				isSuccess = addPlan(action);
//				
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		if(isSuccess){
//			if(ProcurementPlanConst.BPM_SAVE.equals(action)){
//				
//			}else{
//				returnPage = ProcurementPlanConst.NAV_TO_TASK_LIST;
//			}
//			
//		}
//		log.info("isSuccess: " + isSuccess + ", returnPage: "+ returnPage);
//		log.info("submitPlan - End");
//		return returnPage;
//	}
//
//	/**
//	 * update object(s) before based on selected outcome
//	 * @param action
//	 */
//	private void bpmOutcomeHandler(String action){
//		if(BpmOutcomeConst.BPM_SUBMIT.equals(action)){
//			//rollback revision flag
//			for(PpPlanDtl item :this.getManagedBean().getPlan().getPpPlanItems()){
//				item.setRevisionRequired(false);
//			}
//		}else if(BpmOutcomeConst.BPM_REJECT.equals(action)){
//			//rollback budget
//			for(PpPlanDtl item :this.getManagedBean().getPlan().getPpPlanItems()){
//				updateSelectedDo(item, "REMOVE");
//			}
//		}
//	}
//	
//	/**
//	 * update plan master to DB (for draft)
//	 * @param action
//	 * @return boolean
//	 */
//	public boolean updatePlanMaster(String action)throws Exception{
//		log.info("updatePlanMaster - Start");
//		
//		boolean result = false;
//		try{
//			bpmOutcomeHandler(action);
//			ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
//			ProcurementPlanResDTO resDTO = new ProcurementPlanResDTO();
//			reqDTO.setUpdateBudget(
//					(ProcurementPlanConst.MODE_REVISION.equals(this.getManagedBean().getViewMode())
//							|| (BpmOutcomeConst.BPM_REJECT.equals(action)))
//					?true:false);
//			reqDTO.setAction(action);
//			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
//			reqDTO.setTaskId(this.getManagedBean().getBpmTaskId());
//			reqDTO.setPlanMaster(this.getManagedBean().getPlanMaster());
//			IProcurementPlanSO procurementPlanSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IProcurementPlanSO.class);
//			if(!ProcurementPlanConst.MODE_DRAFT.equals(this.getManagedBean().getViewMode())){
//				resDTO =  procurementPlanSO.updatePlan(reqDTO);
//			}else{
//				resDTO =  procurementPlanSO.updatePlanMaster(reqDTO);
//			}
//			result = resDTO.isSuccessful();
//			log.info("updatePlan result: " + result);
//			if(result){
//				
//				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
//						FacesMessage.SEVERITY_INFO,"Record submit successfully.","Plan No(s). : "
//				+ this.getManagedBean().getPlanMaster().getPpPlans().get(0).getPlanNo()));
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Update plan failed."));
//			throw e;
//			
//		}
//
//		log.info("updatePlanMaster - End");
//		return result;
//	}
//	
//	/**
//	 * save plan to DB
//	 * @param action
//	 * @return plan with ID
//	 */
//	public boolean addPlan(String action)throws Exception{
//		log.info("addPlan - Start");
//		boolean result  = false;
//		List<String> planNos = new ArrayList<String>();
//		try{
//			ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
//			reqDTO.setPlanMaster(this.getManagedBean().getPlanMaster());
//			reqDTO.setAction(action);
//			reqDTO.setUpdateBudget(true);
//			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
//			IProcurementPlanSO procurementPlanSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IProcurementPlanSO.class);
//			ProcurementPlanResDTO resDTO = procurementPlanSO.addPlan(reqDTO);
//			if(resDTO.isSuccessful()){
//				result = resDTO.isSuccessful();
//				
//				for(PpPlan plan:this.getManagedBean().getPlanMaster().getPpPlans()){
//					planNos.add(plan.getPlanNo());
//				}
//				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
//						FacesMessage.SEVERITY_INFO,"Record submit successfully.","Plan No(s). : "
//				+ planNos.toString()));
//				log.info("Generated ID... " + resDTO.getPlanMaster().getPlanMasterId());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Save plan failed."));
//			throw e;
//		}
//		log.info("addPlan - End");
//		
//		return result;
//	}
//	
//	/**
//	 * update selected actors
//	 * @param planActor
//	 * @param plan
//	 */
//	public void changeActors(PpPlanActor planActor, PpPlan plan){
//		log.info("changeActors - Start");
//		if(!plan.getPpPlanActors().contains(planActor)){
//			for(PpPlanActor oriActor:plan.getPpPlanActors()){
//				if(planActor.getActorType().equals(oriActor.getActorType()) &&
//						planActor.getActorSeq()==oriActor.getActorSeq()){
//					//remove old
//					plan.getPpPlanActors().remove(oriActor);
//					break;
//				}
//			}
//			planActor.setPmUser(getActor(planActor.getUserId()));
//			//add new
//			plan.addPpPlanActors(planActor);
//		}
//		log.info("changeActors - End");
//		
//	}
	


	/**
	 * pre-render view for plan list
	 */
	public void preRenderPlanList(){
		log.info("preRenderPlanList");
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_LIST);
		FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ProcurementPlanConst.NAV_TO_LIST_PLAN);
	}
//	/**
//	 * the pre-render view to load plan from bpm task
//	 */
//	public void preRenderPlanDet() {
//		log.info("preRenderPlanDet - Start");
//		HttpServletRequest servletRequest = PortalUtil
//				.getHttpServletRequest(new FacesScopes().getRequest());
//		HttpServletRequest originalServletRequest = PortalUtil
//				.getOriginalServletRequest(servletRequest);
//		String taskId = originalServletRequest.getParameter("taskId");
//		System.out.println("taskId:" + taskId);
//
//		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
//				.getServiceObject(IBpmSO.class);
//		String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
//		BpmRequestDTO reqDTO = new BpmRequestDTO();
//
//		reqDTO.setCtxToken(ctx);
//		reqDTO.setTaskId(taskId);
//		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
//		Object obj = res.getPojo();
//
//
//		// Procurement Plan - Plan
//		PPPlanDataType planObj = (PPPlanDataType) obj;
//		PpPlan plan = retrievePlanById(planObj.getPlanId().longValue());
//		
//
//		PlanBackingBean planBB = new PlanBackingBean();
//		planBB.getManagedBean().setBpmTaskId(taskId);
//		planBB.getManagedBean().setActions(res.getAction());
//		planBB.loadTask(plan, res.getTaskName());
//		log.info("preRenderPlanDet - End");
//
//
//		FacesContext fc = FacesContext.getCurrentInstance();
//	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ProcurementPlanConst.NAV_TO_PLAN_DET);
//
//	}
	
//	/**
//	 * retrive plan by plan id
//	 * @param planId
//	 * @return PpPlan
//	 */
//	private PpPlan retrievePlanById(Long planId){
//		log.info("retrievePlanById - Start");
//		ProcurementPlanReqDTO planReqDTO = new ProcurementPlanReqDTO();
//		PpPlan plan = new PpPlan();
//		try{
//			if(CommonMethod.isSelected(planId)){
//				planReqDTO.setPlanId(planId);
//				IProcurementPlanSO planSO = (IProcurementPlanSO) BusinessDelegate
//						.getInstance().getServiceObject("pp", IProcurementPlanSO.class);
//				ProcurementPlanResDTO planResDTO = planSO.retrievePlanById(planReqDTO);
//				if(planResDTO.isSuccessful()){
//					plan = planResDTO.getPlan();
//				}
//			}
//		}catch(Exception e){
//			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR, e.toString(),"Retrieve plan failed."));
//			e.printStackTrace();
//		}
//		log.info("retrievePlanById - End");
//		return plan;
//
//	}
	
	/**
	 * Plan list paging class
	 * @author rain poh
	 */
	private class PlanPagingDataModel extends PagingDataModel<PpPlan> {
		private static final long serialVersionUID = 1L;

		public PlanPagingDataModel() {
			super("dtPlanResult");
		}

		
		/**
		 * @author htpoh
		 * @since 22.11.2013
		 * retrieve default plans by org profile id
		 * 
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PpPlan> pageChangeListener(ISearchBean<PpPlan> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}
			/*if (super.getWrappedData() != null) {
				SearchResultBean<PpBudget> resultBean = new SearchResultBean<PpBudget>();
				resultBean.setResultList((List<PpBudget>)super.getWrappedData());
				resultBean.setTotalRecord(super.getRowCount());
				
				return resultBean;
			}*/
			
			try {				
				
				
				ISearchBean<PpPlan> searchBeanData = getSearchBean(searchBean);
				IProcurementPlanSO procurementPlanSO = (IProcurementPlanSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IProcurementPlanSO.class);
				ProcurementPlanReqDTO reqDTO = new ProcurementPlanReqDTO();
				reqDTO.setSearchBean(searchBeanData);
				ProcurementPlanResDTO resDTO = procurementPlanSO.retrievePlanListByParams(reqDTO);
				if(resDTO.isSuccessful()){
					return resDTO.getPpPlanResultBean();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	/**
	 * @return the planDataModel
	 */
	public IPagingDataModel<PpPlan> getPlanDataModel() {
		return planDataModel;
	}
	/**
	 * @param planDataModel the planDataModel to set
	 */
	public void setPlanDataModel(IPagingDataModel<PpPlan> planDataModel) {
		this.planDataModel = planDataModel;
	}
	
	/**
	 * allocated budget list paging class
	 * @author rain poh
	 */
	private class BudgetAllocPagingDataModel extends PagingDataModel<PpBudgetAllocDtl> {
		
		private static final long serialVersionUID = 1L;

		public BudgetAllocPagingDataModel() {
			super("dtBudgetAlloc");
		}

		
		/**
		 * @author rain poh
		 * @since 2.12.2013
		 * retrieve default budget by ptj profile id
		 * 
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PpBudgetAllocDtl> pageChangeListener(ISearchBean<PpBudgetAllocDtl> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			
			try {				
				

				BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
				
//				searchBean.getDataFilterBeans().add(new SearchFilterBean(
//						"ppBudgetAlloc.ptjOrgProfile.orgProfileId", 
//						SearchOperator.OPERATOR_EQUALS.value(), 
//						getManagedBean().getCurrentProfiles()));
//				reqDTO.setBudgetAllocDtlSearchBean(searchBean);	
				

//				
//	
//				IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
//				BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetAllocDtlSearchBean(reqDTO);
				
//				if (resDTO.isSuccessful()) {
//					return resDTO.getBudgetAllocDtlResultBean();
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}

	/**
	 * @return the budgetAllocDtlDataModel
	 */
	public IPagingDataModel<PpBudgetAllocDtl> getBudgetAllocDtlDataModel() {
		return budgetAllocDtlDataModel;
	}

	/**
	 * @param budgetAllocDtlDataModel the budgetAllocDtlDataModel to set
	 */
	public void setBudgetAllocDtlDataModel(
			IPagingDataModel<PpBudgetAllocDtl> budgetAllocDtlDataModel) {
		this.budgetAllocDtlDataModel = budgetAllocDtlDataModel;
	}



	
}
