package my.ep.web.backingbean.pm.audit.supplierzone;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.audit.AuditConst;
import my.ep.dto.pm.common.audit.location.AuditPMBaseDTO;
import my.ep.dto.pm.common.audit.supplierzone.AUPMSupplierzoneDetailSPDTO;
import my.ep.dto.pm.common.audit.supplierzone.AUPMSupplierzoneMasterSPDTO;
import my.ep.dto.pm.common.audit.supplierzone.AuPMSupplierzoneDTO;
import my.ep.persistence.entity.pm.common.PmZoneState;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.zone.ZoneManagedBean;

public class AuditSupplierzoneBackingBean extends AuditPMSupplierZoneBackingBeanTemplate {
	private static final long serialVersionUID = 1L;

	public AuditPMBaseDTO masterInsert(AuditPMBaseDTO au, Long userId,BaseManagedBean manageBean, String roleCode) {
		ZoneManagedBean zoneManagedBean = (ZoneManagedBean) manageBean;
		AuPMSupplierzoneDTO auPMObj = (AuPMSupplierzoneDTO)au;
		ArrayList<AUPMSupplierzoneMasterSPDTO> masterObjlist = new ArrayList<AUPMSupplierzoneMasterSPDTO>();
		AUPMSupplierzoneMasterSPDTO msterObj = new AUPMSupplierzoneMasterSPDTO();
		if(auPMObj.isAuditEnable()){
		msterObj.setRoleCode(roleCode);
		msterObj.setDoctype(AuditConst.PM_AUD_SPZ);
		msterObj.setPmParameterLvlId(AuditConst.PM_AUD_SPZ_PMPARAMETERLVLID);
		msterObj.setDocNo(zoneManagedBean.getPmZone().getZoneCode());
		msterObj.setPkId(zoneManagedBean.getResZone().getZoneId());
		msterObj.setColumnName(AuditConst.PM_AUD_SPZ_SOURCECOLUMN);
		masterObjlist.add(msterObj);
		}
		auPMObj.setSpzMasterObjlist(masterObjlist);
		
		return auPMObj;
	}

	public AuditPMBaseDTO detailsInsert(BaseManagedBean managedBean, Long userId) {
		ZoneManagedBean zoneManagedBean = (ZoneManagedBean) managedBean;
		AuPMSupplierzoneDTO auPMObj = new AuPMSupplierzoneDTO();
		auPMObj = insertDetailVal(auPMObj, zoneManagedBean, userId);

		return auPMObj;
	}

	List<AUPMSupplierzoneDetailSPDTO> detailObjlist = new ArrayList<AUPMSupplierzoneDetailSPDTO>();
	AUPMSupplierzoneDetailSPDTO dtlObjStatus = new AUPMSupplierzoneDetailSPDTO();

	ArrayList<AUPMSupplierzoneDetailSPDTO> dtlObjlist = new ArrayList<AUPMSupplierzoneDetailSPDTO>();

	private AuPMSupplierzoneDTO insertDetailVal(AuPMSupplierzoneDTO auPMObj, ZoneManagedBean zoneManagedBean,
			Long userId) {
		if (null != zoneManagedBean.getPmZone().getZoneId() && zoneManagedBean.getPmZoneOld().getZoneId() != null) {
			if (zoneManagedBean.getPmZoneOld().getRecordStatusFlg() != zoneManagedBean.getPmZone().getRecordStatusFlg()) {
				dtlObjStatus.setDocId(zoneManagedBean.getPmZone().getZoneId().intValue());
				dtlObjStatus.setPmFieldLabelid(AuditConst.PM_AUD_SPZ_STATUS_FIELDLABELID);
				dtlObjStatus
						.setOldValue(zoneManagedBean.getPmZoneOld().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus
						.setNewValue(zoneManagedBean.getResZone().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				dtlObjlist.add(dtlObjStatus);
				auPMObj.setSpzDetailObjlist(dtlObjlist);
				auPMObj.setAuditEnable(true);
			}
			// deleted rows
			if (null != zoneManagedBean.getZoneStateListDeleted() && zoneManagedBean.getZoneStateListDeleted().size() > 0) {		
				for (PmZoneState st : zoneManagedBean.getZoneStateListDeleted()) {
						if(st.getPmState().getStateCode() != null){
						AUPMSupplierzoneDetailSPDTO dtlObjStateCodeAdd = new AUPMSupplierzoneDetailSPDTO();
						dtlObjStateCodeAdd.setNewValue(st.getPmState().getStateCode());
						dtlObjStateCodeAdd.setDocId(zoneManagedBean.getPmZone().getZoneId().intValue());
						dtlObjStateCodeAdd.setPmFieldLabelid(AuditConst.PM_AUD_SPZ_STATECODE_FIELDLABELID);
						dtlObjStateCodeAdd.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);
						dtlObjlist.add(dtlObjStateCodeAdd);
						auPMObj.setSpzDetailObjlist(dtlObjlist);
						}
						if(st.getPmState().getStateName() != null){
							AUPMSupplierzoneDetailSPDTO dtlObjStateNameAdd = new AUPMSupplierzoneDetailSPDTO();
							dtlObjStateNameAdd.setNewValue(st.getPmState().getStateName());
							dtlObjStateNameAdd.setDocId(zoneManagedBean.getPmZone().getZoneId().intValue());
							dtlObjStateNameAdd.setPmFieldLabelid(AuditConst.PM_AUD_SPZ_STATENAME_FIELDLABELID);
							dtlObjStateNameAdd.setActionType(AuditConst.PM_AUD_ACTION_TYPE_DEL);
							dtlObjlist.add(dtlObjStateNameAdd);
							auPMObj.setSpzDetailObjlist(dtlObjlist);
							}		
					}
				auPMObj.setSpzDetailObjlist(dtlObjlist);
				auPMObj.setAuditEnable(true);
			}
			/* add new rows	*/		
			if (null != zoneManagedBean.getZoneStateListNewAdded() && zoneManagedBean.getZoneStateListNewAdded().size() > 0) {
				for(PmZoneState st : zoneManagedBean.getZoneStateListNewAdded()){
							if(st.getPmState().getStateCode() != null && st.isBrandNew()){
							AUPMSupplierzoneDetailSPDTO dtlObjStateCodeAdd = new AUPMSupplierzoneDetailSPDTO();
							dtlObjStateCodeAdd.setNewValue(st.getPmState().getStateCode());
							dtlObjStateCodeAdd.setDocId(zoneManagedBean.getPmZone().getZoneId().intValue());
							dtlObjStateCodeAdd.setPmFieldLabelid(AuditConst.PM_AUD_SPZ_STATECODE_FIELDLABELID);
							dtlObjStateCodeAdd.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
							dtlObjlist.add(dtlObjStateCodeAdd);
							auPMObj.setSpzDetailObjlist(dtlObjlist);
							}
							if(st.getPmState().getStateName() != null && st.isBrandNew()){
								AUPMSupplierzoneDetailSPDTO dtlObjStateNameAdd = new AUPMSupplierzoneDetailSPDTO();
								dtlObjStateNameAdd.setNewValue(st.getPmState().getStateName());
								dtlObjStateNameAdd.setDocId(zoneManagedBean.getPmZone().getZoneId().intValue());
								dtlObjStateNameAdd.setPmFieldLabelid(AuditConst.PM_AUD_SPZ_STATENAME_FIELDLABELID);
								dtlObjStateNameAdd.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
								dtlObjlist.add(dtlObjStateNameAdd);
								auPMObj.setSpzDetailObjlist(dtlObjlist);
								}						
					}					
					auPMObj.setAuditEnable(true);
			}
		} else {
			dtlObjStatus.setDocId(zoneManagedBean.getZoneResDTO().getPmZone().getZoneId().intValue());
			dtlObjStatus.setPmFieldLabelid(AuditConst.PM_AUD_SPZ_STATUS_FIELDLABELID);
			dtlObjStatus
					.setNewValue(zoneManagedBean.getZoneResDTO().getPmZone().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
			dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(dtlObjStatus);
			
			AUPMSupplierzoneDetailSPDTO zoneCode = new AUPMSupplierzoneDetailSPDTO();
			zoneCode.setDocId(zoneManagedBean.getZoneResDTO().getPmZone().getZoneId().intValue());
			zoneCode.setPmFieldLabelid(AuditConst.PM_AUD_SPZ_ZONECODE_FIELDLABELID);
			zoneCode.setNewValue(zoneManagedBean.getZoneResDTO().getPmZone().getZoneCode());
			zoneCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(zoneCode);
			
			AUPMSupplierzoneDetailSPDTO zoneName = new AUPMSupplierzoneDetailSPDTO();
			zoneName.setDocId(zoneManagedBean.getZoneResDTO().getPmZone().getZoneId().intValue());
			zoneName.setPmFieldLabelid(AuditConst.PM_AUD_SPZ_ZONENAME_FIELDLABELID);
			zoneName.setNewValue(zoneManagedBean.getZoneResDTO().getPmZone().getZoneName());
			zoneName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(zoneName);
			
			auPMObj.setSpzDetailObjlist(dtlObjlist);
				
				if (null != zoneManagedBean.getZoneResDTO().getPmZone().getPmZoneStates() && zoneManagedBean.getZoneResDTO().getPmZone().getPmZoneStates().size() > 0) {
				for(PmZoneState st : zoneManagedBean.getZoneResDTO().getPmZone().getPmZoneStates()){
						if(st.getPmState().getStateCode() != null){
						AUPMSupplierzoneDetailSPDTO dtlObjStateCodeAdd = new AUPMSupplierzoneDetailSPDTO();
						dtlObjStateCodeAdd.setNewValue(st.getPmState().getStateCode());
						dtlObjStateCodeAdd.setDocId(st.getPmZone().getZoneId().intValue());
						dtlObjStateCodeAdd.setPmFieldLabelid(AuditConst.PM_AUD_SPZ_STATECODE_FIELDLABELID);
						dtlObjStateCodeAdd.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						dtlObjlist.add(dtlObjStateCodeAdd);
						auPMObj.setSpzDetailObjlist(dtlObjlist);
						}
						if(st.getPmState().getStateName() != null){
							AUPMSupplierzoneDetailSPDTO dtlObjStateNameAdd = new AUPMSupplierzoneDetailSPDTO();
							dtlObjStateNameAdd.setNewValue(st.getPmState().getStateName());
							dtlObjStateNameAdd.setDocId(st.getPmZone().getZoneId().intValue());
							dtlObjStateNameAdd.setPmFieldLabelid(AuditConst.PM_AUD_SPZ_STATENAME_FIELDLABELID);
							dtlObjStateNameAdd.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
							dtlObjlist.add(dtlObjStateNameAdd);
							auPMObj.setSpzDetailObjlist(dtlObjlist);
							}						
				}
				auPMObj.setSpzDetailObjlist(dtlObjlist);
			}
			auPMObj.setAuditEnable(true);			
		}		
		return auPMObj;
	}
}
