package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.race.RaceManagedBean;

/**
 * AuditPMRaceBackingBean
 * 
 * @author normuliana 28895_35509
 * 
 */
public class AuditPMRaceBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO dtlObjName = new AuditPMSPDTO();
	AuditPMSPDTO dtlSatus = new AuditPMSPDTO();
	AuditPMSPDTO dtlCode = new AuditPMSPDTO();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleRace();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		RaceManagedBean managedBeans = (RaceManagedBean) manageBean;

		dtlSatus.setRoleCode(roleCode);
		dtlSatus.setDoctype(AuditConst.PM_AUD_RACE);
		dtlSatus.setPmParameterLvlId(AuditConst.PM_AUD_RACE_PMPARAMETERLVLID);
		dtlSatus.setDocNo(managedBeans.getPmRace().getRaceCode());
		dtlSatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		dtlSatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_RACE);
		dtlSatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_RACE);

		dtlObjName.setRoleCode(roleCode);
		dtlObjName.setDoctype(AuditConst.PM_AUD_RACE);
		dtlObjName.setPmParameterLvlId(AuditConst.PM_AUD_RACE_PMPARAMETERLVLID);
		dtlObjName.setDocNo(managedBeans.getPmRace().getRaceCode());
		dtlObjName.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		dtlObjName.setLvlCode2(AuditConst.PM_LVL_CODE_2_RACE);
		dtlObjName.setLvlCode3(AuditConst.PM_LVL_CODE_3_RACE);

		if (managedBeans.getOldPmRace() != null && managedBeans.getOldPmRace().getRaceId() != null) {
			if (managedBeans.getPmRace().getRaceName() != null
					&& !managedBeans.getOldPmRace().getRaceName().equalsIgnoreCase(managedBeans.getPmRace().getRaceName())) {
				dtlObjName.setDocId(BigInteger.valueOf(managedBeans.getPmRace().getRaceId()));
				dtlObjName.setFieldLabelCode(AuditConst.PM_AUD_RACE_NAME_FIELDLABELID);
				dtlObjName.setOldValue(managedBeans.getOldPmRace().getRaceName());
				dtlObjName.setNewValue(managedBeans.getPmRace().getRaceName());
				dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(dtlObjName);
				auditPMReqDTO.setObjList(objList);
			}
			if (managedBeans.getOldPmRace().getRecordStatusFlg() != managedBeans.getPmRace().getRecordStatusFlg()) {
				dtlSatus.setDocId(BigInteger.valueOf(managedBeans.getPmRace().getRaceId()));
				dtlSatus.setFieldLabelCode(AuditConst.PM_AUD_RACE_STATUS_FIELDLABELID);
				dtlSatus.setOldValue(managedBeans.getOldPmRace().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				dtlSatus.setNewValue(managedBeans.getPmRace().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				dtlSatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(dtlSatus);
				auditPMReqDTO.setObjList(objList);
			}
		} else {
			dtlCode.setRoleCode(roleCode);
			dtlCode.setDoctype(AuditConst.PM_AUD_RACE);
			dtlCode.setPmParameterLvlId(AuditConst.PM_AUD_RACE_PMPARAMETERLVLID);
			dtlCode.setDocNo(managedBeans.getPmRace().getRaceCode());
			dtlCode.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			dtlCode.setLvlCode2(AuditConst.PM_LVL_CODE_2_RACE);
			dtlCode.setLvlCode3(AuditConst.PM_LVL_CODE_3_RACE);
			dtlCode.setDocId(BigInteger.valueOf(managedBeans.getPmRace().getRaceId()));
			dtlCode.setFieldLabelCode(AuditConst.PM_AUD_RACE_CODE_FIELDLABELID);
			dtlCode.setNewValue(managedBeans.getPmRace().getRaceCode());
			dtlCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlCode);
			auditPMReqDTO.setObjList(objList);

			dtlObjName.setDocId(BigInteger.valueOf(managedBeans.getPmRace().getRaceId()));
			dtlObjName.setFieldLabelCode(AuditConst.PM_AUD_RACE_NAME_FIELDLABELID);
			dtlObjName.setNewValue(managedBeans.getPmRace().getRaceName());
			dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlObjName);
			auditPMReqDTO.setObjList(objList);

			dtlSatus.setDocId(BigInteger.valueOf(managedBeans.getPmRace().getRaceId()));
			dtlSatus.setFieldLabelCode(AuditConst.PM_AUD_RACE_STATUS_FIELDLABELID);
			dtlSatus.setNewValue(managedBeans.getPmRace().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			dtlSatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlSatus);
			auditPMReqDTO.setObjList(objList);
		}

		managedBeans.setPmRace(null);
		managedBeans.setOldPmRace(null);

		return auditPMReqDTO;
	}
}