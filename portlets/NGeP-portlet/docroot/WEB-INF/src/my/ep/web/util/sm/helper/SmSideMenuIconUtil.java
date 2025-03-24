package my.ep.web.util.sm.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.factory.EpLogger;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmStatus;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplDocFile;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmMofAccount;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierBranchBank;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.persistence.entity.sm.SmWorkflowStatus;
import my.ep.type.pm.RoleCode;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;
import my.ep.web.managedbean.sm.applicationView.BaseReviewApplicationManagedBean;
import my.ep.web.vo.SmLicenseInfoVO;
import my.ep.web.vo.SmSectionEditorVO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SmSideMenuIconUtil {
	private static Logger log = EpLogger.getLogger(SmSideMenuIconUtil.class);
	private static final String ICON_TICK = "/css/images/design/ico-tick-checkbox.png";
	private static final String ICON_UNTICK = "/css/images/design/ico-disabled-chckbx.png";
	private static final String ICON_VIEW = "/css/images/design/ico-view.png";

	public static void setSectionEditorVO(SmApplicationManagedBean bean, String sectionType) {
		SmApplicationUtil.checkOverLappingCategories(bean); // #31018
		if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)) {
			if (bean.isSupplierUser()) {
				bean.getEditVO().setSuppEditCompBasic(false);
				
				if (step1CompanyBasicHasDifference(bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getEditVO().setSuppEditCompBasic(true);
				}

				// used to check got changes to display supporting doc
				if (step1CompanyBasicHasDifference(bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getSupplierEditVO().setSuppEditCompBasic(true);
				}
			} else if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditCompBasic(false);
				bean.getEditVO().setPoEditCompBasic(false);

				if (step1CompanyBasicHasDifference(bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditCompBasic(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditCompBasic(false);
				bean.getEditVO().setPoEditCompBasic(false);
				bean.getEditVO().setAppEditCompBasic(false);

				if (step1CompanyBasicHasDifference(bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditCompBasic(true);
				}
			}
		} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BRANCH)) {
			if (bean.isSupplierUser()) {
				bean.getEditVO().setSuppEditAssocProj(false);
				
				if (step2AssocCompProjExpHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getEditVO().setSuppEditAssocProj(true);
				}
				// used to check got changes to display supporting doc
				if (step2AssocCompProjExpHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getSupplierEditVO().setSuppEditAssocProj(true);
				}
			} else if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditAssocProj(false);
				bean.getEditVO().setPoEditAssocProj(false);
				
				if (step2AssocCompProjExpHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditAssocProj(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditAssocProj(false);
				bean.getEditVO().setPoEditAssocProj(false);
				bean.getEditVO().setAppEditAssocProj(false);

				if (step2AssocCompProjExpHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditAssocProj(true);
				}
			}
		} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)) {
			if (bean.isSupplierUser()) {
				bean.getEditVO().setSuppEditPersonnel(false);

				if (step3PersonnelHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl(), bean.getDowngradeMofAcc())) {
					bean.getEditVO().setSuppEditPersonnel(true);
				}
				// used to check got changes to display supporting doc
				if (step3PersonnelHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl(), bean.getDowngradeMofAcc())) {
					bean.getSupplierEditVO().setSuppEditPersonnel(true);
					bean.getSupplierEditVO().setHasForeign(SmApplicationUtil.isCompanyHasForeignPersonnel(bean.getSmAppl().getSmPersonnels()));
				}
			} else if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditPersonnel(false);
				bean.getEditVO().setPoEditPersonnel(false);
				
				if (step3PersonnelHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl(), bean.getDowngradeMofAcc())) {

					bean.getEditVO().setSuppEditPersonnel(true);
				}

				log.info("31230 Check if PO edit personnel");

			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditPersonnel(false);
				bean.getEditVO().setPoEditPersonnel(false);
				bean.getEditVO().setAppEditPersonnel(false);

				if (step3PersonnelHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl(), bean.getDowngradeMofAcc())) {
					bean.getEditVO().setSuppEditPersonnel(true);
				}
			}
		} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			if (bean.isSupplierUser()) {
				bean.getEditVO().setSuppEditEquity(false);

				if (step4EquityHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getEditVO().setSuppEditEquity(true);
				}
				// used to check got changes to display supporting doc
				if (step4EquityHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getSupplierEditVO().setSuppEditEquity(true);
				}
			} else if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditEquity(false);
				bean.getEditVO().setPoEditEquity(false);

				if (step4EquityHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditEquity(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditEquity(false);
				bean.getEditVO().setPoEditEquity(false);
				bean.getEditVO().setAppEditEquity(false);

				if (step4EquityHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditEquity(true);
				}
			}
		} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BANK)) {
			if (bean.isSupplierUser()) {
				bean.getEditVO().setSuppEditBank(false);

				if (step5BankHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getEditVO().setSuppEditBank(true);
				}
				// used to check got changes to display supporting doc
				if (step5BankHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getSupplierEditVO().setSuppEditBank(true);
				}
			} else if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditBank(false);
				bean.getEditVO().setPoEditBank(false);

				if (step5BankHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditBank(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditBank(false);
				bean.getEditVO().setPoEditBank(false);
				bean.getEditVO().setAppEditBank(false);

				if (step5BankHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditBank(true);
				}
			}
		} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
			if (bean.isSupplierUser()) {
				bean.getEditVO().setSuppEditCategory(false);

				if (step6CatCodeHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getEditVO().setSuppEditCategory(true);
				}
				// used to check got changes to display supporting doc
				if (step6CatCodeHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getSupplierEditVO().setSuppEditCategory(true);
				}

			} else if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditCategory(false);
				bean.getEditVO().setPoEditCategory(false);

				if (step6CatCodeHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditCategory(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditCategory(false);
				bean.getEditVO().setPoEditCategory(false);
				bean.getEditVO().setAppEditCategory(false);

				if (step6CatCodeHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditCategory(true);
				}
			}
			// #31230 START
			if (step6OverlappedCategory(bean.isOverLappedCategoryExists())) {
				bean.getEditVO().setHasOverlapped(true);
			} else {
				bean.getEditVO().setHasOverlapped(false); // #31018
			}
			// #31230 END
		} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY)) {
			if (bean.isSupplierUser()) {
				bean.getEditVO().setSuppEditFacility(false);

				if (step7FacilityHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getEditVO().setSuppEditFacility(true);
				}
				// used to check got changes to display supporting doc
				if (step7FacilityHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getSupplierEditVO().setSuppEditFacility(true);
				}
			} else if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditFacility(false);
				bean.getEditVO().setPoEditFacility(false);

				if (step7FacilityHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditFacility(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditFacility(false);
				bean.getEditVO().setPoEditFacility(false);
				bean.getEditVO().setAppEditFacility(false);

				if (step7FacilityHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditFacility(true);
				}
			}
		} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUIP)) {
			if (bean.isSupplierUser()) {
				bean.getEditVO().setSuppEditEquipment(false);

				if (step8EquipmentHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getEditVO().setSuppEditEquipment(true);
				}
				// used to check got changes to display supporting doc
				if (step8EquipmentHasDifference(true, bean.getLatestApproveAppl(), bean.getSmAppl())) {
					bean.getSupplierEditVO().setSuppEditEquipment(true);
				}
			} else if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditEquipment(false);
				bean.getEditVO().setPoEditEquipment(false);

				if (step8EquipmentHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditEquipment(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditEquipment(false);
				bean.getEditVO().setPoEditEquipment(false);
				bean.getEditVO().setAppEditEquipment(false);

				if (step8EquipmentHasDifference(true, bean.getLatestApproveAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditEquipment(true);
				}
			}
		}
	}

	public static void setAllSectionEditorVO(SmApplicationManagedBean bean, boolean setAll, String page) {
		if (setAll) {
			setSectionEditorVO(bean, SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC);
			setSectionEditorVO(bean, SupplierManagementConst.SM_APPL_PAGE_TYPE_BRANCH);
			setSectionEditorVO(bean, SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL);
			setSectionEditorVO(bean, SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY);
			setSectionEditorVO(bean, SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY);
			setSectionEditorVO(bean, SupplierManagementConst.SM_APPL_PAGE_TYPE_BANK);
			setSectionEditorVO(bean, SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY);
			setSectionEditorVO(bean, SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUIP);
			setSectionEditorVO(bean, SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS);
		} else {
			setSectionEditorVO(bean, page);
		}
	}

	public static String getSideMenuVerificationIcon(SmApplicationManagedBean bean, String sectionType) {
		String retVal = StringUtils.EMPTY;
		// #31043 START
		PmStatus applCurrentStatus = null;
		if (bean.getSmAppl().getSmWorkflowStatuses() != null) {
			for (SmWorkflowStatus workflowStatus : bean.getSmAppl().getSmWorkflowStatuses()) {
				if (workflowStatus.getIsCurrent()) {
					applCurrentStatus = workflowStatus.getPmStatus();
				}
			}
		}
		// #31043 END
		if (!bean.isSupplierUser()) {
			retVal = ICON_UNTICK;
			SmApplSectionReview rev = bean.isPoUser() ? bean.getPoSecReview() : bean.getApprvrSecReview();
			if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC) && rev.isBasicVerified() && rev.isStaffVerified() && rev.isOfficeFacilityVerfied() && rev.isCertVerified()) {
				if (bean.getSmAppl().getSupplierType().equals("J")) {
					if (rev.isAssocBodyVerified()) {
						retVal = ICON_TICK;
					}
				} else {
					if (rev.isTrnsNetworkVerified()) {
						retVal = ICON_TICK;
					}
				}
			} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BRANCH) && rev.isAssociateVerified() && rev.isExperienceVerified()) {
				retVal = ICON_TICK;
			} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL) && rev.isPersonnelVerified() && rev.isBranchVerified()) {
				retVal = ICON_TICK;
			} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
				if (bean.getBusinessTypeVal().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT)) {
					if (rev.isCaptialVerified()) {
						retVal = ICON_TICK;
					}
				} else {
					if (rev.isCaptialVerified() && rev.isShareholderVerifed()) {
						if (bean.getBusinessTypeVal().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)
								|| bean.getBusinessTypeVal().equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)) {
							if (rev.isEquityCompVerifed()) {
								retVal = ICON_TICK;
							}
						} else {
							retVal = ICON_TICK;
						}
					}
				}
			} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BANK) && rev.isBankVerified()) {
				retVal = ICON_TICK;
			} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
				if (bean.getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI) || bean.getSmAppl().getApplType().equals(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)) {
					retVal = ICON_VIEW;
				} else {
					// #31043
					if (bean.isReviewSiteVisit()) {
						retVal = ICON_TICK;
					}

					boolean isAllCatcodeVerified = true;
					if (!EntityHelper.isCollectionEmpty(bean.getSmAppl().getSmSupplierCategories())) {
						for (SmSupplierCategory cat : bean.getSmAppl().getSmSupplierCategories()) {
							if (bean.isPoUser()
									&& (cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS || cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL)
									&& cat.getPoDecFlg() == null) {
								isAllCatcodeVerified = false;
							} else if (bean.isApproverUser()
									&& (cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS || cat.getRecordStatusFlg() == DbValueConst.RECORD_STATUS_PENDING_RE_APPROVAL)
									&& cat.getApprvrDecFlg() == null) {
								isAllCatcodeVerified = false;
							}
						}
						// Bug #25676 : SM Showstopper - No Categories Appear in Processing Officer Screen
						// to check, if data not exist no need to auto check
						if (bean.isPoUser() && isAllCatcodeVerified && (bean.getActiveCategoryList() == null || bean.getActiveCategoryList().size() <= 0)) {
							// #31043 start
							if (applCurrentStatus.getStatusId() != Long.parseLong(SupplierManagementConst.PM_STATUS_PENDING_REVISE_APPLICATION)) {
								if (bean.getApprovedCategoryList() != null) {
									isAllCatcodeVerified = true;

								} else {
									isAllCatcodeVerified = false;
								}
							}
							// #31043 end
						}

						if (isAllCatcodeVerified) {
							retVal = ICON_TICK;
						}
					}
				}
			} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY)) {
				retVal = ICON_TICK;

				if ((bean.isShowDockyardPanel() || bean.isShowFactoryPanel() || bean.isShowStorePanel() || bean.isShowWorkShopPanel()) && !rev.isFactoryVerify()) {
					retVal = ICON_UNTICK;
				}
			} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUIP)) {
				retVal = ICON_TICK;
				if (retVal.equals(ICON_TICK) && bean.isShowMachineryEquipmentPanel() && !rev.isMachineryVerified()) {
					retVal = ICON_UNTICK;
				}
				if (retVal.equals(ICON_TICK) && bean.isShowFactoryPanel() && !rev.isManufacturingVerified()) {
					retVal = ICON_UNTICK;
				}
				if (retVal.equals(ICON_TICK) && bean.isShowTravelSoftwarePanel() && !rev.isSupplierSystemVerified()) {
					retVal = ICON_UNTICK;
				}
			} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS)) {

				// 31043 - start
				boolean mandatoryDocVerified = false;
				boolean nonMandatoryDocVerified = true;
				boolean licenseMandatoryDocVerified = true;

				List<SmApplSupportDoc> licenseMandatoryDocs = new ArrayList<>();
				if (!EntityHelper.isCollectionEmpty(bean.getMandatoryLicenseDocs())) {
					for (SmLicenseInfoVO docVO : bean.getMandatoryLicenseDocs()) {
						licenseMandatoryDocs.addAll(docVO.getLicenseSuppDocs());
					}
				}

				if (!EntityHelper.isCollectionEmpty(licenseMandatoryDocs)) {
					for (SmApplSupportDoc doc : licenseMandatoryDocs) {
						if (bean.isPoUser() && doc.getVerifiedFlg() != null && doc.getVerifiedFlg() == 0) {
							licenseMandatoryDocVerified = false;
							break;
						}
						if (bean.isApproverUser() && doc.getVerifiedFlg() != null) {
							doc.setApproverVerifiedFlg(doc.getVerifiedFlg());
							if (doc.getApproverVerifiedFlg() == 0) {
								licenseMandatoryDocVerified = false;
								break;
							}
						}
					}
				}

				if (SmApplicationUtil.isAllSuppDocAlreadyVerified(bean.getMandatoryDocs(), bean.getSmAppl().getSupportingDocMode(), bean.isApproverUser())
						&& SmApplicationUtil.isAllSuppDocAlreadyVerified(bean.getNonMandatoryDocs(), bean.getSmAppl().getSupportingDocMode(), bean.isApproverUser())
						&& SmApplicationUtil.isAllSuppDocAlreadyVerified(licenseMandatoryDocs, bean.getSmAppl().getSupportingDocMode(), bean.isApproverUser())) {
					retVal = ICON_TICK;
				}

				int countDocs = 0;
				int countVerifiedDocs = 0;
				if (!EntityHelper.isCollectionEmpty(bean.getMandatoryDocs())) {
					for (SmApplSupportDoc doc : bean.getMandatoryDocs()) {
						countDocs++;
						if (bean.isPoUser()) {
							if (doc.getVerifiedFlg() != null) {
								if (doc.getVerifiedFlg() == 1)
									countVerifiedDocs++;
							}
						} else if (bean.isApproverUser()) {
							if (doc.getApproverVerifiedFlg() != null) {
								if (doc.getApproverVerifiedFlg() == 1)
									countVerifiedDocs++;
							}
						}
					}
					log.info("No of docs: " + countDocs);
					log.info("No of docs verified " + countVerifiedDocs);
					if (countDocs != 0 && countVerifiedDocs != 0)
						if (countVerifiedDocs == countDocs)
							mandatoryDocVerified = true;
						else
							mandatoryDocVerified = false;
				}

				if (!EntityHelper.isCollectionEmpty(bean.getNonMandatoryDocs())) {
					for (SmApplSupportDoc doc : bean.getNonMandatoryDocs()) {
						if (doc.getSmApplDocFiles() != null) {
							for (SmApplDocFile docFile : doc.getSmApplDocFiles()) {
								if (bean.isPoUser() && !(" ").equals(docFile.getFilePath()) 
										&& (doc.getVerifiedFlg() == null || (doc.getVerifiedFlg() != null && doc.getVerifiedFlg() == 0))) {
									nonMandatoryDocVerified = false;
									break;
								}
								if (bean.isApproverUser() && !(" ").equals(docFile.getFilePath()) 
										&& doc.getApproverVerifiedFlg() != null && doc.getApproverVerifiedFlg() == 0) {
									nonMandatoryDocVerified = false;
									break;
								}
							}
							if (!nonMandatoryDocVerified) {
								break;
							}
						}
					}
				}

				if (mandatoryDocVerified) {
					retVal = ICON_TICK;
					if (!licenseMandatoryDocVerified || !nonMandatoryDocVerified) {
						retVal = ICON_UNTICK;
					}
				} else {
					retVal = ICON_UNTICK;
				}

			} else if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_SITEVISITREV) && rev.isSitevisitVerified()) {
				retVal = ICON_UNTICK;
				if (rev.isSitevisitVerified())
					retVal = ICON_TICK;
			}
			// #31043 - end
		}
		return retVal;
	}

	public static void setPrevApplByRole(SmApplicationManagedBean bean) {
		if (bean.isPoUser()) {
			bean.setSupplierAppl(getSmApplFromSerFile(bean.getSmAppl().getApplNo(), "SUP"));
		} else if (bean.isApproverUser()) {
			// #31230 & #24964 - START
			if (!SmCommonUtil.isSerFileExist(bean.getSmAppl().getApplNo(), "PO")) {
				SmCommonUtil.createSerFile(bean.getSmAppl(), "PO");
			}
			// #31230 & #24964 - END
			bean.setSupplierAppl(getSmApplFromSerFile(bean.getSmAppl().getApplNo(), "SUP"));
			bean.setPoAppl(getSmApplFromSerFile(bean.getSmAppl().getApplNo(), "PO"));
		}
		// #31230 & #24964 - start
		if (bean.getSupplierAppl() == null) {
			SmCommonUtil.createSerFile(bean.getSmAppl(), "SUP");
			bean.setSupplierAppl(getSmApplFromSerFile(bean.getSmAppl().getApplNo(), "SUP"));
		}
		// #31230 & #24964 - end
	}

	private static boolean step1CompanyBasicHasDifference(SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			// companyBasic
			retVal = EntityHelper.isObjectDifference(applPrev.getSmCompanyBasic(), applCurr.getSmCompanyBasic(), 
					new String[] { "paidUpCapital", "authorizedCapital", "totalShare", "totalMember",
					"bumiEquity", "nonBumiEquity", "foreignerEquity", "govtUnitrustEquity", "govtEquity", 
					"foreignCompanyEquity", "localCompanyEquity", "bumiPercentage", "nonBumiPercentage" });
			if (!retVal) {
				if (applPrev.getSmCompanyBasic() != null && applCurr.getSmCompanyBasic() != null) {
					if (!EntityHelper.isCollectionEmpty(applPrev.getSmCompanyBasic().getSmCompanyAddresses()) 
							&& !EntityHelper.isCollectionEmpty(applCurr.getSmCompanyBasic().getSmCompanyAddresses())) {

						// companyBasic address
						retVal = EntityHelper.isObjectDifference(applPrev.getSmCompanyBasic().getSmCompanyAddresses().get(0), 
								applCurr.getSmCompanyBasic().getSmCompanyAddresses().get(0),
								new String[] { "smCompanyBasic" });
					}
				}
			}

			if (!retVal) {
				// staff total
				retVal = EntityHelper.hasDiffBetweenList(applPrev.getSmStaffTotals(), applCurr.getSmStaffTotals(), new String[] { "staffCategory" }, null, null);
			}

			if (!retVal) {
				// HQ
				SmFacility hqCurr = null;
				for (SmFacility hqPrev : applPrev.getSmFacilities()) {
					if ((hqPrev.getFacilityType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE) || hqPrev.getFacilityType().equalsIgnoreCase(
							SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE))
							&& hqPrev.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
						hqCurr = SmApplicationUtil.getHqFacilityByAppl(applCurr);

						if (hqPrev.getOwnershipType().equals(hqCurr.getOwnershipType())) {
							if (hqCurr.getOwnershipType().equals("R") && hqPrev.getOwnershipType().equals("R")) {
								retVal = EntityHelper.isObjectDifference(hqPrev.getSmAddress(), hqCurr.getSmAddress(), null);
							}

							if (!retVal) {
								retVal = EntityHelper.isObjectDifference(hqPrev, hqCurr, new String[] { "smAddress", "facilityName" });
							}
						} else {
							retVal = true;
						}

						break;
					}
				}
			}

			if (!retVal) {
				// Certificate
				retVal = EntityHelper.hasDiffBetweenList(applPrev.getSmSupplierCerts(), applCurr.getSmSupplierCerts(), 
						new String[] { "certTypeId", "certName" }, new String[] { "pmCertBody" }, null);

			}

			if (!retVal && !EntityHelper.isCollectionEmpty(applPrev.getSmAssociationBodies())) {
				// Professional Body
				SmAssociationBody bodyCurr = null;
				for (SmAssociationBody bodyPrev : applPrev.getSmAssociationBodies()) {
					if (bodyPrev.getSmPersonnel() == null) {
						bodyCurr = (SmAssociationBody) EntityHelper.getEntityByMultiplePropertiesFromList(bodyPrev, applCurr.getSmAssociationBodies(), 
								new String[] { "expDate", "regNo" },
								new String[] {}, null);
						retVal = EntityHelper.isObjectDifference(bodyPrev, bodyCurr, new String[] { "smPersonnel", "recordStatusFlg" });
						if (retVal) {
							break;
						}
					}
				}
			}
		}
		return retVal;
	}

	private static boolean step2AssocCompProjExpHasDifference(boolean isSupplier, SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			String[] searchParam = null;

			// associate company
			if (isSupplier) {
				searchParam = new String[] { "regNo" };
			} else {
				searchParam = new String[] { "associateCompanyId" };
			}
			retVal = EntityHelper.hasDiffBetweenList(applPrev.getSmAssociateCompanies(), applCurr.getSmAssociateCompanies(), searchParam, null, null);

			// company experience
			if (!retVal) {
				if (isSupplier) {
					searchParam = new String[] { "clientName", "title" };
				} else {
					searchParam = new String[] { "companyExperienceId" };
				}
				retVal = EntityHelper.hasDiffBetweenList(applPrev.getSmCompanyExperiences(), applCurr.getSmCompanyExperiences(), searchParam, null, null);

				if (!retVal) {
					SmCompanyExperience prevExp = null;
					if (!EntityHelper.isCollectionEmpty(applPrev.getSmCompanyExperiences()) && !EntityHelper.isCollectionEmpty(applCurr.getSmCompanyExperiences())) {
						for (SmCompanyExperience currExp : applCurr.getSmCompanyExperiences()) {
							prevExp = (SmCompanyExperience) EntityHelper.getEntityByMultiplePropertiesFromList(currExp, applPrev.getSmCompanyExperiences(), 
									searchParam, null, null);
							retVal = EntityHelper.isObjectDifference(prevExp, currExp, null);
							if (retVal) {
								break;
							}
							retVal = EntityHelper.hasDiffBetweenList(prevExp.getSmCompanyExperienceCats(), currExp.getSmCompanyExperienceCats(), null, 
									new String[] { "pmCategoryL3" },
									new String[] { "smCompanyExperience" });
						}
					}
				}
			}
		}		
		return retVal;
	}

	private static boolean step3PersonnelHasDifference(boolean isSupplier, SmAppl applPrev, SmAppl applCurr, SmMofAccount downgradeMofAcc) {
		boolean retVal = false;
		String[] excludeProps = new String[] { "smPersonnel" };
		String[] primitive = null;
		String[] nonPrimitive = null;

		if (applPrev != null && applCurr != null) {

			// smPersonnel
			if (!EntityHelper.isCollectionEmpty(applPrev.getSmPersonnels()) && !EntityHelper.isCollectionEmpty(applCurr.getSmPersonnels())) {

				SmPersonnel prevPersonnel = null;
				for (SmPersonnel currPersonnel : applCurr.getSmPersonnels()) {

					// smPersonnel
					if (isSupplier) {
						primitive = new String[] { "identificationNo" };
					} else {
						primitive = new String[] { "personnelId" };
					}

					prevPersonnel = (SmPersonnel) EntityHelper.getEntityByMultiplePropertiesFromList(currPersonnel, applPrev.getSmPersonnels(), primitive, null, null);

					if (downgradeMofAcc != null && currPersonnel.getEpRole() != null && currPersonnel.getEpRole().equals(RoleCode.MOF_SUPPLIER_ADMIN.value())) {
						retVal = EntityHelper.isObjectDifference(prevPersonnel, currPersonnel, 
								new String[] { "shareAmt", "sharePercentage", "softcert", "softcertFlg", "epRole" });
					} else {
						retVal = EntityHelper.isObjectDifference(prevPersonnel, currPersonnel, 
								new String[] { "shareAmt", "sharePercentage", "softcert", "softcertFlg" });
					}

					if (retVal) {
						log.info(">>>>DIFFERENT IN PERSONNEL FOR PERSONNEL:" + currPersonnel.getName() + currPersonnel.getName() 
								+ "  SEARCH CRITERIA PRIMITIVE:" + EntityHelper.getArrayToPrint(primitive) 
								+ "  NON PRIMITIVE:" + EntityHelper.getArrayToPrint(nonPrimitive));
						break;
					}

					// smPersonnel address
					if (isSupplier) {
						primitive = new String[] { "addressType" };
					} else {
						primitive = new String[] { "personnelAddressId" };
					}
					retVal = EntityHelper.hasDiffBetweenList(prevPersonnel.getSmPersonnelAddresses(), currPersonnel.getSmPersonnelAddresses(), primitive, null, excludeProps);

					if (retVal) {
						log.info(">>>>DIFFERENT IN PERSONNEL ADDRESS FOR PERSONNEL:" + currPersonnel.getName() + currPersonnel.getName() 
								+ "  SEARCH CRITERIA PRIMITIVE:" + EntityHelper.getArrayToPrint(primitive) 
								+ "  NON PRIMITIVE:" + EntityHelper.getArrayToPrint(nonPrimitive));
						break;
					}

					// smStaffExperience
					if (isSupplier) {
						primitive = new String[] { "companyName", "fromYear", "toYear" };
					} else {
						primitive = new String[] { "staffExperienceId" };
					}
					retVal = EntityHelper.hasDiffBetweenList(prevPersonnel.getSmStaffExperiences(), currPersonnel.getSmStaffExperiences(), primitive, null, excludeProps);

					if (retVal) {
						log.info(">>>>DIFFERENT IN PERSONNEL STAFF EXPERIENCE FOR PERSONNEL:" + currPersonnel.getName() + currPersonnel.getName() 
								+ "  SEARCH CRITERIA PRIMITIVE:" + EntityHelper.getArrayToPrint(primitive) 
								+ "  NON PRIMITIVE:" + EntityHelper.getArrayToPrint(nonPrimitive));
						break;
					}

					// smRelatedCompany
					if (isSupplier) {
						primitive = new String[] { "regNo" };
					} else {
						primitive = new String[] { "relatedCompanyId" };
					}
					retVal = EntityHelper.hasDiffBetweenList(prevPersonnel.getSmRelatedCompanies(), currPersonnel.getSmRelatedCompanies(), primitive, null, excludeProps);

					if (retVal) {
						log.info(">>>>DIFFERENT IN PERSONNEL RELATED COMPANY FOR PERSONNEL:" + currPersonnel.getName() + currPersonnel.getName() 
								+ "  SEARCH CRITERIA PRIMITIVE:" + EntityHelper.getArrayToPrint(primitive) 
								+ "  NON PRIMITIVE:" + EntityHelper.getArrayToPrint(nonPrimitive));
						break;
					}

					// smAssociationBody
					if (isSupplier) {
						primitive = new String[] { "regNo", "expDate" };
						nonPrimitive = new String[] { "pmCertBody" };
					} else {
						primitive = new String[] { "associationBodyId" };
					}
					retVal = EntityHelper.hasDiffBetweenList(prevPersonnel.getSmAssociationBodies(), currPersonnel.getSmAssociationBodies(), primitive, nonPrimitive, excludeProps);

					if (retVal) {
						log.info(">>>>DIFFERENT IN PERSONNEL ASSOC BODY FOR PERSONNEL:" + currPersonnel.getName() + currPersonnel.getName() 
								+ "  SEARCH CRITERIA PRIMITIVE:" + EntityHelper.getArrayToPrint(primitive) 
								+ "  NON PRIMITIVE:" + EntityHelper.getArrayToPrint(nonPrimitive));
						break;
					}

					// smStaffAcademics
					if (isSupplier) {
						nonPrimitive = new String[] { "qualificationId", "pmEduField" };
						primitive = new String[] { "year", "instituteName" };
					} else {
						primitive = new String[] { "staffAcademicId" };
					}
					retVal = EntityHelper.hasDiffBetweenList(prevPersonnel.getSmStaffAcademics(), currPersonnel.getSmStaffAcademics(),
							primitive, nonPrimitive, excludeProps);

					if (retVal) {
						log.info(">>>>DIFFERENT IN PERSONNEL ACADEMIC FOR PERSONNEL:" + currPersonnel.getName() 
								+ "  SEARCH CRITERIA PRIMITIVE:" + EntityHelper.getArrayToPrint(primitive)
								+ "  NON PRIMITIVE:" + EntityHelper.getArrayToPrint(nonPrimitive));
						break;
					}
				}
			}

			// smSupplierBranch
			if (!retVal) {
				if (isSupplier) {
					primitive = new String[] { "branchCode" };
				} else {
					primitive = new String[] { "supplierBranchId" };
				}
				retVal = EntityHelper.hasDiffBetweenList(applPrev.getSmSupplierBranches(), applCurr.getSmSupplierBranches(), primitive, null, 
						new String[] { "contactPersonId" });
				if (retVal) {
					log.info(">>>>DIFFERENT IN SUPPLIER BRANCH");
				}
			}
		}
		return retVal;
	}

	private static boolean step4EquityHasDifference(boolean isSupplier, SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			String[] searchParam = null;

			// company equity status
			if (applPrev.getSmCompanyBasic() != null && applCurr.getSmCompanyBasic() != null) {

				SmCompanyBasic prev = applPrev.getSmCompanyBasic();
				SmCompanyBasic curr = applCurr.getSmCompanyBasic();

				if (EntityHelper.isBigDecimalValuesDifference(curr.getPaidUpCapital(), prev.getPaidUpCapital())) {
					retVal = true;
				} else if (!retVal && EntityHelper.isBigDecimalValuesDifference(curr.getAuthorizedCapital(), prev.getAuthorizedCapital())) {
					retVal = true;
				} else if (!retVal && EntityHelper.isBigDecimalValuesDifference(curr.getTotalShare(), prev.getTotalShare())) {
					retVal = true;
				} else if (!retVal && EntityHelper.isBigDecimalValuesDifference(curr.getTotalMember(), prev.getTotalMember())) {
					retVal = true;
				} else if (!retVal && EntityHelper.isBigDecimalValuesDifference(curr.getBumiEquity(), prev.getBumiEquity())) {
					retVal = true;
				} else if (!retVal && EntityHelper.isBigDecimalValuesDifference(curr.getNonBumiEquity(), prev.getNonBumiEquity())) {
					retVal = true;
				} else if (!retVal && EntityHelper.isBigDecimalValuesDifference(curr.getForeignerEquity(), prev.getForeignerEquity())) {
					retVal = true;
				} else if (!retVal && EntityHelper.isBigDecimalValuesDifference(curr.getGovtUnitrustEquity(), prev.getGovtUnitrustEquity())) {
					retVal = true;
				} else if (!retVal && EntityHelper.isBigDecimalValuesDifference(curr.getGovtEquity(), prev.getGovtEquity())) {
					retVal = true;
				} else if (!retVal && EntityHelper.isBigDecimalValuesDifference(curr.getForeignCompanyEquity(), prev.getForeignCompanyEquity())) {
					retVal = true;
				} else if (!retVal && EntityHelper.isBigDecimalValuesDifference(curr.getLocalCompanyEquity(), prev.getLocalCompanyEquity())) {
					retVal = true;
				}
			}

			// personnel shareholder
			if (!retVal && !EntityHelper.isCollectionEmpty(applPrev.getSmPersonnels()) && !EntityHelper.isCollectionEmpty(applCurr.getSmPersonnels())) {
				if (isSupplier) {
					searchParam = new String[] { "identificationNo" };
				} else {
					searchParam = new String[] { "personnelId" };
				}
				SmPersonnel prevShareholder = null;
				for (SmPersonnel currShareholder : applCurr.getSmPersonnels()) {
					prevShareholder = (SmPersonnel) EntityHelper.getEntityBySinglePropertyFromList(currShareholder.getIdentificationNo(), 
							applPrev.getSmPersonnels(), searchParam[0]);

					if (prevShareholder != null && EntityHelper.isBigDecimalValuesDifference(prevShareholder.getShareAmt(), currShareholder.getShareAmt())) {
						retVal = true;
					}
				}
			}

			// equity companies
			if (!retVal) {
				if (isSupplier) {
					searchParam = new String[] { "regNo" };
				} else {
					searchParam = new String[] { "equityCompanyId" };
				}
				retVal = EntityHelper.hasDiffBetweenList(applPrev.getSmEquityCompanies(), applCurr.getSmEquityCompanies(), searchParam, null, null);
			}
		}
		return retVal;
	}

	private static boolean step5BankHasDifference(boolean isSupplier, SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			String[] searchParam = null;

			// supplierBank
			if (!retVal) {
				if (isSupplier) {
					searchParam = new String[] { "accountNo", "accountPurpose" };
				} else {
					searchParam = new String[] { "supplierBankId" };
				}
				retVal = EntityHelper.hasDiffBetweenList(applPrev.getSmSupplierBanks(), applCurr.getSmSupplierBanks(), searchParam, null, null);

				if (!retVal) {
					SmSupplierBank prevBank = null;
					if (!EntityHelper.isCollectionEmpty(applPrev.getSmSupplierBanks()) && !EntityHelper.isCollectionEmpty(applCurr.getSmSupplierBanks())) {
						for (SmSupplierBank currBank : applCurr.getSmSupplierBanks()) {
							prevBank = (SmSupplierBank) EntityHelper.getEntityByMultiplePropertiesFromList(currBank, applPrev.getSmSupplierBanks(), 
									searchParam, null, null);
							retVal = EntityHelper.isObjectDifference(prevBank, currBank, null);
							if (retVal) {
								break;
							}

							// smSupplierBranchBanks
							retVal = isSupplierBranchBankHasDifference(prevBank.getSmSupplierBranchBanks(), currBank.getSmSupplierBranchBanks());

							// smChequeSignatories
							if (!retVal) {
								retVal = isChequeSignatoriesHasDifference(prevBank.getSmChequeSignatories(), currBank.getSmChequeSignatories());
							}
						}
					}
				}
			}
		}
		return retVal;
	}

	private static boolean step6CatCodeHasDifference(boolean isSupplier, SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			// 24964 - if equal means no changes
			if (applPrev.getApplId() == applCurr.getApplId()) {
				return false;
			}
			// #32008
			String[] arrExcludeProps = new String[] { 
					"prevApprDecFlg", "apprvrDecFlg", "poDecFlg", "approvedByPo", "prevApprovedByPo", "prevPoDecFlg", "poRemarks", "approverRemarks",
					"prevPoRemarks", "prevApproverRemarks", "approvedDate", "approvedByAp", "prevApprovedByAp", "recordStatusFlg", "overLapCategory", 
					"suspendCategory", "cancelCategory", "isActiveDA", "disciplinaryActionId", "supplierType", "applRefId", "prevOtherDesc", 
					"cancelledDate", "categoryEffDate" };

			// smSupplierCategory
			if (isSupplier) {
				// 30912 - if current bumi means no changes
				if (!applCurr.getApplType().isEmpty() && applCurr.getApplType().equalsIgnoreCase(SupplierManagementConst.APPL_TYPE_BUMI)) {
					return false;
				}
				List<SmSupplierCategory> prevCatList = new ArrayList<SmSupplierCategory>();
				List<SmSupplierCategory> currCatList = new ArrayList<SmSupplierCategory>();

				if (!EntityHelper.isCollectionEmpty(applPrev.getSmSupplierCategories())) {
					for (SmSupplierCategory prev : applPrev.getSmSupplierCategories()) {
						if (prev.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED && prev.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_SUSPENDED) {
							prevCatList.add(prev);
						}
					}
				}

				if (!EntityHelper.isCollectionEmpty(applCurr.getSmSupplierCategories())) {
					for (SmSupplierCategory curr : applCurr.getSmSupplierCategories()) {
						if (curr.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED && curr.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_SUSPENDED) {
							currCatList.add(curr);
						}
					}
				}

				retVal = EntityHelper.hasDiffBetweenList(prevCatList, currCatList, null, new String[] { "pmCategoryL3" }, arrExcludeProps);
			} else {
				retVal = EntityHelper.hasDiffBetweenList(applPrev.getSmSupplierCategories(), applCurr.getSmSupplierCategories(), new String[] { "supplierCategoryId" }, null, arrExcludeProps);
			}
		}
		return retVal;
	}
	
	//#31230 START
	private static boolean step6OverlappedCategory(boolean isOverlapped) {
		boolean retVal = false;
		if (isOverlapped) {
			retVal = true;
		}
		return retVal;
	}
	//#31230 END
	
	private static boolean step7FacilityHasDifference(boolean isSupplier, SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			String[] searchParam = null;

			List<SmFacility> prevFacList = new ArrayList<SmFacility>();
			if (!EntityHelper.isCollectionEmpty(applPrev.getSmFacilities())) {
				for (SmFacility prevFac : applPrev.getSmFacilities()) {
					if (!prevFac.getFacilityType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)
							&& !prevFac.getFacilityType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {
						prevFacList.add(prevFac);
					}
				}
			}

			List<SmFacility> currFacList = new ArrayList<SmFacility>();
			if (!EntityHelper.isCollectionEmpty(applCurr.getSmFacilities())) {
				for (SmFacility currFac : applCurr.getSmFacilities()) {
					if (!currFac.getFacilityType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)
							&& !currFac.getFacilityType().equalsIgnoreCase(SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {
						currFacList.add(currFac);
					}
				}
			}

			if (isSupplier) {
				searchParam = new String[] { "facilityType" };
			} else {
				searchParam = new String[] { "facilityId" };
			}
			retVal = EntityHelper.hasDiffBetweenList(prevFacList, currFacList, searchParam, null, null);
		}
		return retVal;
	}

	private static boolean step8EquipmentHasDifference(boolean isSupplier, SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			// smMachineDetails
			if (isSupplier) {
				retVal = EntityHelper.hasDiffBetweenList(applPrev.getSmMachineDtls(), applCurr.getSmMachineDtls(), 
						new String[] { "brandName", "machineFunction" }, new String[] { "categoryType" },
						null);
			} else {
				retVal = EntityHelper.hasDiffBetweenList(applPrev.getSmMachineDtls(), applCurr.getSmMachineDtls(),
						new String[] { "machineDtlId" }, null, null);
			}

			// Traveling software
			if (!retVal) {
				if (isSupplier) {
					retVal = EntityHelper.hasDiffBetweenList(applPrev.getSmSupplierSystems(), applCurr.getSmSupplierSystems(), null, 
							new String[] { "system" }, null);
					if (!retVal) {
						retVal = EntityHelper.hasDiffBetweenList(applPrev.getSmSupplierSystems(), applCurr.getSmSupplierSystems(), 
								new String[] { "systemName" }, null, null);
					}
				} else {
					retVal = EntityHelper.hasDiffBetweenList(applPrev.getSmSupplierSystems(), applCurr.getSmSupplierSystems(), 
							new String[] { "supplierSystemId" }, null, null);
				}
			}
		}
		return retVal;
	}

	/*
	private static boolean step9SuppDocHasDifference(boolean isSupplier, SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			// only do for po and approver (check if got change in doc files
			// attachment only)
			if (!isSupplier) {
				SmApplSupportDoc prevDoc = null;
				if (!EntityHelper.isCollectionEmpty(applPrev.getSmApplSupportDocs()) && !EntityHelper.isCollectionEmpty(applCurr.getSmApplSupportDocs())) {
					for (SmApplSupportDoc currDoc : applCurr.getSmApplSupportDocs()) {
						prevDoc = (SmApplSupportDoc) EntityHelper.getEntityByMultiplePropertiesFromList(currDoc, applPrev.getSmApplSupportDocs(), 
								new String[] { "applSupportDocId" }, null, null);

						if (prevDoc == null) {
							retVal = true;
							break;
						}

						// docfiles
						retVal = EntityHelper.hasDiffBetweenList(prevDoc.getSmApplDocFiles(), currDoc.getSmApplDocFiles(), 
								new String[] { "applDocFileId" }, null, null);
					}
				}
			}
		}

		return retVal;
	}
	*/
	
	private static SmAppl getSmApplFromSerFile(String applNo, String serType) {
		SmAppl retVal = null;
		try {
			String fileName = SmCommonUtil.getSerializedFileFullDirByApplNo(applNo, serType);
			if (!StringUtils.isBlank(fileName)) {
				SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
				reqDTO.setDeSerFileName(fileName);
				SmApplRevisionResDTO resDTO = SmServiceLookup.getSmApplRevisionSO().readApplicationHist(reqDTO);
				if (resDTO.getSmAppl() != null) {
					retVal = resDTO.getSmAppl();
				}
			}
		} catch (Exception ex) {

		}
		return retVal;
	}

	private static boolean isSupplierBranchBankHasDifference(List<SmSupplierBranchBank> prevBranchBanks, List<SmSupplierBranchBank> currBranchBanks) {
		boolean retVal = false;

		if (EntityHelper.isCollectionEmpty(prevBranchBanks) && !EntityHelper.isCollectionEmpty(currBranchBanks)) {
			retVal = true;
		} else if (!retVal && !EntityHelper.isCollectionEmpty(prevBranchBanks) && EntityHelper.isCollectionEmpty(currBranchBanks)) {
			retVal = true;
		} else if (!retVal && !EntityHelper.isCollectionEmpty(prevBranchBanks) && !EntityHelper.isCollectionEmpty(currBranchBanks)) {

			// check if current exist in previous
			for (SmSupplierBranchBank currBrBank : currBranchBanks) {
				if (getSmSupplierBranchBank(prevBranchBanks, currBrBank.getSmSupplierBranch()) == null) {
					retVal = true;
					break;
				}
			}
			// check if previous exist in current
			for (SmSupplierBranchBank prevBrBank : prevBranchBanks) {
				if (getSmSupplierBranchBank(currBranchBanks, prevBrBank.getSmSupplierBranch()) == null) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}

	private static boolean isChequeSignatoriesHasDifference(List<SmChequeSignatory> prevChequeSignatories, List<SmChequeSignatory> currChequeSignatories) {
		boolean retVal = false;

		if (EntityHelper.isCollectionEmpty(prevChequeSignatories) && !EntityHelper.isCollectionEmpty(currChequeSignatories)) {
			retVal = true;
		} else if (!retVal && !EntityHelper.isCollectionEmpty(prevChequeSignatories) && EntityHelper.isCollectionEmpty(currChequeSignatories)) {
			retVal = true;
		} else if (!retVal && !EntityHelper.isCollectionEmpty(prevChequeSignatories) && !EntityHelper.isCollectionEmpty(currChequeSignatories)) {

			// check if current exist in previous
			for (SmChequeSignatory currChq : currChequeSignatories) {
				if (currChq.getSmPersonnel() != null && currChq.getSmPersonnel().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_SUSPENDED
						&& currChq.getSmPersonnel().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					if (getSmChequeSignatory(prevChequeSignatories, currChq.getSmPersonnel()) == null) {
						retVal = true;
						break;
					}
				}
			}
			// check if previous exist in current
			for (SmChequeSignatory prevChq : prevChequeSignatories) {
				if (prevChq.getSmPersonnel() != null && prevChq.getSmPersonnel().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_SUSPENDED
						&& prevChq.getSmPersonnel().getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
					if (getSmChequeSignatory(currChequeSignatories, prevChq.getSmPersonnel()) == null) {
						retVal = true;
						break;
					}
				}
			}
		}
		return retVal;
	}

	private static SmChequeSignatory getSmChequeSignatory(List<SmChequeSignatory> chequeSignatories, SmPersonnel personnel) {
		SmChequeSignatory retVal = null;
		for (SmChequeSignatory chqTmp : chequeSignatories) {
			if (chqTmp.getSmPersonnel() != null && personnel != null && chqTmp.getSmPersonnel().getIdentificationNo().equals(personnel.getIdentificationNo())) {
				retVal = chqTmp;
				break;
			}
		}
		return retVal;
	}

	private static SmSupplierBranchBank getSmSupplierBranchBank(List<SmSupplierBranchBank> branchBanks, SmSupplierBranch branch) {
		SmSupplierBranchBank retVal = null;
		for (SmSupplierBranchBank brBankTmp : branchBanks) {
			if (brBankTmp.getSmSupplierBranch() != null && branch != null && brBankTmp.getSmSupplierBranch().getBranchCode().equals(branch.getBranchCode())) {
				retVal = brBankTmp;
				break;
			}
		}
		return retVal;
	}
	
	//#32008 - start
	public static void setSectionEditorVO(BaseReviewApplicationManagedBean bean){
		if(!bean.isShowFieldChanges() || bean.getLatestAppl() == null || bean.getSmAppl() == null){
			bean.setEditVO(new SmSectionEditorVO());
			return;
		}
		
		bean.getEditVO().setSuppEditCompBasic(false);
		if (step1CompanyBasicHasDifference(bean.getLatestAppl(), bean.getSmAppl())) {
			bean.getEditVO().setSuppEditCompBasic(true);
		}
		
		bean.getEditVO().setSuppEditAssocProj(false);
		if (step2AssocCompProjExpHasDifference(true, bean.getLatestAppl(), bean.getSmAppl())) {
			bean.getEditVO().setSuppEditAssocProj(true);
		}	
		
		bean.getEditVO().setSuppEditPersonnel(false);
		if (step3PersonnelHasDifference(true, bean.getLatestAppl(), bean.getSmAppl(), null)) {
			bean.getEditVO().setSuppEditPersonnel(true);
		}
			
		bean.getEditVO().setSuppEditEquity(false);
		if (step4EquityHasDifference(true, bean.getLatestAppl(), bean.getSmAppl())) {
			bean.getEditVO().setSuppEditEquity(true);
		}
		
		bean.getEditVO().setSuppEditBank(false);
		if (step5BankHasDifference(true, bean.getLatestAppl(), bean.getSmAppl())) {
			bean.getEditVO().setSuppEditBank(true);
		}
		
		bean.getEditVO().setSuppEditCategory(false);
		
		if (step6CatCodeHasDifference(true, bean.getLatestAppl(),bean.getSmAppl())) {
			bean.getEditVO().setSuppEditCategory(true);
		}
		
		bean.getEditVO().setSuppEditFacility(false);
		if (step7FacilityHasDifference(true, bean.getLatestAppl(), bean.getSmAppl())) {
			bean.getEditVO().setSuppEditFacility(true);
		}
				
		bean.getEditVO().setSuppEditEquipment(false);
		if (step8EquipmentHasDifference(true, bean.getLatestAppl(), bean.getSmAppl())) {
			bean.getEditVO().setSuppEditEquipment(true);
		}
	}
	//#32008 - end
}
