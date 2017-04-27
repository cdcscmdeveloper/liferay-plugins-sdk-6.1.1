/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.report.ct.mc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.backingbean.report.ct.CtReportBackingBean;
import my.ep.web.managedbean.report.ct.mc.ListOfContractRptManagedBean;

import org.apache.log4j.Logger;

/**
 * The Class ListOfContractRptBackingBean.
 * 
 * @author Nancy Ang
 */
@ManagedBean(name = "listOfContractRptBB")
@ViewScoped
public class ListOfContractRptBackingBean extends CtReportBackingBean<ListOfContractRptManagedBean> {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ListOfContractRptBackingBean.class);
	
}
