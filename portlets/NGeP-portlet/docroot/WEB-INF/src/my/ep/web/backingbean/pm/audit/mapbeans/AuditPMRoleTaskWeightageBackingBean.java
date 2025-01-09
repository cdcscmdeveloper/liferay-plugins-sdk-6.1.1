package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmPoRoleTask;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.common.potaskweightage.POTaskWeightageManagedBean;

/**
 * AuditPMRoleTaskWeightageBackingBean
 * 
 * @author haiza
 * 28895_35509
 */
public class AuditPMRoleTaskWeightageBackingBean extends AuditPMBackingBeanTemplate{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO dtlWeightage = new AuditPMSPDTO();
	AuditPMSPDTO detailSupplierType = new AuditPMSPDTO();
	AuditPMSPDTO detailApplicationType = new AuditPMSPDTO();
	AuditPMSPDTO detailCategoryType = new AuditPMSPDTO();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleProcessOfficerRoleTask();
	}

	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		POTaskWeightageManagedBean managedBean = (POTaskWeightageManagedBean)manageBean;
		PmUser selectedUser = managedBean.getSelectedUser();
		
		updateWeightage(roleCode, selectedUser, auditPMReqDTO);
		
		for (PmPoRoleTask roleTask : selectedUser.getPmPoRoleTasks()) {
			if(roleTask.getPoRoleTaskdId() != null){
				PmPoRoleTask exRT = managedBean.getExistingRoleTaskMap().get(roleTask.getPoRoleTaskdId());
				
				if(roleTask.getRecordStatusFlg() != exRT.getRecordStatusFlg()){
					AuditPMSPDTO detailStatus = new AuditPMSPDTO();
					detailStatus.setRoleCode(roleCode);
					detailStatus.setDocType(AuditConst.PM_AUD_TASK_WEIGHTAGE);
					detailStatus.setPmParameterLvlId(AuditConst.PM_AUD_PORTW_PMPARAMETERLVLID);
					detailStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
					detailStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_PORTW);
					detailStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_PORTW);
					detailStatus.setDocId(BigInteger.valueOf(roleTask.getPoRoleTaskdId()));
					detailStatus.setDocGroupId(BigInteger.valueOf(selectedUser.getUserId()));
					detailStatus.setDocNo(selectedUser.getIdentificationNo());
					detailStatus.setFieldLabelCode(AuditConst.PM_AUD_PORTW_STATUS_FIELDLABELID);
					detailStatus.setOldValue(exRT.getRecordStatusFlg() ==1 ?  AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
					detailStatus.setNewValue(roleTask.getRecordStatusFlg() ==1 ?  AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
					detailStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
					
					objList.add(detailStatus);
					auditPMReqDTO.setObjList(objList);
				}
				
			} else {
				/** supplier **/
				detailSupplierType.setRoleCode(roleCode);
				detailSupplierType.setDocType(AuditConst.PM_AUD_TASK_WEIGHTAGE);
				detailSupplierType.setPmParameterLvlId(AuditConst.PM_AUD_PORTW_PMPARAMETERLVLID);
				detailSupplierType.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
				detailSupplierType.setLvlCode2(AuditConst.PM_LVL_CODE_2_PORTW);
				detailSupplierType.setLvlCode3(AuditConst.PM_LVL_CODE_3_PORTW);
				detailSupplierType.setDocId(BigInteger.valueOf(selectedUser.getPmPoWeightage().getPoWeightageId()));
				detailSupplierType.setDocGroupId(BigInteger.valueOf(selectedUser.getUserId()));
				detailSupplierType.setDocNo(selectedUser.getIdentificationNo());
				detailSupplierType.setFieldLabelCode(AuditConst.PM_AUD_PORTW_SUPPLIER_TYPE_FIELDLABELID);
				detailSupplierType.setNewValue(roleTask.getSupplierType());
				detailSupplierType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(detailSupplierType);
				auditPMReqDTO.setObjList(objList);
				
				/** application **/
				detailApplicationType.setRoleCode(roleCode);
				detailApplicationType.setDocType(AuditConst.PM_AUD_TASK_WEIGHTAGE);
				detailApplicationType.setPmParameterLvlId(AuditConst.PM_AUD_PORTW_PMPARAMETERLVLID);
				detailApplicationType.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
				detailApplicationType.setLvlCode2(AuditConst.PM_LVL_CODE_2_PORTW);
				detailApplicationType.setLvlCode3(AuditConst.PM_LVL_CODE_3_PORTW);
				detailApplicationType.setDocId(BigInteger.valueOf(selectedUser.getPmPoWeightage().getPoWeightageId()));
				detailApplicationType.setDocGroupId(BigInteger.valueOf(selectedUser.getUserId()));
				detailApplicationType.setDocNo(selectedUser.getIdentificationNo());
				detailApplicationType.setFieldLabelCode(AuditConst.PM_AUD_PORTW_APPLICATION_TYPE_FIELDLABELID);
				detailApplicationType.setNewValue(roleTask.getApplType());
				detailApplicationType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(detailApplicationType);
				auditPMReqDTO.setObjList(objList);
				
				/** category **/
				detailCategoryType.setRoleCode(roleCode);
				detailCategoryType.setDocType(AuditConst.PM_AUD_TASK_WEIGHTAGE);
				detailCategoryType.setPmParameterLvlId(AuditConst.PM_AUD_PORTW_PMPARAMETERLVLID);
				detailCategoryType.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
				detailCategoryType.setLvlCode2(AuditConst.PM_LVL_CODE_2_PORTW);
				detailCategoryType.setLvlCode3(AuditConst.PM_LVL_CODE_3_PORTW);
				detailCategoryType.setDocId(BigInteger.valueOf(selectedUser.getPmPoWeightage().getPoWeightageId()));
				detailCategoryType.setDocGroupId(BigInteger.valueOf(selectedUser.getUserId()));
				detailCategoryType.setDocNo(selectedUser.getIdentificationNo());
				detailCategoryType.setFieldLabelCode(AuditConst.PM_AUD_PORTW_CATEGORY_TYPE_FIELDLABELID);
				detailCategoryType.setNewValue(roleTask.getCategoryType());
				detailCategoryType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(detailCategoryType);
				auditPMReqDTO.setObjList(objList);
				
				AuditPMSPDTO detailStatus = new AuditPMSPDTO();
				detailStatus.setRoleCode(roleCode);
				detailStatus.setDocType(AuditConst.PM_AUD_TASK_WEIGHTAGE);
				detailStatus.setPmParameterLvlId(AuditConst.PM_AUD_PORTW_PMPARAMETERLVLID);
				detailStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
				detailStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_PORTW);
				detailStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_PORTW);
				detailStatus.setDocId(BigInteger.valueOf(roleTask.getPoRoleTaskdId()));
				detailStatus.setDocGroupId(BigInteger.valueOf(selectedUser.getUserId()));
				detailStatus.setDocNo(selectedUser.getIdentificationNo());
				detailStatus.setFieldLabelCode(AuditConst.PM_AUD_PORTW_STATUS_FIELDLABELID);
				detailStatus.setNewValue(roleTask.getRecordStatusFlg()==1 ?  AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE);
				detailStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				
				objList.add(detailStatus);
				auditPMReqDTO.setObjList(objList);
			}
		}
		
		return auditPMReqDTO;
	}

	private void updateWeightage(String roleCode, PmUser selectedUser, AuditPMReqDTO auditPMReqDTO){
		
		if(selectedUser.getPmPoWeightage().getNewWeight() != selectedUser.getPmPoWeightage().getWeightEnter()){
			
			dtlWeightage.setRoleCode(roleCode);
			dtlWeightage.setDocType(AuditConst.PM_AUD_TASK_WEIGHTAGE);
			dtlWeightage.setPmParameterLvlId(AuditConst.PM_AUD_PORTW_PMPARAMETERLVLID);
			dtlWeightage.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
			dtlWeightage.setLvlCode2(AuditConst.PM_LVL_CODE_2_PORTW);
			dtlWeightage.setLvlCode3(AuditConst.PM_LVL_CODE_3_PORTW);
			dtlWeightage.setDocId(BigInteger.valueOf(selectedUser.getPmPoWeightage().getPoWeightageId()));
			dtlWeightage.setDocGroupId(BigInteger.valueOf(selectedUser.getUserId()));
			dtlWeightage.setDocNo(selectedUser.getIdentificationNo());
			dtlWeightage.setFieldLabelCode(AuditConst.PM_AUD_PORTW_NEW_WEIGHT_FIELDLABELID);
			dtlWeightage.setOldValue(String.valueOf(selectedUser.getPmPoWeightage().getNewWeight()));
			dtlWeightage.setNewValue(String.valueOf(selectedUser.getPmPoWeightage().getWeightEnter()));
			dtlWeightage.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlWeightage);
			auditPMReqDTO.setObjList(objList);
		}
	}

}
