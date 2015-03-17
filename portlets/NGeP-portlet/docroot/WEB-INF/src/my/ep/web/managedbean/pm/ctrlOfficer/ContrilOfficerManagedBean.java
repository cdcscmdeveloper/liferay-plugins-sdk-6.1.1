package my.ep.web.managedbean.pm.ctrlOfficer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.pm.ControllerOfficerSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "ctrlManagedBean")
@SessionScoped
public class ContrilOfficerManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ControllerOfficerSearchBean controllerSearchBean;
	/**
	 * @return the controllerSearchBean
	 */
	public ControllerOfficerSearchBean getControllerSearchBean() {
		return controllerSearchBean;
	}
	/**
	 * @param controllerSearchBean the controllerSearchBean to set
	 */
	public void setControllerSearchBean(ControllerOfficerSearchBean controllerSearchBean) {
		this.controllerSearchBean = controllerSearchBean;
	}
	
	

}
