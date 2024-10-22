package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmDocumentRestr;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.common.supportdoc.SupportDocumentManagedBean;

/**
 * AuditPMQTChecklistBackingBean
 * 
 * @author normuliana
 * 
 */
public class AuditPMQTChecklistBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AuditPMSPDTO detailObjDisplayPriority = new AuditPMSPDTO();
	AuditPMSPDTO detailObjDocumentName = new AuditPMSPDTO();
	AuditPMSPDTO detailObjDocumentDefinition = new AuditPMSPDTO();
	AuditPMSPDTO detailObjSupplierAction = new AuditPMSPDTO();
	AuditPMSPDTO detailObjScoringRequired = new AuditPMSPDTO();
	AuditPMSPDTO detailObjStatus = new AuditPMSPDTO();
	boolean updated = false;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleQTChecklist();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean managedBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		SupportDocumentManagedBean managedBeans = (SupportDocumentManagedBean) managedBean;
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		if (null != managedBeans.getResDTO().getDocument().getDocumentId() && null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.isUpdated()) {
			updated = true;
			auditPMReqDTO = updateDetail(managedBeans, auditPMReqDTO, objList, roleCode);
		} else {
			updated = false;
			/*
			 * New created
			 */
			detailObjDisplayPriority.setRoleCode(roleCode);
			detailObjDisplayPriority.setDocType(managedBeans.getResDTO().getDocument().getDocName());
			detailObjDisplayPriority.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjDisplayPriority.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjDisplayPriority.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjDisplayPriority.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
			detailObjDisplayPriority.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
			detailObjDisplayPriority.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjDisplayPriority.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjDisplayPriority.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_DISPLAY_PRIORITY_FIELDLABELID);
			detailObjDisplayPriority.setNewValue(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjDisplayPriority.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjDisplayPriority);
			auditPMReqDTO.setObjList(objList);

			detailObjDocumentName.setRoleCode(roleCode);
			detailObjDocumentName.setDocType(managedBeans.getResDTO().getDocument().getDocName());
			detailObjDocumentName.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjDocumentName.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjDocumentName.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjDocumentName.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
			detailObjDocumentName.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
			detailObjDocumentName.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjDocumentName.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjDocumentName.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_DOCUMENT_NAME_FIELDLABELID);
			detailObjDocumentName.setNewValue(managedBeans.getResDTO().getDocument().getDocName());
			detailObjDocumentName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjDocumentName);
			auditPMReqDTO.setObjList(objList);

			detailObjDocumentDefinition.setRoleCode(roleCode);
			detailObjDocumentDefinition.setDocType(managedBeans.getResDTO().getDocument().getDocName());
			detailObjDocumentDefinition.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjDocumentDefinition.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjDocumentDefinition.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjDocumentDefinition.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
			detailObjDocumentDefinition.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
			detailObjDocumentDefinition.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjDocumentDefinition.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjDocumentDefinition.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_DOCUMENT_DEFINITION_FIELDLABELID);
			detailObjDocumentDefinition.setNewValueId(BigInteger.valueOf(managedBeans.getSelectedDocument()
					.getDocDefinitionTypeCode().getParameterId()));
			detailObjDocumentDefinition.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjDocumentDefinition);
			auditPMReqDTO.setObjList(objList);

			detailObjSupplierAction.setRoleCode(roleCode);
			detailObjSupplierAction.setDocType(managedBeans.getResDTO().getDocument().getDocName());
			detailObjSupplierAction.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjSupplierAction.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjSupplierAction.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjSupplierAction.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
			detailObjSupplierAction.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
			detailObjSupplierAction.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjSupplierAction.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjSupplierAction.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_SUPPLIER_ACTION_FIELDLABELID);
			detailObjSupplierAction.setNewValueId(BigInteger.valueOf(managedBeans.getSelectedDocument().getChecklistActionCode()
					.getParameterId()));
			detailObjSupplierAction.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjSupplierAction);
			auditPMReqDTO.setObjList(objList);

			detailObjStatus.setRoleCode(roleCode);
			detailObjStatus.setDocType(managedBeans.getResDTO().getDocument().getDocName());
			detailObjStatus.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjStatus.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
			detailObjStatus.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
			detailObjStatus.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjStatus.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjStatus.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjStatus
					.setNewValue(managedBeans.getResDTO().getDocument().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjStatus);
			auditPMReqDTO.setObjList(objList);

			detailObjScoringRequired.setRoleCode(roleCode);
			detailObjScoringRequired.setDocType(managedBeans.getResDTO().getDocument().getDocName());
			detailObjScoringRequired.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjScoringRequired.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjScoringRequired.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjScoringRequired.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
			detailObjScoringRequired.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
			detailObjScoringRequired.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjScoringRequired.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjScoringRequired.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_SCORING_REQUIRED_FIELDLABELID);
			detailObjScoringRequired.setNewValue(managedBeans.getResDTO().getDocument().isScoringReq() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			detailObjScoringRequired.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjScoringRequired);
			auditPMReqDTO.setObjList(objList);

			updateList(managedBeans, auditPMReqDTO, objList, roleCode);
		}

		managedBeans.setSelectedDocumentOld(null);
		managedBeans.getResDTO().setUpdatedPmDocumentRestrList(null);
		managedBeans.getResDTO().setDeletedProcurementCatCodeList(null);

		return auditPMReqDTO;
	}

	private AuditPMReqDTO updateDetail(SupportDocumentManagedBean managedBeans, AuditPMReqDTO auditPMReqDTO,
			ArrayList<AuditPMSPDTO> objList, String roleCode) {
		/* Display priority */
		if (managedBeans.getSelectedDocumentOld().getDisplaySeq() != managedBeans.getResDTO().getDocument().getDisplaySeq()) {
			detailObjDisplayPriority.setRoleCode(roleCode);
			detailObjDisplayPriority.setDocType(managedBeans.getResDTO().getDocument().getDocName());
			detailObjDisplayPriority.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjDisplayPriority.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjDisplayPriority.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjDisplayPriority.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
			detailObjDisplayPriority.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
			detailObjDisplayPriority.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjDisplayPriority.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjDisplayPriority.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_DISPLAY_PRIORITY_FIELDLABELID);
			detailObjDisplayPriority.setOldValue(managedBeans.getSelectedDocumentOld().getDisplaySeq());
			detailObjDisplayPriority.setNewValue(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjDisplayPriority.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjDisplayPriority);
			auditPMReqDTO.setObjList(objList);
		}
		/* detailObjStatus */
		if (managedBeans.getSelectedDocumentOld().getRecordStatusFlg() != managedBeans.getResDTO().getDocument()
				.getRecordStatusFlg()) {
			detailObjStatus.setRoleCode(roleCode);
			detailObjStatus.setDocType(managedBeans.getResDTO().getDocument().getDocName());
			detailObjStatus.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjStatus.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
			detailObjStatus.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
			detailObjStatus.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjStatus.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjStatus.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjStatus
					.setOldValue(managedBeans.getSelectedDocumentOld().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
			detailObjStatus
					.setNewValue(managedBeans.getResDTO().getDocument().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjStatus);
			auditPMReqDTO.setObjList(objList);
		}
		/* detailObjScoringRequired */
		if (managedBeans.getSelectedDocumentOld().isScoringReq() != managedBeans.getResDTO().getDocument().isScoringReq()) {
			detailObjScoringRequired.setRoleCode(roleCode);
			detailObjScoringRequired.setDocType(managedBeans.getResDTO().getDocument().getDocName());
			detailObjScoringRequired.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjScoringRequired.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjScoringRequired.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjScoringRequired.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
			detailObjScoringRequired.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
			detailObjScoringRequired.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjScoringRequired.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjScoringRequired.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_SCORING_REQUIRED_FIELDLABELID);
			detailObjScoringRequired.setOldValue(managedBeans.getSelectedDocumentOld().isScoringReq() ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjScoringRequired.setNewValue(managedBeans.getResDTO().getDocument().isScoringReq() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			detailObjScoringRequired.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjScoringRequired);
			auditPMReqDTO.setObjList(objList);
		}

		updateList(managedBeans, auditPMReqDTO, objList, roleCode);

		return auditPMReqDTO;
	}

	private void updateList(SupportDocumentManagedBean managedBeans, AuditPMReqDTO auditPMReqDTO,
			ArrayList<AuditPMSPDTO> objList, String roleCode) {
		if (null != managedBeans.getResDTO().getUpdatedPmDocumentRestrList()
				&& !managedBeans.getResDTO().getUpdatedPmDocumentRestrList().isEmpty()) {
			updatedPmDocumentRestrList(managedBeans, auditPMReqDTO, objList, roleCode);
		}

		if (null != managedBeans.getResDTO().getDeletedProcurementCatCodeList()
				&& !managedBeans.getResDTO().getDeletedProcurementCatCodeList().isEmpty()) {
			deletedProcurementCatCodeList(managedBeans, auditPMReqDTO, objList, roleCode);
		}
		if (managedBeans.isUpdated() && managedBeans.getSelectedDocumentOld() != null
				&& managedBeans.getMandatoryUpdatedPmDocumentRestrList() != null) {
			List<PmDocumentRestr> checklistRestrictionMandatoryNew = getMandatoryNewValue(managedBeans);
			List<PmDocumentRestr> checklistRestrictionMandatoryOld = getMandatoryOldValue(managedBeans);
			if (checklistRestrictionMandatoryNew != null && checklistRestrictionMandatoryOld != null) {
				updateMandatoryList(managedBeans, auditPMReqDTO, objList, roleCode, checklistRestrictionMandatoryNew,
						checklistRestrictionMandatoryOld);
			}
		}

		managedBeans.setSelectedDocumentOld(null);
		managedBeans.getResDTO().setUpdatedPmDocumentRestrList(null);
		managedBeans.getResDTO().setDeletedProcurementCatCodeList(null);
	}

	private AuditPMReqDTO updateMandatoryList(SupportDocumentManagedBean managedBeans, AuditPMReqDTO auditPMReqDTO,
			ArrayList<AuditPMSPDTO> objList, String roleCode, List<PmDocumentRestr> checklistRestrictionMandatoryNew,
			List<PmDocumentRestr> checklistRestrictionMandatoryOld) {
		for (PmDocumentRestr res : checklistRestrictionMandatoryNew) {
			for (PmDocumentRestr oldValue : checklistRestrictionMandatoryOld) {
				if (res.getDocumentRestrId() == oldValue.getDocumentRestrId() && res.isMandatory() != oldValue.isMandatory()) {
					AuditPMSPDTO detailObjMandatory = new AuditPMSPDTO();
					detailObjMandatory.setRoleCode(roleCode);
					detailObjMandatory.setDocType(managedBeans.getResDTO().getDocument().getDocName());
					detailObjMandatory.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
					detailObjMandatory.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
					detailObjMandatory.setLvlCode1(AuditConst.PM_LVL_CODE_1);
					detailObjMandatory.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
					detailObjMandatory.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
					detailObjMandatory.setDocId(BigInteger.valueOf(res.getDocumentRestrId()));
					detailObjMandatory.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
					detailObjMandatory.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_MANDATORY_FIELDLABELID);
					detailObjMandatory.setNewValue(res.isMandatory() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
					detailObjMandatory.setOldValue(oldValue.isMandatory() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
					detailObjMandatory.setActionType(isUpdated(managedBeans.isUpdated()));
					objList.add(detailObjMandatory);
					auditPMReqDTO.setObjList(objList);
				}
			}
		}
		return auditPMReqDTO;
	}

	private AuditPMReqDTO updatedPmDocumentRestrList(SupportDocumentManagedBean managedBeans, AuditPMReqDTO auditPMReqDTO,
			ArrayList<AuditPMSPDTO> objList, String roleCode) {
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getResDTO().getUpdatedPmDocumentRestrList()) {
			if (pmDocumentRestr.getProcurementCatCode().getParameterId() != null) {
				AuditPMSPDTO detailObjProcurementCategory = new AuditPMSPDTO();
				detailObjProcurementCategory.setRoleCode(roleCode);
				detailObjProcurementCategory.setDocType(managedBeans.getResDTO().getDocument().getDocName());
				detailObjProcurementCategory.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
				detailObjProcurementCategory.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
				detailObjProcurementCategory.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				detailObjProcurementCategory.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
				detailObjProcurementCategory.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode()
						.getParameterCode());
				detailObjProcurementCategory.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjProcurementCategory.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument()
						.getDocumentId()));
				detailObjProcurementCategory.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_PROCUREMENT_CATEGORY_FIELDLABELID);
				detailObjProcurementCategory.setNewValueId(BigInteger.valueOf(pmDocumentRestr.getProcurementCatCode()
						.getParameterId()));
				detailObjProcurementCategory.setActionType(isUpdated(managedBeans.isUpdated()));
				objList.add(detailObjProcurementCategory);
				auditPMReqDTO.setObjList(objList);
			}
			if (pmDocumentRestr.isMandatory() || !pmDocumentRestr.isMandatory()) {
				AuditPMSPDTO detailObjMandatory = new AuditPMSPDTO();
				detailObjMandatory.setRoleCode(roleCode);
				detailObjMandatory.setDocType(managedBeans.getResDTO().getDocument().getDocName());
				detailObjMandatory.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
				detailObjMandatory.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
				detailObjMandatory.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				detailObjMandatory.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
				detailObjMandatory.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
				detailObjMandatory.setDocId(BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId()));
				detailObjMandatory.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
				detailObjMandatory.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_MANDATORY_FIELDLABELID);
				detailObjMandatory.setNewValue(pmDocumentRestr.isMandatory() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
				detailObjMandatory.setActionType(isUpdated(managedBeans.isUpdated()));
				objList.add(detailObjMandatory);
				auditPMReqDTO.setObjList(objList);
			}
		}
		return auditPMReqDTO;
	}

	private AuditPMReqDTO deletedProcurementCatCodeList(SupportDocumentManagedBean managedBeans, AuditPMReqDTO auditPMReqDTO,
			ArrayList<AuditPMSPDTO> objList, String roleCode) {
		for (PmDocumentRestr deletepmDocumentRestr : managedBeans.getResDTO().getDeletedProcurementCatCodeList()) {
			if (deletepmDocumentRestr.getProcurementCatCode().getParameterId() != null) {
				AuditPMSPDTO detailObjProcCatDelete = new AuditPMSPDTO();
				detailObjProcCatDelete.setRoleCode(roleCode);
				detailObjProcCatDelete.setDocType(managedBeans.getResDTO().getDocument().getDocName());
				detailObjProcCatDelete.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
				detailObjProcCatDelete.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
				detailObjProcCatDelete.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				detailObjProcCatDelete.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
				detailObjProcCatDelete.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
				detailObjProcCatDelete.setDocId(BigInteger.valueOf(deletepmDocumentRestr.getDocumentRestrId()));
				detailObjProcCatDelete.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
				detailObjProcCatDelete.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_PROCUREMENT_CATEGORY_FIELDLABELID);
				detailObjProcCatDelete.setOldValueId(BigInteger.valueOf(deletepmDocumentRestr.getProcurementCatCode()
						.getParameterId()));
				detailObjProcCatDelete.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);

				objList.add(detailObjProcCatDelete);
				auditPMReqDTO.setObjList(objList);
			}
			if (deletepmDocumentRestr.isMandatory() || !deletepmDocumentRestr.isMandatory()) {
				AuditPMSPDTO detailObjMandatoryDelete = new AuditPMSPDTO();
				detailObjMandatoryDelete.setRoleCode(roleCode);
				detailObjMandatoryDelete.setDocType(managedBeans.getResDTO().getDocument().getDocName());
				detailObjMandatoryDelete.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
				detailObjMandatoryDelete.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
				detailObjMandatoryDelete.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				detailObjMandatoryDelete.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
				detailObjMandatoryDelete.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
				detailObjMandatoryDelete.setDocId(BigInteger.valueOf(deletepmDocumentRestr.getDocumentRestrId()));
				detailObjMandatoryDelete
						.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
				detailObjMandatoryDelete.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_MANDATORY_FIELDLABELID);
				detailObjMandatoryDelete.setOldValue(deletepmDocumentRestr.isMandatory() ? AuditConst.PM_AUD_YES
						: AuditConst.PM_AUD_NO);
				detailObjMandatoryDelete.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(detailObjMandatoryDelete);
				auditPMReqDTO.setObjList(objList);
			}
		}
		return auditPMReqDTO;
	}

	private List<PmDocumentRestr> getMandatoryNewValue(SupportDocumentManagedBean managedBeans) {
		List<PmDocumentRestr> checklistRestrictionMandatoryNew = new ArrayList<>();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getMandatoryUpdatedPmDocumentRestrList()) {
			if (managedBeans.getSelectedDocument().isByProcureCat() && pmDocumentRestr.getProcurementCatCode() != null
					&& DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr.getRecordStatusFlg()) {
				checklistRestrictionMandatoryNew.add(pmDocumentRestr);
			}
		}
		return checklistRestrictionMandatoryNew;
	}

	private List<PmDocumentRestr> getMandatoryOldValue(SupportDocumentManagedBean managedBeans) {
		List<PmDocumentRestr> checklistRestrictionMandatoryOld = new ArrayList<>();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getSelectedDocumentOld().getPmDocumentRestrs()) {
			if (managedBeans.getSelectedDocument().isByProcureCat() && pmDocumentRestr.getProcurementCatCode() != null
					&& DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr.getRecordStatusFlg()) {
				checklistRestrictionMandatoryOld.add(pmDocumentRestr);
			}
		}
		return checklistRestrictionMandatoryOld;
	}

	private String isUpdated(boolean updated) {
		if (updated) {
			return AuditConst.PM_AUD_ACTION_TYPE_UPD;
		} else {
			return AuditConst.PM_AUD_ACTION_TYPE_INS;
		}
	}
}