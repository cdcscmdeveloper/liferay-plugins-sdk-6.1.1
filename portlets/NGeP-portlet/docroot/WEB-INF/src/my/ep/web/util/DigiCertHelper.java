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
import my.ep.type.pm.RoleCode;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.constant.SPKIConst;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SpkiVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

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
	public static Logger log = Logger.getLogger(DigiCertHelper.class);

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
			log.info(" ENTER generateSecurityToken ");
			KMCClient client = getClient();
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder
					.generateToken(identificationNo, epNo));
			log.info(" OUT generateSecurityToken ");
			log.info("res.getStatusCode()==="+res.getStatusCode());
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
		log.info("tok==="+tok);
		return tok;
	}

	public void revoke(String identificationNo, String epNo) throws IOException {
		log.info("DIGICERT REVOKE:" + identificationNo + "||" + epNo);
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url4 = getRevokeDns() + "/kmc/RequestRevokeCert.aspx?userID="
				+ identificationNo + "&orgid=" + epNo + "&token="
				+ encodedtoken;
		log.info("DG CERT REVOKE URL :"+url4);
		externalContext.redirect(url4);
	}

	public void changePIN(String identificationNo, String epNo)
			throws IOException, PortalException, SystemException {
		log.info("DIGICERT CHANGE PIN:" + identificationNo + "||" + epNo);
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url = getDns() + "/kmc/RequestChangePIN.aspx?userID="
				+ identificationNo + "&token=" + encodedtoken;
		log.info("DG CERT CHANGE PIN URL :"+url);
		externalContext.redirect(url);
	}

	public void updateChallResp(String identificationNo, String epNo)
			throws IOException, PortalException, SystemException {
		log.info("DIGICERT UPDATE CHALL RESP:" + identificationNo + "||" + epNo);
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url = getDns() + "/kmc/RequestChallRespUpdate.aspx?userID="
				+ identificationNo + "&token=" + encodedtoken;
		log.info("DG CERT UPDATE CHALL URL :"+url);
		externalContext.redirect(url);
	}

	public void resetPIN(String identificationNo, String epNo, String email)
			throws IOException, PortalException, SystemException {
		log.info("DIGICERT RESET PIN:" + identificationNo + "||" + epNo + "||"
				+ email);
		String token = generateSecurityToken(identificationNo, epNo);
		String encodedtoken = URLEncoder.encode(token, "UTF-8");
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String url = getDns() + "/kmc/RequestResetPIN.aspx?userID="
				+ identificationNo + "&email=" + email + "&token="
				+ encodedtoken;
		log.info("Reset Pin URL=="+url);
		externalContext.redirect(url);
	}

	public List<String> getChallengeQuestion(String identificationNo) {
		log.info("DIGICERT GET CHALL QUEST:" + identificationNo);
		List<String> retVal = new ArrayList<String>();
		try {
			KMCClient client = getClient();
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder
					.getChallengeQuestions(identificationNo, 1));
			
			PmUser pmUser = new FacesScopes().getUserSessionBean();
			PersonnelReqDTO reqDTO = new PersonnelReqDTO();
			reqDTO.setUserId(pmUser.getUserId());
			PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelByUserId(reqDTO);
			log.info("getPersonnelId:: " + resDTO.getSmPersonnel().getPersonnelId());
			log.info("getSoftcertFlg:: " + resDTO.getSmPersonnel().getSoftcertFlg());
			
			reqDTO.setSmSupplierId(resDTO.getSmAppl().getSmSupplier().getSupplierId());//30425
			if (!SmServiceLookup.getPersonnelSO().isNotExpiredSoftcertBySupplierIdAndUserId(reqDTO)) { //30425
				log.info("#25061 getSoftcertFlg:: if log 1");
				if (resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ
						|| resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING
						|| resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REJECTED) {
					log.info("getSoftcertFlg|776063 1 :: " + resDTO.getSmPersonnel().getSoftcertFlg());
					throw new EpException(MessageConst.ERROR_DIGICERT_776063);
				} else {
					log.info("getSoftcertFlg|776064 2 :: " + resDTO.getSmPersonnel().getSoftcertFlg());
					//#29630 - start
					if (res.getStatusCode() == SPKIConst.ERROR_PIN_BLOCKED) {
						log.info("51; " + MessageConst.ERROR_DIGICERT_776059);
					} else if (res.getStatusCode() == SPKIConst.ERROR_INVALID_PIN) {
						log.info("50; " + MessageConst.ERROR_DIGICERT_776060);
					} else if (res.getStatusCode() == SPKIConst.OPERATION_FAILED) {
						log.info("0; " + MessageConst.ERROR_DIGICERT_776061);
					} else {
						log.info("99; " + MessageConst.ERROR_TRUSTGATE_776063);
					}
					//#29630 - end
					//#30855 - START
					FacesScopes fs = new FacesScopes();
					if(fs.hasRoleUser(RoleCode.SUPPLIER_TEMP.value()) || 
					   fs.hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value()) || 
					   fs.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value()) ||
					   fs.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())){
						throw new EpException(MessageConst.ERROR_DIGICERT_776064);
					}
					if(fs.hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value()) || 
					   fs.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value()) || 
					   fs.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value()) || 
					   fs.hasRoleUser(RoleCode.FL_USER.value())){
						throw new EpException(MessageConst.ERROR_DIGICERT_776065);
					}
					//#30855 - END
				}
			}

			switch (res.getStatusCode()) {
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				log.info("#25061 OPERATION_SUCCESSFUL");
				retVal = (List<String>) res
						.get(IKMCAPIResponse.GetChallengeQuestionResonse.CHALLENGE_RESPONSES);

				if (retVal.size() == 0) {
					throw new EpException(MessageConst.ERROR_DIGICERT_776051);
				}
				break;

			default:
				throw new EpException(MessageConst.ERROR_DIGICERT_776062);
			}

		} catch (Exception ex) {
			log.info("DIGICERT GET CHALL QUEST:" + identificationNo
					+ " ERROR MSG:" + ex.getMessage());
			ex.printStackTrace();
			throw new EpException(ex.getMessage());

		}
		return retVal;
	}

	public SpkiVO getSignedData(String identificationNo, String epNo,
			String pin, Map<Object, Object> challenges, String dataForSigning) {
		log.info("BEFORE DIGICERT SIGNING:" + identificationNo + "||" + epNo
				+ "||" + pin + "||" + dataForSigning);
		byte[] signedData = null;
		SpkiVO vo = new SpkiVO();
		try {
			KMCClient client = getClient();
			IKMCAPIResponse res = client
					.requestService(KMCRequestBuilder.signData(
							identificationNo, epNo, pin.toCharArray(),
							challenges,
							new ByteArrayInputStream(dataForSigning.getBytes())));

			log.info("DIGICERT SIGNING STATUS CODE:" + res.getStatusCode()
					+ " MESSAGE:" + res.getStatusMessage());

			switch (res.getStatusCode()) {
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				signedData = (byte[]) res
						.get(IKMCAPIResponse.SignDataResponse.SIGNATURE);
				break;

			default:
				if (res.getStatusCode() == SPKIConst.ERROR_CERT_REVOKED) {
					vo.setErrMsg(MessageUtils.getMessage(
							MessageConst.ERROR_DIGICERT_776057,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
				} else if (res.getStatusCode() == SPKIConst.ERROR_USER_INVALID) {
					vo.setErrMsg(MessageUtils.getMessage(
							MessageConst.ERROR_DIGICERT_776058,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
				} else if (res.getStatusCode() == SPKIConst.ERROR_PIN_BLOCKED) {
					log.info("51; "+ MessageConst.ERROR_DIGICERT_776059); //#29630
					vo.setErrMsg(MessageUtils.getMessage(
							MessageConst.ERROR_DIGICERT_776059,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
				} else if (res.getStatusCode() == SPKIConst.ERROR_INVALID_PIN) {
					log.info("50; "+ MessageConst.ERROR_DIGICERT_776060); //#29630
					vo.setErrMsg(MessageUtils.getMessage(
							MessageConst.ERROR_DIGICERT_776060,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
				} else if (res.getStatusCode() == SPKIConst.OPERATION_FAILED) {
					log.info("0; "+ MessageConst.ERROR_DIGICERT_776061); //#29630
					vo.setErrMsg(MessageUtils.getMessage(
							MessageConst.ERROR_DIGICERT_776061,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
				} else {
					log.info("99; "+MessageConst.ERROR_TRUSTGATE_776063); //#29630
					vo.setErrMsg(MessageUtils.getMessage(
							MessageConst.ERROR_DIGICERT_776062,
							MessageUtils.BUNDLE_COMMON_MESSAGES,
							new FacesScopes().getUserLocale()));
				}

			}
		} catch (KMCAPIException | InvalidKMCAPIFieldException e) {
			log.info("DIGICERT SIGNING:" + identificationNo + "||" + epNo
					+ "||" + pin + "||" + dataForSigning + " ERROR MSG:"
					+ e.getMessage());
			e.printStackTrace();
			vo.setErrMsg(MessageUtils.getMessage(MessageConst.ERROR_776062,
					MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale()));
		}
		vo.setSignedData(signedData);
		log.info("AFTER DIGICERT SIGNING:" + identificationNo + "||" + epNo
				+ "||" + pin + "||" + dataForSigning);
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
		log.info("getClient=====================================START");
		if (client == null) {
			try {
				client = getInstance().getKMCClient();
				log.info("getClient====================================="+client);
			} catch (KMCAPIException e) {
				log.info("getClient=====================================Error");
				e.printStackTrace();
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
					dns = "https://ep.posdigicert.com.my";
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
				if (StringUtils.isBlank(revokeDns)) {
					revokeDns = "https://ep.posdigicert.com.my";
				}
			} catch (Exception e) {

			}
		}
		return revokeDns;
	}
}
