package my.ep.web.managedbean.pp.budgetallocation;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "budgetAllocationManagedBean")
@SessionScoped
public class BudgetAllocationManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	
	private List budgetList = null;
	
	public List getBudgetList() {
		return budgetList;
	}

	public void setBudgetList(List budgetList) {
		this.budgetList = budgetList;
	}

	private String pageInd;
	private List budgetAllocationList = null;
	//temp
	private List budgetAllocationDetList = null;
	//temp
	private List remarkList = null;
	
	private boolean popup = false;

	private String viewMode;
	
	//temp
	private boolean brandNew;
	
	
	public String getViewMode() {
		return viewMode;
	}

	public void setViewMode(String viewMode) {
		this.viewMode = viewMode;
	}

	public boolean isBrandNew() {
		return brandNew;
	}

	public void setBrandNew(boolean brandNew) {
		this.brandNew = brandNew;
	}

	public boolean isPopup() {
		return popup;
	}

	public void setPopup(boolean popup) {
		this.popup = popup;
	}

	public List getRemarkList() {
		return remarkList;
	}

	public void setRemarkList(List remarkList) {
		this.remarkList = remarkList;
	}

	public List getBudgetAllocationList() {
		return budgetAllocationList;
	}

	public List getBudgetAllocationDetList() {
		return budgetAllocationDetList;
	}

	public void setBudgetAllocationDetList(List budgetAllocationDetList) {
		this.budgetAllocationDetList = budgetAllocationDetList;
	}

	public void setBudgetAllocationList(List budgetAllocationList) {
		this.budgetAllocationList = budgetAllocationList;
	}
	public String getPageInd() {
		if(this.pageInd == null){
			pageInd = "V";
		}
		return pageInd;
	}

	public void setPageInd(String pageInd) {
		this.pageInd = pageInd;
	}
	
	public void cleanAll(){
		this.setBudgetAllocationDetList(null);
		this.setBudgetAllocationList(null);
		this.setPopup(false);
		this.setPageInd(null);
		this.setRemarkList(null);
		this.setViewMode(null);
		this.setBrandNew(false);
		
	}

}
