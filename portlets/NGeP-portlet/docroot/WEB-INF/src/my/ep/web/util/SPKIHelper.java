package my.ep.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import my.ep.constant.MessageConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.sm.common.spki.SpkiReqDTO;
import my.ep.dto.sm.common.spki.SpkiResDTO;
import my.ep.exception.EpException;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SpkiVO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class SPKIHelper {
	public static Logger log = Logger.getLogger(SPKIHelper.class);
	private static SPKIHelper instance;
	private static String SPKI_TLS_LATEST = EpPropertiesUtils.getInstance().getProperty("spki.tls.on");

	public boolean isUsingSpkiTLSLatest() {
		return (SPKI_TLS_LATEST != null && SPKI_TLS_LATEST.trim().equalsIgnoreCase("1")) ? true : false;
	}

	public static SPKIHelper getInstance() {
		if (instance == null) {
			try {
				instance = new SPKIHelper();
			} catch (Exception e) {

			}
		}
		return instance;

	}
	
	public SpkiVO getSignedData(String provider, String identificationNo, String epNo, String pin, Map<Object, Object> challenges,
			String dataForSigning) {
		log.info("BEFORE SIGNING:" + identificationNo + "||" + epNo + "||" + dataForSigning + "||" + provider);
		SpkiResDTO resDTO = null;
		byte[] signedData = null;
		SpkiVO vo = new SpkiVO();
		SpkiReqDTO reqDTO = new SpkiReqDTO();
		reqDTO.setEpNo(epNo);
		reqDTO.setIdentificationNo(identificationNo);
		reqDTO.setSoftCertProvider(provider);
		reqDTO.setDataForSigning(dataForSigning);
		reqDTO.setPin(pin);
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		if (challenges != null && !challenges.isEmpty()) {
			for (Map.Entry<Object, Object> entry : challenges.entrySet()) {
				Object question = entry.getKey();
				Object answer = entry.getValue();
				reqDTO.setSelectedQuestion(question.toString());
				reqDTO.setAnswer(answer.toString());
			}
		}
		try {
			resDTO = SmServiceLookup.getSpkiSO().signData(reqDTO);
			signedData = resDTO.getSignedData();
			vo.setStatusCode(resDTO.getStatusCode());
			vo.setStatusMsg(resDTO.getStatusMsg());
			vo.setErrMsg(resDTO.getErrorMessage());
		} catch (Exception e) {
			log.info("SIGNING:" + identificationNo + "||" + epNo + "||" + dataForSigning + "  ERROR MSG:" + e.getMessage());
			vo.setErrMsg(MessageUtils.getMessage(
					provider.equals(SupplierManagementConst.CERT_TRUSTGATE) ? MessageConst.ERROR_TRUSTGATE_776062
							: MessageConst.ERROR_DIGICERT_776062,
					new String[] { },
					MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale()));
		}
		vo.setSignedData(signedData);
		return vo;
	}
	
	public String generateSecurityToken(String provider, String identificationNo, String epNo) {
		String tok = null;
		SpkiReqDTO reqDTO = new SpkiReqDTO();
		reqDTO.setEpNo(epNo);
		reqDTO.setIdentificationNo(identificationNo);
		reqDTO.setSoftCertProvider(provider);
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		try {
			SpkiResDTO resDTO = SmServiceLookup.getSpkiSO().generateSecurityToken(reqDTO);
			if (!StringUtils.isBlank(resDTO.getErrorMessage())) {
				throw new EpException(resDTO.getErrorMessage());
			}
			tok = resDTO.getToken();
		} catch (Exception ex) {
			log.info("Error call generateSecurityToken for:" + identificationNo + "||" + epNo + "||" + " ERROR MSG:" + ex.getMessage());
			ex.printStackTrace();
			throw new EpException(provider.equals(SupplierManagementConst.CERT_TRUSTGATE) ? MessageConst.ERROR_TRUSTGATE_776062
					: MessageConst.ERROR_DIGICERT_776062);

		}
		return tok;
	}

	public List<String> getChallengeQuestion(String provider, String identificationNo, String epNo, String selectedQuest) {
		SpkiReqDTO reqDTO = new SpkiReqDTO();
		reqDTO.setEpNo(epNo);
		reqDTO.setIdentificationNo(identificationNo);
		reqDTO.setSelectedQuestion(selectedQuest);
		reqDTO.setSoftCertProvider(provider);
		reqDTO.setLanguageCode(new FacesScopes().getUserLocale().getLanguage());
		List<String> retVal = new ArrayList<String>();
		try {
			SpkiResDTO resDTO = SmServiceLookup.getSpkiSO().retrieveChallengeQuestions(reqDTO);
			if (!StringUtils.isBlank(resDTO.getErrorMessage())) {
				throw new EpException(resDTO.getErrorMessage());
			}
			retVal = resDTO.getChallQuestions();
		} catch (Exception ex) {
			log.info("Error call chall question for:" + identificationNo + "||" + epNo + "||" + selectedQuest + " ERROR MSG:" + ex.getMessage());
			ex.printStackTrace();
			throw new EpException(provider.equals(SupplierManagementConst.CERT_TRUSTGATE) ? MessageConst.ERROR_TRUSTGATE_776062
					: MessageConst.ERROR_DIGICERT_776062);

		}
		return retVal;
	}
}
