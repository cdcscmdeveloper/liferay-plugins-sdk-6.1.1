package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmBasicCategory;
import my.ep.persistence.entity.pm.common.PmCategoryL1;
import my.ep.persistence.entity.pm.common.PmCategoryL2;
import my.ep.persistence.entity.pm.common.PmCategoryL3;
import my.ep.persistence.entity.pm.common.PmCategoryRestr;
import my.ep.persistence.entity.pm.common.PmMachineCategory;
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
	AuditPMSPDTO statusL1 = new AuditPMSPDTO();
	AuditPMSPDTO statusL2 = new AuditPMSPDTO();
	AuditPMSPDTO bumiPercentage = new AuditPMSPDTO();
	AuditPMSPDTO paidUpCapital = new AuditPMSPDTO();
	AuditPMSPDTO supplierType = new AuditPMSPDTO(); // type ada id
	AuditPMSPDTO statusL3 = new AuditPMSPDTO();
	AuditPMSPDTO requireOtherCategory = new AuditPMSPDTO();
	AuditPMSPDTO bussCompRestriction = new AuditPMSPDTO();
	AuditPMSPDTO operateFromHome = new AuditPMSPDTO();

	AuditPMSPDTO addCategoryCode = new AuditPMSPDTO();
	AuditPMSPDTO addMainCategoryDesc = new AuditPMSPDTO();
	AuditPMSPDTO addSubCategoryDesc = new AuditPMSPDTO();
	AuditPMSPDTO addSubDivisionDesc = new AuditPMSPDTO();
	
	AuditPMSPDTO deleteCategoryCode = new AuditPMSPDTO();
	AuditPMSPDTO deleteMainCategoryDesc = new AuditPMSPDTO();
	AuditPMSPDTO deleteSubCategoryDesc = new AuditPMSPDTO();
	AuditPMSPDTO deleteSubDivisionDesc = new AuditPMSPDTO();

	AuditPMSPDTO machineCode = new AuditPMSPDTO();
	AuditPMSPDTO machineDesc = new AuditPMSPDTO();
	AuditPMSPDTO siteVisitSection = new AuditPMSPDTO();
	AuditPMSPDTO displaySequence = new AuditPMSPDTO(); // add/modify

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleReasonMaintenance();
	}

	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		CategoryManagedBean managedBean = (CategoryManagedBean) manageBean;

		if (managedBean.getAuditLevelCategory().equalsIgnoreCase(AuditConst.PM_LVL_CODE_3_CCR_CATL1)) {
			updateDetailPmCategoryL1(managedBean, roleCode, auditPMReqDTO);
		} else if (managedBean.getAuditLevelCategory().equalsIgnoreCase(AuditConst.PM_LVL_CODE_3_CCR_CATL2)) {
			updateDetailPmCategoryL2(managedBean, roleCode, auditPMReqDTO);
		} else if (managedBean.getAuditLevelCategory().equalsIgnoreCase(AuditConst.PM_LVL_CODE_3_CCR_CATL3)) {
			setCategoryL3(managedBean, auditPMReqDTO, roleCode);

		}
		managedBean.setAuditOldCategoryL1(null);
		managedBean.setAuditOldCategoryL2(null);
		managedBean.setAuditOldCategoryL3(null);
		managedBean.setAuditLevelCategory(null);
		return auditPMReqDTO;
	}

	private void updateDetailPmCategoryL1(CategoryManagedBean managedBean, String roleCode, AuditPMReqDTO auditPMReqDTO) {
		PmCategoryL1 oldPmCategoryL1 = managedBean.getAuditOldCategoryL1();
		PmCategoryL1 pmCategoryL1 = managedBean.getPmCategoryL1();

		statusL1.setRoleCode(roleCode);
		statusL1.setDocType(managedBean.getCatLevel());
		statusL1.setDocNo(pmCategoryL1.getCategoryL1Code());
		statusL1.setDocGroupId(BigInteger.valueOf(pmCategoryL1.getCategoryL1Id()));
		statusL1.setPmParameterLvlId(AuditConst.PM_AUD_CCR_MAIN_PMPARAMETERLVLID);
		statusL1.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
		statusL1.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
		statusL1.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL1);
		statusL1.setDocId(BigInteger.valueOf(pmCategoryL1.getCategoryL1Id()));
		statusL1.setFieldLabelCode(AuditConst.PM_AUD_CCR_MAIN_STATUS_FIELDLABELCODE);
		statusL1.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);

		if (managedBean.isNewCategory()) {
			statusL1.setNewValue(pmCategoryL1.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);

		} else {
			if (pmCategoryL1.getRecordStatusFlg() != oldPmCategoryL1.getRecordStatusFlg()) {
				statusL1.setOldValue(oldPmCategoryL1.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				statusL1.setNewValue(pmCategoryL1.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
			}
		}
		objList.add(statusL1);
		auditPMReqDTO.setObjList(objList);
	}

	private void updateDetailPmCategoryL2(CategoryManagedBean managedBean, String roleCode, AuditPMReqDTO auditPMReqDTO) {
		PmCategoryL2 pmCategoryL2 = managedBean.getPmCategoryL2();
		PmCategoryL2 oldPmCategoryL2 = managedBean.getAuditOldCategoryL2();

		statusL2.setRoleCode(roleCode);
		statusL2.setDocType(managedBean.getCatLevel());
		statusL2.setDocNo(pmCategoryL2.getCategoryL2Code());
		statusL1.setDocGroupId(BigInteger.valueOf(pmCategoryL2.getPmCategoryL1().getCategoryL1Id()));
		statusL2.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_PMPARAMETERLVLID);
		statusL2.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
		statusL2.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
		statusL2.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL2);
		statusL2.setDocId(BigInteger.valueOf(pmCategoryL2.getCategoryL2Id()));
		statusL2.setFieldLabelCode(AuditConst.PM_AUD_CCR_SUB_STATUS_FIELDLABELCODE);
		statusL2.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);

		if (managedBean.isNewCategory()) {
			statusL2.setNewValue(pmCategoryL2.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);

		} else {
			statusL2.setOldValue(oldPmCategoryL2.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			statusL2.setNewValue(pmCategoryL2.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
		}

		objList.add(statusL2);
		auditPMReqDTO.setObjList(objList);
	}

	private void setCategoryL3(CategoryManagedBean managedBean, AuditPMReqDTO auditPMReqDTO, String roleCode) {

		PmCategoryL3 pmCategoryL3 = managedBean.getPmCategoryL3();
		PmCategoryL3 oldpmCategoryL3 = managedBean.getAuditOldCategoryL3();
		// add delete required cat
		// add delete business type
		// add modify special equipment

		if (managedBean.isNewCategory()) {
			// new for
			// paidUpCapital
			// bumiPercentage
			// supplierType
			// statusL3
			//
			// checkbox - requireOtherCategory, bussCompRestriction, operateFromHome

		} else {
			setCategoryL3CheckBox(managedBean, auditPMReqDTO, roleCode);
			addRequiredCategory(managedBean, auditPMReqDTO, roleCode);
			addBusinessComType(managedBean, auditPMReqDTO, roleCode);
			addSpecialEquipment(managedBean, auditPMReqDTO, roleCode);
			
			if (pmCategoryL3.getPaidUpCapital() != oldpmCategoryL3.getPaidUpCapital()) {
				paidUpCapital.setRoleCode(roleCode);
				paidUpCapital.setDocType(managedBean.getCatLevel());
				paidUpCapital.setDocNo(pmCategoryL3.getCategoryL3Code());
				paidUpCapital.setDocGroupId(new BigInteger(pmCategoryL3.getPmCategoryL2().getPmCategoryL1()
						.getCategoryL1Code()));
				paidUpCapital.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
				paidUpCapital.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
				paidUpCapital.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
				paidUpCapital.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
				paidUpCapital.setDocId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
				paidUpCapital.setFieldLabelCode(AuditConst.PM_AUD_CCR_PAID_CAPITAL_FIELDLABELCODE);
				paidUpCapital.setOldValue(String.valueOf(oldpmCategoryL3.getPaidUpCapital()));
				paidUpCapital.setNewValue(String.valueOf(pmCategoryL3.getPaidUpCapital()));
				paidUpCapital.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(paidUpCapital);
			}

			if (pmCategoryL3.getBumiPercentage() != oldpmCategoryL3.getBumiPercentage()) {
				bumiPercentage.setRoleCode(roleCode);
				bumiPercentage.setDocType(managedBean.getCatLevel());
				bumiPercentage.setDocNo(pmCategoryL3.getCategoryL3Code());
				bumiPercentage.setDocGroupId(new BigInteger(pmCategoryL3.getPmCategoryL2().getPmCategoryL1()
						.getCategoryL1Code()));
				bumiPercentage.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
				bumiPercentage.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
				bumiPercentage.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
				bumiPercentage.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
				bumiPercentage.setDocId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
				bumiPercentage.setFieldLabelCode(AuditConst.PM_AUD_CCR_BUMI_PERCENTAGE_FIELDLABELCODE);
				bumiPercentage.setOldValue(String.valueOf(oldpmCategoryL3.getBumiPercentage()));
				bumiPercentage.setNewValue(String.valueOf(pmCategoryL3.getBumiPercentage()));
				bumiPercentage.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(bumiPercentage);
			}

			if (pmCategoryL3.getSupplierTypeCode().getParameterId() != oldpmCategoryL3.getSupplierTypeCode()
					.getParameterId()) {
				supplierType.setRoleCode(roleCode);
				supplierType.setDocType(managedBean.getCatLevel());
				supplierType.setDocNo(pmCategoryL3.getCategoryL3Code());
				supplierType.setDocGroupId(new BigInteger(pmCategoryL3.getPmCategoryL2().getPmCategoryL1()
						.getCategoryL1Code()));
				supplierType.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
				supplierType.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
				supplierType.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
				supplierType.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
				supplierType.setDocId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
				supplierType.setFieldLabelCode(AuditConst.PM_AUD_CCR_BUMI_PERCENTAGE_FIELDLABELCODE);
				supplierType.setOldValue(oldpmCategoryL3.getSupplierTypeCode().getPmParameterDescsSpecCallOnly().get(0)
						.getCodeDesc());
				supplierType.setNewValue(pmCategoryL3.getSupplierTypeCode().getPmParameterDescsSpecCallOnly().get(0)
						.getCodeDesc());
				supplierType.setOldValueId(BigInteger.valueOf(oldpmCategoryL3.getSupplierTypeCode().getParameterId()));
				supplierType.setNewValueId(BigInteger.valueOf(pmCategoryL3.getSupplierTypeCode().getParameterId()));
				supplierType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(supplierType);
			}

			if (pmCategoryL3.getRecordStatusFlg() != oldpmCategoryL3.getRecordStatusFlg()) {
				statusL3.setRoleCode(roleCode);
				statusL3.setDocType(managedBean.getCatLevel());
				statusL3.setDocNo(pmCategoryL3.getCategoryL3Code());
				statusL3.setDocGroupId(new BigInteger(pmCategoryL3.getPmCategoryL2().getPmCategoryL1()
						.getCategoryL1Code()));
				statusL3.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
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

		auditPMReqDTO.setObjList(objList);
	}

	private void setCategoryL3CheckBox(CategoryManagedBean managedBean, AuditPMReqDTO auditPMReqDTO, String roleCode) {
		PmCategoryL3 pmCategoryL3 = managedBean.getPmCategoryL3();
		PmCategoryL3 oldpmCategoryL3 = managedBean.getAuditOldCategoryL3();

		if (pmCategoryL3.getBasicCategoryReq() != oldpmCategoryL3.getBasicCategoryReq()) {
			requireOtherCategory.setRoleCode(roleCode);
			requireOtherCategory.setDocType(managedBean.getCatLevel());
			requireOtherCategory.setDocNo(pmCategoryL3.getCategoryL3Code());
			requireOtherCategory.setDocGroupId(new BigInteger(pmCategoryL3.getPmCategoryL2().getPmCategoryL1()
					.getCategoryL1Code()));
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
			bussCompRestriction.setDocType(managedBean.getCatLevel());
			bussCompRestriction.setDocNo(pmCategoryL3.getCategoryL3Code());
			bussCompRestriction.setDocGroupId(new BigInteger(pmCategoryL3.getPmCategoryL2().getPmCategoryL1()
					.getCategoryL1Code()));
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

		if (pmCategoryL3.getHomeOperated() != oldpmCategoryL3.getHomeOperated()) {
			operateFromHome.setRoleCode(roleCode);
			operateFromHome.setDocType(managedBean.getCatLevel());
			operateFromHome.setDocNo(pmCategoryL3.getCategoryL3Code());
			operateFromHome.setDocGroupId(new BigInteger(pmCategoryL3.getPmCategoryL2().getPmCategoryL1()
					.getCategoryL1Code()));
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
		auditPMReqDTO.setObjList(objList);
	}

	private void addBusinessComType(CategoryManagedBean managedBean, AuditPMReqDTO auditPMReqDTO, String roleCode){
		// addBussCompType
		// add PM_ CATEGORY_RESTR
		PmCategoryL3 pmCategoryL3 = managedBean.getPmCategoryL3(); //
		List<PmCategoryRestr> newCategoryRestr = pmCategoryL3.getPmCategoryRestrs();
		
		if (managedBean.getCategoryRestrList() != null) {
			for (PmCategoryRestr addCategoryRestr : managedBean.getCategoryRestrList()){
				for (PmCategoryRestr updatedCategoryRestr : newCategoryRestr){
					if(addCategoryRestr.getCompanyTypeId().getParameterId() == updatedCategoryRestr.getCompanyTypeId().getParameterId()){
						AuditPMSPDTO addBussCompType = new AuditPMSPDTO();
						addBussCompType.setRoleCode(roleCode);
						addBussCompType.setDocType(managedBean.getCatLevel());
						addBussCompType.setDocNo(pmCategoryL3.getCategoryL3Code());
						addBussCompType.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
						addBussCompType.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
						addBussCompType.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
						addBussCompType.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
						addBussCompType.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
						addBussCompType.setDocId(BigInteger.valueOf(updatedCategoryRestr.getCategoryRestrId()));
						addBussCompType.setFieldLabelCode(AuditConst.PM_AUD_CCR_BUSS_COM_TYPE_FIELDLABELCODE);
						addBussCompType.setNewValue(updatedCategoryRestr.getCompanyTypeId().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc());
						addBussCompType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(addBussCompType);
					}
				}
			}
		}
		
		auditPMReqDTO.setObjList(objList);
	}
	
	private void addSpecialEquipment(CategoryManagedBean managedBean, AuditPMReqDTO auditPMReqDTO, String roleCode){
		// add PM_ MACHINE_CATEGORY
//		AuditPMSPDTO machineCode = new AuditPMSPDTO();
//		AuditPMSPDTO machineDesc = new AuditPMSPDTO();
//		AuditPMSPDTO siteVisitSection = new AuditPMSPDTO();
//		AuditPMSPDTO displaySequence = new AuditPMSPDTO();
		PmCategoryL3 pmCategoryL3 = managedBean.getPmCategoryL3();
		List<PmMachineCategory> newPmMachine = pmCategoryL3.getPmMachineCategory();
		
		if(managedBean.getTobeAddedPmMachineCategoryList() != null) {
			for(PmMachineCategory addPmMachineCategory : managedBean.getTobeAddedPmMachineCategoryList()){
				for(PmMachineCategory updatedPmMachineCategory : newPmMachine){
					if(addPmMachineCategory.getPmMachine().getMachineId() == updatedPmMachineCategory.getPmMachine().getMachineId()){
						machineCode.setRoleCode(roleCode);
						machineCode.setDocType(managedBean.getCatLevel());
						machineCode.setDocNo(pmCategoryL3.getCategoryL3Code());
						machineCode.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
						machineCode.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
						machineCode.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
						machineCode.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
						machineCode.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
						machineCode.setDocId(BigInteger.valueOf(updatedPmMachineCategory.getMachineCategoryId()));
						machineCode.setFieldLabelCode(AuditConst.PM_AUD_CCR_MACHINE_CODE_FIELDLABELCODE);
						machineCode.setNewValue(updatedPmMachineCategory.getPmMachine().getMachineCode());
						machineCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(machineCode);
						
						machineDesc.setRoleCode(roleCode);
						machineDesc.setDocType(managedBean.getCatLevel());
						machineDesc.setDocNo(pmCategoryL3.getCategoryL3Code());
						machineDesc.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
						machineDesc.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
						machineDesc.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
						machineDesc.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
						machineDesc.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
						machineDesc.setDocId(BigInteger.valueOf(updatedPmMachineCategory.getMachineCategoryId()));
						machineDesc.setFieldLabelCode(AuditConst.PM_AUD_CCR_MACHINE_DESC_FIELDLABELCODE);
						machineDesc.setNewValue(updatedPmMachineCategory.getPmMachine().getMachineDesc());
						machineDesc.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(machineDesc);
						
						// site visit
//						machineCode.setRoleCode(roleCode);
//						machineCode.setDocType(managedBean.getCatLevel());
//						machineCode.setDocNo(pmCategoryL3.getCategoryL3Code());
//						machineCode.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
//						machineCode.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
//						machineCode.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
//						machineCode.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
//						machineCode.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
//						machineCode.setDocId(BigInteger.valueOf(updatedPmMachineCategory.getMachineCategoryId()));
//						machineCode.setFieldLabelCode(AuditConst.PM_AUD_CCR_MACHINE_CODE_FIELDLABELCODE);
//						machineCode.setNewValue(updatedPmMachineCategory.getPmMachine().getMachineCode());
//						machineCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
//						objList.add(machineCode);
						
						displaySequence.setRoleCode(roleCode);
						displaySequence.setDocType(managedBean.getCatLevel());
						displaySequence.setDocNo(pmCategoryL3.getCategoryL3Code());
						displaySequence.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
						displaySequence.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
						displaySequence.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
						displaySequence.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
						displaySequence.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
						displaySequence.setDocId(BigInteger.valueOf(updatedPmMachineCategory.getMachineCategoryId()));
						displaySequence.setFieldLabelCode(AuditConst.PM_AUD_CCR_MACHINE_CODE_FIELDLABELCODE);
						displaySequence.setNewValue(updatedPmMachineCategory.getPmMachine().getMachineCode());
						displaySequence.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(displaySequence);
					}
				}
			}
		}
		auditPMReqDTO.setObjList(objList);
	}
	
	private void addRequiredCategory(CategoryManagedBean managedBean, AuditPMReqDTO auditPMReqDTO, String roleCode) {

		PmCategoryL3 pmCategoryL3 = managedBean.getPmCategoryL3(); // new
		List<PmBasicCategory> newBasicCategory = pmCategoryL3.getPmBasicCategory();
		managedBean.getPmBasicCategoryList();
		if (managedBean.getPmBasicCategoryList() != null) {

			for(PmBasicCategory addBasicCategory : managedBean.getPmBasicCategoryList()) {

				for(PmBasicCategory updatedBasicCategory : newBasicCategory) {

					if(addBasicCategory.getBasicCategoryL3() == updatedBasicCategory.getBasicCategoryL3()) {
						addCategoryCode.setRoleCode(roleCode);
						addCategoryCode.setDocType(managedBean.getCatLevel());
						addCategoryCode.setDocNo(pmCategoryL3.getCategoryL3Code());
						addCategoryCode.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
						addCategoryCode.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
						addCategoryCode.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
						addCategoryCode.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
						addCategoryCode.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
						addCategoryCode.setDocId(BigInteger.valueOf(updatedBasicCategory.getBasicCategoryId()));
						addCategoryCode.setFieldLabelCode(AuditConst.PM_AUD_CCR_CATEGORY_CODE_FIELDLABELCODE);
						addCategoryCode.setNewValue(updatedBasicCategory.getBasicCategoryL1desc());
						addCategoryCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(addCategoryCode);

						addMainCategoryDesc.setRoleCode(roleCode);
						addMainCategoryDesc.setDocType(managedBean.getCatLevel());
						addMainCategoryDesc.setDocNo(pmCategoryL3.getCategoryL3Code());
						addMainCategoryDesc.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
						addMainCategoryDesc.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
						addMainCategoryDesc.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
						addMainCategoryDesc.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
						addMainCategoryDesc.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
						addMainCategoryDesc.setDocId(BigInteger.valueOf(updatedBasicCategory.getBasicCategoryId()));
						addMainCategoryDesc.setFieldLabelCode(AuditConst.PM_AUD_CCR_MAIN_DESC_FIELDLABELCODE);
						addMainCategoryDesc.setNewValue(updatedBasicCategory.getBasicCategoryL1desc());
						addMainCategoryDesc
								.setNewValueId(BigInteger.valueOf(updatedBasicCategory.getBasicCategoryL1()));
						addMainCategoryDesc.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(addMainCategoryDesc);

						addSubCategoryDesc.setRoleCode(roleCode);
						addSubCategoryDesc.setDocType(managedBean.getCatLevel());
						addSubCategoryDesc.setDocNo(pmCategoryL3.getCategoryL3Code());
						addSubCategoryDesc.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
						addSubCategoryDesc.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
						addSubCategoryDesc.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
						addSubCategoryDesc.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
						addSubCategoryDesc.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
						addSubCategoryDesc.setDocId(BigInteger.valueOf(updatedBasicCategory.getBasicCategoryId()));
						addSubCategoryDesc.setFieldLabelCode(AuditConst.PM_AUD_CCR_SUB_DESC_FIELDLABELCODE);
						addSubCategoryDesc.setNewValue(updatedBasicCategory.getBasicCategoryL2desc());
						addSubCategoryDesc.setNewValueId(BigInteger.valueOf(updatedBasicCategory.getBasicCategoryL2()));
						addSubCategoryDesc.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(addSubCategoryDesc);

						addSubDivisionDesc.setRoleCode(roleCode);
						addSubDivisionDesc.setDocType(managedBean.getCatLevel());
						addSubDivisionDesc.setDocNo(pmCategoryL3.getCategoryL3Code());
						addSubDivisionDesc.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
						addSubDivisionDesc.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
						addSubDivisionDesc.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
						addSubDivisionDesc.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
						addSubDivisionDesc.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
						addSubDivisionDesc.setDocId(BigInteger.valueOf(updatedBasicCategory.getBasicCategoryId()));
						addSubDivisionDesc.setFieldLabelCode(AuditConst.PM_AUD_CCR_SUB_DIV_DESC_FIELDLABELCODE);
						addSubDivisionDesc.setNewValue(updatedBasicCategory.getBasicCategoryL3desc());
						addSubDivisionDesc.setNewValueId(BigInteger.valueOf(updatedBasicCategory.getBasicCategoryL3()));
						addSubDivisionDesc.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(addSubDivisionDesc);
					}
				}
			}
		}

		auditPMReqDTO.setObjList(objList);
	}

}
