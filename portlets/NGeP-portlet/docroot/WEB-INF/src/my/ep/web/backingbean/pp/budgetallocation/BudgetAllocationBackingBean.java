/*
 * Copyright © 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pp.budgetallocation;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pp.budgetallocation.IBudgetAllocationSO;
import my.ep.biz.service.pp.budgetallocation.IBudgetDisbursementSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationReqDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationResDTO;
import my.ep.dto.pp.budgetallocation.BudgetDisbursementReqDTO;
import my.ep.dto.pp.budgetallocation.BudgetDisbursementResDTO;
import my.ep.jaxb.pp.budgetdata.PPBudgetDataType;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAlloc;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocOrg;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetDtl;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.bpm.tasklist.BPMTaskListBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.pp.BudgetAllocSearchBean;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.pp.budgetallocation.BudgetAllocationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.CommonMethod;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.liferay.portal.util.PortalUtil;

@ManagedBean(name="budgetAllocBackingBean")
@ViewScoped
public class BudgetAllocationBackingBean extends BaseBackingBean<BudgetAllocationManagedBean>{
	private static Logger log = Logger.getLogger(BudgetAllocationBackingBean.class);

	private IPagingDataModel<PpBudgetDtl> budgetDtlDataModel = new BudgetDtlPagingDataModel();
	
	private IPagingDataModel<PpBudgetAlloc> budgetAllocDataModel = new BudgetAllocPagingDataModel();
	
	/**
	 * get faces scopes
	 * @return FacesScopes
	 */
	private FacesScopes getFacesScopes(){
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes;
	}
	
	/**
	 * constructor of the budget allocation program
	 */
	public BudgetAllocationBackingBean(){
	
		if(this.getManagedBean().getViewMode()==null || ProcurementPlanConst.MODE_LIST.equals(this.getManagedBean().getViewMode())){
			//set current user profiles - TODO
			if(this.getManagedBean().getCurrentProfiles()==null
					&& getFacesScopes().getUserSessionBean()!=null){
				List <PmOrgProfile> ministryProfiles =  CommonMethod.getOrgProfiles(getFacesScopes().getUserSessionBean().getUserId(), OrganizationType.MINISTRY.value());
				this.getManagedBean().setCurrentProfiles(ministryProfiles);
			}
		}

	}
	
	/**
	 * redirect to budget allocation details view from BPM & DB
	 * @param budgetAllocId
	 * @param status
	 * @return xhtml
	 */
	public String redirectToBudgetAllocation(Long budgetAllocId){
		log.info("redirectToBudgetAllocation - Start");
		try{
			if(budgetAllocId!=null && budgetAllocId>0){
				loadTask(retrieveBudgetAllocById(budgetAllocId), "APPROVED" );//ProcurementPlanConst.MODE_VIEW
			}else{
				cleanBudgetAllocDetails();
			}
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("redirectToBudgetAllocation - End");
		return ProcurementPlanConst.NAV_TO_BUDGET_ALLOCATION;
	
	}
	
	/**
	 * initial budget allocation clean view
	 */
	private void cleanBudgetAllocDetails(){
		log.info("cleanBudgetAllocDetails - End");
		this.getManagedBean().getBudgetAllocDtlMapper().clear();
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		this.getManagedBean().setOutComes(ProcurementPlanConst.BPM_DEFAULT_ACTIONS); 
		this.getManagedBean().setSelectedBudgetAllocation(null);
		this.getManagedBean().getSelectedBudgetAllocation().setMinistryOrgProfile(ministryOptions().get(0));
		this.getManagedBean().setSelectedBudgetAllocDtl(null);
		this.getManagedBean().setSelectedBudgetAllocOrg(null);
		this.getManagedBean().getSelectedBudgetAllocation().setPmStatus(CommonMethod.getDefaultStatus(ParameterConst.DOCTYPE_BUDGET_ALLOC));//TODO
		this.getManagedBean().getSelectedBudgetAllocation().setEditable(true);
		this.getManagedBean().setConfirm(true);
		initialBudgetAllocRemark();
		log.info("cleanBudgetAllocDetails - End");
	}
	
	/**
	 * get search bean 
	 * @return ISearchBean<PpBudgetAlloc>
	 */
	private ISearchBean<PpBudgetAlloc> getSearchBean(ISearchBean<PpBudgetAlloc> searchBean){
		log.info("getSearchBean - Start");
		BudgetAllocSearchBean baSearchBean = this.getManagedBean().getBudgetAllocSearchBean();
			
		//filter by profile
		SearchFilterBean defaultBean = new SearchFilterBean();
		defaultBean.setSearchField("ministryOrgProfile");
		defaultBean.setSearchOperator(SearchOperator.OPERATOR_IN.value());
		defaultBean.setSearchValue(this.getManagedBean().getCurrentProfiles());
		searchBean.getDataFilterBeans().add(defaultBean);
		if(baSearchBean.getBudgetAllocDesc()!=null && CommonMethod.isSelected(baSearchBean.getBudgetAllocDesc())){
			log.info("+ budgetAllocDesc " + baSearchBean.getBudgetAllocDesc());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("budgetAllocDesc");
			bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
			bean.setSearchValue(baSearchBean.getBudgetAllocDesc());
			searchBean.getDataFilterBeans().add(bean);
		}
		if(baSearchBean.getBudgetAllocNo()!=null && CommonMethod.isSelected(baSearchBean.getBudgetAllocNo())){
			log.info("+ budgetAllocNo " + baSearchBean.getBudgetAllocNo());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("budgetAllocNo");
			bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
			bean.setSearchValue(baSearchBean.getBudgetAllocNo());
			searchBean.getDataFilterBeans().add(bean);
		}
		if(baSearchBean.getPtjCode()!=null && CommonMethod.isSelected(baSearchBean.getPtjCode())){
			log.info("+ ptjCode " + baSearchBean.getPtjCode());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("ppBudgetAllocDtls.ppBudgetAllocOrgs.pmOrgProfile.orgCode");
			bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
			bean.setSearchValue(baSearchBean.getPtjCode());
			searchBean.getDataFilterBeans().add(bean);
		}
		if(baSearchBean.getGosodoCode()!=null && CommonMethod.isSelected(baSearchBean.getGosodoCode())){
			log.info("+ gosodoCode " + baSearchBean.getGosodoCode());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("ppBudgetAllocDtls.pmDo.doCode");
			bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
			bean.setSearchValue(baSearchBean.getGosodoCode());
			searchBean.getDataFilterBeans().add(bean);
			
		}
		if(baSearchBean.getAmanahCode()!=null && CommonMethod.isSelected(baSearchBean.getAmanahCode())){
			log.info("+ amanahCode " + baSearchBean.getAmanahCode());
			SearchFilterBean bean = new SearchFilterBean();
			bean.setSearchField("ppBudgetAllocDtls.pmMaksud.maksudCode");
			bean.setSearchOperator(SearchOperator.OPERATOR_LIKE.value());
			bean.setSearchValue(baSearchBean.getAmanahCode());
			searchBean.getDataFilterBeans().add(bean);
		}
		log.info("getSearchBean - End");
		return searchBean;
	}
	
	/**
	 * search budget alloc
	 */
	public void searchBudgetAlloc(){
		log.info("searchBudgetAlloc - Start");
		budgetAllocDataModel.reloadData();
		log.info("searchBudgetAlloc - End");
		
	}
	
	/**
	 * retrieve budget allocation by id
	 * @param budgetAllocId
	 * @return PpBudgetAlloc
	 */
	private PpBudgetAlloc retrieveBudgetAllocById(Long budgetAllocId){
		PpBudgetAlloc budgetAlloc = new PpBudgetAlloc();
		if(budgetAllocId!=null){
			BudgetAllocationReqDTO budgetAllocReqDTO = new BudgetAllocationReqDTO();
			budgetAllocReqDTO.setBudgetAllocId(budgetAllocId);
			IBudgetAllocationSO budgetAllocSO = (IBudgetAllocationSO) BusinessDelegate.getInstance()
					.getServiceObject("pp", IBudgetAllocationSO.class);
			BudgetAllocationResDTO budgetAllocResSO = budgetAllocSO.retrieveBudgetAllocationById(budgetAllocReqDTO);
			if(budgetAllocResSO.isSuccessful()){
				budgetAlloc = budgetAllocResSO.getBudgetAlloc();
			}
		}
		return budgetAlloc;
	}
	
	public void test(){
		System.out.println("teeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee1");
		System.out.println(" >> " + this.getManagedBean().getSelectedBudgetAllocDtl().getPmDo().getDoCode());
		System.out.println("teeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee2");	
	}
	
	/**
	 * load task from task list
	 * @param budgetAlloc
	 * @param taskName
	 */
	public void loadTask(PpBudgetAlloc budgetAlloc, String taskName){
		log.info("loadTask - Start");
		log.info("taskName "  + taskName);
		if(budgetAlloc!=null && taskName!=null){
			this.getManagedBean().getBudgetAllocDtlMapper().clear();
			
			this.getManagedBean().setSelectedBudgetAllocation(budgetAlloc);
			this.getManagedBean().getSelectedBudgetAllocation().setPpRemarks(budgetAlloc.getPpRemarks());
			this.getManagedBean().setSelectedBudgetAllocDtl(null);
			this.getManagedBean().setSelectedBudgetAllocOrg(null);
			//1 Budget Allocation 1 GO only
			this.getManagedBean().getSelectedBudgetAllocation().setPpBudget(
					budgetAlloc.getPpBudgetAllocDtls().get(0).getPpBudgetDtl().getPpBudget());
			this.getManagedBean().setConfirm(false);
			initialBudgetAllocRemark();
			//keep track budget changes
			for(PpBudgetAllocDtl item:budgetAlloc.getPpBudgetAllocDtls()){
				updateBudgetAllocDtl(item, ProcurementPlanConst.INIT);
			}
			
//			this.getManagedBean().getSelectedBudgetAllocation().setPpWorkflowStatus(CommonMethod.getDefaultStatus(ProcurementPlanConst.DOCTYPE_BUDGET_ALLOC));//TODO
			if(ProcurementPlanConst.MODE_VIEW.equals(taskName)){
				this.getManagedBean().setOutComes(ProcurementPlanConst.BPM_DEFAULT_ACTIONS); 
				this.getManagedBean().getSelectedBudgetAllocation().setEditable(false);
				this.getManagedBean().setViewMode(taskName);
			}else if(ProcurementPlanConst.BPM_TASK_BUDGET_REVIEW.equals(taskName)){
				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_APPROVAL);
				this.getManagedBean().getSelectedBudgetAllocation().setEditable(true);
			}else if(ProcurementPlanConst.BPM_TASK_BUDGET_REVISE.equals(taskName)){
				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_REVISION);
				this.getManagedBean().getSelectedBudgetAllocation().setEditable(true);
			}else if("APPROVED".equals(taskName)){ //TODO after approval view
				this.getManagedBean().setOutComes(ProcurementPlanConst.BPM_DEFAULT_ACTIONS); 
				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_MAINTAIN);
				this.getManagedBean().getSelectedBudgetAllocation().setEditable(true);
			}
		}
		log.info("loadTask - End");
	}
	
	
	
	/**
	 * pre-render for the budget alloation content page
	 */
	public void preRenderView() {
		log.info("preRenderView - Start");
		try{
			BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
			HttpServletRequest servletRequest = PortalUtil
					.getHttpServletRequest(new FacesScopes().getRequest());
			HttpServletRequest originalServletRequest = PortalUtil
					.getOriginalServletRequest(servletRequest);
			String taskId = originalServletRequest.getParameter("taskId");
		
			System.out.println("taskId:" + taskId);

	
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
					.getServiceObject(IBpmSO.class);
			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
			BpmRequestDTO reqDTO = new BpmRequestDTO();
	
			reqDTO.setCtxToken(ctx);
			reqDTO.setTaskId(taskId);
			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
			Object obj = res.getPojo();
	
			// Procurement Plan - Budget
			PPBudgetDataType budgetObj = (PPBudgetDataType) obj;
			PpBudgetAlloc budgetAlloc = retrieveBudgetAllocById(budgetObj.getBudgetId().longValue());
		
	
			BudgetAllocationBackingBean budgetBB = new BudgetAllocationBackingBean();
			budgetBB.getManagedBean().setBpmTaskId(taskId);
			budgetBB.getManagedBean().setOutComes(res.getAction());
			budgetBB.loadTask(budgetAlloc, res.getTaskName());
			
			log.info("preRenderView - End");
		    FacesContext fc = FacesContext.getCurrentInstance();
		    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ProcurementPlanConst.NAV_TO_BUDGET_ALLOCATION);
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	

	/**
	 * populate the budget allocation dtl 
	 * @param ppBudgetAllocDtl
	 */
	public void showBudgetAllocDtl(PpBudgetAllocDtl ppBudgetAllocDtl){
		log.info("showBudgetAllocDtl - Start");
		boolean isValid = true;
		String invalidMsg = "";
		
		
		//only allowed new entry if valid
		if(ppBudgetAllocDtl==null){
			this.getManagedBean().setSelectedBudgetAllocDtl(new PpBudgetAllocDtl());
			this.getManagedBean().getSelectedBudgetAllocDtl().setEditable(true);
			this.getManagedBean().getSelectedBudgetAllocOrg().setUsedAmt(new BigDecimal(0));
			this.getManagedBean().getSelectedBudgetAllocDtl().setPmStatus(
					CommonMethod.getDefaultStatus(ParameterConst.DOCTYPE_BUDGET_ALLOC_DTL));//TODO	
			
			//ensure the previous entry is not null
			if(!this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls().isEmpty()){
				PpBudgetAllocDtl tempPpBudgetAllocDtl = new PpBudgetAllocDtl();
				tempPpBudgetAllocDtl = this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls().get(
						this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls().size()-1);
				//all transaction must not be empty or invalid 
				if(tempPpBudgetAllocDtl.getPpBudgetAllocOrgs()==null 
						|| (tempPpBudgetAllocDtl.getPpBudgetAllocOrgs()!=null && tempPpBudgetAllocDtl.getPpBudgetAllocOrgs().isEmpty())){
					isValid = false;
					invalidMsg = "PTJ Allocation is empty";
				}
			}
			if(!isValid){
				if(this.getManagedBean().getAmanahFlag()){
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
							FacesMessage.SEVERITY_ERROR,"Amanah Disbursement.", invalidMsg));
				}else{
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
							FacesMessage.SEVERITY_ERROR,"GOSODO Disbursement.", invalidMsg));
				}
				
			}else{
				if(this.getManagedBean().getAmanahFlag()){
					RequestContext.getCurrentInstance().execute("dlItemAmanahDisModal.show()");
				}else{
					RequestContext.getCurrentInstance().execute("dlItemBudgetDisModal.show()");
				}	
			}
		}else{
			
			if(ProcurementPlanConst.MODE_VIEW.equals(this.getManagedBean().getViewMode())
					|| ppBudgetAllocDtl.isBrandNew()){
				ppBudgetAllocDtl.setEditable(false);
			}else{
				ppBudgetAllocDtl.setEditable(true);
			}
			ppBudgetAllocDtl.setSelected(true);
			this.getManagedBean().setSelectedBudgetAllocDtl(ppBudgetAllocDtl);
		}
		
		
		log.info("showBudgetAllocDtl - End");
	}
	
	
	/**
	 * add budget allocation dtl to the list
	 */
	public void addBudgetAllocDtlList(){
		log.info("addBudgetAllocDtlList - Start");
		try{
			boolean isValid = false;
			
			//Initial item list if empty
			if (this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls()== null){
				this.getManagedBean().getSelectedBudgetAllocation().setPpBudgetAllocDtls(new ArrayList<PpBudgetAllocDtl>());
			}
			
			isValid = verifyBudgetAllocDtl(
						this.getManagedBean().getSelectedBudgetAllocDtl(), 
						this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetDtl(),
						false);
			isValid = uniqueGOSODO(this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetDtl().getBudgetDtlId(),
					this.getManagedBean().getSelectedBudgetAllocDtl().getPmDo().getDoId());
			if(this.getManagedBean().getSelectedBudgetAllocDtl().isBrandNew() && !this.getManagedBean().getSelectedBudgetAllocDtl().isSelected()){
				if(isValid){
					//update amount
					updateBudgetAllocDtl(this.getManagedBean().getSelectedBudgetAllocDtl(), ProcurementPlanConst.ADD);
					this.getManagedBean().getSelectedBudgetAllocation().addPpBudgetAllocDtls(this.getManagedBean().getSelectedBudgetAllocDtl());
					
					if(ProcurementPlanConst.MODE_MAINTAIN.equals(this.getManagedBean().getViewMode())){
						//TODO: add this to BPM new list
					}
				}
			}else{
				if(isValid){
					if(this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetDtl().getBudgetDtlId().equals(
							this.getManagedBean().getSelectedBudgetAllocDtl().getOriBudgetDtlId())){
						//update amount
						updateBudgetAllocDtl(this.getManagedBean().getSelectedBudgetAllocDtl(), ProcurementPlanConst.UPDATE);
						
						if(ProcurementPlanConst.MODE_MAINTAIN.equals(this.getManagedBean().getViewMode())){
							//TODO: add this to BPM changed list
						}
					}else{
						//not allow change if having childs
						//rollback amount
						updateBudgetAllocDtl(this.getManagedBean().getSelectedBudgetAllocDtl(), ProcurementPlanConst.CHANGE);
					}
				}
			}
			if(isValid){
				RequestContext.getCurrentInstance().execute("dlItemBudgetDisModal.hide()");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	
		log.info("addBudgetAllocDtlList - End");
	}
	public void addAmanahDtlList(){
		log.info("addAmanahDtlList - Start");
		log.info("addAmanahDtlList - End");
	}
	
	
	
	/**
	 * delete budget allocation dtl from the list
	 * @param item
	 */
	public void deleteBudgetAllocDtl(PpBudgetAllocDtl ppBudgetAllocDtl){
		log.info("deleteBudgetAllocDtl - Start");
		boolean isValid = false;
		
		isValid = verifyBudgetAllocDtl(
					ppBudgetAllocDtl, 
					this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetDtl(),
					true);
		if(isValid){
			if(ppBudgetAllocDtl!=null){
				if(!ppBudgetAllocDtl.isBrandNew()&& !ppBudgetAllocDtl.isDeleted()){
					ppBudgetAllocDtl.setDeleted(true);
					ppBudgetAllocDtl.setRecordStatus(false);
				}else{
					this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls().remove(ppBudgetAllocDtl);
				}
				//rollback amount
				updateBudgetAllocDtl(ppBudgetAllocDtl, ProcurementPlanConst.REMOVE);
				this.getManagedBean().setSelectedBudgetAllocDtl(null);
				this.getManagedBean().setSelectedBudgetAllocOrg(null);
			}
		}
		log.info("deleteBudgetAllocDtl - End");
	}

	
	/**
	 * calculate budget dtl amt
	 * @param item
	 * @param mode
	 */
	public void updateBudgetAllocDtl(PpBudgetAllocDtl item, String mode){
		log.info("updateBudgetAllocDtl - Start");
		log.info("mode " + mode);
		try{
			log.info("mapper keys " + this.getManagedBean().getBudgetAllocDtlMapper().keySet());
			log.info("mapper values " +this.getManagedBean().getBudgetAllocDtlMapper().values());
			long key = item.getPpBudgetDtl().getBudgetDtlId();
			
			if(ProcurementPlanConst.CHANGE.equals(mode)){
				//change GOSO
				long oldKey = item.getOriBudgetDtlId();
				//remove claim amt from old budget dtl
				this.getManagedBean().getBudgetAllocDtlMapper().put(
						oldKey, 
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getBudgetAllocDtlMapper().get(oldKey), 
								item.getOriAllocAmt())
				);
				//update claim amt from new budget dtl
				this.getManagedBean().getBudgetAllocDtlMapper().put(
						key, 
						CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getBudgetAllocDtlMapper().get(key),
								item.getAllocAmt())
				);
				//update used amt from new budget dtl
//				item.getPpBudgetDtl().setUsedAmt(
//					CommonMethod.addBigDecimalNullable(
//							item.getPpBudgetDtl().getUsedAmt(),
//							this.getManagedBean().getBudgetAllocDtlMapper().get(key))
//				);
				item.setOriAllocAmt(item.getAllocAmt());//update ori amount to new amount
				item.setOriBudgetDtlId(item.getPpBudgetDtl().getBudgetDtlId());//update ori budget to new budget
			}else if(ProcurementPlanConst.REMOVE.equals(mode)){
				//remove item
				//remove claim amt from budget dtl
				this.getManagedBean().getBudgetAllocDtlMapper().put(
						key, 
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getBudgetAllocDtlMapper().get(key), 
								item.getAllocAmt())
				);
				//remove used amt from budget dtl
//				item.getPpBudgetDtl().setUsedAmt(
//						CommonMethod.substractBigDecimalNullable(
//								item.getPpBudgetDtl().getUsedAmt(),
//								item.getAllocAmt())
//				);
			}else if(ProcurementPlanConst.ADD.equals(mode)){
				//add new item
				//update claim amt from budget dtl
				this.getManagedBean().getBudgetAllocDtlMapper().put(
						key, 
						CommonMethod.addBigDecimalNullable(
							this.getManagedBean().getBudgetAllocDtlMapper().get(key),
							item.getAllocAmt())
				);
				//update used amt from budget dtl
//				item.getPpBudgetDtl().setUsedAmt(
//						CommonMethod.addBigDecimalNullable(item.getPpBudgetDtl().getUsedAmt(), 
//						item.getAllocAmt()));
				item.setOriAllocAmt(item.getAllocAmt());//initiate ori amount
				item.setOriBudgetDtlId(item.getPpBudgetDtl().getBudgetDtlId());//initiate ori budget
			}else if(ProcurementPlanConst.UPDATE.equals(mode)){
				//update item same GOSO
				BigDecimal newAllocAmt = new BigDecimal(0);
				newAllocAmt = CommonMethod.substractBigDecimalNullable(
						item.getAllocAmt(),
						item.getOriAllocAmt());
				//update claim amt from budget dtl
				this.getManagedBean().getBudgetAllocDtlMapper().put(
						key, 
						CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getBudgetAllocDtlMapper().get(key),
								newAllocAmt)
				);
				//update used amt from budget dtl
//				item.getPpBudgetDtl().setUsedAmt(
//						CommonMethod.addBigDecimalNullable(
//								item.getPpBudgetDtl().getUsedAmt(),
//								newAllocAmt)
//				);
				item.setOriAllocAmt(item.getAllocAmt());//update ori amount to new amount
			}else if(ProcurementPlanConst.INIT.equals(mode)){
				//update claim amt from budget dtl
				this.getManagedBean().getBudgetAllocDtlMapper().put(
						key, new BigDecimal(0));
				item.setOriAllocAmt(item.getAllocAmt());//initiate ori amount
				item.setOriBudgetDtlId(item.getPpBudgetDtl().getBudgetDtlId());//initiate ori budget
			}
			item.getPpBudgetDtl().setFloatingAllocAmt(this.getManagedBean().getBudgetAllocDtlMapper().get(key));
			log.info("mapper keys " + this.getManagedBean().getBudgetAllocDtlMapper().keySet());
			log.info("mapper values " +this.getManagedBean().getBudgetAllocDtlMapper().values());
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("updateBudgetAllocDtl - End");
	}
	
//	
//	public void badAlloAmtChangeListener(ValueChangeEvent event){
//		log.info("badAlloAmtChangeListener - Start");
//		Object oldValueObj = event.getOldValue();
//	    Object newValueObj = event.getNewValue();
//	    BigDecimal oldValue = oldValueObj!=null ? (BigDecimal)oldValueObj : new BigDecimal(0);
//		BigDecimal newValue = newValueObj!=null ? (BigDecimal)newValueObj : new BigDecimal(0);
//		this.getManagedBean().getSelectedBudgetAllocDtl().setOriAllocAmt(oldValue);
//		
//		log.info("badAlloAmtChangeListener - End");
//	}
//	
	
	/**
	 * populate the budget allocation org 
	 * @param ppBudgetAllocOrg
	 */
	public void showBudgetAllocOrg(PpBudgetAllocOrg ppBudgetAllocOrg){
		log.info("showBudgetAllocOrg - Start");
		if(ppBudgetAllocOrg==null){
			log.info("showBudgetAllocOrg - null");
			this.getManagedBean().setSelectedBudgetAllocOrg(null);
			this.getManagedBean().getSelectedBudgetAllocOrg().setEditable(true);
			this.getManagedBean().getSelectedBudgetAllocOrg().setUsedAmt(new BigDecimal(0));
			this.getManagedBean().getSelectedBudgetAllocOrg().setPmStatus(
					CommonMethod.getDefaultStatus(ParameterConst.DOCTYPE_BUDGET_ALLOC_ORG));//TODO
		}else{
			log.info("showBudgetAllocOrg - not null");
			if(ProcurementPlanConst.MODE_VIEW.equals(this.getManagedBean().getViewMode()) || 
					ppBudgetAllocOrg.isBrandNew()){
				ppBudgetAllocOrg.setEditable(false);
			}else{
				ppBudgetAllocOrg.setEditable(true);
			}
			ppBudgetAllocOrg.setSelected(true);
			this.getManagedBean().setSelectedBudgetAllocOrg(ppBudgetAllocOrg);
//			selectedGo();
		}
		log.info("showBudgetAllocOrg - End");
	}
	
	/**
	 * verify and add budget allocation org to the list
	 */
	public void addBudgetAllocOrgList(){
		log.info("addBudgetAllocOrgList - Start");
		boolean isValid = false;
		
		//Initial item list if empty
		if (this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetAllocOrgs()== null){
			this.getManagedBean().getSelectedBudgetAllocDtl().setPpBudgetAllocOrgs(new ArrayList<PpBudgetAllocOrg>());
		}
		System.out.println("1" + this.getManagedBean().getSelectedBudgetAllocOrg().getUsedAmt());
		isValid = verifyBudgetAllocOrg(
				this.getManagedBean().getSelectedBudgetAllocOrg(), 
				this.getManagedBean().getSelectedBudgetAllocDtl(), 
				false);
		isValid = uniquePTJ(this.getManagedBean().getSelectedBudgetAllocOrg().getPmOrgProfile().getOrgProfileId());
		
		if(this.getManagedBean().getSelectedBudgetAllocOrg().isBrandNew() && !this.getManagedBean().getSelectedBudgetAllocOrg().isSelected()){
			if(isValid){
				//update amount
				updateBudgetAllocOrg(this.getManagedBean().getSelectedBudgetAllocOrg(), ProcurementPlanConst.ADD);
				this.getManagedBean().getSelectedBudgetAllocDtl().addPpBudgetAllocOrgs(this.getManagedBean().getSelectedBudgetAllocOrg());
				
				if(ProcurementPlanConst.MODE_MAINTAIN.equals(this.getManagedBean().getViewMode())){
					//TODO: add this to BPM new list
				}
			}
		}else{
			if(isValid){
				//update amount
				updateBudgetAllocOrg(this.getManagedBean().getSelectedBudgetAllocOrg(), ProcurementPlanConst.UPDATE);
				if(ProcurementPlanConst.MODE_MAINTAIN.equals(this.getManagedBean().getViewMode())){
					//TODO: add this to BPM changed list
				}
			}
		}
		if(isValid){
			RequestContext.getCurrentInstance().execute("dlItemOrgAllocModal.hide()");
		}
				
		log.info("addBudgetAllocOrgList - End");
	}

	
	/**
	 * delete budget allocation dtl from the list
	 * @param item
	 */
	public void deleteBudgetAllocOrg(PpBudgetAllocOrg ppBudgetAllocOrg){
		log.info("deleteBudgetAllocOrg - Start");
		boolean isValid = false;
		isValid = verifyBudgetAllocOrg(
				this.getManagedBean().getSelectedBudgetAllocOrg(), 
				this.getManagedBean().getSelectedBudgetAllocDtl(), 
				true);
		if(isValid){
			if(ppBudgetAllocOrg!=null){
				if(!ppBudgetAllocOrg.isBrandNew()&& !ppBudgetAllocOrg.isDeleted()){
					ppBudgetAllocOrg.setDeleted(true);
					ppBudgetAllocOrg.setRecordStatus(false);
					//rollback amount
					updateBudgetAllocOrg(ppBudgetAllocOrg, ProcurementPlanConst.REMOVE);
				}else{
					this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetAllocOrgs().remove(ppBudgetAllocOrg);
				}
				//rollback amount
				updateBudgetAllocOrg(ppBudgetAllocOrg, ProcurementPlanConst.REMOVE);
				this.getManagedBean().setSelectedBudgetAllocOrg(null);
			
			}
		}
		log.info("deleteBudgetAllocOrg - End");
	}
	/**
	 * calculate budget dtl amt
	 * @param item
	 * @param mode
	 */
	public void updateBudgetAllocOrg(PpBudgetAllocOrg item, String mode){
		log.info("updateBudgetAllocOrg - Start");
		log.info("mode " + mode);
		try{
			log.info("budgetAllocDtl " + this.getManagedBean().getSelectedBudgetAllocDtl().getBudgetAllocDtlId());
			log.info("userAmt " + this.getManagedBean().getSelectedBudgetAllocDtl().getUsedAmt());
			if(ProcurementPlanConst.REMOVE.equals(mode)){
				this.getManagedBean().getSelectedBudgetAllocDtl().setFloatingAllocAmt(
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getSelectedBudgetAllocDtl().getFloatingAllocAmt(), 
								item.getOriAllocAmt())
						);
				//remove item
				//remove claim amt from budget dtl
//				this.getManagedBean().getSelectedBudgetAllocDtl().setUsedAmt(
//						CommonMethod.substractBigDecimalNullable(
//								this.getManagedBean().getSelectedBudgetAllocDtl().getUsedAmt(),
//								item.getAllocAmt())
//						);
			}else if(ProcurementPlanConst.ADD.equals(mode)){
				this.getManagedBean().getSelectedBudgetAllocDtl().setFloatingAllocAmt(
						CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getSelectedBudgetAllocDtl().getFloatingAllocAmt(), 
								item.getAllocAmt())
						);
				//add new item
				//update claim amt from budget dtl
//				this.getManagedBean().getSelectedBudgetAllocDtl().setUsedAmt(
//						CommonMethod.addBigDecimalNullable(
//								this.getManagedBean().getSelectedBudgetAllocDtl().getUsedAmt(),
//								item.getAllocAmt())
//						);
				item.setOriAllocAmt(item.getAllocAmt());//initiate ori amount
			}else if(ProcurementPlanConst.UPDATE.equals(mode)){
				
				BigDecimal newAllocAmt = new BigDecimal(0);
				newAllocAmt = CommonMethod.substractBigDecimalNullable(
						item.getAllocAmt(),
						item.getOriAllocAmt());
				//update claim amt from budget dtl
				this.getManagedBean().getSelectedBudgetAllocDtl().setFloatingAllocAmt(
						CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getSelectedBudgetAllocDtl().getFloatingAllocAmt(), 
								newAllocAmt)
						);
//				this.getManagedBean().getSelectedBudgetAllocDtl().setUsedAmt(
//						CommonMethod.addBigDecimalNullable(
//								this.getManagedBean().getSelectedBudgetAllocDtl().getUsedAmt(),
//								newAllocAmt)
//						);
				item.setOriAllocAmt(item.getAllocAmt());//update ori amount to new amount
			}else if(ProcurementPlanConst.INIT.equals(mode)){
				//update claim amt from budget dtl
				item.setOriAllocAmt(item.getAllocAmt());//initiate ori amount
			}
			log.info("budgetAllocDtl " + this.getManagedBean().getSelectedBudgetAllocDtl().getBudgetAllocDtlId());
			log.info("userAmt " + this.getManagedBean().getSelectedBudgetAllocDtl().getUsedAmt());
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("updateBudgetAllocOrg - End");
	}
		
	/**
	 * verify budget allocation details against the ppBudgetDtl
	 * @param newAllocAmt
	 * @param ppBudgetDtl
	 * @param isRemove
	 * @return boolean
	 */
	private boolean verifyBudgetAllocDtl(PpBudgetAllocDtl ppBudgetAllocDtl, PpBudgetDtl ppBudgetDtl, boolean isRemove){
		log.info("verifyBudgetAllocDtls - Start");
		BigDecimal bdMapperFloatAmt = new BigDecimal(0);
		BigDecimal badAllocAmt = new BigDecimal(0);
		BigDecimal bdFloatAllocAmt = new BigDecimal(0);
		BigDecimal badFloatUsedAmt = new BigDecimal(0);
		BigDecimal bdBalanceAmt = new BigDecimal(0);
		boolean isValid = false;
	
		bdMapperFloatAmt = CommonMethod.substractBigDecimalNullable(
				this.getManagedBean().getBudgetAllocDtlMapper().get(ppBudgetDtl.getBudgetDtlId()),
				ppBudgetAllocDtl.getOriAllocAmt());
		
		badAllocAmt = ppBudgetAllocDtl.getAllocAmt();
		
		if(isRemove){
			//find the difference (0-oriAllocAmt)
			badAllocAmt = CommonMethod.substractBigDecimalNullable(
							ppBudgetAllocDtl.getAllocAmt(),
							ppBudgetAllocDtl.getAllocAmt());
		}
		System.out.println("badAllocAmt" +  badAllocAmt);
		
		//actual balance amt (alloc amt - used amt)
		bdBalanceAmt = CommonMethod.substractBigDecimalNullable(
				ppBudgetDtl.getAllocAmt(),
				ppBudgetDtl.getUsedAmt());
		System.out.println("bdBalanceAmt" +  bdBalanceAmt);
		
		//actual balance amt - floating amt
		bdFloatAllocAmt =  CommonMethod.substractBigDecimalNullable( 
				bdBalanceAmt,
				bdMapperFloatAmt);
		System.out.println("bdFloatAllocAmt" +  bdFloatAllocAmt);
		
		badFloatUsedAmt =  CommonMethod.addBigDecimalNullable(
				ppBudgetAllocDtl.getUsedAmt(),
				ppBudgetAllocDtl.getFloatingAllocAmt());
		System.out.println("badFloatUsedAmt" +  badFloatUsedAmt);
		
		
		if(badAllocAmt.compareTo(bdFloatAllocAmt) == 1){
			isValid = false;
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "GOSODO Disbursement invalid." ,"Insufficient budget amount."));
		}else if(badFloatUsedAmt.compareTo(badAllocAmt) == 1){
			isValid = false;
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "GOSODO Disbursement invalid." ,"Allocate Amount had been used."));
		}else{
			isValid = true;
		}
		if(isRemove){
			if(ppBudgetAllocDtl.getPpBudgetAllocOrgs()!=null && !ppBudgetAllocDtl.getPpBudgetAllocOrgs().isEmpty()){
				isValid = false;
			}
		}
		
		log.info("isValid " + isValid);
		log.info("verifyBudgetAllocDtls - End");
		return isValid;
	}
	
	
	/**
	 * verify budget allocation org against the ppBudgetAllocDtl
	 * @param newAllocAmt
	 * @param ppBudgetAllocDtl
	 * @param isRemove
	 * @return boolean
	 */
	private boolean verifyBudgetAllocOrg(PpBudgetAllocOrg ppBudgetAllocOrg, PpBudgetAllocDtl ppBudgetAllocDtl, boolean isRemove){
		log.info("verifyBudgetAllocOrgs - Start");
		if(this.getManagedBean().isConfirm()){
			confirmUpdateBudgetAlloc();
		}
				
		BigDecimal badMapperFloatAmt = new BigDecimal(0);
		BigDecimal baoAllocAmt = new BigDecimal(0);
		BigDecimal badFloatAllocAmt = new BigDecimal(0);
		BigDecimal badBalanceAmt = new BigDecimal(0);
		boolean isValid = false;
	
		baoAllocAmt = ppBudgetAllocOrg.getAllocAmt();
		
		if(isRemove){
			//find the difference (0-oriAllocAmt)
			baoAllocAmt = CommonMethod.substractBigDecimalNullable(
					ppBudgetAllocOrg.getAllocAmt(),
					ppBudgetAllocOrg.getAllocAmt());
		}
		System.out.println("baoAllocAmt " + baoAllocAmt);
		
		//remove the old amt from floating
		badMapperFloatAmt = CommonMethod.substractBigDecimalNullable(
						ppBudgetAllocDtl.getFloatingAllocAmt(),
						ppBudgetAllocOrg.getOriAllocAmt());
		System.out.println("badMapperFloatAmt " + badMapperFloatAmt);
		
		//actual balance amt (alloc amt - used amt)
		badBalanceAmt = CommonMethod.substractBigDecimalNullable(
				ppBudgetAllocDtl.getAllocAmt(),
				ppBudgetAllocDtl.getUsedAmt());
		System.out.println("badBalanceAmt " + badBalanceAmt);
				
				
		//actual balance amt - floating amt	
		badFloatAllocAmt =  CommonMethod.substractBigDecimalNullable(
				badBalanceAmt,
				badMapperFloatAmt);

		if(baoAllocAmt.compareTo(badFloatAllocAmt) == 1){
			isValid = false;
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PTJ Allocation invalid." ,"Insufficient budget amount."));
		}else if(ppBudgetAllocOrg.getUsedAmt().compareTo(baoAllocAmt) == 1){
			isValid = false;
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "PTJ Allocation invalid." ,"Allocate Amount had been used."));
		}else{
			isValid = true;
		}

		if(isRemove){
			if(ppBudgetAllocOrg.getPpPlanDtls()!=null && !ppBudgetAllocOrg.getPpPlanDtls().isEmpty()){
				isValid = false;
			}
		}
		log.info("isValid " + isValid);
		log.info("verifyBudgetAllocOrg - Start");
		return isValid;
	}
	
	

	/**
	 * add remark to budget allocation level
	 */
	public void initialBudgetAllocRemark(){
		log.info("initialBudgetAllocRemark - Start");
		if(this.getManagedBean().getSelectedBudgetAllocation().getPpRemarks()==null){
			this.getManagedBean().getSelectedBudgetAllocation().setPpRemarks(new ArrayList<PpRemark>());
		}
		PpRemark remark = new PpRemark();
		remark.setCreatedBy(getFacesScopes().getUserSessionBean().getUserId());
		remark.setCreatedByObj(getFacesScopes().getUserSessionBean());
		remark.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		remark.setChangedBy(remark.getCreatedBy());
		remark.setChangedDate(remark.getCreatedDate());
		this.getManagedBean().getSelectedBudgetAllocation().addPpRemarks(remark, ParameterConst.DOCTYPE_BUDGET_ALLOC);//TODO check this

		log.info("initialBudgetAllocRemark - End");
	}

	/**
	 * redirect to budget allocation listing page
	 * @return xhtml
	 */
	public String redirectToListBudgetAllocation(){
		log.info("redirectToListBudgetAllocation");
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_LIST);
		return ProcurementPlanConst.NAV_TO_LIST_BUDGET_ALLOCATION;
	}
	

	/**
	 * generate header for view based on the viewMode
	 * @param viewMode
	 * @return generated header
	 */
	public String generateHeader(String viewMode){
		return CommonMethod.generateHeader(viewMode, ProcurementPlanConst.PROG_BUDGET);
	}
		
	/**
	 * retrieve and set approver
	 * @param userId
	 */
	public void selectedApprover(Long userId){
		log.info("selectedApprover - Start");
		PmUser user = new PmUser();
		
		try{
			if(userId!=null){
				UserReqDTO reqDTO = new UserReqDTO();
				IUserSO userSO = BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
				reqDTO.setUserId(userId);
				UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);
//				if(resDTO.isSuccessful()){
					user = resDTO.getPmUser();
//				}
				this.getManagedBean().getSelectedBudgetAllocation().setApproverUser(user);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		log.info("selectedApprover - End");
	
	}

	
//
//	/**
//	 * update budget allocation to DB
//	 * @param action
//	 * @return boolean
//	 */
//	public boolean updateBudgetAllocation(String action){
//		log.info("updateBudgetAllocation - Start");
//	
//		boolean result  = false;
//		try{
//			BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
//			if(BpmOutcomeConst.BPM_SUBMIT.equals(action)){
//				//rollback revision flag
//				PpBudgetAlloc butgetAlloc = this.getManagedBean().getSelectedBudgetAllocation();
//				for(PpBudgetAllocDtl p :butgetAlloc.getPpBudgetAllocDtls()){
//					p.setRevisionRequired(false);
//				}
//			}
//			reqDTO.setAction(action);
//			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
//			reqDTO.setTaskId(this.getManagedBean().getBpmTaskId());
//			reqDTO.setBudgetAllocation(this.getManagedBean().getSelectedBudgetAllocation());
//			reqDTO.setUpdateBudget(
//					(ProcurementPlanConst.MODE_REVISION.equals(this.getManagedBean().getViewMode())
//							|| (BpmOutcomeConst.BPM_REJECT.equals(action)))
//					?true:false);
//			reqDTO.setAction(action);
//			reqDTO.setSelectedGoMapper(this.getManagedBean().getSelectedGoMapper());
//			
//			IBudgetAllocationSO budgetSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
//			BudgetAllocationResDTO resDTO = budgetSO.updateBudgetAllocation(reqDTO);
//			
//			result = resDTO.isSuccessful();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			
//		}
//		log.info("updateBudgetAllocation - End");
//		return result;
//	}
//	
	/**
	 * calculate and update budget alloc dtl
	 */
	private void confirmUpdateBudgetAlloc(){
		log.info("confirmUpdateBudgetAlloc - Start");
		if(this.getManagedBean().isConfirm()){
			for(PpBudgetAllocDtl ppBudgetAllocDtl: this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls()){
				ppBudgetAllocDtl.setUsedAmt(
						CommonMethod.substractBigDecimalNullable(ppBudgetAllocDtl.getUsedAmt(), ppBudgetAllocDtl.getFloatingAllocAmt()));
			}
			this.getManagedBean().setConfirm(false);
		}else{
			for(PpBudgetAllocDtl ppBudgetAllocDtl: this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls()){
				ppBudgetAllocDtl.setUsedAmt(
						CommonMethod.addBigDecimalNullable(ppBudgetAllocDtl.getUsedAmt(), ppBudgetAllocDtl.getFloatingAllocAmt()));
			}
			this.getManagedBean().setConfirm(true);
		}
		
		log.info("confirmUpdateBudgetAlloc - End");
	}
	
	/**
	 * save budget allocation to DB
	 * @param action
	 * @return budget allocation with ID
	 */
	public boolean addBudgetAllocation(String action){
		log.info("addBudgetAllocation - Start");
		boolean result  = false;
		try{
		
			BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
			reqDTO.setBudgetAllocation(this.getManagedBean().getSelectedBudgetAllocation());
			reqDTO.setBudgetAllocDtlMapper(this.getManagedBean().getBudgetAllocDtlMapper());
			reqDTO.setAction(action);
			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
			if(!this.getManagedBean().getSelectedBudgetAllocation().isBrandNew()){
				reqDTO.setTaskId(this.getManagedBean().getBpmTaskId());
			}
			IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
			BudgetAllocationResDTO resDTO = reqSO.submitBudgetAllocation(reqDTO);
			if(resDTO.isSuccessful()){
				result = resDTO.isSuccessful();
				System.out.println("Generated ID... " + resDTO.getBudgetAlloc().getBudgetAllocId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Budget Allocation submit failed." ,e.getMessage()));
		}
		log.info("addBudgetAllocation - End");
		
		return result;
	}


	/**
	 * get all available budget only
	 * @return List <PpBudgetDtl>
	 */
	public List<PpBudget> populateGo(){
		log.info("populateGo - Start");
		List<PpBudget> ppBudgets = new ArrayList<PpBudget>();
		try{
			if(this.getManagedBean().getCurrentProfiles()!=null && !this.getManagedBean().getCurrentProfiles().isEmpty()){
				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
				reqDTO.setPmOrgProfiles(this.getManagedBean().getCurrentProfiles());	
				IBudgetDisbursementSO reqSO = (IBudgetDisbursementSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetDisbursementSO.class);	
				BudgetDisbursementResDTO resDTO = reqSO.retrieveDistinctBudgetList(reqDTO);
				
				if(resDTO.isSuccessful()){
					ppBudgets = resDTO.getBudgetList();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("populateGo - End");
		return ppBudgets;
	}
	
	
	/**
	 * retrieve and set the selected budget(GO)
	 * @param budgetId
	 */
	public void selectedGo(Long budgetId){
		log.info("selectedGo - Start");
		PpBudget ppBudget = new PpBudget();
		try{
			if(budgetId!=null){	
				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
				reqDTO.setBudgetId(budgetId);
				IBudgetDisbursementSO reqSO = (IBudgetDisbursementSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetDisbursementSO.class);
				BudgetDisbursementResDTO resDTO = reqSO.retrieveBudgetById(reqDTO);
				
				if (resDTO.isSuccessful()) {
					ppBudget = resDTO.getBudget(); 
				}
			}
			this.getManagedBean().getSelectedBudgetAllocation().setPpBudget(ppBudget);
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("selectedGo - End");
	}
	
	/**
	 * populate SO from budget dtl by budget id and ministry id
	 * @return List<PpBudgetDtl>
	 */
	public List<PpBudgetDtl> populateSo(Long budgetId) {
		log.info("populateSo - Start");
		log.info("go id" + budgetId);
		List<PpBudgetDtl> ppBudgetDtls = new ArrayList<PpBudgetDtl>();
		try{
			if(budgetId!=null && (this.getManagedBean().getCurrentProfiles()!=null && !this.getManagedBean().getCurrentProfiles().isEmpty())){
				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
				reqDTO.setPmOrgProfiles(this.getManagedBean().getCurrentProfiles());	
				reqDTO.setBudgetId(budgetId);
				IBudgetDisbursementSO reqSO = (IBudgetDisbursementSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetDisbursementSO.class);	
				BudgetDisbursementResDTO resDTO = reqSO.retrieveBudgetDtlsByParams(reqDTO);
				
				if(resDTO.isSuccessful()){
					ppBudgetDtls = resDTO.getBudgetDtlResultBean().getResultList();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("populateSo - End");
		return ppBudgetDtls;
		
	}

	/**
	 * retrieve budget by id
	 * @param budgetDtlId
	 */
	public void selectedBudgetDtl(Long budgetDtlId){
		log.info("selectedBudgetDtl - Start");
		log.info("id " + budgetDtlId);
		PpBudgetDtl budgetDtl = new PpBudgetDtl();
		
		try{
			if(budgetDtlId!=null){
				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
				reqDTO.setBudgetDtlId(budgetDtlId);
				IBudgetDisbursementSO reqSO = (IBudgetDisbursementSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetDisbursementSO.class);
				BudgetDisbursementResDTO resDTO = reqSO.retrieveBudgetDtlById(reqDTO);
				if(resDTO.isSuccessful()){
					budgetDtl = resDTO.getBudgetDtl();
				}
			}
			this.getManagedBean().getSelectedBudgetAllocDtl().setPpBudgetDtl(budgetDtl);
		}catch(Exception e){
			e.printStackTrace();
		}

		log.info("selectedBudgetDtl - End");
	}
	
	private boolean uniqueGOSODO(Long budgetDtlId, Long doId){
		log.info("uniqueGOSODO - Start");
		boolean isValid = true;

		//validate the last record with previous entered records
		if(budgetDtlId!=null && doId!=null &&
				(this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls()!=null 
				&& this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls().size()>1)){
			System.out.println("11");
			for(PpBudgetAllocDtl ppBudgetAllocDtl:this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls()){	
				if(budgetDtlId.equals(ppBudgetAllocDtl.getPpBudgetDtl().getBudgetDtlId()) && doId.equals(ppBudgetAllocDtl.getPmDo().getDoId())){
					isValid = false;
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "GOSODO Disbursement." ,"Duplicate SODO Code is selected."));
					break;
				}
			}
		}
		log.info("isValid " + isValid);
		log.info("uniqueGOSODO - End ");
		return isValid;
	}
	
	private boolean uniquePTJ(Long orgProfileId){
		log.info("uniquePTJ - Start");
		boolean isValid = true;
	
		//validate the last record with previous entered records
		System.out.println("this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetAllocOrgs().size() " +this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetAllocOrgs().size());
		if(orgProfileId!=null && (this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetAllocOrgs()!=null 
				&& this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetAllocOrgs().size()>1)){
			for(PpBudgetAllocOrg ppBudgetAllocOrg:this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetAllocOrgs()){	
				if(orgProfileId.equals(ppBudgetAllocOrg.getPmOrgProfile().getOrgProfileId())){
					isValid = false;
					FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
							FacesMessage.SEVERITY_ERROR, "PTJ Allocation." ,"Duplicate PTJ Code is selected."));
					break;
				}
			}
		}
	
		log.info("isValid " + isValid);
		log.info("uniquePTJ - End ");
		return isValid;
	}
	
	
	/**
	 * populate DO list by SO id
	 * @return DO list
	 */
	public List<PmDo> populateDo(Long soId) {
		log.info("populateDo - Start");
		log.info("id " + soId);
		List<PmDo> pmDos = new ArrayList<PmDo>();
		try{
			if(soId!=null){
				ObjectReqDTO reqDTO = new ObjectReqDTO();
				reqDTO.setSoId(soId);
				IObjectSO objectSO = (IObjectSO) BusinessDelegate.getInstance().getServiceObject("pm", IObjectSO.class);
				ObjectResDTO resDTO = objectSO.retrieveDOListBySOCode(reqDTO);
		//		if(resDTO.isSuccessful()){
					pmDos = resDTO.getPmDoList();
		//		}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("populateDo - End");
		return pmDos;
	}
	
	/**
	 * retrieve and set the PmDo when PmSo.doId is selected
	 * @param doId
	 */
	public void selectedDo(Long doId) {
		log.info("selectedDo - Start");
		log.info("id " + doId);
		PmDo pmDo = new PmDo();
		try{
			if(CommonMethod.isSelected(doId)){
				ObjectReqDTO reqDTO = new ObjectReqDTO();
				reqDTO.setDoId(doId);
				IObjectSO objectSO = (IObjectSO) BusinessDelegate.getInstance().getServiceObject("pm", IObjectSO.class);
				ObjectResDTO resDTO = objectSO.retrieveDOByDOId(reqDTO);
//				if(resDTO.isSuccessful()){
					pmDo = resDTO.getPmDo();
//				}
			}
			this.getManagedBean().getSelectedBudgetAllocDtl().setPmDo(pmDo);
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("selectedDo - End");
	
	}
	

	
	/**
	 * retrieve jabatan list by selected ministry id
	 * @return List<PmOrgProfile>
	 */
	public List<PmOrgProfile> populateJabatan(){
		log.info("populateJabatan");
		return getParentOrgProfile(
				this.getManagedBean().getSelectedBudgetAllocation().getPpBudget().getPmOrgProfile().getOrgProfileId(), 
				OrganizationType.DEPARTMENT.value());
				
	}
	

	/**
	 * retrieve ptj list by selected jabatan id
	 * @param jabatanId
	 * @return List<PmOrgProfile>
	 */
	public List<PmOrgProfile> populatePtj(Long jabatanId){
		log.info("populatePtj");
		return getParentOrgProfile(
				jabatanId, 
				OrganizationType.DIVISION.value());
	}
	
	/**
	 * retrieve and select org profile by id
	 * @param ptjId
	 */
	public void selectedOrgProfile(Long ptjId){
		log.info("selectedOrgProfile - Start");
//		if(ptjId!=null){
//			if(uniquePTJ(ptjId)){
				this.getManagedBean().getSelectedBudgetAllocOrg().setPmOrgProfile(CommonMethod.retrieveOrgProfile(ptjId));
//			}
//		}
		log.info("selectedOrgProfile - End");
	}
	
	/**
	 * retrieve ministry list by login user
	 * @return ministry Options
	 */
	public List<PmOrgProfile> ministryOptions() {
		log.info("ministryOptions");
		return CommonMethod.getOrgProfiles(getFacesScopes().getUserSessionBean().getUserId(), OrganizationType.MINISTRY.value());
	}
	
	/**
	 * retrieve user list by role
	 * @param roleCode
	 * @return List<PmUser>
	 */
	public List<PmUser> userWithRoleOptions(){
		log.info("userWithRoleOptions - Start");
		List<PmUser> options = new ArrayList<PmUser>();
		try{
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setRoleCode(RoleCode.PROGRAM_SECTOR.value());
			IUserSO reqSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject("pm", IUserSO.class);
			UserResDTO resDTO = reqSO.retrieveUserListByRole(reqDTO);
//				if(resDTO.isSuccessful()){
				options = resDTO.getUserList();
//				}
			log.info("result size: " +options.size());
		}catch (Exception e){
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR, e.toString(),"Role "+RoleCode.PROGRAM_SECTOR.value()));
			throw e;
			
		}
		log.info("userWithRoleOptions - End");
		return options;
	}

	/**
	 * retrieve child profiles from PM
	 * @param parentOrgProfileId
	 * @param type
	 * @return list of child
	 */
	private List <PmOrgProfile> getParentOrgProfile(Long parentOrgProfileId, String orgType){
		log.info("getParentOrgProfile - Start");
		log.info("parentOrgProfileId " + parentOrgProfileId);
		List <PmOrgProfile> profileList =  new ArrayList<PmOrgProfile>();
		try{
			if(parentOrgProfileId!=null){
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgType(orgType);
				reqDTO.setParentOrgProfileId(parentOrgProfileId);
				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileListByOrgType(reqDTO);
//				if(resDTO.isSuccessful()){
					profileList = resDTO.getOrgProfileList();
//				}
			}
			System.out.println("size " + profileList.size());
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("getParentOrgProfile - End");
		return profileList;
	}
	


	
	/**
	 * calculate balance amount
	 * @param budgetAmount
	 * @param usedAmount
	 * @return
	 */
	public BigDecimal balanceAmount(BigDecimal budgetAmount, BigDecimal usedAmount){
		return CommonMethod.substractBigDecimalNullable(budgetAmount,usedAmount);
	}

	/**
	 * calculate balance amount included floating amt
	 * @param budgetAmount
	 * @param usedAmount
	 * @param floatingAmount
	 * @return
	 */
	public BigDecimal balanceAmount(BigDecimal budgetAmount, BigDecimal usedAmount, BigDecimal floatingAmount){
		BigDecimal usedFloatAmt = CommonMethod.addBigDecimalNullable(floatingAmount,usedAmount);
		System.out.println("budgetAmount " + budgetAmount);
		System.out.println("usedAmount " + usedAmount);
		System.out.println("floatingAmount " + floatingAmount);
		return CommonMethod.substractBigDecimalNullable(budgetAmount,usedFloatAmt);
	}

	/**
	 * Amanah type options
	 * @return List<SelectItem>
	 */
	public List<SelectItem> amanahTypeOptions() {
		log.info("amanahTypeOptions");
		List<SelectItem>options = new ArrayList<SelectItem>();
		options.add(new SelectItem(ProcurementPlanConst.WITHOUT_VOT, "Without VOT (Amanah)"));
		options.add(new SelectItem(ProcurementPlanConst.WITH_VOT, "With VOT"));
		return options;
	}
	
	/**
	 * Statuses options type options
	 * @return SelectItem[]
	 */
	public SelectItem[] statusDropdownValues(){
		return getStatusDropdownValues(ProcurementPlanConst.MODULE, ParameterConst.DOCTYPE_BUDGET_ALLOC);
	}
	
	/**
	 * check if GOSODO & PTJ is exists in the database.
	 * @return boolean
	 */
	private boolean verifyDuplicateBudgetAndOrg(){
		log.info("verifyDuplicateBudgetAndOrg - Start");
		boolean result = true;
		List<PpBudgetDtl> ppBudgetDtls = new ArrayList<PpBudgetDtl>();
		List<PmDo> pmDos = new ArrayList<PmDo>();;
		List<PmOrgProfile> pmOrgProfiles = new ArrayList<PmOrgProfile>();
		
		for(PpBudgetAllocDtl ppBudgetAllocDtl:this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls()){
			pmDos.add(ppBudgetAllocDtl.getPmDo());
			ppBudgetDtls.add(ppBudgetAllocDtl.getPpBudgetDtl());
			for(PpBudgetAllocOrg ppBudgetAllocOrg:ppBudgetAllocDtl.getPpBudgetAllocOrgs()){
				pmOrgProfiles.add(ppBudgetAllocOrg.getPmOrgProfile());
			}
		}
		ISearchBean<PpBudgetAlloc> searchBean = new SearchBean<PpBudgetAlloc>();
		if(!ppBudgetDtls.isEmpty()){
			SearchFilterBean searchFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
			searchFilterBean.setSearchField("ppBudgetAllocDtls.ppBudgetDtl");
			searchFilterBean.setSearchValue(ppBudgetDtls);
			searchBean.getDataFilterBeans().add(searchFilterBean);
		}
		
		if(!pmDos.isEmpty()){
			SearchFilterBean searchFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
			searchFilterBean.setSearchField("ppBudgetAllocDtls.pmDo");
			searchFilterBean.setSearchValue(pmDos);
			searchBean.getDataFilterBeans().add(searchFilterBean);
		}
		
		if(!pmOrgProfiles.isEmpty()){
			SearchFilterBean searchFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_IN.value());
			searchFilterBean.setSearchField("ppBudgetAllocDtls.ppBudgetAllocOrgs.pmOrgProfile");
			searchFilterBean.setSearchValue(pmOrgProfiles);
			searchBean.getDataFilterBeans().add(searchFilterBean);
		}
		//excluded the current budget allocation doc
		if(!this.getManagedBean().getSelectedBudgetAllocation().isBrandNew()){
			SearchFilterBean searchFilterBean = new SearchFilterBean(SearchOperator.OPERATOR_NOT_EQUALS.value());
			searchFilterBean.setSearchField("budgetAllocNo");
			searchFilterBean.setSearchValue(this.getManagedBean().getSelectedBudgetAllocation().getBudgetAllocNo());
			searchBean.getDataFilterBeans().add(searchFilterBean);
		}
		
		BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
		reqDTO.setBudgetAllocSearchBean(searchBean);
		
		IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
		BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetAllocByParams(reqDTO);
		if(resDTO.isSuccessful()){
			result = resDTO.getBudgetAllocResultBean().getResultList().isEmpty()? false : true;
			
			if(result){
				List <PpBudgetAlloc> ppBudgetAllocs = resDTO.getBudgetAllocResultBean().getResultList();
				for(PpBudgetAlloc a:ppBudgetAllocs){
					System.out.println("a " + a.getBudgetAllocId());
				}
				FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "Duplicate GOSODO and PTJ found." ,""));
			}
			System.out.println("result... " + result);
		}
		log.info("verifyDuplicateBudgetAndOrg - End");
		return result;
	}
	/**
	 * submit budget allocation when button is clicked
	 * @param action
	 * @return xhtml
	 */
	public String submitBudgetAllocation(String action) throws Exception {
		log.info("submitBudgetAllocation - Start");
		boolean isSuccess = false;
		boolean isValid = true;
		String invalidMsg = "";
		String returnPage = "";
		
		//summary is empty
		if(this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls()==null
				|| (this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls()!=null 
				&& this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls().isEmpty()) ){
			isValid = false;
			invalidMsg = "GOSODO Disbursement is empty.";
		}else{
			//organization is empty
			for(PpBudgetAllocDtl ppBudgetAllocDtl:this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls()){
				if(ppBudgetAllocDtl.getPpBudgetAllocOrgs()==null || (ppBudgetAllocDtl.getPpBudgetAllocOrgs()!=null && ppBudgetAllocDtl.getPpBudgetAllocOrgs().isEmpty())){
					isValid = false;
					invalidMsg = "GOSODO Disbursement Transaction for  " + ppBudgetAllocDtl.getPmDo().getDoCode() + " is empty.";
					break;
				}
			}
		}
		if(isValid){
			//validate GOSODO & PTJ as a pair
			if(!verifyDuplicateBudgetAndOrg()){
				//confirm organization budget
				if(!this.getManagedBean().isConfirm()){
					confirmUpdateBudgetAlloc();
				}
				if(this.getManagedBean().getSelectedBudgetAllocation().getBudgetAllocNo()==null){
					this.getManagedBean().getSelectedBudgetAllocation().setBudgetAllocNo(
							CommonMethod.generateDocNumber(
									false, null, null, this.getManagedBean().getSelectedBudgetAllocation().getMinistryOrgProfile()));
				}
				isSuccess = addBudgetAllocation(action);
			}
			
		}else{
			FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(
					FacesMessage.SEVERITY_ERROR,"Budget Disbursement submit failed.",invalidMsg));
		}

	
		log.info("completed: " + isSuccess);
		if(isSuccess){
			returnPage = ProcurementPlanConst.NAV_TO_TASK_LIST;
		}
		log.info("submitBudgetAllocation - End");
		return returnPage;
	}
	
	/**
	 * paginate for budget dtl list
	 * @author RainPoh
	 *
	 */
	private class BudgetDtlPagingDataModel extends PagingDataModel<PpBudgetDtl> {
		private static final long serialVersionUID = 1L;

		public BudgetDtlPagingDataModel() {
			super("dtSumBudget");
		}
		/**
		 * @author rainpoh
		 * @since 19.12.2013
		 * retrieve default budget dtl by ministry id
		 * 
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PpBudgetDtl> pageChangeListener(ISearchBean<PpBudgetDtl> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			try {				
				
				List <PmOrgProfile> ministryProfiles = CommonMethod.getOrgProfiles(getFacesScopes().getUserSessionBean().getUserId(), OrganizationType.MINISTRY.value());// TODO dynamic
//				Long ministryId = ministryProfiles!=null ? ministryProfiles.get(0).getOrgProfileId() : 0L;

				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
				reqDTO.setPmOrgProfiles(ministryProfiles);
				IBudgetDisbursementSO reqSO = (IBudgetDisbursementSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetDisbursementSO.class);	
				BudgetDisbursementResDTO resDTO = reqSO.retrieveBudgetDtlsByParams(reqDTO);

				if (resDTO.isSuccessful()) {
					return resDTO.getBudgetDtlResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}



	/**
	 * @return the budgetDtlDataModel
	 */
	public IPagingDataModel<PpBudgetDtl> getBudgetDtlDataModel() {
		return budgetDtlDataModel;
	}

	/**
	 * @param budgetDtlDataModel the budgetDtlDataModel to set
	 */
	public void setBudgetDtlDataModel(
			IPagingDataModel<PpBudgetDtl> budgetDtlDataModel) {
		this.budgetDtlDataModel = budgetDtlDataModel;
	}

	/**
	 * paginate for budget dtl list
	 * @author RainPoh
	 *
	 */
	private class BudgetAllocPagingDataModel extends PagingDataModel<PpBudgetAlloc> {
		private static final long serialVersionUID = 1L;

		public BudgetAllocPagingDataModel() {
			super("dtBudgetAllocResult");
		}
		/**
		 * @author rainpoh
		 * @since 13.01.2014
		 * retrieve default budget alloc by ministry id
		 * 
		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
		 */
		@Override
		public ISearchResultBean<PpBudgetAlloc> pageChangeListener(ISearchBean<PpBudgetAlloc> searchBean) {
			if (log.isDebugEnabled()) {
				log.debug("pageChangeListener");
			}

			try {	
				BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
				BudgetAllocSearchBean baSearchBean = getManagedBean().getBudgetAllocSearchBean();
				if(baSearchBean.getStatusId()!=null && CommonMethod.isSelected(baSearchBean.getStatusId())){
					log.info("+ statusId " + baSearchBean.getStatusId());
					reqDTO.setStatusId(baSearchBean.getStatusId());
				}
				
				reqDTO.setBudgetAllocSearchBean(searchBean);
				IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);	
				BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetAllocByParams(reqDTO);

				if (resDTO.isSuccessful()) {
					return resDTO.getBudgetAllocResultBean();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
	}



	/**
	 * @return the budgetAllocDataModel
	 */
	public IPagingDataModel<PpBudgetAlloc> getBudgetAllocDataModel() {
		return budgetAllocDataModel;
	}

	/**
	 * @param budgetAllocDataModel the budgetAllocDataModel to set
	 */
	public void setBudgetAllocDataModel(
			IPagingDataModel<PpBudgetAlloc> budgetAllocDataModel) {
		this.budgetAllocDataModel = budgetAllocDataModel;
	}


}
