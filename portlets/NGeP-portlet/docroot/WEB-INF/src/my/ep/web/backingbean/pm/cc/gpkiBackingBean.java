package my.ep.web.backingbean.pm.cc;

import java.sql.Timestamp;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import my.ep.biz.service.pm.authorization.user.IUserSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.constant.MessageConst;
import my.ep.dto.pm.authorization.user.UserReqDTO;
import my.ep.dto.pm.authorization.user.UserResDTO;
import my.ep.exception.EpException;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.logging.LogKey;
import my.ep.util.EpPropertiesUtils;
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "gpkiBackingBean")
@SessionScoped
public class gpkiBackingBean {

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

	public String gpkiSignAction(
			com.sun.faces.facelets.el.ContextualCompositeMethodExpression obj) {
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
		debugLogger("getUserSessionBean().getUserId();=====================================: "
				+ facesScope.getUserSessionBean().getUserId());
		IUserSO userSO = (IUserSO) EpEjbBusinessDelegate.getInstance()
				.getServiceObject(IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		userSO.retrieveUserByUserId(reqDTO);
		UserResDTO resDTO = new UserResDTO();
		resDTO = userSO.retrieveUserByUserId(reqDTO);
		debugLogger("serialNo====================================: " + serialNo);
		debugLogger("subjectDN====================================: "
				+ subjectDN);
		debugLogger("userId====================================: "
				+ subjectDN.substring(subjectDN.indexOf("=") + 1,
						subjectDN.indexOf(",")));
		debugLogger("getIdentificationNo=====================================: "
				+ resDTO.getPmUser().getLoginId());
		System.out.println("ContextualCompositeMethodExpression  2");
		debugLogger("reflectAction=====================================: "
				+ obj);
		Object returnObj = null;
		System.out.println("textBeforeSign:"+textBeforeSign);
		System.out.println("signedText:"+signedText);
		if (getGpkiEnabledProperty().equalsIgnoreCase("true")) {
			if (subjectDN.substring(subjectDN.indexOf("=") + 1,
					subjectDN.indexOf(",")).equals(
					resDTO.getPmUser().getIdentificationNo())) {
				pmDigiSign.setCreatedBy(userId);
				pmDigiSign.setCreatedDate(date);
				pmDigiSign.setOriginalData(textBeforeSign);
				pmDigiSign.setGpkiSignedData(signedText);
				RequestContext.getCurrentInstance().execute("gpki_dlg.hide()");
				debugLogger("=================================Same IC No");
				returnObj = obj.invoke(
						FacesContext.getCurrentInstance().getELContext(),
						new Object[] { pmDigiSign });
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
			RequestContext.getCurrentInstance().execute("gpki_dlg.hide()");

			returnObj = obj.invoke(
					FacesContext.getCurrentInstance().getELContext(),
					new Object[] { pmDigiSign });
			
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
		System.out.println("statusMsg:"+statusMsg);
		FacesContext.getCurrentInstance().addMessage("gpki",
				new FacesMessage(FacesMessage.SEVERITY_ERROR, "", statusMsg));
	}

}
