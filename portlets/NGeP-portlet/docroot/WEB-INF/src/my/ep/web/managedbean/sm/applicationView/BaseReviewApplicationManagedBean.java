package my.ep.web.managedbean.sm.applicationView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.biz.service.cm.catalogue.ICatalogueSO;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.cm.catalogue.CatalogueReqDTO;
import my.ep.dto.cm.catalogue.CatalogueResDTO;
import my.ep.dto.pm.common.status.StatusReqDTO;
import my.ep.dto.sm.bulkApplication.SmBulkApplOverlapCatVO;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.cm.catalog.CmRequestCode;
import my.ep.persistence.entity.cm.catalog.CmSupplierItem;
import my.ep.persistence.entity.cm.catalog.CmSupplierZone;
import my.ep.persistence.entity.pm.authorization.PmDigiCert;
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
import my.ep.persistence.entity.pm.common.PmZone;
import my.ep.persistence.entity.sm.SmAddress;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplInquiry;
import my.ep.persistence.entity.sm.SmApplRejectReason;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
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
import my.ep.persistence.entity.sm.SmRemark;
import my.ep.persistence.entity.sm.SmSmeSupplier;
import my.ep.persistence.entity.sm.SmStaffAcademic;
import my.ep.persistence.entity.sm.SmStaffExperience;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmSupplierCert;
import my.ep.persistence.entity.sm.SmSupplierOwner;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.cm.catalogue.CatalogueBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.BlacklistOwnerDirectorVO;
import my.ep.web.vo.DisciplinaryActionCategoryVO;
import my.ep.web.vo.DisciplinaryActionVO;
import my.ep.web.vo.SmCancelMofRemarksVO;
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
	private List<SmSupplierCategory> pendingApprovedCategoryList;
	private List<SmSupplierBank> smSupplierBankList;
	private List<PmCategoryL3> dockYardFacilitiesVOList;
	private List<PmCategoryL3> storeFacilitiesVOList;
	private List<PmCategoryL3> workshopFacilitiesVOList;
	private List<PmCategoryL3> factoryFacilitiesVOList;
	
	// temp added for testing
	private boolean poUser;
	private boolean supplierUser;
	private boolean approverUser;
	private boolean bpkUser;
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
	private boolean serializedFileExist;
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
	private String srcApplNo;
	private String targetApplNo;
	private String rejectReason;

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
	private List<SelectItem> applTypeItems;
	private List<SelectItem> applStatusItems;
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
	private String searchEpNo;
	private String searchApplNo;
	private String searchApplType;
	private Long searchApplStatusId;
	private Long searchPmRegStatusId;
	private Long searchPmZoneId;
	private PmZone loginUserZone;
	private Date currDate;
	private SmGstSupplier hqGst;
	private boolean updatePersonnelPage;

	private String oriPoApprOverlappedRemark;
	private String poApprOverlappedRemark;
	private String poUserName;
	private String poRemark;
	private Date poRemarkCreatedDate;
	private String approverUserName;
	private String approverRemark;
	private Date approverRemarkCreatedDate;
	private Long remarkId;
	private Long remarkOverlappedId;
	private List<SmRemark> approverRemarkList;
	
	private SmCancelMofRemarksVO cancelRemarkVO;
	//25792 - start
	private SmPersonnelAddress addressAsInIc;
	private SmPersonnelAddress correspondenceAddress;
	private PmDigiCert pmDigiCert;
	private String accountType;
	private String agencyTypeName;
	private boolean isG2GSupplierAdmin;
	private Long agencyType;
	private String agencyName;
	private String sapVendorCode;
	private IPagingDataModel<CmSupplierItem> supplierItemSearchDataModel = new SupplierItemPagingDataModel(); 
	private String catalogueInfoItemStatus;
	private int catalogueInfoTabActiveIndex; 
	private List<CmRequestCode> requestCodeList;
	private boolean redirectToSupplierItemPage;
	private CmSupplierItem selectedSupplierItem;
	private int supplierItemDetailTabActiveIndex;
	private int pricingTypeGroupTabActiveIndex;
	private int pricingTypeTabActiveIndex;
	private List<CmSupplierZone> supplierZoneList;
	private int pendingApprovalSupplierItemSize;
	private int approvedSupplierItemSize;
	private int rejectedSupplierItemSize;
	private int publishedSupplierItemSize;
	private int expiredSupplierItemSize;
	private int totalRecords;
	private String applicationStatusDesc;
	//25792 - end
	// #29664 - start
	private String poRemarkAfterSiteVisit;
	private Date poRemarkAfterSiteVisitCreatedDate;
	private String secondPoUserName = null;
	// #29664 - start
	private boolean basicAccountMaintenance; //27437 : Disciplinary action
	private SmSupplierOwner smSuppOwner; //26238
	private boolean viewSupplierMaintenancePage; // #26639
	
	//28281 - start
	private boolean warningExist;
	private boolean warning;
	private boolean cancelCategoryCode;
	private boolean suspendRegistration;
	private boolean cancelRegistration;
	private boolean blacklistOwnerOrDirector;
	private boolean bumiputeraStatusCancel;
	private boolean manufacturerStatusCancel;
	private List<DisciplinaryActionVO> disciplinaryActionVoList;
	private List<SmPersonnel> ownerAndDirectorList; 
	private List<SmPersonnel> selectedOwnerAndDirectorList; 
	private String applType; 
	private boolean viewFlag; 
	private SmSupplier smSupp; 
	private boolean viewMode; 
	private boolean mofNoExist;
	
	private List<BlacklistOwnerDirectorVO> blacklistownerDirectorList;
	private String startDate;
	private String endDate;
	private String remarks;
	private boolean noDisciplinary;
	private List<DisciplinaryActionCategoryVO> categoryCodeList;
	
	private List<DisciplinaryActionVO> disciplineVOs; 
	private boolean disciplinaryAction;
	//28281 - end 
	
	//30974 - Start (16/7/2021)
	private List<SmApplRejectReason> smApplRejectReasonsByPO;
	private String rejectReasonByPO;
	//30974 - End (16/7/2021)
	
	//31111
	private List<SmBulkApplOverlapCatVO> overlapCategoryVOList;
	
	
	//28771 - start
	private String supportingDocMode;
	
	// #29617
	private SmSmeSupplier smeSupplier;
	
	//#3460
	private String sdoRemark;
	
	public String getSupportingDocMode() {
		return supportingDocMode;
	}

	public void setSupportingDocMode(String supportingDocMode) {
		this.supportingDocMode = supportingDocMode;
	}
	//28771- end
	
	//26238 -  Disciplonary action - 28146
	private boolean flagBlacklistCancel;
	
	// 28342 : Disciplinary Action - start
	private boolean disableDownloadButtonDAHistory;
	// 28342 : Disciplinary Action - end
	
	//28637
	private boolean viewCatCode;
	
	// #28278
	private boolean flagShowButtonSubmit;
	
	//14530
		private SmApplInquiry poToSupplierInquiry;
		private List<SmApplInquiry> poSupplierInquiryHistory;
		private List<SmApplSupportDoc> missinDocs;
	
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
		// selectedManufacturer.setPmUom(new PmUom());
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

		applTypeItems = new ArrayList<SelectItem>();
		SelectItem item = new SelectItem();
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.appltype.basic", null));
		item.setValue(ParameterConst.BASIC_ACCOUNT);
		applTypeItems.add(item);
		item = new SelectItem();
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.appltype.newmof", null));
		item.setValue(ParameterConst.NEW_SM_APPLN_FOR_MOF);
		applTypeItems.add(item);
		item = new SelectItem();
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.appltype.bumiputera",
				null));
		item.setValue(ParameterConst.NEW_SM_APPLN_FOR_BUMI);
		applTypeItems.add(item);
		item = new SelectItem();
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.appltype.categorycode",
				null));
		item.setValue(ParameterConst.NEW_SM_APPLN_FOR_APPLCATCODES);
		applTypeItems.add(item);
		item = new SelectItem();
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.appltype.updateprofile",
				null));
		item.setValue(ParameterConst.NEW_SM_APPLN_FOR_UPDATE);
		applTypeItems.add(item);
		item = new SelectItem();
		//#15004 Start
		//item.setLabel(SmCommonUtil.getMessage("lbl.sm.appltype.renewmof", null));
		item.setLabel(SmCommonUtil.getMessage("lbl.sm.appltype.renewalappl", null));
		//#15004 End
		item.setValue(ParameterConst.NEW_SM_APPLN_FOR_RENEW);
		applTypeItems.add(item);
		item = new SelectItem();
		item.setLabel(SmCommonUtil
				.getMessage("lbl.sm.appltype.cancelmof", null));
		item.setValue(ParameterConst.NEW_SM_APPLN_FOR_CANCEL);
		applTypeItems.add(item);

		applStatusItems = new ArrayList<SelectItem>();
		List<PmStatusDesc> statusDescList = null;
		StatusReqDTO statusReqDTO = new StatusReqDTO();
		statusReqDTO.setLanguageCode(new FacesScopes().getUserLocale()
				.getLanguage());
		statusDescList = SmServiceLookup.getStatusSO()
				.retrieveAllSupplierApplicationStatus(statusReqDTO)
				.getStatusDescList();
		if (!EntityHelper.isCollectionEmpty(statusDescList)) {
			for (PmStatusDesc pd : statusDescList) {
				item = new SelectItem();
				item.setLabel(pd.getStatusName());
				item.setValue(pd.getPmStatus().getStatusId());
				applStatusItems.add(item);
			}
		}

	}

	public boolean isSearchCriteriaEmpty() {
		boolean retVal = true;
		//25792 -  start
		if(SupplierManagementConst.SUPP_TYPE_BASIC.equalsIgnoreCase(getAccountType())) {
			if (StringUtils.isNotBlank(getSearchEpNo())
					|| StringUtils.isNotBlank(getSearchApplNo())
					|| StringUtils.isNotBlank(getSearchApplType())
					|| getSearchApplStatusId() != null
					|| (getSearchSupplier() != null && (StringUtils
							.isNotBlank(getSearchSupplier().getCompanyName()) || StringUtils
							.isNotBlank(getSearchSupplier().getRegNo())))) {
				retVal = false;
			}
		}else if(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT.equalsIgnoreCase(getAccountType())) {
			if (StringUtils.isNotBlank(getAgencyName()) || StringUtils.isNotBlank(getSearchEpNo())) {
				retVal = false;
			}
		}else {
			if (StringUtils.isNotBlank(getSearchMofNo())
					|| getSearchPmRegStatusId() != null
					|| StringUtils.isNotBlank(getSearchSuppType())
					|| StringUtils.isNotBlank(getSearchApplNo())
					|| StringUtils.isNotBlank(getSearchApplType())
					|| getSearchApplStatusId() != null
					|| (getSearchSupplier() != null && (StringUtils
							.isNotBlank(getSearchSupplier().getCompanyName()) || StringUtils
							.isNotBlank(getSearchSupplier().getRegNo())))) {
				retVal = false;
			}
		}
		//25792 - end
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

	protected void validateAtLeastOneSearchCriteria() {
		String searchCompanyName = searchSupplier == null ? StringUtils.EMPTY
				: searchSupplier.getCompanyName();
		String searchRegno = searchSupplier == null ? StringUtils.EMPTY
				: searchSupplier.getRegNo();
		
		//25792 - start
		if(!StringUtils.isBlank(accountType)) {
			if(SupplierManagementConst.SUPP_TYPE_BASIC.equalsIgnoreCase(accountType)) {
				if (StringUtils.isBlank(searchEpNo)
						&& StringUtils.isBlank(searchCompanyName)
						&& StringUtils.isBlank(searchRegno)
						&& StringUtils.isBlank(searchApplType)
						&& searchApplStatusId == null
						&& StringUtils.isBlank(searchApplNo)
						&& StringUtils.isBlank(supportingDocMode)){
					throw new EpException(SmCommonUtil.getMessage(
							"lbl.sm.search.criteria.atleast.one.field", null));
				}
			} else if(SupplierManagementConst.SUPP_TYPE_GOVT_TO_GOVT.equalsIgnoreCase(accountType)) {

				if (StringUtils.isBlank(agencyName) && StringUtils.isBlank(searchEpNo)) {
					throw new EpException(SmCommonUtil.getMessage(
							"lbl.sm.search.criteria.atleast.one.field", null));
				}			
			}
			else {
				if (searchPmRegStatusId == null && StringUtils.isBlank(searchMofNo)
						&& StringUtils.isBlank(searchEpNo)
						&& StringUtils.isBlank(searchCompanyName)
						&& StringUtils.isBlank(searchRegno)
						&& StringUtils.isBlank(searchApplType)
						&& searchApplStatusId == null
						&& StringUtils.isBlank(searchApplNo)
						&& StringUtils.isBlank(supportingDocMode)) {
					throw new EpException(SmCommonUtil.getMessage(
							"lbl.sm.search.criteria.atleast.one.field", null));
				}				
			}
		} 
		//25792 - end
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

	public boolean isSerializedFileExist() {
		return serializedFileExist;
	}

	public void setSerializedFileExist(boolean serializedFileExist) {
		this.serializedFileExist = serializedFileExist;
	}

	public String getSearchEpNo() {
		return searchEpNo;
	}

	public void setSearchEpNo(String searchEpNo) {
		this.searchEpNo = searchEpNo;
	}

	public String getSearchApplNo() {
		return searchApplNo;
	}

	public void setSearchApplNo(String searchApplNo) {
		this.searchApplNo = searchApplNo;
	}

	public String getSearchApplType() {
		return searchApplType;
	}

	public void setSearchApplType(String searchApplType) {
		this.searchApplType = searchApplType;
	}

	public Long getSearchApplStatusId() {
		return searchApplStatusId;
	}

	public void setSearchApplStatusId(Long searchApplStatusId) {
		this.searchApplStatusId = searchApplStatusId;
	}

	public List<SelectItem> getApplTypeItems() {
		return applTypeItems;
	}

	public void setApplTypeItems(List<SelectItem> applTypeItems) {
		this.applTypeItems = applTypeItems;
	}

	public List<SelectItem> getApplStatusItems() {
		return applStatusItems;
	}

	public void setApplStatusItems(List<SelectItem> applStatusItems) {
		this.applStatusItems = applStatusItems;
	}

	public String getSrcApplNo() {
		return srcApplNo;
	}

	public void setSrcApplNo(String srcApplNo) {
		this.srcApplNo = srcApplNo;
	}

	public String getTargetApplNo() {
		return targetApplNo;
	}

	public void setTargetApplNo(String targetApplNo) {
		this.targetApplNo = targetApplNo;
	}

	public PmZone getLoginUserZone() {
		return loginUserZone;
	}

	public void setLoginUserZone(PmZone loginUserZone) {
		this.loginUserZone = loginUserZone;
	}

	public List<PmCategoryL3> getDockYardFacilitiesVOList() {
		return dockYardFacilitiesVOList;
	}

	public void setDockYardFacilitiesVOList(
			List<PmCategoryL3> dockYardFacilitiesVOList) {
		this.dockYardFacilitiesVOList = dockYardFacilitiesVOList;
	}

	public List<PmCategoryL3> getStoreFacilitiesVOList() {
		return storeFacilitiesVOList;
	}

	public void setStoreFacilitiesVOList(
			List<PmCategoryL3> storeFacilitiesVOList) {
		this.storeFacilitiesVOList = storeFacilitiesVOList;
	}

	public List<PmCategoryL3> getWorkshopFacilitiesVOList() {
		return workshopFacilitiesVOList;
	}

	public void setWorkshopFacilitiesVOList(
			List<PmCategoryL3> workshopFacilitiesVOList) {
		this.workshopFacilitiesVOList = workshopFacilitiesVOList;
	}

	public List<PmCategoryL3> getFactoryFacilitiesVOList() {
		return factoryFacilitiesVOList;
	}

	public void setFactoryFacilitiesVOList(
			List<PmCategoryL3> factoryFacilitiesVOList) {
		this.factoryFacilitiesVOList = factoryFacilitiesVOList;
	}

	public boolean isUpdatePersonnelPage() {
		return updatePersonnelPage;
	}

	public void setUpdatePersonnelPage(boolean updatePersonnelPage) {
		this.updatePersonnelPage = updatePersonnelPage;
	}

	public List<SmSupplierCategory> getPendingApprovedCategoryList() {
		return pendingApprovedCategoryList;
	}

	public void setPendingApprovedCategoryList(
			List<SmSupplierCategory> pendingApprovedCategoryList) {
		this.pendingApprovedCategoryList = pendingApprovedCategoryList;
	}

	public boolean isBpkUser() {
		return bpkUser;
	}

	public void setBpkUser(boolean bpkUser) {
		this.bpkUser = bpkUser;
	}

	public String getPoApprOverlappedRemark() {
		return poApprOverlappedRemark;
	}

	public void setPoApprOverlappedRemark(String poApprOverlappedRemark) {
		this.poApprOverlappedRemark = poApprOverlappedRemark;
	}

	public String getPoUserName() {
		return poUserName;
	}

	public void setPoUserName(String poUserName) {
		this.poUserName = poUserName;
	}

	public String getPoRemark() {
		return poRemark;
	}

	public void setPoRemark(String poRemark) {
		this.poRemark = poRemark;
	}

	public Date getPoRemarkCreatedDate() {
		return poRemarkCreatedDate;
	}

	public void setPoRemarkCreatedDate(Date remarkCreatedDate) {
		this.poRemarkCreatedDate = remarkCreatedDate;
	}

	public String getApproverUserName() {
		return approverUserName;
	}

	public void setApproverUserName(String approverUserName) {
		this.approverUserName = approverUserName;
	}

	public String getApproverRemark() {
		return approverRemark;
	}

	public void setApproverRemark(String approverRemark) {
		this.approverRemark = approverRemark;
	}

	public Date getApproverRemarkCreatedDate() {
		return approverRemarkCreatedDate;
	}

	public void setApproverRemarkCreatedDate(Date remarkCreatedDate) {
		this.approverRemarkCreatedDate = remarkCreatedDate;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getOriPoApprOverlappedRemark() {
		return oriPoApprOverlappedRemark;
	}

	public void setOriPoApprOverlappedRemark(String oriPoApprOverlappedRemark) {
		this.oriPoApprOverlappedRemark = oriPoApprOverlappedRemark;
	}

	public Long getRemarkId() {
		return remarkId;
	}

	public void setRemarkId(Long remarkId) {
		this.remarkId = remarkId;
	}

	public SmCancelMofRemarksVO getCancelRemarkVO() {
		return cancelRemarkVO;
	}

	public void setCancelRemarkVO(SmCancelMofRemarksVO cancelRemarkVO) {
		this.cancelRemarkVO = cancelRemarkVO;
	}

	public List<SmRemark> getApproverRemarkList() {
		return approverRemarkList;
	}

	public void setApproverRemarkList(List<SmRemark> approverRemarkList) {
		this.approverRemarkList = approverRemarkList;
	}

	public Long getRemarkOverlappedId() {
		return remarkOverlappedId;
	}

	public void setRemarkOverlappedId(Long remarkOverlappedId) {
		this.remarkOverlappedId = remarkOverlappedId;
	}
	//26238 - start
	public SmSupplierOwner getSmSuppOwner() {
		return smSuppOwner;
	}

	public void setSmSuppOwner(SmSupplierOwner smSuppOwner) {
		this.smSuppOwner = smSuppOwner;
	}
	//26238 - end

	//25792 - start
		private class SupplierItemPagingDataModel extends PagingDataModel<CmSupplierItem> {

			public SupplierItemPagingDataModel() {
				super("supplierItemTableId");
				
			}
			@SuppressWarnings("rawtypes")
			@Override
			protected ISearchResultBean<CmSupplierItem> pageChangeListener(
					ISearchBean<CmSupplierItem> searchBean) {
				
				ICatalogueSO catalogueSO = (ICatalogueSO) BusinessDelegate
						.getInstance().getServiceObject("cm", ICatalogueSO.class);

				CatalogueReqDTO reqDTO = new CatalogueReqDTO();

				try {
					CatalogueBackingBean bb = new CatalogueBackingBean();
					bb.getManagedBean().setExpiredTabPageSize(this.getPageSize());
					
					System.out.println("Supplier: " + getSmSupplier().getSupplierId());
					System.out.println("CatalogueInfo: " + getCatalogueInfoItemStatus());
					reqDTO.setSupplierId(getSmSupplier().getSupplierId());
					reqDTO.setItemStatus(getCatalogueInfoItemStatus());
					reqDTO.setSearchBean(searchBean);
					CatalogueResDTO resDTO = catalogueSO
							.searchCmSupplierItemByStatus(reqDTO);
									
					return resDTO.getSuppItemResultBean();

				} catch (Exception e) {
					e.printStackTrace();
					return new SearchResultBean();
				} finally {
					catalogueSO = null;
				}
			}
			
		}
		
	
	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
	public String getAgencyTypeName() {
		return agencyTypeName;
	}

	public void setAgencyTypeName(String agencyTypeName) {
		this.agencyTypeName = agencyTypeName;
	}

	public boolean isG2GSupplierAdmin() {
		return isG2GSupplierAdmin;
	}

	public void setG2GSupplierAdmin(boolean isG2GSupplierAdmin) {
		this.isG2GSupplierAdmin = isG2GSupplierAdmin;
	}	
	
	public Long getAgencyType() {
		return agencyType;
	}
	
	public void setAgencyType(Long agencyType) {
		this.agencyType = agencyType;
	}
	
	public String getAgencyName() {
		return agencyName;
	}
	
	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}
	
	public String getSapVendorCode() {
		return sapVendorCode;
	}

	public void setSapVendorCode(String sapVendorCode) {
		this.sapVendorCode = sapVendorCode;
	}
	
	public SmPersonnelAddress getAddressAsInIc() {
		return addressAsInIc;
	}

	public void setAddressAsInIc(SmPersonnelAddress addressAsInIc) {
		this.addressAsInIc = addressAsInIc;
	}
	
	public SmPersonnelAddress getCorrespondenceAddress() {
		return correspondenceAddress;
	}

	public void setCorrespondenceAddress(SmPersonnelAddress correspondenceAddress) {
		this.correspondenceAddress = correspondenceAddress;
	}
	
	public PmDigiCert getPmDigiCert() {
		return pmDigiCert;
	}

	public void setPmDigiCert(PmDigiCert pmDigiCert) {
		this.pmDigiCert = pmDigiCert;
	}
	public String getCatalogueInfoItemStatus() {
		return catalogueInfoItemStatus;
	}
	public void setCatalogueInfoItemStatus(String catalogueInfoItemStatus) {
		this.catalogueInfoItemStatus = catalogueInfoItemStatus;
	}
	public int getCatalogueInfoTabActiveIndex() {
		return catalogueInfoTabActiveIndex;
	}
	public void setCatalogueInfoTabActiveIndex(int catalogueInfoTabActiveIndex) {
		this.catalogueInfoTabActiveIndex = catalogueInfoTabActiveIndex;
	}

	public IPagingDataModel<CmSupplierItem> getSupplierItemSearchDataModel() {
		return supplierItemSearchDataModel;
	}

	public List<CmRequestCode> getRequestCodeList() {
		return requestCodeList;
	}

	public void setRequestCodeList(List<CmRequestCode> requestCodeList) {
		this.requestCodeList = requestCodeList;
	}

	public boolean isRedirectToSupplierItemPage() {
		return redirectToSupplierItemPage;
	}

	public void setRedirectToSupplierItemPage(boolean redirectToSupplierItemPage) {
		this.redirectToSupplierItemPage = redirectToSupplierItemPage;
	}

	public CmSupplierItem getSelectedSupplierItem() {
		return selectedSupplierItem;
	}

	public void setSelectedSupplierItem(CmSupplierItem selectedSupplierItem) {
		this.selectedSupplierItem = selectedSupplierItem;
	}

	public int getSupplierItemDetailTabActiveIndex() {
		return supplierItemDetailTabActiveIndex;
	}

	public void setSupplierItemDetailTabActiveIndex(
			int supplierItemDetailTabActiveIndex) {
		this.supplierItemDetailTabActiveIndex = supplierItemDetailTabActiveIndex;
	}

	public int getPricingTypeGroupTabActiveIndex() {
		return pricingTypeGroupTabActiveIndex;
	}

	public void setPricingTypeGroupTabActiveIndex(int pricingTypeGroupTabActiveIndex) {
		this.pricingTypeGroupTabActiveIndex = pricingTypeGroupTabActiveIndex;
	}

	public int getPricingTypeTabActiveIndex() {
		return pricingTypeTabActiveIndex;
	}

	public void setPricingTypeTabActiveIndex(int pricingTypeTabActiveIndex) {
		this.pricingTypeTabActiveIndex = pricingTypeTabActiveIndex;
	}

	public List<CmSupplierZone> getSupplierZoneList() {
		return supplierZoneList;
	}

	public void setSupplierZoneList(List<CmSupplierZone> supplierZoneList) {
		this.supplierZoneList = supplierZoneList;
	}

	public int getPendingApprovalSupplierItemSize() {
		return pendingApprovalSupplierItemSize;
	}

	public void setPendingApprovalSupplierItemSize(
			int pendingApprovalSupplierItemSize) {
		this.pendingApprovalSupplierItemSize = pendingApprovalSupplierItemSize;
	}

	public int getApprovedSupplierItemSize() {
		return approvedSupplierItemSize;
	}

	public void setApprovedSupplierItemSize(int approvedSupplierItemSize) {
		this.approvedSupplierItemSize = approvedSupplierItemSize;
	}

	public int getRejectedSupplierItemSize() {
		return rejectedSupplierItemSize;
	}

	public void setRejectedSupplierItemSize(int rejectedSupplierItemSize) {
		this.rejectedSupplierItemSize = rejectedSupplierItemSize;
	}

	public int getPublishedSupplierItemSize() {
		return publishedSupplierItemSize;
	}

	public void setPublishedSupplierItemSize(int publishedSupplierItemSize) {
		this.publishedSupplierItemSize = publishedSupplierItemSize;
	}

	public int getExpiredSupplierItemSize() {
		return expiredSupplierItemSize;
	}

	public void setExpiredSupplierItemSize(int expiredSupplierItemSize) {
		this.expiredSupplierItemSize = expiredSupplierItemSize;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public String getApplicationStatusDesc() {
		return applicationStatusDesc;
	}

	public void setApplicationStatusDesc(String applicationStatusDesc) {
		this.applicationStatusDesc = applicationStatusDesc;
	}

	//25792 - end	
	
	// #29664 - start
	public String getPoRemarkAfterSiteVisit() {
		return poRemarkAfterSiteVisit;
	}

	public void setPoRemarkAfterSiteVisit(String poRemarkAfterSiteVisit) {
		this.poRemarkAfterSiteVisit = poRemarkAfterSiteVisit;
	}

	public Date getPoRemarkAfterSiteVisitCreatedDate() {
		return poRemarkAfterSiteVisitCreatedDate;
	}

	public void setPoRemarkAfterSiteVisitCreatedDate(Date poRemarkAfterSiteVisitCreatedDate) {
		this.poRemarkAfterSiteVisitCreatedDate = poRemarkAfterSiteVisitCreatedDate;
	}

	public String getSecondPoUserName() {
		return secondPoUserName;
	}

	public void setSecondPoUserName(String secondPoUserName) {
		this.secondPoUserName = secondPoUserName;
	}
	// #29664 - end

	// #26639 - start
	public boolean isViewSupplierMaintenancePage() {
		return viewSupplierMaintenancePage;
	}

	public void setViewSupplierMaintenancePage(boolean viewSupplierMaintenancePage) {
		this.viewSupplierMaintenancePage = viewSupplierMaintenancePage;
	}
	// #26639 - end

	public boolean isBasicAccountMaintenance() {
		return basicAccountMaintenance;
	}

	public void setBasicAccountMaintenance(boolean basicAccountMaintenance) {
		this.basicAccountMaintenance = basicAccountMaintenance;
	}
	
	//28281 - start
	public boolean isWarning() {
		return warning;
	}

	public void setWarning(boolean warning) {
		this.warning = warning;
	}

	public boolean isCancelCategoryCode() {
		return cancelCategoryCode;
	}

	public void setCancelCategoryCode(boolean cancelCategoryCode) {
		this.cancelCategoryCode = cancelCategoryCode;
	}

	public boolean isSuspendRegistration() {
		return suspendRegistration;
	}

	public void setSuspendRegistration(boolean suspendRegistration) {
		this.suspendRegistration = suspendRegistration;
	}

	public boolean isCancelRegistration() {
		return cancelRegistration;
	}

	public void setCancelRegistration(boolean cancelRegistration) {
		this.cancelRegistration = cancelRegistration;
	}

	public boolean isBlacklistOwnerOrDirector() {
		return blacklistOwnerOrDirector;
	}

	public void setBlacklistOwnerOrDirector(boolean blacklistOwnerOrDirector) {
		this.blacklistOwnerOrDirector = blacklistOwnerOrDirector;
	}

	public boolean isBumiputeraStatusCancel() {
		return bumiputeraStatusCancel;
	}

	public void setBumiputeraStatusCancel(boolean bumiputeraStatusCancel) {
		this.bumiputeraStatusCancel = bumiputeraStatusCancel;
	}

	public boolean isManufacturerStatusCancel() {
		return manufacturerStatusCancel;
	}

	public void setManufacturerStatusCancel(boolean manufacturerStatusCancel) {
		this.manufacturerStatusCancel = manufacturerStatusCancel;
	}

	public List<DisciplinaryActionVO> getDisciplinaryActionVoList() {
		return disciplinaryActionVoList;
	}

	public void setDisciplinaryActionVoList(List<DisciplinaryActionVO> disciplinaryActionVoList) {
		this.disciplinaryActionVoList = disciplinaryActionVoList;
	}

	public List<SmPersonnel> getOwnerAndDirectorList() {
		return ownerAndDirectorList;
	}

	public void setOwnerAndDirectorList(List<SmPersonnel> ownerAndDirectorList) {
		this.ownerAndDirectorList = ownerAndDirectorList;
	}

	public List<SmPersonnel> getSelectedOwnerAndDirectorList() {
		return selectedOwnerAndDirectorList;
	}

	public void setSelectedOwnerAndDirectorList(List<SmPersonnel> selectedOwnerAndDirectorList) {
		this.selectedOwnerAndDirectorList = selectedOwnerAndDirectorList;
	}

	public String getApplType() {
		return applType;
	}

	public void setApplType(String applType) {
		this.applType = applType;
	} 

	public boolean isViewFlag() {
		return viewFlag;
	}

	public void setViewFlag(boolean viewFlag) {
		this.viewFlag = viewFlag;
	}

	public SmSupplier getSmSupp() {
		return smSupp;
	}

	public void setSmSupp(SmSupplier smSupp) {
		this.smSupp = smSupp;
	}

	public boolean isViewMode() {
		return viewMode;
	}

	public void setViewMode(boolean viewMode) {
		this.viewMode = viewMode;
	}
	
	public void setMofNoExist(boolean mofNoExist) {
		this.mofNoExist = mofNoExist;
	}

	public List<BlacklistOwnerDirectorVO> getBlacklistownerDirectorList() {
		return blacklistownerDirectorList;
	}

	public void setBlacklistownerDirectorList(List<BlacklistOwnerDirectorVO> blacklistownerDirectorList) {
		this.blacklistownerDirectorList = blacklistownerDirectorList;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public boolean isNoDisciplinary() {
		return noDisciplinary;
	}

	public void setNoDisciplinary(boolean noDisciplinary) {
		this.noDisciplinary = noDisciplinary;
	}

	public List<DisciplinaryActionCategoryVO> getCategoryCodeList() {
		return categoryCodeList;
	}

	public void setCategoryCodeList(List<DisciplinaryActionCategoryVO> categoryCodeList) {
		this.categoryCodeList = categoryCodeList;
	}
	public boolean isWarningExist() {
		return warningExist;
	}
	public void setWarningExist(boolean warningExist) {
		this.warningExist = warningExist;
	}
	
	public boolean isMofNoExist() {
		return mofNoExist;
	}

	public List<DisciplinaryActionVO> getDisciplineVOs() {
		return disciplineVOs;
	}

	public void setDisciplineVOs(List<DisciplinaryActionVO> disciplineVOs) {
		this.disciplineVOs = disciplineVOs;
	}

	public boolean isDisableDownloadButtonDAHistory() {
		return disableDownloadButtonDAHistory;
	}

	public void setDisableDownloadButtonDAHistory(boolean disableDownloadButtonDAHistory) {
		this.disableDownloadButtonDAHistory = disableDownloadButtonDAHistory;
	}
	
	//28281 - end
	
	public boolean isFlagBlacklistCancel() {
		return flagBlacklistCancel;
	}

	public void setFlagBlacklistCancel(boolean flagBlacklistCancel) {
		this.flagBlacklistCancel = flagBlacklistCancel;
	}

	public boolean isViewCatCode() {
		return viewCatCode;
	}

	public void setViewCatCode(boolean viewCatCode) {
		this.viewCatCode = viewCatCode;
	}

	public boolean isFlagShowButtonSubmit() {
		return flagShowButtonSubmit;
	}

	public void setFlagShowButtonSubmit(boolean flagShowButtonSubmit) {
		this.flagShowButtonSubmit = flagShowButtonSubmit;
	}

	public List<SmApplInquiry> getPoSupplierInquiryHistory() {
		return poSupplierInquiryHistory;
	}

	public void setPoSupplierInquiryHistory(List<SmApplInquiry> poSupplierInquiryHistory) {
		this.poSupplierInquiryHistory = poSupplierInquiryHistory;
	}

	public SmApplInquiry getPoToSupplierInquiry() {
		return poToSupplierInquiry;
	}

	public void setPoToSupplierInquiry(SmApplInquiry poToSupplierInquiry) {
		this.poToSupplierInquiry = poToSupplierInquiry;
	}

	public List<SmApplSupportDoc> getMissinDocs() {
		return missinDocs;
	}

	public void setMissinDocs(List<SmApplSupportDoc> missinDocs) {
		this.missinDocs = missinDocs;
	}
	
	// #29617 start
	public SmSmeSupplier getSmeSupplier() {
		return smeSupplier;
	}

	public void setSmeSupplier(SmSmeSupplier smeSupplier) {
		this.smeSupplier = smeSupplier;
	}
	// #29617 end
	
	//START 31111
	public List<SmBulkApplOverlapCatVO> getOverlapCategoryVOList() {
		return overlapCategoryVOList;
	}

	public void setOverlapCategoryVOList(List<SmBulkApplOverlapCatVO> overlapCategoryVOList) {
		this.overlapCategoryVOList = overlapCategoryVOList;
	}
	//END 31111

	public List<SmApplRejectReason> getSmApplRejectReasonsByPO() {
		return smApplRejectReasonsByPO;
	}

	public void setSmApplRejectReasonsByPO(List<SmApplRejectReason> smApplRejectReasonsByPO) {
		this.smApplRejectReasonsByPO = smApplRejectReasonsByPO;
	}

	public String getRejectReasonByPO() {
		return rejectReasonByPO;
	}

	public void setRejectReasonByPO(String rejectReasonByPO) {
		this.rejectReasonByPO = rejectReasonByPO;
	}
	
	public String getSdoRemark() {
		return sdoRemark;
	}

	public void setSdoRemark(String sdoRemark) {
		this.sdoRemark = sdoRemark;
	}
}
