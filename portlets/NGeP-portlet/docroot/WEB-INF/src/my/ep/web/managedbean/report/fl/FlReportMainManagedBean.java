/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.managedbean.report.fl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
/**
 * Managed Bean for FlReport.
 *
 * @author KK LIM
 * @since 26/4/2017
 */
@ManagedBean(name = "flReportMB")
@SessionScoped
public class FlReportMainManagedBean extends FlReportManagedBean{

	private static final long serialVersionUID = 1L;
	
	private boolean showRptCategory;

	/**
	 * @return the showRptCategory
	 */
	public boolean isShowRptCategory() {
		return showRptCategory;
	}

	/**
	 * @param showRptCategory the showRptCategory to set
	 */
	public void setShowRptCategory(boolean showRptCategory) {
		this.showRptCategory = showRptCategory;
	}
}
