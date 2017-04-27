package my.ep.web.backingbean.report.ct;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.constant.ModuleCodeConst;
import my.ep.web.managedbean.report.ct.CtReportMainManagedBean;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class CtReportMainBackingBean.
 * 
 * @author Nancy Ang
 */
@ManagedBean(name = "ctReportBB")
@ViewScoped
public class CtReportMainBackingBean extends CtReportBackingBean<CtReportMainManagedBean> {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CtReportMainBackingBean.class);
	private static final String NAV_TO_CT_REPORT_MAIN = "/xhtml/reports/ct/ctReportMain.xhtml";

	/**
	 * The method used to retrieve the information and render it on screen.
	 */
	public void preRenderView() {
		log.info("preRenderView");
		this.renewSesionObject();
		
		retrieveReportCategoryListByModuleCode(
				ModuleCodeConst.CONTRACT_MANAGEMENT.toUpperCase());
		
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler()
				.handleNavigation(fc, null, NAV_TO_CT_REPORT_MAIN);
	}
	
	/**
	 * The method used to retrieve the report list base on report category.
	 * 
	 * @param reportCategory  the selected reportCategory
	 */
	public void retrieveReportListByCategory(String reportCategory) {
		retrieveReportListByModuleCodeAndSubModule(
				ModuleCodeConst.CONTRACT_MANAGEMENT.toUpperCase(),
				reportCategory);
	}
	
	/**
	 * The method used to retrieve the report value from database.
	 */
	public void onChangeReportName() {	
		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
	}

}
