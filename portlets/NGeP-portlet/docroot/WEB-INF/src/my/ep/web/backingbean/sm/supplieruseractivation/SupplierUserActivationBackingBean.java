package my.ep.web.backingbean.sm.supplieruseractivation;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.supplieruseractivation.ISupplierUserActivationSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.supplieruseractivation.SupplierUserActivationReqDTO;
import my.ep.dto.sm.supplieruseractivation.SupplierUserActivationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.supplieruseractivation.SupplierUserActivationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmRuleHelper;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.UserLocalServiceUtil;


@ManagedBean(name="supplierUserActivationBackingBean")
public class SupplierUserActivationBackingBean extends
		BaseBackingBean<SupplierUserActivationManagedBean> {
	
	private static Logger log = Logger.getLogger(SupplierUserActivationBackingBean.class);

	private String tempPassword;
	
	//private String password;
	//private String confirmPassword;
	private String loginId;
	
		
	public SupplierUserActivationBackingBean(){
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}

	public void prerenderSupplierActivation() {
		renewSesionObject();
		log.info("****prerenderSupplierActivation - start******");
		HttpServletRequest originalServletRequest = new FacesScopes().getOriginalServletRequest();
		String personnelId = originalServletRequest.getParameter("pr");
		String hashSupplierId = originalServletRequest.getParameter("sp");
		String encryptedKey = originalServletRequest.getParameter("kp");
		String createdTimeParam = originalServletRequest.getParameter("ct");
		
		log.info("personnelId: " + personnelId);
		log.info("hashSupplierId: " + hashSupplierId);
		
		SmPersonnel personnel = null;
		SmSupplier supplierProfile = null;
		IPersonnelSO personnelSO = null;
		
		String errMsg = SmCommonUtil.getMessage(
				"lbl.sm.activation.invalid.link.activated", null);
		
		try {
			personnelSO = (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject("sm",IPersonnelSO.class);
			
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setPersonnelId(Long.valueOf(personnelId));
			PersonnelResDTO resDTO = personnelSO.retrieveValidActivationPersonnel(reqDTO);
			
			if(resDTO.getSmPersonnel() != null) {
				personnel = resDTO.getSmPersonnel();
				supplierProfile = personnel.getSmAppl().getSmSupplier();
			}else{
				//retrieve for invalid personnel
				
				// 1)Basic Personnel that already expired link and deleted by
				// scheduler
				// or
				// 2)MOF Personnel that already expired link and remove role by
				// scheduler
				personnel = SmServiceLookup.getPersonnelSO()
						.retrievePersonnelById(reqDTO).getSmPersonnel();
				if (personnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_DELETED
						|| (personnel.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE && StringUtils
								.isBlank(personnel.getEpRole()))) {
					errMsg = SmCommonUtil.getMessage(
							"lbl.sm.activation.invalid.link.expired",
							new String[] { SmRuleHelper.getInstance()
									.getEmailActivationDays() + "" });

					// reset back personnel to null
					personnel = null;
				}
				
			}
		} catch(Exception ex){
			getManagedBean().setValidActivation(false);
		}
		finally {
			personnelSO = null;
		}
		//log.info("hash supplier id: " + SmCommonUtil.hashString(String.valueOf(supplierProfile.getSupplierId())));
		
		FacesContext fc = FacesContext.getCurrentInstance();
		if(personnel != null && supplierProfile != null && !isPersonnelAlreadyDeactivatedOrTerminates(personnel, supplierProfile.getSupplierId()) 
				&& isLatestActivationEmail(createdTimeParam, personnel)) {
			if(hashSupplierId.equals(SmCommonUtil.hashString(String.valueOf(supplierProfile.getSupplierId())))) {
				
				long applDateInMilis = personnel.getCreatedDate().getTime();
				long dayInMilis = 1000 * 60 * 60 * 24;

				Date expiryDate = new Date(applDateInMilis + (SmRuleHelper.getInstance().getEmailActivationDays() * dayInMilis));
				log.info("----Activation Personnel ID----" + personnel.getPersonnelId());
				log.info("----Activation Expiry Date----" + expiryDate);
				if(new Date().before(expiryDate)) { 
					log.info("****VALID - Before Expiry Date******");
					this.getManagedBean().setValidActivation(true);
/*					this.getManagedBean().setPersonnelId(Long.parseLong(personnelId));
					this.getManagedBean().setSupplierId(supplierProfile.getSupplierId());*/
					personnel.setEpRole(getEpRole(personnel, supplierProfile));
					this.getManagedBean().setSmPersonnel(personnel);
					this.getManagedBean().setEncrpKeyStr(encryptedKey);
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/supplieruseractivation/enterActivationDetails.xhtml");
					return;
					// display form to enter temp password
				} else {
					log.info("****VALID - Expired******");
					// redirect & display activation expired
					this.getManagedBean().setValidActivation(false);
					errMsg = SmCommonUtil.getMessage("lbl.sm.activation.invalid.link.expired",
							new String[] { SmRuleHelper.getInstance()
							.getEmailActivationDays() + "" });
				}
			} else {
				// redirect & display invalid activation
				this.getManagedBean().setValidActivation(false);
				errMsg = SmCommonUtil.getMessage("lbl.sm.activation.invalid.link.expired",
						new String[] { SmRuleHelper.getInstance()
						.getEmailActivationDays() + "" });
				
				log.info("****Hash String is not match******");
			}
		} else {
			// redirect & display invalid activation
			this.getManagedBean().setValidActivation(false);
		}
		
		log.info("****Activation - no personnel/supplierProfile ******");
		redirectToInvalidActivationPage(fc,errMsg);
	}
	
	public String activateSupplierUser() {
		log.info("**********activateSupplierUser Start*********");
		log.info("*********isValidActivation**********" + this.getManagedBean().isValidActivation());
		if(this.getManagedBean().isValidActivation()) {
			log.info("*********Activation - is Valid**********");
			String personnelId = String.valueOf(this.getManagedBean().getSmPersonnel().getPersonnelId());
			String supplierId = String.valueOf(this.getManagedBean().getSmPersonnel().getSmAppl().getSmSupplier().getSupplierId());
			String encrpKeyStr = this.getManagedBean().getEncrpKeyStr();
			
			log.info("****activation key***" + this.getTempPassword());
			log.info("****pr***" + personnelId);
			log.info("****suppid***" + supplierId);
//			log.info("****password***" + password);
//			log.info("****confirm password***" + confirmPassword);
			
			//ensure password is not same as loginId
//			if(password.equalsIgnoreCase(loginId)){
//				throw new EpException(MessageConst.ERROR_721038);
//			}
			
			String keyStr = supplierId + personnelId + this.getTempPassword();
			String hashStr = SmCommonUtil.hashString(keyStr);
			log.info("****hashStr***" + hashStr);
			log.info("****EcrypgtStr***" + this.getManagedBean().getEncrpKeyStr());
			
			if(hashStr.equals(encrpKeyStr)){
				log.debug("**********Valid HASH***********");
				
				//Check whether profile is exist in PmOrgProfile
				OrgProfileReqDTO orgReqDTO = new OrgProfileReqDTO();
				orgReqDTO.setOrgProfileId(this.getManagedBean().getSmPersonnel().getSmAppl().getSmSupplier().getSupplierId());
				IOrgProfileSO orgProfileSO = (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject("pm", IOrgProfileSO.class);
				OrgProfileResDTO orgResDTO = orgProfileSO.retrieveOrgProfileByProfileId(orgReqDTO);
				
				if(orgResDTO.getPmOrgProfile() != null){
					//check Liferay UserName
					checkExistLiferayUsername(loginId);
					
					//create PmUser only
					log.info("--------Org Progile in not null------" + orgResDTO.getPmOrgProfile());
					
//					Long draftApplId = null;
//					IMofRegistrationSO mofRegistrationSO = null;
//					try {
//						mofRegistrationSO = (IMofRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm",IMofRegistrationSO.class);
//						MofRegistrationReqDTO mofRegistrationReqDTO = new MofRegistrationReqDTO();
//						mofRegistrationReqDTO.setSupplierId(Long.parseLong(supplierId));
//						MofRegistrationResDTO mofRegistrationResDTO = mofRegistrationSO.retrieveActiveSmApplBySupp(mofRegistrationReqDTO);
//						if(mofRegistrationResDTO.getSmAppl() != null)
//							draftApplId = mofRegistrationResDTO.getSmAppl().getApplId();
//					} finally {
//						
//					}
					
					SupplierUserActivationReqDTO reqDTO = new SupplierUserActivationReqDTO();		
//					reqDTO.setDraftApplId(draftApplId);
					reqDTO.setSmPersonnel(this.getManagedBean().getSmPersonnel());
					reqDTO.setLoginId(loginId);
					//reqDTO.setPassword(password);
//					reqDTO.setConfirmPassword(confirmPassword);
					System.out.println("Proceed to create user.............................");
					ISupplierUserActivationSO reqSO = (ISupplierUserActivationSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierUserActivationSO.class);
					log.info("*******>Personnel add - " + this.getManagedBean().getSmPersonnel().getSmAppl().getSmSupplier().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmState());
					SupplierUserActivationResDTO resDTO = new SupplierUserActivationResDTO();
					
					if(reqDTO.getSmPersonnel().getEpRole().equalsIgnoreCase("U")){
						reqDTO.getSmPersonnel().setEpRole(RoleCode.BASIC_SUPPLIER_USER.value());
					} else if(reqDTO.getSmPersonnel().getEpRole().equalsIgnoreCase("C")){
						reqDTO.getSmPersonnel().setEpRole(RoleCode.CM_USER.value());
					}
					
					
					try{
						resDTO = reqSO.createPmProfileForPersonnel(reqDTO);

						this.getManagedBean().setValidActivation(false);
//							this.getManagedBean().setSmPersonnel(null);
//							renewSesionObject();
						return navigateToAck(resDTO.getNewEpNo(),getManagedBean().getSmPersonnel().getSmAppl().getSupplierType());

					}catch(Exception ex){
						throw new EpException(ex.getMessage());
					}
				} else {
					//if(password.equals(confirmPassword)) {
						//check Liferay UserName
						checkExistLiferayUsername(loginId);
						
						//Create PmOrgProfile and PmUser
						SupplierUserActivationReqDTO reqDTO = new SupplierUserActivationReqDTO();					
						reqDTO.setSmPersonnel(this.getManagedBean().getSmPersonnel());
						//reqDTO.setPassword(password);
						reqDTO.setLoginId(loginId);
	//					reqDTO.setConfirmPassword(confirmPassword);
						
						//insert tracking diary
						reqDTO.setTrackingDiaryVO(SmCommonUtil.assignTrackingDiaryValues(
									 SupplierManagementConst.SUPP_ACCOUNT_ACTIVATION, getManagedBean().getSmPersonnel().getSmAppl().getApplId(), 
									 getManagedBean().getSmPersonnel().getSmAppl().getApplId().toString(), RoleCode.SUPPLIER_TEMP.value()));
						
						System.out.println("Proceed to create user.............................");
						ISupplierUserActivationSO reqSO = (ISupplierUserActivationSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierUserActivationSO.class);
						log.info("*******>Personnel add - " + this.getManagedBean().getSmPersonnel().getSmAppl().getSmSupplier().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmState());
						SupplierUserActivationResDTO resDTO = new SupplierUserActivationResDTO();
						
						//log.info("SmPersonnel code----->" + this.getManagedBean().getSmPersonnel().getIdentificationType().getParameterCode());
						log.info("SmPersonnel code----->" + this.getManagedBean().getSmPersonnel().getIdentificationTypeId());
						try{
							resDTO = reqSO.createPmProfile(reqDTO);
							this.getManagedBean().setValidActivation(false);
							//this.getManagedBean().setSmPersonnel(null);
							//renewSesionObject();
							return navigateToAck(resDTO.getNewEpNo(),getManagedBean().getSmPersonnel().getSmAppl().getSupplierType());
							
						}catch(Exception ex){
							log.info("activateSupplierUser>>>>>>" + ex.getMessage());
							log.info("Personnel ID>>>" + reqDTO.getSmPersonnel().getPersonnelId());
							throw new EpException(ex.getMessage());
						}
						
					/*} else {
						errorMsg("Invalid Password.");
					}*/
				}
			}else{
				log.info("Invalid Activation Key.");
				throw new EpException(MessageConst.ERROR_721031);
			}
		} else {
			log.info("*********Activation - Invalid**********");
			log.info("Invalid Request.");
			throw new EpException(MessageConst.ERROR_721032);
		}

		//return "/xhtml/sm/supplieruseractivation/enterActivationDetails.xhtml";
	
	}
	
	public void errorMsg(String errMsg){
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR,errMsg, errMsg));
	}
	
	public String navigateToAck(String epNo, String supplierType) {
		// FacesScopes fs = new FacesScopes();
		// String server = "http://" + fs.getRequest().getServerName() + ":" +
		// fs.getRequest().getServerPort();
		// String loginUrl = "<a href='" + server + "'>" + "login" + "</a>";
		
		String suppTypeLbl = null;
		
		if(supplierType.equalsIgnoreCase(SupplierManagementConst.STYPE_CONTRACTOR) || 
				supplierType.equalsIgnoreCase(SupplierManagementConst.STYPE_CONSULTANT)){
			suppTypeLbl = "lbl.sm.basic.account.mof";
		}else if(supplierType.equalsIgnoreCase(SupplierManagementConst.ACC_GOVT_SELLER)){
			suppTypeLbl = "lbl.sm.basic.account.govt";
		}else{
			suppTypeLbl = "lbl.sm.basic.account.basic";
		}
		
		String suppTypeLblParam = SmCommonUtil.getMessage(suppTypeLbl, null);
		
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, null, MessageUtils
						.getMessage(MessageConst.INFO_721001,
								new String[] { suppTypeLblParam, epNo },
								MessageUtils.BUNDLE_COMMON_MESSAGES,
								new FacesScopes().getUserLocale())));

		return CommonUIConst.ACKNOWLEDGE_PAGE;
	}
	
	public String naviToLogin(){
		FacesScopes fs = new FacesScopes();
		String server = "http://" + fs.getRequest().getServerName() + ":" + fs.getRequest().getServerPort();
		String url = server + SupplierManagementConst.LOGIN_URL;
		return url;
	}
	
	public String getCompanyId() {
		return EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.PORTAL_COMPANY_ID);
	}
	
	private void checkExistLiferayUsername(String userLoginId){
		try {
			if(UserLocalServiceUtil.getUserByEmailAddress(Long.valueOf(getCompanyId()), userLoginId) != null) {
				throw new EpException(MessageConst.ERROR_721015);
			}
			
		} catch (NumberFormatException e) {
			log.info(e.getMessage());
		} catch (PortalException e) {
			log.info(e.getMessage());
		} catch (SystemException e) {
			log.info(e.getMessage());
		}
	}
	

	// syazwan: added to validate if pending activation personnel already
	// deactivate/terminate in latest approved personnel
	private boolean isPersonnelAlreadyDeactivatedOrTerminates(
			SmPersonnel smPersonnel, Long supplierId) {
		boolean isAlreadyDeactivate = false;
		try {
			IPersonnelSO personnelSO = (IPersonnelSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							IPersonnelSO.class);
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setIdentificationNo(smPersonnel.getIdentificationNo());
			reqDTO.setSmSupplierId(supplierId);
			PersonnelResDTO resDTO = personnelSO
					.retrieveLatestApprovedPersonnelByIdentificationNoAndSupplierId(reqDTO);
			if (resDTO.getSmPersonnel() != null) {
				// already deactivated
				if (resDTO.getSmPersonnel().getAuthorizedFlg() != DbValueConst.RECORD_STATUS_ACTIVE
						&& StringUtils.isBlank(resDTO.getSmPersonnel()
								.getEpRole())) {
					isAlreadyDeactivate = true;
				}
			} else {
				// already terminated
				isAlreadyDeactivate = true;
			}
		} catch (Exception ex) {
			log.info("Error in isPersonnelAlreadyDeactivate:" + ex.getMessage());
		}
		return isAlreadyDeactivate;
	}

	// syazwan: added to check if eprole still BASIC or MOF
	private String getEpRole(SmPersonnel smPersonnel, SmSupplier smSupplier) {
		String epRole = smPersonnel.getEpRole();
		if (smSupplier.getSupplierId() != null
				&& smPersonnel.getEpRole() != null
				&& smPersonnel.getEpRole().equalsIgnoreCase(
						RoleCode.BASIC_SUPPLIER_USER.value())) {
			ISupplierSearchSO supplierSO = (ISupplierSearchSO) BusinessDelegate
					.getInstance().getServiceObject(
							ModuleCodeConst.SUPPLIER_MANAGEMENT,
							ISupplierSearchSO.class);
			SmSupplier smSupplierTmp = null;
			SupplierSearchReqDTO supplierSearchReqDTO = new SupplierSearchReqDTO();
			supplierSearchReqDTO.setSupplierId(smSupplier.getSupplierId());
			try {
				smSupplierTmp = supplierSO.retriveSupplierById(
						supplierSearchReqDTO).getSmSupplier();
				// means already MOF because have applNo
				// change BASIC_SUPPLIER_USER to MOF_SUPPLIER_USER
				if (smSupplierTmp.getSmAppl().getApplNo() != null
						&& StringUtils.isNotBlank(smSupplierTmp.getSmAppl()
								.getApplNo())) {
					epRole = RoleCode.MOF_SUPPLIER_USER.value();
				}
			} catch (Exception ex) {
				log.info(ex.getMessage());
			} finally {
				smSupplierTmp = null;
				supplierSearchReqDTO = null;
			}
		}
		return epRole;
	}
	
	// syazwan:added to check whether the activation email send is latest or
	// already override by new activation email
	private boolean isLatestActivationEmail(String createdTimeParam,
			SmPersonnel smPersonnel) {
		boolean retVal = true;
		try {
			if (createdTimeParam != null
					&& StringUtils.isNotBlank(createdTimeParam)) {
				log.info(">>>>>>>>>>>>>Inside comparing latest activation email for personnelId:"
						+ smPersonnel.getPersonnelId()
						+ " sendMailDate:"
						+ createdTimeParam
						+ " createdDate:"
						+ smPersonnel.getCreatedDate().getTime());
				Long createdTime = Long.parseLong(createdTimeParam);
				if (createdTime.compareTo(smPersonnel.getCreatedDate()
						.getTime()) != 0) {
					retVal = false;
				}
			}
		} catch (Exception ex) {
		}
		return retVal;
	}
	
	//redirect to invalid activation page
	private void redirectToInvalidActivationPage(FacesContext fc,String errMsg) {
		fc.addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, null,errMsg));
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,
						SmApplicationUtil.getApplicationErrorPage());
	}

	public String getTempPassword() {
		return tempPassword;
	}

	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
	}

	/*public String getPassword() {
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
	}*/

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	

}
