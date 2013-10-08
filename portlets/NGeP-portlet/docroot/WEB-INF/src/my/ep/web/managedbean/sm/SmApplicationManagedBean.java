package my.ep.web.managedbean.sm;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplAssociateCompany;
import my.ep.persistence.entity.sm.SmApplContractExperience;
import my.ep.persistence.entity.sm.SmApplFacility;
import my.ep.persistence.entity.sm.SmApplProjectExperience;
import my.ep.persistence.entity.sm.SmApplSupplierBranch;
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

	public SmApplicationManagedBean() {
		selectedBranch = new SmApplSupplierBranch();
		selectedBranches = new ArrayList<SmApplSupplierBranch>();
		selectedAssocCompany = new SmApplAssociateCompany();
		selectedAssocCompanies = new ArrayList<SmApplAssociateCompany>();
		selectedCertList = new ArrayList<SmApplSupplierCert>();
		selectedCert = new SmApplSupplierCert();
		selectedContractProjs = new ArrayList<SmApplContractExperience>();
		selectedContractProj = new SmApplContractExperience();
		selectedConstProjs = new ArrayList<SmApplProjectExperience>();
		selectedConstProj = new SmApplProjectExperience();
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

}
