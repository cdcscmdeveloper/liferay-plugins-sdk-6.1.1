/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl.mc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.web.managedbean.report.fl.FlReportManagedBean;
/**
 * Managed Bean for FlReport.
 *
 * @author Jimmy
 * @since 26/4/2017
 */
@ManagedBean(name = "ddRptManagedBean")
@SessionScoped
public class DeductionRptManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	public DeductionRptManagedBean(){}
	
	private String invNo;
	private String supplierInvNo;
	private String paymentMatchNo;
	private PmOrgProfile selectedOrgProfile;
}
