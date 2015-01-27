package my.ep.web.backingbean.sm.personnelrequestactivation;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.MessageUtils;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.ResourceBundleUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.springframework.util.CollectionUtils;

@ManagedBean(name = "smPersonnelRequestActivationBackingBean")
@ViewScoped
public class SmPersonnelRequestActivationBackingBean {
	private SmSupplier smSupplier;
	private List<SmPersonnel> selectedExpiredActivationPersonnels;
	private List<SmPersonnel> expiredActivationPersonnels;

	@PostConstruct
	public void init() {
		selectedExpiredActivationPersonnels = new ArrayList<SmPersonnel>();
		smSupplier = SmApplicationUtil.getCurrentSupplier();
		initExpiredPersonnels();
	}

	public void resendActivation() {
		validate();
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmPersonnelList(selectedExpiredActivationPersonnels);
		getPersonnelSO().resendPersonnelActivationEmail(reqDTO);

		FacesContext fc = FacesContext.getCurrentInstance();
		fc.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_721028,
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						"/xhtml/sm/userProfileMaintenance/outputMsg");
	}

	public IPersonnelSO getPersonnelSO() {
		return (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);
	}

	private void initExpiredPersonnels() {
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		reqDTO.setSmSupplierId(smSupplier.getSupplierId());
		PersonnelResDTO resDTO = getPersonnelSO()
				.retrieveExpiredPendingActivationApprovedPersonnels(reqDTO);
		if (resDTO.getSmPersonnelList() != null
				&& !resDTO.getSmPersonnelList().isEmpty()) {
			expiredActivationPersonnels = resDTO.getSmPersonnelList();
		} else {
			expiredActivationPersonnels = new ArrayList<SmPersonnel>();
		}
		initPersonnelCompanyRole();

	}

	private void validate() {
		if (CollectionUtils.isEmpty(selectedExpiredActivationPersonnels)) {
			throw new EpException(MessageConst.ERROR_722534,
					new String[] { SmCommonUtil.getMessage("lbl.sm.personnel",
							null) });
		}
	}

	private void initPersonnelCompanyRole() {
		StringBuffer sb = null;
		for (SmPersonnel per : expiredActivationPersonnels) {
			sb = new StringBuffer();
			if (per.isEquityOwner()) {
				sb.append(
						SmCommonUtil.getMessage(
								SupplierManagementConst.PERSONNEL_EQUI_OWNER,
								null)).append(",");
			}
			if (per.isDirector()) {
				sb.append(
						SmCommonUtil.getMessage(
								SupplierManagementConst.PERSONNEL_DIR_ROLENAME,
								null)).append(",");
			}
			if (per.isHigherManagement()) {
				sb.append(
						SmCommonUtil
								.getMessage(
										SupplierManagementConst.PERSONNEL_HGHR_ROLENAME,
										null)).append(",");
			}
			if (per.isClerical()) {
				if (per.getSmAppl()
						.getSupplierType()
						.equalsIgnoreCase(
								SupplierManagementConst.STYPE_CONSULTANT)) {
					sb.append(
							SmCommonUtil
									.getMessage(
											SupplierManagementConst.PERSONNEL_CLERICAL_STAFF_ROLENAME,
											null)).append(",");
				} else {
					sb.append(
							SmCommonUtil
									.getMessage(
											SupplierManagementConst.PERSONNEL_STAFF_ROLENAME,
											null)).append(",");
				}
			}
			if (per.isManagement()) {
				sb.append(
						SmCommonUtil
								.getMessage(
										SupplierManagementConst.PERSONNEL_MANAGEMENT_ROLENAME,
										null)).append(",");
			}
			if (per.isSemiProf()) {
				sb.append(
						SmCommonUtil
								.getMessage(
										SupplierManagementConst.PERSONNEL_SEMIPROF_STAFF_ROLENAME,
										null)).append(",");
			}
			if (!sb.toString().isEmpty() && sb.toString().length() > 0) {
				per.setRoleName(sb.toString().substring(0,
						sb.toString().length() - 1));
			}
			sb = null;
		}
	}

	public List<SmPersonnel> getSelectedExpiredActivationPersonnels() {
		return selectedExpiredActivationPersonnels;
	}

	public void setSelectedExpiredActivationPersonnels(
			List<SmPersonnel> selectedExpiredActivationPersonnels) {
		this.selectedExpiredActivationPersonnels = selectedExpiredActivationPersonnels;
	}

	public List<SmPersonnel> getExpiredActivationPersonnels() {
		return expiredActivationPersonnels;
	}

	public void setExpiredActivationPersonnels(
			List<SmPersonnel> expiredActivationPersonnels) {
		this.expiredActivationPersonnels = expiredActivationPersonnels;
	}

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

}
