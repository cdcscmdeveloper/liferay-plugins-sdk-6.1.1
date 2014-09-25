package my.ep.web.managedbean.pm.settings;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.apache.commons.lang.StringUtils;

import sun.util.logging.resources.logging;

import my.ep.biz.bean.search.ISearchBean;
import my.ep.biz.bean.search.ISearchResultBean;

import my.ep.biz.bean.search.SearchResultBean;

import my.ep.biz.service.pm.general.settings.rule.IRuleSO;

import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.persistence.entity.pm.common.PmConfigGeneral;
import my.ep.persistence.entity.pm.general.settings.PmConfigRule;
import my.ep.persistence.entity.pm.general.settings.PmConfigRuleValue;

import my.ep.web.bean.paginator.IPagingDataModel;
import my.ep.web.bean.paginator.PagingDataModel;
import my.ep.web.managedbean.BaseManagedBean;

import my.ep.web.util.lookupservice.BusinessDelegate;

@ManagedBean(name = "ruleCatalogueMB")
@SessionScoped
public class RuleCatalogueManageBean extends BaseManagedBean {
	private Integer ruleCatTabActiveIndex = 0;
	private PmConfigGeneral selectedConfigGeneral;
	private PmConfigRuleValue selectedRuleValue;
	private PmConfigRule selectedRuleType;
	private List<PmConfigRule> configRuleList;


	public Integer getRuleCatTabActiveIndex() {
		return ruleCatTabActiveIndex;
	}

	public void setRuleCatTabActiveIndex(Integer ruleCatTabActiveIndex) {
		this.ruleCatTabActiveIndex = ruleCatTabActiveIndex;
	}

	public PmConfigGeneral getSelectedConfigGeneral() {
		return selectedConfigGeneral;
	}

	public void setSelectedConfigGeneral(PmConfigGeneral selectedConfigGeneral) {
		this.selectedConfigGeneral = selectedConfigGeneral;
	}

	public PmConfigRule getSelectedRuleType() {
		
		return selectedRuleType;
	}

	public void setSelectedRuleType(PmConfigRule selectedRuleType) {
	
		this.selectedRuleType = selectedRuleType;
	}

	public List<PmConfigRule> getConfigRuleList() {
		return configRuleList;
	}

	public void setConfigRuleList(List<PmConfigRule> configRuleList) {
		this.configRuleList = configRuleList;
	}

	public PmConfigRuleValue getSelectedRuleValue() {
		return selectedRuleValue;
	}

	public void setSelectedRuleValue(PmConfigRuleValue selectedRuleValue) {
		this.selectedRuleValue = selectedRuleValue;
	}


}
