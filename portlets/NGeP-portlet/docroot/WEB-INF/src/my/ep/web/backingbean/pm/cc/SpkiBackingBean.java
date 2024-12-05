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
import my.ep.dto.sm.personnel.PersonnelReqDTO;
import my.ep.dto.sm.personnel.PersonnelResDTO;
import my.ep.exception.EpException;
import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.entity.sm.SmSupplier;
import my.ep.type.pm.RoleCode;
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
import my.ep.web.util.sm.helper.SmServiceLookup;
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
	//#36605
	private static String digicertEnabled = EpPropertiesUtils.getInstance().getProperty("spki.digicert.enabled");
	private static String trustgateEnabled = EpPropertiesUtils.getInstance().getProperty("spki.trustgate.enabled");
	private static final Logger logger = EpLogger.getLogger(SpkiBackingBean.class);
	//#30425 - Start
	public static Logger log = Logger.getLogger(SpkiBackingBean.class);
	//#30425 - End

	public SpkiBackingBean() {
	}

	public String getSpkiEnabledProperty() {
		//#30425 - Start
		log.info(">>>>>>>>>>>> Trying to call EP properties for SPKI <<<<<<<<<<<");
		EpPropertiesUtils prop = EpPropertiesUtils.getInstance();
		log.info("getSpkiEnabledProperty :" + prop.getProperty("spki.enabled"));
		log.info("Try calling gpki property :" + prop.getProperty("gpki.enabled"));
		log.info("Try calling ssm property :" + prop.getProperty("ssm.checking.onoff"));
		log.info("Try calling myIdentity :" + prop.getProperty("myidentity.checking.onoff"));
		System.out.println("getSpkiEnabledProperty :" + prop.getProperty("spki.enabled"));
		log.info(">>>>>>>>>>>> Calling EP Properties for SPKI Done <<<<<<<<<<<<<< ");
		//#30425 - End
		return prop.getProperty("spki.enabled").trim();
	}

	public void getChallengeQuestions() {
		// #30425 - Start
		// >>>>>>>> START - THIS 'IF BLOCK' FOR CHECKING SPKI ON/OFF SHOULD NOT MERGE INTO PRD, ONLY FOR PDS, TRG, UAT, SIT AND DEV ONLY <<<<<<<<<
		if (getSpkiEnabledProperty().equalsIgnoreCase("true")) {
			// start checking for validation
			validateInternalSPKI(getSupplier().getEpNo());
			// end checking for validation
		}
		// >>>>>>>> END - THIS 'IF BLOCK' FOR CHECKING SPKI ON/OFF SHOULD NOT MERGE INTO PRD, ONLY FOR PDS, TRG, UAT, SIT AND DEV ONLY <<<<<<<<<
		// #30425 - End, #36605
		if ("true".equalsIgnoreCase(getSpkiEnabledProperty()) && isCertEnabled(null)) {
			if (getCertIssuer().equals(SupplierManagementConst.CERT_TRUSTGATE)) {
				logger.info("getChallengeQuestions | getCertIssuer-TG : " + getCertIssuer());
				// #30425 - Start
				log.info(">>>>>>>>>>>>>>> into trustgate <<<<<<<<<<<<<");
				// #30425 - End
				challs = TrustGateHelper.getInstance().getChallengeQuestion(
						getLoginUser().getIdentificationNo(),
						getSupplier().getEpNo(), challengeQues1);
			} else {
				logger.info("getChallengeQuestions | getCertIssuer-DG : " + getCertIssuer());
				// #30425 - Start
				log.info(">>>>>>>>>>>>>>> Into Digicert <<<<<<<<<");
				// #30425 - End
				challs = DigiCertHelper.getInstance().getChallengeQuestion(
						getLoginUser().getIdentificationNo());
			}
			setChallengeQues1(challs.get(0));
			RequestContext.getCurrentInstance().execute("spki_dlg.show()");
		}
	}

	public void getChallengeQuestions(String identificationNo) {
		// #30425 - Start
		// >>>>>>>> START - THIS 'IF BLOCK' FOR CHECKING SPKI ON/OFF SHOULD NOT MERGE INTO PRD, ONLY FOR PDS, TRG, UAT, SIT AND DEV ONLY <<<<<<<<<
		if (getSpkiEnabledProperty().equalsIgnoreCase("true")) {
			// start checking for validation
			validateInternalSPKI(getSupplier().getEpNo());
			// end checking for validation
		}
		// >>>>>>>> END - THIS 'IF BLOCK' FOR CHECKING SPKI ON/OFF SHOULD NOT MERGE INTO PRD, ONLY FOR PDS, TRG, UAT, SIT AND DEV ONLY <<<<<<<<<
		// #30425 - End, #36605
		
		if ("true".equalsIgnoreCase(getSpkiEnabledProperty()) && isCertEnabled(identificationNo)) {
			// #30425 - Start
			if (getTypeOfCert(identificationNo).equals(SupplierManagementConst.CERT_TRUSTGATE)) {
			// #30425 - End
				logger.info("getChallengeQuestions(icNo) || getCertIssuer-TG : " + getCertIssuer());
				// #30425 - Start
				log.info(">>>>>>>>>>>>>>> into trustgate param <<<<<<<<<<<<<");
				// #30425 - End
				challs = TrustGateHelper.getInstance().getChallengeQuestion(
						identificationNo, getSupplier().getEpNo(),
						challengeQues1);
			} else {
				logger.info("getChallengeQuestions(icNo) || getCertIssuer-TG : " + getCertIssuer());
				// #30425 - Start
				log.info(">>>>>>>>>>>>>>> Into Digicert param <<<<<<<<<");
				// #30425 - End
				challs = DigiCertHelper.getInstance().getChallengeQuestion(
						identificationNo);
			}
			// #30425 - Start
			log.info("The properties of challs >>>>>>>>>>" + challs);
			log.info("challs size >>>>>>>>>>>>>>>>>>>>>>>" + challs.size());
			// #30425 - End
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
	
	//#36605 - start
	/**
	 * SPKI Sign Transaction
	 * @param originalText
	 * @return
	 */
	public byte[] spkiSignTransaction(String originalText) {
		return spkiSignTransaction(originalText, null);
	}
	
	/**
	 * SPKI Sign Transaction
	 * @param originalText
	 * @param identificationNo
	 * @return
	 */
	public byte[] spkiSignTransaction(String originalText, String identificationNo) {
		if ("true".equalsIgnoreCase(getSpkiEnabledProperty()) && isCertEnabled(identificationNo)) {
			return spkiEnabledSignTransaction(originalText, identificationNo);
		} else {
			return null;
		}
	}
	
	private boolean isCertEnabled(String identificationNo){
		String cert = StringUtils.isBlank(identificationNo) ? getCertIssuer() : getCertIssuer(identificationNo);
		boolean isTrustgate = cert.equals(SupplierManagementConst.CERT_TRUSTGATE) && "true".equals(trustgateEnabled);
		boolean isDigicert = cert.equals(SupplierManagementConst.CERT_DIGICERT) && "true".equals(digicertEnabled);
		logger.info("isCertEnabled-"+cert+"\t"+isTrustgate+"\t"+isDigicert);
		return isTrustgate || isDigicert;
	}
	//#36605 - end

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
		//#36605
		//PIN must be 8 chars long, contain only letters, numbers, and at least 1 alphabet and 1 number
		if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z0-9]{8}$")){
			String cert = getCertIssuer();
			boolean checkTG = cert.equals(SupplierManagementConst.CERT_TRUSTGATE) && !"true".equals(trustgateEnabled);
			boolean checkDG = cert.equals(SupplierManagementConst.CERT_DIGICERT) && !"true".equals(digicertEnabled);
			if (checkTG || checkDG || !"true".equalsIgnoreCase(getSpkiEnabledProperty())){
				//disable for environment other than PRD
				//throw new EpException(MessageConst.ERROR_776060, null);
			}
		}

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
		//#36605
		if (!isCertEnabled(null)){
			return;
		}
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

	//#30425 - Start
	private String getTypeOfCert(String identificationNo){
		return PmCommonUtil.getTypeOfCertIssuer(
				identificationNo, getSupplier().getEpNo());
	}
	
	private void validateInternalSPKI(String epNo){
		PersonnelReqDTO reqDTO = new PersonnelReqDTO();
		PmUser pmUser = new FacesScopes().getUserSessionBean();
		reqDTO.setEpNo(epNo);
		reqDTO.setIdentificationNo(pmUser.getIdentificationNo());
		// check digicert date, still valid or not
		PersonnelResDTO validateDigiCertDate = SmServiceLookup.getPersonnelSO().checkValidateSoftcert(reqDTO);
		reqDTO.setUserId(pmUser.getUserId());
		// retrive personnel
		PersonnelResDTO resDTO = SmServiceLookup.getPersonnelSO().retrievePersonnelByUserId(reqDTO);
		log.info("getPersonnelId >>>>>>>> " + resDTO.getSmPersonnel().getPersonnelId());
		log.info("getSoftcertFlg >>>>>>>> " + resDTO.getSmPersonnel().getSoftcertFlg());
		log.info("getSmAppl.getSmSupplier.getSupplierId >>>>>> " + resDTO.getSmPersonnel().getSmAppl().getSmSupplier().getSupplierId());
		
		// if process checking validation is successful
		if (validateDigiCertDate.isSuccessful()) {
			// if return with results, then personnel have record in pmDigiCert
			if(validateDigiCertDate.isHasRecord()){
				// if the validation from pmDigiCert status = 1, then it will proceed check the date is expired or not
				if (!validateDigiCertDate.isStatusNotValid()) {
					// if the pmDigicert date is expired
					if (validateDigiCertDate.isExpired()) {
						if (resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ
								|| resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING
								|| resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REJECTED) {
							log.info("getSoftcertFlg|776063 1 :: " + resDTO.getSmPersonnel().getSoftcertFlg());
							throw new EpException(MessageConst.ERROR_DIGICERT_776063);
							// Please make sure your soft certificate application is completed and check the status in Softcert Management Menu.
						} else {
							StringBuilder sb = new StringBuilder();
							sb.append(roleDesc());
							throw new EpException(MessageConst.ERROR_DIGICERT_776066, new String[] {sb.toString()});
							// <Role Name> does not have a valid soft certificate. Please apply for a soft certificate first.
						}
					} else {
						//if it is not expired, but it is revoked, it will going into this block
						if(resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REVOKED){
							StringBuilder sb = new StringBuilder();
							sb.append(roleDesc());
							throw new EpException(MessageConst.ERROR_DIGICERT_776066, new String[] {sb.toString()});
							// <Role Name> does not have a valid soft certificate. Please apply for a soft certificate first.
						}
					}
				} else {
					// if softcert in pmDigiCert status = 0, then the softcert might be not useful anymore even the date is not expired
					StringBuilder sb = new StringBuilder();
					sb.append(roleDesc());
					throw new EpException(MessageConst.ERROR_DIGICERT_776066, new String[] { sb.toString() });
					// <Role Name> does not have a valid soft certificate. Please apply for a soft certificate first.
				}
			} else {
				// if return with 0 results, then personnel didn't have record in Pm Digi Cert
				if (!(resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_ISSUED)) {
					if (resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.IS_SOFTCERT_REQ
							|| resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_PROCESSING
							|| resDTO.getSmPersonnel().getSoftcertFlg() == SupplierManagementConst.SOFT_CERT_REJECTED) {
						log.info("getSoftcertFlg|776063 1 :: " + resDTO.getSmPersonnel().getSoftcertFlg());
						throw new EpException(MessageConst.ERROR_DIGICERT_776063);
						// Please make sure your soft certificate application is completed and check the status in Softcert Management Menu.
					} else {
						StringBuilder sb = new StringBuilder();
						sb.append(roleDesc());
						throw new EpException(MessageConst.ERROR_DIGICERT_776066, new String[] {sb.toString()});
						// <Role Name> does not have a valid soft certificate. Please apply for a soft certificate first.
					}
				}
			}
		}
	}
	
	public String roleDesc(){
		String roleCode = null;
		FacesScopes fs = new FacesScopes();
		if (fs.hasRoleUser(RoleCode.G2G_SUPPLIER_ADMIN.value())){
			roleCode = RoleCode.G2G_SUPPLIER_ADMIN.value();
		}else if (fs.hasRoleUser(RoleCode.BASIC_SUPPLIER_ADMIN.value())){
			roleCode = RoleCode.BASIC_SUPPLIER_ADMIN.value();
		}else if (fs.hasRoleUser(RoleCode.MOF_SUPPLIER_ADMIN.value())){
			roleCode = RoleCode.MOF_SUPPLIER_ADMIN.value();
		}else if (fs.hasRoleUser(RoleCode.G2G_SUPPLIER_USER.value())){
			roleCode = RoleCode.G2G_SUPPLIER_USER.value();
		}else if (fs.hasRoleUser(RoleCode.BASIC_SUPPLIER_USER.value())){
			roleCode = RoleCode.BASIC_SUPPLIER_USER.value();
		}else if (fs.hasRoleUser(RoleCode.MOF_SUPPLIER_USER.value())){
			roleCode = RoleCode.MOF_SUPPLIER_USER.value();
		}else if (fs.hasRoleUser(RoleCode.FL_USER.value())){
			roleCode = RoleCode.FL_USER.value();
		}else if (fs.hasRoleUser(RoleCode.SUPPLIER_TEMP.value())){
			roleCode = RoleCode.SUPPLIER_TEMP.value();
		}
		log.info("The Role Code that will be display >>>>> " + roleCode);
		String languageCode = new FacesScopes().getUserLocale().getLanguage();
		return PmCommonUtil.getRoleDescByRoleCodeAndLanguage(roleCode, languageCode);
	}
	//#30425 - End

}
