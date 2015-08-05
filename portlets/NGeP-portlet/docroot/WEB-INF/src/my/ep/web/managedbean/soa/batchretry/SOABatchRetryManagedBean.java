package my.ep.web.managedbean.soa.batchretry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.osb.OsbBatchRetryDtl;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

/**
 * @author MD
 * 
 */
@ManagedBean(name = "SOABatchRetryManagedBean")
@SessionScoped
public class SOABatchRetryManagedBean extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger
			.getLogger(SOABatchRetryManagedBean.class);
	
	private OsbBatchRetryDtl[] selectedRetryLogs;

	public OsbBatchRetryDtl[] getSelectedRetryLogs() {
		return selectedRetryLogs;
	}

	public void setSelectedRetryLogs(OsbBatchRetryDtl[] selectedRetryLogs) {
		this.selectedRetryLogs = selectedRetryLogs;
	}
	
}
