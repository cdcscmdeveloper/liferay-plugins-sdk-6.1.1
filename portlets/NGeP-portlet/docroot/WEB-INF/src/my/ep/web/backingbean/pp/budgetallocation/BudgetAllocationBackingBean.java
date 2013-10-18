package my.ep.web.backingbean.pp.budgetallocation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.service.pp.budgetallocation.IBudgetAllocationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationReqDTO;
import my.ep.dto.pp.budgetallocation.BudgetAllocationResDTO;
import my.ep.jaxb.pp.budgetdata.PSBudgetDataType;
import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAlloc;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.persistence.logging.LogKey;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
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

	
	private PortletRequest getPortletRequest() {
		return (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}
	
	public BudgetAllocationBackingBean(){
		log.info("BudgetAllocationBackingBean - Start");
		if(this.getManagedBean().getViewMode()==null || ProcurementPlanConst.MODE_LIST.equals(this.getManagedBean().getViewMode())){
			getAllBudgets();
		}
		//TODO: temp
		if(MDC.get(LogKey.USER_ID)==null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
		log.info("BudgetAllocationBackingBean - End");
	}
	
	private void getAllBudgets(){
		log.info("getAllBudgets - Start");
		try{
			BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
			reqDTO.setMinistryId(1L);	// TODO dynamic
	
			IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
			BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetList(reqDTO);
			
			if (resDTO.isSuccessful()) {
				this.getManagedBean().setBudgetList(resDTO.getBudgetList());
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		log.info("getAllBudgets - End");
	}
	
	public String redirectToBudgetAllocation(String taskId, String status){//temp status
		log.info("redirectToBudgetAllocation - Start");
		boolean isComplete = false;
	
		try{
			if(taskId!=null && taskId.trim().length()>0){
				log.info("populate view");
				isComplete = retrieveTask(taskId, status);
			}else{
				log.info("new view");
				this.getManagedBean().getSelectedGoMapper().clear();
				this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
				this.getManagedBean().setActions(new String[]{"SAVE", "SUBMIT"}); 
				this.getManagedBean().setSelectedBudgetAllocation(new PpBudgetAlloc());
	//			this.getManagedBean().getSelectedBudgetAllocation().setPmStatus(
	//					CommonMethod.currentStatus(ProcurementPlanConst.STATUS_NEW, ProcurementPlanConst.DOCTYPE_BUDGET_ALLOC));
				this.getManagedBean().getSelectedBudgetAllocation().setStatusId(ProcurementPlanConst.STATUS_PBA_NEW);//TODO
				this.getManagedBean().getSelectedBudgetAllocation().setEditable(true);
				this.getManagedBean().getSelectedBudgetAllocation().setPpRemarks(new ArrayList<PpRemark>());//TODO
	
				this.getManagedBean().setSelectedBudgetAllocItemRmk(new PpRemark());//TODO
				this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl());
				this.getManagedBean().getSelectedBudgetAllocItem().setPpBudget(new PpBudget());
				this.getManagedBean().getSelectedBudgetAllocation().setPpBudgetAllocDtls(new ArrayList<PpBudgetAllocDtl>());
				isComplete = true;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		log.info("redirectToBudgetAllocation - End");
		
		if(isComplete){
			return ProcurementPlanConst.NAV_TO_BUDGET_ALLOCATION;
		}else{
			return "";
		}
	}
	
	public boolean retrieveTask(String taskId, String status){
		String[] actions = null;
		boolean isComplete = false;
		
		if(ProcurementPlanConst.MODE_REVISION.equals(status)){
//			String token = bpmSO.authenticate("roslan", "welcome1");
			this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_REVISION);
			this.getManagedBean().getSelectedGoMapper().clear();
		}else if(ProcurementPlanConst.MODE_APPROVAL.equals(status)){
//			String token = bpmSO.authenticate("lailah", "welcome1"); //temp
			this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_APPROVAL);
			actions = new String[]{"REVISE", "APPROVE"}; //temp
		}
		
		
//		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
//		if(token!=null && !token.isEmpty()){
//			BpmRequestDTO reqBpmDTO = new BpmRequestDTO();
//			reqBpmDTO.setCtxToken(token);
//			reqBpmDTO.setTaskId(taskId);
//			BpmResponseDTO resBpmDTO = bpmSO.getTaskDetails(reqBpmDTO);
//			if(resBpmDTO!=null && resBpmDTO.isSucessful()){
//				PSBudgetDataType taskData = (PSBudgetDataType) resBpmDTO.getPojo();
				BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
				reqDTO.setBudgetAllocId(this.getManagedBean().getTempBudgetId()); //temp
//				reqDTO.setBudgetAllocId(Long.parseLong(taskData.getBudgetId()));
				IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
				BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetAllocation(reqDTO);
				if(resDTO!=null && resDTO.isSuccessful()){
					this.getManagedBean().setActions(actions); //temp
		//			this.getManagedBean().setActions(resBpmDTO.getAction());
					this.getManagedBean().setSelectedBudgetAllocation(resDTO.getBudgetAlloc());
					this.getManagedBean().getSelectedBudgetAllocation().setEditable(false);
					this.getManagedBean().setSelectedBudgetAllocItemRmk(new PpRemark()); //avoid null
					this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl()); //avoid null
					if(ProcurementPlanConst.MODE_REVISION.equals(status)){
						this.getManagedBean().getSelectedBudgetAllocation().setEditable(true);
						for(PpBudgetAllocDtl item:resDTO.getBudgetAlloc().getPpBudgetAllocDtls()){
							updateSelectedGo(item, "INIT");
						}
					}
					isComplete = true;
				}
//		}else{
//			System.out.println("BPM Error Message: (" + resBpm.getErrorCode() + ") " + resBpm.getErrorReason());
//		}
		return isComplete;
	}
	
	/*
	public String redirectToEndorseBudgetAllocation(String taskId){
		log.info("redirectToEndorseBudgetAllocation - Start");
		String returnPage = "";
		try{
			this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_APPROVAL);
			System.out.println(">>>> taskId " + taskId);
			if(taskId!=null && taskId.trim().length()>0){
				
//				IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
//				String token = bpmSO.authenticate("lailah", "welcome1");
//				if(token!=null && !token.isEmpty()){
//					BpmRequestDTO reqBpmDTO = new BpmRequestDTO();
//					reqBpmDTO.setCtxToken(token);
//					reqBpmDTO.setTaskId(taskId);
//					BpmResponseDTO resBpmDTO = bpmSO.getTaskDetails(reqBpmDTO);
//					if(resBpmDTO!=null && resBpmDTO.isSucessful()){
//						System.out.println(">>>>>>>>>>>>>>> resBpmDTO not null");
//						System.out.println(">>>>pojo " + resBpmDTO.getPojo());
//						System.out.println("actionsss>>>>>>>>>>>> " + resBpmDTO.getAction().length);
//						PSBudgetDataType taskData = (PSBudgetDataType) resBpmDTO.getPojo();
						BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
//						System.out.println(">>>>id " + taskData.getBudgetId());
						reqDTO.setBudgetAllocId(this.getManagedBean().getTempBudgetId()); //temp
//						reqDTO.setBudgetAllocId(Long.parseLong(taskData.getBudgetId()));
						IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
						BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetAllocation(reqDTO);
						String[] actions = new String[]{"REVISE", "APPROVE"}; //temp
						if(resDTO!=null && resDTO.isSuccessful()){
							this.getManagedBean().setActions(actions); //temp
//							this.getManagedBean().setActions(resBpmDTO.getAction());
							this.getManagedBean().setSelectedBudgetAllocation(resDTO.getBudgetAlloc());
							this.getManagedBean().getSelectedBudgetAllocation().setEditable(false);
							this.getManagedBean().setSelectedBudgetAllocItemRmk(new PpRemark()); //avoid null
							this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl()); //avoid null
							returnPage = ProcurementPlanConst.NAV_TO_BUDGET_ALLOCATION;
						}
//					}else{
//						System.out.println("BPM Error Message: (" + resBpm.getErrorCode() + ") " + resBpm.getErrorReason());
//					}
//				}
			}
				
		}catch (Exception e){
			e.printStackTrace();
		}
		log.info("redirectToEndorseBudgetAllocation - End");
		return returnPage;
	}
	
	public String redirectToReviseBudgetAllocation(String taskId){
		log.info("redirectToReviseBudgetAllocation - Start");
		String returnPage = "";
		try{
			this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_REVISION);
			System.out.println(">>>> taskId " + taskId);
			if(taskId!=null && taskId.trim().length()>0){
				
//				IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
//				String token = bpmSO.authenticate("roslan", "welcome1");
//				if(token!=null && !token.isEmpty()){
//					BpmRequestDTO reqBpmDTO = new BpmRequestDTO();
//					reqBpmDTO.setCtxToken(token);
//					reqBpmDTO.setTaskId(taskId);
//					BpmResponseDTO resBpmDTO = bpmSO.getTaskDetails(reqBpmDTO);
//					if(resBpmDTO!=null && resBpmDTO.isSuccessful()){
//						System.out.println(">>>>>>>>>>>>>>> resBpmDTO not null");
//						System.out.println(">>>>pojo " + resBpmDTO.getPojo());
//						System.out.println("actionsss>>>>>>>>>>>> " + resBpmDTO.getAction().length);
//						PSBudgetDataType taskData = (PSBudgetDataType) resBpmDTO.getPojo();
						BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
//						System.out.println(">>>>id " + taskData.getBudgetId());
						reqDTO.setBudgetAllocId(this.getManagedBean().getTempBudgetId()); //temp
//						reqDTO.setBudgetAllocId(Long.parseLong(taskData.getBudgetId()));
						IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
						BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetAllocation(reqDTO);
						String[] actions = new String[]{"SAVE", "SUBMIT"}; //temp
						if(resDTO!=null && resDTO.isSuccessful()){
							this.getManagedBean().setActions(actions); //temp
//							this.getManagedBean().setActions(resBpmDTO.getAction());
							this.getManagedBean().setSelectedBudgetAllocation(resDTO.getBudgetAlloc());
							System.out.println("new " +resDTO.getBudgetAlloc().isBrandNew());
							this.getManagedBean().getSelectedBudgetAllocation().setEditable(true);
							this.getManagedBean().setSelectedBudgetAllocItemRmk(new PpRemark()); //avoid null
							this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl()); //avoid null
							this.getManagedBean().getSelectedGoMapper().clear();
							for(PpBudgetAllocDtl item:resDTO.getBudgetAlloc().getPpBudgetAllocDtls()){
								updateSelectedGo(item, "INIT");
							}
							returnPage = ProcurementPlanConst.NAV_TO_BUDGET_ALLOCATION;
						}
						
//					}else{
//						System.out.println("BPM Error Message: (" + resBpmDTO.getErrorCode() + ") " + resBpmDTO.getErrorReason());
//					}
//				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		log.info("redirectToReviseBudgetAllocation - End");
		return returnPage;
	}
	*/
	public void showBudgetAllocationItem(PpBudgetAllocDtl item){
		log.info("showBudgetAllocationItem - Start");
		if(item==null){
			this.getManagedBean().setSelectedBudgetAllocItemRmk(new PpRemark());
			this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl());
			System.out.println(">>>>>>>>>>getBrandNew()" + this.getManagedBean().getSelectedBudgetAllocItem().isBrandNew());
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
	
	public void updateSelectedGo(PpBudgetAllocDtl item, String mode){
		log.info("updateSelectedGo - Start");
		
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
		log.info("updateSelectedGo - End");
	}
	
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
	
	public void deleteBudgetAllocItem(PpBudgetAllocDtl item){
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
	}
	
	
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

	
	public int getYear(int index){
		log.info("getYear - Start");
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    int year = cal.get(Calendar.YEAR)+index;
	    log.info("getYear - End");
	    return year;
	}
	
	public List<SelectItem> financialYearOptions() {
		log.info("financialYearOptions - Start");
		int currentYear = getYear(0);
		int nextYear = getYear(1);
		
		List<SelectItem>financialYearOpt = new ArrayList<SelectItem>();
		financialYearOpt.add(new SelectItem(currentYear, Integer.toString(currentYear)));
		financialYearOpt.add(new SelectItem(nextYear, Integer.toString(nextYear)));
		log.info("financialYearOptions - End");
		return financialYearOpt;
	} 
	
	public String redirectToListBudgetAllocation(){
		log.info("redirectToListBudgetAllocation");
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_LIST);
		return ProcurementPlanConst.NAV_TO_LIST_BUDGET_ALLOCATION;
	}
	
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

	
	public PpBudgetAlloc updateBudgetAllocation(String action){
		log.info("updateBudgetAllocation - Start");
	
		PpBudgetAlloc resultObj  = null;
		BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
		
		if(ProcurementPlanConst.BPM_SUBMIT.equals(action)){
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

		log.info("updateBudgetAllocation - End");
		return resultObj;
	}
	
	
	public PpBudgetAlloc addBudgetAllocation(){
		log.info("addBudgetAllocation - Start");
		PpBudgetAlloc resultObj  = null;
		
		BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
		reqDTO.setBudgetAllocation(this.getManagedBean().getSelectedBudgetAllocation());
		reqDTO.setSelectedGoMapper(this.getManagedBean().getSelectedGoMapper());
		IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
		BudgetAllocationResDTO resDTO = reqSO.addBudgetAllocation(reqDTO);
		System.out.println("after..........");
		if(resDTO.isSuccessful()){
			System.out.println("done..........");
			resultObj = resDTO.getBudgetAlloc();
			System.out.println("id... " + resultObj.getBudgetAllocId());
		}
		System.out.println("end..........");
		log.info("addBudgetAllocation - End");
		
		return resultObj;
	}
	
	public void reserveBudget(){
		log.info("reserveBudget - Start");
	
		BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
		reqDTO.setSelectedGoMapper(this.getManagedBean().getSelectedGoMapper());
		IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
		BudgetAllocationResDTO resDTO = reqSO.reserveBudget(reqDTO);
		if(resDTO.isSuccessful()){
			//re-initiate budget mapper
			PpBudgetAlloc budgetAlloc = this.getManagedBean().getSelectedBudgetAllocation();
			for(PpBudgetAllocDtl item:budgetAlloc.getPpBudgetAllocDtls()){
				updateSelectedGo(item, "INIT");
			}
		}
		System.out.println("result.........." + resDTO.isSuccessful());
		log.info("reserveBudget - End");
	}
	
	public void selectedGo(){
		log.info("selectedGo - Start");
		BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
		reqDTO.setBudgetId(this.getManagedBean().getSelectedBudgetAllocItem().getBudgetId());

		IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(ProcurementPlanConst.SO_PP, IBudgetAllocationSO.class);
		BudgetAllocationResDTO resDTO = reqSO.retrieveBudget(reqDTO);
		
		if (resDTO.isSuccessful()) {
			this.getManagedBean().getSelectedBudgetAllocItem().setPpBudget(resDTO.getBudget());
			this.getManagedBean().getSelectedBudgetAllocItem().setOvrFinancialYear(resDTO.getBudget().getFinancialYear());
//			this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().setAvailableAmount(substractBigDecimalNullable(
//					resDTO.getBudget().getBudgetAmount(), resDTO.getBudget().getUsedAmount()
//					));
//			calculateAvailableAmount(); //update avaialble budget amount
		}
		
//		if(!this.getManagedBean().getSelectedBudgetAllocItem().isBrandNew()){
//			if(this.getManagedBean().getSelectedBudgetAllocItem().getBudgetId()!=this.getManagedBean().getSelectedBudgetAllocItem().getOriPpBudget().getBudgetId()){
//				this.getManagedBean().getSelectedBudgetAllocItem().getOriPpBudget().setDeleted(true);
//				//Update used amount per GO
//				this.getManagedBean().getSelectedBudgetAllocItem().getOriPpBudget().setUsedAmount(
//						updateBudgetUsedAmount(
//								this.getManagedBean().getSelectedBudgetAllocItem().getOriPpBudget().getUsedAmount(), 
//								this.getManagedBean().getSelectedBudgetAllocItem().getOriAllocationAmount(),
//								true)
//				);
//			}
//		}
		
		log.info("selectedGo - End");
	}
	
	public void selectedSo(){
		log.info("selectedSo - Start");
		System.out.println("budgetId>>>>>>>>>>>>>>> " + this.getManagedBean().getSelectedBudgetAllocItem().getBudgetId());
		log.info("selectedSo - End");
	}
	
	public void selectedDo(){
		log.info("selectedDo - Start");
		log.info("selectedDo - End");
	}
	
	public BigDecimal generateAvailableBudget(){
		return
				CommonMethod.substractBigDecimalNullable(
				this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().getBudgetAmount(),
				this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().getUsedAmount());
		
	}


	public boolean submitNewBudget(String action){
		log.info("submitNewBudget - Start");
		boolean isSubmitted = false;
		try {
			PpBudgetAlloc ppBudgetAlloc = saveBudgetAllocation(action);
			if(ppBudgetAlloc!=null && ppBudgetAlloc.getBudgetAllocId()>0L){
				isSubmitted = initialBpmTask(action, ppBudgetAlloc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("submitNewBudget - End");
		return isSubmitted;
	}
	
	
	public boolean submitUpdateBudget(String action){
		log.info("submitUpdateBudget - Start");
		boolean isSubmitted = false;
		try {
			PpBudgetAlloc ppBudgetAlloc = saveBudgetAllocation(action);
			String taskId = this.getManagedBean().getBpmTaskId();
			if(ppBudgetAlloc!=null && taskId!=null && taskId.length()>0){
				System.out.println("call BPM.......");
				isSubmitted = submitBpmTask(taskId, action, ppBudgetAlloc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("submitUpdateBudget - End");
		return isSubmitted;
	}
	
	
	
	public String submitBudgetAllocation(String action) {
		log.info("submitBudgetAllocation - Start");
		boolean isSuccess = false;
		if(ProcurementPlanConst.BPM_SUBMIT.equals(action)){
			if(this.getManagedBean().getSelectedBudgetAllocation().isBrandNew()){
				isSuccess = submitNewBudget(action);
			}else{
				isSuccess = submitUpdateBudget(action);
			}
		}else if (ProcurementPlanConst.BPM_REVISE.equals(action)){
			isSuccess = submitUpdateBudget(action);
		}else if (ProcurementPlanConst.BPM_APPROVE.equals(action)){
			isSuccess = submitUpdateBudget(action);
		}else if (ProcurementPlanConst.BPM_SAVE.equals(action)){
			reserveBudget();
		}

		log.info("submitBudgetAllocation - End");
		if(isSuccess){
			return redirectToListBudgetAllocation();
		}else{
			return "";
		}
	}
	
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
	/*
	public BigDecimal calculateAvailableAmount(){
		log.info("calculateAvailableAmount - Start");
		System.out.println("RM " + this.getManagedBean().getSelectedBudgetAllocItem().getAllocationAmount());
		BigDecimal availableAmt = new BigDecimal(0);
		BigDecimal budgetAmt = this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().getBudgetAmount();
		BigDecimal usedAmt = this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().getUsedAmount();
		BigDecimal allocateAmt = this.getManagedBean().getSelectedBudgetAllocItem().getAllocationAmount();
				
		budgetAmt = budgetAmt!=null ? budgetAmt : new BigDecimal(0);
		usedAmt = usedAmt!=null ? usedAmt : new BigDecimal(0);
		allocateAmt = allocateAmt!=null ? allocateAmt : new BigDecimal(0);
		
		availableAmt = budgetAmt.subtract(usedAmt).subtract(allocateAmt);
		
		this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().setAvailableAmount(availableAmt);
		log.info("calculateAvailableAmount - End");
		return availableAmt;
	}*/
	/*
	public void dummySubmit() {
		// aut
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String token = bpmSO.authenticate("roslan", "welcome1");
		BpmRequestDTO reqDTO = new BpmRequestDTO();
		reqDTO.setCtxToken(token);

		// initial task
//		reqDTO.setProcessId("2307");
//		reqDTO.setOutcome("");
//		PSBudgetDataType obj = new PSBudgetDataType();
//		obj.setApprover("Asd2");
//		obj.setBudgetId("Asd2");
//		obj.setPtj("Asd2");
//		reqDTO.setPojo(obj);
//		bpmSO.initiateAndUpdateTask(reqDTO);

		// retrieve task
		reqDTO.setTaskId("c001b29b-990b-4975-ac42-be1dca9a5e18");
		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
		System.out.println(res.getPojo());
		PSBudgetDataType asd = (PSBudgetDataType) res.getPojo();	
		System.out.println(asd.getApprover());
		
		
//		
//		//plan
//		reqDTO.setTaskId("9dcd67f9-56f7-4f21-93c6-48346549924f");
//		BpmResponseDTO res = bpmSO.getTaskDetails(reqDTO);
//		System.out.println(res.getPojo());
//		PSPlanDataType asd = (PSPlanDataType) res.getPojo();
//		System.out.println(asd.getApprover());
	}
	*/
	public String generateHeader(String viewMode){
		return CommonMethod.generateHeader(viewMode, ProcurementPlanConst.PROG_BUDGET);
	}
	
	
	/**
	 *  BPM METHODS
	 */
	public boolean submitBpmTask(String taskId, String action, PpBudgetAlloc ppBudgetAlloc){
		log.info("submitBpmTask - Start");
		
		//TODO: Temp
		String login = "";
		String pwd = "welcome1";
		if("SUBMIT".equals(action)){
			login = "roslan";
		}else{
			login = "lailah";
		}
	
		log.info("taskId " + taskId);
		log.info("action " + action);
		log.info("obj " + ppBudgetAlloc!=null?"not null": "null");
		
		BpmRequestDTO reqBpm = null;
		BpmResponseDTO resBpm = null;
		
		boolean result = false;
		try{
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String token = bpmSO.authenticate(login, pwd);
			
			if(token!=null && !token.isEmpty()){
				log.info("token " + token);
				reqBpm = new BpmRequestDTO();
				reqBpm.setCtxToken(token);
				reqBpm.setOutcome(action); 
				reqBpm.setTaskId(taskId);
				if(ProcurementPlanConst.BPM_SUBMIT.equals(action)){
					PSBudgetDataType obj = new PSBudgetDataType();
					obj.setApprover(CommonMethod.approverMap.get(ppBudgetAlloc.getApproverId()));
					obj.setBudgetId(String.valueOf(ppBudgetAlloc.getBudgetAllocId()));
					obj.setPtj(String.valueOf(ppBudgetAlloc.getPmOrgProfile().getOrgProfileId())); //TODO check this id
					reqBpm.setPojo(obj);
				}
				resBpm = bpmSO.updateTask(reqBpm);
				result = resBpm!=null ? resBpm.isSuccessful() : false;
				log.info("BPM result " + result);
				if(!result){
					System.out.println("BPM Error Message: (" + resBpm.getErrorCode() + ") " + resBpm.getErrorReason());
				}
				
			}

	
		}catch(Exception e){
			System.out.println("BPM Error Message: (" + resBpm.getErrorCode() + ") " + resBpm.getErrorReason());
		}
		
		log.info("submitBpmTask - End");
		return result;
	}
	
	public boolean initialBpmTask(String action, PpBudgetAlloc ppBudgetAlloc){
		log.info("initialBpmTask - Start");
		
		log.info("action " + action);
		log.info("obj " + ppBudgetAlloc!=null?"not null": "null");
		
		boolean result = false;
		BpmResponseDTO resBpm = null;
		BpmRequestDTO reqBpm = null;
	
		try{
			IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
			String token = bpmSO.authenticate("roslan", "welcome1");
			
			if(token!=null && !token.isEmpty()){
				log.info("token " + token);
				reqBpm = new BpmRequestDTO();
				reqBpm.setCtxToken(token);
				reqBpm.setOutcome(action); 
				reqBpm.setProcessId("Procurement_Strategy.EndorseBudget");
				
				PSBudgetDataType obj = new PSBudgetDataType();
				obj.setApprover(CommonMethod.approverMap.get(ppBudgetAlloc.getApproverId()));
				obj.setBudgetId(String.valueOf(ppBudgetAlloc.getBudgetAllocId()));
				obj.setPtj(String.valueOf(ppBudgetAlloc.getPmOrgProfile().getOrgProfileId())); // TODO check this 
				reqBpm.setPojo(obj);
				resBpm = bpmSO.initiateAndUpdateTask(reqBpm);
				result = resBpm!=null ? resBpm.isSuccessful() : false;
				log.info("BPM result " + result);
				if(!result){
					System.out.println("BPM Error Message: (" + resBpm.getErrorCode() + ") " + resBpm.getErrorReason());
				}
			}

		}catch(Exception e){
			System.out.println("BPM Error Message: (" + resBpm.getErrorCode() + ") " + resBpm.getErrorReason());
		}
		log.info("submitBpmTask - End");
		return result;
	}
	
	//public void generateEditableMode(String status){
		//NEW - new and editable or not new and editable
		//open all fields  disabled !((MODENEW && (editable || brandnew)) & (MODEREVISION && (revisionRequired && editable)))
		
		//REVISION - not new and editable
		//open editable field and revision required (MODEREVISION && (revisionRequired && editable))
		
		//APPROVAL - not editable 
		//open revision flag (MODEAPPROVAL)
		
		//APPROVED - read only (MODEAPPROVED)
		//close all
	//}

}
