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

import org.apache.log4j.Logger;

/**
 * The Class ProposalProductReportManagedBean.
 * 
 * @author:Garikapati Mangaiah.
 */

@ManagedBean(name = "qtProposalProductReportMB")
@SessionScoped
public class ProposalProductReportManagedBean  extends QtReportManagedBean{

	
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(ProposalProductReportManagedBean.class);

}
