package my.ep.web.managedbean.soa.retry;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import my.ep.factory.EpLogger;
import my.ep.persistence.entity.osb.OsbBatchRetryDtl;
import my.ep.web.managedbean.BaseManagedBean;

import org.apache.log4j.Logger;

@ManagedBean(name = "SOABatchRetryManagedBean")
@SessionScoped
public class SOABatchRetryManagedBean extends BaseManagedBean {

	private static final long serialVersionUID = 1L;

	private static final Logger log = EpLogger
			.getLogger(SOABatchRetryManagedBean.class);
	
	private OsbBatchRetryDtl[] selectedBatchRetries;
	private String ibob;
	private String sourceSystem;
	private String targetSystem;

	public String getIbob() {
		return ibob;
	}

	public void setIbob(String ibob) {
		this.ibob = ibob;
	}
	
	public OsbBatchRetryDtl[] getSelectedBatchRetries() {
		return selectedBatchRetries;
	}

	public void setSelectedBatchRetries(OsbBatchRetryDtl[] selectedBatchRetries) {
		this.selectedBatchRetries = selectedBatchRetries;
	}

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getTargetSystem() {
		return targetSystem;
	}

	public void setTargetSystem(String targetSystem) {
		this.targetSystem = targetSystem;
	}
}