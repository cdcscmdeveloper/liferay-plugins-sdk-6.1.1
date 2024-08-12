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

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		SalutationManagedBean managedBeans = (SalutationManagedBean) manageBean;
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		AuditPMSPDTO detailObjName = new AuditPMSPDTO();
		AuditPMSPDTO detailSatus = new AuditPMSPDTO();
		AuditPMSPDTO detailCode = new AuditPMSPDTO();

		detailObjName.setRoleCode(roleCode);
		detailObjName.setDoctype(AuditConst.PM_AUD_SALUTATION);
		detailObjName.setPmParameterLvlId(AuditConst.PM_AUD_SALUTATION_PMPARAMETERLVLID);
		detailObjName.setDocNo(managedBeans.getPmSalutation().getSalutationCode());
		detailObjName.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		detailObjName.setLvlCode2(AuditConst.PM_LVL_CODE_2_SAL);
		detailObjName.setLvlCode3(AuditConst.PM_LVL_CODE_3_SAL);
		
		detailSatus.setRoleCode(roleCode);
		detailSatus.setDoctype(AuditConst.PM_AUD_SALUTATION);
		detailSatus.setPmParameterLvlId(AuditConst.PM_AUD_SALUTATION_PMPARAMETERLVLID);
		detailSatus.setDocNo(managedBeans.getPmSalutation().getSalutationCode());
		detailSatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		detailSatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_SAL);
		detailSatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_SAL);

		if (managedBeans.getOldPmSalutation() != null && managedBeans.getOldPmSalutation().getSalutationId() != null) {
			if (managedBeans.getPmSalutation().getSalutationName() != null
					&& !managedBeans.getOldPmSalutation().getSalutationName()
							.equalsIgnoreCase(managedBeans.getPmSalutation().getSalutationName())) {
				detailObjName.setDocId(BigInteger.valueOf(managedBeans.getPmSalutation().getSalutationId()));
				detailObjName.setFieldLabelCode(AuditConst.PM_AUD_SALUTATION_NAME_FIELDLABELID);
				detailObjName.setOldValue(managedBeans.getOldPmSalutation().getSalutationName());
				detailObjName.setNewValue(managedBeans.getPmSalutation().getSalutationName());
				detailObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(detailObjName);
				auditPMReqDTO.setObjList(objList);
			}
			if (managedBeans.getOldPmSalutation().getRecordStatusFlg() != managedBeans.getPmSalutation()
					.getRecordStatusFlg()) {
				detailSatus.setDocId(BigInteger.valueOf(managedBeans.getPmSalutation().getSalutationId()));
				detailSatus.setFieldLabelCode(AuditConst.PM_AUD_SALUTATION_STATUS_FIELDLABELID);
				detailSatus
						.setOldValue(managedBeans.getOldPmSalutation().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				detailSatus
						.setNewValue(managedBeans.getPmSalutation().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				detailSatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(detailSatus);
				auditPMReqDTO.setObjList(objList);
			}
		} else {
			detailCode.setRoleCode(roleCode);
			detailCode.setDoctype(AuditConst.PM_AUD_SALUTATION);
			detailCode.setPmParameterLvlId(AuditConst.PM_AUD_SALUTATION_PMPARAMETERLVLID);
			detailCode.setDocNo(managedBeans.getPmSalutation().getSalutationCode());
			detailCode.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailCode.setLvlCode2(AuditConst.PM_LVL_CODE_2_SAL);
			detailCode.setLvlCode3(AuditConst.PM_LVL_CODE_3_SAL);
			detailCode.setDocId(BigInteger.valueOf(managedBeans.getPmSalutation().getSalutationId()));
			detailCode.setFieldLabelCode(AuditConst.PM_AUD_SALUTATION_CODE_FIELDLABELID);
			detailCode.setNewValue(managedBeans.getPmSalutation().getSalutationCode());
			detailCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailCode);
			auditPMReqDTO.setObjList(objList);

			detailObjName.setDocId(BigInteger.valueOf(managedBeans.getPmSalutation().getSalutationId()));
			detailObjName.setFieldLabelCode(AuditConst.PM_AUD_SALUTATION_NAME_FIELDLABELID);
			detailObjName.setNewValue(managedBeans.getPmSalutation().getSalutationName());
			detailObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjName);
			auditPMReqDTO.setObjList(objList);

			detailSatus.setDocId(BigInteger.valueOf(managedBeans.getPmSalutation().getSalutationId()));
			detailSatus.setFieldLabelCode(AuditConst.PM_AUD_SALUTATION_STATUS_FIELDLABELID);
			detailSatus.setNewValue(managedBeans.getPmSalutation().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailSatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailSatus);
			auditPMReqDTO.setObjList(objList);
		}

		managedBeans.setPmSalutation(null);
		managedBeans.setOldPmSalutation(null);
		
		return auditPMReqDTO;
	}
}