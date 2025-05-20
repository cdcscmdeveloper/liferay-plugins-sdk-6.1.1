package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.role.AuditPMRoleBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
import my.ep.web.managedbean.sm.softcertprovider.SoftCertProviderManagedBean;

/**
 * AuditPMSoftCertBackingBean
 * 
 * @author haiza 28895_35509
 */
public class AuditPMSoftCertBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO detailProvider = new AuditPMSPDTO();
	AuditPMSPDTO detailEffDate = new AuditPMSPDTO();
	String pattern = "dd/MM/yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

	AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
	String roleCode;
	String docType;
	BigInteger docId;
	String actionType;

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleSoftCert();
	}

	/**
	 * @param managedBeans
	 * @param roleCode
	 * @return
	 */
	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {
		SoftCertProviderManagedBean managedBean = (SoftCertProviderManagedBean) manageBean;
		List<PmConfigRuleValue> configRuleValues = managedBean.getConfigRule().getConfigRuleValues();
		List<PmConfigRuleValue> listNew = managedBean.getNewRuleValue();
		this.roleCode = roleCode;
		for (PmConfigRuleValue ruleValue : configRuleValues) {
			if (ruleValue.isBrandNew()) {
				for (PmConfigRuleValue newRuleValue : listNew) {
					softCertUpdatelist(newRuleValue, ruleValue);
				}
			} else {
				PmConfigRuleValue oldValue = managedBean.getExistingConfigValueMap().get(ruleValue.getRuleValueId());
				String newDate = ruleValue.getExpDate() == null ? "" : simpleDateFormat.format(ruleValue.getExpDate());
				String oldDate = oldValue.getExpDate() == null ? "" : simpleDateFormat.format(oldValue.getExpDate());

				if (!(oldDate.equals(newDate))) {
					this.docType = checkSoftCert(ruleValue.getRuleValue());
					this.docId = BigInteger.valueOf(ruleValue.getRuleValueId());
					detailObj(AuditConst.PM_AUD_SCP_EXP_DATE_FIELDLABELCODE, oldDate, newDate,
							AuditConst.PM_AUD_ACTION_TYPE_UPD);
				}
			}
		}
		auditPMReqDTO.setObjList(objList);
		managedBean.setExistingConfigValueMap(null);
		managedBean.setNewRuleValue(null);
		return auditPMReqDTO;
	}

	private void softCertUpdatelist(PmConfigRuleValue newRuleValue, PmConfigRuleValue ruleValue) {
		if (newRuleValue.getEffDate().equals(ruleValue.getEffDate())) {
			this.docType = checkSoftCert(ruleValue.getRuleValue());
			this.docId = BigInteger.valueOf(newRuleValue.getRuleValueId());

			detailObj(AuditConst.PM_AUD_SCP_PROVIDER_FIELDLABELCODE, null, checkSoftCert(ruleValue.getRuleValue()),
					AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_SCP_EFF_DATE_FIELDLABELCODE, null,
					simpleDateFormat.format(ruleValue.getEffDate()), AuditConst.PM_AUD_ACTION_TYPE_INS);

			detailObj(AuditConst.PM_AUD_SCP_EXP_DATE_FIELDLABELCODE, null,
					ruleValue.getExpDate() != null ? simpleDateFormat.format(ruleValue.getExpDate()) : null,
					AuditConst.PM_AUD_ACTION_TYPE_INS);
		}
	}
	
	private String checkSoftCert(String parameter){
		 
		PmParameter pmParameter = getParameterByTypeAndCode(ParameterTypeConst.SOFTCERT_PROVIDER, parameter);
		String description = null;
		
		for(PmParameterDesc desc : pmParameter.getPmParameterDescsSpecCallOnly()){
			if (desc.getLanguageCode().equalsIgnoreCase(AuditConst.PM_LANGUAGECODE_MS)){
				description = desc.getCodeName();
			}
		}
		return description;
		
	}

	private ArrayList<AuditPMSPDTO> detailObj(BigInteger fieldLabelId, String oldValue, String newValue,
			String actionType) {
		AuditPMSPDTO dtlObj = new AuditPMSPDTO();
		dtlObj.setRoleCode(roleCode);
		dtlObj.setDocType(docType);
		dtlObj.setPmParameterLvlId(AuditConst.PM_AUD_SCP_PMPARAMETERLVLID);
		dtlObj.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
		dtlObj.setLvlCode2(AuditConst.PM_LVL_CODE_2_SCP);
		dtlObj.setLvlCode3(AuditConst.PM_LVL_CODE_3_SCP);
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
