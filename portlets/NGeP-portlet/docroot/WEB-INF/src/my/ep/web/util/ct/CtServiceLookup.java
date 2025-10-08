package my.ep.web.util.ct;



import my.ep.biz.service.ct.ICtSchedulerSO;
import my.ep.biz.service.ct.agreement.IAgreementAutoCancelSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

public class CtServiceLookup {


	
	/**
	 * Get IAgreementAutoCancelSO
	 * 
	 * @return IAgreementAutoCancelSO
	 */
	public static IAgreementAutoCancelSO getAgreementAutoCancelSO() {
		return (IAgreementAutoCancelSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
						IAgreementAutoCancelSO.class);
	}
	
	/**
	 * Get ICtSchedulerSO
	 * 
	 * @return ICtSchedulerSO
	 */
	public static ICtSchedulerSO getCtSchedulerSO() {
		return (ICtSchedulerSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.CONTRACT_MANAGEMENT,
						ICtSchedulerSO.class);
	}

}
