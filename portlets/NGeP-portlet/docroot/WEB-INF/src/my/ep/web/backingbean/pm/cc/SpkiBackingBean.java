package my.ep.web.backingbean.pm.cc;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.biz.service.sm.search.ISupplierSearchSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.dto.sm.search.SupplierSearchReqDTO;
import my.ep.dto.sm.search.SupplierSearchResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.util.EpPropertiesUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.augtech.kmc.client.IKMCAPIResponse;
import com.augtech.kmc.client.IKMCAPIResponse.GenSecToken;
import com.augtech.kmc.client.KMCAPIException;
import com.augtech.kmc.client.KMCClient;
import com.augtech.kmc.client.KMCClientFactory;
import com.augtech.kmc.client.KMCRequestBuilder;
import com.sun.faces.facelets.el.ContextualCompositeMethodExpression;

/**
 * @author Alvin
 * 
 */
@ManagedBean(name = "spkiBackingBean")
@SessionScoped
public class SpkiBackingBean  extends BaseBackingBean implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String PATH = "NGeP-portlet.war/WEB-INF/config/soft-certificate/kmc.properties";
	//	public static final int OPERATION_FAILED  = 0;
	//	public static final int OPERATION_SUCCESSFUL = 1;
	//	public static final int ERROR_USER_EXISTED 	= 10;
	//	public static final int ERROR_USER_NOT_FOUND = 11;
	//	public static final int ERROR_USER_INVALID 	= 12;
	//	public static final int ERROR_USER_REVOKED 	= 13;
	//	public static final int ERROR_USER_SUSPENDED = 14;
	//	public static final int ERROR_USER_INACTIVE 	= 15;
	//	public static final int ERROR_USER_CHALLENGE_EXIST = 16;
	//	public static final int ERROR_USER_ROAMING_EXIST = 17;
	//	public static final int ERROR_WRONG_RESPONSE  = 20;
	//	public static final int ERROR_INVALID_CERT = 21;
	//	public static final int ERROR_POLICY_VIOLATION	= 22;
	//	public static final int ERROR_BACKEND_SERVICES_ERROR = 23;
	//	public static final int ERROR_CERT_GOOD = 40;
	//	public static final int ERROR_CERT_EXPIRED = 41;
	//	public static final int ERROR_CERT_NOT_YET_VALID = 42;
	//	public static final int ERROR_CERT_REVOKED = 43;
	//	public static final int ERROR_CERT_NOT_FOUND	= 44;
	//	public static final int ERROR_CERT_SUSPENDED	= 45;
	//	public static final int ERROR_INVALID_PIN = 50;
	//	public static final int ERROR_PIN_BLOCKED = 51;
	//	public static final int ERROR_PIN_RESETTING = 52;
	//	public static final int ERROR_FATAL_SIGN_ERROR = 99;
	//	public static final int ERROR_SERVICE_RUNTIME = 110;
	//	public static final int ERROR_SERVICE_CONNECTION = 111;

	private static List<String> challs = null;
	private String challengeQues1;
	private String challengeAns1;
	private String password;
	private PmDigiSign pmDigiSign = new PmDigiSign();
	private FacesScopes facesScope = new FacesScopes();
	private byte[] signedData;

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

	public String getSpkiEnabledProperty() {
		EpPropertiesUtils prop = EpPropertiesUtils.getInstance();
		System.out.println("getSpkiEnabledProperty :"+ prop.getProperty("spki.enabled"));
		return prop.getProperty("spki.enabled");
	}

	public void getChallengeQuestions() {
		if(getSpkiEnabledProperty().equalsIgnoreCase("true")){
			try{
				KMCClient client = getClient();

				// Request service for user selected question for authentication

				String loginId = new FacesScopes().getUserScreenName();

				IUserSO userSO =(IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
				UserReqDTO reqDTO = new UserReqDTO();
				reqDTO.setLoginId(loginId);
				UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);
				if(resDTO.getPmUser()==null){
					throw new EpException("PM User is null");
				}
				IKMCAPIResponse res = client.requestService(KMCRequestBuilder.getChallengeQuestions(resDTO.getPmUser().getIdentificationNo(), 1));
				//				List<String> challs = null;
				switch(res.getStatusCode())
				{
				case IKMCAPIResponse.OPERATION_SUCCESSFUL:
					challs =  (List<String>) res.get(IKMCAPIResponse.GetChallengeQuestionResonse.CHALLENGE_RESPONSES);
					// show to the user to allow user answer the challenge
					if(challs.size() == 0)
					{
						System.out.println("No challanges found for the user! ");
						throw new EpException(MessageConst.ERROR_776051);


					}else{
						setChallengeQues1(challs.get(0));
						RequestContext.getCurrentInstance().execute("spki_dlg.show()");
					}

					break;

				default:
					System.out.println("GetChallengeQuestions API return error. Code was "+res.getStatusCode()+". Message was : "+res.getStatusMessage());
					throw new EpException("GetChallengeQuestions API return error. Code was "+res.getStatusCode()+". Message was : "+res.getStatusMessage());

				}


			} catch (Exception ex)
			{
				ex.printStackTrace();
				throw new EpException(ex.getMessage());

			}	
		} /*else {
			RequestContext.getCurrentInstance().execute("spki_dlg.show()");
		}*/
	}

	public String generateSecurityToken(){
		String tok = null;
		try{
			KMCClient client = getClient();

			String loginId = new FacesScopes().getUserScreenName();

			IUserSO userSO =(IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setLoginId(loginId);
			UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);
			if(resDTO.getPmUser()==null){
				throw new EpException("PM User is null");
			}

			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.generateToken(getDigiCertUserId(),getSupplierEpNo(resDTO.getPmUser().getUserId())));
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

	public byte[] spkiEnabledSignTransaction(String originalText) {
		try{
			KMCClient client = getClient();
			Map<Object, Object> challenges = new HashMap<Object, Object>();
			challenges.put(challengeQues1, challengeAns1);

			String dataForSigning = originalText;


			String pin = getPassword();

			System.out.println("getChallengeQues1: " + getChallengeQues1());
			System.out.println("getChallengeAns1: " + getChallengeAns1());
			System.out.println("getPassword: " + getPassword());
			System.out.println("dataForSigning: " + dataForSigning);
			// Request service to signData based on the answered challenges and data 

			String loginId = new FacesScopes().getUserScreenName();

			IUserSO userSO =(IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
			UserReqDTO reqDTO = new UserReqDTO();
			reqDTO.setLoginId(loginId);
			UserResDTO resDTO = userSO.retrieveUserByLoginId(reqDTO);
			if(resDTO.getPmUser()==null){
				throw new EpException("PM User is null");
			}



			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.signData(resDTO.getPmUser().getIdentificationNo(),getSupplierEpNo(resDTO.getPmUser().getUserId()), pin.toCharArray(), challenges, new ByteArrayInputStream(dataForSigning.getBytes())));


			switch(res.getStatusCode())
			{
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				signedData = (byte[])res.get(IKMCAPIResponse.SignDataResponse.SIGNATURE);
				logger.info("Data signing successful. SIGNEDTEXT " +  signedData);
				break;

			default:
				//				FacesContext.getCurrentInstance().addMessage("spki", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "SPKI Signing error."));	
				System.err.println("API return error. Code was "+res.getStatusCode()+" Message was : "+res.getStatusMessage());
				throw new EpException("API return error. Code was "+res.getStatusCode()+" Message was : "+res.getStatusMessage());
				//TODO Please remove this line if want to turn on the SPKI
				//				RequestContext.getCurrentInstance().execute("spki_dlg.hide()");
				//				signedData = "DEFAULT SIGNED TEXT1";
				//				break;
			}

		} catch (Exception ex)
		{
			//FacesContext.getCurrentInstance().addMessage("spki", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "SPKIs Signing error."));	
			throw new EpException(MessageConst.ERROR_776052 + ":"+ex.getMessage());

		}
		return signedData;
	}

	public byte[] spkiSignTransaction(String originalText) {


		if(getSpkiEnabledProperty().equalsIgnoreCase("true")){
			return 	spkiEnabledSignTransaction(originalText);
		}else{
			return  null;
		}
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

	public String getSupplierEpNo(Long userId) {
		ISupplierSearchSO smSupplierSO = null;
		System.out.println("ISearchResultBean<SmSupplier> pageChangeListener");
		smSupplierSO = (ISupplierSearchSO) BusinessDelegate.getInstance()
				.getServiceObject("sm", ISupplierSearchSO.class);
		//Long userId = facesScope.getUserSessionBean().getUserId();

		SupplierSearchReqDTO reqDTO = new SupplierSearchReqDTO();
		reqDTO.setUserId(userId);
		SupplierSearchResDTO res = smSupplierSO.retriveSupplierProfiles(reqDTO);
		System.out.println("res.getResultBean().getResultList():"+res.getResultBean().getResultList());
		return res.getResultBean().getResultList().get(0).getEpNo();	
	}
	
	/**
	 * To perform SPKI Signing Action
	 * 
	 * @param postSignActionMtdExpr
	 * 		the postSignAction method expression to invoke
	 * @param enableAutoUpdateMtdExpr
	 * 		the enableAutoUpdate method expression to invoke
	 * @return
	 */
	public String spkiSignAction(ContextualCompositeMethodExpression postSignActionMtdExpr, ContextualCompositeMethodExpression enableAutoUpdateMtdExpr) {
		logger.info("spkiSignAction");
		
		Object returnObj = null;
		
		if (postSignActionMtdExpr != null) {
			returnObj = postSignActionMtdExpr.invoke(
					FacesContext.getCurrentInstance().getELContext(),
					new Object[] { pmDigiSign });
		}
		
		if (enableAutoUpdateMtdExpr != null) {
			enableAutoUpdateMtdExpr.invoke(
					FacesContext.getCurrentInstance().getELContext(),
					new Object[] {});
		}
		
		RequestContext.getCurrentInstance().execute("spki_dlg.hide()");
		
		if (returnObj != null) {
			return returnObj.toString();
		} else {
			return null;
		}

	}

	public static void main2(String[] args) {
		try{
			// Create property file
			File f = new File("C:/ePerolehanDemo/liferay-portal-6.1.1-ce-ga2/jboss-7.1.1/standalone/deployments/NGeP-portlet.war/WEB-INF/config/soft-certificate/kmc.properties");
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

	public static void main(String[] args) {
		try{
			File f = new File("C:/ePerolehanDemo/liferay-portal-6.1.1-ce-ga2/jboss-7.1.1/standalone/deployments/NGeP-portlet.war/WEB-INF/config/soft-certificate/kmc.properties");
			KMCClient client = KMCClientFactory.getInstance(f);

			// Request service for user selected question for authentication

			String test1 = "54322211114333";
			String test2 = "680410061119";

			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.getChallengeQuestions(test2, 1));

			System.out.println("res.getStatusCode():"+res.getStatusCode());
			//				List<String> challs = null;
			switch(res.getStatusCode())
			{
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				challs =  (List<String>) res.get(IKMCAPIResponse.GetChallengeQuestionResonse.CHALLENGE_RESPONSES);
				// show to the user to allow user answer the challenge
				if(challs.size() == 0)
				{
					System.err.println("No challanges found for the user!");
					//						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Signing error."));	
					//						throw new EpException(MessageConst.ERROR_776051);
					//						return;
				}else{
					//setChallengeQues1(challs.get(0));
					System.out.println("****"+challs.get(0));
					RequestContext.getCurrentInstance().execute("spki_dlg.show()");
				}

				break;

			default:
				System.err.println("GetChallengeQuestions API return error. Code was "+res.getStatusCode()+". Message was : "+res.getStatusMessage());
				//					throw new EpException(MessageConst.ERROR_776051);
				//					return;
			}







		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}




	/////hktan
	public static void main1(String[] args) {
		try{
			byte[] signedData ;
			// Create property file
			String challengeQues1 = new String("In what city you were born?");
			String challengeAns1 = new String("welcome1");
			File f = new File("C:/ePerolehanDemo/liferay-portal-6.1.1-ce-ga2/jboss-7.1.1/standalone/deployments/NGeP-portlet.war/WEB-INF/config/soft-certificate/kmc.properties");
			// Create KMCClient with the property file


			KMCClient client = KMCClientFactory.getInstance(f);
			// Simulate user selected the question and answer accordingly
			Map<Object, Object> challenges = new HashMap<Object, Object>();
			challenges.put(challengeQues1, challengeAns1);

			// Simulate the data ready to sign
			String dataForSigning = "originalText";

			String pin = "efgh1234";

			//System.out.println("SIGN " + getDigiCertUserId() + getSupplierEpNo() + getChallengeQues1() + getChallengeAns1() + getPassword());
			// Request service to signData based on the answered challenges and data 
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.signData("890823102233","STRONG HABIS", pin.toCharArray(), challenges, new ByteArrayInputStream(dataForSigning.getBytes())));


			System.out.println("res.getStatusCode()"+res.getStatusCode());
			switch(res.getStatusCode())
			{
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				//			RequestContext.getCurrentInstance().execute("spki_dlg.hide()");
				FileOutputStream fos = new FileOutputStream("sign3333.bin");
				fos.flush();
				fos.write((byte[])res.get(IKMCAPIResponse.SignDataResponse.SIGNATURE));
				fos.close();


				/*	File ff = new File("sign3333.bin");
			FileInputStream fis = new FileInputStream(ff);
			byte[] buf = new byte[(int) ff.length()];
			DataInputStream dis = new DataInputStream(fis);
			dis.readFully(buf);
			dis.close();
			fis.close();*/

				signedData = (byte[])res.get(IKMCAPIResponse.SignDataResponse.SIGNATURE);
				//System.out.println("Data signing successful. SIGNEDTEXT " +  signedData);



				IKMCAPIResponse res1 = client.requestService(KMCRequestBuilder.verifyData(new ByteArrayInputStream(dataForSigning.getBytes()),signedData));
				switch(res.getStatusCode())
				{
				case IKMCAPIResponse.OPERATION_SUCCESSFUL:
					System.out.println("Data signature is a valid signature");
					break;
				default:
					System.err.println("API return error. Code was "+res1.getStatusCode()+" Message was : "+res1.getStatusMessage());
					return;
				}

				break;

			default:

				System.err.println("API return error. Code was "+res.getStatusCode()+" Message was : "+res.getStatusMessage());
				//throw new EpException(MessageConst.ERROR_776052);
				//			signedData = "DEFAULT SIGNED TEXT";
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




	public byte [] SpkiTesting() {
		byte[] signedData = null ;
		try{

			// Create property file
			String challengeQues1 = new String("In what city you were born?");
			String challengeAns1 = new String("welcome1");
			File f = new File("C:/ePerolehanDemo/liferay-portal-6.1.1-ce-ga2/jboss-7.1.1/standalone/deployments/NGeP-portlet.war/WEB-INF/config/soft-certificate/kmc.properties");
			// Create KMCClient with the property file


			KMCClient client = KMCClientFactory.getInstance(f);
			// Simulate user selected the question and answer accordingly
			Map<Object, Object> challenges = new HashMap<Object, Object>();
			challenges.put(challengeQues1, challengeAns1);

			// Simulate the data ready to sign
			String dataForSigning = "originalText";

			String pin = "efgh1234";

			//System.out.println("SIGN " + getDigiCertUserId() + getSupplierEpNo() + getChallengeQues1() + getChallengeAns1() + getPassword());
			// Request service to signData based on the answered challenges and data 
			IKMCAPIResponse res = client.requestService(KMCRequestBuilder.signData("890823102233","STRONG HABIS", pin.toCharArray(), challenges, new ByteArrayInputStream(dataForSigning.getBytes())));


			System.out.println("res.getStatusCode()"+res.getStatusCode());
			switch(res.getStatusCode())
			{
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				//				RequestContext.getCurrentInstance().execute("spki_dlg.hide()");


				signedData = (byte[])res.get(IKMCAPIResponse.SignDataResponse.SIGNATURE);

				IKMCAPIResponse res1 = client.requestService(KMCRequestBuilder.verifyData(new ByteArrayInputStream(dataForSigning.getBytes()),signedData));
				switch(res.getStatusCode())
				{
				case IKMCAPIResponse.OPERATION_SUCCESSFUL:
					System.out.println("Data signature is a valid signature");
					break;
				default:
					System.err.println("API return error. Code was "+res1.getStatusCode()+" Message was : "+res1.getStatusMessage());

				}

				break;

			default:

				System.err.println("API return error. Code was "+res.getStatusCode()+" Message was : "+res.getStatusMessage());
				//throw new EpException(MessageConst.ERROR_776052);
				//				signedData = "DEFAULT SIGNED TEXT";
			}

		} catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return signedData;
	}




}
