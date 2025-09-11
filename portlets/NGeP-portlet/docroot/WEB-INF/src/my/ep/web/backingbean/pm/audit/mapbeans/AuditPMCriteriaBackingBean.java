package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmCriteriaDesc;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.supplierperformanceCriteria.CriteriaManagedBean;

/**
 * AuditPMUOMBackingBean - 28895-35509
 * 
 * @author normuliana
 * 
 */
public class AuditPMCriteriaBackingBean extends AuditPMBackingBeanTemplate {
	private static final long serialVersionUID = 1L;
	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	String roleCode;
	String docNo;
	BigInteger docId;
	BigInteger docGroupId;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.supplierPerformanceCriteriaUOM();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		CriteriaManagedBean managedBeans = (CriteriaManagedBean) manageBean;
		this.roleCode = roleCode;
		this.docNo = managedBeans.getSelectedCriteria().getCriteriaCode();
		this.docGroupId = BigInteger.valueOf(managedBeans.getSelectedCriteria().getCriteriaId());
		if (null != managedBeans.getPmCriteriaOld()) {
			this.docId = BigInteger.valueOf(managedBeans.getPmCriteriaOld().getCriteriaId());
			if (managedBeans.getPmCriteriaOld().getPmCriteriaDescs() != null) {
				updateCriteriaDesc(managedBeans);
			}
			if (managedBeans.getPmCriteriaOld().getRecordStatusFlg() != managedBeans.getSelectedCriteria().getRecordStatusFlg()) {
				updateSatus(managedBeans);
			}
		} else {
			this.docId = BigInteger.valueOf(managedBeans.getSelectedCriteria().getCriteriaId());
			if (managedBeans.getSelectedCriteria().getPmCriteriaDescs() != null) {
				createNewLanguageCode(managedBeans);
			}
			detailObj(AuditConst.PM_AUD_SUPPPERFCRITERIA_STATUS_FIELDLABELID, null, managedBeans.getSelectedCriteria()
					.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					AuditConst.PM_AUD_ACTION_TYPE_INS);
		}
		auditPMReqDTO.setObjList(objList);
		return auditPMReqDTO;
	}

	private void updateSatus(CriteriaManagedBean managedBeans) {
		if (managedBeans.getPmCriteriaOld().getRecordStatusFlg() != managedBeans.getSelectedCriteria().getRecordStatusFlg()) {
			this.docId = BigInteger.valueOf(managedBeans.getPmCriteriaOld().getCriteriaId());
			detailObj(AuditConst.PM_AUD_SUPPPERFCRITERIA_STATUS_FIELDLABELID, managedBeans.getPmCriteriaOld()
					.getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE, managedBeans
					.getSelectedCriteria().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE : AuditConst.PM_AUD_INACTIVE,
					AuditConst.PM_AUD_ACTION_TYPE_UPD);
		}
	}

	private void updateCriteriaDesc(CriteriaManagedBean managedBeans) {
		Map<String, PmCriteriaDesc> descMap = new HashMap<>();
		for (PmCriteriaDesc desc : managedBeans.getSelectedCriteria().getPmCriteriaDescs()) {
			descMap.put(desc.getLanguageCode().toUpperCase(), desc);
		}

		for (PmCriteriaDesc descOld : managedBeans.getPmCriteriaOld().getPmCriteriaDescs()) {
			String langCode = descOld.getLanguageCode().toUpperCase();
			PmCriteriaDesc desc = descMap.get(langCode);
			if (desc != null
					&& managedBeans.getPmCriteriaOld().getCriteriaId().equals(managedBeans.getSelectedCriteria().getCriteriaId())
					&& !descOld.getCriteria().equalsIgnoreCase(desc.getCriteria())) {
				if (langCode.equalsIgnoreCase(AuditConst.PM_LANGUAGECODE_MS)) {
					detailObj(AuditConst.PM_AUD_SUPPPERFCRITERIA_BM_FIELDLABELID, descOld.getCriteria(), desc.getCriteria(),
							AuditConst.PM_AUD_ACTION_TYPE_UPD);
				} else if (langCode.equalsIgnoreCase(AuditConst.PM_LANGUAGECODE_EN)) {
					detailObj(AuditConst.PM_AUD_SUPPPERFCRITERIA_EN_FIELDLABELID, descOld.getCriteria(), desc.getCriteria(),
							AuditConst.PM_AUD_ACTION_TYPE_UPD);
				}
			}
		}
	}

	private void createNewLanguageCode(CriteriaManagedBean managedBeans) {
		for (PmCriteriaDesc desc : managedBeans.getSelectedCriteria().getPmCriteriaDescs()) {
			if (desc.getLanguageCode().equalsIgnoreCase(AuditConst.PM_LANGUAGECODE_MS)) {
				detailObj(AuditConst.PM_AUD_SUPPPERFCRITERIA_BM_FIELDLABELID, null, desc.getCriteria(),
						AuditConst.PM_AUD_ACTION_TYPE_INS);
			}
			if (desc.getLanguageCode().equalsIgnoreCase(AuditConst.PM_LANGUAGECODE_EN)) {
				detailObj(AuditConst.PM_AUD_SUPPPERFCRITERIA_EN_FIELDLABELID, null, desc.getCriteria(),
						AuditConst.PM_AUD_ACTION_TYPE_INS);
			}
		}
	}

	private ArrayList<AuditPMSPDTO> detailObj(BigInteger fieldLabelId, String oldValue, String newValue, String actionType) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(AuditConst.PM_AUD_SUPPPERFCRITERIA);
		dtlObj.setPmParameterLvlId(AuditConst.PM_AUD_SUPPPERFCRITERIA_PMPARAMETERLVLID);
		dtlObj.setDocNo(docNo);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPCM);
		dtlObj.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPCM);
		dtlObj.setDocId(docId);
		dtlObj.setDocGroupId(docGroupId);
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