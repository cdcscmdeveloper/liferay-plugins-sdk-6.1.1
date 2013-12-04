package my.ep.web.managedbean.sm;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

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
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.web.bean.sm.SvcTemplateBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;

import com.liferay.portal.model.Role;

@ManagedBean(name = "smApplMB")
@SessionScoped
public class SmApplicationManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SmAppl smAppl;

	private SmSupplier smSupplier;

	private int tabActiveIndex;

	private SmSupplierBranch selectedBranch;

	private List<SmSupplierBranch> selectedBranches;

	private SmAssociateCompany selectedAssocCompany;

	private List<SmAssociateCompany> selectedAssocCompanies;

	private List<SmSupplierCert> selectedCertList;

	private SmSupplierCert selectedCert;

	private List<SmContractExperience> selectedContractProjs;

	private SmContractExperience selectedContractProj;

	private List<SmProjectExperience> selectedConstProjs;

	private SmProjectExperience selectedConstProj;

	private String selectedParentCat;

	private String selectedChildCat;

	private String selectedSubChildCat;

	private List<PmCategoryL1> defaultParentCatList;

	private SmSupplierCategory selectedCategory;

	private SmAddress defaultAddr;

	private SmFacility officeFacility;

	private SmFacility selectedFactoryFacility;

	private List<SmFacility> selectedFactoryFacilities;

	private List<SmManufacturingDtl> selectedManufacturingList;

	private SmManufacturingDtl selectedManufacturer;

	private List<SmMachineDtl> selectedMachineList;

	private SmMachineDtl selectedMachine;

	private SmEquityCompany selectedEquityCompany;

	private List<SmEquityCompany> selectedEquityCompanies;

	private List<SmPersonnel> smapplShareHolders;

	private List<SmPersonnel> selectedShareHolders;

	private SmPersonnel selectedShareHolder;

	private SmPersonnelAddress shareHolderPermAddr;

	private SmPersonnelAddress shareHolderMailAddr;

	private SmStaffAcademic shareHolderAcademics;
	// Personnel Info
	private List<SmPersonnel> smapplAuthPersonnels;

	private List<SmPersonnel> selectedPersonnels;

	private SmPersonnel selectedPersonnel;

	private SmPersonnelAddress personnelPermAddr;

	private SmPersonnelAddress personnelMailAddr;

	private SmStaffAcademic personnelAcademics;
	// Bank Info
	private List<String> chequeSingNames;

	private SmSupplierBank selectedBank;

	private SmSupplierBank selectedSignatoryBankAcct;

	private List<SmSupplierBank> selectedBanks;

	private List<SmSupplierCategory> selectedCategories;

	private String delPendAckMsg;

	private boolean poUser;

	private boolean supplierUser;

	private boolean approverUser;

	private String decisionAckMsg;

	// TODO change the type later
	private List<SvcTemplateBean> selectedTemplateList;

	private List<SvcTemplateBean> templateList = new ArrayList<SvcTemplateBean>();

	private String[] bpmActions;

	private String selectedBpmAction;

	private String taskId;
	
	private String headerMsg;

	public SmApplicationManagedBean() {
		// TODO Checking the user role
		FacesScopes sc = new FacesScopes();
		System.out.println(" << ======= Login USer === >>  "
				+ sc.getUserScreenName());
		List<Role> loginUserRoles = sc.getUserRoleList();

		List<String> roleNames = new ArrayList<String>();
		for (Role r : loginUserRoles) {
			System.out.println(" Role ===  >> " + r.getName());
			roleNames.add(r.getName());
		}
		if (sc.hasRoleUser("UPS_PO_HQ")) {
			setPoUser(true);
			System.out.println("  *** LOGGED IN USER IS PO ****");
		} else if (sc.hasRoleUser("UPS_APPROVER_HQ")) {
			setApproverUser(true);
			System.out.println("  *** LOGGED IN USER IS APPROVER ****");
		} else if (sc.hasRoleUser("SUPPLIER_ADMIN")
				|| sc.hasRoleUser("SUPPLIER_USER")) {
			setSupplierUser(true);
			System.out.println("  *** LOGGED IN USER IS SUPPLIER ****");
		} else {
			System.out.println("  *** UNKNOWN USER ****");
		}
		// if (roleNames.contains("HQ_PO")) {
		// setPoUser(true);
		// } else if (roleNames.contains("HQ_REG_APPROVER")) {
		// setApproverUser(true);
		// } else if (roleNames.contains("SUPPLIER_ADMIN")
		// || roleNames.contains("SUPPLIER_USER")) {
		// setSupplierUser(true);
		//
		// } else {
		// System.out.println("****** UNKNOWN ROLE ****** ");
		// }

		System.out.println("PO USER === >> " + isPoUser());
		System.out.println("APPROVER USER === >> " + isApproverUser());
		System.out.println("NORMAL USER === >> " + isSupplierUser());

		defaultAddr = new SmAddress();
		defaultAddr.setPmCity(new PmCity());
		defaultAddr.setPmDistrict(new PmDistrict());
		defaultAddr.setPmCountry(new PmCountry());
		defaultAddr.setPmState(new PmState());

		selectedCategories = new ArrayList<SmSupplierCategory>();
		selectedBranch = new SmSupplierBranch();
		selectedBranch.setSmAddress(defaultAddr);
		selectedBranches = new ArrayList<SmSupplierBranch>();
		selectedAssocCompany = new SmAssociateCompany();
		selectedAssocCompany.setSmAddress(defaultAddr);
		selectedAssocCompanies = new ArrayList<SmAssociateCompany>();
		selectedEquityCompany = new SmEquityCompany();
		selectedEquityCompany.setSmAddress(defaultAddr);
		selectedEquityCompanies = new ArrayList<SmEquityCompany>();
		selectedCertList = new ArrayList<SmSupplierCert>();
		selectedCert = new SmSupplierCert();
		selectedContractProjs = new ArrayList<SmContractExperience>();
		selectedContractProj = new SmContractExperience();
		selectedConstProjs = new ArrayList<SmProjectExperience>();
		selectedConstProj = new SmProjectExperience();
		// For share holder personell
		selectedShareHolder = new SmPersonnel();
		smapplShareHolders = new ArrayList<SmPersonnel>();
		selectedShareHolders = new ArrayList<SmPersonnel>();
		shareHolderMailAddr = new SmPersonnelAddress();
		shareHolderMailAddr.setSmAddress(defaultAddr);
		shareHolderPermAddr = new SmPersonnelAddress();
		shareHolderPermAddr.setSmAddress(defaultAddr);
		selectedShareHolder
				.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		selectedShareHolder.getSmPersonnelAddresses().add(shareHolderMailAddr);
		selectedShareHolder.getSmPersonnelAddresses().add(shareHolderPermAddr);
		selectedShareHolder
				.setSmStaffAcademics(new ArrayList<SmStaffAcademic>());
		shareHolderAcademics = new SmStaffAcademic();
		selectedShareHolder.getSmStaffAcademics().add(shareHolderAcademics);

		// For share personell
		selectedPersonnel = new SmPersonnel();
		smapplAuthPersonnels = new ArrayList<SmPersonnel>();
		selectedPersonnels = new ArrayList<SmPersonnel>();
		personnelMailAddr = new SmPersonnelAddress();
		personnelMailAddr.setSmAddress(defaultAddr);
		personnelPermAddr = new SmPersonnelAddress();
		personnelPermAddr.setSmAddress(defaultAddr);
		selectedPersonnel
				.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		selectedPersonnel.getSmPersonnelAddresses().add(shareHolderMailAddr);
		selectedPersonnel.getSmPersonnelAddresses().add(shareHolderPermAddr);
		selectedPersonnel.setSmStaffAcademics(new ArrayList<SmStaffAcademic>());
		personnelAcademics = new SmStaffAcademic();
		selectedPersonnel.getSmStaffAcademics().add(personnelAcademics);

		//
		selectedFactoryFacility = new SmFacility();
		selectedFactoryFacility.setSmAddress(defaultAddr);
		selectedFactoryFacilities = new ArrayList<SmFacility>();
		selectedManufacturingList = new ArrayList<SmManufacturingDtl>();
		selectedManufacturer = new SmManufacturingDtl();
		selectedMachine = new SmMachineDtl();
		selectedMachineList = new ArrayList<SmMachineDtl>();

		//
		selectedSignatoryBankAcct = new SmSupplierBank();
		smAppl = new SmAppl();
		smAppl.setSmPersonnels(new ArrayList<SmPersonnel>());
		chequeSingNames = new ArrayList<String>();
		selectedBank = new SmSupplierBank();
		selectedBank.setPmbank(new PmBank());
		selectedBank.setSmAddress(defaultAddr);
		selectedBank.setSmChequeSignatories(new ArrayList<SmChequeSignatory>());
		selectedBanks = new ArrayList<SmSupplierBank>();

		// For CategoryCodes
		selectedCategory = new SmSupplierCategory();
		selectedCategory.setPmCategoryL1(new PmCategoryL1());
		selectedCategory.setPmCategoryL2(new PmCategoryL2());
		selectedCategory.setPmCategoryL3(new PmCategoryL3());
		// Dummy data start
		PmCategoryL3 sch1 = new PmCategoryL3();
		sch1.setCategoryName("Child 1.1.1");
		sch1.setCategoryL3Code("1");
		PmCategoryL3 sch2 = new PmCategoryL3();
		sch2.setCategoryName("Child 1.1.2");
		sch2.setCategoryL3Code("2");

		PmCategoryL3 sch3 = new PmCategoryL3();
		sch3.setCategoryName("Child 1.2.1");
		sch3.setCategoryL3Code("3");
		PmCategoryL3 sch4 = new PmCategoryL3();
		sch4.setCategoryName("Child 1.2.2");
		sch4.setCategoryL3Code("4");

		PmCategoryL3 sch5 = new PmCategoryL3();
		sch5.setCategoryName("Child 2.1.1");
		sch5.setCategoryL3Code("5");
		PmCategoryL3 sch6 = new PmCategoryL3();
		sch6.setCategoryName("Child 2.1.2");
		sch6.setCategoryL3Code("6");

		PmCategoryL3 sch7 = new PmCategoryL3();
		sch7.setCategoryName("Child 2.2.1");
		sch7.setCategoryL3Code("7");
		PmCategoryL3 sch8 = new PmCategoryL3();
		sch8.setCategoryName("Child 2.2.2");
		sch8.setCategoryL3Code("8");

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
		ch1.setCategoryL2Code("1");
		ch1.setPmCategoryL3s(c1list);

		PmCategoryL2 ch2 = new PmCategoryL2();
		ch2.setCategoryName("Child 1.2");
		ch2.setCategoryL2Code("2");
		ch2.setPmCategoryL3s(c2list);

		PmCategoryL2 ch3 = new PmCategoryL2();
		ch3.setCategoryName("Child 2.1");
		ch3.setCategoryL2Code("3");
		ch3.setPmCategoryL3s(c3list);

		PmCategoryL2 ch4 = new PmCategoryL2();
		ch4.setCategoryName("Child 2.2");
		ch4.setCategoryL2Code("4");
		ch4.setPmCategoryL3s(c4list);

		List<PmCategoryL2> p1list = new ArrayList<PmCategoryL2>();
		p1list.add(ch1);
		p1list.add(ch2);
		List<PmCategoryL2> p2list = new ArrayList<PmCategoryL2>();
		p2list.add(ch3);
		p2list.add(ch4);

		PmCategoryL1 p1 = new PmCategoryL1();
		p1.setCategoryName("Parent1");
		p1.setCategoryL1Code("1");
		p1.setPmCategoryL2s(p1list);

		PmCategoryL1 p2 = new PmCategoryL1();
		p2.setCategoryName("Parent2");
		p2.setCategoryL1Code("2");
		p2.setPmCategoryL2s(p2list);

		// Dummy data end

		defaultParentCatList = new ArrayList<PmCategoryL1>();
		defaultParentCatList.add(p1);
		defaultParentCatList.add(p2);
		// For CategoryCodes End

		prepareTemplateList();

		System.out.println(" ********* END MANAGED BEAN CREATION ****** ");
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

	public SmSupplierCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(SmSupplierCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public List<SmProjectExperience> getSelectedConstProjs() {
		return selectedConstProjs;
	}

	public void setSelectedConstProjs(
			List<SmProjectExperience> selectedConstProjs) {
		this.selectedConstProjs = selectedConstProjs;
	}

	public SmProjectExperience getSelectedConstProj() {
		return selectedConstProj;
	}

	public void setSelectedConstProj(SmProjectExperience selectedConstProj) {
		this.selectedConstProj = selectedConstProj;
	}

	public List<SmContractExperience> getSelectedContractProjs() {
		return selectedContractProjs;
	}

	public void setSelectedContractProjs(
			List<SmContractExperience> selectedContractProjs) {
		this.selectedContractProjs = selectedContractProjs;
	}

	public SmContractExperience getSelectedContractProj() {
		return selectedContractProj;
	}

	public void setSelectedContractProj(
			SmContractExperience selectedContractProj) {
		this.selectedContractProj = selectedContractProj;
	}

	public List<SmSupplierCert> getSelectedCertList() {
		return selectedCertList;
	}

	public void setSelectedCertList(List<SmSupplierCert> selectedCertList) {
		this.selectedCertList = selectedCertList;
	}

	public SmSupplierCert getSelectedCert() {
		return selectedCert;
	}

	public void setSelectedCert(SmSupplierCert selectedCert) {
		this.selectedCert = selectedCert;
	}

	public SmSupplierBranch getSelectedBranch() {
		return selectedBranch;
	}

	public void setSelectedBranch(SmSupplierBranch selectedBranch) {
		this.selectedBranch = selectedBranch;
	}

	public List<SmSupplierBranch> getSelectedBranches() {
		return selectedBranches;
	}

	public void setSelectedBranches(List<SmSupplierBranch> selectedBranches) {
		this.selectedBranches = selectedBranches;
	}

	public SmFacility getOfficeFacility() {
		return officeFacility;
	}

	public void setOfficeFacility(SmFacility officeFacility) {
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

	public SmAssociateCompany getSelectedAssocCompany() {
		return selectedAssocCompany;
	}

	public void setSelectedAssocCompany(SmAssociateCompany selectedAssocCompany) {
		this.selectedAssocCompany = selectedAssocCompany;
	}

	public List<SmAssociateCompany> getSelectedAssocCompanies() {
		return selectedAssocCompanies;
	}

	public void setSelectedAssocCompanies(
			List<SmAssociateCompany> selectedAssocCompanies) {
		this.selectedAssocCompanies = selectedAssocCompanies;
	}

	public SmAddress getDefaultAddr() {
		return defaultAddr;
	}

	public void setDefaultAddr(SmAddress defaultAddr) {
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

	public SmFacility getSelectedFactoryFacility() {
		return selectedFactoryFacility;
	}

	public void setSelectedFactoryFacility(SmFacility selectedFactoryFacility) {
		this.selectedFactoryFacility = selectedFactoryFacility;
	}

	public List<SmFacility> getSelectedFactoryFacilities() {
		return selectedFactoryFacilities;
	}

	public void setSelectedFactoryFacilities(
			List<SmFacility> selectedFactoryFacilities) {
		this.selectedFactoryFacilities = selectedFactoryFacilities;
	}

	public List<SmManufacturingDtl> getSelectedManufacturingList() {
		return selectedManufacturingList;
	}

	public void setSelectedManufacturingList(
			List<SmManufacturingDtl> selectedManufacturingList) {
		this.selectedManufacturingList = selectedManufacturingList;
	}

	public SmManufacturingDtl getSelectedManufacturer() {
		return selectedManufacturer;
	}

	public void setSelectedManufacturer(SmManufacturingDtl selectedManufacturer) {
		this.selectedManufacturer = selectedManufacturer;
	}

	public List<SmMachineDtl> getSelectedMachineList() {
		return selectedMachineList;
	}

	public void setSelectedMachineList(List<SmMachineDtl> selectedMachineList) {
		this.selectedMachineList = selectedMachineList;
	}

	public SmMachineDtl getSelectedMachine() {
		return selectedMachine;
	}

	public void setSelectedMachine(SmMachineDtl selectedMachine) {
		this.selectedMachine = selectedMachine;
	}

	public SmEquityCompany getSelectedEquityCompany() {
		return selectedEquityCompany;
	}

	public void setSelectedEquityCompany(SmEquityCompany selectedEquityCompany) {
		this.selectedEquityCompany = selectedEquityCompany;
	}

	public List<SmEquityCompany> getSelectedEquityCompanies() {
		return selectedEquityCompanies;
	}

	public void setSelectedEquityCompanies(
			List<SmEquityCompany> selectedEquityCompanies) {
		this.selectedEquityCompanies = selectedEquityCompanies;
	}

	public List<SmPersonnel> getSmapplShareHolders() {
		return smapplShareHolders;
	}

	public void setSmapplShareHolders(List<SmPersonnel> smapplShareHolders) {
		this.smapplShareHolders = smapplShareHolders;
	}

	public List<SmPersonnel> getSelectedShareHolders() {
		return selectedShareHolders;
	}

	public void setSelectedShareHolders(List<SmPersonnel> selectedShareHolders) {
		this.selectedShareHolders = selectedShareHolders;
	}

	public SmPersonnel getSelectedShareHolder() {
		return selectedShareHolder;
	}

	public void setSelectedShareHolder(SmPersonnel selectedShareHolder) {
		this.selectedShareHolder = selectedShareHolder;
	}

	public SmPersonnelAddress getShareHolderPermAddr() {
		return shareHolderPermAddr;
	}

	public void setShareHolderPermAddr(SmPersonnelAddress shareHolderPermAddr) {
		this.shareHolderPermAddr = shareHolderPermAddr;
	}

	public SmPersonnelAddress getShareHolderMailAddr() {
		return shareHolderMailAddr;
	}

	public void setShareHolderMailAddr(SmPersonnelAddress shareHolderMailAddr) {
		this.shareHolderMailAddr = shareHolderMailAddr;
	}

	public SmStaffAcademic getShareHolderAcademics() {
		return shareHolderAcademics;
	}

	public void setShareHolderAcademics(SmStaffAcademic shareHolderAcademics) {
		this.shareHolderAcademics = shareHolderAcademics;
	}

	public List<String> getChequeSingNames() {
		return chequeSingNames;
	}

	public void setChequeSingNames(List<String> chequeSingNames) {
		this.chequeSingNames = chequeSingNames;
	}

	public SmSupplierBank getSelectedSignatoryBankAcct() {
		return selectedSignatoryBankAcct;
	}

	public void setSelectedSignatoryBankAcct(
			SmSupplierBank selectedSignatoryBankAcct) {
		this.selectedSignatoryBankAcct = selectedSignatoryBankAcct;
	}

	public SmSupplierBank getSelectedBank() {
		return selectedBank;
	}

	public void setSelectedBank(SmSupplierBank selectedBank) {
		this.selectedBank = selectedBank;
	}

	public List<SmSupplierBank> getSelectedBanks() {
		return selectedBanks;
	}

	public void setSelectedBanks(List<SmSupplierBank> selectedBanks) {
		this.selectedBanks = selectedBanks;
	}

	public List<SmPersonnel> getSmapplAuthPersonnels() {
		return smapplAuthPersonnels;
	}

	public void setSmapplAuthPersonnels(List<SmPersonnel> smapplAuthPersonnels) {
		this.smapplAuthPersonnels = smapplAuthPersonnels;
	}

	public List<SmPersonnel> getSelectedPersonnels() {
		return selectedPersonnels;
	}

	public void setSelectedPersonnels(List<SmPersonnel> selectedPersonnels) {
		this.selectedPersonnels = selectedPersonnels;
	}

	public SmPersonnel getSelectedPersonnel() {
		return selectedPersonnel;
	}

	public void setSelectedPersonnel(SmPersonnel selectedPersonnel) {
		this.selectedPersonnel = selectedPersonnel;
	}

	public SmPersonnelAddress getPersonnelPermAddr() {
		return personnelPermAddr;
	}

	public void setPersonnelPermAddr(SmPersonnelAddress personnelPermAddr) {
		this.personnelPermAddr = personnelPermAddr;
	}

	public SmPersonnelAddress getPersonnelMailAddr() {
		return personnelMailAddr;
	}

	public void setPersonnelMailAddr(SmPersonnelAddress personnelMailAddr) {
		this.personnelMailAddr = personnelMailAddr;
	}

	public SmStaffAcademic getPersonnelAcademics() {
		return personnelAcademics;
	}

	public void setPersonnelAcademics(SmStaffAcademic personnelAcademics) {
		this.personnelAcademics = personnelAcademics;
	}

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public String getDelPendAckMsg() {
		return delPendAckMsg;
	}

	public void setDelPendAckMsg(String delPendAckMsg) {
		this.delPendAckMsg = delPendAckMsg;
	}

	public boolean isPoUser() {
		return poUser;
	}

	public void setPoUser(boolean poUser) {
		this.poUser = poUser;
	}

	public boolean isSupplierUser() {
		return supplierUser;
	}

	public void setSupplierUser(boolean supplierUser) {
		this.supplierUser = supplierUser;
	}

	public boolean isApproverUser() {
		return approverUser;
	}

	public void setApproverUser(boolean approverUser) {
		this.approverUser = approverUser;
	}

	public List<SvcTemplateBean> getSelectedTemplateList() {
		return selectedTemplateList;
	}

	public void setSelectedTemplateList(
			List<SvcTemplateBean> selectedTemplateList) {
		this.selectedTemplateList = selectedTemplateList;
	}

	public List<SvcTemplateBean> getTemplateList() {
		return templateList;
	}

	public void setTemplateList(List<SvcTemplateBean> templateList) {
		this.templateList = templateList;
	}

	public List<SmSupplierCategory> getSelectedCategories() {
		return selectedCategories;
	}

	public void setSelectedCategories(
			List<SmSupplierCategory> selectedCategories) {
		this.selectedCategories = selectedCategories;
	}

	public String[] getBpmActions() {
		return bpmActions;
	}

	public void setBpmActions(String[] bpmActions) {
		this.bpmActions = bpmActions;
	}

	public String getSelectedBpmAction() {
		return selectedBpmAction;
	}

	public void setSelectedBpmAction(String selectedBpmAction) {
		this.selectedBpmAction = selectedBpmAction;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getDecisionAckMsg() {
		return decisionAckMsg;
	}

	public void setDecisionAckMsg(String decisionAckMsg) {
		this.decisionAckMsg = decisionAckMsg;
	}

	public String getHeaderMsg() {
		return headerMsg;
	}

	public void setHeaderMsg(String headerMsg) {
		this.headerMsg = headerMsg;
	}

}
