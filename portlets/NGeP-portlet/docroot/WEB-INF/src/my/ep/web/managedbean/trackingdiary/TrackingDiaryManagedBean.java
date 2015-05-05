package my.ep.web.managedbean.trackingdiary;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.apache.log4j.Logger;

import my.ep.web.backingbean.trackingdiary.CdTrackingDiaryBackingBean;
import my.ep.web.managedbean.BaseManagedBean;
@ManagedBean(name = "trackingDiaryManagedBean")
@SessionScoped
public class TrackingDiaryManagedBean extends BaseManagedBean {
	private static final long serialVersionUID = 1L;

	private String moduleSelected;
	private String previousSelectedModule;

	public String getModuleSelected() {
		return moduleSelected;
	}
	public void setModuleSelected(String moduleSelected) {
		this.moduleSelected = moduleSelected;
	}
	public String getPreviousSelectedModule() {
		return previousSelectedModule;
	}
	public void setPreviousSelectedModule(String previousSelectedModule) {
		this.previousSelectedModule = previousSelectedModule;
	}


}
