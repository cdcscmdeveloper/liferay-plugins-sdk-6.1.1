package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ProfileManagementConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.fee.settings.PmFeeExemption;
import my.ep.web.managedbean.pm.settings.RuleCatalogueManageBean;

/**
 * AuditPMSysParamTransFeeExemptBackingBean #28895-35509 - GATE3
 * 
 * @author normuliana
 * 
 */
public class AuditPMSysParamTransFeeExemptBackingBean {
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	String roleCode = null;
	String lvlCode2 = null;
	String lvlCode3;
	BigInteger paramlvlId;
	BigInteger docId;
	String feeExemptionOrgType;
	String feeExemptionSuppItem;
	String feeExemptionComodity;
	String feeExemptionContract;
	BigInteger itemName;

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	public AuditPMReqDTO spTFeeProcess(RuleCatalogueManageBean managedBeans, String roleCode) {
		this.roleCode = roleCode;
		this.lvlCode2 = AuditConst.PM_LVL_CODE_2_SPFEE;
		if (managedBeans.getAuditFeeExemptionType() != null) {
			feeExemptionOrgType = managedBeans.getAuditFeeExemptionType().equalsIgnoreCase(
					ProfileManagementConst.FEE_EXEMPTION_TYPE_ORGTYPE) ? managedBeans.getAuditFeeExemptionType() : null;

			feeExemptionSuppItem = managedBeans.getAuditFeeExemptionType().equalsIgnoreCase(
					ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_SUPPLIER) ? managedBeans.getAuditFeeExemptionType() : null;

			feeExemptionComodity = managedBeans.getAuditFeeExemptionType().trim()
					.equalsIgnoreCase(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMODITY) ? managedBeans
					.getAuditFeeExemptionType() : null;

			feeExemptionContract = managedBeans.getAuditFeeExemptionType().equalsIgnoreCase(
					ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT) ? managedBeans.getAuditFeeExemptionType() : null;
		}
		if (feeExemptionOrgType != null) {
			orgTypeProcessTab(managedBeans);
		}

		if (feeExemptionSuppItem != null) {
			suppItemProcessTab(managedBeans);
		}

		if (feeExemptionComodity != null) {
			comodityProcessTab(managedBeans);
		}

		if (feeExemptionContract != null) {
			contractProcessTab(managedBeans);
		}
		return auditPMReqDTO;
	}

	private void orgTypeProcessTab(RuleCatalogueManageBean managedBeans) {
		if (CollectionUtils.isNotEmpty(managedBeans.getAuditFeeExemptionsForOrgTypeOld())
				&& CollectionUtils.isNotEmpty(managedBeans.getAuditDeletedFeeExemptionsForOrgType())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditAddedFeeExemptionsForOrgType())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditUpdatedFeeExemptionsForOrgType())) {
			lvlCode3 = AuditConst.PM_LVL_CODE_3_SPFEE_BYOT;
			paramlvlId = AuditConst.PM_AUD_SYSPARAM_SPFEE_BYOT_PMPARAMETERLVLID;
			spFeeByOrgType(managedBeans.getAuditFeeExemptionsForOrgTypeOld(),
					managedBeans.getAuditUpdatedFeeExemptionsForOrgType(), managedBeans.getAuditDeletedFeeExemptionsForOrgType(),
					managedBeans.getAuditAddedFeeExemptionsForOrgType());
		}
	}

	private void suppItemProcessTab(RuleCatalogueManageBean managedBeans) {
		if (CollectionUtils.isNotEmpty(managedBeans.getAuditFeeExemptionsForOrgItemOld())
				&& CollectionUtils.isNotEmpty(managedBeans.getAuditDeletedFeeExemptionsForOrgItem())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditAddedFeeExemptionsForOrgItem())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditUpdatedFeeExemptionsForOrgItem())) {
			lvlCode3 = AuditConst.PM_LVL_CODE_3_SPFEE_BYSI;
			paramlvlId = AuditConst.PM_AUD_SYSPARAM_SPFEE_BYSI_PMPARAMETERLVLID;
			itemName = AuditConst.PM_AUD_SYSPARAM_SPFEE_BYSI_ITEMNAME_FIELDLABELID;
			spFeeByOrgType(managedBeans.getAuditFeeExemptionsForOrgItemOld(),
					managedBeans.getAuditUpdatedFeeExemptionsForOrgItem(), managedBeans.getAuditDeletedFeeExemptionsForOrgItem(),
					managedBeans.getAuditAddedFeeExemptionsForOrgItem());
		}
	}

	private void comodityProcessTab(RuleCatalogueManageBean managedBeans) {
		if (CollectionUtils.isNotEmpty(managedBeans.getAuditFeeExemptionsForOrgComodityOld())
				&& CollectionUtils.isNotEmpty(managedBeans.getAuditDeletedFeeExemptionsForComodity())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditAddedFeeExemptionsForComodity())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditUpdatedFeeExemptionsForComodity())) {
			lvlCode3 = AuditConst.PM_LVL_CODE_3_SPFEE_BYCO;
			paramlvlId = AuditConst.PM_AUD_SYSPARAM_SPFEE_BYCO_PMPARAMETERLVLID;
			itemName = AuditConst.PM_AUD_SYSPARAM_SPFEE_BYCO_ITEMNAME_FIELDLABELID;
			spFeeByOrgType(managedBeans.getAuditFeeExemptionsForOrgComodityOld(),
					managedBeans.getAuditUpdatedFeeExemptionsForComodity(),
					managedBeans.getAuditDeletedFeeExemptionsForComodity(), managedBeans.getAuditAddedFeeExemptionsForComodity());
		}
	}

	private void contractProcessTab(RuleCatalogueManageBean managedBeans) {
		if (CollectionUtils.isNotEmpty(managedBeans.getAuditFeeExemptionsForOrgContractOld())
				&& CollectionUtils.isNotEmpty(managedBeans.getAuditDeletedFeeExemptionsForContract())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditAddedFeeExemptionsForContract())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditUpdatedFeeExemptionsForContract())) {
			lvlCode3 = AuditConst.PM_LVL_CODE_3_SPFEE_BYCI;
			paramlvlId = AuditConst.PM_AUD_SYSPARAM_SPFEE_BYCI_PMPARAMETERLVLID;
			itemName = AuditConst.PM_AUD_SYSPARAM_SPFEE_BYCI_ITEMNAME_FIELDLABELID;
			spFeeByOrgType(managedBeans.getAuditFeeExemptionsForOrgContractOld(),
					managedBeans.getAuditUpdatedFeeExemptionsForContract(),
					managedBeans.getAuditDeletedFeeExemptionsForContract(), managedBeans.getAuditAddedFeeExemptionsForContract());
		}
	}

	/**
	 * @param pmfeeMain
	 * @param update
	 * @param deletedItem
	 * @param newCreatedItem
	 */
	public void spFeeByOrgType(List<PmFeeExemption> pmfeeMain, List<PmFeeExemption> update, List<PmFeeExemption> deletedItem,
			List<PmFeeExemption> newCreatedItem) {
		for (PmFeeExemption updateVal : update) {
			PmFeeExemption matchingIdUpdated = findMatchingSPFee(pmfeeMain, updateVal);
			if (matchingIdUpdated != null) {
				processSPFeeByOrgTypeUpdate(matchingIdUpdated, updateVal);
			}
		}
		if (CollectionUtils.isNotEmpty(deletedItem)) { // orgId/smSupplier/supplierName
			for (PmFeeExemption deleted : deletedItem) {
				processSPTFeeCreateDeleted(null, deleted);
			}
		}
		if (CollectionUtils.isNotEmpty(newCreatedItem)) {
			for (PmFeeExemption newCreate : newCreatedItem) {
				processSPTFeeCreateDeleted(newCreate, null);
			}
		}
	}

	/**
	 * @param pmfeeMain
	 * @param update
	 * @return
	 */
	public PmFeeExemption findMatchingSPFee(List<PmFeeExemption> pmfeeMain, PmFeeExemption update) {
		for (PmFeeExemption mainVal : pmfeeMain) {
			if (update.getFeeExemptionId().equals(mainVal.getFeeExemptionId())) {
				return mainVal;
			}
		}
		return null; // No match found
	}

	// *******QT CHECKLIST TAB FOR UPDATED VALUE PEOCESS START *******//
	/**
	 * @param pmfeeMain
	 * @param updateVal
	 */
	public void processSPFeeByOrgTypeUpdate(PmFeeExemption pmfeeMain, PmFeeExemption updateVal) {
		if (feeExemptionOrgType != null
				&& updateVal.getOrgTypeId() != null
				&& !pmfeeMain.getOrgTypeId().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc()
						.equals(updateVal.getOrgTypeId().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc())) {
			spFeeAddUpdateDeleteAuditDetail(pmfeeMain, AuditConst.PM_AUD_SYSPARAM_SPFEE_BYOT_ORGTYPE_FIELDLABELID, pmfeeMain
					.getOrgTypeId().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc(), updateVal.getOrgTypeId()
					.getPmParameterDescsSpecCallOnly().get(1).getCodeDesc(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}
		processSPTFeeEffExpDate(pmfeeMain, updateVal);
	}

	/**
	 * @param pmfeeMain
	 * @param updateVal
	 */
	public void processSPTFeeEffExpDate(PmFeeExemption pmfeeMain, PmFeeExemption updateVal) {
		/* Update Eff Date , Exp Date */
		int pmfeeMainInt = pmfeeMain.getExpDate() == null ? 0 : 1;
		int updateValInt = updateVal.getExpDate() == null ? 0 : 1;
		boolean expDatebool = false;
		if ((pmfeeMainInt == 1 && updateValInt == 1) && !pmfeeMain.getExpDate().equals(updateVal.getExpDate())) {
			expDatebool = true;
		}
		if (pmfeeMainInt == 0 && updateValInt == 1) {
			expDatebool = true;
		}
		if (Boolean.valueOf(expDatebool)) {
			getExpDate(pmfeeMain, updateVal, pmfeeMainInt);
		}
		if (updateVal.getEffDate() != null) {
			getEffDate(pmfeeMain, updateVal);
		}
	}

	private void getExpDate(PmFeeExemption pmfeeMain, PmFeeExemption updateVal, int pmfeeMainInt) {
		spFeeAddUpdateDeleteAuditDetail(pmfeeMain, AuditConst.PM_AUD_SYSPARAMETER_EXPDATE_FIELDLABELID, pmfeeMainInt == 0 ? null
				: pmfeeMain.getExpDate().toString(), updateVal.getExpDate() != null ? updateVal.getExpDate().toString() : null,
				AuditConst.PM_AUD_ACTION_TYPE_UPD);
	}

	private void getEffDate(PmFeeExemption pmfeeMain, PmFeeExemption updateVal) {
		if (updateVal.getEffDate() != null && !pmfeeMain.getEffDate().equals(updateVal.getEffDate())) {
			spFeeAddUpdateDeleteAuditDetail(pmfeeMain, AuditConst.PM_AUD_SYSPARAMETER_EFFDATE_FIELDLABELID, pmfeeMain
					.getEffDate().toString(), updateVal.getEffDate().toString(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}
	}

	// *******QT CHECKLIST TAB FOR UPDATED VALUE PEOCESS END *******//

	// **QT CHECKLIST TAB FOR NEW CREATED AND DELETED VALUE PROCESS START**//
	/**
	 * @param newCreatedItem
	 * @param deletedItem
	 */
	public void processSPTFeeCreateDeleted(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		PmFeeExemption pmFeeExemption = newCreatedItem != null ? newCreatedItem : deletedItem;
		handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAMETER_EFFDATE_FIELDLABELID,
				getEffectiveDate(newCreatedItem, null), getEffectiveDate(null, deletedItem));

		handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAMETER_EXPDATE_FIELDLABELID,
				getExpiryDate(newCreatedItem, null), getExpiryDate(null, deletedItem));

		if (feeExemptionOrgType != null) {
			// orgName
			handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAM_SPFEE_BYOT_ORGTYPE_FIELDLABELID,
					byOTNewCreated(newCreatedItem, null), byOTNewCreated(null, deletedItem));
		}
		if (feeExemptionSuppItem != null) {
			// suppName
			handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAM_SPFEE_BYSI_SUPPNAME_FIELDLABELID,
					bySIOrgTypeCodeDescNewDeleted(newCreatedItem, null), bySIOrgTypeCodeDescNewDeleted(null, deletedItem));

			// itemName
			handleFieldAuditSPTFee(pmFeeExemption, itemName, bySIItemNameNewCreated(newCreatedItem, null),
					bySIItemNameNewCreated(null, deletedItem));
		}

		if (feeExemptionComodity != null) {
			// itemName
			handleFieldAuditSPTFee(pmFeeExemption, itemName, byCONewDeleted(newCreatedItem, null),
					byCONewDeleted(null, deletedItem));

			// itemCode
			handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAM_SPFEE_BYCO_ITEMCODE_FIELDLABELID,
					byCOItemCodeNewDeleted(newCreatedItem, null), byCOItemCodeNewDeleted(null, deletedItem));

			// itemType
			handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAM_SPFEE_BYCO_TYPE_FIELDLABELID,
					byCOItemTypeExtensionNewDeleted(newCreatedItem, null), byCOItemTypeExtensionNewDeleted(null, deletedItem));
		}
		if (feeExemptionContract != null) {
			// itemName
			handleFieldAuditSPTFee(pmFeeExemption, itemName, byCINewDeleted(newCreatedItem, null),
					byCINewDeleted(null, deletedItem));

			// ContractNo
			handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAM_SPFEE_BYCI_CONTNO_FIELDLABELID,
					byCIContractNoNewCreated(newCreatedItem, null), byCIContractNoNewCreated(null, deletedItem));
		}
	}

	private void handleFieldAuditSPTFee(PmFeeExemption pmfee, BigInteger fieldLabelId, String newValue, String oldValue) {
		if (newValue != null || oldValue != null) {
			String actionType = newValue != null ? AuditConst.PM_AUD_ACTION_TYPE_INS : AuditConst.PM_AUD_ACTION_TYPE_DEL;
			spFeeAddUpdateDeleteAuditDetail(pmfee, fieldLabelId, oldValue != null ? oldValue : null, newValue != null ? newValue
					: null, actionType);
		}
	}

	private String byOTNewCreated(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		// getOrgTypeCodeDesc
		if (feeExemptionOrgType != null) {
			if (newCreatedItem != null && newCreatedItem.getOrgTypeId() != null) {
				return newCreatedItem.getOrgTypeId().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc();
			} else if (deletedItem != null && deletedItem.getOrgTypeId() != null) {
				return deletedItem.getOrgTypeId().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc();
			}
		}
		return null;
	}

	private String byCINewDeleted(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		// getContractNo / ItemName
		if (newCreatedItem != null && newCreatedItem.getRequestItemId() != null) {
			return newCreatedItem.getRequestItemId().getItemName();
		}

		if (deletedItem != null && deletedItem.getRequestItemId() != null) {
			return deletedItem.getRequestItemId().getItemName();
		}
		return null;
	}

	private String byCONewDeleted(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		/* ItemName */

		if (newCreatedItem != null && newCreatedItem.getUnspscId() != null) {
			return newCreatedItem.getUnspscId().getUnspscTitle();
		} else if (deletedItem != null && deletedItem.getUnspscId() != null) {
			return deletedItem.getUnspscId().getUnspscTitle();
		}
		return null;
	}

	private String getEffectiveDate(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		if (newCreatedItem != null && newCreatedItem.getEffDate() != null) {
			return newCreatedItem.getEffDate().toString();
		} else if (deletedItem != null && deletedItem.getEffDate() != null) {
			return deletedItem.getEffDate().toString();
		}
		return null;
	}

	private String getExpiryDate(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		if (newCreatedItem != null && newCreatedItem.getExpDate() != null) {
			return newCreatedItem.getExpDate().toString();
		} else if (deletedItem != null && deletedItem.getExpDate() != null) {
			return deletedItem.getExpDate().toString();
		}
		return null;
	}

	/**
	 * @param newCreatedItem
	 * @param deletedItem
	 * @return
	 */
	private String bySIOrgTypeCodeDescNewDeleted(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		if (newCreatedItem != null && newCreatedItem.getOrgProfileId() != null) {
			return newCreatedItem.getOrgProfileId().getSmSupplier().getCompanyName();
		}

		if (deletedItem != null && deletedItem.getOrgProfileId() != null) {
			return deletedItem.getOrgProfileId().getSmSupplier().getCompanyName();
		}

		return null;
	}

	private String bySIItemNameNewCreated(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		if (newCreatedItem != null && newCreatedItem.getItemId() != null) {
			return newCreatedItem.getItemId().getItemName();
		}
		if (deletedItem != null && deletedItem.getItemId() != null) {
			return deletedItem.getItemId().getItemName();
		}

		return null;
	}

	private String byCOItemCodeNewDeleted(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		if (newCreatedItem != null && newCreatedItem.getUnspscId() != null) {
			return newCreatedItem.getUnspscId().getUnspscCode();
		} else if (deletedItem != null && deletedItem.getUnspscId() != null) {
			return deletedItem.getUnspscId().getUnspscCode();
		}
		return null;
	}

	private String byCOItemTypeExtensionNewDeleted(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		if (newCreatedItem != null && newCreatedItem.getTypeId() != null) {
			return newCreatedItem.getTypeId().getTypeName();
		} else if (deletedItem != null && deletedItem.getTypeId() != null) {
			return deletedItem.getTypeId().getTypeName();
		}
		return null;
	}

	private String byCIContractNoNewCreated(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		if (newCreatedItem != null && newCreatedItem.getContractId() != null) {
			return newCreatedItem.getContractId().getContractNo();
		} else if (deletedItem != null && deletedItem.getContractId() != null) {
			return deletedItem.getContractId().getContractNo();
		}
		return null;
	}

	// **QT CHECKLIST TAB FOR NEW CREATED AND DELETED VALUE PROCESS END**//

	/**
	 * @param pmfeeMain
	 * @param fieldLabelCode
	 * @param oldValue
	 * @param newValue
	 * @param actionType
	 */
	public void spFeeAddUpdateDeleteAuditDetail(PmFeeExemption pmfeeMain, BigInteger fieldLabelCode, String oldValue,
			String newValue, String actionType) {
		docId = BigInteger.valueOf(pmfeeMain.getFeeExemptionId());
		addAuditDetail(fieldLabelCode, oldValue, newValue, actionType);
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
	 * @param actionType
	 */
	public void addAuditDetail(BigInteger fieldLabelCode, String oldValue, String newValue, String actionType) {
		AuditPMSPDTO detailObj = new AuditPMSPDTO();
		detailObj.setRoleCode(this.roleCode);
		detailObj.setDocType(lvlCode3);
		detailObj.setPmParameterLvlId(paramlvlId);
		detailObj.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		detailObj.setLvlCode2(lvlCode2);
		detailObj.setDocId(docId);
		detailObj.setActionType(actionType);
		detailObj.setLvlCode3(lvlCode3);
		detailObj.setFieldLabelCode(fieldLabelCode);
		detailObj.setOldValue(oldValue);
		detailObj.setNewValue(newValue);
		objList.add(detailObj);
		auditPMReqDTO.setObjList(objList);
	}
}