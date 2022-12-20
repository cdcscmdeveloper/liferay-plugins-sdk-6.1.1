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
package my.ep.web.managedbean.pm.district;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmState;
import my.ep.web.bean.pm.DistrictSearchBean;
import my.ep.web.managedbean.BaseManagedBean;


/**
 * Managed Bean for District
 * 
 * @author Nagasiva
 * @since 14.06.2016
 * 
 */
@ManagedBean(name = "districtManagedBean")
@SessionScoped
public class DistrictManagedBean extends BaseManagedBean {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The division search bean. */
	private DistrictSearchBean districtSearchBean;	
	
	/** The adopt division. */
	private boolean adoptDivision=false;

	/** The selected pm state. */
	private PmState selectedPmState;
	
	/** The list of states. */
	private List<SelectItem> listOfStates;
	
	public DistrictManagedBean(){}
	
	/**
	 * Gets the list of states.
	 * 
	 * @return the listOfStates
	 */
	public List<SelectItem> getListOfStates() {
		if(listOfStates==null){
			listOfStates=new ArrayList<SelectItem>();
		}
		return listOfStates;
	}

	/**
	 * Sets the list of states.
	 * 
	 * @param listOfStates
	 *            the listOfStates to set
	 */
	public void setListOfStates(List<SelectItem> listOfStates) {
		this.listOfStates = listOfStates;
	}

	/**
	 * Checks if is adopt division.
	 * 
	 * @return the adoptDivision
	 */
	public boolean isAdoptDivision() {
		return adoptDivision;
	}

	/**
	 * Gets the selected pm state.
	 * 
	 * @return the selectedPmState
	 */
	public PmState getSelectedPmState() {
		return selectedPmState;
	}

	/**
	 * Sets the selected pm state.
	 * 
	 * @param selectedPmState
	 *            the selectedPmState to set
	 */
	public void setSelectedPmState(PmState selectedPmState) {
		this.selectedPmState = selectedPmState;
	}

	/**
	 * Sets the adopt division.
	 * 
	 * @param adoptDivision
	 *            the adoptDivision to set
	 */
	public void setAdoptDivision(boolean adoptDivision) {
		this.adoptDivision = adoptDivision;
	}

	/**
	 * Gets the district search bean.
	 * 
	 * @return the districtSearchBean
	 */
	public DistrictSearchBean getDistrictSearchBean() {
		return districtSearchBean;
	}

	/**
	 * Sets the district search bean.
	 * 
	 * @param districtSearchBean
	 *            the districtSearchBean to set
	 */
	public void setDistrictSearchBean(DistrictSearchBean districtSearchBean) {
		this.districtSearchBean = districtSearchBean;
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

	/** The is lazy. */
	private boolean isLazy;
	
	

}
