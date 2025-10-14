package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
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
 * AuditPMSysParamBackingBean #28895-35509 - GATE3
 * 
 * @author normuliana
 * 
 */
public class AuditPMSysParamBackingBean extends AuditPMBackingBeanTemplate {
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	String roleCode = null;
	String lvlCode2 = null;
	BigInteger docId;
	String pattern = "dd/MM/yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

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
		AuditPMSysParamTransFeeExemptBackingBean auditPMSysParamTransFeeExemptBackingBean = new AuditPMSysParamTransFeeExemptBackingBean();
		AuditPMSysParamConstantRulesBackingBean auditPMSysParamConstantRulesBackingBean = new AuditPMSysParamConstantRulesBackingBean();
		this.roleCode = roleCode;
		RuleCatalogueManageBean managedBeans = (RuleCatalogueManageBean) manageBean;

		// *** TAB System Param Year End Process ***//
		if (CollectionUtils.isNotEmpty(managedBeans.getResDTO().getFeeExemptions())
				&& CollectionUtils.isNotEmpty(managedBeans.getResDTOOld().getFeeExemptions())) {
			lvlCode2 = AuditConst.PM_LVL_CODE_2_SPYEP;
			yepProcessFeeExemption(managedBeans);
		}
		// *** TAB System Param Quotation Values ***//
		if (CollectionUtils.isNotEmpty(managedBeans.getAuditMainPtjSpecialApprovalList())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditUpdateMainPtjSpecialApprovalList())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditDeletedMainPtjSpecialApprovalList())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditMainPtjSpecialApprovalListNewVal())) {
			lvlCode2 = AuditConst.PM_LVL_CODE_2_SPQTV;
			qtTenderValues(managedBeans.getAuditMainPtjSpecialApprovalList(),
					managedBeans.getAuditUpdateMainPtjSpecialApprovalList(),
					managedBeans.getAuditDeletedMainPtjSpecialApprovalList(),
					managedBeans.getAuditMainPtjSpecialApprovalListNewVal());
		}
		if (managedBeans.getAuditLevelCode() != null) {
			// *** TAB System Param Transaction Fee Exemption ***//
			if (managedBeans.getAuditLevelCode().equalsIgnoreCase(AuditConst.PM_LVL_CODE_2_SPFEE)) {
				auditPMReqDTO = auditPMSysParamTransFeeExemptBackingBean.spTFeeProcess(managedBeans, roleCode);
			}
			// *** TAB System Param Constant Rule ***//
			if (managedBeans.getAuditLevelCode().equalsIgnoreCase(AuditConst.PM_LVL_CODE_2_SPCONSTANTRULE)) {
				auditPMReqDTO = auditPMSysParamConstantRulesBackingBean.spRulesValueTypeProcess(managedBeans, roleCode);
			}
		}

		clearAllAuditBeanVal(managedBeans);
		return auditPMReqDTO;
	}

	/**
	 * @param auditMainPtjSpecialApprovalList
	 * @param update
	 * @param deletedItem
	 * @param newCreatedItem
	 * @param roleCode
	 */
	public void qtTenderValues(List<PmPtjSpecialApproval> auditMainPtjSpecialApprovalList, List<PmPtjSpecialApproval> update,
			List<PmPtjSpecialApproval> deletedItem, List<PmPtjSpecialApproval> newCreatedItem) {
		for (PmPtjSpecialApproval updateVal : update) {
			PmPtjSpecialApproval matchingApprovalUpdated = findMatchingQtValues(auditMainPtjSpecialApprovalList, updateVal);
			if (matchingApprovalUpdated != null) {
				processQtyValuesUpdate(matchingApprovalUpdated, updateVal);
			}
		}
		if (CollectionUtils.isNotEmpty(deletedItem)) {
			for (PmPtjSpecialApproval deleted : deletedItem) {
				processQtyValuesCreateDeleted(null, deleted);
			}
		}
		if (CollectionUtils.isNotEmpty(newCreatedItem)) {
			for (PmPtjSpecialApproval newCreate : newCreatedItem) {
				processQtyValuesCreateDeleted(newCreate, null);
			}
		}
	}

	/**
	 * @param update
	 * @param approval
	 * @return
	 */
	public PmPtjSpecialApproval findMatchingQtValues(List<PmPtjSpecialApproval> update, PmPtjSpecialApproval approval) {
		for (PmPtjSpecialApproval updatedVal : update) {
			if (approval.getPtjSpecialApprovalId().equals(updatedVal.getPtjSpecialApprovalId())) {
				return updatedVal;
			}
		}
		return null; // No match found
	}

	/**
	 * @param approval
	 * @param updateVal
	 * @param roleCode
	 */
	public void processQtyValuesUpdate(PmPtjSpecialApproval approval, PmPtjSpecialApproval updateVal) {
		/* Update */
		if (updateVal.getProcureAmt() != null && !approval.getProcureAmt().equals(updateVal.getProcureAmt())) {
			qtValuesAddUpdateDeleteAuditDetail(approval, AuditConst.PM_AUD_SYSPARAM_SPQTV_LIMIT_FIELDLABELID, updateVal
					.getProcureAmt().toString(), approval.getProcureAmt().toString(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}
		if (updateVal.getEffDate() != null && !approval.getEffDate().equals(updateVal.getEffDate())) {
			qtValuesAddUpdateDeleteAuditDetail(approval, AuditConst.PM_AUD_SYSPARAM_SPQTV_EFFDATE_FIELDLABELID, 
					simpleDateFormat.format(updateVal.getEffDate()), simpleDateFormat.format(approval.getEffDate()), AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}
		if ((updateVal.getExpDate() == null && approval.getExpDate() != null) || updateVal.getExpDate() != null
				&& !approval.getExpDate().equals(updateVal.getExpDate())) {
			qtValuesAddUpdateDeleteAuditDetail(approval, AuditConst.PM_AUD_SYSPARAM_SPQTV_EXPDATE_FIELDLABELID,
					updateVal.getExpDate() != null ? simpleDateFormat.format(updateVal.getExpDate()) : null, simpleDateFormat.format(approval.getExpDate()),
					AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}
	}

	/**
	 * @param newCreatedItem
	 * @param deletedItem
	 * @param roleCode
	 */
	public void processQtyValuesCreateDeleted(PmPtjSpecialApproval newCreatedItem, PmPtjSpecialApproval deletedItem) {
		PmPtjSpecialApproval pmPtjSpecialApproval = newCreatedItem != null ? newCreatedItem : deletedItem;
		handleFieldAudit(pmPtjSpecialApproval, AuditConst.PM_AUD_SYSPARAM_SPQTV_MINISTRY_FIELDLABELID,
				retrieverCreateDeleteMinistryNameVal(newCreatedItem, null),
				retrieverCreateDeleteMinistryNameVal(null, deletedItem));

		handleFieldAudit(pmPtjSpecialApproval, AuditConst.PM_AUD_SYSPARAM_SPQTV_PTJCODE_FIELDLABELID,
				retrieverCreateDeleteOrgCodeVal(newCreatedItem, null), retrieverCreateDeleteOrgCodeVal(null, deletedItem));

		handleFieldAudit(pmPtjSpecialApproval, AuditConst.PM_AUD_SYSPARAM_SPQTV_PTJNAME_FIELDLABELID,
				retrieverCreateDeleteOrgNameVal(newCreatedItem, null), retrieverCreateDeleteOrgNameVal(null, deletedItem));

		handleFieldAudit(pmPtjSpecialApproval, AuditConst.PM_AUD_SYSPARAM_SPQTV_LIMIT_FIELDLABELID,
				retrieverCreateDeleteAmtVal(newCreatedItem, null), retrieverCreateDeleteAmtVal(null, deletedItem));
		
		handleFieldAudit(pmPtjSpecialApproval, AuditConst.PM_AUD_SYSPARAM_SPQTV_EFFDATE_FIELDLABELID,
				retrieverCreateDeleteEffDateVal(newCreatedItem, null), retrieverCreateDeleteEffDateVal(null, deletedItem));

		handleFieldAudit(pmPtjSpecialApproval, AuditConst.PM_AUD_SYSPARAM_SPQTV_EXPDATE_FIELDLABELID,
				retrieverCreateDeleteExpDateVal(newCreatedItem, null), retrieverCreateDeleteExpDateVal(null, deletedItem));
	}

	/**
	 * @param approval
	 * @param deletedItem
	 * @return
	 */
	public String retrieverCreateDeleteEffDateVal(PmPtjSpecialApproval approval, PmPtjSpecialApproval deletedItem) {
		// set New Value
		if (approval != null && approval.getEffDate() != null) {
			return simpleDateFormat.format(approval.getEffDate());
		}
		// set old Value
		if (deletedItem != null && deletedItem.getEffDate() != null) {
			return simpleDateFormat.format(deletedItem.getEffDate());
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
			return simpleDateFormat.format(approval.getExpDate());
		}
		if (deletedItem != null && deletedItem.getExpDate() != null) {
			return simpleDateFormat.format(deletedItem.getExpDate());
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

	private void handleFieldAudit(PmPtjSpecialApproval approval, BigInteger fieldLabelId, String newValue, String oldValue) {
		if (newValue != null || oldValue != null) {
			String actionType = newValue != null ? AuditConst.PM_AUD_ACTION_TYPE_INS : AuditConst.PM_AUD_ACTION_TYPE_DEL;
			qtValuesAddUpdateDeleteAuditDetail(approval, fieldLabelId, oldValue != null ? oldValue : null,
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
	public void qtValuesAddUpdateDeleteAuditDetail(PmPtjSpecialApproval approval, BigInteger fieldLabelCode, String oldValue,
			String newValue, String actionType) {
		docId = BigInteger.valueOf(approval.getPtjSpecialApprovalId());
		addAuditDetail(AuditConst.PM_AUD_SPQTV_PARAM_CODE, AuditConst.PM_AUD_SPQTV_PMPARAMETERLVLID,
				AuditConst.PM_LVL_CODE_3_SPQTV, fieldLabelCode, oldValue, newValue, actionType);
	}

	// /*************** YEP PROCESS START ******************///
	/**
	 * @param managedBeans
	 * @param roleCode
	 */
	public void yepProcessFeeExemption(RuleCatalogueManageBean managedBeans) {
		for (PmFeeExemption exemption : managedBeans.getResDTO().getFeeExemptions()) {
			PmFeeExemption matchingExemption = yepFindMatchingExemption(managedBeans.getResDTOOld().getFeeExemptions(), exemption);

			if (matchingExemption != null) {
				yepProcessIfChanged(exemption, matchingExemption);
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
	public void yepProcessIfChanged(PmFeeExemption exemption, PmFeeExemption exemptionOld) {
		boolean isEffDateChanged = !exemption.getEffDate().equals(exemptionOld.getEffDate());
		boolean isExpDateChanged = !exemption.getExpDate().equals(exemptionOld.getExpDate());

		if (isEffDateChanged || isExpDateChanged) {
			yepProcessBasedOnParamCode(exemption, exemptionOld);
		}
	}

	/**
	 * @param exemption
	 * @param exemptionOld
	 * @param roleCode
	 */
	public void yepProcessBasedOnParamCode(PmFeeExemption exemption, PmFeeExemption exemptionOld) {
		String paramCode = exemption.getOrgTypeId().getParameterCode();
		switch (paramCode) {
		case "7":
			yepProcessFeeExemption(exemption, exemptionOld, AuditConst.PM_LVL_CODE_3_YEP3,
					AuditConst.PM_AUD_YEP3_PMPARAMETERLVLID);
			break;
		case "8":
			yepProcessFeeExemption(exemption, exemptionOld, AuditConst.PM_LVL_CODE_3_YEP5,
					AuditConst.PM_AUD_YEP5_PMPARAMETERLVLID);
			break;
		case "9":
			yepProcessFeeExemption(exemption, exemptionOld, AuditConst.PM_LVL_CODE_3_YEP6,
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
	public void yepProcessFeeExemption(PmFeeExemption exemption, PmFeeExemption exemptionOld, String lvlCode, BigInteger lvlId) {
		if (exemption.getEffDate() != null && !exemption.getEffDate().equals(exemptionOld.getEffDate())) {
			yepAddAuditDetail(exemption, lvlCode, AuditConst.PM_AUD_SYSPARAMETER_EFFDATE_FIELDLABELID, 
					simpleDateFormat.format(exemptionOld.getEffDate()), simpleDateFormat.format(exemption.getEffDate()), lvlId);
		}
		if (exemption.getExpDate() != null && !exemption.getExpDate().equals(exemptionOld.getExpDate())) {
			yepAddAuditDetail(exemption, lvlCode, AuditConst.PM_AUD_SYSPARAMETER_EXPDATE_FIELDLABELID, 
					simpleDateFormat.format(exemptionOld.getExpDate()), simpleDateFormat.format(exemption.getExpDate()), lvlId);
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
	public void yepAddAuditDetail(PmFeeExemption exemption, String lvlCode, BigInteger fieldLabelCode, String oldValue,
			String newValue, BigInteger lvlId) {
		docId = BigInteger.valueOf(exemption.getFeeExemptionId());
		addAuditDetail(exemption.getOrgTypeId().getPmParameterDescsSpecCallOnly().get(1).getCodeName(), lvlId, lvlCode,
				fieldLabelCode, oldValue, newValue, AuditConst.PM_AUD_ACTION_TYPE_UPD);
	}

	// **************************** YEP END ******************//

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
	 * @param actionType
	 */
	public void addAuditDetail(String codeName, BigInteger lvlId, String lvlCode, BigInteger fieldLabelCode, String oldValue,
			String newValue, String actionType) {
		AuditPMSPDTO detailObj = new AuditPMSPDTO();
		detailObj.setRoleCode(this.roleCode);
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

	void clearAllAuditBeanVal(RuleCatalogueManageBean managedBeans) {
		managedBeans.getResDTO().setFeeExemptions(null);
		managedBeans.getResDTOOld().setFeeExemptions(null);
		managedBeans.setAuditMainPtjSpecialApprovalList(null);
		managedBeans.setAuditUpdateMainPtjSpecialApprovalList(null);
		managedBeans.setAuditDeletedMainPtjSpecialApprovalList(null);
		managedBeans.setAuditMainPtjSpecialApprovalListNewVal(null);

		managedBeans.setAuditDeletedFeeExemptionsForOrgType(null);
		managedBeans.setAuditAddedFeeExemptionsForOrgType(null);
		managedBeans.setAuditUpdatedFeeExemptionsForOrgType(null);
		managedBeans.setAuditFeeExemptionsForOrgTypeOld(null);

		managedBeans.setAuditFeeExemptionsForOrgItemOld(null);
		managedBeans.setAuditUpdatedFeeExemptionsForOrgItem(null);
		managedBeans.setAuditDeletedFeeExemptionsForOrgItem(null);
		managedBeans.setAuditAddedFeeExemptionsForOrgItem(null);

		managedBeans.setAuditFeeExemptionsForOrgContractOld(null);
		managedBeans.setAuditUpdatedFeeExemptionsForContract(null);
		managedBeans.setAuditDeletedFeeExemptionsForContract(null);
		managedBeans.setAuditAddedFeeExemptionsForContract(null);

		managedBeans.setAuditFeeExemptionsForOrgComodityOld(null);
		managedBeans.setAuditUpdatedFeeExemptionsForComodity(null);
		managedBeans.setAuditDeletedFeeExemptionsForComodity(null);
		managedBeans.setAuditAddedFeeExemptionsForComodity(null);

		managedBeans.setAuditFeeExemptionType(null);
		managedBeans.setAuditLevelCode(null);
	}
}