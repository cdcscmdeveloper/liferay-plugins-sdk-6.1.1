package my.ep.web.backingbean.sm.softcertprovider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.springframework.util.ObjectUtils;

import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.common.audit.IAuditPMSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.AuditConst;
import my.ep.constant.pm.ParameterTypeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.constant.DbValueConst;
import my.ep.persistence.entity.pm.common.PmParameter;
import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRule;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.type.aut.SearchOperator;
import my.ep.util.MessageUtils;
import my.ep.util.ResourceBundleUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.backingbean.pm.audit.AuditPMBackingBeanTemplate;
import my.ep.web.backingbean.pm.audit.mapbeans.AuditPMSoftCertBackingBean;
import my.ep.web.managedbean.sm.softcertprovider.SoftCertProviderManagedBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.ParameterHelper;
import my.ep.web.util.pm.helper.RuleConfigHelper;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.util.sm.helper.SmServiceLookup;

@ManagedBean(name = "softCertProviderBB")
@RequestScoped
public class SoftCertProviderBackingBean extends BaseBackingBean<SoftCertProviderManagedBean>{
	private static final long serialVersionUID = 1L;
	private static final String SOFT_CERT_PROVIDER_MAIN = "/xhtml/sm/softCertProvider/softCertProviderMain.xhtml";
	public static Logger log = Logger.getLogger(SoftCertProviderBackingBean.class);
	
	public void preRender(){
	/*	RuleReqDTO ruleReqDTO = new RuleReqDTO();
		ruleReqDTO.setRuleType(RuleTypeConst.SOFT_CERT_PROVIDER);*/
		getSoftCertConfigRule();
		
		
		getManagedBean().setRuleType(RuleConfigHelper.getInstance().getPmConfigRuleTypeObj(RuleTypeConst.SOFT_CERT_PROVIDER));
		
		//getManagedBean().setRuleValueLs(ruleResDTO.getRuleValueList());
		getManagedBean().setProviderLs(ParameterHelper.getInstance().getParameterDescList(ParameterTypeConst.SOFTCERT_PROVIDER));
		log.info("<<<<<ProviderLs>>>>>>" + getManagedBean().getProviderLs().size());
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication()
				.getNavigationHandler()
				.handleNavigation(fc, null,SOFT_CERT_PROVIDER_MAIN);
	}
	
	public void addSoftCertProviderRule(){
		PmConfigRuleValue ruleValue = new PmConfigRuleValue();
		ruleValue.setEffDate(getMinEffDate());
		ruleValue.setRuleType(getManagedBean().getRuleType());
	
		getManagedBean().getConfigRule().getConfigRuleValues().add(ruleValue);
	}
	
	public void saveSoftCertProvider(){
		PmConfigRule configRule = getManagedBean().getConfigRule();
		if(getManagedBean().getConfigRule().getConfigRuleValues().size() > 0){
			for(int i = 0; i < configRule.getConfigRuleValues().size(); i++){
				PmConfigRuleValue baseValue= configRule.getConfigRuleValues().get(i);
				
				for(int j = i+1; j < configRule.getConfigRuleValues().size(); j++){
					PmConfigRuleValue value = configRule.getConfigRuleValues().get(j);
					//boolean test = isValidDateRange(baseValue, value);
					boolean test = !isDateOverlaped(baseValue.getEffDate(),
							baseValue.getExpDate(), value.getEffDate(),
							value.getExpDate());
					if(!test){
						throw new EpException(
								MessageConst.ERROR_770109,
								new String[] {ResourceBundleUtils.getMessage("lbl.sm.softcert.provider",
										ResourceBundleUtils.BUNDLE_COMMON_LABELS, new FacesScopes().getUserLocale())});
					}
				}
			}
			
			RuleReqDTO reqDTO = new RuleReqDTO();
			List<PmConfigRule> configRuleLs = new ArrayList<PmConfigRule>();
			configRuleLs.add(getManagedBean().getConfigRule());
			reqDTO.setRuleTypeList(configRuleLs);
			
			/************ AUDIT START #28895-35509 *************/
			AuditPMBackingBeanTemplate auTemplate = new AuditPMSoftCertBackingBean();
			IAuditPMSO pmauditSO = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IAuditPMSO.class);

			if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_SOFT_CERT)
					&& !ObjectUtils.isEmpty(getManagedBean().getConfigRule().getConfigRuleValues().toArray())) {
				List<PmConfigRuleValue> oldRuleValue = SmServiceLookup.getRuleSO().getExistingRuleValue(
						RuleTypeConst.SOFT_CERT_PROVIDER);

				getManagedBean().setExistingConfigValueMap(new HashMap<Long, PmConfigRuleValue>());
				setOldRuleHashMap(oldRuleValue);
			}
			
			RuleResDTO resDto = SmServiceLookup.getRuleSO().updateRuleValueList(reqDTO);
			
			if(resDto.isSuccessful()){
				this.getManagedBean().setNewRuleValue(resDto.getNewRuleTypeList().get(0).getConfigRuleValues());
				if (pmauditSO.isAuditConfig(AuditConst.PM_AUD_CONF_SOFT_CERT)) {
					auTemplate.auditSaveData(getManagedBean(), AuditConst.PM_AUD_SOFT_CERT);
				}
			}
			/************ AUDIT END #28895-35509 *************/
			
			FacesContext facesContext = FacesContext.getCurrentInstance();
			facesContext.addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, null,
							MessageUtils.getMessage(
									MessageConst.INFO_770009,
									MessageUtils.BUNDLE_COMMON_MESSAGES,
									new FacesScopes().getUserLocale())));
		}
		
	}
	/************ AUDIT START #28895-35509 *************/
	/**
	 * @param List<PmConfigRuleValue>
	 * 
	 */
	private void setOldRuleHashMap(List<PmConfigRuleValue> oldRuleValue){
		if (!ObjectUtils.isEmpty(oldRuleValue.toArray())) {
			for (PmConfigRuleValue ruleValue : oldRuleValue) {
				PmConfigRuleValue currentRuleValue = (PmConfigRuleValue) ruleValue.cloneEntity();
				getManagedBean().getExistingConfigValueMap().put(currentRuleValue.getRuleValueId(),
						currentRuleValue);
			}
		}
	}
	/************ AUDIT END #28895-35509 *************/
	
	public void deleteSelectedRow(){
		if(getManagedBean().getDeleteRuleValue() != null){
			
			getManagedBean().getConfigRule().getConfigRuleValues().remove(getManagedBean().getDeleteRuleValue());
			getManagedBean().setDeleteRuleValue(null);
		}
	}
	
	/*public String getRuleValueDesc(){
		try {
			RuleReqDTO reqDTO = new RuleReqDTO();
			
			reqDTO.setRuleType(ruleType);			
			reqDTO.setLanguageCode(CommonConstants.DEFAULT_LANGUAGE_CODE);
			
			return SmServiceLookup.getRuleSO().getRuleDescByType(reqDTO);

		} finally {
			ruleSO = null;
		}
	}*/
	
	public String getParameterDesc(String code){
		PmParameterDesc desc = ParameterHelper.getInstance().
				getParameterDescByParamTypeAndParamCode(ParameterTypeConst.SOFTCERT_PROVIDER, code);
		
		return desc.getCodeName();
	}
	
	public Date getMinEffDate() {
		Calendar c = new GregorianCalendar();
		c.add(Calendar.DATE, 1);
		Date d = c.getTime();
		return d;
	}
	
	public Date getMinExpDate(Date effDate) {
		if(effDate != null){
			Calendar c = new GregorianCalendar();
			c.setTime(effDate);
			c.add(Calendar.DATE, 1);
			Date d = c.getTime();
			return d;
		}
		
		return null;
	}
	
	private void getSoftCertConfigRule(){
		List<PmConfigRule> list = null;
		IRuleSO ruleSO = null;
		try {
			// get from db
			ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
			RuleReqDTO reqDTO = new RuleReqDTO();
			reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
			SearchBean<PmConfigRule> searchBean = new SearchBean<PmConfigRule>();
			// Filter By - status
			searchBean.getDataFilterBeans().add(
				new SearchFilterBean("recordStatus",
				SearchOperator.OPERATOR_EQUALS.value(), 
				DbValueConst.RECORD_STATUS_ACTIVE));
			
			searchBean.getDataFilterBeans().add(
					new SearchFilterBean("ruleType",
					SearchOperator.OPERATOR_EQUALS.value(), 
					RuleTypeConst.SOFT_CERT_PROVIDER));
			reqDTO.setSearchBean(searchBean);
			
			RuleResDTO resDTO = ruleSO.getRuleTypes(reqDTO);
			log.info(">>>>>>>>>>>>resDTO.getResultBean().getResultList().size()>>>>>>>>>>>" + resDTO.getResultBean().getResultList().size());
			if(resDTO.getResultBean().getResultList().size() > 0){
				getManagedBean().setConfigRule(resDTO.getResultBean().getResultList().get(0));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			ruleSO = null;
		}
	}
	
	private boolean isDateOverlaped(Date start1, Date end1, Date start2, Date end2) {
		log.info("..........isDateOverlaped............");
		log.info("..........start1=" + start1 + "......end1=" + end1 + "......");
		log.info("..........start2=" + start2 + "......end2=" + end2 + "......");
		if (end1 == null && end2 == null) {

			return true;
		}
		if (end1 == null) {
			return start1.getTime() <= end2.getTime();

		}
		if (end2 == null) {
			return start2.getTime() <= end1.getTime();

		}
		return start1.getTime() <= end2.getTime()
				&& start2.getTime() <= end1.getTime();
	}
}
