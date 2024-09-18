package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import my.ep.constant.pm.AuditPMSupplierMgtConfigConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmDocumentRestr;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.backingbean.pm.common.supportdoc.SupportDocumentBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.common.supportdoc.SupportDocumentManagedBean;

/**
 * AuditPMSupportingDocumentBackingBean
 * 
 * @author normuliana #28895-35509
 * 
 */
public class AuditPMSupportingDocumentBackingBean extends AuditPMBackingBeanTemplate {

	private static final long serialVersionUID = 1L;
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	boolean updated = false;

	AuditPMSPDTO detailObjDocumentName = new AuditPMSPDTO();
	AuditPMSPDTO detailObjZone = new AuditPMSPDTO();
	AuditPMSPDTO detailObjDisplayPriority = new AuditPMSPDTO();
	AuditPMSPDTO detailObjStatus = new AuditPMSPDTO();
	/* Document Restriction */
	AuditPMSPDTO detailObjAppTypeChkbox = new AuditPMSPDTO();
	AuditPMSPDTO detailObjCompTypeChkbox = new AuditPMSPDTO();
	AuditPMSPDTO detailObjSupplierTypeChkbox = new AuditPMSPDTO();
	AuditPMSPDTO detailObjCategoryChkbox = new AuditPMSPDTO();
	AuditPMSPDTO detailObjCertBodyChkbox = new AuditPMSPDTO();
	/* Document Type * */
	AuditPMSPDTO detailObjMandatoryChkbox = new AuditPMSPDTO();
	AuditPMSPDTO detailObjLicenseChkbox = new AuditPMSPDTO();
	AuditPMSPDTO detailObjHardCopyChkbox = new AuditPMSPDTO();
	AuditPMSPDTO detailObjGSTChkbox = new AuditPMSPDTO();
	AuditPMSPDTO detailObjCLBGChkbox = new AuditPMSPDTO();

	private List<PmDocumentRestr> appTypeRestrictionsUpdated = new ArrayList<>();

	private List<PmDocumentRestr> appTypeRestrictionsNew = new ArrayList<>();
	private List<PmDocumentRestr> comTypeRestrictionsNew = new ArrayList<>();
	private List<PmDocumentRestr> suppTypeRestrictionsNew = new ArrayList<>();
	private List<PmDocumentRestr> categoryRestrictionsNew = new ArrayList<>();
	private List<PmDocumentRestr> certBodyRestrictionsNew = new ArrayList<>();

	private List<PmDocumentRestr> appTypeRestrictionsOld = new ArrayList<>();
	private List<PmDocumentRestr> comTypeRestrictionsOld = new ArrayList<>();
	private List<PmDocumentRestr> suppTypeRestrictionsOld = new ArrayList<>();
	private List<PmDocumentRestr> categoryRestrictionsOld = new ArrayList<>();
	private List<PmDocumentRestr> certBodyRestrictionsOld = new ArrayList<>();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleSupportingDoc();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean managedBean, String roleCode) {
		SupportDocumentManagedBean managedBeans = (SupportDocumentManagedBean) managedBean;
		if (null != managedBeans.getSelectedDocument() && null != managedBeans.getSelectedDocumentOld()) {
			updated = true;
			// /* Display priority **/
			if (!managedBeans.getSelectedDocument().getDisplaySeq()
					.equalsIgnoreCase(managedBeans.getSelectedDocumentOld().getDisplaySeq())) {
				detailObjDisplayPriority.setRoleCode(roleCode);
				detailObjDisplayPriority.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjDisplayPriority.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjDisplayPriority.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjDisplayPriority.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjDisplayPriority.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjDisplayPriority.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjDisplayPriority.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
				detailObjDisplayPriority.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
				detailObjDisplayPriority.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_DISPLAY_PRIORITY_FIELDLABELID);
				detailObjDisplayPriority.setOldValue(managedBeans.getSelectedDocumentOld().getDisplaySeq());
				detailObjDisplayPriority.setNewValue(managedBeans.getSelectedDocument().getDisplaySeq());
				detailObjDisplayPriority.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(detailObjDisplayPriority);
				auditPMReqDTO.setObjList(objList);
			}
			// /* detailObjStatus */
			if (managedBeans.getSelectedDocument().getRecordStatusFlg() != managedBeans.getSelectedDocumentOld()
					.getRecordStatusFlg()) {
				detailObjStatus.setRoleCode(roleCode);
				detailObjStatus.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
				detailObjStatus.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
				detailObjStatus.setDocNo(managedBeans.getSelectedDocument().getDocName());
				detailObjStatus.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
				detailObjStatus.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
				detailObjStatus.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
				detailObjStatus.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
				detailObjStatus.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
				detailObjStatus.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_STATUS_FIELDLABELID);
				detailObjStatus
						.setOldValue(managedBeans.getSelectedDocumentOld().getRecordStatusFlg() == 1 ? AuditPMSupplierMgtConfigConst.PM_AUD_ACTIVE
								: AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE);
				detailObjStatus
						.setNewValue(managedBeans.getSelectedDocument().getRecordStatusFlg() == 1 ? AuditPMSupplierMgtConfigConst.PM_AUD_ACTIVE
								: AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE);
				detailObjStatus.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(detailObjStatus);
				auditPMReqDTO.setObjList(objList);
			}
			getOldValList(managedBeans);

			updatedAddCheckBox(managedBeans, roleCode, updated);

			/* Create new row list in update mode */
			if (null != managedBeans.getMandatoryUpdatedPmDocumentRestrList()) {
				getNewValList(managedBeans, roleCode, updated);
			}

			deleted(managedBeans, roleCode);

			getUpdatedRowFieldAppTypeTabValList(managedBeans, roleCode);
		} else {
			updated = false;
			/*
			 * New created
			 */
			detailObjZone.setRoleCode(roleCode);
			detailObjZone.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjZone.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjZone.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjZone.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjZone.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjZone.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjZone.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjZone.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjZone.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_ZONE_FIELDLABELID);
			detailObjZone.setNewValue(managedBeans.getResDTO().getDocument().getPmZone().getZoneCode());
			detailObjZone.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjZone);
			auditPMReqDTO.setObjList(objList);

			detailObjDocumentName.setRoleCode(roleCode);
			detailObjDocumentName.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjDocumentName.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjDocumentName.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjDocumentName.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjDocumentName.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjDocumentName.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjDocumentName.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjDocumentName.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjDocumentName.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_DOCUMENTNAME_FIELDLABELID);
			detailObjDocumentName.setNewValue(managedBeans.getResDTO().getDocument().getDocName());
			detailObjDocumentName.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjDocumentName);
			auditPMReqDTO.setObjList(objList);

			detailObjDisplayPriority.setRoleCode(roleCode);
			detailObjDisplayPriority.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjDisplayPriority.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjDisplayPriority.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjDisplayPriority.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjDisplayPriority.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjDisplayPriority.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjDisplayPriority.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjDisplayPriority.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjDisplayPriority.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_DISPLAY_PRIORITY_FIELDLABELID);
			detailObjDisplayPriority.setNewValue(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjDisplayPriority.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjDisplayPriority);
			auditPMReqDTO.setObjList(objList);

			detailObjStatus.setRoleCode(roleCode);
			detailObjStatus.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjStatus.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjStatus.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjStatus.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjStatus.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjStatus.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjStatus.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjStatus.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjStatus.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_STATUS_FIELDLABELID);
			detailObjStatus
					.setNewValue(managedBeans.getResDTO().getDocument().getRecordStatusFlg() == 1 ? AuditPMSupplierMgtConfigConst.PM_AUD_ACTIVE
							: AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE);
			detailObjStatus.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjStatus);
			auditPMReqDTO.setObjList(objList);

			/* Create new row list in new mode */
			updatedAddCheckBox(managedBeans, roleCode, updated);
			if (null != managedBeans.getMandatoryUpdatedPmDocumentRestrList()) {
				getNewValList(managedBeans, roleCode, updated);
			}
		}

		managedBeans.setSelectedDocumentOld(null);
		managedBeans.getResDTO().setUpdatedPmDocumentRestrList(null);
		managedBeans.getResDTO().setDeletedProcurementCatCodeList(null);

		return auditPMReqDTO;
	}

	private AuditPMReqDTO updatedAddCheckBox(SupportDocumentManagedBean managedBeans, String roleCode, boolean updated) {
		// /* Restriction Document - detailObjAppTypeChkbox **/
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isByAppType() != managedBeans.getSelectedDocumentOld().isByAppType()
				|| !updated) {
			detailObjAppTypeChkbox.setRoleCode(roleCode);
			detailObjAppTypeChkbox.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjAppTypeChkbox.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjAppTypeChkbox.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjAppTypeChkbox.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjAppTypeChkbox.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjAppTypeChkbox.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjAppTypeChkbox.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjAppTypeChkbox.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjAppTypeChkbox.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_APPTYPECHKBOX_FIELDLABELID);
			oldValue(managedBeans.getSelectedDocument(), managedBeans.getSelectedDocumentOld(), updated);
			detailObjAppTypeChkbox.setNewValue(newValue(managedBeans.getSelectedDocument().isByAppType()));
			detailObjAppTypeChkbox.setActionType(actionType(updated));
			objList.add(detailObjAppTypeChkbox);
			auditPMReqDTO.setObjList(objList);
		}
		// /* Restriction Document - detailObjCompTypeChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isByCompType() != managedBeans.getSelectedDocumentOld().isByCompType()
				|| !updated) {
			detailObjCompTypeChkbox.setRoleCode(roleCode);
			detailObjCompTypeChkbox.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjCompTypeChkbox.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjCompTypeChkbox.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjCompTypeChkbox.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjCompTypeChkbox.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjCompTypeChkbox.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjCompTypeChkbox.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjCompTypeChkbox.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjCompTypeChkbox.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_COMPTYPECHKBOX_FIELDLABELID);
			oldValue(managedBeans.getSelectedDocument(), managedBeans.getSelectedDocumentOld(), updated);
			detailObjCompTypeChkbox.setNewValue(newValue(managedBeans.getSelectedDocument().isByCompType()));
			detailObjCompTypeChkbox.setActionType(actionType(updated));
			objList.add(detailObjCompTypeChkbox);
			auditPMReqDTO.setObjList(objList);
		}
		// /* Restriction Document - detailObjSupplierTypeChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isBySupplierType() != managedBeans.getSelectedDocumentOld()
						.isBySupplierType() || !updated) {
			detailObjSupplierTypeChkbox.setRoleCode(roleCode);
			detailObjSupplierTypeChkbox.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjSupplierTypeChkbox.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjSupplierTypeChkbox.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjSupplierTypeChkbox.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjSupplierTypeChkbox.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjSupplierTypeChkbox.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjSupplierTypeChkbox.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjSupplierTypeChkbox.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjSupplierTypeChkbox
					.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_SUPPLIERTYPECHKBOX_FIELDLABELID);
			oldValue(managedBeans.getSelectedDocument(), managedBeans.getSelectedDocumentOld(), updated);
			detailObjSupplierTypeChkbox.setNewValue(newValue(managedBeans.getSelectedDocument().isBySupplierType()));
			detailObjSupplierTypeChkbox.setActionType(actionType(updated));
			objList.add(detailObjSupplierTypeChkbox);
			auditPMReqDTO.setObjList(objList);
		}

		updatedAddCheckBox2(managedBeans, roleCode, updated);

		return auditPMReqDTO;
	}

	/* Refactor from method -- updatedAddCheckBox */
	private AuditPMReqDTO updatedAddCheckBox2(SupportDocumentManagedBean managedBeans, String roleCode, boolean updated) {
		// /* Restriction Document - detailObjCategoryChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isByCategory() != managedBeans.getSelectedDocumentOld().isByCategory()
				|| !updated) {
			detailObjCategoryChkbox.setRoleCode(roleCode);
			detailObjCategoryChkbox.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjCategoryChkbox.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjCategoryChkbox.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjCategoryChkbox.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjCategoryChkbox.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjCategoryChkbox.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjCategoryChkbox.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjCategoryChkbox.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjCategoryChkbox.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CATEGORYCHKBOX_FIELDLABELID);
			oldValue(managedBeans.getSelectedDocument(), managedBeans.getSelectedDocumentOld(), updated);
			detailObjCategoryChkbox.setNewValue(newValue(managedBeans.getSelectedDocument().isByCategory()));
			detailObjCategoryChkbox.setActionType(actionType(updated));
			objList.add(detailObjCategoryChkbox);
			auditPMReqDTO.setObjList(objList);
		}
		// /* Restriction Document - detailObjCertBodyChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isByCertBody() != managedBeans.getSelectedDocumentOld().isByCertBody()
				|| !updated) {
			detailObjCertBodyChkbox.setRoleCode(roleCode);
			detailObjCertBodyChkbox.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjCertBodyChkbox.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjCertBodyChkbox.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjCertBodyChkbox.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjCertBodyChkbox.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjCertBodyChkbox.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjCertBodyChkbox.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjCertBodyChkbox.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjCertBodyChkbox.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CERTBODYCHKBOX_FIELDLABELID);
			oldValue(managedBeans.getSelectedDocument(), managedBeans.getSelectedDocumentOld(), updated);
			detailObjCertBodyChkbox.setNewValue(newValue(managedBeans.getSelectedDocument().isByCertBody()));
			detailObjCertBodyChkbox.setActionType(actionType(updated));
			objList.add(detailObjCertBodyChkbox);
			auditPMReqDTO.setObjList(objList);
		}
		/* Document Type - detailObjMandatoryChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isMandatory() != managedBeans.getSelectedDocumentOld().isMandatory()
				|| !updated) {
			detailObjMandatoryChkbox.setRoleCode(roleCode);
			detailObjMandatoryChkbox.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjMandatoryChkbox.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjMandatoryChkbox.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjMandatoryChkbox.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjMandatoryChkbox.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjMandatoryChkbox.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjMandatoryChkbox.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjMandatoryChkbox.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjMandatoryChkbox.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_MANDATORYCHKBOX_FIELDLABELID);
			oldValue(managedBeans.getSelectedDocument(), managedBeans.getSelectedDocumentOld(), updated);
			detailObjMandatoryChkbox.setNewValue(newValue(managedBeans.getSelectedDocument().isMandatory()));
			detailObjMandatoryChkbox.setActionType(actionType(updated));
			objList.add(detailObjMandatoryChkbox);
			auditPMReqDTO.setObjList(objList);
		}

		updatedAddCheckBox3(managedBeans, roleCode, updated);

		return auditPMReqDTO;
	}

	/* Refactor from method -- updatedAddCheckBox2 */
	private AuditPMReqDTO updatedAddCheckBox3(SupportDocumentManagedBean managedBeans, String roleCode, boolean updated) {
		/* Document Type - detailObjLicenseChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isLicenseReq() != managedBeans.getSelectedDocumentOld().isLicenseReq()
				|| !updated) {
			detailObjLicenseChkbox.setRoleCode(roleCode);
			detailObjLicenseChkbox.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjLicenseChkbox.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjLicenseChkbox.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjLicenseChkbox.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjLicenseChkbox.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjLicenseChkbox.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjLicenseChkbox.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjLicenseChkbox.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjLicenseChkbox.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_LICENSECHKBOX_FIELDLABELID);
			oldValue(managedBeans.getSelectedDocument(), managedBeans.getSelectedDocumentOld(), updated);
			detailObjLicenseChkbox.setNewValue(newValue(managedBeans.getSelectedDocument().isLicenseReq()));
			detailObjLicenseChkbox.setActionType(actionType(updated));
			objList.add(detailObjLicenseChkbox);
			auditPMReqDTO.setObjList(objList);
		}
		/* Document Type - detailObjHardCopyChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isHardCopyReq() != managedBeans.getSelectedDocumentOld().isHardCopyReq()
				|| !updated) {
			detailObjHardCopyChkbox.setRoleCode(roleCode);
			detailObjHardCopyChkbox.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjHardCopyChkbox.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjHardCopyChkbox.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjHardCopyChkbox.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjHardCopyChkbox.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjHardCopyChkbox.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjHardCopyChkbox.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjHardCopyChkbox.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjHardCopyChkbox.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_HARDCOPYCHKBOX_FIELDLABELID);
			oldValue2(managedBeans.getSelectedDocument(), managedBeans.getSelectedDocumentOld(), updated);
			detailObjHardCopyChkbox.setNewValue(newValue(managedBeans.getSelectedDocument().isHardCopyReq()));
			detailObjHardCopyChkbox.setActionType(actionType(updated));
			objList.add(detailObjHardCopyChkbox);
			auditPMReqDTO.setObjList(objList);
		}
		/* Document Type - detailObjGSTChkbox */
		if (null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.getSelectedDocument().isGst() != managedBeans.getSelectedDocumentOld().isGst() || !updated) {
			detailObjGSTChkbox.setRoleCode(roleCode);
			detailObjGSTChkbox.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
			detailObjGSTChkbox.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
			detailObjGSTChkbox.setDocNo(managedBeans.getSelectedDocument().getDocName());
			detailObjGSTChkbox.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
			detailObjGSTChkbox.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
			detailObjGSTChkbox.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
			detailObjGSTChkbox.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjGSTChkbox.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
			detailObjGSTChkbox.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_GSTCHKBOX_FIELDLABELID);
			oldValue2(managedBeans.getSelectedDocument(), managedBeans.getSelectedDocumentOld(), updated);
			detailObjGSTChkbox.setNewValue(newValue(managedBeans.getSelectedDocument().isGst()));
			detailObjGSTChkbox.setActionType(actionType(updated));
			objList.add(detailObjGSTChkbox);
			auditPMReqDTO.setObjList(objList);
		}

		return auditPMReqDTO;
	}

	private void getNewValList(SupportDocumentManagedBean managedBeans, String roleCode, boolean updated) {
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getMandatoryUpdatedPmDocumentRestrList()) {
			if (pmDocumentRestr.isBrandNew() && DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr.getRecordStatusFlg()) {
				if (managedBeans.getSelectedDocument().isByAppType() && pmDocumentRestr.getAppTypeCode() != null) {
					appTypeRestrictionsNew.add(pmDocumentRestr);
					managedBeans.setAppTypeRestrictionsNew(appTypeRestrictionsNew);
				}
				if (managedBeans.getSelectedDocument().isByCompType() && pmDocumentRestr.getCompanyTypeCode() != null) {
					comTypeRestrictionsNew.add(pmDocumentRestr);

				}
				if (managedBeans.getSelectedDocument().isBySupplierType() && pmDocumentRestr.getSupplierTypeCode() != null) {
					suppTypeRestrictionsNew.add(pmDocumentRestr);
					managedBeans.setSuppTypeRestrictionsNew(suppTypeRestrictionsNew);
				}
			}
		}
		managedBeans.setComTypeRestrictionsNew(comTypeRestrictionsNew);

		getNewValList2(managedBeans, roleCode, updated);
	}

	/* Refactor from method -- getNewValList */
	private void getNewValList2(SupportDocumentManagedBean managedBeans, String roleCode, boolean updated) {
		SupportDocumentBackingBean bean = new SupportDocumentBackingBean();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getMandatoryUpdatedPmDocumentRestrList()) {
			if (pmDocumentRestr.isBrandNew() && DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr.getRecordStatusFlg()) {
				if (managedBeans.getSelectedDocument().isByCategory() && pmDocumentRestr.getPmCategoryL3() != null) {
					pmDocumentRestr.setPmCategoryL3(bean.retrieveCategoryL3DtlById(pmDocumentRestr.getPmCategoryL3()
							.getCategoryL3Id()));
					categoryRestrictionsNew.add(pmDocumentRestr);
					managedBeans.setCategoryRestrictionsNew(categoryRestrictionsNew);
				}
				if (managedBeans.getSelectedDocument().isByCertBody() && pmDocumentRestr.getPmCertBody() != null) {
					certBodyRestrictionsNew.add(pmDocumentRestr);
					managedBeans.setCertBodyRestrictionsNew(certBodyRestrictionsNew);
				}
			}
		}

		updatedNewCreatedRowList(managedBeans, roleCode, updated);

		// getNewValList3(managedBeans, roleCode, updated);
	}

	/* To create a new row list in create and Update mode */
	private AuditPMReqDTO updatedNewCreatedRowList(SupportDocumentManagedBean managedBeans, String roleCode, boolean updated) {
		AuditPMSupportingDocumentNewListBackingBean newListMapBean = new AuditPMSupportingDocumentNewListBackingBean();
		if (null != managedBeans.getAppTypeRestrictionsNew() && !managedBeans.getAppTypeRestrictionsNew().isEmpty()) {
			objList.addAll(newListMapBean.updatedNewCreateRowAppTypeTabList(managedBeans, roleCode, updated));
			auditPMReqDTO.setObjList(objList);
		}
		if (null != managedBeans.getComTypeRestrictionsNew() && !managedBeans.getComTypeRestrictionsNew().isEmpty()) {
			objList.addAll(newListMapBean.updatedNewCreateRowComTypeTabList(managedBeans, roleCode, updated));
			auditPMReqDTO.setObjList(objList);
		}
		if (null != managedBeans.getSuppTypeRestrictionsNew() && !managedBeans.getSuppTypeRestrictionsNew().isEmpty()) {
			objList.addAll(newListMapBean.updatedNewCreateRowSuppTypeTabList(managedBeans, roleCode, updated));
			auditPMReqDTO.setObjList(objList);
		}

		updatedNewCreatedRowList2(managedBeans, roleCode, updated);

		managedBeans.setAppTypeRestrictionsNew(null);
		managedBeans.setComTypeRestrictionsNew(null);
		managedBeans.setSuppTypeRestrictionsNew(null);

		return auditPMReqDTO;
	}

	/* Refactor from method updatedNewCreatedRowList */
	private AuditPMReqDTO updatedNewCreatedRowList2(SupportDocumentManagedBean managedBeans, String roleCode, boolean updated) {
		AuditPMSupportingDocumentNewListBackingBean newListMapBean = new AuditPMSupportingDocumentNewListBackingBean();
		if (null != managedBeans.getCategoryRestrictionsNew() && !managedBeans.getCategoryRestrictionsNew().isEmpty()) {
			objList.addAll(newListMapBean.updatedNewCreateRowCategoryTypeTabList(managedBeans, roleCode, updated));
			auditPMReqDTO.setObjList(objList);
		}
		if (null != managedBeans.getCertBodyRestrictionsNew() && !managedBeans.getCertBodyRestrictionsNew().isEmpty()) {
			objList.addAll(newListMapBean.updatedNewCreateRowCertBodyTypeTabList(managedBeans, roleCode, updated));
			auditPMReqDTO.setObjList(objList);
		}

		managedBeans.setCategoryRestrictionsNew(null);
		managedBeans.setCertBodyRestrictionsNew(null);

		return auditPMReqDTO;
	}

	/* Refactor from method -- getNewValList2 */
	private void getNewValList3(SupportDocumentManagedBean managedBeans, String roleCode, boolean updated) {

		if (null != managedBeans.getComTypeRestrictionsNew() && !managedBeans.getComTypeRestrictionsNew().isEmpty()) {
			updatedNewCreatedRowList(managedBeans, roleCode, updated);
		}
		if (null != managedBeans.getSuppTypeRestrictionsNew() && !managedBeans.getSuppTypeRestrictionsNew().isEmpty()) {
			updatedNewCreatedRowList(managedBeans, roleCode, updated);
		}
		if (null != managedBeans.getCategoryRestrictionsNew() && !managedBeans.getCategoryRestrictionsNew().isEmpty()) {
			updatedNewCreatedRowList(managedBeans, roleCode, updated);
		}
		if (null != managedBeans.getCertBodyRestrictionsNew() && !managedBeans.getCertBodyRestrictionsNew().isEmpty()) {
			updatedNewCreatedRowList(managedBeans, roleCode, updated);
		}
	}

	private void getOldValList(SupportDocumentManagedBean managedBeans) {
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getSelectedDocumentOld().getPmDocumentRestrs()) {
			if (DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr.getRecordStatusFlg()) {
				if (managedBeans.getSelectedDocumentOld().isByAppType() && pmDocumentRestr.getAppTypeCode() != null) {
					appTypeRestrictionsOld.add(pmDocumentRestr);
					managedBeans.setAppTypeRestrictionsOld(appTypeRestrictionsOld);
				}
				if (managedBeans.getSelectedDocumentOld().isByCompType() && pmDocumentRestr.getCompanyTypeCode() != null) {
					comTypeRestrictionsOld.add(pmDocumentRestr);
					managedBeans.setComTypeRestrictionsOld(comTypeRestrictionsOld);
				}
				if (managedBeans.getSelectedDocumentOld().isBySupplierType() && pmDocumentRestr.getSupplierTypeCode() != null) {
					suppTypeRestrictionsOld.add(pmDocumentRestr);
					managedBeans.setSuppTypeRestrictionsOld(suppTypeRestrictionsOld);
				}
			}
		}
		getOldValList2(managedBeans);
	}

	/* Refactor from method -- getOldValList */
	private void getOldValList2(SupportDocumentManagedBean managedBeans) {
		SupportDocumentBackingBean bean = new SupportDocumentBackingBean();
		for (PmDocumentRestr pmDocumentRestr : managedBeans.getSelectedDocumentOld().getPmDocumentRestrs()) {
			if (DbValueConst.RECORD_STATUS_ACTIVE == pmDocumentRestr.getRecordStatusFlg()) {
				if (managedBeans.getSelectedDocumentOld().isByCategory() && pmDocumentRestr.getPmCategoryL3() != null) {
					pmDocumentRestr.setPmCategoryL3(bean.retrieveCategoryL3DtlById(pmDocumentRestr.getPmCategoryL3()
							.getCategoryL3Id()));
					categoryRestrictionsOld.add(pmDocumentRestr);
					managedBeans.setCategoryRestrictionsOld(categoryRestrictionsOld);
				}
				if (managedBeans.getSelectedDocumentOld().isByCertBody() && pmDocumentRestr.getPmCertBody() != null) {
					certBodyRestrictionsOld.add(pmDocumentRestr);
					managedBeans.setCertBodyRestrictionsOld(certBodyRestrictionsOld);
				}
			}
		}
	}

	private void getUpdatedRowFieldAppTypeTabValList(SupportDocumentManagedBean managedBeans, String roleCode) {
		if (null != managedBeans.getSelectedDocument().getPmDocumentRestrs()) {
			for (PmDocumentRestr pmDocumentRestr : managedBeans.getAppTypeRestrictions()) {
				if (!pmDocumentRestr.isBrandNew() && managedBeans.getSelectedDocument().isByAppType()
						&& pmDocumentRestr.getAppTypeCode() != null && !pmDocumentRestr.isDeleted()) {
					appTypeRestrictionsUpdated.add(pmDocumentRestr);
					managedBeans.setAppTypeRestrictionsUpdated(appTypeRestrictionsUpdated);
				}
			}
			if (null != managedBeans.getAppTypeRestrictionsUpdated() && !managedBeans.getAppTypeRestrictionsUpdated().isEmpty()
					&& !managedBeans.getAppTypeRestrictionsOld().isEmpty()) {
				updatedRowFieldAppTypeTabValList(managedBeans, roleCode);
			}
		}
	}

	/* update field value in the row list for AppType Tab */
	private AuditPMReqDTO updatedRowFieldAppTypeTabValList(SupportDocumentManagedBean managedBeans, String roleCode) {
		AuditPMSPDTO detailObjSectionIsCompanyInfoChangedList;
		AuditPMSPDTO detailObjIsPersonnelBranchChangedList;
		AuditPMSPDTO detailObjSectionIsAsscProjChangedList;

		for (PmDocumentRestr pmDocumentRestr : managedBeans.getAppTypeRestrictions()) {
			for (PmDocumentRestr pmDocumentRestrOld : managedBeans.getAppTypeRestrictionsOld()) {
				/*
				 * Update changes value only
				 * 
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isAsscProjChanged()
				 */
				if (!Boolean.valueOf(pmDocumentRestr.isAsscProjChanged()).equals(pmDocumentRestrOld.isAsscProjChanged())
						&& pmDocumentRestr.getDocumentRestrId() == pmDocumentRestrOld.getDocumentRestrId()) {
					detailObjSectionIsAsscProjChangedList = new AuditPMSPDTO();
					detailObjSectionIsAsscProjChangedList.setRoleCode(roleCode);
					detailObjSectionIsAsscProjChangedList.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					detailObjSectionIsAsscProjChangedList
							.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					detailObjSectionIsAsscProjChangedList.setDocNo(managedBeans.getSelectedDocument().getDocName());
					detailObjSectionIsAsscProjChangedList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					detailObjSectionIsAsscProjChangedList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					detailObjSectionIsAsscProjChangedList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					detailObjSectionIsAsscProjChangedList.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjSectionIsAsscProjChangedList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjSectionIsAsscProjChangedList
							.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_ASSCPROJECT_FIELDLABELID);
					detailObjSectionIsAsscProjChangedList.setOldValue(yesNo(pmDocumentRestrOld.isAsscProjChanged()));
					detailObjSectionIsAsscProjChangedList.setNewValue(yesNo(pmDocumentRestr.isAsscProjChanged()));
					detailObjSectionIsAsscProjChangedList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
					objList.add(detailObjSectionIsAsscProjChangedList);
				}
				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isCompanyInfoChanged
				 */
				if (!Boolean.valueOf(pmDocumentRestr.isCompanyInfoChanged()).equals(pmDocumentRestrOld.isCompanyInfoChanged())
						&& pmDocumentRestr.getDocumentRestrId() == pmDocumentRestrOld.getDocumentRestrId()) {
					detailObjSectionIsCompanyInfoChangedList = new AuditPMSPDTO();
					detailObjSectionIsCompanyInfoChangedList.setRoleCode(roleCode);
					detailObjSectionIsCompanyInfoChangedList.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					detailObjSectionIsCompanyInfoChangedList
							.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					detailObjSectionIsCompanyInfoChangedList.setDocNo(managedBeans.getSelectedDocument().getDocName());
					detailObjSectionIsCompanyInfoChangedList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					detailObjSectionIsCompanyInfoChangedList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					detailObjSectionIsCompanyInfoChangedList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					detailObjSectionIsCompanyInfoChangedList.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjSectionIsCompanyInfoChangedList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjSectionIsCompanyInfoChangedList
							.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_COMPANYINFO_FIELDLABELID);
					detailObjSectionIsCompanyInfoChangedList.setOldValue(yesNo(pmDocumentRestrOld.isCompanyInfoChanged()));
					detailObjSectionIsCompanyInfoChangedList.setNewValue(yesNo(pmDocumentRestr.isCompanyInfoChanged()));
					detailObjSectionIsCompanyInfoChangedList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
					objList.add(detailObjSectionIsCompanyInfoChangedList);
				}
				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isPersonnelBranchChanged()
				 */
				if (pmDocumentRestrOld.isPersonnelBranchChanged() != pmDocumentRestr.isPersonnelBranchChanged()
						&& pmDocumentRestr.getDocumentRestrId() == pmDocumentRestrOld.getDocumentRestrId()) {
					detailObjIsPersonnelBranchChangedList = new AuditPMSPDTO();
					detailObjIsPersonnelBranchChangedList.setRoleCode(roleCode);
					detailObjIsPersonnelBranchChangedList.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					detailObjIsPersonnelBranchChangedList
							.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					detailObjIsPersonnelBranchChangedList.setDocNo(managedBeans.getSelectedDocument().getDocName());
					detailObjIsPersonnelBranchChangedList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					detailObjIsPersonnelBranchChangedList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					detailObjIsPersonnelBranchChangedList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					detailObjIsPersonnelBranchChangedList.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjIsPersonnelBranchChangedList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjIsPersonnelBranchChangedList
							.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PERSONALBRANCH_FIELDLABELID);
					detailObjIsPersonnelBranchChangedList.setOldValue(yesNo(pmDocumentRestrOld.isPersonnelBranchChanged()));
					detailObjIsPersonnelBranchChangedList.setNewValue(yesNo(pmDocumentRestr.isPersonnelBranchChanged()));
					detailObjIsPersonnelBranchChangedList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
					objList.add(detailObjIsPersonnelBranchChangedList);
				}
			}
		}

		auditPMReqDTO.setObjList(objList);

		updatedRowFieldAppTypeTabValList2(managedBeans, roleCode);

		return auditPMReqDTO;
	}

	/* Refactor from method updatedRowFieldAppTypeTabValList */
	private AuditPMReqDTO updatedRowFieldAppTypeTabValList2(SupportDocumentManagedBean managedBeans, String roleCode) {
		AuditPMSPDTO isBankChangedList;
		AuditPMSPDTO detailObjIsCapitalEquityChangedList;

		for (PmDocumentRestr pmDocumentRestr : managedBeans.getAppTypeRestrictions()) {
			for (PmDocumentRestr pmDocumentRestrOld : managedBeans.getAppTypeRestrictionsOld()) {
				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isBankChanged()
				 */
				if (pmDocumentRestrOld.isBankChanged() != pmDocumentRestr.isBankChanged()
						&& pmDocumentRestr.getDocumentRestrId() == pmDocumentRestrOld.getDocumentRestrId()) {
					isBankChangedList = new AuditPMSPDTO();
					isBankChangedList.setRoleCode(roleCode);
					isBankChangedList.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					isBankChangedList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					isBankChangedList.setDocNo(managedBeans.getSelectedDocument().getDocName());
					isBankChangedList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					isBankChangedList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					isBankChangedList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					isBankChangedList.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
					isBankChangedList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
					isBankChangedList.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_BANKINFO_FIELDLABELID);
					isBankChangedList.setOldValue(yesNo(pmDocumentRestrOld.isBankChanged()));
					isBankChangedList.setNewValue(yesNo(pmDocumentRestr.isBankChanged()));
					isBankChangedList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
					objList.add(isBankChangedList);
				}
				/*
				 * Restriction Document - AppTypeList - Section : TextList -
				 * isCapitalEquityChanged()
				 */
				if (pmDocumentRestrOld.isCapitalEquityChanged() != pmDocumentRestr.isCapitalEquityChanged()
						&& pmDocumentRestr.getDocumentRestrId() == pmDocumentRestrOld.getDocumentRestrId()) {
					detailObjIsCapitalEquityChangedList = new AuditPMSPDTO();
					detailObjIsCapitalEquityChangedList.setRoleCode(roleCode);
					detailObjIsCapitalEquityChangedList.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					detailObjIsCapitalEquityChangedList
							.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					detailObjIsCapitalEquityChangedList.setDocNo(managedBeans.getSelectedDocument().getDocName());
					detailObjIsCapitalEquityChangedList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					detailObjIsCapitalEquityChangedList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					detailObjIsCapitalEquityChangedList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					detailObjIsCapitalEquityChangedList.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjIsCapitalEquityChangedList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjIsCapitalEquityChangedList
							.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_CAPITALEQUITY_FIELDLABELID);
					detailObjIsCapitalEquityChangedList.setOldValue(yesNo(pmDocumentRestrOld.isCapitalEquityChanged()));
					detailObjIsCapitalEquityChangedList.setNewValue(yesNo(pmDocumentRestr.isCapitalEquityChanged()));
					detailObjIsCapitalEquityChangedList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
					objList.add(detailObjIsCapitalEquityChangedList);
				}
			}
		}
		auditPMReqDTO.setObjList(objList);

		updatedRowFieldAppTypeTabValList3(managedBeans, roleCode);

		return auditPMReqDTO;
	}

	/* Refactor from method updatedRowFieldAppTypeTabValList2 */
	private AuditPMReqDTO updatedRowFieldAppTypeTabValList3(SupportDocumentManagedBean managedBeans, String roleCode) {
		AuditPMSPDTO detailObjFacilityTypeHomeList;
		AuditPMSPDTO detailObjFacilityTypeOfficeList;

		for (PmDocumentRestr pmDocumentRestr : managedBeans.getAppTypeRestrictions()) {
			for (PmDocumentRestr pmDocumentRestrOld : managedBeans.getAppTypeRestrictionsOld()) {
				/* Facility Type : Home Office (Y/N) */
				if (pmDocumentRestrOld.isHomeOffice() != pmDocumentRestr.isHomeOffice()
						&& pmDocumentRestr.getDocumentRestrId() == pmDocumentRestrOld.getDocumentRestrId()) {
					detailObjFacilityTypeHomeList = new AuditPMSPDTO();
					detailObjFacilityTypeHomeList.setRoleCode(roleCode);
					detailObjFacilityTypeHomeList.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					detailObjFacilityTypeHomeList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					detailObjFacilityTypeHomeList.setDocNo(managedBeans.getSelectedDocument().getDocName());
					detailObjFacilityTypeHomeList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					detailObjFacilityTypeHomeList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					detailObjFacilityTypeHomeList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					detailObjFacilityTypeHomeList
							.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument().getDocumentId()));
					detailObjFacilityTypeHomeList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjFacilityTypeHomeList
							.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_FACILITYHOME_FIELDLABELID);
					detailObjFacilityTypeHomeList.setOldValue(yesNo(pmDocumentRestrOld.isHomeOffice()));
					detailObjFacilityTypeHomeList.setNewValue(yesNo(pmDocumentRestr.isHomeOffice()));
					detailObjFacilityTypeHomeList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
					objList.add(detailObjFacilityTypeHomeList);
				}
				/* Facility Type : Business Office (Y/N) */
				if (pmDocumentRestrOld.isBusinessOffice() != pmDocumentRestr.isBusinessOffice()
						&& pmDocumentRestr.getDocumentRestrId() == pmDocumentRestrOld.getDocumentRestrId()) {
					detailObjFacilityTypeOfficeList = new AuditPMSPDTO();
					detailObjFacilityTypeOfficeList.setRoleCode(roleCode);
					detailObjFacilityTypeOfficeList.setDoctype(AuditPMSupplierMgtConfigConst.PM_AUD_SD);
					detailObjFacilityTypeOfficeList.setPmParameterLvlId(AuditPMSupplierMgtConfigConst.PM_AUD_SD_PMPARAMETERLVLID);
					detailObjFacilityTypeOfficeList.setDocNo(managedBeans.getSelectedDocument().getDocName());
					detailObjFacilityTypeOfficeList.setLvlCode1(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_1_SD);
					detailObjFacilityTypeOfficeList.setLvlCode2(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_2_SD);
					detailObjFacilityTypeOfficeList.setLvlCode3(AuditPMSupplierMgtConfigConst.PM_LVL_CODE_3_SD);
					detailObjFacilityTypeOfficeList.setDocId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjFacilityTypeOfficeList.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedDocument()
							.getDocumentId()));
					detailObjFacilityTypeOfficeList
							.setFieldLabelCode(AuditPMSupplierMgtConfigConst.PM_AUD_SD_FACILITYOFFICE_FIELDLABELID);
					detailObjFacilityTypeOfficeList.setOldValue(yesNo(pmDocumentRestrOld.isBusinessOffice()));
					detailObjFacilityTypeOfficeList.setNewValue(yesNo(pmDocumentRestr.isBusinessOffice()));
					detailObjFacilityTypeOfficeList.setActionType(AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD);
					objList.add(detailObjFacilityTypeOfficeList);
				}
			}
		}
		auditPMReqDTO.setObjList(objList);

		return auditPMReqDTO;
	}

	private AuditPMReqDTO deleted(SupportDocumentManagedBean managedBeans, String roleCode) {
		AuditPMSupportingDocumentDeletedBackingBean deletedMapbean = new AuditPMSupportingDocumentDeletedBackingBean();
		System.out.println("getDeleteRestrictions().size() ::::::: " + managedBeans.getDeleteRestrictions().size());
		if (null != managedBeans.getDeleteAppTypeRestrictionsAuditList()
				&& !managedBeans.getDeleteAppTypeRestrictionsAuditList().isEmpty()) {
			objList.addAll(deletedMapbean.deleteAppType(managedBeans, roleCode));
			auditPMReqDTO.setObjList(objList);
		}
		if (null != managedBeans.getDeleteComTypeRestrictionsAudit()
				&& !managedBeans.getDeleteComTypeRestrictionsAudit().isEmpty()) {
			objList.addAll(deletedMapbean.deleteComType(managedBeans, roleCode));
			auditPMReqDTO.setObjList(objList);
		}
		if (null != managedBeans.getDeleteSuppTypeRestrictionsAudit()
				&& !managedBeans.getDeleteSuppTypeRestrictionsAudit().isEmpty()) {
			objList.addAll(deletedMapbean.deleteSuppType(managedBeans, roleCode));
			auditPMReqDTO.setObjList(objList);
		}
		if (null != managedBeans.getDeleteCategoryRestrictionsAudit()
				&& !managedBeans.getDeleteCategoryRestrictionsAudit().isEmpty()) {
			objList.addAll(deletedMapbean.deleteCategoryType(managedBeans, roleCode));
			auditPMReqDTO.setObjList(objList);
		}
		if (null != managedBeans.getDeleteCertBodyRestrictionsAudit()) {
			objList.addAll(deletedMapbean.deleteCertBodyType(managedBeans, roleCode));
			auditPMReqDTO.setObjList(objList);
		}

		managedBeans.setDeleteAppTypeRestrictionsAuditList(null);
		managedBeans.setDeleteComTypeRestrictionsAudit(null);
		managedBeans.setDeleteSuppTypeRestrictionsAudit(null);
		managedBeans.setDeleteCategoryRestrictionsAudit(null);
		managedBeans.setDeleteCertBodyRestrictionsAudit(null);

		return auditPMReqDTO;
	}

	private void oldValue(PmDocument selectedDocument, PmDocument selectedDocumentOld, boolean updated) {
		if (updated) {
			if (!Boolean.valueOf(selectedDocument.isByAppType()).equals(selectedDocumentOld.isByAppType())) {
				detailObjAppTypeChkbox.setOldValue(oldVal(selectedDocumentOld.isByAppType()));
			}
			if (!Boolean.valueOf(selectedDocument.isByCompType()).equals(selectedDocumentOld.isByCompType())) {
				detailObjCompTypeChkbox.setOldValue(oldVal(selectedDocumentOld.isByCompType()));
			}
			if (!Boolean.valueOf(selectedDocument.isBySupplierType()).equals(selectedDocumentOld.isBySupplierType())) {
				detailObjSupplierTypeChkbox.setOldValue(oldVal(selectedDocumentOld.isBySupplierType()));
			}
			if (!Boolean.valueOf(selectedDocument.isByCategory()).equals(selectedDocumentOld.isByCategory())) {
				detailObjCategoryChkbox.setOldValue(oldVal(selectedDocumentOld.isByCategory()));
			}
			if (!Boolean.valueOf(selectedDocument.isByCertBody()).equals(selectedDocumentOld.isByCertBody())) {
				detailObjCertBodyChkbox.setOldValue(oldVal(selectedDocumentOld.isByCertBody()));
			}
			if (!Boolean.valueOf(selectedDocument.isMandatory()).equals(selectedDocumentOld.isMandatory())) {
				detailObjMandatoryChkbox.setOldValue(oldVal(selectedDocumentOld.isMandatory()));
			}
			if (!Boolean.valueOf(selectedDocument.isLicenseReq()).equals(selectedDocumentOld.isLicenseReq())) {
				detailObjLicenseChkbox.setOldValue(oldVal(selectedDocumentOld.isLicenseReq()));
			}
		}
	}

	/* Refactor from method oldValue */
	private void oldValue2(PmDocument selectedDocument, PmDocument selectedDocumentOld, boolean updated) {
		if (updated) {
			if (!Boolean.valueOf(selectedDocument.isHardCopyReq()).equals(selectedDocumentOld.isHardCopyReq())) {
				detailObjHardCopyChkbox.setOldValue(oldVal(selectedDocumentOld.isHardCopyReq()));
			}
			if (!Boolean.valueOf(selectedDocument.isGst()).equals(selectedDocumentOld.isGst())) {
				detailObjGSTChkbox.setOldValue(oldVal(selectedDocumentOld.isGst()));
			}
		}
	}

	private String oldVal(boolean oldValueUpdated) {
		return oldValueUpdated ? AuditPMSupplierMgtConfigConst.PM_AUD_ACTIVE : AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE;
	}

	private String newValue(boolean checkboxUpdated) {
		return checkboxUpdated ? AuditPMSupplierMgtConfigConst.PM_AUD_ACTIVE : AuditPMSupplierMgtConfigConst.PM_AUD_INACTIVE;
	}

	private String actionType(boolean actionTypeUpdated) {
		return actionTypeUpdated ? AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_UPD
				: AuditPMSupplierMgtConfigConst.PM_AUD_ACTION_TYPE_INS;
	}

	private String yesNo(boolean yesNo) {
		return yesNo ? AuditPMSupplierMgtConfigConst.PM_AUD_YES : AuditPMSupplierMgtConfigConst.PM_AUD_NO;
	}
}