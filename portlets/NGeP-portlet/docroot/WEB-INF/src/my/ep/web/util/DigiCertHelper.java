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
import my.ep.util.MessageUtils;
import my.ep.web.constant.SPKIConst;
import my.ep.web.vo.SpkiVO;

import org.apache.commons.lang3.StringUtils;

import com.augtech.kmc.client.IKMCAPIResponse;
import com.augtech.kmc.client.IKMCAPIResponse.GenSecToken;
import com.augtech.kmc.client.InvalidKMCAPIFieldException;
import com.augtech.kmc.client.KMCAPIException;
import com.augtech.kmc.client.KMCClient;
import com.augtech.kmc.client.KMCClientFactory;
import com.augtech.kmc.client.KMCRequestBuilder;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

public class DigiCertHelper {

	private static DigiCertHelper instance;
	private static KMCClient client;
	private static String dns;
	private static String revokeDns;

	public KMCClient getKMCClient() throws KMCAPIException {
		URL resource = getClass().getResource(".");
		String path = resource.getPath();

		String substring = path.substring(0, path.indexOf("NGeP-portlet.war"));
		String filePath = substring + SPKIConst.DIGICERT_CONFIG_FILE_PATH;

		File f = new File(filePath);
		KMCClient client = KMCClientFactory.getInstance(f);
		return client;

	}

	public String generateSecurityToken(String identificationNo, String epNo) {
		String tok = null;
		try {
			KMCClient client = getClient();
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder
					.generateToken(identificationNo, epNo));
			switch (res.getStatusCode()) {
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				tok = (String) res.get(GenSecToken.TOKEN);
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
		String url4 = getRevokeDns() + "/RequestRevokeCert2.aspx?userID="
				+ identificationNo + "&orgid=" + epNo + "&token="
				+ encodedtoken;
		externalContext.redirect(url4);
	}

	public void changePIN(String identificationNo, String epNo)
			throws IOException, PortalException, SystemException {
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url = getDns() + "/kmc/RequestChangePIN.aspx?userID="
				+ identificationNo + "&token=" + encodedtoken;
		externalContext.redirect(url);
	}

	public void updateChallResp(String identificationNo, String epNo)
			throws IOException, PortalException, SystemException {
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url = getDns() + "/kmc/RequestChallRespUpdate.aspx?userID="
				+ identificationNo + "&token=" + encodedtoken;
		externalContext.redirect(url);
	}

	public void resetPIN(String identificationNo, String epNo, String email)
			throws IOException, PortalException, SystemException {
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url = getDns() + "/kmc/RequestResetPIN.aspx?userID="
				+ identificationNo + "&email=" + email + "&token="
				+ encodedtoken;
		externalContext.redirect(url);
	}

	public List<String> getChallengeQuestion(String identificationNo) {
		List<String> retVal = new ArrayList<String>();
		try {
			KMCClient client = getClient();
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder
					.getChallengeQuestions(identificationNo, 1));

			switch (res.getStatusCode()) {
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				retVal = (List<String>) res
						.get(IKMCAPIResponse.GetChallengeQuestionResonse.CHALLENGE_RESPONSES);

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

	public SpkiVO getSignedData(String identificationNo, String epNo,
			String pin, Map<Object, Object> challenges, String dataForSigning) {
		byte[] signedData = null;
		SpkiVO vo = new SpkiVO();
		try {
			KMCClient client = getClient();
			IKMCAPIResponse res = client
					.requestService(KMCRequestBuilder.signData(
							identificationNo, epNo, pin.toCharArray(),
							challenges,
							new ByteArrayInputStream(dataForSigning.getBytes())));

			switch (res.getStatusCode()) {
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				signedData = (byte[]) res
						.get(IKMCAPIResponse.SignDataResponse.SIGNATURE);
				break;

			default:
				if (res.getStatusCode() == SPKIConst.ERROR_CERT_REVOKED) {
					vo.setErrMsg(MessageUtils.getMessage(
							MessageConst.ERROR_776057,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
				} else if (res.getStatusCode() == SPKIConst.ERROR_USER_INVALID) {
					vo.setErrMsg(MessageUtils.getMessage(
							MessageConst.ERROR_776058,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
				} else if (res.getStatusCode() == SPKIConst.ERROR_PIN_BLOCKED) {
					vo.setErrMsg(MessageUtils.getMessage(
							MessageConst.ERROR_776059,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
				} else if (res.getStatusCode() == SPKIConst.ERROR_INVALID_PIN) {
					vo.setErrMsg(MessageUtils.getMessage(
							MessageConst.ERROR_776060,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
				} else if (res.getStatusCode() == SPKIConst.OPERATION_FAILED) {
					vo.setErrMsg(MessageUtils.getMessage(
							MessageConst.ERROR_776061,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
				} else {
					vo.setErrMsg(MessageUtils.getMessage(
							MessageConst.ERROR_776062,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
				}

			}
		} catch (KMCAPIException | InvalidKMCAPIFieldException e) {
			e.printStackTrace();
			vo.setErrMsg(MessageUtils.getMessage(MessageConst.ERROR_776062,
					MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale()));
		}
		vo.setSignedData(signedData);
		return vo;
	}

	public static DigiCertHelper getInstance() {
		if (instance == null) {
			try {
				instance = new DigiCertHelper();
			} catch (Exception e) {

			}
		}
		return instance;

	}

	public static KMCClient getClient() {
		if (client == null) {
			try {
				client = getInstance().getKMCClient();
			} catch (KMCAPIException e) {

			}
		}
		return client;
	}

	public static String getDns() {
		if (StringUtils.isBlank(dns)) {
			try {
				dns = EpPropertiesUtils.getInstance().getProperty(
						"digicert.dns");
				// default value
				if (StringUtils.isBlank(dns)) {
					dns = "http://ep.posdigicert.com.my";
				}
			} catch (Exception e) {

			}
		}
		return dns;
	}

	public static String getRevokeDns() {
		if (StringUtils.isBlank(revokeDns)) {
			try {
				revokeDns = EpPropertiesUtils.getInstance().getProperty(
						"digicert.revoke.dns");
				// default value
				if (StringUtils.isBlank(dns)) {
					revokeDns = "http://ep.posdigicert.com.my";
				}
			} catch (Exception e) {

			}
		}
		return revokeDns;
	}
}
