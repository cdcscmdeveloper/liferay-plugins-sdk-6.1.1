/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.report.ct.cc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.backingbean.report.ct.CtReportBaseBackingBean;
import my.ep.web.managedbean.report.ct.cc.ListOfContractRptManagedBean;

import org.apache.log4j.Logger;

/**
 * The Class ListOfContractRptBackingBean.
 * 
 * @author Nancy Ang
 */
@ManagedBean(name = "listOfContractRptBB")
@ViewScoped
public class ListOfContractRptBackingBean extends CtReportBaseBackingBean<ListOfContractRptManagedBean> {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ListOfContractRptBackingBean.class);
	
}
