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
import java.util.List;

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
import my.ep.dto.sm.payment.FailedReconDtlDTO;
import my.ep.dto.sm.payment.PaymentJobReqDTO;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

/**
 * REST endpoint for reconciliation files in folder.
 * #38750
 *
 * <p>#38750 (defer): the OPS payment resync leg runs from HERE (Liferay) instead of biz-sm, because
 * the biz-sm server has SNI disabled and cannot reach the OPS API. biz-sm PHASE 1 persists the
 * failed rows at record_status = 3; {@code POST /sm/resync-failed-card} drives the OPS API for those
 * rows (scoped by their created-date) and hands each recovered row back to
 * {@code PaymentSO.reconcileRecoveredPayment} for the atomic DB update.</p>
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
	 * #38750 (defer) - Run the deferred OPS resync pass for the card recon-dtl rows that failed
	 * phase-1 reconciliation (record_status = 3) on a given day, then reconcile the recovered ones.
	 *
	 * <p>curl -X POST http://localhost:8080/NGeP-portlet/rest/sm/resync-failed-card
	 * -H "Content-Type: application/json" -d '{"date":"YYYYMMDD","keyPass":8532147}'</p>
	 *
	 * <p>{@code date} is the CSV PROCESSING day (= the day PHASE 1 inserted the rows, created_date
	 * scope), NOT the transaction/settlement date; empty/null defaults to today. Rows that don't
	 * recover stay at record_status = 3 and are retried on the next call for the same date.</p>
	 * @param reqDTO {date, keyPass}
	 * @return plain-text summary, e.g. "recovered 3/10 for 20260711"
	 */
	@POST
	@Path("/resync-failed-card")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response resyncFailedCard(PaymentJobReqDTO reqDTO) {
		try {
			log.info("#resyncFailedCard");
			if (reqDTO == null) {
				return Response.status(400).entity("Body is required").build();
			}
			if (reqDTO.getKeyPass() != 8532147) {
				return Response.status(401).entity("Error Key").build();
			}

			String date = reqDTO.getDate();
			List<FailedReconDtlDTO> failed = SmServiceLookup.getPaymentSO().retrieveFailedReconDtls(date);
			int total = (failed == null) ? 0 : failed.size();
			if (total == 0) {
				return Response.status(200).entity("recovered 0/0 for " + date).build();
			}

			String token = loginOps();
			if (token == null || token.isEmpty()) {
				log.error("[API #38750] OPS login failed; cannot resync " + total + " payment(s) for " + date);
				return Response.status(502).entity("OPS login failed for " + date).build();
			}
			long lastLoginMillis = System.currentTimeMillis();

			int recovered = 0;
			for (FailedReconDtlDTO dtl : failed) {
				Long paymentId = parsePaymentId(dtl.getInvoiceNumber2());
				if (paymentId == null) {
					log.warn("[API #38750] unparseable invoiceNumber2 for reconDtlId=" + dtl.getPaymentReconDtlId());
					continue;
				}

				String body = callResyncApi(paymentId, token);
				// A non-2-char reply (HTML error page) usually means the token expired; re-auth once,
				// but not within 60s of the last login - assume error and move on instead of hammering it.
				if ((body == null || body.length() != 2) && System.currentTimeMillis() - lastLoginMillis >= 60 * 1000) {
					lastLoginMillis = System.currentTimeMillis();
					String newToken = loginOps();
					if (newToken != null && !newToken.isEmpty()) {
						token = newToken;
						body = callResyncApi(paymentId, token);
					}
				}

				if ("00".equals(body)) {
					// atomic + idempotent DB reconcile back in biz-sm
					if (SmServiceLookup.getPaymentSO().reconcileRecoveredPayment(dtl.getPaymentReconDtlId())) {
						recovered++;
					}
				} else {
					log.warn("[API #38750] paymentId=" + paymentId + " not recovered (reply=" + body + ")");
				}
			}

			String summary = "recovered " + recovered + "/" + total + " for " + date;
			log.info("[API #38750] " + summary);
			return Response.status(200).entity(summary).build();
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
	public String checkPayment(@QueryParam("id") Long paymentId){
		log.info("START "+paymentId);
		String token = loginOps();
		log.info("tok:"+token);
		String body = callResyncApi(paymentId, token);
		return "BODY: "+body;
	}

	// ---------------------------------------------------------------------------------------------
	// #38750 (defer) - inline OPS client (ported from biz-sm SmSchedulerSO). The custom SNI socket
	// factory wrapper is intentionally dropped: this Liferay server has SNI enabled, so a plain
	// TLS1.2 HttpsURLConnection is enough. Properties are read lazily per call (not static fields).
	// ---------------------------------------------------------------------------------------------

	private static String prop(String key) {
		return EpPropertiesUtils.getInstance().getProperty(key);
	}

	private static Long parsePaymentId(String invoiceNumber2) {
		if (invoiceNumber2 == null || invoiceNumber2.trim().isEmpty()) {
			return null;
		}
		try {
			return Long.valueOf(invoiceNumber2.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Logs in to the OPS payment service and returns a short-lived (~30 min) bearer token.
	 */
	private String loginOps() {
		String loginUrl = prop("reconcile.payment.login.url");
		String username = prop("reconcile.payment.username");
		String password = EpPropertiesUtils.decrypt(prop("reconcile.payment.password"));
		if (loginUrl == null || loginUrl.trim().isEmpty()) {
			log.warn("[API #38750] reconcile.payment.login.url not configured; cannot obtain OPS token");
			return "";
		}
		if (username == null || password == null) {
			log.warn("[API #38750] reconcile.payment.username/password not configured; cannot obtain OPS token");
			return "";
		}

		HttpURLConnection conn = null;
		try {
			URL url = new URL(loginUrl);
			conn = (HttpURLConnection) url.openConnection();

			// JDK7 client defaults to TLS 1.0; the OPS endpoint requires TLS 1.2
			if (conn instanceof HttpsURLConnection) {
				SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
				sslContext.init(null, null, null);
				((HttpsURLConnection) conn).setSSLSocketFactory(sslContext.getSocketFactory());
			}

			// multipart/form-data, mirroring `curl -F userName=... -F password=...`
			String boundary = "----ngepOps" + System.currentTimeMillis();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			conn.setDoOutput(true);
			conn.setConnectTimeout(30 * 1000); // 30 sec
			conn.setReadTimeout(30 * 1000); // 30 sec

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
			String token = extractToken(readResponseBody(conn, responseCode));
			if (token.isEmpty()) {
				log.warn("[API #38750] OPS login responseCode=" + responseCode + " returned no token");
			}
			return token;
		} catch (Exception e) {
			log.error("[API #38750] OPS login failed: " + e.getMessage(), e);
			return "";
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	/**
	 * Calls the OPS resync API for one payment. Returns the raw reply ("00" = recovered), or "" on
	 * any problem so the caller treats it as not-recovered.
	 */
	private String callResyncApi(Long paymentId, String token) {
		if (paymentId == null) {
			return "";
		}
		String resyncUrl = prop("reconcile.payment.url");
		String apiKey = prop("reconcile.payment.api");
		if (resyncUrl == null || resyncUrl.trim().isEmpty()) {
			log.warn("[API #38750] reconcile.payment.url not configured; skipping paymentId=" + paymentId);
			return "";
		}

		HttpURLConnection conn = null;
		try {
			URL url = new URL(resyncUrl);
			conn = (HttpURLConnection) url.openConnection();

			// JDK7 client defaults to TLS 1.0; the OPS endpoint requires TLS 1.2
			if (conn instanceof HttpsURLConnection) {
				SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
				sslContext.init(null, null, null);
				((HttpsURLConnection) conn).setSSLSocketFactory(sslContext.getSocketFactory());
			}

			conn.setRequestMethod("POST");
			conn.setRequestProperty("X-API-KEY-OPS", apiKey);
			conn.setRequestProperty("X-TOKEN-OPS", token);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setDoOutput(true);
			conn.setConnectTimeout(30 * 1000); // 30 sec
			conn.setReadTimeout(200 * 1000); // ~3.3 min

			String body = "orderId=" + URLEncoder.encode(String.valueOf(paymentId), "UTF-8");
			OutputStream os = conn.getOutputStream();
			os.write(body.getBytes(StandardCharsets.UTF_8));
			os.flush();
			os.close();

			int responseCode = conn.getResponseCode();
			String responseBody = readResponseBody(conn, responseCode);
			log.info("[API #38750] paymentId=" + paymentId + " responseCode=" + responseCode
					+ " body=" + responseBody);
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

	/**
	 * Reads the resync API response body (plain text, e.g. "00"/"11"). Returns "" on any problem so
	 * the caller treats it as a non-success response.
	 */
	private String readResponseBody(HttpURLConnection conn, int responseCode) {
		boolean ok = responseCode >= 200 && responseCode < 300;
		InputStream is = null;
		try {
			is = ok ? conn.getInputStream() : conn.getErrorStream();
		} catch (Exception e) {
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

	/**
	 * Extracts the token from the login JSON, e.g. {"token":"<value>","success":"Y"}
	 */
	private String extractToken(String json) {
		if (json == null) {
			return "";
		}
		String key = "\"token\"";
		int k = json.indexOf(key);
		if (k < 0) {
			return "";
		}
		int colon = json.indexOf(':', k + key.length());
		if (colon < 0) {
			return "";
		}
		int firstQuote = json.indexOf('"', colon + 1);
		if (firstQuote < 0) {
			return "";
		}
		int endQuote = json.indexOf('"', firstQuote + 1);
		if (endQuote < 0) {
			return "";
		}
		return json.substring(firstQuote + 1, endQuote).trim();
	}
}
