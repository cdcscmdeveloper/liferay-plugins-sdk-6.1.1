package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRule;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleDesc;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.web.managedbean.pm.settings.RuleCatalogueManageBean;

import org.apache.commons.collections.CollectionUtils;

/**
 * AuditPMSysParamConstantRulesBackingBean #28895-35509 - GATE3
 * 
 * @author normuliana
 * 
 */
public class AuditPMSysParamConstantRulesBackingBean extends AuditPMSysParamBackingBean {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	String roleCode = null;
	String lvlCode2 = null;
	String lvlCode3;
	BigInteger paramlvlId;
	BigInteger docId;
	String docNo = null;
	String ruleValue;
	String feeExemptionOrgType;
	String feeExemptionSuppItem;
	String feeExemptionComodity;
	String feeExemptionContract;
	BigInteger itemName;
	String pattern = "dd/MM/yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);	
	String amtFormatter = "#,##0.00";
	String groupingBasePattern = "#,##0";

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	public AuditPMReqDTO spRulesValueTypeProcess(RuleCatalogueManageBean managedBeans, String roleCode) {
		this.roleCode = roleCode;
		this.lvlCode2 = AuditConst.PM_LVL_CODE_2_SPCONSTANTRULE;
		getAuditAddedData(managedBeans.getAuditResDTOSelectedRuleValueList(), managedBeans.getAuditAddedSelectedRuleValueList(),
				managedBeans);

		constantRulesTypeValProcessTab(managedBeans);

		return auditPMReqDTO;
	}

	void getAuditAddedData(List<PmConfigRuleValue> getAuditResDTOSelectedRuleValueList,
			List<PmConfigRuleValue> getAuditAddedSelectedRuleValueList, RuleCatalogueManageBean managedBeans) {
		managedBeans.setAuditAddedSelectedRuleValueList(null);
		for (PmConfigRuleValue saveVal : getAuditResDTOSelectedRuleValueList) {
			for (PmConfigRuleValue updateVal : getAuditAddedSelectedRuleValueList) {
				if (Long.valueOf(updateVal.getRuleValueId()) == null || Long.valueOf(updateVal.getRuleValueId()) <= 0) {
					getAuditAddedDataDetail(saveVal, updateVal, managedBeans);
				}
			}
		}
	}

	void getAuditAddedDataDetail(PmConfigRuleValue saveVal, PmConfigRuleValue updateVal, RuleCatalogueManageBean managedBeans) {
		if (updateVal.isBrandNew() && saveVal.getEffDate().equals(updateVal.getEffDate())) {
			updateVal.setRuleValueId(saveVal.getRuleValueId());

			managedBeans.getAuditAddedSelectedRuleValueList().add(updateVal);
		}
	}

	private void constantRulesTypeValProcessTab(RuleCatalogueManageBean managedBeans) {
		if (CollectionUtils.isNotEmpty(managedBeans.getAuditSelectedRuleValueListOld())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditUpdatedSelectedRuleValueList())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditDeletedSelectedRuleValueList())
				|| CollectionUtils.isNotEmpty(managedBeans.getAuditAddedSelectedRuleValueList())) {
			paramlvlId = AuditConst.PM_AUD_SYSPARAM_SPCONSTANTRULE_PMPARAMETERLVLID;
			spRuleValueType(managedBeans.getAuditSelectedRuleValueListOld(), managedBeans.getAuditUpdatedSelectedRuleValueList(),
					managedBeans.getAuditDeletedSelectedRuleValueList(), managedBeans.getAuditAddedSelectedRuleValueList());
		}
	}

	/**
	 * @param ruleValueOld
	 * @param update
	 * @param deletedItem
	 * @param newCreatedItem
	 */
	public void spRuleValueType(List<PmConfigRuleValue> ruleValueOld, List<PmConfigRuleValue> update,
			List<PmConfigRuleValue> deletedItem, List<PmConfigRuleValue> newCreatedItem) {
		boolean hasDocNo = false;
		boolean hasDocNoCategory = false;
		if (CollectionUtils.isNotEmpty(newCreatedItem)) {
				for (PmConfigRuleValue newCreate : newCreatedItem) {

					docId = BigInteger.valueOf(newCreate.getRuleValueId());

					if ("PC".equalsIgnoreCase(newCreate.getRuleDataTypeCode())) {
						hasDocNo = true;
						setDocNo(hasDocNo,hasDocNoCategory,newCreate);
					}

					if (newCreate.getCategoryL3Id() != null && "C".equalsIgnoreCase(newCreate.getRuleDataTypeCode())) {
						hasDocNoCategory = true;
						setDocNo(hasDocNo, hasDocNoCategory, newCreate);
					}

					processSPRulesValueTypeCreateDeleted(newCreate, null);
				}
			}

		/* Temporary close this deleted section
		if (CollectionUtils.isNotEmpty(deletedItem)) {
			for (PmConfigRuleValue deleted : deletedItem) {
				processSPRulesValueTypeCreateDeleted(null, deleted);

			}
		}
		*/
		
		for (PmConfigRuleValue updateVal : update) {
			PmConfigRuleValue matchingIdUpdatedOldVal = findMatchingSPRulesTypeValue(ruleValueOld, updateVal);
			if (matchingIdUpdatedOldVal != null) {
				if("PC".equalsIgnoreCase(updateVal.getRuleDataTypeCode())) {
					hasDocNo = true;
					setDocNo(hasDocNo,hasDocNoCategory,updateVal);
				}
					
				if(matchingIdUpdatedOldVal.getCategoryL3Id() != null && "C".equalsIgnoreCase(updateVal.getRuleDataTypeCode())) {
					hasDocNoCategory = true;
					setDocNo(hasDocNo,hasDocNoCategory,updateVal);
				}
					docId = BigInteger.valueOf(matchingIdUpdatedOldVal.getRuleValueId());

					processSPRuleTypeValEffExpDate(matchingIdUpdatedOldVal, updateVal);
			}
		}
	}
	
	private void setDocNo(boolean hasDocNo, boolean hasDocNoCategory, PmConfigRuleValue pmConfigRuleValue) {
		if (hasDocNo) {
			docNo = pmConfigRuleValue.getRuleValue();
		} else if (hasDocNoCategory) {
			docNo = pmConfigRuleValue.getCategoryL3Id().getPmCategoryL2().getPmCategoryL1().getCategoryL1Code()
					+ pmConfigRuleValue.getCategoryL3Id().getPmCategoryL2().getCategoryL2Code()
					+ pmConfigRuleValue.getCategoryL3Id().getCategoryL3Code();
		}
		hasDocNo = false;
		hasDocNoCategory = false;
	}
	
	/**
	 * @param ruleValueOld
	 * @param update
	 * @return
	 */
	public PmConfigRuleValue findMatchingSPRulesTypeValue(List<PmConfigRuleValue> ruleValueOld, PmConfigRuleValue update) {
		for (PmConfigRuleValue mainVal : ruleValueOld) {
			if (Long.valueOf(update.getRuleValueId()).equals(Long.valueOf(mainVal.getRuleValueId()))) {
				return mainVal;
			}
		}
		return null; // No match found
	}

	/**
	 * @param ruleValueOld
	 * @param updateVal
	 */
	public void processSPRuleTypeValEffExpDate(PmConfigRuleValue ruleValueOld, PmConfigRuleValue updateVal) {
		/* Update Eff Date , Exp Date */
		int ruleValueOldInt = ruleValueOld.getExpDate() == null ? 0 : 1;
		int updateValInt = updateVal.getExpDate() == null ? 0 : 1;
		boolean expDatebool = false;
		if (updateVal.getEffDate() != null) {
			getEffDate(ruleValueOld, updateVal);
		}
		if ((ruleValueOldInt == 1 && updateValInt == 1) && !ruleValueOld.getExpDate().equals(updateVal.getExpDate())) {
			expDatebool = true;
		}
		if (ruleValueOldInt == 0 && updateValInt == 1) {
			expDatebool = true;
		}
		if (Boolean.valueOf(expDatebool)) {
			getExpDate(ruleValueOld, updateVal, ruleValueOldInt);
		}
	}

	private void getEffDate(PmConfigRuleValue ruleValueOld, PmConfigRuleValue updateVal) {
		if (updateVal.getEffDate() != null && !ruleValueOld.getEffDate().equals(updateVal.getEffDate())) {
			spRuleValTypeAddUpdateDeleteAuditDetail(updateVal, AuditConst.PM_AUD_SYSPARAM_SPCONSTANTRULE_EFFDATE_FIELDLABELID,
					simpleDateFormat.format(ruleValueOld.getEffDate()), simpleDateFormat.format(updateVal.getEffDate()),
					AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}
	}

	private void getExpDate(PmConfigRuleValue ruleValueOld, PmConfigRuleValue updateVal, int ruleValueOldInt) {
		spRuleValTypeAddUpdateDeleteAuditDetail(updateVal, AuditConst.PM_AUD_SYSPARAM_SPCONSTANTRULE_EXPDATE_FIELDLABELID,
				ruleValueOldInt == 0 ? null : simpleDateFormat.format(ruleValueOld.getExpDate()), 
						updateVal.getExpDate() != null ? simpleDateFormat.format(updateVal.getExpDate()) : null, AuditConst.PM_AUD_ACTION_TYPE_UPD);
	}



	private PmConfigRuleValue ruleTypeNewOrDeleted(PmConfigRuleValue newCreatedItem, PmConfigRuleValue deletedItem) {
		return newCreatedItem != null ? newCreatedItem : deletedItem;
	}

	/**
	 * @param newCreatedItem
	 * @param deletedItem
	 */
	public void processSPRulesValueTypeCreateDeleted(PmConfigRuleValue newCreatedItem, PmConfigRuleValue deletedItem) {
		PmConfigRuleValue ruleValueType = ruleTypeNewOrDeleted(newCreatedItem, deletedItem);
		if (ruleValueType.getRuleDataTypeCode() != null) {
			if ((ruleValueType.getRuleDataTypeCode().matches("A|N|M|P") && ruleValueType.getRuleRangeType() != BigInteger
					.valueOf(2)) || ruleValueType.getRuleDataTypeCode().matches("PC")) {
				handleFieldAuditSPRulesValueType(ruleValueType, AuditConst.PM_AUD_SYSPARAM_SPCONSTANTRULE_VALUE_FIELDLABELID,
						ruleNumberValueNewCreated(newCreatedItem), ruleNumberValueDeleted(deletedItem));
			}
		}
		// categoryL3Id
		if (ruleValueType.getCategoryL3Id() != null && "C".equals(ruleValueType.getRuleDataTypeCode())) {
			handleFieldAuditSPRulesValueType(ruleValueType, AuditConst.PM_AUD_SYSPARAM_SPCONSTANTRULE_VALUE_FIELDLABELID,
					categoryL3IdNewCreated(newCreatedItem), categoryL3IdDeleted(deletedItem));
			// Category Name
			handleFieldAuditSPRulesValueType(ruleValueType, AuditConst.PM_AUD_SYSPARAM_SPCONSTANTRULE_DESCRIPTION_FIELDLABELID,
					categoryNameNewCreated(newCreatedItem), categoryNameDeleted(deletedItem));
		}
		// FromValue
		if (ruleValueType.getFromValue() != null) {
			handleFieldAuditSPRulesValueType(ruleValueType, AuditConst.PM_AUD_SYSPARAM_SPCONSTANTRULE_FROMVALUE_FIELDLABELID,
					fromValueNewCreated(newCreatedItem), fromValueDeleted(deletedItem));
		}
		// ToValue
		if (ruleValueType.getToValue() != null) {
			handleFieldAuditSPRulesValueType(ruleValueType, AuditConst.PM_AUD_SYSPARAM_SPCONSTANTRULE_TOVALUE_FIELDLABELID,
					toValueNewCreated(newCreatedItem), toValueDeleted(deletedItem));
		}		
		processing(ruleValueType, newCreatedItem, deletedItem);		
	}
	
	private void processing(PmConfigRuleValue ruleValueType, PmConfigRuleValue newCreatedItem,
			PmConfigRuleValue deletedItem) {
		// Rule Description
				if (ruleValueType.getRuleDescription() != null) {
					handleFieldAuditSPRulesValueType(ruleValueType,
							AuditConst.PM_AUD_SYSPARAM_SPCONSTANTRULE_DESCRIPTION_FIELDLABELID,
							ruleDescNewCreated(newCreatedItem), ruleDescDeleted(deletedItem));
				}
		// Zone
		if (ruleValueType.getZoneId() != null) {
			handleFieldAuditSPRulesValueType(ruleValueType,
					AuditConst.PM_AUD_SYSPARAM_SPCONSTANTRULE_ZONE_FIELDLABELID, zoneNewCreated(newCreatedItem),
					zoneDeleted(deletedItem));
		}
		// EffectiveDate
		handleFieldAuditSPRulesValueType(ruleValueType, AuditConst.PM_AUD_SYSPARAM_SPCONSTANTRULE_EFFDATE_FIELDLABELID,
				getEffectiveDate(newCreatedItem, null), getEffectiveDate(null, deletedItem));
		// ExpiryDate
		handleFieldAuditSPRulesValueType(ruleValueType, AuditConst.PM_AUD_SYSPARAM_SPCONSTANTRULE_EXPDATE_FIELDLABELID,
				getExpiryDate(newCreatedItem, null), getExpiryDate(null, deletedItem));
	}

	private void handleFieldAuditSPRulesValueType(PmConfigRuleValue ruleValue, BigInteger fieldLabelId, String newValue,
			String oldValue) {
		if (newValue != null || oldValue != null) {
			String actionType = newValue != null ? AuditConst.PM_AUD_ACTION_TYPE_INS : AuditConst.PM_AUD_ACTION_TYPE_DEL;
			spRuleValTypeAddUpdateDeleteAuditDetail(ruleValue, fieldLabelId, oldValue != null ? oldValue : null,
					newValue != null ? newValue : null, actionType);
		}
	}
	
	private String ruleNumberValueNewCreated(PmConfigRuleValue newCreatedItem) {
		if (newCreatedItem != null
				&& (newCreatedItem.getRuleNumberValue() != null || newCreatedItem.getRuleValue() != null)
				&& (Long.valueOf(newCreatedItem.getRuleValueId()) != null)) {
			DecimalFormat formatter = new DecimalFormat(amtFormatter);
			
			String rawRuleValue = newCreatedItem.getRuleValue();
			if(newCreatedItem.getRuleType().getRuleDataTypeId().getParameterType().equalsIgnoreCase("DAT") && newCreatedItem.getRuleType().getRuleDataTypeId().getParameterCode().equalsIgnoreCase("A")) {
				String number = rawRuleValue;
				double amount = Double.parseDouble(number);
				ruleValue = formatter.format(amount);
			} else if(newCreatedItem.getRuleType().getParameterType() != null) {
				PmParameter pmParameter = getParameterByTypeAndCode(newCreatedItem.getRuleType().getParameterType(),
						newCreatedItem.getRuleValue().toUpperCase());
				ruleValue = pmParameter.getPmParameterDescs().get(1).getCodeName();
			}else if(newCreatedItem.getRuleType().getRuleDataTypeId().getParameterType().equalsIgnoreCase("DAT") && newCreatedItem.getRuleType().getRuleDataTypeId().getParameterCode().equalsIgnoreCase("PC")){
				ruleValue = rawRuleValue;
			} else {
				rawRuleValueNextProcess(rawRuleValue);
			}
			rawRuleValue(rawRuleValue,newCreatedItem);
			
			return ruleValue;
		}
		return null;
	}
	
	private void rawRuleValueNextProcess(String rawRuleValue){
		DecimalFormat basePatternformatter = new DecimalFormat(groupingBasePattern);
		DecimalFormat formatter = new DecimalFormat(amtFormatter);
			if (rawRuleValue.contains(".")) {
				String number = rawRuleValue;
				double amount = Double.parseDouble(number);
				ruleValue = formatter.format(amount);
			} else {
				BigDecimal bd = new BigDecimal(rawRuleValue);
				ruleValue = basePatternformatter.format(bd);
			}
	}

	private void rawRuleValue(String rawRuleValue, PmConfigRuleValue newCreatedItem){
		DecimalFormat basePatternformatter = new DecimalFormat(groupingBasePattern);
		if (rawRuleValue == null && newCreatedItem.getRuleNumberValue() != null) {
			BigDecimal bd = new BigDecimal(newCreatedItem.getRuleNumberValue().toString());
			ruleValue = basePatternformatter.format(bd);
		}
	}
	
	private String ruleNumberValueDeleted(PmConfigRuleValue deletedItem) {
		if (deletedItem != null && (deletedItem.getRuleValue() != null || deletedItem.getRuleNumberValue() != null)
				&& (Long.valueOf(deletedItem.getRuleValueId()) != null)) {
			String rawRuleValue = deletedItem.getRuleValue();
			if ("TRUE".equalsIgnoreCase(rawRuleValue)) {
				ruleValue = AuditConst.PM_AUD_YES;
			} else if ("FALSE".equalsIgnoreCase(rawRuleValue)) {
				ruleValue = AuditConst.PM_AUD_NO;
			} else {
				ruleValue = rawRuleValue;
			}

			if (rawRuleValue == null) {
				ruleValue = deletedItem.getRuleNumberValue() != null ? deletedItem.getRuleNumberValue()
						.toString() : null;
			}
			return ruleValue;
		}
		return null;
	}

	private String categoryL3IdNewCreated(PmConfigRuleValue newCreatedItem) {
		if (newCreatedItem != null
				&& (Long.valueOf(newCreatedItem.getRuleValueId()) != null || Long.valueOf(newCreatedItem
						.getRuleValueId()) > 0) && newCreatedItem.getCategoryL3Id() != null) {
			return newCreatedItem.getCategoryL3Id().getPmCategoryL2().getPmCategoryL1().getCategoryL1Code()
					+ newCreatedItem.getCategoryL3Id().getPmCategoryL2().getCategoryL2Code()
					+ newCreatedItem.getCategoryL3Id().getCategoryL3Code();
		}
		return null;
	}

	private String categoryL3IdDeleted(PmConfigRuleValue deletedItem) {
		if (deletedItem != null
				&& (Long.valueOf(deletedItem.getRuleValueId()) != null || Long.valueOf(deletedItem.getRuleValueId()) > 0)
				&& deletedItem.getCategoryL3Id() != null) {
			return deletedItem.getCategoryL3Id().getPmCategoryL2().getPmCategoryL1().getCategoryL1Code()
					+ deletedItem.getCategoryL3Id().getPmCategoryL2().getCategoryL2Code()
					+ deletedItem.getCategoryL3Id().getCategoryL3Code();
		}
		return null;
	}

	private String fromValueNewCreated(PmConfigRuleValue newCreatedItem) {
		if (newCreatedItem != null
				&& (Long.valueOf(newCreatedItem.getRuleValueId()) != null || Long.valueOf(newCreatedItem
						.getRuleValueId()) > 0) && newCreatedItem.getFromValue() != null) {
			DecimalFormat basePatternformatter = new DecimalFormat(groupingBasePattern);
			String rawValue = newCreatedItem.getFromValue().toString();
			if (rawValue != null) {
				rawValueNotNullProcess(rawValue,newCreatedItem);
			} else {
				BigDecimal bd = new BigDecimal(newCreatedItem.getRuleNumberValue().toString());
				ruleValue = basePatternformatter.format(bd);
			}
		}
		return ruleValue;
	}
	
	private String fromValueDeleted(PmConfigRuleValue deletedItem) {
		if (deletedItem != null
				&& (Long.valueOf(deletedItem.getRuleValueId()) != null || Long.valueOf(deletedItem.getRuleValueId()) > 0)
				&& deletedItem.getFromValue() != null) {
			return deletedItem.getFromValue().toString();
		}
		return null;
	}
	
	private String toValueNewCreated(PmConfigRuleValue newCreatedItem) {
		if (newCreatedItem != null
				&& (Long.valueOf(newCreatedItem.getRuleValueId()) != null || Long.valueOf(newCreatedItem
						.getRuleValueId()) > 0) && newCreatedItem.getToValue() != null) {
			DecimalFormat basePatternformatter = new DecimalFormat(groupingBasePattern);
			String rawValue = newCreatedItem.getToValue().toString();
			if (rawValue != null) {
				rawValueNotNullProcess(rawValue,newCreatedItem);
			} else {
				BigDecimal bd = new BigDecimal(newCreatedItem.getRuleNumberValue().toString());
				ruleValue = basePatternformatter.format(bd);
			}
		}
		return ruleValue;
	}
	
	private void rawValueNotNullProcess(String rawValue,PmConfigRuleValue newCreatedItem){
		DecimalFormat basePatternformatter = new DecimalFormat(groupingBasePattern);
		DecimalFormat formatter = new DecimalFormat(amtFormatter);
		if (Long.valueOf(676).equals(newCreatedItem.getRuleType().getRuleDataTypeId().getParameterId())) {
			String number = rawValue;
			double amount = Double.parseDouble(number);
			ruleValue = formatter.format(amount);
		} else {
			if (rawValue.contains(".")) {
				String number = rawValue;
				double amount = Double.parseDouble(number);
				ruleValue = formatter.format(amount);
			} else {
				BigDecimal bd = new BigDecimal(rawValue);
				ruleValue = basePatternformatter.format(bd);
			}
		}
	}	

	private String toValueDeleted(PmConfigRuleValue deletedItem) {
		if (deletedItem != null
				&& (Long.valueOf(deletedItem.getRuleValueId()) != null || Long.valueOf(deletedItem.getRuleValueId()) > 0)
				&& deletedItem.getToValue() != null) {
			return deletedItem.getToValue().toString();
		}
		return null;
	}

	private String categoryNameNewCreated(PmConfigRuleValue newCreatedItem) {
		if (newCreatedItem != null
				&& (Long.valueOf(newCreatedItem.getRuleValueId()) != null || Long.valueOf(newCreatedItem.getRuleValueId()) > 0)
				&& newCreatedItem.getCategoryL3Id() != null) {
			return newCreatedItem.getCategoryL3Id().getCategoryName();
		}
		return null;
	}

	private String categoryNameDeleted(PmConfigRuleValue deletedItem) {
		if (deletedItem != null
				&& (Long.valueOf(deletedItem.getRuleValueId()) != null || Long.valueOf(deletedItem.getRuleValueId()) > 0)
				&& deletedItem.getCategoryL3Id() != null) {
			return deletedItem.getCategoryL3Id().getCategoryName();
		}
		return null;
	}

	private String ruleDescNewCreated(PmConfigRuleValue newCreatedItem) {
		if (newCreatedItem != null
				&& (Long.valueOf(newCreatedItem.getRuleValueId()) != null || Long.valueOf(newCreatedItem.getRuleValueId()) > 0)
				&& newCreatedItem.getRuleDescription() != null) {
			return newCreatedItem.getRuleDescription();
		}
		return null;
	}

	private String ruleDescDeleted(PmConfigRuleValue deletedItem) {
		if (deletedItem != null
				&& (Long.valueOf(deletedItem.getRuleValueId()) != null || Long.valueOf(deletedItem.getRuleValueId()) > 0)
				&& deletedItem.getRuleDescription() != null) {
			return deletedItem.getRuleDescription();
		}
		return null;
	}

	private String zoneNewCreated(PmConfigRuleValue newCreatedItem) {
		if (newCreatedItem != null
				&& (Long.valueOf(newCreatedItem.getRuleValueId()) != null || Long.valueOf(newCreatedItem.getRuleValueId()) > 0)
				&& newCreatedItem.getZoneId() != null) {
			return newCreatedItem.getZoneName();
		}
		return null;
	}

	private String zoneDeleted(PmConfigRuleValue deletedItem) {
		if (deletedItem != null
				&& (Long.valueOf(deletedItem.getRuleValueId()) != null || Long.valueOf(deletedItem.getRuleValueId()) > 0)
				&& deletedItem.getZoneId() != null) {
			return deletedItem.getZoneName();
		}
		return null;
	}

	private String getEffectiveDate(PmConfigRuleValue newCreatedItem, PmConfigRuleValue deletedItem) {
		if (newCreatedItem != null && newCreatedItem.getEffDate() != null) {
			return simpleDateFormat.format(newCreatedItem.getEffDate());
		} else if (deletedItem != null && deletedItem.getEffDate() != null) {
			return simpleDateFormat.format(deletedItem.getEffDate());
		}
		return null;
	}

	private String getExpiryDate(PmConfigRuleValue newCreatedItem, PmConfigRuleValue deletedItem) {
		if (newCreatedItem != null && newCreatedItem.getExpDate() != null) {
			return simpleDateFormat.format(newCreatedItem.getExpDate());
		} else if (deletedItem != null && deletedItem.getExpDate() != null) {
			return simpleDateFormat.format(deletedItem.getExpDate());
		}
		return null;
	}

	/**
	 * @param ruleValue
	 * @param fieldLabelCode
	 * @param oldValue
	 * @param newValue
	 * @param actionType
	 */
	public void spRuleValTypeAddUpdateDeleteAuditDetail(PmConfigRuleValue ruleValue, BigInteger fieldLabelCode, String oldValue,
			String newValue, String actionType) {
		lvlCode3 = ruleValue.getRuleTypeCode();
		docId = BigInteger.valueOf(ruleValue.getRuleValueId());
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
		detailObj.setDocNo(docNo);
		detailObj.setActionType(actionType);
		detailObj.setLvlCode3(lvlCode3);
		detailObj.setFieldLabelCode(fieldLabelCode);
		detailObj.setOldValue(oldValue);
		detailObj.setNewValue(newValue);
		objList.add(detailObj);
		auditPMReqDTO.setObjList(objList);
	}
}