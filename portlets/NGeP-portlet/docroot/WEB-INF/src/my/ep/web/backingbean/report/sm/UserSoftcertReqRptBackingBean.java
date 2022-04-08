package my.ep.web.backingbean.report.sm;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.constants.CommonConstants;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
import my.ep.web.constant.report.SmReportConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.report.sm.UserSoftcertReqRptManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang.StringUtils;

@ManagedBean(name = "userSoftcertReqRptBB")
@ViewScoped
public class UserSoftcertReqRptBackingBean extends
		SmReportBackingBean<UserSoftcertReqRptManagedBean> {
	private static final long serialVersionUID = 1L;

	public UserSoftcertReqRptBackingBean() {
		setDefaultDateFromAndDateTo();
		getManagedBean().setShowCertProvider(showCertProvider());
		getManagedBean().setSupplierUser(isSuppUser());
	}

	public void generateReport() {
		SimpleDateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
		retrieveReportValueFromDb();

		// #25511. Unable to generate report because of lazy loading, so need to re-initialize supplier
		if (getManagedBean().isSupplierUser()) {
			params.put(SmReportConst.PARAM_COMPANY_NAME, SmApplicationUtil.getCurrentSupplier().getCompanyName());
		} else {
			addCompanyNameParam(params);
		}

		if (StringUtils.isNotBlank(getManagedBean().getUserName())) {
			params.put(SmReportConst.PARAM_USER_NAME, getManagedBean()
					.getUserName());
		}

		if (StringUtils.isNotBlank(getManagedBean().getMofNo())) {
			params.put(SmReportConst.PARAM_MOF_NO, getManagedBean().getMofNo());
		}

		if (StringUtils.isNotBlank(getManagedBean().getRegNo())) {
			params.put(SmReportConst.PARAM_REG_NO, getManagedBean().getRegNo());
		}

		if (StringUtils.isNotBlank(getManagedBean().getIdentificationNo())) {
			params.put(SmReportConst.PARAM_IDENT_NO, getManagedBean()
					.getIdentificationNo());
		}

		if (StringUtils.isNotBlank(getManagedBean().getSoftcertDateType())) {
			params.put(SmReportConst.PARAM_DATE_TYPE, getManagedBean()
					.getSoftcertDateType());

			if (getManagedBean().getDateFrom() != null) {
				params.put(SmReportConst.PARAM_DATE_FROM,
						df.format(getManagedBean().getDateFrom()));
				params.put(SmReportConst.PARAM_DATE_TO,
						df.format(getManagedBean().getDateTo()));
			}
		}

		if (StringUtils.isNotBlank(getManagedBean().getCertApplicationType())) {
			params.put(SmReportConst.PARAM_CERT_APPL_TYPE, getManagedBean().getCertApplicationType());
		}

		if (!getManagedBean().isShowRevoke() && getManagedBean().getCertStatus() != null) {
			params.put(SmReportConst.PARAM_CERT_APPL_STATUS, getManagedBean().getCertStatus());
		}

		if (StringUtils.isNotBlank(getManagedBean().getCertProvider())) {
			params.put(SmReportConst.PARAM_CERT_PROVIDER, getManagedBean().getCertProvider());
		}

		generateSmReport(params);
	}

	public void resetSearch() {
		if(!getManagedBean().isSupplierUser()){
			getManagedBean().setUserName(null);
			getManagedBean().setMofNo(null);
			getManagedBean().setRegNo(null);
			getManagedBean().setIdentificationNo(null);
		}
		getManagedBean().setDateFrom(null);
		getManagedBean().setDateTo(null);
		getManagedBean().setSoftcertDateType(null);
		getManagedBean().setCertApplicationType(null);
		getManagedBean().setCertStatus(null);
		getManagedBean().setCertProvider(null);
		getManagedBean().setShowRevoke(false);
	}

	/**
	 * Cert application type change. Event handle change the type of date
	 * selectable and disables the application status if revoke is chosen.
	 * 
	 * @param event
	 */
	public void certAppTypeOnChange(AjaxBehaviorEvent event) {
		String certAppType = (String) event.getComponent().getAttributes().get("value");
		if (SmReportConst.CERT_APP_REVOKE.equals(certAppType)) {
			getManagedBean().setShowRevoke(true);
			getManagedBean().setCertStatus("");
		} else {
			getManagedBean().setShowRevoke(false);
		}

	}

	private boolean isSuppUser() {
		boolean retVal = false;
		FacesScopes sc = new FacesScopes();
		if (sc.hasRoleUser(SupplierManagementConst.BASIC_SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.BASIC_SUPPLIER_USER)
				|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_USER)
				|| sc.hasRoleUser(SupplierManagementConst.SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.SUPPLIER_USER)
				|| sc.hasRoleUser(RoleCode.FL_USER.value())
				|| sc.hasRoleUser(RoleCode.SUPPLIER_TEMP.value())) {
			retVal = true;

			SmSupplier supplier = SmApplicationUtil.getCurrentSupplier();
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setSmSupplierId(supplier.getSupplierId());
			PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO()
					.retrievePersonnelBySupplierId(reqDTO);
			String loginId = new FacesScopes().getUserScreenName();
			if (!SmCommonUtil.isCollectionEmpty(resDTO.getSmPersonnelList())) {
				for (SmPersonnel personnel : resDTO.getSmPersonnelList()) {
					if (personnel.getPmUser() != null
							&& personnel.getPmUser().getUserId() != null
							&& loginId != null
							&& personnel.getPmUser().getLoginId() != null
							&& loginId.equals(personnel.getPmUser()
									.getLoginId())) {
						getManagedBean().setIdentificationNo(
								personnel.getIdentificationNo());
						getManagedBean().setUserName(personnel.getName());
						getManagedBean().setCompanyName(
								supplier.getCompanyName());
						getManagedBean().setRegNo(supplier.getRegNo());
						getManagedBean().setSelectedOrgProfile(
								personnel.getSmAppl().getSmSupplier()
										.getPmOrgProfile());
						SmMofAccount acc = SmCommonUtil
								.getMofAccountBySuppIdAndSuppType(personnel
										.getSmAppl().getSmSupplier()
										.getSupplierId(), personnel.getSmAppl()
										.getSupplierType());
						if (acc != null) {
							getManagedBean().setMofNo(acc.getMofNo());
						}
						break;
					}
				}
			}
		}
		return retVal;
	}

	/**
	 * Show cert provider only if user is a Supplier Admin.
	 * 
	 * @return
	 */
	private boolean showCertProvider() {

		FacesScopes sc = new FacesScopes();
		if (sc.getUserRoleList().size() == 1
				&& (sc.hasRoleUser(SupplierManagementConst.SUPPLIER_ADMIN)
						|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_ADMIN) || sc
							.hasRoleUser(SupplierManagementConst.BASIC_SUPPLIER_ADMIN))) {
			return false;
		}
		return true;
	}
}
