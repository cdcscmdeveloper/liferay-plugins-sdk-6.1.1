package my.ep.web.backingbean.pm.audit.mapbeans;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import my.ep.constant.pm.AuditConst;
import my.ep.dto.pm.common.audit.AuditPMReqDTO;
import my.ep.dto.pm.common.audit.AuditPMSPDTO;
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
public class CopyOfAuditPMSoftCertBackingBean extends AuditPMBackingBeanTemplate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(CopyOfAuditPMSoftCertBackingBean.class);

	ArrayList<AuditPMSPDTO> objList = new ArrayList<>();
	AuditPMSPDTO detailProvider = new AuditPMSPDTO();
	AuditPMSPDTO detailEffDate = new AuditPMSPDTO();
	String pattern = "dd-MM-yyyy";
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

	@Override
	public String getRoleCode() {
		AuditPMRoleBackingBean auditPMRoleBackingBean = new AuditPMRoleBackingBean();
		return auditPMRoleBackingBean.roleSoftCert();
	}

	@Override
	public AuditPMReqDTO insert(BaseManagedBean manageBean, String roleCode) {

		AuditPMReqDTO auditPMReqDTO = new AuditPMReqDTO();
		SoftCertProviderManagedBean managedBean = (SoftCertProviderManagedBean) manageBean;
		List<PmConfigRuleValue> configRuleValues = managedBean.getConfigRule().getConfigRuleValues();
		List<PmConfigRuleValue> listNew = managedBean.getNewRuleValue();

		for (PmConfigRuleValue ruleValue : configRuleValues) {
			if (ruleValue.isBrandNew()) {

				for (PmConfigRuleValue newRuleValue : listNew) {

					if (newRuleValue.getEffDate().equals(ruleValue.getEffDate())) {
						detailProvider.setRoleCode(roleCode);
						detailProvider.setDocType(ruleValue.getRuleValue());
						detailProvider.setPmParameterLvlId(AuditConst.PM_AUD_SCP_PMPARAMETERLVLID);
						detailProvider.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
						detailProvider.setLvlCode2(AuditConst.PM_LVL_CODE_2_SCP);
						detailProvider.setLvlCode3(AuditConst.PM_LVL_CODE_3_SCP);
						detailProvider.setDocId(BigInteger.valueOf(newRuleValue.getRuleValueId()));
						detailProvider.setFieldLabelCode(AuditConst.PM_AUD_SCP_PROVIDER_FIELDLABELCODE);
						detailProvider.setNewValue(ruleValue.getRuleValue());
						detailProvider.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(detailProvider);

						detailEffDate.setRoleCode(roleCode);
						detailEffDate.setDocType(ruleValue.getRuleValue());
						detailEffDate.setPmParameterLvlId(AuditConst.PM_AUD_SCP_PMPARAMETERLVLID);
						detailEffDate.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
						detailEffDate.setLvlCode2(AuditConst.PM_LVL_CODE_2_SCP);
						detailEffDate.setLvlCode3(AuditConst.PM_LVL_CODE_3_SCP);
						detailEffDate.setDocId(BigInteger.valueOf(newRuleValue.getRuleValueId()));
						detailEffDate.setFieldLabelCode(AuditConst.PM_AUD_SCP_EFF_DATE_FIELDLABELCODE);
						detailEffDate.setNewValue(simpleDateFormat.format(ruleValue.getEffDate()));
						detailEffDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(detailEffDate);

						AuditPMSPDTO detailExpDate = new AuditPMSPDTO();
						detailExpDate.setRoleCode(roleCode);
						detailExpDate.setDocType(ruleValue.getRuleValue());
						detailExpDate.setPmParameterLvlId(AuditConst.PM_AUD_SCP_PMPARAMETERLVLID);
						detailExpDate.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
						detailExpDate.setLvlCode2(AuditConst.PM_LVL_CODE_2_SCP);
						detailExpDate.setLvlCode3(AuditConst.PM_LVL_CODE_3_SCP);
						detailExpDate.setDocId(BigInteger.valueOf(newRuleValue.getRuleValueId()));
						detailExpDate.setFieldLabelCode(AuditConst.PM_AUD_SCP_EXP_DATE_FIELDLABELCODE);
						detailExpDate
								.setNewValue(ruleValue.getExpDate() != null ? simpleDateFormat.format(ruleValue.getExpDate())
										: null);
						detailExpDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_INS);
						objList.add(detailExpDate);
						auditPMReqDTO.setObjList(objList);
					}
				}
			} else {
				PmConfigRuleValue oldValue = managedBean.getExistingConfigValueMap().get(ruleValue.getRuleValueId());
				String newDate = oldValue.getExpDate() != null ? simpleDateFormat.format(ruleValue.getExpDate()) : null;
				String oldDate = ruleValue.getExpDate() != null ? simpleDateFormat.format(oldValue.getExpDate()) : null;
				log.info(">>>>>>>>>>>>newDate>>>>>>>>>>>" + newDate);
				log.info(">>>>>>>>>>>>oldDate>>>>>>>>>>>" + oldDate);

				if (!oldValue.getExpDate().equals(ruleValue.getExpDate())) {
					AuditPMSPDTO detailExpDate = new AuditPMSPDTO();
					detailExpDate.setRoleCode(roleCode);
					detailExpDate.setDocType(ruleValue.getRuleValue());
					detailExpDate.setPmParameterLvlId(AuditConst.PM_AUD_SCP_PMPARAMETERLVLID);
					detailExpDate.setLvlCode1(AuditConst.PM_LVL_CODE_1_SPM);
					detailExpDate.setLvlCode2(AuditConst.PM_LVL_CODE_2_SCP);
					detailExpDate.setLvlCode3(AuditConst.PM_LVL_CODE_3_SCP);
					detailExpDate.setDocId(BigInteger.valueOf(ruleValue.getRuleValueId()));
					detailExpDate.setFieldLabelCode(AuditConst.PM_AUD_SCP_EXP_DATE_FIELDLABELCODE);
					detailExpDate.setOldValue(oldDate);
					detailExpDate.setNewValue(newDate);
					detailExpDate.setActionType(AuditConst.PM_AUD_ACTION_TYPE_UPD);
					objList.add(detailExpDate);

				}
				auditPMReqDTO.setObjList(objList);
			}
		}
		managedBean.setExistingConfigValueMap(null);
		managedBean.setNewRuleValue(null);
		return auditPMReqDTO;
	}
}
