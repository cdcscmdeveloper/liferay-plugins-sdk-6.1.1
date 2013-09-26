package my.ep.web.backingbean.pp.budgetallocation;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.portlet.PortletRequest;

import my.ep.biz.dto.pp.budgetallocation.BudgetAllocationReqDTO;
import my.ep.biz.dto.pp.budgetallocation.BudgetAllocationResDTO;
import my.ep.biz.service.pp.budgetallocation.IBudgetAllocationSO;
import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAlloc;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtlRemark;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocRemark;
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
	
	private PortletRequest getPortletRequest() {
		return (PortletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}
	
	public BudgetAllocationBackingBean(){
		log.info("BudgetAllocationBackingBean - Start");
		getAllBudgets();
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
			this.getManagedBean().getSelectedBudgetAllocItem().setPpBudget(new PpBudget());
		}else{
			this.getManagedBean().setSelectedBudgetAllocItem(item);
		}
		log.info("showBudgetAllocationItem - End");
	}
	
	public void addBudgetAllocationItemList(){
		log.info("addBudgetAllocationItemList - Start");
		//Initial item list if empty
		if (this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls()== null){
			this.getManagedBean().getSelectedBudgetAllocation().setPpBudgetAllocDtls(new ArrayList<PpBudgetAllocDtl>());
		}
		//TODO: Temp & To be removed
		this.getManagedBean().getSelectedBudgetAllocItem().setCreatedBy(1L);
		this.getManagedBean().getSelectedBudgetAllocItem().setCreatedDate(new Timestamp(new Date().getTime()));
		this.getManagedBean().getSelectedBudgetAllocItem().setChangedBy(1L);
		this.getManagedBean().getSelectedBudgetAllocItem().setChangedDate(new Timestamp(new Date().getTime()));
		
		//Add the item remark to the list
		if(this.getManagedBean().getSelectedBudgetAllocItem().getPpBudgetAllocDtlRemarks()==null 
				&& !this.getManagedBean().getSelectedBudgetAllocItemRmk().getRemark().isEmpty()){
			this.getManagedBean().getSelectedBudgetAllocItem().setPpBudgetAllocDtlRemarks(new ArrayList<PpBudgetAllocDtlRemark>());
			this.getManagedBean().getSelectedBudgetAllocItem().addPpBudgetAllocDtlRemarks(this.getManagedBean().getSelectedBudgetAllocItemRmk());
		}
		
		//Update used amount per GO
		this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().setUsedAmount(
				updateBudgetUsedAmount(
						this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().getUsedAmount(), 
						this.getManagedBean().getSelectedBudgetAllocItem().getAllocationAmount())
				);
		this.getManagedBean().getSelectedBudgetAllocation().addPpBudgetAllocDtls(this.getManagedBean().getSelectedBudgetAllocItem());
		log.info("addBudgetAllocationItemList - End");
	}
	
	public BigDecimal updateBudgetUsedAmount(BigDecimal usedAmount, BigDecimal allocateAmount){
		this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().setUsedAmount(usedAmount);
		BigDecimal oriUsedAmt = usedAmount!=null ? usedAmount : new BigDecimal(0);
		BigDecimal allocateAmt =  allocateAmount!=null ? allocateAmount : new BigDecimal(0);
		return oriUsedAmt.add(allocateAmt);
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
		this.getManagedBean().getSelectedBudgetAllocation().addPpBudgetAllocRemarks(remark);
		log.info("addBudgetAllocRemark - End");
	}
	
	public void deleteBudgetAllocItem(PpBudgetAllocDtl item){
		if(item!=null){
			if(item.getBudgetAllocDtlId()>0){
				item.setDeleted(true);
			}else{
				this.getManagedBean().getSelectedBudgetAllocation().getPpBudgetAllocDtls().remove(item);
			}
		}
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
		return ProcurementPlanConst.NAV_TO_LIST_BUDGET_ALLOCATION;
	}
	
	public String saveBudgetAllocation(){
		log.info("saveBudgetAllocation - Start");
		String returnPage = "";
		boolean result = false;
		boolean brandNew = this.getManagedBean().getSelectedBudgetAllocation().isBrandNew();
		if (!brandNew) {
			result = updateBudgetAllocation();
		} else {
			//TODO: Temp & To be removed
			this.getManagedBean().getSelectedBudgetAllocation().setCreatedBy(1L);
			this.getManagedBean().getSelectedBudgetAllocation().setCreatedDate(new Timestamp(new Date().getTime()));
			this.getManagedBean().getSelectedBudgetAllocation().setChangedBy(1L);
			this.getManagedBean().getSelectedBudgetAllocation().setChangedDate(new Timestamp(new Date().getTime()));
			result = addBudgetAllocation();
		}
		if (result){
			returnPage = ProcurementPlanConst.NAV_TO_LIST_BUDGET_ALLOCATION;
		}
		log.info("saveBudgetAllocation - End");
		return returnPage;
	}
	
	public boolean updateBudgetAllocation(){
		log.info("updateBudgetAllocation - Start");
		
		log.info("updateBudgetAllocation - End");
		return false;
	}
	
	public void submitBudgetAllocation(){
		log.info("submitBudgetAllocation - Start");
		
		log.info("submitBudgetAllocation - End");
	}
	public boolean addBudgetAllocation(){
		log.info("addBudgetAllocation - Start");
		BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
		reqDTO.setBudgetAllocation(this.getManagedBean().getSelectedBudgetAllocation());
		IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(IBudgetAllocationSO.class);
		BudgetAllocationResDTO resDTO = reqSO.addBudgetAllocation(reqDTO);
		log.info("addBudgetAllocation - End");
		return resDTO.isSuccessful();
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
			this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().setAvailableAmount(
					calculateAvailableAmount());
		}
		log.info("selectedGo - End");
	}
	
	public void selectedSo(){
		log.info("selectedSo - Start");
		System.out.println("budgetId>>>>>>>>>>>>>>> " + this.getManagedBean().getSelectedBudgetAllocItem().getBudgetId());
//		BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
//		reqDTO.setBudgetId(this.getManagedBean().getSelectedBudgetAllocItem().getBudgetId());
//
//		IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(IBudgetAllocationSO.class);
//		BudgetAllocationResDTO resDTO = reqSO.retrieveBudget(reqDTO);
//		
//		if (resDTO.isSuccessful()) {
//			this.getManagedBean().getSelectedBudgetAllocItem().setPpBudget(resDTO.getBudget());
//			this.getManagedBean().getSelectedBudgetAllocItem().setOvrFinancialYear(resDTO.getBudget().getFinancialYear());
//			this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().setAvailableAmount(
//					calculateAvailableAmount());
//		}
		log.info("selectedSo - End");
	}
	
	public void selectedDo(){
		log.info("selectedDo - Start");

//		BudgetAllocationReqDTO reqDTO = new BudgetAllocationReqDTO();
//		reqDTO.setBudgetId(this.getManagedBean().getSelectedBudgetAllocItem().getBudgetId());
//
//		IBudgetAllocationSO reqSO = (IBudgetAllocationSO) BusinessDelegate.getInstance().getServiceObject(IBudgetAllocationSO.class);
//		BudgetAllocationResDTO resDTO = reqSO.retrieveBudget(reqDTO);
//		
//		if (resDTO.isSuccessful()) {
//			this.getManagedBean().getSelectedBudgetAllocItem().setPpBudget(resDTO.getBudget());
//			this.getManagedBean().getSelectedBudgetAllocItem().setOvrFinancialYear(resDTO.getBudget().getFinancialYear());
//			this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().setAvailableAmount(
//					calculateAvailableAmount());
//		}
		log.info("selectedDo - End");
	}
	
	public BigDecimal calculateAvailableAmount(){
		log.info("calculateAvailableAmount - Start");
		BigDecimal availableAmt = new BigDecimal(0);
		BigDecimal budgetAmt = this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().getBudgetAmount();
		BigDecimal usedAmt = this.getManagedBean().getSelectedBudgetAllocItem().getPpBudget().getUsedAmount();
		BigDecimal allocateAmt = this.getManagedBean().getSelectedBudgetAllocItem().getAllocationAmount();
				
		budgetAmt = budgetAmt!=null ? budgetAmt : new BigDecimal(0);
		usedAmt = usedAmt!=null ? usedAmt : new BigDecimal(0);
		allocateAmt = allocateAmt!=null ? allocateAmt : new BigDecimal(0);
		
		availableAmt = budgetAmt.subtract(usedAmt).subtract(allocateAmt);
		log.info("calculateAvailableAmount - End");
		return availableAmt;
	}

}
