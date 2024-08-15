package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;
import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmCriteriaDesc;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.supplierperformanceCriteria.CriteriaManagedBean;

/**
 * AuditPMUOMBackingBean
 * 
 * @author normuliana
 * 
 */
public class AuditPMCriteriaBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	AuditPMSPDTO detailObjStatus = new AuditPMSPDTO();

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
		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		CriteriaManagedBean managedBeans = (CriteriaManagedBean) manageBean;

		detailObjStatus.setRoleCode(roleCode);
		detailObjStatus.setDoctype(AuditConst.PM_AUD_SUPPPERFCRITERIA);
		detailObjStatus.setPmParameterLvlId(AuditConst.PM_AUD_SUPPPERFCRITERIA_PMPARAMETERLVLID);
		detailObjStatus.setDocNo(managedBeans.getSelectedCriteria().getCriteriaCode());
		detailObjStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
		detailObjStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPCM);
		detailObjStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPCM);

		ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
		if (null != managedBeans.getPmCriteriaOld()) {
			if (managedBeans.getPmCriteriaOld().getRecordStatusFlg() != managedBeans.getSelectedCriteria()
					.getRecordStatusFlg()) {
				detailObjStatus.setDocId(BigInteger.valueOf(managedBeans.getPmCriteriaOld().getCriteriaId()));
				detailObjStatus.setDocGroupId(BigInteger.valueOf(managedBeans.getPmCriteriaOld().getCriteriaId()));
				detailObjStatus.setFieldLabelCode(AuditConst.PM_AUD_SUPPPERFCRITERIA_STATUS_FIELDLABELID);
				detailObjStatus
						.setOldValue(managedBeans.getPmCriteriaOld().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				detailObjStatus
						.setNewValue(managedBeans.getSelectedCriteria().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
								: AuditConst.PM_AUD_INACTIVE);
				detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(detailObjStatus);
				auditPMReqDTO.setObjList(objList);
			}
			if (managedBeans.getPmCriteriaOld().getPmCriteriaDescs() != null) {
				for (PmCriteriaDesc descOld : managedBeans.getPmCriteriaOld().getPmCriteriaDescs()) {
					for (PmCriteriaDesc desc : managedBeans.getSelectedCriteria().getPmCriteriaDescs()) {
						if (descOld.getCriteriaDescId() == desc.getCriteriaDescId()
								&& descOld.getCriteria() != desc.getCriteria()) {
							if (descOld.getLanguageCode().equals("en")) {
								AuditPMSPDTO langCodeEn = new AuditPMSPDTO();
								langCodeEn.setRoleCode(roleCode);
								langCodeEn.setDoctype(AuditConst.PM_AUD_SUPPPERFCRITERIA);
								langCodeEn.setPmParameterLvlId(AuditConst.PM_AUD_SUPPPERFCRITERIA_PMPARAMETERLVLID);
								langCodeEn.setDocNo(managedBeans.getSelectedCriteria().getCriteriaCode());
								langCodeEn.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
								langCodeEn.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPCM);
								langCodeEn.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPCM);
								langCodeEn.setDocId(BigInteger.valueOf(descOld.getCriteriaDescId()));
								langCodeEn.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedCriteria()
										.getCriteriaId()));
								langCodeEn.setFieldLabelCode(AuditConst.PM_AUD_SUPPPERFCRITERIA_EN_FIELDLABELID);
								langCodeEn.setNewValue(desc.getCriteria());
								langCodeEn.setOldValue(descOld.getCriteria());
								langCodeEn.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
								objList.add(langCodeEn);
								auditPMReqDTO.setObjList(objList);
							}

							if (descOld.getLanguageCode().equals("ms")) {
								AuditPMSPDTO langCodeBM = new AuditPMSPDTO();
								langCodeBM.setRoleCode(roleCode);
								langCodeBM.setDoctype(AuditConst.PM_AUD_SUPPPERFCRITERIA);
								langCodeBM.setPmParameterLvlId(AuditConst.PM_AUD_SUPPPERFCRITERIA_PMPARAMETERLVLID);
								langCodeBM.setDocNo(managedBeans.getSelectedCriteria().getCriteriaCode());
								langCodeBM.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
								langCodeBM.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPCM);
								langCodeBM.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPCM);
								langCodeBM.setDocId(BigInteger.valueOf(descOld.getCriteriaDescId()));
								langCodeBM.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedCriteria()
										.getCriteriaId()));
								langCodeBM.setFieldLabelCode(AuditConst.PM_AUD_SUPPPERFCRITERIA_BM_FIELDLABELID);
								langCodeBM.setNewValue(desc.getCriteria());
								langCodeBM.setOldValue(descOld.getCriteria());
								langCodeBM.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
								objList.add(langCodeBM);
								auditPMReqDTO.setObjList(objList);
							}
						}
					}
				}
			}

			managedBeans.setPmCriteriaOld(null);
		} else {
			detailObjStatus.setDocId(BigInteger.valueOf(managedBeans.getSelectedCriteria().getCriteriaId()));
			detailObjStatus.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedCriteria().getCriteriaId()));
			detailObjStatus.setFieldLabelCode(AuditConst.PM_AUD_SUPPPERFCRITERIA_STATUS_FIELDLABELID);
			detailObjStatus
					.setNewValue(managedBeans.getSelectedCriteria().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
			detailObjStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(detailObjStatus);
			auditPMReqDTO.setObjList(objList);

			if (managedBeans.getSelectedCriteria().getPmCriteriaDescs() != null) {
				for (PmCriteriaDesc desc : managedBeans.getSelectedCriteria().getPmCriteriaDescs()) {
					if (desc.getLanguageCode().equals("en")) {
						AuditPMSPDTO langCodeEn = new AuditPMSPDTO();
						langCodeEn.setRoleCode(roleCode);
						langCodeEn.setDoctype(AuditConst.PM_AUD_SUPPPERFCRITERIA);
						langCodeEn.setPmParameterLvlId(AuditConst.PM_AUD_SUPPPERFCRITERIA_PMPARAMETERLVLID);
						langCodeEn.setDocNo(managedBeans.getSelectedCriteria().getCriteriaCode());
						langCodeEn.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
						langCodeEn.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPCM);
						langCodeEn.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPCM);
						langCodeEn.setDocId(BigInteger.valueOf(desc.getCriteriaDescId()));
						langCodeEn
								.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedCriteria().getCriteriaId()));
						langCodeEn.setFieldLabelCode(AuditConst.PM_AUD_SUPPPERFCRITERIA_EN_FIELDLABELID);
						langCodeEn.setNewValue(desc.getCriteria());
						langCodeEn.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(langCodeEn);
						auditPMReqDTO.setObjList(objList);
					}

					if (desc.getLanguageCode().equals("ms")) {
						AuditPMSPDTO langCodeBM = new AuditPMSPDTO();
						langCodeBM.setRoleCode(roleCode);
						langCodeBM.setDoctype(AuditConst.PM_AUD_SUPPPERFCRITERIA);
						langCodeBM.setPmParameterLvlId(AuditConst.PM_AUD_SUPPPERFCRITERIA_PMPARAMETERLVLID);
						langCodeBM.setDocNo(managedBeans.getSelectedCriteria().getCriteriaCode());
						langCodeBM.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPCM);
						langCodeBM.setLvlCode2(AuditConst.PM_LVL_CODE_2_SPCM);
						langCodeBM.setLvlCode3(AuditConst.PM_LVL_CODE_3_SPCM);
						langCodeBM.setDocId(BigInteger.valueOf(desc.getCriteriaDescId()));
						langCodeBM
								.setDocGroupId(BigInteger.valueOf(managedBeans.getSelectedCriteria().getCriteriaId()));
						langCodeBM.setFieldLabelCode(AuditConst.PM_AUD_SUPPPERFCRITERIA_BM_FIELDLABELID);
						langCodeBM.setNewValue(desc.getCriteria());
						langCodeBM.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(langCodeBM);
						auditPMReqDTO.setObjList(objList);
					}
				}
			}
		}
		return auditPMReqDTO;
	}
}