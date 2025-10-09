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
	private static final long serialVersionUID = 1L;
	boolean updated = false;
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	String roleCode;
	String docNo;
	BigInteger docId;
	BigInteger docGroupId;
	String lvlCode3;
	String docType;

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
		SupportDocumentManagedBean managedBeans = (SupportDocumentManagedBean) managedBean;
		this.roleCode = roleCode;
		this.docNo = managedBeans.getResDTO().getDocument().getDisplaySeq();
		this.lvlCode3 = managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode();
		this.docType = managedBeans.getResDTO().getDocument().getDocName();

		if (null != managedBeans.getResDTO().getDocument().getDocumentId() && null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.isUpdated()) {
			updated = true;
			updateDetail(managedBeans);
		} else {
			updated = false;
			/*
			 * New created
			 */
			this.docGroupId = BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId());
			this.lvlCode3 = managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode();

			detailObj(AuditConst.PM_AUD_QTCHECKLIST_DISPLAY_PRIORITY_FIELDLABELID, null, managedBeans.getResDTO().getDocument()
					.getDisplaySeq(), AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_QTCHECKLIST_DOCUMENT_NAME_FIELDLABELID, null, managedBeans.getResDTO().getDocument()
					.getDocName(), AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_QTCHECKLIST_DOCUMENT_DEFINITION_FIELDLABELID, null, managedBeans.getSelectedDocument()
					.getDocDefinitionTypeCode().getPmParameterDescsSpecCallOnly().get(1).getCodeName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_QTCHECKLIST_SUPPLIER_ACTION_FIELDLABELID, null, managedBeans.getSelectedDocument()
					.getChecklistActionCode().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID, null, managedBeans.getResDTO().getDocument()
					.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_QTCHECKLIST_SCORING_REQUIRED_FIELDLABELID, null, managedBeans.getResDTO().getDocument()
					.isScoringReq() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO, AuditConst.PM_AUD_ACTION_TYPE_INS);

			updateList(managedBeans);
		}

		managedBeans.setSelectedDocumentOld(null);
		managedBeans.getResDTO().setUpdatedPmDocumentRestrList(null);
		managedBeans.getResDTO().setDeletedProcurementCatCodeList(null);

		managedBeans.setMandatoryUpdatedPmDocumentRestrList(null);

		auditPMReqDTO.setObjList(objList);
		return auditPMReqDTO;
	}

	private void updateDetail(SupportDocumentManagedBean managedBeans) {
		/* Display priority */
		if (!managedBeans.getSelectedDocumentOld().getDisplaySeq().trim().equals(managedBeans.getResDTO().getDocument().getDisplaySeq().trim())) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId());
			this.lvlCode3 = managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode();

			detailObj(AuditConst.PM_AUD_QTCHECKLIST_DISPLAY_PRIORITY_FIELDLABELID, managedBeans.getSelectedDocumentOld()
					.getDisplaySeq(), managedBeans.getResDTO().getDocument().getDisplaySeq(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}
		/* detailObjStatus */
		if (managedBeans.getSelectedDocumentOld().getRecordStatusFlg() != managedBeans.getResDTO().getDocument()
				.getRecordStatusFlg()) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId());
			this.lvlCode3 = managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode();

			detailObj(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID, managedBeans.getSelectedDocumentOld()
					.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE, managedBeans.getResDTO()
					.getDocument().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}
		/* detailObjScoringRequired */
		if (managedBeans.getSelectedDocumentOld().isScoringReq() != managedBeans.getResDTO().getDocument().isScoringReq()) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId());
			this.docId = BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId());
			this.lvlCode3 = managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode();

			detailObj(AuditConst.PM_AUD_QTCHECKLIST_SCORING_REQUIRED_FIELDLABELID, managedBeans.getSelectedDocumentOld()
					.isScoringReq() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO, managedBeans.getResDTO()
					.getDocument().isScoringReq() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO,
					AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}

		updateList(managedBeans);
	}

	private void updateList(SupportDocumentManagedBean managedBeans) {
		List<PmDocumentRestr> newItemsBeforeSave = new ArrayList<>();

		// Step 1: Collect items that had no ID before saving
		for (PmDocumentRestr item : managedBeans.getReqDTO().getPmDocument().getPmDocumentRestrs()) {
			if (item.getDocumentRestrId() == null && item.isBrandNew()) {
				newItemsBeforeSave.add(item);
			}
		}

		// Step 2: Match those items in the afterSaveList
		List<PmDocumentRestr> newlyCreatedItems = new ArrayList<>();
		for (PmDocumentRestr savedItem : managedBeans.getResDTO().getDocument().getPmDocumentRestrs()) {
			for (PmDocumentRestr originalItem : newItemsBeforeSave) {
				if (isSameWithoutId(originalItem, savedItem)) {
					newlyCreatedItems.add(savedItem);
					break;
				}
			}
			managedBeans.setUpdatedPmDocumentRestrList(newlyCreatedItems);
		}

		updatedPmDocumentRestrList(managedBeans);

		if (null != managedBeans.getResDTO().getDeletedProcurementCatCodeList()
				&& !managedBeans.getResDTO().getDeletedProcurementCatCodeList().isEmpty()) {
			deletedProcurementCatCodeList(managedBeans);
		}
		if (managedBeans.isUpdated() && managedBeans.getSelectedDocumentOld() != null
				&& managedBeans.getMandatoryUpdatedPmDocumentRestrList() != null) {
			List<PmDocumentRestr> checklistRestrictionMandatoryNew = getMandatoryNewValue(managedBeans);
			List<PmDocumentRestr> checklistRestrictionMandatoryOld = getMandatoryOldValue(managedBeans);
			updateMandatoryList(managedBeans, checklistRestrictionMandatoryNew, checklistRestrictionMandatoryOld);
		}

		managedBeans.setSelectedDocumentOld(null);
		managedBeans.getResDTO().setUpdatedPmDocumentRestrList(null);
		managedBeans.getResDTO().setDeletedProcurementCatCodeList(null);
	}

	// Helper method to compare objects without considering ID
	private boolean isSameWithoutId(PmDocumentRestr a, PmDocumentRestr b) {
		// Compare fields that uniquely identify the object (excluding ID)
		return a.getProcurementCatCode().getParameterId().equals(b.getProcurementCatCode().getParameterId());
	}

	private void updateMandatoryList(SupportDocumentManagedBean managedBeans,
			List<PmDocumentRestr> checklistRestrictionMandatoryNew, List<PmDocumentRestr> checklistRestrictionMandatoryOld) {
		for (PmDocumentRestr res : checklistRestrictionMandatoryNew) {
			for (PmDocumentRestr oldValue : checklistRestrictionMandatoryOld) {
				if (res.getDocumentRestrId() != null && res.getDocumentRestrId().equals(oldValue.getDocumentRestrId())
						&& res.isMandatory() != oldValue.isMandatory()) {
					this.docGroupId = BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId());
					this.docId = BigInteger.valueOf(res.getDocumentRestrId());
					this.lvlCode3 = managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode();

					detailObj(AuditConst.PM_AUD_QTCHECKLIST_MANDATORY_FIELDLABELID,
							oldValue.isMandatory() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO,
							res.isMandatory() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO, isUpdated(managedBeans.isUpdated()));
				}
			}
		}
	}

	private void updatedPmDocumentRestrList(SupportDocumentManagedBean managedBeans) {
		this.docGroupId = BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId());
		this.lvlCode3 = managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getUpdatedPmDocumentRestrList()) {
			this.docId = BigInteger.valueOf(pmDocumentRestr.getDocumentRestrId());
			if (pmDocumentRestr.getProcurementCatCode().getParameterId() != null) {
				detailObj(AuditConst.PM_AUD_QTCHECKLIST_PROCUREMENT_CATEGORY_FIELDLABELID, null, pmDocumentRestr
						.getProcurementCatCode().getPmParameterDescsSpecCallOnly().get(1).getCodeName(), isUpdated(managedBeans
						.getSelectedDocument().getPmDocumentRestrs().contains(pmDocumentRestr)));
			}
			if (pmDocumentRestr.isMandatory() || !pmDocumentRestr.isMandatory()) {
				detailObj(AuditConst.PM_AUD_QTCHECKLIST_MANDATORY_FIELDLABELID, null,
						pmDocumentRestr.isMandatory() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO, isUpdated(managedBeans
								.getSelectedDocument().getPmDocumentRestrs().contains(pmDocumentRestr)));
			}
		}
	}

	private void deletedProcurementCatCodeList(SupportDocumentManagedBean managedBeans) {
		for (PmDocumentRestr deletepmDocumentRestr : managedBeans.getResDTO().getDeletedProcurementCatCodeList()) {
			if (deletepmDocumentRestr.getProcurementCatCode().getParameterId() != null) {
				this.docGroupId = BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId());
				this.docId = BigInteger.valueOf(deletepmDocumentRestr.getDocumentRestrId());
				this.lvlCode3 = managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode();

				detailObj(AuditConst.PM_AUD_QTCHECKLIST_PROCUREMENT_CATEGORY_FIELDLABELID, deletepmDocumentRestr
						.getProcurementCatCode().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc(), null,
						AuditConst.PM_AUD_ACTION_TYPE_DEL);
			}
			if (deletepmDocumentRestr.isMandatory() || !deletepmDocumentRestr.isMandatory()) {
				this.docGroupId = BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId());
				this.docId = BigInteger.valueOf(deletepmDocumentRestr.getDocumentRestrId());
				this.lvlCode3 = managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode();
				detailObj(AuditConst.PM_AUD_QTCHECKLIST_MANDATORY_FIELDLABELID,
						deletepmDocumentRestr.isMandatory() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO, null,
						AuditConst.PM_AUD_ACTION_TYPE_DEL);
			}
		}
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

	private void detailObj(BigInteger fieldLabelId, String oldValue, String newValue, String actionType) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(docType);
		dtlObj.setPmParameterLvlId(this.lvlCode3.equalsIgnoreCase("TC") ? AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID_TC
				: AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID_FC);
		dtlObj.setDocNo(docNo);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_DC);
		dtlObj.setLvlCode3(lvlCode3);
		dtlObj.setDocId(docId);
		dtlObj.setDocGroupId(docGroupId);
		dtlObj.setFieldLabelCode(fieldLabelId);
		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}
		dtlObj.setNewValue(newValue);
		dtlObj.setActionType(actionType);
		objList.add(dtlObj);
	}

	private String isUpdated(boolean updated) {
		if (updated) {
			return AuditConst.PM_AUD_ACTION_TYPE_UPD;
		} else {
			return AuditConst.PM_AUD_ACTION_TYPE_INS;
		}
	}
}