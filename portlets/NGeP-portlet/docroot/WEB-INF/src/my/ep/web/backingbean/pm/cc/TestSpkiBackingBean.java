package my.ep.web.backingbean.pm.cc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import my.ep.constant.MessageConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.exception.EpException;
import my.ep.helper.EntityHelper;
import my.ep.util.MessageUtils;
import my.ep.web.util.DigiCertHelper;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.SPKIHelper;
import my.ep.web.util.TrustGateHelper;
import my.ep.web.vo.SpkiVO;

import com.augtech.kmc.client.InvalidKMCAPIFieldException;
import com.augtech.kmc.client.KMCAPIException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.msctg.cdc.pki.IKMCAPIResponse;
import com.msctg.cdc.pki.KMCRequestBuilder;

/**
 * Test SPKI
 */
@ManagedBean(name = "testSpkiBackingBean")
@SessionScoped
public class TestSpkiBackingBean extends SpkiBackingBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private transient TrustHelp trustgate = new TrustHelp();
	private transient DigiHelp digicert = new DigiHelp();
	private transient CertHelper instance = trustgate;
	private String cert = SupplierManagementConst.CERT_TRUSTGATE;
	private String idNo;
	private String epNo;
	private String email;
	private String dataForSigning;
	private String code = "Code: ";
	
	
	public String getTitle(){
		if (cert.equals(SupplierManagementConst.CERT_TRUSTGATE)){
			return "Test Trustgate";
		}
		if (cert.equals(SupplierManagementConst.CERT_DIGICERT)){
			return "Test Digicert";
		}
		return "wrong cert: "+cert;
	}
	
	/**
	 * Change PIN
	 */
	public void requestChangePIN() {
		log.info("testSPKI-RequestChangePIN:\t_"+idNo+"_\t_"+epNo);
		try {
			instance.changePIN(idNo, epNo);
		} catch (Exception e){
			err(e);
		}		
	}
	
	/**
	 * Update challenge response
	 */
	public void requestChallRespUpdate() {
		log.info("testSPKI-RequestChallRespUpdate:\t_"+idNo+"_\t_"+epNo);
		try {
			instance.updateChallResp(idNo, epNo);
		} catch (Exception e){
			err(e);
		}
	}

	/**
	 * Reset PIN
	 */
	public void requestResetPIN() {
		if (email.isEmpty()){
			err(new Exception("Email mandatory for PIN reset"));
		}
		log.info("testSPKI-RequestResetPIN:\t_"+idNo+"_\t_"+epNo+"_\t_"+email);
		try {
			instance.resetPIN(idNo, epNo, email);
		} catch (Exception e){
			err(e);
		}	
	}
	
	/**
	 * Submit to get Challenge Question
	 */
	public void submit(){
		log.info("testSPKI-submit:\t_"+idNo+"_\t_"+epNo+"_ "+helpClass());
		try {
			 setChallengeQues1(instance.getChallengeQuestion().get(0));
		} catch (Exception e){
			err(e);
		}
	}
	
	/**
	 * Sign
	 */
	public void sign(){
		log.info("testSPKI-sign:\t_"+idNo+"_\t_"+epNo+"_\t_"+getPassword()+"_\t_"
				+getChallengeQues1()+"_\t_"+getChallengeAns1()+"_\t_"+dataForSigning);
		try {
			Map<Object, Object> challenges = new HashMap<>();
			challenges.put(getChallengeQues1(), getChallengeAns1());
			
			SpkiVO vo = instance.getSignedData(idNo, epNo, getPassword(), challenges, dataForSigning);
			log.info("testSPKI-post_sign:\t"+vo.getErrMsg());
			if (!vo.getErrMsg().contains(code) && !vo.getErrMsg().contains("error")){
				vo.setErrMsg(getMsg(vo.getErrMsg()));
			}
			
			if (vo.getErrMsg().contains("Code: 1 -")){
				FacesContext.getCurrentInstance().addMessage("spki", 
						new FacesMessage(FacesMessage.SEVERITY_INFO, "", vo.getErrMsg()));
			} else {
				err(new Exception(vo.getErrMsg()));
			}
		} catch (Exception e){
			err(e);
		}
	}
	
	@Override
	public void changeChallengeQuestions() {
		log.info("testSPKI-changeChallengeQuestions:\t_"+idNo+"_\t_"+epNo);
		try {
			 setChallengeQues1(instance.getChallengeQuestion().get(0));
		} catch (Exception e){
			err(e);
		}
	}
	
	/**
	 * Reset
	 */
	public void reset(){
		idNo = "";
		epNo = "";
		email = "";
		setChallengeQues1("");
		setChallengeAns1("");
		setPassword("");
		dataForSigning = "";
	}
	
	private void err(Exception e){
		String msg = e.getMessage();
		if(msg.equals(MessageConst.ERROR_DIGICERT_776051)){
			msg = MessageUtils.getMessage(msg, MessageUtils.BUNDLE_COMMON_MESSAGES,
					new FacesScopes().getUserLocale());
		}
		FacesContext.getCurrentInstance().addMessage("spki", 
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "", msg));
		log.info(e);
	}
	
	private String getMsg(String status){
		String msg = code+status+" - ";
		switch(status.trim()){
		case "0":
			msg += "OPERATION FAILED";
			break;
		case "1":
			msg += "OPERATION SUCCESSFUL";
			break;
		case "12":
			msg += "ERROR USER INVALID";
			break;
		case "14":
			msg += "ERROR USER SUSPENDED";
			break;
		case "15":
			msg += "ERROR USER INACTIVE";
			break;
		case "43":
			msg += "ERROR CERT REVOKED";
			break;
		case "50":
			msg += "ERROR INVALID PIN";
			break;
		case "51":
			msg += "ERROR PIN BLOCKED";
			break;
		case "99":
			msg += "CERT EXPIRED";
			break;
		default:
			msg += "";
		}
		return msg;
	}
	
	private String helpClass(){
		String s = instance.getClass().toString();
		int i = s.indexOf('$');
		return i != -1 ? s.substring(i) : s;
	}
	
	//wrapper
	interface CertHelper{
		/**
		 * @param identificationNo
		 * @param epNo
		 * @throws IOException
		 * @throws PortalException
		 * @throws SystemException
		 */
		@SuppressWarnings("squid:S1160")
		void changePIN(String identificationNo, String epNo) 
				throws IOException, PortalException, SystemException;
		/**
		 * @param identificationNo
		 * @param epNo
		 * @throws IOException
		 * @throws PortalException
		 * @throws SystemException
		 */
		@SuppressWarnings("squid:S1160")
		void updateChallResp(String identificationNo, String epNo) 
				throws IOException, PortalException, SystemException;
		/**
		 * @param identificationNo
		 * @param epNo
		 * @param email
		 * @throws IOException
		 * @throws PortalException
		 * @throws SystemException
		 */
		@SuppressWarnings("squid:S1160")
		void resetPIN(String identificationNo, String epNo, String email)
				throws IOException, PortalException, SystemException;
		/**
		 * @return
		 */
		List<String> getChallengeQuestion();
		
		/**
		 * 
		 * @param identificationNo
		 * @param epNo
		 * @param pin
		 * @param challenges
		 * @param dataForSigning
		 * @return
		 */
		public SpkiVO getSignedData(String identificationNo, String epNo,
				String pin, Map<Object, Object> challenges, String dataForSigning);
	}
	
	class TrustHelp extends TrustGateHelper implements CertHelper{
//		@Override
//		public List<String> getChallengeQuestion(){
//			
//			com.msctg.cdc.pki.KMCClient client = getClient();
//			log.info("Test Client=" + client);
//			KMCRequestBuilder reqbuilder = getReqBuilder();
//			log.info("Test reqbuilder=" + reqbuilder);
//			IKMCAPIResponse.GetChallengeQuestionResponse res = reqbuilder.getChallengeQuestions(idNo, 10, epNo);
//			log.info("Test Question=" + res);
//			
//			log.info("Test TG status code: "+res.getStatusCode());
//			if(EntityHelper.isCollectionEmpty(res.CHALLENGE_RESPONSES)){
//				log.info("empty challenge responses");
//				throw new EpException(MessageConst.ERROR_TRUSTGATE_776051);
//			}
//			if(StringUtils.isBlank(getChallengeQues1())){
//				return res.CHALLENGE_RESPONSES;
//			}
//			if(getChallengeQues1().equalsIgnoreCase(res.CHALLENGE_RESPONSES.get(0))){
//				res.CHALLENGE_RESPONSES.remove(0);
//			}
//				
//			return res.CHALLENGE_RESPONSES;
//		}
		@Override
		public List<String> getChallengeQuestion() {
			log.info("------>GET CHALL QUES TG IDNO:"+idNo+" EPNO:"+epNo);
			return SPKIHelper.getInstance().getChallengeQuestion(
					SupplierManagementConst.CERT_TRUSTGATE, idNo, epNo, null);
		}
		
//		@Override
//		public SpkiVO getSignedData(String identificationNo, String epNo,
//				String pin, Map<Object, Object> challenges, String dataForSigning){
//			SpkiVO vo = new SpkiVO();
//			com.msctg.cdc.pki.KMCRequestBuilder reqbuilder = getReqBuilder();
//			com.msctg.cdc.pki.IKMCAPIResponse.SignDataResponse res = reqbuilder.signData(identificationNo, epNo, pin.toCharArray(), 
//					challenges, new ByteArrayInputStream(dataForSigning.getBytes()));
//			vo.setSignedData(res.SIGNATURE);
//			vo.setErrMsg(code+res.getStatusCode()+" - "+res.getStatusMessage());
//			return vo;
//		}
		
		@Override
		public SpkiVO getSignedData(String identificationNo, String epNo,
				String pin, Map<Object, Object> challenges,
				String dataForSigning) {
			log.info("--->GET SIGNED DATA  FOR TG IDNO:" + identificationNo + " EPNO:"
					+ epNo + " DATA SIGNING:" + dataForSigning);
			return SPKIHelper.getInstance().getSignedData(
					SupplierManagementConst.CERT_TRUSTGATE, identificationNo,
					epNo, pin, challenges, dataForSigning);
		}
	}
	
	class DigiHelp extends DigiCertHelper implements CertHelper{
//		@Override
//		public List<String> getChallengeQuestion(){
//			return super.getChallengeQuestion(idNo);
//		}
		
		@Override
		public List<String> getChallengeQuestion() {
			log.info("------>GET CHALL QUES DG IDNO:"+idNo+" EPNO:"+epNo);
			return SPKIHelper.getInstance().getChallengeQuestion(
					SupplierManagementConst.CERT_DIGICERT, idNo, epNo, null);
		}
		
//		@Override
//		public SpkiVO getSignedData(String identificationNo, String epNo,
//				String pin, Map<Object, Object> challenges, String dataForSigning){
//			SpkiVO vo = new SpkiVO();
//			com.augtech.kmc.client.KMCClient client = getClient();
//			try {
//				com.augtech.kmc.client.IKMCAPIResponse res = client.requestService(
//						com.augtech.kmc.client.KMCRequestBuilder.signData(identificationNo, epNo, pin.toCharArray(),
//						challenges, new ByteArrayInputStream(dataForSigning.getBytes())));
//				vo.setSignedData((byte[]) res
//						.get(com.augtech.kmc.client.IKMCAPIResponse.SignDataResponse.SIGNATURE));
//				vo.setErrMsg(code+res.getStatusCode()+" - "+res.getStatusMessage());
//				if (StringUtils.isBlank(res.getStatusMessage())){
//					vo.setErrMsg(String.valueOf(res.getStatusCode()));
//				}
//			} catch (KMCAPIException e) {
//				log.info(e);
//				vo.setErrMsg("DigiCert error - KMCAPIException");
//			} catch (InvalidKMCAPIFieldException e) {
//				log.info(e);
//				vo.setErrMsg("DigiCert error - InvalidKMCAPIFieldException");
//			}
//			return vo;
//		}
		
		@Override
		public SpkiVO getSignedData(String identificationNo, String epNo,
				String pin, Map<Object, Object> challenges,
				String dataForSigning) {
			log.info("--->GET SIGNED DATA  FOR DG IDNO:" + identificationNo + " EPNO:"
					+ epNo + " DATA SIGNING:" + dataForSigning);
			return SPKIHelper.getInstance().getSignedData(
					SupplierManagementConst.CERT_DIGICERT, identificationNo,
					epNo, pin, challenges, dataForSigning);
		}
	}
	
	//getters, setters
	public String getCert() {
		return cert;
	}

	public void setCert(String cert) {
		this.cert = cert;
		if (cert.equals(SupplierManagementConst.CERT_TRUSTGATE)) {
			this.instance = trustgate;
		} else {
			this.instance = digicert;
		}
		log.info("testSPKI-setCert: "+cert+"\t"+helpClass());
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo.trim();
	}

	public String getEpNo() {
		return epNo;
	}

	public void setEpNo(String epNo) {
		this.epNo = epNo.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email.trim();
	}

	public String getDataForSigning() {
		return dataForSigning;
	}

	public void setDataForSigning(String dataForSigning) {
		this.dataForSigning = dataForSigning;
	}
}
