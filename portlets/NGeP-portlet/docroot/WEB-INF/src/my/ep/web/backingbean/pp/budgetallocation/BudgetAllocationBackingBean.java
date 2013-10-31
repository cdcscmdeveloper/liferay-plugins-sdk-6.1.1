/*
 * Copyright © 2012 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.backingbean.pp.budgetallocation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.service.pm.authorization.orgunit.IOrgUnitSO;
import my.ep.biz.service.pm.common.object.IObjectSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pp.budgetallocation.IBudgetAllocationSO;
import my.ep.biz.service.pp.budgetallocation.IBudgetSO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitReqDTO;
import my.ep.dto.pm.authorization.orgunit.OrgUnitResDTO;
import my.ep.dto.pm.common.object.ObjectReqDTO;
import my.ep.dto.pm.common.object.ObjectResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.pm.common.status.StatusResDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationReqDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationResDTO;
import my.ep.dto.pp.budgetallocation.BudgetReqDTO;
import my.ep.dto.pp.budgetallocation.BudgetResDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.common.PmDo;
import my.ep.persistence.entity.pm.common.PmSo;
import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAlloc;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.BpmOutcomeConst;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.pp.budgetallocation.BudgetAllocationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pp.common.CommonMethod;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

@ManagedBean(name="budgetAllocationBackingBean")
@RequestScoped
public class BudgetAllocationBackingBean extends BaseBackingBean<BudgetAllocationManagedBean>{
	private static Logger log = Logger.getLogger(BudgetAllocationBackingBean.class);
	private final static Long USER_ID_MIN = 3L; //TEMP
	
	/**
	 * 
	 * @return
	 */
	private PortletRequest getPortletRequest() {
		return (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}
	
	/**
	 * constructor of the budget allocation program
	 */
	public BudgetAllocationBackingBean(){
		log.info("BudgetAllocationBackingBean - Start");
		if(this.getManagedBean().getViewMode()==null || ProcurementPlanConst.MODE_LIST.equals(this.getManagedBean().getViewMode())){
			//set received budget list
			List <PpBudget> budgetlist = getAllBudgets(USER_ID_MIN);// TODO dynamic
			this.getManagedBean().setBudgetList(budgetlist!=null ? budgetlist : new ArrayList<PpBudget>());
			
			//set current user profile
			List <PmOrgProfile> ministryProfiles = getOrgProfiles(USER_ID_MIN, ProcurementPlanConst.TYPE_MINISTRY);// TODO dynamic
			this.getManagedBean().setCurrentUser(ministryProfiles!=null ? ministryProfiles.get(0) : new PmOrgProfile());
		}
		//TODO: temp
		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, USER_ID_MIN);
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
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("redirectToBudgetAllocation - End");
		return ProcurementPlanConst.NAV_TO_BUDGET_ALLOCATION;
	
	}
	
	/**
	 * initial budget allocation clean view
	 */
	private void cleanBudgetAllocDetails(){
		log.info("cleanBudgetAllocDetails - End");
		this.getManagedBean().getSelectedGoMapper().clear();
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		this.getManagedBean().setActions(ProcurementPlanConst.BPM_DEFAULT_ACTIONS); 
		this.getManagedBean().setSelectedBudgetAllocation(new PpBudgetAlloc());
//			this.getManagedBean().getSelectedBudgetAllocation().setPmStatus(
//					CommonMethod.currentStatus(ProcurementPlanConst.STATUS_NEW, ProcurementPlanConst.DOCTYPE_BUDGET_ALLOC));
		this.getManagedBean().getSelectedBudgetAllocation().setStatusId(ProcurementPlanConst.STATUS_PBA_NEW);//TODO
		this.getManagedBean().getSelectedBudgetAllocation().setEditable(true);
		this.getManagedBean().getSelectedBudgetAllocation().setPpRemarks(new ArrayList<PpRemark>());//TODO
		this.getManagedBean().getSelectedBudgetAllocation().setPpBudgetAllocDtls(new ArrayList<PpBudgetAllocDtl>());
		log.info("cleanBudgetAllocDetails - End");
	}
	
	/**
	 * load task from task list
	 * @param budgetAlloc
	 * @param taskName
	 */
	public void loadTask(PpBudgetAlloc budgetAlloc, String taskName){
		if(budgetAlloc!=null && taskName!=null){
			if("Review Budget".equalsIgnoreCase(taskName)){
				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_APPROVAL);
				this.getManagedBean().setSelectedBudgetAllocation(budgetAlloc);
				this.getManagedBean().getSelectedBudgetAllocation().setEditable(false); 
				this.getManagedBean().setSelectedBudgetAllocItemRmk(new PpRemark()); //avoid null
				this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl()); //avoid nul
			}else if("Revise Budget".equals(taskName)){
				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_REVISION);
				this.getManagedBean().setSelectedBudgetAllocation(budgetAlloc);
				this.getManagedBean().getSelectedBudgetAllocation().setEditable(true); 
				this.getManagedBean().setSelectedBudgetAllocItemRmk(new PpRemark()); //avoid null
				this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl()); //avoid nul
				for(PpBudgetAllocDtl item:budgetAlloc.getPpBudgetAllocDtls()){
					updateSelectedGo(item, "INIT");
				}
			}
		}
	}
	
	/**
	 * populate the budget allocation dtl 
	 * @param item
	 */
	public void showBudgetAllocationItem(PpBudgetAllocDtl item){
		log.info("showBudgetAllocationItem - Start");
		if(item==null){
			this.getManagedBean().setSelectedBudgetAllocItemRmk(new PpRemark());
			this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl());
			this.getManagedBean().getSelectedBudgetAllocItem().setBrandNew(true);
			this.getManagedBean().getSelectedBudgetAllocItem().setPpBudget(new PpBudget());
			
		}else{
		
			if(ProcurementPlanConst.MODE_REVISION.equals(this.getManagedBean().getViewMode())
					&& item.isRevisionRequired()){
				item.setEditable(true);
			}else{
				item.setEditable(false);
			}
			item.setSelected(true);
			this.getManagedBean().setSelectedBudgetAllocItem(item);
			selectedGo();
		}
		log.info("showBudgetAllocationItem - End");
	}
	
	/**
	 * update local budgets while GO is selected
	 * @param item
	 * @param mode
	 */
	public void updateSelectedGo(PpBudgetAllocDtl item, String mode){
		log.info("updateSelectedGo - Start");
		try{
			log.info("mapper keys " + this.getManagedBean().getSelectedGoMapper().keySet());
			log.info("mapper values " +this.getManagedBean().getSelectedGoMapper().values());
	
			long key = item.getBudgetId();
			if("CHANGE".equals(mode)){
				//change GO
				long oldKey = item.getOriPpBudget().getBudgetId();
				System.out.println(	this.getManagedBean().getSelectedGoMapper().get(oldKey) + "-" + item.getOriAllocationAmount());
				this.getManagedBean().getSelectedGoMapper().put(
						oldKey, 
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getSelectedGoMapper().get(oldKey), 
								item.getOriAllocationAmount())
				);
				System.out.println(">>>>>>>>ori amount " + item.getOriAllocationAmount());
				System.out.println(">>>>>>>>ori Budget " + item.getOriPpBudget().getBudgetId());
				System.out.println(	this.getManagedBean().getSelectedGoMapper().get(key) + "+" + item.getAllocAmount());
				this.getManagedBean().getSelectedGoMapper().put(
						key, 
						CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getSelectedGoMapper().get(key),
								item.getAllocAmount())
				);
				
				item.setOriAllocationAmount(item.getAllocAmount());//update ori amount to new amount
				item.setOriPpBudget(item.getPpBudget());//update ori budget to new budget
				System.out.println(">>>>>>>>update ori amount " + item.getOriAllocationAmount());
				System.out.println(">>>>>>>>update ori Budget " + item.getOriPpBudget().getBudgetId());
			}else if("REMOVE".equals(mode)){
				//remove item
				this.getManagedBean().getSelectedGoMapper().put(
						key, 
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getSelectedGoMapper().get(key), 
								item.getAllocAmount())
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
								item.getAllocAmount())
					);
				item.setOriAllocationAmount(item.getAllocAmount());//initiate ori amount
				item.setOriPpBudget(item.getPpBudget());//initiate ori budget
	//			}
			}else if("UPDATE".equals(mode)){
				//update item with same GO
				this.getManagedBean().getSelectedGoMapper().put(
						key, 
						CommonMethod.substractBigDecimalNullable(
								this.getManagedBean().getSelectedGoMapper().get(key), 
								item.getOriAllocationAmount())
				);
				System.out.println(">>>>>>>>ori RM " + item.getOriAllocationAmount());
				this.getManagedBean().getSelectedGoMapper().put(
						key, 
						CommonMethod.addBigDecimalNullable(
								this.getManagedBean().getSelectedGoMapper().get(key),
								item.getAllocAmount())
				);
				item.setOriAllocationAmount(item.getAllocAmount());//update ori amount to new amount
				System.out.println(">>>>>>>>update ori RM " + item.getOriAllocationAmount());
			}else if("INIT".equals(mode)){
				this.getManagedBean().getSelectedGoMapper().put(
						key, new BigDecimal(0));
				item.setOriAllocationAmount(item.getAllocAmount());//initiate ori amount
				item.setOriPpBudget(item.getPpBudget());//initiate ori budget
			}
			log.info("mapper keys " + this.getManagedBean().getSelectedGoMapper().keySet());
			log.info("mapper values " +this.getManagedBean().getSelectedGoMapper().values());
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("updateSelectedGo - End");
	}
	
	
	/**
	 * add budget allocation dtl to the list
	 */
	public void addBudgetAllocationItemList(){
		log.info("addBudgetAllocationItemList - Start");
		//Initial item list if empty
		if (this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls()== null){
			this.getManagedBean().getSelectedBudgetAllocation().setPpBudgetAllocDtls(new ArrayList<PpBudgetAllocDtl>());
		}

		if(this.getManagedBean().getSelectedBudgetAllocItem().isBrandNew() && !this.getManagedBean().getSelectedBudgetAllocItem().isSelected()){
			//update amount
			this.getManagedBean().getSelectedBudgetAllocItem().setStatusId(ProcurementPlanConst.STATUS_PBD_NEW);
			updateSelectedGo(this.getManagedBean().getSelectedBudgetAllocItem(), "ADD");
			this.getManagedBean().getSelectedBudgetAllocation().addPpBudgetAllocDtls(this.getManagedBean().getSelectedBudgetAllocItem());
		}else{	

			if(this.getManagedBean().getSelectedBudgetAllocItem().getBudgetId()==this.getManagedBean().getSelectedBudgetAllocItem().getOriPpBudget().getBudgetId()){
				//update amount
				updateSelectedGo(this.getManagedBean().getSelectedBudgetAllocItem(), "UPDATE");
			}else{
				//rollback amount
				updateSelectedGo(this.getManagedBean().getSelectedBudgetAllocItem(), "CHANGE");
			}
		}
		//Add the item remark to the list //TODO check
		if(this.getManagedBean().getSelectedBudgetAllocItem().getPpRemarks()==null){
			this.getManagedBean().getSelectedBudgetAllocItem().setPpRemarks(new ArrayList<PpRemark>());
		}
		if(!this.getManagedBean().getSelectedBudgetAllocItemRmk().getRemark().isEmpty()){
			this.getManagedBean().getSelectedBudgetAllocItem().addPpRemarks(this.getManagedBean().getSelectedBudgetAllocItemRmk());
			System.out.println("id....."  + this.getManagedBean().getSelectedBudgetAllocItemRmk().getStatusId());
		}
				
		log.info("addBudgetAllocationItemList - End");
	}
	
	
	/**
	 * delete budget allocation dtl from the list
	 * @param item
	 */
	public void deleteBudgetAllocItem(PpBudgetAllocDtl item){
		log.info("deleteBudgetAllocItem - Start");
		if(item!=null){
			if(!item.isBrandNew()){
				item.setDeleted(true);
				item.setRecordStatus(false);
				//rollback amount
				updateSelectedGo(item, "REMOVE");
			}else{
				this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls().remove(item);
			}
		}
		log.info("deleteBudgetAllocItem - End");
	}
	
	/**
	 * add remark to budget allocation level
	 */
	public void addBudgetAllocRemark(){
		log.info("addBudgetAllocRemark - Start");
		PpRemark remark = new PpRemark();

		if(this.getManagedBean().getSelectedBudgetAllocation().getPpRemarks()==null){
			this.getManagedBean().getSelectedBudgetAllocation().setPpRemarks(new ArrayList<PpRemark>());
		}
		this.getManagedBean().getSelectedBudgetAllocation().addPpRemarks(remark);//TODO check this
		System.out.println(">>>>>>>> " + this.getManagedBean().getSelectedBudgetAllocation().getPpRemarks().size());
		log.info("addBudgetAllocRemark - End");
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
	 * redirect to budget allocation listing page
	 * @return xhtml
	 */
	public String redirectToListBudgetAllocation(){
		log.info("redirectToListBudgetAllocation");
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_LIST);
		return ProcurementPlanConst.NAV_TO_LIST_BUDGET_ALLOCATION;
	}
	

	
	
	/**
	 * filter and retrieve active budget allocation dtl
	 * @return active budget allocation dtl only
	 */
	public List<PpBudgetAllocDtl> activeItems(){
		List<PpBudgetAllocDtl> items = new ArrayList<PpBudgetAllocDtl>();
		PpBudgetAlloc budgetAlloc = this.getManagedBean().getSelectedBudgetAllocation();
		for(PpBudgetAllocDtl p:budgetAlloc.getPpBudgetAllocDtls()){
			 if(p.isRecordStatus()){
				 items.add(p);
			 }
		}
		return items;
	}

	/**
	 * generate header for view based on the viewMode
	 * @param viewMode
	 * @return generated header
	 */
	public String generateHeader(String viewMode){
		return CommonMethod.generateHeader(viewMode, ProcurementPlanConst.PROG_BUDGET);
	}
	
	
	
//	/**
//	 * reserve budget amount
//	 */
//	public void reserveBudget(){
//		log.info("reserveBudget - Start");
//	
//		BudgetReqDTO reqDTO = new BudgetReqDTO();
//		reqDTO.setSelectedGoMapper(this.getManagedBean().getSelectedGoMapper());
//		IBudgetSO reqSO = (IBudgetSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetSO.class);
//		BudgetResDTO resDTO = reqSO.reserveBudget(reqDTO);
//		if(resDTO.isSuccessful()){
//			//re-initiate budget mapper
//			PpBudgetAlloc budgetAlloc = this.getManagedBean().getSelectedBudgetAllocation();
//			for(PpBudgetAllocDtl item:budgetAlloc.getPpBudgetAllocDtls()){
//				updateSelectedGo(item, "INIT");
//			}
//		}
//		System.out.println("result.........." + resDTO.isSuccessful());
//		log.info("reserveBudget - End");
//	}
//	

	
	/**
	 * submit budget allocation when button is clicked
	 * @param action
	 * @return xhtml
	 */
	public String submitBudgetAllocation(String action) {
		log.info("submitBudgetAllocation - Start");
		boolean isSuccess = false;
		String returnPage = "";
		if(BpmOutcomeConst.BPM_SUBMIT.equals(action)){
			if(this.getManagedBean().getSelectedBudgetAllocation().isBrandNew()){
				isSuccess = submitNewBudgetAlloc(action);
			}else{
				isSuccess = submitUpdateBudgetAlloc(action);
			}
		}else if (BpmOutcomeConst.BPM_REVISE.equals(action)){
			isSuccess = submitUpdateBudgetAlloc(action);
		}else if (BpmOutcomeConst.BPM_APPROVE.equals(action)){
			isSuccess = submitUpdateBudgetAlloc(action);
		}else if (BpmOutcomeConst.BPM_SAVE.equals(action)){
//			reserveBudget();
			isSuccess = submitNewBudgetAlloc(action);
		}

		if(isSuccess){
			returnPage = redirectToListBudgetAllocation();
		}

		log.info("submitBudgetAllocation - End");
		return returnPage;
	}
	
	/**
	 * submit new budget allocation to DB and initial BPM task
	 * @param action
	 * @return submit result
	 */
	public boolean submitNewBudgetAlloc(String action){
		log.info("submitNewBudgetAlloc - Start");
		boolean isSubmitted = false;
		try {
			PpBudgetAlloc ppBudgetAlloc = saveBudgetAllocation(action);
			if(ppBudgetAlloc!=null && ppBudgetAlloc.getBudgetAllocId()>0L){
				log.info("calling BPM.......");
//				isSubmitted = initialBpmTask(action, ppBudgetAlloc);
				IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
				BpmResponseDTO resBpm = reqSO.initialBpmTask(action, ppBudgetAlloc);
				isSubmitted = resBpm!=null ? resBpm.isSuccessful() : false;
				if(!resBpm.isSuccessful()){
					this.getManagedBean().setSystemMsg("BPM Error Message: (" + resBpm.getErrorCode() + ") " + resBpm.getErrorReason());
					log.info("BPM Error Message: (" + resBpm.getErrorCode() + ") " + resBpm.getErrorReason());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("submitNewBudgetAlloc - End");
		return isSubmitted;
	}
	
	/**
	 * submit update budget allocation to DB and submit BPM task
	 * @param action
	 * @return submit result
	 */
	public boolean submitUpdateBudgetAlloc(String action){
		log.info("submitUpdateBudgetAlloc - Start");
		boolean isSubmitted = false;
		try {
			PpBudgetAlloc ppBudgetAlloc = saveBudgetAllocation(action);
			String taskId = this.getManagedBean().getBpmTaskId();
			if(ppBudgetAlloc!=null && CommonMethod.isSelected(taskId)){
				System.out.println("call BPM.......");
//				isSubmitted = submitBpmTask(taskId, action, ppBudgetAlloc);
				IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
				BpmResponseDTO resBpm = reqSO.submitBpmTask(taskId, action, ppBudgetAlloc);
				isSubmitted = resBpm!=null ? resBpm.isSuccessful() : false;
				if(!resBpm.isSuccessful()){
					this.getManagedBean().setSystemMsg("BPM Error Message: (" + resBpm.getErrorCode() + ") " + resBpm.getErrorReason());
					log.info("BPM Error Message: (" + resBpm.getErrorCode() + ") " + resBpm.getErrorReason());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("submitUpdateBudgetAlloc - End");
		return isSubmitted;
	}
	
	/**
	 * Save new/udpate budget allocation to DB
	 * @param action
	 * @return
	 */
	public PpBudgetAlloc saveBudgetAllocation(String action){
		log.info("saveBudgetAllocation - Start");
		PpBudgetAlloc resultObj = null;
		boolean brandNew = this.getManagedBean().getSelectedBudgetAllocation().isBrandNew();
		if (!brandNew) {
			resultObj = updateBudgetAllocation(action);
		} else {
			resultObj = addBudgetAllocation();
		}
		log.info("saveBudgetAllocation - End");
		return resultObj;
	}

	/**
	 * update budget allocation to DB
	 * @param action
	 * @return budget allocation with ID
	 */
	public PpBudgetAlloc updateBudgetAllocation(String action){
		log.info("updateBudgetAllocation - Start");
	
		PpBudgetAlloc resultObj  = null;
		try{
			BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
			if(BpmOutcomeConst.BPM_SUBMIT.equals(action)){
				//rollback revision flag
				PpBudgetAlloc butgetAlloc = this.getManagedBean().getSelectedBudgetAllocation();
				for(PpBudgetAllocDtl p :butgetAlloc.getPpBudgetAllocDtls()){
					p.setRevisionRequired(false);
					p.setStatusId(ProcurementPlanConst.STATUS_PENDING_APPROVAL); //TODO change 
				}
			}
			
			reqDTO.setBudgetAllocation(this.getManagedBean().getSelectedBudgetAllocation());
			reqDTO.setSelectedGoMapper(this.getManagedBean().getSelectedGoMapper());
			IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
			BudgetAllocationResDTO resDTO = reqSO.updateBudgetAllocation(reqDTO);
			
			if(resDTO.isSuccessful()){
				resultObj = resDTO.getBudgetAlloc();
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("updateBudgetAllocation - End");
		return resultObj;
	}
	
	/**
	 * save budget allocation to DB
	 * @return budget allocation with ID
	 */
	public PpBudgetAlloc addBudgetAllocation(){
		log.info("addBudgetAllocation - Start");
		PpBudgetAlloc resultObj  = null;
		try{
			BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
			reqDTO.setBudgetAllocation(this.getManagedBean().getSelectedBudgetAllocation());
			reqDTO.setSelectedGoMapper(this.getManagedBean().getSelectedGoMapper());
			IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
			BudgetAllocationResDTO resDTO = reqSO.addBudgetAllocation(reqDTO);
			if(resDTO.isSuccessful()){
				resultObj = resDTO.getBudgetAlloc();
				System.out.println("Generated ID... " + resultObj.getBudgetAllocId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("addBudgetAllocation - End");
		
		return resultObj;
	}
	
	/**
	 * generate available budget amount
	 * @return available budget amount
	 */
	public BigDecimal generateAvailableBudget(){
		return
				CommonMethod.substractBigDecimalNullable(
				this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().getBudgetAmount(),
				this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().getUsedAmount());
	}
	
	/**
	 * populate GO list
	 * @return GO list
	 */
	public List<PpBudget> populateGo() {
		log.info("populateGo");
		return getAllBudgets(USER_ID_MIN);
	}
	
	/**
	 * retrieve and set the selected budget(GO)
	 */
	public void selectedGo(){
		log.info("selectedGo - Start");
		BudgetReqDTO reqDTO = new BudgetReqDTO();
		reqDTO.setBudgetId(this.getManagedBean().getSelectedBudgetAllocItem().getBudgetId());

		IBudgetSO reqSO = (IBudgetSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetSO.class);
		BudgetResDTO resDTO = reqSO.retrieveBudget(reqDTO);
		
		if (resDTO.isSuccessful()) {
			this.getManagedBean().getSelectedBudgetAllocItem().setPpBudget(resDTO.getBudget());
			this.getManagedBean().getSelectedBudgetAllocItem().setOvrFinancialYear(resDTO.getBudget().getFinancialYear());
		}

		log.info("selectedGo - End");
	}
	
	/**
	 * populate SO list
	 * @return SO list
	 */
	public List<PmSo> populateSo() {
		log.info("populateSo - Start");
		List<PmSo> pmSos = new ArrayList<PmSo>();
		
		if(this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().getPmGo()!=null){
			ObjectReqDTO reqDTO = new ObjectReqDTO();
			reqDTO.setGoId(this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().getPmGo().getGoId());
			IObjectSO objectSO = (IObjectSO) BusinessDelegate.getInstance().getServiceObject("pm", IObjectSO.class);
			ObjectResDTO resDTO = objectSO.retrieveSOListByGOCode(reqDTO);
	//		if(resDTO.isSuccessful()){
				pmSos = resDTO.getPmSoList();
	//		}
		}
		log.info("populateSo - End");
		return pmSos;
		
	}
//	
//	/**
//	 * retrieve and set the PmSo when PmSo.soId is selected
//	 */
//	public void selectedSo() {
//		log.info("selectedSo - Start");
//		Long soId = this.getManagedBean().getSelectedBudgetAllocItem().getSoId();
//		try{
//			if(CommonMethod.isSelected(soId)){
//				ObjectReqDTO reqDTO = new ObjectReqDTO();
//				reqDTO.setSoId(soId);
//				IObjectSO objectSO = (IObjectSO) BusinessDelegate.getInstance().getServiceObject("pm", IObjectSO.class);
//				ObjectResDTO resDTO = objectSO.retrieveSOBYSOId(reqDTO);
////				if(resDTO.isSuccessful()){
//					this.getManagedBean().getSelectedBudgetAllocItem().setPmSo(resDTO.getPmSo());
////				}
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//			this.getManagedBean().setSystemMsg(e.getMessage());
//		}
//		log.info("selectedSo - End");
//	
//	}
//	
	/**
	 * populate DO list
	 * @return DO list
	 */
	public List<PmDo> populateDo() {
		log.info("populateDo - Start");
		Long soId = this.getManagedBean().getSelectedBudgetAllocItem().getSoId();
		List<PmDo> pmDos = new ArrayList<PmDo>();
		if(CommonMethod.isSelected(soId)){
			ObjectReqDTO reqDTO = new ObjectReqDTO();
			reqDTO.setSoId(soId);
			IObjectSO objectSO = (IObjectSO) BusinessDelegate.getInstance().getServiceObject("pm", IObjectSO.class);
			ObjectResDTO resDTO = objectSO.retrieveDOListBySOCode(reqDTO);
	//		if(resDTO.isSuccessful()){
				pmDos = resDTO.getPmDoList();
	//		}
		}
		log.info("populateDo - End");
		return pmDos;
	}
	
	/**
	 * retrieve and set the PmDo when PmSo.doId is selected
	 */
	public void selectedDo() {
		log.info("selectedDo - Start");
		Long doId = this.getManagedBean().getSelectedBudgetAllocItem().getDoId();
		try{
			if(CommonMethod.isSelected(doId)){
				ObjectReqDTO reqDTO = new ObjectReqDTO();
				reqDTO.setDoId(doId);
				IObjectSO objectSO = (IObjectSO) BusinessDelegate.getInstance().getServiceObject("pm", IObjectSO.class);
				ObjectResDTO resDTO = objectSO.retrieveDOByDOId(reqDTO);
//				if(resDTO.isSuccessful()){
					this.getManagedBean().getSelectedBudgetAllocItem().setPmDo(resDTO.getPmDo());
//				}
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("selectedDo - End");
	
	}
	
	/**
	 * retrieve ministry list by login user
	 * @return ministry Options
	 */
	public List<PmOrgProfile> ministryOptions() {
		log.info("ministryOptions");
		return getOrgProfiles(USER_ID_MIN, ProcurementPlanConst.TYPE_MINISTRY);
	}
	
	/**
	 * retrieve jabatan list by selected ministry id
	 * @return
	 */
	public List<PmOrgProfile> populateJabatan(){
		log.info("populateJabatan");
		return getParentOrgProfile(this.getManagedBean().getSelectedBudgetAllocation().getMinistryOrgProfileId(), ProcurementPlanConst.TYPE_JABATAN);
	}
	
	/**
	 * retrieve ptj list by selected jabatan id
	 * @return
	 */
	public List<PmOrgProfile> populatePtj(){
		log.info("populatePtj");
		return getParentOrgProfile(this.getManagedBean().getSelectedBudgetAllocation().getJabatanOrgProfileId(), ProcurementPlanConst.TYPE_PTJ);
	}


	/**
	 * retrieve child profiles from PM
	 * @param parentOrgProfileId
	 * @param type
	 * @return list of child
	 */
	private List <PmOrgProfile> getParentOrgProfile(Long parentOrgProfileId, Long type){
		log.info("getParentOrgProfile - Start");
		log.info("parentOrgProfileId " + parentOrgProfileId);
		List <PmOrgProfile> profileList =  new ArrayList<PmOrgProfile>();
		try{
			if(CommonMethod.isSelected(parentOrgProfileId)){
				OrgUnitReqDTO reqDTO = new OrgUnitReqDTO();
				reqDTO.setTypeCodeId(type);
				reqDTO.setOrgProfileId(parentOrgProfileId);
				IOrgUnitSO orgUnitSO = (IOrgUnitSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgUnitSO.class);
				OrgUnitResDTO resDTO = orgUnitSO.retrieveOrgProfileByParentIdAndType(reqDTO);
//				if(resDTO.isSuccessful()){
					profileList = resDTO.getPmorgProfileByParentList();
//				}
			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
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
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("getOrgProfiles - End");
		return profileList;
	}
	
	/**
	 * retrieve default budget by ministry id
	 * @param ministryId
	 * @return budget list
	 */
	private List <PpBudget> getAllBudgets(Long ministryId){
		log.info("getAllBudgets - Start");
		List <PpBudget> budgetList =  new ArrayList<PpBudget>();
		try{
			BudgetReqDTO reqDTO = new BudgetReqDTO();
			reqDTO.setMinistryId(ministryId);	
			IBudgetSO reqSO = (IBudgetSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetSO.class);
			BudgetResDTO resDTO = reqSO.retrieveBudgetList(reqDTO);
			
			if (resDTO.isSuccessful()) {
				budgetList = resDTO.getBudgetList();
			}
			
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("getAllBudgets - End");
		return budgetList;
	}
	
	
	
	private String getStatusDesc(){

		log.info("getAllBudgets - Start");
		List <PpBudget> budgetList =  new ArrayList<PpBudget>();
		try{
			StatusReqDTO reqDTO = new StatusReqDTO();
			reqDTO.setStatusId(ProcurementPlanConst.STATUS_PBA_NEW);
			reqDTO.setDocType("PBA");
			reqDTO.setModuleCode("PP");
			IStatusSO statusSO = (IStatusSO) BusinessDelegate.getInstance().getServiceObject("pm", IStatusSO.class);
			StatusResDTO resDTO = statusSO.retrieveStatusByParams(reqDTO);
//			if(resDTO.isSuccessful()){
//				resDTO.getPmStatus().getPmStatusDescs().get();
//			}
		}catch (Exception e){
			e.printStackTrace();
			this.getManagedBean().setSystemMsg(e.getMessage());
		}
		log.info("getAllBudgets - End");
		return "";
	}

}
