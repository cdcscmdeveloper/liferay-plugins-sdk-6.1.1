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
package my.ep.web.managedbean.pm.division;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.bean.pm.DivisionSearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * Managed Bean for Division
 * 
 * @author Nagasiva
 * @since 13.06.2016
 */
@ManagedBean(name = "divisionManagedBean")
@SessionScoped
public class DivisionManagedBean extends BaseManagedBean{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	
	/** The division search bean. */
	private DivisionSearchBean divisionSearchBean;
	
	/** The is lazy. */
	private boolean isLazy;
	
	private String stateCode;
	
	private String divisionName;
	
	/** The list of states. */
	private List<PmState> listOfStates;
	
	public DivisionManagedBean(){}

	/**
	 * Gets the division search bean.
	 * 
	 * @return the divisionSearchBean
	 */
	public DivisionSearchBean getDivisionSearchBean() {
		return divisionSearchBean;
	}

	/**
	 * Sets the division search bean.
	 * 
	 * @param divisionSearchBean
	 *            the divisionSearchBean to set
	 */
	public void setDivisionSearchBean(DivisionSearchBean divisionSearchBean) {
		this.divisionSearchBean = divisionSearchBean;
	}
	
	/**
	 * Checks if is lazy.
	 * 
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * Sets the lazy.
	 * 
	 * @param isLazy
	 *            the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}
	
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

	
	/**
	 * @return the stateCode
	 */
	public String getStateCode() {
		return stateCode;
	}

	/**
	 * @param stateCode the stateCode to set
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	/**
	 * @return the divisionName
	 */
	public String getDivisionName() {
		return divisionName;
	}

	/**
	 * @param divisionName the divisionName to set
	 */
	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}
	
	
}
