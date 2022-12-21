/**
 * 
 */
package my.ep.web.managedbean.sm;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.bean.sm.CategoryCodeBean;
import my.ep.web.bean.sm.CompanyBranchesBean;
import my.ep.web.bean.sm.CompanyHeadOfficeBean;
import my.ep.web.bean.sm.FacilityBean;
import my.ep.web.bean.sm.SvcTemplateBean;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean
@SessionScoped
public class PoAssignToSvcManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<CompanyBranchesBean> selectedBranchList;

	private List<CompanyHeadOfficeBean> selectedHeadOfficeList;
	private List<FacilityBean> selectedFacilityList;
	private List<CategoryCodeBean> selectedCatCodeList;
	private List<SvcTemplateBean> selectedTemplateList;

	public PoAssignToSvcManagedBean() {
	}

	public List<CompanyBranchesBean> getSelectedBranchList() {
		return selectedBranchList;
	}

	public void setSelectedBranchList(
			List<CompanyBranchesBean> selectedBranchList) {
		this.selectedBranchList = selectedBranchList;
	}

	public List<CompanyHeadOfficeBean> getSelectedHeadOfficeList() {
		return selectedHeadOfficeList;
	}

	public void setSelectedHeadOfficeList(
			List<CompanyHeadOfficeBean> selectedHeadOfficeList) {
		this.selectedHeadOfficeList = selectedHeadOfficeList;
	}

	public List<CategoryCodeBean> getSelectedCatCodeList() {
		return selectedCatCodeList;
	}

	public void setSelectedCatCodeList(
			List<CategoryCodeBean> selectedCatCodeList) {
		this.selectedCatCodeList = selectedCatCodeList;
	}

	public List<SvcTemplateBean> getSelectedTemplateList() {
		return selectedTemplateList;
	}

	public void setSelectedTemplateList(
			List<SvcTemplateBean> selectedTemplateList) {
		this.selectedTemplateList = selectedTemplateList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public List<FacilityBean> getSelectedFacilityList() {
		return selectedFacilityList;
	}

	public void setSelectedFacilityList(List<FacilityBean> selectedFacilityList) {
		this.selectedFacilityList = selectedFacilityList;
	}

	/*
	 * public MofRegistrationManagedBean(List<Car> data) { super(data); }
	 */

}