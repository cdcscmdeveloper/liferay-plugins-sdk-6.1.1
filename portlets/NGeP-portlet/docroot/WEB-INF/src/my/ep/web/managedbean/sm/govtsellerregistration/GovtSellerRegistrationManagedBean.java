package my.ep.web.managedbean.sm.govtsellerregistration;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.sm.registration.RegistrationManagedBean;

@ManagedBean(name = "govtSellerMB")
@SessionScoped
public class GovtSellerRegistrationManagedBean extends RegistrationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8672731459327980724L;
	
	public String targetPage;

	public String getTargetPage() {
		return targetPage;
	}

	public void setTargetPage(String targetPage) {
		this.targetPage = targetPage;
	}

}
