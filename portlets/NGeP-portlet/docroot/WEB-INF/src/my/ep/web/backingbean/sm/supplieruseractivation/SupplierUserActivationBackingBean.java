package my.ep.web.backingbean.sm.supplieruseractivation;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.supplieruseractivation.ISupplierUserActivationSO;
import my.ep.constant.MessageConst;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileReqDTO;
import my.ep.dto.pm.authorization.orgprofile.OrgProfileResDTO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.supplieruseractivation.SupplierUserActivationReqDTO;
import my.ep.dto.sm.supplieruseractivation.SupplierUserActivationResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.type.pm.RoleCode;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.CommonUIConst;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.supplieruseractivation.SupplierUserActivationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;


@ManagedBean(name="supplierUserActivationBackingBean")
public class SupplierUserActivationBackingBean extends
		BaseBackingBean<SupplierUserActivationManagedBean> {
	
	private static Logger log = Logger.getLogger(SupplierUserActivationBackingBean.class);

	private String tempPassword;
	
	private String password;
	private String confirmPassword;
	
		
	public SupplierUserActivationBackingBean(){
		if (MDC.get(LogKey.USER_ID) == null) {
			MDC.put(LogKey.USER_ID, 1L);
		}
	}

	public void prerenderSupplierActivation() {
		HttpServletRequest originalServletRequest = new FacesScopes().getOriginalServletRequest();
		String personnelId = originalServletRequest.getParameter("pr");
		String hashSupplierId = originalServletRequest.getParameter("sp");
		String encryptedKey = originalServletRequest.getParameter("kp");
		
		log.info("personnelId: " + personnelId);
		log.info("hashSupplierId: " + hashSupplierId);
		
		SmPersonnel personnel = null;
		SmSupplier supplierProfile = null;
		IPersonnelSO personnelSO = null;
		
		try {
			personnelSO = (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject("sm",IPersonnelSO.class);
			
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setPersonnelId(Long.valueOf(personnelId));
			PersonnelResDTO resDTO = personnelSO.retrieveValidActivationPersonnel(reqDTO);
			
			if(resDTO.getSmPersonnel() != null) {
				personnel = resDTO.getSmPersonnel();
				supplierProfile = personnel.getSmAppl().getSmSupplier();
			}
		} finally {
			personnelSO = null;
		}
		//log.info("hash supplier id: " + SmCommonUtil.hashString(String.valueOf(supplierProfile.getSupplierId())));
		
		FacesContext fc = FacesContext.getCurrentInstance();
		if(personnel != null && supplierProfile != null) {
			if(hashSupplierId.equals(SmCommonUtil.hashString(String.valueOf(supplierProfile.getSupplierId())))) {
				
				long applDateInMilis = personnel.getCreatedDate().getTime();
				long dayInMilis = 1000 * 60 * 60 * 24;

				Date expiryDate = new Date(applDateInMilis + (30 * dayInMilis));
				
				if(new Date().before(expiryDate)) { 
					this.getManagedBean().setValidActivation(true);
/*					this.getManagedBean().setPersonnelId(Long.parseLong(personnelId));
					this.getManagedBean().setSupplierId(supplierProfile.getSupplierId());*/
					this.getManagedBean().setSmPersonnel(personnel);
					
					this.getManagedBean().setEncrpKeyStr(encryptedKey);
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/supplieruseractivation/enterActivationDetails.xhtml");
					return;
					// display form to enter temp password
				} else {
					// redirect & display activation expired
					this.getManagedBean().setValidActivation(false);
				}
			} else {
				// redirect & display invalid activation
				this.getManagedBean().setValidActivation(false);
			}
		} else {
			// redirect & display invalid activation
			this.getManagedBean().setValidActivation(false);
		}
		
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/supplieruseractivation/prerenderSupplierUser.xhtmls");
	}
	
	public String activateSupplierUser() {
		if(this.getManagedBean().isValidActivation()) {
			String personnelId = String.valueOf(this.getManagedBean().getSmPersonnel().getPersonnelId());
			String supplierId = String.valueOf(this.getManagedBean().getSmPersonnel().getSmAppl().getSmSupplier().getSupplierId());
			String encrpKeyStr = this.getManagedBean().getEncrpKeyStr();
			
			log.info("****activation key***" + this.getTempPassword());
			log.info("****pr***" + personnelId);
			log.info("****suppid***" + supplierId);
			log.info("****password***" + password);
			log.info("****confirm password***" + confirmPassword);
			
			
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
					//create PmUser only
					log.info("--------Org Progile in not null------" + orgResDTO.getPmOrgProfile());
					SupplierUserActivationReqDTO reqDTO = new SupplierUserActivationReqDTO();					
					reqDTO.setSmPersonnel(this.getManagedBean().getSmPersonnel());
					reqDTO.setPassword(password);
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
					
					if(reqDTO.getSmPersonnel().getEpRole().equalsIgnoreCase(RoleCode.BASIC_SUPPLIER_USER.value()) || reqDTO.getSmPersonnel().getEpRole().equalsIgnoreCase(RoleCode.MOF_SUPPLIER_USER.value()) || reqDTO.getSmPersonnel().getEpRole().equalsIgnoreCase(RoleCode.CM_USER.value())){
						resDTO = reqSO.createPmProfileForPersonnel(reqDTO);
						if(resDTO.isSuccessful()){
							this.getManagedBean().setValidActivation(false);
							this.getManagedBean().setSmPersonnel(null);
							return navigateToAck();
						}else{
							throw new EpException(MessageConst.ERROR_721001);
						}
					}
				} else {
					
					if(password.equals(confirmPassword)) {
						//Create PmOrgProfile and PmUser
						SupplierUserActivationReqDTO reqDTO = new SupplierUserActivationReqDTO();					
						reqDTO.setSmPersonnel(this.getManagedBean().getSmPersonnel());
						reqDTO.setPassword(password);
	//					reqDTO.setConfirmPassword(confirmPassword);
						System.out.println("Proceed to create user.............................");
						ISupplierUserActivationSO reqSO = (ISupplierUserActivationSO) BusinessDelegate.getInstance().getServiceObject("sm", ISupplierUserActivationSO.class);
						log.info("*******>Personnel add - " + this.getManagedBean().getSmPersonnel().getSmAppl().getSmSupplier().getSmAppl().getSmCompanyBasic().getSmCompanyAddresses().get(0).getSmAddress().getPmState());
						SupplierUserActivationResDTO resDTO = new SupplierUserActivationResDTO();
						
						resDTO = reqSO.createPmProfile(reqDTO);
						if(resDTO.isSuccessful()){
							this.getManagedBean().setValidActivation(false);
							this.getManagedBean().setSmPersonnel(null);
							return navigateToAck();
						}else{
							throw new EpException(MessageConst.ERROR_721001);
						}
					} else {
						errorMsg("Invalid Password.");
					}
				}
			}else{
				log.info("Invalid Activation Key.");
				throw new EpException(MessageConst.ERROR_721031);
			}
		} else {
			log.info("Invalid Request.");
			throw new EpException(MessageConst.ERROR_721032);
		}
		
		return "/xhtml/sm/supplieruseractivation/enterActivationDetails.xhtml";
	}
	
	public void errorMsg(String errMsg){
		FacesContext.getCurrentInstance().addMessage(
				null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR,errMsg, errMsg));
	}
	
	public String navigateToAck(){
//		FacesScopes fs = new FacesScopes();
//		String server = "http://" + fs.getRequest().getServerName() + ":" + fs.getRequest().getServerPort();
		//String loginUrl = "<a href='" + server + "'>" + "login" + "</a>";
		FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_INFO,null,MessageUtils.getMessage(MessageConst.INFO_721001,
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

	public String getTempPassword() {
		return tempPassword;
	}

	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
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
	
}
