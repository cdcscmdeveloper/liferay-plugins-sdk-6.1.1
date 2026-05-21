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

package my.ep.web.managedbean.report.qt;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.web.bean.report.ScReportFieldsSearchBean;

import org.apache.log4j.Logger;

/**
 * The Class FinancialTableReportManagedBean.
 * 
 * @author:Garikapati Mangaiah.
 */

@ManagedBean(name = "qtFinancialTableReportMB")
@SessionScoped
public class FinancialTableReportManagedBean extends QtReportManagedBean{

	
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(FinancialTableReportManagedBean.class);
	
	
	private String procurementTitle;//#29580
	
	//#29580 - Start 
	/**
	 * @return the procurementTitle
	 */
	public String getProcurementTitle() {
		return procurementTitle;
	}

	/**
	 * @param procurementTitle the procurementTitle to set
	 */
	public void setProcurementTitle(String procurementTitle) {
		this.procurementTitle = procurementTitle;
	}
	//#29580 - End 

}
