package my.ep.web.backingbean.fl.audit;

import java.util.List;

import org.apache.log4j.Logger;

import my.ep.biz.service.IAuditTrailsSO;
import my.ep.biz.service.fl.audit.IAuditFulfilmentSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.fl.AuditFLConst;
import my.ep.dto.fl.audit.AuFLReqDTO;
import my.ep.factory.EpLogger;
import my.ep.web.util.lookupservice.BusinessDelegate;

public class FulfilmentAuditBackingBean {

	private static Logger log = EpLogger.getLogger(FulfilmentAuditBackingBean.class);

	IAuditFulfilmentSO auditFulfilmentSO = (IAuditFulfilmentSO) BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.ORDER_FULFILLMENT, IAuditFulfilmentSO.class);
	
	IAuditTrailsSO auditTrailAuSo = (IAuditTrailsSO) BusinessDelegate.getInstance().getServiceObject(
			ModuleCodeConst.AUDIT, IAuditTrailsSO.class);

	public void auditInsertFL(List<AuFLReqDTO> objList) {
		auditFulfilmentSO.insertAuditFLDetails(objList);
	}

	public boolean getAuditFlConfigState() {
		boolean auditFlConfig;
		auditFlConfig = auditTrailAuSo.isAuditConfig(AuditFLConst.FL_AU_CONF);

		return auditFlConfig;
	}

}
