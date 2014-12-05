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
	    String subjectDN = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("subjectDN");
	    FacesScopes facesScope = new FacesScopes();
	    Long userId = facesScope.getUserSessionBean().getUserId();
//	    Long userId = (Long) MDC.get(LogKey.USER_ID);
		Timestamp date = new Timestamp(System.currentTimeMillis());

		IUserSO userSO = (IUserSO) EpEjbBusinessDelegate.getInstance().getServiceObject(IUserSO.class);
		UserReqDTO reqDTO = new UserReqDTO();
		reqDTO.setUserId(userId);
		userSO.retrieveUserByUserId(reqDTO);
		UserResDTO resDTO = new UserResDTO();
		resDTO = userSO.retrieveUserByUserId(reqDTO);
		debugLogger("serialNo====================================: "+serialNo);
		debugLogger("subjectDN====================================: "+subjectDN);
		debugLogger("userId====================================: "+subjectDN.substring(subjectDN.indexOf("=")+1,subjectDN.indexOf(",")));
		debugLogger("getIdentificationNo=====================================: "+resDTO.getPmUser().getLoginId());	
		debugLogger("reflectAction=====================================: "+obj);		
		if(true){
			pmDigiSign.setCreatedBy(userId);
	        pmDigiSign.setCreatedDate(date);
	        pmDigiSign.setOrginalData(textBeforeSign);
	        pmDigiSign.setSignedData(signedText);

			debugLogger("=================================Same IC No");
	        return obj.invoke(FacesContext.getCurrentInstance().getELContext(), new Object[]{pmDigiSign}).toString(); 	  
		}else{
			FacesContext.getCurrentInstance().addMessage("somekey", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Token's certificate IC number not match with signer."));	
			debugLogger("=================================Not Same IC No ");
		}
        return null; 	  

	}
		
	public void showError() {
	    String statusCode = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("statusCode");
	    String statusMsg = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("statusMsg");
	    FacesContext facesContext = FacesContext.getCurrentInstance();
	    debugLogger("OriginalText====================================: "+statusCode);

	FacesContext.getCurrentInstance().addMessage("somekey", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", statusMsg));	
	}

}
