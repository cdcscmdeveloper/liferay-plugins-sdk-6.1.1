package my.ep.web.managedbean.sm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmCompanyExperienceCat;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmPersonnelAddress;
import my.ep.persistence.entity.sm.SmRelatedCompany;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffExperience;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.web.bean.sm.SvcTemplateBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.vo.SmPersonnleCalcVO;

import com.liferay.portal.model.Role;

@ManagedBean(name = "smApplMB")
@SessionScoped
public class SmApplicationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	private SmAppl smAppl;
	private SmSupplier smSupplier;
	private int tabActiveIndex;
	private SmSupplierBranch selectedBranch;
	private SmSupplierBranch selectedBranchPrev;
	private int selectedBranchIndex;
	private List<SmSupplierBranch> selectedBranches;
	private SmAssociateCompany selectedAssocCompany;
	private List<SmAssociateCompany> selectedAssocCompanies;
	private List<SmSupplierCert> selectedCertList;
	private SmSupplierCert selectedCert;
	private List<SmAssociationBody> selectedAssocBodyList;
	private SmAssociationBody selectedAssocBody;
	private List<SmCompanyExperience> selectedCompanyExperiences;
	private SmCompanyExperience selectedCompanyExperience;
	private String selectedParentCat;
	private String selectedChildCat;
	private String selectedSubChildCat;
	private List<PmCategoryL1> defaultParentCatList;
	private SmSupplierCategory selectedCategory;
	private List<SmSupplierCategory> selectedCategories;
	private List<SmSupplierCategory> deleteCategoryList;
	private List<PmCategoryL1> supplierParentCatList;
	private List<PmCategoryL2> supplierChildCatList;
	private List<PmCategoryL3> supplierSubChildCatList;
	private List<PmCategoryL2> comExpChildCatList;
	private List<PmCategoryL3> comExpSubChildCatList;
	private SmAddress defaultAddr;
	private SmFacility selectedFactoryFacility;
	private List<SmFacility> selectedFactoryFacilities;
	private List<SmFacility> applFactoryFacilities;
	private SmFacility selectedWorkShopFacility;
	private List<SmFacility> selectedWorkShopFacilities;
	private List<SmFacility> applWorkShopFacilities;
	private SmFacility selectedDockyardFacility;
	private List<SmFacility> selectedDockyardFacilities;
	private List<SmFacility> applDockyardFacilities;
	private SmFacility selectedStoreFacility;
	private List<SmFacility> selectedStoreFacilities;
	private List<SmFacility> applStoreFacilities;
	private List<SmManufacturingDtl> selectedManufacturingList;
	private SmManufacturingDtl selectedManufacturer;
	private List<SmMachineDtl> selectedMachineList;
	private SmMachineDtl selectedMachine;
	private SmEquityCompany selectedEquityCompany;
	private List<SmEquityCompany> selectedEquityCompanies;
	private List<SmPersonnel> smapplShareHolders;
	private List<SmPersonnel> selectedShareHolders;
	private SmPersonnel selectedShareHolder;

	private List<SmStaffExperience> selectedShrHldrJobFunctions;
	private SmStaffExperience selectedJobFunction;
	private List<SmAssociationBody> selectedShrHldrProfBodies;
	private SmAssociationBody selectedStfPrfBdy;
	private List<SmRelatedCompany> selectedShrHldrRelatedCompanies;
	private SmRelatedCompany selectedRelatedCompany;
	private List<SmStaffAcademic> selectedShrHldrAcademics;
	private SmStaffAcademic selectedStaffAcademic;
	private boolean valueReqd;
	private boolean showParentMsg;
	private SmPersonnelAddress shareHolderPermAddr;
	private SmPersonnelAddress shareHolderMailAddr;
	private SmPersonnelAddress shareHolderResdAddr;
	private SmPersonnelAddress shareHolderOffAddr;
	private List<SmPersonnel> selectedPersonnels;
	private SmPersonnel selectedPersonnel;
	private SmPersonnel addrUpdatePersonnel;
	private List<SmStaffExperience> selectedPersJobFunctions;
	private List<SmAssociationBody> selectedPersProfBodies;
	private List<SmRelatedCompany> selectedPersRelatedCompanies;
	private List<SmStaffAcademic> selectedPersAcademics;
	private String supplierRemarks;

	private String businessTypeVal;
	private String personnelInfoHeader;
	// Bank Info
	private Long[] chequeSingPerId;
	private Long selBankBranchId;
	private SmSupplierBank selectedBank;
	private SmSupplierBank selectedSignatoryBankAcct;
	private List<SmSupplierBank> selectedBanks;
	private String delPendAckMsg;
	private boolean poUser;
	private boolean supplierUser;
	private boolean approverUser;
	private String decisionAckMsg;
	// TODO change the type later
	private List<SvcTemplateBean> selectedTemplateList;
	private String[] bpmActions;
	private String selectedBpmAction;
	private String taskId;
	private String headerMsg;
	private String poSiteVisitType;
	private boolean testBlock;
	private int maxReviewCount;
	//private int authPerCount;
	private SmApplSectionReview poSecReview;
	private SmApplSectionReview apprvrSecReview;
	private boolean termsAccepted;
	private String newSupplierType;
	private String catTypeConfirm;
	private SmFacility basicCompanyFacility;
	private SmApplSupportDoc supportDoc;
	// Address fields for all the forms
	private List<PmEduField> eduFieldList;
	private List<PmCountry> countryLs;
	private List<PmState> basicCompanyStates;
	private List<PmDistrict> basicCompanyDistricts;
	private List<PmCity> basicCompanyCities;

	private List<PmState> basicOffFacStates;
	private List<PmDistrict> basicOffFacDistricts;
	private List<PmCity> basicOffFacCities;

	private List<PmState> branchCompanyStates;
	private List<PmDistrict> branchCompanyDistricts;
	private List<PmCity> branchCompanyCities;

	private List<PmState> assocCompanyStates;
	private List<PmDistrict> assocCompanyDistricts;
	private List<PmCity> assocCompanyCities;

	private List<PmState> equiCompanyStates;
	private List<PmDistrict> equiCompanyDistricts;
	private List<PmCity> equiCompanyCities;

	private List<PmState> factoryFacilityStates;
	private List<PmDistrict> factoryFacilityDistricts;
	private List<PmCity> factoryFacilityCities;

	private List<PmState> dockFacilityStates;
	private List<PmDistrict> dockFacilityDistricts;
	private List<PmCity> dockFacilityCities;

	private List<PmState> workShopFacilityStates;
	private List<PmDistrict> workShopFacilityDistricts;
	private List<PmCity> workShopFacilityCities;

	private List<PmState> storeFacilityStates;
	private List<PmDistrict> storeFacilityDistricts;
	private List<PmCity> storeFacilityCities;

	private List<PmState> shrHldrPermStates;
	private List<PmDistrict> shrHldrPermDistricts;
	private List<PmCity> shrHldrPermCities;

	private List<PmState> shrHldrOffStates;
	private List<PmDistrict> shrHldrOffDistricts;
	private List<PmCity> shrHldrOffCities;

	private List<PmState> shrHldrMailStates;
	private List<PmDistrict> shrHldrMailDistricts;
	private List<PmCity> shrHldrMailCities;

	private List<PmState> shrHldrResdStates;
	private List<PmDistrict> shrHldrResdDistricts;
	private List<PmCity> shrHldrResdCities;

	private List<PmState> persnlPermStates;
	private List<PmDistrict> persnlPermDistricts;
	private List<PmCity> persnlPermCities;

	private List<PmState> persnlOffStates;
	private List<PmDistrict> persnlOffDistricts;
	private List<PmCity> persnlOffCities;

	private List<PmState> persnlMailStates;
	private List<PmDistrict> persnlMailDistricts;
	private List<PmCity> persnlMailCities;

	private List<PmState> persnlResdStates;
	private List<PmDistrict> persnlResdDistricts;
	private List<PmCity> persnlResdCities;

	private List<PmState> bankStates;
	private List<PmDistrict> bankDistricts;
	private List<PmCity> bankCities;

	private List<PmCertBody> certBodyLs;
	private List<PmCertBody> profBodyLs;
	private List<PmFinancialOrg> finOrgLs;
	private List<PmUom> uomLs;
	List<PmReasonDesc> reasonDescList;
	private List<PmStatusDesc> regStatusDescList;

	private String currentStatus;
	private Long[] reasonId;
	private SmPersonnleCalcVO personCalc;
	private BigDecimal equiPerCapTotal;
	private BigDecimal equiCompCapTotal;
	private boolean validResubmit;
	private boolean draftedAppl;
	private String currentPageType;
	private boolean redirectToPersonPage;
	private PmParameter perIdTypeParam;
	private String facilityType;
	private PmCountry defPmCountry;
	private List<SelectItem> ePRoleList;
	private boolean removeEpRole;
	private List<SmApplSupportDoc> missinDocs;
	private boolean reviewSiteVisit;
	private SelectItem[] businessTypeLs;
	private List<String> supplierToPOList;
	private String reasonDesc;
	private boolean validSmUserLimit;
	private boolean userWithRole;

	public List<String> getSupplierToPOList() {
		return supplierToPOList;
	}

	public void setSupplierToPOList(List<String> supplierToPOList) {
		this.supplierToPOList = supplierToPOList;
	}

	public SmApplicationManagedBean() {
		// TODO Checking the user role
		FacesScopes sc = new FacesScopes();
		System.out.println(" << ======= Login USer === >>  "
				+ sc.getUserScreenName());
		List<Role> loginUserRoles = sc.getUserRoleList();
		poSecReview = new SmApplSectionReview();
		apprvrSecReview = new SmApplSectionReview();
		List<String> roleNames = new ArrayList<String>();
		for (Role r : loginUserRoles) {
			System.out.println(" Role ===  >> " + r.getName());
			roleNames.add(r.getName());
		}
		if (sc.hasRoleUser(SupplierManagementConst.PO_HQ)
				|| sc.hasRoleUser(SupplierManagementConst.PO_HQ_TEMP)) {
			setPoUser(true);
			System.out.println("  *** LOGGED IN USER IS PO ****");
		} else if (sc.hasRoleUser(SupplierManagementConst.APPROVER_HQ)
				|| sc.hasRoleUser(SupplierManagementConst.APPROVER_HQ_TEMP)
				|| sc.hasRoleUser(SupplierManagementConst.APPROVER)) {
			setApproverUser(true);
			System.out.println("  *** LOGGED IN USER IS APPROVER ****");
		} else if (sc.hasRoleUser(SupplierManagementConst.BASIC_SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.BASIC_SUPPLIER_USER)
				|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_USER)
				|| sc.hasRoleUser(SupplierManagementConst.SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.SUPPLIER_USER)) {
			setSupplierUser(true);
			System.out.println("  *** LOGGED IN USER IS SUPPLIER ****");
		} else {
			System.out.println("  *** UNKNOWN USER ****");
		}
		// if (sc.getUserScreenName().equalsIgnoreCase("user026")) {
		// setPoUser(true);
		// }

		System.out.println("PO USER === >> " + isPoUser());
		System.out.println("APPROVER USER === >> " + isApproverUser());
		System.out.println("NORMAL USER === >> " + isSupplierUser());
		defaultAddr = new SmAddress();
		defaultAddr.setPmCity(new PmCity());
		defaultAddr.setPmDistrict(new PmDistrict());
		// defaultAddr.setPmCountry(new PmCountry());
		defaultAddr
				.setPmCountry(SmCommonUtil
						.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE));
		// defaultAddr.getPmCountry().setCountryId(1l);
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
		selectedCert.setPmCertBody(new PmCertBody());
		selectedAssocBodyList = new ArrayList<SmAssociationBody>();
		selectedAssocBody = new SmAssociationBody();
		selectedAssocBody.setPmCertBody(new PmCertBody());
		selectedCompanyExperiences = new ArrayList<SmCompanyExperience>();
		selectedCompanyExperience = new SmCompanyExperience();
		selectedCompanyExperience
				.setSmCompanyExperienceCats(new ArrayList<SmCompanyExperienceCat>());
		selectedCompanyExperience.getSmCompanyExperienceCats().add(
				setDefaulatExpCatVal());
		// selectedConstProjs = new ArrayList<SmProjectExperience>();
		// selectedConstProj = new SmProjectExperience();
		// For share holder personell
		selectedShareHolder = new SmPersonnel();
		selectedShareHolder.setPmCountry(new PmCountry());
		smapplShareHolders = new ArrayList<SmPersonnel>();
		selectedShareHolders = new ArrayList<SmPersonnel>();
		selectedShrHldrAcademics = new ArrayList<SmStaffAcademic>();
		selectedShrHldrJobFunctions = new ArrayList<SmStaffExperience>();
		selectedShrHldrProfBodies = new ArrayList<SmAssociationBody>();
		selectedShrHldrRelatedCompanies = new ArrayList<SmRelatedCompany>();
		shareHolderMailAddr = new SmPersonnelAddress();
		shareHolderMailAddr.setSmAddress(defaultAddr);
		shareHolderPermAddr = new SmPersonnelAddress();
		shareHolderPermAddr.setSmAddress(defaultAddr);
		shareHolderOffAddr = new SmPersonnelAddress();
		shareHolderOffAddr.setSmAddress(defaultAddr);
		shareHolderResdAddr = new SmPersonnelAddress();
		shareHolderResdAddr.setSmAddress(defaultAddr);
		selectedShareHolder
				.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());
		// selectedShareHolder.getSmPersonnelAddresses().add(shareHolderMailAddr);
		// selectedShareHolder.getSmPersonnelAddresses().add(shareHolderPermAddr);
		selectedShareHolder
				.setSmStaffAcademics(new ArrayList<SmStaffAcademic>());
		// shareHolderAcademics = new SmStaffAcademic();
		// selectedShareHolder.getSmStaffAcademics().add(shareHolderAcademics);

		// For share personell
		selectedPersonnel = new SmPersonnel();
		selectedPersonnel.setPmCountry(new PmCountry());
		// smapplAuthPersonnels = new ArrayList<SmPersonnel>();
		selectedPersonnels = new ArrayList<SmPersonnel>();
		selectedPersAcademics = new ArrayList<SmStaffAcademic>();
		selectedPersJobFunctions = new ArrayList<SmStaffExperience>();
		selectedPersProfBodies = new ArrayList<SmAssociationBody>();
		selectedPersRelatedCompanies = new ArrayList<SmRelatedCompany>();

		selectedPersonnel
				.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());

		selectedFactoryFacility = new SmFacility();
		selectedFactoryFacility.setSmAddress(defaultAddr);
		selectedFactoryFacilities = new ArrayList<SmFacility>();
		applFactoryFacilities = new ArrayList<SmFacility>();
		selectedWorkShopFacility = new SmFacility();
		selectedWorkShopFacility.setSmAddress(defaultAddr);
		selectedWorkShopFacilities = new ArrayList<SmFacility>();
		applWorkShopFacilities = new ArrayList<SmFacility>();
		selectedDockyardFacility = new SmFacility();
		selectedDockyardFacility.setSmAddress(defaultAddr);
		selectedDockyardFacilities = new ArrayList<SmFacility>();
		applDockyardFacilities = new ArrayList<SmFacility>();
		selectedStoreFacility = new SmFacility();
		selectedStoreFacility.setSmAddress(defaultAddr);
		selectedStoreFacilities = new ArrayList<SmFacility>();
		applStoreFacilities = new ArrayList<SmFacility>();
		selectedManufacturingList = new ArrayList<SmManufacturingDtl>();
		selectedManufacturer = new SmManufacturingDtl();
		selectedManufacturer.setPmUom(new PmUom());
		selectedMachine = new SmMachineDtl();
		selectedMachineList = new ArrayList<SmMachineDtl>();

		//
		selectedSignatoryBankAcct = new SmSupplierBank();
		smAppl = new SmAppl();
		smAppl.setSmPersonnels(new ArrayList<SmPersonnel>());
		selectedBank = new SmSupplierBank();
		// selectedBank.setPmbank(new PmBank());
		selectedBank.setSmAddress(defaultAddr);
		selectedBank.setPmFinancialOrg(new PmFinancialOrg());
		selectedBank.setSmChequeSignatories(new ArrayList<SmChequeSignatory>());
		selectedBanks = new ArrayList<SmSupplierBank>();

		// For CategoryCodes
		selectedCategory = new SmSupplierCategory();
		selectedCategory.setPmCategoryL1(new PmCategoryL1());
		selectedCategory.setPmCategoryL2(new PmCategoryL2());
		selectedCategory.setPmCategoryL3(new PmCategoryL3());
		// Dummy data start

		// Dummy data end

		supplierParentCatList = new ArrayList<PmCategoryL1>();
		supplierChildCatList = new ArrayList<PmCategoryL2>();
		supplierSubChildCatList = new ArrayList<PmCategoryL3>();
		// For CategoryCodes End
		testBlock = Boolean.TRUE;
		personCalc = new SmPersonnleCalcVO();
		// setdefault panel names
		equiPerCapTotal = new BigDecimal(0);
		equiCompCapTotal = new BigDecimal(0);
		selectedJobFunction = new SmStaffExperience();
		selectedRelatedCompany = new SmRelatedCompany();
		selectedStaffAcademic = new SmStaffAcademic();
		selectedStaffAcademic.setPmEduField(new PmEduField());
		selectedStfPrfBdy = new SmAssociationBody();
		selectedStfPrfBdy.setPmCertBody(new PmCertBody());
		perIdTypeParam = new PmParameter();
		defPmCountry = SmCommonUtil
				.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		if (defPmCountry == null) {
			defPmCountry = new PmCountry();
		}
		System.out.println(" ********* END MANAGED BEAN CREATION ****** ");
	}

	private SmCompanyExperienceCat setDefaulatExpCatVal() {
		SmCompanyExperienceCat cat = new SmCompanyExperienceCat();
		cat.setPmCategoryL1(new PmCategoryL1());
		cat.setPmCategoryL2(new PmCategoryL2());
		cat.setPmCategoryL3(new PmCategoryL3());
		return cat;
	}

	public SmSupplierCategory getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(SmSupplierCategory selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public List<SmSupplierCategory> getDeleteCategoryList() {
		return deleteCategoryList;
	}

	public void setDeleteCategoryList(
			List<SmSupplierCategory> deleteCategoryList) {
		this.deleteCategoryList = deleteCategoryList;
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

	// public SmStaffAcademic getShareHolderAcademics() {
	// return shareHolderAcademics;
	// }
	//
	// public void setShareHolderAcademics(SmStaffAcademic shareHolderAcademics)
	// {
	// this.shareHolderAcademics = shareHolderAcademics;
	// }

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

	// public List<SmPersonnel> getSmapplAuthPersonnels() {
	// return smapplAuthPersonnels;
	// }
	//
	// public void setSmapplAuthPersonnels(List<SmPersonnel>
	// smapplAuthPersonnels) {
	// this.smapplAuthPersonnels = smapplAuthPersonnels;
	// }

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

	// public SmPersonnelAddress getPersonnelPermAddr() {
	// return personnelPermAddr;
	// }
	//
	// public void setPersonnelPermAddr(SmPersonnelAddress personnelPermAddr) {
	// this.personnelPermAddr = personnelPermAddr;
	// }
	//
	// public SmPersonnelAddress getPersonnelMailAddr() {
	// return personnelMailAddr;
	// }
	//
	// public void setPersonnelMailAddr(SmPersonnelAddress personnelMailAddr) {
	// this.personnelMailAddr = personnelMailAddr;
	// }

	// public SmStaffAcademic getPersonnelAcademics() {
	// return personnelAcademics;
	// }
	//
	// public void setPersonnelAcademics(SmStaffAcademic personnelAcademics) {
	// this.personnelAcademics = personnelAcademics;
	// }

	public SmPersonnel getAddrUpdatePersonnel() {
		return addrUpdatePersonnel;
	}

	public void setAddrUpdatePersonnel(SmPersonnel addrUpdatePersonnel) {
		this.addrUpdatePersonnel = addrUpdatePersonnel;
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

	public String getPoSiteVisitType() {
		return poSiteVisitType;
	}

	public void setPoSiteVisitType(String poSiteVisitType) {
		this.poSiteVisitType = poSiteVisitType;
	}

	public boolean isTestBlock() {
		return testBlock;
	}

	public void setTestBlock(boolean testBlock) {
		this.testBlock = testBlock;

	}

	public int getMaxReviewCount() {
		return maxReviewCount;
	}

	public void setMaxReviewCount(int maxReviewCount) {
		this.maxReviewCount = maxReviewCount;
	}

	public SmApplSectionReview getPoSecReview() {
		return poSecReview;
	}

	public void setPoSecReview(SmApplSectionReview poSecReview) {
		this.poSecReview = poSecReview;
	}

	public SmApplSectionReview getApprvrSecReview() {
		return apprvrSecReview;
	}

	public void setApprvrSecReview(SmApplSectionReview apprvrSecReview) {
		this.apprvrSecReview = apprvrSecReview;
	}

	public boolean isTermsAccepted() {
		return termsAccepted;
	}

	public void setTermsAccepted(boolean termsAccepted) {
		this.termsAccepted = termsAccepted;
	}

	public String getNewSupplierType() {
		return newSupplierType;
	}

	public void setNewSupplierType(String newSupplierType) {
		this.newSupplierType = newSupplierType;
	}

	public List<SmCompanyExperience> getSelectedCompanyExperiences() {
		return selectedCompanyExperiences;
	}

	public void setSelectedCompanyExperiences(
			List<SmCompanyExperience> selectedCompanyExperiences) {
		this.selectedCompanyExperiences = selectedCompanyExperiences;
	}

	public SmCompanyExperience getSelectedCompanyExperience() {
		return selectedCompanyExperience;
	}

	public void setSelectedCompanyExperience(
			SmCompanyExperience selectedCompanyExperience) {
		this.selectedCompanyExperience = selectedCompanyExperience;
	}

	public String getCatTypeConfirm() {
		return catTypeConfirm;
	}

	public void setCatTypeConfirm(String catTypeConfirm) {
		this.catTypeConfirm = catTypeConfirm;
	}

	public SmFacility getSelectedWorkShopFacility() {
		return selectedWorkShopFacility;
	}

	public void setSelectedWorkShopFacility(SmFacility selectedWorkShopFacility) {
		this.selectedWorkShopFacility = selectedWorkShopFacility;
	}

	public List<SmFacility> getSelectedWorkShopFacilities() {
		return selectedWorkShopFacilities;
	}

	public void setSelectedWorkShopFacilities(
			List<SmFacility> selectedWorkShopFacilities) {
		this.selectedWorkShopFacilities = selectedWorkShopFacilities;
	}

	public SmFacility getSelectedDockyardFacility() {
		return selectedDockyardFacility;
	}

	public void setSelectedDockyardFacility(SmFacility selectedDockyardFacility) {
		this.selectedDockyardFacility = selectedDockyardFacility;
	}

	public List<SmFacility> getSelectedDockyardFacilities() {
		return selectedDockyardFacilities;
	}

	public void setSelectedDockyardFacilities(
			List<SmFacility> selectedDockyardFacilities) {
		this.selectedDockyardFacilities = selectedDockyardFacilities;
	}

	public SmFacility getSelectedStoreFacility() {
		return selectedStoreFacility;
	}

	public void setSelectedStoreFacility(SmFacility selectedStoreFacility) {
		this.selectedStoreFacility = selectedStoreFacility;
	}

	public List<SmFacility> getSelectedStoreFacilities() {
		return selectedStoreFacilities;
	}

	public void setSelectedStoreFacilities(
			List<SmFacility> selectedStoreFacilities) {
		this.selectedStoreFacilities = selectedStoreFacilities;
	}

	public List<SmFacility> getApplFactoryFacilities() {
		return applFactoryFacilities;
	}

	public void setApplFactoryFacilities(List<SmFacility> applFactoryFacilities) {
		this.applFactoryFacilities = applFactoryFacilities;
	}

	public List<SmFacility> getApplWorkShopFacilities() {
		return applWorkShopFacilities;
	}

	public void setApplWorkShopFacilities(
			List<SmFacility> applWorkShopFacilities) {
		this.applWorkShopFacilities = applWorkShopFacilities;
	}

	public List<SmFacility> getApplDockyardFacilities() {
		return applDockyardFacilities;
	}

	public void setApplDockyardFacilities(
			List<SmFacility> applDockyardFacilities) {
		this.applDockyardFacilities = applDockyardFacilities;
	}

	public List<SmFacility> getApplStoreFacilities() {
		return applStoreFacilities;
	}

	public void setApplStoreFacilities(List<SmFacility> applStoreFacilities) {
		this.applStoreFacilities = applStoreFacilities;
	}

	/**
	 * dummy record for datatable
	 */

	public List<PmCountry> getCountryLs() {
		return countryLs;
	}

	public void setCountryLs(List<PmCountry> countryLs) {
		this.countryLs = countryLs;
	}

	public List<PmState> getBasicCompanyStates() {
		return basicCompanyStates;
	}

	public void setBasicCompanyStates(List<PmState> basicCompanyStates) {
		this.basicCompanyStates = basicCompanyStates;
	}

	public List<PmDistrict> getBasicCompanyDistricts() {
		return basicCompanyDistricts;
	}

	public void setBasicCompanyDistricts(List<PmDistrict> basicCompanyDistricts) {
		this.basicCompanyDistricts = basicCompanyDistricts;
	}

	public List<PmCity> getBasicCompanyCities() {
		return basicCompanyCities;
	}

	public void setBasicCompanyCities(List<PmCity> basicCompanyCities) {
		this.basicCompanyCities = basicCompanyCities;
	}

	public SmFacility getBasicCompanyFacility() {
		return basicCompanyFacility;
	}

	public void setBasicCompanyFacility(SmFacility basicCompanyFacility) {
		this.basicCompanyFacility = basicCompanyFacility;
	}

	public List<PmCertBody> getCertBodyLs() {
		return certBodyLs;
	}

	public void setCertBodyLs(List<PmCertBody> certBodyLs) {
		this.certBodyLs = certBodyLs;
	}

	public List<PmState> getBasicOffFacStates() {
		return basicOffFacStates;
	}

	public void setBasicOffFacStates(List<PmState> basicOffFacStates) {
		this.basicOffFacStates = basicOffFacStates;
	}

	public List<PmDistrict> getBasicOffFacDistricts() {
		return basicOffFacDistricts;
	}

	public void setBasicOffFacDistricts(List<PmDistrict> basicOffFacDistricts) {
		this.basicOffFacDistricts = basicOffFacDistricts;
	}

	public List<PmCity> getBasicOffFacCities() {
		return basicOffFacCities;
	}

	public void setBasicOffFacCities(List<PmCity> basicOffFacCities) {
		this.basicOffFacCities = basicOffFacCities;
	}

	public List<SmAssociationBody> getSelectedAssocBodyList() {
		return selectedAssocBodyList;
	}

	public void setSelectedAssocBodyList(
			List<SmAssociationBody> selectedAssocBodyList) {
		this.selectedAssocBodyList = selectedAssocBodyList;
	}

	public SmAssociationBody getSelectedAssocBody() {
		return selectedAssocBody;
	}

	public void setSelectedAssocBody(SmAssociationBody selectedAssocBody) {
		this.selectedAssocBody = selectedAssocBody;
	}

	public List<PmCertBody> getProfBodyLs() {
		return profBodyLs;
	}

	public void setProfBodyLs(List<PmCertBody> profBodyLs) {
		this.profBodyLs = profBodyLs;
	}

	public List<PmState> getBranchCompanyStates() {
		return branchCompanyStates;
	}

	public void setBranchCompanyStates(List<PmState> branchCompanyStates) {
		this.branchCompanyStates = branchCompanyStates;
	}

	public List<PmDistrict> getBranchCompanyDistricts() {
		return branchCompanyDistricts;
	}

	public void setBranchCompanyDistricts(
			List<PmDistrict> branchCompanyDistricts) {
		this.branchCompanyDistricts = branchCompanyDistricts;
	}

	public List<PmCity> getBranchCompanyCities() {
		return branchCompanyCities;
	}

	public void setBranchCompanyCities(List<PmCity> branchCompanyCities) {
		this.branchCompanyCities = branchCompanyCities;
	}

	public List<PmState> getAssocCompanyStates() {
		return assocCompanyStates;
	}

	public void setAssocCompanyStates(List<PmState> assocCompanyStates) {
		this.assocCompanyStates = assocCompanyStates;
	}

	public List<PmDistrict> getAssocCompanyDistricts() {
		return assocCompanyDistricts;
	}

	public void setAssocCompanyDistricts(List<PmDistrict> assocCompanyDistricts) {
		this.assocCompanyDistricts = assocCompanyDistricts;
	}

	public List<PmCity> getAssocCompanyCities() {
		return assocCompanyCities;
	}

	public void setAssocCompanyCities(List<PmCity> assocCompanyCities) {
		this.assocCompanyCities = assocCompanyCities;
	}

	public List<PmState> getEquiCompanyStates() {
		return equiCompanyStates;
	}

	public void setEquiCompanyStates(List<PmState> equiCompanyStates) {
		this.equiCompanyStates = equiCompanyStates;
	}

	public List<PmDistrict> getEquiCompanyDistricts() {
		return equiCompanyDistricts;
	}

	public void setEquiCompanyDistricts(List<PmDistrict> equiCompanyDistricts) {
		this.equiCompanyDistricts = equiCompanyDistricts;
	}

	public List<PmCity> getEquiCompanyCities() {
		return equiCompanyCities;
	}

	public void setEquiCompanyCities(List<PmCity> equiCompanyCities) {
		this.equiCompanyCities = equiCompanyCities;
	}

	public List<PmCategoryL1> getSupplierParentCatList() {
		return supplierParentCatList;
	}

	public void setSupplierParentCatList(
			List<PmCategoryL1> supplierParentCatList) {
		this.supplierParentCatList = supplierParentCatList;
	}

	public List<PmCategoryL2> getSupplierChildCatList() {
		return supplierChildCatList;
	}

	public void setSupplierChildCatList(List<PmCategoryL2> supplierChildCatList) {
		this.supplierChildCatList = supplierChildCatList;
	}

	public List<PmCategoryL3> getSupplierSubChildCatList() {
		return supplierSubChildCatList;
	}

	public void setSupplierSubChildCatList(
			List<PmCategoryL3> supplierSubChildCatList) {
		this.supplierSubChildCatList = supplierSubChildCatList;
	}

	public List<PmState> getFactoryFacilityStates() {
		return factoryFacilityStates;
	}

	public void setFactoryFacilityStates(List<PmState> factoryFacilityStates) {
		this.factoryFacilityStates = factoryFacilityStates;
	}

	public List<PmDistrict> getFactoryFacilityDistricts() {
		return factoryFacilityDistricts;
	}

	public void setFactoryFacilityDistricts(
			List<PmDistrict> factoryFacilityDistricts) {
		this.factoryFacilityDistricts = factoryFacilityDistricts;
	}

	public List<PmCity> getFactoryFacilityCities() {
		return factoryFacilityCities;
	}

	public void setFactoryFacilityCities(List<PmCity> factoryFacilityCities) {
		this.factoryFacilityCities = factoryFacilityCities;
	}

	public List<PmState> getDockFacilityStates() {
		return dockFacilityStates;
	}

	public void setDockFacilityStates(List<PmState> dockFacilityStates) {
		this.dockFacilityStates = dockFacilityStates;
	}

	public List<PmDistrict> getDockFacilityDistricts() {
		return dockFacilityDistricts;
	}

	public void setDockFacilityDistricts(List<PmDistrict> dockFacilityDistricts) {
		this.dockFacilityDistricts = dockFacilityDistricts;
	}

	public List<PmCity> getDockFacilityCities() {
		return dockFacilityCities;
	}

	public void setDockFacilityCities(List<PmCity> dockFacilityCities) {
		this.dockFacilityCities = dockFacilityCities;
	}

	public List<PmState> getWorkShopFacilityStates() {
		return workShopFacilityStates;
	}

	public void setWorkShopFacilityStates(List<PmState> workShopFacilityStates) {
		this.workShopFacilityStates = workShopFacilityStates;
	}

	public List<PmDistrict> getWorkShopFacilityDistricts() {
		return workShopFacilityDistricts;
	}

	public void setWorkShopFacilityDistricts(
			List<PmDistrict> workShopFacilityDistricts) {
		this.workShopFacilityDistricts = workShopFacilityDistricts;
	}

	public List<PmCity> getWorkShopFacilityCities() {
		return workShopFacilityCities;
	}

	public void setWorkShopFacilityCities(List<PmCity> workShopFacilityCities) {
		this.workShopFacilityCities = workShopFacilityCities;
	}

	public List<PmState> getStoreFacilityStates() {
		return storeFacilityStates;
	}

	public void setStoreFacilityStates(List<PmState> storeFacilityStates) {
		this.storeFacilityStates = storeFacilityStates;
	}

	public List<PmDistrict> getStoreFacilityDistricts() {
		return storeFacilityDistricts;
	}

	public void setStoreFacilityDistricts(
			List<PmDistrict> storeFacilityDistricts) {
		this.storeFacilityDistricts = storeFacilityDistricts;
	}

	public List<PmCity> getStoreFacilityCities() {
		return storeFacilityCities;
	}

	public void setStoreFacilityCities(List<PmCity> storeFacilityCities) {
		this.storeFacilityCities = storeFacilityCities;
	}

	public List<PmUom> getUomLs() {
		return uomLs;
	}

	public void setUomLs(List<PmUom> uomLs) {
		this.uomLs = uomLs;
	}

	public List<SmRelatedCompany> getSelectedShrHldrRelatedCompanies() {
		return selectedShrHldrRelatedCompanies;
	}

	public void setSelectedShrHldrRelatedCompanies(
			List<SmRelatedCompany> selectedShrHldrRelatedCompanies) {
		this.selectedShrHldrRelatedCompanies = selectedShrHldrRelatedCompanies;
	}

	public List<SmStaffAcademic> getSelectedShrHldrAcademics() {
		return selectedShrHldrAcademics;
	}

	public void setSelectedShrHldrAcademics(
			List<SmStaffAcademic> selectedShrHldrAcademics) {
		this.selectedShrHldrAcademics = selectedShrHldrAcademics;
	}

	public List<SmStaffExperience> getSelectedShrHldrJobFunctions() {
		return selectedShrHldrJobFunctions;
	}

	public void setSelectedShrHldrJobFunctions(
			List<SmStaffExperience> selectedShrHldrJobFunctions) {
		this.selectedShrHldrJobFunctions = selectedShrHldrJobFunctions;
	}

	public List<SmAssociationBody> getSelectedShrHldrProfBodies() {
		return selectedShrHldrProfBodies;
	}

	public void setSelectedShrHldrProfBodies(
			List<SmAssociationBody> selectedShrHldrProfBodies) {
		this.selectedShrHldrProfBodies = selectedShrHldrProfBodies;
	}

	public SmPersonnelAddress getShareHolderResdAddr() {
		return shareHolderResdAddr;
	}

	public void setShareHolderResdAddr(SmPersonnelAddress shareHolderResdAddr) {
		this.shareHolderResdAddr = shareHolderResdAddr;
	}

	public SmPersonnelAddress getShareHolderOffAddr() {
		return shareHolderOffAddr;
	}

	public void setShareHolderOffAddr(SmPersonnelAddress shareHolderOffAddr) {
		this.shareHolderOffAddr = shareHolderOffAddr;
	}

	public List<PmState> getShrHldrPermStates() {
		return shrHldrPermStates;
	}

	public void setShrHldrPermStates(List<PmState> shrHldrPermStates) {
		this.shrHldrPermStates = shrHldrPermStates;
	}

	public List<PmDistrict> getShrHldrPermDistricts() {
		return shrHldrPermDistricts;
	}

	public void setShrHldrPermDistricts(List<PmDistrict> shrHldrPermDistricts) {
		this.shrHldrPermDistricts = shrHldrPermDistricts;
	}

	public List<PmCity> getShrHldrPermCities() {
		return shrHldrPermCities;
	}

	public void setShrHldrPermCities(List<PmCity> shrHldrPermCities) {
		this.shrHldrPermCities = shrHldrPermCities;
	}

	public List<PmState> getShrHldrOffStates() {
		return shrHldrOffStates;
	}

	public void setShrHldrOffStates(List<PmState> shrHldrOffStates) {
		this.shrHldrOffStates = shrHldrOffStates;
	}

	public List<PmDistrict> getShrHldrOffDistricts() {
		return shrHldrOffDistricts;
	}

	public void setShrHldrOffDistricts(List<PmDistrict> shrHldrOffDistricts) {
		this.shrHldrOffDistricts = shrHldrOffDistricts;
	}

	public List<PmCity> getShrHldrOffCities() {
		return shrHldrOffCities;
	}

	public void setShrHldrOffCities(List<PmCity> shrHldrOffCities) {
		this.shrHldrOffCities = shrHldrOffCities;
	}

	public List<PmState> getShrHldrMailStates() {
		return shrHldrMailStates;
	}

	public void setShrHldrMailStates(List<PmState> shrHldrMailStates) {
		this.shrHldrMailStates = shrHldrMailStates;
	}

	public List<PmDistrict> getShrHldrMailDistricts() {
		return shrHldrMailDistricts;
	}

	public void setShrHldrMailDistricts(List<PmDistrict> shrHldrMailDistricts) {
		this.shrHldrMailDistricts = shrHldrMailDistricts;
	}

	public List<PmCity> getShrHldrMailCities() {
		return shrHldrMailCities;
	}

	public void setShrHldrMailCities(List<PmCity> shrHldrMailCities) {
		this.shrHldrMailCities = shrHldrMailCities;
	}

	public List<PmState> getShrHldrResdStates() {
		return shrHldrResdStates;
	}

	public void setShrHldrResdStates(List<PmState> shrHldrResdStates) {
		this.shrHldrResdStates = shrHldrResdStates;
	}

	public List<PmDistrict> getShrHldrResdDistricts() {
		return shrHldrResdDistricts;
	}

	public void setShrHldrResdDistricts(List<PmDistrict> shrHldrResdDistricts) {
		this.shrHldrResdDistricts = shrHldrResdDistricts;
	}

	public List<PmCity> getShrHldrResdCities() {
		return shrHldrResdCities;
	}

	public void setShrHldrResdCities(List<PmCity> shrHldrResdCities) {
		this.shrHldrResdCities = shrHldrResdCities;
	}

	public SmSupplierBranch getSelectedBranchPrev() {
		return selectedBranchPrev;
	}

	public void setSelectedBranchPrev(SmSupplierBranch selectedBranchPrev) {
		this.selectedBranchPrev = selectedBranchPrev;
	}

	public int getSelectedBranchIndex() {
		return selectedBranchIndex;
	}

	public void setSelectedBranchIndex(int selectedBranchIndex) {
		this.selectedBranchIndex = selectedBranchIndex;
	}

	public List<PmState> getBankStates() {
		return bankStates;
	}

	public void setBankStates(List<PmState> bankStates) {
		this.bankStates = bankStates;
	}

	public List<PmDistrict> getBankDistricts() {
		return bankDistricts;
	}

	public void setBankDistricts(List<PmDistrict> bankDistricts) {
		this.bankDistricts = bankDistricts;
	}

	public List<PmCity> getBankCities() {
		return bankCities;
	}

	public void setBankCities(List<PmCity> bankCities) {
		this.bankCities = bankCities;
	}

	public List<PmFinancialOrg> getFinOrgLs() {
		return finOrgLs;
	}

	public void setFinOrgLs(List<PmFinancialOrg> finOrgLs) {
		this.finOrgLs = finOrgLs;
	}

	public Long[] getChequeSingPerId() {
		return chequeSingPerId;
	}

	public void setChequeSingPerId(Long[] chequeSingPerId) {
		this.chequeSingPerId = chequeSingPerId;
	}

	public List<SmStaffExperience> getSelectedPersJobFunctions() {
		return selectedPersJobFunctions;
	}

	public void setSelectedPersJobFunctions(
			List<SmStaffExperience> selectedPersJobFunctions) {
		this.selectedPersJobFunctions = selectedPersJobFunctions;
	}

	public List<SmAssociationBody> getSelectedPersProfBodies() {
		return selectedPersProfBodies;
	}

	public void setSelectedPersProfBodies(
			List<SmAssociationBody> selectedPersProfBodies) {
		this.selectedPersProfBodies = selectedPersProfBodies;
	}

	public List<SmRelatedCompany> getSelectedPersRelatedCompanies() {
		return selectedPersRelatedCompanies;
	}

	public void setSelectedPersRelatedCompanies(
			List<SmRelatedCompany> selectedPersRelatedCompanies) {
		this.selectedPersRelatedCompanies = selectedPersRelatedCompanies;
	}

	public List<SmStaffAcademic> getSelectedPersAcademics() {
		return selectedPersAcademics;
	}

	public void setSelectedPersAcademics(
			List<SmStaffAcademic> selectedPersAcademics) {
		this.selectedPersAcademics = selectedPersAcademics;
	}

	// public SmPersonnelAddress getPersonnelOffAddr() {
	// return personnelOffAddr;
	// }
	//
	// public void setPersonnelOffAddr(SmPersonnelAddress personnelOffAddr) {
	// this.personnelOffAddr = personnelOffAddr;
	// }
	//
	// public SmPersonnelAddress getPersonnelResdAddr() {
	// return personnelResdAddr;
	// }
	//
	// public void setPersonnelResdAddr(SmPersonnelAddress personnelResdAddr) {
	// this.personnelResdAddr = personnelResdAddr;
	// }

	public List<PmState> getPersnlPermStates() {
		return persnlPermStates;
	}

	public void setPersnlPermStates(List<PmState> persnlPermStates) {
		this.persnlPermStates = persnlPermStates;
	}

	public List<PmDistrict> getPersnlPermDistricts() {
		return persnlPermDistricts;
	}

	public void setPersnlPermDistricts(List<PmDistrict> persnlPermDistricts) {
		this.persnlPermDistricts = persnlPermDistricts;
	}

	public List<PmCity> getPersnlPermCities() {
		return persnlPermCities;
	}

	public void setPersnlPermCities(List<PmCity> persnlPermCities) {
		this.persnlPermCities = persnlPermCities;
	}

	public List<PmState> getPersnlOffStates() {
		return persnlOffStates;
	}

	public void setPersnlOffStates(List<PmState> persnlOffStates) {
		this.persnlOffStates = persnlOffStates;
	}

	public List<PmDistrict> getPersnlOffDistricts() {
		return persnlOffDistricts;
	}

	public void setPersnlOffDistricts(List<PmDistrict> persnlOffDistricts) {
		this.persnlOffDistricts = persnlOffDistricts;
	}

	public List<PmCity> getPersnlOffCities() {
		return persnlOffCities;
	}

	public void setPersnlOffCities(List<PmCity> persnlOffCities) {
		this.persnlOffCities = persnlOffCities;
	}

	public List<PmState> getPersnlMailStates() {
		return persnlMailStates;
	}

	public void setPersnlMailStates(List<PmState> persnlMailStates) {
		this.persnlMailStates = persnlMailStates;
	}

	public List<PmDistrict> getPersnlMailDistricts() {
		return persnlMailDistricts;
	}

	public void setPersnlMailDistricts(List<PmDistrict> persnlMailDistricts) {
		this.persnlMailDistricts = persnlMailDistricts;
	}

	public List<PmCity> getPersnlMailCities() {
		return persnlMailCities;
	}

	public void setPersnlMailCities(List<PmCity> persnlMailCities) {
		this.persnlMailCities = persnlMailCities;
	}

	public List<PmState> getPersnlResdStates() {
		return persnlResdStates;
	}

	public void setPersnlResdStates(List<PmState> persnlResdStates) {
		this.persnlResdStates = persnlResdStates;
	}

	public List<PmDistrict> getPersnlResdDistricts() {
		return persnlResdDistricts;
	}

	public void setPersnlResdDistricts(List<PmDistrict> persnlResdDistricts) {
		this.persnlResdDistricts = persnlResdDistricts;
	}

	public List<PmCity> getPersnlResdCities() {
		return persnlResdCities;
	}

	public void setPersnlResdCities(List<PmCity> persnlResdCities) {
		this.persnlResdCities = persnlResdCities;
	}

	public SmApplSupportDoc getSupportDoc() {
		return supportDoc;
	}

	public void setSupportDoc(SmApplSupportDoc supportDoc) {
		this.supportDoc = supportDoc;
	}

	public List<PmReasonDesc> getReasonDescList() {
		return reasonDescList;
	}

	public void setReasonDescList(List<PmReasonDesc> reasonDescList) {
		this.reasonDescList = reasonDescList;
	}

	public Long[] getReasonId() {
		return reasonId;
	}

	public void setReasonId(Long[] reasonId) {
		this.reasonId = reasonId;
	}

//	public int getAuthPerCount() {
//		return authPerCount;
//	}
//
//	public void setAuthPerCount(int authPerCount) {
//		this.authPerCount = authPerCount;
//	}

	public SmPersonnleCalcVO getPersonCalc() {
		return personCalc;
	}

	public void setPersonCalc(SmPersonnleCalcVO personCalc) {
		this.personCalc = personCalc;
	}

	public BigDecimal getEquiPerCapTotal() {
		return equiPerCapTotal;
	}

	public void setEquiPerCapTotal(BigDecimal equiPerCapTotal) {
		this.equiPerCapTotal = equiPerCapTotal;
	}

	public BigDecimal getEquiCompCapTotal() {
		return equiCompCapTotal;
	}

	public void setEquiCompCapTotal(BigDecimal equiCompCapTotal) {
		this.equiCompCapTotal = equiCompCapTotal;
	}

	public List<PmStatusDesc> getRegStatusDescList() {
		return regStatusDescList;
	}

	public void setRegStatusDescList(List<PmStatusDesc> regStatusDescList) {
		this.regStatusDescList = regStatusDescList;
	}

	public boolean isValidResubmit() {
		return validResubmit;
	}

	public void setValidResubmit(boolean validResubmit) {
		this.validResubmit = validResubmit;
	}

	public String getSupplierRemarks() {
		return supplierRemarks;
	}

	public void setSupplierRemarks(String supplierRemarks) {
		this.supplierRemarks = supplierRemarks;
	}

	public String getBusinessTypeVal() {
		return businessTypeVal;
	}

	public void setBusinessTypeVal(String businessTypeVal) {
		this.businessTypeVal = businessTypeVal;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public List<PmCategoryL2> getComExpChildCatList() {
		return comExpChildCatList;
	}

	public void setComExpChildCatList(List<PmCategoryL2> comExpChildCatList) {
		this.comExpChildCatList = comExpChildCatList;
	}

	public List<PmCategoryL3> getComExpSubChildCatList() {
		return comExpSubChildCatList;
	}

	public void setComExpSubChildCatList(
			List<PmCategoryL3> comExpSubChildCatList) {
		this.comExpSubChildCatList = comExpSubChildCatList;
	}

	public boolean isDraftedAppl() {
		return draftedAppl;
	}

	public void setDraftedAppl(boolean draftedAppl) {
		this.draftedAppl = draftedAppl;
	}

	public String getCurrentPageType() {
		return currentPageType;
	}

	public void setCurrentPageType(String currentPageType) {
		this.currentPageType = currentPageType;
	}

	public List<PmEduField> getEduFieldList() {
		return eduFieldList;
	}

	public void setEduFieldList(List<PmEduField> eduFieldList) {
		this.eduFieldList = eduFieldList;
	}

	public boolean isRedirectToPersonPage() {
		return redirectToPersonPage;
	}

	public void setRedirectToPersonPage(boolean redirectToPersonPage) {
		this.redirectToPersonPage = redirectToPersonPage;
	}

	public String getPersonnelInfoHeader() {
		return personnelInfoHeader;
	}

	public void setPersonnelInfoHeader(String personnelInfoHeader) {
		this.personnelInfoHeader = personnelInfoHeader;
	}

	public SmStaffExperience getSelectedJobFunction() {
		return selectedJobFunction;
	}

	public void setSelectedJobFunction(SmStaffExperience selectedJobFunction) {
		this.selectedJobFunction = selectedJobFunction;
	}

	public SmRelatedCompany getSelectedRelatedCompany() {
		return selectedRelatedCompany;
	}

	public void setSelectedRelatedCompany(
			SmRelatedCompany selectedRelatedCompany) {
		this.selectedRelatedCompany = selectedRelatedCompany;
	}

	public boolean isValueReqd() {
		return valueReqd;
	}

	public void setValueReqd(boolean valueReqd) {
		this.valueReqd = valueReqd;
	}

	public SmStaffAcademic getSelectedStaffAcademic() {
		return selectedStaffAcademic;
	}

	public void setSelectedStaffAcademic(SmStaffAcademic selectedStaffAcademic) {
		this.selectedStaffAcademic = selectedStaffAcademic;
	}

	public SmAssociationBody getSelectedStfPrfBdy() {
		return selectedStfPrfBdy;
	}

	public void setSelectedStfPrfBdy(SmAssociationBody selectedStfPrfBdy) {
		this.selectedStfPrfBdy = selectedStfPrfBdy;
	}

	public boolean isShowParentMsg() {
		return showParentMsg;
	}

	public void setShowParentMsg(boolean showParentMsg) {
		this.showParentMsg = showParentMsg;
	}

	public PmParameter getPerIdTypeParam() {
		return perIdTypeParam;
	}

	public void setPerIdTypeParam(PmParameter perIdTypeParam) {
		this.perIdTypeParam = perIdTypeParam;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public Long getSelBankBranchId() {
		return selBankBranchId;
	}

	public void setSelBankBranchId(Long selBankBranchId) {
		this.selBankBranchId = selBankBranchId;
	}

	public PmCountry getDefPmCountry() {
		return defPmCountry;
	}

	public void setDefPmCountry(PmCountry defPmCountry) {
		this.defPmCountry = defPmCountry;
	}

	public List<SelectItem> getePRoleList() {
		return ePRoleList;
	}

	public void setePRoleList(List<SelectItem> ePRoleList) {
		this.ePRoleList = ePRoleList;
	}

	public boolean isRemoveEpRole() {
		return removeEpRole;
	}

	public void setRemoveEpRole(boolean removeEpRole) {
		this.removeEpRole = removeEpRole;
	}

	public List<SmApplSupportDoc> getMissinDocs() {
		return missinDocs;
	}

	public void setMissinDocs(List<SmApplSupportDoc> missinDocs) {
		this.missinDocs = missinDocs;
	}

	public boolean isReviewSiteVisit() {
		return reviewSiteVisit;
	}

	public void setReviewSiteVisit(boolean reviewSiteVisit) {
		this.reviewSiteVisit = reviewSiteVisit;
	}

	public SelectItem[] getBusinessTypeLs() {
		return businessTypeLs;
	}

	public void setBusinessTypeLs(SelectItem[] businessTypeLs) {
		this.businessTypeLs = businessTypeLs;
	}

	public String getReasonDesc() {
		return reasonDesc;
	}

	public void setReasonDesc(String reasonDesc) {
		this.reasonDesc = reasonDesc;
	}

	public boolean isValidSmUserLimit() {
		return validSmUserLimit;
	}

	public void setValidSmUserLimit(boolean validSmUserLimit) {
		this.validSmUserLimit = validSmUserLimit;
	}

	public boolean isUserWithRole() {
		return userWithRole;
	}

	public void setUserWithRole(boolean userWithRole) {
		this.userWithRole = userWithRole;
	}

}
