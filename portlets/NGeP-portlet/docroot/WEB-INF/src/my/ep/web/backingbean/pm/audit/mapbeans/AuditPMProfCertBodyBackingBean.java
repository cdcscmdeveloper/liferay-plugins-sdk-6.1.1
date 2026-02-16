package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.util.ArrayList;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.pm.profcertbody.ProfCertifiedBodyManagedBean;

/**
 * AuditProfCertBodyBackingBean
 * 
 * @author haiza 28895_35509
 * 
 */
public class AuditPMProfCertBodyBackingBean extends AuditPMBackingBeanTemplate {

	private static final long serialVersionUID = 1L;
	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO dtlObjProfessional = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjCertified = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjName = new AuditPMSPDTO();
	AuditPMSPDTO dtlObjGLP = new AuditPMSPDTO();
	AuditPMSPDTO dtlStatus = new AuditPMSPDTO();
	AuditPMSPDTO detailAll = new AuditPMSPDTO();

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleProfCertBody();
	}

	/**
	 * @param args
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {

		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		ProfCertifiedBodyManagedBean managedBeans = (ProfCertifiedBodyManagedBean) manageBean;

		String docType = managedBeans.getCertBody().getBodyName();
		
		dtlObjName.setRoleCode(roleCode);
		dtlObjName.setDocType(docType);
		dtlObjName.setPmParameterLvlId(AuditConst.PM_AUD_CERT_BODY_PMPARAMETERLVLID);
		dtlObjName.setDocNo(managedBeans.getCertBody().getBodyCode());
		dtlObjName.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlObjName.setLvlCode2(AuditConst.PM_LVL_CODE_2_PCB);
		dtlObjName.setLvlCode3(AuditConst.PM_LVL_CODE_3_PCB);
		dtlObjName.setDocId(BigInteger.valueOf(managedBeans.getCertBody().getCertBodyId()));
		dtlObjName.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_NAME_FIELDLABELID);

		dtlStatus.setRoleCode(roleCode);
		dtlStatus.setDocType(docType);
		dtlStatus.setPmParameterLvlId(AuditConst.PM_AUD_CERT_BODY_PMPARAMETERLVLID);
		dtlStatus.setDocNo(managedBeans.getCertBody().getBodyCode());
		dtlStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_PCB);
		dtlStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_PCB);
		dtlStatus.setDocId(BigInteger.valueOf(managedBeans.getCertBody().getCertBodyId()));
		dtlStatus.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_STATUS_FIELDLABELID);

		dtlObjGLP.setRoleCode(roleCode);
		dtlObjGLP.setDocType(docType);
		dtlObjGLP.setPmParameterLvlId(AuditConst.PM_AUD_CERT_BODY_PMPARAMETERLVLID);
		dtlObjGLP.setDocNo(managedBeans.getCertBody().getBodyCode());
		dtlObjGLP.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlObjGLP.setLvlCode2(AuditConst.PM_LVL_CODE_2_PCB);
		dtlObjGLP.setLvlCode3(AuditConst.PM_LVL_CODE_3_PCB);
		dtlObjGLP.setDocId(BigInteger.valueOf(managedBeans.getCertBody().getCertBodyId()));
		dtlObjGLP.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_GLP_FIELDLABELID);

		dtlObjCertified.setRoleCode(roleCode);
		dtlObjCertified.setDocType(docType);
		dtlObjCertified.setPmParameterLvlId(AuditConst.PM_AUD_CERT_BODY_PMPARAMETERLVLID);
		dtlObjCertified.setDocNo(managedBeans.getCertBody().getBodyCode());
		dtlObjCertified.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlObjCertified.setLvlCode2(AuditConst.PM_LVL_CODE_2_PCB);
		dtlObjCertified.setLvlCode3(AuditConst.PM_LVL_CODE_3_PCB);
		dtlObjCertified.setDocId(BigInteger.valueOf(managedBeans.getCertBody().getCertBodyId()));
		dtlObjCertified.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_CERT_FIELDLABELID);

		dtlObjProfessional.setRoleCode(roleCode);
		dtlObjProfessional.setDocType(docType);
		dtlObjProfessional.setPmParameterLvlId(AuditConst.PM_AUD_CERT_BODY_PMPARAMETERLVLID);
		dtlObjProfessional.setDocNo(managedBeans.getCertBody().getBodyCode());
		dtlObjProfessional.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlObjProfessional.setLvlCode2(AuditConst.PM_LVL_CODE_2_PCB);
		dtlObjProfessional.setLvlCode3(AuditConst.PM_LVL_CODE_3_PCB);
		dtlObjProfessional.setDocId(BigInteger.valueOf(managedBeans.getCertBody().getCertBodyId()));
		dtlObjProfessional.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_PROF_FIELDLABELID);

		if (managedBeans.getOldAuditPmCertBody() != null
				&& managedBeans.getOldAuditPmCertBody().getCertBodyId() != null) {
			updateOption(managedBeans, auditPMReqDTO);

		} else {

			dtlObjName.setNewValue(managedBeans.getCertBody().getDescription());
			dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlObjName);
			auditPMReqDTO.setObjList(objList);

			dtlObjGLP.setNewValue(managedBeans.getCertBody().isGlp() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			dtlObjGLP.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlObjGLP);
			auditPMReqDTO.setObjList(objList);

			dtlObjCertified.setNewValue(managedBeans.getCertBody().isCertifiedBody() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			dtlObjCertified.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlObjCertified);
			auditPMReqDTO.setObjList(objList);

			dtlObjProfessional.setNewValue(managedBeans.getCertBody().isProfessionalBody() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			dtlObjProfessional.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlObjProfessional);
			auditPMReqDTO.setObjList(objList);

			dtlStatus.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_STATUS_FIELDLABELID);
			dtlStatus.setNewValue(managedBeans.getCertBody().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			dtlStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
			objList.add(dtlStatus);
			auditPMReqDTO.setObjList(objList);

		}
		managedBeans.setOldAuditPmCertBody(null);

		return auditPMReqDTO;
	}

	private void updateOption(ProfCertifiedBodyManagedBean managedBeans, AuditPMReqDTO auditPMReqDTO) {

		if (managedBeans.getOldAuditPmCertBody().getDescription() != null
				&& !managedBeans.getOldAuditPmCertBody().getDescription()
						.equalsIgnoreCase(managedBeans.getCertBody().getDescription())) {

			dtlObjName.setOldValue(managedBeans.getOldAuditPmCertBody().getDescription());
			dtlObjName.setNewValue(managedBeans.getCertBody().getDescription());
			dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlObjName);
			auditPMReqDTO.setObjList(objList);
		}

		if (!managedBeans.getCertBody().getRecordStatusFlg().equals(managedBeans.getOldAuditPmCertBody()
				.getRecordStatusFlg())) {

			dtlStatus
					.setOldValue(managedBeans.getOldAuditPmCertBody().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
							: AuditConst.PM_AUD_INACTIVE);
			dtlStatus.setNewValue(managedBeans.getCertBody().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			dtlStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlStatus);
			auditPMReqDTO.setObjList(objList);
		}

		updateOthers(managedBeans, auditPMReqDTO);
	}

	private void updateOthers(ProfCertifiedBodyManagedBean managedBeans, AuditPMReqDTO auditPMReqDTO) {
		if (managedBeans.getCertBody().isCertifiedBody() != managedBeans.getOldAuditPmCertBody().isCertifiedBody()) {

			dtlObjCertified.setOldValue(managedBeans.getOldAuditPmCertBody().isCertifiedBody() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			dtlObjCertified.setNewValue(managedBeans.getCertBody().isCertifiedBody() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			dtlObjCertified.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlObjCertified);
			auditPMReqDTO.setObjList(objList);

		}
		if (managedBeans.getCertBody().isProfessionalBody() != managedBeans.getOldAuditPmCertBody()
				.isProfessionalBody()) {

			dtlObjProfessional
					.setOldValue(managedBeans.getOldAuditPmCertBody().isProfessionalBody() ? AuditConst.PM_AUD_YES
							: AuditConst.PM_AUD_NO);
			dtlObjProfessional.setNewValue(managedBeans.getCertBody().isProfessionalBody() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			dtlObjProfessional.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlObjProfessional);
			auditPMReqDTO.setObjList(objList);

		}

		if (managedBeans.getCertBody().isGlp() != managedBeans.getOldAuditPmCertBody().isGlp()) {

			dtlObjGLP.setOldValue(managedBeans.getOldAuditPmCertBody().isGlp() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			dtlObjGLP.setNewValue(managedBeans.getCertBody().isGlp() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			dtlObjGLP.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlObjGLP);
			auditPMReqDTO.setObjList(objList);

		}

	}
}
