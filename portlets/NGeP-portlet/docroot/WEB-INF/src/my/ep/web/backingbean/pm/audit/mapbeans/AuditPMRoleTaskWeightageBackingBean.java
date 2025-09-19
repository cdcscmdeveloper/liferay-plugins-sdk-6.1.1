package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.common.PmPoRoleTask;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.constant.sm.SupplierManagementConst;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.common.potaskweightage.POTaskWeightageManagedBean;
import my.ep.web.util.sm.helper.SmCommonUtil;

/**
 * AuditPMRoleTaskWeightageBackingBean processing officer role task
 * 
 * @author haiza 28895_35509
 */
public class AuditPMRoleTaskWeightageBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO dtlWeightage = new AuditPMSPDTO();

	String docNo;
	BigInteger docId;
	String docType;
	String roleCode;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleProcessOfficerRoleTask();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {

		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		POTaskWeightageManagedBean managedBean = (POTaskWeightageManagedBean) manageBean;
		PmUser selectedUser = managedBean.getSelectedUser();

		docType = selectedUser.getUserName();
		docNo = selectedUser.getIdentificationNo();
		updateWeightage(roleCode, selectedUser);

		for (PmPoRoleTask roleTask : selectedUser.getPmPoRoleTasks()) {
			if (roleTask.getPoRoleTaskdId() != null) {
				PmPoRoleTask existingRoleTask = managedBean.getExistingRoleTaskMap().get(roleTask.getPoRoleTaskdId());

				if (roleTask.getRecordStatusFlg() != existingRoleTask.getRecordStatusFlg()) {
					AuditPMSPDTO detailStatus = new AuditPMSPDTO();
					detailStatus.setRoleCode(roleCode);
					detailStatus.setDocType(docType);
					detailStatus.setDocNo(docNo);
					detailStatus.setPmParameterLvlId(AuditConst.PM_AUD_PORTW_PMPARAMETERLVLID);
					detailStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
					detailStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_PORTW);
					detailStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_PORTW);
					detailStatus.setDocId(BigInteger.valueOf(selectedUser.getUserId()));
					detailStatus.setDocGroupId(BigInteger.valueOf(selectedUser.getUserId()));
					detailStatus.setFieldLabelCode(AuditConst.PM_AUD_PORTW_STATUS_FIELDLABELID);
					detailStatus.setOldValue(existingRoleTask.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
					detailStatus.setNewValue(roleTask.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
					detailStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);

					objList.add(detailStatus);
				}

			} else {
				/** supplier **/
				AuditPMSPDTO detailSupplierType = new AuditPMSPDTO();
				detailSupplierType.setRoleCode(roleCode);
				detailSupplierType.setDocType(docType);
				detailSupplierType.setPmParameterLvlId(AuditConst.PM_AUD_PORTW_PMPARAMETERLVLID);
				detailSupplierType.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
				detailSupplierType.setLvlCode2(AuditConst.PM_LVL_CODE_2_PORTW);
				detailSupplierType.setLvlCode3(AuditConst.PM_LVL_CODE_3_PORTW);
				detailSupplierType.setDocId(BigInteger.valueOf(selectedUser.getUserId()));
				detailSupplierType.setDocGroupId(BigInteger.valueOf(selectedUser.getUserId()));
				detailSupplierType.setDocNo(selectedUser.getIdentificationNo());
				detailSupplierType.setFieldLabelCode(AuditConst.PM_AUD_PORTW_SUPPLIER_TYPE_FIELDLABELID);
				detailSupplierType.setNewValue(getSupplierDesc(roleTask.getSupplierType()));
				detailSupplierType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(detailSupplierType);

				/** application **/
				AuditPMSPDTO detailApplicationType = new AuditPMSPDTO();
				detailApplicationType.setRoleCode(roleCode);
				detailApplicationType.setDocType(docType);
				detailApplicationType.setPmParameterLvlId(AuditConst.PM_AUD_PORTW_PMPARAMETERLVLID);
				detailApplicationType.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
				detailApplicationType.setLvlCode2(AuditConst.PM_LVL_CODE_2_PORTW);
				detailApplicationType.setLvlCode3(AuditConst.PM_LVL_CODE_3_PORTW);
				detailApplicationType.setDocId(BigInteger.valueOf(selectedUser.getUserId()));
				detailApplicationType.setDocGroupId(BigInteger.valueOf(selectedUser.getUserId()));
				detailApplicationType.setDocNo(selectedUser.getIdentificationNo());
				detailApplicationType.setFieldLabelCode(AuditConst.PM_AUD_PORTW_APPLICATION_TYPE_FIELDLABELID);
				detailApplicationType.setNewValue(getApplDesc(roleTask.getApplType()));
				detailApplicationType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(detailApplicationType);

				/** category **/
				// check null
				if (roleTask.getCategoryType() != null) {
					AuditPMSPDTO detailCategoryType = new AuditPMSPDTO();
					detailCategoryType.setRoleCode(roleCode);
					detailCategoryType.setDocType(docType);
					detailCategoryType.setPmParameterLvlId(AuditConst.PM_AUD_PORTW_PMPARAMETERLVLID);
					detailCategoryType.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
					detailCategoryType.setLvlCode2(AuditConst.PM_LVL_CODE_2_PORTW);
					detailCategoryType.setLvlCode3(AuditConst.PM_LVL_CODE_3_PORTW);
					detailCategoryType.setDocId(BigInteger.valueOf(selectedUser.getUserId()));
					detailCategoryType.setDocGroupId(BigInteger.valueOf(selectedUser.getUserId()));
					detailCategoryType.setDocNo(selectedUser.getIdentificationNo());
					detailCategoryType.setFieldLabelCode(AuditConst.PM_AUD_PORTW_CATEGORY_TYPE_FIELDLABELID);
					detailCategoryType.setNewValue(checkSMCategoryCode(roleTask.getCategoryType()));
					detailCategoryType.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
					objList.add(detailCategoryType);
				}

				AuditPMSPDTO detailStatus = new AuditPMSPDTO();
				detailStatus.setRoleCode(roleCode);
				detailStatus.setDocType(docType);
				detailStatus.setPmParameterLvlId(AuditConst.PM_AUD_PORTW_PMPARAMETERLVLID);
				detailStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
				detailStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_PORTW);
				detailStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_PORTW);
				detailStatus.setDocId(BigInteger.valueOf(selectedUser.getUserId()));
				detailStatus.setDocGroupId(BigInteger.valueOf(selectedUser.getUserId()));
				detailStatus.setDocNo(selectedUser.getIdentificationNo());
				detailStatus.setFieldLabelCode(AuditConst.PM_AUD_PORTW_STATUS_FIELDLABELID);
				detailStatus.setNewValue(roleTask.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				detailStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(detailStatus);
			}
		}

		auditPMReqDTO.setObjList(objList);
		return auditPMReqDTO;
	}

	private void updateWeightage(String roleCode, PmUser selectedUser) {
		this.roleCode = roleCode;
		docId = BigInteger.valueOf(selectedUser.getUserId());
		docNo = selectedUser.getIdentificationNo();

		if (selectedUser.getPmPoWeightage() != null
				&& (selectedUser.getPmPoWeightage().getNewWeight() != selectedUser.getPmPoWeightage().getWeightEnter())) {

			String oldValue = String.valueOf(selectedUser.getPmPoWeightage().getNewWeight());
			String newValue = String.valueOf(selectedUser.getPmPoWeightage().getWeightEnter());

			detailObj(oldValue, newValue, AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}
	}

	/**
	 * 
	 * @param supplierType
	 * @return
	 */
	private String getSupplierDesc(String supplierType){
		if(supplierType.equalsIgnoreCase(SupplierManagementConst.SUPP_TYPE_CONTRACTOR)){
			return SmCommonUtil.getMessageMalay("lbl.sm.contractor",null);
		} else{
			return SmCommonUtil.getMessageMalay("lbl.sm.consultant",null); // SupplierManagementConst.SUPP_TYPE_CONSULTANT
		}
	}
	
	/**
	 * 
	 * @param applType
	 * @return
	 */
	private String getApplDesc(String applType){
		String label = "lbl.pm.po.task.appltype_" + applType;
		return SmCommonUtil.getMessageMalay(label,null);
	}
	
	private String checkSMCategoryCode(String pidDocType) {
		return pidDocType.equalsIgnoreCase(SupplierManagementConst.SM_APPL_CATEGORY_TYPE_NORMAL) ? SmCommonUtil.getMessageMalay("lbl.sm.catCodes.norm",null) : SmCommonUtil.getMessageMalay("lbl.sm.catCodes.spl",null);
	}

	private ArrayList<AuditPMSPDTO> detailObj(String oldValue, String newValue, String actionType) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(docType);
		dtlObj.setDocNo(docNo);
		dtlObj.setPmParameterLvlId(AuditConst.PM_AUD_PORTW_PMPARAMETERLVLID);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_PORTW);
		dtlObj.setLvlCode3(AuditConst.PM_LVL_CODE_3_PORTW);
		dtlObj.setDocId(docId);
		dtlObj.setDocGroupId(docId);
		dtlObj.setFieldLabelCode(AuditConst.PM_AUD_PORTW_NEW_WEIGHT_FIELDLABELID);
		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}
		dtlObj.setNewValue(newValue);
		dtlObj.setActionType(actionType);
		objList.add(dtlObj);

		return objList;
	}

}