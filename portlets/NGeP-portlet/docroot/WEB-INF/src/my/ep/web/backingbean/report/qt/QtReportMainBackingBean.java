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
package my.ep.web.backingbean.report.qt;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.web.managedbean.report.qt.QtReportMainManagedBean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
/**
 * The Class QtReportMainBackingBean.
 * 
 * @author:Garikapati Mangaiah.
 */

@ManagedBean(name = "qtReportMainBB")
@ViewScoped
public class QtReportMainBackingBean  extends
QtReportBackingBean<QtReportMainManagedBean> implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger
			.getLogger(QtReportMainBackingBean.class);
	private static final String REPORT_MAIN = "/xhtml/reports/new/sc/qt/qtReportMain.xhtml";

	public void preRender() {
		renewSesionObject();
		retrieveReportListByAuthorization(StringUtils
				.upperCase(ModuleCodeConst.SUPPLIER_SELECTION));

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, REPORT_MAIN);
	}


}
