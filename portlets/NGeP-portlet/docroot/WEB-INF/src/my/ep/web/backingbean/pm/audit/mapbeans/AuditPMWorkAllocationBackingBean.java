package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;

import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmPoJobTypeRatio;
import my.ep.type.pm.RoleCode;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.pojobtyperatio.PoJobTypeRatioManagedBean;

/**
 * AuditPMEducationBackingBean
 * 
 * @author haiza
 * 28895_35509
 * 
 */
public class AuditPMWorkAllocationBackingBean extends AuditPMBackingBeanTemplate{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO dtlWeightCategory = new AuditPMSPDTO();
	AuditPMSPDTO dtlStatus = new AuditPMSPDTO();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleWorkAllocation();
	}

	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		PoJobTypeRatioManagedBean managedBean = (PoJobTypeRatioManagedBean)manageBean;
		PmPoJobTypeRatio jobRatio = managedBean.getPoJobTypeRatio();
		
		if(!managedBean.isBrandNew()){
			PmPoJobTypeRatio oldPoJobRatio = managedBean.getOriPoJobTypeRatio();
			
			 if(jobRatio.getPoJobWeight().getPoJobWeightId() != oldPoJobRatio.getPoJobWeight().getPoJobWeightId()){
				 
				dtlWeightCategory.setRoleCode(roleCode);
				dtlWeightCategory.setDocType(AuditConst.PM_AUD_WORK_ALLOCATION);
				dtlWeightCategory.setPmParameterLvlId(AuditConst.PM_AUD_WORK_ALLOCATION_PMPARAMETERLVLID);
				dtlWeightCategory.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
				dtlWeightCategory.setLvlCode2(AuditConst.PM_LVL_CODE_2_WAML);
				dtlWeightCategory.setLvlCode3(AuditConst.PM_LVL_CODE_3_WAML);
				dtlWeightCategory.setDocId(BigInteger.valueOf(jobRatio.getPoJobTypeRatioId()));
				dtlWeightCategory.setFieldLabelCode(AuditConst.PM_AUD_WAML_WEIGHT_CATEGORY_FIELDLABELCODE);
				dtlWeightCategory.setOldValue(checkValue(oldPoJobRatio.getPoJobWeight().getWeightType().getParameterCode()));
				dtlWeightCategory.setNewValue(checkValue(jobRatio.getPoJobWeight().getWeightType().getParameterCode()));
				dtlWeightCategory.setOldValueId(BigInteger.valueOf(oldPoJobRatio.getPoJobWeight().getPoJobWeightId()));
				dtlWeightCategory.setNewValueId(BigInteger.valueOf(jobRatio.getPoJobWeight().getPoJobWeightId()));
				dtlWeightCategory.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(dtlWeightCategory);
				auditPMReqDTO.setObjList(objList);
			 }
			 
			 if(jobRatio.getRecordStatusFlg() != oldPoJobRatio.getRecordStatusFlg()){
				dtlStatus.setRoleCode(roleCode);
				dtlStatus.setDocType(AuditConst.PM_AUD_WORK_ALLOCATION);
				dtlStatus.setPmParameterLvlId(AuditConst.PM_AUD_WORK_ALLOCATION_PMPARAMETERLVLID);
				dtlStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
				dtlStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_WAML);
				dtlStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_WAML);
				dtlStatus.setDocId(BigInteger.valueOf(jobRatio.getPoJobTypeRatioId()));
				dtlStatus.setFieldLabelCode(AuditConst.PM_AUD_WAML_STATUS_FIELDLABELCODE);
				
				dtlStatus.setOldValue(oldPoJobRatio.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				dtlStatus.setNewValue(jobRatio.getRecordStatusFlg() == 1 ? 
						AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
				dtlStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(dtlStatus);
				auditPMReqDTO.setObjList(objList);
			 }
		}else {
			dtlWeightCategory.setRoleCode(roleCode);
			dtlWeightCategory.setDocType(AuditConst.PM_AUD_WORK_ALLOCATION);
			dtlWeightCategory.setPmParameterLvlId(AuditConst.PM_AUD_WORK_ALLOCATION_PMPARAMETERLVLID);
			/**dtlWeightCategory.setDocNo(jobRatio);*/
			dtlWeightCategory.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
			dtlWeightCategory.setLvlCode2(AuditConst.PM_LVL_CODE_2_WAML);
			dtlWeightCategory.setLvlCode3(AuditConst.PM_LVL_CODE_3_WAML);
			dtlWeightCategory.setDocId(BigInteger.valueOf(jobRatio.getPoJobTypeRatioId()));
			dtlWeightCategory.setFieldLabelCode(AuditConst.PM_AUD_WAML_WEIGHT_CATEGORY_FIELDLABELCODE);
			dtlWeightCategory.setNewValue(checkValue(jobRatio.getPoJobWeight().getWeightType().getParameterCode()));
			dtlWeightCategory.setNewValueId(BigInteger.valueOf(jobRatio.getPoJobWeight().getPoJobWeightId()));
			dtlWeightCategory.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlWeightCategory);
			auditPMReqDTO.setObjList(objList);
			
			dtlStatus.setRoleCode(roleCode);
			dtlStatus.setDocType(AuditConst.PM_AUD_WORK_ALLOCATION);
			dtlStatus.setPmParameterLvlId(AuditConst.PM_AUD_WORK_ALLOCATION_PMPARAMETERLVLID);
			/**dtlWeightCategory.setDocNo(jobRatio);*/
			dtlStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
			dtlStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_WAML);
			dtlStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_WAML);
			dtlStatus.setDocId(BigInteger.valueOf(jobRatio.getPoJobTypeRatioId()));
			dtlStatus.setFieldLabelCode(AuditConst.PM_AUD_WAML_STATUS_FIELDLABELCODE);
			dtlStatus.setNewValue(jobRatio.getPoJobWeight().getRecordStatusFlg() == 1 ? 
					AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
			dtlStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlStatus);
			auditPMReqDTO.setObjList(objList);
		}
		
		
		return auditPMReqDTO;
	}
	
	private String checkValue(String parameterCode){
		
		return parameterCode.equalsIgnoreCase(ParameterConst.JOB_WEIGHT_MODERATE) ?
				AuditConst.PM_AUD_MODERATE : parameterCode.equalsIgnoreCase(ParameterConst.JOB_WEIGHT_HIGH) ?
						AuditConst.PM_AUD_HIGH : parameterCode.equalsIgnoreCase(ParameterConst.JOB_WEIGHT_EXTRA_HIGH) ?
								AuditConst.PM_AUD_EXTRA_HIGH : null;
		
	}
}
