package my.ep.web.backingbean.pp;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.pp.BudgetAllocationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;

import org.apache.log4j.Logger;

@ManagedBean(name="budgetAllocationBackingBean")
@RequestScoped
public class BudgetAllocationBackingBean extends BaseBackingBean<BudgetAllocationManagedBean>{
	private static Logger log = Logger.getLogger(BudgetAllocationBackingBean.class);
	public static final String NAV_TO_CREATE_BUDGET_ALLOCATION = "createBudgetAllocation";
	public static final String NEW = "N";
	public static final String REVISION = "S";
	public static final String LIST = "L";
	public static final String VIEW = "R";
	public static final String APPROVAL = "A";

	public String redirectToCreateBudgetAllocation(){
		System.out.println("----------NAV_TO_BUDGET_ALLOCATION");
		this.getManagedBean().setBrandNew(true);
		this.getManagedBean().setViewMode("N");
		this.getManagedBean().setBudgetAllocationDetList(null);
		this.getManagedBean().setRemarkList(null);
		return NAV_TO_CREATE_BUDGET_ALLOCATION; //+ "?faces-redirect=true"; 
    } 
	
	//select retrieve all budget for this ministry
	public List getBudgetAllocationList() {
		//some logic...
		return this.getManagedBean().getBudgetAllocationList();
	}
	
	
	
	
//	public void addBudgetAllocationDet() {
//		System.out.println("------------addNewBudgetAllocationDet");
//		if(this.getManagedBean().getBudgetAllocationDetList()==null){
//			this.getManagedBean().setBudgetAllocationDetList(new ArrayList());
//		}
//		this.getManagedBean().getBudgetAllocationDetList().add(new Object());
//	}
//	
	public void addRemark() {
		System.out.println("------------addRemark");
		if(this.getManagedBean().getRemarkList()==null){
			this.getManagedBean().setRemarkList(new ArrayList());
		}
		this.getManagedBean().getRemarkList().add(new Object());
	}
	

	
	public String redirectToListBudgetAllocation(){
		System.out.println("----------NAV_TO_VIEW_BUDGET_ALLOCATION");
		SubMenuBackingBean subMenuBackingBean = new SubMenuBackingBean();
		return subMenuBackingBean.getNavToListBudgetAllocation();
    }
	
	public String redirectToViewPlan(){
		System.out.println("----------NAV_TO_VIEW_PLAN");
		SubMenuBackingBean subMenuBackingBean = new SubMenuBackingBean();
		return subMenuBackingBean.getNavToListPlan();
    }
	
	public String getNavToCreateBugetAllocation(){
		return NAV_TO_CREATE_BUDGET_ALLOCATION;
	}
	
	public String generateHeader(String viewMode){
		String pageTitle = "";

		if(NEW.equals(viewMode)){
			pageTitle = "lbl.pp.budget.title.new";
		}else if(LIST.equals(viewMode)){
			pageTitle = "lbl.pp.budget.title.list";
		}else if(APPROVAL.equals(viewMode)){
			pageTitle = "lbl.pp.budget.title.approval";
		}else if(REVISION.equals(viewMode)){
			pageTitle = "lbl.pp.plan.budget.revision";

		}
		
		return ResourceBundleUtils.getMessage(pageTitle, 
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, 
				new FacesScopes().getUserLocale());
				
	}

	
}
