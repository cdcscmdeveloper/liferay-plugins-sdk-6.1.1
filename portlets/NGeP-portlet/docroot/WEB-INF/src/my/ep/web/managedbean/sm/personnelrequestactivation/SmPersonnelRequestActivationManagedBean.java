package my.ep.web.managedbean.sm.personnelrequestactivation;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "requestActivationMB")
@SessionScoped
public class SmPersonnelRequestActivationManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4830297292356442553L;

	private SmSupplier smSupplier;
	private SmPersonnel selectedExpiredActivationPersonnel;
	private List<SmPersonnel> expiredActivationPersonnels;
	private String targetPage;

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public SmPersonnel getSelectedExpiredActivationPersonnel() {
		return selectedExpiredActivationPersonnel;
	}

	public void setSelectedExpiredActivationPersonnel(
			SmPersonnel selectedExpiredActivationPersonnel) {
		this.selectedExpiredActivationPersonnel = selectedExpiredActivationPersonnel;
	}

	public List<SmPersonnel> getExpiredActivationPersonnels() {
		return expiredActivationPersonnels;
	}

	public void setExpiredActivationPersonnels(
			List<SmPersonnel> expiredActivationPersonnels) {
		this.expiredActivationPersonnels = expiredActivationPersonnels;
	}

	public String getTargetPage() {
		return targetPage;
	}

	public void setTargetPage(String targetPage) {
		this.targetPage = targetPage;
	}

}
