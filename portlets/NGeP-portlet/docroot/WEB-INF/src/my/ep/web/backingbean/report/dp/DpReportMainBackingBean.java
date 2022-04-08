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
package my.ep.web.backingbean.report.dp;

import java.io.Serializable;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.constant.ModuleCodeConst;
import my.ep.web.managedbean.report.dp.DpReportMainManagedBean;

import org.apache.commons.lang.StringUtils;

/**
 * The Class DpReportMainBackingBean.
 *
 * @author AnJie
 * @since 25.04.2017
 */
@ManagedBean(name = "dpReportBackingBean")
@ViewScoped
public class DpReportMainBackingBean extends
		DpReportBackingBean<DpReportMainManagedBean> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** The Direct Purchase report main page. */
	private static final String REPORT_MAIN = "/xhtml/reports/new/sc/dp/dpReportMain.xhtml";

	/**
	 * Pre render page navigation to direct purchase main page
	 * @return void
	 */
	public void preRenderView() {
		retrieveReportListByAuthorization(StringUtils
				.upperCase(ModuleCodeConst.DIRECT_PURCHASE));

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, REPORT_MAIN);
	}
	
	@Override
	public void generateDpReport( LinkedHashMap<String, String> params) {
		
	}

	@Override
	public void resetSearch() {
		
	}

	@Override
	public LinkedHashMap<String, String> getparams() {
		return null;
	}

	@Override
	public void validateCriteria() {
		
	}

}
