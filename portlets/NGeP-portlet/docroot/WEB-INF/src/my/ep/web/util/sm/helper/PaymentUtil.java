package my.ep.web.util.sm.helper;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import my.ep.util.PasswordUtils;
import my.ep.util.EpPropertiesUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;
import org.primefaces.json.JSONObject;

public class PaymentUtil {
	public static Logger log = Logger.getLogger(PaymentUtil.class);

	public static String constructJsonPayload(String merchantCode, String clientKey, Long orderId, BigDecimal amount, String billName, String email,
			String mobile, String billDesc, String country, String cancelUrl, String responseUrl, String callbackUrl, String language) {
		String payload = null;
		StringBuilder sb = new StringBuilder();
		sb.append("{").append("\"orderId\":\"").append(orderId.toString()).append("\",").append("\"billName\":\"").append(billName).append("\",")
				.append("\"billDesc\":\"").append(billDesc).append("\",").append("\"billEmail\":\"").append(email).append("\",")
				.append("\"billMobile\":\"").append(mobile).append("\",").append("\"responseUrl\":\"").append(responseUrl).append("\",")
				.append("\"callbackUrl\":\"").append(callbackUrl).append("\",").append("\"cancelUrl\":\"").append(cancelUrl).append("\",")
				.append("\"language\":\"").append(language).append("\",").append("\"merchantCode\":\"").append(merchantCode).append("\",");
		try {
			DecimalFormat df = new DecimalFormat("0.00");
			String amountStr = df.format(amount);
			sb.append("\"billAmt\":\"").append(amountStr).append("\"").append("}");
			payload = CryptoUtils.encryptAES(sb.toString(), PasswordUtils.decrypt(clientKey));
			log.info("#38377 order id:"+orderId+"  payload:"+payload);
		} catch (Exception e) {

		}
		return payload;
	}
	
	public static JSONObject getPaymentGatewayJson(String payload) {
		JSONObject jsonObject = null;
		if (!StringUtils.isBlank(payload)) {
			try {
				payload = CryptoUtils.decryptAES(payload, PasswordUtils
						.decrypt(EpPropertiesUtils.getInstance().getProperty(
								"payment.gov.vkey")));
				jsonObject = new JSONObject(payload);
			} catch (Exception e1) {
				log.info("#38377 error decrypted payload:" + e1.getMessage());
			}
		}
		return jsonObject;
	}
}
