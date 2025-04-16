package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

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
	
	String roleCode;
	String docType3;
	BigInteger docId;
	String actionType;
	String docNo;
	BigInteger docGroupId;
	PmCategoryL3 pmCategoryL3;

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
		return auditPMReqDTO;
	}

	/**
	 * @param manageBean
	 * @param auditPMReqDTO
	 */
	private void updateDetailPmCategoryL1(CategoryManagedBean managedBean, AuditPMReqDTO auditPMReqDTO) {
		PmCategoryL1 oldPmCategoryL1 = managedBean.getAuditOldCategoryL1();
		PmCategoryL1 pmCategoryL1 = managedBean.getPmCategoryL1();

		statusL1.setRoleCode(roleCode);
		statusL1.setDocType(AuditConst.PM_AUD_CODE_CATEGORY_L1);
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
		managedBean.setAuditOldCategoryL1(null);
		auditPMReqDTO.setObjList(objList);
	}

	/**
	 * @param manageBean
	 * @param auditPMReqDTO
	 */
	private void updateDetailPmCategoryL2(CategoryManagedBean managedBean, AuditPMReqDTO auditPMReqDTO) {
		PmCategoryL2 pmCategoryL2 = managedBean.getPmCategoryL2();
		PmCategoryL2 oldPmCategoryL2 = managedBean.getAuditOldCategoryL2();

		statusL2.setRoleCode(roleCode);
		statusL2.setDocType(AuditConst.PM_AUD_CODE_CATEGORY_L2);
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
		managedBean.setAuditOldCategoryL2(null);
		auditPMReqDTO.setObjList(objList);
	}

	/**
	 * @param manageBean
	 * @param auditPMReqDTO
	 */
	private void updateDetailPmCategoryL3(CategoryManagedBean managedBean, AuditPMReqDTO auditPMReqDTO) {

		this.pmCategoryL3 = managedBean.getPmCategoryL3();
		this.docType3 = AuditConst.PM_AUD_CODE_CATEGORY_L3;
		this.docNo = pmCategoryL3.getCategoryL3Code();
		this.docId = BigInteger.valueOf(pmCategoryL3.getCategoryL3Id());
		this.docGroupId = BigInteger.valueOf(pmCategoryL3.getCategoryL3Id());
		
		if (managedBean.isNewCategory()) {
			this.actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;
			
			detailObj(AuditConst.PM_AUD_CCR_PAID_CAPITAL_FIELDLABELCODE, null, pmCategoryL3.getPaidUpCapital()
					.toString());

			detailObj(AuditConst.PM_AUD_CCR_BUMI_PERCENTAGE_FIELDLABELCODE, null, pmCategoryL3.getBumiPercentage()
					.toString());

			detailObj(AuditConst.PM_AUD_CCR_SUPPLIER_TYPE_FIELDLABELCODE, null, pmCategoryL3.getSupplierTypeCode()
					.getPmParameterDescs().get(1).getCodeName());

			detailObj(AuditConst.PM_AUD_CCR_SUB_DIVISION_STATUS_FIELDLABELCODE, null,
					pmCategoryL3.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);

			detailObj(AuditConst.PM_AUD_CCR_REQUIRE_OTHER_CAT_FIELDLABELCODE, null,
					pmCategoryL3.getBasicCategoryReq() == 1 ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			
			detailObj(AuditConst.PM_AUD_CCR_BUSS_COMP_RESTRICTION_FIELDLABELCODE, null,
					pmCategoryL3.isByCompType() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			
			detailObj(AuditConst.PM_AUD_CCR_OPERATE_FROM_HOME_FIELDLABELCODE, null,
					pmCategoryL3.getHomeOperated() == 1 ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);

		} else {
			PmCategoryL3 oldpmCategoryL3 = managedBean.getAuditOldCategoryL3();
			this.actionType = AuditConst.PM_AUD_ACTION_TYPE_UPD;
			setCategoryL3Detail(oldpmCategoryL3);
			setCategoryL3CheckBox(managedBean);

			if (!pmCategoryL3.getRecordStatusFlg().equals(oldpmCategoryL3.getRecordStatusFlg())) {
				statusL3.setRoleCode(roleCode);
				statusL3.setDocType(docType3);
				statusL3.setDocNo(pmCategoryL3.getCategoryL3Code());
				statusL3.setDocGroupId(new BigInteger(pmCategoryL3.getPmCategoryL2().getPmCategoryL1()
						.getCategoryL1Code()));
				statusL3.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
				statusL3.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
				statusL3.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
				statusL3.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
				statusL3.setDocId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
				statusL3.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
				statusL3.setFieldLabelCode(AuditConst.PM_AUD_CCR_SUB_DIVISION_STATUS_FIELDLABELCODE);
				statusL3.setOldValue(oldpmCategoryL3.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				statusL3.setNewValue(pmCategoryL3.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				statusL3.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(statusL3);
			}
		}

		deleteRequiredCategory(managedBean);
		deleteBusinessComType(managedBean);
		
		addRequiredCategory(managedBean);
		addBusinessComType(managedBean);
		addSpecialEquipment(managedBean);
		managedBean.setAuditOldCategoryL3(null);
		auditPMReqDTO.setObjList(objList);
	}
	
	private void setCategoryL3Detail(PmCategoryL3 oldpmCategoryL3){
		if (! pmCategoryL3.getPaidUpCapital().equals(oldpmCategoryL3.getPaidUpCapital())) {
			paidUpCapital.setRoleCode(roleCode);
			paidUpCapital.setDocType(docType3);
			paidUpCapital.setDocNo(pmCategoryL3.getCategoryL3Code());
			paidUpCapital.setDocGroupId(new BigInteger(pmCategoryL3.getPmCategoryL2().getPmCategoryL1()
					.getCategoryL1Code()));
			paidUpCapital.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
			paidUpCapital.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
			paidUpCapital.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
			paidUpCapital.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
			paidUpCapital.setDocId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			paidUpCapital.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			paidUpCapital.setFieldLabelCode(AuditConst.PM_AUD_CCR_PAID_CAPITAL_FIELDLABELCODE);
			paidUpCapital.setOldValue(String.valueOf(oldpmCategoryL3.getPaidUpCapital()));
			paidUpCapital.setNewValue(String.valueOf(pmCategoryL3.getPaidUpCapital()));
			paidUpCapital.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(paidUpCapital);
		}

		if (! pmCategoryL3.getBumiPercentage().equals(oldpmCategoryL3.getBumiPercentage())) {
			bumiPercentage.setRoleCode(roleCode);
			bumiPercentage.setDocType(docType3);
			bumiPercentage.setDocNo(pmCategoryL3.getCategoryL3Code());
			bumiPercentage.setDocGroupId(new BigInteger(pmCategoryL3.getPmCategoryL2().getPmCategoryL1()
					.getCategoryL1Code()));
			bumiPercentage.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
			bumiPercentage.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
			bumiPercentage.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
			bumiPercentage.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
			bumiPercentage.setDocId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			bumiPercentage.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			bumiPercentage.setFieldLabelCode(AuditConst.PM_AUD_CCR_BUMI_PERCENTAGE_FIELDLABELCODE);
			bumiPercentage.setOldValue(String.valueOf(oldpmCategoryL3.getBumiPercentage()));
			bumiPercentage.setNewValue(String.valueOf(pmCategoryL3.getBumiPercentage()));
			bumiPercentage.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(bumiPercentage);
		}

		if (pmCategoryL3.getSupplierTypeCode().getParameterId() != oldpmCategoryL3.getSupplierTypeCode()
				.getParameterId()) {
			supplierType.setRoleCode(roleCode);
			supplierType.setDocType(docType3);
			supplierType.setDocNo(pmCategoryL3.getCategoryL3Code());
			supplierType.setDocGroupId(new BigInteger(pmCategoryL3.getPmCategoryL2().getPmCategoryL1()
					.getCategoryL1Code()));
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

		if (pmCategoryL3.getBasicCategoryReq().equals(oldpmCategoryL3.getBasicCategoryReq())) {
			requireOtherCategory.setRoleCode(roleCode);
			requireOtherCategory.setDocType(docType3);
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
			bussCompRestriction.setDocType(docType3);
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

		if (pmCategoryL3.getHomeOperated().equals(oldpmCategoryL3.getHomeOperated())) {
			operateFromHome.setRoleCode(roleCode);
			operateFromHome.setDocType(managedBean.getCatLevel());
			operateFromHome.setDocNo(docType3);
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
	}

	/**
	 * @param manageBean
	 * @param auditPMReqDTO
	 */
	private void addBusinessComType(CategoryManagedBean managedBean){
		List<PmCategoryRestr> newCategoryRestr = pmCategoryL3.getPmCategoryRestrs();
		
		if (CollectionUtils.isNotEmpty(managedBean.getCategoryRestrList())) {
			for (PmCategoryRestr updatedCategoryRestr : newCategoryRestr){
				if(updatedCategoryRestr.isBrandNew()){
					AuditPMSPDTO addBussCompType = new AuditPMSPDTO();
					addBussCompType.setRoleCode(roleCode);
					addBussCompType.setDocType(docType3);
					addBussCompType.setDocNo(pmCategoryL3.getCategoryL3Code());
					addBussCompType.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
					addBussCompType.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
					addBussCompType.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
					addBussCompType.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
					addBussCompType.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
					addBussCompType.setDocId(BigInteger.valueOf(updatedCategoryRestr.getCategoryRestrId()));
					addBussCompType.setFieldLabelCode(AuditConst.PM_AUD_CCR_BUSS_COM_TYPE_FIELDLABELCODE);
					/**addBussCompType.setNewValue(updatedCategoryRestr.getCompanyTypeId().getPmParameterDescsSpecCallOnly().get(1).getCodeDesc());**/
					addBussCompType.setNewValueId(BigInteger.valueOf(updatedCategoryRestr.getCompanyTypeId().getParameterId()));
					addBussCompType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
					objList.add(addBussCompType);
				}
			}
		}
	}
	
	/**
	 * @param manageBean
	 * @param auditPMReqDTO
	 */
	private void addSpecialEquipment(CategoryManagedBean managedBean){
		List<PmMachineCategory> newPmMachine = pmCategoryL3.getPmMachineCategory();
		
		for(PmMachineCategory updatedPmMachineCategory : newPmMachine){
			if(updatedPmMachineCategory.isBrandNew()){
				machineCode.setRoleCode(roleCode);
				machineCode.setDocType(docType3);
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
				machineDesc.setDocType(docType3);
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
				
				String typeCode = pmCategoryL3.getCategoryTypeCode().getParameterCode();
				// site visit
				setSiteVisit(updatedPmMachineCategory, typeCode);
				
				displaySequence.setRoleCode(roleCode);
				displaySequence.setDocType(docType3);
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
	
	private void setSiteVisit(PmMachineCategory pmMachineCategory, String categoryTypeCode) {
		// 243, 245, 248
		this.docId = BigInteger.valueOf(pmMachineCategory.getMachineCategoryId());
		this.actionType = AuditConst.PM_AUD_ACTION_TYPE_INS;

		if (categoryTypeCode.equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_WORKSHOP)
				|| categoryTypeCode.equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_DOCKYARD)) {
			if (pmMachineCategory.getDockYardSiteVisit() != null)
				detailObj(AuditConst.PM_AUD_CCR_SITE_VISIT_TOOL_FIELDLABELCODE, null, AuditConst.PM_AUD_YES);
			else
				detailObj(AuditConst.PM_AUD_CCR_SITE_VISIT_TOOL_FIELDLABELCODE, null, AuditConst.PM_AUD_NO);
			
			if (pmMachineCategory.getWorkshopSiteVisit() != null)
				detailObj(AuditConst.PM_AUD_CCR_SITE_VISIT_EQUIPMENT_FIELDLABELCODE, null, AuditConst.PM_AUD_YES);
			
			else
				detailObj(AuditConst.PM_AUD_CCR_SITE_VISIT_EQUIPMENT_FIELDLABELCODE, null, AuditConst.PM_AUD_NO);
		}

		if (categoryTypeCode.equals(ParameterTypeConst.SPECIAL_CATEGORY_TYPE_PRINTING)) {
			if (pmMachineCategory.getPrintingSiteVisit() != null)
				detailObj(AuditConst.PM_AUD_CCR_SITE_VISIT_ACCESSORIES_FIELDLABELCODE, null, AuditConst.PM_AUD_YES);
			else
				detailObj(AuditConst.PM_AUD_CCR_SITE_VISIT_ACCESSORIES_FIELDLABELCODE, null, AuditConst.PM_AUD_NO);
		}
	}
	
	/**
	 * @param manageBean
	 * @param auditPMReqDTO
	 */
	private void addRequiredCategory(CategoryManagedBean managedBean) {

	List<PmBasicCategory> newBasicCategory = pmCategoryL3.getPmBasicCategory();
	managedBean.getPmBasicCategoryList();
	for(PmBasicCategory updatedBasicCategory : newBasicCategory) {

		if(updatedBasicCategory.isBrandNew()) {
			addCategoryCode.setRoleCode(roleCode);
			addCategoryCode.setDocType(docType3);
			addCategoryCode.setDocNo(docNo);
			addCategoryCode.setDocGroupId(BigInteger.valueOf(pmCategoryL3.getCategoryL3Id()));
			addCategoryCode.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
			addCategoryCode.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
			addCategoryCode.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
			addCategoryCode.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
			addCategoryCode.setDocId(BigInteger.valueOf(updatedBasicCategory.getBasicCategoryId()));
			addCategoryCode.setFieldLabelCode(AuditConst.PM_AUD_CCR_CATEGORY_CODE_FIELDLABELCODE);
			addCategoryCode.setNewValue(updatedBasicCategory.getCodeConcatenate());
			addCategoryCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(addCategoryCode);

			addMainCategoryDesc.setRoleCode(roleCode);
			addMainCategoryDesc.setDocType(docType3);
			addMainCategoryDesc.setDocNo(docNo);
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
			addSubCategoryDesc.setDocType(docType3);
			addSubCategoryDesc.setDocNo(docNo);
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
			addSubDivisionDesc.setDocType(docType3);
			addSubDivisionDesc.setDocNo(docNo);
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
	
	private ArrayList<AuditPMSPDTO> detailObj(BigInteger fieldLabelId, String oldValue, String newValue) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(docType3);
		dtlObj.setDocNo(docNo);
		dtlObj.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_CCR);
		dtlObj.setLvlCode3(AuditConst.PM_LVL_CODE_3_CCR_CATL3);
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
	
	private ArrayList<AuditPMSPDTO> detailDeleteObj(BigInteger fieldLabelId, String oldValue, BigInteger oldValueId) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(docType3);
		dtlObj.setDocNo(docNo);
		dtlObj.setPmParameterLvlId(AuditConst.PM_AUD_CCR_SUB_DIVISION_PMPARAMETERLVLID);
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
		dtlObj.setActionType(actionType);
		objList.add(dtlObj);
		return objList;
	}
	
	private void deleteRequiredCategory(CategoryManagedBean managedBean) {
		// need to compare with PMCategoryL3 to get the desc
		
		this.docGroupId = BigInteger.valueOf(pmCategoryL3.getCategoryL3Id());
		this.actionType = AuditConst.PM_AUD_ACTION_TYPE_DEL;
		if (CollectionUtils.isNotEmpty(managedBean.getDeletedpmBasicCategory())) {
			for(PmBasicCategory deleteBasicCategory : managedBean.getDeletedpmBasicCategory()){
				this.docId = BigInteger.valueOf(deleteBasicCategory.getBasicCategoryId());
				
				detailDeleteObj(AuditConst.PM_AUD_CCR_CATEGORY_CODE_FIELDLABELCODE, deleteBasicCategory.getCodeConcatenate(), null);
				
				detailDeleteObj(AuditConst.PM_AUD_CCR_MAIN_DESC_FIELDLABELCODE, deleteBasicCategory.getBasicCategoryL1desc(), BigInteger.valueOf(deleteBasicCategory.getBasicCategoryL1()));
				
				detailDeleteObj(AuditConst.PM_AUD_CCR_SUB_DESC_FIELDLABELCODE, deleteBasicCategory.getBasicCategoryL2desc(), BigInteger.valueOf(deleteBasicCategory.getBasicCategoryL2()));
				
				detailDeleteObj(AuditConst.PM_AUD_CCR_SUB_DIV_DESC_FIELDLABELCODE, deleteBasicCategory.getBasicCategoryL3desc(), BigInteger.valueOf(deleteBasicCategory.getBasicCategoryL3()));
			}
		}
	}
	
	private void deleteBusinessComType(CategoryManagedBean managedBean) {
		this.actionType = AuditConst.PM_AUD_ACTION_TYPE_DEL;
		this.docGroupId = BigInteger.valueOf(pmCategoryL3.getCategoryL3Id());
		if (managedBean.getDeletedpmRestrCategory() != null) {
			for (PmCategoryRestr deleteCategoryRestr : managedBean.getDeletedpmRestrCategory()) {
				this.docId = BigInteger.valueOf(deleteCategoryRestr.getCategoryRestrId());
				detailDeleteObj(AuditConst.PM_AUD_CCR_BUSS_COM_TYPE_FIELDLABELCODE, null,
						BigInteger.valueOf(deleteCategoryRestr.getCompanyTypeId().getParameterId()));

			}
		}
	}

}
