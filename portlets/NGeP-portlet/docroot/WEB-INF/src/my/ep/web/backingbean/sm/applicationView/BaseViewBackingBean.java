package my.ep.web.backingbean.sm.applicationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;

import my.ep.constant.pm.ParameterConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.sm.SmTrackingDiaryConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.application.SupplierApplicationReqDTO;
import my.ep.dto.sm.application.SupplierApplicationResDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmGstSupplier;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.backingbean.sm.sitevisit.SvoSiteVisitBackingBean;
import my.ep.web.managedbean.sm.applicationView.BaseViewManagedBean;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

public abstract class BaseViewBackingBean<T extends BaseViewManagedBean>
		extends BaseReviewApplicationBackingBean<T> {

	public void setTabValue(int outputTab, String outputPageType) {
		super.setTabValue(outputTab, outputPageType);

		if (outputPageType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)) {
			initCompanyInfo(getManagedBean().getApplId());
		} else if (outputPageType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BRANCH)) {
			initAssocCompProjectInfo();
		} else if (outputPageType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)) {
			initPersonnelBranchInfo();
		} else if (outputPageType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			initEquityInfo();
		} else if (outputPageType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BANK)) {
			initBankInfo();
		} else if (outputPageType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
			initCategory();
		} else if (outputPageType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY)) {
			initFacilityInfo();
		} else if (outputPageType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUIP)) {
			initEquipmentInfo();
		} else if (outputPageType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS)) {
			initSuppDocInfo();
		} else if (outputPageType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_SITEVISITREV)) {
			initSiteVisit();
		} else if (outputPageType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_TRACKING_DIARY)) {
			initTrackingDiary();
		}
	}

	public void initCompanyInfo(Long applId) {
		if (!getManagedBean().isLoadCompanyInfo()) {
			SupplierApplicationReqDTO reqDTO = new SupplierApplicationReqDTO();
			reqDTO.setApplId(applId);
			reqDTO.setTab(new String[] {
					my.ep.constant.sm.SupplierManagementConst.TAB1_COMP_BASIC,
					my.ep.constant.sm.SupplierManagementConst.TAB1_STAFF_INFO,
					my.ep.constant.sm.SupplierManagementConst.TAB1_OFF_FAC,
					my.ep.constant.sm.SupplierManagementConst.TAB1_CERT_INFO,
					my.ep.constant.sm.SupplierManagementConst.TAB1_PROF_INFO });
			SupplierApplicationResDTO resDTO = SmServiceLookup
					.getSupplierApplicationSO().retrieveBasicInfoTab(reqDTO);
			getManagedBean().setSmAppl(resDTO.getAppl());
			getManagedBean().setSmCompanyBasic(resDTO.getCompanyBasic());
			getManagedBean().setSmStaffTotals(resDTO.getStaffTotals());
			getManagedBean().setSmSupplier(
					getManagedBean().getSmAppl().getSmSupplier());
			getManagedBean().setBasicCompanyFacility(resDTO.getHqFacility());
			getManagedBean().setSmSupplierCerts(resDTO.getSupplierCerts());
			getManagedBean().setSmAssociationBodies(
					resDTO.getCompanyAssocBodies());
			getManagedBean().setApprvrSecReview(resDTO.getAppSectionReview());
			getManagedBean().setPoSecReview(resDTO.getPoSectionReview());
			getManagedBean().setSmMofAccount(resDTO.getAcc());

			// set reason to cancel MOF (#24811)
			if (getManagedBean().getSmAppl().getApplType()
					.equals(ParameterConst.NEW_SM_APPLN_FOR_CANCEL)) {
				getManagedBean().setCancelRemarkVO(
						SmCommonUtil.getInstance().getCancelMofRemarksVO(
								getManagedBean().getSmAppl()));
			}

			setSupplierCancellationRemarks(getManagedBean().getSmAppl()
					.getApplType(), getManagedBean().getSmAppl().getApplId());
			setPmStatusDesc(getManagedBean().getSmAppl().getPmStatus() == null ? ""
					: getManagedBean().getSmAppl().getPmStatus().getStatusId()
							.toString());

			getManagedBean().setBusinessTypeVal(
					SmApplicationUtil
							.assignSupplierBusinessType(getManagedBean()
									.getSmSupplier()));
			getManagedBean()
					.setSupplierBusinessTypeVal(
							SmCommonUtil
									.getDropDownViewOnlyValueByCode(
											getManagedBean().getSmAppl()
													.getSupplierType()
													.equals("K") ? ParameterTypeConst.SUPPLIER_BUSINESS_TYPE
													: ParameterTypeConst.SUPPLIER_BUSINESS_TYPE_CONSULTANT,
											getManagedBean().getSmSupplier()
													.getBusinessType()));
			populateLocalAuthority(getManagedBean().getSmSupplier(),
					getManagedBean().getSmCompanyBasic()
							.getSmCompanyAddresses().get(0).getSmAddress());
			SelectItem item = (SelectItem) EntityHelper
					.getEntityBySinglePropertyFromList(getManagedBean()
							.getSmSupplier().getLocalAuthorityId(),
							getManagedBean().getLocalAuthorityItems(), "value");
			if (item != null) {
				getManagedBean().setLocalAuthority(item.getLabel());
			}
			getManagedBean().setHqGst(getSupplierGst(null));
			getManagedBean().setLoadCompanyInfo(true);
		}
	}

	public void initAssocCompProjectInfo() {
		if (!getManagedBean().isLoadAssocCompanyProjects()
				&& getManagedBean().getSmAppl() != null) {
			SupplierApplicationReqDTO reqDTO = new SupplierApplicationReqDTO();
			reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
			reqDTO.setTab(new String[] {
					my.ep.constant.sm.SupplierManagementConst.TAB2_ASSOC_COMP,
					my.ep.constant.sm.SupplierManagementConst.TAB2_COMP_EXP });
			SupplierApplicationResDTO resDTO = SmServiceLookup
					.getSupplierApplicationSO().retrieveSecondTab(reqDTO);
			getManagedBean().setSmAssociateCompanies(
					resDTO.getAssociateCompanies());
			getManagedBean().setSmCompanyExperiences(
					resDTO.getCompanyExperiences());
			getManagedBean().setLoadAssocCompanyProjects(true);
		}
	}

	public void initPersonnelBranchInfo() {
		if (!getManagedBean().isLoadPersonnelBranch()) {
			SupplierApplicationReqDTO reqDTO = new SupplierApplicationReqDTO();
			reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
			reqDTO.setTab(new String[] {
					my.ep.constant.sm.SupplierManagementConst.TAB3_BRANCH_INFO,
					my.ep.constant.sm.SupplierManagementConst.TAB3_PERS_INFO });
			SupplierApplicationResDTO resDTO = SmServiceLookup
					.getSupplierApplicationSO().retrieveThirdTab(reqDTO);
			getManagedBean().setSmSupplierBranches(
					resDTO.getSmSupplierBranches());
			for (SmSupplierBranch smSupplierBranch : getManagedBean()
					.getSmSupplierBranches()) {
				if (smSupplierBranch.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					smSupplierBranch.setGst(getSupplierGst(smSupplierBranch
							.getBranchCode() != null ? smSupplierBranch
							.getBranchCode() : smSupplierBranch
							.getSupplierBranchId()));
					if (smSupplierBranch.getGst() == null) {
						smSupplierBranch.setGst(new SmGstSupplier());
					}
					if (smSupplierBranch.getSmPersonnel() == null) {
						smSupplierBranch.setSmPersonnel(new SmPersonnel());
					}
					SmApplicationUtil
							.initGstSuppDocs(smSupplierBranch.getGst());
				}
			}
			getManagedBean().setSmPersonnels(resDTO.getSmPersonnels());
			getManagedBean().setPersonCalc(
					SmCommonUtil.calcPersonnelListCount(getManagedBean()
							.getSmPersonnels(), getManagedBean()
							.getSmStaffTotals(), getManagedBean().getSmAppl()
							.getSupplierType()));
			getManagedBean().setLoadPersonnelBranch(true);
		}
	}

	public void initEquityInfo() {
		if (!getManagedBean().isLoadCapitalEquityStatus()) {
			List<String> tabList = new ArrayList<String>();
			tabList.add(my.ep.constant.sm.SupplierManagementConst.TAB4_COMP_SHARE_INFO);

			if (getManagedBean().getSmPersonnels() == null) {
				tabList.add(my.ep.constant.sm.SupplierManagementConst.TAB4_INDV_SHARE_INFO);
			}
			if (getManagedBean().getSmCompanyBasic() == null) {
				tabList.add(my.ep.constant.sm.SupplierManagementConst.TAB4_EQUITY_INFO);
			}

			String[] tabArr = new String[tabList.size()];
			tabArr = tabList.toArray(tabArr);
			SupplierApplicationReqDTO reqDTO = new SupplierApplicationReqDTO();
			reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
			reqDTO.setTab(tabArr);
			SupplierApplicationResDTO resDTO = SmServiceLookup
					.getSupplierApplicationSO().retrieveFourthTab(reqDTO);

			getManagedBean()
					.setSmEquityCompanies(resDTO.getSmEquityCompanies());

			if (getManagedBean().getSmPersonnels() == null) {
				getManagedBean().setSmPersonnels(resDTO.getSmPersonnels());
			}
			if (getManagedBean().getSmCompanyBasic() == null) {
				getManagedBean().setSmCompanyBasic(resDTO.getCompanyBasic());
			}

			// captialOrEquityStatusInfoFormViewOnly
			SmCommonUtil
					.calculateSmApplicationPaidUpCapitalPercent(getManagedBean()
							.getSmCompanyBasic());

			// captialOrEquityShareHolderInfoFormViewOnly
			setApplicationShareHolders(getManagedBean().getSmPersonnels());

			if (getManagedBean().getPersonCalc() == null) {
				getManagedBean().setPersonCalc(
						SmCommonUtil.calcPersonnelListCount(getManagedBean()
								.getSmPersonnels(), getManagedBean()
								.getSmStaffTotals(), getManagedBean()
								.getSmAppl().getSupplierType()));
			}
			getManagedBean().setLoadCapitalEquityStatus(true);
		}
	}

	public void initBankInfo() {
		if (!getManagedBean().isLoadBankInfo()) {
			SupplierApplicationReqDTO reqDTO = new SupplierApplicationReqDTO();
			reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
			reqDTO.setTab(new String[] { my.ep.constant.sm.SupplierManagementConst.TAB5_SUPP_BANK_INFO });
			SupplierApplicationResDTO resDTO = SmServiceLookup
					.getSupplierApplicationSO().retrieveFifthTab(reqDTO);
			getManagedBean().setSmSupplierBankList(resDTO.getSmSupplierBanks());
			getManagedBean().setSelectedSignatoryBankAcct(new SmSupplierBank());
			removeDeletedFromList(getManagedBean().getSmSupplierBankList());
		}
	}

	public void initCategory() {
		if (!getManagedBean().isLoadCategoryInfo()) {
			SupplierApplicationReqDTO reqDTO = new SupplierApplicationReqDTO();
			reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
			reqDTO.setTab(new String[] { my.ep.constant.sm.SupplierManagementConst.TAB8_CATEGORY_INFO });
			SupplierApplicationResDTO resDTO = SmServiceLookup
					.getSupplierApplicationSO().retrieveEighthTab(reqDTO);
			getManagedBean().setSuppProfileOrHistoryCategoryList(
					new ArrayList<SmSupplierCategory>());
			List<SmSupplierCategory> catList = new ArrayList<>();
			for (SmSupplierCategory cat : resDTO.getSmSupplierCategories()) {
				if (cat.getRecordStatusFlg().compareTo(
						DbValueConst.RECORD_STATUS_ACTIVE) == 0
						|| cat.getRecordStatusFlg().compareTo(
								SupplierManagementConst.PENDING_ACT_STATUS) == 0) {
					catList.add(cat);
				}
				if (getManagedBean().isViewApplicationHistory()) {
					// for history load all category
					getManagedBean().getSuppProfileOrHistoryCategoryList().add(
							cat);
				} else {
					// for supp profile load all active category only
					if (cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_ACTIVE) {
						getManagedBean().getSuppProfileOrHistoryCategoryList()
								.add(cat);
					}
				}
			}
			getManagedBean().setActiveCategoryList(catList);

			// sort by record status
			Collections.sort(getManagedBean().getActiveCategoryList(),
					new Comparator<SmSupplierCategory>() {
						@Override
						public int compare(final SmSupplierCategory s1,
								final SmSupplierCategory s2) {
							return s1.getRecordStatusFlg()
									- s2.getRecordStatusFlg();
						}
					});

			SmApplicationUtil.sortSupplierCategory(getManagedBean()
					.getActiveCategoryList());
			getManagedBean().setLoadCategoryInfo(true);
		}
	}

	public void initFacilityInfo() {
		if (!getManagedBean().isLoadFacilityInfo()) {
			SupplierApplicationReqDTO reqDTO = new SupplierApplicationReqDTO();
			reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
			reqDTO.setTab(new String[] { my.ep.constant.sm.SupplierManagementConst.TAB6_FACILITY_INFO });
			SupplierApplicationResDTO resDTO = SmServiceLookup
					.getSupplierApplicationSO().retrieveSixthTab(reqDTO);
			List<SmFacility> facilities = resDTO.getSmFacilities();
			reqDTO.setTab(new String[] { my.ep.constant.sm.SupplierManagementConst.TAB8_CATEGORY_INFO });
			List<SmSupplierCategory> categories = SmServiceLookup
					.getSupplierApplicationSO().retrieveEighthTab(reqDTO)
					.getSmSupplierCategories();
			
			//#32809 - start
			getManagedBean()
					.setDockYardFacilitiesVOList(
							getCategoryFacilityVO(
									my.ep.web.constant.sm.SupplierManagementConst.SM_FACILITY_TYPE_DOCKYARD,
									getManagedBean().getSmAppl(), facilities));
			getManagedBean()
					.setWorkshopFacilitiesVOList(
							getCategoryFacilityVO(
									my.ep.web.constant.sm.SupplierManagementConst.SM_FACILITY_TYPE_WORKSHOP,
									getManagedBean().getSmAppl(), facilities));
			getManagedBean()
					.setStoreFacilitiesVOList(
							getCategoryFacilityVO(
									my.ep.web.constant.sm.SupplierManagementConst.SM_FACILITY_TYPE_STORE,
									getManagedBean().getSmAppl(), facilities));
			getManagedBean()
					.setFactoryFacilitiesVOList(
							getCategoryFacilityVO(
									my.ep.web.constant.sm.SupplierManagementConst.SM_FACILITY_TYPE_FACTORY,
									getManagedBean().getSmAppl(), facilities));
			//#32809 - end
			getManagedBean().setLoadFacilityInfo(true);
		}
	}

	public void initEquipmentInfo() {
		if (!getManagedBean().isLoadEquipmentInfo()) {
			SupplierApplicationReqDTO reqDTO = new SupplierApplicationReqDTO();
			reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
			reqDTO.setTab(new String[] {
					my.ep.constant.sm.SupplierManagementConst.TAB7_MACHINE_INFO,
					my.ep.constant.sm.SupplierManagementConst.TAB7_MANUFACT_INFO,
					my.ep.constant.sm.SupplierManagementConst.TAB7_TRAVEL_SYS_INFO });
			SupplierApplicationResDTO resDTO = SmServiceLookup
					.getSupplierApplicationSO().retrieveSeventhTab(reqDTO);
			getManagedBean().setSmMachineDtls(resDTO.getSmMachineDtls());
			getManagedBean().setSmManufacturingDtls(
					resDTO.getSmManufacturingDtls());
			getManagedBean()
					.setSmSupplierSystems(resDTO.getSmSupplierSystems());
			getManagedBean().setLoadEquipmentInfo(true);
		}
	}

	public void initSuppDocInfo() {
		if (!getManagedBean().isLoadSuppDocInfo()) {
			SupplierApplicationReqDTO reqDTO = new SupplierApplicationReqDTO();
			reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
			reqDTO.setTab(new String[] { my.ep.constant.sm.SupplierManagementConst.TAB9_SUPP_DOC_INFO });
			SupplierApplicationResDTO resDTO = SmServiceLookup
					.getSupplierApplicationSO().retrieveNinthTab(reqDTO);
			getManagedBean().setSmApplSupportDocs(resDTO.getSupportDocs());
			initBumiRecommendNote();
			getManagedBean().setLoadSuppDocInfo(true);
		}
	}

	public void initBumiRecommendNote() {
		getManagedBean().setBumiRecommendationNote("");
		if (getManagedBean().getSmAppl() != null) {
			String applType = getManagedBean().getSmAppl().getApplType() == null ? ""
					: getManagedBean().getSmAppl().getApplType();
			String suppType = getManagedBean().getSmAppl().getSupplierType() == null ? ""
					: getManagedBean().getSmAppl().getSupplierType();

			if (getManagedBean().getSmPersonnels() == null
					|| getManagedBean().getSmSupplierBankList() == null) {
				SupplierApplicationReqDTO reqDTO = new SupplierApplicationReqDTO();
				reqDTO.setApplId(getManagedBean().getSmAppl().getApplId());
				SupplierApplicationResDTO resDTO = null;
				if (getManagedBean().getSmSupplierBankList() == null) {
					reqDTO.setTab(new String[] { my.ep.constant.sm.SupplierManagementConst.TAB5_SUPP_BANK_INFO });
					resDTO = SmServiceLookup.getSupplierApplicationSO()
							.retrieveFifthTab(reqDTO);
					getManagedBean().setSmSupplierBankList(
							resDTO.getSmSupplierBanks());
				}
				if (getManagedBean().getSmPersonnels() == null) {
					reqDTO.setTab(new String[] { my.ep.constant.sm.SupplierManagementConst.TAB3_PERS_INFO });
					resDTO = SmServiceLookup.getSupplierApplicationSO()
							.retrieveThirdTab(reqDTO);
					getManagedBean().setSmPersonnels(resDTO.getSmPersonnels());
					getManagedBean().setPersonCalc(
							SmCommonUtil.calcPersonnelListCount(
									getManagedBean().getSmPersonnels(),
									getManagedBean().getSmStaffTotals(),
									getManagedBean().getSmAppl()
											.getSupplierType()));
				}
			}
			getManagedBean().setBumiRecommendationNote(
					SmCommonUtil.displayBumiRecommendNote(getManagedBean()
							.getSmSupplierBankList(), getManagedBean()
							.getPersonCalc(), getManagedBean()
							.getCurrentPageType(), applType, suppType,
							getManagedBean().getSmCompanyBasic()
									.getBumiPercent(), getManagedBean()
									.isSupplierUser()));
		}

	}

	public void initSiteVisit() {
		SvoSiteVisitBackingBean svo = new SvoSiteVisitBackingBean();
		svo.getManagedBean().setAppId(getManagedBean().getSmAppl().getApplId());
		svo.reviewSiteVisitReport();
	}

	public void initTrackingDiary() {
		getManagedBean().getTrackingDiary().setTrackingDiaryParam(
				SmTrackingDiaryConst.SM_DEFAULT_DOC_TYPE,
				getManagedBean().getSmAppl().getApplId(), false);
		getManagedBean().getTrackingDiary().getManagedBean()
				.getSearchTrackingDiaryDataModel().reloadData();
	}

	public void viewApplication(SmAppl selectedSmAppl) {
		String baseUrl = "";
		String baseUrlLabel = "";
		String basePage = "";

		if (getManagedBean().isViewApplicationHistory()) {
			basePage = "/xhtml/sm/revisionSmApplView/viewHistory/smViewHistorySideMenu_BasePage.xhtml";
			if (this.getManagedBean().isBpkUser()) {
				baseUrl = "/supplier-profile-search";
			} else {
				baseUrl = "/supplier-profile";
			}
			baseUrlLabel = SmCommonUtil.getMessage("lbl.sm.supprofile", null);
		} else {
			basePage = "/xhtml/sm/revisionSmApplView/viewProfile/smViewProfileSideMenu_BasePage.xhtml";
			if (this.getManagedBean().isBpkUser()) {
				baseUrl = "/supplier-application-search";
			} else {
				baseUrl = "/application-history";
			}
			baseUrlLabel = SmCommonUtil.getMessage(
					"lbl.sm.supplier.application.history", null);
		}

		// reset previous session redirect to personnel private page
		getManagedBean().setRedirectToPersonPage(false);

		getManagedBean().setRejectReason(getApplicationRejectionReason());

		setSupplierProfileOrHistoryUrl(baseUrl, baseUrlLabel, selectedSmAppl
				.getSmSupplier().getSupplierId().toString());
		SmCommonUtil.redirectToPage(basePage, "", null, null);
	}

	public void backFromPersonnelPage() {
		if (getManagedBean().getCurrentPageType().equalsIgnoreCase(
				SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
		}
		getManagedBean().setRedirectToPersonPage(false);
		RequestContext.getCurrentInstance().reset(":form:tt:shareHolderDialog");
	}

	/**
	 * Redirect back to Search Supplier Profile page
	 * 
	 * @return String
	 */
	public String backToSearch() {
		if (getManagedBean().isViewApplicationHistory()) {
			return "searchHistoryPage.xhtml";
		} else {
			return "searchSupplierProfilePage.xhtml";
		}
	}
}
