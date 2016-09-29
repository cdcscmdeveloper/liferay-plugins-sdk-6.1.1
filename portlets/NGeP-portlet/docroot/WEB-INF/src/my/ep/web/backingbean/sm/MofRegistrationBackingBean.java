/**
 * 
 */
package my.ep.web.backingbean.sm;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.dto.sm.mofRegistration.MofRegistrationReqDTO;
import my.ep.dto.sm.mofRegistration.MofRegistrationResDTO;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.bean.sm.CompanyBranchesBean;
import my.ep.web.bean.sm.MofRegistrationUtilBean;
import my.ep.web.bean.sm.PersonnelBean;
import my.ep.web.bean.sm.SupportingDocumentBean;
import my.ep.web.managedbean.sm.MofRegistrationManagedBean;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

@ManagedBean
@RequestScoped
public class MofRegistrationBackingBean extends
		BaseBackingBean<MofRegistrationManagedBean> implements Cloneable {

	private List<SupportingDocumentBean> dataList;
	private List<MofRegistrationUtilBean> utilList;
	private List<PersonnelBean> personList;
	private List<PersonnelBean> authList;
	private List<PersonnelBean> signList;
	private List<CompanyBranchesBean> branchList;
	private CompanyBranchesBean selectedBranch;
	private String sampleText;

	public String getSampleText() {
		return sampleText;
	}

	public void setSampleText(String sampleText) {
		this.sampleText = sampleText;
	}

	public MofRegistrationBackingBean() {
		/**
		 * Sample Code For UI Purpose -start
		 */
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
		prepareSmAppl();
		/**
		 * Sample Code For UI Purpose -End
		 */

	}

	/**
	 * Sample Code For UI Purpose -start
	 */
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
		dataList.add(new SupportingDocumentBean(1, "Document2", "Desc1",
				Boolean.FALSE, "Document1", "Document1", "Y", "d",
				Boolean.FALSE, "w", 101099, "PERALATAN KOMUNIKASI",
				"ALAT-ALAT", "ALAT", "DISTRIBUTOR", "Normal", Boolean.FALSE,
				"Normal", "New Case Received ", "20 -02-2013"));
		dataList.add(new SupportingDocumentBean(2, "Document2", "Desc2  ",
				Boolean.FALSE, "Document1", "Document1", "Y", "d",
				Boolean.FALSE, "w", 101099, "PERALATAN KOMUNIKASI",
				"ALAT-ALAT", "ALAT", "DISTRIBUTOR", "Normal", Boolean.FALSE,
				"", "New Case Received ", "20 -02-2013"));
		return dataList;

	}

	private void prepareSmAppl() {
		SmAppl smAppl = new SmAppl();
		List<SmSupplierCategory> catList = new ArrayList<SmSupplierCategory>();
		for (int i = 0; i < 3; i++) {
			PmCategoryL1 parent = new PmCategoryL1();
			parent.setCategoryL1Code(String.valueOf(i + 1));
			parent.setCategoryName("Parent" + (i + 1));
			PmCategoryL2 clvl1 = new PmCategoryL2();
			clvl1.setCategoryL2Code(String.valueOf(i + 1));
			clvl1.setCategoryName("Child" + (i + 1));
			PmCategoryL3 sublvl1 = new PmCategoryL3();
			sublvl1.setCategoryL3Code(String.valueOf(i + 1));
			sublvl1.setCategoryName("Sub - Child" + (i + 1));
			SmSupplierCategory cat = new SmSupplierCategory();
			if (i % 3 == 0) {
				cat.setSpecialCategory(true);
			} else {
				cat.setSpecialCategory(false);
			}

			cat.setPmCategoryL1(parent);
			cat.setPmCategoryL2(clvl1);
			cat.setPmCategoryL3(sublvl1);
			catList.add(cat);

		}

		smAppl.setSmSupplierCategories(catList);
		getManagedBean().setSmappl(smAppl);
		// return smAppl;
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
		if (getManagedBean().getSelecteDataList().size() != 0) {
			for (int i = 0; i < getDataList().size(); i++) {
				SupportingDocumentBean sdbTemp = getDataList().get(i);
				for (SupportingDocumentBean sdb : getManagedBean()
						.getSelecteDataList()) {
					if (sdbTemp.equals(sdb)) {
						sdbTemp.setRecordSelected(Boolean.TRUE);
						System.out.println("*** ENTER *****");
						break;
					} else {
						sdbTemp.setRemarks("");
						sdbTemp.setRecordSelected(Boolean.FALSE);
					}
				}
				getDataList().set(i, sdbTemp);
				System.out.println("Data List For Index = " + i + "  is "
						+ getDataList().get(i).isRecordSelected());
			}

		} else {
			for (int i = 0; i < getDataList().size(); i++) {
				SupportingDocumentBean sdbTemp = getDataList().get(i);
				sdbTemp.setRemarks("");
				sdbTemp.setRecordSelected(Boolean.FALSE);
				getDataList().set(i, sdbTemp);
			}
		}
	}

	public void onRowUnSelect(UnselectEvent event) {
		System.out
				.println("*********** Enterd THE UN RoW SELECT METHOD ***********"
						+ getManagedBean().getSelecteDataList().size());
		if (getManagedBean().getSelecteDataList().size() != 0) {
			for (int i = 0; i < getDataList().size(); i++) {
				SupportingDocumentBean sdbTemp = getDataList().get(i);
				for (SupportingDocumentBean sdb : getManagedBean()
						.getSelecteDataList()) {
					if (sdbTemp.equals(sdb)) {
						sdbTemp.setRecordSelected(Boolean.TRUE);
						System.out.println("*** ENTER *****");
						break;
					} else {
						sdbTemp.setRemarks("");
						sdbTemp.setRecordSelected(Boolean.FALSE);
					}
				}
				getDataList().set(i, sdbTemp);
				System.out.println("Data List For Index = " + i + "  is "
						+ getDataList().get(i).isRecordSelected());
			}

		} else {
			for (int i = 0; i < getDataList().size(); i++) {
				SupportingDocumentBean sdbTemp = getDataList().get(i);
				sdbTemp.setRemarks("");
				sdbTemp.setRecordSelected(Boolean.FALSE);
				getDataList().set(i, sdbTemp);
			}
		}
	}

	public List<CompanyBranchesBean> preapreBranches() {
		branchList.add(new CompanyBranchesBean(1, "Branch1", "Addr1"));
		branchList.add(new CompanyBranchesBean(2, "Branch2", "Addr2"));
		branchList.add(new CompanyBranchesBean(3, "Branch3", "Addr3"));

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

	/**
	 * Sample Code For UI Purpose -End
	 */

	/**
	 * 
	 * Sample Connect To SOs - Start
	 * 
	 */

	public void retrieveSupplierProfile() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.retrieveSupplierProfile(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	public void createApplication() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.createApplication(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	public void updateApplication() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.updateApplication(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	public void submitApplication() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.submitApplication(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	public void updatePaymentStatus() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.updatePaymentStatus(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	public void createSMApplication() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.createSMApplication(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	public void retrieveLatestSupplierDetails() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.retrieveLatestSupplierDetails(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	public void retrieveQuestionList() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.retrieveQuestionList(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	public void submitAnswers() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.submitAnswers(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	public void retrieveCategoryCodes() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.retrieveCategoryCodes(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	public void deleteCategoryCodes() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.deleteCategoryCodes(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	public void retrievePendingApplicationDetails() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.retrievePendingApplicationDetails(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	public void deletePendingApplicationDetails() {
		/*IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.deletePendingApplicationDetails(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}*/
	}

	public void submitForApproval() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.submitForApproval(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	public void submitCancellationRequest() {
		IMofRegistrationSO mofRegistrationSO = null;
		try {
			mofRegistrationSO = BusinessDelegate.getInstance()
					.getServiceObject(IMofRegistrationSO.class);
			MofRegistrationReqDTO mofReqDTO = new MofRegistrationReqDTO();
			MofRegistrationResDTO mofResDTO = mofRegistrationSO
					.submitCancellationRequest(mofReqDTO);

		} finally {
			mofRegistrationSO = null;
		}
	}

	/**
	 * 
	 * Sample Connect To SOs - End
	 * 
	 */

}