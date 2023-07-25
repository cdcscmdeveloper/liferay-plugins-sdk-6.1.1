package my.ep.web.backingbean.pm.location.audit;

import java.util.ArrayList;

import my.ep.constant.audit.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.audittrail.AUPMDetailSPDTO;
import my.ep.dto.pm.common.audittrail.AuPMLocationDTO;
import my.ep.dto.pm.common.audittrail.AuditBaseDTO;
import my.ep.web.managedbean.pm.location.LocationManagedBean;

public class AuditLocationMapInsertVal extends AuditBaseDTO {

	private static final long serialVersionUID = 1L;
	AUPMDetailSPDTO detailObjName = new AUPMDetailSPDTO();
	AUPMDetailSPDTO detailObjDV = new AUPMDetailSPDTO();
	AUPMDetailSPDTO detailObjStatus = new AUPMDetailSPDTO();
	ArrayList<AUPMDetailSPDTO> detailObjlist = new ArrayList<AUPMDetailSPDTO>();

	public AuPMLocationDTO auLocationMapInsertVal(LocationManagedBean locationManagedBean, Long userId) {
		AuPMLocationDTO auPMDTOObj = new AuPMLocationDTO();
		
		if (ParameterConst.LOCATION_LEVEL_STATE.equals(locationManagedBean.getSelectedType())) {
			auPMDTOObj.setST(true);
		}
		if (ParameterConst.LOCATION_LEVEL_DIVISION.equals(locationManagedBean.getSelectedType())) {
			auPMDTOObj.setDV(true);
		}
		if (ParameterConst.LOCATION_LEVEL_DISTRICT.equals(locationManagedBean.getSelectedType())) {
			auPMDTOObj.setDS(true);
		}
		if (ParameterConst.LOCATION_LEVEL_CITY.equals(locationManagedBean.getSelectedType())) {
			auPMDTOObj.setCT(true);
		}			
		if (auPMDTOObj.getDV()) {			
			auPMDTOObj = insertDVVal(auPMDTOObj,locationManagedBean);
		}
		if (auPMDTOObj.getST()) {
			auPMDTOObj = insertSTVal(auPMDTOObj,locationManagedBean ,userId);
		}
		if (auPMDTOObj.getDS()) {
			auPMDTOObj = insertDSVal(auPMDTOObj,locationManagedBean);			
		}
		if (auPMDTOObj.getCT()) {
			auPMDTOObj = insertCTVal(auPMDTOObj,locationManagedBean);
		}
		return auPMDTOObj;
	}
	
	private AuPMLocationDTO insertDVVal(AuPMLocationDTO auPMDTOObj,LocationManagedBean locationManagedBean){
		detailObjName.setDocId(locationManagedBean.getResDivision().getDivisionId().intValue());
		detailObjStatus.setDocId(locationManagedBean.getResDivision().getDivisionId().intValue());
		detailObjStatus.setFieldLabelid(AuditConst.PM_AUD_DIV_DVSTATUS_FieldLabelid);
		detailObjName.setFieldLabelid(AuditConst.PM_AUD_DIV_DVNAME_FieldLabelid);
		auPMDTOObj.setLocCategory("DV");
		if (null != locationManagedBean.getDivision().getDivisionId()&& locationManagedBean.getDivisionOldVal().getPmDivision().getDivisionId() != null) {
			auPMDTOObj.setLocOldDtlVal(true);
			if (!locationManagedBean.getResDivision().getDivisionName().equalsIgnoreCase(locationManagedBean.getDivisionOldVal().getPmDivision().getDivisionName())) {
				auPMDTOObj.setLocOldNameVal(true);			
				detailObjName.setOldValue(locationManagedBean.getDivisionOldVal().getPmDivision().getDivisionName());
				detailObjName.setNewValue(locationManagedBean.getResDivision().getDivisionName());
				detailObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(detailObjName);
				auPMDTOObj.setDetailObjlist(detailObjlist);
			}
			if (locationManagedBean.getResDivision().getRecordStatusFlg() != locationManagedBean.getDivisionOldVal().getPmDivision().getRecordStatusFlg()) {
				auPMDTOObj.setLocOldStatusVal(true);
				detailObjStatus.setOldValue(locationManagedBean.getDivisionOldVal().getPmDivision().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				detailObjStatus.setNewValue(locationManagedBean.getResDivision().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(detailObjStatus);
				auPMDTOObj.setDetailObjlist(detailObjlist);
			}
		}else{
			/** INSERT NEW transaction ***/
			detailObjName.setNewValue(locationManagedBean.getResDivision().getDivisionName());
			detailObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);		
			
			detailObjStatus.setNewValue(locationManagedBean.getResDivision().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(detailObjName);
			detailObjlist.add(detailObjStatus);
			auPMDTOObj.setDetailObjlist(detailObjlist);
		}
		return auPMDTOObj;
	}

	private AuPMLocationDTO insertSTVal(AuPMLocationDTO auPMDTOObj,LocationManagedBean locationManagedBean,Long userId){
		auPMDTOObj.setLocCategory("ST");
		detailObjName.setDocId(locationManagedBean.getResState().getStateId().intValue());
		detailObjStatus.setDocId(locationManagedBean.getResState().getStateId().intValue());
		detailObjDV.setDocId(locationManagedBean.getResState().getStateId().intValue());		
		if (null != locationManagedBean.getState().getStateId() && locationManagedBean.getStateOldVal().getPmState().getStateId() != null) {
			auPMDTOObj.setLocOldDtlVal(true);
			if (!locationManagedBean.getState().getStateName().equalsIgnoreCase(locationManagedBean.getStateOldVal().getPmState().getStateName())) {
				auPMDTOObj.setLocOldNameVal(true);
				detailObjName.setFieldLabelid(AuditConst.PM_AUD_ST_STNAME_FieldLabelid);
				detailObjName.setOldValue(locationManagedBean.getStateOldVal().getPmState().getStateName());
				detailObjName.setNewValue(locationManagedBean.getResState().getStateName());
				detailObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(detailObjName);
				auPMDTOObj.setDetailObjlist(detailObjlist);
			}
			if (locationManagedBean.getResState().getAdoptDivision() != locationManagedBean.getStateOldVal().getPmState().getAdoptDivision()) {
				auPMDTOObj.setLocOldAdoptDivVal(true);
				detailObjDV.setFieldLabelid(AuditConst.PM_AUD_ST_STDIV_FieldLabelid);
				detailObjDV.setOldValue(locationManagedBean.getStateOldVal().getPmState().getAdoptDivision() == true ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
				detailObjDV.setNewValue(locationManagedBean.getResState().getAdoptDivision() == true ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
				detailObjDV.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);				
				detailObjlist.add(detailObjDV);
				auPMDTOObj.setDetailObjlist(detailObjlist);
			}
			if (locationManagedBean.getResState().getRecordStatusFlg() != locationManagedBean.getStateOldVal().getPmState().getRecordStatusFlg()) {
				auPMDTOObj.setLocOldStatusVal(true);
				detailObjStatus.setFieldLabelid(AuditConst.PM_AUD_ST_STSTATUS_FieldLabelid);
				detailObjStatus.setOldValue(locationManagedBean.getStateOldVal().getPmState().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				detailObjStatus.setNewValue(locationManagedBean.getResState().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(detailObjStatus);
				auPMDTOObj.setDetailObjlist(detailObjlist);
			}
		} else {
			detailObjName.setFieldLabelid(AuditConst.PM_AUD_ST_STNAME_FieldLabelid);
			detailObjName.setNewValue(locationManagedBean.getResState().getStateName());
			detailObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(detailObjName);

			detailObjDV.setFieldLabelid(AuditConst.PM_AUD_ST_STDIV_FieldLabelid);
			detailObjDV.setNewValue(locationManagedBean.getResState().getAdoptDivision() == true ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			detailObjDV.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(detailObjDV);

			detailObjStatus.setFieldLabelid(AuditConst.PM_AUD_ST_STSTATUS_FieldLabelid);
			detailObjStatus.setNewValue(locationManagedBean.getResState().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(detailObjStatus);
			
			auPMDTOObj.setDetailObjlist(detailObjlist);
		}
		return auPMDTOObj;
	}
	
	private AuPMLocationDTO insertDSVal(AuPMLocationDTO auPMDTOObj,LocationManagedBean locationManagedBean){
		auPMDTOObj.setLocCategory("DS");
		detailObjName.setFieldLabelid(AuditConst.PM_AUD_ST_DSNAME_FieldLabelid);
		detailObjName.setDocId(locationManagedBean.getResDistrict().getDistrictId().intValue());
		detailObjStatus.setFieldLabelid(AuditConst.PM_AUD_ST_DSSTATUS_FieldLabelid); // District
		detailObjStatus.setDocId(locationManagedBean.getResDistrict().getDistrictId().intValue());	
		if (null != locationManagedBean.getDistrict().getDistrictId() && locationManagedBean.getDistrictOldVal().getPmDistrict().getDistrictId() != null) {
			/** UPDATE transaction ***/
			if (!locationManagedBean.getResDistrict().getDistrictName().equalsIgnoreCase(locationManagedBean.getDistrictOldVal().getPmDistrict().getDistrictName())) {
				detailObjName.setOldValue(locationManagedBean.getDistrictOldVal().getPmDistrict().getDistrictName());
				detailObjName.setNewValue(locationManagedBean.getResDistrict().getDistrictName());
				detailObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(detailObjName);
				auPMDTOObj.setDetailObjlist(detailObjlist);
			}
			if (locationManagedBean.getResDistrict().getRecordStatusFlg() != locationManagedBean.getDistrictOldVal().getPmDistrict().getRecordStatusFlg()) {
				detailObjStatus.setOldValue(locationManagedBean.getDistrictOldVal().getPmDistrict().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				detailObjStatus.setNewValue(locationManagedBean.getResDistrict().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(detailObjStatus);
				auPMDTOObj.setDetailObjlist(detailObjlist);
			}
		} else {
			/** INSERT NEW transaction ***/
			detailObjName.setNewValue(locationManagedBean.getResDistrict().getDistrictName());
			detailObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObjStatus.setNewValue(locationManagedBean.getResDistrict().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObjlist.add(detailObjName);
			detailObjlist.add(detailObjStatus);
			auPMDTOObj.setDetailObjlist(detailObjlist);
		}
		return auPMDTOObj;
	}
	
	private AuPMLocationDTO insertCTVal(AuPMLocationDTO auPMDTOObj,LocationManagedBean locationManagedBean){
		auPMDTOObj.setLocCategory("CT");
		detailObjName.setDocId(locationManagedBean.getResCity().getCityId().intValue());
		detailObjName.setFieldLabelid(AuditConst.PM_AUD_CT_CTNAME_FieldLabelid);
		detailObjStatus.setFieldLabelid(AuditConst.PM_AUD_CT_CTSTATUS_FieldLabelid); // City
		detailObjStatus.setDocId(locationManagedBean.getResCity().getCityId().intValue());						
		if (null != locationManagedBean.getCity().getCityId() && locationManagedBean.getCityOldVal().getPmCity().getCityId() != null) {
			auPMDTOObj.setLocOldDtlVal(true);
			/** UPDATE transaction ***/
			if (!locationManagedBean.getResCity().getCityName().equalsIgnoreCase(locationManagedBean.getCityOldVal().getPmCity().getCityName())) {
				auPMDTOObj.setLocOldNameVal(true);	
				detailObjName.setOldValue(locationManagedBean.getCityOldVal().getPmCity().getCityName());
				detailObjName.setNewValue(locationManagedBean.getResCity().getCityName());
				detailObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(detailObjName);
				auPMDTOObj.setDetailObjlist(detailObjlist);
			}
			if (locationManagedBean.getResCity().getRecordStatusFlg() != locationManagedBean.getCityOldVal().getPmCity().getRecordStatusFlg()) {
				auPMDTOObj.setLocOldStatusVal(true);
				detailObjStatus.setOldValue(locationManagedBean.getCityOldVal().getPmCity().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				detailObjStatus.setNewValue(locationManagedBean.getResCity().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(detailObjStatus);
				auPMDTOObj.setDetailObjlist(detailObjlist);
			}
		} else {
			/** INSERT NEW transaction ***/
			detailObjName.setNewValue(locationManagedBean.getResCity().getCityName());
			detailObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObjStatus.setNewValue(locationManagedBean.getResCity().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObjlist.add(detailObjName);
			detailObjlist.add(detailObjStatus);
			auPMDTOObj.setDetailObjlist(detailObjlist);
		}
		return auPMDTOObj;
	}
}
