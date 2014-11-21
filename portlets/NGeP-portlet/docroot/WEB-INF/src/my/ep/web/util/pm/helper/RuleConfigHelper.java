package my.ep.web.util.pm.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.general.settings.PmConfigRule;
import my.ep.persistence.entity.pm.general.settings.PmConfigRuleValue;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * Singleton Helper For RuleConfig.
 * 
 * @author Zeinab
 * @since 20.11.2014
 * 
 */
public class RuleConfigHelper {
	private static Logger log = EpLogger.getLogger(RoleHelper.class);

	public static final String KEY_SEPARATOR = "_";

	private static RuleConfigHelper instance;
	private static Map<String, List<PmConfigRule>> ruleTypeMap = new HashMap<String, List<PmConfigRule>>();
	private static Map<String, List<PmConfigRuleValue>> ruleTypeValueMap = new HashMap<String, List<PmConfigRuleValue>>();

	private RuleConfigHelper() {
	}

	/**
	 * Get singleton of RuleConfigHelper
	 * 
	 * @return RuleConfigHelper singleton
	 */
	public static RuleConfigHelper getInstance() {
		if (instance == null) {
			instance = new RuleConfigHelper();
		}
		return instance;
	}

	/**
	 * Get a list of rule Types
	 * 
	 * @return list of PmConfigRule
	 */
	public List<PmConfigRule> getRuleTypeList() {
		debugLogger("getRuleTypeList");

		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		String key = languageCode;
		debugLogger("key: " + key);

		// get the ruleTypeList from map.
		List<PmConfigRule> ruleTypelist = ruleTypeMap.get(key);

		debugLogger("ruleTypelist: " + ruleTypelist);

		if (ruleTypelist == null) {
			// get from db
			ruleTypelist = getPmConfigRuleTypeListFromDB(key);

		}

		return ruleTypelist;

	}

	/**
	 * Get a list of rule Type values by ruleType
	 * 
	 * @return list of PmConfigRuleValue
	 */
	public List<PmConfigRuleValue> getRuleTypeValueListByRuleType(
			String ruleType) {
		debugLogger("getRuleTypeValueListByRuleType: ruleType :" + ruleType);

		String key = ruleType;
		debugLogger("key: " + key);

		// get the ruleTypeValueList from map.
		List<PmConfigRuleValue> ruleTypeValuelist = ruleTypeValueMap.get(key);

		debugLogger("ruleTypeValuelist: " + ruleTypeValuelist);

		if (ruleTypeValuelist == null) {
			// get from db
			ruleTypeValuelist = getPmRuleTypeValuesByRuleTypeFromDB(ruleType);
		}

		return ruleTypeValuelist;

	}

	private List<PmConfigRule> getPmConfigRuleTypeListFromDB(String languageCode) {
		List<PmConfigRule> list = null;
		IRuleSO ruleSO = null;
		try {
			// get from db
			ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
					"pm", IRuleSO.class);
			RuleReqDTO reqRDTO = new RuleReqDTO();
			reqRDTO.setLanguageCode(languageCode);
			RuleResDTO resRDTO = ruleSO.getRuleTypes(reqRDTO);
			list = resRDTO.getRuleTypeList();

			ruleTypeMap.put(languageCode, list);
			debugLogger("put into map: " + list);

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			ruleSO = null;
		}

		return list;
	}

	private List<PmConfigRuleValue> getPmRuleTypeValuesByRuleTypeFromDB(
			String ruleType) {
		List<PmConfigRuleValue> list = null;
		IRuleSO ruleSO = null;
		try {

			ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
					"pm", IRuleSO.class);
			RuleReqDTO reqRDTO = new RuleReqDTO();
			reqRDTO.setRuleType(ruleType);
			RuleResDTO resRDTO = ruleSO.getRuleValuesByRuleType(reqRDTO);
			list = resRDTO.getRuleValueList();
			ruleTypeValueMap.put(ruleType, list);
			debugLogger("put into map: " + list);

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			ruleSO = null;
		}

		return list;
	}
	
	public void relodeRuleTypeValueMap(){
		ruleTypeValueMap.clear();
		List<PmConfigRule> ruleTypeList = getRuleTypeList();
		for(PmConfigRule rule : ruleTypeList){
			getRuleTypeValueListByRuleType(rule.getRuleType());
		}
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

}
