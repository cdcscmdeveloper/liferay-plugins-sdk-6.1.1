package my.ep.web.util.sm.helper;

import my.ep.constant.pm.ParameterConst;
import my.ep.helper.EntityHelper;
import my.ep.persistence.entity.sm.SmApplSectionReview;
import my.ep.persistence.entity.sm.SmSupplierCategory;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.sm.SmApplicationManagedBean;

public class SmSideMenuIconUtil {

	public static String getSideMenuVerificationIcon(SmApplicationManagedBean bean,
			String sectionType) {
		String retVal = "ui-icon-white-blank";
		if (!bean.isSupplierUser()) {
			retVal = "ui-icon-untick";
			SmApplSectionReview rev = bean.isPoUser() ? bean.getPoSecReview()
					: bean.getApprvrSecReview();
			if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BASIC)
					&& rev.isBasicVerified()
					&& rev.isStaffVerified()
					&& rev.isOfficeFacilityVerfied() && rev.isCertVerified()) {
				if (bean.getSmAppl().getSupplierType().equals("J")) {
					if (rev.isAssocBodyVerified()) {
						retVal = "ui-icon-tickcheckbox";
					}
				} else {
					if (rev.isTrnsNetworkVerified()) {
						retVal = "ui-icon-tickcheckbox";
					}
				}
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BRANCH)
					&& rev.isAssociateVerified() && rev.isExperienceVerified()) {
				retVal = "ui-icon-tickcheckbox";
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_PERSONNEL)
					&& rev.isPersonnelVerified() && rev.isBranchVerified()) {
				retVal = "ui-icon-tickcheckbox";
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUITY)) {
				if (bean.getBusinessTypeVal()
						.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROS_DEFAULT)) {
					if (rev.isCaptialVerified()) {
						retVal = "ui-icon-tickcheckbox";
					}
				} else {
					if (rev.isCaptialVerified() && rev.isShareholderVerifed()) {
						if (bean.getBusinessTypeVal()
								.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)
								|| bean.getBusinessTypeVal()
										.equals(SupplierManagementConst.SM_SUPPLIER_BUSINESS_TYPE_ROC_DEFAULT)) {
							if (rev.isEquityCompVerifed()) {
								retVal = "ui-icon-tickcheckbox";
							}
						} else {
							retVal = "ui-icon-tickcheckbox";
						}
					}
				}
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_BANK)
					&& rev.isBankVerified()) {
				retVal = "ui-icon-tickcheckbox";
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_CATEGORY)) {
				if (bean.getSmAppl().getApplType()
						.equals(ParameterConst.NEW_SM_APPLN_FOR_BUMI)
						|| bean.getSmAppl().getApplType()
								.equals(ParameterConst.NEW_SM_APPLN_FOR_UPDATE)) {
					retVal = "ui-icon-tickcheckbox";
				} else {
					boolean isAllCatcodeVerified = true;
					if (!EntityHelper.isCollectionEmpty(bean.getSmAppl()
							.getSmSupplierCategories())) {
						for (SmSupplierCategory cat : bean.getSmAppl()
								.getSmSupplierCategories()) {
							if (cat.getRecordStatusFlg() == SupplierManagementConst.PENDING_ACT_STATUS
									&& cat.getPoDecFlg() == null) {
								isAllCatcodeVerified = false;
							}
						}
						if (isAllCatcodeVerified) {
							retVal = "ui-icon-tickcheckbox";
						}
					}
				}
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_FACTORY)
					&& rev.isFactoryVerify()) {
				retVal = "ui-icon-tickcheckbox";
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_EQUIP)
					&& rev.isSupplierSystemVerified()
					&& rev.isMachineryVerified()) {
				retVal = "ui-icon-tickcheckbox";
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_SUPPORTDOCS)
					&& SmApplicationUtil.isAllSuppDocAlreadyVerified(bean
							.getSmAppl())) {
				retVal = "ui-icon-tickcheckbox";
			} else if (sectionType
					.equals(SupplierManagementConst.SM_APPL_PAGE_TYPE_SITEVISITREV)
					&& rev.isSitevisitVerified()) {
				retVal = "ui-icon-tickcheckbox";
			}
		}
		return retVal;
	}
}
