package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmBasicCategory;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCategoryRestr;
import my.ep.persistence.entity.pm.common.PmMachineCategory;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.category.CategoryManagedBean;

/**
 * AuditPMCategoryCodeBackingBean
 * 
 * @author haiza 28895_35509
 */
public class AuditPMCategoryCodeBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	
	/** The log. 
	private static Logger log = Logger.getLogger(AuditPMCategoryCodeBackingBean.class);*/
	
	String roleCode;
	String docType3;
	BigInteger docId;
	String actionType;
	String docNo;
	BigInteger docGroupId;
	PmCategoryL3 pmCategoryL3;
	BigInteger parameterLvlIdL3;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleCategoryCode();
	}

	/**
	 * @param manageBean
	 * @param roleCode
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		CategoryManagedBean managedBean = (CategoryManagedBean) manageBean;
		this.roleCode = roleCode;

		if (managedBean.getAuditLevelCategory().equalsIgnoreCase(AuditConst.PM_LVL_CODE_3_CCR_CATL1)) {
			updateDetailPmCategoryL1(managedBean, auditPMReqDTO);
		} else if (managedBean.getAuditLevelCategory().equalsIgnoreCase(AuditConst.PM_LVL_CODE_3_CCR_CATL2)) {
			updateDetailPmCategoryL2(managedBean, auditPMReqDTO);
		} else if (managedBean.getAuditLevelCategory().equalsIgnoreCase(AuditConst.PM_LVL_CODE_3_CCR_CATL3)) {
			updateDetailPmCategoryL3(managedBean, auditPMReqDTO);

		}

		managedBean.setAuditOldCategoryL1(null);
		managedBean.setAuditOldCategoryL2(null);
		managedBean.setAuditOldCategoryL3(null);
		managedBean.setAuditLevelCategory(null);
		managedBean.setAuditUpdatedCategoryL1(null);
		managedBean.setAuditUpdatedCategoryL2(null);
		managedBean.setAuditUpdatedCategoryL3(null);
		return auditPMReqDTO;
	}

	/**
	 * @param manageBean
	 * @param auditPMReqDTO
	 */
	private void updateDetailPmCategoryL1(CategoryManagedBean managedBean, AuditPMReqDTO auditPMReqDTO) {
		PmCategoryL1 oldPmCategoryL1 = managedBean.getAuditOldCategoryL1();
		PmCategoryL1 pmCategoryL1 = managedBean.getPmCategoryL1();
		AuditPMSPDTO statusL1 = new AuditPMSPDTO();

		BigInteger categoryL1Id = BigInteger.valueOf(managedBean.getAuditUpdatedCategoryL1().getCategoryL1Id());
		statusL1.setRoleCode(roleCode);
		statusL1.setDocId(categoryL1Id);
		statusL1.setDocGroupId(categoryL1Id);
		statusL1.setDocType(pmCategoryL1.getCategoryName());
		statusL1.setDocNo(pmCategoryL1.getCategoryL1Code());
		statusL1.setPmParameterLvlId(AuditConst.PM_AUD_CCR_MAIN_PMPARAMETERLVLID);
		statusL1.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
		statusL1.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
		statusL1.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL1);
		statusL1.setFieldLabelCode(AuditConst.PM_AUD_CCR_MAIN_STATUS_FIELDLABELCODE);

		if (managedBean.isNewCategory()) {
			statusL1.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			statusL1.setNewValue(pmCategoryL1.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			
			objList.add(statusL1);

		} else {
			statusL1.setDocId(BigInteger.valueOf(pmCategoryL1.getCategoryL1Id()));
			statusL1.setDocGroupId(BigInteger.valueOf(pmCategoryL1.getCategoryL1Id()));
			statusL1.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			
			if (!pmCategoryL1.getRecordStatusFlg().equals(oldPmCategoryL1.getRecordStatusFlg())) {
				statusL1.setOldValue(oldPmCategoryL1.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				statusL1.setNewValue(pmCategoryL1.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				
				objList.add(statusL1);
			}
		}
		auditPMReqDTO.setObjList(objList);
	}

	/**
	 * @param manageBean
	 * @param auditPMReqDTO
	 */
	private void updateDetailPmCategoryL2(CategoryManagedBean managedBean, AuditPMReqDTO auditPMReqDTO) {
		PmCategoryL2 pmCategoryL2 = managedBean.getPmCategoryL2();
		PmCategoryL2 oldPmCategoryL2 = managedBean.getAuditOldCategoryL2();
		BigInteger categoryL2Id = BigInteger.valueOf(managedBean.getAuditUpdatedCategoryL2().getCategoryL2Id());
		AuditPMSPDTO statusL2 = new AuditPMSPDTO();
		statusL2.setRoleCode(roleCode);
		statusL2.setDocType(pmCategoryL2.getCategoryName());
		statusL2.setDocNo(pmCategoryL2.getCategoryL2Code());
		statusL2.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_PMPARAMETERLVLID);
		statusL2.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
		statusL2.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
		statusL2.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL2);
		statusL2.setDocId(categoryL2Id);
		statusL2.setDocGroupId(categoryL2Id);
		statusL2.setFieldLabelCode(AuditConst.PM_AUD_CCR_SUB_STATUS_FIELDLABELCODE);
		statusL2.setNewValue(pmCategoryL2.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
				: AuditConst.PM_AUD_INACTIVE);

		if (managedBean.isNewCategory()) {
			statusL2.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(statusL2);

		} else {
			
			if (!pmCategoryL2.getRecordStatusFlg().equals(oldPmCategoryL2.getRecordStatusFlg())) {
				statusL2.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				statusL2.setOldValue(oldPmCategoryL2.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				
				objList.add(statusL2);
			}
		}
		auditPMReqDTO.setObjList(objList);
	}

	/**
	 * @param manageBean
	 * @param auditPMReqDTO
	 */
	private void updateDetailPmCategoryL3(CategoryManagedBean managedBean, AuditPMReqDTO auditPMReqDTO) {

		this.pmCategoryL3 = managedBean.getPmCategoryL3();
		this.docType3 = pmCategoryL3.getCategoryName();
		this.docNo = pmCategoryL3.getCategoryL3Code();
		this.parameterLvlIdL3 = AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID;
		PmCategoryL3 oldpmCategoryL3 = managedBean.getAuditOldCategoryL3();

		if (managedBean.isNewCategory()) {
			BigInteger categoryL3Id = BigInteger.valueOf(managedBean.getAuditUpdatedCategoryL3().getCategoryL3Id());
			this.docId = categoryL3Id;
			this.docGroupId = categoryL3Id;
			this.actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			DecimalFormat formatter = new DecimalFormat("#,##0.00");

			detailObj(AuditConst.PM_AUD_CCR_PAID_CAPITAL_FIELDLABELCODE, null, formatter.format(pmCategoryL3.getPaidUpCapital()), null, null);

			detailObj(AuditConst.PM_AUD_CCR_BUMI_PERCENTAGE_FIELDLABELCODE, null, pmCategoryL3.getBumiPercentage()
					.toString()+ AuditConst.PM_AUD_PERCENTAGE, null, null);

			detailObj(AuditConst.PM_AUD_CCR_SUPPLIER_TYPE_FIELDLABELCODE, null, checkPmParameterDesc(pmCategoryL3.getSupplierTypeCode()), null, null);

			detailObj(AuditConst.PM_AUD_CCR_REQUIRE_OTHER_CAT_FIELDLABELCODE, null,
					pmCategoryL3.getBasicCategoryReq() == 1 ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO, null, null);

			detailObj(AuditConst.PM_AUD_CCR_BUSS_COMP_RESTRICTION_FIELDLABELCODE, null,
					pmCategoryL3.isByCompType() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO, null, null);

			detailObj(AuditConst.PM_AUD_CCR_OPERATE_FROM_HOME_FIELDLABELCODE, null,
					pmCategoryL3.getHomeOperated() == 1 ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO, null, null);
			
			detailObj(AuditConst.PM_AUD_CCR_SUB_DIVISION_STATUS_FIELDLABELCODE, null,
					pmCategoryL3.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE, null, null);

		} else {
			this.actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
			this.docGroupId = BigInteger.valueOf(pmCategoryL3.getCategoryL3Id());
			setCategoryL3Detail(oldpmCategoryL3);
			setCategoryL3CheckBox(managedBean);

			if (!pmCategoryL3.getRecordStatusFlg().equals(oldpmCategoryL3.getRecordStatusFlg())) {
				AuditPMSPDTO statusL3 = new AuditPMSPDTO();
				statusL3.setRoleCode(roleCode);
				statusL3.setDocType(docType3);
				statusL3.setDocNo(docNo);
				statusL3.setDocGroupId(docGroupId);
				statusL3.setPmParameterLvlId(parameterLvlIdL3);
				statusL3.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
				statusL3.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
				statusL3.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
				statusL3.setDocId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
				statusL3.setFieldLabelCode(AuditConst.PM_AUD_CCR_SUB_DIVISION_STATUS_FIELDLABELCODE);
				statusL3.setOldValue(oldpmCategoryL3.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				statusL3.setNewValue(pmCategoryL3.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				statusL3.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(statusL3);
			}
		}

		addRequiredCategory(managedBean);
		deleteRequiredCategory(managedBean);
		
		addBusinessComType(managedBean);
		deleteBusinessComType(managedBean);
		
		addSpecialEquipment(managedBean);
		// check for update machine
		if (!managedBean.isNewCategory()) {
			updateSpecialEquipment(managedBean);
		}
					
		auditPMReqDTO.setObjList(objList);
	}

	private void setCategoryL3Detail(PmCategoryL3 oldpmCategoryL3) {
		AuditPMSPDTO bumiPercentage = new AuditPMSPDTO();
		AuditPMSPDTO paidUpCapital = new AuditPMSPDTO();
		AuditPMSPDTO supplierType = new AuditPMSPDTO();
		
		if (pmCategoryL3.getPaidUpCapital().compareTo(oldpmCategoryL3.getPaidUpCapital()) != 0) {
			paidUpCapital.setRoleCode(roleCode);
			paidUpCapital.setDocType(docType3);
			paidUpCapital.setDocNo(docNo);
			paidUpCapital.setDocGroupId(docGroupId);
			paidUpCapital.setPmParameterLvlId(parameterLvlIdL3);
			paidUpCapital.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
			paidUpCapital.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
			paidUpCapital.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
			paidUpCapital.setDocId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			paidUpCapital.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			paidUpCapital.setFieldLabelCode(AuditConst.PM_AUD_CCR_PAID_CAPITAL_FIELDLABELCODE);
			DecimalFormat formatter = new DecimalFormat("#,##0.00");
			paidUpCapital.setOldValue(formatter.format(oldpmCategoryL3.getPaidUpCapital().setScale(2)));
			paidUpCapital.setNewValue(formatter.format(pmCategoryL3.getPaidUpCapital()));
			paidUpCapital.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(paidUpCapital);
		}

		if (!pmCategoryL3.getBumiPercentage().equals(oldpmCategoryL3.getBumiPercentage())) {
			bumiPercentage.setRoleCode(roleCode);
			bumiPercentage.setDocType(docType3);
			bumiPercentage.setDocNo(docNo);
			bumiPercentage.setDocGroupId(docGroupId);
			bumiPercentage.setPmParameterLvlId(parameterLvlIdL3);
			bumiPercentage.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
			bumiPercentage.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
			bumiPercentage.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
			bumiPercentage.setDocId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			bumiPercentage.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			bumiPercentage.setFieldLabelCode(AuditConst.PM_AUD_CCR_BUMI_PERCENTAGE_FIELDLABELCODE);
			bumiPercentage.setOldValue(String.valueOf(oldpmCategoryL3.getBumiPercentage()) + AuditConst.PM_AUD_PERCENTAGE);
			bumiPercentage.setNewValue(String.valueOf(pmCategoryL3.getBumiPercentage()) + AuditConst.PM_AUD_PERCENTAGE);
			bumiPercentage.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(bumiPercentage);
		}

		if (pmCategoryL3.getSupplierTypeCode().getParameterId() != oldpmCategoryL3.getSupplierTypeCode()
				.getParameterId()) {
			supplierType.setRoleCode(roleCode);
			supplierType.setDocType(docType3);
			supplierType.setDocNo(docNo);
			supplierType.setDocGroupId(docGroupId);
			supplierType.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
			supplierType.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
			supplierType.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
			supplierType.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
			supplierType.setDocId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			supplierType.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			supplierType.setFieldLabelCode(AuditConst.PM_AUD_CCR_SUPPLIER_TYPE_FIELDLABELCODE);
			supplierType.setOldValue(oldpmCategoryL3.getSupplierTypeCode().getPmParameterDescsSpecCallOnly().get(0)
					.getCodeDesc());
			supplierType.setNewValue(pmCategoryL3.getSupplierTypeCode().getPmParameterDescsSpecCallOnly().get(0)
					.getCodeDesc());
			supplierType.setOldValueId(BigInteger.valueOf(oldpmCategoryL3.getSupplierTypeCode().getParameterId()));
			supplierType.setNewValueId(BigInteger.valueOf(pmCategoryL3.getSupplierTypeCode().getParameterId()));
			supplierType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(supplierType);
		}
	}

	/**
	 * @param manageBean
	 * @param auditPMReqDTO
	 */
	private void setCategoryL3CheckBox(CategoryManagedBean managedBean) {
		PmCategoryL3 oldpmCategoryL3 = managedBean.getAuditOldCategoryL3();
		AuditPMSPDTO requireOtherCategory = new AuditPMSPDTO();
		AuditPMSPDTO bussCompRestriction = new AuditPMSPDTO();
		AuditPMSPDTO operateFromHome = new AuditPMSPDTO();

		if (!pmCategoryL3.getBasicCategoryReq().equals(oldpmCategoryL3.getBasicCategoryReq())) {
			requireOtherCategory.setRoleCode(roleCode);
			requireOtherCategory.setDocType(docType3);
			requireOtherCategory.setDocNo(docNo);
			requireOtherCategory.setDocGroupId(docGroupId);
			requireOtherCategory.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
			requireOtherCategory.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
			requireOtherCategory.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
			requireOtherCategory.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
			requireOtherCategory.setDocId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			requireOtherCategory.setFieldLabelCode(AuditConst.PM_AUD_CCR_REQUIRE_OTHER_CAT_FIELDLABELCODE);
			requireOtherCategory.setOldValue(oldpmCategoryL3.getBasicCategoryReq() == 1 ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			requireOtherCategory.setNewValue(pmCategoryL3.getBasicCategoryReq() == 1 ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			requireOtherCategory.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(requireOtherCategory);
		}

		if (pmCategoryL3.isByCompType() != oldpmCategoryL3.isByCompType()) {
			bussCompRestriction.setRoleCode(roleCode);
			bussCompRestriction.setDocType(docType3);
			bussCompRestriction.setDocNo(docNo);
			bussCompRestriction.setDocGroupId(docGroupId);
			bussCompRestriction.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
			bussCompRestriction.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
			bussCompRestriction.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
			bussCompRestriction.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
			bussCompRestriction.setDocId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			bussCompRestriction.setFieldLabelCode(AuditConst.PM_AUD_CCR_BUSS_COMP_RESTRICTION_FIELDLABELCODE);
			bussCompRestriction.setOldValue(oldpmCategoryL3.isByCompType() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			bussCompRestriction.setNewValue(pmCategoryL3.isByCompType() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			bussCompRestriction.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(bussCompRestriction);
		}

		if (!pmCategoryL3.getHomeOperated().equals(oldpmCategoryL3.getHomeOperated())) {
			operateFromHome.setRoleCode(roleCode);
			operateFromHome.setDocType(docType3);
			operateFromHome.setDocNo(docNo);
			operateFromHome.setDocGroupId(docGroupId);
			operateFromHome.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
			operateFromHome.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
			operateFromHome.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
			operateFromHome.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
			operateFromHome.setDocId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			operateFromHome.setFieldLabelCode(AuditConst.PM_AUD_CCR_OPERATE_FROM_HOME_FIELDLABELCODE);
			operateFromHome.setOldValue(oldpmCategoryL3.getHomeOperated() == 1 ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			operateFromHome.setNewValue(pmCategoryL3.getHomeOperated() == 1 ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			operateFromHome.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(operateFromHome);
		}
	}

	/**
	 * @param manageBean
	 * @param auditPMReqDTO
	 */
	private void addBusinessComType(CategoryManagedBean managedBean) {
		List<PmCategoryRestr> newCategoryRestr = pmCategoryL3.getPmCategoryRestrs();
		PmCategoryL3 updatedCategoryL3 = managedBean.getAuditUpdatedCategoryL3();
		List<PmCategoryRestr> savedCategoryRestrList = updatedCategoryL3.getPmCategoryRestrs();
		this.actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;

		if (CollectionUtils.isNotEmpty(managedBean.getCategoryRestrList())) {
			for (PmCategoryRestr updatedCategoryRestr : newCategoryRestr) {
				for(PmCategoryRestr savedCatRestr : savedCategoryRestrList){
					if (updatedCategoryRestr.isBrandNew() && updatedCategoryRestr.getCompanyTypeId().getParameterId().equals(savedCatRestr.getCompanyTypeId().getParameterId())) {
						
						this.docId = BigInteger.valueOf(savedCatRestr.getCategoryRestrId());
						detailObj(AuditConst.PM_AUD_CCR_BUSS_COM_TYPE_FIELDLABELCODE, null, null, null, BigInteger.valueOf(updatedCategoryRestr.getCompanyTypeId()
								.getParameterId()));
						break;
					}
				}
			}
		}
	}

	/**
	 * @param manageBean
	 * @param auditPMReqDTO
	 */
	private void addSpecialEquipment(CategoryManagedBean managedBean) {
		List<PmMachineCategory> newPmMachine = pmCategoryL3.getPmMachineCategory();
		PmCategoryL3 updatedCategoryL3 = managedBean.getAuditUpdatedCategoryL3();
		List<PmMachineCategory> savedPmMachine = updatedCategoryL3.getPmMachineCategory();
		
		if (CollectionUtils.isNotEmpty(newPmMachine)) {
			for (PmMachineCategory updatedPmMachineCategory : newPmMachine) {
				for(PmMachineCategory savedPmMachineCategory : savedPmMachine) {
					if (updatedPmMachineCategory.isBrandNew() && updatedPmMachineCategory.getPmMachine().getMachineId().equals(savedPmMachineCategory.getPmMachine().getMachineId())) {
						
						AuditPMSPDTO machineCode = new AuditPMSPDTO();
						AuditPMSPDTO machineDesc = new AuditPMSPDTO();
						AuditPMSPDTO displaySequence = new AuditPMSPDTO(); // add/modify
						
						machineCode.setRoleCode(roleCode);
						machineCode.setDocType(docType3);
						machineCode.setDocNo(docNo);
						machineCode.setDocGroupId(docGroupId);
						machineCode.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
						machineCode.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
						machineCode.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
						machineCode.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
						machineCode.setDocId(BigInteger.valueOf(savedPmMachineCategory.getMachineCategoryId()));
						machineCode.setFieldLabelCode(AuditConst.PM_AUD_CCR_MACHINE_CODE_FIELDLABELCODE);
						machineCode.setNewValue(updatedPmMachineCategory.getPmMachine().getMachineCode());
						machineCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(machineCode);

						machineDesc.setRoleCode(roleCode);
						machineDesc.setDocType(docType3);
						machineDesc.setDocNo(docNo);
						machineDesc.setDocGroupId(docGroupId);
						machineDesc.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
						machineDesc.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
						machineDesc.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
						machineDesc.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
						machineDesc.setDocId(BigInteger.valueOf(savedPmMachineCategory.getMachineCategoryId()));
						machineDesc.setFieldLabelCode(AuditConst.PM_AUD_CCR_MACHINE_DESC_FIELDLABELCODE);
						machineDesc.setNewValue(updatedPmMachineCategory.getPmMachine().getMachineDesc());
						machineDesc.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(machineDesc);

						String typeCode = pmCategoryL3.getCategoryTypeCode().getParameterCode();
						// site visit
						this.docId = BigInteger.valueOf(savedPmMachineCategory.getMachineCategoryId());
						setSiteVisit(updatedPmMachineCategory, typeCode);

						displaySequence.setRoleCode(roleCode);
						displaySequence.setDocType(docType3);
						displaySequence.setDocNo(docNo);
						displaySequence.setDocGroupId(docGroupId);
						displaySequence.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
						displaySequence.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
						displaySequence.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
						displaySequence.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
						displaySequence.setDocId(BigInteger.valueOf(savedPmMachineCategory.getMachineCategoryId()));
						displaySequence.setFieldLabelCode(AuditConst.PM_AUD_CCR_DISP_SEQ_FIELDLABELCODE);
						displaySequence.setNewValue(String.valueOf(updatedPmMachineCategory.getDisplaySeq()));
						displaySequence.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(displaySequence);
						break;
					}
				}
			}
		}
	}

	private void setSiteVisit(PmMachineCategory pmMachineCategory, String categoryTypeCode) {
		// 243, 245, 248
		this.actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;

		if (categoryTypeCode.equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_WORKSHOP)
				|| categoryTypeCode.equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_DOCKYARD)) {
			if (pmMachineCategory.getDockYardSiteVisit() != null)
				detailObj(AuditConst.PM_AUD_CCR_SITE_VISIT_TOOL_FIELDLABELCODE, null, AuditConst.PM_AUD_YES, null, null);
			else
				detailObj(AuditConst.PM_AUD_CCR_SITE_VISIT_TOOL_FIELDLABELCODE, null, AuditConst.PM_AUD_NO, null, null);

			if (pmMachineCategory.getWorkshopSiteVisit() != null)
				detailObj(AuditConst.PM_AUD_CCR_SITE_VISIT_EQUIPMENT_FIELDLABELCODE, null, AuditConst.PM_AUD_YES, null, null);

			else
				detailObj(AuditConst.PM_AUD_CCR_SITE_VISIT_EQUIPMENT_FIELDLABELCODE, null, AuditConst.PM_AUD_NO, null, null);
		}

		if (categoryTypeCode.equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_PRINTING)) {
			if (pmMachineCategory.getPrintingSiteVisit() != null)
				detailObj(AuditConst.PM_AUD_CCR_SITE_VISIT_ACCESSORIES_FIELDLABELCODE, null, AuditConst.PM_AUD_YES, null, null);
			else
				detailObj(AuditConst.PM_AUD_CCR_SITE_VISIT_ACCESSORIES_FIELDLABELCODE, null, AuditConst.PM_AUD_NO, null, null);
		}
	}

	/**
	 * @param manageBean
	 * @param auditPMReqDTO
	 */
	private void addRequiredCategory(CategoryManagedBean managedBean) {

		PmCategoryL3 updatedCategoryL3 = managedBean.getAuditUpdatedCategoryL3();
		List<PmBasicCategory> updatedBasicCategory = updatedCategoryL3.getPmBasicCategory();
		List<PmBasicCategory> newBasicCategory = pmCategoryL3.getPmBasicCategory();

		this.actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
		
		if (CollectionUtils.isNotEmpty(newBasicCategory)) {
			for (PmBasicCategory basicCategory : newBasicCategory) {
				for (PmBasicCategory updateIdBasicCategory : updatedBasicCategory) {
					if (basicCategory.isBrandNew()
							&& updateIdBasicCategory.getBasicCategoryL3().equals(basicCategory.getBasicCategoryL3())) {
						
						this.docId = BigInteger.valueOf(updateIdBasicCategory.getBasicCategoryId());
						detailObj(AuditConst.PM_AUD_CCR_CATEGORY_CODE_FIELDLABELCODE, null, basicCategory.getCodeConcatenate(), null, null);
						
						detailObj(AuditConst.PM_AUD_CCR_MAIN_DESC_FIELDLABELCODE, null, basicCategory.getBasicCategoryL1desc(), null, null);
						
						detailObj(AuditConst.PM_AUD_CCR_SUB_DESC_FIELDLABELCODE, null, basicCategory.getBasicCategoryL2desc(), null, BigInteger.valueOf(basicCategory.getBasicCategoryL2()));
						
						detailObj(AuditConst.PM_AUD_CCR_SUB_DIV_DESC_FIELDLABELCODE, null, basicCategory.getBasicCategoryL3desc(), null, BigInteger.valueOf(basicCategory.getBasicCategoryL3()));
						
						break;
					}
				}
			}
		}
	}

	private ArrayList<AuditPMSPDTO> detailObj(BigInteger fieldLabelId, String oldValue, String newValue, BigInteger oldValueId, BigInteger newValueId) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(docType3);
		dtlObj.setDocNo(docNo);
		dtlObj.setPmParameterLvlId(parameterLvlIdL3);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
		dtlObj.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
		dtlObj.setDocId(docId);
		dtlObj.setDocGroupId(docGroupId);
		dtlObj.setFieldLabelCode(fieldLabelId);
		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}
		
		if (oldValueId != null) {
			dtlObj.setOldValueId(oldValueId);
		}
		
		if (newValue != null) {
			dtlObj.setNewValue(newValue);
		}
		
		if (newValueId != null) {
			dtlObj.setNewValueId(newValueId);
		}
		
		dtlObj.setActionType(actionType);
		objList.add(dtlObj);
		return objList;
	}

	private ArrayList<AuditPMSPDTO> detailDeleteObj(BigInteger fieldLabelId, String oldValue, BigInteger oldValueId) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(docType3);
		dtlObj.setDocNo(docNo);
		dtlObj.setPmParameterLvlId(parameterLvlIdL3);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
		dtlObj.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
		dtlObj.setDocId(docId);
		dtlObj.setDocGroupId(docGroupId);
		dtlObj.setFieldLabelCode(fieldLabelId);

		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}

		if (oldValueId != null) {
			dtlObj.setOldValueId(oldValueId);
		}
		dtlObj.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);
		objList.add(dtlObj);
		return objList;
	}

	private void deleteRequiredCategory(CategoryManagedBean managedBean) {
		if (CollectionUtils.isNotEmpty(managedBean.getDeletedpmBasicCategory())) {
			for (PmBasicCategory deleteBasicCategory : managedBean.getDeletedpmBasicCategory()) {
				this.docId = BigInteger.valueOf(deleteBasicCategory.getBasicCategoryId());
				detailDeleteObj(AuditConst.PM_AUD_CCR_CATEGORY_CODE_FIELDLABELCODE,
						deleteBasicCategory.getCodeConcatenate(), null);

				detailDeleteObj(AuditConst.PM_AUD_CCR_MAIN_DESC_FIELDLABELCODE,
						deleteBasicCategory.getBasicCategoryL1desc(),
						BigInteger.valueOf(deleteBasicCategory.getBasicCategoryL1()));

				detailDeleteObj(AuditConst.PM_AUD_CCR_SUB_DESC_FIELDLABELCODE,
						deleteBasicCategory.getBasicCategoryL2desc(),
						BigInteger.valueOf(deleteBasicCategory.getBasicCategoryL2()));

				detailDeleteObj(AuditConst.PM_AUD_CCR_SUB_DIV_DESC_FIELDLABELCODE,
						deleteBasicCategory.getBasicCategoryL3desc(),
						BigInteger.valueOf(deleteBasicCategory.getBasicCategoryL3()));
			}
		}
	}

	private void deleteBusinessComType(CategoryManagedBean managedBean) {
		if (CollectionUtils.isNotEmpty(managedBean.getDeletedpmRestrCategory())) {
			for (PmCategoryRestr deleteCategoryRestr : managedBean.getDeletedpmRestrCategory()) {
				this.docId = BigInteger.valueOf(deleteCategoryRestr.getCategoryRestrId());
				detailDeleteObj(AuditConst.PM_AUD_CCR_BUSS_COM_TYPE_FIELDLABELCODE, null,
						BigInteger.valueOf(deleteCategoryRestr.getCompanyTypeId().getParameterId()));

			}
		}
	}
	
	private String checkPmParameterDesc(PmParameter pmParameter) {
		String description = null;

		if(CollectionUtils.isNotEmpty(pmParameter.getPmParameterDescsSpecCallOnly())) {
			for (PmParameterDesc desc : pmParameter.getPmParameterDescsSpecCallOnly()) {
				if (desc.getLanguageCode().equalsIgnoreCase(AuditConst.PM_LANGUAGECODE_MS)) {
					description = desc.getCodeName();
				}
			}
		}
		
		return description;

	}
	
	private void updateSpecialEquipment(CategoryManagedBean managedBean) {
		PmCategoryL3 updatedCategoryL3 = managedBean.getAuditUpdatedCategoryL3();
		List<PmMachineCategory> savedPmMachines = updatedCategoryL3.getPmMachineCategory();
		List<PmMachineCategory> oldPmMachines = managedBean.getAuditOldCategoryL3().getPmMachineCategory();
		this.actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
		
		if (CollectionUtils.isNotEmpty(savedPmMachines) && CollectionUtils.isNotEmpty(oldPmMachines)) {
			for (PmMachineCategory savedPmMachine : savedPmMachines) {
				for (PmMachineCategory oldPmMachine : oldPmMachines) {
					this.docId = BigInteger.valueOf(savedPmMachine.getMachineCategoryId());
					// check site visit && display sequence
					if(savedPmMachine.getMachineCategoryId().equals(oldPmMachine.getMachineCategoryId())){
						String typeCode = pmCategoryL3.getCategoryTypeCode().getParameterCode();
						updateSiteVisit(oldPmMachine, savedPmMachine, typeCode);
						
						break;
					}
				}
			}
		}
	}
	
	private void updateSiteVisit(PmMachineCategory oldPmMachine, PmMachineCategory savedPmMachine, String categoryTypeCode) {
		// 243, 245, 248

		if (ParameterTypeConst.SPECIAL_CATEGORY_TYPE_WORKSHOP.equals(categoryTypeCode)
		        || ParameterTypeConst.SPECIAL_CATEGORY_TYPE_DOCKYARD.equals(categoryTypeCode)) {

		    compareAndAuditSiteVisit(
		        savedPmMachine.getDockYardSiteVisit(),
		        oldPmMachine.getDockYardSiteVisit(),
		        AuditConst.PM_AUD_CCR_SITE_VISIT_TOOL_FIELDLABELCODE
		    );

		    compareAndAuditSiteVisit(
		        savedPmMachine.getWorkshopSiteVisit(),
		        oldPmMachine.getWorkshopSiteVisit(),
		        AuditConst.PM_AUD_CCR_SITE_VISIT_EQUIPMENT_FIELDLABELCODE
		    );
		}

		if (ParameterTypeConst.SPECIAL_CATEGORY_TYPE_PRINTING.equals(categoryTypeCode)) {
		    compareAndAuditSiteVisit(
		        savedPmMachine.getPrintingSiteVisit(),
		        oldPmMachine.getPrintingSiteVisit(),
		        AuditConst.PM_AUD_CCR_SITE_VISIT_ACCESSORIES_FIELDLABELCODE
		    );
		}
		
		if (!savedPmMachine.getDisplaySeq().equals(oldPmMachine.getDisplaySeq())) {
			detailObj(AuditConst.PM_AUD_CCR_DISP_SEQ_FIELDLABELCODE,
					String.valueOf(oldPmMachine.getDisplaySeq()),
					String.valueOf(savedPmMachine.getDisplaySeq()), null, null);
		}

	}
	
	private void compareAndAuditSiteVisit(Object newVal, Object oldVal, BigInteger fieldLabelCode) {
	    if (newVal != null && !newVal.equals(oldVal)) {
	        detailObj(fieldLabelCode, AuditConst.PM_AUD_NO, AuditConst.PM_AUD_YES, null, null);
	    } else if (oldVal != null && !oldVal.equals(newVal)) {
	        detailObj(fieldLabelCode, AuditConst.PM_AUD_YES, AuditConst.PM_AUD_NO, null, null);
	    }
	}

}
