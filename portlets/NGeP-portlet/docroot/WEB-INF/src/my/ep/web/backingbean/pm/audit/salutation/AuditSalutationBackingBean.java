package my.ep.web.backingbean.pm.audit.salutation;

import java.util.ArrayList;
import java.util.List;

import my.ep.constant.audit.AuditConst;
import my.ep.dto.pm.common.audit.salutation.AuPmSalutationDTO;
import my.ep.dto.pm.common.audit.salutation.AuPmSalutationDetailSPDTO;
import my.ep.dto.pm.common.audit.salutation.AuPmSalutationMasterSPDTO;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.salutation.SalutationManagedBean;

public class AuditSalutationBackingBean extends AuditPMSalutationBackingBeanTemplate {
	private static final long serialVersionUID = 1L;

	public AuPmSalutationDTO masterInsert(AuPmSalutationDTO au, Long userId, BaseManagedBean manageBean, String roleCode) {
		SalutationManagedBean salManagedBean = (SalutationManagedBean) manageBean;
		AuPmSalutationDTO auPMObj = (AuPmSalutationDTO) au;
		ArrayList<AuPmSalutationMasterSPDTO> masterObjlist = new ArrayList<AuPmSalutationMasterSPDTO>();
		AuPmSalutationMasterSPDTO msterObj = new AuPmSalutationMasterSPDTO();

		msterObj.setRoleCode(roleCode);
		msterObj.setDoctype(AuditConst.PM_AUD_SALUTATION);
		msterObj.setPmParameterLvlId(AuditConst.PM_AUD_SALUTATION_PMPARAMETERLVLID);
		msterObj.setDocNo(salManagedBean.getPmSalutation().getSalutationCode());
		msterObj.setPkId(salManagedBean.getPmSalutation().getSalutationId());
		msterObj.setColumnName(AuditConst.PM_AUD_SALUTATION_SOURCECOLUMN);
		masterObjlist.add(msterObj);

		auPMObj.setSalutationMasterObjlist(masterObjlist);

		salManagedBean.setPmSalutation(null);
		salManagedBean.setOldPmSalutation(null);
		
		return auPMObj;
	}

	public AuPmSalutationDTO detailsInsert(BaseManagedBean managedBean, Long userId) {
		SalutationManagedBean salManagedBean = (SalutationManagedBean) managedBean;
		AuPmSalutationDTO auPMObj = new AuPmSalutationDTO();
		auPMObj = insertDetailVal(auPMObj, salManagedBean, userId);

		return auPMObj;
	}

	List<AuPmSalutationDetailSPDTO> detailObjlist = new ArrayList<AuPmSalutationDetailSPDTO>();
	ArrayList<AuPmSalutationDetailSPDTO> dtlObjlist = new ArrayList<AuPmSalutationDetailSPDTO>();
	AuPmSalutationDetailSPDTO dtlObjName = new AuPmSalutationDetailSPDTO();
	AuPmSalutationDetailSPDTO dtlSatus = new AuPmSalutationDetailSPDTO();
	AuPmSalutationDetailSPDTO dtlCode = new AuPmSalutationDetailSPDTO();

	private AuPmSalutationDTO insertDetailVal(AuPmSalutationDTO auPMObj, SalutationManagedBean salManagedBean, Long userId) {
		if (salManagedBean.getOldPmSalutation() != null && salManagedBean.getOldPmSalutation().getSalutationId() != null) {
			if (salManagedBean.getPmSalutation().getSalutationName() != null
					&& !salManagedBean.getOldPmSalutation().getSalutationName().equalsIgnoreCase(salManagedBean.getPmSalutation().getSalutationName())) {
				dtlObjName.setDocId(salManagedBean.getPmSalutation().getSalutationId().intValue());
				dtlObjName.setPmFieldLabelid(AuditConst.PM_AUD_SALUTATION_NAME_FIELDLABELID);
				dtlObjName.setOldValue(salManagedBean.getOldPmSalutation().getSalutationName());
				dtlObjName.setNewValue(salManagedBean.getPmSalutation().getSalutationName());
				dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				dtlObjlist.add(dtlObjName);
				auPMObj.setSalutationDetailObjlist(dtlObjlist);			
			}
			if (salManagedBean.getOldPmSalutation().getRecordStatusFlg() != salManagedBean.getPmSalutation().getRecordStatusFlg()) {
				dtlSatus.setDocId(salManagedBean.getPmSalutation().getSalutationId().intValue());
				dtlSatus.setPmFieldLabelid(AuditConst.PM_AUD_SALUTATION_STATUS_FIELDLABELID);
				dtlSatus.setOldValue(salManagedBean.getOldPmSalutation().getRecordStatusFlg()==1?AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlSatus.setNewValue(salManagedBean.getPmSalutation().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
				dtlSatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				dtlObjlist.add(dtlSatus);
				auPMObj.setSalutationDetailObjlist(dtlObjlist);
			}			
		} else {
			dtlCode.setDocId(salManagedBean.getPmSalutation().getSalutationId().intValue());
			dtlCode.setPmFieldLabelid(AuditConst.PM_AUD_SALUTATION_CODE_FIELDLABELID);
			dtlCode.setNewValue(salManagedBean.getPmSalutation().getSalutationCode());
			dtlCode.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(dtlCode);
			auPMObj.setSalutationDetailObjlist(dtlObjlist);	
			
			dtlObjName.setDocId(salManagedBean.getPmSalutation().getSalutationId().intValue());
			dtlObjName.setPmFieldLabelid(AuditConst.PM_AUD_SALUTATION_NAME_FIELDLABELID);
			dtlObjName.setNewValue(salManagedBean.getPmSalutation().getSalutationName());
			dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(dtlObjName);
			auPMObj.setSalutationDetailObjlist(dtlObjlist);		

			dtlSatus.setDocId(salManagedBean.getPmSalutation().getSalutationId().intValue());
			dtlSatus.setPmFieldLabelid(AuditConst.PM_AUD_SALUTATION_STATUS_FIELDLABELID);
			dtlSatus.setNewValue(salManagedBean.getPmSalutation().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE:AuditConst.PM_AUD_INACTIVE);
			dtlSatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			dtlObjlist.add(dtlSatus);
			auPMObj.setSalutationDetailObjlist(dtlObjlist);
		}
		return auPMObj;
	}
}
