package my.ep.web.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import my.ep.constant.MessageConst;
import my.ep.exception.EpException;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.constant.SPKIConst;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.msctg.cdc.pki.IKMCAPIResponse;
import com.msctg.cdc.pki.KMCClient;
import com.msctg.cdc.pki.KMCClientFactory;
import com.msctg.cdc.pki.KMCRequestBuilder;

public class TrustGateHelper {
	private static TrustGateHelper instance;
	private static KMCClient client;
	private static KMCRequestBuilder reqBuilder;
	private static String dns;
	public static Logger log = Logger.getLogger(TrustGateHelper.class);

	public KMCClient getKMCClient() {
		URL resource = getClass().getResource(".");
		String path = resource.getPath();

		String substring = path.substring(0, path.indexOf("NGeP-portlet.war"));
		String filePath = substring + SPKIConst.TRUSTGATE_CONFIG_FILE_PATH;

		File f = new File(filePath);
		KMCClient client = KMCClientFactory.getInstance(f);
		return client;
	}

	public List<String> getChallengeQuestion(String identificationNo,
			String epNo, String selectedQuest) {
		List<String> retVal = new ArrayList<String>();
		try {
			KMCClient client = getClient();
			KMCRequestBuilder reqbuilder = getReqBuilder();
			IKMCAPIResponse.GetChallengeQuestionResponse res = reqbuilder
					.getChallengeQuestions(identificationNo, 10, epNo);

			switch (res.getStatusCode()) {
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				retVal = getChallQuest(selectedQuest, res.CHALLENGE_RESPONSES);

				if (retVal.size() == 0) {
					throw new EpException(MessageConst.ERROR_776051);
				}
				break;

			default:
				throw new EpException(MessageConst.ERROR_776062);
			}

		} catch (Exception ex) {
			throw new EpException(ex.getMessage());

		}
		return retVal;
	}

	private List<String> getChallQuest(String selectedQuest,
			List<String> questions) {
		List<String> retVal = new ArrayList<String>();
		for (String quest : questions) {
			if (!quest.equals(selectedQuest)) {
				retVal.add(quest);
				break;
			}
		}
		return retVal;
	}

	public byte[] getSignedData(String identificationNo, String epNo,
			String pin, Map<Object, Object> challenges, String dataForSigning) {
		byte[] signedData = null;

		try {
			KMCClient client = getClient();
			KMCRequestBuilder reqbuilder = getReqBuilder();
			IKMCAPIResponse.SignDataResponse res = reqbuilder.signData(
					identificationNo, epNo, pin.toCharArray(), challenges,
					new ByteArrayInputStream(dataForSigning.getBytes()));

			switch (res.getStatusCode()) {
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				signedData = res.SIGNATURE;
				break;

			default:
				if (res.getStatusCode() == SPKIConst.ERROR_CERT_REVOKED) {
					throw new EpException(MessageConst.ERROR_776057);
				} else if (res.getStatusCode() == SPKIConst.ERROR_USER_INVALID) {
					throw new EpException(MessageConst.ERROR_776058);
				} else if (res.getStatusCode() == SPKIConst.ERROR_PIN_BLOCKED) {
					throw new EpException(MessageConst.ERROR_776059);
				} else if (res.getStatusCode() == SPKIConst.ERROR_INVALID_PIN) {
					throw new EpException(MessageConst.ERROR_776060);
				} else if (res.getStatusCode() == SPKIConst.OPERATION_FAILED) {
					throw new EpException(MessageConst.ERROR_776061);
				} else {
					throw new EpException(MessageConst.ERROR_776062);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new EpException(MessageConst.ERROR_776062);
		}

		return signedData;
	}

	public String generateSecurityToken(String identificationNo, String epNo) {
		String tok = null;
		try {
			KMCClient client = getClient();
			KMCRequestBuilder reqbuilder = getReqBuilder();
			IKMCAPIResponse.GenSecToken res = reqbuilder.generateToken(
					identificationNo, epNo);
			switch (res.getStatusCode()) {
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				tok = res.TOKEN;
				break;

			default:
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new EpException(ex.getMessage());
		}

		return tok;
	}

	public void revoke(String identificationNo, String epNo) throws IOException {
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url4 = getDns() + "/cdc/requestRevoke/requestRevoke.jsp?secTkn="
				+ encodedtoken;
		externalContext.redirect(url4);
	}

	public void resetPIN(String identificationNo, String epNo, String email)
			throws IOException, PortalException, SystemException {
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url = getDns() + "/cdc/resetPIN/resetPIN.jsp?secTkn="
				+ encodedtoken;
		externalContext.redirect(url);
	}

	public void changePIN(String identificationNo, String epNo)
			throws IOException, PortalException, SystemException {
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url = getDns() + "/cdc/changePIN/checkPin.jsp?secTkn="
				+ encodedtoken;
		externalContext.redirect(url);
	}

	public void updateChallResp(String identificationNo, String epNo)
			throws IOException, PortalException, SystemException {
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url = getDns() + "/cdc/updateCR/checkPin.jsp?secTkn="
				+ encodedtoken;
		externalContext.redirect(url);
	}

	public static TrustGateHelper getInstance() {
		if (instance == null) {
			try {
				instance = new TrustGateHelper();
			} catch (Exception e) {

			}
		}
		return instance;

	}

	public static KMCClient getClient() {
		if (client == null) {
			try {
				client = getInstance().getKMCClient();
			} catch (Exception e) {

			}
		}
		return client;
	}

	public static KMCRequestBuilder getReqBuilder() {
		if (reqBuilder == null) {
			try {
				reqBuilder = new KMCRequestBuilder();
			} catch (Exception e) {

			}
		}
		return reqBuilder;
	}

	public static String getDns() {
		if (StringUtils.isBlank(dns)) {
			try {
				dns = EpPropertiesUtils.getInstance().getProperty(
						"trustgate.dns");
				// default value
				if (StringUtils.isBlank(dns)) {
					dns = "https://digitalid.msctrustgate.com";
				}
			} catch (Exception e) {

			}
		}
		return dns;
	}
}
