package my.ep.web.managedbean.sm.compexp;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmCompExpDraft;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * @author firdaus.omar Redmine #9299
 */
@ManagedBean(name = "compExpMB")
@SessionScoped
public class CompExpManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private boolean agreeTnC;
	private List<SmCompExpDraft> smDraftList;
	private List<SmCompExpDraft> ctDraftList;
	private List<SmCompExpDraft> selectedDraft;

	public boolean isAgreeTnC() {
		return agreeTnC;
	}

	public void setAgreeTnC(boolean agreeTnC) {
		this.agreeTnC = agreeTnC;
	}

	public List<SmCompExpDraft> getSmDraftList() {
		return smDraftList;
	}

	public void setSmDraftList(List<SmCompExpDraft> smDraftList) {
		this.smDraftList = smDraftList;
	}

	public List<SmCompExpDraft> getCtDraftList() {
		return ctDraftList;
	}

	public void setCtDraftList(List<SmCompExpDraft> ctDraftList) {
		this.ctDraftList = ctDraftList;
	}

	public List<SmCompExpDraft> getSelectedDraft() {
		return selectedDraft;
	}

	public void setSelectedDraft(List<SmCompExpDraft> selectedDraft) {
		this.selectedDraft = selectedDraft;
	}

}