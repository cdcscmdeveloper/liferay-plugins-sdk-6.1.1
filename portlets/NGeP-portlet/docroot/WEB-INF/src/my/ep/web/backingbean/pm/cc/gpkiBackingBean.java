package my.ep.web.backingbean.pm.cc;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import my.ep.biz.service.pm.common.state.IStateSO;
import my.ep.bpm.dto.BpmRequestDTO;
import my.ep.bpm.service.IBpmSO;
import my.ep.dto.pm.common.state.StateReqDTO;
import my.ep.dto.pm.common.state.StateResDTO;
import my.ep.util.lookupservice.EpBpmBusinessDelegate;
import my.ep.web.util.FacesScopes;
import my.ep.web.util.lookupservice.BusinessDelegate;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.Http.Method;


@ManagedBean(name = "gpkiBackingBean")
@RequestScoped
public class gpkiBackingBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(gpkiBackingBean.class);
	private String signedText;

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

	public void reflectSignAction(com.sun.faces.facelets.el.ContextualCompositeMethodExpression obj) {
//	Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		Class cls = null;
		try {
			cls = Class.forName("my.ep.web.backingbean.pp.budgetallocation.BudgetAllocationBackingBean");
		} catch (ClassNotFoundException e2) { 
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	       Class partypes[] = new Class[2];
	        partypes[0] = Integer.TYPE;
	        partypes[1] = Integer.TYPE;
	        java.lang.reflect.Method meth = null;
			try {
				meth = cls.getMethod("add", partypes);
			} catch (NoSuchMethodException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SecurityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        my.ep.web.backingbean.pp.budgetallocation.BudgetAllocationBackingBean bean = new my.ep.web.backingbean.pp.budgetallocation.BudgetAllocationBackingBean();
	        Object arglist[] = new Object[2];
	        arglist[0] = new Integer(37);
	        arglist[1] = new Integer(47);
	        Object retobj = null;
			try {
				retobj = meth.invoke(bean, arglist);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        Integer retval = (Integer)retobj;
	        System.out.println(retval.intValue());
	        String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("param1");
		    String value2 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("param2");

	    debugLogger("OriginalText====================================: "+value);
	    debugLogger("SignedText====================================: "+value2);
	    System.err.println("================================================");

		StateReqDTO reqDTO = new StateReqDTO();
		reqDTO.setCountryId(1l);
		IStateSO reqSO = (IStateSO) BusinessDelegate.getInstance()
				.getServiceObject("pm", IStateSO.class);
		StateResDTO resDTO = reqSO.retriveStatesListByCountryID(reqDTO);
		System.err.println("================================================"+ resDTO.getPmState().getStateName());
	    debugLogger("reflect  =====================================: "+retobj.toString());
	    
	    debugLogger("reflectAction=====================================: "+obj);
	}
	
	public void updatecert() {


	    debugLogger("OriginalText====================================: ");
	    debugLogger("SignedText====================================: ");
	    debugLogger("ser====================================: ");

		}
		
	
	public void signAction() {
//	Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

	    String value = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("signedText");
	    String value2 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("textBeforeSign");
	    String value3 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("serialNo");

    debugLogger("OriginalText====================================: "+value);
    debugLogger("SignedText====================================: "+value2);
    debugLogger("ser====================================: "+value3);

	}

}
