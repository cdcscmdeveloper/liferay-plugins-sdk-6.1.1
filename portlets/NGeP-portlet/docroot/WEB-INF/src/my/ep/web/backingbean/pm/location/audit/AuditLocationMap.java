package my.ep.web.backingbean.pm.location.audit;

import java.util.ArrayList;

import my.ep.constant.audit.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.audittrail.AUPMDetailSPDTO;
import my.ep.dto.pm.common.audittrail.AUPMMasterSPDTO;
import my.ep.dto.pm.common.audittrail.AuPMLocationDTO;
import my.ep.dto.pm.common.audittrail.AuditPMBaseDTO;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.location.LocationManagedBean;

public class AuditLocationMap extends AuditSaveBackingBeanTemplate {

	private static final long serialVersionUID = 1L;
	AUPMDetailSPDTO dtlObjName = new AUPMDetailSPDTO();
	AUPMDetailSPDTO dtlObjDV = new AUPMDetailSPDTO();
	AUPMDetailSPDTO dtlObjStatus = new AUPMDetailSPDTO();
	ArrayList<AUPMDetailSPDTO> detailObjlist = new ArrayList<AUPMDetailSPDTO>();
	
	public ArrayList<AUPMMasterSPDTO> masterInsert(AuditPMBaseDTO au, Long userId,BaseManagedBean manageBean,String roleCode) {
		LocationManagedBean locationManagedBean = (LocationManagedBean)manageBean; 
		AuPMLocationDTO auPMObj =(AuPMLocationDTO) au;
		ArrayList<AUPMMasterSPDTO> masterObjlist = new ArrayList<AUPMMasterSPDTO>();
		AUPMMasterSPDTO msterObj = new AUPMMasterSPDTO();
		msterObj.setRoleCode(roleCode);

		msterObj.setDoctype(auPMObj.getST() == true ? AuditConst.PM_AUD_ST_DOC_TYPE
				: auPMObj.getDV() == true ? AuditConst.PM_AUD_DIV_DOC_TYPE
						: auPMObj.getDS() == true ? AuditConst.PM_AUD_DS_DOC_TYPE
								: auPMObj.getCT() == true ? AuditConst.PM_AUD_CT_DOC_TYPE : null);
		msterObj.setPmParameterLvlId(auPMObj.getST() == true ? AuditConst.PM_AUD_ST_PMPARAMETERLVLID : auPMObj
				.getDV() == true ? AuditConst.PM_AUD_DIV_PMPARAMETERLVLID
				: auPMObj.getDS() == true ? AuditConst.PM_AUD_DS_PMPARAMETERLVLID
						: auPMObj.getCT() == true ? AuditConst.PM_AUD_CT_PMPARAMETERLVLID : null);
		msterObj.setDocNo(auPMObj.getST() == true ? locationManagedBean.getState().getStateCode() : auPMObj
				.getDV() == true ? locationManagedBean.getDivision().getDivisionCode()
				: auPMObj.getDS() == true ? locationManagedBean.getDistrict().getDistrictCode()
						: auPMObj.getCT() == true ? locationManagedBean.getCity().getCityCode() : null);
		masterObjlist.add(msterObj);

		return masterObjlist;
	}
	
	public AuditPMBaseDTO detailsInsert(BaseManagedBean managedBean, Long userId) {
		LocationManagedBean locationManagedBean = (LocationManagedBean)managedBean ;
		AuPMLocationDTO auPMObj = new AuPMLocationDTO();
		
		if (ParameterConst.LOCATION_LEVEL_STATE.equals(locationManagedBean.getSelectedType())) {
			auPMObj.setST(true);
		}
		if (ParameterConst.LOCATION_LEVEL_DIVISION.equals(locationManagedBean.getSelectedType())) {
			auPMObj.setDV(true);
		}
		if (ParameterConst.LOCATION_LEVEL_DISTRICT.equals(locationManagedBean.getSelectedType())) {
			auPMObj.setDS(true);
		}
		if (ParameterConst.LOCATION_LEVEL_CITY.equals(locationManagedBean.getSelectedType())) {
			auPMObj.setCT(true);
		}			
		if (auPMObj.getDV()) {			
			auPMObj = insertDivisionVal(auPMObj,locationManagedBean);
		}
		if (auPMObj.getST()) {
			auPMObj = insertStateVal(auPMObj,locationManagedBean ,userId);
		}
		if (auPMObj.getDS()) {
			auPMObj = insertDistrictVal(auPMObj,locationManagedBean);			
		}
		if (auPMObj.getCT()) {
			auPMObj = insertCityVal(auPMObj,locationManagedBean);
		}
		return auPMObj;
	}
	
	private AuPMLocationDTO insertDivisionVal(AuPMLocationDTO auPMObj,LocationManagedBean locationManagedBean){
		dtlObjName.setDocId(locationManagedBean.getResDivision().getDivisionId().intValue());
		dtlObjStatus.setDocId(locationManagedBean.getResDivision().getDivisionId().intValue());
		dtlObjStatus.setFieldLabelid(AuditConst.PM_AUD_DIV_DVSTATUS_FieldLabelid);
		dtlObjName.setFieldLabelid(AuditConst.PM_AUD_DIV_DVNAME_FieldLabelid);
		auPMObj.setLocCategory("DV");
		if (null != locationManagedBean.getDivision().getDivisionId()&& locationManagedBean.getDivisionOldVal().getPmDivision().getDivisionId() != null) {
			auPMObj.setLocOldDtlVal(true);
			if (!locationManagedBean.getResDivision().getDivisionName().equalsIgnoreCase(locationManagedBean.getDivisionOldVal().getPmDivision().getDivisionName())) {
				auPMObj.setLocOldNameVal(true);			
				dtlObjName.setOldValue(locationManagedBean.getDivisionOldVal().getPmDivision().getDivisionName());
				dtlObjName.setNewValue(locationManagedBean.getResDivision().getDivisionName());
				dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(dtlObjName);
				auPMObj.setDetailObjlist(detailObjlist);
			}
			if (locationManagedBean.getResDivision().getRecordStatusFlg() != locationManagedBean.getDivisionOldVal().getPmDivision().getRecordStatusFlg()) {
				auPMObj.setLocOldStatusVal(true);
				dtlObjStatus.setOldValue(locationManagedBean.getDivisionOldVal().getPmDivision().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus.setNewValue(locationManagedBean.getResDivision().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(dtlObjStatus);
				auPMObj.setDetailObjlist(detailObjlist);
			}
		}else{
			/** INSERT NEW transaction ***/
			dtlObjName.setNewValue(locationManagedBean.getResDivision().getDivisionName());
			dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);		
			
			dtlObjStatus.setNewValue(locationManagedBean.getResDivision().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
			dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjName);
			detailObjlist.add(dtlObjStatus);
			auPMObj.setDetailObjlist(detailObjlist);
		}
		return auPMObj;
	}

	private AuPMLocationDTO insertStateVal(AuPMLocationDTO auPMObj,LocationManagedBean locationManagedBean,Long userId){
		auPMObj.setLocCategory("ST");
		dtlObjName.setDocId(locationManagedBean.getResState().getStateId().intValue());
		dtlObjStatus.setDocId(locationManagedBean.getResState().getStateId().intValue());
		dtlObjDV.setDocId(locationManagedBean.getResState().getStateId().intValue());		
		if (null != locationManagedBean.getState().getStateId() && locationManagedBean.getStateOldVal().getPmState().getStateId() != null) {
			auPMObj.setLocOldDtlVal(true);
			if (!locationManagedBean.getState().getStateName().equalsIgnoreCase(locationManagedBean.getStateOldVal().getPmState().getStateName())) {
				auPMObj.setLocOldNameVal(true);
				dtlObjName.setFieldLabelid(AuditConst.PM_AUD_ST_STNAME_FieldLabelid);
				dtlObjName.setOldValue(locationManagedBean.getStateOldVal().getPmState().getStateName());
				dtlObjName.setNewValue(locationManagedBean.getResState().getStateName());
				dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(dtlObjName);
				auPMObj.setDetailObjlist(detailObjlist);
			}
			if (locationManagedBean.getResState().getAdoptDivision() != locationManagedBean.getStateOldVal().getPmState().getAdoptDivision()) {
				auPMObj.setLocOldAdoptDivVal(true);
				dtlObjDV.setFieldLabelid(AuditConst.PM_AUD_ST_STDIV_FieldLabelid);
				dtlObjDV.setOldValue(locationManagedBean.getStateOldVal().getPmState().getAdoptDivision() == true ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
				dtlObjDV.setNewValue(locationManagedBean.getResState().getAdoptDivision() == true ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
				dtlObjDV.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);				
				detailObjlist.add(dtlObjDV);
				auPMObj.setDetailObjlist(detailObjlist);
			}
			if (locationManagedBean.getResState().getRecordStatusFlg() != locationManagedBean.getStateOldVal().getPmState().getRecordStatusFlg()) {
				auPMObj.setLocOldStatusVal(true);
				dtlObjStatus.setFieldLabelid(AuditConst.PM_AUD_ST_STSTATUS_FieldLabelid);
				dtlObjStatus.setOldValue(locationManagedBean.getStateOldVal().getPmState().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus.setNewValue(locationManagedBean.getResState().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(dtlObjStatus);
				auPMObj.setDetailObjlist(detailObjlist);
			}
		} else {
			dtlObjName.setFieldLabelid(AuditConst.PM_AUD_ST_STNAME_FieldLabelid);
			dtlObjName.setNewValue(locationManagedBean.getResState().getStateName());
			dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjName);

			dtlObjDV.setFieldLabelid(AuditConst.PM_AUD_ST_STDIV_FieldLabelid);
			dtlObjDV.setNewValue(locationManagedBean.getResState().getAdoptDivision() == true ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			dtlObjDV.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjDV);

			dtlObjStatus.setFieldLabelid(AuditConst.PM_AUD_ST_STSTATUS_FieldLabelid);
			dtlObjStatus.setNewValue(locationManagedBean.getResState().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
			dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjStatus);
			
			auPMObj.setDetailObjlist(detailObjlist);
		}
		return auPMObj;
	}
	
	private AuPMLocationDTO insertDistrictVal(AuPMLocationDTO auPMObj,LocationManagedBean locationManagedBean){
		auPMObj.setLocCategory("DS");
		dtlObjName.setFieldLabelid(AuditConst.PM_AUD_ST_DSNAME_FieldLabelid);
		dtlObjName.setDocId(locationManagedBean.getResDistrict().getDistrictId().intValue());
		dtlObjStatus.setFieldLabelid(AuditConst.PM_AUD_ST_DSSTATUS_FieldLabelid); // District
		dtlObjStatus.setDocId(locationManagedBean.getResDistrict().getDistrictId().intValue());	
		if (null != locationManagedBean.getDistrict().getDistrictId() && locationManagedBean.getDistrictOldVal().getPmDistrict().getDistrictId() != null) {
			/** UPDATE transaction ***/
			if (!locationManagedBean.getResDistrict().getDistrictName().equalsIgnoreCase(locationManagedBean.getDistrictOldVal().getPmDistrict().getDistrictName())) {
				dtlObjName.setOldValue(locationManagedBean.getDistrictOldVal().getPmDistrict().getDistrictName());
				dtlObjName.setNewValue(locationManagedBean.getResDistrict().getDistrictName());
				dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(dtlObjName);
				auPMObj.setDetailObjlist(detailObjlist);
			}
			if (locationManagedBean.getResDistrict().getRecordStatusFlg() != locationManagedBean.getDistrictOldVal().getPmDistrict().getRecordStatusFlg()) {
				dtlObjStatus.setOldValue(locationManagedBean.getDistrictOldVal().getPmDistrict().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus.setNewValue(locationManagedBean.getResDistrict().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(dtlObjStatus);
				auPMObj.setDetailObjlist(detailObjlist);
			}
		} else {
			/** INSERT NEW transaction ***/
			dtlObjName.setNewValue(locationManagedBean.getResDistrict().getDistrictName());
			dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);

			dtlObjStatus.setNewValue(locationManagedBean.getResDistrict().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
			dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObjlist.add(dtlObjName);
			detailObjlist.add(dtlObjStatus);
			auPMObj.setDetailObjlist(detailObjlist);
		}
		return auPMObj;
	}
	
	private AuPMLocationDTO insertCityVal(AuPMLocationDTO auPMObj,LocationManagedBean locationManagedBean){
		auPMObj.setLocCategory("CT");
		dtlObjName.setDocId(locationManagedBean.getResCity().getCityId().intValue());
		dtlObjName.setFieldLabelid(AuditConst.PM_AUD_CT_CTNAME_FieldLabelid);
		dtlObjStatus.setFieldLabelid(AuditConst.PM_AUD_CT_CTSTATUS_FieldLabelid); // City
		dtlObjStatus.setDocId(locationManagedBean.getResCity().getCityId().intValue());						
		if (null != locationManagedBean.getCity().getCityId() && locationManagedBean.getCityOldVal().getPmCity().getCityId() != null) {
			auPMObj.setLocOldDtlVal(true);
			/** UPDATE transaction ***/
			if (!locationManagedBean.getResCity().getCityName().equalsIgnoreCase(locationManagedBean.getCityOldVal().getPmCity().getCityName())) {
				auPMObj.setLocOldNameVal(true);	
				dtlObjName.setOldValue(locationManagedBean.getCityOldVal().getPmCity().getCityName());
				dtlObjName.setNewValue(locationManagedBean.getResCity().getCityName());
				dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(dtlObjName);
				auPMObj.setDetailObjlist(detailObjlist);
			}
			if (locationManagedBean.getResCity().getRecordStatusFlg() != locationManagedBean.getCityOldVal().getPmCity().getRecordStatusFlg()) {
				auPMObj.setLocOldStatusVal(true);
				dtlObjStatus.setOldValue(locationManagedBean.getCityOldVal().getPmCity().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus.setNewValue(locationManagedBean.getResCity().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(dtlObjStatus);
				auPMObj.setDetailObjlist(detailObjlist);
			}
		} else {
			/** INSERT NEW transaction ***/
			dtlObjName.setNewValue(locationManagedBean.getResCity().getCityName());
			dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);

			dtlObjStatus.setNewValue(locationManagedBean.getResCity().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
			dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObjlist.add(dtlObjName);
			detailObjlist.add(dtlObjStatus);
			auPMObj.setDetailObjlist(detailObjlist);
		}
		return auPMObj;
	}
}
