package my.ep.web.managedbean.sc.procure;

import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * 
 * @author David
 */
@ManagedBean(name = "procurementPlanManageBean")
@SessionScoped
public class ProcurementPlanManageBean extends BaseManagedBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//Search fields
	
	private String planNumber;	
	private String procurementMode;	
	private String planStatus;
	private PpPlan currentPlan;
	private Date notificationDate;
	private int selectedIndex=-1;

	private List<PpPlan> procurementPlanList;
	

	public ProcurementPlanManageBean()  {
		
			}

	public List<PpPlan> getProcurementPlanList() {
		return procurementPlanList;
	}

	public void setProcurementPlanList(List<PpPlan> procurementPlanList) {
		this.procurementPlanList = procurementPlanList;
	}

	
	public String getPlanNumber() {
		return planNumber;
	}

	public void setPlanNumber(String planNumber) {
		this.planNumber = planNumber;
	}

	public String getProcurementMode() {
		return procurementMode;
	}

	public void setProcurementMode(String procurementMode) {
		this.procurementMode = procurementMode;
	}

	public String getPlanStatus() {
		return planStatus;
	}

	public void setPlanStatus(String planStatus) {
		this.planStatus = planStatus;
	}

	public PpPlan getCurrentPlan() {
		return currentPlan;
	}

	public void setCurrentPlan(PpPlan currentPlan) {
		this.currentPlan = currentPlan;
	}

	public Date getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(Date notificationDate) {
		this.notificationDate = notificationDate;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
}
