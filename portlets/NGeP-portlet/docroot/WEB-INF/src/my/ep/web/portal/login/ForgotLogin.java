package my.ep.web.portal.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import my.ep.biz.service.osb.ntf.INtfClientSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.NotificationTextConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.osb.ntf.NtfRequestDTO;
import my.ep.dto.osb.ntf.NtfResponseDTO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.authorization.PmUserOrg;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name="ForgotLogin")
@RequestScoped
public class ForgotLogin {
	
	private final static Logger log = Logger.getLogger(ForgotLogin.class.getName());
	private String emailAddress;
	private String loginId;
	
	public ForgotLogin() {
		//log.info("ForgotLogin");
	}
	
	public void cancelBtnAction () throws IOException {
		//log.info("cancelbtnaction");
		FacesContext.getCurrentInstance().getExternalContext().redirect(
				 EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_APPLICATION_URL));
	}
	
	public String forgotLoginPassword() {
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setLoginId(this.loginId);
		
		UserResDTO resDTO = userSO.forgotLoginPassword(reqDTO);
		
		if (resDTO.isSuccessful() && !StringUtils.isEmpty(resDTO.getEmailAddress())) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, 
							MessageUtils.getMessage(MessageConst.ERROR_700122,
									new String[] {resDTO.getEmailAddress()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			return CommonUIConst.ACKNOWLEDGE_PAGE;
		}
		
    	return null;
	}
	
	public String forgotLoginId() {
		//log.info("Email Address: " + this.emailAddress);
		
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setEmail(getEmailAddress());
		reqDTO.setFetchOrg(true);
		
		UserResDTO resDTO = userSO.retrieveUserByEmail(reqDTO);
		
		if (resDTO.getUserList() != null && resDTO.getUserList().size() > 0) {
			sendForgotLoginIdEmail(resDTO.getUserList());
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null, 
							MessageUtils.getMessage(MessageConst.ERROR_700122,
									new String[] {getEmailAddress()}, 
									MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale())));
			return CommonUIConst.ACKNOWLEDGE_PAGE;
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, null, 
							MessageUtils.getMessage(MessageConst.ERROR_700121,
									MessageUtils.BUNDLE_COMMON_MESSAGES, 
									new FacesScopes().getUserLocale())));
		}
    	return null;
	}
	
	private void sendForgotLoginIdEmail(List <PmUser> pmUserList) {
		List<String> emailLst = new ArrayList<String>();
		List<String> contentParaLst = new ArrayList<String>();
		StringBuilder sbContent = new StringBuilder();
		
		String loginIdStr = ResourceBundleUtils.getMessage("lbl.lg.login.id",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		String companyOrgStr = ResourceBundleUtils.getMessage("lbl.lg.company.org.name",
				ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale());
		
		emailLst.add(this.emailAddress);
		for (PmUser pmUser : pmUserList) {
			if (sbContent.length() != 0) {
				sbContent.append("<BR/><BR/>");
			}
			
			sbContent.append(loginIdStr);
			sbContent.append(" : <strong>");
			sbContent.append(pmUser.getLoginId());
			sbContent.append("</strong><BR/>");
			
			for(PmUserOrg pmUserOrg : pmUser.getPmUserOrgs()) {
				if (pmUserOrg.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
					sbContent.append(companyOrgStr);
					sbContent.append(" : ");
					
					if (pmUserOrg.getPmOrgProfile().getOrgTypeCode().getParameterCode().equalsIgnoreCase(
							ParameterConst.ORG_TYPE_SUPPLIER)) {
						sbContent.append(pmUserOrg.getPmOrgProfile().getSmSupplier().getCompanyName());
					} else {
						sbContent.append(pmUserOrg.getPmOrgProfile().getOrgName());
					}
					break;
				}
			}
		}
		contentParaLst.add(sbContent.toString());
		sendNotif(NotificationTextConst.TEMPLATE_ID_401166, emailLst, contentParaLst);
	}
	
	private void sendNotif(String templateId,
			List<String> receiverList, List<String> contentParams) {
		
		INtfClientSO ntfClientSO = (INtfClientSO) EpEjbBusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.NTF, INtfClientSO.class);
		NtfRequestDTO reqDTO = new NtfRequestDTO();

		reqDTO.setTemplateId(templateId);
		reqDTO.setSenderId(ProfileManagementConst.EMAIL_SENDER);
		reqDTO.setEmailToArr(receiverList.toArray(new String[receiverList.size()]));
		reqDTO.setCntParaArr(contentParams.toArray(new String[contentParams.size()]));

		try {
			NtfResponseDTO resDTO = ntfClientSO.processAndPublishViaEmail(reqDTO);
			if (!NotificationTextConst.STATUS_CODE_SUCCESS.equals(resDTO.getStatusCode())) {
				throw new EpException(resDTO.getStatusDesc());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		return emailAddress;
	}
	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}

	/**
	 * @param userId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}	
}
