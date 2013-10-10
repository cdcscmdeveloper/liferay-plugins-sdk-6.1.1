package my.ep.web.managedbean.sm;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmAddress;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplAssociateCompany;
import my.ep.persistence.entity.sm.SmApplContractExperience;
import my.ep.persistence.entity.sm.SmApplFacility;
import my.ep.persistence.entity.sm.SmApplProjectExperience;
import my.ep.persistence.entity.sm.SmApplSupplierBranch;
import my.ep.persistence.entity.sm.SmApplSupplierCategory;
import my.ep.persistence.entity.sm.SmApplSupplierCert;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "smApplMB")
@SessionScoped
public class SmApplicationManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SmAppl smAppl;

	private int tabActiveIndex;

	private SmApplSupplierBranch selectedBranch;

	private List<SmApplSupplierBranch> selectedBranches;

	private SmApplAssociateCompany selectedAssocCompany;

	private List<SmApplAssociateCompany> selectedAssocCompanies;

	private List<SmApplSupplierCert> selectedCertList;

	private SmApplSupplierCert selectedCert;

	private List<SmApplContractExperience> selectedContractProjs;

	private SmApplContractExperience selectedContractProj;

	private List<SmApplProjectExperience> selectedConstProjs;

	private SmApplProjectExperience selectedConstProj;

	private String selectedParentCat;

	private String selectedChildCat;

	private String selectedSubChildCat;

	private List<PmCategoryL1> defaultParentCatList;

	private SmApplSupplierCategory selectedCategory;

	private PmAddress defaultAddr;

	public SmApplicationManagedBean() {
		defaultAddr = new PmAddress();
		defaultAddr.setPmCity(new PmCity());
		defaultAddr.setPmDistrict(new PmDistrict());
		defaultAddr.setPmCountry(new PmCountry());
		defaultAddr.setPmState(new PmState());

		selectedBranch = new SmApplSupplierBranch();
		selectedBranch.setPmAddress(defaultAddr);
		selectedBranches = new ArrayList<SmApplSupplierBranch>();
		selectedAssocCompany = new SmApplAssociateCompany();
		selectedAssocCompanies = new ArrayList<SmApplAssociateCompany>();
		selectedCertList = new ArrayList<SmApplSupplierCert>();
		selectedCert = new SmApplSupplierCert();
		selectedContractProjs = new ArrayList<SmApplContractExperience>();
		selectedContractProj = new SmApplContractExperience();
		selectedConstProjs = new ArrayList<SmApplProjectExperience>();
		selectedConstProj = new SmApplProjectExperience();
		//
		selectedCategory = new SmApplSupplierCategory();
		selectedCategory.setPmCategoryL1(new PmCategoryL1());
		selectedCategory.setPmCategoryL2(new PmCategoryL2());
		selectedCategory.setPmCategoryL3(new PmCategoryL3());

		// For CategoryCodes

		// Dummy data start
		PmCategoryL3 sch1 = new PmCategoryL3();
		sch1.setCategoryName("Child 1.1.1");
		sch1.setCateogryL3Code("1");
		PmCategoryL3 sch2 = new PmCategoryL3();
		sch2.setCategoryName("Child 1.1.2");
		sch2.setCateogryL3Code("2");

		PmCategoryL3 sch3 = new PmCategoryL3();
		sch3.setCategoryName("Child 1.2.1");
		sch3.setCateogryL3Code("3");
		PmCategoryL3 sch4 = new PmCategoryL3();
		sch4.setCategoryName("Child 1.2.2");
		sch4.setCateogryL3Code("4");

		PmCategoryL3 sch5 = new PmCategoryL3();
		sch5.setCategoryName("Child 2.1.1");
		sch5.setCateogryL3Code("5");
		PmCategoryL3 sch6 = new PmCategoryL3();
		sch6.setCategoryName("Child 2.1.2");
		sch6.setCateogryL3Code("6");

		PmCategoryL3 sch7 = new PmCategoryL3();
		sch7.setCategoryName("Child 2.2.1");
		sch7.setCateogryL3Code("7");
		PmCategoryL3 sch8 = new PmCategoryL3();
		sch8.setCategoryName("Child 2.2.2");
		sch8.setCateogryL3Code("8");

		List<PmCategoryL3> c1list = new ArrayList<PmCategoryL3>();
		c1list.add(sch1);
		c1list.add(sch2);
		List<PmCategoryL3> c2list = new ArrayList<PmCategoryL3>();
		c2list.add(sch3);
		c2list.add(sch4);
		List<PmCategoryL3> c3list = new ArrayList<PmCategoryL3>();
		c3list.add(sch5);
		c3list.add(sch6);
		List<PmCategoryL3> c4list = new ArrayList<PmCategoryL3>();
		c4list.add(sch7);
		c4list.add(sch8);

		PmCategoryL2 ch1 = new PmCategoryL2();
		ch1.setCategoryName("Child 1.1");
		ch1.setCateogryL2Code("1");
		ch1.setPmCategoryL3s(c1list);

		PmCategoryL2 ch2 = new PmCategoryL2();
		ch2.setCategoryName("Child 1.2");
		ch2.setCateogryL2Code("2");
		ch2.setPmCategoryL3s(c2list);

		PmCategoryL2 ch3 = new PmCategoryL2();
		ch3.setCategoryName("Child 2.1");
		ch3.setCateogryL2Code("3");
		ch3.setPmCategoryL3s(c3list);

		PmCategoryL2 ch4 = new PmCategoryL2();
		ch4.setCategoryName("Child 2.2");
		ch4.setCateogryL2Code("4");
		ch4.setPmCategoryL3s(c4list);

		List<PmCategoryL2> p1list = new ArrayList<PmCategoryL2>();
		p1list.add(ch1);
		p1list.add(ch2);
		List<PmCategoryL2> p2list = new ArrayList<PmCategoryL2>();
		p2list.add(ch3);
		p2list.add(ch4);

		PmCategoryL1 p1 = new PmCategoryL1();
		p1.setCategoryName("Parent1");
		p1.setCateogryL1Code("1");
		p1.setPmCategoryL2s(p1list);

		PmCategoryL1 p2 = new PmCategoryL1();
		p2.setCategoryName("Parent2");
		p2.setCateogryL1Code("2");
		p2.setPmCategoryL2s(p2list);

		// Dummy data end

		defaultParentCatList = new ArrayList<PmCategoryL1>();
		defaultParentCatList.add(p1);
		defaultParentCatList.add(p2);
		// For CategoryCodes End
	}

	public SmApplSupplierCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(SmApplSupplierCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public List<SmApplProjectExperience> getSelectedConstProjs() {
		return selectedConstProjs;
	}

	public void setSelectedConstProjs(
			List<SmApplProjectExperience> selectedConstProjs) {
		this.selectedConstProjs = selectedConstProjs;
	}

	public SmApplProjectExperience getSelectedConstProj() {
		return selectedConstProj;
	}

	public void setSelectedConstProj(SmApplProjectExperience selectedConstProj) {
		this.selectedConstProj = selectedConstProj;
	}

	public List<SmApplContractExperience> getSelectedContractProjs() {
		return selectedContractProjs;
	}

	public void setSelectedContractProjs(
			List<SmApplContractExperience> selectedContractProjs) {
		this.selectedContractProjs = selectedContractProjs;
	}

	public SmApplContractExperience getSelectedContractProj() {
		return selectedContractProj;
	}

	public void setSelectedContractProj(
			SmApplContractExperience selectedContractProj) {
		this.selectedContractProj = selectedContractProj;
	}

	public List<SmApplSupplierCert> getSelectedCertList() {
		return selectedCertList;
	}

	public void setSelectedCertList(List<SmApplSupplierCert> selectedCertList) {
		this.selectedCertList = selectedCertList;
	}

	public SmApplSupplierCert getSelectedCert() {
		return selectedCert;
	}

	public void setSelectedCert(SmApplSupplierCert selectedCert) {
		this.selectedCert = selectedCert;
	}

	public SmApplSupplierBranch getSelectedBranch() {
		return selectedBranch;
	}

	public void setSelectedBranch(SmApplSupplierBranch selectedBranch) {
		this.selectedBranch = selectedBranch;
	}

	public List<SmApplSupplierBranch> getSelectedBranches() {
		return selectedBranches;
	}

	public void setSelectedBranches(List<SmApplSupplierBranch> selectedBranches) {
		this.selectedBranches = selectedBranches;
	}

	private SmApplFacility officeFacility;

	public SmApplFacility getOfficeFacility() {
		return officeFacility;
	}

	public void setOfficeFacility(SmApplFacility officeFacility) {
		this.officeFacility = officeFacility;
	}

	public int getTabActiveIndex() {
		return tabActiveIndex;
	}

	public void setTabActiveIndex(int tabActiveIndex) {
		this.tabActiveIndex = tabActiveIndex;
	}

	public SmAppl getSmAppl() {
		return smAppl;
	}

	public void setSmAppl(SmAppl smAppl) {
		this.smAppl = smAppl;
	}

	public SmApplAssociateCompany getSelectedAssocCompany() {
		return selectedAssocCompany;
	}

	public void setSelectedAssocCompany(
			SmApplAssociateCompany selectedAssocCompany) {
		this.selectedAssocCompany = selectedAssocCompany;
	}

	public List<SmApplAssociateCompany> getSelectedAssocCompanies() {
		return selectedAssocCompanies;
	}

	public void setSelectedAssocCompanies(
			List<SmApplAssociateCompany> selectedAssocCompanies) {
		this.selectedAssocCompanies = selectedAssocCompanies;
	}

	public PmAddress getDefaultAddr() {
		return defaultAddr;
	}

	public void setDefaultAddr(PmAddress defaultAddr) {
		this.defaultAddr = defaultAddr;
	}

	public String getSelectedParentCat() {
		return selectedParentCat;
	}

	public void setSelectedParentCat(String selectedParentCat) {
		this.selectedParentCat = selectedParentCat;
	}

	public String getSelectedChildCat() {
		return selectedChildCat;
	}

	public void setSelectedChildCat(String selectedChildCat) {
		this.selectedChildCat = selectedChildCat;
	}

	public String getSelectedSubChildCat() {
		return selectedSubChildCat;
	}

	public void setSelectedSubChildCat(String selectedSubChildCat) {
		this.selectedSubChildCat = selectedSubChildCat;
	}

	public List<PmCategoryL1> getDefaultParentCatList() {
		return defaultParentCatList;
	}

	public void setDefaultParentCatList(List<PmCategoryL1> defaultParentCatList) {
		this.defaultParentCatList = defaultParentCatList;
	}

}
