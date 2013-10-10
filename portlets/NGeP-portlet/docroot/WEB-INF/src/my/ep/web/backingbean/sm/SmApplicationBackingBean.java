package my.ep.web.backingbean.sm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

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
import my.ep.persistence.entity.sm.SmApplStaffTotal;
import my.ep.persistence.entity.sm.SmApplSupplierBranch;
import my.ep.persistence.entity.sm.SmApplSupplierCategory;
import my.ep.persistence.entity.sm.SmApplSupplierCert;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;

import org.apache.log4j.Logger;

@ManagedBean(name = "backingBean")
// @FacesConverter("mytest")
@RequestScoped
public class SmApplicationBackingBean extends
		BaseBackingBean<SmApplicationManagedBean> {

	public static Logger log = Logger.getLogger(SmApplicationBackingBean.class);

	private static final String PROCEED_MOF_REG = "mofRegBasePage";

	private int currentTab;
	private int outputTab;

	public SmApplicationBackingBean() {

	}

	SmAppl sm;

	public String proceedMofRegistration() {
		System.out.println("**** ENTER BACKIN BEAN *************");
		System.out.println("--- test ---"
				+ getManagedBean().getSelectedCategory().getPmCategoryL1()
						.getCategoryName());
		SmAppl appl = new SmAppl();
		appl.setApplId(12727);
		appl.setApplNo(" KN-01102013-0011");
		// 1- For Contractor
		// 2 - For Consultant
		appl.setSupplierType("2");
		List<SmApplStaffTotal> staffList = new ArrayList<SmApplStaffTotal>();
		if (appl.getSupplierType().equals("1")) {
			SmApplStaffTotal stf = new SmApplStaffTotal();
			stf.setStaffCategory("C");
			staffList.add(stf);
		} else if (appl.getSupplierType().equals("2")) {
			SmApplStaffTotal stf1 = new SmApplStaffTotal();
			stf1.setStaffCategory("P");
			staffList.add(stf1);
			SmApplStaffTotal stf2 = new SmApplStaffTotal();
			stf2.setStaffCategory("S");
			staffList.add(stf2);
			SmApplStaffTotal stf3 = new SmApplStaffTotal();
			stf3.setStaffCategory("L");
			staffList.add(stf3);
		}
		appl.setSmApplStaffTotals(staffList);
		appl.setApplType("NM");
		if (appl.getSmApplSupplierBranches() == null) {
			appl.setSmApplSupplierBranches(new ArrayList<SmApplSupplierBranch>());
		}
		if (appl.getSmApplAssociateCompanies() == null) {
			appl.setSmApplAssociateCompanies(new ArrayList<SmApplAssociateCompany>());
		}
		if (appl.getSmApplSupplierCerts() == null) {
			appl.setSmApplSupplierCerts(new ArrayList<SmApplSupplierCert>());
		}
		if (appl.getSmApplContractExperiences() == null) {
			appl.setSmApplContractExperiences(new ArrayList<SmApplContractExperience>());
		}
		if (appl.getSmApplProjectExperiences() == null) {
			appl.setSmApplProjectExperiences(new ArrayList<SmApplProjectExperience>());
		}
		if (appl.getSmApplSupplierCategories() == null) {
			appl.setSmApplSupplierCategories(new ArrayList<SmApplSupplierCategory>());
		}
		getManagedBean().setSmAppl(appl);
		getManagedBean().setTabActiveIndex(0);
		getManagedBean().setOfficeFacility(new SmApplFacility());
		System.out.println("**** ENTER BACKIN BEAN *************"
				+ getManagedBean().getSmAppl().getApplType());
		return PROCEED_MOF_REG;
	}

	public void updateSmAppl() throws Exception {
		try {
			System.out.println("**** ENTER ACTION METHOD BEAN *************"
					+ currentTab + "  " + outputTab);

			// sm.setApplId(13131);
			getManagedBean().setTabActiveIndex(outputTab);

		} catch (Exception e) {
			System.out.println("this is error");
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Sample error message",
							"PrimeFaces makes no mistakes"));
			getManagedBean().setTabActiveIndex(currentTab);
		}

	}

	public void setTabValue(int currentTab, int outputTab) {
		this.currentTab = currentTab;
		this.outputTab = outputTab;
	}

	public void officeFacilitySelected(AjaxBehaviorEvent e) {
		System.out.println(" ******* On Change Office Facility ***** "
				+ getManagedBean().getOfficeFacility().getOwnershipType());
		System.out.println(" **** Values In loop *********"
				+ getManagedBean().getOfficeFacility().getRentalRate());
	}

	// -- Process Branches Start
	public void addOrUpdateBranchToSmAppl() {

		if (getManagedBean().getSmAppl().getSmApplSupplierBranches()
				.contains(getManagedBean().getSelectedBranch())) {

			System.out.println(" **** THIS IS AN UPDATE For Branches******"
					+ getManagedBean().getSelectedBranch().getPhone());
		} else {
			getManagedBean().getSmAppl().getSmApplSupplierBranches()
					.add(getManagedBean().getSelectedBranch());
		}
	}

	public void assignSelBranch(SmApplSupplierBranch branchBean) {
		getManagedBean().setSelectedBranch(branchBean);
	}

	public void assignNewBranchBean() {
		getManagedBean().setSelectedBranch(new SmApplSupplierBranch());
		getManagedBean().getSelectedBranch().setPmAddress((setDefaultAddr()));
	}

	// -- Process Branches End

	// -- Process Associated Companies Start
	public void addOrUpdateAssocToSmAppl() {

		if (getManagedBean().getSmAppl().getSmApplAssociateCompanies()
				.contains(getManagedBean().getSelectedAssocCompany())) {

			System.out.println(" **** THIS IS AN UPDATE Assoc Companies******");
		} else {
			getManagedBean().getSmAppl().getSmApplAssociateCompanies()
					.add(getManagedBean().getSelectedAssocCompany());
		}
	}

	public void assignSelAssoc(SmApplAssociateCompany assocBean) {
		getManagedBean().setSelectedAssocCompany(assocBean);
	}

	public void assignNewAssocBean() {
		getManagedBean().setSelectedAssocCompany(new SmApplAssociateCompany());
	}

	// -- Process Associated Companies End

	// -- Process Certs Start
	public void addOrUpdateCertsToSmAppl() throws ParseException {
		if (getManagedBean().getSmAppl().getSmApplSupplierCerts()
				.contains(getManagedBean().getSelectedCert())) {

			System.out.println(" **** THIS IS AN UPDATE Certs******");
		} else {
			getManagedBean().getSmAppl().getSmApplSupplierCerts()
					.add(getManagedBean().getSelectedCert());
		}
	}

	public void assignSelCert(SmApplSupplierCert certBean) {
		getManagedBean().setSelectedCert(certBean);
	}

	public void assignNewCert() {
		getManagedBean().setSelectedCert(new SmApplSupplierCert());
	}

	// -- Process Certss End
	// -- Process Contract Projs Start
	public void addOrUpdateContractProjsToSmAppl() {
		if (getManagedBean().getSmAppl().getSmApplContractExperiences()
				.contains(getManagedBean().getSelectedContractProj())) {

			System.out
					.println(" **** THIS IS AN UPDATE FOR Contract Prjs******");
		} else {
			getManagedBean().getSmAppl().getSmApplContractExperiences()
					.add(getManagedBean().getSelectedContractProj());
		}
	}

	public void assignSelContractProj(SmApplContractExperience contractBean) {
		getManagedBean().setSelectedContractProj(contractBean);
	}

	public void assignNewContractProj() {
		getManagedBean()
				.setSelectedContractProj(new SmApplContractExperience());
	}

	// -- Process Contract Projs End

	// -- Process Consultant Projs Start
	public void addOrUpdateConsultantProjsToSmAppl() {
		if (getManagedBean().getSmAppl().getSmApplProjectExperiences()
				.contains(getManagedBean().getSelectedConstProj())) {

			System.out
					.println(" **** THIS IS AN UPDATE FOR Consultant Prjs******");
		} else {
			getManagedBean().getSmAppl().getSmApplProjectExperiences()
					.add(getManagedBean().getSelectedConstProj());
		}
	}

	public void assignSelConsultantProj(SmApplProjectExperience consultantBean) {
		getManagedBean().setSelectedConstProj(consultantBean);
	}

	public void assignNewConsultantProj() {
		getManagedBean().setSelectedConstProj(new SmApplProjectExperience());
	}

	// -- Process Consultant Projs End

	// -- Process Category Codes Start

	public void changeParentCategory(AjaxBehaviorEvent e) {
		String selPmCatL1 = getManagedBean().getSelectedParentCat();
		if (selPmCatL1 == null) {
			PmCategoryL1 p = new PmCategoryL1();
			p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
			getManagedBean().getSelectedCategory().setPmCategoryL1(p);
		} else {
			for (PmCategoryL1 p : getManagedBean().getDefaultParentCatList()) {
				if (p.getCateogryL1Code().equalsIgnoreCase(selPmCatL1)) {
					if (p.getPmCategoryL2s() == null) {
						p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
					}
					getManagedBean().getSelectedCategory().setPmCategoryL1(p);

				}
			}
		}
		PmCategoryL2 p2 = new PmCategoryL2();
		p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		getManagedBean().getSelectedCategory().setPmCategoryL2(
				new PmCategoryL2());
		getManagedBean().getSelectedCategory().setPmCategoryL3(
				new PmCategoryL3());
		getManagedBean().setSelectedChildCat(null);
		getManagedBean().setSelectedSubChildCat(null);

	}

	public void changeChildCategory(AjaxBehaviorEvent e) {
		String selPmCatL2 = getManagedBean().getSelectedChildCat();
		if (selPmCatL2 == null) {
			PmCategoryL2 p2 = new PmCategoryL2();
			p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
			getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
		} else {
			for (PmCategoryL2 p2 : getManagedBean().getSelectedCategory()
					.getPmCategoryL1().getPmCategoryL2s()) {
				if (p2.getCateogryL2Code().equalsIgnoreCase(selPmCatL2)) {
					if (p2.getPmCategoryL3s() == null) {
						p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
					}
					getManagedBean().getSelectedCategory().setPmCategoryL2(p2);

				}
			}
		}
		getManagedBean().getSelectedCategory().setPmCategoryL3(
				new PmCategoryL3());
		getManagedBean().setSelectedSubChildCat(null);

	}

	public void changeSubChildCategory(AjaxBehaviorEvent e) {
		String selPmCatL3 = getManagedBean().getSelectedSubChildCat();
		if (selPmCatL3 == null) {
			System.out.println(" --- Selected is a null --- ");
			PmCategoryL3 p3 = new PmCategoryL3();
			getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
		} else {
			for (PmCategoryL3 p3 : getManagedBean().getSelectedCategory()
					.getPmCategoryL2().getPmCategoryL3s()) {
				if (p3.getCateogryL3Code().equalsIgnoreCase(selPmCatL3)) {
					getManagedBean().getSelectedCategory().setPmCategoryL3(p3);

				}
			}
		}

	}

	public void changeCatEntryModeValues(AjaxBehaviorEvent e) {
		System.out.println("**** ENTER THE CHANGE OF CATEGORY TYPE **** "
				+ getManagedBean().getSelectedCategory().getEntryMode());
		resetSelectedCategoryParams();

	}

	/**
	 * resetting all the values
	 * 
	 * @param e
	 */
	private void resetSelectedCategoryParams() {
		getManagedBean().setSelectedSubChildCat(null);
		getManagedBean().setSelectedChildCat(null);
		getManagedBean().setSelectedParentCat(null);
		PmCategoryL1 p = new PmCategoryL1();
		p.setPmCategoryL2s(new ArrayList<PmCategoryL2>());
		getManagedBean().getSelectedCategory().setPmCategoryL1(p);
		PmCategoryL2 p2 = new PmCategoryL2();
		p2.setPmCategoryL3s(new ArrayList<PmCategoryL3>());
		getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
		PmCategoryL3 p3 = new PmCategoryL3();
		getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
	}

	public void assignNewCategoryCode() {
		getManagedBean().setSelectedCategory(new SmApplSupplierCategory());
		resetSelectedCategoryParams();
	}

	public void saveSelectedCategory() {
		System.out.println(" ******* Save From Selection ****"
				+ getManagedBean().getSelectedCategory().getCategoryType());
		getManagedBean().getSmAppl().getSmApplSupplierCategories()
				.add(getManagedBean().getSelectedCategory());
	}

	// TODO needs many validations and SO retrieve the PMcat1,2,3 details - To
	// be impl in future
	public void saveEnteredCategory() {
		System.out.println(" ******* Save From Entry ****"
				+ getManagedBean().getSelectedCategory().getCategoryType());
		PmCategoryL1 p = new PmCategoryL1();
		p.setCateogryL1Code(getManagedBean().getSelectedParentCat());
		p.setCategoryName("ParentSample");
		getManagedBean().getSelectedCategory().setPmCategoryL1(p);
		PmCategoryL2 p2 = new PmCategoryL2();
		p2.setCateogryL2Code(getManagedBean().getSelectedChildCat());
		p2.setCategoryName("ChildSample");
		getManagedBean().getSelectedCategory().setPmCategoryL2(p2);
		PmCategoryL3 p3 = new PmCategoryL3();
		p3.setCateogryL3Code(getManagedBean().getSelectedSubChildCat());
		p3.setCategoryName("SubChildSample");
		getManagedBean().getSelectedCategory().setPmCategoryL3(p3);
		getManagedBean().getSmAppl().getSmApplSupplierCategories()
				.add(getManagedBean().getSelectedCategory());

	}

	// -- Process Category Codes End
	private PmAddress setDefaultAddr() {
		PmAddress addr = new PmAddress();
		addr.setPmCity(new PmCity());
		addr.setPmCountry(new PmCountry());
		addr.setPmState(new PmState());
		addr.setPmDistrict(new PmDistrict());

		return addr;
	}

}
