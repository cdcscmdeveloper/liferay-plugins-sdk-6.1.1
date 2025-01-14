package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmZoneState;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.zone.ZoneManagedBean;

/**
 * AuditPMSPZBackingBean #28895-35509
 * 
 * @author normuliana
 * 
 */
public class AuditPMSupplierZoneBackingBean extends AuditPMBackingBeanTemplate {
	private static final long serialVersionUID = 1L;
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	String roleCode;
	String docNo;
	BigInteger docId;
	BigInteger docGroupId;

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
		ZoneManagedBean managedBeans = (ZoneManagedBean) managedBean;

		this.roleCode = roleCode;
		this.docNo = managedBeans.getPmZone().getZoneCode();

		if (null != managedBeans.getPmZone().getZoneId() && managedBeans.getPmZoneOld().getZoneId() != null) {
			updateDetail(managedBeans);
		} else {
			/*
			 * New created
			 */
			this.docGroupId = BigInteger.valueOf(managedBeans.getZoneResDTO().getPmZone().getZoneId());
			this.docId = BigInteger.valueOf(managedBeans.getZoneResDTO().getPmZone().getZoneId());

			detailObj(AuditConst.PM_AUD_SPZ_STATUS_FIELDLABELID, null, managedBeans.getZoneResDTO().getPmZone()
					.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_SPZ_ZONECODE_FIELDLABELID, null, managedBeans.getZoneResDTO().getPmZone().getZoneCode(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_SPZ_ZONENAME_FIELDLABELID, null, managedBeans.getZoneResDTO().getPmZone().getZoneName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			if (null != managedBeans.getZoneResDTO().getPmZone().getPmZoneStates()
					&& !managedBeans.getZoneResDTO().getPmZone().getPmZoneStates().isEmpty()) {
				zoneStateCodeSateName(managedBeans);
			}
		}
		managedBeans.setZoneResDTO(null);
		managedBeans.setPmZoneOld(null);
		managedBeans.setZoneStateListNewAdded(null);
		managedBeans.setZoneStateListDeleted(null);

		auditPMReqDTO.setObjList(objList);

		return auditPMReqDTO;
	}

	private void updateDetail(ZoneManagedBean managedBeans) {
		if (managedBeans.getPmZoneOld().getRecordStatusFlg() != managedBeans.getPmZone().getRecordStatusFlg()) {
			this.docGroupId = BigInteger.valueOf(managedBeans.getPmZone().getZoneId());
			this.docId = BigInteger.valueOf(managedBeans.getPmZone().getZoneId());
			detailObj(
					AuditConst.PM_AUD_SPZ_STATUS_FIELDLABELID,
					managedBeans.getPmZoneOld().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					managedBeans.getResZone().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}
		// deleted rows
		if (null != managedBeans.getZoneStateListDeleted() && !managedBeans.getZoneStateListDeleted().isEmpty()) {
			zoneStateListDeleted(managedBeans);
		}
		/* add new rows */
		if (null != managedBeans.getZoneStateListNewAdded() && !managedBeans.getZoneStateListNewAdded().isEmpty()) {
			zoneStateListNewAdded(managedBeans);
		}
	}

	private void zoneStateListDeleted(ZoneManagedBean managedBeans) {
		for (PmZoneState st : managedBeans.getZoneStateListDeleted()) {
			if (st.getPmState().getStateCode() != null) {
				this.docGroupId = BigInteger.valueOf(st.getPmZone().getZoneId());
				this.docId = BigInteger.valueOf(st.getPmState().getStateId());
				detailObj(AuditConst.PM_AUD_SPZ_STATECODE_FIELDLABELID, st.getPmState().getStateCode(), null,
						AuditConst.PM_AUD_ACTION_TYPE_DEL);
			}
			if (st.getPmState().getStateName() != null) {
				this.docGroupId = BigInteger.valueOf(st.getPmZone().getZoneId());
				this.docId = BigInteger.valueOf(st.getPmState().getStateId());
				detailObj(AuditConst.PM_AUD_SPZ_STATENAME_FIELDLABELID, st.getPmState().getStateName(), null,
						AuditConst.PM_AUD_ACTION_TYPE_DEL);
			}
		}
		managedBeans.setZoneStateListDeleted(new ArrayList<PmZoneState>());
	}

	private void zoneStateListNewAdded(ZoneManagedBean managedBeans) {
		for (PmZoneState st : managedBeans.getZoneStateListNewAdded()) {
			if (st.getPmState().getStateCode() != null && st.isBrandNew()) {
				this.docGroupId = BigInteger.valueOf(st.getPmZone().getZoneId());
				this.docId = BigInteger.valueOf(st.getPmState().getStateId());
				detailObj(AuditConst.PM_AUD_SPZ_STATECODE_FIELDLABELID, null, st.getPmState().getStateCode(),
						AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
			if (st.getPmState().getStateName() != null && st.isBrandNew()) {
				this.docGroupId = BigInteger.valueOf(st.getPmZone().getZoneId());
				this.docId = BigInteger.valueOf(st.getPmState().getStateId());
				detailObj(AuditConst.PM_AUD_SPZ_STATENAME_FIELDLABELID, null, st.getPmState().getStateName(),
						AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
		}
		managedBeans.setZoneStateListNewAdded(new ArrayList<PmZoneState>());
	}

	private void zoneStateCodeSateName(ZoneManagedBean managedBeans) {
		for (PmZoneState st : managedBeans.getZoneResDTO().getPmZone().getPmZoneStates()) {
			if (st.getPmState().getStateCode() != null) {
				this.docGroupId = BigInteger.valueOf(st.getPmZone().getZoneId());
				this.docId = BigInteger.valueOf(st.getPmState().getStateId());
				detailObj(AuditConst.PM_AUD_SPZ_STATECODE_FIELDLABELID, null, st.getPmState().getStateCode(),
						AuditConst.PM_AUD_ACTION_TYPE_INS);
			}
			if (st.getPmState().getStateName() != null) {
				this.docGroupId = BigInteger.valueOf(st.getPmZone().getZoneId());
				this.docId = BigInteger.valueOf(st.getPmState().getStateId());
				detailObj(AuditConst.PM_AUD_SPZ_STATENAME_FIELDLABELID, null, st.getPmState().getStateName(),
						AuditConst.PM_AUD_ACTION_TYPE_INS);
			}
		}
	}

	private void detailObj(BigInteger fieldLabelId, String oldValue, String newValue, String actionType) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(AuditConst.PM_AUD_SPZ);
		dtlObj.setPmParameterLvlId(AuditConst.PM_AUD_SPZ_PMPARAMETERLVLID);
		dtlObj.setDocNo(docNo);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPZ);
		dtlObj.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPZ);
		dtlObj.setDocId(docId);
		dtlObj.setDocGroupId(docGroupId);
		dtlObj.setFieldLabelCode(fieldLabelId);
		if (oldValue != null) {
			dtlObj.setOldValue(oldValue);
		}
		dtlObj.setNewValue(newValue);
		dtlObj.setActionType(actionType);
		objList.add(dtlObj);
	}
}