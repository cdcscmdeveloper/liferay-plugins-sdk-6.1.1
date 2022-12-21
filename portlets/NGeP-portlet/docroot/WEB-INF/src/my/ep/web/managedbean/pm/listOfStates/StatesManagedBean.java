/* 
 ***************************************************   
 * Product nextgen
 *                ePerolehan 
 * Release 1.00    
 *    
 * Copyright © 2013 Archisoft. All rights reserved.
 * 
 ****************************************************
 */
package my.ep.web.managedbean.pm.listOfStates;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.managedbean.BaseManagedBean;


/**
 * EJB ManagedBean For List Of States.
 * 
 * @author Nagasiva
 * @since 10.06.2016
 * 
 */
@ManagedBean(name = "statesgMB")
@SessionScoped
public class StatesManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new states managed bean.
	 */
	public StatesManagedBean() {

	}

	/** The list of states. */
	List<PmState> listOfStates;

	/**
	 * Gets the list of states.
	 * 
	 * @return the listOfStates
	 */
	public List<PmState> getListOfStates() {
		return listOfStates;
	}

	/**
	 * Sets the list of states.
	 * 
	 * @param listOfStates
	 *            the listOfStates to set
	 */
	public void setListOfStates(List<PmState> listOfStates) {
		this.listOfStates = listOfStates;
	}

}
