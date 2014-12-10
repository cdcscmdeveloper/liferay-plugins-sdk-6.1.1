package my.ep.web.util.pm.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.ep.biz.bean.search.ISearchResultBean;
import my.ep.biz.bean.search.SearchBean;
import my.ep.biz.bean.search.SearchFilterBean;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.general.settings.PmConfigRule;
import my.ep.persistence.entity.pm.general.settings.PmConfigRuleValue;
import my.ep.type.aut.SearchOperator;
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
	private static Map<String, PmConfigRule> ruleTypeObjMap = new HashMap<String, PmConfigRule>();
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
		log.info("getRuleTypeValueListByRuleType: ruleType :" + ruleType);

		String key = ruleType;
		log.info("key: " + key);

		// get the ruleTypeValueList from map.
		List<PmConfigRuleValue> ruleTypeValuelist = ruleTypeValueMap.get(key);

		log.info("ruleTypeValuelist: " + ruleTypeValuelist);

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

	private PmConfigRule getPmConfigRuleTypeObjFromDB(String ruleType) {
		log.info(".....................getPmConfigRuleTypeObjFromDB.....................");
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		PmConfigRule pmConfigRule = null;
		IRuleSO ruleSO = null;
		SearchBean<PmConfigRule> searchBean = new SearchBean<PmConfigRule>();
		RuleResDTO resDTO = new RuleResDTO();
		try {
			// get from db
			ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
					"pm", IRuleSO.class);
			RuleReqDTO reqRDTO = new RuleReqDTO();
			reqRDTO.setLanguageCode(languageCode);

			reqRDTO.getSearchBean()
					.getDataFilterBeans()
					.add(new SearchFilterBean("ruleType",
							SearchOperator.OPERATOR_EQUALS.value(), ruleType));

			RuleResDTO resRDTO = ruleSO.getRuleTypes(reqRDTO);
			if (resRDTO.getRuleTypeList() != null && resRDTO.getRuleTypeList().size() > 0) {
				pmConfigRule = resRDTO.getRuleTypeList().get(0);
				// put in map
				ruleTypeObjMap
						.put(getRuleTypeObjMapKey(ruleType), pmConfigRule);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			ruleSO = null;
		}

		return pmConfigRule;
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
			log.info("put into map: " + list);

		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(e.getMessage());
		} finally {
			ruleSO = null;
		}

		return list;
	}

	public void relodeRuleTypeValueMaps() {
		ruleTypeMap.clear();
		ruleTypeObjMap.clear();
		ruleTypeValueMap.clear();
		List<PmConfigRule> ruleTypeList = getRuleTypeList();
		for (PmConfigRule rule : ruleTypeList) {
			getRuleTypeValueListByRuleType(rule.getRuleType());
		}
	}

	private String getRuleTypeObjMapKey(String RuleType) {
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		StringBuffer key = new StringBuffer();
		key.append(RuleType);
		key.append("_");
		key.append(languageCode);

		debugLogger("key: " + key.toString());

		return key.toString();
	}

	public boolean compareAValueWithRuleTypeValues(String ruleType,
			String compareValue) {
		log.info("compareAValueWithRuleTypeValues");
		PmConfigRule pmConfigRule = ruleTypeObjMap
				.get(getRuleTypeObjMapKey(ruleType));
		if (pmConfigRule == null) {
			// get ruleTypeObj from DB
			pmConfigRule = getPmConfigRuleTypeObjFromDB(ruleType);
		}

		boolean compareResult = false;
		try {
			List<PmConfigRuleValue> ruleValueList = getRuleTypeValueListByRuleType(ruleType);
			log.info(".............ruleValueList=" + ruleValueList);
			if (ruleValueList != null && ruleValueList.size() > 0) {
				PmConfigRuleValue ruleValue = ruleValueList.get(0);// always
																	// should
																	// be
																	// one
																	// value
																	// because
																	// overlapped
																	// date
																	// range
																	// not
																	// allowed
																	// for A
																	// and P
				log.info(".............ruleValueList.get(0)=" + ruleValue);
				if (pmConfigRule != null
						&& pmConfigRule.getRuleDataTypeId().getParameterCode()
								.equalsIgnoreCase("A")) {

					if (pmConfigRule.getOperatorType() == SearchOperator.OPERATOR_EQUALS
							.value()) {

						compareResult = Long.parseLong(compareValue) == Long
								.parseLong(ruleValue.getRuleValue());

					} else if (pmConfigRule.getOperatorType() == SearchOperator.OPERATOR_NOT_EQUALS
							.value()) {

						compareResult = Long.parseLong(compareValue) != Long
								.parseLong(ruleValue.getRuleValue());

					} else if (pmConfigRule.getOperatorType() == SearchOperator.OPERATOR_GREATER
							.value()) {

						compareResult = Long.parseLong(compareValue) > Long
								.parseLong(ruleValue.getRuleValue());

					} else if (pmConfigRule.getOperatorType() == SearchOperator.OPERATOR_GREATER_EQUAL
							.value()) {

						compareResult = Long.parseLong(compareValue) >= Long
								.parseLong(ruleValue.getRuleValue());

					} else if (pmConfigRule.getOperatorType() == SearchOperator.OPERATOR_LESS
							.value()) {

						compareResult = Long.parseLong(compareValue) < Long
								.parseLong(ruleValue.getRuleValue());

					} else if (pmConfigRule.getOperatorType() == SearchOperator.OPERATOR_LESS_EQUAL
							.value()) {

						compareResult = Long.parseLong(compareValue) <= Long
								.parseLong(ruleValue.getRuleValue());

					}

				} else if (pmConfigRule.getRuleDataTypeId().getParameterCode()
						.equalsIgnoreCase("p")) {// if it is percentage

					if (pmConfigRule.getOperatorType() == SearchOperator.OPERATOR_EQUALS
							.value()) {
						compareResult = Double.parseDouble(compareValue) == Double
								.parseDouble(ruleValue.getRuleValue());

					} else if (pmConfigRule.getOperatorType() == SearchOperator.OPERATOR_NOT_EQUALS
							.value()) {

						compareResult = Double.parseDouble(compareValue) != Double
								.parseDouble(ruleValue.getRuleValue());

					} else if (pmConfigRule.getOperatorType() == SearchOperator.OPERATOR_GREATER
							.value()) {

						compareResult = Double.parseDouble(compareValue) > Double
								.parseDouble(ruleValue.getRuleValue());

					} else if (pmConfigRule.getOperatorType() == SearchOperator.OPERATOR_GREATER_EQUAL
							.value()) {

						compareResult = Double.parseDouble(compareValue) >= Double
								.parseDouble(ruleValue.getRuleValue());

					} else if (pmConfigRule.getOperatorType() == SearchOperator.OPERATOR_LESS
							.value()) {

						compareResult = Double.parseDouble(compareValue) < Double
								.parseDouble(ruleValue.getRuleValue());

					} else if (pmConfigRule.getOperatorType() == SearchOperator.OPERATOR_LESS_EQUAL
							.value()) {

						compareResult = Double.parseDouble(compareValue) <= Double
								.parseDouble(ruleValue.getRuleValue());

					}

				} else if (pmConfigRule.getRuleDataTypeId().getParameterCode()
						.equalsIgnoreCase("c")) { // configRule.getOperatorType()
													// should always be ==
					// otherwise it's category
					for (PmConfigRuleValue ruleV : ruleValueList) {
						String categoryValue = ruleV.getCategoryL3Id()
								.getCategoryL3Code()
								+ ruleV.getCategoryL3Id().getPmCategoryL2()
										.getCategoryL2Code()
								+ ruleV.getCategoryL3Id().getPmCategoryL2()
										.getPmCategoryL1().getCategoryL1Code();

						compareResult = (Long.parseLong(compareValue) == Long
								.parseLong(categoryValue));
						if (compareResult == true) {
							break;
						}
					}

				} else if (pmConfigRule.getRuleDataTypeId().getParameterCode()
						.equalsIgnoreCase("m")) {
					for (PmConfigRuleValue ruleV : ruleValueList) {
						compareResult = (ruleV.getRuleValue()
								.equals(compareValue));
						if (compareResult == true) {
							break;
						}
					}
				}
			}

		} catch (NumberFormatException e) {
			log.error(e);
			throw new EpException(
					"CompareValue should be a long value for Amount or Category RuleTypes, And a float value for Percentage RuleType.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return compareResult;
	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
	}

}
