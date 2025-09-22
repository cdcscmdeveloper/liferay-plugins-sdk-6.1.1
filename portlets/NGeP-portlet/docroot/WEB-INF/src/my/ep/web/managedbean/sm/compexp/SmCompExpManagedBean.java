package my.ep.web.managedbean.sm.compexp;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.managedbean.BaseManagedBean;

/**
 * @redmine #9299
 * Managed Bean for SM Company Experience
 */
@ManagedBean(name = "smCompExpMB")
@SessionScoped
public class SmCompExpManagedBean extends BaseManagedBean {
	
	private static final long serialVersionUID = 1L;
	private boolean agreeUpdateTnC;
	private boolean draftExist;

	public boolean isAgreeUpdateTnC() {
		return agreeUpdateTnC;
	}

	public void setAgreeUpdateTnC(boolean agreeUpdateTnC) {
		this.agreeUpdateTnC = agreeUpdateTnC;
	}

	public boolean isDraftExist() {
		return draftExist;
	}

	public void setDraftExist(boolean draftExist) {
		this.draftExist = draftExist;
	}

}
