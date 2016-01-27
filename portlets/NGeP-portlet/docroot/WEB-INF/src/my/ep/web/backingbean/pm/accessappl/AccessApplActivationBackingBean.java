package my.ep.web.backingbean.pm.accessappl;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import my.ep.biz.service.pm.access.application.IAccessApplSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.access.application.AccessApplReqDTO;
import my.ep.dto.pm.access.application.AccessApplResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmAccessAppl;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.managedbean.pm.accessappl.AccessApplActivationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;

@ManagedBean(name = "accessApplActivationBackingBean")
public class AccessApplActivationBackingBean extends
BaseBackingBean<AccessApplActivationManagedBean>{
	
	/** The log. */
	private static Logger log = Logger
			.getLogger(AccessApplActivationBackingBean.class);
	
	private String loginId;
	private String activationKey;
	
	private static final String ACCESS_APPL_ACTIVATION_PAGE = "/xhtml/pm/accessappl/accessApplicationActivation.xhtml";
	private static final String ACCESS_APPL_OUTPUT_MSG = "/xhtml/pm/accessappl/outputMsg.xhtml";
	
	

	public void preRenderActivation(){
		log.info("preRenderActivation");
		renewSesionObject();
		IAccessApplSO accessApplSO = null;
		try {
			HttpServletRequest originalServletRequest = new FacesScopes()
					.getOriginalServletRequest();
			String encyptActivationKey = originalServletRequest.getParameter("kp");
			String hashAccessApplId = originalServletRequest.getParameter("sp");
			String createdTimeParam = originalServletRequest.getParameter("ct");
			String accessApplIdParam = originalServletRequest
					.getParameter("ap");
			PmAccessAppl accessAppl = null;
			accessApplSO = (IAccessApplSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IAccessApplSO.class);
			
			AccessApplReqDTO reqDTO = new AccessApplReqDTO();
			reqDTO.setAccessApplId(Long.valueOf(accessApplIdParam));
			AccessApplResDTO resDTO = accessApplSO.retrieveAccessApplById(reqDTO);
			
			if(resDTO.isSuccessful()){
				accessAppl = resDTO.getAccessAppl();
			}
			
			if(accessAppl != null  
					&& hashAccessApplId.equals(
							PmCommonUtil.hashString(String.valueOf(accessAppl.getAccessApplId())))
							&& isLatestActivationEmail(createdTimeParam, accessAppl)) {
				Date latestApproved = null;
				if(accessAppl.getCompanyProfileStatus() != null && accessAppl.getMarketResearchStatus() !=null){
					if(accessAppl.getCompanyProfileStatus().getChangedDate().
							after(accessAppl.getMarketResearchStatus().getChangedDate())){
						latestApproved = accessAppl.getCompanyProfileStatus().getChangedDate();
					}else{
						latestApproved = accessAppl.getMarketResearchStatus().getChangedDate();
					}
				}else if(accessAppl.getCompanyProfileStatus() != null){
					latestApproved = accessAppl.getCompanyProfileStatus().getChangedDate();
				}else if(accessAppl.getMarketResearchStatus() !=null){
					latestApproved = accessAppl.getMarketResearchStatus().getChangedDate();
				}
				long applDateInMilis = latestApproved.getTime();
				long dayInMilis = 1000 * 60 * 60 * 24;

				Date expiryDate = new Date(applDateInMilis + (7 * dayInMilis));
				log.info("----Activation Access Appl ID----" + accessAppl.getAccessApplId());
				log.info("----Activation Access Appl Expiry Date----" + expiryDate);
				if(new Date().before(expiryDate)) { 
					log.info("****VALID - Before Expiry Date******");
					this.getManagedBean().setAccessAppl(accessAppl);
					this.getManagedBean().setEncryptActivationKey(encyptActivationKey);
					PmCommonUtil.redirectToPage(
							ACCESS_APPL_ACTIVATION_PAGE,
							null,null);
				} else {
					log.info("****VALID - Expired******");
					PmCommonUtil.redirectToPage(
							ACCESS_APPL_OUTPUT_MSG,
							MessageConst.INFO_721031,null);
				}
			}else{
				log.info("****Invalid Access Appl - not found******");
				PmCommonUtil.redirectToPage(
						ACCESS_APPL_OUTPUT_MSG,
						MessageConst.INFO_721031,null);
			}
		} catch (Exception ex) {
			PmCommonUtil.redirectToPage(
					ACCESS_APPL_OUTPUT_MSG,
					MessageConst.INFO_721031,null);
		}
		
	}
	
	//  to check whether the activation email send is latest or
	// already override by new activation email
	private boolean isLatestActivationEmail(String createdTimeParam,
				PmAccessAppl accessAppl) {
		log.info("----isLatestActivationEmail----");
		boolean retVal = true;
		try {
			if (createdTimeParam != null
					&& StringUtils.isNotBlank(createdTimeParam)) {
					Long createdTime = Long.parseLong(createdTimeParam);
					log.info("----isLatestActivationEmail----" 
							+ createdTime + " vs " + accessAppl.getCreatedDate()
							.getTime());
					if (createdTime.compareTo(accessAppl.getCreatedDate()
							.getTime()) != 0) {
						retVal = false;
					}
				}
			} catch (Exception ex) {
			}
			return retVal;
		}
	
	
	public String activateAccount() {
		String keyStr = getManagedBean().getAccessAppl().getAccessApplId()
				+ activationKey;
		String hashStr = PmCommonUtil.hashString(keyStr);
		if (!hashStr.equals(getManagedBean().getEncryptActivationKey())) {
			throw new EpException(MessageConst.ERROR_721040);
		} else {
			log.info("success");
			IAccessApplSO accessApplSO = null;
			try{
				PmAccessAppl accessAppl = getManagedBean().getAccessAppl();
				accessApplSO = (IAccessApplSO) BusinessDelegate.getInstance()
						.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
								IAccessApplSO.class);
				
				AccessApplReqDTO reqDTO = new AccessApplReqDTO();
				reqDTO.setAccessApplId(accessAppl.getAccessApplId());
				AccessApplResDTO resDTO = accessApplSO.activateAccessAppl(reqDTO);
				if(resDTO.isSuccessful()){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, null, "Account Activated"));
					return CommonUIConst.ACKNOWLEDGE_PAGE;
				}
			}catch(Exception ex){
				throw new EpException(ex.getMessage());
			}finally{
				accessApplSO = null;
			}
			return null;
		}

	}

	/**
	 * @return the loginId
	 */
	public String getLoginId() {
		return loginId;
	}

	/**
	 * @param loginId the loginId to set
	 */
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	/**
	 * @return the activationKey
	 */
	public String getActivationKey() {
		return activationKey;
	}

	/**
	 * @param activationKey the activationKey to set
	 */
	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

}
