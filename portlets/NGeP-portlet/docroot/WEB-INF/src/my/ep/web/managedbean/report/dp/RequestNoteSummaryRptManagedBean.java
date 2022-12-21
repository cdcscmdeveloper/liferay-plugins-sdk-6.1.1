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
 * The Class RequestNoteSummaryRptManagedBean.
 *
 * @author AnJie
 * @since 03.05.2017
 */
@ManagedBean(name="rnSummaryRptManagedBean")
@SessionScoped
public class RequestNoteSummaryRptManagedBean extends DpReportManagedBean{
	private static final long serialVersionUID = 1L;
	
	/** The procurement type. */
	private String procurementType;

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
