package my.ep.web.backingbean.pm.audit.location;

import java.util.ArrayList;

import my.ep.constant.audit.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.audit.location.AUPMLocationDetailSPDTO;
import my.ep.dto.pm.common.audit.location.AUPMLocationMasterSPDTO;
import my.ep.dto.pm.common.audit.location.AuPMLocationDTO;
import my.ep.dto.pm.common.audit.location.AuditPMBaseDTO;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.location.LocationManagedBean;

public class AuditLocationBackingBean extends AuditPMLocationBackingBeanTemplate {

	private static final long serialVersionUID = 1L;
	AUPMLocationDetailSPDTO dtlObjName = new AUPMLocationDetailSPDTO();
	AUPMLocationDetailSPDTO dtlObjDV = new AUPMLocationDetailSPDTO();
	AUPMLocationDetailSPDTO dtlObjStatus = new AUPMLocationDetailSPDTO();
	AUPMLocationDetailSPDTO dtlObjStateCode = new AUPMLocationDetailSPDTO();
	AUPMLocationDetailSPDTO dtlObjCountry = new AUPMLocationDetailSPDTO();
	AUPMLocationDetailSPDTO dtlObjStateId = new AUPMLocationDetailSPDTO();
	AUPMLocationDetailSPDTO dtlObjDivisionId = new AUPMLocationDetailSPDTO();
	AUPMLocationDetailSPDTO dtlObjDistrictId = new AUPMLocationDetailSPDTO();
	AUPMLocationDetailSPDTO dtlObjCode = new AUPMLocationDetailSPDTO();
	ArrayList<AUPMLocationDetailSPDTO> detailObjlist = new ArrayList<AUPMLocationDetailSPDTO>();
	
	@SuppressWarnings("static-access")
	public ArrayList<AUPMLocationMasterSPDTO> masterInsert(AuditPMBaseDTO au, Long userId,BaseManagedBean manageBean,String roleCode) {
		LocationManagedBean locationManagedBean = (LocationManagedBean)manageBean; 
		AuPMLocationDTO auPMObj =(AuPMLocationDTO) au;
		ArrayList<AUPMLocationMasterSPDTO> masterObjlist = new ArrayList<AUPMLocationMasterSPDTO>();
		AUPMLocationMasterSPDTO msterObj = new AUPMLocationMasterSPDTO();
		msterObj.setRoleCode(roleCode);
		
		locationManagedBean.setPmMasterLocationSreialId(msterObj.getSerialversionuid());
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
		msterObj.setPkId(auPMObj.getST() == true ? locationManagedBean.getResState().getStateId() : auPMObj
				.getDV() == true ? locationManagedBean.getResDivision().getDivisionId()
				: auPMObj.getDS() == true ? locationManagedBean.getResDistrict().getDistrictId()
						: auPMObj.getCT() == true ? locationManagedBean.getResCity().getCityId() : null);
		msterObj.setColumnName(auPMObj.getST() == true ? AuditConst.PM_AUD_ST_SOURCECOLUMN_ST : auPMObj
				.getDV() == true ? AuditConst.PM_AUD_DV_SOURCECOLUMN_DVNAME
				: auPMObj.getDS() == true ? AuditConst.PM_AUD_DS_SOURCECOLUMN_DSNAME
						: auPMObj.getCT() == true ? AuditConst.PM_AUD_CT_SOURCECOLUMN_CTNAME : null);		
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
		dtlObjStatus.setPmFieldLabelid(AuditConst.PM_AUD_DIV_DVSTATUS_FIELDLABELID);
		dtlObjName.setPmFieldLabelid(AuditConst.PM_AUD_DIV_DVNAME_FIELDLABELID);
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
			dtlObjCountry.setDocId(locationManagedBean.getResDivision().getDivisionId().intValue());
			dtlObjStateId.setDocId(locationManagedBean.getResDivision().getDivisionId().intValue());
			dtlObjCode.setDocId(locationManagedBean.getResDivision().getDivisionId().intValue());
			
			dtlObjCountry.setPmFieldLabelid(AuditConst.PM_AUD_DIV_DVCOUNTRY_FIELDLABELID);
			dtlObjCountry.setNewValueId(locationManagedBean.getDivision().getPmState().getPmCountry().getCountryId().intValue());
			dtlObjCountry.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjCountry);
			
			dtlObjStateId.setPmFieldLabelid(AuditConst.PM_AUD_DIV_DVSTATECODE_FIELDLABELID);
			dtlObjStateId.setNewValueId(locationManagedBean.getDivision().getPmState().getStateId().intValue());
			dtlObjStateId.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjStateId);			
			
			dtlObjCode.setPmFieldLabelid(AuditConst.PM_AUD_DIV_DVCODE_FIELDLABELID);
			dtlObjCode.setNewValue(locationManagedBean.getResDivision().getDivisionCode());
			dtlObjCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjCode);
			
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
				dtlObjName.setPmFieldLabelid(AuditConst.PM_AUD_ST_STNAME_FIELDLABELID);
				dtlObjName.setOldValue(locationManagedBean.getStateOldVal().getPmState().getStateName());
				dtlObjName.setNewValue(locationManagedBean.getResState().getStateName());
				dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(dtlObjName);
				auPMObj.setDetailObjlist(detailObjlist);
			}
			if (locationManagedBean.getResState().getAdoptDivision() != locationManagedBean.getStateOldVal().getPmState().getAdoptDivision()) {
				auPMObj.setLocOldAdoptDivVal(true);
				dtlObjDV.setPmFieldLabelid(AuditConst.PM_AUD_ST_STDIV_FIELDLABELID);
				dtlObjDV.setOldValue(locationManagedBean.getStateOldVal().getPmState().getAdoptDivision() == true ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
				dtlObjDV.setNewValue(locationManagedBean.getResState().getAdoptDivision() == true ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
				dtlObjDV.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);				
				detailObjlist.add(dtlObjDV);
				auPMObj.setDetailObjlist(detailObjlist);
			}
			if (locationManagedBean.getResState().getRecordStatusFlg() != locationManagedBean.getStateOldVal().getPmState().getRecordStatusFlg()) {
				auPMObj.setLocOldStatusVal(true);
				dtlObjStatus.setPmFieldLabelid(AuditConst.PM_AUD_ST_STSTATUS_FIELDLABELID);
				dtlObjStatus.setOldValue(locationManagedBean.getStateOldVal().getPmState().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus.setNewValue(locationManagedBean.getResState().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				detailObjlist.add(dtlObjStatus);
				auPMObj.setDetailObjlist(detailObjlist);
			}
		} else {
			dtlObjCountry.setDocId(locationManagedBean.getResState().getStateId().intValue());
			dtlObjStateCode.setDocId(locationManagedBean.getResState().getStateId().intValue());
			dtlObjDV.setDocId(locationManagedBean.getResState().getStateId().intValue());
			
			dtlObjStateCode.setPmFieldLabelid(AuditConst.PM_AUD_ST_STSTATECODE_FIELDLABELID);
			dtlObjStateCode.setNewValue(locationManagedBean.getResState().getStateCode());
			dtlObjStateCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjStateCode);
			
			dtlObjCountry.setPmFieldLabelid(AuditConst.PM_AUD_ST_STCOUNTRY_FIELDLABELID);
			dtlObjCountry.setNewValueId(locationManagedBean.getState().getPmCountry().getCountryId().intValue());
			dtlObjCountry.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjCountry);
			
			dtlObjName.setPmFieldLabelid(AuditConst.PM_AUD_ST_STNAME_FIELDLABELID);
			dtlObjName.setNewValue(locationManagedBean.getResState().getStateName());
			dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjName);

			dtlObjDV.setPmFieldLabelid(AuditConst.PM_AUD_ST_STDIV_FIELDLABELID);
			dtlObjDV.setNewValue(locationManagedBean.getResState().getAdoptDivision() == true ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			dtlObjDV.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjDV);

			dtlObjStatus.setPmFieldLabelid(AuditConst.PM_AUD_ST_STSTATUS_FIELDLABELID);
			dtlObjStatus.setNewValue(locationManagedBean.getResState().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
			dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjStatus);
						
			auPMObj.setDetailObjlist(detailObjlist);
		}
		return auPMObj;
	}
	
	private AuPMLocationDTO insertDistrictVal(AuPMLocationDTO auPMObj,LocationManagedBean locationManagedBean){
		auPMObj.setLocCategory("DS");
		dtlObjName.setPmFieldLabelid(AuditConst.PM_AUD_ST_DSNAME_FIELDLABELID);
		dtlObjName.setDocId(locationManagedBean.getResDistrict().getDistrictId().intValue());
		dtlObjStatus.setPmFieldLabelid(AuditConst.PM_AUD_ST_DSSTATUS_FIELDLABELID); // District
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
			dtlObjCountry.setDocId(locationManagedBean.getResDistrict().getDistrictId().intValue());
			dtlObjStateId.setDocId(locationManagedBean.getResDistrict().getDistrictId().intValue());
			dtlObjCode.setDocId(locationManagedBean.getResDistrict().getDistrictId().intValue());
			/** INSERT NEW transaction ***/
			dtlObjCountry.setPmFieldLabelid(AuditConst.PM_AUD_DS_DSCOUNTRY_FIELDLABELID);
			dtlObjCountry.setNewValueId(locationManagedBean.getDistrict().getPmState().getPmCountry().getCountryId().intValue());
			dtlObjCountry.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjCountry);
			
			dtlObjStateId.setPmFieldLabelid(AuditConst.PM_AUD_DS_DSSTATEID_FIELDLABELID);
			dtlObjStateId.setNewValueId(locationManagedBean.getDistrict().getPmState().getStateId().intValue());
			dtlObjStateId.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjStateId);
			
			if(locationManagedBean.getResDistrict().getPmDivision() != null){
			dtlObjDivisionId.setDocId(locationManagedBean.getResDistrict().getDistrictId().intValue());
			dtlObjDivisionId.setPmFieldLabelid(AuditConst.PM_AUD_DS_DSDIVISIONID_FIELDLABELID);
			dtlObjDivisionId.setNewValueId(locationManagedBean.getDistrict().getPmDivision().getDivisionId().intValue());
			dtlObjDivisionId.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjDivisionId);		
			}	
			dtlObjCode.setPmFieldLabelid(AuditConst.PM_AUD_DS_DSCODE_FIELDLABELID);
			dtlObjCode.setNewValue(locationManagedBean.getResDistrict().getDistrictCode());
			dtlObjCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjCode);
			
			dtlObjName.setNewValue(locationManagedBean.getResDistrict().getDistrictName());
			dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjName);

			dtlObjStatus.setNewValue(locationManagedBean.getResDistrict().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
			dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjStatus);

			auPMObj.setDetailObjlist(detailObjlist);
		}
		return auPMObj;
	}
	
	private AuPMLocationDTO insertCityVal(AuPMLocationDTO auPMObj,LocationManagedBean locationManagedBean){
		auPMObj.setLocCategory("CT");		
		dtlObjName.setDocId(locationManagedBean.getResCity().getCityId().intValue());
		dtlObjName.setPmFieldLabelid(AuditConst.PM_AUD_CT_CTNAME_FIELDLABELID);
		dtlObjStatus.setPmFieldLabelid(AuditConst.PM_AUD_CT_CTSTATUS_FIELDLABELID); // City
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
			dtlObjCountry.setDocId(locationManagedBean.getResCity().getCityId().intValue());
			dtlObjStateId.setDocId(locationManagedBean.getResCity().getCityId().intValue());
			dtlObjDistrictId.setDocId(locationManagedBean.getResCity().getCityId().intValue());
			dtlObjCode.setDocId(locationManagedBean.getResCity().getCityId().intValue());
			
			dtlObjCountry.setPmFieldLabelid(AuditConst.PM_AUD_CT_CTCOUNTRY_FIELDLABELID);
			dtlObjCountry.setNewValueId(locationManagedBean.getState().getPmCountry().getCountryId().intValue());
			dtlObjCountry.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjCountry);
			
			dtlObjStateId.setPmFieldLabelid(AuditConst.PM_AUD_CT_CTSTATECODE_FIELDLABELID);
			dtlObjStateId.setNewValueId(locationManagedBean.getDistrict().getPmState().getStateId().intValue());
			dtlObjStateId.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjStateId);
			
			dtlObjDistrictId.setPmFieldLabelid(AuditConst.PM_AUD_CT_CTDISTRICTID_FIELDLABELID);
			dtlObjDistrictId.setNewValueId(locationManagedBean.getCity().getPmDistrict().getDistrictId().intValue());
			dtlObjDistrictId.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjDistrictId);
			
			dtlObjCode.setPmFieldLabelid(AuditConst.PM_AUD_CT_CTCODE_FIELDLABELID);
			dtlObjCode.setNewValue(locationManagedBean.getCity().getCityCode());
			dtlObjCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			detailObjlist.add(dtlObjCode);
			
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
