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
package my.ep.web.managedbean.pm.city;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import my.ep.web.bean.pm.CitySearchBean;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * The Class cityManagedBean.
 * 
 * @author Nagasiva
 * @since 04.07.2016
 */
@ManagedBean(name = "cityManagedBean")
@SessionScoped
public class CityManagedBean extends BaseManagedBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CitySearchBean citySearchBean;
	
	private boolean adoptDivision=false;
	
	private boolean isLazy;
	
	private List<SelectItem> listOfStates;
	
	public CityManagedBean(){}

	/**
	 * @return the citySearchBean
	 */
	public CitySearchBean getCitySearchBean() {
		return citySearchBean;
	}

	/**
	 * @return the listOfStates
	 */
	public List<SelectItem> getListOfStates() {
		if(listOfStates==null){
			listOfStates=new ArrayList<SelectItem>();
		}
		return listOfStates;
	}

	/**
	 * @param listOfStates the listOfStates to set
	 */
	public void setListOfStates(List<SelectItem> listOfStates) {
		this.listOfStates = listOfStates;
	}

	/**
	 * @return the adoptDivision
	 */
	public boolean isAdoptDivision() {
		return adoptDivision;
	}

	/**
	 * @param adoptDivision the adoptDivision to set
	 */
	public void setAdoptDivision(boolean adoptDivision) {
		this.adoptDivision = adoptDivision;
	}

	/**
	 * @return the isLazy
	 */
	public boolean isLazy() {
		return isLazy;
	}

	/**
	 * @param isLazy the isLazy to set
	 */
	public void setLazy(boolean isLazy) {
		this.isLazy = isLazy;
	}

	/**
	 * @param citySearchBean the citySearchBean to set
	 */
	public void setCitySearchBean(CitySearchBean citySearchBean) {
		this.citySearchBean = citySearchBean;
	}	

}
