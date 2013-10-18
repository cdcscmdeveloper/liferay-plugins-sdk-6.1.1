package my.ep.web.managedbean.pp.budgetallocation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pp.PpRemark;
import my.ep.persistence.entity.pp.budgetallocation.PpBudget;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAlloc;
import my.ep.persistence.entity.pp.budgetallocation.PpBudgetAllocDtl;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "budgetAllocationManagedBean")
@SessionScoped
public class BudgetAllocationManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;

	private Map<Long, BigDecimal> selectedGoMapper = new HashMap<Long, BigDecimal>();

	private List <PpBudget> budgetList;

	private PpBudgetAlloc selectedBudgetAllocation;
	
	private PpBudgetAllocDtl selectedBudgetAllocItem;
	
	private PpRemark selectedBudgetAllocItemRmk;

	private List <PpBudgetAlloc> budgetAllocationList;
	
	private String[] actions;

	private String viewMode;
	
	private String bpmTaskId; 
	
	private Long tempBudgetId; //TODO : temp for task retrieving
  

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

	/**
	 * @return the budgetList
	 */
	public List<PpBudget> getBudgetList() {
		return budgetList;
	}

	/**
	 * @param budgetList the budgetList to set
	 */
	public void setBudgetList(List<PpBudget> budgetList) {
		this.budgetList = budgetList;
	}
	
	/**
	 * @return the selectedBudgetAllocation
	 */
	public PpBudgetAlloc getSelectedBudgetAllocation() {
		return selectedBudgetAllocation;
	}

	/**
	 * @param selectedBudgetAllocation the selectedBudgetAllocation to set
	 */
	public void setSelectedBudgetAllocation(PpBudgetAlloc selectedBudgetAllocation) {
		this.selectedBudgetAllocation = selectedBudgetAllocation;
	}

	/**
	 * @return the selectedBudgetAllocItem
	 */
	public PpBudgetAllocDtl getSelectedBudgetAllocItem() {
		return selectedBudgetAllocItem;
	}

	/**
	 * @param selectedBudgetAllocItem the selectedBudgetAllocItem to set
	 */
	public void setSelectedBudgetAllocItem(PpBudgetAllocDtl selectedBudgetAllocItem) {
		this.selectedBudgetAllocItem = selectedBudgetAllocItem;
	}

	/**
	 * @return the budgetAllocationList
	 */
	public List<PpBudgetAlloc> getBudgetAllocationList() {
		return budgetAllocationList;
	}

	/**
	 * @param budgetAllocationList the budgetAllocationList to set
	 */
	public void setBudgetAllocationList(List<PpBudgetAlloc> budgetAllocationList) {
		this.budgetAllocationList = budgetAllocationList;
	}
	
	/**
	 * @return the viewMode
	 */
	public String getViewMode() {
		return viewMode;
	}

	/**
	 * @param viewMode the viewMode to set
	 */
	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}
	
	/**
	 * @return the selectedBudgetAllocItemRmk
	 */
	public PpRemark getSelectedBudgetAllocItemRmk() {
		return selectedBudgetAllocItemRmk;
	}

	/**
	 * @param selectedBudgetAllocItemRmk the selectedBudgetAllocItemRmk to set
	 */
	public void setSelectedBudgetAllocItemRmk(
			PpRemark selectedBudgetAllocItemRmk) {
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
	
	
	
	
}
