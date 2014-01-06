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

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pp.budgetallocation.IBudgetDisbursementSO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pp.budgetallocation.BudgetDisbursementReqDTO;
import my.ep.dto.pp.budgetallocation.BudgetDisbursementResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocOrg;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetDtl;
import my.ep.type.pm.OrganizationType;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.pp.budgetallocation.BudgetAllocationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.CommonMethod;

import org.apache.log4j.Logger;

@ManagedBean(name="budgetAllocBackingBean")
@ViewScoped
public class BudgetAllocationBackingBean extends BaseBackingBean<BudgetAllocationManagedBean>{
	private static Logger log = Logger.getLogger(BudgetAllocationBackingBean.class);
	
//	private IPagingDataModel<PpBudget> budgetDataModel = new BudgetPagingDataModel();

	private IPagingDataModel<PpBudgetDtl> budgetDtlDataModel = new BudgetDtlPagingDataModel();
	
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
		log.info("BudgetAllocationBackingBean - Start");
		if(this.getManagedBean().getViewMode()==null || ProcurementPlanConst.MODE_LIST.equals(this.getManagedBean().getViewMode())){
			//set current user profile
			if(getFacesScopes().getUserSessionBean()!=null){
				List <PmOrgProfile> ministryProfiles = getOrgProfiles(getFacesScopes().getUserSessionBean().getUserId(), OrganizationType.MINISTRY.value());// TODO dynamic
				this.getManagedBean().setCurrentUser(ministryProfiles!=null ? ministryProfiles.get(0) : new PmOrgProfile());
			}
			
		}
		log.info("BudgetAllocationBackingBean - End");
	}
	

	
	/**
	 * redirect to budget allocation details view from BPM & DB
	 * @param taskId
	 * @param status
	 * @return xhtml
	 */
	public String redirectToBudgetAllocation(){
		log.info("redirectToBudgetAllocation - Start");
		try{
			cleanBudgetAllocDetails();
	
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
		this.getManagedBean().getBudgetAllocOrgMapper().clear();
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		this.getManagedBean().setOutComes(ProcurementPlanConst.BPM_DEFAULT_ACTIONS); 
		this.getManagedBean().setSelectedBudgetAllocation(null);
		this.getManagedBean().getSelectedBudgetAllocation().setMinistryOrgProfile(ministryOptions().get(0));
		this.getManagedBean().setSelectedBudgetAllocDtl(null);
		this.getManagedBean().setSelectedBudgetAllocOrg(null);
		this.getManagedBean().getSelectedBudgetAllocation().setPpWorkflowStatus(CommonMethod.getDefaultStatus(ProcurementPlanConst.DOCTYPE_BUDGET_ALLOC));//TODO
		this.getManagedBean().getSelectedBudgetAllocation().setEditable(true);
		initialBudgetAllocRemark();
		log.info("cleanBudgetAllocDetails - End");
	}
	
	
//	
//	/**
//	 * load task from task list
//	 * @param budgetAlloc
//	 * @param taskName
//	 */
//	public void loadTask(PpBudgetAlloc budgetAlloc, String taskName){
//		log.info("loadTask - Start");
//		if(budgetAlloc!=null && taskName!=null){
//			this.getManagedBean().setSelectedBudgetAllocation(budgetAlloc);
//			this.getManagedBean().getSelectedBudgetAllocation().setPpRemarks(budgetAlloc.getPpRemarks());
//			this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl());
//			initialBudgetAllocRemark();
//			if(ProcurementPlanConst.BPM_TASK_BUDGET_REVIEW.equalsIgnoreCase(taskName)){
//				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_APPROVAL);
//				this.getManagedBean().getSelectedBudgetAllocation().setEditable(false); 
//			}else if(ProcurementPlanConst.BPM_TASK_BUDGET_REVISE.equals(taskName)){
//				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_REVISION);
//				this.getManagedBean().getSelectedBudgetAllocation().setEditable(true); 
//				for(PpBudgetAllocDtl item:budgetAlloc.getPpBudgetAllocDtls()){
//					updateSelectedGo(item, "INIT");
//				}
//			}else{
//				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_VIEW);
//				this.getManagedBean().getSelectedBudgetAllocation().setEditable(false); 
//			}
//		
//		}
//		log.info("loadTask - End");
//	}
//	
//	

	/**
	 * populate the budget allocation dtl 
	 * @param ppBudgetAllocDtl
	 */
	public void showBudgetAllocationDtl(PpBudgetAllocDtl ppBudgetAllocDtl){
		log.info("showBudgetAllocationDtl - Start");
		if(ppBudgetAllocDtl==null){
			this.getManagedBean().setSelectedBudgetAllocDtl(new PpBudgetAllocDtl());
			this.getManagedBean().getSelectedBudgetAllocDtl().setEditable(true);
			this.getManagedBean().getSelectedBudgetAllocDtl().setPpWorkflowStatus(
					CommonMethod.getDefaultStatus(ProcurementPlanConst.DOCTYPE_BUDGET_ALLOC_DTL));//TODO	
		}else{
			if(ProcurementPlanConst.MODE_REVISION.equals(this.getManagedBean().getViewMode())
					|| ppBudgetAllocDtl.isBrandNew()){
				ppBudgetAllocDtl.setEditable(true);
			}else{
				ppBudgetAllocDtl.setEditable(false);
			}
			ppBudgetAllocDtl.setSelected(true);
			this.getManagedBean().setSelectedBudgetAllocDtl(ppBudgetAllocDtl);
//			selectedGo();
		}
		log.info("showBudgetAllocationDtl - End");
	}
	
	
	/**
	 * add budget allocation dtl to the list
	 */
	public void addBudgetAllocationDtlList(){
		log.info("addBudgetAllocationItemList - Start");
		//Initial item list if empty
		if (this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls()== null){
			this.getManagedBean().getSelectedBudgetAllocation().setPpBudgetAllocDtls(new ArrayList<PpBudgetAllocDtl>());
		}
		this.getManagedBean().getSelectedBudgetAllocation().setVerifyDisburse(false);
		this.getManagedBean().getSelectedBudgetAllocation().setVerifyDisburseRequired(true);
		
		if(this.getManagedBean().getSelectedBudgetAllocDtl().isBrandNew() && !this.getManagedBean().getSelectedBudgetAllocDtl().isSelected()){
			//update amount
			updateBudgetAllocDtl(this.getManagedBean().getSelectedBudgetAllocDtl(), ProcurementPlanConst.ADD);
			this.getManagedBean().getSelectedBudgetAllocation().addPpBudgetAllocDtls(this.getManagedBean().getSelectedBudgetAllocDtl());
		}else{
			System.out.println(this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetDtl().getBudgetDtlId());
			System.out.println(this.getManagedBean().getSelectedBudgetAllocDtl().getOriBudgetDtlId());
			if(this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetDtl().getBudgetDtlId()
					== this.getManagedBean().getSelectedBudgetAllocDtl().getOriBudgetDtlId()){
				//update amount
				updateBudgetAllocDtl(this.getManagedBean().getSelectedBudgetAllocDtl(), ProcurementPlanConst.UPDATE);
			}else{
				//rollback amount
				updateBudgetAllocDtl(this.getManagedBean().getSelectedBudgetAllocDtl(), ProcurementPlanConst.CHANGE);
			}
		}
		
				
		log.info("addBudgetAllocationItemList - End");
	}
	
	/**
	 * delete budget allocation dtl from the list
	 * @param item
	 */
	public void deleteBudgetAllocationDtl(PpBudgetAllocDtl ppBudgetAllocDtl){
		log.info("deleteBudgetAllocationDtl - Start");
		if(ppBudgetAllocDtl!=null){
			if(!ppBudgetAllocDtl.isBrandNew()&& !ppBudgetAllocDtl.isDeleted()){
				ppBudgetAllocDtl.setDeleted(true);
				ppBudgetAllocDtl.setRecordStatus(false);
			}else{
				this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls().remove(ppBudgetAllocDtl);
			}
			//rollback amount
			updateBudgetAllocDtl(ppBudgetAllocDtl, ProcurementPlanConst.REMOVE);
			this.getManagedBean().getSelectedBudgetAllocation().setVerifyDisburse(false);
			this.getManagedBean().getSelectedBudgetAllocation().setVerifyDisburseRequired(true);
		}
		log.info("deleteBudgetAllocationDtl - End");
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
				item.getPpBudgetDtl().setUsedAmt(
					CommonMethod.addBigDecimalNullable(
							item.getPpBudgetDtl().getUsedAmt(),
							this.getManagedBean().getBudgetAllocDtlMapper().get(key))
				);
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
				item.getPpBudgetDtl().setUsedAmt(
						CommonMethod.substractBigDecimalNullable(
								item.getPpBudgetDtl().getUsedAmt(),
								item.getAllocAmt())
				);
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
				item.getPpBudgetDtl().setUsedAmt(
						CommonMethod.addBigDecimalNullable(item.getPpBudgetDtl().getUsedAmt(), 
						item.getAllocAmt()));
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
				item.getPpBudgetDtl().setUsedAmt(
						CommonMethod.addBigDecimalNullable(
								item.getPpBudgetDtl().getUsedAmt(),
								newAllocAmt)
				);
				item.setOriAllocAmt(item.getAllocAmt());//update ori amount to new amount
			}else if(ProcurementPlanConst.INIT.equals(mode)){
				//update claim amt from budget dtl
				this.getManagedBean().getBudgetAllocDtlMapper().put(
						key, new BigDecimal(0));
				item.setOriAllocAmt(item.getAllocAmt());//initiate ori amount
				item.setOriBudgetDtlId(item.getPpBudgetDtl().getBudgetDtlId());//initiate ori budget
			}
			log.info("mapper keys " + this.getManagedBean().getBudgetAllocDtlMapper().keySet());
			log.info("mapper values " +this.getManagedBean().getBudgetAllocDtlMapper().values());
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("updateBudgetAllocDtl - End");
	}
	
	
	/**
	 * populate the budget allocation org 
	 * @param ppBudgetAllocOrg
	 */
	public void showBudgetAllocationOrg(PpBudgetAllocOrg ppBudgetAllocOrg){
		log.info("showBudgetAllocationOrg - Start");
		if(ppBudgetAllocOrg==null){
			this.getManagedBean().setSelectedBudgetAllocOrg(new PpBudgetAllocOrg());
			this.getManagedBean().getSelectedBudgetAllocOrg().setEditable(true);
			this.getManagedBean().getSelectedBudgetAllocOrg().setPpWorkflowStatus(
					CommonMethod.getDefaultStatus(ProcurementPlanConst.DOCTYPE_BUDGET_ALLOC_ORG));//TODO
		}else{
			if(ProcurementPlanConst.MODE_REVISION.equals(this.getManagedBean().getViewMode()) || 
					ppBudgetAllocOrg.isBrandNew()){
				ppBudgetAllocOrg.setEditable(true);
			}else{
				ppBudgetAllocOrg.setEditable(false);
			}
			ppBudgetAllocOrg.setSelected(true);
			this.getManagedBean().setSelectedBudgetAllocOrg(ppBudgetAllocOrg);
//			selectedGo();
		}
		log.info("showBudgetAllocationOrg - End");
	}
	
	/**
	 * add budget allocation org to the list
	 */
	public void addBudgetAllocationOrgList(){
		log.info("addBudgetAllocationOrgList - Start");
		//Initial item list if empty
		if (this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetAllocOrgs()== null){
			this.getManagedBean().getSelectedBudgetAllocDtl().setPpBudgetAllocOrgs(new ArrayList<PpBudgetAllocOrg>());
		}

		//update GOSODO budget
		this.getManagedBean().getSelectedBudgetAllocDtl().setUsedAmt(
				CommonMethod.addBigDecimalNullable(
						this.getManagedBean().getSelectedBudgetAllocDtl().getUsedAmt(), 
						this.getManagedBean().getSelectedBudgetAllocOrg().getAllocAmt()));
		this.getManagedBean().getSelectedBudgetAllocation().setVerifyAlloc(false);
		this.getManagedBean().getSelectedBudgetAllocation().setVerifyAllocRequired(true);
		
		if(this.getManagedBean().getSelectedBudgetAllocOrg().isBrandNew() && !this.getManagedBean().getSelectedBudgetAllocOrg().isSelected()){
			//update amount
			updateBudgetAllocOrg(this.getManagedBean().getSelectedBudgetAllocOrg(), ProcurementPlanConst.ADD);
			this.getManagedBean().getSelectedBudgetAllocDtl().addPpBudgetAllocOrgs(this.getManagedBean().getSelectedBudgetAllocOrg());
		}else{
			if(this.getManagedBean().getSelectedBudgetAllocOrg().getPpBudgetAllocDtl().getBudgetAllocDtlId()
					==this.getManagedBean().getSelectedBudgetAllocOrg().getOriBudgetAllocDtlId()){
				//update amount
				updateBudgetAllocOrg(this.getManagedBean().getSelectedBudgetAllocOrg(), ProcurementPlanConst.UPDATE);
			}else{
				//rollback amount
				updateBudgetAllocOrg(this.getManagedBean().getSelectedBudgetAllocOrg(), ProcurementPlanConst.CHANGE);
			}
		}
				
		log.info("addBudgetAllocationOrgList - End");
	}

	
	/**
	 * delete budget allocation dtl from the list
	 * @param item
	 */
	public void deleteBudgetAllocationOrg(PpBudgetAllocOrg ppBudgetAllocOrg){
		log.info("deleteBudgetAllocationOrg - Start");
		if(ppBudgetAllocOrg!=null){
			if(!ppBudgetAllocOrg.isBrandNew()&& !ppBudgetAllocOrg.isDeleted()){
				ppBudgetAllocOrg.setDeleted(true);
				ppBudgetAllocOrg.setRecordStatus(false);
				//rollback amount
				updateBudgetAllocOrg(ppBudgetAllocOrg, ProcurementPlanConst.REMOVE);
			}else{
				this.getManagedBean().getSelectedBudgetAllocDtl().getPpBudgetAllocOrgs().remove(ppBudgetAllocOrg);
			}
			//update GOSODO budget
			this.getManagedBean().getSelectedBudgetAllocDtl().setUsedAmt(
					CommonMethod.substractBigDecimalNullable(
							this.getManagedBean().getSelectedBudgetAllocDtl().getUsedAmt(), 
							this.getManagedBean().getSelectedBudgetAllocOrg().getAllocAmt()));
			this.getManagedBean().getSelectedBudgetAllocation().setVerifyAlloc(false);
			this.getManagedBean().getSelectedBudgetAllocation().setVerifyAllocRequired(true);
		}
		log.info("deleteBudgetAllocationOrg - End");
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
			log.info("mapper keys " + this.getManagedBean().getBudgetAllocOrgMapper().keySet());
			log.info("mapper values " +this.getManagedBean().getBudgetAllocOrgMapper().values());
			long key = item.getPpBudgetAllocDtl().getBudgetAllocDtlId();
			
			if(ProcurementPlanConst.CHANGE.equals(mode)){
				//change GOSO
				long oldKey = item.getOriBudgetAllocDtlId();
				//remove claim amt from old budget dtl
				this.getManagedBean().getBudgetAllocOrgMapper().put(
						oldKey, 
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getBudgetAllocOrgMapper().get(oldKey), 
								item.getOriAllocAmt())
				);
				//update claim amt from new budget dtl
				this.getManagedBean().getBudgetAllocOrgMapper().put(
						key, 
						CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getBudgetAllocOrgMapper().get(key),
								item.getAllocAmt())
				);
				//update used amt from new budget dtl
				item.getPpBudgetAllocDtl().setUsedAmt(
					CommonMethod.addBigDecimalNullable(
							item.getPpBudgetAllocDtl().getUsedAmt(),
							this.getManagedBean().getBudgetAllocOrgMapper().get(key))
				);
				item.setOriAllocAmt(item.getAllocAmt());//update ori amount to new amount
				item.setOriBudgetAllocDtlId(item.getPpBudgetAllocDtl().getBudgetAllocDtlId());//update ori budget to new budget
			}else if(ProcurementPlanConst.REMOVE.equals(mode)){
				//remove item
				//remove claim amt from budget dtl
				this.getManagedBean().getBudgetAllocOrgMapper().put(
						key, 
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getBudgetAllocOrgMapper().get(key), 
								item.getAllocAmt())
				);
				//remove used amt from budget dtl
				item.getPpBudgetAllocDtl().setUsedAmt(
						CommonMethod.substractBigDecimalNullable(
								item.getPpBudgetAllocDtl().getUsedAmt(),
								item.getAllocAmt())
				);
			}else if(ProcurementPlanConst.ADD.equals(mode)){
				//add new item
				//update claim amt from budget dtl
				this.getManagedBean().getBudgetAllocOrgMapper().put(
						key, 
						CommonMethod.addBigDecimalNullable(
							this.getManagedBean().getBudgetAllocOrgMapper().get(key),
							item.getAllocAmt())
				);
				//update used amt from budget dtl
				item.getPpBudgetAllocDtl().setUsedAmt(
						CommonMethod.addBigDecimalNullable(item.getPpBudgetAllocDtl().getUsedAmt(), 
						item.getAllocAmt()));
				item.setOriAllocAmt(item.getAllocAmt());//initiate ori amount
				item.setOriBudgetAllocDtlId(item.getPpBudgetAllocDtl().getBudgetAllocDtlId());//initiate ori budget
			}else if(ProcurementPlanConst.UPDATE.equals(mode)){
				//update item same GOSO
				BigDecimal newAllocAmt = new BigDecimal(0);
				newAllocAmt = CommonMethod.substractBigDecimalNullable(
						item.getAllocAmt(),
						item.getOriAllocAmt());
				//update claim amt from budget dtl
				this.getManagedBean().getBudgetAllocOrgMapper().put(
						key, 
						CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getBudgetAllocOrgMapper().get(key),
								newAllocAmt)
				);
				//update used amt from budget dtl
				item.getPpBudgetAllocDtl().setUsedAmt(
						CommonMethod.addBigDecimalNullable(
								item.getPpBudgetAllocDtl().getUsedAmt(),
								newAllocAmt)
				);
				item.setOriAllocAmt(item.getAllocAmt());//update ori amount to new amount
			}else if(ProcurementPlanConst.INIT.equals(mode)){
				//update claim amt from budget dtl
				this.getManagedBean().getBudgetAllocOrgMapper().put(
						key, new BigDecimal(0));
				item.setOriAllocAmt(item.getAllocAmt());//initiate ori amount
				item.setOriBudgetAllocDtlId(item.getPpBudgetAllocDtl().getBudgetAllocDtlId());//initiate ori budget
			}
			log.info("mapper keys " + this.getManagedBean().getBudgetAllocOrgMapper().keySet());
			log.info("mapper values " +this.getManagedBean().getBudgetAllocOrgMapper().values());
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("updateBudgetAllocOrg - End");
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
		this.getManagedBean().getSelectedBudgetAllocation().addPpRemarks(remark, ProcurementPlanConst.DOCTYPE_BUDGET_ALLOC);//TODO check this

		log.info("initialBudgetAllocRemark - End");
	}
//
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
//	/**
//	 * redirect to budget allocation listing page
//	 * @return xhtml
//	 */
//	public String redirectToListBudgetAllocation(){
//		log.info("redirectToListBudgetAllocation");
//		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_LIST);
//		return ProcurementPlanConst.NAV_TO_LIST_BUDGET_ALLOCATION;
//	}
//	
//
//	
//	
//	/**
//	 * filter and retrieve active budget allocation dtl
//	 * @return active budget allocation dtl only
//	 */
//	public List<PpBudgetAllocDtl> activeItems(){
//		List<PpBudgetAllocDtl> items = new ArrayList<PpBudgetAllocDtl>();
//		PpBudgetAlloc budgetAlloc = this.getManagedBean().getSelectedBudgetAllocation();
//		for(PpBudgetAllocDtl p:budgetAlloc.getPpBudgetAllocDtls()){
//			 if(p.isRecordStatus()){
//				 items.add(p);
//			 }
//		}
//		return items;
//	}
//
	/**
	 * generate header for view based on the viewMode
	 * @param viewMode
	 * @return generated header
	 */
	public String generateHeader(String viewMode){
		return CommonMethod.generateHeader(viewMode, ProcurementPlanConst.PROG_BUDGET);
	}
	
//	
//	
////	/**
////	 * reserve budget amount
////	 */
////	public void reserveBudget(){
////		log.info("reserveBudget - Start");
////	
////		BudgetReqDTO reqDTO = new BudgetReqDTO();
////		reqDTO.setSelectedGoMapper(this.getManagedBean().getSelectedGoMapper());
////		IBudgetSO reqSO = (IBudgetSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetSO.class);
////		BudgetResDTO resDTO = reqSO.reserveBudget(reqDTO);
////		if(resDTO.isSuccessful()){
////			//re-initiate budget mapper
////			PpBudgetAlloc budgetAlloc = this.getManagedBean().getSelectedBudgetAllocation();
////			for(PpBudgetAllocDtl item:budgetAlloc.getPpBudgetAllocDtls()){
////				updateSelectedGo(item, "INIT");
////			}
////		}
////		System.out.println("result.........." + resDTO.isSuccessful());
////		log.info("reserveBudget - End");
////	}
//
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
				UserResDTO resDTO = userSO.retrieveUserDetails(reqDTO);
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
//	 * submit budget allocation when button is clicked
//	 * @param action
//	 * @return xhtml
//	 */
//	public String submitBudgetAllocation(String action) {
//		log.info("submitBudgetAllocation - Start");
//		boolean isSuccess = false;
//		String returnPage = "";
//
//		boolean brandNew = this.getManagedBean().getSelectedBudgetAllocation().isBrandNew();
//		
//		if (!brandNew) {
//			isSuccess = updateBudgetAllocation(action);
//		} else {
//			isSuccess = addBudgetAllocation(action);
//		}
//		
//		log.info("completed: " + isSuccess);
//		if(isSuccess){
//			returnPage = ProcurementPlanConst.NAV_TO_TASK_LIST;
//		}
//
//		log.info("submitBudgetAllocation - End");
//		return returnPage;
//	}
//	
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
//	/**
//	 * save budget allocation to DB
//	 * @param action
//	 * @return budget allocation with ID
//	 */
//	public boolean addBudgetAllocation(String action){
//		log.info("addBudgetAllocation - Start");
//		boolean result  = false;
//		try{
//			BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
//			reqDTO.setBudgetAllocation(this.getManagedBean().getSelectedBudgetAllocation());
//			reqDTO.setSelectedGoMapper(this.getManagedBean().getSelectedGoMapper());
//			reqDTO.setAction(action);
//			reqDTO.setUpdateBudget(true);
//			reqDTO.setToken(getFacesScopes().getBPMAuthenticationCtxToken());
//			IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
//			BudgetAllocationResDTO resDTO = reqSO.addBudgetAllocation(reqDTO);
//			if(resDTO.isSuccessful()){
//				result = resDTO.isSuccessful();
//				System.out.println("Generated ID... " + resDTO.getBudgetAlloc().getBudgetAllocId());
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			
//		}
//		log.info("addBudgetAllocation - End");
//		
//		return result;
//	}
//
//	/**
//	 * generate available budget amount
//	 * @param budgetAmt
//	 * @param usedAmt
//	 * @return available budget amount
//	 */
//	public BigDecimal generateAvailableBudget(BigDecimal budgetAmt, BigDecimal usedAmt){
//		return
//				CommonMethod.substractBigDecimalNullable(budgetAmt,usedAmt);
//	}
//	
//	/**
//	 * populate GO list
//	 * @return GO list
//	 */
//	public List<PpBudget> populateGo() {
//		log.info("populateGo");
//		return getAllAvailableBudgets(this.getManagedBean().getCurrentUser().getOrgProfileId());
//	}
//
//	/**
//	 * get all available budget only
//	 * @return List <PpBudget>
//	 */
//	public List<PpBudget> allAvailableBudgets(){
//		log.info("allAvailableBudgets - Start");
//		List <PpBudget> budgetList =  new ArrayList<PpBudget>();	
//		try{
//			if(this.getManagedBean().getCurrentUser()!=null && this.getManagedBean().getCurrentUser().getOrgProfileId()!=null){
//				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
//				reqDTO.setMinistryId(this.getManagedBean().getCurrentUser().getOrgProfileId());
//				IBudgetDisbursementSO reqSO = (IBudgetDisbursementSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetDisbursementSO.class);
//				BudgetDisbursementResDTO resDTO = reqSO.retrieveAvailableBudgetsByMinistry(reqDTO);
//				
//				if (resDTO.isSuccessful()) {
//					budgetList = resDTO.getBudgetList();
//				}
//				System.out.println("getBudgetList() " + budgetList.size());
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		log.info("allAvailableBudgets - End");
//		return budgetList;
//	}
	/**
	 * get all available budget only
	 * @return List <PpBudgetDtl>
	 */
	public List<PpBudget> populateGo(){
		log.info("populateGo - Start");
		List<PpBudget> ppBudgets = new ArrayList<PpBudget>();
		try{
			if(this.getManagedBean().getCurrentUser()!=null && this.getManagedBean().getCurrentUser().getOrgProfileId()!=null){
				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
				reqDTO.setMinistryId(this.getManagedBean().getCurrentUser().getOrgProfileId());	
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
			if(budgetId!=null && this.getManagedBean().getCurrentUser()!=null){
				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
				reqDTO.setMinistryId(this.getManagedBean().getCurrentUser().getOrgProfileId());	
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
				this.getManagedBean().getCurrentUser().getOrgProfileId(), 
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
	 * 
	 * @param ptjId
	 */
	public void selectedOrgProfile(Long ptjId){
		log.info("selectedOrgProfile - Start");
		PmOrgProfile profile =  new PmOrgProfile();
		try{
			if(ptjId!=null){
				OrgProfileReqDTO reqDTO = new OrgProfileReqDTO();
				reqDTO.setOrgProfileId(ptjId);
				IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
				OrgProfileResDTO resDTO = orgProfileSO.retrieveOrgProfileByProfileId(reqDTO);
//				if(resDTO.isSuccessful()){
					profile = resDTO.getPmOrgProfile();
//				}
			}
			this.getManagedBean().getSelectedBudgetAllocOrg().setPmOrgProfile(profile);
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("selectedOrgProfile - End");
		
	}
	/**
	 * retrieve ministry list by login user
	 * @return ministry Options
	 */
	public List<PmOrgProfile> ministryOptions() {
		log.info("ministryOptions");
		return getOrgProfiles(getFacesScopes().getUserSessionBean().getUserId(), OrganizationType.MINISTRY.value());
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
					System.out.println("size "+resDTO.getOrgProfileList().size());
//				}
			}
		}catch (Exception e){
			e.printStackTrace();
			
		}
		log.info("getOrgProfiles - End");
		return profileList;
	}
//	
//	/**
//	 * retrieve default budget by ministry id
//	 * @param ministryId
//	 * @return budget list
//	 */
//	private List <PpBudget> getAllBudgets(Long ministryId){
//		log.info("getAllBudgets - Start");
//		System.out.println("id " + ministryId);
//		List <PpBudget> budgetList =  new ArrayList<PpBudget>();
//		try{
//			BudgetReqDTO reqDTO = new BudgetReqDTO();
//			
//			SearchBean<PpBudget> searchBean = (SearchBean<PpBudget>)super.createSearchBean(PpBudget.class, "");
//			searchBean.getDataFilterBeans().add(new SearchFilterBean(
//					"orgProfileId", 
//					SearchOperator.OPERATOR_EQUALS.value(), 
//					this.getManagedBean().getCurrentUser().getOrgProfileId()));
//			searchBean.setNavigatable(false);
//			reqDTO.setBudgetSearchBean(searchBean);	
//			
//			IBudgetSO reqSO = (IBudgetSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetSO.class);
//			BudgetResDTO resDTO = reqSO.retrieveBudgetList(reqDTO);
//			
//			if (resDTO.isSuccessful()) {
//				budgetList = resDTO.getBudgetResultBean().getResultList();
//			}
//			
//		}catch (Exception e){
//			e.printStackTrace();
//			
//		}
//		log.info("getAllBudgets - End");
//		return budgetList;
//	}
//	
//	
//	/**
//	 * pre-render for the budget alloation content page
//	 */
//	public void preRenderView() {
//		try{
//			BPMTaskListBackingBean bb = new BPMTaskListBackingBean();
//			HttpServletRequest servletRequest = PortalUtil
//					.getHttpServletRequest(new FacesScopes().getRequest());
//			HttpServletRequest originalServletRequest = PortalUtil
//					.getOriginalServletRequest(servletRequest);
//			String taskId = originalServletRequest.getParameter("taskId");
//		
//			System.out.println("taskId:" + taskId);
//
//	
//			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance()
//					.getServiceObject(IBpmSO.class);
//			String ctx = getFacesScopes().getBPMAuthenticationCtxToken();
//			BpmRequestDTO reqDTO = new BpmRequestDTO();
//	
//			reqDTO.setCtxToken(ctx);
//			reqDTO.setTaskId(taskId);
//			BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
//			Object obj = res.getPojo();
//	
//			// Procurement Plan - Budget
//			PPBudgetDataType budgetObj = (PPBudgetDataType) obj;
//	
//			BudgetAllocationReqDTO budgetReqDTO = new BudgetAllocationReqDTO();
//			budgetReqDTO.setBudgetAllocId(budgetObj.getBudgetId().longValue());
//			IBudgetAllocationSO budgetSO = (IBudgetAllocationSO) BusinessDelegate
//					.getInstance()
//					.getServiceObject("pp", IBudgetAllocationSO.class);
//			BudgetAllocationResDTO budgetResSO = budgetSO
//					.retrieveBudgetAllocation(budgetReqDTO);
//	
//			BudgetAllocationBackingBean budgetBB = new BudgetAllocationBackingBean();
////			budgetBB.getManagedBean().setLoginUser(loginId);
//			budgetBB.getManagedBean().setBpmTaskId(taskId);
//			budgetBB.getManagedBean().setActions(res.getAction());
//			budgetBB.loadTask(budgetResSO.getBudgetAlloc(), res.getTaskName());
//			
//			 
//		    FacesContext fc = FacesContext.getCurrentInstance();
//		    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, ProcurementPlanConst.NAV_TO_BUDGET_ALLOCATION);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//
//	}
//	
//
//	private class BudgetPagingDataModel extends PagingDataModel<PpBudget> {
//		private static final long serialVersionUID = 1L;
//
//		public BudgetPagingDataModel() {
//			super("dtBudget");
//		}
//
//		
//		/**
//		 * @author ktfong
//		 * @since 28.10.2013
//		 * retrieve default budget by ministry id
//		 * 
//		 * @see my.ep.web.bean.paginator.PagingDataModel#pageChangeListener(my.ep.biz.bean.search.ISearchBean)
//		 */
//		@Override
//		public ISearchResultBean<PpBudget> pageChangeListener(ISearchBean<PpBudget> searchBean) {
//			if (log.isDebugEnabled()) {
//				log.debug("pageChangeListener");
//			}
//			/*if (super.getWrappedData() != null) {
//				SearchResultBean<PpBudget> resultBean = new SearchResultBean<PpBudget>();
//				resultBean.setResultList((List<PpBudget>)super.getWrappedData());
//				resultBean.setTotalRecord(super.getRowCount());
//				
//				return resultBean;
//			}*/
//			
//			try {				
//				
//				List <PmOrgProfile> ministryProfiles = getOrgProfiles(getFacesScopes().getUserSessionBean().getUserId(), OrganizationType.MINISTRY.value());// TODO dynamic
//				Long ministryId = ministryProfiles!=null ? ministryProfiles.get(0).getOrgProfileId() : 0L;
//				
//				
//				BudgetReqDTO reqDTO = new BudgetReqDTO();
//				
//				searchBean.getDataFilterBeans().add(new SearchFilterBean(
//						"orgProfileId", 
//						SearchOperator.OPERATOR_EQUALS.value(), 
//						ministryId));
//				reqDTO.setBudgetSearchBean(searchBean);	
//				
//				IBudgetSO reqSO = (IBudgetSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetSO.class);
//				
//				BudgetResDTO resDTO = reqSO.retrieveBudgetList(reqDTO);
//				
//				if (resDTO.isSuccessful()) {
//					return resDTO.getBudgetResultBean();
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			return null;
//		}
//	}
//
//	/**
//	 * @return the budgetDataModel
//	 */
//	public IPagingDataModel<PpBudget> getBudgetDataModel() {
//		return budgetDataModel;
//	}
//
//	/**
//	 * @param budgetDataModel the budgetDataModel to set
//	 */
//	public void setBudgetDataModel(IPagingDataModel<PpBudget> budgetDataModel) {
//		this.budgetDataModel = budgetDataModel;
//	}
	
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
				
				List <PmOrgProfile> ministryProfiles = getOrgProfiles(getFacesScopes().getUserSessionBean().getUserId(), OrganizationType.MINISTRY.value());// TODO dynamic
				Long ministryId = ministryProfiles!=null ? ministryProfiles.get(0).getOrgProfileId() : 0L;

				BudgetDisbursementReqDTO reqDTO = new BudgetDisbursementReqDTO();
//				searchBean.getDataFilterBeans().add(new SearchFilterBean(
//						"orgProfileId", 
//						SearchOperator.OPERATOR_EQUALS.value(), 
//						ministryId));
//				reqDTO.setBudgetDtlSearchBean(searchBean);	
				reqDTO.setMinistryId(ministryId);
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


}
