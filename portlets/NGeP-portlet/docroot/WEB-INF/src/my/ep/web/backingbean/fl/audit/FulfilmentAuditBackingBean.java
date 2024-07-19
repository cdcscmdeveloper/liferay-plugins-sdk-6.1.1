package my.ep.web.backingbean.fl.audit;


import my.ep.biz.service.IAuditTrailsSO;
import my.ep.biz.service.fl.audit.IAuditFulfilmentSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.AuditFLConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

/**
 * Backing Bean for FL audit
 * 
 * @author razin
 * @since 10/07/2024
 */
public class FulfilmentAuditBackingBean {

	IAuditFulfilmentSO auditFulfilmentSO = BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.ORDER_FULFILLMENT, IAuditFulfilmentSO.class);

	IAuditTrailsSO auditTrailAuSo = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.AUDIT,
			IAuditTrailsSO.class);

	/**
	 * get config audit fl state
	 * 
	 * @return auditFlConfig config for audit fl
	 * 
	 */
	public boolean getAuditFlConfigState() {
		boolean auditFlConfig;
		auditFlConfig = auditTrailAuSo.isAuditConfig(AuditFLConst.FL_AU_CONF);

		return auditFlConfig;
	}

}
