package my.ep.web.backingbean.pm.cc;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.bouncycastle.crypto.tls.Certificate;

import my.ep.biz.service.pm.common.digisign.IDigiSignSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.pm.common.digisign.DigiSignReqDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.backingbean.pm.userprofile.UserprofileBackingBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import com.augtech.kmc.client.AccountInfo;
import com.augtech.kmc.client.ContactDetail;
import com.augtech.kmc.client.IKMCAPIRequest;
import com.augtech.kmc.client.IKMCAPIResponse;
import com.augtech.kmc.client.IKMCAPIResponse.GenSecToken;
import com.augtech.kmc.client.IUploadBundle;
import com.augtech.kmc.client.KMCClient;
import com.augtech.kmc.client.KMCClientFactory;
import com.augtech.kmc.client.KMCRequestBuilder;
import com.augtech.kmc.client.OrganizationDetail;
import com.augtech.kmc.client.PersonalDetail;
import com.augtech.kmc.client.UploadBundle;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * @author Alvin
 * 
 */
@ManagedBean(name = "spkiBackingBean")
@SessionScoped
public class SpkiBackingBean {
	
	private static List<String> challs = null;
	private String challengeQues1;
	private String challsAnswer;
	private String password;
	
	public SpkiBackingBean() {}
	
	private static final Logger logger = EpLogger
			.getLogger(UserprofileBackingBean.class);

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}
	
	 public void testCreate() throws Exception {

//		   UserCreate test = new UserCreate();
//
//		      try
//		      {
//		         test.execute();
//		      }
//		      catch (Exception ex)
//		      {
//		         test.log("EXCEPTION: " + ex.getMessage());
//		      }
		      DigiSignReqDTO reqDTO = new DigiSignReqDTO();
		      IDigiSignSO digiSignSO = (IDigiSignSO) EpEjbBusinessDelegate.getInstance().getServiceObject("pm", IDigiSignSO.class);

		      		PmDigiSign pmDigiSign = new PmDigiSign();
		      		pmDigiSign.setCertificate("111111");
		      		pmDigiSign.setDigiSignId(4);
		      		pmDigiSign.setDocType("CT");
		      		pmDigiSign.setCreatedBy(new BigDecimal(1));
		      		pmDigiSign.setCreatedDate(null);
		      		pmDigiSign.setDocId(new BigDecimal(11));
		      		pmDigiSign.setDocType("CT");
		      		pmDigiSign.setOrginalData("textbeforesign");
		      		pmDigiSign.setSignedData("signedData");
//		      		reqDTO.setPmDigiSign(pmDigiSign);
		      		digiSignSO.saveOrUpdateDigiSign(reqDTO);
			}
	 
	 
	public void getChallengeQuestions(){ 
	try{
			// Create property file
			File f = new File("kmc.properties");
			// Create KMCClient with the property file
			KMCClient client = KMCClientFactory.getInstance(f);

			// Request service for user selected question for authentication
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.getChallengeQuestions("781212141111", 1));
//			List<String> challs = null;
			switch(res.getStatusCode())
			{
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				challs =  (List<String>) res.get(IKMCAPIResponse.GetChallengeQuestionResonse.CHALLENGE_RESPONSES);
				// show to the user to allow user answer the challenge
				setChallengeQues1(challs.get(0));
				for(int i = 0 ; i < challs.size() ; i ++)
				{
					System.out.println("Challange of "+(i+1)+" is : "+challs.get(i));
				}
				
				break;

				default:
				System.err.println("API return error. Code was "+res.getStatusCode()+". Message was : "+res.getStatusMessage());
				return;
			}

			if(challs.size() == 0)
			{
				System.err.println("No challanges found for the user! Data signing failed!");
				return;
			}
				
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	public String generateSecurityToken(){
		File f = new File("kmc.properties");
		String tok = null;
		try{
			KMCClient client = KMCClientFactory.getInstance(f);
			
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.generateToken("UserID", "ORG123"));
			switch(res.getStatusCode())
			{
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				tok = (String) res.get(GenSecToken.TOKEN);
				System.out.println("Token is "+tok);
				break;

			default:
				System.err.println("API return error. Message was : "+res.getStatusMessage());
			}
//			assertEquals(IKMCAPIResponse.OPERATION_SUCCESSFUL,res.getStatusCode());
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
//			fail(ex.getMessage());
		}
		
		return tok;
	}
	
	public void spkiSignTransaction() {
		try{
			// Create property file
			File f = new File("kmc.properties");
			// Create KMCClient with the property file
			KMCClient client = KMCClientFactory.getInstance(f);

//			// Request service for user selected question for authentication
//			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.getChallengeQuestions("800808081011", 1));
//			List<String> challs = null;
//			switch(res.getStatusCode())
//			{
//			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
//				challs =  (List<String>) res.get(IKMCAPIResponse.GetChallengeQuestionResonse.CHALLENGE_RESPONSES);
//				// show to the user to allow user answer the challenge
//				for(int i = 0 ; i < challs.size() ; i ++)
//				{
//					System.out.println("Challange of "+(i+1)+" is : "+challs.get(i));
//				}
//				
//				break;
//
//				default:
//				System.err.println("API return error. Code was "+res.getStatusCode()+". Message was : "+res.getStatusMessage());
//				return;
//			}
//
//			if(challs.size() == 0)
//			{IKMCAPIResponse
//				System.err.println("No challanges found for the user! Data signing failed!");
//				return;
//			}
//			
			// Simulate user selected the question and answer accordingly
			Map<Object, Object> challenges = new HashMap<Object, Object>();
			challenges.put("Name?", "Don't care!");

			// Simulate the data ready to sign
			String dataForSigning = "This is data for signing!";
			
			// Simulate usel expression that maps to a server side
 
			String pin = "123123";
			
			// Request service to signData based on the answered challenges and data 
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.signData("800808081011","cdc", pin.toCharArray(), challenges, new ByteArrayInputStream(dataForSigning.getBytes())));
			switch(res.getStatusCode())
			{
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				// application take over from here for the digital signature handling
				// Here is sample to write the signature to file...
				FileOutputStream fos = new FileOutputStream("sign.bin");
				fos.write((byte[])res.get(IKMCAPIResponse.SignDataResponse.SIGNATURE));
				fos.flush();
				fos.close();
				System.out.println("Data signing successful. Signature saved to sign.bin");
				break;

			default:
				System.err.println("API return error. Code was "+res.getStatusCode()+" Message was : "+res.getStatusMessage());
				return;
			}
			
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}
	
	public void signAction() {
//		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		    String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("signedText");
		    String value2 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("textBeforeSign");
		    String value3 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("serialNo");

//https://kmcep-dev.digicert.com.my/RequestChallRespUpdate.aspx
//https://kmcep-dev.digicert.com.my/RequestChangePIN.aspx
//https://kmcep-dev.digicert.com.my/RequestResetPIN.aspx
//https://kmcep-dev.digicert.com.my/RequestRevokeCert.aspx
	    debugLogger("this.challengeQues1====================================: "+this.challengeQues1);
	    debugLogger("SignedText====================================: "+this.challsAnswer);
	    debugLogger("this.password ====================================: "+this.password );

		}
	public void RequestChangePIN() throws IOException, PortalException, SystemException {
		
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		externalContext.redirect("https://kmcep-dev.digicert.com.my/RequestChallRespUpdate.aspx"+generateSecurityToken());
	}
	
	
	public String getChallengeQues1() {
		return challengeQues1;
	}
	public void setChallengeQues1(String challengeQues1) {
		this.challengeQues1 = challengeQues1;
	}
	public String getChallsAnswer() {
		return challsAnswer;
	}
	public void setChallsAnswer(String challsAnswer) {
		this.challsAnswer = challsAnswer;

	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}


}
