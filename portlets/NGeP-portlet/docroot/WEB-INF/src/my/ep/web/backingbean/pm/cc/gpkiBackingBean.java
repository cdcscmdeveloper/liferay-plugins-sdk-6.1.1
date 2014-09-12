package my.ep.web.backingbean.pm.cc;

import java.sql.Timestamp;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import my.ep.persistence.entity.pm.common.PmDigiSign;
import my.ep.persistence.logging.LogKey;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;


@ManagedBean(name = "gpkiBackingBean")
@RequestScoped
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
		
		debugLogger("userId====================================: "+userId);
		debugLogger("reflectAction=====================================: "+obj);
		
		pmDigiSign.setCreatedBy(userId);
        pmDigiSign.setCertificate("111111");
        pmDigiSign.setCreatedDate(date);
        pmDigiSign.setOrginalData(textBeforeSign);
        pmDigiSign.setSignedData(signedText);
        
        return obj.invoke(FacesContext.getCurrentInstance().getELContext(), new Object[]{pmDigiSign}).toString(); 	  
	}
	

		
	public void showError() {
	    String statusCode = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("statusCode");
	    String statusMsg = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("statusMsg");
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
