package my.ep.web.integration.common;

import java.util.HashMap;

import my.ep.biz.service.IGenericSO;
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
import my.ep.biz.service.sm.gst.ISupplierGstSO;
import my.ep.biz.service.sm.payment.IPaymentSO;
import my.ep.biz.service.sm.registration.IRegistrationSO;
import my.ep.biz.service.sm.remarks.ISmRemarksSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.biz.service.sm.vendorcode.IVendorCodeSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;

/*
 *@author P Ganga prasad
 */

public class InitializeSO {
	protected static HashMap<String,IGenericSO> SOMap = new HashMap<String,IGenericSO>();

	protected static IVotFundSO votFundSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IVotFundSO.class);
	protected static IProgramActivitySO programActivitySO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IProgramActivitySO.class);
	protected static IProjectSetiaSO projectSetiaSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IProjectSetiaSO.class);
	protected static IGlAccountSO glAccountSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IGlAccountSO.class);
	protected static ICodificationSO codificationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.CATALOGUE_MANAGEMENT,
					ICodificationSO.class);
	protected static IAddressSO addressSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IAddressSO.class);
	protected static IOrgProfileSO orgProfileSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IOrgProfileSO.class);
	protected static IConfigGeneralSO configGeneralSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IConfigGeneralSO.class);
	protected static IPaymentMatchSO paymentSO = (IPaymentMatchSO) EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT,
					IPaymentMatchSO.class);

	protected static IParameterSO parameterSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IParameterSO.class);

	protected static ISmRemarksSO smRemarksSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISmRemarksSO.class);

	protected static ISupplierSearchSO supplierSearchSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT,
					ISupplierSearchSO.class);

	protected static IAgOfficeSO agOfficeSO =EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IAgOfficeSO.class);
	protected static IFinancialOrgSO financialOrgSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IFinancialOrgSO.class);
	protected static IFLOsbSO flOsbSO =EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.ORDER_FULFILLMENT, IFLOsbSO.class);

	protected static IQTAwardSO qtAwardSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IQTAwardSO.class);

	protected static IPaymentSO iPaymentSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, IPaymentSO.class);

	protected static IRequestItemSO reqItemSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.SUPPLIER_SELECTION,
					IRequestItemSO.class);

	protected static IPmInterfaceSO interfaceSO = (IPmInterfaceSO)EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
					IPmInterfaceSO.class);

	protected static IVendorCodeSO iVendorCodeSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, IVendorCodeSO.class);
	protected static IRegistrationSO registrationSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, IRegistrationSO.class);
	protected static IRemoteClientSO remoteClientSO = EpEjbBusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.MDB,
					IRemoteClientSO.class);
	protected static ISupplierGstSO supplierGstSO = EpEjbBusinessDelegate.getInstance().getServiceObject(
					ModuleCodeConst.SUPPLIER_MANAGEMENT, ISupplierGstSO.class);
	

	
	public InitializeSO() {
		super();
		
	
	}

	@SuppressWarnings("unused")
	public void cleanUpResources() {
		IVotFundSO votFundSO = null;
		IProgramActivitySO programActivitySO = null;
		IProjectSetiaSO projectSetiaSO = null;
		IGlAccountSO glAccountSO = null;
		ICodificationSO codificationSO = null;
		IAddressSO addressSO = null;
		IOrgProfileSO orgProfileSO = null;
		IConfigGeneralSO configGeneralSO = null;
		IPaymentMatchSO paymentSO = null;
		IParameterSO parameterSO = null;
		ISmRemarksSO smRemarksSO = null;
		ISupplierSearchSO supplierSearchSO = null;
		IAgOfficeSO agOfficeSO = null;
		IFinancialOrgSO financialOrgSO = null;
		IFLOsbSO flOsbSO = null;
		IQTAwardSO qtAwardSO = null;
		IPaymentSO iPaymentSO = null;
		IRequestItemSO reqItemSO = null;
		IPmInterfaceSO interfaceSO = null;
		IVendorCodeSO iVendorCodeSO = null;
		IRegistrationSO registrationSO = null;
		IRemoteClientSO remoteClientSO = null;
	}

}