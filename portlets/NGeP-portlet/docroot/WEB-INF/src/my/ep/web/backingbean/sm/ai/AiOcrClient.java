package my.ep.web.backingbean.sm.ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.log4j.Logger;

import my.ep.util.EpPropertiesUtils;

/**
 * Outbound OCR HTTP transport
 */
public final class AiOcrClient {

	private static final Logger log = Logger.getLogger(AiOcrClient.class);

	private AiOcrClient() {
	}

	/**
	 * The HTTP status and body of one OCR call.
	 */
	public static final class OcrResponse {
		public final int status;
		public final String body;

		private OcrResponse(int status, String body) {
			this.status = status;
			this.body = body;
		}
	}

	/**
	 * Call API
	 * @param requestBody
	 * @param docType
	 * @return
	 */
	public static OcrResponse callDocumentOcr(String requestBody, String docType) {
		if (!AiDocSupport.isAiEnabled()) {
			log.info("AI doc extraction is off (google.doc.ai.onoff), skipping OCR call for docType: " + docType);
			return null;
		}
		if (requestBody == null || requestBody.trim().isEmpty()) {
			return null;
		}

		String url = EpPropertiesUtils.getInstance().getProperty("google.doc.url");
		String key = EpPropertiesUtils.decrypt(EpPropertiesUtils.getInstance().getProperty("google.doc.key"));
		String tenantId = EpPropertiesUtils.getInstance().getProperty("google.doc.tenant.id");

		if (url == null || url.trim().isEmpty()) {
			log.error("ngep.properties missing property: google.doc.url");
			return null;
		}
		if (key == null || key.trim().isEmpty()) {
			log.error("ngep.properties missing property: google.doc.key");
			return null;
		}

		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			if (conn instanceof HttpsURLConnection) {
				SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
				sslContext.init(null, null, null);
				((HttpsURLConnection) conn).setSSLSocketFactory(sslContext.getSocketFactory());
			}
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("X-API-Key", key);
			if (tenantId != null && !tenantId.trim().isEmpty()) {
				conn.setRequestProperty("puncakai-tenant-id", tenantId);
			}
			conn.setDoOutput(true);
			conn.setConnectTimeout(30 * 1000); // 30 sec
			conn.setReadTimeout(120 * 1000); // 2 min

			byte[] jsonBytes = requestBody.getBytes(StandardCharsets.UTF_8);
			conn.setFixedLengthStreamingMode(jsonBytes.length);
			OutputStream os = conn.getOutputStream();
			os.write(jsonBytes);
			os.flush();
			os.close();

			int responseCode = conn.getResponseCode();
			log.info("callDocumentOcr [" + docType + "] status=" + responseCode);
			String body = readResponseBody(conn, responseCode);
			return body == null ? null : new OcrResponse(responseCode, body);

		} catch (Exception e) {
			log.error("AI OCR call failed for docType=" + docType, e);
			return null;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private static String readResponseBody(HttpURLConnection conn, int responseCode) {
		boolean ok = responseCode >= 200 && responseCode < 300;
		InputStream is;
		try {
			is = ok ? conn.getInputStream() : conn.getErrorStream();
		} catch (IOException e) {
			is = conn.getErrorStream();
		}
		if (is == null) {
			return null;
		}
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return sb.toString();
		} catch (IOException e) {
			log.error("Error reading AI OCR response", e);
			return null;
		}
	}
}
