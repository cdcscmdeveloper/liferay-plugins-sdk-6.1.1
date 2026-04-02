package my.ep.web.util.fl.yep;

import my.ep.biz.service.fl.IFulfilmentYepSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

public class FlYepServiceLookup {

	/**
	 * Get IFulfilmentYepSO
	 * 
	 * @return IFulfilmentYepSO
	 */
	public static IFulfilmentYepSO getFlYepSO() {
		return (IFulfilmentYepSO) BusinessDelegate.getInstance().getServiceObject(
				ModuleCodeConst.ORDER_FULFILLMENT, IFulfilmentYepSO.class);
	}

}
