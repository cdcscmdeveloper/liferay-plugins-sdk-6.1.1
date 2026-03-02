package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmMachine;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.travellingTicket.TravellingTicketManagedBean;
import my.ep.web.util.pm.helper.ParameterHelper;

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

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {

		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		TravellingTicketManagedBean managedBean = (TravellingTicketManagedBean) manageBean;
		PmMachine pmMachine = managedBean.getUpdatedPmMachine();
		String doctype = checkPmParameterDesc(managedBean.getPmMachineSection().getCategoryType());

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
			
			if (!oldPmMachine.getMachineDesc().equals(pmMachine.getMachineDesc())) {
				detailName.setOldValue(oldPmMachine.getMachineDesc());
				detailName.setNewValue(pmMachine.getMachineDesc());
				detailName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(detailName);
				auditPMReqDTO.setObjList(objList);
			}
			if (!oldPmMachine.getRecordStatusFlg().equals(pmMachine.getRecordStatusFlg())) {
				detailStatus.setOldValue(oldPmMachine.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				detailStatus.setNewValue(pmMachine.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				detailStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(detailStatus);
				auditPMReqDTO.setObjList(objList);
			}
		}

		managedBean.setUpdatedPmMachine(null);
		managedBean.setOldPmMachine(null);
		return auditPMReqDTO;
	}
	
	private String checkPmParameterDesc(PmParameter pmParameter) {
		String description = null;
		
		if(pmParameter.getParameterId() != null) {
			PmParameter parameter = ParameterHelper.getInstance().getPmParameterById(pmParameter.getParameterId());
			for (PmParameterDesc desc : parameter.getPmParameterDescsSpecCallOnly()) {
				if (desc.getLanguageCode().equalsIgnoreCase(AuditConst.PM_LANGUAGECODE_MS)) {
					description = desc.getCodeName();
				}
			}
		}
		
		return description;

	}

}
