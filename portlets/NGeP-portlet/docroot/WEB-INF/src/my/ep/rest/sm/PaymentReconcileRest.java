package my.ep.rest.sm;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

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
	 * @return
	 */
	@GET
	@Path("/retry-reconcile-card")
	public String retryreconcilecard() throws IOException {
		log.info("*** Starting REST retry: retryreconcilecard");
		IPaymentReconcileServiceSO service = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION, IPaymentReconcileServiceSO.class);
		service.retryCardTransactionBatch();
		return "Done retryreconcilecard!";
	}
	
	/**
	 * REST endpoint to trigger a retry of fpx transaction reconciliation.
	 * @return
	 */
	@GET
	@Path("/retry-reconcile-fpx")
	public String retryreconcilefpx() throws IOException {
		log.info("*** Starting REST retry: retryreconcilefpx");
		IPaymentReconcileServiceSO service = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION, IPaymentReconcileServiceSO.class);
		service.retryStoreFpxTransactionBatch();
		return "Done retryreconcilefpx!";
	}
	
	/**
	 * REST endpoint to trigger a test card log insertion.
	 * @return transId
	 */
	@GET
	@Path("/card-inbound-test")
	public String cardinboundtest() {
		log.info("[cardinboundtest] THIS REST IS JUST FOR TESTING PURPOSE!");
		IPaymentReconcileServiceSO service = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION, IPaymentReconcileServiceSO.class);
		String transId = service.reconcileCardTransactionInbound();
		return "Done cardinboundtest! transId : "+transId;
	}
	
	/**
	 * REST endpoint to trigger a test card reconcile.
	 * card-reconcile-test?uuid=xxx&fileName=xxx
	 * @param transId
	 * @param fileName
	 * @return
	 */
	@GET
	@Path("/card-reconcile-test")
	public String cardreconciletest(@QueryParam("transId") String transId, @QueryParam("fileName") String fileName) throws IOException {
		log.info("[cardreconciletest] THIS REST IS JUST FOR TESTING PURPOSE!");
		IPaymentReconcileServiceSO service = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION, IPaymentReconcileServiceSO.class);
		service.cardReconcileRest(transId, fileName);
		return "Done cardreconciletest! transId : " + transId + " fileName : "+fileName;
	}
	
	/**
	 * REST endpoint to trigger a test fpx log insertion.
	 * @return transId
	 */
	@GET
	@Path("/fpx-inbound-test")
	public String fpxinboundtest() {
		log.info("[fpxinboundtest] THIS REST IS JUST FOR TESTING PURPOSE!");
		IPaymentReconcileServiceSO service = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION, IPaymentReconcileServiceSO.class);
		String transId = service.storeFpxTransactionInbound();
		return "Done fpxinboundtest! transId : "+transId;
	}
	
	/**
	 * REST endpoint to trigger a test fpx reconcile.
	 * fpx-reconcile-test?uuid=xxx&fileName=xxx
	 * @param transId
	 * @param fileName
	 * @return
	 */
	@GET
	@Path("/fpx-reconcile-test")
	public String fpxreconciletest(@QueryParam("transId") String transId, @QueryParam("fileName") String fileName) throws IOException {
		log.info("[fpxreconciletest] THIS REST IS JUST FOR TESTING PURPOSE!");
		IPaymentReconcileServiceSO service = BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.INTEGRATION, IPaymentReconcileServiceSO.class);
		service.fpxReconcileRest(transId, fileName);
		return "Done fpxreconciletest! transId : " + transId + " fileName : "+fileName;
	}
	
}
