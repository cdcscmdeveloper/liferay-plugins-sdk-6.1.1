package my.ep.web.managedbean.sm.softcertprovider;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.persistence.entity.pm.common.PmParameterDesc;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRule;
import my.ep.persistence.entity.pm.general.settings.rule.PmConfigRuleValue;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "softCertProviderMB")
@SessionScoped
public class SoftCertProviderManagedBean extends BaseManagedBean{
	private static final long serialVersionUID = 1L;

	private PmConfigRule configRule;
	private List<PmParameterDesc> providerLs;
	private PmConfigRuleValue deleteRuleValue;
	private PmConfigRule ruleType;

	public PmConfigRule getConfigRule() {
		return configRule;
	}

	public void setConfigRule(PmConfigRule configRule) {
		this.configRule = configRule;
	}

	public List<PmParameterDesc> getProviderLs() {
		return providerLs;
	}

	public void setProviderLs(List<PmParameterDesc> providerLs) {
		this.providerLs = providerLs;
	}

	public PmConfigRuleValue getDeleteRuleValue() {
		return deleteRuleValue;
	}

	public void setDeleteRuleValue(PmConfigRuleValue deleteRuleValue) {
		this.deleteRuleValue = deleteRuleValue;
	}

	public PmConfigRule getRuleType() {
		return ruleType;
	}

	public void setRuleType(PmConfigRule ruleType) {
		this.ruleType = ruleType;
	}

}
