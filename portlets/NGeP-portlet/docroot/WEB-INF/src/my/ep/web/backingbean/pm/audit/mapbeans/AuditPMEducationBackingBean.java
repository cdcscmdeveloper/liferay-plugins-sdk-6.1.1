package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmEduField;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.educationfield.EducationManagedBean;

/**
 * AuditPMEducationBackingBean
 * 
 * @author haiza 28895_35509
 * 
 */
public class AuditPMEducationBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO dtlObjName = new AuditPMSPDTO();
	AuditPMSPDTO dtlStatus = new AuditPMSPDTO();
	
	String docType;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleEducation();
	}

	/**
	 * @param args
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {

		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		EducationManagedBean managedBeans = (EducationManagedBean) manageBean;
		PmEduField pmEduField = managedBeans.getPmEduField();
		
		docType = pmEduField.getFieldName();

		dtlObjName.setRoleCode(roleCode);
		dtlObjName.setDocType(docType);
		dtlObjName.setPmParameterLvlId(AuditConst.PM_AUD_EDUCATION_PMPARAMETERLVLID);
		dtlObjName.setDocNo(pmEduField.getFieldCode());
		dtlObjName.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlObjName.setLvlCode2(AuditConst.PM_LVL_CODE_2_EDU);
		dtlObjName.setLvlCode3(AuditConst.PM_LVL_CODE_3_EDU);
		dtlObjName.setDocId(BigInteger.valueOf(managedBeans.getPmEduField().getEduFieldId()));
		dtlObjName.setFieldLabelCode(AuditConst.PM_AUD_EDUCATION_NAME_FIELDLABELID);

		dtlStatus.setRoleCode(roleCode);
		dtlStatus.setDocType(docType);
		dtlStatus.setPmParameterLvlId(AuditConst.PM_AUD_EDUCATION_PMPARAMETERLVLID);
		dtlStatus.setDocNo(pmEduField.getFieldCode());
		dtlStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_EDU);
		dtlStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_EDU);
		dtlStatus.setDocId(BigInteger.valueOf(pmEduField.getEduFieldId()));
		dtlStatus.setFieldLabelCode(AuditConst.PM_AUD_EDUCATION_STATUS_FIELDLABELID);

		if (managedBeans.getOldAuditPmEduField() != null
				&& managedBeans.getOldAuditPmEduField().getEduFieldId() != null) {
			if (pmEduField.getFieldName() != null
					&& !managedBeans.getOldAuditPmEduField().getFieldName()
							.equalsIgnoreCase(pmEduField.getFieldName())) {
				dtlObjName.setOldValue(managedBeans.getOldAuditPmEduField().getFieldName());
				dtlObjName.setNewValue(pmEduField.getFieldName());
				dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(dtlObjName);
				auditPMReqDTO.setObjList(objList);
			}
			// status
			if (managedBeans.getOldAuditPmEduField().getRecordStatusFlg() != pmEduField.getRecordStatusFlg()) {
				dtlStatus
						.setOldValue(managedBeans.getOldAuditPmEduField().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				dtlStatus.setNewValue(pmEduField.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				dtlStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(dtlStatus);
				auditPMReqDTO.setObjList(objList);
			}

		} else {

			dtlObjName.setNewValue(pmEduField.getFieldName());
			dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlObjName);
			auditPMReqDTO.setObjList(objList);

			dtlStatus.setNewValue(pmEduField.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			dtlStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlStatus);
			auditPMReqDTO.setObjList(objList);
		}
		managedBeans.setOldAuditPmEduField(null);

		return auditPMReqDTO;
	}
}
