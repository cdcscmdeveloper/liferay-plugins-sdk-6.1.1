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
		System.out.println("ContextualCompositeMethodExpression");
		
		String textBeforeSign = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap()
				.get("textBeforeSign");
		String signedText = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap()
				.get("signedText");
		String serialNo = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("serialNo");
		String subjectDN = FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap().get("subjectDN");
		FacesScopes facesScope = new FacesScopes();
		Long userId = facesScope.getUserSessionBean().getUserId();
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
		System.out.println("serialNo====================================: " + serialNo);
		System.out.println("subjectDN====================================: "
				+ subjectDN);
		System.out.println("userId====================================: "
				+ subjectDN.substring(subjectDN.indexOf("=") + 1,
						subjectDN.indexOf(",")));
		System.out.println("getIdentificationNo=====================================: "
				+ resDTO.getPmUser().getLoginId());
		System.out.println("ContextualCompositeMethodExpression  2");
		System.out.println("postSignActionMtdExpr=====================================: "
				+ postSignActionMtdExpr);
		System.out.println("enableAutoUpdateMtdExpr=====================================: "
				+ enableAutoUpdateMtdExpr);
		Object returnObj = null;
		System.out.println("textBeforeSign:"+textBeforeSign);
		System.out.println("signedText:"+signedText);
		
		try {
			if (getGpkiEnabledProperty().equalsIgnoreCase("true")) {
				if (subjectDN.substring(subjectDN.indexOf("=") + 1,
						subjectDN.indexOf(",")).equals(
						resDTO.getPmUser().getIdentificationNo())) {
					pmDigiSign.setCreatedBy(userId);
					pmDigiSign.setCreatedDate(date);
					pmDigiSign.setOriginalData(textBeforeSign);
					pmDigiSign.setGpkiSignedData(signedText);
					System.out.println("=================================Same IC No");
					
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
			throw e;
		}
		
		RequestContext.getCurrentInstance().execute("gpkiBlockWidget.hide()");

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

}
