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


/**
 * The Class DivisionSearchBean.
 * 
 *    @author Nagasiva
 *    @since 13.06.2016
 */
public class DivisionSearchBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The state code. */
	private String stateCode;
	
	/** The division name. */
	private String divisionName;
	
	public DivisionSearchBean(){}
	
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
