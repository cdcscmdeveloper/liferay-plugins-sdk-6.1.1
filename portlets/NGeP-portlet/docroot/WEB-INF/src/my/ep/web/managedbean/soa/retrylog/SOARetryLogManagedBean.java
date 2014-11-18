package my.ep.web.managedbean.soa.retrylog;

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
@ManagedBean(name = "soaRetryLogManagedBean")
@SessionScoped
public class SOARetryLogManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger
			.getLogger(SOARetryLogManagedBean.class);
	
	private OsbRetryDtl[] selectedRetryLogs;

	public OsbRetryDtl[] getSelectedRetryLogs() {
		return selectedRetryLogs;
	}

	public void setSelectedRetryLogs(OsbRetryDtl[] selectedRetryLogs) {
		this.selectedRetryLogs = selectedRetryLogs;
	}
	
}
