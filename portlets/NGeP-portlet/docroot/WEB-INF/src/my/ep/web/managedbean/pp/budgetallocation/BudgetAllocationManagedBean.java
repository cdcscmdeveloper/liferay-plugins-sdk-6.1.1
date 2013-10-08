package my.ep.web.managedbean.pp.budgetallocation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAlloc;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtlRemark;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "budgetAllocationManagedBean")
@SessionScoped
public class BudgetAllocationManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;

	private Map<Long, BigDecimal> selectedGoMapper = new HashMap<Long, BigDecimal>();

	private List <PpBudget> budgetList;

	private PpBudgetAlloc selectedBudgetAllocation;
	
	private PpBudgetAllocDtl selectedBudgetAllocItem;
	
	private PpBudgetAllocDtlRemark selectedBudgetAllocItemRmk;

	private List <PpBudgetAlloc> budgetAllocationList;
	
	private String[] actions;

	private String viewMode;
	
	private String bpmTaskId; 
	
	
	private Long tempBudgetId; //TODO : temp


	/**
	 * @return the tempBudgetId
	 */
	public Long getTempBudgetId() {
		return tempBudgetId;
	}

	/**
	 * @param tempBudgetId the tempBudgetId to set
	 */
	public void setTempBudgetId(Long tempBudgetId) {
		this.tempBudgetId = tempBudgetId;
	}

	/**
	 * @return the bpmTaskId
	 */
	public String getBpmTaskId() {
		return bpmTaskId;
	}

	/**
	 * @param bpmTaskId the bpmTaskId to set
	 */
	public void setBpmTaskId(String bpmTaskId) {
		this.bpmTaskId = bpmTaskId;
	}

	public List<PpBudget> getBudgetList() {
//		if(budgetList==null){
//			budgetList = new ArrayList<PpBudget>();
//		}
		return budgetList;
	}

	public void setBudgetList(List<PpBudget> budgetList) {
		this.budgetList = budgetList;
	}
	
	public PpBudgetAlloc getSelectedBudgetAllocation() {
		return selectedBudgetAllocation;
	}

	public void setSelectedBudgetAllocation(PpBudgetAlloc selectedBudgetAllocation) {
		this.selectedBudgetAllocation = selectedBudgetAllocation;
	}

	public PpBudgetAllocDtl getSelectedBudgetAllocItem() {
		return selectedBudgetAllocItem;
	}

	public void setSelectedBudgetAllocItem(PpBudgetAllocDtl selectedBudgetAllocItem) {
		this.selectedBudgetAllocItem = selectedBudgetAllocItem;
	}

	public List<PpBudgetAlloc> getBudgetAllocationList() {
		return budgetAllocationList;
	}

	public void setBudgetAllocationList(List<PpBudgetAlloc> budgetAllocationList) {
		this.budgetAllocationList = budgetAllocationList;
	}

	public String getViewMode() {
		return viewMode;
	}

	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}
	
	public PpBudgetAllocDtlRemark getSelectedBudgetAllocItemRmk() {
		return selectedBudgetAllocItemRmk;
	}

	public void setSelectedBudgetAllocItemRmk(
			PpBudgetAllocDtlRemark selectedBudgetAllocItemRmk) {
		this.selectedBudgetAllocItemRmk = selectedBudgetAllocItemRmk;
	}

	/**
	 * @return the actions
	 */
	public String[] getActions() {
		return actions;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(String[] actions) {
		this.actions = actions;
	}

	
	
	
	/**
	 * @return the selectedGoMapper
	 */
	public Map<Long, BigDecimal> getSelectedGoMapper() {
		return selectedGoMapper;
	}

	/**
	 * @param selectedGoMapper the selectedGoMapper to set
	 */
	public void setSelectedGoMapper(Map<Long, BigDecimal> selectedGoMapper) {
		this.selectedGoMapper = selectedGoMapper;
	}
	
	
	
	
	
	
//	
//	private PpBudgetAlloc budgetAllocation = null;
//	
//	public PpBudgetAlloc getBudgetAllocation() {
//		return budgetAllocation;
//	}
//
//	public void setBudgetAllocation(PpBudgetAlloc budgetAllocation) {
//		this.budgetAllocation = budgetAllocation;
//	}
//
//
//	
//	
//	
//	private String pageInd;
//
//	//temp
//	private List budgetAllocationDetList = null;
//	//temp
//	private List remarkList = null;
//	
//	private boolean popup = false;
//
//	//temp
//	private boolean brandNew;
//	
//	
//	
//	
//	public boolean isBrandNew() {
//		return brandNew;
//	}
//
//	public void setBrandNew(boolean brandNew) {
//		this.brandNew = brandNew;
//	}
//
//	public boolean isPopup() {
//		return popup;
//	}
//
//	public void setPopup(boolean popup) {
//		this.popup = popup;
//	}
//
//	public List getRemarkList() {
//		return remarkList;
//	}
//
//	public void setRemarkList(List remarkList) {
//		this.remarkList = remarkList;
//	}
//
//
//	public List getBudgetAllocationDetList() {
//		return budgetAllocationDetList;
//	}
//
//	public void setBudgetAllocationDetList(List budgetAllocationDetList) {
//		this.budgetAllocationDetList = budgetAllocationDetList;
//	}
//
//
//	public String getPageInd() {
//		if(this.pageInd == null){
//			pageInd = "V";
//		}
//		return pageInd;
//	}
//
//	public void setPageInd(String pageInd) {
//		this.pageInd = pageInd;
//	}
//
//
//
}
