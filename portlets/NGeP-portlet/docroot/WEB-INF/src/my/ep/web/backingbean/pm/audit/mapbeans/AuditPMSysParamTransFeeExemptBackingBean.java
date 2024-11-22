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
		feeExemptionOrgType = managedBeans.getAuditFeeExemptionType() != null
				&& managedBeans.getAuditFeeExemptionType().equalsIgnoreCase(ProfileManagementConst.FEE_EXEMPTION_TYPE_ORGTYPE) ? managedBeans
				.getAuditFeeExemptionType() : null;
		if (feeExemptionOrgType != null) {
			orgTypeProcessTab(managedBeans);
		}

		feeExemptionSuppItem = managedBeans.getAuditFeeExemptionType() != null
				&& managedBeans.getAuditFeeExemptionType().equalsIgnoreCase(
						ProfileManagementConst.FEE_EXEMPTION_TYPE_ORG_AND_SUPPLIER) ? managedBeans.getAuditFeeExemptionType()
				: null;
		if (feeExemptionSuppItem != null) {
			suppItemProcessTab(managedBeans);
		}

		feeExemptionComodity = managedBeans.getAuditFeeExemptionType() != null
				&& managedBeans.getAuditFeeExemptionType().trim()
						.equalsIgnoreCase(ProfileManagementConst.FEE_EXEMPTION_TYPE_COMMODITY) ? managedBeans
				.getAuditFeeExemptionType() : null;
		if (feeExemptionComodity != null) {
			comodityProcessTab(managedBeans);
		}

		feeExemptionContract = managedBeans.getAuditFeeExemptionType() != null
				&& managedBeans.getAuditFeeExemptionType().equalsIgnoreCase(ProfileManagementConst.FEE_EXEMPTION_TYPE_CONTRACT) ? managedBeans
				.getAuditFeeExemptionType() : null;
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
			spFeeAddUpdateDeleteAuditDetail(pmfeeMain, AuditConst.PM_AUD_SYSPARAMETER_EXPDATE_FIELDLABELID,
					pmfeeMainInt == 0 ? null : pmfeeMain.getExpDate().toString(), updateVal.getExpDate() != null ? updateVal
							.getExpDate().toString() : null, AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}
		if (updateVal.getEffDate() != null && !pmfeeMain.getEffDate().equals(updateVal.getEffDate())) {
			spFeeAddUpdateDeleteAuditDetail(pmfeeMain, AuditConst.PM_AUD_SYSPARAMETER_EFFDATE_FIELDLABELID, pmfeeMain
					.getEffDate().toString(), updateVal.getEffDate().toString(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}
	}

	/**
	 * @param newCreatedItem
	 * @param deletedItem
	 */
	public void processSPTFeeCreateDeleted(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		PmFeeExemption pmFeeExemption = newCreatedItem != null ? newCreatedItem : deletedItem;
		handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAMETER_EFFDATE_FIELDLABELID,
				retrieveCreateDeleteVal(newCreatedItem, null, 1), retrieveCreateDeleteVal(null, deletedItem, 1));

		handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAMETER_EXPDATE_FIELDLABELID,
				retrieveCreateDeleteVal(newCreatedItem, null, 2), retrieveCreateDeleteVal(null, deletedItem, 2));

		if (feeExemptionOrgType != null) {
			handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAM_SPFEE_BYOT_ORGTYPE_FIELDLABELID,
					retrieveCreateDeleteVal(newCreatedItem, null, 3), retrieveCreateDeleteVal(null, deletedItem, 3));
		}
		if (feeExemptionSuppItem != null) {
			handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAM_SPFEE_BYSI_SUPPNAME_FIELDLABELID,
					retrieveCreateDeleteVal(newCreatedItem, null, 3), retrieveCreateDeleteVal(null, deletedItem, 3));

		}
		if (feeExemptionSuppItem != null || feeExemptionComodity != null || feeExemptionContract != null) {
			handleFieldAuditSPTFee(pmFeeExemption, itemName, retrieveCreateDeleteVal(newCreatedItem, null, 4),
					retrieveCreateDeleteVal(null, deletedItem, 4));
		}
		if (feeExemptionComodity != null) {
			handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAM_SPFEE_BYCO_ITEMCODE_FIELDLABELID,
					retrieveCreateDeleteVal(newCreatedItem, null, 5), retrieveCreateDeleteVal(null, deletedItem, 5));

			handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAM_SPFEE_BYCO_TYPE_FIELDLABELID,
					retrieveCreateDeleteVal(newCreatedItem, null, 6), retrieveCreateDeleteVal(null, deletedItem, 6));
		}
		if (feeExemptionContract != null) {
			handleFieldAuditSPTFee(pmFeeExemption, AuditConst.PM_AUD_SYSPARAM_SPFEE_BYCI_CONTNO_FIELDLABELID,
					retrieveCreateDeleteVal(newCreatedItem, null, 7), retrieveCreateDeleteVal(null, deletedItem, 7));
		}
	}

	private void handleFieldAuditSPTFee(PmFeeExemption pmfee, BigInteger fieldLabelId, String newValue, String oldValue) {
		if (newValue != null || oldValue != null) {
			String actionType = newValue != null ? AuditConst.PM_AUD_ACTION_TYPE_INS : AuditConst.PM_AUD_ACTION_TYPE_DEL;
			spFeeAddUpdateDeleteAuditDetail(pmfee, fieldLabelId, oldValue != null ? oldValue : null, newValue != null ? newValue
					: null, actionType);
		}
	}

	/**
	 * @param newCreatedItem
	 * @param deletedItem
	 * @param paramVal
	 * @return
	 */
	public String retrieveCreateDeleteVal(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem, int paramVal) {
		switch (paramVal) {
		case 1:
			return getEffectiveDate(newCreatedItem, deletedItem);
		case 2:
			return getExpiryDate(newCreatedItem, deletedItem);
		case 3:
			return getOrgTypeCodeDesc(newCreatedItem, deletedItem);
		case 4:
			return getItemName(newCreatedItem, deletedItem);
		case 5:
			return getItemCode(newCreatedItem, deletedItem);
		case 6:
			return getItemTypeExtension(newCreatedItem, deletedItem);
		case 7:
			return getContractNo(newCreatedItem, deletedItem);
		default:
			return null; // or throw an IllegalArgumentException if paramVal is
							// unexpected
		}
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

	private String getOrgTypeCodeDesc(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		if (feeExemptionOrgType != null) {
			if (newCreatedItem != null && newCreatedItem.getOrgTypeId() != null) {
				return newCreatedItem.getOrgTypeId().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc();
			} else if (deletedItem != null && deletedItem.getOrgTypeId() != null) {
				return deletedItem.getOrgTypeId().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc();
			}
		}
		if (feeExemptionSuppItem != null) {
			if (newCreatedItem != null && newCreatedItem.getOrgProfileId() != null) {
				return newCreatedItem.getOrgProfileId().getSmSupplier().getCompanyName();
			} else if (deletedItem != null && deletedItem.getOrgProfileId() != null) {
				return deletedItem.getOrgProfileId().getSmSupplier().getCompanyName();
			}
		}

		return null;
	}

	private String getItemName(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		if (feeExemptionComodity != null || feeExemptionContract != null) {
			if (newCreatedItem != null && newCreatedItem.getTypeId() != null) {
				return newCreatedItem.getTypeId().getUnspscId().getCmUnspsc().getUnspscTitle();
			} else if (deletedItem != null && deletedItem.getTypeId() != null) {
				return deletedItem.getTypeId().getUnspscId().getCmUnspsc().getUnspscTitle();
			}
		}
		if (feeExemptionSuppItem != null) {
			if (newCreatedItem != null && newCreatedItem.getItemId() != null) {
				return newCreatedItem.getItemId().getItemName();
			} else if (deletedItem != null && deletedItem.getItemId() != null) {
				return deletedItem.getItemId().getItemName();
			}
		}
		return null;
	}

	private String getItemCode(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		if (newCreatedItem != null && newCreatedItem.getTypeId() != null) {
			return newCreatedItem.getTypeId().getUnspscId().getUnspscCode();
		} else if (deletedItem != null && deletedItem.getTypeId() != null) {
			return deletedItem.getTypeId().getUnspscId().getUnspscCode();
		}
		return null;
	}

	private String getItemTypeExtension(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		if (newCreatedItem != null && newCreatedItem.getTypeId() != null) {
			return newCreatedItem.getTypeId().getTypeName();
		} else if (deletedItem != null && deletedItem.getTypeId() != null) {
			return deletedItem.getTypeId().getTypeName();
		}
		return null;
	}

	private String getContractNo(PmFeeExemption newCreatedItem, PmFeeExemption deletedItem) {
		if (newCreatedItem != null && newCreatedItem.getContractId() != null) {
			return newCreatedItem.getContractId().getContractNo();
		} else if (deletedItem != null && deletedItem.getContractId() != null) {
			return deletedItem.getContractId().getContractNo();
		}
		return null;
	}

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