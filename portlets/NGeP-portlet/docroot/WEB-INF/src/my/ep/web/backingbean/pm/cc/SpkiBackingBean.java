package my.ep.web.backingbean.pm.cc;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.bouncycastle.crypto.tls.Certificate;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Base64Encoder;

import my.ep.biz.service.pm.common.digisign.IDigiSignSO;
import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.dto.pm.common.digisign.DigiSignReqDTO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.logging.LogKey;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.integration.common.IntegrationConstants;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import com.augtech.kmc.client.AccountInfo;
import com.augtech.kmc.client.ContactDetail;
import com.augtech.kmc.client.IKMCAPIRequest;
import com.augtech.kmc.client.IKMCAPIResponse;
import com.augtech.kmc.client.IKMCAPIResponse.GenSecToken;
import com.augtech.kmc.client.IUploadBundle;
import com.augtech.kmc.client.KMCAPIException;
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
import com.sun.xml.internal.fastinfoset.util.StringArray;

/**
 * @author Alvin
 * 
 */
@ManagedBean(name = "spkiBackingBean")
@SessionScoped
public class SpkiBackingBean {
	public static final String PATH = "NGeP-portlet.war/WEB-INF/config/soft-certificate/kmc.properties";

	private static List<String> challs = null;
	private String challengeQues1;
	private String challengeAns1;
	private String password;
	private PmDigiSign pmDigiSign = new PmDigiSign();
 
	FacesScopes facesScope = new FacesScopes();

	private String s;

	public SpkiBackingBean() {}
	
	private static final Logger logger = EpLogger
			.getLogger(SpkiBackingBean.class);

	private void debugLogger(Object message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
		logger.info(message);
	}
	
	private KMCClient getClient() throws KMCAPIException {
		URL resource = getClass().getResource(".");
		String path = resource.getPath();
		System.out.println("path value with (.) :"+path);
		
		String substring = path.substring(0,path.indexOf("NGeP-portlet.war"));
		String filePath = substring+PATH;
		System.out.println("filePath :"+filePath);
		
		// Create property file
		File f = new File(filePath);
//		File f = new File("C:/ePerolehanDemo/liferay-portal-6.1.1-ce-ga2/jboss-7.1.1/standalone/deployments/NGeP-portlet.war/WEB-INF/config/soft-certificate/kmc.properties");
		// Create KMCClient with the property file
		KMCClient client = KMCClientFactory.getInstance(f);
		return client;
		
	}
	
	public void getChallengeQuestions(){ 
	try{
			KMCClient client = getClient();
			
			// Request service for user selected question for authentication
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.getChallengeQuestions("890823108979", 1));
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
		String tok = null;
		try{
			KMCClient client = getClient();
			
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.generateToken(getDigiCertUserId(),getSupplierEpNo()));
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
	
	public String spkiSignTransaction(String originalText) {
		try{
//			String s ;
			KMCClient client = getClient();

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
			challenges.put(challengeQues1, challengeAns1);

			// Simulate the data ready to sign
			String dataForSigning = originalText;
			
			// Simulate usel expression that maps to a server side
 
			String pin = getPassword();
	
			System.out.println("SIGN" + getChallengeQues1() + getChallengeAns1() + getPassword());
			// Request service to signData based on the answered challenges and data 
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.signData("890823108979","eP-1008N0001", pin.toCharArray(), challenges, new ByteArrayInputStream(dataForSigning.getBytes())));
			switch(res.getStatusCode())
			{
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				// application take over from here for the digital signature handling
				// Here is sample to write the signature to file...
//				FileOutputStream fos = new FileOutputStream("sign.bin");
//				fos.write((byte[])res.get(IKMCAPIResponse.SignDataResponse.SIGNATURE));
//				fos.flush();
//				fos.close();
//				fos.write((byte[])res.get(IKMCAPIResponse.SignDataResponse.SIGNATURE));
				s = new String(((byte[])res.get(IKMCAPIResponse.SignDataResponse.SIGNATURE)));
				System.out.println("Data signing successful. SIGNEDTEXT " +  s);
				break;

			default:
				System.err.println("API return error. Code was "+res.getStatusCode()+" Message was : "+res.getStatusMessage());
//				return "SIGNEDTEXT";
				throw new EpException(MessageConst.ERROR_776051);
		}
			
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return s;
	}
	public void spkiSignAction(com.sun.faces.facelets.el.ContextualCompositeMethodExpression obj) {
        String textBeforeSign = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("inputTextBeforeSign");
	    String signedText = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("signedText");
	    String serialNo = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("serialNo");
//	    spkiSignTransaction();
	    Long userId = (Long) MDC.get(LogKey.USER_ID);
		Timestamp date = new Timestamp(System.currentTimeMillis());
		
		pmDigiSign.setCreatedBy(userId);
        pmDigiSign.setCreatedDate(date);
        pmDigiSign.setOrginalData(textBeforeSign);
        pmDigiSign.setSignedData(signedText);
//		obj.invoke(FacesContext.getCurrentInstance().getELContext(), new Object[]{pmDigiSign});

	    debugLogger("textBeforeSign==================================: "+textBeforeSign);
//	    debugLogger("reflectAction=====================================: "+ challsAnswer+ password);
	}
	public void signAction(my.ep.persistence.entity.pm.common.PmDigiSign pmDigiSign) {

		    DigiSignReqDTO reqDTO = new DigiSignReqDTO();
	        IDigiSignSO digiSignSO = (IDigiSignSO) EpEjbBusinessDelegate.getInstance().getServiceObject("pm", IDigiSignSO.class);
	        reqDTO.setPmDigiSign(pmDigiSign);
	        digiSignSO.saveOrUpdateDigiSign(reqDTO);

	    debugLogger("========================: "+pmDigiSign.getSignedData());

	}
	public void setChallengeQues1(String challengeQues1) {
		this.challengeQues1 = challengeQues1;
	}

	public String getDigiCertUserId() {
				
		return facesScope.getUserSessionBean().getIdentificationNo();	
	}

	public String getSupplierEmail() {
		
		return facesScope.getUserSessionBean().getEmail();
	}

	public String getSupplierEpNo() {
		ISupplierSearchSO smSupplierSO = null;
		System.out.println("ISearchResultBean<SmSupplier> pageChangeListener");
		smSupplierSO = (ISupplierSearchSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISupplierSearchSO.class);
	    Long userId = facesScope.getUserSessionBean().getUserId();

		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setUserId(userId);
		SupplierSearchResDTO res = smSupplierSO.retriveSupplierProfiles(reqDTO);

		return res.getResultBean().getResultList().get(0).getEpNo();	
	}
	
	public static void main(String[] args) {
		try{
			// Create property file
			File f = new File("kmc.properties");
			// Create KMCClient with the property file
			KMCClient client = KMCClientFactory.getInstance(f);

			// Request service for user selected question for authentication
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.getChallengeQuestions("800808081011", 1));
//			List<String> challs = null;
			switch(res.getStatusCode())
			{
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				challs =  (List<String>) res.get(IKMCAPIResponse.GetChallengeQuestionResonse.CHALLENGE_RESPONSES);
				// show to the user to allow user answer the challenge
//				setChallengeQues1(challs.get(0));
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

	public String getChallengeQues1() {
		return challengeQues1;
	}

	public String getChallengeAns1() {
		return challengeAns1;
	}

	public void setChallengeAns1(String challengeAns1) {
		this.challengeAns1 = challengeAns1;
	}

	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}
}
