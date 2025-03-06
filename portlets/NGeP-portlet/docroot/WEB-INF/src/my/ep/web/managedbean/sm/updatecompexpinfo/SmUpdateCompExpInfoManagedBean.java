package my.ep.web.managedbean.sm.updatecompexpinfo;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmRvAssistOfficer;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "updateCompExpInfoMB")
@SessionScoped
public class SmUpdateCompExpInfoManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private boolean agreeTC;
	private List<Object> smCompExpDrafList;
	private List<Object> ctCompExpDrafList;

	public boolean isAgreeTC() {
		return agreeTC;
	}

	public void setAgreeTC(boolean agreeTC) {
		this.agreeTC = agreeTC;
	}

	public List<Object> getSmCompExpDrafList() {
		return smCompExpDrafList;
	}

	public void setSmCompExpDrafList(List<Object> smCompExpDrafList) {
		this.smCompExpDrafList = smCompExpDrafList;
	}

	public List<Object> getCtCompExpDrafList() {
		return ctCompExpDrafList;
	}

	public void setCtCompExpDrafList(List<Object> ctCompExpDrafList) {
		this.ctCompExpDrafList = ctCompExpDrafList;
	}
	
}