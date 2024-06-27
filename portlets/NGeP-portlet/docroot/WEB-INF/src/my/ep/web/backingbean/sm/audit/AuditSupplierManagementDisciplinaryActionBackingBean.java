package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.CommonUtils;

/**
 * @author Firdaus Omar
 * @since 25.06.2024 #28895_35501
 */

public class AuditSupplierManagementDisciplinaryActionBackingBean extends
		AuditSupplierManagementBackingBeanTemplate {

	private static final long serialVersionUID = 1L;

	/**
	 * @param managedBean
	 * @param org
	 * @param user
	 * @param roleCode
	 *            prepare audit trail data
	 */
	@Override
	public AuditSupplierManagementDTO prepareAuditData(BaseManagedBean managedBean, String roleCode, PmOrgProfile org) {

		AuditSupplierManagementDTO auObj = new AuditSupplierManagementDTO();
		ViewSuppProfileManagedBean mBean = (ViewSuppProfileManagedBean) managedBean;
		List<AuditSupplierManagementDetailDTO> auDetails = new ArrayList<>();

		SmDisciplinaryAction oldDisciplinaryAction = mBean.getOldSmDisciplinaryAction();
		SmDisciplinaryAction newDisciplinaryAction = mBean.getDisciplineVO().getSmDiscplinaryAction();

		Locale locale = new Locale("ms");
		
		if(oldDisciplinaryAction == null){
			oldDisciplinaryAction = new SmDisciplinaryAction();
		}

		if (!oldDisciplinaryAction.isWarning() && newDisciplinaryAction.isWarning()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null, ResourceBundleUtils
							.getMessage("lbl.sm.disciplinary.jktt.warning", ResourceBundleUtils.BUNDLE_COMMON_LABELS,
									locale), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE));
		}
		if (oldDisciplinaryAction.isCancelCategory() != newDisciplinaryAction.isCancelCategory()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null, ResourceBundleUtils
							.getMessage("lbl.sm.disciplinary.jktt.cancel.cat.code",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE));
		}
		if (oldDisciplinaryAction.isSuspendRegistration() != newDisciplinaryAction.isSuspendCategory()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null, ResourceBundleUtils
							.getMessage("lbl.sm.disciplinary.jktt.suspend.registration",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE));
		}
		if (oldDisciplinaryAction.isCancelRegistration() != newDisciplinaryAction.isCancelRegistration()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null, ResourceBundleUtils
							.getMessage("lbl.sm.disciplinary.jktt.cancel.registration",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE));
		}
		if (oldDisciplinaryAction.isBlacklistOwner() != newDisciplinaryAction.isBlacklistOwner()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null, ResourceBundleUtils
							.getMessage("lbl.sm.disciplinary.jktt.blacklist.owner",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE));
		}
		if (oldDisciplinaryAction.isBumiputeraCancel() != newDisciplinaryAction.isBumiputeraCancel()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null, ResourceBundleUtils
							.getMessage("lbl.sm.disciplinary.jktt.cancel.of.bumiputera",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE));
		}
		if (oldDisciplinaryAction.isManufacturerCancel() != newDisciplinaryAction.isManufacturerCancel()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null, ResourceBundleUtils
							.getMessage("lbl.sm.disciplinary.jktt.cancel.of.manufacturer",
									ResourceBundleUtils.BUNDLE_COMMON_LABELS, locale), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE));
		}

		CommonUtils commonUtils = new CommonUtils();

		if (oldDisciplinaryAction.getStartDate() != null) {
			if (!oldDisciplinaryAction.getStartDate().equals(newDisciplinaryAction.getStartDate())) {
				auDetails.add(createAuditData(
						AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
						AuditSupplierManagementConst.AU_FIELD_LABEL_ID_START_DATE,
						commonUtils.getFormattedDate(oldDisciplinaryAction.getStartDate(),
								commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE)),
						commonUtils.getFormattedDate(newDisciplinaryAction.getStartDate(),
								commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE)), null,
						null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_START_DATE));
			}
		} else if (newDisciplinaryAction.getStartDate() != null) {
			auDetails.add(createAuditData(
					AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_START_DATE,
					null,
					commonUtils.getFormattedDate(newDisciplinaryAction.getStartDate(),
							commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE)), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_START_DATE));
		}

		if (oldDisciplinaryAction.getEndDate() != null) {
			if (!oldDisciplinaryAction.getEndDate().equals(newDisciplinaryAction.getEndDate())) {
				auDetails.add(createAuditData(
						AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
						AuditSupplierManagementConst.AU_FIELD_LABEL_ID_END_DATE,
						commonUtils.getFormattedDate(oldDisciplinaryAction.getEndDate(),
								commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE)),
						commonUtils.getFormattedDate(newDisciplinaryAction.getEndDate(),
								commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE)), null,
						null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_END_DATE));
			}
		} else if (newDisciplinaryAction.getEndDate() != null) {
			auDetails.add(createAuditData(
					AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_END_DATE,
					null,
					commonUtils.getFormattedDate(newDisciplinaryAction.getEndDate(),
							commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE)), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_END_DATE));
		}

		if (oldDisciplinaryAction.getRemark() != null) {
			if (!oldDisciplinaryAction.getRemark().equals(newDisciplinaryAction.getRemark())) {
				auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
						AuditSupplierManagementConst.AU_FIELD_LABEL_ID_REMARKS, oldDisciplinaryAction.getRemark(),
						newDisciplinaryAction.getRemark(), null, null,
						AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_REMARKS));
			}
		} else if (newDisciplinaryAction.getRemark() != null) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_REMARKS, null, newDisciplinaryAction.getRemark(),
					null, null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_REMARKS));
		}

		auditBaseData(auObj, mBean.getSmSupplier(), roleCode, org, auDetails,
				AuditSupplierManagementConst.AU_GROUP_SECTION_DISCIPLINARY_ACTION);

		return auObj;
	}
}
