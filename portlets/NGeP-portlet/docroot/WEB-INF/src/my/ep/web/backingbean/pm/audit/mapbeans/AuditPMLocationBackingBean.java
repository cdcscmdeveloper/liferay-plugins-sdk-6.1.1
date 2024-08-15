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
 * AuditPMBackingBean
 * 
 * @author normuliana
 * 
 */
public class AuditPMLocationBackingBean extends AuditPMBackingBeanTemplate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AuditPMSPDTO objName = new AuditPMSPDTO();
	AuditPMSPDTO objDV = new AuditPMSPDTO();
	AuditPMSPDTO objStatus = new AuditPMSPDTO();
	AuditPMSPDTO objStateCode = new AuditPMSPDTO();
	AuditPMSPDTO objCountry = new AuditPMSPDTO();
	AuditPMSPDTO objStateId = new AuditPMSPDTO();
	AuditPMSPDTO objDivisionId = new AuditPMSPDTO();
	AuditPMSPDTO objDistrictId = new AuditPMSPDTO();
	AuditPMSPDTO objCode = new AuditPMSPDTO();

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
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		LocationManagedBean managedBeans = (LocationManagedBean) manageBean;
		if (ParameterConst.LOCATION_LEVEL_STATE.equals(managedBeans.getSelectedType())) {
			auditPMReqDTO = insertStateVal(managedBeans, roleCode);
		}
		if (ParameterConst.LOCATION_LEVEL_DIVISION.equals(managedBeans.getSelectedType())) {
			auditPMReqDTO = insertDivisionVal(managedBeans, roleCode);
		}
		if (ParameterConst.LOCATION_LEVEL_DISTRICT.equals(managedBeans.getSelectedType())) {
			auditPMReqDTO = insertDistrictVal(managedBeans, roleCode);
		}
		if (ParameterConst.LOCATION_LEVEL_CITY.equals(managedBeans.getSelectedType())) {
			auditPMReqDTO = insertCityVal(managedBeans, roleCode);
		}
		return auditPMReqDTO;
	}

	private AuditPMReqDTO insertStateVal(LocationManagedBean managedBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();

		if (null != managedBean.getState().getStateId()
				&& managedBean.getStateOldVal().getPmState().getStateId() != null) {
			if (!managedBean.getState().getStateName()
					.equalsIgnoreCase(managedBean.getStateOldVal().getPmState().getStateName())) {
				objName.setRoleCode(roleCode);
				objName.setDoctype(AuditConst.PM_AUD_ST_DOC_TYPE);
				objName.setPmParameterLvlId(AuditConst.PM_AUD_ST_PMPARAMETERLVLID);
				objName.setDocNo(managedBean.getState().getStateCode());
				objName.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				objName.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
				objName.setLvlCode3(AuditConst.PM_LVL_CODE_3_ST);
				objName.setDocId(BigInteger.valueOf(managedBean.getResState().getStateId()));
				objName.setFieldLabelCode(AuditConst.PM_AUD_ST_STNAME_FIELDLABELID);
				objName.setOldValue(managedBean.getStateOldVal().getPmState().getStateName());
				objName.setNewValue(managedBean.getResState().getStateName());
				objName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(objName);
				auditPMReqDTO.setObjList(objList);
			}
			if (managedBean.getResState().getAdoptDivision() != managedBean.getStateOldVal().getPmState()
					.getAdoptDivision()) {
				objDV.setRoleCode(roleCode);
				objDV.setDoctype(AuditConst.PM_AUD_ST_DOC_TYPE);
				objDV.setPmParameterLvlId(AuditConst.PM_AUD_ST_PMPARAMETERLVLID);
				objDV.setDocNo(managedBean.getState().getStateCode());
				objDV.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				objDV.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
				objDV.setLvlCode3(AuditConst.PM_LVL_CODE_3_ST);
				objDV.setDocId(BigInteger.valueOf(managedBean.getResState().getStateId()));
				objDV.setFieldLabelCode(AuditConst.PM_AUD_ST_STDIV_FIELDLABELID);
				objDV.setOldValue(oldValue(managedBean));
				objDV.setNewValue(newValue(managedBean));
				objDV.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(objDV);
				auditPMReqDTO.setObjList(objList);
			}
			if (managedBean.getResState().getRecordStatusFlg() != managedBean.getStateOldVal().getPmState()
					.getRecordStatusFlg()) {
				objStatus.setRoleCode(roleCode);
				objStatus.setDoctype(AuditConst.PM_AUD_ST_DOC_TYPE);
				objStatus.setPmParameterLvlId(AuditConst.PM_AUD_ST_PMPARAMETERLVLID);
				objStatus.setDocNo(managedBean.getState().getStateCode());
				objStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				objStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
				objStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_ST);
				objStatus.setDocId(BigInteger.valueOf(managedBean.getResState().getStateId()));
				objStatus.setFieldLabelCode(AuditConst.PM_AUD_ST_STSTATUS_FIELDLABELID);
				objStatus
						.setOldValue(managedBean.getStateOldVal().getPmState().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				objStatus.setNewValue(managedBean.getResState().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				objStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(objStatus);
				auditPMReqDTO.setObjList(objList);
			}
		} else {
			objStateCode.setRoleCode(roleCode);
			objStateCode.setDoctype(AuditConst.PM_AUD_ST_DOC_TYPE);
			objStateCode.setPmParameterLvlId(AuditConst.PM_AUD_ST_PMPARAMETERLVLID);
			objStateCode.setDocNo(managedBean.getState().getStateCode());
			objStateCode.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objStateCode.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objStateCode.setLvlCode3(AuditConst.PM_LVL_CODE_3_ST);
			objStateCode.setDocId(BigInteger.valueOf(managedBean.getResState().getStateId()));
			objStateCode.setFieldLabelCode(AuditConst.PM_AUD_ST_STSTATECODE_FIELDLABELID);
			objStateCode.setNewValue(managedBean.getResState().getStateCode());
			objStateCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objStateCode);

			objCountry.setRoleCode(roleCode);
			objCountry.setDoctype(AuditConst.PM_AUD_ST_DOC_TYPE);
			objCountry.setPmParameterLvlId(AuditConst.PM_AUD_ST_PMPARAMETERLVLID);
			objCountry.setDocNo(managedBean.getState().getStateCode());
			objCountry.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objCountry.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objCountry.setLvlCode3(AuditConst.PM_LVL_CODE_3_ST);
			objCountry.setDocId(BigInteger.valueOf(managedBean.getResState().getStateId()));
			objCountry.setFieldLabelCode(AuditConst.PM_AUD_ST_STCOUNTRY_FIELDLABELID);
			objCountry.setNewValueId(BigInteger.valueOf(managedBean.getState().getPmCountry().getCountryId()));
			objCountry.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objCountry);

			objName.setRoleCode(roleCode);
			objName.setDoctype(AuditConst.PM_AUD_ST_DOC_TYPE);
			objName.setPmParameterLvlId(AuditConst.PM_AUD_ST_PMPARAMETERLVLID);
			objName.setDocNo(managedBean.getState().getStateCode());
			objName.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objName.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objName.setLvlCode3(AuditConst.PM_LVL_CODE_3_ST);
			objName.setDocId(BigInteger.valueOf(managedBean.getResState().getStateId()));
			objName.setFieldLabelCode(AuditConst.PM_AUD_ST_STNAME_FIELDLABELID);
			objName.setNewValue(managedBean.getResState().getStateName());
			objName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objName);

			objDV.setRoleCode(roleCode);
			objDV.setDoctype(AuditConst.PM_AUD_ST_DOC_TYPE);
			objDV.setPmParameterLvlId(AuditConst.PM_AUD_ST_PMPARAMETERLVLID);
			objDV.setDocNo(managedBean.getState().getStateCode());
			objDV.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objDV.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objDV.setLvlCode3(AuditConst.PM_LVL_CODE_3_ST);
			objDV.setDocId(BigInteger.valueOf(managedBean.getResState().getStateId()));
			objDV.setFieldLabelCode(AuditConst.PM_AUD_ST_STDIV_FIELDLABELID);
			objDV.setNewValue(newValue(managedBean));
			objDV.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objDV);

			objStatus.setRoleCode(roleCode);
			objStatus.setDoctype(AuditConst.PM_AUD_ST_DOC_TYPE);
			objStatus.setPmParameterLvlId(AuditConst.PM_AUD_ST_PMPARAMETERLVLID);
			objStatus.setDocNo(managedBean.getState().getStateCode());
			objStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_ST);
			objStatus.setDocId(BigInteger.valueOf(managedBean.getResState().getStateId()));
			objStatus.setFieldLabelCode(AuditConst.PM_AUD_ST_STSTATUS_FIELDLABELID);
			objStatus.setNewValue(managedBean.getResState().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			objStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objStatus);

			auditPMReqDTO.setObjList(objList);
		}
		return auditPMReqDTO;
	}

	private AuditPMReqDTO insertDivisionVal(LocationManagedBean managedBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();

		if (null != managedBean.getDivision().getDivisionId()
				&& managedBean.getDivisionOldVal().getPmDivision().getDivisionId() != null) {
			if (!managedBean.getResDivision().getDivisionName()
					.equalsIgnoreCase(managedBean.getDivisionOldVal().getPmDivision().getDivisionName())) {
				objName.setRoleCode(roleCode);
				objName.setDoctype(AuditConst.PM_AUD_DIV_DOC_TYPE);
				objName.setPmParameterLvlId(AuditConst.PM_AUD_DIV_PMPARAMETERLVLID);
				objName.setDocNo(managedBean.getDivision().getDivisionCode());
				objName.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				objName.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
				objName.setLvlCode3(AuditConst.PM_LVL_CODE_3_DIV);
				objName.setDocId(BigInteger.valueOf(managedBean.getResDivision().getDivisionId()));
				objName.setFieldLabelCode(AuditConst.PM_AUD_DIV_DVNAME_FIELDLABELID);
				objName.setOldValue(managedBean.getDivisionOldVal().getPmDivision().getDivisionName());
				objName.setNewValue(managedBean.getResDivision().getDivisionName());
				objName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(objName);
				auditPMReqDTO.setObjList(objList);
			}
			if (managedBean.getResDivision().getRecordStatusFlg() != managedBean.getDivisionOldVal().getPmDivision()
					.getRecordStatusFlg()) {
				objStatus.setRoleCode(roleCode);
				objStatus.setDoctype(AuditConst.PM_AUD_DIV_DOC_TYPE);
				objStatus.setPmParameterLvlId(AuditConst.PM_AUD_DIV_PMPARAMETERLVLID);
				objStatus.setDocNo(managedBean.getDivision().getDivisionCode());
				objStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				objStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
				objStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_DIV);
				objStatus.setDocId(BigInteger.valueOf(managedBean.getResDivision().getDivisionId()));
				objStatus.setFieldLabelCode(AuditConst.PM_AUD_DIV_DVSTATUS_FIELDLABELID);
				objStatus
						.setOldValue(managedBean.getDivisionOldVal().getPmDivision().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				objStatus.setNewValue(managedBean.getResDivision().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				objStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(objStatus);
				auditPMReqDTO.setObjList(objList);
			}
		} else {
			/** INSERT NEW transaction ***/
			objCountry.setRoleCode(roleCode);
			objCountry.setDoctype(AuditConst.PM_AUD_DIV_DOC_TYPE);
			objCountry.setPmParameterLvlId(AuditConst.PM_AUD_DIV_PMPARAMETERLVLID);
			objCountry.setDocNo(managedBean.getDivision().getDivisionCode());
			objCountry.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objCountry.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objCountry.setLvlCode3(AuditConst.PM_LVL_CODE_3_DIV);
			objCountry.setDocId(BigInteger.valueOf(managedBean.getResDivision().getDivisionId()));
			objCountry.setFieldLabelCode(AuditConst.PM_AUD_DIV_DVCOUNTRY_FIELDLABELID);
			objCountry.setNewValueId(BigInteger.valueOf(managedBean.getDivision().getPmState().getPmCountry()
					.getCountryId()));
			objCountry.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objCountry);

			objStateId.setRoleCode(roleCode);
			objStateId.setDoctype(AuditConst.PM_AUD_DIV_DOC_TYPE);
			objStateId.setPmParameterLvlId(AuditConst.PM_AUD_DIV_PMPARAMETERLVLID);
			objStateId.setDocNo(managedBean.getDivision().getDivisionCode());
			objStateId.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objStateId.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objStateId.setLvlCode3(AuditConst.PM_LVL_CODE_3_DIV);
			objStateId.setDocId(BigInteger.valueOf(managedBean.getResDivision().getDivisionId()));
			objStateId.setFieldLabelCode(AuditConst.PM_AUD_DIV_DVSTATECODE_FIELDLABELID);
			objStateId.setNewValueId(BigInteger.valueOf(managedBean.getDivision().getPmState().getStateId()));
			objStateId.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objStateId);

			objCode.setRoleCode(roleCode);
			objCode.setDoctype(AuditConst.PM_AUD_DIV_DOC_TYPE);
			objCode.setPmParameterLvlId(AuditConst.PM_AUD_DIV_PMPARAMETERLVLID);
			objCode.setDocNo(managedBean.getDivision().getDivisionCode());
			objCode.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objCode.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objCode.setLvlCode3(AuditConst.PM_LVL_CODE_3_DIV);
			objCode.setDocId(BigInteger.valueOf(managedBean.getResDivision().getDivisionId()));
			objCode.setFieldLabelCode(AuditConst.PM_AUD_DIV_DVCODE_FIELDLABELID);
			objCode.setNewValue(managedBean.getResDivision().getDivisionCode());
			objCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objCode);

			objName.setRoleCode(roleCode);
			objName.setDoctype(AuditConst.PM_AUD_DIV_DOC_TYPE);
			objName.setPmParameterLvlId(AuditConst.PM_AUD_DIV_PMPARAMETERLVLID);
			objName.setDocNo(managedBean.getDivision().getDivisionCode());
			objName.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objName.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objName.setLvlCode3(AuditConst.PM_LVL_CODE_3_DIV);
			objName.setDocId(BigInteger.valueOf(managedBean.getResDivision().getDivisionId()));
			objName.setFieldLabelCode(AuditConst.PM_AUD_DIV_DVNAME_FIELDLABELID);
			objName.setNewValue(managedBean.getResDivision().getDivisionName());
			objName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);

			objStatus.setRoleCode(roleCode);
			objStatus.setDoctype(AuditConst.PM_AUD_DIV_DOC_TYPE);
			objStatus.setPmParameterLvlId(AuditConst.PM_AUD_DIV_PMPARAMETERLVLID);
			objStatus.setDocNo(managedBean.getDivision().getDivisionCode());
			objStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_DIV);
			objStatus.setDocId(BigInteger.valueOf(managedBean.getResDivision().getDivisionId()));
			objStatus.setFieldLabelCode(AuditConst.PM_AUD_DIV_DVSTATUS_FIELDLABELID);
			objStatus.setNewValue(managedBean.getResDivision().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			objStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objName);
			objList.add(objStatus);
			auditPMReqDTO.setObjList(objList);
		}
		return auditPMReqDTO;
	}

	private AuditPMReqDTO insertDistrictVal(LocationManagedBean managedBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();

		if (null != managedBean.getDistrict().getDistrictId()
				&& managedBean.getDistrictOldVal().getPmDistrict().getDistrictId() != null) {
			/** UPDATE transaction ***/
			if (!managedBean.getResDistrict().getDistrictName()
					.equalsIgnoreCase(managedBean.getDistrictOldVal().getPmDistrict().getDistrictName())) {
				objName.setRoleCode(roleCode);
				objName.setDoctype(AuditConst.PM_AUD_DS_DOC_TYPE);
				objName.setPmParameterLvlId(AuditConst.PM_AUD_DS_PMPARAMETERLVLID);
				objName.setDocNo(managedBean.getResDistrict().getDistrictCode());
				objName.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				objName.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
				objName.setLvlCode3(AuditConst.PM_LVL_CODE_3_DST);
				objName.setFieldLabelCode(AuditConst.PM_AUD_ST_DSNAME_FIELDLABELID);
				objName.setDocId(BigInteger.valueOf(managedBean.getResDistrict().getDistrictId()));
				objName.setOldValue(managedBean.getDistrictOldVal().getPmDistrict().getDistrictName());
				objName.setNewValue(managedBean.getResDistrict().getDistrictName());
				objName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(objName);
				auditPMReqDTO.setObjList(objList);
			}
			if (managedBean.getResDistrict().getRecordStatusFlg() != managedBean.getDistrictOldVal().getPmDistrict()
					.getRecordStatusFlg()) {
				objStatus.setRoleCode(roleCode);
				objStatus.setDoctype(AuditConst.PM_AUD_DS_DOC_TYPE);
				objStatus.setPmParameterLvlId(AuditConst.PM_AUD_DS_PMPARAMETERLVLID);
				objStatus.setDocNo(managedBean.getResDistrict().getDistrictCode());
				objStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				objStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
				objStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_DST);
				objStatus.setFieldLabelCode(AuditConst.PM_AUD_ST_DSSTATUS_FIELDLABELID); // District
				objStatus.setDocId(BigInteger.valueOf(managedBean.getResDistrict().getDistrictId()));
				objStatus
						.setOldValue(managedBean.getDistrictOldVal().getPmDistrict().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				objStatus.setNewValue(managedBean.getResDistrict().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				objStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(objStatus);
				auditPMReqDTO.setObjList(objList);
			}
		} else {
			/** INSERT NEW transaction ***/
			objCountry.setRoleCode(roleCode);
			objCountry.setDoctype(AuditConst.PM_AUD_DS_DOC_TYPE);
			objCountry.setPmParameterLvlId(AuditConst.PM_AUD_DS_PMPARAMETERLVLID);
			objCountry.setDocNo(managedBean.getResDistrict().getDistrictCode());
			objCountry.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objCountry.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objCountry.setLvlCode3(AuditConst.PM_LVL_CODE_3_DST);
			objCountry.setDocId(BigInteger.valueOf(managedBean.getResDistrict().getDistrictId().intValue()));
			objCountry.setFieldLabelCode(AuditConst.PM_AUD_DS_DSCOUNTRY_FIELDLABELID);
			objCountry.setNewValueId(BigInteger.valueOf(managedBean.getDistrict().getPmState().getPmCountry()
					.getCountryId()));
			objCountry.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objCountry);

			objStateId.setRoleCode(roleCode);
			objStateId.setDoctype(AuditConst.PM_AUD_DS_DOC_TYPE);
			objStateId.setPmParameterLvlId(AuditConst.PM_AUD_DS_PMPARAMETERLVLID);
			objStateId.setDocNo(managedBean.getResDistrict().getDistrictCode());
			objStateId.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objStateId.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objStateId.setLvlCode3(AuditConst.PM_LVL_CODE_3_DST);
			objStateId.setDocId(BigInteger.valueOf(managedBean.getResDistrict().getDistrictId().intValue()));
			objStateId.setFieldLabelCode(AuditConst.PM_AUD_DS_DSSTATEID_FIELDLABELID);
			objStateId.setNewValueId(BigInteger.valueOf(managedBean.getDistrict().getPmState().getStateId()));
			objStateId.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objStateId);

			if (managedBean.getResDistrict().getPmDivision() != null) {
				objDivisionId.setRoleCode(roleCode);
				objDivisionId.setDoctype(AuditConst.PM_AUD_DS_DOC_TYPE);
				objDivisionId.setPmParameterLvlId(AuditConst.PM_AUD_DS_PMPARAMETERLVLID);
				objDivisionId.setDocNo(managedBean.getResDistrict().getDistrictCode());
				objDivisionId.setLvlCode1(AuditConst.PM_LVL_CODE_1);
				objDivisionId.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
				objDivisionId.setLvlCode3(AuditConst.PM_LVL_CODE_3_DST);
				objDivisionId.setDocId(BigInteger.valueOf(managedBean.getResDistrict().getDistrictId()));
				objDivisionId.setFieldLabelCode(AuditConst.PM_AUD_DS_DSDIVISIONID_FIELDLABELID);
				objDivisionId.setNewValueId(BigInteger.valueOf(managedBean.getDistrict().getPmDivision()
						.getDivisionId()));
				objDivisionId.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
				objList.add(objDivisionId);
			}
			objCode.setRoleCode(roleCode);
			objCode.setDoctype(AuditConst.PM_AUD_DS_DOC_TYPE);
			objCode.setPmParameterLvlId(AuditConst.PM_AUD_DS_PMPARAMETERLVLID);
			objCode.setDocNo(managedBean.getResDistrict().getDistrictCode());
			objCode.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objCode.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objCode.setLvlCode3(AuditConst.PM_LVL_CODE_3_DST);
			objCode.setDocId(BigInteger.valueOf(managedBean.getResDistrict().getDistrictId().intValue()));
			objCode.setFieldLabelCode(AuditConst.PM_AUD_DS_DSCODE_FIELDLABELID);
			objCode.setNewValue(managedBean.getResDistrict().getDistrictCode());
			objCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objCode);

			objName.setRoleCode(roleCode);
			objName.setDoctype(AuditConst.PM_AUD_DS_DOC_TYPE);
			objName.setPmParameterLvlId(AuditConst.PM_AUD_DS_PMPARAMETERLVLID);
			objName.setDocNo(managedBean.getResDistrict().getDistrictCode());
			objName.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objName.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objName.setLvlCode3(AuditConst.PM_LVL_CODE_3_DST);
			objName.setDocId(BigInteger.valueOf(managedBean.getResDistrict().getDistrictId()));
			objName.setFieldLabelCode(AuditConst.PM_AUD_ST_DSNAME_FIELDLABELID);
			objName.setNewValue(managedBean.getResDistrict().getDistrictName());
			objName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objName);

			objStatus.setRoleCode(roleCode);
			objStatus.setDoctype(AuditConst.PM_AUD_DS_DOC_TYPE);
			objStatus.setPmParameterLvlId(AuditConst.PM_AUD_DS_PMPARAMETERLVLID);
			objStatus.setDocNo(managedBean.getResDistrict().getDistrictCode());
			objStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_DST);
			objStatus.setDocId(BigInteger.valueOf(managedBean.getResDistrict().getDistrictId()));
			objStatus.setFieldLabelCode(AuditConst.PM_AUD_ST_DSSTATUS_FIELDLABELID);
			objStatus.setNewValue(managedBean.getResDistrict().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			objStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objStatus);

			auditPMReqDTO.setObjList(objList);
		}

		return auditPMReqDTO;
	}

	private AuditPMReqDTO insertCityVal(LocationManagedBean managedBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();

		objName.setRoleCode(roleCode);
		objName.setDoctype(AuditConst.PM_AUD_CT_DOC_TYPE);
		objName.setPmParameterLvlId(AuditConst.PM_AUD_CT_PMPARAMETERLVLID);
		objName.setDocNo(managedBean.getCity().getCityCode());
		objName.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		objName.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
		objName.setLvlCode3(AuditConst.PM_LVL_CODE_3_CT);
		objName.setDocId(BigInteger.valueOf(managedBean.getResCity().getCityId()));
		objName.setFieldLabelCode(AuditConst.PM_AUD_CT_CTNAME_FIELDLABELID);

		objStatus.setRoleCode(roleCode);
		objStatus.setDoctype(AuditConst.PM_AUD_CT_DOC_TYPE);
		objStatus.setPmParameterLvlId(AuditConst.PM_AUD_CT_PMPARAMETERLVLID);
		objStatus.setDocNo(managedBean.getCity().getCityCode());
		objStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		objStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
		objStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_CT);
		objStatus.setFieldLabelCode(AuditConst.PM_AUD_CT_CTSTATUS_FIELDLABELID);
		objStatus.setDocId(BigInteger.valueOf(managedBean.getResCity().getCityId()));
		if (null != managedBean.getCity().getCityId() && managedBean.getCityOldVal().getPmCity().getCityId() != null) {
			/** UPDATE transaction ***/
			if (!managedBean.getResCity().getCityName()
					.equalsIgnoreCase(managedBean.getCityOldVal().getPmCity().getCityName())) {
				objName.setOldValue(managedBean.getCityOldVal().getPmCity().getCityName());
				objName.setNewValue(managedBean.getResCity().getCityName());
				objName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(objName);
				auditPMReqDTO.setObjList(objList);
			}
			if (managedBean.getResCity().getRecordStatusFlg() != managedBean.getCityOldVal().getPmCity()
					.getRecordStatusFlg()) {
				objStatus
						.setOldValue(managedBean.getCityOldVal().getPmCity().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				objStatus.setNewValue(managedBean.getResCity().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				objStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(objStatus);
				auditPMReqDTO.setObjList(objList);
			}
		} else {
			/** INSERT NEW transaction ***/
			objCountry.setRoleCode(roleCode);
			objCountry.setDoctype(AuditConst.PM_AUD_CT_DOC_TYPE);
			objCountry.setPmParameterLvlId(AuditConst.PM_AUD_CT_PMPARAMETERLVLID);
			objCountry.setDocNo(managedBean.getCity().getCityCode());
			objCountry.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objCountry.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objCountry.setLvlCode3(AuditConst.PM_LVL_CODE_3_CT);
			objCountry.setDocId(BigInteger.valueOf(managedBean.getResCity().getCityId()));
			objCountry.setFieldLabelCode(AuditConst.PM_AUD_CT_CTCOUNTRY_FIELDLABELID);
			objCountry.setNewValueId(BigInteger.valueOf(managedBean.getState().getPmCountry().getCountryId()));
			objCountry.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objCountry);

			objStateId.setRoleCode(roleCode);
			objStateId.setDoctype(AuditConst.PM_AUD_CT_DOC_TYPE);
			objStateId.setPmParameterLvlId(AuditConst.PM_AUD_CT_PMPARAMETERLVLID);
			objStateId.setDocNo(managedBean.getCity().getCityCode());
			objStateId.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objStateId.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objStateId.setLvlCode3(AuditConst.PM_LVL_CODE_3_CT);
			objStateId.setDocId(BigInteger.valueOf(managedBean.getResCity().getCityId()));
			objStateId.setFieldLabelCode(AuditConst.PM_AUD_CT_CTSTATECODE_FIELDLABELID);
			objStateId.setNewValueId(BigInteger.valueOf(managedBean.getDistrict().getPmState().getStateId()));
			objStateId.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objStateId);

			objDistrictId.setRoleCode(roleCode);
			objDistrictId.setDoctype(AuditConst.PM_AUD_CT_DOC_TYPE);
			objDistrictId.setPmParameterLvlId(AuditConst.PM_AUD_CT_PMPARAMETERLVLID);
			objDistrictId.setDocNo(managedBean.getCity().getCityCode());
			objDistrictId.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objDistrictId.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objDistrictId.setLvlCode3(AuditConst.PM_LVL_CODE_3_CT);
			objDistrictId.setDocId(BigInteger.valueOf(managedBean.getResCity().getCityId()));
			objDistrictId.setFieldLabelCode(AuditConst.PM_AUD_CT_CTDISTRICTID_FIELDLABELID);
			objDistrictId.setNewValueId(BigInteger.valueOf(managedBean.getCity().getPmDistrict().getDistrictId()));
			objDistrictId.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objDistrictId);

			objCode.setRoleCode(roleCode);
			objCode.setDoctype(AuditConst.PM_AUD_CT_DOC_TYPE);
			objCode.setPmParameterLvlId(AuditConst.PM_AUD_CT_PMPARAMETERLVLID);
			objCode.setDocNo(managedBean.getCity().getCityCode());
			objCode.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			objCode.setLvlCode2(AuditConst.PM_LVL_CODE_2_LOC);
			objCode.setLvlCode3(AuditConst.PM_LVL_CODE_3_CT);
			objCode.setDocId(BigInteger.valueOf(managedBean.getResCity().getCityId()));
			objCode.setFieldLabelCode(AuditConst.PM_AUD_CT_CTCODE_FIELDLABELID);
			objCode.setNewValue(managedBean.getCity().getCityCode());
			objCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(objCode);

			objName.setNewValue(managedBean.getResCity().getCityName());
			objName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);

			objStatus.setNewValue(managedBean.getResCity().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			objStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);

			objList.add(objName);
			objList.add(objStatus);
			auditPMReqDTO.setObjList(objList);
		}

		return auditPMReqDTO;
	}

	/**
	 * @return String OldValue
	 */
	private String oldValue(LocationManagedBean managedBean) {
		if (managedBean.getStateOldVal().getPmState().getAdoptDivision()) {
			objDV.setOldValue(AuditConst.PM_AUD_YES);
		} else {
			objDV.setOldValue(AuditConst.PM_AUD_NO);
		}
		return objDV.getOldValue();
	}

	/**
	 * @return String NewValue
	 */
	private String newValue(LocationManagedBean managedBean) {
		if (managedBean.getResState().getAdoptDivision()) {
			objDV.setNewValue(AuditConst.PM_AUD_YES);
		} else {
			objDV.setNewValue(AuditConst.PM_AUD_NO);
		}
		return objDV.getNewValue();
	}
}