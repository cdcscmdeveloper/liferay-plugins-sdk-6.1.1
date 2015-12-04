package my.ep.web.constant.cm;

import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.pm.RuleTypeConst;
import my.ep.dto.pm.general.settings.rule.RuleReqDTO;
import my.ep.dto.pm.general.settings.rule.RuleResDTO;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmRuleHelper;

public class CmHelper {
	
	private static CmHelper instance;

	public static CmHelper getInstance() {
		
		if (instance == null) {
			instance = new CmHelper();
		}
		return instance;
	}

	public static void setInstance(CmHelper instance) {
		CmHelper.instance = instance;
	}
	
	
	
	public int getStandardPricingYear(){
		
		return 1;
	}

	public int getMaxCategoryAllowed() {
		RuleResDTO ruleResDTO = getRuleValueByRuleType(RuleTypeConst.CM_IMAGE_LIMIT);
		if (ruleResDTO != null && ruleResDTO.isSuccessful()
				&& ruleResDTO.getRuleValueList() != null
				&& ruleResDTO.getRuleValueList().size() > 0) {
			return (Integer.parseInt(ruleResDTO.getRuleValueList().get(0)
					.getRuleValue()));
		} else {
			return 2048;
		}
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
