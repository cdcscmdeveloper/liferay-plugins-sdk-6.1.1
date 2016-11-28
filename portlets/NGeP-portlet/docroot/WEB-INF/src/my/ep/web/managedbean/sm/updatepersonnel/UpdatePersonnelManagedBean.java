package my.ep.web.managedbean.sm.updatepersonnel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.sm.SmApplicationManagedBean;

@ManagedBean(name = "updatePersonnelMB")
@SessionScoped
public class UpdatePersonnelManagedBean extends SmApplicationManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2369634084956032415L;
	
	private int targetPage;

	public int getTargetPage() {
		return targetPage;
	}

	public void setTargetPage(int targetPage) {
		this.targetPage = targetPage;
	}

}
