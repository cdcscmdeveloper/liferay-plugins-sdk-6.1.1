package my.ep.web.backingbean.pm.cc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.MessageConst;
import my.ep.constant.ModuleCodeConst;
import my.ep.constant.sm.SupplierManagementConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.web.backingbean.BaseBackingBean;
import my.ep.web.constant.SPKIConst;
import my.ep.web.util.DigiCertHelper;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.TrustGateHelper;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.pm.helper.PmCommonUtil;
import my.ep.web.util.sm.helper.SmApplicationUtil;
import my.ep.web.util.sm.helper.SmCommonUtil;
import my.ep.web.vo.SpkiVO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.augtech.kmc.client.IKMCAPIResponse;
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
public class SpkiBackingBean extends BaseBackingBean implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String PATH = "NGeP-portlet.war/WEB-INF/config/soft-certificate/kmc.properties";
	// public static final int OPERATION_FAILED = 0;
	// public static final int OPERATION_SUCCESSFUL = 1;
	// public static final int ERROR_USER_EXISTED = 10;
	// public static final int ERROR_USER_NOT_FOUND = 11;
	// public static final int ERROR_USER_INVALID = 12;
	// public static final int ERROR_USER_REVOKED = 13;
	// public static final int ERROR_USER_SUSPENDED = 14;
	// public static final int ERROR_USER_INACTIVE = 15;
	// public static final int ERROR_USER_CHALLENGE_EXIST = 16;
	// public static final int ERROR_USER_ROAMING_EXIST = 17;
	// public static final int ERROR_WRONG_RESPONSE = 20;
	// public static final int ERROR_INVALID_CERT = 21;
	// public static final int ERROR_POLICY_VIOLATION = 22;
	// public static final int ERROR_BACKEND_SERVICES_ERROR = 23;
	// public static final int ERROR_CERT_GOOD = 40;
	// public static final int ERROR_CERT_EXPIRED = 41;
	// public static final int ERROR_CERT_NOT_YET_VALID = 42;
	// public static final int ERROR_CERT_REVOKED = 43;
	// public static final int ERROR_CERT_NOT_FOUND = 44;
	// public static final int ERROR_CERT_SUSPENDED = 45;
	// public static final int ERROR_INVALID_PIN = 50;
	// public static final int ERROR_PIN_BLOCKED = 51;
	// public static final int ERROR_PIN_RESETTING = 52;
	// public static final int ERROR_FATAL_SIGN_ERROR = 99;
	// public static final int ERROR_SERVICE_RUNTIME = 110;
	// public static final int ERROR_SERVICE_CONNECTION = 111;

	public static final int ERROR_CERT_REVOKED = 43;
	public static final int ERROR_USER_INVALID = 12;
	public static final int ERROR_PIN_BLOCKED = 51;
	public static final int ERROR_INVALID_PIN = 50;
	public static final int OPERATION_FAILED = 0;

	private static List<String> challs = null;
	private String challengeQues1;
	private String challengeAns1;
	private String password;
	private PmDigiSign pmDigiSign = new PmDigiSign();
	private byte[] signedData;
	private SmSupplier supplier;
	private PmUser loginUser;
	private String certIssuer;
	private static final Logger logger = EpLogger.getLogger(SpkiBackingBean.class);
	//test #30425
	public static Logger log = Logger.getLogger(SpkiBackingBean.class);

	public SpkiBackingBean() {
	}

	public String getSpkiEnabledProperty() {
		EpPropertiesUtils prop = EpPropertiesUtils.getInstance();
		System.out.println("getSpkiEnabledProperty :"
				+ prop.getProperty("spki.enabled"));
		return prop.getProperty("spki.enabled").trim();
	}

	public void getChallengeQuestions() {
		if (getSpkiEnabledProperty().equalsIgnoreCase("true")) {
			if (getCertIssuer().equals(SupplierManagementConst.CERT_TRUSTGATE)) {
				logger.info("getChallengeQuestions | getCertIssuer-TG : " + getCertIssuer());
				challs = TrustGateHelper.getInstance().getChallengeQuestion(
						getLoginUser().getIdentificationNo(),
						getSupplier().getEpNo(), challengeQues1);
			} else {
				logger.info("getChallengeQuestions | getCertIssuer-DG : " + getCertIssuer());
				challs = DigiCertHelper.getInstance().getChallengeQuestion(
						getLoginUser().getIdentificationNo());
			}
			setChallengeQues1(challs.get(0));
			RequestContext.getCurrentInstance().execute("spki_dlg.show()");
		}
	}

	//test #30425
	public void getChallengeQuestions(String identificationNo) {
		if (getSpkiEnabledProperty().equalsIgnoreCase("true")) {
			if (getCertIssuer(identificationNo).equals(SupplierManagementConst.CERT_TRUSTGATE)) {
				logger.info("getChallengeQuestions(icNo) || getCertIssuer-TG : " + getCertIssuer());
				log.info(">>>>>>>>>>>>>>> masuk dalam trust gate <<<<<<<<<<<<<");
				challs = TrustGateHelper.getInstance().getChallengeQuestion(
						identificationNo, getSupplier().getEpNo(),
						challengeQues1);
			} else {
				logger.info("getChallengeQuestions(icNo) || getCertIssuer-TG : " + getCertIssuer());
				log.info(">>>>>>>>>>>>>>> masuk dalam certIssuer gate <<<<<<<<<");
				challs = DigiCertHelper.getInstance().getChallengeQuestion(
						identificationNo);
			}
			log.info("The properties of challs >>>>>>>>>>" + challs);
			log.info("challs size >>>>>>>>>>>>>>>>>>>>>>>" + challs.size());
			setChallengeQues1(challs.get(0));
			RequestContext.getCurrentInstance().execute("spki_dlg.show()");
		}
	}

	public byte[] spkiEnabledSignTransaction(String originalText,
			String identificationNo) {
		SpkiVO vo = null;
		String pin = getPassword();
		if (getSupplier() == null) {
			throw new EpException(MessageConst.ERROR_721018);
		}
		if (getLoginUser() == null
				|| (getLoginUser() != null && getLoginUser().getUserId() == null)) {
			throw new EpException(MessageConst.ERROR_721017);
		}
		if (StringUtils.isBlank(pin) && StringUtils.isBlank(challengeAns1)) {
			throw new EpException(SmCommonUtil.getMessage("lbl.cc.spki.title",
					null));
		}
		if (StringUtils.isBlank(pin)) {
			throw new EpException(MessageUtils.getMessage(
					MessageConst.ERROR_721110, new String[] { SmCommonUtil
							.getMessage("lbl.cc.spki.pin", null) },
					MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes()
							.getUserLocale()));
		}
		if (StringUtils.isBlank(challengeAns1)) {
			throw new EpException(MessageUtils.getMessage(
					MessageConst.ERROR_721110,
					new String[] { SmCommonUtil.getMessage(
							"lbl.cc.spki.challresp.answer", null) },
					MessageUtils.BUNDLE_COMMON_MESSAGES, new FacesScopes()
							.getUserLocale()));
		}

		identificationNo = StringUtils.isBlank(identificationNo) ? getLoginUser()
				.getIdentificationNo() : identificationNo;

		Map<Object, Object> challenges = new HashMap<Object, Object>();
		challenges.put(challengeQues1, challengeAns1);

		String dataForSigning = originalText;

		if (getCertIssuer(identificationNo).equals(SupplierManagementConst.CERT_TRUSTGATE)) {
			vo = TrustGateHelper.getInstance().getSignedData(identificationNo,
					getSupplier().getEpNo(), pin, challenges, dataForSigning);
		} else {
			vo = DigiCertHelper.getInstance().getSignedData(identificationNo,
					getSupplier().getEpNo(), pin, challenges, dataForSigning);
		}
		if (StringUtils.isBlank(vo.getErrMsg())) {
			signedData = vo.getSignedData();
		} else {
			throw new EpException(vo.getErrMsg());
		}
		return signedData;
	}

	public byte[] spkiSignTransaction(String originalText) {

		if (getSpkiEnabledProperty().equalsIgnoreCase("true")) {
			return spkiEnabledSignTransaction(originalText, null);
		} else {
			return null;
		}
	}

	public byte[] spkiSignTransaction(String originalText,
			String identificationNo) {

		if (getSpkiEnabledProperty().equalsIgnoreCase("true")) {
			return spkiEnabledSignTransaction(originalText, identificationNo);
		} else {
			return null;
		}
	}

	public void setChallengeQues1(String challengeQues1) {
		this.challengeQues1 = challengeQues1;
	}

	public String getDigiCertUserId() {

		return new FacesScopes().getUserSessionBean().getIdentificationNo();
	}

	public String getSupplierEmail() {

		return new FacesScopes().getUserSessionBean().getEmail();
	}

	/**
	 * To perform SPKI Signing Action
	 * 
	 * @param postSignActionMtdExpr
	 *            the postSignAction method expression to invoke
	 * @param enableAutoUpdateMtdExpr
	 *            the enableAutoUpdate method expression to invoke
	 * @return
	 */
	public String spkiSignAction(
			ContextualCompositeMethodExpression postSignActionMtdExpr,
			ContextualCompositeMethodExpression enableAutoUpdateMtdExpr) {
		logger.info("spkiSignAction");

		Object returnObj = null;

		try {

			if (postSignActionMtdExpr != null) {
				returnObj = postSignActionMtdExpr.invoke(FacesContext
						.getCurrentInstance().getELContext(),
						new Object[] { pmDigiSign });
			}

			if (enableAutoUpdateMtdExpr != null) {
				enableAutoUpdateMtdExpr.invoke(FacesContext
						.getCurrentInstance().getELContext(), new Object[] {});
			}

			RequestContext.getCurrentInstance().execute("spki_dlg.hide()");
		} catch (Exception e) {
			RequestContext.getCurrentInstance().execute(
					"setRunEnableAutoUpdate(true);");
			throw e;
		}

		if (returnObj != null) {
			return returnObj.toString();
		} else {
			return null;
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

	public byte[] SpkiTesting() {
		byte[] signedData = null;
		try {

			// Create property file
			String challengeQues1 = new String("In what city you were born?");
			String challengeAns1 = new String("welcome1");
			File f = new File(
					"C:/ePerolehanDemo/liferay-portal-6.1.1-ce-ga2/jboss-7.1.1/standalone/deployments/NGeP-portlet.war/WEB-INF/config/soft-certificate/kmc.properties");
			// Create KMCClient with the property file

			KMCClient client = KMCClientFactory.getInstance(f);
			// Simulate user selected the question and answer accordingly
			Map<Object, Object> challenges = new HashMap<Object, Object>();
			challenges.put(challengeQues1, challengeAns1);

			// Simulate the data ready to sign
			String dataForSigning = "originalText";

			String pin = "efgh1234";

			// System.out.println("SIGN " + getDigiCertUserId() +
			// getSupplierEpNo() + getChallengeQues1() + getChallengeAns1() +
			// getPassword());
			// Request service to signData based on the answered challenges and
			// data
			IKMCAPIResponse res = client
					.requestService(KMCRequestBuilder.signData("890823102233",
							"STRONG HABIS", pin.toCharArray(), challenges,
							new ByteArrayInputStream(dataForSigning.getBytes())));

			System.out.println("res.getStatusCode()" + res.getStatusCode());
			switch (res.getStatusCode()) {
			case IKMCAPIResponse.OPERATION_SUCCESSFUL:
				// RequestContext.getCurrentInstance().execute("spki_dlg.hide()");

				signedData = (byte[]) res
						.get(IKMCAPIResponse.SignDataResponse.SIGNATURE);

				IKMCAPIResponse res1 = client.requestService(KMCRequestBuilder
						.verifyData(
								new ByteArrayInputStream(dataForSigning
										.getBytes()), signedData));
				switch (res.getStatusCode()) {
				case IKMCAPIResponse.OPERATION_SUCCESSFUL:
					System.out.println("Data signature is a valid signature");
					break;
				default:
					System.err.println("API return error. Code was "
							+ res1.getStatusCode() + " Message was : "
							+ res1.getStatusMessage());

				}

				break;

			default:

				System.err.println("API return error. Code was "
						+ res.getStatusCode() + " Message was : "
						+ res.getStatusMessage());
				// throw new EpException(MessageConst.ERROR_776052);
				// signedData = "DEFAULT SIGNED TEXT";
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return signedData;
	}

	public void clearInput() {

		this.setChallengeAns1("");
		this.setPassword("");
	}

	public void changeChallengeQuestions() {
		if (getCertIssuer().equals(SupplierManagementConst.CERT_TRUSTGATE)) {
			challs = TrustGateHelper.getInstance().getChallengeQuestion(
					getLoginUser().getIdentificationNo(),
					getSupplier().getEpNo(), challengeQues1);
		} else {
			challs = DigiCertHelper.getInstance().getChallengeQuestion(
					getLoginUser().getIdentificationNo());
		}
		setChallengeQues1(challs.get(0));
	}

	public SmSupplier getSupplier() {
		if (supplier == null || supplier.getSupplierId() == null) {
			supplier = SmApplicationUtil.getCurrentSupplier();
		}
		return supplier;
	}

	public PmUser getLoginUser() {
		if (loginUser == null || loginUser.getLoginId() == null) {
			IUserSO userSO = (IUserSO) BusinessDelegate.getInstance()
					.getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT,
							IUserSO.class);
			UserReqDTO userReqDTO = new UserReqDTO();
			userReqDTO.setLoginId(new FacesScopes().getUserScreenName());
			UserResDTO resDTO = userSO.retrieveUserByLoginId(userReqDTO);
			if (resDTO.getPmUser() == null) {
				throw new EpException("PM User is null");
			}
			loginUser = resDTO.getPmUser();
		}
		return loginUser;
	}

	public String getCertIssuer() {
		if (StringUtils.isBlank(certIssuer)) {
			certIssuer = getCertIssuer(getLoginUser().getIdentificationNo());
		}
		return certIssuer;
	}

	private String getCertIssuer(String identificationNo) {
		return PmCommonUtil.getCertIssuerByIdentificationNoAndEpNo(
				identificationNo, getSupplier().getEpNo());
	}

}
