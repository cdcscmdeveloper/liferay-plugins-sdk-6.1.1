/**
 * 
 */
package my.ep.web.backingbean.sm;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.sm.CategoryCodeBean;
import my.ep.web.bean.sm.CompanyBranchesBean;
import my.ep.web.bean.sm.CompanyHeadOfficeBean;
import my.ep.web.bean.sm.FacilityBean;
import my.ep.web.bean.sm.SvcTemplateBean;
import my.ep.web.managedbean.sm.PoAssignToSvcManagedBean;

@ManagedBean
@SessionScoped
public class PoAssignToSvcBackingBean extends
		BaseBackingBean<PoAssignToSvcManagedBean> {

	private List<CompanyBranchesBean> branchList;
	private List<CompanyHeadOfficeBean> headOfficeList;
	private List<FacilityBean> facilityList;
	private List<CategoryCodeBean> catCodeList;
	private List<SvcTemplateBean> templateList;

	public PoAssignToSvcBackingBean() {
		branchList = new ArrayList<CompanyBranchesBean>();
		headOfficeList = new ArrayList<CompanyHeadOfficeBean>();
		facilityList = new ArrayList<FacilityBean>();
		catCodeList = new ArrayList<CategoryCodeBean>();
		templateList = new ArrayList<SvcTemplateBean>();
		preparebranchList();
		prepareHeadOffList();
		prepareCatList();
		prepareFacList();
		prepareTemplateList();
	}

	public List<CompanyBranchesBean> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<CompanyBranchesBean> branchList) {
		this.branchList = branchList;
	}

	public List<CompanyHeadOfficeBean> getHeadOfficeList() {
		return headOfficeList;
	}

	public void setHeadOfficeList(List<CompanyHeadOfficeBean> headOfficeList) {
		this.headOfficeList = headOfficeList;
	}

	public List<FacilityBean> getFacilityList() {
		return facilityList;
	}

	public void setFacilityList(List<FacilityBean> facilityList) {
		this.facilityList = facilityList;
	}

	public List<CategoryCodeBean> getCatCodeList() {
		return catCodeList;
	}

	public void setCatCodeList(List<CategoryCodeBean> catCodeList) {
		this.catCodeList = catCodeList;
	}

	public List<SvcTemplateBean> getTemplateList() {
		return templateList;
	}

	public void setTemplateList(List<SvcTemplateBean> templateList) {
		this.templateList = templateList;
	}

	private List<CompanyBranchesBean> preparebranchList() {
		branchList.add(new CompanyBranchesBean(1, "My Branch1",
				"Jln 2, Petaling Jaya,Malaysia "));
		branchList.add(new CompanyBranchesBean(2, "My Branch2",
				"Jln 2, Kuala Lumpur,Malaysia "));
		return branchList;

	}

	private List<CompanyHeadOfficeBean> prepareHeadOffList() {
		headOfficeList.add(new CompanyHeadOfficeBean(1,
				"Jln 123, CyberJaya,Selangor,Malaysia"));

		return headOfficeList;

	}

	private List<FacilityBean> prepareFacList() {
		facilityList.add(new FacilityBean(1, "Jln 2, Petaling Jaya,Malaysia"));
		facilityList.add(new FacilityBean(2, "Jln 3, Petaling Jaya,Malaysia"));
		facilityList.add(new FacilityBean(3, "Jln 4, Petaling Jaya,Malaysia"));

		return facilityList;
	}

	private List<CategoryCodeBean> prepareCatList() {

		catCodeList.add(new CategoryCodeBean(1, 100199, "CodeName1"));
		catCodeList.add(new CategoryCodeBean(1, 101199, "CodeName2"));
		catCodeList.add(new CategoryCodeBean(1, 102199, "CodeName3"));

		return catCodeList;
	}

	private List<SvcTemplateBean> prepareTemplateList() {
		templateList.add(new SvcTemplateBean(1, "Status Bumiputera"));
		templateList.add(new SvcTemplateBean(2, "Status Pembuatan"));
		templateList.add(new SvcTemplateBean(3, "Insurans"));
		templateList.add(new SvcTemplateBean(4, "Limbungan"));
		templateList.add(new SvcTemplateBean(5, "Percetaken"));
		templateList.add(new SvcTemplateBean(6, "Pengangkut"));

		return templateList;
	}

}