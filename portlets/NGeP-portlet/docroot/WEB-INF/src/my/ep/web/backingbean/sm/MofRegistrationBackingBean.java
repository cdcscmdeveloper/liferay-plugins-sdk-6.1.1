/**
 * 
 */
package my.ep.web.backingbean.sm;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.sm.CompanyBranchesBean;
import my.ep.web.bean.sm.MofRegistrationUtilBean;
import my.ep.web.bean.sm.PersonnelBean;
import my.ep.web.bean.sm.SupportingDocumentBean;
import my.ep.web.managedbean.sm.MofRegistrationManagedBean;

import org.primefaces.event.SelectEvent;

@ManagedBean
@SessionScoped
public class MofRegistrationBackingBean extends
		BaseBackingBean<MofRegistrationManagedBean> implements Cloneable {

	private List<SupportingDocumentBean> dataList;
	private List<MofRegistrationUtilBean> utilList;
	private List<PersonnelBean> personList;
	private List<PersonnelBean> authList;
	private List<PersonnelBean> signList;
	private List<CompanyBranchesBean> branchList;
	private CompanyBranchesBean selectedBranch;

	public MofRegistrationBackingBean() {
		selectedBranch = new CompanyBranchesBean();
		dataList = new ArrayList<SupportingDocumentBean>();
		utilList = new ArrayList<MofRegistrationUtilBean>();
		personList = new ArrayList<PersonnelBean>();
		authList = new ArrayList<PersonnelBean>();
		branchList = new ArrayList<CompanyBranchesBean>();
		prepareDataList();
		prepareUtilList();
		preparePersonList();
		preparAuthList();
		preapreBranches();
	}

	public List<SupportingDocumentBean> getDataList() {
		return dataList;
	}

	public void setDataList(List<SupportingDocumentBean> dataList) {
		this.dataList = dataList;
	}

	public List<MofRegistrationUtilBean> getUtilList() {
		return utilList;
	}

	public void setUtilList(List<MofRegistrationUtilBean> utilList) {
		this.utilList = utilList;
	}

	public List<PersonnelBean> getPersonList() {
		return personList;
	}

	public void setPersonList(List<PersonnelBean> personList) {
		this.personList = personList;
	}

	public List<PersonnelBean> getAuthList() {
		return authList;
	}

	public void setAuthList(List<PersonnelBean> authList) {
		this.authList = authList;
	}

	public List<PersonnelBean> getSignList() {
		return signList;
	}

	public void setSignList(List<PersonnelBean> signList) {
		this.signList = signList;
	}

	public List<CompanyBranchesBean> getBranchList() {
		return branchList;
	}

	public void setBranchList(List<CompanyBranchesBean> branchList) {
		this.branchList = branchList;
	}

	public CompanyBranchesBean getSelectedBranch() {
		return selectedBranch;
	}

	public void setSelectedBranch(CompanyBranchesBean selectedBranch) {
		this.selectedBranch = selectedBranch;
	}

	private List<SupportingDocumentBean> prepareDataList() {
		dataList.add(new SupportingDocumentBean(1, "Document2",
				"BORANG PENGISYTIHARAN KEMENTERIAN KEWANGAN YANG  ",
				Boolean.FALSE, "Document1", "Document1", "Y", "d",
				Boolean.FALSE, "w", 101099, "PERALATAN KOMUNIKASI",
				"ALAT-ALAT", "ALAT", "DISTRIBUTOR", "Normal", Boolean.FALSE,
				"Normal", "New Case Received ", "20 -02-2013"));
		dataList.add(new SupportingDocumentBean(2, "Document2",
				"BORANG PENGISYTIHARAN KEMENTERIAN KEWANGAN YANG  ",
				Boolean.FALSE, "Document1", "Document1", "Y", "d",
				Boolean.FALSE, "w", 101099, "PERALATAN KOMUNIKASI",
				"ALAT-ALAT", "ALAT", "DISTRIBUTOR", "Normal", Boolean.FALSE,
				"", "New Case Received ", "20 -02-2013"));
		return dataList;

	}

	private List<MofRegistrationUtilBean> prepareUtilList() {
		utilList.add(new MofRegistrationUtilBean(1, "Sample Name"));

		return utilList;
	}

	private List<PersonnelBean> preparePersonList() {
		personList
				.add(new PersonnelBean(1, "XXXX", Boolean.TRUE, Boolean.TRUE));
		personList.add(new PersonnelBean(2, "YYYY", Boolean.FALSE,
				Boolean.FALSE));
		personList
				.add(new PersonnelBean(3, "ZZZZ", Boolean.TRUE, Boolean.FALSE));

		return personList;

	}

	private List<PersonnelBean> preparAuthList() {
		authList.add(new PersonnelBean(1, "XXXX", Boolean.TRUE, Boolean.TRUE));

		authList.add(new PersonnelBean(3, "ZZZZ", Boolean.TRUE, Boolean.FALSE));

		return authList;

	}

	public List<PersonnelBean> getSignatoryList() {
		signList = new ArrayList<PersonnelBean>();
		signList.addAll(authList);
		System.out.println("ENTER THE ACTION CLASS");
		System.out.println("****** LIST ******" + signList.size());
		return signList;

	}

	public void onRowSelect(SelectEvent event) {
		System.out
				.println("*********** Enterd THE RoW SELECT METHOD ***********"
						+ getManagedBean().getSelecteDataList().size());
	}

	public List<CompanyBranchesBean> preapreBranches() {
		branchList.add(new CompanyBranchesBean(1, "Branch1", "Addr1"));
		branchList.add(new CompanyBranchesBean(2, "Branch2", "Addr2"));
		branchList.add(new CompanyBranchesBean(3, "Branch3", "Addr3"));
		branchList.add(new CompanyBranchesBean(4, "Branch4", "Addr4"));

		return branchList;
	}

	public void setSelectedBranchValue(CompanyBranchesBean branch) {

		System.out.println("****** Selected Bean Value *******"
				+ branch.getBranchName());
		setSelectedBranch(branch);

	}

	public void sendEnquiry(SupportingDocumentBean data) {
		if (!data.isDocumentValid()) {
			System.out.println("**** Select Value ***" + data.getRemarks());
			data.setRemarks(null);

		}

	}

}