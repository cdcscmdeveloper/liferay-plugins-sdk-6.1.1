package my.ep.web.util.sm.helper;

import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * 
 * @author KS
 * 
 */
public class SmRuleHelper {

	public int getApplicationSubmissionDays() {
		return 60;
	}

	public int getApplicationResubmissionDays() {
		return 60;
	}

	public int getMaxCategoryAllowed() {
		RuleResDTO ruleResDTO = getRuleValueByRuleType(RuleTypeConst.SM_CATEGORY_MAX_LIMIT);
		if (ruleResDTO != null && ruleResDTO.isSuccessful()
				&& ruleResDTO.getRuleValueList() != null
				&& ruleResDTO.getRuleValueList().size() > 0) {
			return (Integer.parseInt(ruleResDTO.getRuleValueList().get(0)
					.getRuleValue()));
		} else {
			return 30;
		}
	}

	public int getMaxMofAuthorizedUser() {
		return 5;
	}

	public int getMaxAdditionalMofAuthorizedUser() {
		return 10;
	}

	public int getMaxBasicAuthorizedUser() {
		return 3;
	}

	public int getEmailActivationDays() {
		return 7;
	}

	/**
	 * Duration restrict to maintain Directors/Shareholders after new MOF
	 * registration. 6 Months
	 * 
	 * @return
	 */
	public int getMaintainRestrictedDays() {
		return 180;
	}

	public int getRenewalNotificationDays() {
		RuleResDTO ruleResDTO = getRuleValueByRuleType(RuleTypeConst.SM_CATEGORY_MAX_LIMIT);
		if (ruleResDTO != null && ruleResDTO.isSuccessful()
				&& ruleResDTO.getRuleValueList() != null
				&& ruleResDTO.getRuleValueList().size() > 0) {
			return (Integer.parseInt(ruleResDTO.getRuleValueList().get(0)
					.getRuleValue()));
		} else {

			return 90;
		}

	}

	public int getValidRenewalDays() {
		return 365;
	}

	public int getGstRegNumLength() {
		return 12;
	}

	public int getConsutantPersonnelMinExp() {
		return 5;
	}

	public int getMaxEquityOwnerCountForROBIndividual() {
		return 1;
	}

	public int getMaxEquityOwnerCountForOtherThanROBIndividaul() {
		return 20;
	}

	public int getMinPaidUpCapitalForConsultant() {
		return 50000;
	}

	public int getMinPaidUpCapitalForContractor() {
		return 2500;
	}

	private RuleResDTO getRuleValueByRuleType(String ruleType) {
		IRuleSO ruleSO = null;
		try {
			ruleSO = (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
			RuleReqDTO ruleReqDTO = new RuleReqDTO();
			ruleReqDTO.setRuleType(ruleType);
			RuleResDTO ruleResDTO = ruleSO.getRuleValuesByRuleType(ruleReqDTO);
			return ruleResDTO;
		} finally {
			ruleSO = null;
		}
	}
}
