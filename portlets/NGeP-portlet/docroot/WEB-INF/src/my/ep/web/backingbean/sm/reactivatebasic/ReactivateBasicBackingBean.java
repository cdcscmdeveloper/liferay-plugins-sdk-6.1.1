package my.ep.web.backingbean.sm.reactivatebasic;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpServletRequest;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.ReactivateSupplierVO;

import org.apache.commons.lang3.StringUtils;

@ManagedBean(name = "reactivateBasicBackingBean")
@SessionScoped
public class ReactivateBasicBackingBean extends
		BaseBackingBean<BaseManagedBean> {

	private ReactivateSupplierVO reactivateBasic;
	private SmSupplier basicSupplier;
	private SmPersonnel basicSupplierAdmin;
	private String loginId;
	private String activationKey;
	private String encryptActivationKey;
	private String createdTimeParam;
	private String adminPersonnelIdParam;

	/**
	 * Pre-render reactivate basic account
	 */
	public void preRenderReactivateBasic() {
		renewObject();
		reactivateBasic = new ReactivateSupplierVO();
		SmCommonUtil.redirectToPage(
				"/xhtml/sm/reactivatebasic/reactivateBasicAccount.xhtml", "",
				null);
	}

	/**
	 * Pre-render basic account activation
	 */
	public void preRenderBasicAccountReactivation() {
		renewObject();
		try {
			HttpServletRequest originalServletRequest = new FacesScopes()
					.getOriginalServletRequest();
			encryptActivationKey = originalServletRequest.getParameter("kp");
			createdTimeParam = originalServletRequest.getParameter("ct");
			adminPersonnelIdParam = originalServletRequest.getParameter("pr");

			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			reqDTO.setPersonnelId(Long.parseLong(adminPersonnelIdParam));
			PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
			personnelReqDTO.setPersonnelId(reqDTO.getPersonnelId());
			basicSupplierAdmin = SmServiceLookup.getPersonnelSO()
					.retrievePersonnelById(personnelReqDTO).getSmPersonnel();
			basicSupplier = SmServiceLookup.getRegistrationSO()
					.retrieveValidReactivateBasicSupplier(reqDTO).getSmSupp();
			loginId = basicSupplierAdmin.getPmUser().getLoginId();
			validateActivationUrl();
		} catch (Exception ex) {
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.INFO_721031, null);
		}
	}

	/**
	 * Request to reactivate basic account
	 */
	public void reactivateBasic() {
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		FacesScopes fs = new FacesScopes();
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setIdentificationNo(reactivateBasic.getIdentificationNo());
		reqDTO.setEmail(reactivateBasic.getEmail());
		reqDTO.setEstablishDate(reactivateBasic.getEstablishDate());
		reqDTO.setRegNo(reactivateBasic.getRegNo());
		reqDTO.setPortalBaseUrl(SmCommonUtil.getBasePortalUrl());

		try {
			RegistrationResDTO resDTO = SmServiceLookup.getRegistrationSO()
					.requestToReactivateBasicAccount(reqDTO);

			if (resDTO.isSuccessful()) {
				renewObject();
				SmCommonUtil.redirectToPage(
						"/xhtml/sm/userProfileMaintenance/outputMsg",
						MessageConst.INFO_721028, null);
			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
	}

	/**
	 * Process basic account activation
	 */
	public void activateAccount() {
		String keyStr = basicSupplier.getSupplierId().toString()
				+ basicSupplierAdmin.getPersonnelId().toString()
				+ activationKey;
		String hashStr = SmCommonUtil.hashString(keyStr);
		if (!hashStr.equals(encryptActivationKey)) {
			throw new EpException(MessageConst.ERROR_721040);
		} else {
			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			FacesScopes fs = new FacesScopes();
			reqDTO.setServerName(fs.getRequest().getServerName());
			reqDTO.setServerPort(fs.getRequest().getServerPort());
			reqDTO.setSmSupp(basicSupplier);
			try {
				SmServiceLookup.getRegistrationSO().reactivateBasicAccount(
						reqDTO);
				renewObject();
				SmCommonUtil.redirectToPage(
						SmApplicationUtil.getApplicationErrorPage(),
						MessageConst.INFO_721029, null);
			} catch (Exception ex) {
				throw new EpException(ex.getMessage());
			}
		}

	}

	private void validateActivationUrl() {
		if (basicSupplier == null || StringUtils.isBlank(encryptActivationKey)
				|| basicSupplierAdmin == null) {
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.INFO_721031, null);
		} else if (createdTimeParam != null
				&& StringUtils.isNotBlank(createdTimeParam)
				&& new Long(createdTimeParam).compareTo(basicSupplier
						.getChangedDate().getTime()) != 0) {
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.INFO_721031, null);
		} else if (createdTimeParam == null
				|| StringUtils.isBlank(createdTimeParam)) {
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.INFO_721031, null);
		} else {
			SmCommonUtil.redirectToPage(
					"/xhtml/sm/reactivatebasic/basicAccountActivation.xhtml",
					"", null);
		}

	}

	private void renewObject() {
		basicSupplier = null;
		loginId = StringUtils.EMPTY;
		activationKey = StringUtils.EMPTY;
		reactivateBasic = null;
		basicSupplierAdmin = null;
		encryptActivationKey = StringUtils.EMPTY;
	}

	public ReactivateSupplierVO getReactivateBasic() {
		return reactivateBasic;
	}

	public void setReactivateBasic(ReactivateSupplierVO reactivateBasic) {
		this.reactivateBasic = reactivateBasic;
	}

	public SmSupplier getBasicSupplier() {
		return basicSupplier;
	}

	public void setBasicSupplier(SmSupplier basicSupplier) {
		this.basicSupplier = basicSupplier;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public String getEncryptActivationKey() {
		return encryptActivationKey;
	}

	public void setEncryptActivationKey(String encryptActivationKey) {
		this.encryptActivationKey = encryptActivationKey;
	}

	public SmPersonnel getBasicSupplierAdmin() {
		return basicSupplierAdmin;
	}

	public void setBasicSupplierAdmin(SmPersonnel basicSupplierAdmin) {
		this.basicSupplierAdmin = basicSupplierAdmin;
	}
}
