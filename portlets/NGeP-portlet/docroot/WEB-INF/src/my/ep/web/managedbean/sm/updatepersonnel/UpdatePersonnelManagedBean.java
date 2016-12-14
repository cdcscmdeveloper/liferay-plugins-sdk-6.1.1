package my.ep.web.managedbean.sm.updatepersonnel;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;

@ManagedBean(name = "updatePersonnelMB")
@SessionScoped
public class UpdatePersonnelManagedBean extends SmApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2369634084956032415L;
	private List<SmPersonnel> authorizedPersonnels;
	private List<SmPersonnel> normalPersonnels;

	public List<SmPersonnel> getAuthorizedPersonnels() {
		return authorizedPersonnels;
	}

	public void setAuthorizedPersonnels(List<SmPersonnel> authorizedPersonnels) {
		this.authorizedPersonnels = authorizedPersonnels;
	}

	public List<SmPersonnel> getNormalPersonnels() {
		return normalPersonnels;
	}

	public void setNormalPersonnels(List<SmPersonnel> normalPersonnels) {
		this.normalPersonnels = normalPersonnels;
	}

}
