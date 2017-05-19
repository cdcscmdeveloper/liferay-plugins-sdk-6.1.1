package my.ep.web.backingbean.report.ct;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constants.CommonConstants;
import my.ep.exception.EpException;
import my.ep.web.constant.report.CtReportConst;
import my.ep.web.managedbean.report.ct.CtReportManagedBean;
import my.ep.web.util.DateUtils;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * The Class CtReportBackingBean.
 * 
 * @author Nancy Ang
 */
@ManagedBean(name = "ctReportBB")
@ViewScoped
public class CtReportBackingBean extends CtReportBaseBackingBean<CtReportManagedBean> {
	
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CtReportBackingBean.class);
	private static final String NAV_TO_CT_REPORT_MAIN = "/xhtml/reports/ct/ctReportMain.xhtml";
	
	public CtReportBackingBean() {
		// set default date value
		getManagedBean().setFromDate(DateUtils.getFirstDateOfTheYear());
		getManagedBean().setToDate(DateUtils.getCurrentDateWithoutTime());
	}
	
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
	 * This method use to generate CT report.
	 */
	public void generateCtReport() {
		log.info("generateCtReport");
		
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		
		if (getManagedBean().getSelectedMinistry() != null) {
			map.put(CtReportConst.PARAM_KEY_MINISTRY, getManagedBean()
					.getSelectedMinistry().getOrgProfileId().toString());
		}
		if (getManagedBean().getSelectedKumpulanPTJ() != null) {
			map.put(CtReportConst.PARAM_KEY_KUMPULAN_PTJ, getManagedBean()
					.getSelectedKumpulanPTJ().getOrgProfileId().toString());
		}		
		if (getManagedBean().getSelectedPTJ() != null) {
			map.put(CtReportConst.PARAM_KEY_PTJ, getManagedBean()
					.getSelectedPTJ().getOrgProfileId().toString());
		}		
		if (StringUtils.isNotEmpty(getManagedBean().getContractNo())) {
			map.put(CtReportConst.PARAM_KEY_CONTRACT_NO, getManagedBean()
					.getContractNo());
		}		
		if (StringUtils.isNotEmpty(getManagedBean().getContractPhysicalNo())) {
			map.put(CtReportConst.PARAM_KEY_PHYSICAL_CONTRACT_NO, getManagedBean()
					.getContractPhysicalNo());
		}		
		if (StringUtils.isNotEmpty(getManagedBean().getLoaNo())) {
			map.put(CtReportConst.PARAM_KEY_LOA_NO, getManagedBean()
					.getLoaNo());
		}		
		if (StringUtils.isNotEmpty(getManagedBean().getLoaPhysicalNo())) {
			map.put(CtReportConst.PARAM_KEY_LOA_PHYSICAL_NO, getManagedBean()
					.getLoaPhysicalNo());
		}
		if (StringUtils.isNotEmpty(getManagedBean().getAgreement())) {
			map.put(CtReportConst.PARAM_KEY_AGREEMENT, getManagedBean()
					.getAgreement());
		}
		if (getManagedBean().getProcurementMode() != null) {
			map.put(CtReportConst.PARAM_KEY_PROCUREMENT_MODE, getManagedBean()
					.getProcurementMode().toString());
		}
		if (StringUtils.isNotEmpty(getManagedBean().getStatus())) {
			map.put(CtReportConst.PARAM_KEY_STATUS, getManagedBean()
					.getStatus());
		}
		if (getManagedBean().getfromDate() != null
				&& StringUtils.isNotEmpty(getManagedBean().getfromDate().toString())) {
			map.put(CtReportConst.PARAM_KEY_FROM_DATE, df.format(
					getManagedBean().getfromDate()));
		}
		if (getManagedBean().getToDate() != null
				&& StringUtils.isNotEmpty(getManagedBean().getToDate().toString())) {
			map.put(CtReportConst.PARAM_KEY_TO_DATE, df.format(
					getManagedBean().getToDate()));
		}
		
		if (MapUtils.isNotEmpty(map)) {
			// mandatory map values
			map.put(CtReportConst.PARAM_KEY_REPORT, getManagedBean()
					.getReportName());
			map.put(CtReportConst.PARAM_KEY_REPORT_ID, getManagedBean()
					.getReportCode());
			map.put(CtReportConst.PARAM_KEY_USER_ID, getFacesScopes()
					.getUserSessionBean().getUserId().toString());
		} else {
			throw new EpException(MessageConst.ERROR_REPORT_780003);
		}		
		generateCtReport(map);
	}
	
	/**
	 * The method used to reset the search criteria for report.
	 */
	public void resetAction() {
		getManagedBean().setCurrentActiveOrg(null);
		getManagedBean().setDisableMinistryField(false);
		getManagedBean().setDisableOrgField(false);
		getManagedBean().setSelectedMinistry(null);
		getManagedBean().setSelectedKumpulanPTJ(null);
		getManagedBean().setSelectedPTJ(null);
		getManagedBean().setContractNo(null);
		getManagedBean().setContractPhysicalNo(null);
		getManagedBean().setLoaNo(null);
		getManagedBean().setLoaPhysicalNo(null);
		getManagedBean().setAgreement(null);
		getManagedBean().setProcurementMode(null);
		getManagedBean().setStatus(null);
		getManagedBean().setFromDate(DateUtils.getFirstDateOfTheYear());
		getManagedBean().setToDate(DateUtils.getCurrentDateWithoutTime());
	}
}
