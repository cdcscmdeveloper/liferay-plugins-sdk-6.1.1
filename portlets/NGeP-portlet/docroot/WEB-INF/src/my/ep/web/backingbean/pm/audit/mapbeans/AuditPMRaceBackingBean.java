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
	private static final long serialVersionUID = 1L;
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	String roleCode;
	String raceCode;
	BigInteger docId;

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
		RaceManagedBean managedBeans = (RaceManagedBean) manageBean;
		this.roleCode = roleCode;
		this.raceCode = managedBeans.getPmRace().getRaceCode();
		this.docId = BigInteger.valueOf(managedBeans.getPmRace().getRaceId());
		if (managedBeans.getOldPmRace() != null && managedBeans.getOldPmRace().getRaceId() != null) {
			if (managedBeans.getPmRace().getRaceName() != null
					&& !managedBeans.getOldPmRace().getRaceName().equalsIgnoreCase(managedBeans.getPmRace().getRaceName())) {
				detailObj(AuditConst.PM_AUD_RACE_NAME_FIELDLABELID, managedBeans.getOldPmRace().getRaceName(), managedBeans
						.getPmRace().getRaceName(), AuditConst.PM_AUD_ACTION_TYPE_UPD);

			}
			if (managedBeans.getOldPmRace().getRecordStatusFlg() != managedBeans.getPmRace().getRecordStatusFlg()) {
				detailObj(AuditConst.PM_AUD_RACE_STATUS_FIELDLABELID,
						managedBeans.getOldPmRace().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE,
						managedBeans.getPmRace().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE, AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
		} else {
			detailObj(AuditConst.PM_AUD_RACE_CODE_FIELDLABELID, null, managedBeans.getPmRace().getRaceCode(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_RACE_NAME_FIELDLABELID, null, managedBeans.getPmRace().getRaceName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_RACE_STATUS_FIELDLABELID, null,
					managedBeans.getPmRace().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					AuditConst.PM_AUD_ACTION_TYPE_INS);
		}
		managedBeans.setPmRace(null);
		managedBeans.setOldPmRace(null);
		auditPMReqDTO.setObjList(objList);
		return auditPMReqDTO;
	}

	private ArrayList<AuditPMSPDTO> detailObj(BigInteger fieldLabelId, String oldValue, String newValue, String actionType) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(AuditConst.PM_AUD_RACE);
		dtlObj.setPmParameterLvlId(AuditConst.PM_AUD_RACE_PMPARAMETERLVLID);
		dtlObj.setDocNo(raceCode);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_RACE);
		dtlObj.setLvlCode3(AuditConst.PM_LVL_CODE_3_RACE);
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