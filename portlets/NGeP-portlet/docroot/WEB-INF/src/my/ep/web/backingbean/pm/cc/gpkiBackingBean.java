package my.ep.web.backingbean.pm.cc;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.constant.ModuleCodeConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.pm.authorization.PmUser;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.logging.LogKey;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;
import my.ep.web.util.sm.helper.SmCommonUtil;

import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;

import com.sun.faces.facelets.el.ContextualCompositeMethodExpression;

@ManagedBean(name = "gpkiBackingBean")
@SessionScoped
public class gpkiBackingBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(gpkiBackingBean.class);
	private String signedText;
	private PmDigiSign pmDigiSign = new PmDigiSign();
	private Exception nonSigningException;
	//#35488 - start
	private boolean isMediumTypeOTP = true;
	private String nric = "";
	private static String gpkiEnabled = EpPropertiesUtils.getInstance().getProperty("gpki.enabled");
	private static String gpkiNewRoaming = EpPropertiesUtils.getInstance().getProperty("mygpki.new.roaming");
	private static String modeUsed = EpPropertiesUtils.getInstance().getProperty("mygpki.modeUsed");
	private static String appCode = EpPropertiesUtils.getInstance().getProperty("mygpki.appCode");
	private static String agencyId = EpPropertiesUtils.getInstance().getProperty("mygpki.agencyid");
	private static String signType = EpPropertiesUtils.getInstance().getProperty("mygpki.signType");
	private static String saluran = EpPropertiesUtils.getInstance().getProperty("mygpki.saluran");
	private static String otpExp = EpPropertiesUtils.getInstance().getProperty("mygpki.otpExpiry");
	private static String hostOTP = EpPropertiesUtils.getInstance().getProperty("mygpki.hostOTP");
	private static String hostBroker = EpPropertiesUtils.getInstance().getProperty("mygpki.hostBroker");
	private static boolean disableBtn = false;
	private static String serialNumber = "SERIALNUMBER";
	private static String sn = "SN";
	private static String surname = "SURNAME";
	private static String nonce = "gpkiNonce";
	private static String nonceTime = "nonceTime";
	//#35488 - end

	public gpkiBackingBean() {

	}
	
	//#35488
	public String getGpkiEnabledProperty() {
		return gpkiEnabled;
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

	/**
	 * Post Signing Token (old function)
	 * @param postSignActionMtdExpr
	 * @param enableAutoUpdateMtdExpr
	 * @return
	 */
	public String gpkiSignAction(ContextualCompositeMethodExpression postSignActionMtdExpr, 
			ContextualCompositeMethodExpression enableAutoUpdateMtdExpr) {
		long startTime = System.currentTimeMillis();
		String textBeforeSign = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap()
				.get("textBeforeSign");
		signedText = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap()
				.get("signedText");
		String subjectDN = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("subjectDN");
		Long userId = getUserIdSessionFromLoginId(); 
		Timestamp date = new Timestamp(System.currentTimeMillis());
		IUserSO userSO = EpEjbBusinessDelegate.getInstance().getServiceObject(IUserSO.class);	//#35488
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		UserResDTO resDTO = userSO.retrieveUserByUserId(reqDTO);	//#35488
		Object returnObj = null;
		
		try {
			this.setNonSigningException(null);
			if ("true".equalsIgnoreCase(getGpkiEnabledProperty())) {
				//#35488 - start
				Map<String, String> dnMap = splitSubjectDN(subjectDN);
				String icNo = dnMap.get(serialNumber);
				String snicNo = dnMap.get(sn);
				String surnameIcNo = dnMap.get(surname);
				//#35488 - end
				 
				if (icNo.equals(resDTO.getPmUser().getIdentificationNo()) || 
						snicNo.equals(resDTO.getPmUser().getIdentificationNo()) ||
							surnameIcNo.equals(resDTO.getPmUser().getIdentificationNo())) {
					pmDigiSign.setCreatedBy(userId);
					pmDigiSign.setCreatedDate(date);
					pmDigiSign.setOriginalData(textBeforeSign);
					pmDigiSign.setGpkiSignedData(signedText);
					
					returnObj = postSignActionMtdExpr.invoke(
							FacesContext.getCurrentInstance().getELContext(),
							new Object[] { pmDigiSign });
					
					if (enableAutoUpdateMtdExpr != null) {
						enableAutoUpdateMtdExpr.invoke(
								FacesContext.getCurrentInstance().getELContext(),
								new Object[] {});
					}
					
					RequestContext.getCurrentInstance().execute("gpki_dlg.hide()");
					
				} else {
					FacesContext
							.getCurrentInstance()
							.addMessage(
									"gpki",
									new FacesMessage(FacesMessage.SEVERITY_ERROR,
											"",
											"Token's certificate not match with signer identity."));
				}
			} else {
				pmDigiSign.setCreatedBy(userId);
				pmDigiSign.setCreatedDate(date);
				pmDigiSign.setOriginalData(textBeforeSign);
				pmDigiSign.setGpkiSignedData(signedText);

				returnObj = postSignActionMtdExpr.invoke(
						FacesContext.getCurrentInstance().getELContext(),
						new Object[] { pmDigiSign });
				
				if (enableAutoUpdateMtdExpr != null) {
					enableAutoUpdateMtdExpr.invoke(
							FacesContext.getCurrentInstance().getELContext(),
							new Object[] {});
				}
				
				RequestContext.getCurrentInstance().execute("gpki_dlg.hide()");
				
			}
		} catch (Exception e) {
			RequestContext.getCurrentInstance().execute("setRunGPKIEnableAutoUpdate(true);");	
			if (enableAutoUpdateMtdExpr != null) {
				enableAutoUpdateMtdExpr.invoke(
						FacesContext.getCurrentInstance().getELContext(),
						new Object[] {});
			}
			nonSigningException = e;
			RequestContext.getCurrentInstance().execute("gpki_dlg.hide()");
		}
		
		RequestContext.getCurrentInstance().execute("gpkiBlockWidget.hide()");
		log.info("gpkiBackingBean.gpkiSignAction(): duration in milli seconds is: " + (System.currentTimeMillis() - startTime));
		if (returnObj != null) {
			return returnObj.toString();
		} else {
			return null;
		}

	}
	
	//#35488
	/**
	 * Show error
	 */
	public void showError() {
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		PmUser user = new FacesScopes().getUserSessionBean();
		String icNo="";
		if(user != null){
			icNo = user.getIdentificationNo().trim();
		}
		log.info("--35488--err- "+map.get("response"));
		log.info("GPKI Sign Error: Response:"+map.get("response")+" : IC No:"+icNo);
		String statusMsg = map.get("statusMsg");
		String extMsg = map.get("extMsg");
		String field = map.get("field");
		String nonceNull = map.get("gpkiNonceNull");
		if (extMsg != null && extMsg.trim().length() > 0) {
			statusMsg = extMsg;
		}
		if(nonceNull != null){
			statusMsg = SmCommonUtil.getMessage("lbl.gpki.nonce.null", null);
		}
		if(field != null){
			statusMsg = SmCommonUtil.getMessage("lbl.sm.required.msg", new String[] { field.trim() });
		}
		log.info("GPKI TB SIGN ERROR: "+statusMsg+" || IC User:"+icNo);
		FacesContext.getCurrentInstance().addMessage("gpki", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", statusMsg));
	}

	public void onDialogCloseAction() throws Exception {
		if(this.getNonSigningException()!=null) {
			throw this.getNonSigningException();
		}
	}
	
	public Exception getNonSigningException() {
		return nonSigningException;
	}

	public void setNonSigningException(Exception nonSigningException) {
		this.nonSigningException = nonSigningException;
	}

	//#35488 - start
	public boolean isMediumTypeOTP(){
		return isMediumTypeOTP;
	}
	
	/**
	 * Set medium type, roaming OTP or token -default to OTP for any error:
	 * id 2: softcert, 3: roaming, 4: token, 5: roaming OTP
	 */
	public void setMediumType(){
		Map<String, String> map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String id = map.get("mediumTypeId");
		isMediumTypeOTP = true;
		if("4".equals(id)){
			isMediumTypeOTP = false;
		}
		log.info("--35488--"+map.get("response"));
		log.info("--35488--JS-nric "+map.get("js_nric")+" -- "+isMediumTypeOTP);
		PmUser user = getPmUserFromLoginId();
		String idNo = "...";
		if(user != null){
			log.info("--35488--Request IC:"+user.getUserId()+"  "+user.getIdentificationNo());
			idNo = user.getIdentificationNo();
		}
		log.info("35488 TB GPKI: Login IC eP: " + idNo
				+ "  Request eP : " + map.get("js_nric")
				+ "  ResponseGPKI:" + map.get("response"));
	}
	
	
	public void getUserDetails(){
		nric = "";
		String email = "";
		PmUser user = new FacesScopes().getUserSessionBean();
		if(user != null){
			nric = user.getIdentificationNo().trim();
		}
		user = getPmUserFromLoginId();
		if(user != null){
			if (!nric.equals(user.getIdentificationNo().trim())){
				log.info("35488 - different IC: "+nric+"  new IC: "+user.getIdentificationNo().trim());
			}
			nric = user.getIdentificationNo().trim();
			if(user.getEmail() != null){
				email = user.getEmail().trim();
			}
		}
		RequestContext.getCurrentInstance().addCallbackParam("nric", nric);
		RequestContext.getCurrentInstance().addCallbackParam("email", email);
		RequestContext.getCurrentInstance().addCallbackParam("modeUsed", modeUsed);
		RequestContext.getCurrentInstance().addCallbackParam("appCode", appCode);
		RequestContext.getCurrentInstance().addCallbackParam("agencyId", agencyId);
		RequestContext.getCurrentInstance().addCallbackParam("signType", signType);
		RequestContext.getCurrentInstance().addCallbackParam("saluran", saluran);
		RequestContext.getCurrentInstance().addCallbackParam("otpExp", otpExp);
		RequestContext.getCurrentInstance().addCallbackParam("hostOTP", hostOTP);
		RequestContext.getCurrentInstance().addCallbackParam("hostBroker", hostBroker);
		Map<String, Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		try {
			String gpkiNonce = (String) map.get(nonce);
			if(gpkiNonce != null){
				RequestContext.getCurrentInstance().addCallbackParam(nonce, gpkiNonce);
			}
			Long requestTime = (Long) map.get(nonceTime);
			if(requestTime != null){
				RequestContext.getCurrentInstance().addCallbackParam(nonceTime, requestTime);
			}
		} catch (Exception e){
			log.error(e);
		}
	}
	
	/**
	 * Post Signing OTP/Token
	 * @param postSignMtd
	 * @param enableAutoUpdateMtd
	 * @return pmDigiSign
	 */
	public String postSignActionOTP(ContextualCompositeMethodExpression postSignMtd, 
			ContextualCompositeMethodExpression enableAutoUpdateMtd){
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String, String> map = fc.getExternalContext().getRequestParameterMap();
		if(!isValidNric(map)){
			fc.addMessage("gpki", new FacesMessage(FacesMessage.SEVERITY_ERROR, "",
					"Token's certificate not match with signer identity."));
			return null;
		}
		pmDigiSign.setCreatedBy(getUserIdSessionFromLoginId());
		pmDigiSign.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		pmDigiSign.setOriginalData(map.get("textBeforeSign"));
		pmDigiSign.setGpkiSignedData(map.get("signedText"));
		pmDigiSign.setGpkiMedium(isMediumTypeOTP ? "5" : "4");
		
		Object returnObj = null;
		try {
			returnObj = postSignMtd.invoke(fc.getELContext(), new Object[] { pmDigiSign });
		} catch (Exception e){
			e.printStackTrace();
			nonSigningException = e;
			log.info("--35488--"+map.get("response"));
			log.error(e);
		}
		if (enableAutoUpdateMtd != null) {
			enableAutoUpdateMtd.invoke(fc.getELContext(), new Object[] {});
		}
		String dialog = map.get("dialog") == null ? "gpki_dlg2" : map.get("dialog");
		RequestContext.getCurrentInstance().execute(dialog + ".hide()");
		return returnObj != null ? returnObj.toString() : null;
	}
	
	/**
	 * Validate IC when GPKI is on & click from the Sign button
	 * @param map
	 * @return
	 */
	private boolean isValidNric(Map<String, String> requestMap){
		if(!isGpkiNewRoaming() || requestMap.get("skipBtn") != null){
			return true;
		}
		Map<String, String> dnMap = splitSubjectDN(requestMap.get("subjectDN"));
		return dnMap.get(serialNumber).equals(nric) || dnMap.get(sn).equals(nric)
				|| dnMap.get(surname).equals(nric);
	}
	
	/**
	 * @param subjectDN from GPKI response
	 * @return map of serial number, sn and surname
	 */
	private Map<String, String> splitSubjectDN(String subjectDN){
		Map<String, String> map = new HashMap<>(3);
		map.put(serialNumber, "");
		map.put(sn, "");
		map.put(surname, "");
		if(subjectDN == null){
			return map;
		}
		for(String s : subjectDN.split(",")){
			try {
				if(s.contains(serialNumber)){
					map.put(serialNumber, s.substring(s.indexOf('=')+1));
				}
				if(s.contains(sn)){
					map.put(sn, s.substring(s.indexOf('=')+1));
				}
				if(s.contains(surname)){
					map.put(surname, s.substring(s.indexOf('=')+1));
				}
			} catch (IndexOutOfBoundsException e){
				log.error(e);
			}
		}
		return map;
	}
	
	public boolean isGpkiNewRoaming(){
		return gpkiNewRoaming != null && gpkiNewRoaming.length() > 0 
				&& gpkiNewRoaming.charAt(0) == '1';
	}
	
	public boolean getBtn(){
		return disableBtn;
	}
	
	/**
	 * Enable Re-send OTP button
	 */
	public static void enableBtn(){
		disableBtn = false;
	}
	
	/**
	 * Disable Re-send OTP button
	 */
	public static void disableBtn(){
		disableBtn = true;
	}
	
	/**
	 * Copied from BaseBackingBean - 
	 * Method to return current session userId from user screenName(loginId)
	 * @return userId
	 */
	public Long getUserIdSessionFromLoginId(){
		FacesScopes facesScopes = new FacesScopes();
		Long userIdSession = 1L;
		
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(facesScopes.getUserScreenName());
		PmUser pmUser = userSO.retrieveUserByLoginId(userReqDTO).getPmUser();
		
		if (pmUser == null) {
			userIdSession = facesScopes.getUserSessionBean().getUserId();
		} else {
			userIdSession = pmUser.getUserId();
		}
		
		new LogKey(userIdSession);
		return userIdSession;
	}
	
	private PmUser getPmUserFromLoginId(){
		FacesScopes facesScopes = new FacesScopes();
		
		IUserSO userSO = (IUserSO) BusinessDelegate.getInstance().getServiceObject(ModuleCodeConst.PROFILE_MANAGEMENT, IUserSO.class);
		UserReqDTO userReqDTO = new UserReqDTO();
		userReqDTO.setLoginId(facesScopes.getUserScreenName());
		PmUser pmUser = userSO.retrieveUserByLoginId(userReqDTO).getPmUser();
		if (pmUser == null) {
			pmUser = facesScopes.getUserSessionBean();
			log.info("--35488--bean user id: "+pmUser.getUserId()+" -- "+facesScopes.getUserScreenName());
		} 
		return pmUser;
	}
	
	public void saveNonce(){
		FacesContext fc = FacesContext.getCurrentInstance();
		Map<String, String> map = fc.getExternalContext().getRequestParameterMap();
		fc.getExternalContext().getSessionMap().put(nonce, map.get(nonce));
		fc.getExternalContext().getSessionMap().put(nonceTime, System.currentTimeMillis());
	}
	
	public String getNotes(){
		return SmCommonUtil.getMessage("lbl.gpki.otp.notes", new String[] {otpExp});
	}
	//#35488 - end
}
