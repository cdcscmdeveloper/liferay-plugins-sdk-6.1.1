package my.ep.web.managedbean.soa.serviceretry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.osb.OsbRetryDtl;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * @author MD
 * 
 */
@ManagedBean(name = "SoaServiceRetryManagedBean")
@SessionScoped
public class SOAServiceRetryManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger
			.getLogger(SOAServiceRetryManagedBean.class);
	
	private OsbRetryDtl[] selectedServiceRetries;

	private String targetSystem;
	private String payload;
	
	public OsbRetryDtl[] getSelectedServiceRetries() {
		return selectedServiceRetries;
	}

	public void setSelectedServiceRetries(OsbRetryDtl[] selectedServiceRetries) {
		this.selectedServiceRetries = selectedServiceRetries;
	}

	public String getTargetSystem() {
		return targetSystem;
	}
	
	public void setTargetSystem(String targetSystem) {
		this.targetSystem = targetSystem;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}