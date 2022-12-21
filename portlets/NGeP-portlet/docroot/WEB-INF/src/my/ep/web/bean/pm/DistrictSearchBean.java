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
package my.ep.web.bean.pm;

import java.io.Serializable;

import my.ep.persistence.entity.pm.common.PmState;


/**
* The Class DistrictSearchBean.
* 
*    @author Nagasiva
*    @since 14.06.2016
*/

public class DistrictSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The state code. */
	private String stateCode;
	
	private PmState pmState;
	/** The division name. */
	private String divisionName;	
	
	/** The district name. */
	private String districtName;
	
	public DistrictSearchBean(){}
	
	/**
	 * Gets the state code.
	 * 
	 * @return the stateCode
	 */
	public String getStateCode() {
		return stateCode;
	}

	/**
	 * Sets the state code.
	 * 
	 * @param stateCode
	 *            the stateCode to set
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	/**
	 * Gets the division name.
	 * 
	 * @return the divisionName
	 */
	public String getDivisionName() {
		return divisionName;
	}

	/**
	 * Sets the division name.
	 * 
	 * @param divisionName
	 *            the divisionName to set
	 */
	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	/**
	 * Gets the district name.
	 * 
	 * @return the districtName
	 */
	public String getDistrictName() {
		return districtName;
	}

	/**
	 * Sets the district name.
	 * 
	 * @param districtName
	 *            the districtName to set
	 */
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	/**
	 * @return the pmState
	 */
	public PmState getPmState() {
		return pmState;
	}

	/**
	 * @param pmState the pmState to set
	 */
	public void setPmState(PmState pmState) {
		this.pmState = pmState;
	}


}
