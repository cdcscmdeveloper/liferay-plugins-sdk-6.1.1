package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.salutation.SalutationManagedBean;

/**
 * AuditPMSalutationBackingBean
 * 
 * @author normuliana 28895_35509
 * 
 */
public class AuditPMSalutationBackingBean extends AuditPMBackingBeanTemplate {
	private static final long serialVersionUID = 1L;
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	String roleCode;
	String docNo;
	String docType;
	BigInteger docId;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleSalutation();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		SalutationManagedBean managedBeans = (SalutationManagedBean) manageBean;
		this.roleCode = roleCode;
		this.docNo = managedBeans.getPmSalutation().getSalutationCode();
		this.docType = managedBeans.getPmSalutation().getSalutationName();
		this.docId = BigInteger.valueOf(managedBeans.getPmSalutation().getSalutationId());
		if (managedBeans.getOldPmSalutation() != null && managedBeans.getOldPmSalutation().getSalutationId() != null) {
			if (managedBeans.getPmSalutation().getSalutationName() != null
					&& !managedBeans.getOldPmSalutation().getSalutationName()
							.equalsIgnoreCase(managedBeans.getPmSalutation().getSalutationName())) {
				detailObj(AuditConst.PM_AUD_SALUTATION_NAME_FIELDLABELID, managedBeans.getOldPmSalutation().getSalutationName(),
						managedBeans.getPmSalutation().getSalutationName(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
			if (managedBeans.getOldPmSalutation().getRecordStatusFlg() != managedBeans.getPmSalutation().getRecordStatusFlg()) {
				detailObj(AuditConst.PM_AUD_SALUTATION_STATUS_FIELDLABELID, managedBeans.getOldPmSalutation()
						.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE, managedBeans
						.getPmSalutation().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
						AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
		} else {
			detailObj(AuditConst.PM_AUD_SALUTATION_CODE_FIELDLABELID, null, managedBeans.getPmSalutation().getSalutationCode(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_SALUTATION_NAME_FIELDLABELID, null, managedBeans.getPmSalutation().getSalutationName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_SALUTATION_STATUS_FIELDLABELID, null,
					managedBeans.getPmSalutation().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE, AuditConst.PM_AUD_ACTION_TYPE_INS);
		}
		managedBeans.setPmSalutation(null);
		managedBeans.setOldPmSalutation(null);
		auditPMReqDTO.setObjList(objList);
		return auditPMReqDTO;
	}

	private ArrayList<AuditPMSPDTO> detailObj(BigInteger fieldLabelId, String oldValue, String newValue, String actionType) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(this.docType);
		dtlObj.setPmParameterLvlId(AuditConst.PM_AUD_SALUTATION_PMPARAMETERLVLID);
		dtlObj.setDocNo(docNo);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_SAL);
		dtlObj.setLvlCode3(AuditConst.PM_LVL_CODE_3_SAL);
		dtlObj.setDocId(docId);
		dtlObj.setFieldLabelCode(fieldLabelId);
		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}
		dtlObj.setNewValue(newValue);
		dtlObj.setActionType(actionType);
		objList.add(dtlObj);
		return objList;
	}
}