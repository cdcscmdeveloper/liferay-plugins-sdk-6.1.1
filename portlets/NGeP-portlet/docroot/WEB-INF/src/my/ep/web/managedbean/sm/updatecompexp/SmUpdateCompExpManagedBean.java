package my.ep.web.managedbean.sm.updatecompexp;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.sm.SmCompExpDraft;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "updateCompExpMB")
@SessionScoped
public class SmUpdateCompExpManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;
	
	private boolean agreeTC;
	private List<SmCompExpDraft> smCompExpDrafList;
	private List<SmCompExpDraft> ctCompExpDrafList;
	private SmCompExpDraft selectedCompExpDrafList;
	private PmParameter selectedOrgType;

	public boolean isAgreeTC() {
		return agreeTC;
	}

	public void setAgreeTC(boolean agreeTC) {
		this.agreeTC = agreeTC;
	}

	public List<SmCompExpDraft> getSmCompExpDrafList() {
		return smCompExpDrafList;
	}

	public void setSmCompExpDrafList(List<SmCompExpDraft> smCompExpDrafList) {
		this.smCompExpDrafList = smCompExpDrafList;
	}

	public List<SmCompExpDraft> getCtCompExpDrafList() {
		return ctCompExpDrafList;
	}

	public void setCtCompExpDrafList(List<SmCompExpDraft> ctCompExpDrafList) {
		this.ctCompExpDrafList = ctCompExpDrafList;
	}

	public SmCompExpDraft getSelectedCompExpDrafList() {
		return selectedCompExpDrafList;
	}

	public void setSelectedCompExpDrafList(SmCompExpDraft selectedCompExpDrafList) {
		this.selectedCompExpDrafList = selectedCompExpDrafList;
	}

	public PmParameter getSelectedOrgType() {
		return selectedOrgType;
	}

	public void setSelectedOrgType(PmParameter selectedOrgType) {
		this.selectedOrgType = selectedOrgType;
	}
}