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
package my.ep.web.managedbean.report.dp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * The Class SimpleQuoteDetailRptManagedBean.
 *
 * @author AnJie
 * @since 03.05.2017
 */
@ManagedBean(name="sqDetailRptManagedBean")
@SessionScoped
public class SimpleQuoteDetailRptManagedBean extends DpReportManagedBean{
	private static final long serialVersionUID = 1L;
	
	/** The simpple quote group. */
	private String simpleQuoteGroup;
	
	/** The procurement type. */
	private String procurementType;

	
	/**
	 * @return the simpleQuoteGroup
	 */
	public String getSimpleQuoteGroup() {
		return simpleQuoteGroup;
	}

	/**
	 * @param simpleQuoteGroup the simpleQuoteGroup to set
	 */
	public void setSimpleQuoteGroup(String simpleQuoteGroup) {
		this.simpleQuoteGroup = simpleQuoteGroup;
	}

	/**
	 * @return the procurementType
	 */
	public String getProcurementType() {
		return procurementType;
	}

	/**
	 * @param procurementType the procurementType to set
	 */
	public void setProcurementType(String procurementType) {
		this.procurementType = procurementType;
	}
}
