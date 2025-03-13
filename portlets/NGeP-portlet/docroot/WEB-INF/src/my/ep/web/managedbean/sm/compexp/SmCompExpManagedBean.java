package my.ep.web.managedbean.sm.compexp;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmCompExpDraft;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.web.managedbean.BaseManagedBean;

/**
 * ManagedBean for Sm Company Experience
 * 
 * @author firdaus.omar
 */
@ManagedBean(name = "compExpMB")
@SessionScoped
public class SmCompExpManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private SmSupplier supplier;
	private boolean agreeTnC;
	private List<SmCompExpDraft> smDraftList;
	private List<SmCompExpDraft> ctDraftList;
	private SmCompExpDraft selectedDraft;
	private boolean fromSM;
	private boolean fromCT;

	public SmSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(SmSupplier supplier) {
		this.supplier = supplier;
	}

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

	public SmCompExpDraft getSelectedDraft() {
		return selectedDraft;
	}

	public void setSelectedDraft(SmCompExpDraft selectedDraft) {
		this.selectedDraft = selectedDraft;
	}

	public boolean isFromSM() {
		return fromSM;
	}

	public void setFromSM(boolean fromSM) {
		this.fromSM = fromSM;
	}

	public boolean isFromCT() {
		return fromCT;
	}

	public void setFromCT(boolean fromCT) {
		this.fromCT = fromCT;
	}

}