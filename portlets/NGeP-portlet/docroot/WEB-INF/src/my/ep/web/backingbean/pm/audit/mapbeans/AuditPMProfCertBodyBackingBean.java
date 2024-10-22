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
		/**
		 * PmCertBody oldCertBody = managedBeans.getOldAuditPmCertBody();
		 * PmCertBody newCertBody = managedBeans.getCertBody();
		 **/

		/**
		 * dtlObjProfessional.setRoleCode(roleCode);
		 * dtlObjProfessional.setDocType(AuditConst.PM_AUD_CERT_BODY);
		 * dtlObjProfessional
		 * .setPmParameterLvlId(AuditConst.PM_AUD_CERT_BODY_PMPARAMETERLVLID);
		 * dtlObjProfessional
		 * .setDocNo(managedBeans.getCertBody().getBodyCode());
		 * dtlObjProfessional.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		 * dtlObjProfessional.setLvlCode2(AuditConst.PM_LVL_CODE_2_PCB);
		 * dtlObjProfessional.setLvlCode3(AuditConst.PM_LVL_CODE_3_PCB);
		 * 
		 * dtlObjCertified.setRoleCode(roleCode);
		 * dtlObjCertified.setDocType(AuditConst.PM_AUD_CERT_BODY);
		 * dtlObjCertified
		 * .setPmParameterLvlId(AuditConst.PM_AUD_CERT_BODY_PMPARAMETERLVLID);
		 * dtlObjCertified.setDocNo(managedBeans.getCertBody().getBodyCode());
		 * dtlObjCertified.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		 * dtlObjCertified.setLvlCode2(AuditConst.PM_LVL_CODE_2_PCB);
		 * dtlObjCertified.setLvlCode3(AuditConst.PM_LVL_CODE_3_PCB);
		 **/

		dtlObjName.setRoleCode(roleCode);
		dtlObjName.setDocType(AuditConst.PM_AUD_CERT_BODY);
		dtlObjName.setPmParameterLvlId(AuditConst.PM_AUD_CERT_BODY_PMPARAMETERLVLID);
		dtlObjName.setDocNo(managedBeans.getCertBody().getBodyCode());
		dtlObjName.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlObjName.setLvlCode2(AuditConst.PM_LVL_CODE_2_PCB);
		dtlObjName.setLvlCode3(AuditConst.PM_LVL_CODE_3_PCB);

		/**
		 * dtlObjGLP.setRoleCode(roleCode);
		 * dtlObjGLP.setDocType(AuditConst.PM_AUD_CERT_BODY);
		 * dtlObjGLP.setPmParameterLvlId
		 * (AuditConst.PM_AUD_CERT_BODY_PMPARAMETERLVLID);
		 * dtlObjGLP.setDocNo(managedBeans.getCertBody().getBodyCode());
		 * dtlObjGLP.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		 * dtlObjGLP.setLvlCode2(AuditConst.PM_LVL_CODE_2_PCB);
		 * dtlObjGLP.setLvlCode3(AuditConst.PM_LVL_CODE_3_PCB);
		 */

		dtlStatus.setRoleCode(roleCode);
		dtlStatus.setDocType(AuditConst.PM_AUD_CERT_BODY);
		dtlStatus.setPmParameterLvlId(AuditConst.PM_AUD_CERT_BODY_PMPARAMETERLVLID);
		dtlStatus.setDocNo(managedBeans.getCertBody().getBodyCode());
		dtlStatus.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlStatus.setLvlCode2(AuditConst.PM_LVL_CODE_2_PCB);
		dtlStatus.setLvlCode3(AuditConst.PM_LVL_CODE_3_PCB);

		if (managedBeans.getOldAuditPmCertBody() != null && managedBeans.getOldAuditPmCertBody().getCertBodyId() != null) {
			updateOption(managedBeans, auditPMReqDTO, roleCode);

			if (managedBeans.getOldAuditPmCertBody().getBodyName() != null
					&& !managedBeans.getOldAuditPmCertBody().getBodyName()
							.equalsIgnoreCase(managedBeans.getCertBody().getBodyName())) {
				dtlObjName.setDocId(BigInteger.valueOf(managedBeans.getCertBody().getCertBodyId()));
				dtlObjName.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_NAME_FIELDLABELID);
				dtlObjName.setOldValue(managedBeans.getOldAuditPmCertBody().getBodyName());
				dtlObjName.setNewValue(managedBeans.getCertBody().getBodyName());
				dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(dtlObjName);
				auditPMReqDTO.setObjList(objList);

			}

			if (managedBeans.getCertBody().getRecordStatusFlg() != managedBeans.getOldAuditPmCertBody().getRecordStatusFlg()) {
				dtlStatus.setDocId(BigInteger.valueOf(managedBeans.getCertBody().getCertBodyId()));
				dtlStatus.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_STATUS_FIELDLABELID);
				dtlStatus.setOldValue(managedBeans.getOldAuditPmCertBody().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				dtlStatus.setNewValue(managedBeans.getCertBody().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
						: AuditConst.PM_AUD_INACTIVE);
				dtlStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
				objList.add(dtlStatus);
				auditPMReqDTO.setObjList(objList);
			}

		} else {
			dtlObjName.setDocId(BigInteger.valueOf(managedBeans.getCertBody().getCertBodyId()));
			dtlObjName.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_NAME_FIELDLABELID);
			dtlObjName.setNewValue(managedBeans.getCertBody().getBodyName());
			dtlObjName.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlObjName);
			auditPMReqDTO.setObjList(objList);

			dtlObjGLP.setDocId(BigInteger.valueOf(managedBeans.getCertBody().getCertBodyId()));
			dtlObjGLP.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_GLP_FIELDLABELID);
			dtlObjGLP.setNewValue(managedBeans.getCertBody().isGlp() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO); // PM_AUD_YES
																														// PM_AUD_NO
			dtlObjGLP.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlObjGLP);
			auditPMReqDTO.setObjList(objList);

			dtlStatus.setDocId(BigInteger.valueOf(managedBeans.getCertBody().getCertBodyId()));
			dtlStatus.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_STATUS_FIELDLABELID);
			dtlStatus.setNewValue(managedBeans.getCertBody().getRecordStatusFlg() == 1 ? AuditConst.PM_AUD_ACTIVE
					: AuditConst.PM_AUD_INACTIVE);
			dtlStatus.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlStatus);
			auditPMReqDTO.setObjList(objList);
		}
		managedBeans.setOldAuditPmCertBody(null);

		return auditPMReqDTO;
	}

	private void updateOption(ProfCertifiedBodyManagedBean managedBeans, AuditPMReqDTO auditPMReqDTO, String roleCode) {

		if (managedBeans.getCertBody().isCertifiedBody() != managedBeans.getOldAuditPmCertBody().isCertifiedBody()) {
			dtlObjCertified.setRoleCode(roleCode);
			dtlObjCertified.setDocType(AuditConst.PM_AUD_CERT_BODY);
			dtlObjCertified.setPmParameterLvlId(AuditConst.PM_AUD_CERT_BODY_PMPARAMETERLVLID);
			dtlObjCertified.setDocNo(managedBeans.getCertBody().getBodyCode());
			dtlObjCertified.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
			dtlObjCertified.setLvlCode2(AuditConst.PM_LVL_CODE_2_PCB);
			dtlObjCertified.setLvlCode3(AuditConst.PM_LVL_CODE_3_PCB);
			dtlObjCertified.setDocId(BigInteger.valueOf(managedBeans.getCertBody().getCertBodyId()));
			dtlObjCertified.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_CERT_FIELDLABELID);
			dtlObjCertified.setOldValue(managedBeans.getOldAuditPmCertBody().isGlp() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			dtlObjCertified.setNewValue(managedBeans.getCertBody().isGlp() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO); // PM_AUD_YES
																															// PM_AUD_NO
			dtlObjCertified.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlObjCertified);
			auditPMReqDTO.setObjList(objList);

		}
		if (managedBeans.getCertBody().isProfessionalBody() != managedBeans.getOldAuditPmCertBody().isProfessionalBody()) {
			dtlObjProfessional.setRoleCode(roleCode);
			dtlObjProfessional.setDocType(AuditConst.PM_AUD_CERT_BODY);
			dtlObjProfessional.setPmParameterLvlId(AuditConst.PM_AUD_CERT_BODY_PMPARAMETERLVLID);
			dtlObjProfessional.setDocNo(managedBeans.getCertBody().getBodyCode());
			dtlObjProfessional.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
			dtlObjProfessional.setLvlCode2(AuditConst.PM_LVL_CODE_2_PCB);
			dtlObjProfessional.setLvlCode3(AuditConst.PM_LVL_CODE_3_PCB);
			dtlObjProfessional.setDocId(BigInteger.valueOf(managedBeans.getCertBody().getCertBodyId()));
			dtlObjProfessional.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_PROF_FIELDLABELID);
			dtlObjProfessional.setOldValue(managedBeans.getOldAuditPmCertBody().isGlp() ? AuditConst.PM_AUD_YES
					: AuditConst.PM_AUD_NO);
			dtlObjProfessional.setNewValue(managedBeans.getCertBody().isGlp() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO); // PM_AUD_YES
																																// PM_AUD_NO
			dtlObjProfessional.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlObjProfessional);
			auditPMReqDTO.setObjList(objList);

		}

		if (managedBeans.getCertBody().isGlp() != managedBeans.getOldAuditPmCertBody().isGlp()) {
			dtlObjGLP.setRoleCode(roleCode);
			dtlObjGLP.setDocType(AuditConst.PM_AUD_CERT_BODY);
			dtlObjGLP.setPmParameterLvlId(AuditConst.PM_AUD_CERT_BODY_PMPARAMETERLVLID);
			dtlObjGLP.setDocNo(managedBeans.getCertBody().getBodyCode());
			dtlObjGLP.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
			dtlObjGLP.setLvlCode2(AuditConst.PM_LVL_CODE_2_PCB);
			dtlObjGLP.setLvlCode3(AuditConst.PM_LVL_CODE_3_PCB);
			dtlObjGLP.setDocId(BigInteger.valueOf(managedBeans.getCertBody().getCertBodyId()));
			dtlObjGLP.setFieldLabelCode(AuditConst.PM_AUD_CERT_BODY_GLP_FIELDLABELID);
			dtlObjGLP.setOldValue(managedBeans.getOldAuditPmCertBody().isGlp() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO);
			dtlObjGLP.setNewValue(managedBeans.getCertBody().isGlp() ? AuditConst.PM_AUD_YES : AuditConst.PM_AUD_NO); // PM_AUD_YES
																														// PM_AUD_NO
			dtlObjGLP.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
			objList.add(dtlObjGLP);
			auditPMReqDTO.setObjList(objList);

		}
	}

}
