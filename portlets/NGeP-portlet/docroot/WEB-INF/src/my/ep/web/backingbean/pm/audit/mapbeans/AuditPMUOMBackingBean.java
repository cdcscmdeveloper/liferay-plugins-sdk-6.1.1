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
 * AuditPMUOMBackingBean #28895-35509
 * 
 * @author normuliana
 * 
 */
public class AuditPMUOMBackingBean extends AuditPMBackingBeanTemplate {
	private static final long serialVersionUID = 1L;
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	String roleCode;
	String docNo;
	String docType;
	BigInteger docId;

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
		UnitOfMeasurementManagedBean managedBeans = (UnitOfMeasurementManagedBean) manageBean;
		this.roleCode = roleCode;
		this.docNo = managedBeans.getPmUom().getUomCode();
		this.docType = managedBeans.getPmUom().getUomName();
		if (null != managedBeans.getAuditUomOldVal() && managedBeans.getAuditUomOldVal().getUomId() > 0) {
			if (managedBeans.getAuditUomOldVal().getRecordStatusFlg() != managedBeans.getPmUom().getRecordStatusFlg()) {
				this.docId = BigInteger.valueOf(managedBeans.getAuditUomOldVal().getUomId());
				detailObj(AuditConst.PM_AUD_UOM_STATUS_FIELDLABELID,
						managedBeans.getAuditUomOldVal().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE,
						managedBeans.getUomResDTO().getPmUom().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE, AuditConst.PM_AUD_ACTION_TYPE_UPD);
			}
			managedBeans.setAuditUomOldVal(null);
		} else {
			this.docId = BigInteger.valueOf(managedBeans.getUomResDTO().getPmUom().getUomId());

			detailObj(AuditConst.PM_AUD_UOM_UOMCODE_FIELDLABELID, null, managedBeans.getUomResDTO().getPmUom().getUomCode(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_UOM_UOMNAME_FIELDLABELID, null, managedBeans.getUomResDTO().getPmUom().getUomName(),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_UOM_NOOFDECIMAL_FIELDLABELID, null, managedBeans.getUomResDTO().getPmUom()
					.getDecimalScale().toString(), AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_UOM_STATUS_FIELDLABELID, null, managedBeans.getUomResDTO().getPmUom()
					.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					AuditConst.PM_AUD_ACTION_TYPE_INS);
		}
		auditPMReqDTO.setObjList(objList);
		return auditPMReqDTO;
	}

	private ArrayList<AuditPMSPDTO> detailObj(BigInteger fieldLabelId, String oldValue, String newValue, String actionType) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(this.docType);
		dtlObj.setPmParameterLvlId(AuditConst.PM_AUD_UOM_PMPARAMETERLVLID);
		dtlObj.setDocNo(docNo);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_UOM);
		dtlObj.setLvlCode3(AuditConst.PM_LVL_CODE_3_UOM);
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
}