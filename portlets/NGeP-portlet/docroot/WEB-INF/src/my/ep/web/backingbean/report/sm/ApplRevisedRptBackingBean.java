package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import my.ep.constants.CommonConstants;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserRole;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.type.pm.RoleCode;
import my.ep.web.constant.report.SmReportConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.report.sm.ApplRevisedRptManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;

@ManagedBean(name = "applRevisedRptBB")
@ViewScoped
public class ApplRevisedRptBackingBean extends SmReportBackingBean<ApplRevisedRptManagedBean> {
	private static final long serialVersionUID = 1L;
	private static final Logger log = EpLogger.getLogger(ApplRevisedRptBackingBean.class);

	public ApplRevisedRptBackingBean() {
		setDefaultDateFromAndDateTo();
		getUserZone();
		getUserRole();
		getUserValidation();
	}

	// To find user zone
	public String getUserZone() {
		PmZone zone = SmCommonUtil.getInstance().getUserZone();

		if (zone != null) {
			getManagedBean().setZone(zone.getZoneName().toUpperCase());
		} else {
			getManagedBean().setZone(null);
		}

		log.info("User Zone>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + getManagedBean().getZone());
		return getManagedBean().getZone();
	}

	// To find user role
	public String getUserRole() {
		FacesScopes fs = new FacesScopes();
		PmUser user = fs.getUserSessionBean();
		List<PmUserRole> userRoles = SmCommonUtil.getUserRolesByUserId(user.getUserId());
		String ePRole = StringUtils.EMPTY;
		if (!EntityHelper.isCollectionEmpty(userRoles)) {
			for (PmUserRole userRole : userRoles) {
				if (userRole.getPmRole().getRoleCode().equals(RoleCode.UPS_APPROVER.value())) {
					ePRole = userRole.getPmRole().getRoleCode();
					break;
				}
			}
		}
		log.info("User Role >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + ePRole);
		return ePRole;
	}

	// User Validation
	public void getUserValidation() {
		if (getUserRole().equalsIgnoreCase(RoleCode.UPS_APPROVER.value()) && getUserZone().equalsIgnoreCase("SABAH")) {
			getManagedBean().setZone(SmReportConst.SM_ZONE_SABAH);
			getManagedBean().setSupplierType(SupplierManagementConst.SUPP_TYPE_CONTRACTOR);
			getManagedBean().setFlag(true);
		} else if (getUserRole().equalsIgnoreCase(RoleCode.UPS_APPROVER.value()) && getUserZone().equalsIgnoreCase("SARAWAK")) {
			getManagedBean().setZone(SmReportConst.SM_ZONE_SARAWAK);
			getManagedBean().setSupplierType(SupplierManagementConst.SUPP_TYPE_CONTRACTOR);
			getManagedBean().setFlag(true);
		} else {
			getManagedBean().setFlag(false);
		}
		log.info("Flag Data >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + getManagedBean().isFlag());
	}

	// Generate Report
	public void generateReport() {
		checkOneYearDifferent(getManagedBean().getDateFrom(), getManagedBean().getDateTo());
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		SimpleDateFormat df = new SimpleDateFormat(CommonConstants.DEFAULT_DATE_FORMAT);
		retrieveReportValueFromDb();

		params.put(SmReportConst.PARAM_DATE_FROM, df.format(getManagedBean().getDateFrom()));
		params.put(SmReportConst.PARAM_DATE_TO, df.format(getManagedBean().getDateTo()));
		params.put(SmReportConst.PARAM_ZONE, getManagedBean().getZone() == null ? StringUtils.EMPTY : getManagedBean()
				.getZone());

		if (getManagedBean().getSelectedOrgProfile() != null
				&& getManagedBean().getSelectedOrgProfile().getSmSupplier() != null) {
			params.put(SmReportConst.PARAM_COMPANY_NAME, getManagedBean().getSelectedOrgProfile().getSmSupplier()
					.getCompanyName());
		}
		if (StringUtils.isNotBlank(getManagedBean().getSupplierType())) {
			params.put(SmReportConst.PARAM_SUPPLIER_TYPE, getManagedBean().getSupplierType());
		}
		if (StringUtils.isNotBlank(getManagedBean().getApplNo())) {
			params.put(SmReportConst.PARAM_APPL_NO, getManagedBean().getApplNo());
		}

		if (!StringUtils.isBlank(getManagedBean().getApplType())) {
			params.put(SmReportConst.PARAM_APPL_TYPE, getManagedBean().getApplType());
		}

		generateSmReport(params);
	}

	// Reset Button
	public void resetSearch() {
		if (getUserRole().equalsIgnoreCase(RoleCode.UPS_APPROVER.value()) && getUserZone().equalsIgnoreCase("SABAH")) {
			getManagedBean().setZone(SmReportConst.SM_ZONE_SABAH);
			getManagedBean().setSupplierType(SupplierManagementConst.SUPP_TYPE_CONTRACTOR);
		} else if (getUserRole().equalsIgnoreCase(RoleCode.UPS_APPROVER.value()) && getUserZone().equalsIgnoreCase("SARAWAK")) {
			getManagedBean().setZone(SmReportConst.SM_ZONE_SARAWAK);
			getManagedBean().setSupplierType(SupplierManagementConst.SUPP_TYPE_CONTRACTOR);
		} else {
			getManagedBean().setZone(null);
			getManagedBean().setSupplierType(null);
		}
		getManagedBean().setSelectedOrgProfile(null);
		getManagedBean().setApplNo(null);
		getManagedBean().setApplType(null);
		
		setDefaultDateFromAndDateTo();
	}
}
