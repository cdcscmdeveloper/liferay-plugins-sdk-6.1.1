package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.persistence.entity.pm.common.PmZoneState;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.zone.ZoneManagedBean;

/**
 * AuditPMSPZBackingBean
 * 
 * @author normuliana
 * 
 */
public class AuditPMSupplierZoneBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AuditPMSPDTO detailObjStatus = new AuditPMSPDTO();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleSupplierZone();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean managedBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		ZoneManagedBean managedBeans = (ZoneManagedBean) managedBean;

		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		if (null != managedBeans.getPmZone().getZoneId() && managedBeans.getPmZoneOld().getZoneId() != null) {
			auditPMReqDTO = updateDetail(managedBeans, auditPMReqDTO, objList, roleCode);
		} else {
			/*
			 * New created
			 */
			detailObjStatus.setRoleCode(roleCode);
			detailObjStatus.setDoctype(AuditConst.PM_AUD_SPZ);
			detailObjStatus.setPmParameterLvlId(AuditConst.PM_AUD_SPZ_PMPARAMETERLVLID);
			detailObjStatus.setDocNo(managedBeans.getPmZone().getZoneCode());
			detailObjStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPZ);
			detailObjStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPZ);
			detailObjStatus.setDocId(BigInteger.valueOf(managedBeans.getZoneResDTO().getPmZone().getZoneId()));
			detailObjStatus.setFieldLabelCode(AuditConst.PM_AUD_SPZ_STATUS_FIELDLABELID);
			detailObjStatus
					.setNewValue(managedBeans.getZoneResDTO().getPmZone().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjStatus);

			AuditPMSPDTO zoneCode = new AuditPMSPDTO();
			zoneCode.setRoleCode(roleCode);
			zoneCode.setDoctype(AuditConst.PM_AUD_SPZ);
			zoneCode.setPmParameterLvlId(AuditConst.PM_AUD_SPZ_PMPARAMETERLVLID);
			zoneCode.setDocNo(managedBeans.getPmZone().getZoneCode());
			zoneCode.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			zoneCode.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPZ);
			zoneCode.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPZ);
			zoneCode.setDocId(BigInteger.valueOf(managedBeans.getZoneResDTO().getPmZone().getZoneId()));
			zoneCode.setFieldLabelCode(AuditConst.PM_AUD_SPZ_ZONECODE_FIELDLABELID);
			zoneCode.setNewValue(managedBeans.getZoneResDTO().getPmZone().getZoneCode());
			zoneCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(zoneCode);

			AuditPMSPDTO zoneName = new AuditPMSPDTO();
			zoneName.setRoleCode(roleCode);
			zoneName.setDoctype(AuditConst.PM_AUD_SPZ);
			zoneName.setPmParameterLvlId(AuditConst.PM_AUD_SPZ_PMPARAMETERLVLID);
			zoneName.setDocNo(managedBeans.getPmZone().getZoneCode());
			zoneName.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			zoneName.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPZ);
			zoneName.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPZ);
			zoneName.setDocId(BigInteger.valueOf(managedBeans.getZoneResDTO().getPmZone().getZoneId()));
			zoneName.setFieldLabelCode(AuditConst.PM_AUD_SPZ_ZONENAME_FIELDLABELID);
			zoneName.setNewValue(managedBeans.getZoneResDTO().getPmZone().getZoneName());
			zoneName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(zoneName);

			auditPMReqDTO.setObjList(objList);

			if (null != managedBeans.getZoneResDTO().getPmZone().getPmZoneStates()
					&& !managedBeans.getZoneResDTO().getPmZone().getPmZoneStates().isEmpty()) {
				auditPMReqDTO = zoneStateCodeSateName(managedBeans, auditPMReqDTO, objList, roleCode);
			}
		}

		managedBeans.setZoneResDTO(null);
		managedBeans.setPmZoneOld(null);
		managedBeans.setZoneStateListNewAdded(null);
		managedBeans.setZoneStateListDeleted(null);

		return auditPMReqDTO;
	}

	private AuditPMReqDTO updateDetail(ZoneManagedBean managedBeans, AuditPMReqDTO auditPMReqDTO,
			ArrayList<AuditPMSPDTO> objList, String roleCode) {
		if (managedBeans.getPmZoneOld().getRecordStatusFlg() != managedBeans.getPmZone().getRecordStatusFlg()) {
			detailObjStatus.setRoleCode(roleCode);
			detailObjStatus.setDoctype(AuditConst.PM_AUD_SPZ);
			detailObjStatus.setPmParameterLvlId(AuditConst.PM_AUD_SPZ_PMPARAMETERLVLID);
			detailObjStatus.setDocNo(managedBeans.getPmZone().getZoneCode());
			detailObjStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			detailObjStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPZ);
			detailObjStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPZ);
			detailObjStatus.setDocId(BigInteger.valueOf(managedBeans.getPmZone().getZoneId()));
			detailObjStatus.setFieldLabelCode(AuditConst.PM_AUD_SPZ_STATUS_FIELDLABELID);
			detailObjStatus
					.setOldValue(managedBeans.getPmZoneOld().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setNewValue(managedBeans.getResZone().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(detailObjStatus);
			auditPMReqDTO.setObjList(objList);
		}
		// deleted rows
		if (null != managedBeans.getZoneStateListDeleted() && !managedBeans.getZoneStateListDeleted().isEmpty()) {
			zoneStateListDeleted(managedBeans, auditPMReqDTO, objList, roleCode);
		}
		/* add new rows */
		if (null != managedBeans.getZoneStateListNewAdded() && !managedBeans.getZoneStateListNewAdded().isEmpty()) {
			zoneStateListNewAdded(managedBeans, auditPMReqDTO, objList, roleCode);
		}
		return auditPMReqDTO;
	}

	private AuditPMReqDTO zoneStateListDeleted(ZoneManagedBean managedBeans, AuditPMReqDTO auditPMReqDTO,
			ArrayList<AuditPMSPDTO> objList, String roleCode) {
		for (PmZoneState st : managedBeans.getZoneStateListDeleted()) {
			if (st.getPmState().getStateCode() != null) {
				AuditPMSPDTO dtlObjStateCodeAdd = new AuditPMSPDTO();
				dtlObjStateCodeAdd.setRoleCode(roleCode);
				dtlObjStateCodeAdd.setDoctype(AuditConst.PM_AUD_SPZ);
				dtlObjStateCodeAdd.setPmParameterLvlId(AuditConst.PM_AUD_SPZ_PMPARAMETERLVLID);
				dtlObjStateCodeAdd.setDocNo(managedBeans.getPmZone().getZoneCode());
				dtlObjStateCodeAdd.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				dtlObjStateCodeAdd.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPZ);
				dtlObjStateCodeAdd.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPZ);
				dtlObjStateCodeAdd.setOldValue(st.getPmState().getStateCode());
				dtlObjStateCodeAdd.setDocId(BigInteger.valueOf(st.getPmState().getStateId()));
				dtlObjStateCodeAdd.setDocGroupId(BigInteger.valueOf(managedBeans.getPmZone().getZoneId()));
				dtlObjStateCodeAdd.setFieldLabelCode(AuditConst.PM_AUD_SPZ_STATECODE_FIELDLABELID);
				dtlObjStateCodeAdd.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(dtlObjStateCodeAdd);
				auditPMReqDTO.setObjList(objList);
			}
			if (st.getPmState().getStateName() != null) {
				AuditPMSPDTO dtlObjStateNameAdd = new AuditPMSPDTO();
				dtlObjStateNameAdd.setRoleCode(roleCode);
				dtlObjStateNameAdd.setDoctype(AuditConst.PM_AUD_SPZ);
				dtlObjStateNameAdd.setPmParameterLvlId(AuditConst.PM_AUD_SPZ_PMPARAMETERLVLID);
				dtlObjStateNameAdd.setDocNo(managedBeans.getPmZone().getZoneCode());
				dtlObjStateNameAdd.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				dtlObjStateNameAdd.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPZ);
				dtlObjStateNameAdd.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPZ);
				dtlObjStateNameAdd.setOldValue(st.getPmState().getStateName());
				dtlObjStateNameAdd.setDocId(BigInteger.valueOf(st.getPmState().getStateId()));
				dtlObjStateNameAdd.setDocGroupId(BigInteger.valueOf(managedBeans.getPmZone().getZoneId()));
				dtlObjStateNameAdd.setFieldLabelCode(AuditConst.PM_AUD_SPZ_STATENAME_FIELDLABELID);
				dtlObjStateNameAdd.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);
				objList.add(dtlObjStateNameAdd);
				auditPMReqDTO.setObjList(objList);
			}
		}
		auditPMReqDTO.setObjList(objList);
		managedBeans.setZoneStateListDeleted(new ArrayList<PmZoneState>());

		return auditPMReqDTO;
	}

	private AuditPMReqDTO zoneStateListNewAdded(ZoneManagedBean managedBeans, AuditPMReqDTO auditPMReqDTO,
			ArrayList<AuditPMSPDTO> objList, String roleCode) {
		for (PmZoneState st : managedBeans.getZoneStateListNewAdded()) {
			if (st.getPmState().getStateCode() != null && st.isBrandNew()) {
				AuditPMSPDTO dtlObjStateCodeAdd = new AuditPMSPDTO();
				dtlObjStateCodeAdd.setRoleCode(roleCode);
				dtlObjStateCodeAdd.setDoctype(AuditConst.PM_AUD_SPZ);
				dtlObjStateCodeAdd.setPmParameterLvlId(AuditConst.PM_AUD_SPZ_PMPARAMETERLVLID);
				dtlObjStateCodeAdd.setDocNo(managedBeans.getPmZone().getZoneCode());
				dtlObjStateCodeAdd.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				dtlObjStateCodeAdd.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPZ);
				dtlObjStateCodeAdd.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPZ);
				dtlObjStateCodeAdd.setNewValue(st.getPmState().getStateCode());
				dtlObjStateCodeAdd.setDocId(BigInteger.valueOf(st.getPmState().getStateId()));
				dtlObjStateCodeAdd.setDocGroupId(BigInteger.valueOf(st.getPmZone().getZoneId()));
				dtlObjStateCodeAdd.setFieldLabelCode(AuditConst.PM_AUD_SPZ_STATECODE_FIELDLABELID);
				dtlObjStateCodeAdd.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(dtlObjStateCodeAdd);
				auditPMReqDTO.setObjList(objList);
			}
			if (st.getPmState().getStateName() != null && st.isBrandNew()) {
				AuditPMSPDTO dtlObjStateNameAdd = new AuditPMSPDTO();
				dtlObjStateNameAdd.setRoleCode(roleCode);
				dtlObjStateNameAdd.setDoctype(AuditConst.PM_AUD_SPZ);
				dtlObjStateNameAdd.setPmParameterLvlId(AuditConst.PM_AUD_SPZ_PMPARAMETERLVLID);
				dtlObjStateNameAdd.setDocNo(managedBeans.getPmZone().getZoneCode());
				dtlObjStateNameAdd.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				dtlObjStateNameAdd.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPZ);
				dtlObjStateNameAdd.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPZ);
				dtlObjStateNameAdd.setNewValue(st.getPmState().getStateName());
				dtlObjStateNameAdd.setDocId(BigInteger.valueOf(st.getPmState().getStateId()));
				dtlObjStateNameAdd.setDocGroupId(BigInteger.valueOf(st.getPmZone().getZoneId()));
				dtlObjStateNameAdd.setFieldLabelCode(AuditConst.PM_AUD_SPZ_STATENAME_FIELDLABELID);
				dtlObjStateNameAdd.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(dtlObjStateNameAdd);
				auditPMReqDTO.setObjList(objList);
			}
		}
		managedBeans.setZoneStateListNewAdded(new ArrayList<PmZoneState>());

		return auditPMReqDTO;
	}

	private AuditPMReqDTO zoneStateCodeSateName(ZoneManagedBean managedBeans, AuditPMReqDTO auditPMReqDTO,
			ArrayList<AuditPMSPDTO> objList, String roleCode) {
		for (PmZoneState st : managedBeans.getZoneResDTO().getPmZone().getPmZoneStates()) {
			if (st.getPmState().getStateCode() != null) {
				AuditPMSPDTO dtlObjStateCodeAdd = new AuditPMSPDTO();
				dtlObjStateCodeAdd.setRoleCode(roleCode);
				dtlObjStateCodeAdd.setDoctype(AuditConst.PM_AUD_SPZ);
				dtlObjStateCodeAdd.setPmParameterLvlId(AuditConst.PM_AUD_SPZ_PMPARAMETERLVLID);
				dtlObjStateCodeAdd.setDocNo(managedBeans.getPmZone().getZoneCode());
				dtlObjStateCodeAdd.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				dtlObjStateCodeAdd.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPZ);
				dtlObjStateCodeAdd.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPZ);
				dtlObjStateCodeAdd.setNewValue(st.getPmState().getStateCode());
				dtlObjStateCodeAdd.setDocId(BigInteger.valueOf(st.getPmState().getStateId()));
				dtlObjStateCodeAdd.setDocGroupId(BigInteger.valueOf(st.getPmZone().getZoneId()));
				dtlObjStateCodeAdd.setFieldLabelCode(AuditConst.PM_AUD_SPZ_STATECODE_FIELDLABELID);
				dtlObjStateCodeAdd.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(dtlObjStateCodeAdd);
				auditPMReqDTO.setObjList(objList);
			}
			if (st.getPmState().getStateName() != null) {
				AuditPMSPDTO dtlObjStateNameAdd = new AuditPMSPDTO();
				dtlObjStateNameAdd.setRoleCode(roleCode);
				dtlObjStateNameAdd.setDoctype(AuditConst.PM_AUD_SPZ);
				dtlObjStateNameAdd.setPmParameterLvlId(AuditConst.PM_AUD_SPZ_PMPARAMETERLVLID);
				dtlObjStateNameAdd.setDocNo(managedBeans.getPmZone().getZoneCode());
				dtlObjStateNameAdd.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				dtlObjStateNameAdd.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPZ);
				dtlObjStateNameAdd.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPZ);
				dtlObjStateNameAdd.setNewValue(st.getPmState().getStateName());
				dtlObjStateNameAdd.setDocId(BigInteger.valueOf(st.getPmState().getStateId()));
				dtlObjStateNameAdd.setDocGroupId(BigInteger.valueOf(st.getPmZone().getZoneId()));
				dtlObjStateNameAdd.setFieldLabelCode(AuditConst.PM_AUD_SPZ_STATENAME_FIELDLABELID);
				dtlObjStateNameAdd.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(dtlObjStateNameAdd);
				auditPMReqDTO.setObjList(objList);
			}
		}
		auditPMReqDTO.setObjList(objList);

		return auditPMReqDTO;
	}
}