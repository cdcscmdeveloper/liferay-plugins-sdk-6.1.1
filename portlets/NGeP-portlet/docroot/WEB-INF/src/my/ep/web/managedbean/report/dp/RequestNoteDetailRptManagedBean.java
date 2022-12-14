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
 * The Class RequestNoteDetailRptManagedBean.
 *
 * @author AnJie
 * @since 03.05.2017
 */
@ManagedBean(name="rnDetailRptManagedBean")
@SessionScoped
public class RequestNoteDetailRptManagedBean extends DpReportManagedBean{
	private static final long serialVersionUID = 1L;
	
	/** The request note group. */
	private String requestNoteGroup;
	
	/** The procurement type. */
	private String procurementType;

	/**
	 * @return the requestNoteGroup
	 */
	public String getRequestNoteGroup() {
		return requestNoteGroup;
	}

	/**
	 * @param requestNoteGroup the requestNoteGroup to set
	 */
	public void setRequestNoteGroup(String requestNoteGroup) {
		this.requestNoteGroup = requestNoteGroup;
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
