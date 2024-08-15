package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.unitofmeasurement.UnitOfMeasurementManagedBean;

/**
 * AuditPMUOMBackingBean
 * 
 * @author normuliana
 * 
 */
public class AuditPMUOMBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AuditPMSPDTO detailObjStatus = new AuditPMSPDTO();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleUOM();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		UnitOfMeasurementManagedBean managedBeans = (UnitOfMeasurementManagedBean) manageBean;

		detailObjStatus.setRoleCode(roleCode);
		detailObjStatus.setDoctype(AuditConst.PM_AUD_UOM);
		detailObjStatus.setPmParameterLvlId(AuditConst.PM_AUD_UOM_PMPARAMETERLVLID);
		detailObjStatus.setDocNo(managedBeans.getPmUom().getUomCode());
		detailObjStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		detailObjStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_UOM);
		detailObjStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_UOM);

		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		if (null != managedBeans.getAuditUomOldVal() && managedBeans.getAuditUomOldVal().getUomId() > 0) {
			if (managedBeans.getAuditUomOldVal().getRecordStatusFlg() != managedBeans.getPmUom().getRecordStatusFlg()) {
				detailObjStatus.setDocId(BigInteger.valueOf(managedBeans.getAuditUomOldVal().getUomId()));
				detailObjStatus.setFieldLabelCode(AuditConst.PM_AUD_UOM_STATUS_FIELDLABELID);
				detailObjStatus
						.setOldValue(managedBeans.getAuditUomOldVal().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				detailObjStatus
						.setNewValue(managedBeans.getUomResDTO().getPmUom().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(detailObjStatus);
				auditPMReqDTO.setObjList(objList);
			}
			managedBeans.setAuditUomOldVal(null);
		} else {
			detailObjStatus.setDocId(BigInteger.valueOf(managedBeans.getUomResDTO().getPmUom().getUomId()));
			detailObjStatus.setFieldLabelCode(AuditConst.PM_AUD_UOM_STATUS_FIELDLABELID);
			detailObjStatus
					.setNewValue(managedBeans.getUomResDTO().getPmUom().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjStatus);

			AuditPMSPDTO uomCode = new AuditPMSPDTO();
			uomCode.setRoleCode(roleCode);
			uomCode.setDoctype(AuditConst.PM_AUD_UOM);
			uomCode.setPmParameterLvlId(AuditConst.PM_AUD_UOM_PMPARAMETERLVLID);
			uomCode.setDocNo(managedBeans.getPmUom().getUomCode());
			uomCode.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			uomCode.setLvlCode2(AuditConst.PM_LVL_CODE_2_UOM);
			uomCode.setLvlCode3(AuditConst.PM_LVL_CODE_3_UOM);
			uomCode.setDocId(BigInteger.valueOf(managedBeans.getUomResDTO().getPmUom().getUomId()));
			uomCode.setFieldLabelCode(AuditConst.PM_AUD_UOM_UOMCODE_FIELDLABELID);
			uomCode.setNewValue(managedBeans.getUomResDTO().getPmUom().getUomCode());
			uomCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(uomCode);

			AuditPMSPDTO uomName = new AuditPMSPDTO();
			uomName.setRoleCode(roleCode);
			uomName.setDoctype(AuditConst.PM_AUD_UOM);
			uomName.setPmParameterLvlId(AuditConst.PM_AUD_UOM_PMPARAMETERLVLID);
			uomName.setDocNo(managedBeans.getPmUom().getUomCode());
			uomName.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			uomName.setLvlCode2(AuditConst.PM_LVL_CODE_2_UOM);
			uomName.setLvlCode3(AuditConst.PM_LVL_CODE_3_UOM);
			uomName.setDocId(BigInteger.valueOf(managedBeans.getUomResDTO().getPmUom().getUomId()));
			uomName.setFieldLabelCode(AuditConst.PM_AUD_UOM_UOMNAME_FIELDLABELID);
			uomName.setNewValue(managedBeans.getUomResDTO().getPmUom().getUomName());
			uomName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(uomName);

			AuditPMSPDTO noOfDecimal = new AuditPMSPDTO();
			noOfDecimal.setRoleCode(roleCode);
			noOfDecimal.setDoctype(AuditConst.PM_AUD_UOM);
			noOfDecimal.setPmParameterLvlId(AuditConst.PM_AUD_UOM_PMPARAMETERLVLID);
			noOfDecimal.setDocNo(managedBeans.getPmUom().getUomCode());
			noOfDecimal.setLvlCode1(AuditConst.PM_LVL_CODE_1);
			noOfDecimal.setLvlCode2(AuditConst.PM_LVL_CODE_2_UOM);
			noOfDecimal.setLvlCode3(AuditConst.PM_LVL_CODE_3_UOM);
			noOfDecimal.setDocId(BigInteger.valueOf(managedBeans.getUomResDTO().getPmUom().getUomId()));
			noOfDecimal.setFieldLabelCode(AuditConst.PM_AUD_UOM_NOOFDECIMAL_FIELDLABELID);
			noOfDecimal.setNewValue(managedBeans.getUomResDTO().getPmUom().getDecimalScale().toString());
			noOfDecimal.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(noOfDecimal);

			auditPMReqDTO.setObjList(objList);
		}

		return auditPMReqDTO;
	}
}