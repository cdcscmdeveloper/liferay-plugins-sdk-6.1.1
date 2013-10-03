package my.ep.web.backingbean.pp.budgetallocation;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.Transient;
import javax.portlet.PortletRequest;

import my.ep.biz.dto.pp.budgetallocation.BudgetAllocationReqDTO;
import my.ep.biz.dto.pp.budgetallocation.BudgetAllocationResDTO;
import my.ep.biz.service.pp.budgetallocation.IBudgetAllocationSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.dto.BpmResponseDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.jaxb.pp.budgetdata.PSBudgetDataType;
import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAlloc;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtlRemark;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocRemark;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.pp.ProcurementPlanConst;
import my.ep.web.managedbean.pp.budgetallocation.BudgetAllocationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

@ManagedBean(name="budgetAllocationBackingBean")
@RequestScoped
public class BudgetAllocationBackingBean extends BaseBackingBean<BudgetAllocationManagedBean>{
	private static Logger log = Logger.getLogger(BudgetAllocationBackingBean.class);

	
	//temp
	private static final Map<Long, String> approverMap;
    static
    {
    	approverMap = new HashMap<Long, String>();
    	approverMap.put(1L, "lailah");
    	approverMap.put(2L, "khai");
    }
	
	
	private PortletRequest getPortletRequest() {
		return (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}
	
	public BudgetAllocationBackingBean(){
		log.info("BudgetAllocationBackingBean - Start");
		System.out.println(">>>>>>>>>>>>>>>>..view " + this.getManagedBean().getViewMode());
		if(this.getManagedBean().getViewMode()==null || ProcurementPlanConst.MODE_LIST.equals(this.getManagedBean().getViewMode())){
			getAllBudgets();
		}
			
		log.info("BudgetAllocationBackingBean - End");
	}
	

	
	private void getAllBudgets(){
		log.info("getAllBudgets - Start");
		BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
		reqDTO.setMinistryId(1L);	// TODO dynamic

		IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(IBudgetAllocationSO.class);
		BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetList(reqDTO);
		
		if (resDTO.isSuccessful()) {
			this.getManagedBean().setBudgetList(resDTO.getBudgetList());
		}
		log.info("getAllBudgets - End");
	}
	
	
	public String redirectToCreateBudgetAllocation(){
		log.info("redirectToCreateBudgetAllocation - Start");
		this.getManagedBean().getSelectedGoMapper().clear();
		this.getManagedBean().setViewMode(ProcurementPlanConst.MODE_NEW);
		this.getManagedBean().setSelectedBudgetAllocation(new PpBudgetAlloc());
		this.getManagedBean().getSelectedBudgetAllocation().setStatusCode("New");
		this.getManagedBean().getSelectedBudgetAllocation().setEditable(true);
		this.getManagedBean().setSelectedBudgetAllocItemRmk(new PpBudgetAllocDtlRemark());
		this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl());
		this.getManagedBean().getSelectedBudgetAllocItem().setPpBudget(new PpBudget());
		this.getManagedBean().getSelectedBudgetAllocation().setPpBudgetAllocDtls(new ArrayList<PpBudgetAllocDtl>());
		this.getManagedBean().getSelectedBudgetAllocation().setPpBudgetAllocRemarks(new ArrayList<PpBudgetAllocRemark>());
		
		log.info("redirectToCreateBudgetAllocation - End");
		return ProcurementPlanConst.NAV_TO_CREATE_BUDGET_ALLOCATION;
    } 
	
	public String redirectToEndorseBudgetAllocation(String taskId){
		log.info("redirectToEndorseBudgetAllocation - Start");
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
//					if(resBpmDTO!=null){
//						System.out.println(">>>>>>>>>>>>>>> resBpmDTO not null");
//						System.out.println(">>>>pojo " + resBpmDTO.getPojo());
//						System.out.println("actionsss>>>>>>>>>>>> " + resBpmDTO.getAction().length);
//						
//						 String[] actionsZ
//						PSBudgetDataType taskData = (PSBudgetDataType) resBpmDTO.getPojo();
						BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
//						System.out.println(">>>>id " + taskData.getBudgetId());
						reqDTO.setBudgetAllocId(140L);
						IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(IBudgetAllocationSO.class);
						BudgetAllocationResDTO resDTO = reqSO.retrieveBudgetAllocation(reqDTO);
						 String[] actions = new String[]{"REVISE", "APPROVE"};
						this.getManagedBean().setActions(actions);
						this.getManagedBean().setSelectedBudgetAllocation(resDTO.getBudgetAlloc());
						System.out.println("new " +resDTO.getBudgetAlloc().isBrandNew());
						this.getManagedBean().getSelectedBudgetAllocation().setEditable(false);
						this.getManagedBean().setSelectedBudgetAllocItemRmk(new PpBudgetAllocDtlRemark()); //avoid null
						this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl()); //avoid null
						this.getManagedBean().getSelectedGoMapper().clear();
						for(PpBudgetAllocDtl item:resDTO.getBudgetAlloc().getPpBudgetAllocDtls()){
							updateSelectedGo(item, "ADD");
						}
//					}
//				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		log.info("redirectToEndorseBudgetAllocation - End");
		return ProcurementPlanConst.NAV_TO_REVIEW_BUDGET_ALLOCATION;
	}
	
	public void showBudgetAllocationItem(PpBudgetAllocDtl item){
		log.info("showBudgetAllocationItem - Start");
		if(item==null){
			this.getManagedBean().setSelectedBudgetAllocItemRmk(new PpBudgetAllocDtlRemark());
			//TODO: Temp & To be removed
			this.getManagedBean().getSelectedBudgetAllocItemRmk().setCreatedBy(1L);
			this.getManagedBean().getSelectedBudgetAllocItemRmk().setCreatedDate(new Timestamp(new Date().getTime()));
			this.getManagedBean().getSelectedBudgetAllocItemRmk().setChangedBy(1L);
			this.getManagedBean().getSelectedBudgetAllocItemRmk().setChangedDate(new Timestamp(new Date().getTime()));
			
			this.getManagedBean().setSelectedBudgetAllocItem(new PpBudgetAllocDtl());
			this.getManagedBean().getSelectedBudgetAllocItem().setSelected(false);
			this.getManagedBean().getSelectedBudgetAllocItem().setPpBudget(new PpBudget());
		}else{
			item.setSelected(true);
			this.getManagedBean().setSelectedBudgetAllocItem(item);
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
					substractBigDecimalNullable(
							this.getManagedBean().getSelectedGoMapper().get(oldKey), 
							item.getOriAllocationAmount())
			);
			System.out.println(">>>>>>>>ori amount " + item.getOriAllocationAmount());
			System.out.println(">>>>>>>>ori Budget " + item.getOriPpBudget().getBudgetId());
			System.out.println(	this.getManagedBean().getSelectedGoMapper().get(key) + "+" + item.getAllocationAmount());
			this.getManagedBean().getSelectedGoMapper().put(
					key, 
					addBigDecimalNullable(
							this.getManagedBean().getSelectedGoMapper().get(key),
							item.getAllocationAmount())
			);
			
			item.setOriAllocationAmount(item.getAllocationAmount());//update ori amount to new amount
			item.setOriPpBudget(item.getPpBudget());//update ori budget to new budget
			System.out.println(">>>>>>>>update ori amount " + item.getOriAllocationAmount());
			System.out.println(">>>>>>>>update ori Budget " + item.getOriPpBudget().getBudgetId());
		}else if("REMOVE".equals(mode)){
			//remove item
			this.getManagedBean().getSelectedGoMapper().put(
					key, 
					substractBigDecimalNullable(
							this.getManagedBean().getSelectedGoMapper().get(key), 
							item.getAllocationAmount())
			);
		}else if("ADD".equals(mode)){
			//add item
//			if(this.getManagedBean().getSelectedGoMapper().get(key)==null){
//				this.getManagedBean().getSelectedGoMapper().put(key, item.getAllocationAmount());
//			}else{
				this.getManagedBean().getSelectedGoMapper().put(
						key, 
						addBigDecimalNullable(
							this.getManagedBean().getSelectedGoMapper().get(key),
							item.getAllocationAmount())
				);
			item.setOriAllocationAmount(item.getAllocationAmount());//initiate ori amount
			item.setOriPpBudget(item.getPpBudget());//initiate ori budget
//			}
		}else if("UPDATE".equals(mode)){
			//update item with same GO
			this.getManagedBean().getSelectedGoMapper().put(
					key, 
					substractBigDecimalNullable(
							this.getManagedBean().getSelectedGoMapper().get(key), 
							item.getOriAllocationAmount())
			);
			System.out.println(">>>>>>>>ori RM " + item.getOriAllocationAmount());
			this.getManagedBean().getSelectedGoMapper().put(
					key, 
					addBigDecimalNullable(
							this.getManagedBean().getSelectedGoMapper().get(key),
							item.getAllocationAmount())
			);
			item.setOriAllocationAmount(item.getAllocationAmount());//update ori amount to new amount
			System.out.println(">>>>>>>>update ori RM " + item.getOriAllocationAmount());
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
		//TODO: Temp & To be removed
		if(this.getManagedBean().getSelectedBudgetAllocItem().isBrandNew()){
			this.getManagedBean().getSelectedBudgetAllocItem().setCreatedBy(1L);
			this.getManagedBean().getSelectedBudgetAllocItem().setCreatedDate(new Timestamp(new Date().getTime()));
		}
		this.getManagedBean().getSelectedBudgetAllocItem().setChangedBy(1L);
		this.getManagedBean().getSelectedBudgetAllocItem().setChangedDate(new Timestamp(new Date().getTime()));
		
		//Add the item remark to the list
		if(this.getManagedBean().getSelectedBudgetAllocItem().getPpBudgetAllocDtlRemarks()==null 
				&& !this.getManagedBean().getSelectedBudgetAllocItemRmk().getRemark().isEmpty()){
			this.getManagedBean().getSelectedBudgetAllocItem().setPpBudgetAllocDtlRemarks(new ArrayList<PpBudgetAllocDtlRemark>());
			this.getManagedBean().getSelectedBudgetAllocItem().addPpBudgetAllocDtlRemarks(this.getManagedBean().getSelectedBudgetAllocItemRmk());
		}
		
		if(this.getManagedBean().getSelectedBudgetAllocItem().isBrandNew() && !this.getManagedBean().getSelectedBudgetAllocItem().isSelected()){
			//update amount
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
	
	
	public BigDecimal addBigDecimalNullable(BigDecimal amountOne, BigDecimal amountTwo){
		BigDecimal amtOne = amountOne!=null ? amountOne : new BigDecimal(0);
		BigDecimal amtTwo =  amountTwo!=null ? amountTwo : new BigDecimal(0);
		return amtOne.add(amtTwo);
	}
	
	public BigDecimal substractBigDecimalNullable(BigDecimal amountOne, BigDecimal amountTwo){
		BigDecimal amtOne = amountOne!=null ? amountOne : new BigDecimal(0);
		BigDecimal amtTwo =  amountTwo!=null ? amountTwo : new BigDecimal(0);
		return amtOne.subtract(amtTwo);
	}
	
	
	public void addBudgetAllocRemark(){
		log.info("addBudgetAllocRemark - Start");
		PpBudgetAllocRemark remark = new PpBudgetAllocRemark();
		
		//TODO: Temp & To be removed
		remark.setCreatedBy(1L);
		remark.setCreatedDate(new Timestamp(new Date().getTime()));
		remark.setChangedBy(1L);
		remark.setChangedDate(new Timestamp(new Date().getTime()));
		
		
		if(this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocRemarks()==null){
			this.getManagedBean().getSelectedBudgetAllocation().setPpBudgetAllocRemarks(new ArrayList<PpBudgetAllocRemark>());
		}
		//this.getManagedBean().getSelectedBudgetAllocation().addPpBudgetAllocRemarks(remark);
		this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocRemarks().add(remark);
		System.out.println(">>>>>>>> " + this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocRemarks().size());
		log.info("addBudgetAllocRemark - End");
	}
	
	
	
	public String generateHeader(String viewMode){
		log.info("generateHeader - Start");
		String titlePrefix = "";
		String titleLabel = "";

		if(ProcurementPlanConst.MODE_NEW.equals(viewMode)){
			titlePrefix = "lbl.pp.budget.title.new";
		}else if(ProcurementPlanConst.MODE_LIST.equals(viewMode)){
			titlePrefix = "lbl.pp.budget.title.list";
		}else if(ProcurementPlanConst.MODE_APPROVAL.equals(viewMode)){
			titlePrefix = "lbl.pp.budget.title.approval";
		}else if(ProcurementPlanConst.MODE_REVISION.equals(viewMode)){
			titlePrefix = "lbl.pp.plan.budget.revision";
		}
		
		titleLabel = ResourceBundleUtils.getMessage(titlePrefix, 
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, 
				new FacesScopes().getUserLocale());
		
		log.info("generateHeader - End");
		return titleLabel;
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
	
	public PpBudgetAlloc saveBudgetAllocation(){
		log.info("saveBudgetAllocation - Start");
		PpBudgetAlloc resultObj = null;
		boolean brandNew = this.getManagedBean().getSelectedBudgetAllocation().isBrandNew();
		if (!brandNew) {
			resultObj = updateBudgetAllocation();
		} else {
			resultObj = addBudgetAllocation();
		}
		log.info("saveBudgetAllocation - End");
		return resultObj;
	}

	
	public PpBudgetAlloc updateBudgetAllocation(){
		log.info("updateBudgetAllocation - Start");
	
		PpBudgetAlloc resultObj  = null;
		
		this.getManagedBean().getSelectedBudgetAllocation().setChangedBy(1L);
		this.getManagedBean().getSelectedBudgetAllocation().setChangedDate(new Timestamp(new Date().getTime()));

		BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
		reqDTO.setBudgetAllocation(this.getManagedBean().getSelectedBudgetAllocation());
		reqDTO.setSelectedGoMapper(this.getManagedBean().getSelectedGoMapper());
		IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(IBudgetAllocationSO.class);
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
		
		//TODO: Temp & To be removed
		this.getManagedBean().getSelectedBudgetAllocation().setCreatedBy(1L);
		this.getManagedBean().getSelectedBudgetAllocation().setCreatedDate(new Timestamp(new Date().getTime()));
		this.getManagedBean().getSelectedBudgetAllocation().setChangedBy(1L);
		this.getManagedBean().getSelectedBudgetAllocation().setChangedDate(new Timestamp(new Date().getTime()));

		BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
		reqDTO.setBudgetAllocation(this.getManagedBean().getSelectedBudgetAllocation());
		reqDTO.setSelectedGoMapper(this.getManagedBean().getSelectedGoMapper());
		IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(IBudgetAllocationSO.class);
		BudgetAllocationResDTO resDTO = reqSO.addBudgetAllocation(reqDTO);
		
		if(resDTO.isSuccessful()){
			resultObj = resDTO.getBudgetAlloc();
		}
	
		log.info("addBudgetAllocation - End");
		
		return resultObj;
		
	}
	
	public void selectedGo(){
		log.info("selectedGo - Start");
		BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
		reqDTO.setBudgetId(this.getManagedBean().getSelectedBudgetAllocItem().getBudgetId());

		IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(IBudgetAllocationSO.class);
		BudgetAllocationResDTO resDTO = reqSO.retrieveBudget(reqDTO);
		
		if (resDTO.isSuccessful()) {
			this.getManagedBean().getSelectedBudgetAllocItem().setPpBudget(resDTO.getBudget());
			this.getManagedBean().getSelectedBudgetAllocItem().setOvrFinancialYear(resDTO.getBudget().getFinancialYear());
			this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().setAvailableAmount(substractBigDecimalNullable(
					resDTO.getBudget().getBudgetAmount(), resDTO.getBudget().getUsedAmount()
					));
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
	}

	public boolean submitNewBudget(String action){
		log.info("submitNewBudget - Start");
		boolean isSubmitted = false;
		PpBudgetAlloc ppBudgetAlloc = saveBudgetAllocation();
		if(ppBudgetAlloc!=null && ppBudgetAlloc.getBudgetAllocId()>0L){
			try {
				isSubmitted = initialBpmTask(action, ppBudgetAlloc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		log.info("submitNewBudget - End");
		return isSubmitted;
	}
	
	
	public boolean submitUpdateBudget(String action){
		log.info("submitUpdateBudget - Start");
		boolean isSubmitted = false;
		PpBudgetAlloc ppBudgetAlloc = saveBudgetAllocation();
		String taskId = this.getManagedBean().getBpmTaskId();
		try {
			if(taskId!=null && taskId.length()>0){
				isSubmitted = submitBpmTask(taskId, action, ppBudgetAlloc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("submitUpdateBudget - End");
		return isSubmitted;
	}
	
	public boolean submitBpmTask(String taskId, String action, PpBudgetAlloc ppBudgetAlloc){
		log.info("submitBpmTask - Start");
		String login = "";
		String pwd = "welcome1";
		//temp
		if("SUBMIT".equals(action)){
			login = "roslan";
		}else{
			login = "lailah";
		}
		
		boolean result = false;
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String token = bpmSO.authenticate(login, pwd);
		BpmResponseDTO res = null;
		if(token!=null && !token.isEmpty()){
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setOutcome(action); 
			reqDTO.setTaskId(taskId);
			if("SUBMIT".equals(action)){
				PSBudgetDataType obj = new PSBudgetDataType();
				obj.setApprover(approverMap.get(ppBudgetAlloc.getApproverId()));
				obj.setBudgetId(String.valueOf(ppBudgetAlloc.getBudgetAllocId()));
				obj.setPtj(String.valueOf(ppBudgetAlloc.getDivisionId()));
				reqDTO.setPojo(obj);
			}
			res = bpmSO.updateTask(reqDTO);
			result = res!=null ? res.isSuccessful() : false;
		}
		log.info("submitBpmTask - End");
		return result;
	}
	
	public boolean initialBpmTask(String action, PpBudgetAlloc ppBudgetAlloc){
		log.info("initialBpmTask - Start");
		boolean result = false;
		IBpmSO bpmSO = (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(IBpmSO.class);
		String token = bpmSO.authenticate("roslan", "welcome1");
		BpmResponseDTO res = null;
		if(token!=null && !token.isEmpty()){
			BpmRequestDTO reqDTO = new BpmRequestDTO();
			reqDTO.setCtxToken(token);
			reqDTO.setOutcome(action); 
			reqDTO.setProcessId("Procurement_Strategy.EndorseBudget");
			
			PSBudgetDataType obj = new PSBudgetDataType();
			obj.setApprover(approverMap.get(ppBudgetAlloc.getApproverId()));
			obj.setBudgetId(String.valueOf(ppBudgetAlloc.getBudgetAllocId()));
			obj.setPtj(String.valueOf(ppBudgetAlloc.getDivisionId()));
			reqDTO.setPojo(obj);
		
			res = bpmSO.initiateAndUpdateTask(reqDTO);
			result = res!=null ? res.isSuccessful() : false;
		}
		log.info("submitBpmTask - End");
		return result;
	}
	
	public String submitBudgetAllocation(String action) {
		log.info("submitBudgetAllocation - Start");
		if("SUBMIT".equals(action)){
			if(this.getManagedBean().getSelectedBudgetAllocation().isBrandNew()){
				submitNewBudget(action);
			}else{
				submitUpdateBudget(action);
			}
		}else if ("REVISE".equals(action)){
			submitUpdateBudget(action);
		}else if ("APPROVE".equals(action)){
			submitUpdateBudget(action);
		}else if ("SAVE".equals(action)){
			saveBudgetAllocation();
		}
		
		
		log.info("submitBudgetAllocation - End");
		if("SAVE".equals(action)){
			return "";
		}else{
			return redirectToListBudgetAllocation();
		}
		
	}
	
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
	

}
