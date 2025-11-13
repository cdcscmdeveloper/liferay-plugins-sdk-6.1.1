package my.ep.web.backingbean.sm.audit;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.sm.AuditSupplierManagementConst;
import my.ep.constants.CommonConstants;
import my.ep.dto.sm.audit.AuditSupplierManagementDTO;
import my.ep.dto.sm.audit.AuditSupplierManagementDetailDTO;
import my.ep.persistence.entity.pm.authorization.PmOrgProfile;
import my.ep.persistence.entity.sm.SmDisciplinaryAction;
import my.ep.persistence.entity.sm.SmPersonnel;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.applicationView.ViewSuppProfileManagedBean;
import my.ep.web.util.CommonUtils;

/**
 * @author Firdaus Omar
 * @since 25.06.2024 #28895_35501_DA
 */

public class AuditSupplierManagementDisciplinaryActionBackingBean extends AuditSupplierManagementBackingBeanTemplate {

	private static final long serialVersionUID = 1L;

	/**
	 * @param managedBean
	 * @param org
	 * @param user
	 * @param roleCode
	 *            prepare audit trail data
	 */
	@Override
	public AuditSupplierManagementDTO prepareAuditData(BaseManagedBean managedBean, String roleCode, PmOrgProfile org, Long loginUserId) {

		AuditSupplierManagementDTO auObj = new AuditSupplierManagementDTO();
		ViewSuppProfileManagedBean mBean = (ViewSuppProfileManagedBean) managedBean;
		List<AuditSupplierManagementDetailDTO> auDetails = new ArrayList<>();

		SmDisciplinaryAction oldDisciplinaryAction = mBean.getOldSmDisciplinaryAction();
		SmDisciplinaryAction newDisciplinaryAction = mBean.getDisciplineVO().getSmDiscplinaryAction();

		if (oldDisciplinaryAction == null) {
			oldDisciplinaryAction = new SmDisciplinaryAction();
		}

		warning(oldDisciplinaryAction, newDisciplinaryAction, auDetails);

		cancelCategory(oldDisciplinaryAction, newDisciplinaryAction, auDetails);

		suspendRegistration(oldDisciplinaryAction, newDisciplinaryAction, auDetails);

		cancelRegistration(oldDisciplinaryAction, newDisciplinaryAction, auDetails);

		// #28895_35501_DA - Updated FRS
		blackListOwner(oldDisciplinaryAction, newDisciplinaryAction, auDetails);

		bumiCancel(oldDisciplinaryAction, newDisciplinaryAction, auDetails);

		manufacturerCancel(oldDisciplinaryAction, newDisciplinaryAction, auDetails);

		starDate(oldDisciplinaryAction, newDisciplinaryAction, auDetails);

		endDate(oldDisciplinaryAction, newDisciplinaryAction, auDetails);

		remark(oldDisciplinaryAction, newDisciplinaryAction, auDetails);
		// #28895_35501_DA - Updated FRS - Start
		if (this.getManagedBean().isBlacklistOwner()) {
			ownerList(auDetails, mBean);
		}
		// #28895_35501_DA - Updated FRS - End

		auditBaseData(auObj, mBean.getSmSupplier(), roleCode, org, auDetails, AuditSupplierManagementConst.AU_GROUP_SECTION_DISCIPLINARY_ACTION,
				loginUserId);

		return auObj;
	}

	private void warning(SmDisciplinaryAction oldDisciplinaryAction, SmDisciplinaryAction newDisciplinaryAction,
			List<AuditSupplierManagementDetailDTO> auDetails) {
		if (!oldDisciplinaryAction.isWarning() && newDisciplinaryAction.isWarning()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null, getLabel("lbl.sm.disciplinary.jktt.warning"), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE));
		}
	}

	private void cancelCategory(SmDisciplinaryAction oldDisciplinaryAction, SmDisciplinaryAction newDisciplinaryAction,
			List<AuditSupplierManagementDetailDTO> auDetails) {
		if (!oldDisciplinaryAction.isCancelCategory() && newDisciplinaryAction.isCancelCategory()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null, getLabel("lbl.sm.disciplinary.jktt.cancel.cat.code"), null,
					null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE));
		}

	}

	private void suspendRegistration(SmDisciplinaryAction oldDisciplinaryAction, SmDisciplinaryAction newDisciplinaryAction,
			List<AuditSupplierManagementDetailDTO> auDetails) {
		if (!oldDisciplinaryAction.isSuspendRegistration() && newDisciplinaryAction.isSuspendRegistration()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null, getLabel("lbl.sm.disciplinary.jktt.suspend.registration"),
					null, null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE));
		}
	}

	private void cancelRegistration(SmDisciplinaryAction oldDisciplinaryAction, SmDisciplinaryAction newDisciplinaryAction,
			List<AuditSupplierManagementDetailDTO> auDetails) {
		if (!oldDisciplinaryAction.isCancelRegistration() && newDisciplinaryAction.isCancelRegistration()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null, getLabel("lbl.sm.disciplinary.jktt.cancel.registration"),
					null, null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE));
		}
	}

	// #28895_35501_DA - Updated FRS - Start
	private void blackListOwner(SmDisciplinaryAction oldDisciplinaryAction, SmDisciplinaryAction newDisciplinaryAction, 
			List<AuditSupplierManagementDetailDTO> auDetails) {
		if (!oldDisciplinaryAction.isBlacklistOwner() && newDisciplinaryAction.isBlacklistOwner()) { 
			this.getManagedBean().setBlacklistOwner(true);
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD, 
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null, getLabel("lbl.sm.disciplinary.jktt.blacklist.owner"), null, 
					null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE)); 
		} 
	} 
	// #28895_35501_DA - Updated FRS - End

	private void bumiCancel(SmDisciplinaryAction oldDisciplinaryAction, SmDisciplinaryAction newDisciplinaryAction,
			List<AuditSupplierManagementDetailDTO> auDetails) {
		if (!oldDisciplinaryAction.isBumiputeraCancel() && newDisciplinaryAction.isBumiputeraCancel()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null, getLabel("lbl.sm.disciplinary.jktt.cancel.of.bumiputera"),
					null, null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE));
		}
	}

	private void manufacturerCancel(SmDisciplinaryAction oldDisciplinaryAction, SmDisciplinaryAction newDisciplinaryAction,
			List<AuditSupplierManagementDetailDTO> auDetails) {
		if (!oldDisciplinaryAction.isManufacturerCancel() && newDisciplinaryAction.isManufacturerCancel()) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_PUNISHMENT_TYPE, null,
					getLabel("lbl.sm.disciplinary.jktt.cancel.of.manufacturer"), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_PUNISHMENT_TYPE));
		}
	}

	private void starDate(SmDisciplinaryAction oldDisciplinaryAction, SmDisciplinaryAction newDisciplinaryAction,
			List<AuditSupplierManagementDetailDTO> auDetails) {
		CommonUtils commonUtils = new CommonUtils();

		if (oldDisciplinaryAction.getStartDate() != null) {
			if (!oldDisciplinaryAction.getStartDate().equals(newDisciplinaryAction.getStartDate())) {
				auDetails.add(createAuditData(
						AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
						AuditSupplierManagementConst.AU_FIELD_LABEL_ID_START_DATE,
						commonUtils.getFormattedDate(oldDisciplinaryAction.getStartDate(),
								commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE)),
						commonUtils.getFormattedDate(newDisciplinaryAction.getStartDate(),
								commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE)), null, null,
						AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_START_DATE));
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
	}

	private void endDate(SmDisciplinaryAction oldDisciplinaryAction, SmDisciplinaryAction newDisciplinaryAction,
			List<AuditSupplierManagementDetailDTO> auDetails) {
		CommonUtils commonUtils = new CommonUtils();

		if (oldDisciplinaryAction.getEndDate() != null) {
			if (!oldDisciplinaryAction.getEndDate().equals(newDisciplinaryAction.getEndDate())) {
				auDetails.add(createAuditData(
						AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
						AuditSupplierManagementConst.AU_FIELD_LABEL_ID_END_DATE,
						commonUtils.getFormattedDate(oldDisciplinaryAction.getEndDate(),
								commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE)),
						commonUtils.getFormattedDate(newDisciplinaryAction.getEndDate(),
								commonUtils.getDisplayDateTimeFormat(CommonConstants.DATE_FORMAT_TYPE_DATE)), null, null,
						AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_END_DATE));
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
	}

	private void remark(SmDisciplinaryAction oldDisciplinaryAction, SmDisciplinaryAction newDisciplinaryAction,
			List<AuditSupplierManagementDetailDTO> auDetails) {
		if (oldDisciplinaryAction.getRemark() != null) {
			if (!oldDisciplinaryAction.getRemark().equals(newDisciplinaryAction.getRemark())) {
				auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_MODIFY,
						AuditSupplierManagementConst.AU_FIELD_LABEL_ID_REMARKS, oldDisciplinaryAction.getRemark(), newDisciplinaryAction.getRemark(),
						null, null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_REMARKS));
			}
		} else if (newDisciplinaryAction.getRemark() != null) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD, AuditSupplierManagementConst.AU_FIELD_LABEL_ID_REMARKS,
					null, newDisciplinaryAction.getRemark(), null, null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_REMARKS));
		}
	}
	// #28895_35501_DA - Updated FRS - Start
	private void ownerList(List<AuditSupplierManagementDetailDTO> auDetails, ViewSuppProfileManagedBean mBean) {
		List<SmPersonnel> selectedOwnerAndDirectorList = mBean.getSelectedOwnerAndDirectorList();
		for (SmPersonnel owner : selectedOwnerAndDirectorList) {
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD, AuditSupplierManagementConst.AU_FIELD_LABEL_ID_NAME, null,
					owner.getName(), null, null, AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_NAME));
			auDetails.add(createAuditData(AuditSupplierManagementConst.AU_ACTION_TYPE_ADD,
					AuditSupplierManagementConst.AU_FIELD_LABEL_ID_IDENTIFICATION_NO, null, owner.getIdentificationNo(), null, null,
					AuditSupplierManagementConst.AU_FIELD_LABEL_CODE_IDENTIFICATION_NO));
		}
	}
	// #28895_35501_DA - Updated FRS - End
}