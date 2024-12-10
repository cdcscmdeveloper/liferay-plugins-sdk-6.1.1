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
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.constant.SPKIConst;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SpkiVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

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
	//34807
	private static String revokeUrl;
	private static String changePinUrl;
	private static String resetPinUrl;
	private static String updateCRUrl;
	
	public static Logger log = Logger.getLogger(TrustGateHelper.class);

	public KMCClient getKMCClient() {
		URL resource = getClass().getResource(".");
		String path = resource.getPath();

		String substring = path.substring(0, path.indexOf("NGeP-portlet.war"));
		String filePath = substring + SPKIConst.TRUSTGATE_CONFIG_FILE_PATH;

		File f = new File(filePath);
		KMCClient client = null;
		try {
			client = KMCClientFactory.getInstance(f);
		} catch (Exception e) {
			log.info("Error while getKMCClient");
			e.printStackTrace();
		}
		return client;
	}

	public List<String> getChallengeQuestion(String identificationNo, String epNo, String selectedQuest) {
		log.info("TRUSTGATE CHALL QUEST:" + identificationNo + "||" + epNo + "||" + selectedQuest);
		List<String> retVal = new ArrayList<String>();
		try {
			log.info("Before Get Client=======================");
			KMCClient client = getClient();
			log.info("After Get Client=======================" + client);
			KMCRequestBuilder reqbuilder = getReqBuilder();
			log.info("After Get reqbuilder=======================" + reqbuilder);
			IKMCAPIResponse.GetChallengeQuestionResponse res = reqbuilder.getChallengeQuestions(identificationNo, 10, epNo);
			log.info("After Get GetChallengeQuestionResponse=======================" + res);
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setUserId(pmUser.getUserId());
			PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelByUserId(reqDTO);
			log.info("getPersonnelId:: " + resDTO.getSmPersonnel().getPersonnelId());
			log.info("getSoftcertFlg:: " + resDTO.getSmPersonnel().getSoftcertFlg());

			/* #29228 - Start */
			if (!(resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_ISSUED)) {
				/* #29228 - End */
				if (resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ || resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING
						|| resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REJECTED) {
					log.info("getSoftcertFlg|776063 1 :: " + resDTO.getSmPersonnel().getSoftcertFlg());
					throw new EpException(MessageConst.ERROR_DIGICERT_776063);
				} else {
					log.info("getSoftcertFlg|776064 2 :: " + resDTO.getSmPersonnel().getSoftcertFlg());
					// #29630 - start
					if (res.getStatusCode() == SPKIConst.ERROR_PIN_BLOCKED) {
						log.info("51; " + MessageConst.ERROR_DIGICERT_776059);
					} else if (res.getStatusCode() == SPKIConst.ERROR_INVALID_PIN) {
						log.info("50; " + MessageConst.ERROR_DIGICERT_776060);
					} else if (res.getStatusCode() == SPKIConst.OPERATION_FAILED) {
						log.info("0; " + MessageConst.ERROR_DIGICERT_776061);
					} else {
						log.info("99; " + MessageConst.ERROR_TRUSTGATE_776063);
					}
					// #29630 - end
					throw new EpException(MessageConst.ERROR_DIGICERT_776064);
				}
			}

			switch (res.getStatusCode()) {
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				log.info("#25061 OPERATION_SUCCESSFUL");
				retVal = getChallQuest(selectedQuest, res.CHALLENGE_RESPONSES);

				if (retVal.size() == 0) {
					throw new EpException(MessageConst.ERROR_TRUSTGATE_776051);
				}
				break;

			default:
				throw new EpException(MessageConst.ERROR_TRUSTGATE_776062);
			}

		} catch (Exception ex) {
			log.info("TRUSTGATE CHALL QUEST:" + identificationNo + "||" + epNo + "||" + selectedQuest + " ERROR MSG:" + ex.getMessage());
			ex.printStackTrace();
			throw new EpException(ex.getMessage());

		}
		return retVal;
	}

	private List<String> getChallQuest(String selectedQuest, List<String> questions) {
		log.info("#25061 getChallQuest");
		log.info("#25061 selectedQuest " + selectedQuest);
		List<String> retVal = new ArrayList<String>();
		for (String quest : questions) {
			log.info("#25061 quest " + quest);
			if (!quest.equals(selectedQuest)) {
				retVal.add(quest);
				break;
			}
		}
		return retVal;
	}

	public SpkiVO getSignedData(String identificationNo, String epNo, String pin, Map<Object, Object> challenges, String dataForSigning) {
		log.info("BEFORE TRUSTGATE SIGNING:" + identificationNo + "||" + epNo + "||" + pin + "||" + dataForSigning);
		byte[] signedData = null;
		SpkiVO vo = new SpkiVO();
		try {
			//KMCClient client = getClient();
			KMCRequestBuilder reqbuilder = getReqBuilder();
			IKMCAPIResponse.SignDataResponse res = reqbuilder.signData(identificationNo, epNo, pin.toCharArray(), challenges, new ByteArrayInputStream(dataForSigning.getBytes()));

			log.info("TRUSTGATE SIGNING STATUS CODE:" + res.getStatusCode() + " MESSAGE:" + res.getStatusMessage());

			switch (res.getStatusCode()) {
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				signedData = res.SIGNATURE;
				break;

			default:
				if (res.getStatusCode() == SPKIConst.ERROR_CERT_REVOKED) {
					vo.setErrMsg(MessageUtils.getMessage(MessageConst.ERROR_TRUSTGATE_776057, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));
				} else if (res.getStatusCode() == SPKIConst.ERROR_USER_INVALID) {
					vo.setErrMsg(MessageUtils.getMessage(MessageConst.ERROR_TRUSTGATE_776058, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));
				} else if (res.getStatusCode() == SPKIConst.ERROR_PIN_BLOCKED) {
					vo.setErrMsg(MessageUtils.getMessage(MessageConst.ERROR_TRUSTGATE_776059, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));
				} else if (res.getStatusCode() == SPKIConst.ERROR_INVALID_PIN) {
					vo.setErrMsg(MessageUtils.getMessage(MessageConst.ERROR_TRUSTGATE_776060, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));
				} else if (res.getStatusCode() == SPKIConst.OPERATION_FAILED) {
					vo.setErrMsg(MessageUtils.getMessage(MessageConst.ERROR_TRUSTGATE_776061, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));
					// START 28683
				} else if (res.getStatusCode() == SPKIConst.CERT_EXPIRED) {
					vo.setErrMsg(MessageUtils.getMessage(MessageConst.ERROR_TRUSTGATE_776063, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));
				} else {
					log.info("getSignedData res.getStatusCode()===" + res.getStatusCode());
					vo.setErrMsg(MessageUtils.getMessage(MessageConst.ERROR_TRUSTGATE_776062, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));
				}
				// END 28683

			}
		} catch (Exception e) {
			log.info("TRUSTGATE SIGNING:" + identificationNo + "||" + epNo + "||" + pin + "||" + dataForSigning + "  ERROR MSG:" + e.getMessage());
			e.printStackTrace();
			vo.setErrMsg(MessageUtils.getMessage(MessageConst.ERROR_776062, MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes().getUserLocale()));
		}
		vo.setSignedData(signedData);
		log.info("AFTER TRUSTGATE SIGNING:" + identificationNo + "||" + epNo + "||" + pin + "||" + dataForSigning);
		return vo;
	}

	public String generateSecurityToken(String identificationNo, String epNo) {
		String tok = null;
		try {
			// KMCClient client = getClient();
			KMCRequestBuilder reqbuilder = getReqBuilder();
			IKMCAPIResponse.GenSecToken res = reqbuilder.generateToken(identificationNo, epNo);
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
		log.info("TRUSTGATE REVOKE:" + identificationNo + "||" + epNo);
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		//ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

		String url4 = getDns() + "/" + getTgUrl("revoke") + encodedtoken;// 34807
		RequestContext.getCurrentInstance().execute("window.open('" + url4 + "', '_blank')");
		// externalContext.redirect(url4);
	}

	public void resetPIN(String identificationNo, String epNo, String email)
			throws IOException, PortalException, SystemException {
		log.info("TRUSTGATE RESET:" + identificationNo + "||" + epNo + "||"
				+ email);
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();

		String url = getDns() + "/" + getTgUrl("resetpin") + encodedtoken;//34807

		externalContext.redirect(url);
	}

	public void changePIN(String identificationNo, String epNo)
			throws IOException, PortalException, SystemException {
		log.info("TRUSTGATE CHANGE PIN:" + identificationNo + "||" + epNo);
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();

		String url = getDns() + "/" + getTgUrl("changepin") + encodedtoken;//34807

		externalContext.redirect(url);
	}

	public void updateChallResp(String identificationNo, String epNo)
			throws IOException, PortalException, SystemException {
		log.info("TRUSTGATE UPDATE CHALL RESP:" + identificationNo + "||"
				+ epNo);
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();

		String url = getDns() + "/" + getTgUrl("updateCR") + encodedtoken;//34807

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
		log.info("getClient=====================================START");
		if (client == null) {
			try {
				client = getInstance().getKMCClient();
				log.info("getClient====================================="+client);
			} catch (Exception e) {
				log.info("getClient=====================================Error");
				e.printStackTrace();
			}
		}
		return client;
	}

	public static KMCRequestBuilder getReqBuilder() {
		log.info("reqBuilder=====================================START");

		if (reqBuilder == null) {
			try {
				reqBuilder = new KMCRequestBuilder();
				log.info("reqBuilder=====================================");
			} catch (Exception e) {
				log.info("reqBuilder=====================================Error");
				e.printStackTrace();
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
	//34807
	/*
	 * To get TG URL by type
	 * 
	 * */
	public static String getTgUrl(String type) {
		if("revoke".equalsIgnoreCase(type)){
			if (StringUtils.isBlank(revokeUrl)) {
				revokeUrl = EpPropertiesUtils.getInstance().getProperty("trustgate.revoke");
			}
			return revokeUrl;
		}else if("updateCR".equalsIgnoreCase(type)){
			if (StringUtils.isBlank(updateCRUrl)) {
				updateCRUrl = EpPropertiesUtils.getInstance().getProperty("trustgate.updatecr");
			}
			return updateCRUrl;
		}else if("resetpin".equalsIgnoreCase(type)){
			if (StringUtils.isBlank(resetPinUrl)) {
				resetPinUrl = EpPropertiesUtils.getInstance().getProperty("trustgate.resetpin");
			}
			return resetPinUrl;
		}else if("changepin".equalsIgnoreCase(type)){
			if (StringUtils.isBlank(changePinUrl)) {
				changePinUrl = EpPropertiesUtils.getInstance().getProperty("trustgate.changepin");
			}
			return changePinUrl;
		}else{
			return "";
		}		
	}
}
