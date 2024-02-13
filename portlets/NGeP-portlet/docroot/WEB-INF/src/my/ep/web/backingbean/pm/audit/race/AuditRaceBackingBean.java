package my.ep.web.backingbean.pm.audit.race;

import java.util.ArrayList;
import java.util.List;
import my.ep.constant.audit.AuditConst;
import my.ep.dto.pm.common.audit.race.AuPmRaceDTO;
import my.ep.dto.pm.common.audit.race.AuPmRaceDetailSPDTO;
import my.ep.dto.pm.common.audit.race.AuPmRaceMasterSPDTO;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.race.RaceManagedBean;

public class AuditRaceBackingBean extends AuditPMRaceBackingBeanTemplate {
	private static final long serialVersionUID = 1L;

	public AuPmRaceDTO masterInsert(AuPmRaceDTO au, Long userId, BaseManagedBean manageBean, String roleCode) {
		RaceManagedBean raceManagedBean = (RaceManagedBean) manageBean;
		AuPmRaceDTO auPMObj = (AuPmRaceDTO) au;
		ArrayList<AuPmRaceMasterSPDTO> masterObjlist = new ArrayList<AuPmRaceMasterSPDTO>();
		AuPmRaceMasterSPDTO msterObj = new AuPmRaceMasterSPDTO();

		msterObj.setRoleCode(roleCode);
		msterObj.setDoctype(AuditConst.PM_AUD_RACE);
		msterObj.setPmParameterLvlId(AuditConst.PM_AUD_RACE_PMPARAMETERLVLID);
		msterObj.setDocNo(raceManagedBean.getPmRace().getRaceCode());
		msterObj.setPkId(raceManagedBean.getPmRace().getRaceId());
		msterObj.setColumnName(AuditConst.PM_AUD_RACE_SOURCECOLUMN);
		masterObjlist.add(msterObj);

		auPMObj.setRaceMasterObjlist(masterObjlist);

		raceManagedBean.setPmRace(null);
		raceManagedBean.setOldPmRace(null);
		
		return auPMObj;
	}

	public AuPmRaceDTO detailsInsert(BaseManagedBean managedBean, Long userId) {
		RaceManagedBean raceManagedBean = (RaceManagedBean) managedBean;
		AuPmRaceDTO auPMObj = new AuPmRaceDTO();
		auPMObj = insertDetailVal(auPMObj, raceManagedBean, userId);

		return auPMObj;
	}

	List<AuPmRaceDetailSPDTO> detailObjlist = new ArrayList<AuPmRaceDetailSPDTO>();
	ArrayList<AuPmRaceDetailSPDTO> dtlObjlist = new ArrayList<AuPmRaceDetailSPDTO>();
	AuPmRaceDetailSPDTO dtlObjName = new AuPmRaceDetailSPDTO();
	AuPmRaceDetailSPDTO dtlSatus = new AuPmRaceDetailSPDTO();
	AuPmRaceDetailSPDTO dtlCode = new AuPmRaceDetailSPDTO();

	private AuPmRaceDTO insertDetailVal(AuPmRaceDTO auPMObj, RaceManagedBean raceManagedBean, Long userId) {
		if (raceManagedBean.getOldPmRace() != null && raceManagedBean.getOldPmRace().getRaceId() != null) {
			if (raceManagedBean.getPmRace().getRaceName() != null
					&& !raceManagedBean.getOldPmRace().getRaceName().equalsIgnoreCase(raceManagedBean.getPmRace().getRaceName())) {
				dtlObjName.setDocId(raceManagedBean.getPmRace().getRaceId().intValue());
				dtlObjName.setPmFieldLabelid(AuditConst.PM_AUD_RACE_NAME_FIELDLABELID);
				dtlObjName.setOldValue(raceManagedBean.getOldPmRace().getRaceName());
				dtlObjName.setNewValue(raceManagedBean.getPmRace().getRaceName());
				dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				dtlObjlist.add(dtlObjName);
				auPMObj.setRaceDetailObjlist(dtlObjlist);			
			}
			if (raceManagedBean.getOldPmRace().getRecordStatusFlg() != raceManagedBean.getPmRace().getRecordStatusFlg()) {
				dtlSatus.setDocId(raceManagedBean.getPmRace().getRaceId().intValue());
				dtlSatus.setPmFieldLabelid(AuditConst.PM_AUD_RACE_STATUS_FIELDLABELID);
				dtlSatus.setOldValue(raceManagedBean.getOldPmRace().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlSatus.setNewValue(raceManagedBean.getPmRace().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlSatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				dtlObjlist.add(dtlSatus);
				auPMObj.setRaceDetailObjlist(dtlObjlist);
			}
		} else {
			dtlCode.setDocId(raceManagedBean.getPmRace().getRaceId().intValue());
			dtlCode.setPmFieldLabelid(AuditConst.PM_AUD_RACE_CODE_FIELDLABELID);
			dtlCode.setNewValue(raceManagedBean.getPmRace().getRaceCode());
			dtlCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(dtlCode);
			auPMObj.setRaceDetailObjlist(dtlObjlist);	
			
			dtlObjName.setDocId(raceManagedBean.getPmRace().getRaceId().intValue());
			dtlObjName.setPmFieldLabelid(AuditConst.PM_AUD_RACE_NAME_FIELDLABELID);
			dtlObjName.setNewValue(raceManagedBean.getPmRace().getRaceName());
			dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(dtlObjName);
			auPMObj.setRaceDetailObjlist(dtlObjlist);		

			dtlSatus.setDocId(raceManagedBean.getPmRace().getRaceId().intValue());
			dtlSatus.setPmFieldLabelid(AuditConst.PM_AUD_RACE_STATUS_FIELDLABELID);
			dtlSatus.setNewValue(raceManagedBean.getPmRace().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
			dtlSatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(dtlSatus);
			auPMObj.setRaceDetailObjlist(dtlObjlist);
		}
		return auPMObj;
	}		
}
