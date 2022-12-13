package my.ep.web.managedbean.notify.retry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.pm.common.PmNotifyMessage;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * @author Jimmy
 */

@ManagedBean(name = "NotifyRetryManagedBean")
@SessionScoped
public class NotifyRetryManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger
			.getLogger(NotifyRetryManagedBean.class);
	
	private PmNotifyMessage[] selectedNotifyRetries;

	private String targetSystem;
	private String subject;
	private String payload;
	
	public PmNotifyMessage[] getSelectedNotifyRetries() {
		return selectedNotifyRetries;
	}

	public void setSelectedNotifyRetries(PmNotifyMessage[] selectedNotifyRetries) {
		this.selectedNotifyRetries = selectedNotifyRetries;
	}

	public String getTargetSystem() {
		return targetSystem;
	}
	
	public void setTargetSystem(String targetSystem) {
		this.targetSystem = targetSystem;
	}

	public void setPayload(String subject, String payload) {
		this.subject = subject;
		this.payload = payload;
	}
	
	public String getPayloadSubject() {
		return subject;
	}
	
	public String getPayloadContent() {
		return payload;
	}
}