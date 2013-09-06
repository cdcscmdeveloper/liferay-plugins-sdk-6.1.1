package my.ep.web.backingbean.sm.registration;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import org.apache.log4j.Logger;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;

@ManagedBean(name = "registrationBackingBean")
@RequestScoped
public class RegistrationBackingBean extends
		BaseBackingBean<RegistrationManagedBean> {
	public static boolean test = true;
	public static Logger log = Logger.getLogger(RegistrationManagedBean.class);
	public static String newICMasking = "999999999999";

	public RegistrationBackingBean() {

	}
	
	public void testRead(){
		log.info("----------testRead-------------"+ this.getManagedBean().getMofSuppType());
	}

	public String proceedToCompanyInfo() {
		if (this.getManagedBean().getBusinessType().equals("Ind")) {
			return "firstUserRegistration";
		} else {
			return "corporateAccount";
		}
	}

	public String proceedToFirstUserRegistration() {
		return "firstUserRegistration";
	}

	public void identificationTypeOnChange() {
		log.info("-----------------------identificationTypeOnChange---------------"
				+ this.getManagedBean().getUserIdentificationType());

		if (this.getManagedBean().getUserIdentificationType() == 0) {
			this.getManagedBean().setIdTypeMasking(newICMasking);
		} else {
			this.getManagedBean().setIdTypeMasking("");
		}
	}

	public void nationalityOnChange() {
		if (this.getManagedBean().getUserNationality() == 0) {
			this.getManagedBean().setUserIdentificationType(0);
			log.info("-----------------------nationalityOnChange---------------");
		}else if(this.getManagedBean().getUserNationality() == 1){
			this.getManagedBean().setUserIdentificationType(1);
		}
		//if (this.getManagedBean().getUserIdentificationType() == 0) {
			
			log.info("-----------------------nationalityOnChange---------------");
		//}
	}
}
/*
 * public String getRegistrationType(){ if(this.getManagedBean().getRegType() ==
 * "Basic"){
 * 
 * }else if(this.getManagedBean().getRegType() == "MOF"){
 * 
 * }
 * 
 * return ""; }
 */

