package my.ep.web.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import my.ep.dto.sm.common.spki.SpkiReqDTO;
import my.ep.dto.sm.common.spki.SpkiResDTO;
import my.ep.exception.EpException;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.util.sm.helper.SmServiceLookup;
import my.ep.web.vo.SpkiVO;

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
		log.info("BEFORE SIGNING:" + identificationNo + "||" + epNo + "||" + pin + "||" + dataForSigning + "||" + provider);
		SpkiResDTO resDTO = null;
		byte[] signedData = null;
		SpkiVO vo = new SpkiVO();
		SpkiReqDTO reqDTO = new SpkiReqDTO();
		reqDTO.setEpNo(epNo);
		reqDTO.setIdentificationNo(identificationNo);
		reqDTO.setSoftCertProvider(provider);
		reqDTO.setDataForSigning(dataForSigning);
		reqDTO.setPin(pin);
		if (challenges != null && !challenges.isEmpty()) {
			for (Map.Entry<Object, Object> entry : challenges.entrySet()) {
				Object question = entry.getKey();
				Object answer = entry.getValue();
				reqDTO.setSelectedQuestion(question.toString());
				reqDTO.setAnswer(answer.toString());
				log.info("question: " + question);
				log.info("answer: " + answer);
			}
		}
		try {
			resDTO = SmServiceLookup.getSpkiSO().signData(reqDTO);
			signedData = resDTO.getSignedData();
		} catch (Exception e) {
			log.info("SIGNING:" + identificationNo + "||" + epNo + "||" + pin + "||" + dataForSigning + "  ERROR MSG:" + e.getMessage());
			vo.setErrMsg(resDTO.getErrorMessage());
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
		try {
			SpkiResDTO resDTO = SmServiceLookup.getSpkiSO().generateSecurityToken(reqDTO);
			tok = resDTO.getToken();
		} catch (Exception ex) {
			log.info("Error call generateSecurityToken for:" + identificationNo + "||" + epNo + "||" + " ERROR MSG:" + ex.getMessage());
			ex.printStackTrace();
			throw new EpException(ex.getMessage());

		}
		return tok;
	}

	public List<String> getChallengeQuestion(String provider, String identificationNo, String epNo, String selectedQuest) {
		SpkiReqDTO reqDTO = new SpkiReqDTO();
		reqDTO.setEpNo(epNo);
		reqDTO.setIdentificationNo(identificationNo);
		reqDTO.setSelectedQuestion(selectedQuest);
		reqDTO.setSoftCertProvider(provider);
		List<String> retVal = new ArrayList<String>();
		try {
			SpkiResDTO resDTO = SmServiceLookup.getSpkiSO().retrieveChallengeQuestions(reqDTO);
			retVal = resDTO.getChallQuestions();
		} catch (Exception ex) {
			log.info("Error call chall question for:" + identificationNo + "||" + epNo + "||" + selectedQuest + " ERROR MSG:" + ex.getMessage());
			ex.printStackTrace();
			throw new EpException(ex.getMessage());

		}
		return retVal;
	}
}
