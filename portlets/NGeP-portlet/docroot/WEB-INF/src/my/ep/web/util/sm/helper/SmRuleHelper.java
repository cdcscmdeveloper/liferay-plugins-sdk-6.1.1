package my.ep.web.util.sm.helper;

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
		return 30;
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
	 * Duration restrict to maintain Directors/Shareholders after new MOF registration.
	 * 6 Months
	 * @return
	 */
	public int getMaintainRestrictedDays() {
		return 180;
	}
	
	public int getRenewalNotificationDays() {
		return 90;
	}
	
	public int getValidRenewalDays() {
		return 365;
	}
}
