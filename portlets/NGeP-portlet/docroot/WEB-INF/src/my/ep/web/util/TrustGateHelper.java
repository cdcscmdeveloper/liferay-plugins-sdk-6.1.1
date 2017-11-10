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
import my.ep.web.constant.SPKIConst;

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

	public KMCClient getKMCClient() {
		URL resource = getClass().getResource(".");
		String path = resource.getPath();

		String substring = path.substring(0, path.indexOf("NGeP-portlet.war"));
		String filePath = substring + SPKIConst.TRUSTGATE_CONFIG_FILE_PATH;

		File f = new File("C:\\ePerolehanDemo\\epproject\\NGep-scheduler-client\\conf\\kmc-msctg-pv1.0.properties");
		KMCClient client = KMCClientFactory.getInstance(f);
		return client;
	}

	public List<String> getChallengeQuestion(String identificationNo,
			String epNo) {
		List<String> retVal = new ArrayList<String>();
		try {
			KMCClient client = getClient();
			KMCRequestBuilder reqbuilder = getReqBuilder();
			IKMCAPIResponse.GetChallengeQuestionResponse res = reqbuilder
					.getChallengeQuestions(identificationNo, 1, epNo);

			switch (res.getStatusCode()) {
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				retVal = res.CHALLENGE_RESPONSES;

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

	public byte[] getSignedData(String identificationNo, String epNo,
			String pin, Map<Object, Object> challenges, String dataForSigning) {
		byte[] signedData = null;
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
			throw new EpException(MessageConst.ERROR_776062);

		}
		return signedData;
	}

	public String generateSecurityToken(String identificationNo, String epNo) {
		String tok = null;
		try {
			KMCClient client = getClient();
			KMCRequestBuilder reqbuilder = new KMCRequestBuilder();
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
		String url4 = "http://kmc-dev.digicert.com.my/RequestRevokeCert2.aspx?userID="
				+ identificationNo
				+ "&orgid="
				+ epNo
				+ "&token="
				+ encodedtoken;
		externalContext.redirect(url4);
	}
	
	public void resetPIN(String identificationNo, String epNo, String email)
			throws IOException, PortalException, SystemException {
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url = "https://kmcep-dev.digicert.com.my/kmc/RequestResetPIN.aspx?userID="
				+ identificationNo
				+ "&email="
				+ email
				+ "&token="
				+ encodedtoken;
		externalContext.redirect(url);
	}

	public void changePIN(String identificationNo, String epNo)
			throws IOException, PortalException, SystemException {
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url = "https://kmcep-dev.digicert.com.my/kmc/RequestChangePIN.aspx?userID="
				+ identificationNo + "&token=" + encodedtoken;
		externalContext.redirect(url);
	}

	public void updateChallResp(String identificationNo, String epNo)
			throws IOException, PortalException, SystemException {
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url = "https://kmcep-dev.digicert.com.my/kmc/RequestChallRespUpdate.aspx?userID="
				+ identificationNo + "&token=" + encodedtoken;
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
}
