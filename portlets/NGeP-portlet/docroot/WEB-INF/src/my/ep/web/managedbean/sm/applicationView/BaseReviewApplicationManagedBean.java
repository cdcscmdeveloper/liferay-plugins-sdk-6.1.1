package my.ep.web.managedbean.sm.applicationView;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.persistence.entity.pm.common.PmState;
import my.ep.persistence.entity.pm.common.PmStatusDesc;
import my.ep.persistence.entity.pm.common.PmUom;
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmAssociateCompany;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmMachineDtl;
import my.ep.persistence.entity.sm.SmManufacturingDtl;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmMofCert;
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
import my.ep.type.pm.RoleCode;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.vo.SmPersonnleCalcVO;

import org.apache.commons.lang3.StringUtils;

public abstract class BaseReviewApplicationManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2349279881524427216L;

	private SmAppl smAppl;
	private SmSupplier smSupplier;
	private int tabActiveIndex;
	private String currentPageType;

	private SmSupplierCert selectedCert;
	private SelectItem[] businessTypeLs;
	private SmFacility basicCompanyFacility;
	private SmAssociateCompany selectedAssocCompany;
	private SmSupplierBranch selectedBranch;
	private SmCompanyExperience selectedCompanyExperience;
	private SmPersonnel selectedShareHolder;
	private SmPersonnel selectedPersonnel;
	private SmStaffExperience selectedJobFunction;
	private SmRelatedCompany selectedRelatedCompany;
	private SmAssociationBody selectedStfPrfBdy;
	private SmStaffAcademic selectedStaffAcademic;
	private List<PmCertBody> profBodyLs;

	private List<PmCategoryL1> supplierParentCatList;
	private List<PmCategoryL2> supplierChildCatList;
	private List<PmCategoryL3> supplierSubChildCatList;
	private SmPersonnleCalcVO personCalc;
	private PmCountry defaultCountry;
	private String personnelInfoHeader;
	private boolean userWithRole;
	private boolean redirectToPersonPage;
	private List<PmEduField> eduFieldList;
	private SmPersonnelAddress shareHolderPermAddr;
	private SmPersonnelAddress shareHolderMailAddr;
	private SmPersonnelAddress shareHolderResdAddr;
	private SmPersonnelAddress shareHolderOffAddr;
	private SmAddress defaultAddr;
	private String businessTypeVal;
	private List<SmPersonnel> smapplShareHolders;
	private List<SmSupplierCategory> activeCategoryList;
	private List<SmSupplierCategory> approvedCategoryList;
	private List<SmSupplierCategory> deleteNewApplCategoryList;
	private List<SmSupplierCategory> suppProfileOrHistoryCategoryList;
	private List<SmSupplierBank> smSupplierBankList;

	// temp added for testing
	private boolean poUser;
	private boolean supplierUser;
	private boolean approverUser;
	private boolean reviewSiteVisit;
	private boolean reviewApplicationExist;
	private boolean hasPreviousApplication;
	private boolean viewSupplierProfile;
	private boolean searchSupplierProfile;
	private boolean previousApproved;
	private boolean original;
	private boolean viewApplicationHistory;
	private boolean overLappedCategoryExists;
	private boolean displaySearchPage;
	private String overLappedCategoryMsg;
	private String selectedCertType;
	private String selectedBankAccountPurpose;
	private String catTypeConfirm;
	private String facilityType;
	private String selBankBranchName;
	private String chequeSignPersonnelName;
	private String bankBranchName;
	private String applicationNoParam;
	private String supplierBusinessTypeVal;
	private String selectedMachineryPurchaseMethod;
	private String selectedShareholderIdentificationType;
	private String selectedCompanyExperienceProcurementMode;
	private String selectedCompanyExperienceCatL1;
	private String selectedCompanyExperienceCatL2;
	private String selectedCompanyExperienceCatL3;
	private String supplierRemarks;
	private String currentStatus;
	private String portletUrl;
	private String portletUrlLabel;
	private String supplierIdParam;
	private String regCatCodeConfirmHeader;
	private String regCatCodeConfirmMsg;
	private String resubmitUrl;
	private String localAuthority;
	private String currRegStatus;

	private Long[] chequeSingPerId;
	private Long selBankBranchId;
	private BigDecimal equiCompCapTotal;
	private int maxReviewCount;
	private SmSupplierBank selectedSignatoryBankAcct;
	private SmSupplierBank selectedBank;
	private PmCountry defPmCountry;
	private SmEquityCompany selectedEquityCompany;
	private SmApplSectionReview poSecReview;
	private SmApplSectionReview apprvrSecReview;
	private SmFacility selectedFactoryFacility;
	private SmFacility selectedWorkShopFacility;
	private SmFacility selectedDockyardFacility;
	private SmFacility selectedStoreFacility;
	private SmManufacturingDtl selectedManufacturer;
	private SmMachineDtl selectedMachine;
	private SmAssociationBody selectedAssocBody;
	private SmMofAccount smMofAccount;
	private SmMofCert smMofCert;
	private List<SmFacility> applFactoryFacilities;
	private List<SmFacility> applDockyardFacilities;
	private List<SmFacility> applWorkShopFacilities;
	private List<SmFacility> applStoreFacilities;
	private List<SmMofCert> mofCertLs;
	private String bumiRecommendationNote;
	private String roleCode;

	private String revTypeParam;
	private int maxTab;

	private List<PmStatusDesc> regStatusDescList;
	private List<PmZone> zoneList;
	private boolean mofSupplier;
	private boolean renderedBackButton;
	private int searchBy;
	private SmSupplier searchSupplier;
	private String searchMofNo;
	private String searchSuppType;
	private Long searchPmRegStatusId;
	private Long searchPmZoneId;
	private Date currDate;
	private SmGstSupplier hqGst;

	public BaseReviewApplicationManagedBean() {
		selectedCert = new SmSupplierCert();
		selectedCert.setPmCertBody(new PmCertBody());
		personCalc = new SmPersonnleCalcVO();
		selectedStfPrfBdy = new SmAssociationBody();
		selectedStfPrfBdy.setPmCertBody(new PmCertBody());

		// temp added for testing
		initLoginUser();
		initDefaultAddress();
		initSelectedFacility();
		selectedMachine = new SmMachineDtl();
		selectedManufacturer = new SmManufacturingDtl();
		selectedManufacturer.setPmUom(new PmUom());
		selectedSignatoryBankAcct = new SmSupplierBank();
		selectedBank = new SmSupplierBank();
		selectedCompanyExperience = new SmCompanyExperience();
		selectedEquityCompany = new SmEquityCompany();
		selectedStaffAcademic = new SmStaffAcademic();
		selectedAssocBody = new SmAssociationBody();
		selectedAssocBody.setPmCertBody(new PmCertBody());
		apprvrSecReview = new SmApplSectionReview();
		poSecReview = new SmApplSectionReview();
		equiCompCapTotal = new BigDecimal(0);
		setReviewApplicationExist(true);
		setViewSupplierProfile(false);

		shareHolderMailAddr = new SmPersonnelAddress();
		shareHolderMailAddr.setSmAddress(defaultAddr);
		shareHolderPermAddr = new SmPersonnelAddress();
		shareHolderPermAddr.setSmAddress(defaultAddr);
		shareHolderOffAddr = new SmPersonnelAddress();
		shareHolderOffAddr.setSmAddress(defaultAddr);
		shareHolderResdAddr = new SmPersonnelAddress();
		shareHolderResdAddr.setSmAddress(defaultAddr);

		currDate = new Date();

	}

	public boolean isSearchCriteriaEmpty() {
		boolean retVal = true;
		if (StringUtils.isNotBlank(getSearchMofNo())
				|| getSearchPmRegStatusId() != null
				|| StringUtils.isNotBlank(getSearchSuppType())
				|| (getSearchSupplier() != null && (StringUtils
						.isNotBlank(getSearchSupplier().getCompanyName()) || StringUtils
						.isNotBlank(getSearchSupplier().getRegNo())))) {
			retVal = false;
		}
		return retVal;
	}

	private void initLoginUser() {
		FacesScopes sc = new FacesScopes();
		if (sc.hasRoleUser(SupplierManagementConst.PO_HQ)
				|| sc.hasRoleUser(SupplierManagementConst.PO_HQ_TEMP)) {
			setPoUser(true);
		} else if (sc.hasRoleUser(SupplierManagementConst.APPROVER_HQ)
				|| sc.hasRoleUser(SupplierManagementConst.APPROVER_HQ_TEMP)
				|| sc.hasRoleUser(SupplierManagementConst.APPROVER)) {
			setApproverUser(true);
		} else if (sc.hasRoleUser(SupplierManagementConst.BASIC_SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.BASIC_SUPPLIER_USER)
				|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_USER)
				|| sc.hasRoleUser(RoleCode.FL_USER.value())
				|| sc.hasRoleUser(SupplierManagementConst.SUPPLIER_ADMIN)
				|| sc.hasRoleUser(SupplierManagementConst.SUPPLIER_USER)) {
			setSupplierUser(true);
		}
	}

	private void initDefaultAddress() {
		defaultAddr = new SmAddress();
		defaultAddr.setPmCity(new PmCity());
		defaultAddr.setPmDistrict(new PmDistrict());
		defaultAddr
				.setPmCountry(SmCommonUtil
						.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE));
		defaultAddr.setPmState(new PmState());
	}

	private void initSelectedFacility() {
		selectedWorkShopFacility = new SmFacility();
		selectedWorkShopFacility.setSmAddress(defaultAddr);
		selectedFactoryFacility = new SmFacility();
		selectedFactoryFacility.setSmAddress(defaultAddr);
		selectedDockyardFacility = new SmFacility();
		selectedDockyardFacility.setSmAddress(defaultAddr);
		selectedStoreFacility = new SmFacility();
		selectedStoreFacility.setSmAddress(defaultAddr);
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

	public SmSupplier getSmSupplier() {
		return smSupplier;
	}

	public void setSmSupplier(SmSupplier smSupplier) {
		this.smSupplier = smSupplier;
	}

	public SmSupplierCert getSelectedCert() {
		return selectedCert;
	}

	public void setSelectedCert(SmSupplierCert selectedCert) {
		this.selectedCert = selectedCert;
	}

	public SelectItem[] getBusinessTypeLs() {
		return businessTypeLs;
	}

	public void setBusinessTypeLs(SelectItem[] businessTypeLs) {
		this.businessTypeLs = businessTypeLs;
	}

	public SmFacility getBasicCompanyFacility() {
		return basicCompanyFacility;
	}

	public void setBasicCompanyFacility(SmFacility basicCompanyFacility) {
		this.basicCompanyFacility = basicCompanyFacility;
	}

	public SmAssociateCompany getSelectedAssocCompany() {
		return selectedAssocCompany;
	}

	public void setSelectedAssocCompany(SmAssociateCompany selectedAssocCompany) {
		this.selectedAssocCompany = selectedAssocCompany;
	}

	public SmSupplierBranch getSelectedBranch() {
		return selectedBranch;
	}

	public void setSelectedBranch(SmSupplierBranch selectedBranch) {
		this.selectedBranch = selectedBranch;
	}

	public SmCompanyExperience getSelectedCompanyExperience() {
		return selectedCompanyExperience;
	}

	public void setSelectedCompanyExperience(
			SmCompanyExperience selectedCompanyExperience) {
		this.selectedCompanyExperience = selectedCompanyExperience;
	}

	public SmPersonnel getSelectedShareHolder() {
		return selectedShareHolder;
	}

	public void setSelectedShareHolder(SmPersonnel selectedShareHolder) {
		this.selectedShareHolder = selectedShareHolder;
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

	public String getCurrentPageType() {
		return currentPageType;
	}

	public void setCurrentPageType(String currentPageType) {
		this.currentPageType = currentPageType;
	}

	public SmPersonnleCalcVO getPersonCalc() {
		return personCalc;
	}

	public void setPersonCalc(SmPersonnleCalcVO personCalc) {
		this.personCalc = personCalc;
	}

	public PmCountry getDefaultCountry() {
		return defaultCountry;
	}

	public void setDefaultCountry(PmCountry defaultCountry) {
		this.defaultCountry = defaultCountry;
	}

	public String getPersonnelInfoHeader() {
		return personnelInfoHeader;
	}

	public void setPersonnelInfoHeader(String personnelInfoHeader) {
		this.personnelInfoHeader = personnelInfoHeader;
	}

	public boolean isUserWithRole() {
		return userWithRole;
	}

	public void setUserWithRole(boolean userWithRole) {
		this.userWithRole = userWithRole;
	}

	public SmPersonnel getSelectedPersonnel() {
		return selectedPersonnel;
	}

	public void setSelectedPersonnel(SmPersonnel selectedPersonnel) {
		this.selectedPersonnel = selectedPersonnel;
	}

	public boolean isRedirectToPersonPage() {
		return redirectToPersonPage;
	}

	public void setRedirectToPersonPage(boolean redirectToPersonPage) {
		this.redirectToPersonPage = redirectToPersonPage;
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

	public SmAssociationBody getSelectedStfPrfBdy() {
		return selectedStfPrfBdy;
	}

	public void setSelectedStfPrfBdy(SmAssociationBody selectedStfPrfBdy) {
		this.selectedStfPrfBdy = selectedStfPrfBdy;
	}

	public SmStaffAcademic getSelectedStaffAcademic() {
		return selectedStaffAcademic;
	}

	public void setSelectedStaffAcademic(SmStaffAcademic selectedStaffAcademic) {
		this.selectedStaffAcademic = selectedStaffAcademic;
	}

	public List<PmEduField> getEduFieldList() {
		return eduFieldList;
	}

	public void setEduFieldList(List<PmEduField> eduFieldList) {
		this.eduFieldList = eduFieldList;
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

	public SmAddress getDefaultAddr() {
		return defaultAddr;
	}

	public void setDefaultAddr(SmAddress defaultAddr) {
		this.defaultAddr = defaultAddr;
	}

	public String getBusinessTypeVal() {
		return businessTypeVal;
	}

	public void setBusinessTypeVal(String businessTypeVal) {
		this.businessTypeVal = businessTypeVal;
	}

	public List<SmPersonnel> getSmapplShareHolders() {
		return smapplShareHolders;
	}

	public void setSmapplShareHolders(List<SmPersonnel> smapplShareHolders) {
		this.smapplShareHolders = smapplShareHolders;
	}

	public List<PmCertBody> getProfBodyLs() {
		return profBodyLs;
	}

	public void setProfBodyLs(List<PmCertBody> profBodyLs) {
		this.profBodyLs = profBodyLs;
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

	public boolean isReviewSiteVisit() {
		return reviewSiteVisit;
	}

	public void setReviewSiteVisit(boolean reviewSiteVisit) {
		this.reviewSiteVisit = reviewSiteVisit;
	}

	public String getSelectedCertType() {
		return selectedCertType;
	}

	public void setSelectedCertType(String selectedCertType) {
		this.selectedCertType = selectedCertType;
	}

	public SmSupplierBank getSelectedSignatoryBankAcct() {
		return selectedSignatoryBankAcct;
	}

	public void setSelectedSignatoryBankAcct(
			SmSupplierBank selectedSignatoryBankAcct) {
		this.selectedSignatoryBankAcct = selectedSignatoryBankAcct;
	}

	public Long[] getChequeSingPerId() {
		return chequeSingPerId;
	}

	public void setChequeSingPerId(Long[] chequeSingPerId) {
		this.chequeSingPerId = chequeSingPerId;
	}

	public Long getSelBankBranchId() {
		return selBankBranchId;
	}

	public void setSelBankBranchId(Long selBankBranchId) {
		this.selBankBranchId = selBankBranchId;
	}

	public SmSupplierBank getSelectedBank() {
		return selectedBank;
	}

	public void setSelectedBank(SmSupplierBank selectedBank) {
		this.selectedBank = selectedBank;
	}

	public PmCountry getDefPmCountry() {
		return defPmCountry;
	}

	public void setDefPmCountry(PmCountry defPmCountry) {
		this.defPmCountry = defPmCountry;
	}

	public String getSelectedBankAccountPurpose() {
		return selectedBankAccountPurpose;
	}

	public void setSelectedBankAccountPurpose(String selectedBankAccountPurpose) {
		this.selectedBankAccountPurpose = selectedBankAccountPurpose;
	}

	public SmEquityCompany getSelectedEquityCompany() {
		return selectedEquityCompany;
	}

	public void setSelectedEquityCompany(SmEquityCompany selectedEquityCompany) {
		this.selectedEquityCompany = selectedEquityCompany;
	}

	public BigDecimal getEquiCompCapTotal() {
		return equiCompCapTotal;
	}

	public void setEquiCompCapTotal(BigDecimal equiCompCapTotal) {
		this.equiCompCapTotal = equiCompCapTotal;
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

	public int getMaxReviewCount() {
		return maxReviewCount;
	}

	public void setMaxReviewCount(int maxReviewCount) {
		this.maxReviewCount = maxReviewCount;
	}

	public String getCatTypeConfirm() {
		return catTypeConfirm;
	}

	public void setCatTypeConfirm(String catTypeConfirm) {
		this.catTypeConfirm = catTypeConfirm;
	}

	public List<SmFacility> getApplFactoryFacilities() {
		return applFactoryFacilities;
	}

	public void setApplFactoryFacilities(List<SmFacility> applFactoryFacilities) {
		this.applFactoryFacilities = applFactoryFacilities;
	}

	public List<SmFacility> getApplDockyardFacilities() {
		return applDockyardFacilities;
	}

	public void setApplDockyardFacilities(
			List<SmFacility> applDockyardFacilities) {
		this.applDockyardFacilities = applDockyardFacilities;
	}

	public List<SmFacility> getApplWorkShopFacilities() {
		return applWorkShopFacilities;
	}

	public void setApplWorkShopFacilities(
			List<SmFacility> applWorkShopFacilities) {
		this.applWorkShopFacilities = applWorkShopFacilities;
	}

	public List<SmFacility> getApplStoreFacilities() {
		return applStoreFacilities;
	}

	public void setApplStoreFacilities(List<SmFacility> applStoreFacilities) {
		this.applStoreFacilities = applStoreFacilities;
	}

	public String getFacilityType() {
		return facilityType;
	}

	public void setFacilityType(String facilityType) {
		this.facilityType = facilityType;
	}

	public SmFacility getSelectedFactoryFacility() {
		return selectedFactoryFacility;
	}

	public void setSelectedFactoryFacility(SmFacility selectedFactoryFacility) {
		this.selectedFactoryFacility = selectedFactoryFacility;
	}

	public SmFacility getSelectedWorkShopFacility() {
		return selectedWorkShopFacility;
	}

	public void setSelectedWorkShopFacility(SmFacility selectedWorkShopFacility) {
		this.selectedWorkShopFacility = selectedWorkShopFacility;
	}

	public SmFacility getSelectedDockyardFacility() {
		return selectedDockyardFacility;
	}

	public void setSelectedDockyardFacility(SmFacility selectedDockyardFacility) {
		this.selectedDockyardFacility = selectedDockyardFacility;
	}

	public SmFacility getSelectedStoreFacility() {
		return selectedStoreFacility;
	}

	public void setSelectedStoreFacility(SmFacility selectedStoreFacility) {
		this.selectedStoreFacility = selectedStoreFacility;
	}

	public SmManufacturingDtl getSelectedManufacturer() {
		return selectedManufacturer;
	}

	public void setSelectedManufacturer(SmManufacturingDtl selectedManufacturer) {
		this.selectedManufacturer = selectedManufacturer;
	}

	public SmMachineDtl getSelectedMachine() {
		return selectedMachine;
	}

	public void setSelectedMachine(SmMachineDtl selectedMachine) {
		this.selectedMachine = selectedMachine;
	}

	public String getSelBankBranchName() {
		return selBankBranchName;
	}

	public void setSelBankBranchName(String selBankBranchName) {
		this.selBankBranchName = selBankBranchName;
	}

	public String getChequeSignPersonnelName() {
		return chequeSignPersonnelName;
	}

	public void setChequeSignPersonnelName(String chequeSignPersonnelName) {
		this.chequeSignPersonnelName = chequeSignPersonnelName;
	}

	public String getApplicationNoParam() {
		return applicationNoParam;
	}

	public void setApplicationNoParam(String applicationNoParam) {
		this.applicationNoParam = applicationNoParam;
	}

	public String getSupplierBusinessTypeVal() {
		return supplierBusinessTypeVal;
	}

	public void setSupplierBusinessTypeVal(String supplierBusinessTypeVal) {
		this.supplierBusinessTypeVal = supplierBusinessTypeVal;
	}

	public SmAssociationBody getSelectedAssocBody() {
		return selectedAssocBody;
	}

	public void setSelectedAssocBody(SmAssociationBody selectedAssocBody) {
		this.selectedAssocBody = selectedAssocBody;
	}

	public String getSelectedMachineryPurchaseMethod() {
		return selectedMachineryPurchaseMethod;
	}

	public void setSelectedMachineryPurchaseMethod(
			String selectedMachineryPurchaseMethod) {
		this.selectedMachineryPurchaseMethod = selectedMachineryPurchaseMethod;
	}

	public boolean isReviewApplicationExist() {
		return reviewApplicationExist;
	}

	public void setReviewApplicationExist(boolean reviewApplicationExist) {
		this.reviewApplicationExist = reviewApplicationExist;
	}

	public String getSupplierRemarks() {
		return supplierRemarks;
	}

	public void setSupplierRemarks(String supplierRemarks) {
		this.supplierRemarks = supplierRemarks;
	}

	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	public String getRevTypeParam() {
		return revTypeParam;
	}

	public void setRevTypeParam(String revTypeParam) {
		this.revTypeParam = revTypeParam;
	}

	public boolean isHasPreviousApplication() {
		return hasPreviousApplication;
	}

	public void setHasPreviousApplication(boolean hasPreviousApplication) {
		this.hasPreviousApplication = hasPreviousApplication;
	}

	public boolean isViewSupplierProfile() {
		return viewSupplierProfile;
	}

	public void setViewSupplierProfile(boolean viewSupplierProfile) {
		this.viewSupplierProfile = viewSupplierProfile;
	}

	public SmMofAccount getSmMofAccount() {
		return smMofAccount;
	}

	public void setSmMofAccount(SmMofAccount smMofAccount) {
		this.smMofAccount = smMofAccount;
	}

	public String getBumiRecommendationNote() {
		return bumiRecommendationNote;
	}

	public void setBumiRecommendationNote(String bumiRecommendationNote) {
		this.bumiRecommendationNote = bumiRecommendationNote;
	}

	public List<SmSupplierCategory> getActiveCategoryList() {
		return activeCategoryList;
	}

	public void setActiveCategoryList(
			List<SmSupplierCategory> activeCategoryList) {
		this.activeCategoryList = activeCategoryList;
	}

	public String getSelectedShareholderIdentificationType() {
		return selectedShareholderIdentificationType;
	}

	public void setSelectedShareholderIdentificationType(
			String selectedShareholderIdentificationType) {
		this.selectedShareholderIdentificationType = selectedShareholderIdentificationType;
	}

	public String getSelectedCompanyExperienceProcurementMode() {
		return selectedCompanyExperienceProcurementMode;
	}

	public void setSelectedCompanyExperienceProcurementMode(
			String selectedCompanyExperienceProcurementMode) {
		this.selectedCompanyExperienceProcurementMode = selectedCompanyExperienceProcurementMode;
	}

	public int getMaxTab() {
		return maxTab;
	}

	public void setMaxTab(int maxTab) {
		this.maxTab = maxTab;
	}

	public boolean isPreviousApproved() {
		return previousApproved;
	}

	public void setPreviousApproved(boolean previousApproved) {
		this.previousApproved = previousApproved;
	}

	public boolean isOriginal() {
		return original;
	}

	public void setOriginal(boolean original) {
		this.original = original;
	}

	public String getSelectedCompanyExperienceCatL1() {
		return selectedCompanyExperienceCatL1;
	}

	public void setSelectedCompanyExperienceCatL1(
			String selectedCompanyExperienceCatL1) {
		this.selectedCompanyExperienceCatL1 = selectedCompanyExperienceCatL1;
	}

	public String getSelectedCompanyExperienceCatL2() {
		return selectedCompanyExperienceCatL2;
	}

	public void setSelectedCompanyExperienceCatL2(
			String selectedCompanyExperienceCatL2) {
		this.selectedCompanyExperienceCatL2 = selectedCompanyExperienceCatL2;
	}

	public String getSelectedCompanyExperienceCatL3() {
		return selectedCompanyExperienceCatL3;
	}

	public void setSelectedCompanyExperienceCatL3(
			String selectedCompanyExperienceCatL3) {
		this.selectedCompanyExperienceCatL3 = selectedCompanyExperienceCatL3;
	}

	public boolean isSearchSupplierProfile() {
		return searchSupplierProfile;
	}

	public void setSearchSupplierProfile(boolean searchSupplierProfile) {
		this.searchSupplierProfile = searchSupplierProfile;
	}

	public boolean isViewApplicationHistory() {
		return viewApplicationHistory;
	}

	public void setViewApplicationHistory(boolean viewApplicationHistory) {
		this.viewApplicationHistory = viewApplicationHistory;
	}

	public boolean isMofSupplier() {
		return mofSupplier;
	}

	public void setMofSupplier(boolean mofSupplier) {
		this.mofSupplier = mofSupplier;
	}

	public boolean isRenderedBackButton() {
		return renderedBackButton;
	}

	public void setRenderedBackButton(boolean renderedBackButton) {
		this.renderedBackButton = renderedBackButton;
	}

	public int getSearchBy() {
		return searchBy;
	}

	public void setSearchBy(int searchBy) {
		this.searchBy = searchBy;
	}

	public SmSupplier getSearchSupplier() {
		return searchSupplier;
	}

	public void setSearchSupplier(SmSupplier searchSupplier) {
		this.searchSupplier = searchSupplier;
	}

	public String getPortletUrl() {
		return portletUrl;
	}

	public void setPortletUrl(String portletUrl) {
		this.portletUrl = portletUrl;
	}

	public String getSupplierIdParam() {
		return supplierIdParam;
	}

	public void setSupplierIdParam(String supplierIdParam) {
		this.supplierIdParam = supplierIdParam;
	}

	public String getPortletUrlLabel() {
		return portletUrlLabel;
	}

	public void setPortletUrlLabel(String portletUrlLabel) {
		this.portletUrlLabel = portletUrlLabel;
	}

	public String getSearchMofNo() {
		return searchMofNo;
	}

	public void setSearchMofNo(String searchMofNo) {
		this.searchMofNo = searchMofNo;
	}

	public String getSearchSuppType() {
		return searchSuppType;
	}

	public void setSearchSuppType(String searchSuppType) {
		this.searchSuppType = searchSuppType;
	}

	public Long getSearchPmRegStatusId() {
		return searchPmRegStatusId;
	}

	public void setSearchPmRegStatusId(Long searchPmRegStatusId) {
		this.searchPmRegStatusId = searchPmRegStatusId;
	}

	public List<PmStatusDesc> getRegStatusDescList() {
		return regStatusDescList;
	}

	public void setRegStatusDescList(List<PmStatusDesc> regStatusDescList) {
		this.regStatusDescList = regStatusDescList;
	}

	public List<PmZone> getZoneList() {
		return zoneList;
	}

	public void setZoneList(List<PmZone> zoneList) {
		this.zoneList = zoneList;
	}

	public Long getSearchPmZoneId() {
		return searchPmZoneId;
	}

	public void setSearchPmZoneId(Long searchPmZoneId) {
		this.searchPmZoneId = searchPmZoneId;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public List<SmSupplierBank> getSmSupplierBankList() {
		return smSupplierBankList;
	}

	public void setSmSupplierBankList(List<SmSupplierBank> smSupplierBankList) {
		this.smSupplierBankList = smSupplierBankList;
	}

	public Date getCurrDate() {
		return currDate;
	}

	public void setCurrDate(Date currDate) {
		this.currDate = currDate;
	}

	public List<SmSupplierCategory> getApprovedCategoryList() {
		return approvedCategoryList;
	}

	public void setApprovedCategoryList(
			List<SmSupplierCategory> approvedCategoryList) {
		this.approvedCategoryList = approvedCategoryList;
	}

	public boolean isOverLappedCategoryExists() {
		return overLappedCategoryExists;
	}

	public void setOverLappedCategoryExists(boolean overLappedCategoryExists) {
		this.overLappedCategoryExists = overLappedCategoryExists;
	}

	public String getOverLappedCategoryMsg() {
		return overLappedCategoryMsg;
	}

	public void setOverLappedCategoryMsg(String overLappedCategoryMsg) {
		this.overLappedCategoryMsg = overLappedCategoryMsg;
	}

	public String getRegCatCodeConfirmHeader() {
		return regCatCodeConfirmHeader;
	}

	public void setRegCatCodeConfirmHeader(String regCatCodeConfirmHeader) {
		this.regCatCodeConfirmHeader = regCatCodeConfirmHeader;
	}

	public String getRegCatCodeConfirmMsg() {
		return regCatCodeConfirmMsg;
	}

	public void setRegCatCodeConfirmMsg(String regCatCodeConfirmMsg) {
		this.regCatCodeConfirmMsg = regCatCodeConfirmMsg;
	}

	public List<SmSupplierCategory> getDeleteNewApplCategoryList() {
		return deleteNewApplCategoryList;
	}

	public void setDeleteNewApplCategoryList(
			List<SmSupplierCategory> deleteNewApplCategoryList) {
		this.deleteNewApplCategoryList = deleteNewApplCategoryList;
	}

	public boolean isDisplaySearchPage() {
		return displaySearchPage;
	}

	public void setDisplaySearchPage(boolean displaySearchPage) {
		this.displaySearchPage = displaySearchPage;
	}

	public List<SmSupplierCategory> getSuppProfileOrHistoryCategoryList() {
		return suppProfileOrHistoryCategoryList;
	}

	public void setSuppProfileOrHistoryCategoryList(
			List<SmSupplierCategory> suppProfileOrHistoryCategoryList) {
		this.suppProfileOrHistoryCategoryList = suppProfileOrHistoryCategoryList;
	}

	public SmGstSupplier getHqGst() {
		return hqGst;
	}

	public void setHqGst(SmGstSupplier hqGst) {
		this.hqGst = hqGst;
	}

	public String getBankBranchName() {
		return bankBranchName;
	}

	public void setBankBranchName(String bankBranchName) {
		this.bankBranchName = bankBranchName;
	}

	public List<SmMofCert> getMofCertLs() {
		return mofCertLs;
	}

	public void setMofCertLs(List<SmMofCert> mofCertLs) {
		this.mofCertLs = mofCertLs;
	}

	public String getResubmitUrl() {
		return resubmitUrl;
	}

	public void setResubmitUrl(String resubmitUrl) {
		this.resubmitUrl = resubmitUrl;
	}

	public String getLocalAuthority() {
		return localAuthority;
	}

	public void setLocalAuthority(String localAuthority) {
		this.localAuthority = localAuthority;
	}

	public SmMofCert getSmMofCert() {
		return smMofCert;
	}

	public void setSmMofCert(SmMofCert smMofCert) {
		this.smMofCert = smMofCert;
	}

	public String getCurrRegStatus() {
		return currRegStatus;
	}

	public void setCurrRegStatus(String currRegStatus) {
		this.currRegStatus = currRegStatus;
	}

}
