package my.ep.web.backingbean.pm.cc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.net.ssl.SSLContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.EpPropertiesConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.util.FacesScopes;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "myGpkiBackingBean")
@SessionScoped
public class MyGpkiBackingBean  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(MyGpkiBackingBean.class);
	private String signedText;
	private PmDigiSign pmDigiSign = new PmDigiSign();
	private Exception nonSigningException;
	private String qrCodeText;

	public MyGpkiBackingBean() {

	}


	
	public String getGpkiEnabledProperty() {
		EpPropertiesUtils prop = EpPropertiesUtils.getInstance();
		System.out.println("getGpkiEnabledProperty :" + prop.getProperty("gpki.enabled"));
		return prop.getProperty("gpki.enabled");
	}

	public String getSignedText() {
		return signedText;
	}

	public void setSignedText(String signedText) {
		this.signedText = signedText;
	}

	public PmDigiSign getPmDigiSign() {
		return pmDigiSign;
	}

	public void setPmDigiSign(PmDigiSign pmDigiSign) {
		this.pmDigiSign = pmDigiSign;
	}
	
	
	
	String myGPKIBaseUrl = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.MYGPKI_BASE_URL);
	String myGPKIEpIssuerCode = EpPropertiesUtils.getInstance().getProperty(EpPropertiesConst.MYGPKI_EP_ISSUER_CODE);
	
	public void preRender() {	
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "myGPKIActivate");
	}
	
	public void showDialog(){
		RequestContext.getCurrentInstance().execute("qrDialog.show()");
	}
	
	public void loadQRCode(){
		setQrCodeText(activate());
	}
	
	
	public void hideDialog(){
		RequestContext.getCurrentInstance().execute("qrDialog.hide()");
	}

	public void deactivate() {
//		String returnFlag = "false";

		FacesScopes facesScope = new FacesScopes();
		Long userId = facesScope.getUserSessionBean().getUserId();
		IUserSO userSO = (IUserSO) EpEjbBusinessDelegate.getInstance().getServiceObject(IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		userSO.retrieveUserByUserId(reqDTO);
		UserResDTO resDTO = new UserResDTO();
		resDTO = userSO.retrieveUserByUserId(reqDTO);

		final String ic = resDTO.getPmUser().getIdentificationNo();

		this.setNonSigningException(null);
		try {

			HttpHost target = new HttpHost(myGPKIBaseUrl, 443, "https");
			HttpPost post = new HttpPost("/OTPAuthenticatorService/rest/deactivateQRCode?");
			CloseableHttpClient httpclient = getHttpClient();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userID", ic));
			params.add(new BasicNameValuePair("issuerCode", myGPKIEpIssuerCode));

			post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

			CloseableHttpResponse response = httpclient.execute(target, post);

			HttpEntity entity = response.getEntity();
			String output = EntityUtils.toString(entity);
			EntityUtils.consume(entity);

			response.close();

			log.info("Deactivate - User IC : " + ic);
			log.info("Deactivate - MyGPKI Output : " + output);

			if (output.equalsIgnoreCase("OPERATION_SUCCESSFULL")) {
				log.info("================================= MyGPKI Deactivated");
				FacesContext.getCurrentInstance().addMessage(
						"mygpkiActivateNotify",
						new FacesMessage(FacesMessage.SEVERITY_INFO,"","Your OTP profile has been deactivated. Please use the new QR image for activation"));

			} else {
				FacesContext.getCurrentInstance().addMessage(
						"mygpkiActivateNotify",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Deactivate process failed - "
								+ output));
			}
			
			httpclient.close();

		} catch (Exception e) {
			log.info("MyGPKI : Deactiavate process failed - Exception : "+e.toString());
			FacesContext.getCurrentInstance().addMessage(
					"mygpkiActivateNotify",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Deactivate process failed - " + e.toString()));

		}
	}

	public String activate() {

		FacesScopes facesScope = new FacesScopes();
		Long userId = facesScope.getUserSessionBean().getUserId();
		IUserSO userSO = (IUserSO) EpEjbBusinessDelegate.getInstance().getServiceObject(IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		userSO.retrieveUserByUserId(reqDTO);
		UserResDTO resDTO = new UserResDTO();
		resDTO = userSO.retrieveUserByUserId(reqDTO);

		final String ic = resDTO.getPmUser().getIdentificationNo();
		String errorDesc = "";

		try {
			
			HttpHost target = new HttpHost(myGPKIBaseUrl, 443, "https");
			HttpPost post = new HttpPost("/OTPAuthenticatorService/rest/getQRCode?");
			CloseableHttpClient httpclient = getHttpClient();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userID", ic));
			params.add(new BasicNameValuePair("issuerCode", myGPKIEpIssuerCode));

			post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

			CloseableHttpResponse response = httpclient.execute(target, post);

			HttpEntity entity = response.getEntity();
			String output = EntityUtils.toString(entity);
			EntityUtils.consume(entity);

			response.close();
			httpclient.close();

			log.info("MyGPKI Registered User IC : " + ic);
			log.info("MyGPKI QR Output : " + output);
			
			
			if (output.equalsIgnoreCase("OPERATION_FAILED")) {
				errorDesc = " - Operation failed";
			} else if (output.equalsIgnoreCase("OTP_USER_NOT_EXIST")) {
				errorDesc = " - OTP user not exist";
			} else if (output.equalsIgnoreCase("OTP_ISSUER_CODE_NOT_EXIST")) {
				errorDesc = " - OTP issuer not exist";
			} else if (output.equalsIgnoreCase("PARAMETER_INCOMPLETE ")) {
				errorDesc = " - Parameter incomplete";
			} else {
				log.info("================================= MyGPKI qr code generated");
				return output;
			}

			FacesContext.getCurrentInstance().addMessage(
					"mygpkiActivateNotify",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Activate process failed. Error : " + output));			

		} catch (Exception e) {
			log.error("MyGPKI : Activate process failed - Exception : "+e.toString());
			return null;
		}

		return errorDesc;

	}
	
	public void verifyOTP(String otpNo) {
		FacesScopes facesScope = new FacesScopes();
		Long userId = facesScope.getUserSessionBean().getUserId();
		IUserSO userSO = (IUserSO) EpEjbBusinessDelegate.getInstance().getServiceObject(IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		userSO.retrieveUserByUserId(reqDTO);
		UserResDTO resDTO = new UserResDTO();
		resDTO = userSO.retrieveUserByUserId(reqDTO);

		final String ic = resDTO.getPmUser().getIdentificationNo();

		try {
			if(null == otpNo || otpNo.isEmpty()){
				throw new Exception("OTP No is empty");
			}

			HttpHost target = new HttpHost(myGPKIBaseUrl, 443, "https");
			HttpPost post = new HttpPost("/OTPAuthenticatorService/rest/verifyOTP?");
			CloseableHttpClient httpclient = getHttpClient();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userID", ic));
			params.add(new BasicNameValuePair("issuerCode", myGPKIEpIssuerCode));
			params.add(new BasicNameValuePair("OTPNo", otpNo));
			

			post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

			CloseableHttpResponse response = httpclient.execute(target, post);

			HttpEntity entity = response.getEntity();
			String output = EntityUtils.toString(entity);
			EntityUtils.consume(entity);

			response.close();
			httpclient.close();
			
			log.info("Verify OTP - User IC : " + ic);
			log.info("Verify OTP - MyGPKI Output : " + output);

			if (output.equalsIgnoreCase("OPERATION_SUCCESSFULL") || output.equalsIgnoreCase("OTP_VALID") ) {
				log.info("================================= MyGPKI OTP Verified");
				FacesContext.getCurrentInstance().addMessage(
						"mygpkiActivateNotify",
						new FacesMessage(FacesMessage.SEVERITY_INFO,"","OTP Verified"));

			} else {
				String errorDesc = "";
				if (output.equalsIgnoreCase("OPERATION_FAILED")) {
					errorDesc = " - Operation failed";
				} else if (output.equalsIgnoreCase("OTP_USER_NOT_EXIST")) {
					errorDesc = " - OTP user not exist";
				} else if (output.equalsIgnoreCase("OTP_ISSUER_CODE_NOT_EXIST")) {
					errorDesc = " - OTP issuer not exist";
				} else if (output.equalsIgnoreCase("PARAMETER_INCOMPLETE")) {
					errorDesc = " - Parameter incomplete";
				} else if (output.equalsIgnoreCase("OTP_INVALID")) {
					errorDesc = " - OTP invalid";
				}else{
					errorDesc = "";
				}
				FacesContext.getCurrentInstance().addMessage(
						"mygpkiActivateNotify",
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Verify OTP process failed. Error : "
								+ output));
			}

		} catch (Exception e) {
			log.error("MyGPKI : Verify OTP process failed - Exception : "+e.toString());
			FacesContext.getCurrentInstance().addMessage(
					"mygpkiActivateNotify",
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Verify OTP process failed. Error : "
							+ e.toString()));
		}

	}
	
	public CloseableHttpClient getHttpClient() {
		CloseableHttpClient httpclient = null;
		SSLContext sslcontext = SSLContexts.createSystemDefault();
		SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslcontext,
				new String[] { "TLSv1", "SSLv3", "TLSv1.1", "TLSv1.2" }, null,
				SSLConnectionSocketFactory.getDefaultHostnameVerifier());

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslConnectionSocketFactory)
				.build();
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

		httpclient = HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).setConnectionManager(cm)
				.build();

		return httpclient;
	}

	public void showError() {
		String statusCode = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("statusCode");
		String statusMsg = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("statusMsg");
		String extMsg = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("extMsg");

		System.out.println("statusMsg:" + statusMsg);
		System.out.println("extMsg:" + extMsg);

		String errorMsg = "";
		if (extMsg != null && extMsg.trim().length() > 0) {
			errorMsg = extMsg;
		} else {
			errorMsg = statusMsg;
		}

		FacesContext.getCurrentInstance().addMessage("mygpkiActivateNotify",
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errorMsg));
	}

	public void onDialogCloseAction() throws Exception {
		if (this.getNonSigningException() != null) {
			throw this.getNonSigningException();
		}
	}

	public Exception getNonSigningException() {
		return nonSigningException;
	}

	public void setNonSigningException(Exception nonSigningException) {
		this.nonSigningException = nonSigningException;
	}

	public String getQrCodeText() {
		return qrCodeText;
	}

	public void setQrCodeText(String qrCodeText) {
		this.qrCodeText = qrCodeText;
	}



}
