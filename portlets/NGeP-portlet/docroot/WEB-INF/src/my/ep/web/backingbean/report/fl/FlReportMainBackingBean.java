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
import my.ep.type.pm.RoleCode;
import my.ep.web.managedbean.report.fl.FlReportMainManagedBean;
import my.ep.web.util.FacesScopes;
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
	
	private static final String serviceFeeCode = "SF"; //#29554

	public void preRenderView() {
		log.info("preRenderView");
		this.renewSesionObject();
		retrieveReportCategoryListByModuleCode(ModuleCodeConst.ORDER_FULFILLMENT.toUpperCase());

		/*
		String userRole = retrieveRoleByUserId(userId);

		if (userRole != null) {
			getManagedBean().setUserRole(userRole);
		}*/
		//#29554 start
		getManagedBean().setShowRptCategory(true);
		if(isGovCollector()){
			if(!(isAuditor() || isEpAdmin())){
				retrieveReportListByModuleCodeAndSubModule(ModuleCodeConst.ORDER_FULFILLMENT.toUpperCase(),serviceFeeCode);
				getManagedBean().setShowRptCategory(false);
			}
		}
		//#29554 end

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, NAV_TO_FLREPORT);
	}
	
	// #29554 start
	/**
	 * Checks if user has role The Collector (Government).
	 * 
	 * @return true, if is The Collector (Government)
	 */
	public boolean isGovCollector() {
		return getFacesScopes().hasRoleUser(RoleCode.COLLECTOR_GOV.value());
	}

	/**
	 * Checks if user has role Internal/External/BPK auditor.
	 * 
	 * @return true, if is Internal/External/BPK auditor
	 */
	public boolean isAuditor() {
		return getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_AUDITOR.value())
				|| getFacesScopes().hasRoleUser(RoleCode.AUDITOR_GENERAL.value());
	}

	/**
	 * Checks if user has role Ep Admin.
	 * 
	 * @return true, if is Ep Admin
	 */
	public boolean isEpAdmin() {
		FacesScopes facesScopes = new FacesScopes();
		return facesScopes.hasRoleUser(RoleCode.EP_ADMIN.value());
	}
	// #29554 end


}