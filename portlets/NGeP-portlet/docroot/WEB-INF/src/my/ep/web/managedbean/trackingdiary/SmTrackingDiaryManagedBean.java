package my.ep.web.managedbean.trackingdiary;

import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import my.ep.web.managedbean.BaseManagedBean;

@ManagedBean(name = "smTrackingDiaryManagedBean")
@SessionScoped
public class SmTrackingDiaryManagedBean<PmTrackingDiary> extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String applicationType;
	public String getApplicationType() {
		return applicationType;
	}
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

}
