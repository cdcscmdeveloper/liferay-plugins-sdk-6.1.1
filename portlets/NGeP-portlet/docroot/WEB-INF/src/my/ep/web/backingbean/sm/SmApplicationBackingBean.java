package my.ep.web.backingbean.sm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.AjaxBehaviorEvent;

import my.ep.persistence.entity.pm.common.PmBank;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmContractExperience;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmProjectExperience;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffTotal;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;

import org.apache.log4j.Logger;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "backingBean")
@FacesConverter("signa")
@RequestScoped
public class SmApplicationBackingBean extends
		BaseBackingBean<SmApplicationManagedBean> implements
		Converter{

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
		List<SmStaffTotal> staffList = new ArrayList<SmStaffTotal>();
		if (appl.getSupplierType().equals("1")) {
			SmStaffTotal stf = new SmStaffTotal();
			stf.setStaffCategory("C");
			staffList.add(stf);
		} else if (appl.getSupplierType().equals("2")) {
			SmStaffTotal stf1 = new SmStaffTotal();
			stf1.setStaffCategory("P");
			staffList.add(stf1);
			SmStaffTotal stf2 = new SmStaffTotal();
			stf2.setStaffCategory("S");
			staffList.add(stf2);
			SmStaffTotal stf3 = new SmStaffTotal();
			stf3.setStaffCategory("L");
			staffList.add(stf3);
		}
		appl.setSmStaffTotals(staffList);
		appl.setApplType("NM");
		if (appl.getSmSupplierBranches() == null) {
			appl.setSmSupplierBranches(new ArrayList<SmSupplierBranch>());
		}
		if (appl.getSmAssociateCompanies() == null) {
			appl.setSmAssociateCompanies(new ArrayList<SmAssociateCompany>());
		}
		if (appl.getSmSupplierCerts() == null) {
			appl.setSmSupplierCerts(new ArrayList<SmSupplierCert>());
		}
		if (appl.getSmContractExperiences() == null) {
			appl.setSmContractExperiences(new ArrayList<SmContractExperience>());
		}
		if (appl.getSmProjectExperiences() == null) {
			appl.setSmProjectExperiences(new ArrayList<SmProjectExperience>());
		}
		if (appl.getSmEquityCompanies() == null) {
			appl.setSmEquityCompanies(new ArrayList<SmEquityCompany>());
		}
		if (appl.getSmSupplierCategories() == null) {
			appl.setSmSupplierCategories(new ArrayList<SmSupplierCategory>());
		}
		if (appl.getSmFacilities() == null) {
			appl.setSmFacilities(new ArrayList<SmFacility>());
		}
		if (appl.getSmManufacturingDtls() == null) {
			appl.setSmManufacturingDtls(new ArrayList<SmManufacturingDtl>());
		}
		if (appl.getSmMachineDtls() == null) {
			appl.setSmMachineDtls(new ArrayList<SmMachineDtl>());
		}
		if (appl.getSmPersonnels() == null) {
			appl.setSmPersonnels(new ArrayList<SmPersonnel>());
		}
		if (appl.getSmSupplierBanks() == null) {
			appl.setSmSupplierBanks(new ArrayList<SmSupplierBank>());
		}
		getManagedBean().setSmAppl(appl);
		getManagedBean().setTabActiveIndex(0);
		getManagedBean().setOfficeFacility(new SmFacility());
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

		if (getManagedBean().getSmAppl().getSmSupplierBranches()
				.contains(getManagedBean().getSelectedBranch())) {

			System.out.println(" **** THIS IS AN UPDATE For Branches******"
					+ getManagedBean().getSelectedBranch().getSmAddress()
							.getPhoneNo());
		} else {
			getManagedBean().getSmAppl().getSmSupplierBranches()
					.add(getManagedBean().getSelectedBranch());
		}
	}

	public void assignSelBranch(SmSupplierBranch branchBean) {
		getManagedBean().setSelectedBranch(branchBean);
	}

	public void assignNewBranchBean() {
		getManagedBean().setSelectedBranch(new SmSupplierBranch());
		getManagedBean().getSelectedBranch().setSmAddress((setDefaultAddr()));
	}

	public void deleteSelectedBranches() {
		FacesContext.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Sample info message", "Delete Branch ::: "
										+ getManagedBean()
												.getSelectedBranches().size()));
		System.out.println("*** Delete Branches ******* "
				+ getManagedBean().getSelectedBranches().size());
	}

	// -- Process Branches End

	// -- Process Associated Companies Start
	public void addOrUpdateAssocToSmAppl() {

		if (getManagedBean().getSmAppl().getSmAssociateCompanies()
				.contains(getManagedBean().getSelectedAssocCompany())) {

			System.out.println(" **** THIS IS AN UPDATE Assoc Companies******");
		} else {
			getManagedBean().getSmAppl().getSmAssociateCompanies()
					.add(getManagedBean().getSelectedAssocCompany());
		}
	}

	public void assignSelAssoc(SmAssociateCompany assocBean) {
		getManagedBean().setSelectedAssocCompany(assocBean);
	}

	public void assignNewAssocBean() {
		getManagedBean().setSelectedAssocCompany(new SmAssociateCompany());
		getManagedBean().getSelectedAssocCompany().setSmAddress(
				(setDefaultAddr()));
	}

	// -- Process Associated Companies End

	// -- Process Certs Start
	public void addOrUpdateCertsToSmAppl() throws ParseException {
		if (getManagedBean().getSmAppl().getSmSupplierCerts()
				.contains(getManagedBean().getSelectedCert())) {

			System.out.println(" **** THIS IS AN UPDATE Certs******");
		} else {
			getManagedBean().getSmAppl().getSmSupplierCerts()
					.add(getManagedBean().getSelectedCert());
		}
	}

	public void assignSelCert(SmSupplierCert certBean) {
		getManagedBean().setSelectedCert(certBean);
	}

	public void assignNewCert() {
		getManagedBean().setSelectedCert(new SmSupplierCert());
	}

	// -- Process Certss End
	// -- Process Contract Projs Start
	public void addOrUpdateContractProjsToSmAppl() {
		if (getManagedBean().getSmAppl().getSmContractExperiences()
				.contains(getManagedBean().getSelectedContractProj())) {

			System.out
					.println(" **** THIS IS AN UPDATE FOR Contract Prjs******");
		} else {
			getManagedBean().getSmAppl().getSmContractExperiences()
					.add(getManagedBean().getSelectedContractProj());
		}
	}

	public void assignSelContractProj(SmContractExperience contractBean) {
		getManagedBean().setSelectedContractProj(contractBean);
	}

	public void assignNewContractProj() {
		getManagedBean().setSelectedContractProj(new SmContractExperience());
	}

	// -- Process Contract Projs End

	// -- Process Consultant Projs Start
	public void addOrUpdateConsultantProjsToSmAppl() {
		if (getManagedBean().getSmAppl().getSmProjectExperiences()
				.contains(getManagedBean().getSelectedConstProj())) {

			System.out
					.println(" **** THIS IS AN UPDATE FOR Consultant Prjs******");
		} else {
			getManagedBean().getSmAppl().getSmProjectExperiences()
					.add(getManagedBean().getSelectedConstProj());
		}
	}

	public void assignSelConsultantProj(SmProjectExperience consultantBean) {
		getManagedBean().setSelectedConstProj(consultantBean);
	}

	public void assignNewConsultantProj() {
		getManagedBean().setSelectedConstProj(new SmProjectExperience());
	}

	// -- Process Consultant Projs End

	// -- Process Equity Companies Start
	public void addOrUpdateEquityCompanyToSmAppl() {

		if (getManagedBean().getSmAppl().getSmEquityCompanies()
				.contains(getManagedBean().getSelectedEquityCompany())) {

			System.out
					.println(" **** THIS IS AN UPDATE Equity Companies******");
		} else {
			System.out.println("****** ADD EQUIRY ********* ");
			getManagedBean().getSmAppl().getSmEquityCompanies()
					.add(getManagedBean().getSelectedEquityCompany());
		}
	}

	public void assignSelEquiCompany(SmEquityCompany equiCompBean) {
		getManagedBean().setSelectedEquityCompany(equiCompBean);
	}

	public void assignNewEquiCompBean() {
		getManagedBean().setSelectedEquityCompany(new SmEquityCompany());
		getManagedBean().getSelectedEquityCompany().setSmAddress(
				(setDefaultAddr()));
	}

	// -- Process Equity Companies End

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
		getManagedBean().setSelectedCategory(new SmSupplierCategory());
		resetSelectedCategoryParams();
	}

	public void saveSelectedCategory() {
		System.out.println(" ******* Save From Selection ****"
				+ getManagedBean().getSelectedCategory().getCategoryType());
		getManagedBean().getSmAppl().getSmSupplierCategories()
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
		getManagedBean().getSmAppl().getSmSupplierCategories()
				.add(getManagedBean().getSelectedCategory());

	}

	// -- Process Category Codes End
	// factory start
	public void addOrUpdateFactoryToSmAppl() {

		if (getManagedBean().getSmAppl().getSmFacilities()
				.contains(getManagedBean().getSelectedFactoryFacility())) {

			System.out.println(" **** THIS IS AN UPDATE For Factoy******"
					+ getManagedBean().getSelectedFactoryFacility()
							.getSmAddress().getPhoneNo());
		} else {
			System.out.println("-- Before Save --"
					+ getManagedBean().getSelectedFactoryFacility()
							.getSmAddress().getAddress1());
			getManagedBean().getSmAppl().getSmFacilities()
					.add(getManagedBean().getSelectedFactoryFacility());
		}
	}

	public void assignSelFactory(SmFacility facilityBean) {
		getManagedBean().setSelectedFactoryFacility(facilityBean);
	}

	public void assignNewFactoryBean() {
		getManagedBean().setSelectedFactoryFacility(new SmFacility());
		getManagedBean().getSelectedFactoryFacility().setSmAddress(
				(setDefaultAddr()));
	}

	public void factoryOwnerTypeSel(AjaxBehaviorEvent e) {
		System.out.println("Selected Owner type === "
				+ getManagedBean().getSelectedFactoryFacility()
						.getOwnershipType());
	}

	// factory end

	// manufacturer start
	public void addOrUpdateManufacturerToSmAppl() {

		if (getManagedBean().getSmAppl().getSmManufacturingDtls()
				.contains(getManagedBean().getSelectedManufacturer())) {

			System.out.println(" **** THIS IS AN UPDATE For manufacturer******"
					+ getManagedBean().getSelectedManufacturer().getBrand());
		} else {

			getManagedBean().getSmAppl().getSmManufacturingDtls()
					.add(getManagedBean().getSelectedManufacturer());
		}
	}

	public void assignSelManufacturer(SmManufacturingDtl manBean) {
		getManagedBean().setSelectedManufacturer(manBean);
	}

	public void assignNewManufacturerBean() {
		getManagedBean().setSelectedManufacturer(new SmManufacturingDtl());
	}

	// manufacturer end

	// machinery start
	public void addOrUpdateMachineToSmAppl() {

		if (getManagedBean().getSmAppl().getSmMachineDtls()
				.contains(getManagedBean().getSelectedMachine())) {

			System.out.println(" **** THIS IS AN UPDATE For machinery ******"
					+ getManagedBean().getSelectedMachine().getBrandName());
		} else {

			getManagedBean().getSmAppl().getSmMachineDtls()
					.add(getManagedBean().getSelectedMachine());
		}
	}

	public void assignSelMachinery(SmMachineDtl macBean) {
		getManagedBean().setSelectedMachine(macBean);
	}

	public void assignNewMachineryBean() {
		getManagedBean().setSelectedMachine(new SmMachineDtl());
	}

	// manufacturer end

	// -- Process Share Holder Personnel Start
	public void addOrUpdateShareHolderToSmAppl() throws ParseException {
		if (getManagedBean().getSmAppl().getSmPersonnels()
				.contains(getManagedBean().getSelectedShareHolder())) {

			System.out.println(" **** THIS IS AN UPDATE Certs******");
		} else {
			// TODO Staff academics and addr settings start
			getManagedBean().getSelectedShareHolder().getSmPersonnelAddresses()
					.clear();
			getManagedBean().getSelectedShareHolder().getSmPersonnelAddresses()
					.add(getManagedBean().getShareHolderMailAddr());
			getManagedBean().getSelectedShareHolder().getSmPersonnelAddresses()
					.add(getManagedBean().getShareHolderPermAddr());
			getManagedBean().getSelectedShareHolder().getSmStaffAcademics()
					.clear();
			getManagedBean().getSelectedShareHolder().getSmStaffAcademics()
					.add(getManagedBean().getShareHolderAcademics());
			// TODO Staff academics and addr settings end

			getManagedBean().getSmAppl().getSmPersonnels()
					.add(getManagedBean().getSelectedShareHolder());
			getManagedBean().getSmapplShareHolders().add(
					getManagedBean().getSelectedShareHolder());
		}
	}

	public void assignSelShareHolder(SmPersonnel shareHolderBean) {
		for (SmPersonnelAddress addr : shareHolderBean
				.getSmPersonnelAddresses()) {
			if (addr.getAddressType().equalsIgnoreCase("I")) {
				getManagedBean().setShareHolderPermAddr(addr);
			}
			if (addr.getAddressType().equalsIgnoreCase("R")) {
				getManagedBean().setShareHolderMailAddr(addr);
			}
		}
		for (SmStaffAcademic stf : shareHolderBean.getSmStaffAcademics()) {
			getManagedBean().setShareHolderAcademics(stf);
		}
		getManagedBean().setSelectedShareHolder(shareHolderBean);
	}

	public void assignNewShareHolder() {
		System.out.println("******* ADD NEW SHARE *************");
		SmPersonnelAddress perm = new SmPersonnelAddress();
		perm.setAddressType("I");
		perm.setSmAddress(setDefaultAddr());
		getManagedBean().setShareHolderPermAddr(perm);

		SmPersonnelAddress mail = new SmPersonnelAddress();
		mail.setAddressType("R");
		mail.setSmAddress(setDefaultAddr());
		getManagedBean().setShareHolderMailAddr(mail);

		getManagedBean().setShareHolderAcademics(new SmStaffAcademic());

		SmPersonnel shr = new SmPersonnel();
		shr.setEquityOwner(true);
		shr.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		shr.setSmStaffAcademics(new ArrayList<SmStaffAcademic>());
		getManagedBean().setSelectedShareHolder(shr);
	}

	// -- Process Share Holder Personnel End

	// Process Bank Info Start
	public void addOrUpdateBankInfoToSmAppl() {
		// TODO tobe changed later
		getManagedBean().getTargetChequeList().clear();
		getManagedBean().getTargetChequeList().addAll(
				getManagedBean().getChequeSings().getTarget());
		System.out.println(" ***** AFTER SETTING TO TARGET ***********"
				+ getManagedBean().getChequeSings().getTarget().size());
		// for (SmPersonnel sm : getManagedBean().getTargetChequeList()) {
		// SmChequeSignatory ch = new SmChequeSignatory();
		// ch.setSmPersonnel(sm);
		// ch.setSmSupplierBank(getManagedBean().getSelectedBank());
		// getManagedBean().getSelectedBank().getSmChequeSignatories().add(ch);
		// }

		for (int i = 0; i < getManagedBean().getChequeSings().getTarget()
				.size(); i++) {
			SmChequeSignatory ch = new SmChequeSignatory();
			String sm = (getManagedBean().getChequeSings().getTarget().get(i)
					.getName());
			System.out.println(" *** AFTER SETTING IN LOOP " + i
					+ "   name == " + sm);
			ch.setSmSupplierBank(getManagedBean().getSelectedBank());
			getManagedBean().getSelectedBank().getSmChequeSignatories().add(ch);

		}
		System.out.println("** After Loop ** ");
		if (getManagedBean().getSmAppl().getSmSupplierBanks()
				.contains(getManagedBean().getSelectedBank())) {

			System.out.println(" **** THIS IS AN UPDATE Bank******");
		} else {
			System.out.println("Before add ===== "
					+ getManagedBean().getSelectedBank()
							.getSmChequeSignatories().size());
			getManagedBean().getSmAppl().getSmSupplierBanks()
					.add(getManagedBean().getSelectedBank());
		}
	}

	public void assignSelBank(SmSupplierBank shareBankBean) {

		getManagedBean().getTargetChequeList().clear();
		getManagedBean().setTargetChequeList(new ArrayList<SmPersonnel>());
		getManagedBean().getSourceChequeList().clear();
		getManagedBean().setSourceChequeList(
				getManagedBean().getSmAppl().getSmPersonnels());
		System.out.println("Before Loop source size ===== "
				+ getManagedBean().getSourceChequeList().size());
		System.out.println("Before Loop target size ===== "
				+ getManagedBean().getTargetChequeList().size());

		System.out.println("******* SIZE ********** "
				+ shareBankBean.getSmChequeSignatories().size());
		for (SmChequeSignatory ch : shareBankBean.getSmChequeSignatories()) {
			SmPersonnel pr = ch.getSmPersonnel();
			getManagedBean().getSourceChequeList().remove(pr);
			getManagedBean().getTargetChequeList().add(pr);

		}
		shareBankBean.getSmChequeSignatories().clear();
		System.out.println("After Loop source size ===== "
				+ getManagedBean().getSourceChequeList().size());
		System.out.println("After Loop target size ===== "
				+ getManagedBean().getTargetChequeList().size());
		getManagedBean().setChequeSings(
				new DualListModel<>(getManagedBean().getSourceChequeList(),
						getManagedBean().getTargetChequeList()));
		getManagedBean().setSelectedBank(shareBankBean);
	}

	public void assignNewBank() {
		System.out.println("******* ADD NEW Bank Info *************");
		getManagedBean().setTargetChequeList(new ArrayList<SmPersonnel>());
		getManagedBean().setSourceChequeList(
				getManagedBean().getSmAppl().getSmPersonnels());
		getManagedBean().setChequeSings(
				new DualListModel<>(getManagedBean().getSourceChequeList(),
						getManagedBean().getTargetChequeList()));
		SmSupplierBank bank = new SmSupplierBank();
		bank.setSmAddress(setDefaultAddr());
		bank.setPmbank(new PmBank());
		bank.setSmChequeSignatories(new ArrayList<SmChequeSignatory>());
		getManagedBean().setSelectedBank(bank);
	}

	// -- Process Bank Info End

	private SmAddress setDefaultAddr() {
		SmAddress addr = new SmAddress();
		addr.setPmCity(new PmCity());
		addr.setPmCountry(new PmCountry());
		addr.setPmState(new PmState());
		addr.setPmDistrict(new PmDistrict());

		return addr;
	}
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		// TODO Auto-generated method stub
		return ((SmPersonnel) arg2).getName();
	}
}
