/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.report.ct.cc;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.backingbean.report.ct.CtReportBaseBackingBean;
import my.ep.web.managedbean.report.ct.cc.ListOfCircularRptManagedBean;

import org.apache.log4j.Logger;

/**
 * The Class ListOfCircularRptBackingBean.
 * 
 * @author Nancy Ang
 */
@ManagedBean(name = "listOfCircularRptBB")
@ViewScoped
public class ListOfCircularRptBackingBean extends CtReportBaseBackingBean<ListOfCircularRptManagedBean> {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ListOfCircularRptBackingBean.class);
	
}
