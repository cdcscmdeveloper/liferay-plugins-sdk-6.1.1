package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.authorization.PmPtjSpecialApproval;
import my.ep.persistence.entity.pm.fee.settings.PmFeeExemption;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.settings.RuleCatalogueManageBean;

/**
 * AuditPMSysParamBackingBean #28895-35509
 * 
 * @author normuliana
 * 
 */
public class AuditPMSysParamBackingBean extends AuditPMBackingBeanTemplate {
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleSysParam();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		RuleCatalogueManageBean managedBeans = (RuleCatalogueManageBean) manageBean;
		if (CollectionUtils.isNotEmpty(managedBeans.getResDTO().getFeeExemptions())
				&& CollectionUtils.isNotEmpty(managedBeans.getResDTOOld().getFeeExemptions())) {
			yepProcessFeeExemption(managedBeans, roleCode);
		}
		if (CollectionUtils.isNotEmpty(managedBeans.getAuditMainPtjSpecialApprovalList())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditCloneMainPtjSpecialApprovalList())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditDeletedMainPtjSpecialApprovalList())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditMainPtjSpecialApprovalListNewVal())) {
			qtTenderValues(managedBeans.getAuditMainPtjSpecialApprovalList(),
					managedBeans.getAuditCloneMainPtjSpecialApprovalList(),
					managedBeans.getAuditDeletedMainPtjSpecialApprovalList(),
					managedBeans.getAuditMainPtjSpecialApprovalListNewVal(), roleCode);
		}

		managedBeans.getResDTO().setFeeExemptions(null);
		managedBeans.getResDTOOld().setFeeExemptions(null);
		managedBeans.setAuditMainPtjSpecialApprovalList(null);
		managedBeans.setAuditCloneMainPtjSpecialApprovalList(null);
		managedBeans.setAuditDeletedMainPtjSpecialApprovalList(null);
		managedBeans.setAuditMainPtjSpecialApprovalListNewVal(null);
		return auditPMReqDTO;
	}

	/**
	 * @param auditMainPtjSpecialApprovalList
	 * @param clone
	 * @param deletedItem
	 * @param newCreatedItem
	 * @param roleCode
	 */
	public void qtTenderValues(List<PmPtjSpecialApproval> auditMainPtjSpecialApprovalList, List<PmPtjSpecialApproval> clone,
			List<PmPtjSpecialApproval> deletedItem, List<PmPtjSpecialApproval> newCreatedItem, String roleCode) {
		for (PmPtjSpecialApproval approval : auditMainPtjSpecialApprovalList) {
			PmPtjSpecialApproval matchingApprovalUpdated = findMatchingQtValues(clone, approval);
			if (matchingApprovalUpdated != null) {
				processQtyValuesUpdate(approval, matchingApprovalUpdated, roleCode);
			}
		}
		if (deletedItem != null) {
			for (PmPtjSpecialApproval deleted : deletedItem) {
				processQtyValuesCreateDeleted(null, deleted, roleCode);
			}
		}
		if (newCreatedItem != null) {
			for (PmPtjSpecialApproval newCreate : newCreatedItem) {
				processQtyValuesCreateDeleted(newCreate, null, roleCode);
			}
		}
		// auditPMReqDTO.setObjList(objList);
	}

	/**
	 * @param clone
	 * @param approval
	 * @return
	 */
	public PmPtjSpecialApproval findMatchingQtValues(List<PmPtjSpecialApproval> clone, PmPtjSpecialApproval approval) {
		for (PmPtjSpecialApproval approvalClone : clone) {
			if (approval.getPtjSpecialApprovalId().equals(approvalClone.getPtjSpecialApprovalId())) {
				return approvalClone;
			}
		}
		return null; // No match found
	}

	/**
	 * @param approval
	 * @param clone
	 * @param roleCode
	 */
	public void processQtyValuesUpdate(PmPtjSpecialApproval approval, PmPtjSpecialApproval clone, String roleCode) {
		if (clone != null) {/* Update */
			if (approval.getExpDate() != null && clone.getExpDate() != null && !approval.getExpDate().equals(clone.getExpDate())) {
				qtValuesAddUpdateDeleteAuditDetail(approval, roleCode, AuditConst.PM_AUD_SYSPARAM_SPQTV_EXPDATE_FIELDLABELID,
						clone.getExpDate().toString(), approval.getExpDate().toString(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
			if (approval.getEffDate() != null && clone.getEffDate() != null && !approval.getEffDate().equals(clone.getEffDate())) {
				qtValuesAddUpdateDeleteAuditDetail(approval, roleCode, AuditConst.PM_AUD_SYSPARAM_SPQTV_EFFDATE_FIELDLABELID,
						clone.getEffDate().toString(), approval.getEffDate().toString(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
			if (approval.getProcureAmt() != null && clone.getProcureAmt() != null
					&& !approval.getProcureAmt().equals(clone.getProcureAmt())) {
				qtValuesAddUpdateDeleteAuditDetail(approval, roleCode, AuditConst.PM_AUD_SYSPARAM_SPQTV_EFFDATE_FIELDLABELID,
						clone.getProcureAmt().toString(), approval.getProcureAmt().toString(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
		}
	}

	/**
	 * @param newCreatedItem
	 * @param deletedItem
	 * @param roleCode
	 */
	public void processQtyValuesCreateDeleted(PmPtjSpecialApproval newCreatedItem, PmPtjSpecialApproval deletedItem,
			String roleCode) {
		PmPtjSpecialApproval pmPtjSpecialApproval = newCreatedItem != null ? newCreatedItem : deletedItem;
		handleFieldAudit(pmPtjSpecialApproval, roleCode, AuditConst.PM_AUD_SYSPARAM_SPQTV_EFFDATE_FIELDLABELID,
				retrieverCreateDeleteEffDateVal(newCreatedItem, null), retrieverCreateDeleteEffDateVal(null, deletedItem));

		handleFieldAudit(pmPtjSpecialApproval, roleCode, AuditConst.PM_AUD_SYSPARAM_SPQTV_EXPDATE_FIELDLABELID,
				retrieverCreateDeleteExpDateVal(newCreatedItem, null), retrieverCreateDeleteExpDateVal(null, deletedItem));

		handleFieldAudit(pmPtjSpecialApproval, roleCode, AuditConst.PM_AUD_SYSPARAM_SPQTV_LIMIT_FIELDLABELID,
				retrieverCreateDeleteAmtVal(newCreatedItem, null), retrieverCreateDeleteAmtVal(null, deletedItem));

		handleFieldAudit(pmPtjSpecialApproval, roleCode, AuditConst.PM_AUD_SYSPARAM_SPQTV_PTJNAME_FIELDLABELID,
				retrieverCreateDeleteOrgNameVal(newCreatedItem, null), retrieverCreateDeleteOrgNameVal(null, deletedItem));

		handleFieldAudit(pmPtjSpecialApproval, roleCode, AuditConst.PM_AUD_SYSPARAM_SPQTV_PTJCODE_FIELDLABELID,
				retrieverCreateDeleteOrgCodeVal(newCreatedItem, null), retrieverCreateDeleteOrgCodeVal(null, deletedItem));

		handleFieldAudit(pmPtjSpecialApproval, roleCode, AuditConst.PM_AUD_SYSPARAM_SPQTV_MINISTRY_FIELDLABELID,
				retrieverCreateDeleteMinistryNameVal(newCreatedItem, null),
				retrieverCreateDeleteMinistryNameVal(null, deletedItem));
	}

	/**
	 * @param approval
	 * @param deletedItem
	 * @return
	 */
	public String retrieverCreateDeleteEffDateVal(PmPtjSpecialApproval approval, PmPtjSpecialApproval deletedItem) {
		if (approval != null && approval.getEffDate() != null) {
			return approval.getEffDate().toString();
		}
		if (deletedItem != null && deletedItem.getEffDate() != null) {
			return deletedItem.getEffDate().toString();
		}
		return null;
	}

	/**
	 * @param approval
	 * @param deletedItem
	 * @return
	 */
	public String retrieverCreateDeleteExpDateVal(PmPtjSpecialApproval approval, PmPtjSpecialApproval deletedItem) {
		if (approval != null && approval.getExpDate() != null) {
			return approval.getExpDate().toString();
		}
		if (deletedItem != null && deletedItem.getExpDate() != null) {
			return deletedItem.getExpDate().toString();
		}
		return null;
	}

	/**
	 * @param approval
	 * @param deletedItem
	 * @return
	 */
	public String retrieverCreateDeleteAmtVal(PmPtjSpecialApproval approval, PmPtjSpecialApproval deletedItem) {
		if (approval != null && approval.getProcureAmt() != null) {
			return approval.getProcureAmt().toString();
		}
		if (deletedItem != null && deletedItem.getProcureAmt() != null) {
			return deletedItem.getProcureAmt().toString();
		}
		return null;
	}

	/**
	 * @param approval
	 * @param deletedItem
	 * @return
	 */
	public String retrieverCreateDeleteOrgNameVal(PmPtjSpecialApproval approval, PmPtjSpecialApproval deletedItem) {
		if (approval != null && approval.getPmOrgProfile() != null && approval.getPmOrgProfile().getOrgValidity() != null) {
			return approval.getPmOrgProfile().getOrgValidity().getOrgName();
		}
		if (deletedItem != null && deletedItem.getPmOrgProfile() != null
				&& deletedItem.getPmOrgProfile().getOrgValidity() != null) {
			return deletedItem.getPmOrgProfile().getOrgValidity().getOrgName();
		}
		return null;
	}

	/**
	 * @param approval
	 * @param deletedItem
	 * @return
	 */
	public String retrieverCreateDeleteOrgCodeVal(PmPtjSpecialApproval approval, PmPtjSpecialApproval deletedItem) {
		if (approval != null && approval.getPmOrgProfile().getOrgValidity() != null) {
			return approval.getPmOrgProfile().getOrgValidity().getOrgCode();
		}
		if (deletedItem != null && deletedItem.getPmOrgProfile().getOrgValidity() != null) {
			return deletedItem.getPmOrgProfile().getOrgValidity().getOrgCode();
		}
		return null;
	}

	/**
	 * @param approval
	 * @param deletedItem
	 * @return
	 */
	public String retrieverCreateDeleteMinistryNameVal(PmPtjSpecialApproval approval, PmPtjSpecialApproval deletedItem) {
		if (approval != null && approval.getPmOrgProfile() != null) {
			return approval.getPmOrgProfile().getMinistryName();
		}
		if (deletedItem != null && deletedItem.getPmOrgProfile() != null) {
			return deletedItem.getPmOrgProfile().getMinistryName();
		}
		return null;
	}

	private void handleFieldAudit(PmPtjSpecialApproval approval, String roleCode, BigInteger fieldLabelId, String newValue,
			String oldValue) {
		if (newValue != null || oldValue != null) {
			String actionType = newValue != null ? AuditConst.PM_AUD_ACTION_TYPE_INS : AuditConst.PM_AUD_ACTION_TYPE_DEL;
			qtValuesAddUpdateDeleteAuditDetail(approval, roleCode, fieldLabelId, oldValue != null ? oldValue : null,
					newValue != null ? newValue : null, actionType);
		}
	}

	/**
	 * @param approval
	 * @param roleCode
	 * @param fieldLabelCode
	 * @param oldValue
	 * @param newValue
	 * @param actionType
	 */
	public void qtValuesAddUpdateDeleteAuditDetail(PmPtjSpecialApproval approval, String roleCode, BigInteger fieldLabelCode,
			String oldValue, String newValue, String actionType) {
		addAuditDetail(roleCode, AuditConst.PM_AUD_SPQTV_PARAM_CODE, AuditConst.PM_AUD_SPQTV_PMPARAMETERLVLID,
				BigInteger.valueOf(approval.getPtjSpecialApprovalId()), AuditConst.PM_LVL_CODE_3_SPQTV,
				AuditConst.PM_LVL_CODE_2_SPQTV, fieldLabelCode, oldValue, newValue, actionType);
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 */
	public void yepProcessFeeExemption(RuleCatalogueManageBean managedBeans, String roleCode) {
		for (PmFeeExemption exemption : managedBeans.getResDTO().getFeeExemptions()) {
			PmFeeExemption matchingExemption = yepFindMatchingExemption(managedBeans.getResDTOOld().getFeeExemptions(), exemption);

			if (matchingExemption != null) {
				yepProcessIfChanged(exemption, matchingExemption, roleCode);
			}
		}
		auditPMReqDTO.setObjList(objList);
	}

	/**
	 * @param oldExemptions
	 * @param exemption
	 * @return
	 */
	public PmFeeExemption yepFindMatchingExemption(List<PmFeeExemption> oldExemptions, PmFeeExemption exemption) {
		for (PmFeeExemption exemptionOld : oldExemptions) {
			if (exemption.getFeeExemptionId().equals(exemptionOld.getFeeExemptionId())) {
				return exemptionOld;
			}
		}
		return null; // No match found
	}

	/**
	 * @param exemption
	 * @param exemptionOld
	 * @param roleCode
	 */
	public void yepProcessIfChanged(PmFeeExemption exemption, PmFeeExemption exemptionOld, String roleCode) {
		boolean isEffDateChanged = !exemption.getEffDate().equals(exemptionOld.getEffDate());
		boolean isExpDateChanged = !exemption.getExpDate().equals(exemptionOld.getExpDate());

		if (isEffDateChanged || isExpDateChanged) {
			yepProcessBasedOnParamCode(exemption, exemptionOld, roleCode);
		}
	}

	/**
	 * @param exemption
	 * @param exemptionOld
	 * @param roleCode
	 */
	public void yepProcessBasedOnParamCode(PmFeeExemption exemption, PmFeeExemption exemptionOld, String roleCode) {
		String paramCode = exemption.getOrgTypeId().getParameterCode();
		switch (paramCode) {
		case "7":
			yepProcessFeeExemption(exemption, exemptionOld, AuditConst.PM_LVL_CODE_3_YEP3, roleCode,
					AuditConst.PM_AUD_YEP3_PMPARAMETERLVLID);
			break;
		case "8":
			yepProcessFeeExemption(exemption, exemptionOld, AuditConst.PM_LVL_CODE_3_YEP5, roleCode,
					AuditConst.PM_AUD_YEP5_PMPARAMETERLVLID);
			break;
		case "9":
			yepProcessFeeExemption(exemption, exemptionOld, AuditConst.PM_LVL_CODE_3_YEP6, roleCode,
					AuditConst.PM_AUD_YEP6_PMPARAMETERLVLID);
			break;
		default:
			// Handle unexpected paramCode if necessary
		}
	}

	/*
	 * Helper method to handle the repetitive creation and setting of
	 * AuditPMSPDTO objects for YEP
	 */
	/**
	 * @param exemption
	 * @param exemptionOld
	 * @param lvlCode
	 * @param roleCode
	 * @param lvlId
	 */
	public void yepProcessFeeExemption(PmFeeExemption exemption, PmFeeExemption exemptionOld, String lvlCode, String roleCode,
			BigInteger lvlId) {
		if (exemption.getEffDate() != null && !exemption.getEffDate().equals(exemptionOld.getEffDate())) {
			yepAddAuditDetail(exemption, roleCode, lvlCode, AuditConst.PM_AUD_SYSPARAMETER_EFFDATE_FIELDLABELID, exemptionOld
					.getEffDate().toString(), exemption.getEffDate().toString(), lvlId);
		}
		if (exemption.getExpDate() != null && !exemption.getExpDate().equals(exemptionOld.getExpDate())) {
			yepAddAuditDetail(exemption, roleCode, lvlCode, AuditConst.PM_AUD_SYSPARAMETER_EXPDATE_FIELDLABELID, exemptionOld
					.getExpDate().toString(), exemption.getExpDate().toString(), lvlId);
		}
	}

	/* Separate helper to create and add AuditPMSPDTO to the list */
	/**
	 * @param exemption
	 * @param roleCode
	 * @param lvlCode
	 * @param fieldLabelCode
	 * @param oldValue
	 * @param newValue
	 * @param lvlId
	 */
	public void yepAddAuditDetail(PmFeeExemption exemption, String roleCode, String lvlCode, BigInteger fieldLabelCode,
			String oldValue, String newValue, BigInteger lvlId) {
		addAuditDetail(roleCode, exemption.getOrgTypeId().getPmParameterDescsSpecCallOnly().get(1).getCodeName(), lvlId,
				BigInteger.valueOf(exemption.getFeeExemptionId()), lvlCode, AuditConst.PM_LVL_CODE_2_SPYEP, fieldLabelCode,
				oldValue, newValue, AuditConst.PM_AUD_ACTION_TYPE_UPD);
	}

	/**
	 * @param roleCode
	 * @param codeName
	 * @param lvlId
	 * @param docId
	 * @param lvlCode
	 * @param lvlCode2
	 * @param fieldLabelCode
	 * @param oldValue
	 * @param newValue
	 */
	public void addAuditDetail(String roleCode, String codeName, BigInteger lvlId, BigInteger docId, String lvlCode,
			String lvlCode2, BigInteger fieldLabelCode, String oldValue, String newValue, String actionType) {
		AuditPMSPDTO detailObj = new AuditPMSPDTO();
		detailObj.setRoleCode(roleCode);
		detailObj.setDocType(codeName);
		detailObj.setPmParameterLvlId(lvlId);
		detailObj.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		detailObj.setLvlCode2(lvlCode2);
		detailObj.setDocId(docId);
		detailObj.setActionType(actionType);
		detailObj.setLvlCode3(lvlCode);
		detailObj.setFieldLabelCode(fieldLabelCode);
		detailObj.setOldValue(oldValue);
		detailObj.setNewValue(newValue);
		objList.add(detailObj);
		auditPMReqDTO.setObjList(objList);
	}
}