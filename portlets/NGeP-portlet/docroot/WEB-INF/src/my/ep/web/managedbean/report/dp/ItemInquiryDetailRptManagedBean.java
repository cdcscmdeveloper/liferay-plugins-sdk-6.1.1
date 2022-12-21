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
 * The Class ItemInquiryDetailRptManagedBean.
 *
 * @author AnJie
 * @since 03.05.2017
 */
@ManagedBean(name="itemInquiryDetailRptManagedBean")
@SessionScoped
public class ItemInquiryDetailRptManagedBean extends DpReportManagedBean{
	private static final long serialVersionUID = 1L;
	/** The sc pi dtls prc rpt group. */
	private String scPIDtlsPrcRptGroup;
	
	/**
	 * @return the scPIDtlsPrcRptGroup
	 */
	public String getScPIDtlsPrcRptGroup() {
		return scPIDtlsPrcRptGroup;
	}
	/**
	 * @param scPIDtlsPrcRptGroup the scPIDtlsPrcRptGroup to set
	 */
	public void setScPIDtlsPrcRptGroup(String scPIDtlsPrcRptGroup) {
		this.scPIDtlsPrcRptGroup = scPIDtlsPrcRptGroup;
	}

}
