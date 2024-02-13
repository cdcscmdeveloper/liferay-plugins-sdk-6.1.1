package my.ep.web.backingbean.pm.audit.uom;

import java.util.ArrayList;
import java.util.List;
import my.ep.constant.audit.AuditConst;
import my.ep.dto.pm.common.audit.uom.AUPMUomDetailSPDTO;
import my.ep.dto.pm.common.audit.uom.AUPMUomMasterSPDTO;
import my.ep.dto.pm.common.audit.uom.AuPMUomDTO;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.unitofmeasurement.UnitOfMeasurementManagedBean;

public class AuditUomBackingBean extends AuditPMUomBackingBeanTemplate {
	private static final long serialVersionUID = 1L;

	public AuPMUomDTO masterInsert(AuPMUomDTO au, Long userId,BaseManagedBean manageBean, String roleCode) {
		UnitOfMeasurementManagedBean uomManagedBean = (UnitOfMeasurementManagedBean) manageBean;
		AuPMUomDTO auPMObj = (AuPMUomDTO)au;
		ArrayList<AUPMUomMasterSPDTO> masterObjlist = new ArrayList<AUPMUomMasterSPDTO>();
		AUPMUomMasterSPDTO msterObj = new AUPMUomMasterSPDTO();

		msterObj.setRoleCode(roleCode);
		msterObj.setDoctype(AuditConst.PM_AUD_UOM);
		msterObj.setPmParameterLvlId(AuditConst.PM_AUD_UOM_PMPARAMETERLVLID);
		msterObj.setDocNo(uomManagedBean.getPmUom().getUomCode());
		msterObj.setPkId(uomManagedBean.getUomResDTO().getPmUom().getUomId());
		msterObj.setColumnName(AuditConst.PM_AUD_UOM_SOURCECOLUMN);
		masterObjlist.add(msterObj);

		auPMObj.setUomMasterObjlist(masterObjlist);
		
		return auPMObj;
	}

	public AuPMUomDTO detailsInsert(BaseManagedBean managedBean, Long userId) {
		UnitOfMeasurementManagedBean uomManagedBean = (UnitOfMeasurementManagedBean) managedBean;
		AuPMUomDTO auPMObj = new AuPMUomDTO();
		auPMObj = insertDetailVal(auPMObj, uomManagedBean, userId);

		return auPMObj;
	}

	List<AUPMUomDetailSPDTO> detailObjlist = new ArrayList<AUPMUomDetailSPDTO>();
	AUPMUomDetailSPDTO dtlObjStatus = new AUPMUomDetailSPDTO();
	ArrayList<AUPMUomDetailSPDTO> dtlObjlist = new ArrayList<AUPMUomDetailSPDTO>();

	private AuPMUomDTO insertDetailVal(AuPMUomDTO auPMObj, UnitOfMeasurementManagedBean uomManagedBean,
			Long userId) {
		if (null != uomManagedBean.getPmUomOld() && uomManagedBean.getPmUomOld().getUomId() > 0 ) {
			if (uomManagedBean.getPmUomOld().getRecordStatusFlg() != uomManagedBean.getPmUom().getRecordStatusFlg()) {
				dtlObjStatus.setDocId(uomManagedBean.getPmUomOld().getUomId().intValue());
				dtlObjStatus.setPmFieldLabelid(AuditConst.PM_AUD_UOM_STATUS_FIELDLABELID);
				dtlObjStatus
						.setOldValue(uomManagedBean.getPmUomOld().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus
						.setNewValue(uomManagedBean.getUomResDTO().getPmUom().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				dtlObjlist.add(dtlObjStatus);
				auPMObj.setUomDetailObjlist(dtlObjlist);				
			}
			uomManagedBean.setPmUomOld(null);
		} else {
			dtlObjStatus.setDocId(uomManagedBean.getUomResDTO().getPmUom().getUomId().intValue());
			dtlObjStatus.setPmFieldLabelid(AuditConst.PM_AUD_UOM_STATUS_FIELDLABELID);
			dtlObjStatus
					.setNewValue(uomManagedBean.getUomResDTO().getPmUom().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
			dtlObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(dtlObjStatus);
			
			AUPMUomDetailSPDTO uomCode = new AUPMUomDetailSPDTO();
			uomCode.setDocId(uomManagedBean.getUomResDTO().getPmUom().getUomId().intValue());
			uomCode.setPmFieldLabelid(AuditConst.PM_AUD_UOM_UOMCODE_FIELDLABELID);
			uomCode.setNewValue(uomManagedBean.getUomResDTO().getPmUom().getUomCode());
			uomCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(uomCode);
			
			AUPMUomDetailSPDTO uomName = new AUPMUomDetailSPDTO();
			uomName.setDocId(uomManagedBean.getUomResDTO().getPmUom().getUomId().intValue());
			uomName.setPmFieldLabelid(AuditConst.PM_AUD_UOM_UOMNAME_FIELDLABELID);
			uomName.setNewValue(uomManagedBean.getUomResDTO().getPmUom().getUomName());
			uomName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(uomName);
			
			AUPMUomDetailSPDTO noOfDecimal = new AUPMUomDetailSPDTO();
			noOfDecimal.setDocId(uomManagedBean.getUomResDTO().getPmUom().getUomId().intValue());
			noOfDecimal.setPmFieldLabelid(AuditConst.PM_AUD_UOM_NOOFDECIMAL_FIELDLABELID);
			noOfDecimal.setNewValue(uomManagedBean.getUomResDTO().getPmUom().getDecimalScale().toString());
			noOfDecimal.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(noOfDecimal);
			
			auPMObj.setUomDetailObjlist(dtlObjlist);
			}	
		return auPMObj;
	}
}
