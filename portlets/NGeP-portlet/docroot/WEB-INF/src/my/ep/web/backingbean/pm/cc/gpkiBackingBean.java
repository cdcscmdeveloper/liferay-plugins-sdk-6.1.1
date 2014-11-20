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
import my.ep.util.MessageUtils;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.util.lookupservice.EpEjbBusinessDelegate;
import my.ep.web.util.FacesScopes;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;


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
	
	public gpkiBackingBean(){

	}
	
	private void debugLogger(Object message) {
        if(log.isDebugEnabled()) {
            log.debug(message);
        }
        log.info(message);
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
	
	public String gpkiSignAction(com.sun.faces.facelets.el.ContextualCompositeMethodExpression obj) {
        String textBeforeSign = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("textBeforeSign");
	    String signedText = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("signedText");
	    String serialNo = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("serialNo");
		
	    Long userId = (Long) MDC.get(LogKey.USER_ID);
		Timestamp date = new Timestamp(System.currentTimeMillis());
//		IUserSO userSO = (IUserSO) EpEjbBusinessDelegate.getInstance().getServiceObject(IUserSO.class);
//
//		UserReqDTO reqDTO = new UserReqDTO();
//
//		reqDTO.setUserId(userId);
//		userSO.retrieveUserByUserId(reqDTO);
//		UserResDTO resDTO = new UserResDTO();
//		resDTO = userSO.retrieveUserByUserId(reqDTO);
//		debugLogger("userId====================================: "+userId);
//		debugLogger("resDTO=====================================: "+resDTO.getPmUser().getIdentificationCode());	
		debugLogger("reflectAction=====================================: "+obj);		
		
		
		pmDigiSign.setCreatedBy(userId);
        pmDigiSign.setCreatedDate(date);
        pmDigiSign.setOrginalData(textBeforeSign);
        pmDigiSign.setSignedData(signedText);
		debugLogger("=================================obj.invoke===: ");
       
        return obj.invoke(FacesContext.getCurrentInstance().getELContext(), new Object[]{pmDigiSign}).toString(); 	  
	}
	

		
	public void showError() {
	    String statusCode = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("statusCode");
	    String statusMsg = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("statusMsg");
	    FacesContext facesContext = FacesContext.getCurrentInstance(); ;
	    debugLogger("OriginalText====================================: "+statusCode);
//	    facesContext.addMessage(
//				"somekey",
//				new FacesMessage(FacesMessage.SEVERITY_INFO, null,
//						MessageUtils.getMessage(
//								MessageConst.ERROR_776001,
//								MessageUtils.BUNDLE_COMMON_MESSAGES,
//								new FacesScopes().getUserLocale())));  
//								throw new EpException(MessageConst.ERROR_715026);
	FacesContext.getCurrentInstance().addMessage("somekey", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", statusMsg));	
	}
//	public void signAction() {
////	Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
//
//	    String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("signedText");
//	    String value2 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("textBeforeSign");
//	    String value3 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("serialNo");
//
//	    debugLogger("OriginalText====================================: "+value);
//	    debugLogger("SignedText====================================: "+value2);
//	    debugLogger("ser====================================: "+value3);
//
//	}

}
