package my.ep.rest.sm;

//38750 START
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import my.ep.util.EpPropertiesUtils;
import my.ep.web.util.sm.helper.SmServiceLookup;

import org.apache.log4j.Logger;

@Path("/sm")
public class PaymentReconcileRest {

public static final Logger log = Logger.getLogger(PaymentReconcileRest.class);

	@GET
	@Path("/opslogin")
	public String opslogin(@QueryParam("key") String key) {
		log.info("[API #38750] opslogin (web tier direct)");
		if (!isValidApiKey(key)) {
			log.warn("[API #38750] opslogin rejected: invalid api key");
			return "";
		}
		return loginOps();
	}

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

	private String loginOps() {
		String loginUrl = EpPropertiesUtils.getInstance().getProperty("reconcile.payment.login.url");
		String username = EpPropertiesUtils.getInstance().getProperty("reconcile.payment.username");
		String password = EpPropertiesUtils.decrypt(EpPropertiesUtils.getInstance().getProperty("reconcile.payment.password"));
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
	//38750 END
}
