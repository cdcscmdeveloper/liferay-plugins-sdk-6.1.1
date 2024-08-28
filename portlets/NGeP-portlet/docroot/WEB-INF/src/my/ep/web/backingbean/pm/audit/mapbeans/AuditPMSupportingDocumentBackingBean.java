package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmDocument;
import my.ep.persistence.entity.pm.common.PmDocumentRestr;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.common.supportdoc.SupportDocumentManagedBean;

/**
 * AuditPMSupportingDocumentBackingBean
 * 
 * @author normuliana #28895-35509
 * 
 */
public class AuditPMSupportingDocumentBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AuditPMSPDTO detailObjDisplayPriority = new AuditPMSPDTO();
	AuditPMSPDTO detailObjStatus = new AuditPMSPDTO();
	/* Document Restriction */
	AuditPMSPDTO detailObjAppType = new AuditPMSPDTO();
	AuditPMSPDTO detailObjCompType = new AuditPMSPDTO();
	AuditPMSPDTO detailObjSupplierType = new AuditPMSPDTO();
	AuditPMSPDTO detailObjCategory = new AuditPMSPDTO();
	AuditPMSPDTO detailObjCertBody = new AuditPMSPDTO();
	/* Document Type * */
	AuditPMSPDTO detailObjMandatory = new AuditPMSPDTO();
	AuditPMSPDTO detailObjLicense = new AuditPMSPDTO();
	AuditPMSPDTO detailObjHardCopy = new AuditPMSPDTO();
	AuditPMSPDTO detailObjGST = new AuditPMSPDTO();
	AuditPMSPDTO detailObjCLBG = new AuditPMSPDTO();

	boolean updated = false;

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
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		SupportDocumentManagedBean managedBeans = (SupportDocumentManagedBean) managedBean;
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		if (null != managedBeans.getResDTO().getDocument().getDocumentId() && null != managedBeans.getSelectedDocumentOld()
				&& managedBeans.isUpdated()) {
			updated = true;
			auditPMReqDTO = updateDetail(managedBeans.getSelectedDocument(), managedBeans.getSelectedDocumentOld(), objList,
					roleCode);
		} else {
			updated = false;
			/*
			 * New created
			 */
			detailObjDisplayPriority.setRoleCode(roleCode);
			detailObjDisplayPriority.setDoctype(managedBeans.getResDTO().getDocument().getDocName());
			detailObjDisplayPriority.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjDisplayPriority.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjDisplayPriority.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjDisplayPriority.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjDisplayPriority.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPMC);
			detailObjDisplayPriority.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjDisplayPriority.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_DISPLAY_PRIORITY_FIELDLABELID);
			detailObjDisplayPriority.setNewValue(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjDisplayPriority.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjDisplayPriority);
			auditPMReqDTO.setObjList(objList);

			detailObjStatus.setRoleCode(roleCode);
			detailObjStatus.setDoctype(managedBeans.getResDTO().getDocument().getDocName());
			detailObjStatus.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjStatus.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
			detailObjStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjStatus.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
			detailObjStatus.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
			detailObjStatus.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjStatus
					.setNewValue(managedBeans.getResDTO().getDocument().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjStatus);
			auditPMReqDTO.setObjList(objList);

			// updateList(managedBeans, auditPMReqDTO, objList, roleCode);
		}

		managedBeans.setSelectedDocumentOld(null);
		managedBeans.getResDTO().setUpdatedPmDocumentRestrList(null);
		managedBeans.getResDTO().setDeletedProcurementCatCodeList(null);

		return auditPMReqDTO;
	}

	// private AuditPMReqDTO updateDetail(SupportDocumentManagedBean
	// managedBeans, ArrayList<AuditPMSPDTO> objList,
	// String roleCode) {
	// AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	// if (managedBeans.getSelectedDocument().isByAppType() &&
	// managedBeans.getSelectedDocumentOld().isByAppType()) {
	// auditPMReqDTO = updateDetailMain(managedBeans.getSelectedDocument(),
	// managedBeans.getSelectedDocumentOld(),
	// objList, roleCode);
	// }
	// if (managedBeans.getSelectedDocument().isByCompType() &&
	// managedBeans.getSelectedDocumentOld().isByCompType()) {
	// auditPMReqDTO = updateDetailMain(managedBeans.getSelectedDocument(),
	// managedBeans.getSelectedDocumentOld(),
	// objList, roleCode);
	// }
	// if (managedBeans.getSelectedDocument().isBySupplierType()) {
	// auditPMReqDTO = updateDetailList(managedBeans, objList, roleCode);
	// }
	// if (managedBeans.getSelectedDocument().isByCategory()) {
	// auditPMReqDTO = updateDetailList(managedBeans, objList, roleCode);
	// }
	// if (managedBeans.getSelectedDocument().isByCertBody()) {
	// auditPMReqDTO = updateDetailList(managedBeans, objList, roleCode);
	// }
	// return auditPMReqDTO;
	// } ////

	private AuditPMReqDTO updateDetail(PmDocument selectedDocument, PmDocument selectedDocumentOld,
			ArrayList<AuditPMSPDTO> objList, String roleCode) {
		// /* Display priority **/
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		if (selectedDocument.getDisplaySeq() != selectedDocumentOld.getDisplaySeq()) {
			detailObjDisplayPriority.setRoleCode(roleCode);
			detailObjDisplayPriority.setDoctype(selectedDocument.getDocName());
			detailObjDisplayPriority.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjDisplayPriority.setDocNo(selectedDocument.getDisplaySeq());
			detailObjDisplayPriority.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjDisplayPriority.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjDisplayPriority.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPMC);
			detailObjDisplayPriority.setDocId(BigInteger.valueOf(selectedDocument.getDocumentId()));
			detailObjDisplayPriority.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_DISPLAY_PRIORITY_FIELDLABELID);
			detailObjDisplayPriority.setOldValue(selectedDocumentOld.getDisplaySeq());
			detailObjDisplayPriority.setNewValue(selectedDocument.getDisplaySeq());
			detailObjDisplayPriority.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjDisplayPriority);
			auditPMReqDTO.setObjList(objList);
		}
		// /* detailObjStatus */
		if (selectedDocument.getRecordStatusFlg() != selectedDocumentOld.getRecordStatusFlg()) {
			detailObjStatus.setRoleCode(roleCode);
			detailObjStatus.setDoctype(selectedDocument.getDocName());
			detailObjStatus.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjStatus.setDocNo(selectedDocument.getDisplaySeq());
			detailObjStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPMC);
			detailObjStatus.setDocId(BigInteger.valueOf(selectedDocument.getDocumentId()));
			detailObjStatus.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjStatus.setOldValue(selectedDocumentOld.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setNewValue(selectedDocument.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjStatus);
			auditPMReqDTO.setObjList(objList);
		}
		// /* Restriction Document - detailObjAppType **/
		if (selectedDocument.isByAppType() != selectedDocumentOld.isByAppType()) {
			detailObjAppType.setRoleCode(roleCode);
			detailObjAppType.setDoctype(selectedDocument.getDocName());
			detailObjAppType.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjAppType.setDocNo(selectedDocument.getDisplaySeq());
			detailObjAppType.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjAppType.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjAppType.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPMC);
			detailObjAppType.setDocId(BigInteger.valueOf(selectedDocument.getDocumentId()));
			detailObjAppType.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjAppType.setOldValue(selectedDocumentOld.isByAppType() ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjAppType.setNewValue(selectedDocument.isByAppType() ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			detailObjAppType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjAppType);
			auditPMReqDTO.setObjList(objList);
		}
		// /* Restriction Document - detailObjCompType */
		if (selectedDocument.isByCompType() != selectedDocumentOld.isByCompType()) {
			detailObjCompType.setRoleCode(roleCode);
			detailObjCompType.setDoctype(selectedDocument.getDocName());
			detailObjCompType.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjCompType.setDocNo(selectedDocument.getDisplaySeq());
			detailObjCompType.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjCompType.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjCompType.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPMC);
			detailObjCompType.setDocId(BigInteger.valueOf(selectedDocument.getDocumentId()));
			detailObjCompType.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjCompType.setOldValue(selectedDocumentOld.isByCompType() ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjCompType
					.setNewValue(selectedDocument.isByCompType() ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			detailObjCompType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjCompType);
			auditPMReqDTO.setObjList(objList);
		}
		// /* Restriction Document - detailObjSupplierType */
		if (selectedDocument.isBySupplierType() != selectedDocumentOld.isBySupplierType()) {
			detailObjSupplierType.setRoleCode(roleCode);
			detailObjSupplierType.setDoctype(selectedDocument.getDocName());
			detailObjSupplierType.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjSupplierType.setDocNo(selectedDocument.getDisplaySeq());
			detailObjSupplierType.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjSupplierType.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjSupplierType.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPMC);
			detailObjSupplierType.setDocId(BigInteger.valueOf(selectedDocument.getDocumentId()));
			detailObjSupplierType.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjSupplierType.setOldValue(selectedDocumentOld.isBySupplierType() ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjSupplierType.setNewValue(selectedDocument.isBySupplierType() ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjSupplierType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjSupplierType);
			auditPMReqDTO.setObjList(objList);
		}
		// /* Restriction Document - detailObjCategory */
		if (selectedDocument.isByCategory() != selectedDocumentOld.isByCategory()) {
			detailObjCategory.setRoleCode(roleCode);
			detailObjCategory.setDoctype(selectedDocument.getDocName());
			detailObjCategory.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjCategory.setDocNo(selectedDocument.getDisplaySeq());
			detailObjCategory.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjCategory.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjCategory.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPMC);
			detailObjCategory.setDocId(BigInteger.valueOf(selectedDocument.getDocumentId()));
			detailObjCategory.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjCategory.setOldValue(selectedDocumentOld.isByCategory() ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjCategory
					.setNewValue(selectedDocument.isByCategory() ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			detailObjCategory.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjCategory);
			auditPMReqDTO.setObjList(objList);
		}
		// /* Restriction Document - detailObjCertBody */
		if (selectedDocument.isByCertBody() != selectedDocumentOld.isByCertBody()) {
			detailObjCertBody.setRoleCode(roleCode);
			detailObjCertBody.setDoctype(selectedDocument.getDocName());
			detailObjCertBody.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjCertBody.setDocNo(selectedDocument.getDisplaySeq());
			detailObjCertBody.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjCertBody.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjCertBody.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPMC);
			detailObjCertBody.setDocId(BigInteger.valueOf(selectedDocument.getDocumentId()));
			detailObjCertBody.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjCertBody.setOldValue(selectedDocumentOld.isByCertBody() ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjCertBody
					.setNewValue(selectedDocument.isByCertBody() ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			detailObjCertBody.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjCertBody);
			auditPMReqDTO.setObjList(objList);
		}
		// /* Document Type - detailObjMandatory */
		if (selectedDocument.isMandatory() != selectedDocumentOld.isMandatory()) {
			detailObjMandatory.setRoleCode(roleCode);
			detailObjMandatory.setDoctype(selectedDocument.getDocName());
			detailObjMandatory.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjMandatory.setDocNo(selectedDocument.getDisplaySeq());
			detailObjMandatory.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjMandatory.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjMandatory.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPMC);
			detailObjMandatory.setDocId(BigInteger.valueOf(selectedDocument.getDocumentId()));
			detailObjMandatory.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjMandatory.setOldValue(selectedDocumentOld.isMandatory() ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjMandatory
					.setNewValue(selectedDocument.isMandatory() ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			detailObjMandatory.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjMandatory);
			auditPMReqDTO.setObjList(objList);
		}
		// /* Document Type - detailObjLicense */
		if (selectedDocument.isLicenseReq() != selectedDocumentOld.isLicenseReq()) {
			detailObjLicense.setRoleCode(roleCode);
			detailObjLicense.setDoctype(selectedDocument.getDocName());
			detailObjLicense.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjLicense.setDocNo(selectedDocument.getDisplaySeq());
			detailObjLicense.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjLicense.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjLicense.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPMC);
			detailObjLicense.setDocId(BigInteger.valueOf(selectedDocument.getDocumentId()));
			detailObjLicense.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjLicense.setOldValue(selectedDocumentOld.isLicenseReq() ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjLicense.setNewValue(selectedDocument.isLicenseReq() ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			detailObjLicense.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjLicense);
			auditPMReqDTO.setObjList(objList);
		}
		// /* Document Type - detailObjHardCopy */
		if (selectedDocument.isHardCopyReq() != selectedDocumentOld.isHardCopyReq()) {
			detailObjHardCopy.setRoleCode(roleCode);
			detailObjHardCopy.setDoctype(selectedDocument.getDocName());
			detailObjHardCopy.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjHardCopy.setDocNo(selectedDocument.getDisplaySeq());
			detailObjHardCopy.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjHardCopy.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjHardCopy.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPMC);
			detailObjHardCopy.setDocId(BigInteger.valueOf(selectedDocument.getDocumentId()));
			detailObjHardCopy.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjHardCopy.setOldValue(selectedDocumentOld.isHardCopyReq() ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjHardCopy.setNewValue(selectedDocument.isHardCopyReq() ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjHardCopy.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjHardCopy);
			auditPMReqDTO.setObjList(objList);
		}
		// /* Document Type - detailObjGST */
		if (selectedDocument.isGst() != selectedDocumentOld.isGst()) {
			detailObjGST.setRoleCode(roleCode);
			detailObjGST.setDoctype(selectedDocument.getDocName());
			detailObjGST.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjGST.setDocNo(selectedDocument.getDisplaySeq());
			detailObjGST.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjGST.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjGST.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPMC);
			detailObjGST.setDocId(BigInteger.valueOf(selectedDocument.getDocumentId()));
			detailObjGST.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjGST.setOldValue(selectedDocumentOld.isGst() ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			detailObjGST.setNewValue(selectedDocument.isGst() ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			detailObjGST.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjGST);
			auditPMReqDTO.setObjList(objList);
		}
		// /* Document Type - detailObjCLBG */
		if (selectedDocument.isClbg() != selectedDocumentOld.isClbg()) {
			detailObjCLBG.setRoleCode(roleCode);
			detailObjCLBG.setDoctype(selectedDocument.getDocName());
			detailObjCLBG.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
			detailObjCLBG.setDocNo(selectedDocument.getDisplaySeq());
			detailObjCLBG.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
			detailObjCLBG.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
			detailObjCLBG.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPMC);
			detailObjCLBG.setDocId(BigInteger.valueOf(selectedDocument.getDocumentId()));
			detailObjCLBG.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
			detailObjCLBG.setOldValue(selectedDocumentOld.isClbg() ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			detailObjCLBG.setNewValue(selectedDocument.isClbg() ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			detailObjCLBG.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjCLBG);
			auditPMReqDTO.setObjList(objList);
		}

		if (selectedDocumentOld.getPmDocumentRestrs() != null) {
			auditPMReqDTO = updateDetailList(selectedDocument, selectedDocumentOld.getPmDocumentRestrs(), objList, roleCode);
		}
		return auditPMReqDTO;
	}

	private AuditPMReqDTO updateDetailList(PmDocument selectedDocument, List<PmDocumentRestr> pmDocumentRestrsOld,
			ArrayList<AuditPMSPDTO> objList, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		for (PmDocumentRestr pmDocumentRestr : selectedDocument.getPmDocumentRestrs()) {
			for (PmDocumentRestr pmDocumentRestrOld : pmDocumentRestrsOld) {
				// /* Document Type - detailObjAppType *//
				if (selectedDocument.isByAppType() && pmDocumentRestr.getAppTypeCode() != null) {

				}
			}

		}

		// AuditPMSPDTO detailObjCompType = new AuditPMSPDTO();
		// AuditPMSPDTO detailObjSupplierType = new AuditPMSPDTO();
		// AuditPMSPDTO detailObjCategory = new AuditPMSPDTO();
		// AuditPMSPDTO detailObjCertBody = new AuditPMSPDTO();

		return auditPMReqDTO;
	}

	private AuditPMReqDTO deleteDetail(SupportDocumentManagedBean managedBeans, AuditPMReqDTO auditPMReqDTO,
			ArrayList<AuditPMSPDTO> objList, String roleCode) {

		return auditPMReqDTO;
	}

	// private AuditPMReqDTO updateDetail1(SupportDocumentManagedBean
	// managedBeans, AuditPMReqDTO auditPMReqDTO,
	// ArrayList<AuditPMSPDTO> objList, String roleCode) {
	// /* Display priority */
	// if (managedBeans.getSelectedDocumentOld().getDisplaySeq() !=
	// managedBeans.getResDTO().getDocument()
	// .getDisplaySeq()) {
	// detailObjDisplayPriority.setRoleCode(roleCode);
	// detailObjDisplayPriority.setDoctype(managedBeans.getResDTO().getDocument().getDocName());
	// detailObjDisplayPriority.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
	// detailObjDisplayPriority.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
	// detailObjDisplayPriority.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
	// detailObjDisplayPriority.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
	// detailObjDisplayPriority.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode()
	// .getParameterCode());
	// detailObjDisplayPriority.setDocId(BigInteger
	// .valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
	// detailObjDisplayPriority.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_DISPLAY_PRIORITY_FIELDLABELID);
	// detailObjDisplayPriority.setOldValue(managedBeans.getSelectedDocumentOld().getDisplaySeq());
	// detailObjDisplayPriority.setNewValue(managedBeans.getResDTO().getDocument().getDisplaySeq());
	// detailObjDisplayPriority.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
	// objList.add(detailObjDisplayPriority);
	// auditPMReqDTO.setObjList(objList);
	// }
	// /* detailObjStatus */
	// if (managedBeans.getSelectedDocumentOld().getRecordStatusFlg() !=
	// managedBeans.getResDTO().getDocument()
	// .getRecordStatusFlg()) {
	// detailObjStatus.setRoleCode(roleCode);
	// detailObjStatus.setDoctype(managedBeans.getResDTO().getDocument().getDocName());
	// detailObjStatus.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
	// detailObjStatus.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
	// detailObjStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
	// detailObjStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
	// detailObjStatus.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode().getParameterCode());
	// detailObjStatus.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
	// detailObjStatus.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_STATUS_FIELDLABELID);
	// detailObjStatus
	// .setOldValue(managedBeans.getSelectedDocumentOld().getRecordStatusFlg()
	// == 1 ? AuditConst.PM_AUD_ACTIVE
	// : AuditConst.PM_AUD_INACTIVE);
	// detailObjStatus
	// .setNewValue(managedBeans.getResDTO().getDocument().getRecordStatusFlg()
	// == 1 ? AuditConst.PM_AUD_ACTIVE
	// : AuditConst.PM_AUD_INACTIVE);
	// detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
	// objList.add(detailObjStatus);
	// auditPMReqDTO.setObjList(objList);
	// }
	// /* detailObjScoringRequired */
	// if (managedBeans.getSelectedDocumentOld().isScoringReq() !=
	// managedBeans.getResDTO().getDocument()
	// .isScoringReq()) {
	// detailObjScoringRequired.setRoleCode(roleCode);
	// detailObjScoringRequired.setDoctype(managedBeans.getResDTO().getDocument().getDocName());
	// detailObjScoringRequired.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
	// detailObjScoringRequired.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
	// detailObjScoringRequired.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
	// detailObjScoringRequired.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
	// detailObjScoringRequired.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode()
	// .getParameterCode());
	// detailObjScoringRequired.setDocId(BigInteger
	// .valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
	// detailObjScoringRequired.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_SCORING_REQUIRED_FIELDLABELID);
	// detailObjScoringRequired
	// .setOldValue(managedBeans.getSelectedDocumentOld().isScoringReq() ?
	// AuditConst.PM_AUD_ACTIVE
	// : AuditConst.PM_AUD_INACTIVE);
	// detailObjScoringRequired
	// .setNewValue(managedBeans.getResDTO().getDocument().isScoringReq() ?
	// AuditConst.PM_AUD_YES
	// : AuditConst.PM_AUD_NO);
	// detailObjScoringRequired.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
	// objList.add(detailObjScoringRequired);
	// auditPMReqDTO.setObjList(objList);
	// }
	//
	// updateList(managedBeans, auditPMReqDTO, objList, roleCode);
	//
	// return auditPMReqDTO;
	// }
	//
	// private void updateList(SupportDocumentManagedBean managedBeans,
	// AuditPMReqDTO auditPMReqDTO,
	// ArrayList<AuditPMSPDTO> objList, String roleCode) {
	// if (null != managedBeans.getResDTO().getUpdatedPmDocumentRestrList()
	// && !managedBeans.getResDTO().getUpdatedPmDocumentRestrList().isEmpty()) {
	// updatedPmDocumentRestrList(managedBeans, auditPMReqDTO, objList,
	// roleCode);
	// }
	//
	// if (null != managedBeans.getResDTO().getDeletedProcurementCatCodeList()
	// &&
	// !managedBeans.getResDTO().getDeletedProcurementCatCodeList().isEmpty()) {
	// deletedProcurementCatCodeList(managedBeans, auditPMReqDTO, objList,
	// roleCode);
	// }
	// if (managedBeans.isUpdated() && managedBeans.getSelectedDocumentOld() !=
	// null
	// && managedBeans.getMandatoryUpdatedPmDocumentRestrList() != null) {
	// List<PmDocumentRestr> checklistRestrictionMandatoryNew =
	// getMandatoryNewValue(managedBeans);
	// List<PmDocumentRestr> checklistRestrictionMandatoryOld =
	// getMandatoryOldValue(managedBeans);
	// if (checklistRestrictionMandatoryNew != null &&
	// checklistRestrictionMandatoryOld != null) {
	// updateMandatoryList(managedBeans, auditPMReqDTO, objList, roleCode,
	// checklistRestrictionMandatoryNew,
	// checklistRestrictionMandatoryOld);
	// }
	// }
	//
	// managedBeans.setSelectedDocumentOld(null);
	// managedBeans.getResDTO().setUpdatedPmDocumentRestrList(null);
	// managedBeans.getResDTO().setDeletedProcurementCatCodeList(null);
	// }
	//
	// private AuditPMReqDTO updateMandatoryList(SupportDocumentManagedBean
	// managedBeans, AuditPMReqDTO auditPMReqDTO,
	// ArrayList<AuditPMSPDTO> objList, String roleCode, List<PmDocumentRestr>
	// checklistRestrictionMandatoryNew,
	// List<PmDocumentRestr> checklistRestrictionMandatoryOld) {
	// for (PmDocumentRestr res : checklistRestrictionMandatoryNew) {
	// for (PmDocumentRestr oldValue : checklistRestrictionMandatoryOld) {
	// if (res.getDocumentRestrId() == oldValue.getDocumentRestrId()
	// && res.isMandatory() != oldValue.isMandatory()) {
	// AuditPMSPDTO detailObjMandatory = new AuditPMSPDTO();
	// detailObjMandatory.setRoleCode(roleCode);
	// detailObjMandatory.setDoctype(managedBeans.getResDTO().getDocument().getDocName());
	// detailObjMandatory.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
	// detailObjMandatory.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
	// detailObjMandatory.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
	// detailObjMandatory.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
	// detailObjMandatory.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode()
	// .getParameterCode());
	// detailObjMandatory.setDocId(BigInteger.valueOf(res.getDocumentRestrId()));
	// detailObjMandatory.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument()
	// .getDocumentId()));
	// detailObjMandatory.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_MANDATORY_FIELDLABELID);
	// detailObjMandatory.setNewValue(res.isMandatory() ? AuditConst.PM_AUD_YES
	// : AuditConst.PM_AUD_NO);
	// detailObjMandatory.setOldValue(oldValue.isMandatory() ?
	// AuditConst.PM_AUD_YES
	// : AuditConst.PM_AUD_NO);
	// detailObjMandatory.setActionType(isUpdated(managedBeans.isUpdated()));
	// objList.add(detailObjMandatory);
	// auditPMReqDTO.setObjList(objList);
	// }
	// }
	// }
	// return auditPMReqDTO;
	// }
	//
	// private AuditPMReqDTO
	// updatedPmDocumentRestrList(SupportDocumentManagedBean managedBeans,
	// AuditPMReqDTO auditPMReqDTO, ArrayList<AuditPMSPDTO> objList, String
	// roleCode) {
	// for (PmDocumentRestr pmDocumentRestr :
	// managedBeans.getResDTO().getUpdatedPmDocumentRestrList()) {
	// if (pmDocumentRestr.getProcurementCatCode().getParameterId() != null) {
	// AuditPMSPDTO detailObjProcurementCategory = new AuditPMSPDTO();
	// detailObjProcurementCategory.setRoleCode(roleCode);
	// detailObjProcurementCategory.setDoctype(managedBeans.getResDTO().getDocument().getDocName());
	// detailObjProcurementCategory.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
	// detailObjProcurementCategory.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
	// detailObjProcurementCategory.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
	// detailObjProcurementCategory.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
	// detailObjProcurementCategory.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode()
	// .getParameterCode());
	// detailObjProcurementCategory.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument()
	// .getDocumentId()));
	// detailObjProcurementCategory.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument()
	// .getDocumentId()));
	// detailObjProcurementCategory
	// .setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_PROCUREMENT_CATEGORY_FIELDLABELID);
	// detailObjProcurementCategory.setNewValueId(BigInteger.valueOf(pmDocumentRestr.getProcurementCatCode()
	// .getParameterId()));
	// detailObjProcurementCategory.setActionType(isUpdated(managedBeans.isUpdated()));
	// objList.add(detailObjProcurementCategory);
	// auditPMReqDTO.setObjList(objList);
	// }
	// if (pmDocumentRestr.isMandatory() || !pmDocumentRestr.isMandatory()) {
	// AuditPMSPDTO detailObjMandatory = new AuditPMSPDTO();
	// detailObjMandatory.setRoleCode(roleCode);
	// detailObjMandatory.setDoctype(managedBeans.getResDTO().getDocument().getDocName());
	// detailObjMandatory.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
	// detailObjMandatory.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
	// detailObjMandatory.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
	// detailObjMandatory.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
	// detailObjMandatory.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode()
	// .getParameterCode());
	// detailObjMandatory.setDocId(BigInteger.valueOf(managedBeans.getResDTO().getDocument().getDocumentId()));
	// detailObjMandatory.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument()
	// .getDocumentId()));
	// detailObjMandatory.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_MANDATORY_FIELDLABELID);
	// detailObjMandatory.setNewValue(pmDocumentRestr.isMandatory() ?
	// AuditConst.PM_AUD_YES
	// : AuditConst.PM_AUD_NO);
	// detailObjMandatory.setActionType(isUpdated(managedBeans.isUpdated()));
	// objList.add(detailObjMandatory);
	// auditPMReqDTO.setObjList(objList);
	// }
	// }
	// return auditPMReqDTO;
	// }
	//
	// private AuditPMReqDTO
	// deletedProcurementCatCodeList(SupportDocumentManagedBean managedBeans,
	// AuditPMReqDTO auditPMReqDTO, ArrayList<AuditPMSPDTO> objList, String
	// roleCode) {
	// for (PmDocumentRestr deletepmDocumentRestr :
	// managedBeans.getResDTO().getDeletedProcurementCatCodeList()) {
	// if (deletepmDocumentRestr.getProcurementCatCode().getParameterId() !=
	// null) {
	// AuditPMSPDTO detailObjProcCatDelete = new AuditPMSPDTO();
	// detailObjProcCatDelete.setRoleCode(roleCode);
	// detailObjProcCatDelete.setDoctype(managedBeans.getResDTO().getDocument().getDocName());
	// detailObjProcCatDelete.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
	// detailObjProcCatDelete.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
	// detailObjProcCatDelete.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
	// detailObjProcCatDelete.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
	// detailObjProcCatDelete.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode()
	// .getParameterCode());
	// detailObjProcCatDelete.setDocId(BigInteger.valueOf(deletepmDocumentRestr.getDocumentRestrId()));
	// detailObjProcCatDelete.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument()
	// .getDocumentId()));
	// detailObjProcCatDelete
	// .setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_PROCUREMENT_CATEGORY_FIELDLABELID);
	// detailObjProcCatDelete.setOldValueId(BigInteger.valueOf(deletepmDocumentRestr.getProcurementCatCode()
	// .getParameterId()));
	// detailObjProcCatDelete.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);
	//
	// objList.add(detailObjProcCatDelete);
	// auditPMReqDTO.setObjList(objList);
	// }
	// if (deletepmDocumentRestr.isMandatory() ||
	// !deletepmDocumentRestr.isMandatory()) {
	// AuditPMSPDTO detailObjMandatoryDelete = new AuditPMSPDTO();
	// detailObjMandatoryDelete.setRoleCode(roleCode);
	// detailObjMandatoryDelete.setDoctype(managedBeans.getResDTO().getDocument().getDocName());
	// detailObjMandatoryDelete.setPmParameterLvlId(AuditConst.PM_AUD_QTCHECKLIST_PMPARAMETERLVLID);
	// detailObjMandatoryDelete.setDocNo(managedBeans.getResDTO().getDocument().getDisplaySeq());
	// detailObjMandatoryDelete.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPMC);
	// detailObjMandatoryDelete.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPMC);
	// detailObjMandatoryDelete.setLvlCode3(managedBeans.getSelectedDocument().getDocCategoryCode()
	// .getParameterCode());
	// detailObjMandatoryDelete.setDocId(BigInteger.valueOf(deletepmDocumentRestr.getDocumentRestrId()));
	// detailObjMandatoryDelete.setDocGroupId(BigInteger.valueOf(managedBeans.getResDTO().getDocument()
	// .getDocumentId()));
	// detailObjMandatoryDelete.setFieldLabelCode(AuditConst.PM_AUD_QTCHECKLIST_MANDATORY_FIELDLABELID);
	// detailObjMandatoryDelete.setOldValue(deletepmDocumentRestr.isMandatory()
	// ? AuditConst.PM_AUD_YES
	// : AuditConst.PM_AUD_NO);
	// detailObjMandatoryDelete.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);
	// objList.add(detailObjMandatoryDelete);
	// auditPMReqDTO.setObjList(objList);
	// }
	// }
	// return auditPMReqDTO;
	// }
	//
	// private List<PmDocumentRestr>
	// getMandatoryNewValue(SupportDocumentManagedBean managedBeans) {
	// List<PmDocumentRestr> checklistRestrictionMandatoryNew = new
	// ArrayList<>();
	// for (PmDocumentRestr pmDocumentRestr :
	// managedBeans.getMandatoryUpdatedPmDocumentRestrList()) {
	// if (managedBeans.getSelectedDocument().isByProcureCat() &&
	// pmDocumentRestr.getProcurementCatCode() != null
	// && DbValueConst.RECORD_STATUS_ACTIVE ==
	// pmDocumentRestr.getRecordStatusFlg()) {
	// checklistRestrictionMandatoryNew.add(pmDocumentRestr);
	// }
	// }
	// return checklistRestrictionMandatoryNew;
	// }
	//
	// private List<PmDocumentRestr>
	// getMandatoryOldValue(SupportDocumentManagedBean managedBeans) {
	// List<PmDocumentRestr> checklistRestrictionMandatoryOld = new
	// ArrayList<>();
	// for (PmDocumentRestr pmDocumentRestr :
	// managedBeans.getSelectedDocumentOld().getPmDocumentRestrs()) {
	// if (managedBeans.getSelectedDocument().isByProcureCat() &&
	// pmDocumentRestr.getProcurementCatCode() != null
	// && DbValueConst.RECORD_STATUS_ACTIVE ==
	// pmDocumentRestr.getRecordStatusFlg()) {
	// checklistRestrictionMandatoryOld.add(pmDocumentRestr);
	// }
	// }
	// return checklistRestrictionMandatoryOld;
	// }
	//
	private String isUpdated(boolean updated) {
		if (updated) {
			return AuditConst.PM_AUD_ACTION_TYPE_UPD;
		} else {
			return AuditConst.PM_AUD_ACTION_TYPE_INS;
		}
	}
}