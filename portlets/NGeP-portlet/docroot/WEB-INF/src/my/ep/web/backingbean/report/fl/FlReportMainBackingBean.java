/*
 *  Copyright © 2013 Archisoft. All rights reserved.
 */
package my.ep.web.backingbean.report.fl;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import my.ep.constant.ModuleCodeConst;
import my.ep.factory.EpLogger;
import my.ep.web.managedbean.report.fl.FlReportMainManagedBean;
/**
 * Backing Bean for FlReport.
 *
 * @author KK LIM
 * @since 26/4/2017
 */
@ManagedBean(name = "flReportBB")
@SessionScoped
public class FlReportMainBackingBean extends FlReportBackingBean<FlReportMainManagedBean> {

	private static final long serialVersionUID = 1L;
	private static final String NAV_TO_FLREPORT = "/xhtml/reports/fl/flReportMain.xhtml";

	/** The Constant log. */
	private static final Logger log = EpLogger
			.getLogger(FlReportMainBackingBean.class);

	Long userId = getFacesScopes().getUserSessionBean().getUserId();

	public void preRenderView() {
		log.info("preRenderView");
		this.renewSesionObject();
		retrieveReportCategoryListByModuleCode(ModuleCodeConst.ORDER_FULFILLMENT.toUpperCase());

		String userRole = retrieveRoleByUserId(userId);

		if (userRole != null) {
			getManagedBean().setUserRole(userRole);
		}

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_FLREPORT);
	}


}