package my.ep.web.util.sm.helper;

import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.pm.common.businessnature.IBusinessNatureSO;
import my.ep.biz.service.pm.common.city.ICitySO;
import my.ep.biz.service.pm.common.country.ICountrySO;
import my.ep.biz.service.pm.common.district.IDistrictSO;
import my.ep.biz.service.pm.common.document.IDocumentSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.pm.common.status.IStatusSO;
import my.ep.biz.service.pm.common.zone.IZoneSO;
import my.ep.biz.service.pm.general.settings.rule.IRuleSO;
import my.ep.biz.service.sm.attachment.ISmAttachmentSO;
import my.ep.biz.service.sm.common.myidentity.IMyIdentitySO;
import my.ep.biz.service.sm.mofAccount.IMofAccountSO;
import my.ep.biz.service.sm.mofRegistration.IMofRegistrationSO;
import my.ep.biz.service.sm.payment.IPaymentSO;
import my.ep.biz.service.sm.personnel.IPersonnelSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.reviewApplication.IReviewApplicationSO;
import my.ep.biz.service.sm.revision.ISmApplRevisionSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.siteVisit.ISiteVisitSO;
import my.ep.biz.service.sm.supplieruseractivation.ISupplierUserActivationSO;
import my.ep.biz.service.sm.workFlowStatus.ISmWorkFlowStatusSO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.util.lookupservice.BusinessDelegate;

public class SmServiceLookup {

	/**
	 * Get IPersonnelSO
	 * 
	 * @return IPersonnelSO
	 */
	public static IPersonnelSO getPersonnelSO() {
		return (IPersonnelSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, IPersonnelSO.class);
	}

	/**
	 * Get IPaymentSO
	 * 
	 * @return IPaymentSO
	 */
	public static IPaymentSO getPaymentSO() {
		return (IPaymentSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, IPaymentSO.class);
	}

	/**
	 * Get ICountrySO
	 * 
	 * @return ICountrySO
	 */
	public static ICountrySO getCountrySO() {
		return (ICountrySO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ICountrySO.class);
	}

	/**
	 * Get IStateSO
	 * 
	 * @return IStateSO
	 */
	public static IStateSO getStateSO() {
		return (IStateSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IStateSO.class);
	}

	/**
	 * Get IDistrictSO
	 * 
	 * @return IDistrictSO
	 */
	public static IDistrictSO getDistrictSO() {
		return (IDistrictSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IDistrictSO.class);
	}

	/**
	 * Get ICitySO
	 * 
	 * @return ICitySO
	 */
	public static ICitySO getCitySO() {
		return (ICitySO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, ICitySO.class);
	}

	/**
	 * Get IMyIdentitySO
	 * 
	 * @return IMyIdentitySO
	 */
	public static IMyIdentitySO getMyIdentitySO() {
		return (IMyIdentitySO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, IMyIdentitySO.class);
	}

	/**
	 * Get IUserSO
	 * 
	 * @return IUserSO
	 */
	public static IUserSO getUserSO() {
		return (IUserSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
	}

	/**
	 * Get IParameterSO
	 * 
	 * @return IParameterSO
	 */
	public static IParameterSO getParameterSO() {
		return (IParameterSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IParameterSO.class);
	}

	/**
	 * Get IMofRegistrationSO
	 * 
	 * @return IMofRegistrationSO
	 */
	public static IMofRegistrationSO getMofRegistrationSO() {
		return (IMofRegistrationSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IMofRegistrationSO.class);
	}

	/**
	 * Get ISmApplRevisionSO
	 * 
	 * @return ISmApplRevisionSO
	 */
	public static ISmApplRevisionSO getSmApplRevisionSO() {
		return (ISmApplRevisionSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISmApplRevisionSO.class);
	}

	/**
	 * Get IMofAccountSO
	 * 
	 * @return IMofAccountSO
	 */
	public static IMofAccountSO getMofAccountSO() {
		return (IMofAccountSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, IMofAccountSO.class);
	}

	/**
	 * Get IStatusSO
	 * 
	 * @return IStatusSO
	 */
	public static IStatusSO getStatusSO() {
		return (IStatusSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IStatusSO.class);
	}

	/**
	 * Get ISupplierSearchSO
	 * 
	 * @return ISupplierSearchSO
	 */
	public static ISupplierSearchSO getSupplierSearchSO() {
		return (ISupplierSearchSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierSearchSO.class);
	}

	/**
	 * Get IBpmSO
	 * 
	 * @return IBpmSO
	 */
	public static IBpmSO getBpmSO() {
		return (IBpmSO) EpBpmBusinessDelegate.getInstance().getServiceObject(
				IBpmSO.class);
	}

	/**
	 * Get IRegistrationSO
	 * 
	 * @return IRegistrationSO
	 */
	public static IRegistrationSO getRegistrationSO() {
		return (IRegistrationSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IRegistrationSO.class);
	}

	/**
	 * Get ISmWorkFlowStatusSO
	 * 
	 * @return ISmWorkFlowStatusSO
	 */
	public static ISmWorkFlowStatusSO getSmWorkFlowStatusSO() {
		return (ISmWorkFlowStatusSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISmWorkFlowStatusSO.class);
	}

	/**
	 * Get IReviewApplicationSO
	 * 
	 * @return IReviewApplicationSO
	 */
	public static IReviewApplicationSO getReviewApplicationSO() {
		return (IReviewApplicationSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						IReviewApplicationSO.class);
	}

	/**
	 * Get ISmRemarksSO
	 * 
	 * @return ISmRemarksSO
	 */
	public static ISmRemarksSO getSmRemarksSO() {
		return (ISmRemarksSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmRemarksSO.class);
	}

	/**
	 * Get ISmAttachmentSO
	 * 
	 * @return ISmAttachmentSO
	 */
	public static ISmAttachmentSO getSmAttachmentSO() {
		return (ISmAttachmentSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISmAttachmentSO.class);
	}

	/**
	 * Get IDocumentSO
	 * 
	 * @return IDocumentSO
	 */
	public static IDocumentSO getDocumentSO() {
		return (IDocumentSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IDocumentSO.class);
	}

	/**
	 * Get IFinancialOrgSO
	 * 
	 * @return IFinancialOrgSO
	 */
	public static IFinancialOrgSO getFinancialOrgSO() {
		return (IFinancialOrgSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IFinancialOrgSO.class);
	}

	/**
	 * Get IBusinessNatureSO
	 * 
	 * @return IBusinessNatureSO
	 */
	public static IBusinessNatureSO getBusinessNatureSO() {
		return (IBusinessNatureSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
						IBusinessNatureSO.class);
	}

	/**
	 * Get IOrgProfileSO
	 * 
	 * @return IOrgProfileSO
	 */
	public static IOrgProfileSO getOrgProfileSO() {
		return (IOrgProfileSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IOrgProfileSO.class);
	}

	/**
	 * Get ISupplierUserActivationSO
	 * 
	 * @return ISupplierUserActivationSO
	 */
	public static ISupplierUserActivationSO getSupplierUserActivationSO() {
		return (ISupplierUserActivationSO) BusinessDelegate.getInstance()
				.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
						ISupplierUserActivationSO.class);
	}

	/**
	 * Get ISiteVisitSO
	 * 
	 * @return ISiteVisitSO
	 */
	public static ISiteVisitSO getSiteVisitSO() {
		return (ISiteVisitSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.SUPPLIER_MANAGEMENT, ISiteVisitSO.class);
	}

	/**
	 * Get IRuleSO
	 * 
	 * @return IRuleSO
	 */
	public static IRuleSO getRuleSO() {
		return (IRuleSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IRuleSO.class);
	}
	
	/**
	 * Get IZoneSO
	 * 
	 * @return IZoneSO
	 */
	public static IZoneSO getZoneSO() {
		return (IZoneSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.PROFILE_MANAGEMENT, IZoneSO.class);
	}
}
