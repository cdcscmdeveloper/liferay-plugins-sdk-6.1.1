package my.ep.web.backingbean.sm.reactivatebasic;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import my.ep.constant.MessageConst;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.ReactivateSupplierVO;

@ManagedBean(name = "reactivateBasicBackingBean")
@SessionScoped
public class ReactivateBasicBackingBean extends
		BaseBackingBean<BaseManagedBean> {

	private ReactivateSupplierVO reactivateBasic;
	private SmSupplier basicSupplier;
	private Long basicSupplierAdminId;
	private String loginId;
	private String activationKey;
	private String password;
	private String confirmPassword;
	private String encryptActivationKey;

	/**
	 * Request to reactivate basic account
	 */
	public void reactivateBasic() {
		reactivateBasic = new ReactivateSupplierVO();
		RegistrationReqDTO reqDTO = new RegistrationReqDTO();
		FacesScopes fs = new FacesScopes();
		reqDTO.setServerName(fs.getRequest().getServerName());
		reqDTO.setServerPort(fs.getRequest().getServerPort());
		reqDTO.setIdentificationNo(reactivateBasic.getIdentificationNo());
		reqDTO.setEmail(reactivateBasic.getEmail());
		reqDTO.setEstablishDate(reactivateBasic.getEstablishDate());
		reqDTO.setRegNo(reactivateBasic.getRegNo());

		try {
			RegistrationResDTO resDTO = SmServiceLookup.getRegistrationSO()
					.requestToReactivateBasicAccount(reqDTO);

			if (resDTO.isSuccessful()) {
				renewObject();
				SmCommonUtil.redirectToPage(
						"/xhtml/sm/userProfileMaintenance/outputMsg",
						MessageConst.INFO_721028);
			}
		} catch (Exception e) {
			throw new EpException(e.getMessage());
		}
	}

	/**
	 * Basic account activation
	 */
	public void basicAccountReactivation() {
		renewObject();
		try {
			HttpServletRequest originalServletRequest = new FacesScopes()
					.getOriginalServletRequest();

			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			reqDTO.setPersonnelId(Long.parseLong(originalServletRequest
					.getParameter("pr")));
			encryptActivationKey = originalServletRequest.getParameter("kp");
			basicSupplierAdminId = reqDTO.getPersonnelId();
			basicSupplier = SmServiceLookup.getRegistrationSO()
					.retrieveValidReactivateBasicSupplier(reqDTO).getSmSupp();
			if (basicSupplier == null
					|| StringUtils.isBlank(encryptActivationKey)
					|| basicSupplierAdminId == null) {
				SmCommonUtil.redirectToPage(
						SmApplicationUtil.getApplicationErrorPage(),
						MessageConst.INFO_721212);
			}
		} catch (Exception ex) {
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.INFO_721212);
		}
	}

	/**
	 * Process basic account activation
	 */
	public void activateAccount() {
		String keyStr = basicSupplier.getSupplierId() + basicSupplierAdminId
				+ activationKey;
		String hashStr = SmCommonUtil.hashString(keyStr);
		if (password.equalsIgnoreCase(loginId)) {
			throw new EpException(MessageConst.ERROR_721038);
		} else if (!password.equalsIgnoreCase(confirmPassword)) {
			throw new EpException(MessageConst.ERROR_721039);
		} else if (!hashStr.equals(encryptActivationKey)) {
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
			} catch (Exception ex) {
				throw new EpException(ex.getMessage());
			}
		}

	}

	private void renewObject() {
		basicSupplier = null;
		loginId = "";
		activationKey = "";
		password = "";
		confirmPassword = "";
		reactivateBasic = null;
		basicSupplierAdminId = null;
		encryptActivationKey = "";
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public Long getBasicSupplierAdminId() {
		return basicSupplierAdminId;
	}

	public void setBasicSupplierAdminId(Long basicSupplierAdminId) {
		this.basicSupplierAdminId = basicSupplierAdminId;
	}

	public String getEncryptActivationKey() {
		return encryptActivationKey;
	}

	public void setEncryptActivationKey(String encryptActivationKey) {
		this.encryptActivationKey = encryptActivationKey;
	}
}
