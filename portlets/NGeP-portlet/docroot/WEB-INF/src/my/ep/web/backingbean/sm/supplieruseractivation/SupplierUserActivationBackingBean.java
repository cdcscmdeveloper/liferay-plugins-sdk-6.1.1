package my.ep.web.backingbean.sm.supplieruseractivation;

import javax.faces.bean.ManagedBean;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
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


	public void prerenderSupplierActivation() {
		
		HttpServletRequest originalServletRequest = new FacesScopes().getOriginalServletRequest();
		String personnelId = originalServletRequest.getParameter("pr");
		String hashSupplierId = originalServletRequest.getParameter("sp");
		
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
	
	public String activateSupplierUser() {
		
		if(this.getManagedBean().isValidActivation()) {
			// validate temp password
			// proceed to create user into PM, Liferay, LDAP
			// display successful message or error message
		} else {
			// display error message
		}
		
		return "/xhtml/shared/acknowledge.xhtml";
	}
	
	public static void main(String[] args) {
		Date expiryDate = new Date(new Date().getTime() + (7 * 1000 * 60 * 60 * 24));
		long ggg = 30 * 60 * 60 * 24 * 1000;
		long hhh = ggg * 30;
		System.out.println("------::::" + 30 * 60 * 60 * 24);
		System.out.println("------::::" + hhh);
		
		System.out.println("expiryDate" + expiryDate);
		
		System.out.println(new Date().before(expiryDate));
		
		System.out.println(SmCommonUtil.hashString("315"));
	}
	
}
