/*
 * Copyright © 2015 Archisoft Consulting Sdn Bhd. All rights reserved.
 */
package my.ep.web.managedbean.pm.ctrlOfficer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.pm.ControllerOfficerSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Control Officer	
 * 
 * @author venugopal
 * @since 17/3/2015
 *
 */
@ManagedBean(name = "ctrlManagedBean")
@SessionScoped
public class ContrilOfficerManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ControllerOfficerSearchBean controllerSearchBean;
	
	public ContrilOfficerManagedBean(){}
	
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
