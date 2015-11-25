package my.ep.web.util.sm.helper;

import java.math.BigDecimal;

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

	private static SmRuleHelper instance;

	public int getApplicationSubmissionDays() {
		return SmServiceLookup.getSmRuleSO()
				.retrieveApplicationSubmissionDays()
				.getApplicationSubmissionDays();
	}

	public int getApplicationResubmissionDays() {
		return SmServiceLookup.getSmRuleSO()
				.retrieveApplicationReSubmissionDays()
				.getApplicationSubmissionDays();
	}

	public int getMaxCategoryAllowed() {
		return SmServiceLookup.getSmRuleSO().retrieveMaxCategoryAllowed()
				.getMaxCategoryAllowed();
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
	 * Duration restriction to maintain Personnel after approved (6 Months)
	 * 
	 * @return
	 */
	public int getPersonnelNoneEditableDurationMonths() {
		return 6;
	}

	public int getRenewalNotificationDays() {
		return 90;
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

	public int getYearsAllowedToAddDeletedCatCode() {
		return 1;
	}

	public BigDecimal getProcessingFee() {
		return SmServiceLookup.getSmRuleSO().retrieveProcessingFeeRate()
				.getProcessingFeeRate();
	}

	public BigDecimal getRegistrationFee() {
		return SmServiceLookup.getSmRuleSO().retrieveRegistrationFeeRate()
				.getRegistrationFeeRate();
	}

	public BigDecimal getSoftCertFee() {
		return SmServiceLookup.getSmRuleSO().retrieveSoftcertRate()
				.getSoftcertRate();
	}

	public static SmRuleHelper getInstance() {
		if (instance == null) {
			instance = new SmRuleHelper();
		}
		return instance;
	}

	public int getMaxSupportingDocumentSize() {
		return 20 * 1024 * 1024;
	}

	public int getMaxSoftCertDocumentSize() {
		return 1 * 1024 * 1024;
	}
}
