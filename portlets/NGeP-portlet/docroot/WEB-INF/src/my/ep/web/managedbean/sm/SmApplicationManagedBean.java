package my.ep.web.managedbean.sm;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.bean.search.SearchResultBean;
import my.ep.constants.CommonConstants;
import my.ep.dto.pm.common.reason.ReasonReqDTO;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCertBody;
import my.ep.persistence.entity.pm.common.PmCity;
import my.ep.persistence.entity.pm.common.PmCountry;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.pm.common.PmDistrict;
import my.ep.persistence.entity.pm.common.PmEduField;
//import my.ep.persistence.entity.pm.common.PmEduLevel;
import my.ep.persistence.entity.pm.common.PmFinancialOrg;
import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmRace;
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
import my.ep.persistence.entity.sm.SmAttachment;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmCompanyExperienceCat;
import my.ep.persistence.entity.sm.SmEquityCompany;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmGstSupplierDoc;
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
import my.ep.persistence.entity.sm.SmSupplierSystem;
import my.ep.persistence.entity.sm.SmSvTask;
import my.ep.type.aut.SearchOperator;
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.pm.cc.SpkiBackingBean;
import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.bean.sm.SvcTemplateBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SmApplStatusInfoVO;
import my.ep.web.vo.SmPersonnleCalcVO;
import my.ep.web.vo.SmSectionEditorVO;
import my.ep.web.vo.SmSiteVisitFacilityVO;
import my.ep.web.vo.SmSupplierBasicInfoVO;

import org.apache.commons.lang3.StringUtils;

import com.liferay.portal.model.Role;

@ManagedBean(name = "smApplMB")
@SessionScoped
public class SmApplicationManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;
	private boolean showFieldChanges;
	private SmAppl latestAppl;
	private SmAssociateCompany latestAssocCompany;
	private SmAssociationBody latestProfBody;
	private SmPersonnel latestPersonnel;
	private SmPersonnelAddress latestPermAddr;
	private SmPersonnelAddress latestMailAddr;
	private SmPersonnelAddress latestResdAddr;
	private SmPersonnelAddress latestOffAddr;
	private SmStaffExperience latestJobFunction;
	private SmRelatedCompany latestRelatedCompany;
	private SmAssociationBody latestStfPrfBdy;
	private SmStaffAcademic latestStaffAcademic;
	private SmEquityCompany latestEquityCompany;
	private SmSupplierBranch latestBranch;
	private SmSupplierBank latestBank;
	private SmCompanyExperience latestCompanyExperience;
	private SmSupplierCert latestCert;
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
	private List<SmCompanyExperienceCat> selectedCompExpCats;
	private SmCompanyExperience selectedCompanyExperience;
	private String selectedParentCat;
	private String selectedChildCat;
	private String selectedSubChildCat;
	private List<PmCategoryL1> defaultParentCatList;
	private SmSupplierCategory selectedCategory;
	private List<SmSupplierCategory> selectedCategories;
	private List<SmSupplierCategory> deleteCategoryList;
	private List<SmSupplierCategory> deleteNewApplCategoryList;
	private List<SmSupplierCategory> deleteExistingCategoryList;
	private List<SmSupplierCategory> activeCategoryList;
	private List<SmSupplierCategory> approvedCategoryList;
	private List<SmSupplierCategory> splCategoryList;
	private List<SmAssociationBody> nonPersonnelProfBodyList;
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
	private List<SmSupplierSystem> selectedSupplierSystemList;
	private SmMachineDtl selectedMachine;
	private SmSupplierSystem selectedSystem;
	private SmEquityCompany selectedEquityCompany;
	private List<SmEquityCompany> selectedEquityCompanies;
	private List<SmPersonnel> smapplShareHolders;
	private List<SmPersonnel> selectedShareHolders;
	private SmPersonnel selectedShareHolder;

	private List<SmStaffExperience> selectedShrHldrJobFunctions;
	private SmStaffExperience selectedJobFunction;
	private List<SmAssociationBody> selectedShrHldrProfBodies;
	private SmAssociationBody selectedStfPrfBdy;
	private SmSvTask svTask;
	private List<SmSvTask> svTaskList;
	private List<SmRelatedCompany> selectedShrHldrRelatedCompanies;
	private SmRelatedCompany selectedRelatedCompany;
	private List<SmStaffAcademic> selectedShrHldrAcademics;
	private SmStaffAcademic selectedStaffAcademic;
	private boolean valueReqd;
	private boolean rejectReasonReqd;
	private boolean showParentMsg;
	private SmPersonnelAddress shareHolderPermAddr;
	private SmPersonnelAddress shareHolderMailAddr;
	private SmPersonnelAddress shareHolderResdAddr;
	private SmPersonnelAddress shareHolderOffAddr;
	private String expiryDate;
	private List<SmPersonnel> selectedPersonnels;
	private SmPersonnel selectedPersonnel;
	private SmPersonnel addrUpdatePersonnel;
	private List<SmStaffExperience> selectedPersJobFunctions;
	private List<SmAssociationBody> selectedPersProfBodies;
	private List<SmRelatedCompany> selectedPersRelatedCompanies;
	private List<SmStaffAcademic> selectedPersAcademics;
	private List<SmPersonnel> prevApprovedPersonnels;
	private String supplierRemarks;

	private String businessTypeVal;
	private String personnelInfoHeader;
	// Bank Info
	private Long[] chequeSingPerId;
	private Long[] selectedBankBranchIds;
	private Long[] selectedSuppCategoryFacilityIds;
	private Long selBankBranchId;
	private Long selSuppCategoryFacilityId;
	private SmSupplierBank selectedBank;
	private SmSupplierBank selectedSignatoryBankAcct;
	private List<SmSupplierBank> selectedSignatoryBankAccts;
	private List<SmSupplierBank> selectedBanks;
	private String delPendAckMsg;
	private boolean poUser;
	private boolean supplierUser;
	private boolean approverUser;
	private boolean mofSupplier;
	private String decisionAckMsg;
	// TODO change the type later
	private List<SvcTemplateBean> selectedTemplateList;
	private String[] bpmActions;
	private String bpmTaskName;
	private String selectedBpmAction;
	private String taskId;
	private String headerMsg;
	private String poSiteVisitType;
	private boolean testBlock;
	private int maxReviewCount;
	// private int authPerCount;
	private SmApplSectionReview poSecReview;
	private SmApplSectionReview apprvrSecReview;
	private boolean termsAccepted;
	private boolean othersSystem;
	private boolean othersMachine;
	private String newSupplierType;
	private String catTypeConfirm;
	private SmFacility basicCompanyFacility;
	private SmApplSupportDoc supportDoc;
	// Address fields for all the forms
	private List<PmEduField> eduFieldList;
	private List<PmParameterDesc> eduLevelList;
	private List<PmCountry> countryLs;
	private List<SelectItem> basicCompanyStates;
	private List<SelectItem> basicCompanyDistricts;
	private List<SelectItem> basicCompanyCities;
	private List<PmRace> racesList;

	private List<SelectItem> basicOffFacStates;
	private List<SelectItem> basicOffFacDistricts;
	private List<SelectItem> basicOffFacCities;

	private List<SelectItem> branchCompanyStates;
	private List<SelectItem> branchCompanyDistricts;
	private List<SelectItem> branchCompanyCities;

	private List<SelectItem> assocCompanyStates;
	private List<SelectItem> assocCompanyDistricts;
	private List<SelectItem> assocCompanyCities;

	private List<SelectItem> equiCompanyStates;
	private List<SelectItem> equiCompanyDistricts;
	private List<SelectItem> equiCompanyCities;

	private List<SelectItem> factoryFacilityStates;
	private List<SelectItem> factoryFacilityDistricts;
	private List<SelectItem> factoryFacilityCities;

	private List<SelectItem> dockFacilityStates;
	private List<SelectItem> dockFacilityDistricts;
	private List<SelectItem> dockFacilityCities;

	private List<SelectItem> workShopFacilityStates;
	private List<SelectItem> workShopFacilityDistricts;
	private List<SelectItem> workShopFacilityCities;

	private List<SelectItem> storeFacilityStates;
	private List<SelectItem> storeFacilityDistricts;
	private List<SelectItem> storeFacilityCities;

	private List<SelectItem> shrHldrPermStates;
	private List<SelectItem> shrHldrPermDistricts;
	private List<SelectItem> shrHldrPermCities;

	private List<SelectItem> shrHldrOffStates;
	private List<SelectItem> shrHldrOffDistricts;
	private List<SelectItem> shrHldrOffCities;

	private List<SelectItem> shrHldrMailStates;
	private List<SelectItem> shrHldrMailDistricts;
	private List<SelectItem> shrHldrMailCities;

	private List<SelectItem> shrHldrResdStates;
	private List<SelectItem> shrHldrResdDistricts;
	private List<SelectItem> shrHldrResdCities;

	private List<SelectItem> salutationSelectItems;

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

	private List<SelectItem> bankStates;
	private List<SelectItem> bankDistricts;
	private List<SelectItem> bankCities;
	
	private List<Long> svTaskIdsNeedToDelete;

	private List<PmCertBody> certBodyLs;
	private List<PmCertBody> profBodyLs;
	private List<PmFinancialOrg> finOrgLs;
	private List<PmUom> uomLs;
	private List<SelectItem> uomSelectItems;
	private List<SelectItem> raceSelectItems;
	private List<SelectItem> profBodyItems;
	private List<PmReasonDesc> reasonDescList;
	private List<PmReasonDesc> reasonDescInitList;
	private List<PmReasonDesc> deletePendingApplDescList;
	private List<PmReasonDesc> selectedReasonDescs;
	private Long rejectionOtherReasonId;
	private Long deletePendingApplOtherReasonId;
	private List<PmStatusDesc> regStatusDescList;

	private String currentStatus;
	private String applRegStatus;
	private Long[] reasonId;
	private SmPersonnleCalcVO personCalc;
	private BigDecimal equiPerCapTotal;
	private BigDecimal equiCompCapTotal;
	private BigDecimal bumiEquityPercentage;
	private boolean validResubmit;
	private String resubmitInValidMsg;
	private boolean draftedAppl;
	private String currentPageType;
	private boolean redirectToPersonPage;
	private PmParameter perIdTypeParam;
	private String facilityType;
	private PmCountry defPmCountry;
	private List<SelectItem> ePRoleList;
	private boolean removeEpRole;
	private List<SmApplSupportDoc> missinDocs;
	private List<SmApplSupportDoc> mandatoryDocs;
	private List<SmApplSupportDoc> nonMandatoryDocs;
	private boolean reviewSiteVisit;
	private SelectItem[] businessTypeLs;
	private List<String> supplierToPOList;
	private String reasonDesc;
	private boolean validSmUserLimit;
	private boolean userWithRole;
	private boolean validEquityLimit;
	private String today;
	private String revSupUrl;
	private String revAppUrl;
	private String revisionBaseUrl;
	private String revisionSupplierAppNoParam;
	private String revisionApproverAppNoParam;
	private String bumiRecommendationNote;
	private boolean qualifiedForBumi;
	private Date currDate;
	private SelectItem[] identificationTypes;
	private SmAttachment cancellationAttachment;
	private boolean disableIcEmail;
	private boolean selectedAll;
	private boolean selected;
	private int personnelCountCheck;
	private String overLappedCategoryMsg;
	private String overLappedCategoryRemarks;
	private boolean overLappedCategoryExists;
	private boolean disAllowOwnerEdit;
	private boolean disabledHomeOffice;
	private Integer currYear;
	private String typeOfBusiness;
	private SmSupplierBasicInfoVO basicInfo;
	private boolean allowedToApprove;
	private boolean disabledJustification;
	private String regCatCodeConfirmHeader;
	private String regCatCodeConfirmMsg;
	private BigDecimal rentPrevVal;
	private BigDecimal purPricePrevVal;
	private String ownerTypePrevVal;
	private boolean hasSuppDocToDownload;
	private boolean showFactoryPanel;
	private boolean showStorePanel;
	private boolean showDockyardPanel;
	private boolean showWorkShopPanel;
	private boolean showPersonnelViewOnly;
	private boolean showTravelSoftwarePanel;
	private boolean showMachineryEquipmentPanel;
	private boolean disabledShareholderPersonnelNameAndNationality;
	private boolean alreadyBusinessSpecialCatcode;
	private String businessOfficeCatCodeMsg;
	private String selectedShareholderIdentificationType;
	private String revisionApplType;
	private Long myCountryId;
	private String applnApprovalRemarks;
	private String applnRejectionRemarks;
	private String applnOverLapRemarks;
	private String poToSvcRmrk;
	private PmParameter certificate;
	private PmParameter diploma;
	private PmParameter glpCert;
//	private PmEduLevel certEduLevel;
//	private PmEduLevel diplomaEduLevel;
	private PmParameterDesc glp;
	private List<SelectItem> acadQualifications;
	private List<SelectItem> travellingSoftwareItems;
	private List<SelectItem> categoryTypeItems;
	private List<SelectItem> categoryCodeItems;
	private List<SelectItem> machineItems;
	private Long prevBasicStateId;
	private SmApplStatusInfoVO applStatusInfo;
	private PmDigiSign pmDigiSign;
	private boolean disabledCompanyName;
	private boolean disabledBusinessType;
	private boolean disableWithFedralTransaction;
	private boolean disableBranchGst;
	private boolean roleStaffAndPermAddrReqd;
	private boolean roleStaffAndMailAddrReqd;
	private String mofNumber;
	private List<SelectItem> countryItems;
	private SmGstSupplier companyBasicGst;
	private SmGstSupplier selectedGst;
	private SmGstSupplierDoc selectedGstSuppDoc;
	private List<SmSiteVisitFacilityVO> siteVisitFacVOList;
	private boolean branchSameAsHq;
	private String facAddresSameAs;
	private List<SelectItem> addrSameAsItems;
	private boolean claimTask;
	private boolean disableRoleSelection;
	private boolean siteVisitAfterApproval;
	private boolean poInquiryRespond;
	private boolean changeCurrRegStatus;
	private boolean showPoApprovedOrRecommendButton;
	private boolean showNewChanges;

	private String businessTypeParamCode;
	private String physicalConsulting;
	private String personType;
	private String currRegStatus;

	private BigDecimal currBumiEquity;
	private BigDecimal currNonBumiEquity;
	private BigDecimal currForeignerEquity;
	private BigDecimal currGovtUnitrustEquity;
	private BigDecimal currGovtEquity;
	private BigDecimal currForeignCompanyEquity;
	private BigDecimal currLocalCompanyEquity;

	private SmAppl supplierAppl;
	private SmAppl poAppl;

	private SmSectionEditorVO editVO;
	private List<PmReasonDesc> selectedPmReasonDescs;
	private String searchRejectReason;
	private IPagingDataModel<PmReasonDesc> rejectReasonDataModel = new RejectReasonPagingDataModel();

	private class RejectReasonPagingDataModel extends
			PagingDataModel<PmReasonDesc> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public RejectReasonPagingDataModel() {
			super("rejectReasonDtId");
		}

		@Override
		protected ISearchResultBean<PmReasonDesc> pageChangeListener(
				ISearchBean<PmReasonDesc> searchBean) {
			ReasonReqDTO reqDTO = new ReasonReqDTO();
			reqDTO.setReasonDescSearchBean(searchBean);
			reqDTO.setLanguage(new FacesScopes().getUserLocale().getLanguage());
			try {
				return getRejectDescResultBean(reqDTO);
			} catch (Exception ex) {
				return new SearchResultBean();
			} finally {
				reqDTO = null;
			}
		}

		private ISearchResultBean<PmReasonDesc> getRejectDescResultBean(
				ReasonReqDTO reqDTO) {
			reqDTO.getReasonDescSearchBean().getDataFilterBeans().clear();
			if (!StringUtils.isNotBlank(getSearchRejectReason())) {
				SearchFilterBean reasonDescFilterBean = new SearchFilterBean(
						SearchOperator.OPERATOR_LIKE.value());
				reasonDescFilterBean.setSearchField("reasonDesc");
				reasonDescFilterBean.setSearchValue(getSearchRejectReason());
				reqDTO.getReasonDescSearchBean().getDataFilterBeans()
						.add(reasonDescFilterBean);
			}
			return SmServiceLookup.getReasonSO()
					.retrieveReasonDescBySearchBean(reqDTO)
					.getReasonDescResultBean();
		}
	}

	public void searchRejectReasonDesc() {
		rejectReasonDataModel.reloadData();
	}

	/** The spki backing bean. */
	@ManagedProperty(value = "#{spkiBackingBean}")
	private SpkiBackingBean spkiBackingBean;

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
				|| sc.hasRoleUser(SupplierManagementConst.SUPPLIER_USER)
				|| sc.hasRoleUser(RoleCode.FL_USER.value())
				|| sc.hasRoleUser(RoleCode.SUPPLIER_TEMP.value())) {
			setSupplierUser(true);
			if (sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_ADMIN)
					|| sc.hasRoleUser(SupplierManagementConst.MOF_SUPPLIER_USER)) {
				setMofSupplier(true);
			}
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

		editVO = new SmSectionEditorVO();

		selectedCategories = new ArrayList<SmSupplierCategory>();
		selectedBranch = new SmSupplierBranch();
		selectedBranch.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
		selectedBranch.setSmPersonnel(new SmPersonnel());
		selectedBranches = new ArrayList<SmSupplierBranch>();
		selectedAssocCompany = new SmAssociateCompany();
		selectedAssocCompany.setSmAddress(SmApplicationUtil
				.setDefaultAddr(null));
		selectedAssocCompanies = new ArrayList<SmAssociateCompany>();
		selectedEquityCompany = new SmEquityCompany();
		selectedEquityCompany.setSmAddress(SmApplicationUtil
				.setDefaultAddr(null));
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
		selectedShareHolder.setPmCountry(SmApplicationUtil.setDefaultAddr(null)
				.getPmCountry());
		smapplShareHolders = new ArrayList<SmPersonnel>();
		selectedShareHolders = new ArrayList<SmPersonnel>();
		selectedShrHldrAcademics = new ArrayList<SmStaffAcademic>();
		selectedShrHldrJobFunctions = new ArrayList<SmStaffExperience>();
		selectedShrHldrProfBodies = new ArrayList<SmAssociationBody>();
		selectedShrHldrRelatedCompanies = new ArrayList<SmRelatedCompany>();
		shareHolderMailAddr = new SmPersonnelAddress();
		shareHolderMailAddr
				.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
		shareHolderPermAddr = new SmPersonnelAddress();
		shareHolderPermAddr
				.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
		shareHolderOffAddr = new SmPersonnelAddress();
		shareHolderOffAddr.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
		shareHolderResdAddr = new SmPersonnelAddress();
		shareHolderResdAddr
				.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
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
		selectedPersonnel.setPmCountry(SmApplicationUtil.setDefaultAddr(null)
				.getPmCountry());
		// smapplAuthPersonnels = new ArrayList<SmPersonnel>();
		selectedPersonnels = new ArrayList<SmPersonnel>();
		selectedPersAcademics = new ArrayList<SmStaffAcademic>();
		selectedPersJobFunctions = new ArrayList<SmStaffExperience>();
		selectedPersProfBodies = new ArrayList<SmAssociationBody>();
		selectedPersRelatedCompanies = new ArrayList<SmRelatedCompany>();

		selectedPersonnel
				.setSmPersonnelAddresses(new ArrayList<SmPersonnelAddress>());

		selectedFactoryFacility = new SmFacility();
		selectedFactoryFacility.setSmAddress(SmApplicationUtil
				.setDefaultAddr(null));
		selectedFactoryFacilities = new ArrayList<SmFacility>();
		applFactoryFacilities = new ArrayList<SmFacility>();
		selectedWorkShopFacility = new SmFacility();
		selectedWorkShopFacility.setSmAddress(SmApplicationUtil
				.setDefaultAddr(null));
		selectedWorkShopFacilities = new ArrayList<SmFacility>();
		applWorkShopFacilities = new ArrayList<SmFacility>();
		selectedDockyardFacility = new SmFacility();
		selectedDockyardFacility.setSmAddress(SmApplicationUtil
				.setDefaultAddr(null));
		selectedDockyardFacilities = new ArrayList<SmFacility>();
		applDockyardFacilities = new ArrayList<SmFacility>();
		selectedStoreFacility = new SmFacility();
		selectedStoreFacility.setSmAddress(SmApplicationUtil
				.setDefaultAddr(null));
		selectedStoreFacilities = new ArrayList<SmFacility>();
		applStoreFacilities = new ArrayList<SmFacility>();
		selectedManufacturingList = new ArrayList<SmManufacturingDtl>();
		selectedManufacturer = new SmManufacturingDtl();
		selectedManufacturer.setPmUom(new PmUom());
		selectedMachine = new SmMachineDtl();
		selectedMachine.setCategoryType(new PmParameter());
		selectedMachine.setCategoryCode(new PmCategoryL3());
		selectedMachine.setMachine(new PmMachine());
		selectedMachineList = new ArrayList<SmMachineDtl>();
		selectedSystem = new SmSupplierSystem();
		selectedSystem.setSystem(new PmMachine());

		//
		selectedSignatoryBankAcct = new SmSupplierBank();
		selectedSignatoryBankAccts = new ArrayList<SmSupplierBank>();
		smAppl = new SmAppl();
		smAppl.setSmPersonnels(new ArrayList<SmPersonnel>());
		selectedBank = new SmSupplierBank();
		// selectedBank.setPmbank(new PmBank());
		selectedBank.setSmAddress(SmApplicationUtil.setDefaultAddr(null));
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
		// selectedStaffAcademic.setPmEduLevel(new PmEduLevel());
		selectedStaffAcademic.setQualificationId(new PmParameter());
		selectedStfPrfBdy = new SmAssociationBody();
		selectedStfPrfBdy.setPmCertBody(new PmCertBody());
		perIdTypeParam = new PmParameter();
		defPmCountry = SmCommonUtil
				.retrieveCountryByCountryCode(SupplierManagementConst.COUNTRY_MALAYSIA_CODE);
		if (defPmCountry == null) {
			defPmCountry = new PmCountry();
		}
		uomSelectItems = SmApplicationUtil.getUomSelectItems();
		raceSelectItems = SmApplicationUtil.getRaceSelectItems();
		svTask = new SmSvTask();
		DateFormat df = new SimpleDateFormat(
				CommonConstants.DEFAULT_DATE_FORMAT);
		today = df.format(new Date());
		currDate = new Date();
		currYear = Calendar.getInstance().get(Calendar.YEAR);
		myCountryId = SmCommonUtil.getMyCountryId();
		selectedGst = new SmGstSupplier();
		companyBasicGst = new SmGstSupplier();
		salutationSelectItems = SmCommonUtil.getSalutationItems();
		addrSameAsItems = new ArrayList<SelectItem>();
		glp = SmCommonUtil.retrieveParameterDescByTypeAndCodeAndLang("CRT",
				"GLP");
		certBodyLs = SmApplicationUtil.retrievePmCertBodyList(glp
				.getPmParameter().getParameterCode());
		currBumiEquity = BigDecimal.ZERO;
		currNonBumiEquity = BigDecimal.ZERO;
		currForeignerEquity = BigDecimal.ZERO;
		currGovtUnitrustEquity = BigDecimal.ZERO;
		currGovtEquity = BigDecimal.ZERO;
		currForeignCompanyEquity = BigDecimal.ZERO;
		currLocalCompanyEquity = BigDecimal.ZERO;
		selectedPmReasonDescs = new ArrayList<PmReasonDesc>();
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

	public List<SelectItem> getBasicCompanyStates() {
		return basicCompanyStates;
	}

	public void setBasicCompanyStates(List<SelectItem> basicCompanyStates) {
		this.basicCompanyStates = basicCompanyStates;
	}

	public List<SelectItem> getBasicCompanyDistricts() {
		return basicCompanyDistricts;
	}

	public void setBasicCompanyDistricts(List<SelectItem> basicCompanyDistricts) {
		this.basicCompanyDistricts = basicCompanyDistricts;
	}

	public List<SelectItem> getBasicCompanyCities() {
		return basicCompanyCities;
	}

	public void setBasicCompanyCities(List<SelectItem> basicCompanyCities) {
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

	public List<SelectItem> getBasicOffFacStates() {
		return basicOffFacStates;
	}

	public void setBasicOffFacStates(List<SelectItem> basicOffFacStates) {
		this.basicOffFacStates = basicOffFacStates;
	}

	public List<SelectItem> getBasicOffFacDistricts() {
		return basicOffFacDistricts;
	}

	public void setBasicOffFacDistricts(List<SelectItem> basicOffFacDistricts) {
		this.basicOffFacDistricts = basicOffFacDistricts;
	}

	public List<SelectItem> getBasicOffFacCities() {
		return basicOffFacCities;
	}

	public void setBasicOffFacCities(List<SelectItem> basicOffFacCities) {
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

	public List<SelectItem> getBranchCompanyStates() {
		return branchCompanyStates;
	}

	public void setBranchCompanyStates(List<SelectItem> branchCompanyStates) {
		this.branchCompanyStates = branchCompanyStates;
	}

	public List<SelectItem> getBranchCompanyDistricts() {
		return branchCompanyDistricts;
	}

	public void setBranchCompanyDistricts(
			List<SelectItem> branchCompanyDistricts) {
		this.branchCompanyDistricts = branchCompanyDistricts;
	}

	public List<SelectItem> getBranchCompanyCities() {
		return branchCompanyCities;
	}

	public void setBranchCompanyCities(List<SelectItem> branchCompanyCities) {
		this.branchCompanyCities = branchCompanyCities;
	}

	public List<SelectItem> getAssocCompanyStates() {
		return assocCompanyStates;
	}

	public void setAssocCompanyStates(List<SelectItem> assocCompanyStates) {
		this.assocCompanyStates = assocCompanyStates;
	}

	public List<SelectItem> getAssocCompanyDistricts() {
		return assocCompanyDistricts;
	}

	public void setAssocCompanyDistricts(List<SelectItem> assocCompanyDistricts) {
		this.assocCompanyDistricts = assocCompanyDistricts;
	}

	public List<SelectItem> getAssocCompanyCities() {
		return assocCompanyCities;
	}

	public void setAssocCompanyCities(List<SelectItem> assocCompanyCities) {
		this.assocCompanyCities = assocCompanyCities;
	}

	public List<SelectItem> getEquiCompanyStates() {
		return equiCompanyStates;
	}

	public void setEquiCompanyStates(List<SelectItem> equiCompanyStates) {
		this.equiCompanyStates = equiCompanyStates;
	}

	public List<SelectItem> getEquiCompanyDistricts() {
		return equiCompanyDistricts;
	}

	public void setEquiCompanyDistricts(List<SelectItem> equiCompanyDistricts) {
		this.equiCompanyDistricts = equiCompanyDistricts;
	}

	public List<SelectItem> getEquiCompanyCities() {
		return equiCompanyCities;
	}

	public void setEquiCompanyCities(List<SelectItem> equiCompanyCities) {
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

	public List<SelectItem> getFactoryFacilityStates() {
		return factoryFacilityStates;
	}

	public void setFactoryFacilityStates(List<SelectItem> factoryFacilityStates) {
		this.factoryFacilityStates = factoryFacilityStates;
	}

	public List<SelectItem> getFactoryFacilityDistricts() {
		return factoryFacilityDistricts;
	}

	public void setFactoryFacilityDistricts(
			List<SelectItem> factoryFacilityDistricts) {
		this.factoryFacilityDistricts = factoryFacilityDistricts;
	}

	public List<SelectItem> getFactoryFacilityCities() {
		return factoryFacilityCities;
	}

	public void setFactoryFacilityCities(List<SelectItem> factoryFacilityCities) {
		this.factoryFacilityCities = factoryFacilityCities;
	}

	public List<SelectItem> getDockFacilityStates() {
		return dockFacilityStates;
	}

	public void setDockFacilityStates(List<SelectItem> dockFacilityStates) {
		this.dockFacilityStates = dockFacilityStates;
	}

	public List<SelectItem> getDockFacilityDistricts() {
		return dockFacilityDistricts;
	}

	public void setDockFacilityDistricts(List<SelectItem> dockFacilityDistricts) {
		this.dockFacilityDistricts = dockFacilityDistricts;
	}

	public List<SelectItem> getDockFacilityCities() {
		return dockFacilityCities;
	}

	public void setDockFacilityCities(List<SelectItem> dockFacilityCities) {
		this.dockFacilityCities = dockFacilityCities;
	}

	public List<SelectItem> getWorkShopFacilityStates() {
		return workShopFacilityStates;
	}

	public void setWorkShopFacilityStates(
			List<SelectItem> workShopFacilityStates) {
		this.workShopFacilityStates = workShopFacilityStates;
	}

	public List<SelectItem> getWorkShopFacilityDistricts() {
		return workShopFacilityDistricts;
	}

	public void setWorkShopFacilityDistricts(
			List<SelectItem> workShopFacilityDistricts) {
		this.workShopFacilityDistricts = workShopFacilityDistricts;
	}

	public List<SelectItem> getWorkShopFacilityCities() {
		return workShopFacilityCities;
	}

	public void setWorkShopFacilityCities(
			List<SelectItem> workShopFacilityCities) {
		this.workShopFacilityCities = workShopFacilityCities;
	}

	public List<SelectItem> getStoreFacilityStates() {
		return storeFacilityStates;
	}

	public void setStoreFacilityStates(List<SelectItem> storeFacilityStates) {
		this.storeFacilityStates = storeFacilityStates;
	}

	public List<SelectItem> getStoreFacilityDistricts() {
		return storeFacilityDistricts;
	}

	public void setStoreFacilityDistricts(
			List<SelectItem> storeFacilityDistricts) {
		this.storeFacilityDistricts = storeFacilityDistricts;
	}

	public List<SelectItem> getStoreFacilityCities() {
		return storeFacilityCities;
	}

	public void setStoreFacilityCities(List<SelectItem> storeFacilityCities) {
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

	public List<SelectItem> getShrHldrPermStates() {
		return shrHldrPermStates;
	}

	public void setShrHldrPermStates(List<SelectItem> shrHldrPermStates) {
		this.shrHldrPermStates = shrHldrPermStates;
	}

	public List<SelectItem> getShrHldrPermDistricts() {
		return shrHldrPermDistricts;
	}

	public void setShrHldrPermDistricts(List<SelectItem> shrHldrPermDistricts) {
		this.shrHldrPermDistricts = shrHldrPermDistricts;
	}

	public List<SelectItem> getShrHldrPermCities() {
		return shrHldrPermCities;
	}

	public void setShrHldrPermCities(List<SelectItem> shrHldrPermCities) {
		this.shrHldrPermCities = shrHldrPermCities;
	}

	public List<SelectItem> getShrHldrOffStates() {
		return shrHldrOffStates;
	}

	public void setShrHldrOffStates(List<SelectItem> shrHldrOffStates) {
		this.shrHldrOffStates = shrHldrOffStates;
	}

	public List<SelectItem> getShrHldrOffDistricts() {
		return shrHldrOffDistricts;
	}

	public void setShrHldrOffDistricts(List<SelectItem> shrHldrOffDistricts) {
		this.shrHldrOffDistricts = shrHldrOffDistricts;
	}

	public List<SelectItem> getShrHldrOffCities() {
		return shrHldrOffCities;
	}

	public void setShrHldrOffCities(List<SelectItem> shrHldrOffCities) {
		this.shrHldrOffCities = shrHldrOffCities;
	}

	public List<SelectItem> getShrHldrMailStates() {
		return shrHldrMailStates;
	}

	public void setShrHldrMailStates(List<SelectItem> shrHldrMailStates) {
		this.shrHldrMailStates = shrHldrMailStates;
	}

	public List<SelectItem> getShrHldrMailDistricts() {
		return shrHldrMailDistricts;
	}

	public void setShrHldrMailDistricts(List<SelectItem> shrHldrMailDistricts) {
		this.shrHldrMailDistricts = shrHldrMailDistricts;
	}

	public List<SelectItem> getShrHldrMailCities() {
		return shrHldrMailCities;
	}

	public void setShrHldrMailCities(List<SelectItem> shrHldrMailCities) {
		this.shrHldrMailCities = shrHldrMailCities;
	}

	public List<SelectItem> getShrHldrResdStates() {
		return shrHldrResdStates;
	}

	public void setShrHldrResdStates(List<SelectItem> shrHldrResdStates) {
		this.shrHldrResdStates = shrHldrResdStates;
	}

	public List<SelectItem> getShrHldrResdDistricts() {
		return shrHldrResdDistricts;
	}

	public void setShrHldrResdDistricts(List<SelectItem> shrHldrResdDistricts) {
		this.shrHldrResdDistricts = shrHldrResdDistricts;
	}

	public List<SelectItem> getShrHldrResdCities() {
		return shrHldrResdCities;
	}

	public void setShrHldrResdCities(List<SelectItem> shrHldrResdCities) {
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

	public List<SelectItem> getBankStates() {
		return bankStates;
	}

	public void setBankStates(List<SelectItem> bankStates) {
		this.bankStates = bankStates;
	}

	public List<SelectItem> getBankDistricts() {
		return bankDistricts;
	}

	public void setBankDistricts(List<SelectItem> bankDistricts) {
		this.bankDistricts = bankDistricts;
	}

	public List<SelectItem> getBankCities() {
		return bankCities;
	}

	public void setBankCities(List<SelectItem> bankCities) {
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

	// public int getAuthPerCount() {
	// return authPerCount;
	// }
	//
	// public void setAuthPerCount(int authPerCount) {
	// this.authPerCount = authPerCount;
	// }

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

	public Long getSelSuppCategoryFacilityId() {
		return selSuppCategoryFacilityId;
	}

	public void setSelSuppCategoryFacilityId(Long selSuppCategoryFacilityId) {
		this.selSuppCategoryFacilityId = selSuppCategoryFacilityId;
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

	public boolean isValidEquityLimit() {
		return validEquityLimit;
	}

	public void setValidEquityLimit(boolean validEquityLimit) {
		this.validEquityLimit = validEquityLimit;
	}

	public String getToday() {
		return today;
	}

	public void setToday(String today) {
		this.today = today;
	}

	public List<SmCompanyExperienceCat> getSelectedCompExpCats() {
		return selectedCompExpCats;
	}

	public void setSelectedCompExpCats(
			List<SmCompanyExperienceCat> selectedCompExpCats) {
		this.selectedCompExpCats = selectedCompExpCats;
	}

	public String getRevSupUrl() {
		return revSupUrl;
	}

	public void setRevSupUrl(String revSupUrl) {
		this.revSupUrl = revSupUrl;
	}

	public String getRevAppUrl() {
		return revAppUrl;
	}

	public void setRevAppUrl(String revAppUrl) {
		this.revAppUrl = revAppUrl;
	}

	public List<PmRace> getRacesList() {
		return racesList;
	}

	public void setRacesList(List<PmRace> racesList) {
		this.racesList = racesList;
	}

	public List<SmSupplierCategory> getActiveCategoryList() {
		return activeCategoryList;
	}

	public void setActiveCategoryList(
			List<SmSupplierCategory> activeCategoryList) {
		this.activeCategoryList = activeCategoryList;
	}

	public String getRevisionBaseUrl() {
		return revisionBaseUrl;
	}

	public void setRevisionBaseUrl(String revisionBaseUrl) {
		this.revisionBaseUrl = revisionBaseUrl;
	}

	public String getRevisionSupplierAppNoParam() {
		return revisionSupplierAppNoParam;
	}

	public void setRevisionSupplierAppNoParam(String revisionSupplierAppNoParam) {
		this.revisionSupplierAppNoParam = revisionSupplierAppNoParam;
	}

	public String getRevisionApproverAppNoParam() {
		return revisionApproverAppNoParam;
	}

	public void setRevisionApproverAppNoParam(String revisionApproverAppNoParam) {
		this.revisionApproverAppNoParam = revisionApproverAppNoParam;
	}

	public List<SmAssociationBody> getNonPersonnelProfBodyList() {
		return nonPersonnelProfBodyList;
	}

	public void setNonPersonnelProfBodyList(
			List<SmAssociationBody> nonPersonnelProfBodyList) {
		this.nonPersonnelProfBodyList = nonPersonnelProfBodyList;
	}

	public SelectItem[] getIdentificationTypes() {
		return identificationTypes;
	}

	public void setIdentificationTypes(SelectItem[] identificationTypes) {
		this.identificationTypes = identificationTypes;
	}

	public List<SmSupplierCategory> getDeleteNewApplCategoryList() {
		return deleteNewApplCategoryList;
	}

	public void setDeleteNewApplCategoryList(
			List<SmSupplierCategory> deleteNewApplCategoryList) {
		this.deleteNewApplCategoryList = deleteNewApplCategoryList;
	}

	public SmAttachment getCancellationAttachment() {
		return cancellationAttachment;
	}

	public void setCancellationAttachment(SmAttachment cancellationAttachment) {
		this.cancellationAttachment = cancellationAttachment;
	}

	public List<SmSupplierCategory> getSplCategoryList() {
		return splCategoryList;
	}

	public void setSplCategoryList(List<SmSupplierCategory> splCategoryList) {
		this.splCategoryList = splCategoryList;
	}

	public List<SelectItem> getUomSelectItems() {
		return uomSelectItems;
	}

	public void setUomSelectItems(List<SelectItem> uomSelectItems) {
		this.uomSelectItems = uomSelectItems;
	}

	public List<SelectItem> getRaceSelectItems() {
		return raceSelectItems;
	}

	public void setRaceSelectItems(List<SelectItem> raceSelectItems) {
		this.raceSelectItems = raceSelectItems;
	}

	public String getBumiRecommendationNote() {
		return bumiRecommendationNote;
	}

	public void setBumiRecommendationNote(String bumiRecommendationNote) {
		this.bumiRecommendationNote = bumiRecommendationNote;
	}

	public boolean isQualifiedForBumi() {
		return qualifiedForBumi;
	}

	public void setQualifiedForBumi(boolean qualifiedForBumi) {
		this.qualifiedForBumi = qualifiedForBumi;
	}

	public BigDecimal getBumiEquityPercentage() {
		return bumiEquityPercentage;
	}

	public void setBumiEquityPercentage(BigDecimal bumiEquityPercentage) {
		this.bumiEquityPercentage = bumiEquityPercentage;
	}

	public Date getCurrDate() {
		return currDate;
	}

	public void setCurrDate(Date currDate) {
		this.currDate = currDate;
	}

	public boolean isDisableIcEmail() {
		return disableIcEmail;
	}

	public void setDisableIcEmail(boolean disableIcEmail) {
		this.disableIcEmail = disableIcEmail;
	}

	public boolean isSelectedAll() {
		return selectedAll;
	}

	public void setSelectedAll(boolean selectedAll) {
		this.selectedAll = selectedAll;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getPersonnelCountCheck() {
		return personnelCountCheck;
	}

	public void setPersonnelCountCheck(int personnelCountCheck) {
		this.personnelCountCheck = personnelCountCheck;
	}

	public String getOverLappedCategoryMsg() {
		return overLappedCategoryMsg;
	}

	public void setOverLappedCategoryMsg(String overLappedCategoryMsg) {
		this.overLappedCategoryMsg = overLappedCategoryMsg;
	}

	public boolean isOverLappedCategoryExists() {
		return overLappedCategoryExists;
	}

	public void setOverLappedCategoryExists(boolean overLappedCategoryExists) {
		this.overLappedCategoryExists = overLappedCategoryExists;
	}

	public boolean isDisAllowOwnerEdit() {
		return disAllowOwnerEdit;
	}

	public void setDisAllowOwnerEdit(boolean disAllowOwnerEdit) {
		this.disAllowOwnerEdit = disAllowOwnerEdit;
	}

	public SmSvTask getSvTask() {
		return svTask;
	}

	public void setSvTask(SmSvTask svTask) {
		this.svTask = svTask;
	}

	public Integer getCurrYear() {
		return currYear;
	}

	public void setCurrYear(Integer currYear) {
		this.currYear = currYear;
	}

	public boolean isDisabledHomeOffice() {
		return disabledHomeOffice;
	}

	public void setDisabledHomeOffice(boolean disabledHomeOffice) {
		this.disabledHomeOffice = disabledHomeOffice;
	}

	public String getTypeOfBusiness() {
		return typeOfBusiness;
	}

	public void setTypeOfBusiness(String typeOfBusiness) {
		this.typeOfBusiness = typeOfBusiness;
	}

	public boolean isMofSupplier() {
		return mofSupplier;
	}

	public void setMofSupplier(boolean mofSupplier) {
		this.mofSupplier = mofSupplier;
	}

	public String getResubmitInValidMsg() {
		return resubmitInValidMsg;
	}

	public void setResubmitInValidMsg(String resubmitInValidMsg) {
		this.resubmitInValidMsg = resubmitInValidMsg;
	}

	public SmSupplierBasicInfoVO getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(SmSupplierBasicInfoVO basicInfo) {
		this.basicInfo = basicInfo;
	}

	public boolean isAllowedToApprove() {
		return allowedToApprove;
	}

	public void setAllowedToApprove(boolean allowedToApprove) {
		this.allowedToApprove = allowedToApprove;
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

	public boolean isDisabledJustification() {
		return disabledJustification;
	}

	public void setDisabledJustification(boolean disabledJustification) {
		this.disabledJustification = disabledJustification;
	}

	public String getBpmTaskName() {
		return bpmTaskName;
	}

	public void setBpmTaskName(String bpmTaskName) {
		this.bpmTaskName = bpmTaskName;
	}

	public BigDecimal getPurPricePrevVal() {
		return purPricePrevVal;
	}

	public void setPurPricePrevVal(BigDecimal purPricePrevVal) {
		this.purPricePrevVal = purPricePrevVal;
	}

	public BigDecimal getRentPrevVal() {
		return rentPrevVal;
	}

	public void setRentPrevVal(BigDecimal rentPrevVal) {
		this.rentPrevVal = rentPrevVal;
	}

	public String getOwnerTypePrevVal() {
		return ownerTypePrevVal;
	}

	public void setOwnerTypePrevVal(String ownerTypePrevVal) {
		this.ownerTypePrevVal = ownerTypePrevVal;
	}

	public boolean isHasSuppDocToDownload() {
		return hasSuppDocToDownload;
	}

	public void setHasSuppDocToDownload(boolean hasSuppDocToDownload) {
		this.hasSuppDocToDownload = hasSuppDocToDownload;
	}

	public boolean isShowFactoryPanel() {
		return showFactoryPanel;
	}

	public void setShowFactoryPanel(boolean showFactoryPanel) {
		this.showFactoryPanel = showFactoryPanel;
	}

	public boolean isShowStorePanel() {
		return showStorePanel;
	}

	public void setShowStorePanel(boolean showStorePanel) {
		this.showStorePanel = showStorePanel;
	}

	public boolean isShowDockyardPanel() {
		return showDockyardPanel;
	}

	public void setShowDockyardPanel(boolean showDockyardPanel) {
		this.showDockyardPanel = showDockyardPanel;
	}

	public boolean isShowWorkShopPanel() {
		return showWorkShopPanel;
	}

	public void setShowWorkShopPanel(boolean showWorkShopPanel) {
		this.showWorkShopPanel = showWorkShopPanel;
	}

	public String getSelectedShareholderIdentificationType() {
		return selectedShareholderIdentificationType;
	}

	public void setSelectedShareholderIdentificationType(
			String selectedShareholderIdentificationType) {
		this.selectedShareholderIdentificationType = selectedShareholderIdentificationType;
	}

	public boolean isShowPersonnelViewOnly() {
		return showPersonnelViewOnly;
	}

	public void setShowPersonnelViewOnly(boolean showPersonnelViewOnly) {
		this.showPersonnelViewOnly = showPersonnelViewOnly;
	}

	public boolean isDisabledShareholderPersonnelNameAndNationality() {
		return disabledShareholderPersonnelNameAndNationality;
	}

	public void setDisabledShareholderPersonnelNameAndNationality(
			boolean disabledShareholderPersonnelNameAndNationality) {
		this.disabledShareholderPersonnelNameAndNationality = disabledShareholderPersonnelNameAndNationality;
	}

	public String getRevisionApplType() {
		return revisionApplType;
	}

	public void setRevisionApplType(String revisionApplType) {
		this.revisionApplType = revisionApplType;
	}

	public Long getMyCountryId() {
		return myCountryId;
	}

	public void setMyCountryId(Long myCountryId) {
		this.myCountryId = myCountryId;
	}

	public String getApplnApprovalRemarks() {
		return applnApprovalRemarks;
	}

	public void setApplnApprovalRemarks(String applnApprovalRemarks) {
		this.applnApprovalRemarks = applnApprovalRemarks;
	}

	public String getApplnRejectionRemarks() {
		return applnRejectionRemarks;
	}

	public void setApplnRejectionRemarks(String applnRejectionRemarks) {
		this.applnRejectionRemarks = applnRejectionRemarks;
	}

	public String getApplnOverLapRemarks() {
		return applnOverLapRemarks;
	}

	public void setApplnOverLapRemarks(String applnOverLapRemarks) {
		this.applnOverLapRemarks = applnOverLapRemarks;
	}

	public String getPoToSvcRmrk() {
		return poToSvcRmrk;
	}

	public void setPoToSvcRmrk(String poToSvcRmrk) {
		this.poToSvcRmrk = poToSvcRmrk;
	}

	public List<SmPersonnel> getPrevApprovedPersonnels() {
		return prevApprovedPersonnels;
	}

	public void setPrevApprovedPersonnels(
			List<SmPersonnel> prevApprovedPersonnels) {
		this.prevApprovedPersonnels = prevApprovedPersonnels;
	}

	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}

	public PmParameter getCertificate() {
		return certificate;
	}

	public void setCertificate(PmParameter certificate) {
		this.certificate = certificate;
	}

	public PmParameter getDiploma() {
		return diploma;
	}

	public void setDiploma(PmParameter diploma) {
		this.diploma = diploma;
	}

	public List<SelectItem> getAcadQualifications() {
		return acadQualifications;
	}

	public void setAcadQualifications(List<SelectItem> acadQualifications) {
		this.acadQualifications = acadQualifications;
	}

	public Long getPrevBasicStateId() {
		return prevBasicStateId;
	}

	public void setPrevBasicStateId(Long prevBasicStateId) {
		this.prevBasicStateId = prevBasicStateId;
	}

	public SmApplStatusInfoVO getApplStatusInfo() {
		return applStatusInfo;
	}

	public void setApplStatusInfo(SmApplStatusInfoVO applStatusInfo) {
		this.applStatusInfo = applStatusInfo;
	}

	public SpkiBackingBean getSpkiBackingBean() {
		return spkiBackingBean;
	}

	public void setSpkiBackingBean(SpkiBackingBean spkiBackingBean) {
		this.spkiBackingBean = spkiBackingBean;
	}

	public PmParameter getGlpCert() {
		return glpCert;
	}

	public void setGlpCert(PmParameter glpCert) {
		this.glpCert = glpCert;
	}

	public boolean isDisabledCompanyName() {
		return disabledCompanyName;
	}

	public void setDisabledCompanyName(boolean disabledCompanyName) {
		this.disabledCompanyName = disabledCompanyName;
	}

	public boolean isDisabledBusinessType() {
		return disabledBusinessType;
	}

	public void setDisabledBusinessType(boolean disabledBusinessType) {
		this.disabledBusinessType = disabledBusinessType;
	}

	public boolean isDisableWithFedralTransaction() {
		return disableWithFedralTransaction;
	}

	public void setDisableWithFedralTransaction(
			boolean disableWithFedralTransaction) {
		this.disableWithFedralTransaction = disableWithFedralTransaction;
	}

	public boolean isDisableBranchGst() {
		return disableBranchGst;
	}

	public void setDisableBranchGst(boolean disableBranchGst) {
		this.disableBranchGst = disableBranchGst;
	}

	public List<SmSupplierCategory> getApprovedCategoryList() {
		return approvedCategoryList;
	}

	public void setApprovedCategoryList(
			List<SmSupplierCategory> approvedCategoryList) {
		this.approvedCategoryList = approvedCategoryList;
	}

	public String getApplRegStatus() {
		return applRegStatus;
	}

	public void setApplRegStatus(String applRegStatus) {
		this.applRegStatus = applRegStatus;
	}

	public String getMofNumber() {
		return mofNumber;
	}

	public void setMofNumber(String mofNumber) {
		this.mofNumber = mofNumber;
	}

	public boolean isRejectReasonReqd() {
		return rejectReasonReqd;
	}

	public void setRejectReasonReqd(boolean rejectReasonReqd) {
		this.rejectReasonReqd = rejectReasonReqd;
	}

	public Long getRejectionOtherReasonId() {
		return rejectionOtherReasonId;
	}

	public void setRejectionOtherReasonId(Long rejectionOtherReasonId) {
		this.rejectionOtherReasonId = rejectionOtherReasonId;
	}

	/*public PmEduLevel getCertEduLevel() {
		return certEduLevel;
	}

	public void setCertEduLevel(PmEduLevel certEduLevel) {
		this.certEduLevel = certEduLevel;
	}

	public PmEduLevel getDiplomaEduLevel() {
		return diplomaEduLevel;
	}

	public void setDiplomaEduLevel(PmEduLevel diplomaEduLevel) {
		this.diplomaEduLevel = diplomaEduLevel;
	}*/

	public List<PmParameterDesc> getEduLevelList() {
		return eduLevelList;
	}

	public void setEduLevelList(List<PmParameterDesc> eduLevelList) {
		this.eduLevelList = eduLevelList;
	}

	public boolean isRoleStaffAndPermAddrReqd() {
		return roleStaffAndPermAddrReqd;
	}

	public void setRoleStaffAndPermAddrReqd(boolean roleStaffAndPermAddrReqd) {
		this.roleStaffAndPermAddrReqd = roleStaffAndPermAddrReqd;
	}

	public boolean isRoleStaffAndMailAddrReqd() {
		return roleStaffAndMailAddrReqd;
	}

	public void setRoleStaffAndMailAddrReqd(boolean roleStaffAndMailAddrReqd) {
		this.roleStaffAndMailAddrReqd = roleStaffAndMailAddrReqd;
	}

	public List<PmReasonDesc> getDeletePendingApplDescList() {
		return deletePendingApplDescList;
	}

	public void setDeletePendingApplDescList(
			List<PmReasonDesc> deletePendingApplDescList) {
		this.deletePendingApplDescList = deletePendingApplDescList;
	}

	public Long getDeletePendingApplOtherReasonId() {
		return deletePendingApplOtherReasonId;
	}

	public void setDeletePendingApplOtherReasonId(
			Long deletePendingApplOtherReasonId) {
		this.deletePendingApplOtherReasonId = deletePendingApplOtherReasonId;
	}

	public List<SelectItem> getCountryItems() {
		return countryItems;
	}

	public void setCountryItems(List<SelectItem> countryItems) {
		this.countryItems = countryItems;
	}

	public SmGstSupplier getCompanyBasicGst() {
		return companyBasicGst;
	}

	public void setCompanyBasicGst(SmGstSupplier companyBasicGst) {
		this.companyBasicGst = companyBasicGst;
	}

	public SmGstSupplierDoc getSelectedGstSuppDoc() {
		return selectedGstSuppDoc;
	}

	public void setSelectedGstSuppDoc(SmGstSupplierDoc selectedGstSuppDoc) {
		this.selectedGstSuppDoc = selectedGstSuppDoc;
	}

	public SmGstSupplier getSelectedGst() {
		return selectedGst;
	}

	public void setSelectedGst(SmGstSupplier selectedGst) {
		this.selectedGst = selectedGst;
	}

	public boolean isBranchSameAsHq() {
		return branchSameAsHq;
	}

	public void setBranchSameAsHq(boolean branchSameAsHq) {
		this.branchSameAsHq = branchSameAsHq;
	}

	public SmAppl getLatestAppl() {
		return latestAppl;
	}

	public void setLatestAppl(SmAppl latestAppl) {
		this.latestAppl = latestAppl;
	}

	public SmAssociateCompany getLatestAssocCompany() {
		return latestAssocCompany;
	}

	public void setLatestAssocCompany(SmAssociateCompany latestAssocCompany) {
		this.latestAssocCompany = latestAssocCompany;
	}

	public SmAssociationBody getLatestProfBody() {
		return latestProfBody;
	}

	public void setLatestProfBody(SmAssociationBody latestProfBody) {
		this.latestProfBody = latestProfBody;
	}

	public boolean isShowFieldChanges() {
		return showFieldChanges;
	}

	public void setShowFieldChanges(boolean showFieldChanges) {
		this.showFieldChanges = showFieldChanges;
	}

	public SmPersonnel getLatestPersonnel() {
		return latestPersonnel;
	}

	public void setLatestPersonnel(SmPersonnel latestPersonnel) {
		this.latestPersonnel = latestPersonnel;
	}

	public SmPersonnelAddress getLatestPermAddr() {
		return latestPermAddr;
	}

	public void setLatestPermAddr(SmPersonnelAddress latestPermAddr) {
		this.latestPermAddr = latestPermAddr;
	}

	public SmPersonnelAddress getLatestMailAddr() {
		return latestMailAddr;
	}

	public void setLatestMailAddr(SmPersonnelAddress latestMailAddr) {
		this.latestMailAddr = latestMailAddr;
	}

	public SmPersonnelAddress getLatestResdAddr() {
		return latestResdAddr;
	}

	public void setLatestResdAddr(SmPersonnelAddress latestResdAddr) {
		this.latestResdAddr = latestResdAddr;
	}

	public SmPersonnelAddress getLatestOffAddr() {
		return latestOffAddr;
	}

	public void setLatestOffAddr(SmPersonnelAddress latestOffAddr) {
		this.latestOffAddr = latestOffAddr;
	}

	public SmStaffExperience getLatestJobFunction() {
		return latestJobFunction;
	}

	public void setLatestJobFunction(SmStaffExperience latestJobFunction) {
		this.latestJobFunction = latestJobFunction;
	}

	public SmRelatedCompany getLatestRelatedCompany() {
		return latestRelatedCompany;
	}

	public void setLatestRelatedCompany(SmRelatedCompany latestRelatedCompany) {
		this.latestRelatedCompany = latestRelatedCompany;
	}

	public SmAssociationBody getLatestStfPrfBdy() {
		return latestStfPrfBdy;
	}

	public void setLatestStfPrfBdy(SmAssociationBody latestStfPrfBdy) {
		this.latestStfPrfBdy = latestStfPrfBdy;
	}

	public SmStaffAcademic getLatestStaffAcademic() {
		return latestStaffAcademic;
	}

	public void setLatestStaffAcademic(SmStaffAcademic latestStaffAcademic) {
		this.latestStaffAcademic = latestStaffAcademic;
	}

	public SmEquityCompany getLatestEquityCompany() {
		return latestEquityCompany;
	}

	public void setLatestEquityCompany(SmEquityCompany latestEquityCompany) {
		this.latestEquityCompany = latestEquityCompany;
	}

	public SmSupplierBranch getLatestBranch() {
		return latestBranch;
	}

	public void setLatestBranch(SmSupplierBranch latestBranch) {
		this.latestBranch = latestBranch;
	}

	public SmSupplierBank getLatestBank() {
		return latestBank;
	}

	public void setLatestBank(SmSupplierBank latestBank) {
		this.latestBank = latestBank;
	}

	public SmCompanyExperience getLatestCompanyExperience() {
		return latestCompanyExperience;
	}

	public void setLatestCompanyExperience(
			SmCompanyExperience latestCompanyExperience) {
		this.latestCompanyExperience = latestCompanyExperience;
	}

	public SmSupplierCert getLatestCert() {
		return latestCert;
	}

	public void setLatestCert(SmSupplierCert latestCert) {
		this.latestCert = latestCert;
	}

	public boolean isShowNewChanges() {
		String newChanges = EpPropertiesUtils.getInstance().getProperty(
				"sm.show.new.changes");
		boolean showNewChanges = Boolean.valueOf(newChanges);
		return showNewChanges;
	}

	public void setShowNewChanges(boolean showNewChanges) {
		this.showNewChanges = showNewChanges;
	}

	public boolean isAlreadyBusinessSpecialCatcode() {
		return alreadyBusinessSpecialCatcode;
	}

	public void setAlreadyBusinessSpecialCatcode(
			boolean alreadyBusinessSpecialCatcode) {
		this.alreadyBusinessSpecialCatcode = alreadyBusinessSpecialCatcode;
	}

	public String getBusinessOfficeCatCodeMsg() {
		return businessOfficeCatCodeMsg;
	}

	public void setBusinessOfficeCatCodeMsg(String businessOfficeCatCodeMsg) {
		this.businessOfficeCatCodeMsg = businessOfficeCatCodeMsg;
	}

	public List<SmSupplierSystem> getSelectedSupplierSystemList() {
		return selectedSupplierSystemList;
	}

	public void setSelectedSupplierSystemList(
			List<SmSupplierSystem> selectedSupplierSystemList) {
		this.selectedSupplierSystemList = selectedSupplierSystemList;
	}

	public SmSupplierSystem getSelectedSystem() {
		return selectedSystem;
	}

	public void setSelectedSystem(SmSupplierSystem selectedSystem) {
		this.selectedSystem = selectedSystem;
	}

	public List<SelectItem> getTravellingSoftwareItems() {
		return travellingSoftwareItems;
	}

	public void setTravellingSoftwareItems(
			List<SelectItem> travellingSoftwareItems) {
		this.travellingSoftwareItems = travellingSoftwareItems;
	}

	public boolean isShowTravelSoftwarePanel() {
		return showTravelSoftwarePanel;
	}

	public void setShowTravelSoftwarePanel(boolean showTravelSoftwarePanel) {
		this.showTravelSoftwarePanel = showTravelSoftwarePanel;
	}

	public boolean isOthersSystem() {
		return othersSystem;
	}

	public void setOthersSystem(boolean othersSystem) {
		this.othersSystem = othersSystem;
	}

	public Long[] getSelectedBankBranchIds() {
		return selectedBankBranchIds;
	}

	public void setSelectedBankBranchIds(Long[] selectedBankBranchIds) {
		this.selectedBankBranchIds = selectedBankBranchIds;
	}

	public List<SmSiteVisitFacilityVO> getSiteVisitFacVOList() {
		return siteVisitFacVOList;
	}

	public void setSiteVisitFacVOList(
			List<SmSiteVisitFacilityVO> siteVisitFacVOList) {
		this.siteVisitFacVOList = siteVisitFacVOList;
	}

	public List<SmSvTask> getSvTaskList() {
		return svTaskList;
	}

	public void setSvTaskList(List<SmSvTask> svTaskList) {
		this.svTaskList = svTaskList;
	}

	public List<SelectItem> getSalutationSelectItems() {
		return salutationSelectItems;
	}

	public void setSalutationSelectItems(List<SelectItem> salutationSelectItems) {
		this.salutationSelectItems = salutationSelectItems;
	}

	public List<SelectItem> getCategoryTypeItems() {
		return categoryTypeItems;
	}

	public void setCategoryTypeItems(List<SelectItem> categoryTypeItems) {
		this.categoryTypeItems = categoryTypeItems;
	}

	public List<SelectItem> getCategoryCodeItems() {
		return categoryCodeItems;
	}

	public void setCategoryCodeItems(List<SelectItem> categoryCodeItems) {
		this.categoryCodeItems = categoryCodeItems;
	}

	public List<SelectItem> getMachineItems() {
		return machineItems;
	}

	public void setMachineItems(List<SelectItem> machineItems) {
		this.machineItems = machineItems;
	}

	public boolean isOthersMachine() {
		return othersMachine;
	}

	public void setOthersMachine(boolean othersMachine) {
		this.othersMachine = othersMachine;
	}

	public String getFacAddresSameAs() {
		return facAddresSameAs;
	}

	public void setFacAddresSameAs(String facAddresSameAs) {
		this.facAddresSameAs = facAddresSameAs;
	}

	public List<SelectItem> getAddrSameAsItems() {
		return addrSameAsItems;
	}

	public void setAddrSameAsItems(List<SelectItem> addrSameAsItems) {
		this.addrSameAsItems = addrSameAsItems;
	}

	public boolean isClaimTask() {
		return claimTask;
	}

	public void setClaimTask(boolean claimTask) {
		this.claimTask = claimTask;
	}

	public boolean isDisableRoleSelection() {
		return disableRoleSelection;
	}

	public void setDisableRoleSelection(boolean disableRoleSelection) {
		this.disableRoleSelection = disableRoleSelection;
	}

	public boolean isSiteVisitAfterApproval() {
		return siteVisitAfterApproval;
	}

	public void setSiteVisitAfterApproval(boolean siteVisitAfterApproval) {
		this.siteVisitAfterApproval = siteVisitAfterApproval;
	}

	public boolean isPoInquiryRespond() {
		return poInquiryRespond;
	}

	public void setPoInquiryRespond(boolean poInquiryRespond) {
		this.poInquiryRespond = poInquiryRespond;
	}

	public String getBusinessTypeParamCode() {
		return businessTypeParamCode;
	}

	public void setBusinessTypeParamCode(String businessTypeParamCode) {
		this.businessTypeParamCode = businessTypeParamCode;
	}

	public String getPhysicalConsulting() {
		return physicalConsulting;
	}

	public void setPhysicalConsulting(String physicalConsulting) {
		this.physicalConsulting = physicalConsulting;
	}

	public List<SmSupplierBank> getSelectedSignatoryBankAccts() {
		return selectedSignatoryBankAccts;
	}

	public void setSelectedSignatoryBankAccts(
			List<SmSupplierBank> selectedSignatoryBankAccts) {
		this.selectedSignatoryBankAccts = selectedSignatoryBankAccts;
	}

	public String getPersonType() {
		return personType;
	}

	public void setPersonType(String personType) {
		this.personType = personType;
	}

	public PmParameterDesc getGlp() {
		return glp;
	}

	public void setGlp(PmParameterDesc glp) {
		this.glp = glp;
	}

	public BigDecimal getCurrBumiEquity() {
		return currBumiEquity;
	}

	public void setCurrBumiEquity(BigDecimal currBumiEquity) {
		this.currBumiEquity = currBumiEquity;
	}

	public BigDecimal getCurrNonBumiEquity() {
		return currNonBumiEquity;
	}

	public void setCurrNonBumiEquity(BigDecimal currNonBumiEquity) {
		this.currNonBumiEquity = currNonBumiEquity;
	}

	public BigDecimal getCurrForeignerEquity() {
		return currForeignerEquity;
	}

	public void setCurrForeignerEquity(BigDecimal currForeignerEquity) {
		this.currForeignerEquity = currForeignerEquity;
	}

	public BigDecimal getCurrGovtUnitrustEquity() {
		return currGovtUnitrustEquity;
	}

	public void setCurrGovtUnitrustEquity(BigDecimal currGovtUnitrustEquity) {
		this.currGovtUnitrustEquity = currGovtUnitrustEquity;
	}

	public BigDecimal getCurrGovtEquity() {
		return currGovtEquity;
	}

	public void setCurrGovtEquity(BigDecimal currGovtEquity) {
		this.currGovtEquity = currGovtEquity;
	}

	public BigDecimal getCurrForeignCompanyEquity() {
		return currForeignCompanyEquity;
	}

	public void setCurrForeignCompanyEquity(BigDecimal currForeignCompanyEquity) {
		this.currForeignCompanyEquity = currForeignCompanyEquity;
	}

	public BigDecimal getCurrLocalCompanyEquity() {
		return currLocalCompanyEquity;
	}

	public void setCurrLocalCompanyEquity(BigDecimal currLocalCompanyEquity) {
		this.currLocalCompanyEquity = currLocalCompanyEquity;
	}

	public String getSearchRejectReason() {
		return searchRejectReason;
	}

	public void setSearchRejectReason(String searchRejectReason) {
		this.searchRejectReason = searchRejectReason;
	}

	public IPagingDataModel<PmReasonDesc> getRejectReasonDataModel() {
		return rejectReasonDataModel;
	}

	public void setRejectReasonDataModel(
			IPagingDataModel<PmReasonDesc> rejectReasonDataModel) {
		this.rejectReasonDataModel = rejectReasonDataModel;
	}

	public List<PmReasonDesc> getSelectedPmReasonDescs() {
		return selectedPmReasonDescs;
	}

	public void setSelectedPmReasonDescs(
			List<PmReasonDesc> selectedPmReasonDescs) {
		this.selectedPmReasonDescs = selectedPmReasonDescs;
	}

	public boolean isChangeCurrRegStatus() {
		return changeCurrRegStatus;
	}

	public void setChangeCurrRegStatus(boolean changeCurrRegStatus) {
		this.changeCurrRegStatus = changeCurrRegStatus;
	}

	public String getCurrRegStatus() {
		return currRegStatus;
	}

	public void setCurrRegStatus(String currRegStatus) {
		this.currRegStatus = currRegStatus;
	}

	public List<SmSupplierCategory> getDeleteExistingCategoryList() {
		return deleteExistingCategoryList;
	}

	public void setDeleteExistingCategoryList(
			List<SmSupplierCategory> deleteExistingCategoryList) {
		this.deleteExistingCategoryList = deleteExistingCategoryList;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isShowPoApprovedOrRecommendButton() {
		return showPoApprovedOrRecommendButton;
	}

	public void setShowPoApprovedOrRecommendButton(
			boolean showPoApprovedOrRecommendButton) {
		this.showPoApprovedOrRecommendButton = showPoApprovedOrRecommendButton;
	}

	public SmSectionEditorVO getEditVO() {
		return editVO;
	}

	public void setEditVO(SmSectionEditorVO editVO) {
		this.editVO = editVO;
	}

	public SmAppl getSupplierAppl() {
		return supplierAppl;
	}

	public void setSupplierAppl(SmAppl supplierAppl) {
		this.supplierAppl = supplierAppl;
	}

	public SmAppl getPoAppl() {
		return poAppl;
	}

	public void setPoAppl(SmAppl poAppl) {
		this.poAppl = poAppl;
	}

	public String getOverLappedCategoryRemarks() {
		return overLappedCategoryRemarks;
	}

	public void setOverLappedCategoryRemarks(String overLappedCategoryRemarks) {
		this.overLappedCategoryRemarks = overLappedCategoryRemarks;
	}

	public boolean isShowMachineryEquipmentPanel() {
		return showMachineryEquipmentPanel;
	}

	public void setShowMachineryEquipmentPanel(
			boolean showMachineryEquipmentPanel) {
		this.showMachineryEquipmentPanel = showMachineryEquipmentPanel;
	}

	public List<SelectItem> getProfBodyItems() {
		return profBodyItems;
	}

	public void setProfBodyItems(List<SelectItem> profBodyItems) {
		this.profBodyItems = profBodyItems;
	}

	public List<PmReasonDesc> getSelectedReasonDescs() {
		return selectedReasonDescs;
	}

	public void setSelectedReasonDescs(List<PmReasonDesc> selectedReasonDescs) {
		this.selectedReasonDescs = selectedReasonDescs;
	}

	public List<PmReasonDesc> getReasonDescInitList() {
		return reasonDescInitList;
	}

	public void setReasonDescInitList(List<PmReasonDesc> reasonDescInitList) {
		this.reasonDescInitList = reasonDescInitList;
	}

	public Long[] getSelectedSuppCategoryFacilityIds() {
		return selectedSuppCategoryFacilityIds;
	}

	public void setSelectedSuppCategoryFacilityIds(
			Long[] selectedSuppCategoryFacilityIds) {
		this.selectedSuppCategoryFacilityIds = selectedSuppCategoryFacilityIds;
	}

	public List<SmApplSupportDoc> getMandatoryDocs() {
		return mandatoryDocs;
	}

	public void setMandatoryDocs(List<SmApplSupportDoc> mandatoryDocs) {
		this.mandatoryDocs = mandatoryDocs;
	}

	public List<SmApplSupportDoc> getNonMandatoryDocs() {
		return nonMandatoryDocs;
	}

	public void setNonMandatoryDocs(List<SmApplSupportDoc> nonMandatoryDocs) {
		this.nonMandatoryDocs = nonMandatoryDocs;
	}

	public List<Long> getSvTaskIdsNeedToDelete() {
		return svTaskIdsNeedToDelete;
	}

	public void setSvTaskIdsNeedToDelete(List<Long> svTaskIdsNeedToDelete) {
		this.svTaskIdsNeedToDelete = svTaskIdsNeedToDelete;
	}
}
