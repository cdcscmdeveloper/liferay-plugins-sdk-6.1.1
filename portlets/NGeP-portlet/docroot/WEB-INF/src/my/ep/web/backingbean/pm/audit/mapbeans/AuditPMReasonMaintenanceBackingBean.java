package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmPIDReason;
import my.ep.persistence.entity.pm.common.PmReason;
import my.ep.persistence.entity.pm.common.PmReasonDesc;
import my.ep.persistence.entity.pm.common.PmSupplierReason;
import my.ep.type.pm.ReasonParameterLevel;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.common.rejectionreason.RejectionReasonManagedBean;

/**
 * AuditPMReasonMaintenanceBackingBean
 * 
 * @author haiza 28895_35509
 */
public class AuditPMReasonMaintenanceBackingBean  extends AuditPMBackingBeanTemplate{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO detailDescriptionMS = new AuditPMSPDTO();

	AuditPMSPDTO detailStatus = new AuditPMSPDTO();
	AuditPMSPDTO purchaseFromContract = new AuditPMSPDTO();
	AuditPMSPDTO contractOrCircular = new AuditPMSPDTO();
	AuditPMSPDTO sourcingMethod = new AuditPMSPDTO();
	AuditPMSPDTO reasonApplicability = new AuditPMSPDTO();
	
	AuditPMSPDTO restriction = new AuditPMSPDTO();
	AuditPMSPDTO restrictionDetails = new AuditPMSPDTO();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleReasonMaintenance();
	}

	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {

		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		RejectionReasonManagedBean managedBeans = (RejectionReasonManagedBean)manageBean;
		PmReason reason = managedBeans.getSelectedReason(); 
		
		String doctype = reason.getReasonCategoryCode().getPmParameterDescsSpecCallOnly().get(0).getCodeName();
		String parameterCode = reason.getReasonCategoryCode().getParameterCode();
		BigInteger parameterLevelId = new BigInteger(ReasonParameterLevel.valueOf(parameterCode).value());
		
		if(reason.isBrandNew()){
			PmReason newIdReason = managedBeans.getNewPmReason();// new added with updated id
			for(PmReasonDesc desc : reason.getPmReasonDescs()){
				
				detailDescriptionMS.setRoleCode(roleCode);
				detailDescriptionMS.setDocType(doctype);
				detailDescriptionMS.setDocNo(reason.getReasonCode());
				detailDescriptionMS.setDocGroupId(BigInteger.valueOf(newIdReason.getReasonId()));
				detailDescriptionMS.setPmParameterLvlId(parameterLevelId); // use enum
				detailDescriptionMS.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				detailDescriptionMS.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
				detailDescriptionMS.setLvlCode3(parameterCode);
				detailDescriptionMS.setDocId(BigInteger.valueOf(desc.getReasonDescId()));
				detailDescriptionMS.setFieldLabelCode("en".equals(desc.getLanguageCode()) ? 
						AuditConst.PM_AUD_RC_DESC_EN_FIELDLABELCODE : AuditConst.PM_AUD_RC_DESC_BM_FIELDLABELCODE);
				detailDescriptionMS.setNewValue(desc.getReasonDesc());
				detailDescriptionMS.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(detailDescriptionMS);
				auditPMReqDTO.setObjList(objList);
			}
			
			detailStatus.setRoleCode(roleCode);
			detailStatus.setDocType(doctype);
			detailStatus.setDocNo(reason.getReasonCode());
			detailStatus.setDocGroupId(BigInteger.valueOf(newIdReason.getReasonId()));
			detailStatus.setPmParameterLvlId(parameterLevelId);
			detailStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
			detailStatus.setLvlCode3(parameterCode);
			detailStatus.setDocId(BigInteger.valueOf(newIdReason.getReasonId()));
			detailStatus.setFieldLabelCode(AuditConst.PM_AUD_RC_STATUS_FIELDLABELCODE);
			detailStatus.setNewValue(reason.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailStatus);
			auditPMReqDTO.setObjList(objList);
			
			// create new PID
			// create new SRR
			
		}else {
			PmReason oldReason = managedBeans.getOldPmReason();
			PmReason existingReason = managedBeans.getSelectedReason();
			if(oldReason.getRecordStatusFlg() != existingReason.getRecordStatusFlg()){
				detailStatus.setRoleCode(roleCode);
				detailStatus.setDocType(doctype);
				detailStatus.setDocNo(existingReason.getReasonCode());
				detailStatus.setDocGroupId(BigInteger.valueOf(existingReason.getReasonId()));
				detailStatus.setPmParameterLvlId(parameterLevelId);
				detailStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				detailStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
				detailStatus.setLvlCode3(parameterCode);
				detailStatus.setDocId(BigInteger.valueOf(existingReason.getReasonId()));
				detailStatus.setFieldLabelCode(AuditConst.PM_AUD_RC_STATUS_FIELDLABELCODE);
				detailStatus.setOldValue(oldReason.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				detailStatus.setNewValue(existingReason.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				detailStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(detailStatus);
				auditPMReqDTO.setObjList(objList);
			}
			
			if(parameterCode.equalsIgnoreCase(ParameterConst.PURCHASE_IN_DEFAULT)){
				updatePID(oldReason, existingReason, auditPMReqDTO, roleCode, doctype, parameterCode);
				addPIDReason(existingReason, auditPMReqDTO, roleCode, doctype, parameterCode);
			}
			
			if(parameterCode.equalsIgnoreCase(ParameterConst.REASON_CATEGORY_SUPPLIER)){
				updateSRR(oldReason, existingReason, auditPMReqDTO, roleCode, doctype, parameterCode);
				addRestriction(existingReason, auditPMReqDTO, roleCode, doctype, parameterCode);
				if(managedBeans.getDeleteRestrictions() != null){
					deleteRestrictionDetail(managedBeans.getDeleteRestrictions(), existingReason, auditPMReqDTO, roleCode, doctype, parameterCode);
				}
			}
			
		}
		managedBeans.setOldPmReason(null);
		managedBeans.setNewPmReason(null);
		return auditPMReqDTO;
	}

	// create newPID
	private void updatePID(PmReason oldReason,  PmReason existingReason, AuditPMReqDTO auditPMReqDTO, String roleCode, String doctype, String parameterCode){
		if(oldReason.isContractPurchase() != existingReason.isContractPurchase()){
			
			purchaseFromContract.setRoleCode(roleCode);
			purchaseFromContract.setDocType(doctype);
			purchaseFromContract.setDocNo(existingReason.getReasonCode());
			purchaseFromContract.setDocGroupId(BigInteger.valueOf(existingReason.getReasonId()));
			purchaseFromContract.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode).value()));
			purchaseFromContract.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			purchaseFromContract.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
			purchaseFromContract.setLvlCode3(parameterCode);
			purchaseFromContract.setDocId(BigInteger.valueOf(existingReason.getReasonId()));
			purchaseFromContract.setFieldLabelCode(AuditConst.PM_AUD_RC_PURCHASE_FROM_CONTRACT_FIELDLABELCODE);
			purchaseFromContract.setOldValue(oldReason.isContractPurchase() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			purchaseFromContract.setNewValue(existingReason.isContractPurchase() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			purchaseFromContract.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(purchaseFromContract);
			auditPMReqDTO.setObjList(objList);
		}
		
		/** contract or circular criteria*/
		contractOrCircularCriteria(oldReason, existingReason, auditPMReqDTO, roleCode, doctype, parameterCode);
		
		if(existingReason.isContractPurchase() && (oldReason.getSourceMethod().getParameterId() !=  existingReason.getSourceMethod().getParameterId())){
			sourcingMethod.setRoleCode(roleCode);
			sourcingMethod.setDocType(doctype);
			sourcingMethod.setDocNo(existingReason.getReasonCode());
			sourcingMethod.setDocGroupId(BigInteger.valueOf(existingReason.getReasonId()));
			sourcingMethod.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode).value()));
			sourcingMethod.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			sourcingMethod.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
			sourcingMethod.setLvlCode3(parameterCode);
			sourcingMethod.setDocId(BigInteger.valueOf(existingReason.getReasonId()));
			sourcingMethod.setFieldLabelCode(AuditConst.PM_AUD_RC_SOURCE_METHOD_FIELDLABELCODE);
			sourcingMethod.setOldValue(String.valueOf(oldReason.getConditionField().getParameterId()));
			sourcingMethod.setNewValue(String.valueOf(existingReason.getConditionField().getParameterId()));
			sourcingMethod.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(sourcingMethod);
			auditPMReqDTO.setObjList(objList);
		}
	}
	
	private void contractOrCircularCriteria(PmReason oldReason, PmReason existingReason, AuditPMReqDTO auditPMReqDTO, String roleCode, String doctype, String parameterCode){
		
		contractOrCircular.setRoleCode(roleCode);
		contractOrCircular.setDocType(doctype);
		contractOrCircular.setDocNo(existingReason.getReasonCode());
		contractOrCircular.setDocGroupId(BigInteger.valueOf(existingReason.getReasonId()));
		contractOrCircular.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode).value()));
		contractOrCircular.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		contractOrCircular.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
		contractOrCircular.setLvlCode3(parameterCode);
		contractOrCircular.setDocId(BigInteger.valueOf(existingReason.getReasonId()));
		contractOrCircular.setFieldLabelCode(AuditConst.PM_AUD_RC_CONTRACT_CIRCULAR_FIELDLABELCODE);
		
		contractOrCircular.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
		
		if(existingReason.getConditionField().getParameterId()==null && oldReason.getConditionField().getParameterId() != null){
			contractOrCircular.setOldValue(String.valueOf(oldReason.getConditionField().getParameterId()));
			contractOrCircular.setNewValue(null);
			objList.add(contractOrCircular);
			auditPMReqDTO.setObjList(objList);
		}
		
		else if(existingReason.getConditionField().getParameterId()!=null && oldReason.getConditionField() == null){
			contractOrCircular.setOldValue(null);
			contractOrCircular.setNewValue(String.valueOf(existingReason.getConditionField().getParameterId()));
			objList.add(contractOrCircular);
			auditPMReqDTO.setObjList(objList);
		} 
		else if (existingReason.getConditionField().getParameterId()!=null && oldReason.getConditionField().getParameterId()!= null &&
				existingReason.getConditionField().getParameterId()!= oldReason.getConditionField().getParameterId()){
			contractOrCircular.setOldValue(String.valueOf(oldReason.getConditionField().getParameterId()));
			contractOrCircular.setNewValue(String.valueOf(existingReason.getConditionField().getParameterId()));
			objList.add(contractOrCircular);
			auditPMReqDTO.setObjList(objList);
			
		}
			
	}

	// create new SRR
	private void updateSRR(PmReason oldReason,  PmReason existingReason, AuditPMReqDTO auditPMReqDTO, String roleCode, String doctype, String parameterCode){
		
		if(oldReason.isByAppType() != existingReason.isByAppType()){
			restriction.setRoleCode(roleCode);
			restriction.setDocType(doctype);
			restriction.setDocNo(existingReason.getReasonCode());
			restriction.setDocGroupId(BigInteger.valueOf(existingReason.getReasonId()));
			restriction.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode).value()));
			restriction.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			restriction.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
			restriction.setLvlCode3(parameterCode);
			restriction.setDocId(BigInteger.valueOf(existingReason.getReasonId()));
			restriction.setFieldLabelCode(AuditConst.PM_AUD_RC_RES_APP_TYPE_FIELDLABELCODE);
			restriction.setOldValue(oldReason.isByAppType() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			restriction.setNewValue(existingReason.isByAppType()  ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			restriction.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(restriction);
			auditPMReqDTO.setObjList(objList);
		}
		
		if(oldReason.isByRegType() != existingReason.isByRegType()){
			restriction.setRoleCode(roleCode);
			restriction.setDocType(doctype);
			restriction.setDocNo(existingReason.getReasonCode());
			restriction.setDocGroupId(BigInteger.valueOf(existingReason.getReasonId()));
			restriction.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode).value()));
			restriction.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			restriction.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
			restriction.setLvlCode3(parameterCode);
			restriction.setDocId(BigInteger.valueOf(existingReason.getReasonId()));
			restriction.setFieldLabelCode(AuditConst.PM_AUD_RC_RES_BUSS_TYPE_FIELDLABELCODE);
			restriction.setOldValue(oldReason.isByRegType() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			restriction.setNewValue(existingReason.isByRegType()  ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			restriction.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(restriction);
			auditPMReqDTO.setObjList(objList);
		}
		
		if(oldReason.isBySupplierType() != existingReason.isBySupplierType()){
			restriction.setRoleCode(roleCode);
			restriction.setDocType(doctype);
			restriction.setDocNo(existingReason.getReasonCode());
			restriction.setDocGroupId(BigInteger.valueOf(existingReason.getReasonId()));
			restriction.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode).value()));
			restriction.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			restriction.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
			restriction.setLvlCode3(parameterCode);
			restriction.setDocId(BigInteger.valueOf(existingReason.getReasonId()));
			restriction.setFieldLabelCode(AuditConst.PM_AUD_RC_RES_SUPPL_TYPE_FIELDLABELCODE);
			restriction.setOldValue(oldReason.isBySupplierType() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			restriction.setNewValue(existingReason.isBySupplierType()  ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			restriction.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(restriction);
			auditPMReqDTO.setObjList(objList);
		}
		
	}
	
	private void deleteRestrictionDetail(List<PmSupplierReason> deleteRestrictionDetails, PmReason existingReason, AuditPMReqDTO auditPMReqDTO, String roleCode, String doctype, String parameterCode){
		for( PmSupplierReason supplierReason : deleteRestrictionDetails){
			restriction.setRoleCode(roleCode);
			restriction.setDocType(doctype);
			restriction.setDocNo(existingReason.getReasonCode());
			restriction.setDocGroupId(BigInteger.valueOf(existingReason.getReasonId()));
			restriction.setDocId(BigInteger.valueOf(supplierReason.getSupplierReasonId()));
			restriction.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode).value()));
			restriction.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			restriction.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
			restriction.setLvlCode3(parameterCode);
			restriction.setFieldLabelCode(checkLabelCode(supplierReason.getRestrType().getParameterCode()));
			restriction.setOldValue(supplierReason.getRestrType().getPmParameterDescsSpecCallOnly().get(1).getCodeName());
			restriction.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);
			objList.add(restriction);
			auditPMReqDTO.setObjList(objList);
		}
	}
	
	private BigInteger checkLabelCode(String parameterCode) {
		return parameterCode.equalsIgnoreCase(AuditConst.PM_AUD_RC_APP_TYPE) ? AuditConst.PM_AUD_RC_RES_DTL_APP_TYPE_FIELDLABELCODE
				: parameterCode.equalsIgnoreCase(AuditConst.PM_AUD_RC_BUSS_TYPE) ? AuditConst.PM_AUD_RC_RES_DTL_BUSS_TYPE_FIELDLABELCODE
						: parameterCode.equalsIgnoreCase(AuditConst.PM_AUD_RC_SUPPL_TYPE) ? AuditConst.PM_AUD_RC_RES_DTL_SUPPL_TYPE_FIELDLABELCODE
								: null;

	}
	
//	private BigInteger checkRestrictionLabelCode(String parameterCode) {
//		return parameterCode.equalsIgnoreCase(AuditConst.PM_AUD_RC_APP_TYPE) ? AuditConst.PM_AUD_RC_RES_APP_TYPE_FIELDLABELCODE
//				: parameterCode.equalsIgnoreCase(AuditConst.PM_AUD_RC_BUSS_TYPE) ? AuditConst.PM_AUD_RC_RES_BUSS_TYPE_FIELDLABELCODE
//						: parameterCode.equalsIgnoreCase(AuditConst.PM_AUD_RC_SUPPL_TYPE) ? AuditConst.PM_AUD_RC_RES_SUPPL_TYPE_FIELDLABELCODE
//								: null;
//
//	}
	
	private void addRestriction(PmReason existingReason, AuditPMReqDTO auditPMReqDTO, String roleCode, String doctype, String parameterCode){
		/**
		 * this.getManagedBean().setAppTypeRestrictions(appTypeRestrictions);
			this.getManagedBean().setComTypeRestrictions(comTypeRestrictions);
			this.getManagedBean().setSuppTypeRestrictions(suppTypeRestrictions);
		 */
		for(PmSupplierReason suppReason : existingReason.getPmSupplierReasons()){
			if(suppReason.isSelected()){
				restrictionDetails.setRoleCode(roleCode);
				restrictionDetails.setDocType(doctype);
				restrictionDetails.setDocNo(existingReason.getReasonCode());
				restrictionDetails.setDocGroupId(BigInteger.valueOf(existingReason.getReasonId()));
				restrictionDetails.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode).value()));
				restrictionDetails.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				restrictionDetails.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
				restrictionDetails.setLvlCode3(parameterCode);
				restrictionDetails.setDocId(BigInteger.valueOf(suppReason.getSupplierReasonId()));
				 // ST / AT / SBT
				restrictionDetails.setFieldLabelCode(checkLabelCode(suppReason.getRestrType().getPmParameterType().getParameterType()));
				restrictionDetails.setNewValue(suppReason.getRestrType().getPmParameterDescsSpecCallOnly().get(1).getCodeName());
				restrictionDetails.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(restrictionDetails);
			}
		}
		auditPMReqDTO.setObjList(objList);
	}
	
	private void addPIDReason(PmReason existingReason, AuditPMReqDTO auditPMReqDTO, String roleCode, String doctype, String parameterCode){
		for(PmPIDReason pidReason : existingReason.getPmPIDReasonList()){
			if(pidReason.isBrandNew()){
				reasonApplicability.setRoleCode(roleCode);
				reasonApplicability.setDocType(doctype);
				reasonApplicability.setDocNo(existingReason.getReasonCode());
				reasonApplicability.setDocGroupId(BigInteger.valueOf(existingReason.getReasonId()));
				reasonApplicability.setPmParameterLvlId(new BigInteger(ReasonParameterLevel.valueOf(parameterCode).value()));
				reasonApplicability.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				reasonApplicability.setLvlCode2(AuditConst.PM_LVL_CODE_2_RC);
				reasonApplicability.setLvlCode3(parameterCode);
				reasonApplicability.setDocId(BigInteger.valueOf(pidReason.getPidReasonId()));
				reasonApplicability.setFieldLabelCode(AuditConst.PM_AUD_RC_REASON_APPLICABILITY_FIELDLABELCODE);
				reasonApplicability.setNewValue(String.valueOf(existingReason.getConditionField().getParameterId()));
				reasonApplicability.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(reasonApplicability);
				auditPMReqDTO.setObjList(objList);
			}
		}
	}
}
