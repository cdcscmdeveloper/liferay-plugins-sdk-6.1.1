package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;

import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.location.LocationManagedBean;

/**
 * AuditPMBackingBean #28895-35509
 * 
 * @author normuliana
 * 
 */
public class AuditPMLocationBackingBean extends AuditPMBackingBeanTemplate {
	private static final long serialVersionUID = 1L;
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	String roleCode;
	String docNo;
	BigInteger docId;
	String docType;
	BigInteger paramLvlId;
	String lvlCode3;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleLocation();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate#insert(my.ep
	 * .web.managedbean.BaseManagedBean, java.lang.String)
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		LocationManagedBean managedBeans = (LocationManagedBean) manageBean;
		this.roleCode = roleCode;
		if (ParameterConst.LOCATION_LEVEL_STATE.equals(managedBeans.getSelectedType())) {
			docType = managedBeans.getState().getStateName();// AuditConst.PM_AUD_ST_DOC_TYPE;
			paramLvlId = AuditConst.PM_AUD_ST_PMPARAMETERLVLID;
			docNo = managedBeans.getState().getStateCode();
			lvlCode3 = AuditConst.PM_LVL_CODE_3_ST;
			docId = BigInteger.valueOf(managedBeans.getResState().getStateId());
			insertStateVal(managedBeans);
		}
		if (ParameterConst.LOCATION_LEVEL_DIVISION.equals(managedBeans.getSelectedType())) {
			docType = managedBeans.getDivision().getDivisionName();// AuditConst.PM_AUD_DIV_DOC_TYPE;
			paramLvlId = AuditConst.PM_AUD_DIV_PMPARAMETERLVLID;
			docNo = managedBeans.getDivision().getDivisionCode();
			lvlCode3 = AuditConst.PM_LVL_CODE_3_DIV;
			docId = BigInteger.valueOf(managedBeans.getResDivision().getDivisionId());
			insertDivisionVal(managedBeans);
		}
		if (ParameterConst.LOCATION_LEVEL_DISTRICT.equals(managedBeans.getSelectedType())) {
			docType = managedBeans.getResDistrict().getDistrictName();// AuditConst.PM_AUD_DS_DOC_TYPE;
			paramLvlId = AuditConst.PM_AUD_DS_PMPARAMETERLVLID;
			docNo = managedBeans.getResDistrict().getDistrictCode();
			lvlCode3 = AuditConst.PM_LVL_CODE_3_DST;
			docId = BigInteger.valueOf(managedBeans.getResDistrict().getDistrictId());
			insertDistrictVal(managedBeans);
		}
		if (ParameterConst.LOCATION_LEVEL_CITY.equals(managedBeans.getSelectedType())) {
			docType = managedBeans.getCity().getCityName();// AuditConst.PM_AUD_CT_DOC_TYPE;
			paramLvlId = AuditConst.PM_AUD_CT_PMPARAMETERLVLID;
			docNo = managedBeans.getCity().getCityCode();
			lvlCode3 = AuditConst.PM_LVL_CODE_3_CT;
			docId = BigInteger.valueOf(managedBeans.getResCity().getCityId());
			insertCityVal(managedBeans);
		}
		auditPMReqDTO.setObjList(objList);
		return auditPMReqDTO;
	}

	private void insertStateVal(LocationManagedBean managedBean) {
		if (null != managedBean.getState().getStateId() && managedBean.getStateOldVal().getPmState().getStateId() != null) {
			if (!managedBean.getState().getStateName().equalsIgnoreCase(managedBean.getStateOldVal().getPmState().getStateName())) {
				detailObj(AuditConst.PM_AUD_ST_STNAME_FIELDLABELID, managedBean.getStateOldVal().getPmState().getStateName(),
						managedBean.getResState().getStateName(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
			if (managedBean.getResState().getAdoptDivision() != managedBean.getStateOldVal().getPmState().getAdoptDivision()) {
				detailObj(AuditConst.PM_AUD_ST_STDIV_FIELDLABELID, oldValue(managedBean), newValue(managedBean),
						AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
			if (managedBean.getResState().getRecordStatusFlg() != managedBean.getStateOldVal().getPmState().getRecordStatusFlg()) {
				detailObj(AuditConst.PM_AUD_ST_STSTATUS_FIELDLABELID, managedBean.getStateOldVal().getPmState()
						.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE, managedBean
						.getResState().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
						AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
		} else {
			detailObj(AuditConst.PM_AUD_ST_STCOUNTRY_FIELDLABELID, null, managedBean.getResCountry().getCountryName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_ST_STSTATECODE_FIELDLABELID, null, managedBean.getResState().getStateCode(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_ST_STNAME_FIELDLABELID, null, managedBean.getResState().getStateName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_ST_STDIV_FIELDLABELID, null, newValue(managedBean), AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_ST_STSTATUS_FIELDLABELID, null,
					managedBean.getResState().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					AuditConst.PM_AUD_ACTION_TYPE_INS);
		}
	}

	private void insertDivisionVal(LocationManagedBean managedBean) {
		if (null != managedBean.getDivision().getDivisionId()
				&& managedBean.getDivisionOldVal().getPmDivision().getDivisionId() != null) {
			if (!managedBean.getResDivision().getDivisionName()
					.equalsIgnoreCase(managedBean.getDivisionOldVal().getPmDivision().getDivisionName())) {
				detailObj(AuditConst.PM_AUD_DIV_DVNAME_FIELDLABELID, managedBean.getDivisionOldVal().getPmDivision()
						.getDivisionName(), managedBean.getResDivision().getDivisionName(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
			if (managedBean.getResDivision().getRecordStatusFlg() != managedBean.getDivisionOldVal().getPmDivision()
					.getRecordStatusFlg()) {
				detailObj(AuditConst.PM_AUD_DIV_DVSTATUS_FIELDLABELID, managedBean.getDivisionOldVal().getPmDivision()
						.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE, managedBean
						.getResDivision().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
						AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
		} else {
			/** INSERT NEW transaction ***/
			detailObj(AuditConst.PM_AUD_DIV_DVCOUNTRY_FIELDLABELID, null, managedBean.getResCountry().getCountryName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_DIV_DVSTATECODE_FIELDLABELID, null, managedBean.getState().getStateName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_DIV_DVCODE_FIELDLABELID, null, managedBean.getResDivision().getDivisionCode(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_DIV_DVNAME_FIELDLABELID, null, managedBean.getResDivision().getDivisionName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_DIV_DVSTATUS_FIELDLABELID, null,
					managedBean.getResDivision().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE, AuditConst.PM_AUD_ACTION_TYPE_INS);
		}
	}

	private void insertDistrictVal(LocationManagedBean managedBean) {
		if (null != managedBean.getDistrict().getDistrictId()
				&& managedBean.getDistrictOldVal().getPmDistrict().getDistrictId() != null) {
			/** UPDATE transaction ***/
			if (!managedBean.getResDistrict().getDistrictName()
					.equalsIgnoreCase(managedBean.getDistrictOldVal().getPmDistrict().getDistrictName())) {
				detailObj(AuditConst.PM_AUD_ST_DSNAME_FIELDLABELID, managedBean.getDistrictOldVal().getPmDistrict()
						.getDistrictName(), managedBean.getResDistrict().getDistrictName(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
			if (managedBean.getResDistrict().getRecordStatusFlg() != managedBean.getDistrictOldVal().getPmDistrict()
					.getRecordStatusFlg()) {
				detailObj(AuditConst.PM_AUD_ST_DSSTATUS_FIELDLABELID, managedBean.getDistrictOldVal().getPmDistrict()
						.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE, managedBean
						.getResDistrict().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
						AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
		} else {
			/** INSERT NEW transaction ***/
			detailObj(AuditConst.PM_AUD_DS_DSCOUNTRY_FIELDLABELID, null, managedBean.getResCountry().getCountryName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_DS_DSSTATEID_FIELDLABELID, null,
					managedBean.getResDistrict().getPmState().getStateName(), AuditConst.PM_AUD_ACTION_TYPE_INS);

			if (managedBean.getDistrict().getPmDivision() != null) {
				detailObj(AuditConst.PM_AUD_DS_DSDIVISIONID_FIELDLABELID, null, managedBean.getResDistrict().getPmDivision()
						.getDivisionName(), AuditConst.PM_AUD_ACTION_TYPE_INS);
			}

			detailObj(AuditConst.PM_AUD_DS_DSCODE_FIELDLABELID, null, managedBean.getResDistrict().getDistrictCode(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_ST_DSNAME_FIELDLABELID, null, managedBean.getResDistrict().getDistrictName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_ST_DSSTATUS_FIELDLABELID, null,
					managedBean.getResDistrict().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE, AuditConst.PM_AUD_ACTION_TYPE_INS);
		}
	}

	private void insertCityVal(LocationManagedBean managedBean) {
		if (null != managedBean.getCity().getCityId() && managedBean.getCityOldVal().getPmCity().getCityId() != null) {
			/** UPDATE transaction ***/
			if (!managedBean.getResCity().getCityName().equalsIgnoreCase(managedBean.getCityOldVal().getPmCity().getCityName())) {
				detailObj(AuditConst.PM_AUD_CT_CTNAME_FIELDLABELID, managedBean.getCityOldVal().getPmCity().getCityName(),
						managedBean.getResCity().getCityName(), AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
			if (managedBean.getResCity().getRecordStatusFlg() != managedBean.getCityOldVal().getPmCity().getRecordStatusFlg()) {
				detailObj(AuditConst.PM_AUD_CT_CTSTATUS_FIELDLABELID, managedBean.getCityOldVal().getPmCity()
						.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE, managedBean
						.getResCity().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
						AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
		} else {
			/** INSERT NEW transaction ***/
			detailObj(AuditConst.PM_AUD_CT_CTCOUNTRY_FIELDLABELID, null, managedBean.getResCountry().getCountryName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_CT_CTSTATECODE_FIELDLABELID, null, managedBean.getResState().getStateName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_CT_CTDISTRICTID_FIELDLABELID, null, managedBean.getResCity().getPmDistrict()
					.getDistrictName(), AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_CT_CTCODE_FIELDLABELID, null, managedBean.getResCity().getCityCode(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_CT_CTNAME_FIELDLABELID, null, managedBean.getResCity().getCityName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_CT_CTSTATUS_FIELDLABELID, null,
					managedBean.getResCity().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					AuditConst.PM_AUD_ACTION_TYPE_INS);
		}
	}

	private ArrayList<AuditPMSPDTO> detailObj(BigInteger fieldLabelId, String oldValue, String newValue, String actionType) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(docType);
		dtlObj.setPmParameterLvlId(paramLvlId);
		dtlObj.setDocNo(docNo);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
		dtlObj.setLvlCode3(lvlCode3);
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

	/**
	 * @return String OldValue
	 */
	private String oldValue(LocationManagedBean managedBean) {
		String oldValue;
		if (managedBean.getStateOldVal().getPmState().getAdoptDivision()) {
			oldValue = AuditConst.PM_AUD_YES;
		} else {
			oldValue = AuditConst.PM_AUD_NO;
		}
		return oldValue;
	}

	/**
	 * @return String NewValue
	 */
	private String newValue(LocationManagedBean managedBean) {
		String newValue;
		if (managedBean.getResState().getAdoptDivision()) {
			newValue = AuditConst.PM_AUD_YES;
		} else {
			newValue = AuditConst.PM_AUD_NO;
		}
		return newValue;
	}
}