/**
 * 
 */
package my.ep.web.managedbean.sm;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.bean.sm.CompanyBranchesBean;
import my.ep.web.bean.sm.MofRegistrationUtilBean;
import my.ep.web.bean.sm.PersonnelBean;
import my.ep.web.bean.sm.SupportingDocumentBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean
@SessionScoped
public class MofRegistrationManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SmAppl smappl;

	public SmAppl getSmappl() {
		return smappl;
	}

	public void setSmappl(SmAppl smappl) {
		this.smappl = smappl;
	}

	public MofRegistrationManagedBean() {
	}

	private List<MofRegistrationUtilBean> selectedUtilList;
	private List<PersonnelBean> selectedPersonList;
	private List<SupportingDocumentBean> selecteDataList;
	private List<CompanyBranchesBean> selectedBranches;
	private List<SmSupplierCategory> selectedCategories;

	public List<PersonnelBean> getSelectedPersonList() {
		return selectedPersonList;
	}

	public void setSelectedPersonList(List<PersonnelBean> selectedPersonList) {
		this.selectedPersonList = selectedPersonList;
	}

	public List<MofRegistrationUtilBean> getSelectedUtilList() {
		return selectedUtilList;
	}

	public void setSelectedUtilList(
			List<MofRegistrationUtilBean> selectedUtilList) {
		this.selectedUtilList = selectedUtilList;
	}

	public List<SupportingDocumentBean> getSelecteDataList() {
		return selecteDataList;
	}

	public void setSelecteDataList(List<SupportingDocumentBean> selecteDataList) {
		this.selecteDataList = selecteDataList;
	}

	public List<CompanyBranchesBean> getSelectedBranches() {
		return selectedBranches;
	}

	public void setSelectedBranches(List<CompanyBranchesBean> selectedBranches) {
		this.selectedBranches = selectedBranches;
	}

	public List<SmSupplierCategory> getSelectedCategories() {
		return selectedCategories;
	}

	public void setSelectedCategories(
			List<SmSupplierCategory> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	/*
	 * public MofRegistrationManagedBean(List<Car> data) { super(data); }
	 */

}