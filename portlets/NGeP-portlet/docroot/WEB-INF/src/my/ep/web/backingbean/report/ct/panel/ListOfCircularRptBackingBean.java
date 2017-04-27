/**
 * Copyright © 2013 Archisoft. All rights reserved.
 */

package my.ep.web.backingbean.report.ct.panel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import my.ep.web.backingbean.report.ct.CtReportBackingBean;
import my.ep.web.managedbean.report.ct.panel.ListOfCircularRptManagedBean;

import org.apache.log4j.Logger;

/**
 * The Class ListOfCircularRptBackingBean.
 * 
 * @author Nancy Ang
 */
@ManagedBean(name = "listOfCircularRptBB")
@ViewScoped
public class ListOfCircularRptBackingBean extends CtReportBackingBean<ListOfCircularRptManagedBean> {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(ListOfCircularRptBackingBean.class);
	
}
