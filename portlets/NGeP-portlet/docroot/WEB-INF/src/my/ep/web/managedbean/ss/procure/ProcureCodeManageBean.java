package my.ep.web.managedbean.ss.procure;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pp.procurementplan.PpPlan;
import my.ep.web.bean.pp.ProcurementPlanSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * 
 * @author David
 */
@ManagedBean(name = "procureMB")
@SessionScoped
public class ProcureCodeManageBean extends BaseManagedBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ProcurementPlanSearchBean planSearchBean;

	private List<PpPlan> procurementPlanList;
	private	PpPlan		currentPlan = new PpPlan();

	public ProcureCodeManageBean() throws Exception {
		super();
		planSearchBean= new ProcurementPlanSearchBean();
			}

	public List<PpPlan> getProcurementPlanList() {
		return procurementPlanList;
	}

	public void setProcurementPlanList(List<PpPlan> procurementPlanList) {
		this.procurementPlanList = procurementPlanList;
	}

	public ProcurementPlanSearchBean getPlanSearchBean() {
		return planSearchBean;
	}

	public void setPlanSearchBean(ProcurementPlanSearchBean planSearchBean) {
		this.planSearchBean = planSearchBean;
	}

	public PpPlan getCurrentPlan() {
		return currentPlan;
	}

	public void setCurrentPlan(PpPlan currentPlan) {
		this.currentPlan = currentPlan;
	}
	
}
