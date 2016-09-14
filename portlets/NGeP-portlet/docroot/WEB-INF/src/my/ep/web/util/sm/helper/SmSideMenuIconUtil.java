package my.ep.web.util.sm.helper;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.pm.ParameterConst;
import my.ep.dto.sm.revision.SmApplRevisionReqDTO;
import my.ep.dto.sm.revision.SmApplRevisionResDTO;
import my.ep.helper.EntityHelper;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.sm.SmAppl;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmApplSupportDoc;
import my.ep.persistence.entity.sm.SmAssociationBody;
import my.ep.persistence.entity.sm.SmChequeSignatory;
import my.ep.persistence.entity.sm.SmCompanyBasic;
import my.ep.persistence.entity.sm.SmCompanyExperience;
import my.ep.persistence.entity.sm.SmFacility;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.persistence.entity.sm.SmSupplierBank;
import my.ep.persistence.entity.sm.SmSupplierBranch;
import my.ep.persistence.entity.sm.SmSupplierBranchBank;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;

import org.apache.commons.lang.StringUtils;

public class SmSideMenuIconUtil {
	private static final String ICON_TICK = "/css/images/design/ico-tick-checkbox.png";
	private static final String ICON_UNTICK = "/css/images/design/ico-disabled-chckbx.png";

	public static void setSectionEditorVO(SmApplicationManagedBean bean,
			String sectionType) {
		if (sectionType.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)) {
			if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditCompBasic(false);
				bean.getEditVO().setPoEditCompBasic(false);
				if (step1CompanyBasicHasDifference(bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditCompBasic(true);
				}
				if (step1CompanyBasicHasDifference(bean.getSupplierAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setPoEditCompBasic(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditCompBasic(false);
				bean.getEditVO().setPoEditCompBasic(false);
				bean.getEditVO().setAppEditCompBasic(false);
				if (step1CompanyBasicHasDifference(bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditCompBasic(true);
				}
				if (step1CompanyBasicHasDifference(bean.getSupplierAppl(),
						bean.getPoAppl())) {
					bean.getEditVO().setPoEditCompBasic(true);
				}
				if (step1CompanyBasicHasDifference(bean.getPoAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setAppEditCompBasic(true);
				}
			}
		} else if (sectionType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BRANCH)) {
			if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditAssocProj(false);
				bean.getEditVO().setPoEditAssocProj(false);
				if (step2AssocCompProjExpHasDifference(true,
						bean.getLatestAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditAssocProj(true);
				}
				if (step2AssocCompProjExpHasDifference(false,
						bean.getSupplierAppl(), bean.getSmAppl())) {
					bean.getEditVO().setPoEditAssocProj(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditAssocProj(false);
				bean.getEditVO().setPoEditAssocProj(false);
				bean.getEditVO().setAppEditAssocProj(false);
				if (step2AssocCompProjExpHasDifference(true,
						bean.getLatestAppl(), bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditAssocProj(true);
				}
				if (step2AssocCompProjExpHasDifference(false,
						bean.getSupplierAppl(), bean.getPoAppl())) {
					bean.getEditVO().setPoEditAssocProj(true);
				}
				if (step2AssocCompProjExpHasDifference(false, bean.getPoAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setAppEditAssocProj(true);
				}
			}
		} else if (sectionType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)) {
			if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditPersonnel(false);
				bean.getEditVO().setPoEditPersonnel(false);
				if (step3PersonnelHasDifference(true, bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditPersonnel(true);
				}
				if (step3PersonnelHasDifference(false, bean.getSupplierAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setPoEditPersonnel(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditPersonnel(false);
				bean.getEditVO().setPoEditPersonnel(false);
				bean.getEditVO().setAppEditPersonnel(false);
				if (step3PersonnelHasDifference(true, bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditPersonnel(true);
				}
				if (step3PersonnelHasDifference(false, bean.getSupplierAppl(),
						bean.getPoAppl())) {
					bean.getEditVO().setPoEditPersonnel(true);
				}
				if (step3PersonnelHasDifference(false, bean.getPoAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setAppEditPersonnel(true);
				}
			}
		} else if (sectionType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
			if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditEquity(false);
				bean.getEditVO().setPoEditEquity(false);
				if (step4EquityHasDifference(true, bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditEquity(true);
				}
				if (step4EquityHasDifference(false, bean.getSupplierAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setPoEditEquity(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditEquity(false);
				bean.getEditVO().setPoEditEquity(false);
				bean.getEditVO().setAppEditEquity(false);
				if (step4EquityHasDifference(true, bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditEquity(true);
				}
				if (step4EquityHasDifference(false, bean.getSupplierAppl(),
						bean.getPoAppl())) {
					bean.getEditVO().setPoEditEquity(true);
				}
				if (step4EquityHasDifference(false, bean.getPoAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setAppEditEquity(true);
				}
			}
		} else if (sectionType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BANK)) {
			if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditBank(false);
				bean.getEditVO().setPoEditBank(false);
				if (step5BankHasDifference(true, bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditBank(true);
				}
				if (step5BankHasDifference(false, bean.getSupplierAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setPoEditBank(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditBank(false);
				bean.getEditVO().setPoEditBank(false);
				bean.getEditVO().setAppEditBank(false);
				if (step5BankHasDifference(true, bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditBank(true);
				}
				if (step5BankHasDifference(false, bean.getSupplierAppl(),
						bean.getPoAppl())) {
					bean.getEditVO().setPoEditBank(true);
				}
				if (step5BankHasDifference(false, bean.getPoAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setAppEditBank(true);
				}
			}
		} else if (sectionType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
			if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditCategory(false);
				bean.getEditVO().setPoEditCategory(false);
				if (step6CatCodeHasDifference(true, bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditCategory(true);
				}
				if (step6CatCodeHasDifference(false, bean.getSupplierAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setPoEditCategory(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditCategory(false);
				bean.getEditVO().setPoEditCategory(false);
				bean.getEditVO().setAppEditCategory(false);
				if (step6CatCodeHasDifference(true, bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditCategory(true);
				}
				if (step6CatCodeHasDifference(false, bean.getSupplierAppl(),
						bean.getPoAppl())) {
					bean.getEditVO().setPoEditCategory(true);
				}
				if (step6CatCodeHasDifference(false, bean.getPoAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setAppEditCategory(true);
				}
			}
		} else if (sectionType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY)) {
			if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditFacility(false);
				bean.getEditVO().setPoEditFacility(false);
				if (step7FacilityHasDifference(true, bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditFacility(true);
				}
				if (step7FacilityHasDifference(false, bean.getSupplierAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setPoEditFacility(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditFacility(false);
				bean.getEditVO().setPoEditFacility(false);
				bean.getEditVO().setAppEditFacility(false);
				if (step7FacilityHasDifference(true, bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditFacility(true);
				}
				if (step7FacilityHasDifference(false, bean.getSupplierAppl(),
						bean.getPoAppl())) {
					bean.getEditVO().setPoEditFacility(true);
				}
				if (step7FacilityHasDifference(false, bean.getPoAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setAppEditFacility(true);
				}
			}
		} else if (sectionType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUIP)) {
			if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditEquipment(false);
				bean.getEditVO().setPoEditEquipment(false);
				if (step8EquipmentHasDifference(true, bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditEquipment(true);
				}
				if (step8EquipmentHasDifference(false, bean.getSupplierAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setPoEditEquipment(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditEquipment(false);
				bean.getEditVO().setPoEditEquipment(false);
				bean.getEditVO().setAppEditEquipment(false);
				if (step8EquipmentHasDifference(true, bean.getLatestAppl(),
						bean.getSupplierAppl())) {
					bean.getEditVO().setSuppEditEquipment(true);
				}
				if (step8EquipmentHasDifference(false, bean.getSupplierAppl(),
						bean.getPoAppl())) {
					bean.getEditVO().setPoEditEquipment(true);
				}
				if (step8EquipmentHasDifference(false, bean.getPoAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setAppEditEquipment(true);
				}
			}
		} else if (sectionType
				.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS)) {
			if (bean.isPoUser()) {
				bean.getEditVO().setSuppEditSuppDoc(false);
				bean.getEditVO().setPoEditSuppDoc(false);
				if (step9SuppDocHasDifference(false, bean.getSupplierAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setPoEditSuppDoc(true);
				}
			} else if (bean.isApproverUser()) {
				bean.getEditVO().setSuppEditSuppDoc(false);
				bean.getEditVO().setPoEditSuppDoc(false);
				bean.getEditVO().setAppEditSuppDoc(false);
				if (step9SuppDocHasDifference(false, bean.getSupplierAppl(),
						bean.getPoAppl())) {
					bean.getEditVO().setPoEditSuppDoc(true);
				}
				if (step9SuppDocHasDifference(false, bean.getPoAppl(),
						bean.getSmAppl())) {
					bean.getEditVO().setAppEditSuppDoc(true);
				}
			}
		}
	}

	public static void setAllSectionEditorVO(SmApplicationManagedBean bean,
			boolean setAll) {
		if (setAll) {
			setSectionEditorVO(bean,
					SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC);
			setSectionEditorVO(bean,
					SupplierManagementConst.SM_APPL_PAGE_TYPE_BRANCH);
			setSectionEditorVO(bean,
					SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL);
			setSectionEditorVO(bean,
					SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY);
			setSectionEditorVO(bean,
					SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY);
			setSectionEditorVO(bean,
					SupplierManagementConst.SM_APPL_PAGE_TYPE_BANK);
			setSectionEditorVO(bean,
					SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY);
			setSectionEditorVO(bean,
					SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUIP);
			setSectionEditorVO(bean,
					SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS);
		} else {
			setSectionEditorVO(bean, bean.getCurrentPageType());
		}
	}

	public static String getSideMenuVerificationIcon(
			SmApplicationManagedBean bean, String sectionType) {
		String retVal = StringUtils.EMPTY;
		if (!bean.isSupplierUser()) {
			retVal = ICON_UNTICK;
			SmApplSectionReview rev = bean.isPoUser() ? bean.getPoSecReview()
					: bean.getApprvrSecReview();
			if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)
					&& rev.isBasicVerified()
					&& rev.isStaffVerified()
					&& rev.isOfficeFacilityVerfied() && rev.isCertVerified()) {
				if (bean.getSmAppl().getSupplierType().equals("J")) {
					if (rev.isAssocBodyVerified()) {
						retVal = ICON_TICK;
					}
				} else {
					if (rev.isTrnsNetworkVerified()) {
						retVal = ICON_TICK;
					}
				}
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BRANCH)
					&& rev.isAssociateVerified() && rev.isExperienceVerified()) {
				retVal = ICON_TICK;
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)
					&& rev.isPersonnelVerified() && rev.isBranchVerified()) {
				retVal = ICON_TICK;
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
				if (bean.getBusinessTypeVal()
						.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT)) {
					if (rev.isCaptialVerified()) {
						retVal = ICON_TICK;
					}
				} else {
					if (rev.isCaptialVerified() && rev.isShareholderVerifed()) {
						if (bean.getBusinessTypeVal()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)
								|| bean.getBusinessTypeVal()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)) {
							if (rev.isEquityCompVerifed()) {
								retVal = ICON_TICK;
							}
						} else {
							retVal = ICON_TICK;
						}
					}
				}
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BANK)
					&& rev.isBankVerified()) {
				retVal = ICON_TICK;
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
				if (bean.getSmAppl().getApplType()
						.equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)
						|| bean.getSmAppl().getApplType()
								.equals(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)) {
					retVal = ICON_TICK;
				} else {
					boolean isAllCatcodeVerified = true;
					if (!EntityHelper.isCollectionEmpty(bean.getSmAppl()
							.getSmSupplierCategories())) {
						for (SmSupplierCategory cat : bean.getSmAppl()
								.getSmSupplierCategories()) {
							if (bean.isPoUser()
									&& cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS
									&& cat.getPoDecFlg() == null) {
								isAllCatcodeVerified = false;
							} else if (bean.isApproverUser()
									&& cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS
									&& cat.getApprvrDecFlg() == null) {
								isAllCatcodeVerified = false;
							}
						}
						if (isAllCatcodeVerified) {
							retVal = ICON_TICK;
						}
					}
				}
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY)
					&& rev.isFactoryVerify()) {
				retVal = ICON_TICK;
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUIP)
					&& rev.isSupplierSystemVerified()
					&& rev.isMachineryVerified()) {
				retVal = ICON_TICK;
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS)
					&& SmApplicationUtil.isAllSuppDocAlreadyVerified(bean
							.getMandatoryDocs(), bean.getSmAppl()
							.getSupportingDocMode(),bean.isApproverUser())
					&& SmApplicationUtil.isAllSuppDocAlreadyVerified(bean
							.getNonMandatoryDocs(), bean.getSmAppl()
							.getSupportingDocMode(),bean.isApproverUser())) {
				retVal = ICON_TICK;
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_SITEVISITREV)
					&& rev.isSitevisitVerified()) {
				retVal = ICON_TICK;
			}
		}
		return retVal;
	}

	public static void setPrevApplByRole(SmApplicationManagedBean bean) {
		if (bean.isPoUser()) {
			bean.setSupplierAppl(getSmApplFromSerFile(bean.getSmAppl()
					.getApplNo(), "SUP"));
		} else if (bean.isApproverUser()) {
			bean.setSupplierAppl(getSmApplFromSerFile(bean.getSmAppl()
					.getApplNo(), "SUP"));
			bean.setPoAppl(getSmApplFromSerFile(bean.getSmAppl().getApplNo(),
					"PO"));
		}
	}

	private static boolean step1CompanyBasicHasDifference(SmAppl applPrev,
			SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			// companyBasic
			retVal = EntityHelper.isObjectDifference(
					applPrev.getSmCompanyBasic(), applCurr.getSmCompanyBasic(),
					new String[] { "paidUpCapital", "authorizedCapital",
							"totalShare", "totalMember", "bumiEquity",
							"nonBumiEquity", "foreignerEquity",
							"govtUnitrustEquity", "govtEquity",
							"foreignCompanyEquity", "localCompanyEquity",
							"bumiPercentage", "nonBumiPercentage" });
			if (!retVal) {
				if (applPrev.getSmCompanyBasic() != null
						&& applCurr.getSmCompanyBasic() != null) {
					if (!EntityHelper.isCollectionEmpty(applPrev
							.getSmCompanyBasic().getSmCompanyAddresses())
							&& !EntityHelper.isCollectionEmpty(applCurr
									.getSmCompanyBasic()
									.getSmCompanyAddresses())) {

						// companyBasic address
						retVal = EntityHelper.isObjectDifference(applPrev
								.getSmCompanyBasic().getSmCompanyAddresses()
								.get(0), applCurr.getSmCompanyBasic()
								.getSmCompanyAddresses().get(0),
								new String[] { "smCompanyBasic" });
					}
				}
			}

			if (!retVal) {
				// staff total
				retVal = EntityHelper.hasDiffBetweenList(
						applPrev.getSmStaffTotals(),
						applCurr.getSmStaffTotals(),
						new String[] { "staffCategory" }, null, null);
			}

			if (!retVal) {
				// HQ
				SmFacility hqCurr = null;
				for (SmFacility hqPrev : applPrev.getSmFacilities()) {
					if ((hqPrev
							.getFacilityType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE) || hqPrev
							.getFacilityType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE))
							&& hqPrev.getRecordStatusFlg() != DbValueConst.RECORD_STATUS_DELETED) {
						hqCurr = SmApplicationUtil
								.getHqFacilityByAppl(applCurr);
						retVal = EntityHelper.isObjectDifference(hqPrev,
								hqCurr, null);
						break;
					}
				}
			}

			if (!retVal) {
				// Certificate
				retVal = EntityHelper.hasDiffBetweenList(
						applPrev.getSmSupplierCerts(),
						applCurr.getSmSupplierCerts(), new String[] {
								"certTypeId", "certName" },
						new String[] { "pmCertBody" }, null);

			}

			if (!retVal
					&& !EntityHelper.isCollectionEmpty(applPrev
							.getSmAssociationBodies())) {
				// Professional Body
				SmAssociationBody bodyCurr = null;
				for (SmAssociationBody bodyPrev : applPrev
						.getSmAssociationBodies()) {
					if (bodyPrev.getSmPersonnel() == null) {
						bodyCurr = (SmAssociationBody) EntityHelper
								.getEntityByMultiplePropertiesFromList(
										bodyPrev,
										applCurr.getSmAssociationBodies(),
										new String[] { "expDate", "regNo" },
										new String[] {}, null);
						retVal = EntityHelper.isObjectDifference(bodyPrev,
								bodyCurr, new String[] { "smPersonnel" });
						if (retVal) {
							break;
						}
					}
				}
			}

		}
		return retVal;
	}

	private static boolean step2AssocCompProjExpHasDifference(
			boolean isSupplier, SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			String[] searchParam = null;

			// associate company
			if (isSupplier) {
				searchParam = new String[] { "regNo" };
			} else {
				searchParam = new String[] { "associateCompanyId" };
			}
			retVal = EntityHelper
					.hasDiffBetweenList(applPrev.getSmAssociateCompanies(),
							applCurr.getSmAssociateCompanies(), searchParam,
							null, null);

			// company experience
			if (!retVal) {
				if (isSupplier) {
					searchParam = new String[] { "clientName", "title" };
				} else {
					searchParam = new String[] { "companyExperienceId" };
				}
				retVal = EntityHelper.hasDiffBetweenList(
						applPrev.getSmCompanyExperiences(),
						applCurr.getSmCompanyExperiences(), searchParam, null,
						null);

				if (!retVal) {
					SmCompanyExperience prevExp = null;
					if (!EntityHelper.isCollectionEmpty(applPrev
							.getSmCompanyExperiences())
							&& !EntityHelper.isCollectionEmpty(applCurr
									.getSmCompanyExperiences())) {
						for (SmCompanyExperience currExp : applCurr
								.getSmCompanyExperiences()) {
							prevExp = (SmCompanyExperience) EntityHelper
									.getEntityByMultiplePropertiesFromList(
											currExp,
											applPrev.getSmCompanyExperiences(),
											searchParam, null, null);
							retVal = EntityHelper.isObjectDifference(prevExp,
									currExp, null);
							if (retVal) {
								break;
							}
							retVal = EntityHelper.hasDiffBetweenList(
									prevExp.getSmCompanyExperienceCats(),
									currExp.getSmCompanyExperienceCats(), null,
									new String[] { "pmCategoryL3" },
									new String[] { "smCompanyExperience" });
						}
					}
				}
			}
		}
		return retVal;
	}

	private static boolean step3PersonnelHasDifference(boolean isSupplier,
			SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;
		String[] excludeProps = new String[] { "smPersonnel" };
		String[] primitive = null;
		String[] nonPrimitive = null;

		if (applPrev != null && applCurr != null) {

			// smPersonnel
			if (!EntityHelper.isCollectionEmpty(applPrev.getSmPersonnels())
					&& !EntityHelper.isCollectionEmpty(applCurr
							.getSmPersonnels())) {

				SmPersonnel prevPersonnel = null;
				for (SmPersonnel currPersonnel : applCurr.getSmPersonnels()) {

					// smPersonnel
					if (isSupplier) {
						primitive = new String[] { "identificationNo" };
					} else {
						primitive = new String[] { "personnelId" };
					}

					prevPersonnel = (SmPersonnel) EntityHelper
							.getEntityByMultiplePropertiesFromList(
									currPersonnel, applPrev.getSmPersonnels(),
									primitive, null, null);
					retVal = EntityHelper.isObjectDifference(prevPersonnel,
							currPersonnel, new String[] { "shareAmt",
									"sharePercentage" });
					if (retVal) {
						break;
					}

					// smPersonnel address
					if (isSupplier) {
						primitive = new String[] { "addressType" };
					} else {
						primitive = new String[] { "personnelAddressId" };
					}
					retVal = EntityHelper.hasDiffBetweenList(
							prevPersonnel.getSmPersonnelAddresses(),
							currPersonnel.getSmPersonnelAddresses(), primitive,
							null, excludeProps);

					if (retVal) {
						break;
					}

					// smStaffExperience
					if (isSupplier) {
						primitive = new String[] { "companyName", "fromYear",
								"toYear" };
					} else {
						primitive = new String[] { "staffExperienceId" };
					}
					retVal = EntityHelper.hasDiffBetweenList(
							prevPersonnel.getSmStaffExperiences(),
							currPersonnel.getSmStaffExperiences(), primitive,
							null, excludeProps);

					if (retVal) {
						break;
					}

					// smRelatedCompany
					if (isSupplier) {
						primitive = new String[] { "regNo" };
					} else {
						primitive = new String[] { "relatedCompanyId" };
					}
					retVal = EntityHelper.hasDiffBetweenList(
							prevPersonnel.getSmRelatedCompanies(),
							currPersonnel.getSmRelatedCompanies(), primitive,
							null, excludeProps);

					if (retVal) {
						break;
					}

					// smAssociationBody
					if (isSupplier) {
						primitive = new String[] { "regNo", "expDate" };
						nonPrimitive = new String[] { "pmCertBody" };
					} else {
						primitive = new String[] { "associationBodyId" };
					}
					retVal = EntityHelper.hasDiffBetweenList(
							prevPersonnel.getSmAssociationBodies(),
							currPersonnel.getSmAssociationBodies(), primitive,
							nonPrimitive, excludeProps);

					if (retVal) {
						break;
					}

					// smStaffAcademics
					if (isSupplier) {
						nonPrimitive = new String[] { "pmEduField",
								"pmEduLevel" };
					} else {
						primitive = new String[] { "staffAcademicId" };
					}
					retVal = EntityHelper.hasDiffBetweenList(
							prevPersonnel.getSmStaffAcademics(),
							currPersonnel.getSmStaffAcademics(), null,
							nonPrimitive, excludeProps);

					if (retVal) {
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
				retVal = EntityHelper.hasDiffBetweenList(
						applPrev.getSmSupplierBranches(),
						applCurr.getSmSupplierBranches(), primitive, null,
						new String[] { "contactPersonId" });
			}
		}

		return retVal;
	}

	private static boolean step4EquityHasDifference(boolean isSupplier,
			SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			String[] searchParam = null;

			// company equity status
			if (applPrev.getSmCompanyBasic() != null
					&& applCurr.getSmCompanyBasic() != null) {

				SmCompanyBasic prev = applPrev.getSmCompanyBasic();
				SmCompanyBasic curr = applCurr.getSmCompanyBasic();

				if (EntityHelper.isBigDecimalValuesDifference(
						curr.getPaidUpCapital(), prev.getPaidUpCapital())) {
					retVal = true;
				} else if (!retVal
						&& EntityHelper.isBigDecimalValuesDifference(
								curr.getAuthorizedCapital(),
								prev.getAuthorizedCapital())) {
					retVal = true;
				} else if (!retVal
						&& EntityHelper.isBigDecimalValuesDifference(
								curr.getTotalShare(), prev.getTotalShare())) {
					retVal = true;
				} else if (!retVal
						&& EntityHelper.isBigDecimalValuesDifference(
								curr.getTotalMember(), prev.getTotalMember())) {
					retVal = true;
				} else if (!retVal
						&& EntityHelper.isBigDecimalValuesDifference(
								curr.getBumiEquity(), prev.getBumiEquity())) {
					retVal = true;
				} else if (!retVal
						&& EntityHelper.isBigDecimalValuesDifference(
								curr.getNonBumiEquity(),
								prev.getNonBumiEquity())) {
					retVal = true;
				} else if (!retVal
						&& EntityHelper.isBigDecimalValuesDifference(
								curr.getForeignerEquity(),
								prev.getForeignerEquity())) {
					retVal = true;
				} else if (!retVal
						&& EntityHelper.isBigDecimalValuesDifference(
								curr.getGovtUnitrustEquity(),
								prev.getGovtUnitrustEquity())) {
					retVal = true;
				} else if (!retVal
						&& EntityHelper.isBigDecimalValuesDifference(
								curr.getGovtEquity(), prev.getGovtEquity())) {
					retVal = true;
				} else if (!retVal
						&& EntityHelper.isBigDecimalValuesDifference(
								curr.getForeignCompanyEquity(),
								prev.getForeignCompanyEquity())) {
					retVal = true;
				} else if (!retVal
						&& EntityHelper.isBigDecimalValuesDifference(
								curr.getLocalCompanyEquity(),
								prev.getLocalCompanyEquity())) {
					retVal = true;
				}
			}

			// personnel shareholder
			if (!retVal
					&& !EntityHelper.isCollectionEmpty(applPrev
							.getSmPersonnels())
					&& !EntityHelper.isCollectionEmpty(applCurr
							.getSmPersonnels())) {
				if (isSupplier) {
					searchParam = new String[] { "identificationNo" };
				} else {
					searchParam = new String[] { "personnelId" };
				}
				SmPersonnel prevShareholder = null;
				for (SmPersonnel currShareholder : applCurr.getSmPersonnels()) {
					prevShareholder = (SmPersonnel) EntityHelper
							.getEntityBySinglePropertyFromList(
									currShareholder.getIdentificationNo(),
									applPrev.getSmPersonnels(), searchParam[0]);

					if (prevShareholder != null
							&& EntityHelper.isBigDecimalValuesDifference(
									prevShareholder.getShareAmt(),
									currShareholder.getShareAmt())) {
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
				retVal = EntityHelper.hasDiffBetweenList(
						applPrev.getSmEquityCompanies(),
						applCurr.getSmEquityCompanies(), searchParam, null,
						null);
			}
		}

		return retVal;
	}

	private static boolean step5BankHasDifference(boolean isSupplier,
			SmAppl applPrev, SmAppl applCurr) {
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
				retVal = EntityHelper.hasDiffBetweenList(
						applPrev.getSmSupplierBanks(),
						applCurr.getSmSupplierBanks(), searchParam, null, null);

				if (!retVal) {
					SmSupplierBank prevBank = null;
					if (!EntityHelper.isCollectionEmpty(applPrev
							.getSmSupplierBanks())
							&& !EntityHelper.isCollectionEmpty(applCurr
									.getSmSupplierBanks())) {
						for (SmSupplierBank currBank : applCurr
								.getSmSupplierBanks()) {
							prevBank = (SmSupplierBank) EntityHelper
									.getEntityByMultiplePropertiesFromList(
											currBank,
											applPrev.getSmSupplierBanks(),
											searchParam, null, null);
							retVal = EntityHelper.isObjectDifference(prevBank,
									currBank, null);
							if (retVal) {
								break;
							}

							// smSupplierBranchBanks
							retVal = isSupplierBranchBankHasDifference(
									prevBank.getSmSupplierBranchBanks(),
									currBank.getSmSupplierBranchBanks());

							// smChequeSignatories
							if (!retVal) {
								retVal = isChequeSignatoriesHasDifference(
										prevBank.getSmChequeSignatories(),
										currBank.getSmChequeSignatories());
							}
						}
					}
				}
			}
		}
		return retVal;
	}

	private static boolean step6CatCodeHasDifference(boolean isSupplier,
			SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {

			// smSupplierCategory
			if (isSupplier) {
				retVal = EntityHelper.hasDiffBetweenList(
						applPrev.getSmSupplierCategories(),
						applCurr.getSmSupplierCategories(), null,
						new String[] { "pmCategoryL3" }, null);
			} else {
				retVal = EntityHelper.hasDiffBetweenList(
						applPrev.getSmSupplierCategories(),
						applCurr.getSmSupplierCategories(),
						new String[] { "supplierCategoryId" }, null,
						new String[] { "prevApprDecFlg", "apprvrDecFlg",
								"poDecFlg", "approvedByPo", "prevApprovedByPo",
								"prevPoDecFlg", "poRemarks", "approverRemarks",
								"prevPoRemarks", "prevApproverRemarks",
								"approvedDate" });
			}
		}
		return retVal;
	}

	private static boolean step7FacilityHasDifference(boolean isSupplier,
			SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			String[] searchParam = null;

			List<SmFacility> prevFacList = new ArrayList<SmFacility>();
			if (!EntityHelper.isCollectionEmpty(applPrev.getSmFacilities())) {
				for (SmFacility prevFac : applPrev.getSmFacilities()) {
					if (!prevFac
							.getFacilityType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)
							&& !prevFac
									.getFacilityType()
									.equalsIgnoreCase(
											SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {
						prevFacList.add(prevFac);
					}
				}
			}

			List<SmFacility> currFacList = new ArrayList<SmFacility>();
			if (!EntityHelper.isCollectionEmpty(applCurr.getSmFacilities())) {
				for (SmFacility currFac : applCurr.getSmFacilities()) {
					if (!currFac
							.getFacilityType()
							.equalsIgnoreCase(
									SupplierManagementConst.SM_FACILITY_TYPE_HOMEOFFICE)
							&& !currFac
									.getFacilityType()
									.equalsIgnoreCase(
											SupplierManagementConst.SM_FACILITY_TYPE_BUSSINESSOFFICE)) {
						currFacList.add(currFac);
					}
				}
			}

			if (isSupplier) {
				searchParam = new String[] { "facilityType" };
			} else {
				searchParam = new String[] { "facilityId" };
			}
			retVal = EntityHelper.hasDiffBetweenList(prevFacList, currFacList,
					searchParam, null, null);
		}

		return retVal;
	}

	private static boolean step8EquipmentHasDifference(boolean isSupplier,
			SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			// smMachineDetails
			if (isSupplier) {
				retVal = EntityHelper.hasDiffBetweenList(
						applPrev.getSmMachineDtls(),
						applCurr.getSmMachineDtls(), new String[] {
								"brandName", "machineFunction" },
						new String[] { "categoryType" }, null);
			} else {
				retVal = EntityHelper.hasDiffBetweenList(
						applPrev.getSmMachineDtls(),
						applCurr.getSmMachineDtls(),
						new String[] { "machineDtlId" }, null, null);
			}

			// Traveling software
			if (!retVal) {
				if (isSupplier) {
					retVal = EntityHelper.hasDiffBetweenList(
							applPrev.getSmSupplierSystems(),
							applCurr.getSmSupplierSystems(), null,
							new String[] { "system" }, null);
					if (!retVal) {
						retVal = EntityHelper.hasDiffBetweenList(
								applPrev.getSmSupplierSystems(),
								applCurr.getSmSupplierSystems(),
								new String[] { "systemName" }, null, null);
					}
				} else {
					retVal = EntityHelper.hasDiffBetweenList(
							applPrev.getSmSupplierSystems(),
							applCurr.getSmSupplierSystems(),
							new String[] { "supplierSystemId" }, null, null);
				}
			}
		}

		return retVal;
	}

	private static boolean step9SuppDocHasDifference(boolean isSupplier,
			SmAppl applPrev, SmAppl applCurr) {
		boolean retVal = false;

		if (applPrev != null && applCurr != null) {
			// only do for po and approver (check if got change in doc files
			// attachment only)
			if (!isSupplier) {
				SmApplSupportDoc prevDoc = null;
				if (!EntityHelper.isCollectionEmpty(applPrev
						.getSmApplSupportDocs())
						&& !EntityHelper.isCollectionEmpty(applCurr
								.getSmApplSupportDocs())) {
					for (SmApplSupportDoc currDoc : applCurr
							.getSmApplSupportDocs()) {
						prevDoc = (SmApplSupportDoc) EntityHelper
								.getEntityByMultiplePropertiesFromList(currDoc,
										applPrev.getSmApplSupportDocs(),
										new String[] { "applSupportDocId" },
										null, null);

						if (prevDoc == null) {
							retVal = true;
							break;
						}

						// docfiles
						retVal = EntityHelper.hasDiffBetweenList(
								prevDoc.getSmApplDocFiles(),
								currDoc.getSmApplDocFiles(),
								new String[] { "applDocFileId" }, null, null);
					}
				}
			}
		}

		return retVal;
	}

	private static SmAppl getSmApplFromSerFile(String applNo, String serType) {
		SmAppl retVal = null;
		try {
			String fileName = SmCommonUtil.getSerializedFileFullDirByApplNo(
					applNo, serType);
			if (!StringUtils.isBlank(fileName)) {
				SmApplRevisionReqDTO reqDTO = new SmApplRevisionReqDTO();
				reqDTO.setDeSerFileName(fileName);
				SmApplRevisionResDTO resDTO = SmServiceLookup
						.getSmApplRevisionSO().readApplicationHist(reqDTO);
				if (resDTO.getSmAppl() != null) {
					retVal = resDTO.getSmAppl();
				}
			}
		} catch (Exception ex) {

		}
		return retVal;
	}

	private static boolean isSupplierBranchBankHasDifference(
			List<SmSupplierBranchBank> prevBranchBanks,
			List<SmSupplierBranchBank> currBranchBanks) {
		boolean retVal = false;

		if (EntityHelper.isCollectionEmpty(prevBranchBanks)
				&& !EntityHelper.isCollectionEmpty(currBranchBanks)) {
			retVal = true;
		} else if (!retVal && !EntityHelper.isCollectionEmpty(prevBranchBanks)
				&& EntityHelper.isCollectionEmpty(currBranchBanks)) {
			retVal = true;
		} else if (!retVal && !EntityHelper.isCollectionEmpty(prevBranchBanks)
				&& !EntityHelper.isCollectionEmpty(currBranchBanks)) {

			// check if current exist in previous
			for (SmSupplierBranchBank currBrBank : currBranchBanks) {
				if (getSmSupplierBranchBank(prevBranchBanks,
						currBrBank.getSmSupplierBranch()) == null) {
					retVal = true;
					break;
				}
			}
			// check if previous exist in current
			for (SmSupplierBranchBank prevBrBank : prevBranchBanks) {
				if (getSmSupplierBranchBank(currBranchBanks,
						prevBrBank.getSmSupplierBranch()) == null) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}

	private static boolean isChequeSignatoriesHasDifference(
			List<SmChequeSignatory> prevChequeSignatories,
			List<SmChequeSignatory> currChequeSignatories) {
		boolean retVal = false;

		if (EntityHelper.isCollectionEmpty(prevChequeSignatories)
				&& !EntityHelper.isCollectionEmpty(currChequeSignatories)) {
			retVal = true;
		} else if (!retVal
				&& !EntityHelper.isCollectionEmpty(prevChequeSignatories)
				&& EntityHelper.isCollectionEmpty(currChequeSignatories)) {
			retVal = true;
		} else if (!retVal
				&& !EntityHelper.isCollectionEmpty(prevChequeSignatories)
				&& !EntityHelper.isCollectionEmpty(currChequeSignatories)) {

			// check if current exist in previous
			for (SmChequeSignatory currChq : currChequeSignatories) {
				if (getSmChequeSignatory(prevChequeSignatories,
						currChq.getSmPersonnel()) == null) {
					retVal = true;
					break;
				}
			}
			// check if previous exist in current
			for (SmChequeSignatory prevChq : prevChequeSignatories) {
				if (getSmChequeSignatory(currChequeSignatories,
						prevChq.getSmPersonnel()) == null) {
					retVal = true;
					break;
				}
			}
		}
		return retVal;
	}

	private static SmChequeSignatory getSmChequeSignatory(
			List<SmChequeSignatory> chequeSignatories, SmPersonnel personnel) {
		SmChequeSignatory retVal = null;
		for (SmChequeSignatory chqTmp : chequeSignatories) {
			if (chqTmp.getSmPersonnel() != null
					&& personnel != null
					&& chqTmp.getSmPersonnel().getIdentificationNo()
							.equals(personnel.getIdentificationNo())) {
				retVal = chqTmp;
				break;
			}
		}
		return retVal;
	}

	private static SmSupplierBranchBank getSmSupplierBranchBank(
			List<SmSupplierBranchBank> branchBanks, SmSupplierBranch branch) {
		SmSupplierBranchBank retVal = null;
		for (SmSupplierBranchBank brBankTmp : branchBanks) {
			if (brBankTmp.getSmSupplierBranch() != null
					&& branch != null
					&& brBankTmp.getSmSupplierBranch().getBranchCode()
							.equals(branch.getBranchCode())) {
				retVal = brBankTmp;
				break;
			}
		}
		return retVal;
	}
}
