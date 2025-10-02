package my.ep.rest.sm;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import my.ep.biz.service.intg.sm.IPaymentReconcileServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

/**
 * REST endpoint for reconciliation files in folder.
 * #38750
*/
@Path("/sm")
public class PaymentReconcileRest {

public static final Logger log = Logger.getLogger(PaymentReconcileRest.class);

	/**
	 * REST endpoint to trigger a retry of card transaction reconciliation.
	 * @return a String once the retry process has been initiated
	 */
	@GET
	@Path("/retry-reconcile-card")
	public String retryreconcilecard() throws IOException {
		log.info("*** Starting REST retry: retryreconcilecard");
		IPaymentReconcileServiceSO service = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION, IPaymentReconcileServiceSO.class);
		service.retryCardTransactionBatch();
		return "Done!";
	}
	
	/**
	 * REST endpoint to trigger a retry of fpx transaction reconciliation.
	 * @return a String once the retry process has been initiated
	 */
	@GET
	@Path("/retry-reconcile-fpx")
	public String retryreconcilefpx() throws IOException {
		log.info("*** Starting REST retry: retryreconcilefpx");
		IPaymentReconcileServiceSO service = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION, IPaymentReconcileServiceSO.class);
		service.retryStoreFpxTransactionBatch();
		return "Done!";
	}
}
