package my.ep.web.util.sm.helper;

import java.math.BigDecimal;

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
				.getApplicationReSubmissionDays();
	}

	public int getMaxCategoryAllowed() {
		return SmServiceLookup.getSmRuleSO().retrieveMaxCategoryAllowed()
				.getMaxCategoryAllowed();
	}

	public int getMaxMofAuthorizedUser() {
		return SmServiceLookup.getSmRuleSO().retrieveMaxMofAuthUser()
				.getMaxMofAuthorizedUser();
	}

	public int getMaxAdditionalMofAuthorizedUser() {
		return SmServiceLookup.getSmRuleSO().retrieveMaxAdditionalMofAuthUser()
				.getMaxAdditionalMofAuthUser();
	}

	public int getMaxBasicAuthorizedUser() {
		return SmServiceLookup.getSmRuleSO().retrieveMaxBasicAuthUser()
				.getMaxBasicAuthUser();
	}

	public int getEmailActivationDays() {
		return SmServiceLookup.getSmRuleSO().retrieveEmailActivationDays()
				.getEmailActivationDays();
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

	public int getMaxPaymentBankForEachBranch() {
		return 35;
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
