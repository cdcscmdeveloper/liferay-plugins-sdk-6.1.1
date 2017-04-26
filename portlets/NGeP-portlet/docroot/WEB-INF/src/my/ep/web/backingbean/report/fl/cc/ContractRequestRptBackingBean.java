/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.report.fl.cc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.log4j.Logger;

import my.ep.factory.EpLogger;
import my.ep.web.backingbean.report.fl.FlReportBackingBean;
import my.ep.web.managedbean.report.fl.cc.ContractRequestRptManagedBean;
/**
 * Backing Bean for ContractRequestRpt.
 *
 * @author KK LIM
 * @since 26/4/2017
 */
@ManagedBean(name = "crRptBackingBean")
@SessionScoped
public class ContractRequestRptBackingBean extends FlReportBackingBean<ContractRequestRptManagedBean> {

	private static final long serialVersionUID = 1L;

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(ContractRequestRptBackingBean.class);

	
	
	
}