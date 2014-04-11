package my.ep.web.managedbean.sm.supplieruseractivation;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "supplierUserActivationManagedBean")
@SessionScoped
public class SupplierUserActivationManagedBean extends BaseManagedBean {

	boolean validActivation;

	public boolean isValidActivation() {
		return validActivation;
	}

	public void setValidActivation(boolean validActivation) {
		this.validActivation = validActivation;
	}
	
}
