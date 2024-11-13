package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.travellingTicket.TravellingTicketManagedBean;

/**
 * AuditPMEquipmentMachineBackingBean
 * 
 * @author haiza 28895_35509
 */
public class AuditPMEquipmentMachineBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO detailName = new AuditPMSPDTO();
	AuditPMSPDTO detailStatus = new AuditPMSPDTO();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleEquipmentMachine();
	}

	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {

		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		TravellingTicketManagedBean managedBean = (TravellingTicketManagedBean) manageBean;
		PmMachine pmMachine = managedBean.getUpdatedPmMachine();
		String doctype = managedBean.getPmMachineSection().getCategoryType().getPmParameterDescsSpecCallOnly().get(0)
				.getCodeName();

		detailName.setRoleCode(roleCode);
		detailName.setDocType(doctype);
		detailName.setDocNo(pmMachine.getMachineCode());
		detailName.setPmParameterLvlId(AuditConst.PM_AUD_SEMSM_PMPARAMETERLVLID);
		detailName.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		detailName.setLvlCode2(AuditConst.PM_LVL_CODE_2_SEMSM);
		detailName.setLvlCode3(AuditConst.PM_LVL_CODE_3_SEMSM);
		detailName.setDocId(BigInteger.valueOf(pmMachine.getMachineId()));
		detailName.setFieldLabelCode(AuditConst.PM_AUD_SEMSM_NAME_FIELDLABELCODE);

		detailStatus.setRoleCode(roleCode);
		detailStatus.setDocType(doctype);
		detailStatus.setDocNo(pmMachine.getMachineCode());
		detailStatus.setPmParameterLvlId(AuditConst.PM_AUD_SEMSM_PMPARAMETERLVLID);
		detailStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		detailStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_SEMSM);
		detailStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_SEMSM);
		detailStatus.setDocId(BigInteger.valueOf(pmMachine.getMachineId()));
		detailStatus.setFieldLabelCode(AuditConst.PM_AUD_SEMSM_STATUS_FIELDLABELCODE);

		if (managedBean.getPmMachine().isBrandNew()) {
			detailName.setNewValue(pmMachine.getMachineDesc());
			detailName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailName);
			auditPMReqDTO.setObjList(objList);

			detailStatus.setNewValue(pmMachine.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailStatus);
			auditPMReqDTO.setObjList(objList);

		} else {
			PmMachine oldPmMachine = managedBean.getOldPmMachine();
			if (oldPmMachine.getMachineDesc() != pmMachine.getMachineDesc()) {
				detailName.setOldValue(oldPmMachine.getMachineDesc());
				detailName.setNewValue(pmMachine.getMachineDesc());
				detailName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(detailName);
				auditPMReqDTO.setObjList(objList);
			}
			if (oldPmMachine.getRecordStatusFlg() != pmMachine.getRecordStatusFlg()) {
				detailStatus.setOldValue(oldPmMachine.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				detailStatus.setNewValue(pmMachine.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				detailStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(detailStatus);
				auditPMReqDTO.setObjList(objList);
			}
		}

		return auditPMReqDTO;
	}

}
