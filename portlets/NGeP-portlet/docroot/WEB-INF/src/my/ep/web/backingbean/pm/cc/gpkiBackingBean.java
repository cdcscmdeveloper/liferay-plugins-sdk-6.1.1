package my.ep.web.backingbean.pm.cc;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.util.FacesScopes;

import org.apache.commons.lang.StringUtils;
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

	public gpkiBackingBean() {

	}

	private void debugLogger(Object message) {
		if (log.isDebugEnabled()) {
			log.debug(message);
		}
		log.info(message);
	}

	public String getGpkiEnabledProperty() {
		EpPropertiesUtils prop = EpPropertiesUtils.getInstance();
		System.out.println("getGpkiEnabledProperty :"
				+ prop.getProperty("gpki.enabled"));
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

	public String gpkiSignAction(ContextualCompositeMethodExpression postSignActionMtdExpr, ContextualCompositeMethodExpression enableAutoUpdateMtdExpr) {
		long startTime = System.currentTimeMillis();
		log.info("gpkiBackingBean.gpkiSignAction(): startTime in milli seconds is: " + startTime);
		log.info("ContextualCompositeMethodExpression");
		
		String textBeforeSign = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap()
				.get("textBeforeSign");
		log.info("W/O TOken : enter function--"+textBeforeSign);
		String signedText = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap()
				.get("signedText");
		String serialNo = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("serialNo");
		String subjectDN = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("subjectDN");
		FacesScopes facesScope = new FacesScopes();
		Long userId = facesScope.getUserSessionBean().getUserId();
		log.info("gpkiBackingBean.gpkiSignAction(): userId is: " + userId);
		// Long userId = (Long) MDC.get(LogKey.USER_ID);
		Timestamp date = new Timestamp(System.currentTimeMillis());
		System.out.println("getUserSessionBean().getUserId();=====================================: "
				+ facesScope.getUserSessionBean().getUserId());
		IUserSO userSO = (IUserSO) EpEjbBusinessDelegate.getInstance()
				.getServiceObject(IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		userSO.retrieveUserByUserId(reqDTO);
		UserResDTO resDTO = new UserResDTO();
		resDTO = userSO.retrieveUserByUserId(reqDTO);
		log.info("serialNo====================================: " + serialNo);
		log.info("subjectDN====================================: "+ subjectDN);
		log.info("getIdentificationNo=====================================: "
				+ resDTO.getPmUser().getIdentificationNo());
		log.info("ContextualCompositeMethodExpression  2");
		log.info("postSignActionMtdExpr=====================================: "
				+ postSignActionMtdExpr);
		log.info("enableAutoUpdateMtdExpr=====================================: "
				+ enableAutoUpdateMtdExpr);
		Object returnObj = null;
		log.info("textBeforeSign:"+textBeforeSign);
		log.info("signedText:"+signedText);
		
		try {
			this.setNonSigningException(null);
			log.info("getGpkiEnabledProperty()====================================: " + getGpkiEnabledProperty());
			if (getGpkiEnabledProperty().equalsIgnoreCase("true")) {
				log.info("getGpkiEnabledProperty()====================================:Enter ON ");
				String[] split = subjectDN.split(",");
				String icNo = "";
				String snicNo = "";
				String surnameIcNo = "";
				
				for(int i =0; i<split.length; i++){
					if(split[i].contains("SERIALNUMBER")){
						System.out.println(""+split[i]);
						String icno = split[i];
						log.info(""+icno.substring(icno.indexOf("=")+1, icno.length()));	
						icNo = icno.substring(icno.indexOf("=")+1, icno.length());
					}
					
					if(split[i].contains("SN")){
						System.out.println(""+split[i]);
						String snicno = split[i];
						log.info(""+snicno.substring(snicno.indexOf("=")+1, snicno.length()));	
						snicNo = snicno.substring(snicno.indexOf("=")+1, snicno.length());
					}
					
					if(split[i].contains("SURNAME")){
						System.out.println(""+split[i]);
						String surnameIcno = split[i];
						log.info(""+surnameIcno.substring(surnameIcno.indexOf("=")+1, surnameIcno.length()));	
						surnameIcNo = surnameIcno.substring(surnameIcno.indexOf("=")+1, surnameIcno.length());
					}
	
				}
				 
				if (icNo.equals(resDTO.getPmUser().getIdentificationNo()) || 
						snicNo.equals(resDTO.getPmUser().getIdentificationNo()) ||
							surnameIcNo.equals(resDTO.getPmUser().getIdentificationNo())) {
					pmDigiSign.setCreatedBy(userId);
					pmDigiSign.setCreatedDate(date);
					pmDigiSign.setOriginalData(textBeforeSign);
					pmDigiSign.setGpkiSignedData(signedText);
					log.info("=================================Same IC No");
					
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
				log.info("getGpkiEnabledProperty()====================================:Enter OFF ");
				log.info("W/O TOken : textBeforeSign--"+textBeforeSign);
				pmDigiSign.setCreatedBy(userId);
				pmDigiSign.setCreatedDate(date);
				pmDigiSign.setOriginalData(textBeforeSign);
				pmDigiSign.setGpkiSignedData(signedText);
				log.info("W/O TOken : set data success--");
				returnObj = postSignActionMtdExpr.invoke(
						FacesContext.getCurrentInstance().getELContext(),
						new Object[] { pmDigiSign });
				log.info("W/O TOken : postSignActionMtdExpr success--"+enableAutoUpdateMtdExpr);
				if (enableAutoUpdateMtdExpr != null) {
					enableAutoUpdateMtdExpr.invoke(
							FacesContext.getCurrentInstance().getELContext(),
							new Object[] {});
				}
				log.info("W/O TOken : enableAutoUpdateMtdExpr success--");
				RequestContext.getCurrentInstance().execute("gpki_dlg.hide()");
				
			}
		} catch (Exception e) {
			log.info("W/O TOken : enableAutoUpdateMtdExpr Error setRunGPKIEnableAutoUpdate--");
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

	public void showError() {
		String statusCode = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap()
				.get("statusCode");
		String statusMsg = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("statusMsg");
		String extMsg = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap()
				.get("extMsg");
		
		System.out.println("statusMsg:"+statusMsg);
		System.out.println("extMsg:"+extMsg);
		
		String errorMsg = "";
		if(extMsg!=null && extMsg.trim().length()>0){
			errorMsg = extMsg;
		}else{
			errorMsg = statusMsg;
		}
		
		FacesContext.getCurrentInstance().addMessage("gpki",
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "", errorMsg));
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
	

}
