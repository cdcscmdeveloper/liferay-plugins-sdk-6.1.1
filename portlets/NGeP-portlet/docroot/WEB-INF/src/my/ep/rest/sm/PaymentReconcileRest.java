package my.ep.rest.sm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import my.ep.biz.service.intg.sm.IPaymentReconcileServiceSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.sm.payment.PaymentJobReqDTO;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmServiceLookup;

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
	
	/**
	 * Reconcile card from DB
	 * curl -X POST http://localhost:8080/NGeP-portlet/rest/sync/reconcile-card-db
	 * -H "Content-Type: application/json" \ -d '{"date":"YYYYMMDD","keyPass":"123"}'
	 * @param reqDTO
	 * @return
	 */
	@POST
	@Path("/reconcile-card-db")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
	public Response reconcileCardDB(PaymentJobReqDTO reqDTO) {
		try {
			log.info("#reconcileCardDB");
			if(reqDTO == null){
				log.info("Body Is Null");
				return Response.status(400).entity("Body is required").build();
			}
			
			if(reqDTO.getKeyPass() != 8532147){
				log.info("Key Wrong");
				return Response.status(401).entity("Error Key").build();
			}
			
			boolean success = SmServiceLookup.getSmSchedulerSO().reconcileCardRest(reqDTO.getDate());
			log.info("Finished reconcileCardDB: "+success);	
			return Response.status(200).entity("reconcileCardDB success!!").build();
		} catch (Exception e) {
			log.error(e);
			return Response.status(406).entity(e.getMessage()).build();
		}
	}
	
	/**
	 * Check payment API
	 * @param paymentId
	 * @return
	 */
	@GET
	@Path("/checkpay")
	public String checkPayment(@QueryParam("id") Long paymentId, @QueryParam("key") String key){
		log.info("START "+paymentId);
		if (!isValidApiKey(key)) {
			log.warn("[API #38750] checkpay rejected: invalid api key");
			return "Error Key";
		}
		String token = loginOps();
		log.info("tok:"+token);
		String body = callResyncApi(paymentId, token, key);
		return "BODY: "+body;
	}
	
	@GET
	@Path("/opslogin")
	public String opslogin(@QueryParam("key") String key) {
		log.info("[API #38750] opslogin (web tier direct) reconcile.payment.api="
				+ mask3(EpPropertiesUtils.getInstance().getProperty("reconcile.payment.api")));
		if (!isValidApiKey(key)) {
			log.warn("[API #38750] opslogin rejected: invalid api key");
			return "";
		}
		return loginOps();
	}
	
	@GET
	@Path("/resync")
	public String resync(@QueryParam("id") Long id, @QueryParam("token") String token, @QueryParam("apikey") String apikey) {
		log.info("[API #38750] resync id=" + id);
		if (!isValidApiKey(apikey)) {
			log.warn("[API #38750] resync rejected: invalid api key");
			return "";
		}
		return callResyncApi(id, token, apikey);
	}

	//38750 - TEMP
	@GET
	@Path("/poke-opslogin")
	public String pokeOpslogin() {
		log.info("[API #38750] poke-opslogin -> biz-sm loginOpsViaPortal");
		String token = SmServiceLookup.getSmSchedulerSO().loginOpsViaPortal();
		return "biz-sm got token via portal: " + token;
	}

	private boolean isValidApiKey(String apikey) {
		String expected = EpPropertiesUtils.getInstance().getProperty("reconcile.payment.api");
		return expected != null && !expected.trim().isEmpty() && expected.equals(apikey);
	}

	/** mask a config value for logging: first 3 chars + length only, never the whole secret. */
	private static String mask3(String s) {
		if (s == null) {
			return "null";
		}
		if (s.isEmpty()) {
			return "empty";
		}
		return s.substring(0, Math.min(3, s.length())) + "***(len=" + s.length() + ")";
	}

	private String loginOps() {
		String loginUrl = EpPropertiesUtils.getInstance().getProperty("reconcile.payment.login.url");
		String username = EpPropertiesUtils.getInstance().getProperty("reconcile.payment.username");
		String password = EpPropertiesUtils.decrypt(EpPropertiesUtils.getInstance().getProperty("reconcile.payment.password"));
		log.info("[API #38750] loginOps props reconcile.payment.login.url=" + loginUrl
				+ " reconcile.payment.username=" + mask3(username)
				+ " reconcile.payment.password=" + (password == null ? "null" : "set"));
		if (loginUrl == null || loginUrl.trim().isEmpty()) {
			log.warn("[API #38750] reconcile.payment.login.url not configured");
			return "";
		}
		if (username == null || password == null) {
			log.warn("[API #38750] reconcile.payment.username/password not configured");
			return "";
		}
		HttpURLConnection conn = null;
		try {
			URL url = new URL(loginUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (conn instanceof HttpsURLConnection) {
				SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
				sslContext.init(null, null, null);
				((HttpsURLConnection) conn).setSSLSocketFactory(sslContext.getSocketFactory());
			}
			String boundary = "----ngepOps" + System.currentTimeMillis();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			conn.setDoOutput(true);
			conn.setConnectTimeout(30 * 1000);
			conn.setReadTimeout(30 * 1000);
			StringBuilder form = new StringBuilder();
			form.append("--").append(boundary).append("\r\n");
			form.append("Content-Disposition: form-data; name=\"userName\"\r\n\r\n");
			form.append(username).append("\r\n");
			form.append("--").append(boundary).append("\r\n");
			form.append("Content-Disposition: form-data; name=\"password\"\r\n\r\n");
			form.append(password).append("\r\n");
			form.append("--").append(boundary).append("--\r\n");
			OutputStream os = conn.getOutputStream();
			os.write(form.toString().getBytes(StandardCharsets.UTF_8));
			os.flush();
			os.close();
			int responseCode = conn.getResponseCode();
			String body = readResponseBody(conn, responseCode);
			log.info("[API #38750] OPS login responseCode=" + responseCode + " body=" + body);
			return body;
		} catch (Exception e) {
			log.error("[API #38750] OPS login failed: " + e.getMessage(), e);
			return "";
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	
	private String callResyncApi(Long paymentId, String token, String apikey) {
		if (paymentId == null) {
			return "";
		}
		String resyncUrl = EpPropertiesUtils.getInstance().getProperty("reconcile.payment.url");
		if (resyncUrl == null || resyncUrl.trim().isEmpty()) {
			log.warn("[API #38750] reconcile.payment.url not configured; skipping paymentId=" + paymentId);
			return "";
		}
		log.info("[API #38750] callResyncApi props reconcile.payment.url=" + resyncUrl
				+ " reconcile.payment.api=" + mask3(apikey) + " token=" + mask3(token) + " paymentId=" + paymentId);
		HttpURLConnection conn = null;
		try {
			URL url = new URL(resyncUrl);
			conn = (HttpURLConnection) url.openConnection();
			if (conn instanceof HttpsURLConnection) {
				SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
				sslContext.init(null, null, null);
				((HttpsURLConnection) conn).setSSLSocketFactory(sslContext.getSocketFactory());
			}
			conn.setRequestMethod("POST");
			conn.setRequestProperty("X-API-KEY-OPS", apikey);
			conn.setRequestProperty("X-TOKEN-OPS", token);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setDoOutput(true);
			conn.setConnectTimeout(30 * 1000);
			conn.setReadTimeout(200 * 1000);
			String reqBody = "orderId=" + URLEncoder.encode(String.valueOf(paymentId), "UTF-8");
			OutputStream os = conn.getOutputStream();
			os.write(reqBody.getBytes(StandardCharsets.UTF_8));
			os.flush();
			os.close();
			int responseCode = conn.getResponseCode();
			String responseBody = readResponseBody(conn, responseCode);
			log.info("[API #38750] paymentId=" + paymentId + " responseCode=" + responseCode + " body=" + responseBody);
			return responseBody;
		} catch (Exception e) {
			log.error("[API #38750] resync failed for paymentId=" + paymentId + " : " + e.getMessage(), e);
			return "";
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	
	private String readResponseBody(HttpURLConnection conn, int responseCode) {
		boolean ok = responseCode >= 200 && responseCode < 300;
		InputStream is = null;
		try {
			is = ok ? conn.getInputStream() : conn.getErrorStream();
		} catch (Exception e){
			log.error("", e);
		}
		if (is == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		try (BufferedReader read = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			String line;
			while ((line = read.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString().trim();
		} catch (Exception e) {
			log.error("[API #38750] could not read response", e);
			return "";
		}
	}
}