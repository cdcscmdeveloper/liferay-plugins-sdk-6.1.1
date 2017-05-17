package my.ep.web.backingbean.report.ct;

import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.type.pm.RoleCode;
import my.ep.web.constant.report.CtReportConst;
import my.ep.web.managedbean.report.ct.CtReportManagedBean;
import my.ep.web.util.pm.helper.PmCommonUtil;

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
		resetAction();
		if (StringUtils.isNotBlank(getManagedBean().getReportCode())) {
			retrieveReportValueFromDb();
		}
		
		setCurrentActiveOrg();
	}
	
	/**
	 * retrieve and set the current active org
	 */
	private void setCurrentActiveOrg() {
		log.info("setCurrentActiveOrg");
		if (getFacesScopes().getUserSessionBean().getUserId() == null) {
			throw new EpException(MessageConst.ERROR_770149);
		}
		List<PmUserOrg> userOrgList = PmCommonUtil
				.retrieveActiveOrgWithoutRole(getFacesScopes()
						.getUserSessionBean().getUserId(), false);

		if (userOrgList == null || userOrgList.isEmpty()) {
			throw new EpException(MessageConst.ERROR_770105);
		}

		PmOrgProfile pmOrgProfile = userOrgList.get(0).getPmOrgProfile();
		this.getManagedBean().setCurrentActiveOrg(pmOrgProfile);

		if (getFacesScopes().hasRoleUser(RoleCode.MIN_CONTRACT_ADMIN.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CCA.value())
				|| getFacesScopes().hasRoleUser(RoleCode.CT_APPROVER.value())
				|| getFacesScopes().hasRoleUser(RoleCode.BPK_CC_APPROVER.value())) {
			getManagedBean().setDisableOrgField(true);
			getManagedBean().setDisableMinistryField(true);
			getManagedBean().setSelectedMinistry(
					retrieveMinistryOrgProfile(pmOrgProfile.getOrgProfileId()));
			getManagedBean().setSelectedKumpulanPTJ(
					retrieveJabatanOrgProfile(pmOrgProfile.getOrgProfileId()));
			getManagedBean().setSelectedPTJ(pmOrgProfile);
		} else if(getFacesScopes().hasRoleUser(RoleCode.AUDITOR_INTERNAL.value())){
			getManagedBean().setSelectedMinistry(
					pmOrgProfile);
			getManagedBean().setDisableMinistryField(true);
		}
	} 
	
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
		getManagedBean().setProcurementMethod(null);
		getManagedBean().setStatus(null);
		getManagedBean().setFromDate(null);
		getManagedBean().setToDate(null);
	}
	
	/**
	 * This method use to generate CT report.
	 */
	public void generateCtReport() {
		generateCtReport(constructParamMap());
	}
	
	private LinkedHashMap<String, String> constructParamMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		
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
		if (getManagedBean().getContractNo() != null) {
			map.put(CtReportConst.PARAM_KEY_CONTRACT_NO, getManagedBean()
					.getContractNo());
		}		
		if (getManagedBean().getContractPhysicalNo() != null) {
			map.put(CtReportConst.PARAM_KEY_PHYSICAL_CONTRACT_NO, getManagedBean()
					.getContractPhysicalNo());
		}		
		if (getManagedBean().getLoaNo() != null) {
			map.put(CtReportConst.PARAM_KEY_LOA_NO, getManagedBean()
					.getLoaNo());
		}		
		if (getManagedBean().getLoaPhysicalNo() != null) {
			map.put(CtReportConst.PARAM_KEY_LOA_PHYSICAL_NO, getManagedBean()
					.getLoaPhysicalNo());
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
		
		return map;
	}
}
