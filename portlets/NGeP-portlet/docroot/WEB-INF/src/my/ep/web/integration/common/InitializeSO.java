package my.ep.web.integration.common;

import my.ep.biz.service.cm.codification.ICodificationSO;
import my.ep.biz.service.fl.payment.IPaymentMatchSO;
import my.ep.biz.service.integration.jms.IRemoteClientSO;
import my.ep.biz.service.osb.fl.IFLOsbSO;
import my.ep.biz.service.pm.authorization.orgprofile.IOrgProfileSO;
import my.ep.biz.service.pm.common.address.IAddressSO;
import my.ep.biz.service.pm.common.agoffice.IAgOfficeSO;
import my.ep.biz.service.pm.common.batch.IPmInterfaceSO;
import my.ep.biz.service.pm.common.confgeneral.IConfigGeneralSO;
import my.ep.biz.service.pm.common.financialorg.IFinancialOrgSO;
import my.ep.biz.service.pm.common.glaccount.IGlAccountSO;
import my.ep.biz.service.pm.common.parameter.IParameterSO;
import my.ep.biz.service.pm.common.programactivity.IProgramActivitySO;
import my.ep.biz.service.pm.common.projectsetia.IProjectSetiaSO;
import my.ep.biz.service.pm.common.votfund.IVotFundSO;
import my.ep.biz.service.sc.quationtender.IQTAwardSO;
import my.ep.biz.service.sc.requestitem.IRequestItemSO;
import my.ep.biz.service.sm.payment.IPaymentSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.vendorcode.IVendorCodeSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

/*
 *@author P Ganga prasad
 */

public class InitializeSO {

	protected IVotFundSO votFundSO = (IVotFundSO) BusinessDelegate
			.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IVotFundSO.class);
	protected IProgramActivitySO programActivitySO = BusinessDelegate
			.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IProgramActivitySO.class);
	protected IProjectSetiaSO projectSetiaSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IProjectSetiaSO.class);
	protected IGlAccountSO glAccountSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IGlAccountSO.class);
	protected ICodificationSO codificationSO = (ICodificationSO) BusinessDelegate
			.getInstance()
			.getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,
					ICodificationSO.class);
	protected IAddressSO addressSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IAddressSO.class);
	protected IOrgProfileSO orgProfileSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IOrgProfileSO.class);
	protected IConfigGeneralSO configGeneralSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IConfigGeneralSO.class);
	protected IPaymentMatchSO paymentSO = (IPaymentMatchSO) BusinessDelegate
			.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,
					IPaymentMatchSO.class);

	protected IParameterSO parameterSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IParameterSO.class);

	protected ISmRemarksSO smRemarksSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
					ISmRemarksSO.class);

	protected ISupplierSearchSO supplierSearchSO = BusinessDelegate
			.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT,
					ISupplierSearchSO.class);

	protected IAgOfficeSO agOfficeSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IAgOfficeSO.class);
	protected IFinancialOrgSO financialOrgSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IFinancialOrgSO.class);
	protected IFLOsbSO flOsbSO = BusinessDelegate
			.getInstance()
			.getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFLOsbSO.class);

	protected IQTAwardSO qtAwardSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTAwardSO.class);

	protected IPaymentSO iPaymentSO = (IPaymentSO) BusinessDelegate
			.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, IPaymentSO.class);

	protected IRequestItemSO reqItemSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IRequestItemSO.class);

	protected IPmInterfaceSO interfaceSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IPmInterfaceSO.class);

	protected IVendorCodeSO iVendorCodeSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
					IVendorCodeSO.class);
	protected IRegistrationSO registrationSO = BusinessDelegate.getInstance()
			.getServiceObject(ModuleCodeConst.SUPPLIER_MANAGEMENT,
					IRegistrationSO.class);
	protected IRemoteClientSO remoteClientSO = BusinessDelegate.getInstance()
			.getServiceObject("mdb", IRemoteClientSO.class);

	public InitializeSO() {
		super();
	}

}