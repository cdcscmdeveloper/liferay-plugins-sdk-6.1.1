package my.ep.web.backingbean.sm.reactivatebasic;

import javax.faces.application.FacesMessage;
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
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "reactivateBasicBackingBean")
@SessionScoped
public class ReactivateBasicBackingBean extends
		BaseBackingBean<BaseManagedBean> {

	/**
	 * 
	 */
	public static Logger log = Logger.getLogger(ReactivateBasicBackingBean.class); //#34095
	private static final long serialVersionUID = -5634641289404728683L;
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
				"/xhtml/sm/reactivatebasic/reactivateBasicAccount.xhtml", null,
				null, null);
	}

	/**
	 * Pre-render basic account activation
	 */
	public void preRenderBasicAccountReactivation() {
		renewObject();
		try {
			//#34095 - START
			log.info("#34095 - ReactiveBB log #1");
			HttpServletRequest originalServletRequest = new FacesScopes()
					.getOriginalServletRequest();
			log.info("#34095 - ReactiveBB log #2");
			encryptActivationKey = originalServletRequest.getParameter("kp");
			log.info("#34095 - ReactiveBB log #3 - encryptActivation : " + encryptActivationKey);
			createdTimeParam = originalServletRequest.getParameter("ct");
			log.info("#34095 - ReactiveBB log #4 - createdTimeParam : " + createdTimeParam);
			adminPersonnelIdParam = originalServletRequest.getParameter("pr");
			log.info("#34095 - ReactiveBB log #5 - adminPersonnelIdParam : " + adminPersonnelIdParam);

			RegistrationReqDTO reqDTO = new RegistrationReqDTO();
			log.info("#34095 - ReactiveBB log #6");
			reqDTO.setPersonnelId(Long.parseLong(adminPersonnelIdParam));
			log.info("#34095 - ReactiveBB log #7 - reqDTO personnelId : " + reqDTO.getPersonnelId());
			PersonnelReqDTO personnelReqDTO = new PersonnelReqDTO();
			log.info("#34095 - ReactiveBB log #8");
			personnelReqDTO.setPersonnelId(reqDTO.getPersonnelId());
			log.info("#34095 - ReactiveBB log #9 - personnelReqDTO personnelId : " + personnelReqDTO.getPersonnelId());
			basicSupplierAdmin = SmServiceLookup.getPersonnelSO()
					.retrievePersonnelById(personnelReqDTO).getSmPersonnel();
			log.info("#34095 - ReactiveBB log #10 - supplierAdmin name : " + basicSupplierAdmin.getName());
			log.info("#34095 - ReactiveBB log #10 - supplierAdmin record status : " + basicSupplierAdmin.getRecordStatusFlg());
			log.info("#34095 - ReactiveBB log #10 - supplierAdmin email : " + basicSupplierAdmin.getEmail());
			log.info("#34095 - ReactiveBB log #10 - supplierAdmin eprole : " + basicSupplierAdmin.getEpRole());
			basicSupplier = SmServiceLookup.getRegistrationSO()
					.retrieveValidReactivateBasicSupplier(reqDTO).getSmSupp();
			log.info("#34095 - ReactiveBB log #11");
			log.info("#34095 - ReactiveBB log #11 - supplier records : " + basicSupplier.getRecordStatusFlg());
			log.info("#34095 - ReactiveBB log #11 - supplier records : " + basicSupplier.getEpNo());
			log.info("#34095 - ReactiveBB log #11 - supplier changeddate : " + basicSupplier.getChangedDate());
			loginId = basicSupplierAdmin.getPmUser().getLoginId();
			log.info("#34095 - ReactiveBB log #12 - login id : " + loginId);
			validateActivationUrl();
			log.info("#34095 - log #13");
		} catch (Exception ex) {
			log.info("#34095 - ReactiveBB log #14 catch");
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.INFO_721031, null, FacesMessage.SEVERITY_INFO);
		}
		//#34095 - END
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
						MessageConst.INFO_721028, null,
						FacesMessage.SEVERITY_INFO);
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
						MessageConst.INFO_721029, null,
						FacesMessage.SEVERITY_INFO);
			} catch (Exception ex) {
				throw new EpException(ex.getMessage());
			}
		}

	}

	private void validateActivationUrl() {
		//#34095 - START
		if (basicSupplier == null || StringUtils.isBlank(encryptActivationKey)
				|| basicSupplierAdmin == null) {
			log.info("#34095 - validateActivationUrl log #1");
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.INFO_721031, null, FacesMessage.SEVERITY_INFO);
		} else if (createdTimeParam != null
				&& StringUtils.isNotBlank(createdTimeParam)
				&& new Long(createdTimeParam).compareTo(basicSupplier
						.getChangedDate().getTime()) != 0) {
			log.info("#34095 - validateActivationUrl log #2");
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.INFO_721031, null, FacesMessage.SEVERITY_INFO);
		} else if (createdTimeParam == null
				|| StringUtils.isBlank(createdTimeParam)) {
			log.info("#34095 - validateActivationUrl log #3");
			SmCommonUtil.redirectToPage(
					SmApplicationUtil.getApplicationErrorPage(),
					MessageConst.INFO_721031, null, FacesMessage.SEVERITY_INFO);
		} else {
			log.info("#34095 - validateActivationUrl log #4");
			SmCommonUtil.redirectToPage(
					"/xhtml/sm/reactivatebasic/basicAccountActivation.xhtml",
					"", null, FacesMessage.SEVERITY_INFO);
			//#34095 - END
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
