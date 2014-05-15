package my.ep.web.backingbean.sm.supplieruseractivation;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.dto.sm.registration.RegistrationReqDTO;
import my.ep.dto.sm.registration.RegistrationResDTO;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.logging.LogKey;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.supplieruseractivation.SupplierUserActivationManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

import java.util.Date;

@ManagedBean(name="supplierUserActivationBackingBean")
public class SupplierUserActivationBackingBean extends
		BaseBackingBean<SupplierUserActivationManagedBean> {
	
	private static Logger log = Logger.getLogger(SupplierUserActivationBackingBean.class);

	private String tempPassword;
	
		
	public String getTempPassword() {
		return tempPassword;
	}

	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
	}

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
			PersonnelResDTO resDTO = personnelSO.retrievePersonnelById(reqDTO);
			
			if(resDTO.isSuccessful()) {
				personnel = resDTO.getSmPersonnel();
				supplierProfile = personnel.getSmAppl().getSmSupplier();
			}
		} finally {
			personnelSO = null;
		}
		log.info("hash supplier id: " + SmCommonUtil.hashString(String.valueOf(supplierProfile.getSupplierId())));
		
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
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.getApplication().getNavigationHandler().handleNavigation(fc, null,"/xhtml/sm/supplieruseractivation/enterActivationDetails.xhtml");
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
	}
	
	public void activateSupplierUser() {
		if(this.getManagedBean().isValidActivation()) {
//			HttpServletRequest originalServletRequest = new FacesScopes().getOriginalServletRequest();
			String personnelId = String.valueOf(this.getManagedBean().getSmPersonnel().getPersonnelId());
			String supplierId = String.valueOf(this.getManagedBean().getSmPersonnel().getSmAppl().getSmSupplier().getSupplierId());
			String encrpKeyStr = this.getManagedBean().getEncrpKeyStr();
			
			System.out.println("****temp pass***" + this.getTempPassword());
			System.out.println("****pr***" + personnelId);
			System.out.println("****suppid***" + supplierId);
			
			String keyStr = supplierId + personnelId + this.getTempPassword();
			String hashStr = SmCommonUtil.hashString(keyStr);
			
			System.out.println("****hashStr***" + hashStr);
			System.out.println("****EcrypgtStr***" + this.getManagedBean().getEncrpKeyStr());
			
			if(hashStr.equals(encrpKeyStr)){
				System.out.println("**********Valid HASH***********");
				RegistrationReqDTO reqDTO = new RegistrationReqDTO();
				reqDTO.setSmPersonnel(this.getManagedBean().getSmPersonnel());
				IRegistrationSO reqSO = (IRegistrationSO) BusinessDelegate.getInstance().getServiceObject("sm", IRegistrationSO.class);
				RegistrationResDTO resDTO = reqSO.createPmProfile(reqDTO);

				if(resDTO.isSuccessful()){
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO,
									"Your account has been updated successfully.", ""));
				}else{
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,"PM Creation Error.", ""));
				}
			}else{
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,"Invalid Temporary Password.", ""));
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,"isValidActivation.", ""));
		}
		
		//return "/xhtml/shared/acknowledge.xhtml";
	}
	
//	public static void main(String[] args) {
//		Date expiryDate = new Date(new Date().getTime() + (7 * 1000 * 60 * 60 * 24));
//		long ggg = 30 * 60 * 60 * 24 * 1000;
//		long hhh = ggg * 30;
//		System.out.println("------::::" + 30 * 60 * 60 * 24);
//		System.out.println("------::::" + hhh);
//		
//		System.out.println("expiryDate" + expiryDate);
//		
//		System.out.println(new Date().before(expiryDate));
//		
//		System.out.println(SmCommonUtil.hashString("315"));
//	}
	
}
