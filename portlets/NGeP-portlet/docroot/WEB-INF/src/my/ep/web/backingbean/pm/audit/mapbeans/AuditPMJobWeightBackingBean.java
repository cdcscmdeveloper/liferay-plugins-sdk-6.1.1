package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmPoJobWeight;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.pojobweight.PoJobWeightManagedBean;

/**
 * AuditPMJobWeightBackingBean
 * 
 * @author haiza
 * 28895_35509
 */
public class AuditPMJobWeightBackingBean extends AuditPMBackingBeanTemplate{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO dtlModerate = new AuditPMSPDTO();
	AuditPMSPDTO dtlHigh = new AuditPMSPDTO();
	AuditPMSPDTO dtlExtraHigh = new AuditPMSPDTO();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.rolePoJobWeight();
	}

	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		PoJobWeightManagedBean managedBean = (PoJobWeightManagedBean)manageBean;
		List<PmPoJobWeight> jobList = managedBean.getJobWeightList();
		
		for(PmPoJobWeight jobWeight : jobList){
			for(PmPoJobWeight oriJobWeight : managedBean.getOriJobWeightList()){
				if(jobWeight.getPoJobWeightId().equals(oriJobWeight.getPoJobWeightId()) &&
						!jobWeight.getWeightValue().equals(oriJobWeight.getWeightValue())){
					
					updateDetails(jobWeight, oriJobWeight, roleCode, auditPMReqDTO);
					break;
				}
			}
		}
		
		return auditPMReqDTO;
	}
	
	private void updateDetails(PmPoJobWeight jobWeight, PmPoJobWeight oriJobWeight, String roleCode, AuditPMReqDTO auditPMReqDTO){
		
		if(ParameterConst.JOB_WEIGHT_MODERATE.equals(jobWeight.getWeightType().getParameterCode())){
			dtlModerate.setRoleCode(roleCode);
			dtlModerate.setDocType(AuditConst.PM_AUD_JOB_WEIGHT_RATIO);
			dtlModerate.setPmParameterLvlId(AuditConst.PM_AUD_JOB_WEIGHT_RATIO_PMPARAMETERLVLID);
			dtlModerate.setDocNo(jobWeight.getWeightType().getParameterCode());
			dtlModerate.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
			dtlModerate.setLvlCode2(AuditConst.PM_LVL_CODE_2_JWR);
			dtlModerate.setLvlCode3(AuditConst.PM_LVL_CODE_3_JWR);
			dtlModerate.setDocId(BigInteger.valueOf(jobWeight.getPoJobWeightId()));
			dtlModerate.setFieldLabelCode(AuditConst.PM_AUD_JOB_WEIGHT_MODERATE_FIELDLABELCODE);
			dtlModerate.setOldValue(String.valueOf(oriJobWeight.getWeightValue()));
			dtlModerate.setNewValue(String.valueOf(jobWeight.getWeightValue()));
			dtlModerate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlModerate);
			auditPMReqDTO.setObjList(objList);
			
		}else if(ParameterConst.JOB_WEIGHT_HIGH.equals(jobWeight.getWeightType().getParameterCode())){
			dtlHigh.setRoleCode(roleCode);
			dtlHigh.setDocType(AuditConst.PM_AUD_JOB_WEIGHT_RATIO);
			dtlHigh.setPmParameterLvlId(AuditConst.PM_AUD_JOB_WEIGHT_RATIO_PMPARAMETERLVLID);
			dtlHigh.setDocNo(jobWeight.getWeightType().getParameterCode());
			dtlHigh.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
			dtlHigh.setLvlCode2(AuditConst.PM_LVL_CODE_2_JWR);
			dtlHigh.setLvlCode3(AuditConst.PM_LVL_CODE_3_JWR);
			dtlHigh.setDocId(BigInteger.valueOf(jobWeight.getPoJobWeightId()));
			dtlHigh.setFieldLabelCode(AuditConst.PM_AUD_JOB_WEIGHT_HIGH_FIELDLABELCODE);
			dtlHigh.setOldValue(String.valueOf(oriJobWeight.getWeightValue()));
			dtlHigh.setNewValue(String.valueOf(jobWeight.getWeightValue()));
			dtlHigh.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlHigh);
			auditPMReqDTO.setObjList(objList);
			
		}else if(ParameterConst.JOB_WEIGHT_EXTRA_HIGH.equals(jobWeight.getWeightType().getParameterCode())){
			dtlExtraHigh.setRoleCode(roleCode);
			dtlExtraHigh.setDocType(AuditConst.PM_AUD_JOB_WEIGHT_RATIO);
			dtlExtraHigh.setPmParameterLvlId(AuditConst.PM_AUD_JOB_WEIGHT_RATIO_PMPARAMETERLVLID);
			dtlExtraHigh.setDocNo(jobWeight.getWeightType().getParameterCode());
			dtlExtraHigh.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
			dtlExtraHigh.setLvlCode2(AuditConst.PM_LVL_CODE_2_JWR);
			dtlExtraHigh.setLvlCode3(AuditConst.PM_LVL_CODE_3_JWR);
			dtlExtraHigh.setDocId(BigInteger.valueOf(jobWeight.getPoJobWeightId()));
			dtlExtraHigh.setFieldLabelCode(AuditConst.PM_AUD_JOB_WEIGHT_EXTRA_HIGH_FIELDLABELCODE);
			dtlExtraHigh.setOldValue(String.valueOf(oriJobWeight.getWeightValue()));
			dtlExtraHigh.setNewValue(String.valueOf(jobWeight.getWeightValue()));
			dtlExtraHigh.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlExtraHigh);
			auditPMReqDTO.setObjList(objList);
		}	
		
	}
}
