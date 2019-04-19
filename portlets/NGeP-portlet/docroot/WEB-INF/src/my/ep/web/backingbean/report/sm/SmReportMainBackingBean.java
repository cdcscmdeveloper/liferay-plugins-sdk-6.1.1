package my.ep.web.backingbean.report.sm;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.constant.ModuleCodeConst;
import my.ep.web.managedbean.report.sm.SmReportMainManagedBean;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "smReportBB")
@ViewScoped
public class SmReportMainBackingBean extends
		SmReportBackingBean<SmReportMainManagedBean> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8556539806948027354L;
	private static final String REPORT_MAIN = "/xhtml/reports/new/sm/smReportMain.xhtml";

	public void preRender() {
		renewSesionObject();
		retrieveReportListByAuthorization(StringUtils
				.upperCase(ModuleCodeConst.SUPPLIER_MANAGEMENT));

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, REPORT_MAIN);
	}

}
