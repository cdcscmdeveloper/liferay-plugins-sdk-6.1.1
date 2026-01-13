package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmPIDReason;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmSupplierReason;
import my.ep.type.pm.ReasonParameterLevel;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.common.rejectionreason.RejectionReasonManagedBean;
import my.ep.web.util.pm.helper.ParameterHelper;

/**
 * AuditPMReasonMaintenanceBackingBean
 * 
 * @author 28895_35509
 */
public class AuditPMReasonMaintenanceBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();

	RejectionReasonManagedBean managedBeans;
	String roleCode;
	String docType;
	String docNo;
	String parameterCode;
	BigInteger parameterLevelId;
	BigInteger docId;
	BigInteger docGroupId;
	String actionType;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleReasonMaintenance();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {

		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		managedBeans = (RejectionReasonManagedBean) manageBean;
		PmReason reason = managedBeans.getSelectedReason();
		this.roleCode = roleCode;
		docNo = reason.getReasonCode();
		this.docType = checkDocType(reason.getPmReasonDescs());
		this.parameterCode = reason.getReasonCategoryCode().getParameterCode();
		parameterLevelId = new BigInteger(ReasonParameterLevel.valueOf(parameterCode).value());

		if (reason.isBrandNew()) {
			PmReason savedReason = managedBeans.getNewPmReason();// new added
			docId = BigInteger.valueOf(savedReason.getReasonId());
			docGroupId = this.docId;
			setNewDescription(reason);

			// create new PID
			if (parameterCode.equalsIgnoreCase(ParameterConst.PURCHASE_IN_DEFAULT)) {
				setNewPIDDetails(savedReason);
			}

			// create new SRR
			if (parameterCode.equalsIgnoreCase(ParameterConst.REASON_CATEGORY_SUPPLIER)) {
				setNewSRRDetails(savedReason);
			}

			AuditPMSPDTO detailStatus = new AuditPMSPDTO();
			detailStatus.setRoleCode(roleCode);
			detailStatus.setDocType(docType);
			detailStatus.setDocNo(docNo);
			detailStatus.setDocGroupId(docGroupId);
			detailStatus.setPmParameterLvlId(parameterLevelId);
			detailStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
			detailStatus.setLvlCode3(parameterCode);
			detailStatus.setDocId(docId);
			detailStatus.setFieldLabelCode(AuditConst.PM_AUD_RC_STATUS_FIELDLABELCODE);
			detailStatus.setNewValue(reason.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailStatus);

		} else {
			PmReason oldReason = managedBeans.getOldPmReason();
			PmReason existingReason = managedBeans.getSelectedReason();
			docId = BigInteger.valueOf(existingReason.getReasonId());
			docGroupId = this.docId;

			if (parameterCode.equalsIgnoreCase(ParameterConst.PURCHASE_IN_DEFAULT)) {
				updatePID(oldReason, existingReason);
				addPIDReason(managedBeans.getAuditAddPIDReasonList());
				// delete reason
				deletePIDReason(managedBeans.getDeletePIDReasonList());
			}

			if (parameterCode.equalsIgnoreCase(ParameterConst.REASON_CATEGORY_SUPPLIER)) {
				updateSRR(oldReason, existingReason);
				addRestriction();
				deleteRestrictionDetail();
			}

			if (!oldReason.getRecordStatusFlg().equals(existingReason.getRecordStatusFlg())) {

				AuditPMSPDTO detailStatus = new AuditPMSPDTO();
				detailStatus.setRoleCode(roleCode);
				detailStatus.setDocType(docType);
				detailStatus.setDocNo(docNo);
				detailStatus.setDocGroupId(docGroupId);
				detailStatus.setPmParameterLvlId(parameterLevelId);
				detailStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				detailStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
				detailStatus.setLvlCode3(parameterCode);
				detailStatus.setDocId(docId);
				detailStatus.setFieldLabelCode(AuditConst.PM_AUD_RC_STATUS_FIELDLABELCODE);
				detailStatus.setOldValue(oldReason.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				detailStatus.setNewValue(existingReason.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				detailStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(detailStatus);
			}

		}
		auditPMReqDTO.setObjList(objList);
		managedBeans.setOldPmReason(null);
		managedBeans.setNewPmReason(null);
		managedBeans.setAuditAddAppTypeRestrictions(null);
		managedBeans.setAuditAddComTypeRestrictions(null);
		managedBeans.setAuditAddSuppTypeRestrictions(null);
		managedBeans.setAuditAddPIDReasonList(null);
		managedBeans.setDeletePIDReasonList(null);
		managedBeans.setAuditDeleteAppRestrictions(null);
		managedBeans.setAuditDeleteComRestrictions(null);
		managedBeans.setAuditDeleteSuppRestrictions(null);
		return auditPMReqDTO;
	}

	private void setNewDescription(PmReason reason) {
		for (PmReasonDesc desc : reason.getPmReasonDescs()) {
			AuditPMSPDTO detailDescription = new AuditPMSPDTO();
			detailDescription.setRoleCode(roleCode);
			detailDescription.setDocType(docType);
			detailDescription.setDocNo(reason.getReasonCode());
			detailDescription.setDocGroupId(docGroupId);
			detailDescription.setPmParameterLvlId(parameterLevelId);
			detailDescription.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailDescription.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
			detailDescription.setLvlCode3(parameterCode);
			detailDescription.setDocId(docId);
			detailDescription
					.setFieldLabelCode(AuditConst.PM_LANGUAGECODE_EN.equals(desc.getLanguageCode()) ? AuditConst.PM_AUD_RC_DESC_EN_FIELDLABELCODE
							: AuditConst.PM_AUD_RC_DESC_BM_FIELDLABELCODE);
			detailDescription.setNewValue(desc.getReasonDesc());
			detailDescription.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailDescription);
		}
	}

	private void setNewPIDDetails(PmReason reason) {
		this.actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
		this.docId = BigInteger.valueOf(reason.getReasonId());

		detailObj(AuditConst.PM_AUD_RC_PURCHASE_FROM_CONTRACT_FIELDLABELCODE, null,
				reason.isContractPurchase() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);

		if (reason.getConditionField() != null) {
			detailObj(AuditConst.PM_AUD_RC_CONTRACT_CIRCULAR_FIELDLABELCODE, null,
					checkPmParameterDesc(reason.getConditionField()));
		}

		if (reason.getSourceMethod() != null) {
			detailObj(AuditConst.PM_AUD_RC_SOURCE_METHOD_FIELDLABELCODE, null,
					checkPmParameterDesc(reason.getSourceMethod()));
		}

		createNewPIDReason(reason.getPmPIDReasonList());
	}

	private void updatePID(PmReason oldReason, PmReason existingReason) {
		if (oldReason.isContractPurchase() != existingReason.isContractPurchase()) {
			AuditPMSPDTO purchaseFromContract = new AuditPMSPDTO();
			purchaseFromContract.setRoleCode(roleCode);
			purchaseFromContract.setDocType(docType);
			purchaseFromContract.setDocNo(existingReason.getReasonCode());
			purchaseFromContract.setDocGroupId(docGroupId);
			purchaseFromContract.setPmParameterLvlId(parameterLevelId);
			purchaseFromContract.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			purchaseFromContract.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
			purchaseFromContract.setLvlCode3(parameterCode);
			purchaseFromContract.setDocId(docId);
			purchaseFromContract.setFieldLabelCode(AuditConst.PM_AUD_RC_PURCHASE_FROM_CONTRACT_FIELDLABELCODE);
			purchaseFromContract.setOldValue(oldReason.isContractPurchase() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			purchaseFromContract.setNewValue(existingReason.isContractPurchase() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			purchaseFromContract.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(purchaseFromContract);
		}

		/** contract or circular criteria */
		contractOrCircularCriteria(oldReason, existingReason);
		/** sourcing method */
		sourcingMethod(oldReason, existingReason);

	}

	private void contractOrCircularCriteria(PmReason oldReason, PmReason existingReason) {

		AuditPMSPDTO contractOrCircular = new AuditPMSPDTO();
		contractOrCircular.setRoleCode(roleCode);
		contractOrCircular.setDocType(docType);
		contractOrCircular.setDocNo(existingReason.getReasonCode());
		contractOrCircular.setDocGroupId(docGroupId);
		contractOrCircular.setPmParameterLvlId(parameterLevelId);
		contractOrCircular.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		contractOrCircular.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
		contractOrCircular.setLvlCode3(parameterCode);
		contractOrCircular.setDocId(docId);
		contractOrCircular.setFieldLabelCode(AuditConst.PM_AUD_RC_CONTRACT_CIRCULAR_FIELDLABELCODE);
		contractOrCircular.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);

		if ((existingReason.getConditionField() != null && existingReason.getConditionField().getParameterId() == null)
				&& (oldReason.getConditionField() != null && oldReason.getConditionField().getParameterId() != null)) {
			contractOrCircular.setOldValue(checkPmParameterDesc(oldReason.getConditionField()));
			contractOrCircular.setOldValueId(BigInteger.valueOf(oldReason.getConditionField().getParameterId()));
			contractOrCircular.setNewValue(null);
			objList.add(contractOrCircular);
		} else if ((existingReason.getConditionField() != null && existingReason.getConditionField().getParameterId() != null)
				&& (oldReason.getConditionField() == null)) {
			contractOrCircular.setOldValue(null);
			contractOrCircular.setNewValue(checkPmParameterDesc(existingReason.getConditionField()));
			contractOrCircular.setNewValueId(BigInteger.valueOf(existingReason.getConditionField().getParameterId()));
			objList.add(contractOrCircular);
		} else if ((existingReason.getConditionField() != null && existingReason.getConditionField().getParameterId() != null)
				&& (oldReason.getConditionField() != null && oldReason.getConditionField().getParameterId() != null)
				&& !existingReason.getConditionField().getParameterId()
						.equals(oldReason.getConditionField().getParameterId())) {
			contractOrCircular.setOldValue(checkPmParameterDesc(oldReason.getConditionField()));
			contractOrCircular.setNewValue(checkPmParameterDesc(existingReason.getConditionField()));
			objList.add(contractOrCircular);

		}
	}

	private void sourcingMethod(PmReason oldReason, PmReason existingReason) {

		PmParameter existingSourcingMethod = existingReason.getSourceMethod();
		PmParameter oldSourcingMethod = oldReason.getSourceMethod();
		AuditPMSPDTO sourcingMethod = new AuditPMSPDTO();

		sourcingMethod.setRoleCode(roleCode);
		sourcingMethod.setDocType(docType);
		sourcingMethod.setDocNo(existingReason.getReasonCode());
		sourcingMethod.setDocGroupId(docGroupId);
		sourcingMethod.setPmParameterLvlId(parameterLevelId);
		sourcingMethod.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		sourcingMethod.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
		sourcingMethod.setLvlCode3(parameterCode);
		sourcingMethod.setDocId(docId);
		sourcingMethod.setFieldLabelCode(AuditConst.PM_AUD_RC_SOURCE_METHOD_FIELDLABELCODE);
		sourcingMethod.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);

		if ((existingSourcingMethod != null && existingSourcingMethod.getParameterId() == null)
				&& (oldSourcingMethod != null && oldSourcingMethod.getParameterId() != null)) {
			sourcingMethod.setOldValue(checkPmParameterDesc(oldSourcingMethod));
			sourcingMethod.setOldValueId(BigInteger.valueOf(oldSourcingMethod.getParameterId()));
			sourcingMethod.setNewValue(null);
			objList.add(sourcingMethod);
		} else if ((existingSourcingMethod != null && existingSourcingMethod.getParameterId() != null)
				&& (oldSourcingMethod == null)) {
			sourcingMethod.setOldValue(null);
			sourcingMethod.setNewValue(checkPmParameterDesc(existingSourcingMethod));
			sourcingMethod.setNewValueId(BigInteger.valueOf(existingSourcingMethod.getParameterId()));
			objList.add(sourcingMethod);

		} else if ((existingSourcingMethod != null && existingSourcingMethod.getParameterId() != null)
				&& (oldSourcingMethod != null && oldSourcingMethod.getParameterId() != null)
				&& !existingSourcingMethod.getParameterId().equals(oldSourcingMethod.getParameterId())) {
			sourcingMethod.setOldValue(checkPmParameterDesc(oldSourcingMethod));
			sourcingMethod.setNewValue(checkPmParameterDesc(existingSourcingMethod));
			objList.add(sourcingMethod);

		}

	}

	// create new SRR
	private void setNewSRRDetails(PmReason pmReason) {
		this.actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
		this.docId = BigInteger.valueOf(pmReason.getReasonId());

		detailObj(AuditConst.PM_AUD_RC_RES_APP_TYPE_FIELDLABELCODE, null,
				pmReason.isByAppType() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);

		detailObj(AuditConst.PM_AUD_RC_RES_BUSS_TYPE_FIELDLABELCODE, null,
				pmReason.isByRegType() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);

		detailObj(AuditConst.PM_AUD_RC_RES_SUPPL_TYPE_FIELDLABELCODE, null,
				pmReason.isBySupplierType() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);

		addRestriction();
	}

	private void updateSRR(PmReason oldReason, PmReason existingReason) {

		if (oldReason.isByAppType() != existingReason.isByAppType()) {
			AuditPMSPDTO restrictionAppType = new AuditPMSPDTO();
			restrictionAppType.setRoleCode(roleCode);
			restrictionAppType.setDocType(docType);
			restrictionAppType.setDocNo(existingReason.getReasonCode());
			restrictionAppType.setDocGroupId(docGroupId);
			restrictionAppType.setPmParameterLvlId(parameterLevelId);
			restrictionAppType.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			restrictionAppType.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
			restrictionAppType.setLvlCode3(parameterCode);
			restrictionAppType.setDocId(BigInteger.valueOf(existingReason.getReasonId()));
			restrictionAppType.setFieldLabelCode(AuditConst.PM_AUD_RC_RES_APP_TYPE_FIELDLABELCODE);
			restrictionAppType.setOldValue(oldReason.isByAppType() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			restrictionAppType.setNewValue(existingReason.isByAppType() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			restrictionAppType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(restrictionAppType);
		}

		if (oldReason.isByRegType() != existingReason.isByRegType()) {
			AuditPMSPDTO restrictionRegType = new AuditPMSPDTO();
			restrictionRegType.setRoleCode(roleCode);
			restrictionRegType.setDocType(docType);
			restrictionRegType.setDocNo(existingReason.getReasonCode());
			restrictionRegType.setDocGroupId(docGroupId);
			restrictionRegType.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode).value()));
			restrictionRegType.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			restrictionRegType.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
			restrictionRegType.setLvlCode3(parameterCode);
			restrictionRegType.setDocId(BigInteger.valueOf(existingReason.getReasonId()));
			restrictionRegType.setFieldLabelCode(AuditConst.PM_AUD_RC_RES_BUSS_TYPE_FIELDLABELCODE);
			restrictionRegType.setOldValue(oldReason.isByRegType() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			restrictionRegType.setNewValue(existingReason.isByRegType() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			restrictionRegType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(restrictionRegType);
		}

		if (oldReason.isBySupplierType() != existingReason.isBySupplierType()) {
			AuditPMSPDTO restrictionSuppType = new AuditPMSPDTO();
			restrictionSuppType.setRoleCode(roleCode);
			restrictionSuppType.setDocType(docType);
			restrictionSuppType.setDocNo(existingReason.getReasonCode());
			restrictionSuppType.setDocGroupId(docGroupId);
			restrictionSuppType.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode).value()));
			restrictionSuppType.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			restrictionSuppType.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
			restrictionSuppType.setLvlCode3(parameterCode);
			restrictionSuppType.setDocId(BigInteger.valueOf(existingReason.getReasonId()));
			restrictionSuppType.setFieldLabelCode(AuditConst.PM_AUD_RC_RES_SUPPL_TYPE_FIELDLABELCODE);
			restrictionSuppType.setOldValue(oldReason.isBySupplierType() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			restrictionSuppType.setNewValue(existingReason.isBySupplierType() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			restrictionSuppType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(restrictionSuppType);
		}

	}

	private void deleteRestrictionDetail() {
		// auditDeleteRestrictions
		if (CollectionUtils.isNotEmpty(managedBeans.getAuditDeleteAppRestrictions())) {
			for (PmSupplierReason suppReason : managedBeans.getAuditDeleteAppRestrictions()) {
				AuditPMSPDTO restrictionDetails = new AuditPMSPDTO();
				restrictionDetails.setRoleCode(roleCode);
				restrictionDetails.setDocType(docType);
				restrictionDetails.setDocNo(docNo);
				restrictionDetails.setDocId(BigInteger.valueOf(suppReason.getSupplierReasonId()));
				restrictionDetails.setDocGroupId(docGroupId);
				restrictionDetails.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode)
						.value()));
				restrictionDetails.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				restrictionDetails.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
				restrictionDetails.setLvlCode3(parameterCode);
				restrictionDetails.setDocId(BigInteger.valueOf(suppReason.getSupplierReasonId()));
				// ST / AT / SBT
				restrictionDetails.setFieldLabelCode(AuditConst.PM_AUD_RC_RES_DTL_APP_TYPE_FIELDLABELCODE);
				restrictionDetails.setOldValue(checkPmParameterDesc(suppReason.getRestrType()));
				restrictionDetails.setOldValueId(BigInteger.valueOf(suppReason.getRestrType().getParameterId()));
				restrictionDetails.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(restrictionDetails);
			}
		}

		if (CollectionUtils.isNotEmpty(managedBeans.getAuditDeleteComRestrictions())) {
			for (PmSupplierReason suppReason : managedBeans.getAuditDeleteComRestrictions()) {
				AuditPMSPDTO restrictionDetails = new AuditPMSPDTO();
				restrictionDetails.setRoleCode(roleCode);
				restrictionDetails.setDocType(docType);
				restrictionDetails.setDocNo(docNo);
				restrictionDetails.setDocId(BigInteger.valueOf(suppReason.getSupplierReasonId()));
				restrictionDetails.setDocGroupId(docGroupId);
				restrictionDetails.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode)
						.value()));
				restrictionDetails.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				restrictionDetails.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
				restrictionDetails.setLvlCode3(parameterCode);
				restrictionDetails.setDocId(BigInteger.valueOf(suppReason.getSupplierReasonId()));
				// ST / AT / SBT
				restrictionDetails.setFieldLabelCode(AuditConst.PM_AUD_RC_RES_DTL_BUSS_TYPE_FIELDLABELCODE);
				restrictionDetails.setOldValue(checkPmParameterDesc(suppReason.getRestrType()));
				restrictionDetails.setOldValueId(BigInteger.valueOf(suppReason.getRestrType().getParameterId()));
				restrictionDetails.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(restrictionDetails);
			}
		}

		if (CollectionUtils.isNotEmpty(managedBeans.getAuditDeleteSuppRestrictions())) {
			for (PmSupplierReason suppReason : managedBeans.getAuditDeleteSuppRestrictions()) {
				AuditPMSPDTO restrictionDetails = new AuditPMSPDTO();
				restrictionDetails.setRoleCode(roleCode);
				restrictionDetails.setDocType(docType);
				restrictionDetails.setDocNo(docNo);
				restrictionDetails.setDocId(BigInteger.valueOf(suppReason.getSupplierReasonId()));
				restrictionDetails.setDocGroupId(docGroupId);
				restrictionDetails.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode)
						.value()));
				restrictionDetails.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				restrictionDetails.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
				restrictionDetails.setLvlCode3(parameterCode);
				
				// ST / AT / SBT
				restrictionDetails.setFieldLabelCode(AuditConst.PM_AUD_RC_RES_DTL_SUPPL_TYPE_FIELDLABELCODE);
				restrictionDetails.setOldValue(checkPmParameterDesc(suppReason.getRestrType()));
				restrictionDetails.setOldValueId(BigInteger.valueOf(suppReason.getRestrType().getParameterId()));
				restrictionDetails.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(restrictionDetails);
			}
		}
	}

	private void addRestriction() {
		if (CollectionUtils.isNotEmpty(managedBeans.getAuditAddAppTypeRestrictions())) {
			for (PmSupplierReason suppReason : managedBeans.getAuditAddAppTypeRestrictions()) {
				AuditPMSPDTO restrictionDetails = new AuditPMSPDTO();
				restrictionDetails.setRoleCode(roleCode);
				restrictionDetails.setDocType(docType);
				restrictionDetails.setDocNo(docNo);
				restrictionDetails.setDocGroupId(docGroupId);
				restrictionDetails.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode)
						.value()));
				restrictionDetails.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				restrictionDetails.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
				restrictionDetails.setLvlCode3(parameterCode);
				restrictionDetails.setDocId(checkDocIdSupplierReason(suppReason.getRestrType().getParameterId()));
				// ST / AT / SBT
				restrictionDetails.setFieldLabelCode(AuditConst.PM_AUD_RC_RES_DTL_APP_TYPE_FIELDLABELCODE);
				restrictionDetails.setNewValue(checkPmParameterDesc(suppReason.getRestrType()));
				restrictionDetails.setNewValueId(BigInteger.valueOf(suppReason.getRestrType().getParameterId()));
				restrictionDetails.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(restrictionDetails);
			}
		}

		if (CollectionUtils.isNotEmpty(managedBeans.getAuditAddComTypeRestrictions())) {
			for (PmSupplierReason suppReason : managedBeans.getAuditAddComTypeRestrictions()) {
				AuditPMSPDTO restrictionDetails = new AuditPMSPDTO();
				restrictionDetails.setRoleCode(roleCode);
				restrictionDetails.setDocType(docType);
				restrictionDetails.setDocNo(docNo);
				restrictionDetails.setDocGroupId(docGroupId);
				restrictionDetails.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode)
						.value()));
				restrictionDetails.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				restrictionDetails.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
				restrictionDetails.setLvlCode3(parameterCode);
				restrictionDetails.setDocId(checkDocIdSupplierReason(suppReason.getRestrType().getParameterId()));
				// ST / AT / SBT
				restrictionDetails.setFieldLabelCode(AuditConst.PM_AUD_RC_RES_DTL_BUSS_TYPE_FIELDLABELCODE);
				restrictionDetails.setNewValue(checkPmParameterDesc(suppReason.getRestrType()));
				restrictionDetails.setNewValueId(BigInteger.valueOf(suppReason.getRestrType().getParameterId()));
				restrictionDetails.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(restrictionDetails);
			}
		}

		if (CollectionUtils.isNotEmpty(managedBeans.getAuditAddSuppTypeRestrictions())) {
			for (PmSupplierReason suppReason : managedBeans.getAuditAddSuppTypeRestrictions()) {
				AuditPMSPDTO restrictionDetails = new AuditPMSPDTO();
				restrictionDetails.setRoleCode(roleCode);
				restrictionDetails.setDocType(docType);
				restrictionDetails.setDocNo(docNo);
				restrictionDetails.setDocGroupId(docGroupId);
				restrictionDetails.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode)
						.value()));
				restrictionDetails.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				restrictionDetails.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
				restrictionDetails.setLvlCode3(parameterCode);
				restrictionDetails.setDocId(checkDocIdSupplierReason(suppReason.getRestrType().getParameterId()));
				// ST / AT / SBT
				restrictionDetails.setFieldLabelCode(AuditConst.PM_AUD_RC_RES_DTL_SUPPL_TYPE_FIELDLABELCODE);
				restrictionDetails.setNewValue(checkPmParameterDesc(suppReason.getRestrType()));
				restrictionDetails.setNewValueId(BigInteger.valueOf(suppReason.getRestrType().getParameterId()));
				restrictionDetails.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(restrictionDetails);
			}
		}

	}

	private void addPIDReason(List<PmPIDReason> pidReasonList) {
		if (CollectionUtils.isNotEmpty(pidReasonList)) {
			for (PmPIDReason pidReason : pidReasonList) {
				if (pidReason.isBrandNew()) {
					AuditPMSPDTO reasonApplicability = new AuditPMSPDTO();
					reasonApplicability.setRoleCode(roleCode);
					reasonApplicability.setDocType(docType);
					reasonApplicability.setDocNo(docNo);
					reasonApplicability.setDocGroupId(docGroupId);
					reasonApplicability.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode)
							.value()));
					reasonApplicability.setLvlCode1(AuditConst.PM_LVL_CODE_1);
					reasonApplicability.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
					reasonApplicability.setLvlCode3(parameterCode);
					reasonApplicability.setDocId(checkDocIdPIDReason(pidReason.getDocType()));
					reasonApplicability.setFieldLabelCode(AuditConst.PM_AUD_RC_REASON_APPLICABILITY_FIELDLABELCODE);
					reasonApplicability.setNewValue(checkPIDReason(pidReason.getDocType()));
					reasonApplicability.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
					objList.add(reasonApplicability);
				}
			}
		}
	}

	private void createNewPIDReason(List<PmPIDReason> pidReasonList) {
		if (CollectionUtils.isNotEmpty(pidReasonList)) {
			for (PmPIDReason pidReason : pidReasonList) {
				AuditPMSPDTO reasonApplicability = new AuditPMSPDTO();
				reasonApplicability.setRoleCode(roleCode);
				reasonApplicability.setDocType(docType);
				reasonApplicability.setDocNo(docNo);
				reasonApplicability.setDocGroupId(docGroupId);
				reasonApplicability.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode)
						.value()));
				reasonApplicability.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				reasonApplicability.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
				reasonApplicability.setLvlCode3(parameterCode);
				reasonApplicability.setDocId(BigInteger.valueOf(pidReason.getPidReasonId()));
				reasonApplicability.setFieldLabelCode(AuditConst.PM_AUD_RC_REASON_APPLICABILITY_FIELDLABELCODE);
				reasonApplicability.setNewValue(checkPIDReason(pidReason.getDocType()));
				reasonApplicability.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(reasonApplicability);
			}
		}
	}

	private void deletePIDReason(List<PmPIDReason> pidReasonList) {
		if (CollectionUtils.isNotEmpty(pidReasonList)) {
			for (PmPIDReason pidReason : pidReasonList) {
				AuditPMSPDTO reasonApplicability = new AuditPMSPDTO();
				reasonApplicability.setRoleCode(roleCode);
				reasonApplicability.setDocType(docType);
				reasonApplicability.setDocNo(docNo);
				reasonApplicability.setDocGroupId(docGroupId);
				reasonApplicability.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode)
						.value()));
				reasonApplicability.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				reasonApplicability.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
				reasonApplicability.setLvlCode3(parameterCode);
				reasonApplicability.setDocId(BigInteger.valueOf(pidReason.getPidReasonId()));
				reasonApplicability.setFieldLabelCode(AuditConst.PM_AUD_RC_REASON_APPLICABILITY_FIELDLABELCODE);
				reasonApplicability.setOldValue(checkPIDReason(pidReason.getDocType()));
				reasonApplicability.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(reasonApplicability);
			}
		}
	}

	/**
	 */
	private String checkPIDReason(String pidDocType) {

		PmParameter pmParameter = getParameterByTypeAndCode("DT", pidDocType);
		String description = null;

		for (PmParameterDesc desc : pmParameter.getPmParameterDescsSpecCallOnly()) {
			if (desc.getLanguageCode().equalsIgnoreCase(AuditConst.PM_LANGUAGECODE_MS)) {
				description = desc.getCodeName();
			}
		}
		return description;

	}

	private String checkDocType(List<PmReasonDesc> pmReasonDescs) {

		String description = null;

		for (PmReasonDesc desc : pmReasonDescs) {
			if (desc.getLanguageCode().equalsIgnoreCase(AuditConst.PM_LANGUAGECODE_EN)) {
				description = desc.getReasonDesc();
			}
		}

		return description;
	}

	private String checkPmParameterDesc(PmParameter pmParameter) {
		String description = null;

		if (pmParameter.getParameterId() != null) {
			PmParameter parameter = ParameterHelper.getInstance().getPmParameterById(pmParameter.getParameterId());
			for (PmParameterDesc desc : parameter.getPmParameterDescsSpecCallOnly()) {
				if (desc.getLanguageCode().equalsIgnoreCase(AuditConst.PM_LANGUAGECODE_MS)) {
					description = desc.getCodeName();
				}
			}
		}

		return description;

	}

	private BigInteger checkDocIdPIDReason(String pidReason) {
		PmReason pmReason = managedBeans.getSelectedReason();
		BigInteger docId = null;

		for (PmPIDReason pmPIDReason : pmReason.getPmPIDReasonList()) {
			if (pmPIDReason.getDocType().equals(pidReason)) {
				docId = BigInteger.valueOf(pmPIDReason.getPidReasonId());
				break;
			}
		}

		return docId;
	}

	private BigInteger checkDocIdSupplierReason(Long pmParameterId) {
		PmReason pmReason = managedBeans.getNewPmReason() == null ? managedBeans.getSelectedReason() : managedBeans.getNewPmReason();
		BigInteger docId = null;

		for (PmSupplierReason pmSupplierReason : pmReason.getPmSupplierReasons()) {
			if (pmSupplierReason.getRestrType().getParameterId().equals(pmParameterId)
					&& pmSupplierReason.getRecordStatusFlg().equals(AuditConst.PM_AUD_ACTIVE1)) {
				docId = BigInteger.valueOf(pmSupplierReason.getSupplierReasonId());
				break;
			}
		}

		return docId;
	}

	/**
	 * 
	 * @param fieldLabelId
	 * @param oldValue
	 * @param newValue
	 * @return
	 */
	private ArrayList<AuditPMSPDTO> detailObj(BigInteger fieldLabelId, String oldValue, String newValue) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(docType);
		dtlObj.setDocNo(docNo);
		dtlObj.setPmParameterLvlId(parameterLevelId);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
		dtlObj.setLvlCode3(parameterCode);
		dtlObj.setDocId(docId);
		dtlObj.setDocGroupId(docGroupId);
		dtlObj.setFieldLabelCode(fieldLabelId);
		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}
		dtlObj.setNewValue(newValue);
		dtlObj.setActionType(actionType);
		objList.add(dtlObj);
		return objList;
	}
}
