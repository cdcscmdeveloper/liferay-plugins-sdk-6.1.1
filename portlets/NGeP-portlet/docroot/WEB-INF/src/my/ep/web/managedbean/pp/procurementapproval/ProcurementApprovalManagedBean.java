package my.ep.web.managedbean.pp.procurementapproval;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.web.managedbean.BaseManagedBean;


@ManagedBean(name = "procurmentApprovalManagedBean")
@SessionScoped
public class ProcurementApprovalManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;
	
	private PmOrgProfile currentUser;
	
	private List<PpPlan> pendingApprovalPlans;
	
	/**
	 * @return the pendingApprovalPlans
	 */
	public List<PpPlan> getPendingApprovalPlans() {
		return pendingApprovalPlans;
	}

	/**
	 * @param pendingApprovalPlans the pendingApprovalPlans to set
	 */
	public void setPendingApprovalPlans(List<PpPlan> pendingApprovalPlans) {
		this.pendingApprovalPlans = pendingApprovalPlans;
	}

	/**
	 * @return the currentUser
	 */
	public PmOrgProfile getCurrentUser() {
		return currentUser;
	}

	/**
	 * @param currentUser the currentUser to set
	 */
	public void setCurrentUser(PmOrgProfile currentUser) {
		this.currentUser = currentUser;
	}
	
	


}
