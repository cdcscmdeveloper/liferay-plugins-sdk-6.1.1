package my.ep.web.backingbean.pp;

import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pp.PlanManagedBean;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;

@ManagedBean(name="subMenuBackingBean")
@RequestScoped

public class SubMenuBackingBean extends BaseBackingBean<BaseManagedBean>{
	private static Logger log = Logger.getLogger(SubMenuBackingBean.class);
	/* Navigation */
	
	private static final String NAV_TO_LIST_BUDGET_ALLOCATION = "listBudget";
	private static final String NAV_TO_REVIEW_BUDGET_ALLOCATION = "reviewBudgetAllocation";
	private static final String NAV_TO_LIST_PLAN = "listPlan";
	private static final String NAV_TO_REVIEW_PLAN = "reviewPlan";
	private static final String NAV_TO_VERIFY_PLAN = "verifyPlan";
	
	
	public String getNavToListBudgetAllocation(){
		return NAV_TO_LIST_BUDGET_ALLOCATION;
	}

	public String getNavToListPlan(){
		return NAV_TO_LIST_PLAN;
	}
	
	public String getNavToReviewPlan(){
		return NAV_TO_REVIEW_PLAN;
	}
	
	
	public String redirectToListBudgetAllocation(){
		System.out.println("----------NAV_TO_VIEW_BUDGET_ALLOCATION");
		BudgetAllocationBackingBean budgetAllocationBackingBean = new BudgetAllocationBackingBean();
		budgetAllocationBackingBean.getManagedBean().cleanAll();
		budgetAllocationBackingBean.getManagedBean().setViewMode("L");
		return NAV_TO_LIST_BUDGET_ALLOCATION; 
    }
	
	public String redirectToListPlan(){
		System.out.println("----------NAV_TO_VIEW_PLAN");
		PlanBackingBean planBackingBean = new PlanBackingBean();
		planBackingBean.getManagedBean().cleanAll();
		planBackingBean.getManagedBean().setViewMode("L");
		return NAV_TO_LIST_PLAN;
    }
	
	public String redirectToReviewPlan(){
		System.out.println("----------NAV_TO_VIEW_PENDING_APPROVAL");
		PlanBackingBean planBackingBean = new PlanBackingBean();
		planBackingBean.getManagedBean().cleanAll();
		planBackingBean.getManagedBean().setViewMode("A");
		planBackingBean.getManagedBean().setBrandNew(false);
		
		return NAV_TO_REVIEW_PLAN;
    }
	
	public String redirectToVerifyPlan(){
		System.out.println("----------NAV_TO_VIEW_PENDING_VERIFICATION");
		PlanBackingBean planBackingBean = new PlanBackingBean();
		planBackingBean.getManagedBean().cleanAll();
		planBackingBean.getManagedBean().setViewMode("V");
		planBackingBean.getManagedBean().setBrandNew(false);
		
		return 	NAV_TO_VERIFY_PLAN;
    }
	
	public String redirectToReviewBudgetAllocation(){
		System.out.println("----------NAV_TO_REVIEW_BUDGET_ALLOCATION");
		BudgetAllocationBackingBean budgetAllocationBackingBean = new BudgetAllocationBackingBean();
		
		budgetAllocationBackingBean.getManagedBean().setBrandNew(false);
		budgetAllocationBackingBean.getManagedBean().setViewMode("A");
		budgetAllocationBackingBean.getManagedBean().setBudgetAllocationDetList(null);
		budgetAllocationBackingBean.getManagedBean().setRemarkList(null);
		return NAV_TO_REVIEW_BUDGET_ALLOCATION;
    } 

}
